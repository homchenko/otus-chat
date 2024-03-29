package ru.homchenko.java.basic.chat.server;

public class Application {
    public static void main(String[] args) {
        new Server(8189).start();
    }
}
