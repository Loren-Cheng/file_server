package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.StreamHandler;

import static server.Main.exit;

public class ClientHandler {
    private final Socket socket;
    private FileRepository fileRepository;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.fileRepository = new FileRepository();
    }

    public void handleRequest() {
        try (
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())
        ) {
            String msg = inputStream.readUTF();
            List<String> command = handleCommand(msg);
            String requestType = command.get(0);
            String fileName = !exit ? command.get(1) : "";

            switch (requestType) {
                case "PUT":
                    String content = command.get(2);
                    handlePutRequest(outputStream, fileName, content);
                    return;
                case "GET":
                    handleGetRequest(outputStream, fileName);
                    return;
                case "GET_BYTES_ID":
                    fileName = FileStatus.getInstance().getFileNameFromId(Long.valueOf(fileName));
                    handleGetBytesRequest(outputStream, fileName);
                    return;
                case "GET_BYTES_FILE_NAME":
                    handleGetBytesRequest(outputStream, fileName);
                    return;

                case "DELETE":
                    handleDeleteRequest(outputStream, fileName);
                    return;
                case "DELETE_ID":
                    fileName = FileStatus.getInstance().getFileNameFromId(Long.valueOf(fileName));
                    handleDeleteRequest(outputStream, fileName);
                    return;
                case "SAVE":
                    handleSaveRequest(outputStream, inputStream, fileName, command);
                    return;
                case "EXIT":
                    exit = true;
                    return;
                default:
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleSaveRequest(DataOutputStream outputStream, DataInputStream inputStream, String fileName, List<String> command) throws IOException {
        File file = new File(ServerConfigs.serverFileDitPath + fileName);
        UUID uuid = UUID.randomUUID();
        String idName = uuid.toString();
        String fileNameInServer = command.get(1).equals("HandleId") ? idName : command.get(1);
        int length = inputStream.readInt();
        byte[] message = new byte[length];
        File fileInServer = new File(ServerConfigs.serverFileDitPath + fileNameInServer);
        if (fileInServer.exists()) {
            String respondMsg = "403";
            outputStream.writeUTF(respondMsg);
        } else {
            fileName = fileNameInServer;
            String respondMsg = "200";
            inputStream.readFully(message, 0, length);
            //store inputStream in message
            long id = fileRepository.saveFile(fileName, message);
            outputStream.writeUTF(respondMsg);
            outputStream.writeUTF(String.valueOf(id));
        }
    }

    private List<String> handleCommand(String msg) {
        String requestType;

        String[] commandArr;
        List<String> commandList = new ArrayList<>();

//        if (msg.equals("EXIT")) {
//            requestType = msg;
//            msgArr = new String[0];
//        } else {
        commandArr = msg.split(" ");
        commandList = List.of(commandArr);
//        }
        return commandList;
    }

    private void handlePutRequest(DataOutputStream outputStream, String fileName, String content) throws IOException {
        File file = new File(ServerConfigs.serverFileDitPath + fileName);
        if (file.exists()) {
            String respondMsg = "403";
            outputStream.writeUTF(respondMsg);
        } else {
            String respondMsg = "200";
            fileRepository.save(fileName, content);
            outputStream.writeUTF(respondMsg);
        }
    }

    private void handleGetRequest(DataOutputStream outputStream, String fileName) {
        File file = new File(ServerConfigs.serverFileDitPath + fileName);
        if (file.exists()) {
            String content = fileRepository.get(fileName);
            String respondMsg = "200 " + content;
            try {
                outputStream.writeUTF(respondMsg);
            } catch (IOException e) {
//                throw new RuntimeException(e);
            }
        } else {
            String respondMsg = "404";
            try {
                outputStream.writeUTF(respondMsg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleGetBytesRequest(DataOutputStream outputStream, String fileName) {
        File file = new File(ServerConfigs.serverFileDitPath + fileName);
        byte[] bytes;
        if (file.exists()) {
            try {
                bytes = Files.readAllBytes(Path.of(ServerConfigs.serverFileDitPath + fileName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String respondMsg = "200";
            try {
                outputStream.writeUTF(respondMsg);
                outputStream.writeInt(bytes.length);
                outputStream.write(bytes);

            } catch (IOException e) {
//                throw new RuntimeException(e);
            }
        } else {
            String respondMsg = "404";
            try {
                outputStream.writeUTF(respondMsg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleDeleteRequest(DataOutputStream outputStream, String fileName) {
        File file = new File(ServerConfigs.serverFileDitPath + fileName);
        if (file.exists()) {
            boolean deleted = fileRepository.delete(fileName);
            String respondMsg = "200";
            try {
                outputStream.writeUTF(respondMsg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            String respondMsg = "404";
            try {
                outputStream.writeUTF(respondMsg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}