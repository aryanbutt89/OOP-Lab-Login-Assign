package application;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.stage.Stage;


public class RegistrationController {

    @FXML
    private TextField usernameField;
    
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    

    @FXML
    private void handleRegister() {
        if (usernameField == null || passwordField == null || emailField == null) {
            System.out.println("Error: TextField is null");
            return;
        }

        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        // Check if the email ends with "@gmail.com"
        if (!email.toLowerCase().endsWith("@gmail.com")) {
            showAlert("Invalid Email", "Please enter a valid Gmail address.");
            return;
        }

        if (registerUser(username, password, email)) {
            // Registration successful, close the registration form
            showAlert("Registration Successful", "User " + username + " has been registered.");
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.close();
        } else {
            // Registration failed, handle the error (e.g., display an error message)
            System.out.println("Registration failed!");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // Method to register a user in the database
 // Method to register a user in the database
    private boolean registerUser(String username, String password, String email) {
        try {
            // Establish database connection
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hell", "root", "S@fyan82");

            // Fetch the last ID in the users table
            String queryLastID = "SELECT MAX(ID) FROM users";
            PreparedStatement preparedStatementLastID = conn.prepareStatement(queryLastID);
            ResultSet resultSetLastID = preparedStatementLastID.executeQuery();
            resultSetLastID.next();
            int lastID = resultSetLastID.getInt(1);

            // SQL query to insert a new user into the database
            String queryInsert = "INSERT INTO users (ID, Username, Email, Password) VALUES (?, ?, ?, ?)";

            // Create a PreparedStatement object to execute the query
            PreparedStatement preparedStatement = conn.prepareStatement(queryInsert);

            // Set the parameters for the PreparedStatement
            preparedStatement.setInt(1, lastID + 1);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, password);

            // Execute the query to insert the user into the database
            int rowsInserted = preparedStatement.executeUpdate();

            // Close the PreparedStatement and database connection
            preparedStatement.close();
            conn.close();

            // Check if the user was successfully inserted into the database
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}