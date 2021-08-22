package cc.brainbook.android.colorarcprogressbar.util;

import android.graphics.Color;

public class Util {

    ///https://stackoverflow.com/questions/7779621/how-to-get-programmatically-a-list-of-colors-from-a-gradient-on-android/7779834
    public static int getColorFromGradient(int[] colors, float[] positions, float v ){

        if( colors.length == 0 || colors.length != positions.length ){
            throw new IllegalArgumentException();
        }

        if( colors.length == 1 ){
            return colors[0];
        }

        if( v <= positions[0]) {
            return colors[0];
        }

        if( v >= positions[positions.length-1]) {
            return colors[positions.length-1];
        }

        for( int i = 1; i < positions.length; ++i ){
            if( v <= positions[i] ){
                float t = (v - positions[i-1]) / (positions[i] - positions[i-1]);
                return lerpColor(colors[i-1], colors[i], t);
            }
        }

        //should never make it here
        throw new RuntimeException();
    }

    public static int lerpColor( int colorA, int colorB, float t){
        int alpha = (int)Math.floor(Color.alpha(colorA) * ( 1 - t ) + Color.alpha(colorB) * t);
        int red   = (int)Math.floor(Color.red(colorA)   * ( 1 - t ) + Color.red(colorB)   * t);
        int green = (int)Math.floor(Color.green(colorA) * ( 1 - t ) + Color.green(colorB) * t);
        int blue  = (int)Math.floor(Color.blue(colorA)  * ( 1 - t ) + Color.blue(colorB)  * t);

        return Color.argb(alpha, red, green, blue);
    }

}
