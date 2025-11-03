package com.meditrack.controller;

import com.meditrack.service.MediTrackService;
import com.meditrack.model.Medicine;
import com.meditrack.model.Warehouse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TestController {
    
    @Autowired
    private MediTrackService mediTrackService;
    
    @GetMapping("/test")
    public String test() {
        return mediTrackService.getMedicineCount();
    }
    
    @GetMapping("/")
    public String home() {
        return "🏥 MediTrack Java Backend is RUNNING! 💊";
    }
    
    @GetMapping("/medicines")
    public List<Medicine> getAllMedicines() {
        return mediTrackService.getAllMedicines();
    }
    
    @GetMapping("/warehouses")
    public List<Warehouse> getAllWarehouses() {
        return mediTrackService.getAllWarehouses();
    }
    
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMedicines", mediTrackService.getAllMedicines().size());
        stats.put("totalWarehouses", mediTrackService.getAllWarehouses().size());
        stats.put("message", "📊 Medicine tracking system is working!");
        return stats;
    }
}