package org.ProjectX.factories.Checkbox;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.ProjectX.Database.JobRepository;


public class Applied implements CheckboxInterface {
    public HBox createSpecificCheckbox(String company, String jobTitle,boolean applied, Runnable action) {
        HBox checkboxContainer = new HBox(5);
        CheckBox checkbox = new CheckBox();

        checkboxContainer.setAlignment(Pos.BOTTOM_CENTER);
        HBox.setMargin(checkbox, new Insets(0, 0, 5, 0));
        Label checkboxTitle = new Label("Beworben");
        checkboxTitle.setStyle("-fx-font-size: 16px;");
        checkbox.setSelected(applied);

        checkbox.setOnAction(_ -> {
            JobRepository db = JobRepository.getInstance();
            db.updateAppliedJob(checkbox.isSelected(), company, jobTitle);
            action.run();
        });
        checkboxContainer.getChildren().addAll(checkbox, checkboxTitle);
        return checkboxContainer;
    }
}
