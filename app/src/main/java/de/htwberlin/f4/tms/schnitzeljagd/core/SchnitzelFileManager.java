package de.htwberlin.f4.tms.schnitzeljagd.core;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.htwberlin.f4.tms.schnitzeljagd.util.IOHelper;

/**
 * @author Tino Herrmann, Fabian Schmöker
 * @version 1.0
 */
public class SchnitzelFileManager {
    private String pathToSchnitzelFilesDir;
    private static SchnitzelFileManager instance;

    /**
     * @param schnitzelDirName
     * @throws IOException
     */
    private SchnitzelFileManager(String schnitzelDirName) throws IOException {
        if (IOHelper.isExternalStorageWriteable())
            pathToSchnitzelFilesDir = IOHelper.createDirectory(schnitzelDirName,
                    Environment.getExternalStorageDirectory().getAbsolutePath()).getAbsolutePath();
        else
            throw new IOException("External Storage is not writable to create schnitzfile dir.");
    }

    /**
     * @param schnitzelDirName
     * @return
     * @throws IOException
     */
    public static synchronized SchnitzelFileManager getInstance(String schnitzelDirName)
            throws IOException {
        if (instance == null)
            instance = new SchnitzelFileManager(schnitzelDirName);
        return instance;
    }

    /**
     * @return
     */
    public static synchronized SchnitzelFileManager getInstance() {
        if (instance == null)
            throw new IllegalStateException("SchnitzelFileManager is not instantiated, " +
                                            "call getInstance() with string parameter.");
        return instance;
    }

    /**
     * @return
     */
    public String getSchnitzelFilesDir() {
        return pathToSchnitzelFilesDir;
    }

    /**
     * @return
     */
    public List<File> getSchnitzelFiles() {
        return IOHelper.getFilesFromDir(pathToSchnitzelFilesDir, "schnitzel");
    }

    /**
     *
     */
    public void cleanSchnitzelFilesDir() {
        IOHelper.deleteFileOrDirectory(pathToSchnitzelFilesDir);
        pathToSchnitzelFilesDir =
                IOHelper.createDirectory(pathToSchnitzelFilesDir).getAbsolutePath();
    }

    /**
     * @return
     */
    public boolean isSchnitzelFilesDirExistent() {
        return IOHelper.isExistent(pathToSchnitzelFilesDir);
    }
}
