/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;


import es.upv.inf.Product.Category;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import model.Componente;
import model.PC;

/**
 * FXML Controller class
 *
 * @author Víctor
 */
public class TabWindowController implements Initializable {

    @FXML
    private VBox theVBox;
    @FXML
    private TableView<Componente> tableView;
    @FXML
    private TableColumn<Componente,Category> catCol;
    @FXML
    private TableColumn<Componente,String> itemCol;
    @FXML
    private TableColumn<Componente,Integer> quantCol;
    @FXML
    private TableColumn<Componente,Double> priceCol;
    @FXML
    private Label totalLabel;
    @FXML
    private Label IVALabel;
    @FXML
    private Label total;
    @FXML
    private Label IVA;
    @FXML
    private Label labelData;


    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

       
    }    
    
    //Inits all the Budget of a PC
    public void initStage(PC aPC){
        
    //Display of the tableView
    priceCol.setCellValueFactory(c-> new SimpleDoubleProperty(c.getValue().getPrice()).asObject());
    quantCol.setCellValueFactory(new PropertyValueFactory<Componente,Integer>("cantidad"));
    itemCol.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getDescrip()));
    tableView.setItems(FXCollections.observableArrayList(aPC.getComponents()));
    
    //Computes total price and NAT price
    List<Componente> lista = aPC.getComponents();
    Double price = 0.0;
    for(int i=0;i<lista.size();i++){
      Componente comp = lista.get(i);
      price+=comp.getCantidad()*comp.getPrice();
    }
    price = (Math.round(price * Math.pow(10,2))) / Math.pow(10,2);
    total.setText(price.toString() + " €");
    price= (Math.round(price * 1.21 * Math.pow(10, 2))) / Math.pow(10,2);
    IVA.setText(price.toString() + " €");
    
    //Calculates days elapsed from the creation of the PC and today
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    Date dat = new Date();
    long time= dat.getTime() - aPC.getDate().getTime();
    time = Math.round(time / (3600*24*1000));
    int daysElapsed = 7 - (int) time;
    String s;
    if(daysElapsed<=0){s = " This budget is timed out";}
    else{s=" . This budget will time out in " + daysElapsed + " days";}
    labelData.setText("Budget made in " + df.format(aPC.getDate()) + s);
    
    }
    
    //Used to print
    public void changesToPrint(){
        tableView.autosize();
        tableView.setFixedCellSize(25);
        itemCol.setMinWidth(400);
        tableView.setMinHeight(30*tableView.getItems().size());
        tableView.prefHeightProperty().bind(Bindings.size(tableView.getItems()).multiply(tableView.getFixedCellSize()).add(30));
    }
    
}
