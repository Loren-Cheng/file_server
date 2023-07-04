package client;

public interface Command {
    String put(String fileName, String fileContent);

    String get(String fileName);

    String delete(String fileNameInServer, long id);

}
