package org.ProjectX;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.ProjectX.util.HibernateUtil;


/*

toDo: Input Validator for URL Builder

toDo: Implement JMS for Event-based Communication
toDo: Crawler Factory Design Pattern
toDo: showAlert in Utility amd make every error show for user
toDO: optimize CSS for Alerts
toDO: ErrorHandling with UI
 */


public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/WelcomeScreen.fxml"));
        Scene welcomeScene = new Scene(root);
        stage.setScene(welcomeScene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Shutting down Hibernate...");
        HibernateUtil.getSessionFactory().close();
        super.stop();
    }


}