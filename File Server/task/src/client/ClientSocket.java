package client;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;


public class ClientSocket implements Command {
    private String serverAddress;
    private int serverPort;


    public ClientSocket(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    @Override
    public String put(String fileName, String fileContent) {
        String receivedMsg;
        try (
                Socket socket = new Socket(serverAddress, serverPort);
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())
        ) {
            String msg = "PUT " + fileName + " " + fileContent;
            dataOutputStream.writeUTF(msg);
            System.out.println("The request was sent.");
            receivedMsg = dataInputStream.readUTF();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return receivedMsg;
    }

    /**
     * @param fileName
     * @param fileNameInSever
     * @return
     */
    public String saveFileInServer(String fileName, String fileNameInSever, String path) {
        String receivedCodeAndId;
        try (
                Socket socket = new Socket(serverAddress, serverPort);
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())
        ) {
            byte[] message = Files.readAllBytes(Path.of(path + fileName));
            String msg = "SAVE " + fileNameInSever + " " + "NoThisField";
            dataOutputStream.writeUTF(msg);
            dataOutputStream.writeInt(message.length);
            dataOutputStream.write(message);

            System.out.println("The request was sent.");
            String receivedCode = dataInputStream.readUTF();
            String receivedId = dataInputStream.readUTF();
            receivedCodeAndId = receivedCode + " " + receivedId;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return receivedCodeAndId;
    }

    @Override
    public String get(String fileName) {
        String receivedMsg;
        try (
                Socket socket = new Socket(serverAddress, serverPort);
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())
        ) {
            String msg = "GET " + fileName;
            dataOutputStream.writeUTF(msg);
            System.out.println("The request was sent.");
            receivedMsg = dataInputStream.readUTF();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return receivedMsg;
    }

    /**
     * choose one of fileName or id to get
     *
     * @param fileNameInServer default fileName " "(1 space)
     * @param id               default -1 ;
     * @return 12
     */
    public String getBytesAndSave(String fileNameInServer, long id, ByteArrayOutputStream bytes) {

        String code;
        try (
                Socket socket = new Socket(serverAddress, serverPort);
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())
        ) {
            String msg;
            if (id == -1) {
                msg = "GET_BYTES_FILE_NAME " + fileNameInServer;
            } else {
                msg = "GET_BYTES_ID " + id;
            }
            dataOutputStream.writeUTF(msg);
            System.out.println("The request was sent.");
            code = dataInputStream.readUTF();
            if (code.equals("200")) {
                int length = dataInputStream.readInt();
//                bytes = dataInputStream.readNBytes(length);
                byte[] tmp = new byte[length];
                dataInputStream.readFully(tmp);
                bytes.write(tmp);

            } else if (code.equals("404")) {

            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return code;
    }

    /**
     * choose one of fileName or id to delete
     *
     * @param fileNameInServer default fileName " "(1 space)
     * @param id               default -1 ;
     * @return status code
     */
    @Override
    public String delete(String fileNameInServer, long id) {
        String receivedMsg;
        String msg;
        try (
                Socket socket = new Socket(serverAddress, serverPort);
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())
        ) {
            if (id == -1) {
                msg = "DELETE " + fileNameInServer;
            } else {
                msg = "DELETE_ID " + id;
            }
            dataOutputStream.writeUTF(msg);
            System.out.println("The request was sent.");
            receivedMsg = dataInputStream.readUTF();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return receivedMsg;
    }

    public void exit() {
        try (
                Socket socket = new Socket(serverAddress, serverPort);
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())
        ) {
            String msg = "EXIT";
            dataOutputStream.writeUTF(msg);
            System.out.println("The request was sent.");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
