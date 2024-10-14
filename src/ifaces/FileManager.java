package ifaces;

import java.io.File;

public interface FileManager {
    void saveFileToPath(String filename, String fileContent, String path);
    String readFileFromPath(String filename, String path);
    boolean deleteFile(String filename, String path);
    String getFilePath(String filename, String extension);
    File compressFile(String filePath);
    File decompressFile(String filePath);
}

