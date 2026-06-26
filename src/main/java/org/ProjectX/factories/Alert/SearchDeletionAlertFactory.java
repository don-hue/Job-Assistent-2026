package org.ProjectX.factories.Alert;

public class SearchDeletionAlertFactory extends AlertFactory{
    @Override
    public AlertInterface createAlert(){
        return new SearchDeletion();
    }
}
