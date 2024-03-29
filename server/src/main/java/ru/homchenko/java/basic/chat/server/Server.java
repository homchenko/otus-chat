package ru.homchenko.java.basic.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private List<ClientHandler> clients;

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            System.out.printf("Сервер запущен на порте: %d, ожидаем подключения клиентов\n", this.port);

            while (true) {
                Socket socket = serverSocket.accept();
                this.subscribe(new ClientHandler(this, socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public synchronized void sendMessageSelectedUser(String msg) {
        int spaceIdx1 = msg.indexOf(" ");
        String subStr = msg.substring(spaceIdx1 + 1);
        int spaceIdx2 = subStr.indexOf(" ");
        String userName = subStr.substring(0, spaceIdx2);

        for (ClientHandler client : clients) {
            if (client.getUsername().equals(userName)) {
                client.sendMessage(msg);
                break;
            }
        }
    }
}
