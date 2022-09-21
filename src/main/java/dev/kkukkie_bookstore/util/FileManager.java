package dev.kkukkie_bookstore.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileManager {

    private static final Logger logger = LoggerFactory.getLogger(FileManager.class);

    private static final String[] SIZE_UNITS = new String[] { "KB", "MB", "GB" };

    public static boolean isExist(String fileName) {
        if (fileName == null) { return false; }

        File file = new File(fileName);
        return file.exists();
    }

    public static boolean mkdirs(String fileName) {
        if (fileName == null) { return false; }

        File file = new File(fileName);
        return file.mkdirs();
    }

    public static boolean writeBytes(File file, byte[] data, boolean isAppend) {
        if (file == null || data == null || data.length == 0) { return false; }

        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file, isAppend))) {
            bufferedOutputStream.write(data);
            return true;
        } catch (Exception e) {
            logger.warn("[FileManager] Fail to write the file. (fileName={})", file.getAbsolutePath(), e);
            return false;
        }
    }

    public static boolean writeBytes(String fileName, byte[] data, boolean isAppend) {
        if (fileName == null) { return false; }

        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fileName, isAppend))) {
            bufferedOutputStream.write(data);
            return true;
        } catch (Exception e) {
            logger.warn("[FileManager] Fail to write the file. (fileName={})", fileName, e);
            return false;
        }
    }

    public static boolean writeString(String fileName, String data, boolean isAppend) {
        if (fileName == null) { return false; }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, isAppend))) {
            bufferedWriter.write(data);
            return true;
        } catch (Exception e) {
            logger.warn("[FileManager] Fail to write the file. (fileName={})", fileName, e);
            return false;
        }
    }

    public static byte[] readAllBytes(String fileName) {
        if (fileName == null) { return new byte[0]; }

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fileName))) {
            return bufferedInputStream.readAllBytes();
        } catch (Exception e) {
            //logger.warn("[FileManager] Fail to read the file. (fileName={})", fileName);
            return new byte[0];
        }
    }

    public static List<String> readAllLines(String fileName) {
        if (fileName == null) { return Collections.emptyList(); }

        List<String> lines = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while( (line = bufferedReader.readLine()) != null ) {
                lines.add(line);
            }
            return lines;
        } catch (Exception e) {
            //logger.warn("[FileManager] Fail to read the file. (fileName={})", fileName);
            return lines;
        }
    }

    // [/home/uangel/udash/media] + [animal/tigers/tigers.mp4] > [/home/uangel/udash/media/animal/tigers/tigers.mp4]
    public static String concatFilePath(String from, String to) {
        if (from == null) { return null; }
        if (to == null) { return from; }

        String resultPath = from.trim();
        if (!to.startsWith(File.separator)) {
            if (!resultPath.endsWith(File.separator)) {
                resultPath += File.separator;
            }
        } else {
            if (resultPath.endsWith(File.separator)) {
                resultPath = resultPath.substring(0, resultPath.lastIndexOf("/"));
            }
        }

        resultPath += to.trim();
        return resultPath;
    }

    // [/home/uangel/udash/media/animal/tigers/tigers.mp4] > [/home/uangel/udash/media/animal/tigers]
    public static String getParentPathFromUri(String uri) {
        if (uri == null) { return null; }
        if (!uri.contains("/")) { return uri; }
        return uri.substring(0, uri.lastIndexOf("/")).trim();
    }

    // [/home/uangel/udash/media/animal/tigers/tigers.mp4] > [tigers.mp4]
    public static String getFileNameWithExtensionFromUri(String uri) {
        if (uri == null) { return null; }
        if (!uri.contains("/")) { return uri; }

        int lastSlashIndex = uri.lastIndexOf("/");
        if (lastSlashIndex == (uri.length() - 1)) { return null; }
        return uri.substring(uri.lastIndexOf("/") + 1).trim();
    }

    // [/home/uangel/udash/media/animal/tigers/tigers.mp4] > [/home/uangel/udash/media/animal/tigers/tigers]
    public String getFilePathWithoutExtensionFromUri(String uri) {
        if (uri == null) { return null; }
        if (!uri.contains(".")) { return uri; }
        return uri.substring(0, uri.lastIndexOf(".")).trim();
    }

    // [/home/uangel/udash/media/animal/tigers/tigers.mp4] > [tigers]
    public static String getFileNameFromUri(String uri) {
        uri = getFileNameWithExtensionFromUri(uri);
        if (uri == null) { return null; }
        if (!uri.contains(".")) { return uri; }

        uri = uri.substring(0, uri.lastIndexOf(".")).trim();
        return uri;
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            logger.warn("[FileManager] Fail to delete the file. File is not exist. (path={})", path);
            return false;
        }

        try {
            if (file.isDirectory()) {
                FileUtils.deleteDirectory(file);
            } else {
                FileUtils.forceDelete(file);
            }
            //logger.debug("[FileManager] Success to delete the file. (path={})", path);
            return true;
        } catch (Exception e) {
            logger.warn("[FileManager] Fail to delete the file. (path={})", path, e);
            return false;
        }
    }

    public static boolean deleteFile(File file) {
        if (file == null) { return false; }

        try {
            if (file.isDirectory()) {
                FileUtils.deleteDirectory(file);
            } else {
                FileUtils.forceDelete(file);
            }
            if (!file.exists()) {
                logger.info("[FileManager] Success to delete the file. (path={})", file.getAbsolutePath());
            }
            return true;
        } catch (Exception e) {
            logger.warn("[FileManager] Fail to delete the file. (path={})", file.getAbsolutePath(), e);
            return false;
        }
    }

    public static int getSizeFromUnit(String sizeUnit) {
        if (sizeUnit == null || sizeUnit.isEmpty()) { return 0; }

        String unit = null;
        for (String curUnit : SIZE_UNITS) {
            if (sizeUnit.endsWith(curUnit)) { // 200MB
                unit = curUnit;
                break;
            }
        }
        if (unit == null) { return 0; }

        // 200MB > 200 + MB > 200 * 1,000,000 > 200,000,000
        String integerOnly = sizeUnit.substring(0, sizeUnit.indexOf(unit));
        int size = Integer.parseInt(integerOnly);

        switch (unit) {
            case "KB":
                size *= 1000;
                break;
            case "MB":
                size *= 1000000;
                break;
            case "GB":
                size *= 1000000000;
                break;
            default:
        }

        return size;
    }

}
