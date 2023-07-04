package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34522;

    private static ClientSocket clientSocket = new ClientSocket(SERVER_ADDRESS, SERVER_PORT);

    public static void main(String[] args) {
        String action = promptAction();
        if (action.equals(Action.PUT.getNum())) {
            String fileName = promptFileName();
            String fileNameInSever = promptFileNameInServer();
            String responseCodeAndId = clientSocket.saveFileInServer(fileName, fileNameInSever, ClientConfigs.clientFileDirPath);
            System.out.println(putResponseStatus(responseCodeAndId));
        } else if (action.equals(Action.GET.getNum())) {
            int chooseNameOrId = promptNameOrId();
            if (chooseNameOrId == 1) {
                String fileName = promptFileName();
                try (
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                ) {
                    String code = clientSocket.getBytesAndSave(fileName, -1, bytes);
                    if (code.equals("404")) {
                        System.out.println("The response says that this file is not found!");
                    } else if (code.equals("200")) {
                        try {
                            String fileNameInClient = promptFileNameInClient();
                            Path path = Path.of(ClientConfigs.clientFileDirPath + fileNameInClient);
                            Files.write(path, bytes.toByteArray());
                            System.out.println("File saved on the hard drive!");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (chooseNameOrId == 2) {
                int id = promptId();
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                String code = clientSocket.getBytesAndSave(" ", id, bytes);
                if (code.equals("404")) {
                    System.out.println("The response says that this file is not found!");
                } else if (code.equals("200")) {
                    try {
                        String fileNameInClient = promptFileNameInClient();
                        Path path = Path.of(ClientConfigs.clientFileDirPath + fileNameInClient);
                        Files.write(path, bytes.toByteArray());
                        System.out.println("File saved on the hard drive!");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }


        } else if (action.equals(Action.DELETE.getNum())) {

            int nameOrId = promptDeleteByNameOrId();
            String response = "";
            if (nameOrId == 1) {
                String fileName = promptFileName();
                response = clientSocket.delete(fileName, -1);
            } else {
                int id = promptId();
                response = clientSocket.delete(" ", id);
            }
            System.out.println(deleteResponseStatus(response));
        } else if (action.equals(Action.EXIT.getName())) {
            clientSocket.exit();
        }
    }

    private static int promptDeleteByNameOrId() {
        System.out.println("Do you want to delete the file by name or by id (1 - name, 2 - id):");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }


    public enum Action {
        GET("1", "get a file"),
        PUT("2", "save a file"),
        DELETE("3", "delete a file"),
        EXIT("exit", "exit");

        private final String num;
        private final String name;

        Action(String num, String name) {
            this.num = num;
            this.name = name;
        }

        public String getNum() {
            return num;
        }

        public String getName() {
            return name;
        }
    }


    private static int promptNameOrId() {
        System.out.print("Do you want to get the file by name or by id (1 - name, 2 - id):");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }

    private static String promptFileName() {
        System.out.print("Enter name of the file:");
        Scanner scanner = new Scanner(System.in);
        String fileName = scanner.next();
        return fileName;
    }

    private static String promptFileNameInClient() {
        System.out.print("The file was downloaded! Specify a name for it:");
        Scanner scanner = new Scanner(System.in);
        String fileName = scanner.nextLine();
        return fileName;
    }

    private static int promptId() {
        System.out.print("Enter id:");
        Scanner scanner = new Scanner(System.in);
        int id = scanner.nextInt();
        return id;
    }

    private static String promptFileNameInServer() {
        System.out.print("Enter name of the file to be saved on server:");
        Scanner scanner = new Scanner(System.in);
        String fileName = scanner.nextLine();
        if (fileName.equals("")) {
            fileName = "HandleId";
        }
        return fileName;
    }

    private static String promptAction() {
        System.out.print("Enter action (1 - get a file, 2 - save a file, 3 - delete a file):");
        Scanner scanner = new Scanner(System.in);
        String action = scanner.nextLine();
        return action;
    }

    private static String promptContent() {
        System.out.print("Enter file content: ");
        StringBuilder fileContentBuilder = new StringBuilder();
        Scanner scanner = new Scanner(System.in);
        fileContentBuilder.append(scanner.nextLine());
        String fileContent = fileContentBuilder.toString();
        return fileContent;
    }

    private static String putResponseStatus(String response) {
        String[] responseCodeAndId = response.split(" ");
        response = responseCodeAndId[0];
        long id = Long.parseLong(responseCodeAndId[1]);
        switch (response) {
            case "403":
                return "The response says that creating the file was forbidden!";
            case "200":
                return "Response says that file is saved! ID = " + id;
            default:
                return "";
        }
    }

    private static String getResponseStatus(String response, String content) {
        switch (response) {
            case "200":
                return "The content of the file is:" + content;
            case "404":
                return "The response says that this file is not found!";
            default:
                return "";
        }
    }

    private static String deleteResponseStatus(String response) {
        switch (response) {
            case "200":
                return "The response says that the file was successfully deleted!";
            case "404":
                return "The response says that the file was not found!";
            default:
                return "";
        }
    }
}
