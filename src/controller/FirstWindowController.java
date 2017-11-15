/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Víctor
 */
public class FirstWindowController implements Initializable {

    @FXML
    private Button cancelbutton;
    @FXML
    private Button configurebutton;
    
    private Stage primaryStage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Change Locale to translate to English alerts displayed
        Locale.setDefault(Locale.US);
        
        //Inits tooltips
        configurebutton.setTooltip(new Tooltip("Starts the configuration of a PC"));
        cancelbutton.setTooltip(new Tooltip("Exits the APP"));
        
    }    
    
    //Inits variable stage
    public void initStage(Stage stage) {
        primaryStage = stage;
    }
    
    //Cancel -> Close
    @FXML
    private void pressCancel(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Do you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
        primaryStage.close();
        } 
    }
    
    // Configure -> Go to SelectItemsWindow
    @FXML
    private void pressConfigure(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomizePCWindow.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        CustomizePCWindowController mainController =loader.<CustomizePCWindowController>getController();
        mainController.initStage(primaryStage);
        
        primaryStage.setTitle("Customize your PC");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
}
