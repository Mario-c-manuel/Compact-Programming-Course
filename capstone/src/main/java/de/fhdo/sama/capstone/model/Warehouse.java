package de.fhdo.sama.capstone.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Warehouse {
    private final String id;
    private final String name;
    private final Location location;
    private final List<Medicine> medicines = new ArrayList<>();

    public Warehouse(String id, String name, Location location) {
        this.id = id; this.name = name; this.location = location;
    }

    // copy-constructor helper
    public void addMedicine(Medicine m) { medicines.add(m); }
    public List<Medicine> getMedicines() { return medicines; }
    public String getId() { return id; }
    public String getName() { return name; }
    public Location getLocation() { return location; }

    public boolean removeMedicineByName(String name, int qty) {
        Optional<Medicine> opt = medicines.stream().filter(m -> m.getName().equals(name)).findFirst();
        if (opt.isEmpty()) return false;
        Medicine m = opt.get();
        if (m.getQuantity() < qty) return false;
        m.setQuantity(m.getQuantity() - qty);
        return true;
    }

    public String toDisplayString() {
        int total = medicines.stream().mapToInt(Medicine::getQuantity).sum();
        return name + " (" + total + " items)";
    }
}
