package storage;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import model.RentalContract;
import java.time.LocalDate;

public class ContractStorage {

    private static final Map<Long, RentalContract> contracts = new HashMap<>();
    private static long idCounter = 1L;

    // Initialisation avec des contrats de test
    static {
        // Contrat actif
        RentalContract c1 = new RentalContract(idCounter++, 1L, 2L, 1L, 4L,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(4));
        contracts.put(c1.getId(), c1);

        // Contrat clôturé (retour à temps)
        RentalContract c2 = new RentalContract(idCounter++, 3L, 2L, 3L, 4L,
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(5));
        c2.setStatut("CLOTURE");
        c2.setDateFinReelle(LocalDate.now().minusDays(5));
        c2.setEtatDepart("BON_ETAT");
        c2.setEtatRetour("BON_ETAT");
        contracts.put(c2.getId(), c2);

        // Contrat en retard (pénalités à appliquer)
        RentalContract c3 = new RentalContract(idCounter++, 2L, 3L, 2L, 4L,
                LocalDate.now().minusDays(7),
                LocalDate.now().minusDays(2));
        c3.setStatut("EN_RETARD");
        c3.setPenalites(120.0); // 2 jours de retard à 20%/heure
        contracts.put(c3.getId(), c3);
    }

    // Méthodes CRUD
    public static Map<Long, RentalContract> getAllContracts() {
        return new HashMap<>(contracts);
    }

    public static RentalContract getContractById(Long id) {
        return contracts.get(id);
    }

    public static RentalContract getContractByReservationId(Long reservationId) {
        for (RentalContract c : contracts.values()) {
            if (reservationId.equals(c.getReservationId())) {
                return c;
            }
        }
        return null;
    }

    public static void addContract(RentalContract contract) {
        if (contract.getId() == null) {
            contract.setId(idCounter++);
        }
        contracts.put(contract.getId(), contract);
    }

    public static void updateContract(RentalContract contract) {
        contracts.put(contract.getId(), contract);
    }

    public static void deleteContract(Long id) {
        contracts.remove(id);
    }

    public static List<RentalContract> getContractsByClient(Long clientId) {
        List<RentalContract> result = new ArrayList<>();
        for (RentalContract c : contracts.values()) {
            if (clientId.equals(c.getClientId())) {
                result.add(c);
            }
        }
        return result;
    }

    public static List<RentalContract> getActiveContracts() {
        List<RentalContract> result = new ArrayList<>();
        for (RentalContract c : contracts.values()) {
            if ("ACTIF".equals(c.getStatut())) {
                result.add(c);
            }
        }
        return result;
    }

    public static List<RentalContract> getOverdueContracts() {
        List<RentalContract> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (RentalContract c : contracts.values()) {
            if ("ACTIF".equals(c.getStatut()) &&
                    c.getDateFinPrevue() != null &&
                    c.getDateFinPrevue().isBefore(today)) {
                result.add(c);
            }
        }
        return result;
    }

    public static List<RentalContract> getContractsByManager(Long managerId) {
        List<RentalContract> result = new ArrayList<>();
        for (RentalContract c : contracts.values()) {
            if (managerId.equals(c.getManagerId())) {
                result.add(c);
            }
        }
        return result;
    }

    public static boolean hasActiveContractForVehicle(Long vehicleId) {
        for (RentalContract c : contracts.values()) {
            if (vehicleId.equals(c.getVehicleId()) && "ACTIF".equals(c.getStatut())) {
                return true;
            }
        }
        return false;
    }

    public static long getNextId() {
        return idCounter;
    }

    public static List<RentalContract> getContractsByVehicle(Long vehicleId) {
        List<RentalContract> result = new ArrayList<>();
        for (RentalContract c : contracts.values()) {
            if (vehicleId.equals(c.getVehicleId())) {
                result.add(c);
            }
        }
        return result;
    }
}