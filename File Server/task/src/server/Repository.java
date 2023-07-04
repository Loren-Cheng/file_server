package server;

public interface Repository {
    void save(String fileName, String content);

/**
 * @param fileName
 * @param content
 * @return File id in the sever
 */
    long saveFile(String fileName, byte[] content);

    String get(String fileName);

    boolean delete(String fileName);
}
