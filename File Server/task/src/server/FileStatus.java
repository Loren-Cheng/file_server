package server;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private ConcurrentHashMap<String, Long> fileNameIdMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, String> idFileNameMap = new ConcurrentHashMap<>();
    private Long id = 0L;

    private static FileStatus fileStatus;

    public static synchronized FileStatus getInstance() {
        if (fileStatus != null) {
            return fileStatus;
        } else {
            try {
                FileInputStream fis = new FileInputStream("/Users/chengyichung/ideaProjects/File Server/File Server/task/src/server/fileStatus.ser");
                ObjectInputStream ois = new ObjectInputStream(fis);
                fileStatus = (FileStatus) ois.readObject();
                ois.close();
                fis.close();
            } catch (FileNotFoundException e) {
//                System.out.println("File not found. A new instance will be created.");
                fileStatus = new FileStatus();
            } catch (IOException | ClassNotFoundException e) {
//                System.out.println("An error occurred while reading the file. A new instance will be created.");
                fileStatus = new FileStatus();
            }
        }
        return fileStatus;
    }

    public synchronized long addFile(String fileName) {
        ++id;
        fileNameIdMap.put(fileName, id);
        idFileNameMap.put(id, fileName);
//        for (Map.Entry<String, Long> entry : fileNameIdMap.entrySet()) {
//            System.out.println(entry.getKey());
//            System.out.println(entry.getValue());
//        }
        persist();
        return id;
    }

    public synchronized void deleteFile(String fileName) {
        long id = fileNameIdMap.get(fileName);
        fileNameIdMap.remove(fileName);
        idFileNameMap.remove(id);
        persist();
    }

    public long getIdFromName(String fileName) {
        return fileNameIdMap.get(fileName);
    }

    public String getNameFromId(long id) {
        return idFileNameMap.get(id);
    }

    public String getFileNameFromId(Long id) {
        return idFileNameMap.get(id);
    }

    private void persist() {
        try {
            FileOutputStream fos = new FileOutputStream("/Users/chengyichung/ideaProjects/File Server/File Server/task/src/server/fileStatus.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(fileStatus);
            oos.close();
            fos.close();
        } catch (IOException ignored) {

        }
    }
}