package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private DatabaseConnection databaseConnection = new DatabaseConnection();

    @FXML
    void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (validateLogin(username, password)) {
            showAlert("Login Successful", "Welcome " + username);
        } else {
            showAlert("Login Failed", "Invalid username or password");
        }
    }

    @FXML
    void handleRegister(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Registration.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (!username.isEmpty() && !password.isEmpty()) {
            if (deleteUser(username, password)) {
                showAlert("Deletion Successful", "User " + username + " has been deleted.");
            } else {
                showAlert("Deletion Failed", "Could not delete user. User might not exist.");
            }
        } else {
            showAlert("Deletion Failed", "Please enter both username and password.");
        }
    }

    @FXML
    void handleDeleteAllUsers(ActionEvent event) {
        // Authenticate user before allowing deletion of all users
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Authentication");
        dialog.setHeaderText("Enter Admin Credentials");
        dialog.setContentText("Username:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String enteredUsername = result.get();
            // Check if enteredUsername is the admin username
            if (enteredUsername.equals("ARYAN")) {
                // If username is correct, prompt for password
                PasswordField passwordInput = new PasswordField();
                dialog.getDialogPane().setContent(passwordInput);
                dialog.setHeaderText("Enter Password:");
                dialog.setContentText("Password:");
                Optional<String> passwordResult = dialog.showAndWait();
                if (passwordResult.isPresent()) {
                    String enteredPassword = passwordResult.get();
                    // Check if enteredPassword is the admin password
                    if (enteredPassword.equals("S@fyan82")) {
                        // If both username and password are correct, proceed with deleting all users
                        if (deleteAllUsers(enteredUsername, enteredPassword)) {
                            showAlert("Deletion Successful", "All users have been deleted.");
                        } else {
                            showAlert("Deletion Failed", "Failed to delete all users.");
                        }
                    } else {
                        showAlert("Authentication Failed", "Invalid password.");
                    }
                }
            } else {
                showAlert("Authentication Failed", "Invalid username.");
            }
        }
    }

    private boolean validateLogin(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean deleteUser(String username, String password) {
        String query = "DELETE FROM users WHERE username = ? AND password = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean deleteAllUsers(String adminUsername, String adminPassword) {
        // Implement the logic to delete all users from the database
        // Ensure proper authentication before performing this action
        if (adminUsername.equals("ARYAN") && adminPassword.equals("S@fyan82")) {
            // If admin credentials are correct, proceed with deleting all users
            try (Connection connection = databaseConnection.getConnection();
                 Statement statement = connection.createStatement()) {
                // Execute SQL query to delete all users
                int rowsAffected = statement.executeUpdate("DELETE FROM users");
                return rowsAffected > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            // If admin credentials are incorrect, return false
            return false;
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
