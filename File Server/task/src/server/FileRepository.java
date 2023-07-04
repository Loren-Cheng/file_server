package server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class FileRepository implements Repository {
    String dirPath;

    public FileRepository() {
        this.dirPath = ServerConfigs.serverFileDitPath;
    }

    @Override
    public void save(String fileName, String content) {
        File file = new File(dirPath + fileName);
        try (
                FileWriter fileWriter = new FileWriter(file);
        ) {
            fileWriter.write(content);
            Long id = FileStatus.getInstance().addFile(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * the method store message to file in default path and make id, fileName in the HashMap
     * @param fileName
     * @param message persistent data
     * @return id in the file server
     */
    @Override
    public long saveFile(String fileName, byte[] message) {
        fileName = fileName == "HandleId" ? "" : fileName;
        File file = new File(dirPath + fileName);
        long id = 0l;
        try (
                FileWriter fileWriter = new FileWriter(file);
        ) {

            Files.write(Path.of(dirPath + fileName), message);
            id = FileStatus.getInstance().addFile(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    @Override
    public String get(String fileName) {
        File file = new File(dirPath + fileName);
        try {
            Scanner scanner = new Scanner(file);
            StringBuilder contentBuilder = new StringBuilder("");
            while (scanner.hasNext()) {
                contentBuilder.append(scanner.nextLine());
            }
            return contentBuilder.toString();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(String fileName) {
        File file = new File(dirPath + fileName);
        boolean isDeleted = file.delete();
        if (isDeleted) {
            FileStatus.getInstance().deleteFile(fileName);
        }
        return isDeleted;
    }
}
