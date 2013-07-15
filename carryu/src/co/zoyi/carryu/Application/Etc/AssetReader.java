package co.zoyi.carryu.Application.Etc;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class AssetReader {
    public static String readString(Context context, String fileName) {
        InputStream stream;
        try {
            stream = context.getAssets().open(fileName);
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
