package service;

import model.Invoice;
import model.RentalContract;
import model.Reservation;
import model.User;
import model.Vehicle;
import storage.ContractStorage;
import storage.InvoiceStorage;
import storage.ReservationStorage;
import storage.UserStorage;
import storage.VehicleStorage;

import java.util.*;

/**
 * Service de génération des rapports pour l'administrateur.
 * 
 * Rapports disponibles :
 * - Taux d'occupation de la flotte
 * - Revenus par agence
 * - Revenus par catégorie de véhicule
 * - Véhicules en maintenance
 * - Clients les plus fidèles
 */
public class ReportService {

    // ============================================================
    // 1. TAUX D'OCCUPATION DE LA FLOTTE
    // ============================================================

    /**
     * Retourne le taux d'occupation global et par agence.
     * Taux = véhicules EN_LOCATION / total véhicules × 100
     */
    public static Map<String, Object> getFleetOccupancyReport() {
        Map<Long, Vehicle> all = VehicleStorage.getAllVehicles();

        int total = all.size();
        int enLocation = 0;
        int enMaintenance = 0;
        int disponible = 0;
        int accidente = 0;

        // Par agence : <agence, [total, enLocation]>
        Map<String, int[]> byAgency = new LinkedHashMap<>();

        for (Vehicle v : all.values()) {
            String agence = v.getAgence() != null ? v.getAgence() : "Inconnue";
            byAgency.putIfAbsent(agence, new int[] { 0, 0, 0 }); // [total, enLocation, maintenance]
            byAgency.get(agence)[0]++;

            switch (v.getStatut()) {
                case "EN_LOCATION":
                    enLocation++;
                    byAgency.get(agence)[1]++;
                    break;
                case "EN_MAINTENANCE":
                    enMaintenance++;
                    byAgency.get(agence)[2]++;
                    break;
                case "ACCIDENTE":
                    accidente++;
                    break;
                default:
                    disponible++;
                    break;
            }
        }

        double tauxGlobal = (total == 0) ? 0.0 : (enLocation * 100.0 / total);

        // Construire le détail par agence
        List<Map<String, Object>> agencyDetails = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : byAgency.entrySet()) {
            int[] counts = entry.getValue();
            Map<String, Object> agencyStats = new LinkedHashMap<>();
            agencyStats.put("agence", entry.getKey());
            agencyStats.put("totalVehicules", counts[0]);
            agencyStats.put("enLocation", counts[1]);
            agencyStats.put("enMaintenance", counts[2]);
            agencyStats.put("tauxOccupation", counts[0] == 0 ? 0.0 : (counts[1] * 100.0 / counts[0]));
            agencyDetails.add(agencyStats);
        }

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("totalVehicules", total);
        report.put("vehiculesDisponibles", disponible);
        report.put("vehiculesEnLocation", enLocation);
        report.put("vehiculesEnMaintenance", enMaintenance);
        report.put("vehiculesAccidentes", accidente);
        report.put("tauxOccupationGlobal", Math.round(tauxGlobal * 100.0) / 100.0);
        report.put("detailParAgence", agencyDetails);

        return report;
    }

    // ============================================================
    // 2. REVENUS PAR AGENCE
    // ============================================================

    /**
     * Calcule les revenus totaux générés par chaque agence
     * en croisant les contrats → véhicules → agence.
     */
    public static Map<String, Object> getRevenueByAgency() {
        Map<String, Double> revenueMap = new LinkedHashMap<>();

        for (Invoice invoice : InvoiceStorage.getAllInvoices().values()) {
            // Retrouver le contrat pour obtenir le véhicule
            RentalContract contract = ContractStorage.getContractById(invoice.getContractId());
            if (contract == null)
                continue;

            Vehicle vehicle = VehicleStorage.getVehicleById(contract.getVehicleId());
            String agence = (vehicle != null && vehicle.getAgence() != null)
                    ? vehicle.getAgence()
                    : "Inconnue";

            revenueMap.merge(agence, invoice.getMontantTotal(), Double::sum);
        }

        // Construire liste triée par revenus décroissants
        List<Map<String, Object>> details = new ArrayList<>();
        revenueMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(e -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("agence", e.getKey());
                    item.put("revenuTotal", Math.round(e.getValue() * 100.0) / 100.0);
                    details.add(item);
                });

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("revenusParAgence", details);
        report.put("totalGlobal", revenueMap.values().stream().mapToDouble(Double::doubleValue).sum());
        return report;
    }

    // ============================================================
    // 3. REVENUS PAR CATÉGORIE DE VÉHICULE
    // ============================================================

    public static Map<String, Object> getRevenueByCategory() {
        Map<String, Double> revenueMap = new LinkedHashMap<>();

        for (Invoice invoice : InvoiceStorage.getAllInvoices().values()) {
            RentalContract contract = ContractStorage.getContractById(invoice.getContractId());
            if (contract == null)
                continue;

            Vehicle vehicle = VehicleStorage.getVehicleById(contract.getVehicleId());
            String categorie = (vehicle != null && vehicle.getCategorie() != null)
                    ? vehicle.getCategorie()
                    : "Inconnue";

            revenueMap.merge(categorie, invoice.getMontantTotal(), Double::sum);
        }

        List<Map<String, Object>> details = new ArrayList<>();
        revenueMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(e -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("categorie", e.getKey());
                    item.put("revenuTotal", Math.round(e.getValue() * 100.0) / 100.0);
                    details.add(item);
                });

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("revenusParCategorie", details);
        report.put("totalGlobal", revenueMap.values().stream().mapToDouble(Double::doubleValue).sum());
        return report;
    }

    // ============================================================
    // 4. VÉHICULES EN MAINTENANCE
    // ============================================================

    public static List<Map<String, Object>> getMaintenanceReport() {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Vehicle v : VehicleStorage.getAllVehicles().values()) {
            if ("EN_MAINTENANCE".equals(v.getStatut()) || "ACCIDENTE".equals(v.getStatut())) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", v.getId());
                item.put("marque", v.getMarque());
                item.put("modele", v.getModele());
                item.put("immatriculation", v.getImmatriculation());
                item.put("statut", v.getStatut());
                item.put("agence", v.getAgence());
                result.add(item);
            }
        }
        return result;
    }

    // ============================================================
    // 5. CLIENTS LES PLUS FIDÈLES
    // ============================================================

    /**
     * Classement des clients par nombre de contrats clôturés.
     */
    public static List<Map<String, Object>> getTopClientsReport() {
        // Compter les contrats par client
        Map<Long, Long> contractCount = new HashMap<>();
        Map<Long, Double> totalSpent = new HashMap<>();

        for (RentalContract contract : ContractStorage.getAllContracts().values()) {
            if ("CLOTURE".equals(contract.getStatut())) {
                Long clientId = contract.getClientId();
                contractCount.merge(clientId, 1L, Long::sum);

                // Montant dépensé = facture associée
                Invoice invoice = InvoiceStorage.getInvoiceByContractId(contract.getId());
                double montant = (invoice != null) ? invoice.getMontantTotal() : 0.0;
                totalSpent.merge(clientId, montant, Double::sum);
            }
        }

        // Trier par nombre de contrats (puis montant si égalité)
        List<Map<String, Object>> ranking = new ArrayList<>();
        contractCount.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .forEach(e -> {
                    Long clientId = e.getKey();
                    User user = UserStorage.getUserById(clientId);

                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("clientId", clientId);
                    item.put("username", user != null ? user.getUsername() : "Inconnu");
                    if (user instanceof model.Client) {
                        model.Client c = (model.Client) user;
                        item.put("nom", c.getNom());
                        item.put("prenom", c.getPrenom());
                        item.put("email", c.getEmail());
                    }
                    item.put("nombreLocations", e.getValue());
                    item.put("totalDepense", Math.round(totalSpent.getOrDefault(clientId, 0.0) * 100.0) / 100.0);
                    ranking.add(item);
                });

        return ranking;
    }
}
