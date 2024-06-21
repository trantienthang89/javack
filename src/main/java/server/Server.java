package server;

import client.ClientHandler;
import controller.ClientFormController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private Socket socket;
    private static Server server;
    private static Connection connection;

    private List<ClientHandler> clients = new ArrayList<>();

    private Server() throws IOException {
        serverSocket = new ServerSocket(12346);
        // Initialize the database connection
        String url = "jdbc:mysql://localhost:3306/login";
        String user = "root";
        String pass = "root";
        try {
            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Kết nối database thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IOException("Failed to connect to the database");
        }
    }

    public static Server getInstance() throws IOException {
        return server != null ? server : (server = new Server());
    }

    public void makeSocket() {
        while (!serverSocket.isClosed()){
            try{
                socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket,clients);
                clients.add(clientHandler);
                System.out.println("client socket accepted "+socket.toString());
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    public static boolean registerUser(String name, String password) throws SQLException {
        String hashedPassword = PasswordHasher.hashPassword(password);
        if (hashedPassword == null) {
            // Xử lý lỗi nếu không thể mã hóa mật khẩu
            return false;
        }

        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, hashedPassword);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }


    // Method to handle user authentication
    public static boolean authenticateUser(String name, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            String hashedPassword = PasswordHasher.hashPassword(password);
            statement.setString(2, hashedPassword);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        }
    }



}

