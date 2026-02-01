package service;

import model.Invoice;
import model.RentalContract;
import storage.InvoiceStorage;
import storage.ContractStorage;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class InvoiceService {
    
    // Récupérer toutes les factures
    public static Map<Long, Invoice> getAllInvoices() {
        return InvoiceStorage.getAllInvoices();
    }
    
    // Récupérer les factures d'un client
    public static List<Invoice> getClientInvoices(Long clientId) {
        return InvoiceStorage.getInvoicesByClient(clientId);
    }
    
    // Récupérer une facture par ID
    public static Invoice getInvoiceById(Long invoiceId) {
        return InvoiceStorage.getInvoiceById(invoiceId);
    }
    
    // Récupérer la facture associée à un contrat
    public static Invoice getInvoiceByContractId(Long contractId) {
        return InvoiceStorage.getInvoiceByContractId(contractId);
    }
    
    // Payer une facture
    public static boolean payInvoice(Long invoiceId, String modePaiement) {
        Invoice invoice = InvoiceStorage.getInvoiceById(invoiceId);
        if (invoice == null || !"EN_ATTENTE".equals(invoice.getStatutPaiement())) {
            return false;
        }
        
        invoice.setStatutPaiement("PAYEE");
        invoice.setModePaiement(modePaiement);
        invoice.setDatePaiement(LocalDate.now());
        
        InvoiceStorage.updateInvoice(invoice);
        return true;
    }
    
    // Générer une facture pour un contrat existant
    public static Invoice generateInvoiceForContract(Long contractId) {
        RentalContract contract = ContractStorage.getContractById(contractId);
        if (contract == null) {
            throw new IllegalArgumentException("Contrat introuvable");
        }
        
        // Vérifier si une facture existe déjà
        Invoice existing = InvoiceStorage.getInvoiceByContractId(contractId);
        if (existing != null) {
            return existing;
        }
        
        // Récupérer le montant de location depuis la réservation
        // (Dans un cas réel, on aurait une méthode pour calculer cela)
        // Pour l'exemple, on utilise les pénalités du contrat
        
        Invoice invoice = new Invoice();
        invoice.setContractId(contractId);
        invoice.setClientId(contract.getClientId());
        invoice.setMontantLocation(calculateRentalAmount(contract));
        invoice.setMontantPenalites(contract.getPenalites());
        invoice.setDetailsPenalites("Pénalités du contrat #" + contractId);
        
        InvoiceStorage.addInvoice(invoice);
        return invoice;
    }
    
    // Calculer le montant de location pour un contrat
    private static double calculateRentalAmount(RentalContract contract) {
        // Dans un cas réel, on irait chercher la réservation
        // Pour l'exemple, on calcule un montant basé sur la durée
        long days = ChronoUnit.DAYS.between(contract.getDateDebut(), contract.getDateFinPrevue());
        return days * 50.0; // Tarif moyen de 50€/jour
    }
    
    // Vérifier les factures en retard (non payées après 30 jours)
    public static List<Invoice> checkOverdueInvoices() {
        List<Invoice> overdue = InvoiceStorage.getOverdueInvoices();
        
        // Mettre à jour le statut si nécessaire
        for (Invoice invoice : overdue) {
            if ("EN_ATTENTE".equals(invoice.getStatutPaiement())) {
                invoice.setStatutPaiement("EN_RETARD");
                InvoiceStorage.updateInvoice(invoice);
            }
        }
        
        return overdue;
    }
    
    // Appliquer des intérêts de retard (2% par mois)
    public static void applyLateFees() {
        List<Invoice> overdue = checkOverdueInvoices();
        LocalDate today = LocalDate.now();
        
        for (Invoice invoice : overdue) {
            if (invoice.getDateEmission() == null) continue;
            
            long monthsLate = ChronoUnit.MONTHS.between(invoice.getDateEmission(), today);
            if (monthsLate > 0) {
                double lateFee = invoice.getMontantTotal() * 0.02 * monthsLate;
                invoice.setMontantPenalites(invoice.getMontantPenalites() + lateFee);
                invoice.setMontantTotal(invoice.getMontantTotal() + lateFee);
                
                String details = invoice.getDetailsPenalites();
                if (details == null) details = "";
                details += " | Intérêts de retard (" + monthsLate + " mois): +" + lateFee + "€";
                invoice.setDetailsPenalites(details);
                
                InvoiceStorage.updateInvoice(invoice);
            }
        }
    }
    
    // Calculer les statistiques financières
    public static FinancialStats calculateFinancialStats() {
        FinancialStats stats = new FinancialStats();
        
        Map<Long, Invoice> allInvoices = InvoiceStorage.getAllInvoices();
        
        for (Invoice invoice : allInvoices.values()) {
            stats.totalInvoices++;
            
            if ("PAYEE".equals(invoice.getStatutPaiement())) {
                stats.paidAmount += invoice.getMontantTotal();
                stats.paidInvoices++;
            } else if ("EN_ATTENTE".equals(invoice.getStatutPaiement())) {
                stats.pendingAmount += invoice.getMontantTotal();
                stats.pendingInvoices++;
            } else if ("EN_RETARD".equals(invoice.getStatutPaiement())) {
                stats.overdueAmount += invoice.getMontantTotal();
                stats.overdueInvoices++;
            }
            
            stats.totalPenalties += invoice.getMontantPenalites();
        }
        
        return stats;
    }
    
    // Classe interne pour les statistiques
    public static class FinancialStats {
        public int totalInvoices = 0;
        public int paidInvoices = 0;
        public int pendingInvoices = 0;
        public int overdueInvoices = 0;
        public double paidAmount = 0.0;
        public double pendingAmount = 0.0;
        public double overdueAmount = 0.0;
        public double totalPenalties = 0.0;
        
        @Override
        public String toString() {
            return String.format(
                "Statistiques: %d factures (Payées: %d, En attente: %d, En retard: %d)%n" +
                "Montants: Payés=%.2f€, En attente=%.2f€, En retard=%.2f€, Pénalités=%.2f€",
                totalInvoices, paidInvoices, pendingInvoices, overdueInvoices,
                paidAmount, pendingAmount, overdueAmount, totalPenalties
            );
        }
    }
    
    // Générer un rapport financier
    public static String generateFinancialReport() {
        FinancialStats stats = calculateFinancialStats();
        return stats.toString();
    }
}