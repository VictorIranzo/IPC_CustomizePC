/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import es.upv.inf.Product;
import es.upv.inf.Product.Category;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Alert;

/**
 *
 * @author Victor
 */
public class Componente{
    private final IntegerProperty cantidad = new SimpleIntegerProperty();
    
    private String stringC;
    
    public Componente(){}

    public int getCantidad() {
        return cantidad.get();
    }

    public void setCantidad(int value) {
        cantidad.set(value);
    }

    public IntegerProperty cantidadProperty() {
        return cantidad;
    }

    
    private final ObjectProperty<Product> producto = new SimpleObjectProperty<Product>();

    public Product getProducto() {
        return producto.get();
    }

    public void setProducto(Product value) {
        producto.set(value);
    }

    public ObjectProperty productoProperty() {
        return producto;
    }
    
    public Componente(Product producto,int cant){
        this.producto.setValue(producto);
        this.cantidad.setValue(cant);
    }
    
    //Getters of product attributes
    public Category getCategory(){return this.getProducto().getCategory();}
    public Double getPrice(){return this.getProducto().getPrice();}
    public String getDescrip(){return this.getProducto().getDescription().toUpperCase();}
    public int getStock(){return this.getProducto().getStock();}

    //Changes the quantity of the component to a String parseInt
    public boolean changeQ(String s){
        try{
        int n = Integer.parseInt(s);
            if(n>0){this.setCantidad(n); return true;}
            else{
             Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Negative or zero number");
            alert.showAndWait();
            this.setCantidad(1); return false;
            }
        }catch(NumberFormatException e){             
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("String instead a number");
            alert.showAndWait();
            this.setCantidad(1); return false;
        }
    }
    
}
