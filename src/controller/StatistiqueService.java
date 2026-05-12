/*
 * StatistiqueService.java - Agrège les données de tous les services pour les indicateurs du tableau de bord.
 * Ce service n'a pas de registre propre ; il délègue aux trois autres services (SRP).
 *
 * Concepts Stream utilisés :
 *   groupingBy() + counting() -> répartition en Map
 *   mapToInt() + average()    -> moyenne (retourne un OptionalDouble)
 *   mapToDouble() + sum()     -> somme de valeurs numériques
 *   DoubleSummaryStatistics   -> résumé complet (min, max, sum, avg, count) en un seul passage
 */

package controller;

import model.Patient;
import model.Soin;

import java.time.LocalDate;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class StatistiqueService {

    private static StatistiqueService instance;

    private final PatientService   patientService;
    private final PersonnelService personnelService;
    private final SoinService      soinService;

    private StatistiqueService() {
        // On récupère les singletons existants pour travailler sur les mêmes données
        this.patientService   = PatientService.getInstance();
        this.personnelService = PersonnelService.getInstance();
        this.soinService      = SoinService.getInstance();
    }

    public static synchronized StatistiqueService getInstance() {
        if (instance == null) instance = new StatistiqueService();
        return instance;
    }

    // -------------------------------------------------------------------
    // Statistiques patients
    // -------------------------------------------------------------------

    public int  getNombreTotalPatients()    { return patientService.getNombre(); }
    public long getNombrePatientsAdmis()    { return patientService.getNombreAdmis(); }
    public long getNombrePatientsNonAdmis() { return patientService.getNombreNonAdmis(); }

    // Retourne 0.0 si capaciteTotale <= 0 pour éviter une division par zéro
    public double getTauxOccupation(int capaciteTotale) {
        if (capaciteTotale <= 0) return 0.0;
        return (patientService.getNombreAdmis() * 100.0) / capaciteTotale;
    }

    // average() retourne un OptionalDouble ; orElse(0.0) donne 0 si aucun patient admis
    public double getAgeMoyenPatientsAdmis() {
        return patientService.listerPatientsAdmis().stream()
                .mapToInt(Patient::getAge)
                .average()
                .orElse(0.0);
    }

    // -------------------------------------------------------------------
    // Statistiques personnel
    // -------------------------------------------------------------------

    public int getNombreMedecins()       { return personnelService.getNombreMedecins(); }
    public int getNombreInfirmiers()     { return personnelService.getNombreInfirmiers(); }
    public int getNombreTotalPersonnel() { return personnelService.getNombreTotalPersonnel(); }

    // LinkedHashMap préserve l'ordre alphabétique des spécialités après tri
    public Map<String, Long> getRepartitionParSpecialite() {
        return personnelService.getRepartitionParSpecialite()
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    // -------------------------------------------------------------------
    // Statistiques soins
    // -------------------------------------------------------------------

    public int  getNombreConsultations()    { return soinService.getNombreConsultations(); }
    public int  getNombreActesChirurgicaux(){ return soinService.getNombreActes(); }
    public int  getNombreUrgencesEnAttente(){ return soinService.getNombreUrgencesEnAttente(); }
    public long getNombreActesRealises()    { return soinService.getNombreActesRealises(); }

    public long getNombreConsultationsAujourdhui() {
        LocalDate aujourdhui = LocalDate.now();
        return soinService.listerConsultations().stream()
                .filter(c -> c.getDateSoin().equals(aujourdhui))
                .count();
    }

    // -------------------------------------------------------------------
    // Statistiques financières
    // -------------------------------------------------------------------

    public double getChiffreAffairesTotal() {
        return patientService.getChiffreAffairesPatients() + soinService.getCoutTotalSoins();
    }

    // summaryStatistics() calcule count/sum/min/max/avg en un seul passage sur le stream
    public DoubleSummaryStatistics getStatistiquesFinancieresSoins() {
        return soinService.listerConsultations().stream()
                .mapToDouble(Soin::getCout)
                .summaryStatistics();
    }
}
