/*
 * PatientService.java - Couche service pour les patients (toute la logique métier).
 * Les servlets appellent ce service ; ils ne font aucun traitement eux-mêmes.
 *
 * Pattern Singleton : une seule instance partagée dans toute l'application,
 * ce qui garantit que tous les composants travaillent sur les mêmes données en mémoire.
 * synchronized évite de créer deux instances si plusieurs requêtes arrivent simultanément.
 */

package controller;

import model.DonneeInvalideException;
import model.EntiteIntrouvableException;
import model.Patient;
import util.Registre;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PatientService {

    private static PatientService instance;
    private final Registre<Patient> registre;

    private PatientService() {
        this.registre = new Registre<>();
    }

    public static synchronized PatientService getInstance() {
        if (instance == null) instance = new PatientService();
        return instance;
    }

    // -------------------------------------------------------------------
    // CRUD
    // -------------------------------------------------------------------

    public void ajouter(Patient patient) {
        if (patient == null) throw new DonneeInvalideException("L'objet patient ne peut pas être null.");
        registre.ajouter(patient);
    }

    public boolean supprimer(String id) { return registre.supprimer(id); }

    public void modifier(Patient patient) {
        if (patient == null) throw new DonneeInvalideException("L'objet patient ne peut pas être null.");
        registre.mettreAJour(patient);
    }

    public Patient trouverParId(String id) { return registre.trouverParId(id); }

    // Recherche par numéro métier lisible (ex : "P-2024-001") plutôt que par UUID interne
    public Patient trouverParNumero(String numeroPatient) {
        if (numeroPatient == null || numeroPatient.isBlank()) return null;
        return registre.getTous().stream()
                .filter(p -> p.getNumeroPatient().equals(numeroPatient))
                .findFirst()
                .orElse(null);
    }

    public List<Patient> listerTous() { return registre.getTous(); }

    // -------------------------------------------------------------------
    // Actions métier
    // -------------------------------------------------------------------

    public void admettre(String id, String chambre) {
        Patient patient = trouverParId(id);
        if (patient == null) throw new EntiteIntrouvableException("Patient", id);
        if (chambre == null || chambre.isBlank())
            throw new DonneeInvalideException("chambre", "le numéro de chambre est obligatoire.");
        patient.admettre(chambre);
    }

    public void sortir(String id) {
        Patient patient = trouverParId(id);
        if (patient == null) throw new EntiteIntrouvableException("Patient", id);
        patient.sortir();
    }

    // -------------------------------------------------------------------
    // Recherche multicritères avec Streams
    // -------------------------------------------------------------------

    // Chaque critère est optionnel : null ou vide = ignoré
    public List<Patient> rechercherMulticriteres(String nom, Boolean admis, String groupeSanguin) {
        return registre.getTous().stream()
                .filter(p -> nom == null || nom.isBlank()
                        || p.getNom().toLowerCase().contains(nom.toLowerCase()))
                .filter(p -> admis == null || p.isAdmis() == admis)
                .filter(p -> groupeSanguin == null || groupeSanguin.isBlank()
                        || groupeSanguin.equalsIgnoreCase(p.getGroupeSanguin()))
                .collect(Collectors.toList());
    }

    // Filtre puis trie en une seule passe ; triColonne : "nom" ou "date"
    public List<Patient> rechercherEtTrier(String nom, Boolean admis, String groupeSanguin,
                                           String triColonne, boolean croissant) {
        List<Patient> resultats = rechercherMulticriteres(nom, admis, groupeSanguin);
        if ("date".equals(triColonne)) {
            // nullsLast : patients sans date d'admission placés en dernier
            Comparator<Patient> comp = Comparator.comparing(
                    Patient::getDateAdmission, Comparator.nullsLast(Comparator.naturalOrder()));
            resultats.sort(croissant ? comp : comp.reversed());
        } else if ("nom".equals(triColonne)) {
            Comparator<Patient> comp = Comparator.comparing(Patient::getNom, String.CASE_INSENSITIVE_ORDER);
            resultats.sort(croissant ? comp : comp.reversed());
        }
        return resultats;
    }

    public List<Patient> listerPatientsAdmis() { return registre.filtrer(Patient::isAdmis); }

    // -------------------------------------------------------------------
    // Tri dynamique
    // -------------------------------------------------------------------

    public List<Patient> trierParNom(boolean croissant) {
        Comparator<Patient> parNom = Comparator.comparing(Patient::getNom, String.CASE_INSENSITIVE_ORDER);
        return registre.trierPar(croissant ? parNom : parNom.reversed());
    }

    // nullsLast : patients non encore admis (sans date) placés en dernier
    public List<Patient> trierParDateAdmission(boolean croissant) {
        Comparator<Patient> parDate = Comparator.comparing(
                Patient::getDateAdmission, Comparator.nullsLast(Comparator.naturalOrder()));
        return registre.trierPar(croissant ? parDate : parDate.reversed());
    }

    // -------------------------------------------------------------------
    // Statistiques
    // -------------------------------------------------------------------

    public int getNombre()          { return registre.getNombre(); }
    public long getNombreAdmis()    { return registre.compter(Patient::isAdmis); }
    public long getNombreNonAdmis() { return registre.compter(p -> !p.isAdmis()); }

    // mapToDouble transforme chaque patient en son montant, sum() les additionne
    public double getChiffreAffairesPatients() {
        return registre.getTous().stream()
                .mapToDouble(Patient::calculerMontantTotal)
                .sum();
    }

    public Registre<Patient> getRegistre() { return registre; }
}
