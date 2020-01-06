package com.novv.dzdesk.util;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import com.novv.dzdesk.R;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.*;

public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    private static final int BUFFER_SIZE = 1024;


    public static boolean renameFile(String sourcePath, String destPath) {
        return new File(sourcePath).renameTo(new File(destPath));
    }

    /**
     * 获取文件扩展名
     *
     * @param file 指定文件
     * @return String
     */
    public static String getExtName(File file) {
        String name = file.getName();
        int index = name.lastIndexOf('.');
        if (index < 0) {
            return "";
        }
        return name.substring(index + 1);
    }

    public static String getExtName(String name) {
        int index = name.lastIndexOf('.');
        if (index < 0) {
            return "";
        }
        return name.substring(index + 1);
    }

    /**
     * 获取文件名 如：sdcard/my.txt , file name is my.txt
     */
    public static String getFileName(File tempFile) {
        String path = tempFile.getAbsolutePath();
        path = getFileName(path);
        return path;
    }

    /**
     * 获取文件名 如：sdcard/my.txt , file name is my.txt
     */
    public static String getFileName(String path) {
        if (path == null) {
            return "";
        }
        int lastIndex = path.lastIndexOf("/");
        if (lastIndex == -1) {
            return path;
        }
        return path.substring(lastIndex + 1);
    }

    /**
     * 获取文件名 如：sdcard/my.txt , file name is my
     */
    public static String getFileNameWithoutSuffix(String path) {
        String FileName = getFileName(path);
        if (FileName == null) {
            return FileName;
        }
        String[] NameArray = FileName.split("\\.");
        if (NameArray != null && NameArray.length > 0) {
            return NameArray[0];
        } else {
            return FileName;
        }
    }

    /**
     * 根据文件路径删除文件
     */
    public static boolean deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return true;
        }
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }

    /**
     * 删除文件夹中的文件（文件夹本身和子文件夹不会删除）
     *
     * @param dir 指定文件夹
     */
    public static void deleteAllFilesIn(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.isDirectory()) {
                        file.delete();
                    }
                }
            }
        }
    }

    /**
     * 删除文件夹（递归删除）
     *
     * @param dir 指定文件夹
     */
    public static void deleteDir(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDir(file);
                }
            }
            dir.delete();
        }
    }

    /**
     * 删除文件夹(递归删除)
     */
    public static void deleteFolder(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            if (dir.isDirectory()) {
                File[] files = null;
                try {
                    files = dir.listFiles();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (files == null) {
                    return;
                }
                for (File file : files) {
                    if (file == null) {
                        continue;
                    }
                    deleteFolder(file.getAbsolutePath());
                }
            }
            dir.delete();
        }
    }

    /**
     * 读输入流并保存为字节数组
     *
     * @param is     指定输入流
     * @param ungzip 是否使用gzip解压输入流
     * @return byte[]
     */
    public static byte[] readInputStream(InputStream is, boolean ungzip) {
        if (is == null) {
            LogUtil.w(TAG, "readInputStream", "input stream is null");
            return null;
        }

        ByteArrayOutputStream baos = null;
        try {
            if (ungzip) {
                is = new GZIPInputStream(is);
            }
            byte[] buf = new byte[BUFFER_SIZE];
            baos = new ByteArrayOutputStream();
            int len = 0;
            while ((len = is.read(buf)) > 0) {
                baos.write(buf, 0, len);
            }
            baos.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 读文件并保存为字节数组
     *
     * @param file   指定文件
     * @param ungzip 是否使用gzip解压输入流
     * @return byte[]
     */
    public static byte[] readFile(File file, boolean ungzip) {
        if (file == null) {
            LogUtil.w(TAG, "readFile", "file is null");
            return null;
        }

        try {
            return readInputStream(new FileInputStream(file), ungzip);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将字节数组写入目标文件
     *
     * @param target 目标文件
     * @param data   需要写入的字节数组
     * @param gzip   是否使用gzip压缩数据
     * @return 是否成功
     */
    public static boolean writeFile(File target, byte[] data, boolean gzip) {
        if (target == null || data == null) {
            LogUtil.w(TAG, "writeFile", "invalid argument");
            return false;
        }

        OutputStream os = null;
        try {
            File dir = target.getParentFile();
            if (dir != null) {
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(target);
            if (gzip) {
                os = new GZIPOutputStream(fos);
            } else {
                os = new BufferedOutputStream(fos);
            }
            os.write(data);
            os.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 将字符串按照指定字符集进行编码后写入目标文件
     *
     * @param target  目标文件
     * @param str     需要写入的字符串
     * @param charset 指定的字符集，如UTF-8
     * @param gzip    是否使用gzip压缩数据
     * @return 是否成功
     */
    public static boolean writeFile(File target, String str, String charset,
            boolean gzip) {
        if (str == null || charset == null) {
            LogUtil.w(TAG, "writeFile", "invalid argument");
            return false;
        }

        byte[] data = null;
        try {
            data = str.getBytes(charset);
            return writeFile(target, data, gzip);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将指定输入流写入目标文件
     *
     * @param target 目标文件
     * @param is     指定输入流
     * @param gzip   是否使用gzip压缩数据
     * @return 是否成功
     */
    public static boolean writeFile(File target, InputStream is, boolean gzip) {
        if (is == null) {
            LogUtil.w(TAG, "writeFile", "input stream is null");
            return false;
        }

        OutputStream os = null;
        try {
            File dir = target.getParentFile();
            if (dir != null) {
                dir.mkdirs();
            }
            os = new FileOutputStream(target);
            if (gzip) {
                os = new GZIPOutputStream(os);
            }
            byte buf[] = new byte[BUFFER_SIZE];
            int len = 0;
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
            os.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 将源文件拷贝到目标文件（如果目标文件存在，将会被覆盖）
     *
     * @param source 源文件
     * @param target 目标文件
     * @return 是否成功
     */
    public static boolean copyFile(File source, File target) {
        InputStream is = null;
        OutputStream os = null;
        try {
            File dir = target.getParentFile();
            if (dir != null) {
                dir.mkdirs();
            }
            is = new FileInputStream(source);
            os = new FileOutputStream(target);
            byte buf[] = new byte[BUFFER_SIZE];
            int len = 0;
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    // 文件拷贝
    // 要复制的目录下的所有非子目录(文件夹)文件拷贝
    public static int copyFileWithPath(String fromFile, String toFile) {
        try {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return 0;

        } catch (Exception ex) {
            return -1;
        }
    }

    /**
     * 将源文件移动到目标文件（如果目标文件存在，将会被覆盖）
     *
     * @param source 源文件
     * @param target 目标文件
     * @return 是否成功
     */
    public static boolean moveFile(File source, File target) {
        if (!copyFile(source, target)) {
            return false;
        }
        return source.delete();
    }

    /**
     * 将assets中的文件拷贝到目标磁盘文件（如果目标文件存在，将会被覆盖）
     *
     * @param context 上下文
     * @param source  源文件在assets中的路径
     * @param target  目标磁盘文件
     * @return 是否成功
     */
    public static boolean copyFileFromAssetsToSDCard(Context context,
            String source, File target) {
        if (context == null || source == null || source.length() == 0
                || target == null) {
            LogUtil.w(TAG, "copyFileFromAssetsToSDCard", "invalid argument");
            return false;
        }

        File dir = target.getParentFile();
        if (dir != null) {
            dir.mkdirs();
        }

        AssetManager assets = context.getAssets();
        InputStream is = null;
        OutputStream os = null;
        try {
            is = assets.open(source);
            os = new FileOutputStream(target);
            byte buf[] = new byte[BUFFER_SIZE];
            int len = 0;
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 从assert中得到文件
     *
     * @param fileName The name of the asset to open. This name can be hierarchical.
     */
    public static String geFileFromAssets(Context context, String fileName) {
        if (context == null || TextUtils.isEmpty(fileName)) {
            return null;
        }

        StringBuilder s = new StringBuilder("");
        try {
            InputStreamReader in = new InputStreamReader(context.getResources()
                    .getAssets().open(fileName));
            BufferedReader br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                s.append(line);
            }
            return s.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将assets中指定文件夹中的所有文件拷贝到目标磁盘文件夹中（如果目标文件存在，将会被覆盖）
     *
     * @param context   上下文
     * @param sourceDir assets中的源文件夹
     * @param targetDir 目标磁盘文件夹
     * @return 是否成功
     */
    public static boolean copyAllFilesInDirFromAssetsToSDCard(Context context,
            String sourceDir, File targetDir) {
        if (context == null || sourceDir == null || sourceDir.length() == 0
                || targetDir == null) {
            LogUtil.w(TAG, "copyAllFilesInDirFromAssetsToSDCard",
                    "invalid argument");
            return false;
        }

        targetDir.mkdirs();

        AssetManager assets = context.getAssets();
        String[] srcFiles = null;
        try {
            srcFiles = assets.list(sourceDir);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        for (String srcFile : srcFiles) {
            if (!copyFileFromAssetsToSDCard(context, sourceDir + File.separator
                    + srcFile, new File(targetDir, srcFile))) {
                LogUtil.w(context, "copyAllFilesInDirFromAssetsToSDCard",
                        "failed to copy file in assets:" + srcFile);
            }
        }
        return true;
    }

    /**
     * 压缩多个文件成一个zip文件
     *
     * @param inputFiles 源文件列表
     * @param zipFile    压缩后的文件
     * @return 是否成功
     */
    public static boolean zipFiles(File[] inputFiles, File zipFile) {
        return zipFiles(inputFiles, zipFile, Deflater.BEST_COMPRESSION);
    }

    /**
     * 压缩多个文件成一个zip文件
     *
     * @param inputFiles 源文件列表
     * @param zipFile    压缩后的文件
     * @param level      压缩级别，一般取Deflater.BEST_COMPRESSION
     * @return 是否成功
     */
    public static boolean zipFiles(File[] inputFiles, File zipFile, int level) {
        if (inputFiles == null || zipFile == null) {
            LogUtil.w(TAG, "zipFiles", "invalid argument");
            return false;
        }

        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipFile));
            zos.setMethod(ZipOutputStream.DEFLATED);
            zos.setLevel(level);
            byte[] buf = new byte[BUFFER_SIZE];
            for (File inputFile : inputFiles) {
                if (!inputFile.isFile()) {
                    continue;
                }

                FileInputStream fis;
                try {
                    fis = new FileInputStream(inputFile);
                    zos.putNextEntry(new ZipEntry(inputFile.getName()));
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                int len = 0;
                try {
                    while ((len = fis.read(buf)) > 0) {
                        zos.write(buf, 0, len);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fis != null) {
                            zos.closeEntry();
                            fis.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 解压输入流到目标文件夹
     *
     * @param is        输入流
     * @param targetDir 目标文件夹
     * @return 是否成功
     */
    public static boolean unzipInputStream(InputStream is, File targetDir) {
        if (is == null || targetDir == null) {
            LogUtil.w(TAG, "unzipInputStream", "invalid argument");
            return false;
        }

        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(is);
            byte buf[] = new byte[BUFFER_SIZE];
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                FileOutputStream fos = null;
                try {
                    File entryFile = new File(targetDir, entry.getName());
                    File entryDir = entryFile.getParentFile();
                    if (!entryDir.exists()) {
                        entryDir.mkdirs();
                    }
                    fos = new FileOutputStream(entryFile);
                    int len = 0;
                    while ((len = zis.read(buf)) > 0) {
                        fos.write(buf, 0, len);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zis != null) {
                try {
                    zis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 解压ZIP包
     *
     * @param zipFile   zip文件路径
     * @param targetDir 解压后存放路径
     */
    public static void unZip(String zipFile, String targetDir) {
        int BUFFER = 4096;
        String strEntry;
        try {
            BufferedOutputStream dest = null;
            FileInputStream fis = new FileInputStream(zipFile);
            ZipInputStream zis = new ZipInputStream(
                    new BufferedInputStream(fis));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                try {
                    LogUtil.i("Unzip： ", "=" + entry);
                    int count;
                    byte data[] = new byte[BUFFER];
                    strEntry = entry.getName();
                    File entryFile = new File(targetDir, strEntry);
                    File entryDir = new File(entryFile.getParent());
                    if (!entryDir.exists()) {
                        entryDir.mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(entryFile);
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            zis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解压ZIP包到目标文件夹
     *
     * @param zipFile   zip文件
     * @param targetDir 目标文件夹
     * @return 是否成功
     */
    public static boolean unzipFile(File zipFile, File targetDir) {
        if (zipFile == null || targetDir == null) {
            LogUtil.w(TAG, "unzipFile", "invalid argument");
            return false;
        }

        try {
            return unzipInputStream(new FileInputStream(zipFile), targetDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从Raw中得到资源
     *
     * @param resId The resource identifier to open, as generated by the appt tool.
     */
    public static String geFileFromRaw(Context context, int resId) {
        if (context == null) {
            return null;
        }

        StringBuilder s = new StringBuilder();
        try {
            InputStreamReader in = new InputStreamReader(context.getResources()
                    .openRawResource(resId));
            BufferedReader br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                s.append(line);
            }
            return s.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据byte数组，生成文件 如果目录文件父目录不存在，则创建
     */
    public static boolean saveFileFromByte(byte[] bfile, String filePath) {
        File file = new File(filePath);
        String fileDir = file.getParent();
        File dir = new File(fileDir);
        if (!dir.exists() || dir.isFile()) {// 判断文件目录是否存在
            dir.mkdirs();
        }
        return bytesToFile(bfile, file);
    }

    /**
     * 根据byte数组，生成文件
     */
    public static boolean saveFileFromByte(byte[] bfile, String filePath,
            String fileName) {
        File file = null;
        File dir = new File(filePath);
        if (!dir.exists() || dir.isFile()) {// 判断文件目录是否存在
            dir.mkdirs();
        }
        file = new File(filePath, fileName);
        return bytesToFile(bfile, file);
    }

    private static boolean bytesToFile(byte[] bfile, File file) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 按修改时间排序
     *
     * @param files      指定文件集合
     * @param descending 是否为降序，为true，则按照修改日期由近（大）及远（小）排序，否则按照按照由远（小）及近（大）排序
     */
    public static void sortFilesByDate(List<File> files, boolean descending) {
        Collections.sort(files, new FileDateComparator(descending));
    }

    /**
     * 按修改时间排序
     *
     * @param files      指定文件集合
     * @param descending 是否为降序，为true，则按照修改日期由近（大）及远（小）排序，否则按照按照由远（小）及近（大）排序
     */
    public static void sortFilesByDate(File[] files, boolean descending) {
        if (descending) {
            for (int i = 0; i < files.length; i++) {
                for (int j = 0; j < i; j++) {
                    if (files[j].lastModified() < files[j + 1].lastModified()) {
                        File temp = files[j];
                        files[j] = files[j + 1];
                        files[j + 1] = temp;
                    }
                }
            }
        } else {
            for (int i = 0; i < files.length; i++) {
                for (int j = 0; j < i; j++) {
                    if (files[j + 1].lastModified() < files[j].lastModified()) {
                        File temp = files[j];
                        files[j] = files[j + 1];
                        files[j + 1] = temp;
                    }
                }
            }
        }
    }

    /**
     * 文件大小的转换
     */
    public static String transfSize(long size) {
        String resultString = "";

        DecimalFormat df = new DecimalFormat("###.##");
        float f;
        if (size < 1024 * 1024) {
            f = (float) ((float) size / (float) 1024);
            resultString = (df.format(Float.valueOf(f).doubleValue()) + "KB");
        } else {
            f = (float) ((float) size / (float) (1024 * 1024));
            resultString = (df.format(Float.valueOf(f).doubleValue()) + "MB");
        }

        return resultString;
    }

    public static File makeDir(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;

    }

    public static int saveFileFromInputStream(InputStream is, File toFile) {
        try {
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = is.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            is.close();
            fosto.close();
            return 0;

        } catch (Exception ex) {
            return -1;
        }
    }

    /**
     * 将对象serializable成文件，放到files文件夹里
     */
    public static void serializableToFile(Context context, String fileName,
            Object object) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(fos);
            closeStream(oos);
        }
    }

    /**
     * 将文件反serializable成对象，放到files文件夹里
     */
    public static Object unSerializableFromFile(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        if (!file.exists()) {
            return null;
        }
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(fileName);
            ois = new ObjectInputStream(fis);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(fis);
            closeStream(ois);
        }
        return null;
    }

    /**
     * 将对象serializable成文件
     */
    public static void serializableToFile(String filePath, Object object) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(filePath);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(fos);
            closeStream(oos);
        }
    }

    /**
     * 将文件反serializable成对象
     */
    public static Object unSerializableFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(filePath);
            ois = new ObjectInputStream(fis);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(fis);
            closeStream(ois);
        }
        return null;
    }

    public static void closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyFolder(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }
            String files[] = src.list();
            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                // 递归复制
                copyFolder(srcFile, destFile);
            }
        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;

            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }

    public static boolean isFileExists(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 获取文件大小，可以是文件，也可以是文件夹
     *
     * @return 单位byte
     */
    public static long getFileSize(File file) {
        if (file == null || !file.exists()) {
            return -1;
        }

        if (file.isFile()) {
            return file.length();
        }

        long size = 0;
        File[] subFiles = file.listFiles();
        for (File temp : subFiles) {
            if (temp.isDirectory()) {
                getFileSize(temp);
            } else {
                size = size + temp.length();
            }
        }
        return size;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access Framework
     * Documents, as well as the _data field for the MediaStore and other file-based
     * ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
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
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("imageList".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    //--------------------------

    /**
     * Get the value of the data column for this Uri. This is useful for MediaStore Uris, and other
     * file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
            String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = MediaStore.Video.VideoColumns.DATA;
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showToast(context, R.string.op_failed);
        } catch (Error e) {
            e.printStackTrace();
            ToastUtil.showToast(context, R.string.op_failed);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    private static class FileDateComparator implements Comparator<File> {

        private boolean mDescending;

        public FileDateComparator(boolean descending) {
            mDescending = descending;
        }

        @Override
        public int compare(File lhs, File rhs) {
            long lDate = lhs.lastModified();
            long rDate = rhs.lastModified();
            if (lDate < rDate) {
                return mDescending ? 1 : -1;
            } else if (lDate > rDate) {
                return mDescending ? -1 : 1;
            }
            return 0;
        }
    }
}
