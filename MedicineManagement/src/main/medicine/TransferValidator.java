package main.medicine;

import java.util.List;

public class TransferValidator {

    public void validateTransfer(List<Medicine> medicines) throws Exception {
        try {
            if (medicines == null) throw new NullPointerException("Transfer list cannot be null");
            if (medicines.isEmpty()) throw new IllegalArgumentException("Transfer has no medicines");
        } catch (Exception e) {
            System.err.println("Validation failed: " + e.getMessage());
            throw e; // rethrow for higher-level handling
        }
    }
}
