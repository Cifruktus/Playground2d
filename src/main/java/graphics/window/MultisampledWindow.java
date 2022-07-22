package graphics.window;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;

// Modified code from LWJGL examples
public class MultisampledWindow {

    long window;
    int width = 1000;
    int height = 1000;
    boolean resetFramebuffer;

    /* Multisampled FBO objects */
    int multisampledColorRenderBuffer;
    int multisampledDepthRenderBuffer;
    int multisampledFbo;
    int samples = 8;

    /* Single-sampled FBO objects */
    int colorTexture;
    int fbo;

    GLFWErrorCallback errorCallback;
    GLFWKeyCallback keyCallback;
    GLFWFramebufferSizeCallback fbCallback;
    Callback debugProc;

    public boolean isClosed(){
        return glfwWindowShouldClose(window);
    }

    public void init() {
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(width, height, "Playground", NULL, NULL);
        if (window == NULL) {
            throw new AssertionError("Failed to create the GLFW window");
        }

        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (action != GLFW_RELEASE)
                    return;

                if (key == GLFW_KEY_ESCAPE) {
                    glfwSetWindowShouldClose(window, true);
                }
            }
        });

        glfwSetFramebufferSizeCallback(window, fbCallback = new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                if (width > 0 && height > 0 && (
                        MultisampledWindow.this.width != width || MultisampledWindow.this.height != height)) {
                    MultisampledWindow.this.width = width;
                    MultisampledWindow.this.height = height;
                    MultisampledWindow.this.resetFramebuffer = true;
                }
            }
        });

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
        glfwShowWindow(window);

        try (MemoryStack frame = MemoryStack.stackPush()) {
            IntBuffer framebufferSize = frame.mallocInt(2);
            nglfwGetFramebufferSize(window, memAddress(framebufferSize), memAddress(framebufferSize) + 4);
            width = framebufferSize.get(0);
            height = framebufferSize.get(1);
        }

        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        debugProc = GLUtil.setupDebugMessageCallback();

        /* Query maximum sample count */
        samples = glGetInteger(GL_MAX_SAMPLES);
        System.out.println("Using " + samples + "x multisampling");
        /* Initially create the FBOs */
        createFBOs();
        glfwSwapInterval(-1);
    }

    void createFBOs() {
        /* Create multisampled FBO */
        multisampledColorRenderBuffer = glGenRenderbuffers();
        multisampledDepthRenderBuffer = glGenRenderbuffers();
        multisampledFbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, multisampledFbo);
        glBindRenderbuffer(GL_RENDERBUFFER, multisampledColorRenderBuffer);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_RGBA8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, multisampledColorRenderBuffer);
        glBindRenderbuffer(GL_RENDERBUFFER, multisampledDepthRenderBuffer);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, multisampledDepthRenderBuffer);
        int fboStatus = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (fboStatus != GL_FRAMEBUFFER_COMPLETE) {
            throw new AssertionError("Could not create FBO: " + fboStatus);
        }
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        /* Create single-sampled FBO */
        colorTexture = glGenTextures();
        fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        glBindTexture(GL_TEXTURE_2D, colorTexture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR); // we also want to sample this texture later
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR); // we also want to sample this texture later
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTexture, 0);
        fboStatus = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (fboStatus != GL_FRAMEBUFFER_COMPLETE) {
            throw new AssertionError("Could not create FBO: " + fboStatus);
        }
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    void resizeFBOs() {
        /* Delete multisampled FBO objects */
        glDeleteRenderbuffers(multisampledDepthRenderBuffer);
        glDeleteRenderbuffers(multisampledColorRenderBuffer);
        glDeleteFramebuffers(multisampledFbo);
        /* Delete single-sampled FBO objects */
        glDeleteTextures(colorTexture);
        glDeleteFramebuffers(fbo);
        /* Recreate everything */
        createFBOs();
    }

    public void updateScreen(WindowDrawCallback dc){

        /* Update the FBO if the window changed in size */
        if (resetFramebuffer) {
            resizeFBOs();
            resetFramebuffer = false;
        }

        /* Render to multisampled FBO */
        glBindFramebuffer(GL_FRAMEBUFFER, multisampledFbo);
        {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glViewport(0, 0, width, height);

          //  glTranslatef(-2f,-2f,0);
            glScalef(2f,2f,0);

            dc.draw();
        }

        GL11.glDisable(GL11.GL_BLEND);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        /* Resolve by blitting to non-multisampled FBO */
        glBindFramebuffer(GL_READ_FRAMEBUFFER, multisampledFbo);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo);
        glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL_NEAREST);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        /* Now you can just read from the resolved colorTexture */

        /* But we will just draw it on the viewport using a fullscreen quad */
        glEnable(GL_TEXTURE_2D);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
        glBindTexture(GL_TEXTURE_2D, colorTexture);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(-1, -1);
        glTexCoord2f(1, 0);
        glVertex2f(1, -1);
        glTexCoord2f(1, 1);
        glVertex2f(1, 1);
        glTexCoord2f(0, 1);
        glVertex2f(-1, 1);
        glEnd();
        glDisable(GL_TEXTURE_2D);

        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    public void close(){
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);


        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    void destroy() {
        glfwDestroyWindow(window);

        if (debugProc != null) debugProc.free();
        keyCallback.free();
        fbCallback.free();

        glfwTerminate();
        errorCallback.free();
    }

}