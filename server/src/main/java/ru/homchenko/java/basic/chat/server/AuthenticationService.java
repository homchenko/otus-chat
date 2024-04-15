package ru.homchenko.java.basic.chat.server;

public interface AuthenticationService {
    void getUsersListFromDB();

    String getNicknameByLoginAndPassword(String login, String password);
    boolean register(String login, String password, String nickname);
    boolean isLoginAlreadyExist(String login);
    boolean isNicknameAlreadyExist(String nickname);
    String getUserRoleByLoginAndPassword(String login, String password);
}
