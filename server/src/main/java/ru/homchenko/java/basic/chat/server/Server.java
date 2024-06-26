package ru.homchenko.java.basic.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    private int port;
    private List<ClientHandler> clients;

    private AuthenticationService authenticationService;

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            //this.authenticationService = new InMemoryAuthenticationService();
            this.authenticationService = new DBAuthenticationService();
            //this.authenticationService.getUsersListFromDB();
            System.out.println("Сервис аутентификации запущен: " + authenticationService.getClass().getSimpleName());
            System.out.printf("Сервер запущен на порту: %d, ожидаем подключения клиентов\n", port);
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    new ClientHandler(this, socket);
                } catch (Exception e) {
                    System.out.println("Возникла ошибка при обработке подключившегося клиента");
                }
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
        String[] tokens = msg.split(" ");
        String nickname = tokens[1];

        Map<String, ClientHandler> usersMap = new HashMap<>();
        for (ClientHandler client : clients) {
            usersMap.put(client.getNickname(), client);
        }

        for (Map.Entry<String, ClientHandler> entry : usersMap.entrySet()) {
            if (entry.getKey().equals(nickname)) {
                entry.getValue().sendMessage(msg);
                break;
            }
        }
    }

    public synchronized boolean isNicknameBusy(String nickname) {
        for (ClientHandler c : clients) {
            if (c.getNickname().equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void kickUser(String msg, String role) {
        if (role == null)
            return;

        if (role.equals("ADMIN")) {
            String[] tokens = msg.split(" ");
            String nickname = tokens[1];

            for (ClientHandler c : clients) {
                if (c.getRole().equals(role))
                    return;

                if (c.getNickname().equals(nickname)) {
                    c.disconnect();
                    System.out.println(nickname + " отключен");
                }
            }
        }
    }
}
