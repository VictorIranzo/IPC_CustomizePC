/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Víctor
 */
@XmlRootElement
public class ListPCWrapper {
    
    private List<PC> pcList;
    
    public ListPCWrapper() { pcList = new ArrayList<PC>(); }
    
    @XmlElement(name = "PC")
    
   
    public List<PC> getPCList() {
        return pcList;
    }
    
    public void setPCList(List<PC> list) {
        pcList = list;
    }
    
    public boolean addtoWrapper(PC aPC){
    return pcList.add(aPC);
    }
}

