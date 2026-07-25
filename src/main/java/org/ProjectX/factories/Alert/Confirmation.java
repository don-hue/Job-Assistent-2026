package org.ProjectX.factories.Alert;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.ProjectX.database.CompanyRepository;

public class Confirmation implements AlertInterface {
     @Override
    public void showAlertUpdateCompany(String title, String header, String text, String company, Runnable action) {
        CompanyRepository db = CompanyRepository.getInstance();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(_ -> {
                    db.updateShowCompany(company);
                    action.run();
                });
    }
}
