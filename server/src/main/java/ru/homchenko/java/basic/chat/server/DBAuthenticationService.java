package ru.homchenko.java.basic.chat.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBAuthenticationService implements AuthenticationService {
    private class User {
        private String login;
        private String password;
        private String nickname;
        private String role;

        public User(String login, String password, String nickname, String role) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
            this.role = role;
        }
    }

    private List<DBAuthenticationService.User> users;
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USERS_QUERY = "select u.nickname, u.email, u.\"password\", r.\"name\"  from users u join user_to_role utr on u.id = utr.user_id join roles r on utr.role_id = r.id ";

    public DBAuthenticationService() {
        this.users = new ArrayList<>();
        this.getUsersListFromDB();
    }

    @Override
    public void getUsersListFromDB() {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, "postgres", "postgres")) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet usersResultSet = statement.executeQuery(USERS_QUERY)) {
                    while (usersResultSet.next()) {

                        String nickname = usersResultSet.getString(1);
                        String email = usersResultSet.getString(2);
                        String password = usersResultSet.getString(3);
                        String role = usersResultSet.getString(4);
                        this.users.add(new User(email, password, nickname, role));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //public void getUsersList()
    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        for (DBAuthenticationService.User u : users) {
            if (u.login.equals(login) && u.password.equals(password)) {
                return u.nickname;
            }
        }
        return null;
    }

    @Override
    public boolean register(String login, String password, String nickname) {
        if (isLoginAlreadyExist(login)) {
            return false;
        }
        if (isNicknameAlreadyExist(nickname)) {
            return false;
        }
        users.add(new DBAuthenticationService.User(login, password, nickname, "USER"));
        return true;
    }

    @Override
    public boolean isLoginAlreadyExist(String login) {
        for (DBAuthenticationService.User u : users) {
            if (u.login.equals(login)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isNicknameAlreadyExist(String nickname) {
        for (DBAuthenticationService.User u : users) {
            if (u.nickname.equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getUserRoleByLoginAndPassword(String login, String password) {
        for (DBAuthenticationService.User u : users) {
            if (u.login.equals(login) && u.password.equals(password)) {
                return u.role;
            }
        }
        return null;
    }
}
