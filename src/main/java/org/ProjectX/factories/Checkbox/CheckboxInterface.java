package org.ProjectX.factories.Checkbox;
import javafx.scene.layout.HBox;

public interface CheckboxInterface {
    HBox createSpecificCheckbox(String company, String jobTitle, boolean applied, Runnable action);
}
