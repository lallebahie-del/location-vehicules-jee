package storage;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import model.Invoice;
import java.time.LocalDate;

public class InvoiceStorage {
    
    private static final Map<Long, Invoice> invoices = new HashMap<>();
    private static long idCounter = 1L;
    
    // Initialisation avec des factures de test
    static {
        // Facture payée
        Invoice i1 = new Invoice(idCounter++, 2L, 2L, 250.0, 0.0);
        i1.setStatutPaiement("PAYEE");
        i1.setDatePaiement(LocalDate.now().minusDays(2));
        i1.setModePaiement("CARTE");
        invoices.put(i1.getId(), i1);
        
        // Facture en attente (sans pénalités)
        Invoice i2 = new Invoice(idCounter++, 1L, 2L, 180.0, 0.0);
        invoices.put(i2.getId(), i2);
        
        // Facture avec pénalités
        Invoice i3 = new Invoice(idCounter++, 3L, 3L, 325.0, 120.0);
        i3.setDetailsPenalites("Retard de 2 jours: 120.0€ (20%/heure)");
        invoices.put(i3.getId(), i3);
        
        // Facture en retard
        Invoice i4 = new Invoice(idCounter++, 4L, 2L, 450.0, 0.0);
        i4.setStatutPaiement("EN_RETARD");
        i4.setDateEmission(LocalDate.now().minusDays(15));
        invoices.put(i4.getId(), i4);
    }
    
    // Méthodes CRUD
    public static Map<Long, Invoice> getAllInvoices() {
        return new HashMap<>(invoices);
    }
    
    public static Invoice getInvoiceById(Long id) {
        return invoices.get(id);
    }
    
    public static Invoice getInvoiceByContractId(Long contractId) {
        for (Invoice i : invoices.values()) {
            if (contractId.equals(i.getContractId())) {
                return i;
            }
        }
        return null;
    }
    
    public static void addInvoice(Invoice invoice) {
        if (invoice.getId() == null) {
            invoice.setId(idCounter++);
        }
        invoices.put(invoice.getId(), invoice);
    }
    
    public static void updateInvoice(Invoice invoice) {
        invoices.put(invoice.getId(), invoice);
    }
    
    public static void deleteInvoice(Long id) {
        invoices.remove(id);
    }
    
    public static List<Invoice> getInvoicesByClient(Long clientId) {
        List<Invoice> result = new ArrayList<>();
        for (Invoice i : invoices.values()) {
            if (clientId.equals(i.getClientId())) {
                result.add(i);
            }
        }
        return result;
    }
    
    public static List<Invoice> getPendingInvoices() {
        List<Invoice> result = new ArrayList<>();
        for (Invoice i : invoices.values()) {
            if ("EN_ATTENTE".equals(i.getStatutPaiement())) {
                result.add(i);
            }
        }
        return result;
    }
    
    public static List<Invoice> getOverdueInvoices() {
        List<Invoice> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (Invoice i : invoices.values()) {
            if (("EN_ATTENTE".equals(i.getStatutPaiement()) || "EN_RETARD".equals(i.getStatutPaiement())) &&
                i.getDateEmission() != null && 
                i.getDateEmission().plusDays(30).isBefore(today)) {
                result.add(i);
            }
        }
        return result;
    }
    
    public static double getTotalRevenue() {
        double total = 0.0;
        for (Invoice i : invoices.values()) {
            if ("PAYEE".equals(i.getStatutPaiement())) {
                total += i.getMontantTotal();
            }
        }
        return total;
    }
    
    public static double getPendingRevenue() {
        double total = 0.0;
        for (Invoice i : invoices.values()) {
            if ("EN_ATTENTE".equals(i.getStatutPaiement())) {
                total += i.getMontantTotal();
            }
        }
        return total;
    }
    
    public static long getNextId() {
        return idCounter;
    }
}