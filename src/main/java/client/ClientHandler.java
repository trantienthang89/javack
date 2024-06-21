package client;
import server.Server;

import java.io.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ClientHandler  {
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private List<ClientHandler> clients;
    private Connection connection;
    private BufferedReader in;
    private PrintWriter out;
    private String msg = "";

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        try {
            this.socket = socket;
            this.clients = clients;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        }catch (IOException e){
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (socket.isConnected()) {
                        msg = dataInputStream.readUTF();
                        for (ClientHandler clientHandler : clients) {
                            if (clientHandler.socket.getPort() != socket.getPort()) {
                                clientHandler.dataOutputStream.writeUTF(msg);
                                clientHandler.dataOutputStream.flush();
                            }
                        }
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void handleLogin(String username, String password) {
        try {

            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                out.println("LOGIN_SUCCESS");
            } else {
                out.println("LOGIN_FAILURE");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("LOGIN_FAILURE");
        }
    }

    private void handleRegister(String username, String password) {
        try {

            PreparedStatement checkStmt = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                out.println("REGISTER_FAILURE");
            } else {
                PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
                insertStmt.setString(1, username);

                insertStmt.executeUpdate();
                out.println("REGISTER_SUCCESS");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("REGISTER_FAILURE");
        }
    }
}
