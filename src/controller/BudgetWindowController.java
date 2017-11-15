/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;


import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.PC;

/**
 * FXML Controller class
 *
 * @author Victor
 */
public class BudgetWindowController implements Initializable {
    @FXML
    private TabPane tabPane;
    @FXML
    private Button compareBut;
    @FXML
    private Button printBut;
    @FXML
    private Button exitBut;
    
    private List<PC> listaPCs;
    private Stage primaryStage;
    @FXML
    private Button modifyButton;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Change Locale to translate to English alerts displayed
        Locale.setDefault(Locale.US);
        
        //Inits tooltips
        compareBut.setTooltip(new Tooltip("Add a new PC to compare it with the rest"));
        printBut.setTooltip(new Tooltip("Prints the selected budget"));
        exitBut.setTooltip(new Tooltip("Close the application"));
        modifyButton.setTooltip(new Tooltip("Goes back to the previous window with the selected PC"));
         
    }    

    //->CustomizePCWindow with the list of PCs already done
    @FXML
    private void pressCompare(ActionEvent event) {
        onGoToSelectItemsWindow(listaPCs);
    }

    //Prints node of the selected tab
    @FXML
    private void pressPrint(ActionEvent event) throws Exception {
            PrinterJob job = PrinterJob.createPrinterJob();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TabWindow.fxml"));
            Parent root = loader.load();
            TabWindowController controller = loader.<TabWindowController>getController();
            controller.initStage(listaPCs.get(tabPane.getSelectionModel().getSelectedIndex()));
            controller.changesToPrint();
            Node node = (Node) root;
            VBox vbox = new VBox(60);
            vbox.setAlignment(Pos.CENTER);
            ImageView imagen = new ImageView("images/computer_256.png");
            imagen.setFitHeight(60);
            imagen.setFitWidth(300);
            vbox.getChildren().add(imagen);
            vbox.getChildren().add(node);
            if (job != null && job.showPrintDialog(new Stage())) {
            vbox.setScaleX(0.60);
            vbox.setTranslateX(-50);
            vbox.setTranslateY(-5);     
            boolean success = job.printPage(vbox);
            if (success) {
                job.endJob();
            }
        }
    }

    //Close the APP
    @FXML
    private void pressExit(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Do you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
        primaryStage.close();
        } 
    }

    //Initialize variables
    public void initStage(Stage primaryStage,List<PC> PCs) {
        this.primaryStage=primaryStage;
        this.listaPCs=PCs;
        initTabs();
    }
    
    //Go to CustomizePCWindow with all PCs to ADD A NEW ONE
    private void onGoToSelectItemsWindow(List<PC> listaVieja){
        try{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomizePCWindow.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        CustomizePCWindowController mainController =loader.<CustomizePCWindowController>getController();
        mainController.fromBudgetWindow(primaryStage,listaPCs,null);
        
        primaryStage.setTitle("Customize your PC");
        primaryStage.setScene(scene);
        primaryStage.show();
        }catch(IOException e){}
    }

    //Init tabs loading the TabWindow fxml and inits it with the controller
    private void initTabs() {
            for(int i=0;i<listaPCs.size();i++){
            try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TabWindow.fxml"));
            Parent root = loader.load();
            loader.<TabWindowController>getController().initStage(listaPCs.get(i));
            Node nodo = (Node) root;
            tabPane.getTabs().add(new Tab(listaPCs.get(i).getName()));
            tabPane.getTabs().get(i).setContent(nodo);
                }catch(IOException e){e.printStackTrace();}
            
            }    
    }

    //Removes actual PC selected in the list and goes to CustomizePC with it and the list of PCs
    @FXML
    private void modifyPress(ActionEvent event) {
        int i = tabPane.getSelectionModel().getSelectedIndex();
        PC modifyPC = listaPCs.get(i);
        listaPCs.remove(i);
        tabPane.getTabs().remove(i);
        onGoToSelectItemsWindowWithPC(listaPCs,modifyPC);
        
    }
    
    //Go to CustomizePCWindow with all PCs to MODIFY ONE OF THEM
    private void onGoToSelectItemsWindowWithPC(List<PC> listaVieja,PC modifPC){
        try{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomizePCWindow.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        CustomizePCWindowController mainController =loader.<CustomizePCWindowController>getController();
        mainController.fromBudgetWindow(primaryStage,listaVieja, modifPC);
        
        primaryStage.setTitle("Customize your PC");
        primaryStage.setScene(scene);
        primaryStage.show();

        }catch(IOException e){}
    }
    
}
