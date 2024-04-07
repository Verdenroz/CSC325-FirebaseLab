package aydin.firebasedemospring2024;

import java.io.IOException;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class WelcomeController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private Label registerLabel;

    @FXML
    private Label noticeLabel;

    @FXML
    void loginButtonClicked(ActionEvent actionEvent) throws IOException {
        String inputEmail  = emailField.getText();
        String inputPassword = passwordField.getText();

        if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
            noticeLabel.setText("Please fill in all fields.");
            return;
        }

        if(loginUser(inputEmail, inputPassword)) {
            System.out.println("Login successful");
            DemoApp.setRoot("primary");
        } else {
            noticeLabel.setText("Invalid credentials.");
        }

    }

    public boolean loginUser(String email, String password) {
        try {
            DocumentSnapshot document = DemoApp.fstore.collection("users").document(email).get().get();
            if (document.exists()) {
                String storedPassword = (String) document.get("password");
                if (password.equals(storedPassword)) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }


    public void registerButtonClicked(MouseEvent mouseEvent) throws IOException {
        DemoApp.setRoot("registration");
    }
}
