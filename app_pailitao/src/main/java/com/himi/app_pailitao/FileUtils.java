
package com.himi.app_pailitao;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;

public class FileUtils {

    public static String EXTRA_DIR_PATH = "";
    public static String DATA_DIR_PATH = "";
    public static String CACHE_DIR_PATH = "";
    public static String EMOTION_DIR_PATH = "";

    /**
     * 用户卸载之后仍然存在的数据库
     */
    public static String OUTOF_DATA_PATH = "";

    public static String IMAGE_FILE_LOCATION;
    public static Uri IMAGE_FILE_LOCATION_URI;

    public static String AUDIO_FILE_LOCATION;
    public static Uri AUDIO_FILE_LOCATION_URI;

    public static String CHAOCHAO_EMOJI_PACKAGE;
    public static String SCHOOL_GAOZHONG_PACKAGE;
    public static String SCHOOL_CHUZHONG_PACKAGE;

    public static void init() {
        Context context = App.THIS;

        EXTRA_DIR_PATH = getDiskCacheDir(context, "extra");
        makeDirectory(EXTRA_DIR_PATH);

        DATA_DIR_PATH = getDiskCacheDir(context, "data");
        makeDirectory(DATA_DIR_PATH);

        CACHE_DIR_PATH = getDiskCacheDirForCache(context);
        makeDirectory(CACHE_DIR_PATH);

        EMOTION_DIR_PATH = DATA_DIR_PATH + "emotion";
        makeDirectory(EMOTION_DIR_PATH);

        OUTOF_DATA_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getPackageName() + "/";
        makeDirectory(OUTOF_DATA_PATH);

        File file = null;
        IMAGE_FILE_LOCATION = DATA_DIR_PATH + File.separator + "temp.jpg";
        file = new File(IMAGE_FILE_LOCATION);
        IMAGE_FILE_LOCATION_URI = Uri.fromFile(file);

        AUDIO_FILE_LOCATION = DATA_DIR_PATH + File.separator + "temp.mp3";
        file = new File(AUDIO_FILE_LOCATION);
        AUDIO_FILE_LOCATION_URI = Uri.fromFile(file);

        CHAOCHAO_EMOJI_PACKAGE = FileUtils.EMOTION_DIR_PATH + File.separator + "chaochao.zip";
        SCHOOL_GAOZHONG_PACKAGE = FileUtils.DATA_DIR_PATH + File.separator + "gaozhong.zip";
        SCHOOL_CHUZHONG_PACKAGE = FileUtils.DATA_DIR_PATH + File.separator + "chuzhong.zip";
    }

    /**
     * 临时文件缓存路径
     */
    public static String getCachedPath(String url) {
        return CACHE_DIR_PATH + MD5Utils.getMD5String(url);
    }

    /**
     * 原图图片的缓存路径
     */
    public static String getExtraPath(String url) {
        return EXTRA_DIR_PATH + MD5Utils.getMD5String(url);
    }

    /**
     * 附件的缓存路径， 图片文件参考 getExtraImagePath(String url, String filename)
     */
    public static String getExtraPath(String url, String filename) {
        String suffix = getSuffix(filename);
        return EXTRA_DIR_PATH + MD5Utils.getMD5String(url) + "." + suffix;
    }

    /**
     * 附件webp图片的缓存路径
     */
    private static String getExtraWebpPath(String url) {
        return EXTRA_DIR_PATH + MD5Utils.getMD5String(url) + ".webp";
    }

    /**
     * 附件的缓存路径,仅仅使用URL
     */
    public static String getExtraPathFromUrl(String url) {
        return EXTRA_DIR_PATH + MD5Utils.getMD5String(url);
    }

    /**
     * 存放固定的数据，不能删除
     */
    public static String getDataPath(String filename) {
        return DATA_DIR_PATH + filename;
    }

    /**
     * 存放外置sd卡的目录
     */
    public static String getOutofDataPath(String filename) {
        return OUTOF_DATA_PATH + filename;
    }

    // ///////////////////////////////////////////////////////
    /*
     * 使用FileInputStream读取文件
     */
    public static String readFile(String fileName) {
        FileInputStream fis = null;
        try {
            File file = new File(fileName);
            fis = new FileInputStream(file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            return new String(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /*
     * 使用BufferedReader读取文件,返回String
     */
    public static String readFileByLine(String fileName) {
        try {
            File file = new File(fileName);
            BufferedReader bf = new BufferedReader(new FileReader(file));
            String line = "";
            StringBuilder result = new StringBuilder();
            while (line != null) {
                line = bf.readLine();
                if (line == null) {
                    break;
                }
                result.append(line.trim());
            }
            bf.close();
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * 使用BufferedReader读取文件,返回LinkedList<String>
     */
    public static LinkedList<String> readFileByLine2(String fileName) {
        // ArrayList<String> strList = new ArrayList<String>();
        LinkedList<String> strList = new LinkedList<String>();
        try {
            File file = new File(fileName);
            BufferedReader bf = new BufferedReader(new FileReader(file));
            String line = "";
            while (line != null) {
                line = bf.readLine();
                if (line == null) {
                    break;
                }
                strList.add(line.trim());
            }
            bf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strList;
    }

    /*
     * 使用FileOutputStream写入文件 windows 的换行符师“\r\n” linux 的换行符师“\n” mac 的换行符师“\r”
     */
    public static void writeFile(String filename, String content, boolean isAppend) {
        BufferedWriter bw = null;
        try {
            File file = new File(filename);
            if (isAppend) {
                // 第二个参数意义是说是否以append方式添加内容
                bw = new BufferedWriter(new FileWriter(file, true));
                bw.write(content);
                bw.flush();
            } else {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(content.getBytes());
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void overWriteFile(String filepath, String content) {
        deleteFile(filepath);
        writeFile(filepath, content, true);
    }

    /*
     * 将大小转化为变现形式M,K
     */
    public static String formatSize(long filesize) {
        float size = (float) filesize;
        size = size / 1024f;
        if (size < 1024f) {
            DecimalFormat fnum = new DecimalFormat("##0.00");
            return fnum.format(size) + " KB";
        } else {
            size = size / 1024f;
            DecimalFormat fnum = new DecimalFormat("##0.00");
            return fnum.format(size) + " MB";
        }
    }

    // //////////////////////////////////////////////
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    public static boolean DeleteFolder(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        if (!file.exists()) {
            return flag;
        } else {
            if (file.isFile()) {
                return deleteFile(sPath);
            } else {
                return deleteDirectory(sPath);
            }
        }
    }

    public static boolean deleteDirectory(String sPath) {
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        if (files == null) {
            return false;
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            } else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag)
            return false;
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    // ///////////////////////////////////////////////////////

    private static String getDiskCacheDir(Context context, String uniqueName) {
        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState()) ? getExternalCacheDir(context).getPath() : context
                        .getCacheDir().getPath();
        return cachePath + File.separator + uniqueName + File.separator;
    }

    private static String getDiskCacheDirForCache(Context context) {
        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState()) ? getExternalCacheDir(context).getPath() : context
                        .getCacheDir().getPath();
        return cachePath + File.separator + "cache" + File.separator;
    }

    private static File getExternalCacheDir(Context context) {
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/";// +
                                                                                  // "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    // ////////////////////////////////////////////////////////////

    public static String getFileContent(String filepath) {
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                return "";
            }
            BufferedReader bf = new BufferedReader(new FileReader(file));
            String line = "";
            StringBuilder result = new StringBuilder();
            while (line != null) {
                line = bf.readLine();
                if (line == null) {
                    break;
                }
                result.append(line.trim());
            }
            bf.close();
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static ArrayList<String> getFileContentByLine(String filepath) {
        ArrayList<String> result = new ArrayList<>();
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                return result;
            }
            BufferedReader bf = new BufferedReader(new FileReader(file));
            String line = "";
            while (line != null) {
                line = bf.readLine();
                if (line == null) {
                    break;
                }
                result.add(line.trim());
            }
            bf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int getFileContentLineCount(String filepath) {
        int count = 0;
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                return count;
            }
            BufferedReader bf = new BufferedReader(new FileReader(file));
            String line = "";
            while (line != null) {
                line = bf.readLine();
                if (line == null) {
                    break;
                }
                count++;
            }
            bf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long
     */
    public static long getFolderSize(File file) {

        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);

                } else {
                    size = size + fileList[i].length();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    // 统计手机缓存
    public static long getTotalCacheSize() {
        return FileUtils.getFolderSize(new File(FileUtils.CACHE_DIR_PATH))
                + FileUtils.getFolderSize(new File(FileUtils.EXTRA_DIR_PATH));
    }

    // 删除指定文件夹下的所有文件，保留文件夹
    public static boolean clearFolder(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        if (tempList.length == 0) {
            flag = true;
        } else {
            File temp = null;
            for (int i = 0; i < tempList.length; i++) {
                if (path.endsWith(File.separator)) {
                    temp = new File(path + tempList[i]);
                } else {
                    temp = new File(path + File.separator + tempList[i]);
                }
                if (temp.isFile()) {
                    flag = temp.delete();
                    if (!flag) {
                        // 如果删除文件异常，那么直接结束
                        return flag;
                    }
                }
                if (temp.isDirectory()) {
                    flag = clearFolder(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                    DeleteFolder(path + "/" + tempList[i]);// 再删除空文件夹
                    if (!flag) {
                        return flag;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 主动清除缓存
     */
    public static boolean clearCache() {
        if (clearFolder(CACHE_DIR_PATH) && clearFolder(EXTRA_DIR_PATH)) {
            File file = new File(CACHE_DIR_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }

            file = new File(EXTRA_DIR_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }

            return true;
        }
        return false;
    }

    public static boolean exist(String filepath) {
        boolean result = false;
        File file = new File(filepath);
        result = file.exists();
        file = null;
        return result;
    }

    // 从raw获取文件内容
    public static String getFromRaw(int rawId) {
        try {
            InputStreamReader inputReader = new InputStreamReader(App.THIS.getResources()
                    .openRawResource(rawId));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(App.THIS.getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static ArrayList<String> getFromAssetsByLine(String fileName) {
        ArrayList<String> result = new ArrayList<>();
        try {
             InputStreamReader inputReader = new InputStreamReader(App.THIS.getAssets().open(fileName));
            BufferedReader bf = new BufferedReader(inputReader);
            String line = "";
            while (line != null) {
                line = bf.readLine();
                if (line == null) {
                    break;
                }
                result.add(line.trim());
            }
            bf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 获取图库图片路径
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for MediaStore Uris, and other
     * file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
            String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * 获取SD卡上所有表情包的名称
     */
    public static ArrayList<String> getExtraEmotionPackageName() {
        final ArrayList<String> extra_emotion_name = new ArrayList<String>();
        final String extra_emtion_folder_path = DATA_DIR_PATH + "emotion";
        final File emotion_folder = new File(extra_emtion_folder_path);
        if (emotion_folder.exists()) {
            final File[] folders = emotion_folder.listFiles();
            for (File folder : folders) {
                if (folder.isDirectory()) {
                    extra_emotion_name.add(getFileNameNoEx(folder.getName()));
                }
            }
        }
        return extra_emotion_name;
    }

    /**
     * 获取SD卡上所有表情包的icon
     */
    public static ArrayList<String> getExtraEmotionIcon() {
        final ArrayList<String> extra_emotion_icon = new ArrayList<String>();
        final String extra_emtion_folder_path = DATA_DIR_PATH + "emotion";
        final File emotion_folder = new File(extra_emtion_folder_path);
        if (emotion_folder.exists()) {
            final File[] folders = emotion_folder.listFiles();
            for (File folder : folders) {
                if (folder.isDirectory()) {
                    final File[] files = folder.listFiles();
                    for (File file : files) {
                        if (file.getName().endsWith("png") && !file.getName().contains("_")) {
                            extra_emotion_icon.add(file.getAbsolutePath());
                        }
                    }
                }
            }
        }
        return extra_emotion_icon;
    }

    /**
     * 获取不带扩展名的文件名
     *
     * @param filePath
     */
    public static String getFileNameNoEx(String filePath) {
        int start = filePath.lastIndexOf("/");
        int dot = filePath.lastIndexOf('.');
        if (start == -1) {
            if (dot == -1) {
                return filePath;
            } else {
                return filePath.substring(0, dot);
            }
        } else {
            if (dot == -1) {
                return filePath.substring(start + 1);
            } else {
                return filePath.substring(start + 1, dot);
            }
        }
    }

    /**
     * 获取带扩展名的文件名
     *
     * @param filePath
     */
    public static String getFileNameWithSuffix(String filePath) {
        int start = filePath.lastIndexOf("/");
        if (start == -1) {
            start = 0;
        }
        return filePath.substring(start + 1);
    }

    /*
     * 获取文件的扩展名
     */
    public static String getFileSuffixName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /*
     * 组合缓存文件名
     */
    public static String combineFileName(String category, int... args) {
        for (int i = 0; args != null && i < args.length; i++) {
            category += "_" + args[i];
        }
        return category;
    };

    // 获取文件类型
    public static String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
        // 获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /* 获取文件的后缀名 */
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "")
            return type;
        // 在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) {

            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    public static boolean copyFile(String oldPath, String newPath) {
        InputStream inStream = null;
        FileOutputStream fs = null;
        try {
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newfile = new File(newPath);
            if (newfile.exists()) {
                newfile.delete();
            }
            if (oldfile.exists()) {
                inStream = new FileInputStream(oldPath);
                fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamUtils.closeSilently(inStream);
            StreamUtils.closeSilently(fs);
        }
        return false;
    }

    /**
     * 复制文件（夹）到一个目标文件夹
     * 
     * @param resFile 源文件（夹）
     * @param objFolderFile 目标文件夹
     * @throws IOException 异常时抛出
     */
    public static void copy(File resFile, File objFolderFile) throws IOException {
        if (!resFile.exists())
            return;
        if (!objFolderFile.exists())
            objFolderFile.mkdirs();
        if (resFile.isFile()) {
            File objFile = new File(objFolderFile.getPath() + File.separator + resFile.getName());
            // 复制文件到目标地
            InputStream ins = new FileInputStream(resFile);
            FileOutputStream outs = new FileOutputStream(objFile);
            byte[] buffer = new byte[1024 * 512];
            int length;
            while ((length = ins.read(buffer)) != -1) {
                outs.write(buffer, 0, length);
            }
            ins.close();
            outs.flush();
            outs.close();
        } else {
            String objFolder = objFolderFile.getPath() + File.separator + resFile.getName();
            File _objFolderFile = new File(objFolder);
            _objFolderFile.mkdirs();
            for (File sf : resFile.listFiles()) {
                copy(sf, new File(objFolder));
            }
        }
    }

    /**
     * 将文件（夹）移动到令一个文件夹
     * 
     * @param resFile 源文件（夹）
     * @param objFolderFile 目标文件夹
     * @throws IOException 异常时抛出
     */
    public static void move(File resFile, File objFolderFile) throws IOException {
        copy(resFile, objFolderFile);
        delete(resFile);
    }

    /**
     * 删除文件（夹）
     * 
     * @param file 文件（夹）
     */
    public static void delete(File file) {
        if (!file.exists())
            return;
        if (file.isFile()) {
            file.delete();
        } else {
            for (File f : file.listFiles()) {
                delete(f);
            }
            file.delete();
        }
    }

    /**
     * 聊天中，发送语音和图片的时候，第一步先把临时文件放在extra文件夹下
     * isNeedRename:是否需要rename步骤，只要这个filename不是用户的资源，而只是app的资源，就需要rename
     */
    public static boolean chatSaveExtra(String tmpFilePath, long local_timestamp, String filename,
            boolean isNeedRename) {
        // 1.检查原文件是否存在
        File sourceFile = new File(tmpFilePath);
        if (!sourceFile.exists()) {
            return false;
        }

        // 2.检查文件夹是否存在
        File extraFolder = new File(EXTRA_DIR_PATH);
        if (!extraFolder.exists()) {
            extraFolder.mkdirs();
        }

        File newFile = new File(FileUtils.getExtraPath(String.valueOf(local_timestamp), filename));
        // 3.rename ：图片会将原来的用户的图片给干掉了~~~只有语音需要rename
        if (isNeedRename && sourceFile.renameTo(newFile)) {
            return true;
        }
        // 4.rename失败，尝试copy
        if (FileUtils.copyFile(tmpFilePath, newFile.getAbsolutePath())) {
            return true;
        }

        return false;
    }

    public static boolean makeDirectory(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (!file.isDirectory()) {
                file.delete();
                return file.mkdirs();
            } else {
                return true;
            }
        } else {
            return file.mkdirs();
        }
    }

    public static String getSuffix(String filename) {
        String suffix = "tmp";
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                suffix = filename.substring(dot + 1);
            }
        }
        return suffix;
    }

    public static String[][] MIME_MapTable = {
            // {后缀名，MIME类型}
            {
                    ".c", "text/plain"
            },
            {
                    ".class", "application/octet-stream"
            },
            {
                    ".conf", "text/plain"
            },
            {
                    ".cpp", "text/plain"
            },
            {
                    ".doc", "application/msword"
            },
            {
                    ".docx",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            },
            {
                    ".xls", "application/vnd.ms-excel"
            },
            {
                    ".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            },
            {
                    ".exe", "application/octet-stream"
            },
            {
                    ".gz", "application/x-gzip"
            },
            {
                    ".h", "text/plain"
            },
            {
                    ".htm", "text/html"
            },
            {
                    ".html", "text/html"
            },
            {
                    ".jar", "application/java-archive"
            },
            {
                    ".java", "text/plain"
            },
            {
                    ".js", "application/x-javascript"
            },
            {
                    ".log", "text/plain"
            },
            {
                    ".mpc", "application/vnd.mpohun.certificate"
            },
            {
                    ".msg", "application/vnd.ms-outlook"
            },
            {
                    ".pdf", "application/pdf"
            },
            {
                    ".pps", "application/vnd.ms-powerpoint"
            },
            {
                    ".ppt", "application/vnd.ms-powerpoint"
            },
            {
                    ".pptx",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            },
            {
                    ".prop", "text/plain"
            }, {
                    ".rc", "text/plain"
            },
            {
                    ".rmvb", "audio/x-pn-realaudio"
            }, {
                    ".rtf", "application/rtf"
            },
            {
                    ".sh", "text/plain"
            }, {
                    ".tar", "application/x-tar"
            },
            {
                    ".tgz", "application/x-compressed"
            }, {
                    ".txt", "text/plain"
            },
            {
                    ".wps", "application/vnd.ms-works"
            }, {
                    ".xml", "text/plain"
            },
            {
                    ".z", "application/x-compress"
            }, {
                    ".zip", "application/x-zip-compressed"
            },
            {
                    "", "*/*"
            }
    };

    public static boolean copyAssetDataToSD(String assetFilePath, String outFilePath) {
        InputStream input = null;
        OutputStream output = null;
        try {
            output = new FileOutputStream(outFilePath);
            input = App.THIS.getAssets().open(assetFilePath);
            byte[] buffer = new byte[1024];
            int length = input.read(buffer);
            while (length > 0) {
                output.write(buffer, 0, length);
                length = input.read(buffer);
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException e2) {
                // TODO: handle exception
            }
        }
        return false;
    }

    public static void saveFileContent(String filepath, String content) {
        FileUtils.deleteFile(filepath);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filepath);
            out.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            FileUtils.deleteFile(filepath);
        } finally {
            StreamUtils.closeSilently(out);
        }
    }
}
