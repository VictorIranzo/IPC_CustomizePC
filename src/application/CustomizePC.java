/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import controller.FirstWindowController;
import java.util.Locale;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Víctor
 */
public class CustomizePC extends Application {
    
    
    @Override
    public void start(Stage stage) throws Exception {
        //Loads FirstWindow
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FirstWindow.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        FirstWindowController mainController =loader.<FirstWindowController>getController();
        mainController.initStage(stage);
        
        stage.setTitle("Customize your PC");
        stage.getIcons().add(new Image("images/pc.png"));
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        Locale.setDefault(Locale.UK);
    }
    
}
   
