    package controller;

    import com.jfoenix.controls.JFXPasswordField;
    import com.jfoenix.controls.JFXTextField;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Scene;
    import javafx.scene.control.Alert;
    import javafx.stage.Stage;
    import server.Server;

    import java.io.IOException;
    import java.sql.SQLException;

    public class LoginFormController {
        public JFXTextField txtName;
        public JFXPasswordField txtPassword;

        public void initialize() {
            // Any initialization code if needed
        }

        public void logInButtonOnAction(ActionEvent actionEvent) throws IOException {
            String name = txtName.getText();
            String password = txtPassword.getText();

            if (!name.isEmpty() && !password.isEmpty()) {
                try {
                    if (Server.authenticateUser(name, password)) {
                        openClientForm(name);
                    } else {
                        showErrorAlert("Tài khoản hoặc mật khẩu sai");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showErrorAlert("Failed to authenticate user");
                }
            } else {
                showErrorAlert("Vui lòng nhập tài khoản hoặc mật khẩu");
            }
        }

        public void registerButtonOnAction(ActionEvent actionEvent) {
            String name = txtName.getText();
            String password = txtPassword.getText();

            if (!name.isEmpty() && !password.isEmpty()) {
                try {
                    boolean registrationSuccess = Server.registerUser(name, password);
                    if (registrationSuccess) {
                        showInfoAlert("Đăng kí thành công");
                        txtName.clear();
                        txtPassword.clear();
                    } else {
                        showErrorAlert("Đăng kí thất bại");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showErrorAlert("Failed to register user");
                }
            } else {
                showErrorAlert("Please enter a valid username and password");
            }
        }


        private void openClientForm(String name) throws IOException {
            Stage primaryStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/ClientForm.fxml"));

            ClientFormController controller = new ClientFormController();
            controller.setClientName(name);
            fxmlLoader.setController(controller);

            primaryStage.setScene(new Scene(fxmlLoader.load()));
            primaryStage.setTitle(name);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.setOnCloseRequest(windowEvent -> {
                controller.shutdown();
            });
            primaryStage.show();

            txtName.clear();
            txtPassword.clear();
        }

        private void showErrorAlert(String message) {
            new Alert(Alert.AlertType.ERROR, message).show();
            txtName.clear();
            txtPassword.clear();
        }

        private void showInfoAlert(String message) {
            new Alert(Alert.AlertType.INFORMATION, message).show();
        }
    }
