package main.medicine;

public class WarehouseDispatcher {

    public void dispatchMedicine(String warehouseName, Medicine med, int qty) throws Exception {
        try {
            if (warehouseName == null) throw new NullPointerException("Warehouse not specified");
            if (med == null) throw new NullPointerException("Medicine not specified");
            if (qty <= 0) throw new IllegalArgumentException("Invalid quantity");

            med.updateQuantity(-qty);
            System.out.println("Dispatched " + qty + " units of " + med);

        } catch (Exception e) {
            throw new Exception("Dispatch operation failed", e);
        }
    }
}
