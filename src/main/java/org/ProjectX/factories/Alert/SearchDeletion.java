package org.ProjectX.factories.Alert;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.ProjectX.Database.CompanyRepository;
import org.ProjectX.Database.SearchUrlRepository;

public class SearchDeletion implements AlertInterface{
    public void showAlertDeleteSearch(String title, String header, String text, String url, Runnable action){
        SearchUrlRepository db = SearchUrlRepository.getInstance();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(_ -> {
                    db.deleteSearch(url);
                    action.run();
                });
    }
}

