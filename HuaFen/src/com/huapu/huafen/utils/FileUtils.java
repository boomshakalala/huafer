package com.huapu.huafen.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.huapu.huafen.MyApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author liang_xs
 * @date 2016-03-27
 */
public class FileUtils {
    public static final String UPDATE_FOLDER_NAME = "update";
    public static final String LOGO_FOLDER_NAME = "logo";
    public static final String CAMERA_FOLDER_NAME = "camera";
    private final static String IMAGE_FOLDER_NAME = "/AndroidImage";

    /**
     * android/app包名文件夹的路径
     */
    public static String SD_APP_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/Android/data/"
            + MyApplication.getApplication().getPackageName();
    /**
     * data/app包名文件夹的路径
     */
    public static String DATA_APP_PATH = MyApplication.getApplication()
            .getFilesDir().getAbsolutePath();

    public static boolean isSDCardExist() {
        if (Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    // 缓存的文件
    public static final String CACHE_DIR = "cache";
    // 保存图片的路径
    public static final String ICON_DIR = "icon";
    public static final String VIDEO = "video";
    public static final String H_DIR = "huafer";
    // 下载文件的路径 音频
    public static final String DOWNLOAD_DIR = "download";
    public static final String VOICE = "voice";
    public static final String VOICENOTICE = "voicenotice";

    public static String getCacheDir() {
        return getDir(CACHE_DIR);
    }

    public static String getHDir() {
        return getDir(H_DIR);
    }

    public static String getIconDir() {
        return getDir(ICON_DIR);
    }

    public static String getVideoDir() {
        return getDir(VIDEO);
    }


    private static String baseVideoPath = getVideoDir();
    private static String tempPath = baseVideoPath + File.separator + "temp";
    private static String videoPath = baseVideoPath + File.separator + "video";
    private static String uploadPath = baseVideoPath + File.separator + "upload";

    public static String getTempPath() {
        return tempPath;
    }

    public static String getVideoPath() {
        return videoPath;
    }

    public static String getUploadPath() {
        return uploadPath;
    }

    public static String getDownloadDir() {
        return getDir(DOWNLOAD_DIR);
    }

    public static String getVoiceDir() {
        return getDir(VOICE);
    }

    public static String getVoiceNoticeDir() {
        return getDir(VOICENOTICE);
    }

    private static String getDir(String name) {
        StringBuilder sb = new StringBuilder();
        if (isSDCardAvailable()) {
            sb.append(getExternalStoragePath());
        } else {
            sb.append(getCachePath());
        }
        sb.append(name);
        sb.append(File.separator);
        String path = sb.toString();
        if (createDirs(path)) {
            return path;
        } else {
            return null;
        }
    }

    private static String getImageDirectory() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                SD_APP_PATH + IMAGE_FOLDER_NAME : DATA_APP_PATH + IMAGE_FOLDER_NAME;
    }

    private static String getVideoDirectory() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                SD_APP_PATH + videoPath : DATA_APP_PATH + videoPath;
    }

    public static boolean saveBitmap(String fileName, Bitmap bitmap) {
        if (bitmap == null) {
            return false;
        }

        if (bitmap.isRecycled())
            return false;

        try {
            String path = getImageDirectory();
            File folderFile = new File(path);
            if (!folderFile.exists()) {
                folderFile.mkdir();
            }
            File file = new File(path + File.separator + fileName);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Bitmap getBitmap(String fileName) {
        Bitmap bmp = BitmapFactory.decodeFile(getImageDirectory() + File.separator + fileName);
        //此写法是为了防止用户插拔sd卡，导致图片路径错误，无法取出图片
        if (bmp == null) {
            String path = getImageDirectory();
            if ((SD_APP_PATH + IMAGE_FOLDER_NAME).equals(path)) {
                path = DATA_APP_PATH + IMAGE_FOLDER_NAME;
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 2;   //width，hight设为原来的二分一

            bmp = BitmapFactory.decodeFile(path + File.separator + fileName, options);
        }
        return bmp;
    }


    /**
     * 创建文件夹
     */
    private static boolean createDirs(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            return file.mkdirs();
        }
        return true;
    }

    private static String getCachePath() {
        File f = MyApplication.getApplication().getCacheDir();
        return f.getAbsolutePath() + "/";
    }

    /**
     * 返回sd卡的路径
     *
     * @return
     */
    private static String getExternalStoragePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(File.separator);
        sb.append("Android/data/");
        sb.append(MyApplication.getApplication().getPackageName());
        sb.append(File.separator);
        return sb.toString();
    }

    /**
     * 判断sd卡是否挂载
     */
    public static boolean isSDCardAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            return true;
        } else {
            return false;
        }
    }

    private static FileUtils mInstance;

    public static FileUtils getInst() {
        if (mInstance == null) {
            synchronized (FileUtils.class) {
                if (mInstance == null) {
                    mInstance = new FileUtils();
                }
            }
        }
        return mInstance;
    }


    public static String getHCameraPath() {
        String photoPath = FileUtils.getHDir();
        File dir = new File(photoPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String imageName = DateTimeUtils.dtFormat(new Date(), "yyyyMMddHHmmss")
                + ".jpg";
        String fileName = photoPath + "/" + imageName;
//        String fileName = "";
        // 文件夹路径
//        String pathUrl = FileUtils.getInst().getPhotoSavedPath() + "/";
//        String pathUrl = FileUtils.SD_APP_PATH + File.separator + ICON_DIR + "/";
//        String imageName = DateTimeUtils.dtFormat(new Date(), "yyyyMMddHHmmss")+".jpg";
//        File file = new File(pathUrl);
//        file.mkdirs();// 创建文件夹
//        fileName = pathUrl + imageName;
        return fileName;
    }

    public static String getCameraPhotoPath() {
        String photoPath = FileUtils.getIconDir();
        File dir = new File(photoPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String imageName = DateTimeUtils.dtFormat(new Date(), "yyyyMMddHHmmss")
                + ".jpg";
        String fileName = photoPath + "/" + imageName;
//        String fileName = "";  
        // 文件夹路径  
//        String pathUrl = FileUtils.getInst().getPhotoSavedPath() + "/";  
//        String pathUrl = FileUtils.SD_APP_PATH + File.separator + ICON_DIR + "/";  
//        String imageName = DateTimeUtils.dtFormat(new Date(), "yyyyMMddHHmmss")+".jpg";  
//        File file = new File(pathUrl);  
//        file.mkdirs();// 创建文件夹  
//        fileName = pathUrl + imageName;  
        return fileName;
    }

    /**
     * 保存图片
     */
    public static String saveBitmap(Bitmap bm, String folder, String picName) {
        if (bm == null || TextUtils.isEmpty(folder) || TextUtils.isEmpty(picName)) {
            return "";
        }
        try {
            File dir = new File(folder);
            File f = new File(folder, picName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            LogUtil.i("", "已经保存");
            return folder + picName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static File getSDCardRoot() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * @return boolean
     * @Title: delAllFile
     * @Description: 删除除strFileName外的所有文件
     * @author liang_xs
     */
    public static boolean delAllFile(String strPath, String strFileName) {
        boolean flag = false;
        File file = new File(strPath);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (strPath.endsWith(File.separator)) {
                temp = new File(strPath + tempList[i]);
            } else {
                temp = new File(strPath + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                if (!TextUtils.isEmpty(strFileName)
                        && temp.getName().equals(strFileName)) {
                    continue;
                } else {
                    temp.delete();
                }
            }
            if (temp.isDirectory()) {
                continue;
                // flag = true;
            }
        }
        return flag;
    }

    /**
     * @return boolean
     * @Title: delAllFile
     * @author liang_xs
     */
    public static boolean delAllFile(String strPath) {
        boolean flag = false;
        File file = new File(strPath);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        if (file == null) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (strPath.endsWith(File.separator)) {
                temp = new File(strPath + tempList[i]);
            } else {
                temp = new File(strPath + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                continue;
                // flag = true;
            }
        }
        return flag;
    }

    /**
     * @return File
     * @Title: creatDir
     * @Description: 创建指定名称的文件夹
     * @author liang_xs
     */
    public static File creatDir(String foldName) {
        File dir;
        if (isSDCardExist()) {
            dir = new File(FileUtils.SD_APP_PATH, foldName);
            if (!dir.exists()) {
                try {
                    dir.mkdirs();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            dir = MyApplication.getApplication().getFilesDir();
        }
        return dir;
    }

    /**
     * 判断制定文件是否存在
     */
    public boolean isFileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * @return int
     * @Title: getSDDirSize
     * @Description: 获取文件夹大小
     * @author liang_xs
     */
    public int getSDDirSize(String dirName) {
        File dir = new File(SD_APP_PATH + dirName);
        if (dir == null || !dir.exists() || dir.isFile()) {
            return 0;
        }
        return (int) (dir.length() / 1024 / 1024);
    }

    /**
     * @return int
     * @Title: getFileSize
     * @Description: 获取文件大小
     * @author liang_xs
     */
    public static String getFileSize(String strFilePath) {
        File file = new File(strFilePath);
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }
        long size = file.length();
        return formatSize(size);
    }

    /**
     * @return String
     * @Title: formatSize
     * @Description: 转换大小
     * @author liang_xs
     */
    public static String formatSize(long size) {
        String strSuffix = null;
        float fSize = 0;

        if (size >= 1024) {
            strSuffix = "KB";
            fSize = size / 1024;
            if (fSize >= 1024) {
                strSuffix = "MB";
                fSize /= 1024;
            }
            if (fSize >= 1024) {
                strSuffix = "GB";
                fSize /= 1024;
            }
        } else {
            fSize = size;
        }
        java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
        StringBuilder resultBuffer = new StringBuilder(df.format(fSize));
        if (strSuffix != null || "".equals(strSuffix))
            resultBuffer.append(strSuffix);
        return resultBuffer.toString();
    }

    /**
     * 删除一个目录（可以是非空目录）
     *
     * @param
     */
    public static boolean delDir(String strPath) {
        File dir = new File(strPath);
        if (dir == null || !dir.exists() || dir.isFile()) {
            return false;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                delDir(strPath);// 递归
            }
        }
        dir.delete();
        return true;
    }

    /**
     * 判断文件是否可写
     */
    public static boolean isWriteable(String path) {
        try {
            if (StringUtils.isEmpty(path)) {
                return false;
            }
            File f = new File(path);
            return f.exists() && f.canWrite();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 修改文件的权限,例如"777"等
     */
    public static void chmod(String path, String mode) {
        try {
            String command = "chmod " + mode + " " + path;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (Exception e) {
        }
    }

    /**
     * @return void
     * @Title: installApp
     * @Description: 安装
     * @author liang_xs
     */
    public static void installApp(Context context, String strFilePath) {
        FileUtils.chmod(strFilePath, "704");
        // if (!FileUtils.isWriteable(strFilePath)) {
        // try {
        // FileOutputStream file = context.openFileOutput(strFilePath,
        // Context.MODE_WORLD_READABLE);
        // } catch (FileNotFoundException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }
        File file = new File(strFilePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    private final static int kSystemRootStateUnknow = -1;
    private final static int kSystemRootStateDisable = 0;
    private final static int kSystemRootStateEnable = 1;
    private static int systemRootState = kSystemRootStateUnknow;

    /**
     * 判断手机是否root权限
     */
    public static boolean isRootSystem() {
        if (systemRootState == kSystemRootStateEnable) {
            return true;
        } else if (systemRootState == kSystemRootStateDisable) {
            return false;
        }
        File f = null;
        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/",
                "/system/sbin/", "/sbin/", "/vendor/bin/"};
        try {
            for (int i = 0; i < kSuSearchPaths.length; i++) {
                f = new File(kSuSearchPaths[i] + "su");
                if (f != null && f.exists()) {
                    systemRootState = kSystemRootStateEnable;
                    return true;
                }
            }
        } catch (Exception e) {
        }
        systemRootState = kSystemRootStateDisable;
        return false;
    }


    public static void writeString2File(String text, String filePath, String fileName) {
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(filePath + fileName);
            fos.write(text.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void makeDir(File file) {
        File tempPath = new File(file.getParent());
        if (!tempPath.exists()) {
            tempPath.mkdirs();
        }
    }


    public static String getDefaultStoragePath(Context context) {
        //如果存在外部存储设备则直接返回
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        //如果未挂载外部存储设备,则遍历内部存储卡
        String path = null;
        ArrayList<String> devMountList = getDevMountList();

        for (String devMount : devMountList) {
            File file = new File(devMount);
            if (file.isDirectory() && file.canWrite()) {
                path = file.getAbsolutePath();
                long timeStamp = System.currentTimeMillis();
                File testWritable = new File(path, "test_" + timeStamp);
                if (testWritable.mkdirs()) {
                    testWritable.delete();
                    break;
                } else {
                    path = null;
                }
            }
        }

        if (path != null) {
            return new File(path).getAbsolutePath();
        }

        return context.getCacheDir().getAbsolutePath();
    }

    /**
     * 遍历 "system/etc/vold.fstab” 文件，获取全部的Android的挂载点信息
     *
     * @return
     */
    private static ArrayList<String> getDevMountList() {
        String[] toSearch = readFile("/etc/vold.fstab").split(" ");
        ArrayList<String> out = new ArrayList<String>();
        for (int i = 0; i < toSearch.length; i++) {
            if (toSearch[i].contains("dev_mount")) {
                if (new File(toSearch[i + 2]).exists()) {
                    out.add(toSearch[i + 2]);
                }
            }
        }
        return out;
    }

    public static String readFile(String filePath) {
        String fileContent = "";
        File file = new File(filePath);
        if (file == null || !file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file));
            reader = new BufferedReader(is);
            String line = null;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                fileContent += line + " ";
            }
            reader.close();
            return fileContent;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileContent;
    }


    /**
     * 获取单个文件的MD5值！
     *
     * @param file
     * @return
     */


    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }
}
