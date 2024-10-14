package ifaces;

import java.io.File;

public interface FileManager {
    public void saveFileToPath(String filename, String fileContent, String path);
    public String readFileFromPath(String filename, String path);
    public boolean deleteFile(String filename, String path);
    public String getFilePath(String filename, String extension);
    public File compressFile(String filePath);
    public File decompressFile(String filePath);
}

