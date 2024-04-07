package aydin.firebasedemospring2024;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RegistrationController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private Label noticeLabel;


    @FXML
    void registerButtonClicked(ActionEvent event) throws IOException {
        if (registerUser()) {
            DemoApp.setRoot("primary");
        }
    }

    public boolean registerUser() {
        // Check if any fields are empty
        if (nameField.getText().isEmpty() || emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            noticeLabel.setText("Please fill in all fields.");
            return false;
        }

        // Check if a username or email already exists in Firestore
        if (emailExists(emailField.getText())) {
            noticeLabel.setText("Username or email already exists.");
            return false;
        }

        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(emailField.getText())
                .setEmailVerified(false)
                .setPassword(passwordField.getText())
                .setDisplayName(nameField.getText())
                .setDisabled(false);
        UserRecord userRecord;
        try {
            userRecord = DemoApp.fauth.createUser(request);
        } catch (FirebaseAuthException e) {
            noticeLabel.setText(e.getMessage());
            return false;
        }

        CollectionReference userCollection = DemoApp.fstore.collection("users");
        Map<String, Object> data = new HashMap<>();
        data.put("username", nameField.getText());
        data.put("email", emailField.getText());
        data.put("password", passwordField.getText());
        ApiFuture<WriteResult> writeResult = userCollection.document(emailField.getText()).set(data);

        try {
            writeResult.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public void loginButtonClicked(MouseEvent actionEvent) throws IOException {
        DemoApp.setRoot("welcome");
    }

    /**
     * Check if an email already exists in Firestore
     * @param email the email to check
     * @return true if the email exists, false otherwise
     */
    public boolean emailExists(String email) {
        try {
            QuerySnapshot document = DemoApp.fstore.collection("users").whereEqualTo("email", email).get().get();
            return !document.isEmpty();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
