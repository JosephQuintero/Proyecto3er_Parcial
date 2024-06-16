package proyecto3er_Parcial;

import java.awt.Color;

public class ColorUtils {
    public static Color getRainbowColor(float ratio) {
        // ratio is between 0 and 1
        int hue = (int) (ratio * 360); // hue from 0 to 360
        return Color.getHSBColor(hue / 360f, 1.0f, 1.0f); // full saturation and brightness
    }
}