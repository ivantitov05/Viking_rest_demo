package ru.mephi.vikingdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.mephi.vikingdemo.gui.VikingDesktopFrame;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingService;

@Component
public class VikingListener {
    private VikingService service;
    private VikingDesktopFrame gui;

    @Autowired
    public VikingListener(VikingService service) {
        this.service = service;
    }
    
    public void setGui(VikingDesktopFrame gui){
        this.gui = gui;
    }

    void testAdd() {
        gui.addNewViking(service.createRandomViking());
    }
    
    public void addViking(Viking viking) {
        if (gui != null) {
            gui.addNewViking(viking);
        } else {
            System.out.println("GUI is null, cannot add viking: " + viking.name());
        }
    }

    public void deleteViking(String name) {
        if (gui != null) {
            gui.deleteViking(name);
        } else {
            System.out.println("GUI is null, cannot delete viking: " + name);
        }
    }

    public void updateViking(String oldName, Viking updatedViking) {
        if (gui != null) {
            gui.updateViking(oldName, updatedViking);
        } else {
            System.out.println("GUI is null, cannot update viking: " + oldName);
        }
    }
}