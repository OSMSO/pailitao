
package com.himi.app_pailitao;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BitmapUtils {
    public static final int MAX_BITMAP_WIDTH = DisplayUtils.screenWidth > 1080 ? 1080 : DisplayUtils.screenWidth;
    public static final int MAX_BITMAP_HEIGHT = DisplayUtils.screenWidth > 1920 ? 1920 : DisplayUtils.screenWidth;

    /**
     * 44dp 系统通知栏的头像
     */
    public static int NOTITY_ICON_SIZE;

    public static void init() {
        NOTITY_ICON_SIZE = DisplayUtils.getDp2Px(44);
    }

    public static final int IO_BUFFER_SIZE = 1024;

    /**
     * 把网络io数据流decode成bitmap
     *
     * @param is        原始的io流
     * @param reqWidth  需要的bitmap的宽 如果是0则表示使用原图
     * @param reqHeight 需要的bitmap的高 如果是0则表示使用原图
     * @return Bitmap 可能返回null
     * @see http://stackoverflow.com/questions/10730520/decodestream-returns-null
     */
    public static Bitmap decodeSampledBitmapFromInputStream(InputStream is,
                                                            int reqWidth, int reqHeight) {
        InputStream copyOfin = null;
        if (reqWidth <= 0 || reqHeight <= 0) {
            return BitmapFactory.decodeStream(is);
        } else {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                copy(is, out);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            copyOfin = new ByteArrayInputStream(out.toByteArray());
        }

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(copyOfin, null, options);
    }

    /**
     * 把本地的文件decode成bitmap
     *
     * @param pathPath 文件的路径
     * @return Bitmap 可能返回null
     */
    public static Bitmap decodeSampledBitmapFromFile(String pathPath) {
        if (DisplayUtils.screenWidth > MAX_BITMAP_WIDTH) {
            return decodeSampledBitmapFromFile(pathPath, MAX_BITMAP_WIDTH, MAX_BITMAP_HEIGHT);
        } else {
            return decodeSampledBitmapFromFile(pathPath, DisplayUtils.screenWidth,
                    DisplayUtils.screenHeight);
        }
    }

    public static Bitmap decodeSampledBitmapFromFile(String pathPath, int reqWidth, int reqHeight) {
        if (TextUtils.isEmpty(pathPath)) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathPath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathPath, options);
    }

    /**
     * 把apk中的资源decode成bitmap
     *
     * @param res       Resources
     * @param resId     资源的id
     * @param reqWidth  需要的bitmap的宽 如果是0则表示使用原图
     * @param reqHeight 需要的bitmap的高 如果是0则表示使用原图
     * @return Bitmap 可能返回null
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[IO_BUFFER_SIZE];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * 将原图按照指定的宽高进行缩放
     *
     * @param oldBitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap oldBitmap, int newWidth, int newHeight) {
        int width = oldBitmap.getWidth();
        int height = oldBitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(newBitmap, matrix, null);
        return newBitmap;
    }

    public static Bitmap decodeUriAsBitmap(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    /**
     * 旋转图片，使图片保持正确的方向。
     *
     * @param bitmap  原始图片
     * @param degrees 原始图片的角度
     * @return Bitmap 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees == 0 || null == bitmap) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);
        if (null != bitmap) {
            bitmap.recycle();
        }
        return bmp;
    }

    /**
     * 根据图片长宽裁剪，长大于宽，返回原图，宽大于长，返回旋转90度的图片
     */
    public static Bitmap getRectBitmap(String imagepath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagepath, options);

        Bitmap bitmap = decodeSampledBitmapFromFile(imagepath);
        if (options.outHeight > options.outWidth) {
            return rotateBitmap(bitmap, -90);
        }
        return bitmap;
    }
}
