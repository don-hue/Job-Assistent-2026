package org.ProjectX.factories.Alert;

public class InformationAlertFactory extends AlertFactory {
    @Override
    public AlertInterface createAlert() {
        return new Information();
    }
}
