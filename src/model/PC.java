/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


import es.upv.inf.Product.Category;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;



/**
 *
 * @author Victor
 */
@XmlRootElement
public class PC {
    
    private Date date;
    private String name = "";
    private List<Componente> components = new ArrayList<Componente>();

    public PC(String name, List<Componente> components) {
        this.name=name;
        this.components.addAll(components);
        date = new Date();
    }

    public PC(String name) {
    this.name=name;
    date = new Date();
    }
    
    public PC(String name, Date dat){
      this.name=name;
      this.date= dat;
    }

    public PC() {
    }
   

    public List<Componente> getComponents() {
        return components;
    }
    
    public void setComponents(List<Componente> value){
        this.components=value;
    }



    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name=value;
    }
    
    public void addElement(Componente p){
        this.components.add(p);
    }
    
    //Check if we have all the mandatory elements
    // returns "OK" if it's correct
    // else it returns a String showing missed elements
    public String checkMandatoryElements(){
        boolean isMother =false ,isCPU =false, isRAM=false,
                isGraphs=false, isHD=false, isCase = false;
        for(int i=0;i<components.size();i++){
            Componente element= components.get(i);
            Category cat = element.getProducto().getCategory();
            if(cat.equals(Category.MOTHERBOARD)){isMother=true;}
            if(cat.equals(Category.CPU)){isCPU=true;}
            if(cat.equals(Category.RAM)){isRAM=true;}
            if(cat.equals(Category.GPU)){isGraphs=true;}
            if(cat.equals(Category.HDD) || cat.equals(Category.HDD_SSD)){isHD=true;}
            if(cat.equals(Category.CASE)){isCase=true;}
        }
        if(isMother && isCPU && isRAM && isGraphs && isHD && isCase){
            return "OK";
        }
        
        String s="You have to select:" + '\n';
        if(!isMother){s= s + "Mother Board"+'\n';}
        if(!isCPU){s= s + "CPU"+'\n';}
        if(!isRAM){s= s + "Memory RAM"+'\n';}
        if(!isGraphs){s= s + "Graphics card"+'\n';}
        if(!isHD){s= s + "Hard Disk"+'\n';}
        if(!isCase){s= s + "Case" +'\n';}
        return s;
    }
    
    @Override
    public String toString(){return this.name;}
    
    public void deleteElement(int i){this.getComponents().remove(i);}
    
    public void setDate(Date date){
        this.date = date;
    }
    
    public Date getDate(){return this.date;}
    
}


