package graphics;

public class Colors {
    public final static Color black = new Color(0xf000000);
    public final static Color white = new Color(0xffffff);

    public final static Color red = new Color(0xf94144);
    public final static Color darkOrange = new Color(0xf3722c);
    public final static Color orange = new Color(0xf8961e);
    public final static Color yellow = new Color(0xf9c74f);
    public final static Color green = new Color(0x90be6d);
    public final static Color cyan = new Color(0x43aa8b);
    public final static Color blue = new Color(0x577590);

    public final static Color background = white.lerp(black, 0.85);

    public final static Color[] scheme = new Color[]{red, darkOrange, orange, yellow, green, cyan, blue};
}
