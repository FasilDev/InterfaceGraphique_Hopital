/*
 * Fichier : StatistiqueService.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Agrège les données de tous les services pour produire les indicateurs
 *        affichés sur le tableau de bord et la page statistiques.
 *        Ce service n'a pas de registre propre — il interroge les trois autres services.
 *
 * Interactions : PatientService, PersonnelService, SoinService,
 *                StatistiqueServlet, JSP statistiques.jsp
 *
 * Principe de séparation des responsabilités (SRP) :
 *   PatientService gère les données des patients.
 *   StatistiqueService se charge uniquement des calculs et agrégations.
 *   Chaque classe a une seule raison de changer — c'est plus facile à maintenir.
 *
 * Concepts Java utilisés (importants pour la soutenance) :
 *   - groupingBy() + counting()    : regroupement et comptage dans une Map
 *   - mapToDouble() + sum()        : somme de valeurs numériques
 *   - mapToInt() + average()       : moyenne, retourne un OptionalDouble
 *   - filter() + count()           : comptage conditionnel
 *   - DoubleSummaryStatistics      : résumé statistique complet (min, max, sum, avg, count)
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

    // Ce service délègue aux autres plutôt que de dupliquer les données en mémoire
    private final PatientService    patientService;
    private final PersonnelService  personnelService;
    private final SoinService       soinService;

    private StatistiqueService() {
        // On récupère les singletons existants — on travaille sur les mêmes données que partout
        this.patientService   = PatientService.getInstance();
        this.personnelService = PersonnelService.getInstance();
        this.soinService      = SoinService.getInstance();
    }

    public static synchronized StatistiqueService getInstance() {
        if (instance == null) {
            instance = new StatistiqueService();
        }
        return instance;
    }

    // -----------------------------------------------------------------------
    // Statistiques patients
    // -----------------------------------------------------------------------

    public int getNombreTotalPatients() {
        return patientService.getNombre();
    }

    public long getNombrePatientsAdmis() {
        return patientService.getNombreAdmis();
    }

    public long getNombrePatientsNonAdmis() {
        return patientService.getNombreNonAdmis();
    }

    /**
     * Calcule le taux d'occupation des lits en pourcentage.
     * Retourne 0.0 si la capacité vaut 0 — évite une division par zéro.
     *
     * @param capaciteTotale Nombre total de lits dans l'établissement
     * @return Taux entre 0.0 et 100.0
     */
    public double getTauxOccupation(int capaciteTotale) {
        if (capaciteTotale <= 0) return 0.0;
        long admis = patientService.getNombreAdmis();
        return (admis * 100.0) / capaciteTotale;
    }

    /**
     * Calcule l'âge moyen des patients actuellement hospitalisés.
     * mapToInt() convertit chaque Patient en son âge (calculé via Period.between).
     * average() retourne un OptionalDouble : orElse(0.0) donne 0 si la liste est vide.
     */
    public double getAgeMoyenPatientsAdmis() {
        return patientService.listerPatientsAdmis().stream()
                .mapToInt(Patient::getAge)
                .average()
                .orElse(0.0);
    }

    // -----------------------------------------------------------------------
    // Statistiques personnel
    // -----------------------------------------------------------------------

    public int getNombreMedecins() {
        return personnelService.getNombreMedecins();
    }

    public int getNombreInfirmiers() {
        return personnelService.getNombreInfirmiers();
    }

    public int getNombreTotalPersonnel() {
        return personnelService.getNombreTotalPersonnel();
    }

    /**
     * Retourne le nombre de médecins par spécialité, trié alphabétiquement.
     * On passe par un LinkedHashMap pour garantir que l'ordre alphabétique est conservé
     * dans la Map finale — un HashMap ordinaire ne garantit pas l'ordre des clés.
     *
     * Exemple : {"Cardiologie" -> 3, "Neurologie" -> 2, "Urgences" -> 5}
     */
    public Map<String, Long> getRepartitionParSpecialite() {
        return personnelService.getRepartitionParSpecialite()
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,           // résolution de doublon (ne peut pas arriver ici)
                        LinkedHashMap::new     // préserve l'ordre de tri dans la Map résultante
                ));
    }

    // -----------------------------------------------------------------------
    // Statistiques soins
    // -----------------------------------------------------------------------

    public int getNombreConsultations() {
        return soinService.getNombreConsultations();
    }

    public int getNombreActesChirurgicaux() {
        return soinService.getNombreActes();
    }

    public int getNombreUrgencesEnAttente() {
        return soinService.getNombreUrgencesEnAttente();
    }

    public long getNombreActesRealises() {
        return soinService.getNombreActesRealises();
    }

    /**
     * Compte les consultations réalisées aujourd'hui.
     * LocalDate.now() donne la date du jour ; equals() compare uniquement la date, pas l'heure.
     */
    public long getNombreConsultationsAujourdhui() {
        LocalDate aujourdhui = LocalDate.now();
        return soinService.listerConsultations().stream()
                .filter(c -> c.getDateSoin().equals(aujourdhui))
                .count();
    }

    // -----------------------------------------------------------------------
    // Statistiques financières
    // -----------------------------------------------------------------------

    /**
     * Calcule le chiffre d'affaires total de l'hôpital :
     * séjours des patients + coût de l'ensemble des soins.
     */
    public double getChiffreAffairesTotal() {
        return patientService.getChiffreAffairesPatients()
                + soinService.getCoutTotalSoins();
    }

    /**
     * Retourne un résumé statistique des coûts de soins en un seul passage sur le Stream.
     * DoubleSummaryStatistics regroupe : count, sum, min, max et average.
     * Utile pour afficher plusieurs indicateurs sans recalculer plusieurs fois.
     *
     * Accès : stats.getSum(), stats.getAverage(), stats.getMin(), stats.getMax()
     */
    public DoubleSummaryStatistics getStatistiquesFinancieresSoins() {
        return soinService.listerConsultations().stream()
                .mapToDouble(Soin::getCout)
                .summaryStatistics();
    }
}
