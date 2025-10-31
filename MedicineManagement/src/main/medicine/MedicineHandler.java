package main.medicine;

public class MedicineHandler {

    public void updateMedicineQuantity(Medicine med, int amount) {
        try {
            if (med == null) throw new NullPointerException("Medicine cannot be null");
            if (amount == 0) throw new IllegalArgumentException("Amount cannot be zero");

            med.updateQuantity(amount);
            System.out.println("Quantity updated successfully.");

        } catch (NullPointerException | IllegalArgumentException e) {
            System.err.println("Error updating medicine: " + e.getMessage());
        } finally {
            System.out.println("Operation complete.");
        }
    }
}
