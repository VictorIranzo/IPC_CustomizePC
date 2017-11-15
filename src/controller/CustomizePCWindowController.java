/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import es.upv.inf.Product;
import es.upv.inf.Product.Category;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import model.Componente;
import model.ListPCWrapper;
import model.PC;

/**
 * FXML Controller class
 *
 * @author vicirji
 */
public class CustomizePCWindowController implements Initializable {
    
    private Stage primaryStage;
    private List<PC> listaAnterior;
    private PC ourPC;
    private Image yes = new Image("images/yes.png");
    private Image no = new Image("images/no.png");
    private List<ImageView> listaImages;
    private Date dataLoad;
    private boolean loaded;
    private Stage filterStage;
    private int numFilterOpen;
    
    @FXML
    private TableView<Componente> tableView;
    @FXML
    private TableColumn<Componente,String> qCol;
    @FXML
    private TableColumn<Componente,Category> catCol;
    @FXML
    private TableColumn<Componente,Product> productCol;
    @FXML
    private TableColumn<Componente,Double> priceCol;
    private TableColumn<Componente,Integer> stockCol;
    @FXML
    private ComboBox<PC> comboPredifined;
    @FXML
    private ImageView motherImage;
    @FXML
    private ImageView RAMImage;
    @FXML
    private ImageView processorImage;
    @FXML
    private ImageView caseImage;
    @FXML
    private ImageView GPUImage;
    @FXML
    private ImageView HDImage;
    @FXML
    private Button deleteButton;
    @FXML
    private Button continueButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button loadButton;
    @FXML
    private Button addButton;
    
     
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    //Change Locale to translate to English alerts displayed
    Locale.setDefault(Locale.US);
    
    //Carga tooltips de los botones
    saveButton.setTooltip(new Tooltip("Saves the current PC into a file"));
    deleteButton.setTooltip(new Tooltip("Deletes the selected component"));
    continueButton.setTooltip(new Tooltip("Continue to next window, where you can compare other budgets"));
    addButton.setTooltip(new Tooltip("Open a window to add new components"));
    loadButton.setTooltip(new Tooltip("Loads a PC made by the user"));
    comboPredifined.setTooltip(new Tooltip("Allows the selection of a PC made by the enterprise"));

    // Inicializa comboPredifined -> pcs.xml
    try{
    File file = new File("src/library/pcs.xml");
    JAXBContext jaxbContext = JAXBContext.newInstance(ListPCWrapper.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    ListPCWrapper pcs = (ListPCWrapper) jaxbUnmarshaller.unmarshal(file);
    ObservableList<PC> data2= FXCollections.observableArrayList(pcs.getPCList());
    comboPredifined.setItems(data2);
    }catch(Exception e){e.printStackTrace();}

    //Inicializa la vista de las columnas de la TableView    
    priceCol.setCellValueFactory(c-> new SimpleDoubleProperty(c.getValue().getPrice()).asObject());
    
    productCol.setCellValueFactory(new PropertyValueFactory<Componente,Product>("producto"));
    productCol.setCellFactory(v -> new TableCell<Componente,Product>() {
            @Override 
            protected void updateItem(Product item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
            setText(null);
            } else {
                setText(item.getDescription().toUpperCase());
                }
            }
        });
    
    catCol.setCellValueFactory(c-> new SimpleObjectProperty<Category>(c.getValue().getCategory()));
    
    //Hacemos editable la columna qCol, donde se indicará la cantida del producto
    tableView.setEditable(true);
    qCol.setCellFactory(TextFieldTableCell.forTableColumn());
    qCol.setCellValueFactory(c->new SimpleStringProperty(Integer.toString(c.getValue().getCantidad())));
    
    //Listener al comboBox de predifinidos -> Selección de uno: actualizar tableView
    comboPredifined.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PC>(){
             @Override
             public void changed(ObservableValue<? extends PC> observable, PC oldValue, PC newValue) {
                 dataLoad = newValue.getDate();
                 loaded= true;
                 passPC(newValue);
             }
         });
     
    //Deshabilitar botones Delete(hasta que se seleccione un Componente)
    // y continue hasta que no se tengan todos los componentes obligatorios
    deleteButton.disableProperty().bind(
            Bindings.equal(-1,
        tableView.getSelectionModel().selectedIndexProperty()));

    continueButton.disableProperty().set(true);
     
    }    

    //Cargamos un PC desde el archivo del usuario, preguntando via ChoiceDialogue -> loaded to True
    @FXML
    private void loadPCpress(ActionEvent event) {
        try{
        File file = new File("src/library/pcs_user.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(ListPCWrapper.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        ListPCWrapper pcs = (ListPCWrapper) jaxbUnmarshaller.unmarshal(file);
        List<PC> choices = pcs.getPCList();
        ChoiceDialog<PC> dialog = new ChoiceDialog<PC>(new PC(),choices);
        dialog.setTitle("Load a PC");
        dialog.setHeaderText("Load a PC");
        dialog.setContentText("Select a PC");
        Optional<PC> result = dialog.showAndWait();
        if (result.isPresent()) {
            dataLoad = result.get().getDate();
            loaded=true;
            passPC(result.get());
        }
        }catch(Exception e){e.printStackTrace();}
    }
    
    //Guardamos un PC en el archivo del usuario -> Preguntar nombre PC
    //Condición: todos los componentes obligatorios en el PC
    @FXML
    private void savePCpress(ActionEvent event){
        PC nextPC = new PC("nextPC");
        if(addComponents(nextPC)){
        String s=nextPC.checkMandatoryElements();
        if(!s.equals("OK")){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("You haven't selected mandatory elements");
            alert.setContentText(s);
            alert.showAndWait();
        }
        else{
            TextInputDialog dialog = new TextInputDialog("My PC"); // Default value
            dialog.setTitle("Give a name to your PC");
            dialog.setContentText("Enter a name:");
            Optional<String> result = dialog.showAndWait();
            if(result.isPresent()){
                nextPC.setName(result.get());
                    try {
                    File file = new File("src/library/pcs_user.xml");
                    JAXBContext jaxbContext = JAXBContext.newInstance(ListPCWrapper.class);
                    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                    ListPCWrapper pcs = (ListPCWrapper) jaxbUnmarshaller.unmarshal(file);
                    //ListPCWrapper pcs = new ListPCWrapper();
                    pcs.addtoWrapper(nextPC);
                    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    jaxbMarshaller.marshal(pcs, file); // save to a file
                    jaxbMarshaller.marshal(pcs, System.out); // echo to the console
                    } catch (JAXBException e) {e.printStackTrace();}
                }
            }
        }
    }

    //Opens FilterWindow
    @FXML
    private void addPress(ActionEvent event) {
                onGoToFilterWindow(ourPC);
    }
    
    //Comes from the BudgetWindow with all PCs made, inits all variables 
    //and updates tableView to the one being modified
    //Use: when we are modifying a createdPC and when we add a new One
    public void fromBudgetWindow(Stage primaryStage, List<PC> listaPCs, PC modifyPC) {
        this.listaAnterior=listaPCs;
        this.primaryStage=primaryStage;
        this.listaImages = new ArrayList<ImageView>();
        this.filterStage=null;
        this.numFilterOpen=0;
        listaImages.add(motherImage);
        listaImages.add(processorImage);
        listaImages.add(GPUImage);
        listaImages.add(RAMImage);
        listaImages.add(caseImage);
        listaImages.add(HDImage);
        if(modifyPC==null){this.ourPC= new PC("ourPC"); this.loaded= false; this.dataLoad=null;}
        else{
            this.dataLoad= modifyPC.getDate();
            this.loaded=true;
            passPC(modifyPC);
        }
    }

    //Check if PC has all mandatory elements. Correct -> onGoToBudgetWindow
    @FXML
    private void continuePress(ActionEvent event) {
        PC nextPC;
        if(loaded){nextPC = new PC(ourPC.getName(),this.dataLoad);}
        else{
        nextPC = new PC("nextPC");}
        if(addComponents(nextPC)){
        String s=nextPC.checkMandatoryElements();
        if(!s.equals("OK")){
            
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("You haven't selected mandatory elements");
            alert.setContentText(s);
            alert.showAndWait();
            
        }
        else{
                listaAnterior.add(nextPC);
                onGoToBudgetWindow(listaAnterior,nextPC);
                }
        
        }
    }
    
    //Inits all variables when we come from FirstWindow
    public void initStage(Stage newStage){
        this.filterStage=null;
        this.numFilterOpen=0;
        this.primaryStage = newStage;
        this.listaAnterior = new ArrayList<PC>();
        this.ourPC = new PC("ourPC");
        this.listaImages = new ArrayList<ImageView>();
        this.dataLoad= null;
        this.loaded= false;
        listaImages.add(motherImage);
        listaImages.add(processorImage);
        listaImages.add(GPUImage);
        listaImages.add(RAMImage);
        listaImages.add(caseImage);
        listaImages.add(HDImage);
        updateImages();
    }
    
    //Adds all components to thePC. If we overflow stock -> return false
    private boolean addComponents(PC thePC){
        List<Componente> listaComp= new ArrayList(tableView.getItems());
        for(int i=0;i<listaComp.size();i++){
            Componente c = listaComp.get(i);
            if(c.getCantidad()>c.getStock()){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Exceeded the stock for a Product:");
            alert.setContentText(c.getDescrip());
            alert.showAndWait();
                return false;
            }
            else{
                thePC.addElement(c);
                }
        }            
        return true;
    }
    
    //IF loaded, don't ask a new name for the PC
    //ELSE ask for a name for current PC
    //-> BudgetWindow
    private void onGoToBudgetWindow(List<PC> listaNueva, PC currentPC){
        if(filterStage!=null){filterStage.close();}
        if(!loaded){    
            TextInputDialog dialog = new TextInputDialog("My PC"); // Default value
            dialog.setTitle("Give a name to your PC");
            dialog.setContentText("Enter a name:");
            Optional<String> result = dialog.showAndWait();
            if(result.isPresent()){
                currentPC.setName(result.get());
            }
        }
        try{    
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BudgetWindow.fxml"));
        Parent root = (Parent) loader.load();
        Scene scene = new Scene(root);
        loader.<BudgetWindowController>getController().initStage(primaryStage,listaNueva);
        primaryStage.setTitle("Budgets");
        primaryStage.setScene(scene);
        primaryStage.show();
        }catch(IOException e){e.printStackTrace();}
    }
    
    //->FilterWindow    
    private void onGoToFilterWindow(PC thePC){
        if(this.numFilterOpen<1){ this.numFilterOpen++;
        try {
        filterStage = new Stage();
        FXMLLoader myLoader = new FXMLLoader(getClass().getResource("/view/FilterWindow.fxml"));
        Parent root = myLoader.load();
        FilterWindowController newWindow=myLoader.<FilterWindowController>getController();
        newWindow.initStage(filterStage,thePC,CustomizePCWindowController.this);
        Scene scene = new Scene(root);
        filterStage.setScene(scene);
        filterStage.setTitle("Filter");
        filterStage.getIcons().add(new Image("images/pc.png"));
        filterStage.show();    
        } catch (IOException e) {e.printStackTrace();}
        } 
    }
    
    //Reduece the num of FilterWindow opened
    public void closeFilterWindow(){this.numFilterOpen--;}

    //Changes currentPC reference to PC passed as parameter, updates tableView
    //to thePC elements and updates images of mandatory elements
    public void passPC(PC thePC) {
        this.ourPC=thePC;
        ObservableList<Componente> data2= FXCollections.observableArrayList(new ArrayList<Componente>());
        data2.addAll(thePC.getComponents());
        tableView.setItems(data2);
        updateImages();
    }

    //Deletes the component selected in the tableView
    @FXML
    private void deletePress(ActionEvent event) {
        if(!tableView.getSelectionModel().isEmpty()){
            ourPC.deleteElement(tableView.getSelectionModel().getSelectedIndex());
            passPC(ourPC);
        }
    }

    //Update images: X: not present , Tick: preset
    //IF mandatory elements -> enable Save button and Continue button
    private void updateImages(){
        for(int i=0;i<listaImages.size();i++){
            listaImages.get(i).setImage(no);
        }
        for(int i=0; i<tableView.getItems().size();i++){
            Componente c = tableView.getItems().get(i);
            Category cat = c.getCategory();
            if(cat.equals(Category.CPU)){processorImage.setImage(yes);}
            if(cat.equals(Category.CASE)){caseImage.setImage(yes);}
            if(cat.equals(Category.MOTHERBOARD)){motherImage.setImage(yes);}
            if(cat.equals(Category.RAM)){RAMImage.setImage(yes);}
            if(cat.equals(Category.GPU)){GPUImage.setImage(yes);}
            if(cat.equals(Category.HDD) || cat.equals(Category.HDD_SSD)){HDImage.setImage(yes);}
        }
        if(ourPC.checkMandatoryElements().equals("OK")){
            saveButton.disableProperty().set(false);
            continueButton.disableProperty().set(false);}
        else{saveButton.disableProperty().set(true);
            continueButton.disableProperty().set(true);}
    }

    //If a quantity cell is edited, the quantity of the component is changed too
    @FXML
    private void editQMake(TableColumn.CellEditEvent<Componente,String> event) {
       tableView.getSelectionModel().getSelectedItem().changeQ(event.getNewValue());
    }


}
