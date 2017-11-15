/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import es.upv.inf.Database;
import es.upv.inf.Product;
import es.upv.inf.Product.Category;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Componente;
import model.PC;

/**
 * FXML Controller class
 *
 * @author Victor
 */
public class FilterWindowController implements Initializable {

    private CustomizePCWindowController ourWindow;
    private PC thePC;
    
    @FXML
    private ComboBox<Category> comboCat;
    @FXML
    private TextField descriptionText;
    @FXML
    private TextField minPrice;
    @FXML
    private TextField maxPrice;
    @FXML
    private Button inspectButton;
    @FXML
    private TableView<Product> tableView;
    @FXML
    private TableColumn<Product,Category> categoryCol;
    @FXML
    private TableColumn<Product,String> descriptionCol;
    @FXML
    private TableColumn<Product,Double> priceCol;
    @FXML
    private TableColumn<Product,Integer> stockCol;
    @FXML
    private Button addButton;
    @FXML
    private Button backButton;
    
    private Stage primaryStage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Change Locale to translate to English alerts displayed
        Locale.setDefault(Locale.US);

        //First, we init tableView to all products in the database
        tableView.setItems(initTableView());
        
        //Inits the tooltips
        addButton.setTooltip(new Tooltip("Adds the selected product to the actual PC"));
        backButton.setTooltip(new Tooltip("Close this window"));
        inspectButton.setTooltip(new Tooltip("Filters list of products following the input fields"));
        
        //Display of columns in the tableView
        priceCol.setCellValueFactory(new PropertyValueFactory<Product,Double>("price"));
        stockCol.setCellValueFactory(new PropertyValueFactory<Product,Integer>("stock"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<Product,String>("description"));       
        categoryCol.setCellValueFactory(new PropertyValueFactory<Product,Category>("category"));
        
        //Init of the comboBox of categories
        //We also add a null category -> Filter by all categories
        ObservableList<Category> data = FXCollections.observableArrayList(Category.values());
        data.add(0,null);
        comboCat.setItems(data);
        
        //Listener to comboCat: selection -> filter automatically
        comboCat.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Category>(){
             @Override
             public void changed(ObservableValue<? extends Category> observable, Category oldValue, Category newValue) {
                 inspectPress(new ActionEvent());
             }
         });
        
        //Listeners to min and max Price
        minPrice.textProperty().addListener(new ChangeListener<String>(){
             @Override
             public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                 inspectPress(new ActionEvent());
             }
         });
        maxPrice.textProperty().addListener(new ChangeListener<String>(){
             @Override
             public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                 inspectPress(new ActionEvent());
             }
         });
        
        //Listener to description TextField: change -> filter automatically
        descriptionText.textProperty().addListener(new ChangeListener<String>(){
             @Override
             public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                 inspectPress(new ActionEvent());
             }
         });
        
        //Disables addButton if any product is selected
        addButton.disableProperty().bind(
                Bindings.equal(-1,
            tableView.getSelectionModel().selectedIndexProperty()));
        
        //If we make double click in an element = press Add
        tableView.setRowFactory( tv -> {
            TableRow<Product> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
            Product rowData = row.getItem();
            thePC.addElement(new Componente(tableView.getSelectionModel().getSelectedItem(),1));
            ourWindow.passPC(thePC);
            }
            });
        return row ;
        });
        
    }    

    //Filter taking into account if we have a Category selected and all fields
    @FXML
    private void inspectPress(ActionEvent event) {
        List<Product> filterList = new ArrayList<Product>();
        String des = descriptionText.getText();
        double min=0,max=Double.MAX_VALUE;
            try{
        if(!minPrice.getText().isEmpty())min= Double.parseDouble(minPrice.getText());
        if(!maxPrice.getText().isEmpty())max= Double.parseDouble(maxPrice.getText());
                
        if((comboCat.getSelectionModel().getSelectedIndex())!=0 && !comboCat.getSelectionModel().isEmpty()){
             Category cat = comboCat.getSelectionModel().getSelectedItem();
            if(!des.isEmpty()){filterList=Database.getProductByCategoryDescriptionAndPrice(cat, des, min, max, true);}
            else{filterList=Database.getProductByCategoryAndPrice(cat, min, max, true);}
        }
        else{
          if(!des.isEmpty()){filterList=allByDescriptionAndPrice(des,min,max);}
          else{filterList=allByPrice(min,max);}
        }
        ObservableList<Product> data= FXCollections.observableArrayList(filterList);
        tableView.setItems(data);
        }catch(NumberFormatException e){onGoToErrorWindow();}
    }
    
    //Add component with a quantity of 1 (see in CustomizePCWindow)
    @FXML
    private void addPress(ActionEvent event) {
        thePC.addElement(new Componente(tableView.getSelectionModel().getSelectedItem(),1));
        ourWindow.passPC(thePC);

    }

    //Close FilterWindow
    @FXML
    private void backPress(ActionEvent event) {
        this.ourWindow.closeFilterWindow();
        primaryStage.close();
    }

    //Initialize variables
    public void initStage(Stage aNewStage,PC thePC, CustomizePCWindowController ourWindow) {
        this.primaryStage=aNewStage;
        this.ourWindow=ourWindow;
        this.thePC=thePC;
    }
    
    //Inits the tableView to all the elements in the Database
    private ObservableList<Product> initTableView(){
        List<Product> lista = new ArrayList<Product>();
        for(int i=0;i<Category.values().length;i++){
            lista.addAll(Database.getProductByCategory(Category.values()[i]));
        }
        ObservableList<Product> data= FXCollections.observableArrayList(lista);
        return data;
    }

    //Filter in all categories by price
    private List<Product> allByPrice(double min, double max) {
        List<Product> res = new ArrayList<Product>();
        for(int i=0;i<Category.values().length;i++){
            res.addAll(Database.getProductByCategoryAndPrice(Category.values()[i],min,max,true));
        }
        return res;
    }

    //Filter in all categories by description and price
    private List<Product> allByDescriptionAndPrice(String des, double min, double max) {
        List<Product> res = new ArrayList<Product>();
        for(int i=0;i<Category.values().length;i++){
            res.addAll(Database.getProductByCategoryDescriptionAndPrice(Category.values()[i],des,min,max,true));
        }
        return res;
    }

    //Filter in all categories by description
    private List<Product> allByDescription(String des) {
        List<Product> res = new ArrayList<Product>();
        for(int i=0;i<Category.values().length;i++){
            res.addAll(Database.getProductByCategoryAndDescription(Category.values()[i],des,true));
        }
        return res;    
    }

    //Opens a dialogue : Bad Input in a number
    private void onGoToErrorWindow() {
         Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            // or null if we do not want a header
            alert.setContentText("Bad Input in a number");
            alert.showAndWait();
    }
    
}
