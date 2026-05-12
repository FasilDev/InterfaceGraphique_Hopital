/*
 * SoinService.java - Couche service pour les soins médicaux.
 * Gère les consultations, les actes chirurgicaux et la file d'urgences.
 * Quand un ActeChirurgical non réalisé est ajouté, il entre automatiquement dans la file d'urgences.
 */

package controller;

import model.ActeChirurgical;
import model.Consultation;
import model.DonneeInvalideException;
import model.FileUrgences;
import model.Soin;
import util.Registre;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SoinService {

    private static SoinService instance;

    private final Registre<Consultation>    registreConsultations;
    private final Registre<ActeChirurgical> registreActes;
    private final FileUrgences              fileUrgences;

    private SoinService() {
        this.registreConsultations = new Registre<>();
        this.registreActes         = new Registre<>();
        this.fileUrgences          = new FileUrgences();
    }

    public static synchronized SoinService getInstance() {
        if (instance == null) instance = new SoinService();
        return instance;
    }

    // -------------------------------------------------------------------
    // Consultations — CRUD
    // -------------------------------------------------------------------

    public void ajouterConsultation(Consultation consultation) {
        if (consultation == null) throw new DonneeInvalideException("L'objet consultation ne peut pas être null.");
        registreConsultations.ajouter(consultation);
    }

    public boolean supprimerConsultation(String id)         { return registreConsultations.supprimer(id); }

    public void modifierConsultation(Consultation consultation) {
        if (consultation == null) throw new DonneeInvalideException("L'objet consultation ne peut pas être null.");
        registreConsultations.mettreAJour(consultation);
    }

    public Consultation trouverConsultationParId(String id) { return registreConsultations.trouverParId(id); }

    public List<Consultation> listerConsultations()         { return registreConsultations.getTous(); }

    public List<Consultation> listerConsultationsParPatient(String numeroPatient) {
        if (numeroPatient == null || numeroPatient.isBlank()) return new ArrayList<>();
        return registreConsultations.filtrer(c -> c.getNumeroPatient().equals(numeroPatient));
    }

    // -------------------------------------------------------------------
    // Actes chirurgicaux — CRUD
    // -------------------------------------------------------------------

    // Un acte non réalisé est aussi versé dans la file d'urgences (tri automatique par priorité)
    public void ajouterActeChirurgical(ActeChirurgical acte) {
        if (acte == null) throw new DonneeInvalideException("L'objet acte chirurgical ne peut pas être null.");
        registreActes.ajouter(acte);
        if (!acte.isRealise()) {
            fileUrgences.ajouterUrgence(acte);
        }
    }

    public boolean supprimerActe(String id) {
        fileUrgences.supprimerUrgence(id);
        return registreActes.supprimer(id);
    }

    public void modifierActe(ActeChirurgical acte) {
        if (acte == null) throw new DonneeInvalideException("L'objet acte ne peut pas être null.");
        registreActes.mettreAJour(acte);
    }

    public ActeChirurgical trouverActeParId(String id)  { return registreActes.trouverParId(id); }

    public List<ActeChirurgical> listerActes()          { return registreActes.getTous(); }

    public List<ActeChirurgical> listerActesParPatient(String numeroPatient) {
        if (numeroPatient == null || numeroPatient.isBlank()) return new ArrayList<>();
        return registreActes.getTous().stream()
                .filter(a -> a.getNumeroPatient().equals(numeroPatient))
                .sorted(Comparator.comparing(Soin::getDateSoin).reversed())
                .collect(Collectors.toList());
    }

    // Recherche par patient et/ou médecin, avec tri par date croissant ou décroissant
    public List<Consultation> rechercherConsultations(String numeroPatient, String matriculeMedecin,
                                                      boolean croissant) {
        Comparator<Soin> parDate = Comparator.comparing(Soin::getDateSoin);
        if (!croissant) parDate = parDate.reversed();
        return registreConsultations.getTous().stream()
                .filter(c -> numeroPatient == null || numeroPatient.isBlank()
                        || c.getNumeroPatient().equals(numeroPatient))
                .filter(c -> matriculeMedecin == null || matriculeMedecin.isBlank()
                        || c.getMatriculeMedecin().equals(matriculeMedecin))
                .sorted(parDate)
                .collect(Collectors.toList());
    }

    public List<ActeChirurgical> rechercherActes(String numeroPatient, String matriculeMedecin,
                                                 boolean croissant) {
        Comparator<Soin> parDate = Comparator.comparing(Soin::getDateSoin);
        if (!croissant) parDate = parDate.reversed();
        return registreActes.getTous().stream()
                .filter(a -> numeroPatient == null || numeroPatient.isBlank()
                        || a.getNumeroPatient().equals(numeroPatient))
                .filter(a -> matriculeMedecin == null || matriculeMedecin.isBlank()
                        || a.getMatriculeMedecin().equals(matriculeMedecin))
                .sorted(parDate)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------
    // Urgences
    // -------------------------------------------------------------------

    public List<ActeChirurgical> listerUrgencesEnAttente() { return fileUrgences.listerUrgencesTriees(); }

    // Retire l'acte le plus urgent de la file et le marque comme réalisé
    public ActeChirurgical traiterProchaineUrgence() {
        ActeChirurgical acte = fileUrgences.traiterProchainUrgence();
        if (acte != null) {
            acte.marquerRealise();
            registreActes.mettreAJour(acte);
        }
        return acte;
    }

    public ActeChirurgical voirProchaineUrgence()  { return fileUrgences.voirProchaineUrgence(); }
    public int getNombreUrgencesEnAttente()         { return fileUrgences.getNombreUrgences(); }

    // -------------------------------------------------------------------
    // Recherche transversale
    // -------------------------------------------------------------------

    // Stream.concat() fusionne les deux streams (consultations + actes) en un seul
    public List<Soin> listerSoinsParPatient(String numeroPatient) {
        if (numeroPatient == null || numeroPatient.isBlank()) return new ArrayList<>();
        return Stream.concat(
                registreConsultations.getTous().stream(),
                registreActes.getTous().stream()
        )
                .filter(s -> s.getNumeroPatient().equals(numeroPatient))
                .sorted(Comparator.comparing(Soin::getDateSoin).reversed())
                .collect(Collectors.toList());
    }

    public List<Soin> listerSoinsParMedecin(String matriculeMedecin) {
        if (matriculeMedecin == null || matriculeMedecin.isBlank()) return new ArrayList<>();
        return Stream.concat(
                registreConsultations.getTous().stream(),
                registreActes.getTous().stream()
        )
                .filter(s -> s.getMatriculeMedecin().equals(matriculeMedecin))
                .sorted(Comparator.comparing(Soin::getDateSoin).reversed())
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------
    // Statistiques
    // -------------------------------------------------------------------

    public int getNombreConsultations()  { return registreConsultations.getNombre(); }
    public int getNombreActes()          { return registreActes.getNombre(); }

    public long getNombreActesRealises() {
        return registreActes.compter(ActeChirurgical::isRealise);
    }

    public double getCoutTotalSoins() {
        double totalConsultations = registreConsultations.getTous().stream().mapToDouble(Soin::getCout).sum();
        double totalActes         = registreActes.getTous().stream().mapToDouble(Soin::getCout).sum();
        return totalConsultations + totalActes;
    }

    public Registre<Consultation>    getRegistreConsultations() { return registreConsultations; }
    public Registre<ActeChirurgical> getRegistreActes()         { return registreActes;         }
    public FileUrgences              getFileUrgences()           { return fileUrgences;           }
}
