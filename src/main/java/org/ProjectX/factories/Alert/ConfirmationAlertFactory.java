package org.ProjectX.factories.Alert;

public class ConfirmationAlertFactory extends AlertFactory {
    @Override
    public AlertInterface createAlert(){
        return new Confirmation();
    }
}
