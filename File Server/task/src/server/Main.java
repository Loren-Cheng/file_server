package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static server.ServerConfigs.PORT;

public class Main {
    static boolean exit = false;

    public static void main(String[] args) throws IOException {
        startServer();
    }

    private static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (!exit) {
            Socket socket = serverSocket.accept();
            ClientHandler handler = new ClientHandler(socket);
            handler.handleRequest();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
