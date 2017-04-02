package com.damianmichalak.shopping_list.helper;


import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class QRHelper {

    public static void generateQR(String input, ImageView qrView) {
        final BitMatrix result;

        try {
            result = new MultiFormatWriter().encode(input, BarcodeFormat.QR_CODE, 500, 500, null);

            int w = result.getWidth();
            int h = result.getHeight();
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                int offset = y * w;
                for (int x = 0; x < w; x++) {
                    pixels[offset + x] = result.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
                }
            }
            final Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, 500, 0, 0, w, h);

            qrView.setImageBitmap(bitmap);

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        }
    }
}
