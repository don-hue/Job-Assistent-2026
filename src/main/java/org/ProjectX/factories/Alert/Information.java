package org.ProjectX.factories.Alert;

import javafx.scene.control.Alert;

public class Information implements AlertInterface {
    public void showAlert(String title ,String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
