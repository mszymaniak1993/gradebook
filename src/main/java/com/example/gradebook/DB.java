package com.example.gradebook;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DB {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gradebook";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private Connection connection;
    private Statement statement;

    public DB () {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException ex) {
            System.err.println("JDBC driver not found");
            ex.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            createTable();
        } catch (SQLException ex) {
            System.err.println("Problem opening connection");
            ex.printStackTrace();
        }
    }

    public boolean createTable() {
        String create = "CREATE TABLE IF NOT EXISTS users" +
                "(id INTEGER AUTO_INCREMENT, login VARCHAR(45), password VARCHAR(50), PRIMARY KEY (id))";
        try {
            statement.execute(create);
        } catch (SQLException e) {
            System.err.println("Table creation error");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean insertData(String table, String login, String password) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " +
                    table + " (`id`,`login`,`password`) VALUES (null,?,?)");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.err.println("Error while inserting user data: " + login);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<User> getData(String table) {
        List<User> output = new LinkedList<User>();
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + table);
            int id;
            String login, password;
            while (resultSet.next()) {
                id = resultSet.getInt("id");
                login = resultSet.getString("login");
                password = resultSet.getString("password");

                output.add(new User(id, login, password));
            }
        } catch (SQLException e) {
            System.err.println("Problem with reading data from database");
            e.printStackTrace();
            return null;
        }
        return output;
    }

    public void closeConnetion() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.err.println("Problem with closing connection to the database");
            e.printStackTrace();
        }
    }
}
