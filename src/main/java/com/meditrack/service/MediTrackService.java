package com.meditrack.service;

import com.meditrack.model.Medicine;
import com.meditrack.model.Warehouse;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class MediTrackService {
    
    private final List<Medicine> medicines = new ArrayList<>();
    private final List<Warehouse> warehouses = new ArrayList<>();
    
    public MediTrackService() {
        System.out.println("🎉 MediTrack Service Started! Your medicine app is coming to life!");
        
        // Initialize medicines
        medicines.add(new Medicine("med1", "Artemether/Lumefantrine (20/120mg)", 2500, "2025-12-31", 
                                 "wh1", "Shelf A1", "Antimalarial", 500, 3.50));
        medicines.add(new Medicine("med2", "Azithromycin 500mg", 90, "2024-10-31", 
                                 "wh2", "Rack B2", "Antibiotic", 150, 0.75));
        medicines.add(new Medicine("med3", "Tenofovir/Lamivudine/Dolutegravir (TLD)", 800, "2026-06-30", 
                                 "wh1", "Shelf A2", "Antiretroviral", 200, 7.20));
        
        // Initialize warehouses
        warehouses.add(new Warehouse("wh1", "Nairobi Central Store", "Nairobi, Kenya", -1.2921, 36.8219));
        warehouses.add(new Warehouse("wh2", "Mogadishu MedHub", "Mogadishu, Somalia", 2.0469, 45.3182));
        warehouses.add(new Warehouse("wh3", "Juba Distribution Point", "Juba, South Sudan", 4.8594, 31.5713));
    }
    
    public String getMedicineCount() {
        return "📊 We have " + medicines.size() + " medicines in the system!";
    }
    
    public List<Medicine> getAllMedicines() {
        return medicines;
    }
    
    public List<Warehouse> getAllWarehouses() {
        return warehouses;
    }
}