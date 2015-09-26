package de.htwberlin.f4.tms.schnitzeljagd.util;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tino Herrmann, Fabian Schmöker
 * @version 1.0
 */
public class IOHelper {
    private IOHelper() {

    }

    /**
     * @param text
     * @param fileName
     * @param dirPath
     * @throws IOException
     */
    public static void writeText(String text, String fileName, String dirPath) throws IOException {
        File file = new File(dirPath + File.separatorChar + fileName);
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(text.getBytes());
        outputStream.close();
    }

    /**
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String readText(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream fInputStream = new FileInputStream(file);
        BufferedReader bReader = new BufferedReader(new InputStreamReader(fInputStream));
        StringBuilder outputText = new StringBuilder();
        String line;
        while ((line = bReader.readLine()) != null) {
            outputText.append(line);
            outputText.append('\n');
        }
        bReader.close();
        return outputText.toString();
    }

    /**
     * @param directoryName
     * @param parentDirectoryPath
     * @return
     */
    public static File createDirectory(String directoryName, String parentDirectoryPath) {
        File directory = null;
        if ((directoryName != null) && (!directoryName.isEmpty()))
            directory = createDirectory(parentDirectoryPath + File.separator + directoryName);
        else
            throw new IllegalArgumentException("Invalid arguments to create internal directory.");
        return directory;
    }

    /**
     * @param directoryPath
     * @return
     */
    public static File createDirectory(String directoryPath) {
        File directory = null;
        if (directoryPath != null) {
            directory = new File(directoryPath);
            directory.mkdirs();
        } else
            throw new IllegalArgumentException("Invalid arguments to create internal directory.");
        return directory;
    }

    /**
     * @param path
     * @return
     */
    public static boolean isExistent(String path) {
        return new File(path).exists();
    }

    /**
     * @param dirPath
     * @return
     */
    public static List<File> getFilesFromDir(String dirPath) {
        File directory = new File(dirPath);
        ArrayList<File> files = new ArrayList<File>();
        if (directory.exists() && directory.isDirectory()) {
            if (directory.listFiles() != null)
                for (File file : directory.listFiles()) {
                    if (file.isFile())
                        files.add(file);
                }
        } else
            throw new IllegalArgumentException("Invalid argument to determine files from dir.");
        return files;
    }

    /**
     * @param dirPath
     * @param filterExtension
     * @return
     */
    public static List<File> getFilesFromDir(String dirPath, final String filterExtension) {
        File directory = new File(dirPath);
        ArrayList<File> files = new ArrayList<File>();
        if (directory.exists() && directory.isDirectory()) {
            if (directory.listFiles() != null) {
                File[] fileArray = directory.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith("." + filterExtension);
                    }
                });
                for (File file : fileArray) {
                    if (file.isFile())
                        files.add(file);
                }
            }
        } else
            throw new IllegalArgumentException("Invalid argument to determine files from dir.");
        return files;
    }

    /**
     * @param path
     */
    public static void deleteFileOrDirectory(String path) {
        File fileOrDirectory = new File(path);
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteFileOrDirectory(child.getAbsolutePath());
        fileOrDirectory.delete();
    }

    /**
     * @return
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    /**
     * @return
     */
    public static boolean isExternalStorageWriteable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }
}
