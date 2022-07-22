package graphics.window;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    public static long window;

    public static boolean controlUp = false;
    public static boolean controlDown = false;
    public static boolean controlLeft = false;
    public static boolean controlRight = false;
    public static boolean enterPressed = false;

    public void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable

        //Display.create(new PixelFormat(/*Alpha Bits*/8, /*Depth bits*/ 8, /*Stencil bits*/ 0, /*samples*/8));

        // Create the window
        window = glfwCreateWindow(1000, 1000, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
             if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                 glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop

            if (action == GLFW_RELEASE || action == GLFW_PRESS) {
                boolean pressed = action == GLFW_PRESS;

                if (key == GLFW_KEY_LEFT) controlLeft = pressed;
                if (key == GLFW_KEY_RIGHT) controlRight = pressed;
                if (key == GLFW_KEY_UP) controlUp = pressed;
                if (key == GLFW_KEY_DOWN) controlDown = pressed;
                if (key == GLFW_KEY_ENTER) enterPressed = pressed;
            }

        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        GL.createCapabilities();
        glClearColor(0.1f, 0.1f, 0.1f, 0.0f);

      //  glTranslatef(-1f,-1f,0);

       glTranslatef(-2f,-2f,0);
       glScalef(2f,2f,0);
    }

    public boolean isClosed(){
        return glfwWindowShouldClose(window);
    }

    public void close(){
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);


        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void updateScreen(WindowDrawCallback dc){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        dc.draw();

        glfwSwapBuffers(window);
        glfwPollEvents();
    }
}

