package ru.mephi.vikingdemo.service;

import org.springframework.stereotype.Service;
import ru.mephi.vikingdemo.model.Viking;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class VikingService {
    private final CopyOnWriteArrayList<Viking> vikings = new CopyOnWriteArrayList<>();
    private final VikingFactory vikingFactory;
    
    public VikingService(VikingFactory vikingFactory) {
        this.vikingFactory = vikingFactory;
    }
    
    public List<Viking> findAll() {
        return List.copyOf(vikings);
    }
    
    public Viking createRandomViking() {
        Viking viking = vikingFactory.createRandomViking();
        vikings.add(viking);
        return viking;
    }
    
    public Viking save(Viking viking) {
        vikings.add(viking);
        System.out.println("Saved viking: " + viking.name());
        return viking;
    }
    
    public boolean deleteByName(String name) {
        boolean removed = vikings.removeIf(viking -> viking.name().equalsIgnoreCase(name));
        if (removed) {
            System.out.println("Deleted viking with name: " + name);
        } else {
            System.out.println("Viking not found with name: " + name);
        }
        return removed;
    }
    
    public Viking findByName(String name) {
        return vikings.stream()
            .filter(viking -> viking.name().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }

    public boolean update(String name, Viking updatedViking) {
        for (int i = 0; i < vikings.size(); i++) {
            if (vikings.get(i).name().equalsIgnoreCase(name)) {
                vikings.set(i, updatedViking);
                System.out.println("Updated viking: " + name + " -> " + updatedViking.name());
                return true;
            }
        }
        return false;
    }

    public boolean partialUpdate(String name, Viking updatedViking) {
        for (int i = 0; i < vikings.size(); i++) {
            Viking existing = vikings.get(i);
            if (existing.name().equalsIgnoreCase(name)) {
                Viking merged = new Viking(
                    updatedViking.name() != null ? updatedViking.name() : existing.name(),
                    updatedViking.age() != 0 ? updatedViking.age() : existing.age(),
                    updatedViking.heightCm() != 0 ? updatedViking.heightCm() : existing.heightCm(),
                    updatedViking.hairColor() != null ? updatedViking.hairColor() : existing.hairColor(),
                    updatedViking.beardStyle() != null ? updatedViking.beardStyle() : existing.beardStyle(),
                    updatedViking.equipment() != null ? updatedViking.equipment() : existing.equipment()
                );
                vikings.set(i, merged);
                System.out.println("Partially updated viking: " + name);
                return true;
            }
        }
        return false;
    }
}