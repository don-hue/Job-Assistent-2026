package org.ProjectX.factories.Alert;

public interface AlertInterface {
    default void showAlert(String title, String content) {
        System.out.println("Default needs to be overridden");
    };
    default void showAlertUpdateCompany(String title, String header, String text, String company, Runnable action) {
        System.out.println("Default needs to be overridden");
    };
}
