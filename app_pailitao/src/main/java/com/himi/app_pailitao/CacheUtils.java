
package com.himi.app_pailitao;

import android.text.TextUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CacheUtils {
    public static final String IMAGE_TAIL = "_cropped";

    public static String IMAGE_FOLDER_PATH;
    public static String ANWSER_FOLDER_PATH;

    public static void init() {
        IMAGE_FOLDER_PATH = FileUtils.CACHE_DIR_PATH + "soutiimage" + File.separator;
        ANWSER_FOLDER_PATH = FileUtils.CACHE_DIR_PATH + "soutianwser" + File.separator;

        File file = new File(IMAGE_FOLDER_PATH);
        if (!file.exists() || !file.isDirectory()) {
            FileUtils.makeDirectory(IMAGE_FOLDER_PATH);
        }
        file = new File(ANWSER_FOLDER_PATH);
        if (!file.exists() || !file.isDirectory()) {
            FileUtils.makeDirectory(ANWSER_FOLDER_PATH);
        }
    }

    /**
     * xxx/xxx/xxx_cropped.xxx
     */
    public static long getImageCreateTime(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            return 0L;
        }
        int index = imagePath.lastIndexOf(File.separator);
        if (index >= 0) {
            imagePath = imagePath.substring(index + 1);
        }
        index = imagePath.indexOf(IMAGE_TAIL);
        if (index == 13) {
            try {
                return Long.valueOf(imagePath.substring(0, index));
            } catch (NumberFormatException e) {
                return 0L;
            }
        }
        return 0L;
    }

    public static void delete(long timestamp) {
        (new File(getImagePath(timestamp))).delete();
        (new File(getAfantiAnwserPath(timestamp))).delete();
        (new File(getMixuebaAnwserPath(timestamp))).delete();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    public static String getImagePath(long imageCreateTime) {
        check();
        StringBuilder builder = new StringBuilder(IMAGE_FOLDER_PATH);
        builder.append(imageCreateTime);
        builder.append(IMAGE_TAIL);
        builder.append(".jpg");
        return builder.toString();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////

    public static String getAfantiAnwserPath(long imageCreateTime) {
        check();
        StringBuilder builder = new StringBuilder(ANWSER_FOLDER_PATH);
        builder.append(imageCreateTime);
        builder.append("_afanti.txt");
        return builder.toString();
    }

    public static String getAfantiAnwserContent(long imageCreateTime) {
        return FileUtils.getFileContent(getAfantiAnwserPath(imageCreateTime));
    }

    public static void saveAfantiAnwserContent(long imageCreateTime, String content) {
        FileUtils.saveFileContent(getAfantiAnwserPath(imageCreateTime), content);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////

    public static String getMixuebaAnwserPath(long imageCreateTime) {
        check();
        StringBuilder builder = new StringBuilder(ANWSER_FOLDER_PATH);
        builder.append(imageCreateTime);
        builder.append("_mixueba.txt");
        return builder.toString();
    }

    public static String getMixuebaAnwserContent(long imageCreateTime) {
        return FileUtils.getFileContent(getMixuebaAnwserPath(imageCreateTime));
    }

    public static void saveMixuebaAnwserContent(long imageCreateTime, String content) {
        FileUtils.saveFileContent(getMixuebaAnwserPath(imageCreateTime), content);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////

    public static int getCachedAnwsersCount() {
        check();
        int count = 0;
        File cacheFolder = new File(IMAGE_FOLDER_PATH);
        if (!cacheFolder.exists()) {
            return count;
        }
        if (!cacheFolder.isDirectory()) {
            cacheFolder.delete();
            FileUtils.makeDirectory(IMAGE_FOLDER_PATH);
            return count;
        }
        File[] files = cacheFolder.listFiles();
        if (files == null) {
            return count;
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                long time = getImageCreateTime(files[i].getName());
                if (time == 0L) {
                    continue;
                }
                count++;
            }
        }
        return count;
    }

    public static ArrayList<SoutiCache> listCachedAnwsers() {
        check();
        ArrayList<SoutiCache> list = new ArrayList<>();
        File cacheFolder = new File(IMAGE_FOLDER_PATH);
        if (!cacheFolder.exists()) {
            return list;
        }
        if (!cacheFolder.isDirectory()) {
            cacheFolder.delete();
            FileUtils.makeDirectory(IMAGE_FOLDER_PATH);
            return list;
        }
        File[] files = cacheFolder.listFiles();
        if (files == null) {
            return list;
        }
        final SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                long time = getImageCreateTime(files[i].getName());
                if (time == 0L) {
                    continue;
                }
                SoutiCache cache = new SoutiCache();
                cache.imagepath = files[i].getPath();
                cache.timestamp = time;
                cache.timeStr = format.format(time);

                list.add(cache);
            }
        }
        Collections.sort(list, new Comparator<SoutiCache>() {

            @Override
            public int compare(SoutiCache lhs, SoutiCache rhs) {
                return (int) (rhs.timestamp - lhs.timestamp);
            }
        });
        return list;
    }

    private static void check() {
        if (TextUtils.isEmpty(IMAGE_FOLDER_PATH) || TextUtils.isEmpty(ANWSER_FOLDER_PATH)) {
            init();
        }
    }

    public static final class SoutiCache {
        public String imagepath;
        public long timestamp;
        public String timeStr;
    }
}
