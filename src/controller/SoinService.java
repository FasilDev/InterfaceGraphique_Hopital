/*
 * Fichier : SoinService.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Couche service pour les soins médicaux.
 *        Gère deux registres (consultations et actes chirurgicaux) et la file d'urgences.
 *        Quand un acte chirurgical non réalisé est ajouté, il entre automatiquement
 *        dans la file d'urgences pour y être trié par priorité médicale.
 *
 * Interactions : Registre<Consultation>, Registre<ActeChirurgical>, FileUrgences,
 *                Consultation, ActeChirurgical, Soin, SoinServlet, UrgenceServlet,
 *                StatistiqueService
 *
 * Pourquoi la file d'urgences est gérée ici ?
 *   Les urgences sont des ActeChirurgical (sous-classe de Soin), donc il est naturel
 *   que SoinService coordonne les deux. On aurait pu créer un UrgenceService séparé,
 *   mais pour un projet de cette taille, c'est une complexité inutile.
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

    // La PriorityQueue interne à FileUrgences trie automatiquement par niveauPriorite
    private final FileUrgences fileUrgences;

    private SoinService() {
        this.registreConsultations = new Registre<>();
        this.registreActes         = new Registre<>();
        this.fileUrgences          = new FileUrgences();
    }

    public static synchronized SoinService getInstance() {
        if (instance == null) {
            instance = new SoinService();
        }
        return instance;
    }

    // -----------------------------------------------------------------------
    // Consultations — CRUD
    // -----------------------------------------------------------------------

    /**
     * Ajoute une consultation dans le registre.
     */
    public void ajouterConsultation(Consultation consultation) {
        if (consultation == null) {
            throw new DonneeInvalideException("L'objet consultation ne peut pas être null.");
        }
        registreConsultations.ajouter(consultation);
    }

    public boolean supprimerConsultation(String id) {
        return registreConsultations.supprimer(id);
    }

    public void modifierConsultation(Consultation consultation) {
        if (consultation == null) throw new DonneeInvalideException("L'objet consultation ne peut pas être null.");
        registreConsultations.mettreAJour(consultation);
    }

    public Consultation trouverConsultationParId(String id) {
        return registreConsultations.trouverParId(id);
    }

    public List<Consultation> listerConsultations() {
        return registreConsultations.getTous();
    }

    /**
     * Liste toutes les consultations d'un patient donné.
     * Le filtre porte sur le numéro métier (ex : "P-2024-001"), pas sur l'UUID interne.
     */
    public List<Consultation> listerConsultationsParPatient(String numeroPatient) {
        if (numeroPatient == null || numeroPatient.isBlank()) return new ArrayList<>();
        return registreConsultations.filtrer(
                c -> c.getNumeroPatient().equals(numeroPatient)
        );
    }

    // -----------------------------------------------------------------------
    // Actes chirurgicaux — CRUD
    // -----------------------------------------------------------------------

    /**
     * Ajoute un acte chirurgical dans le registre.
     * Si l'acte n'est pas encore réalisé, il est aussi versé dans la file d'urgences
     * où il sera automatiquement trié par niveau de priorité médicale.
     */
    public void ajouterActeChirurgical(ActeChirurgical acte) {
        if (acte == null) {
            throw new DonneeInvalideException("L'objet acte chirurgical ne peut pas être null.");
        }
        registreActes.ajouter(acte);
        // Un acte en attente est une urgence à traiter : on l'ajoute à la file
        if (!acte.isRealise()) {
            fileUrgences.ajouterUrgence(acte);
        }
    }

    /**
     * Supprime un acte et le retire aussi de la file d'urgences si besoin.
     */
    public boolean supprimerActe(String id) {
        fileUrgences.supprimerUrgence(id);
        return registreActes.supprimer(id);
    }

    public void modifierActe(ActeChirurgical acte) {
        if (acte == null) throw new DonneeInvalideException("L'objet acte ne peut pas être null.");
        registreActes.mettreAJour(acte);
    }

    public ActeChirurgical trouverActeParId(String id) {
        return registreActes.trouverParId(id);
    }

    public List<ActeChirurgical> listerActes() {
        return registreActes.getTous();
    }

    /**
     * Liste les actes chirurgicaux d'un patient, triés du plus récent au plus ancien.
     * Comparator.comparing(...).reversed() inverse l'ordre naturel des dates.
     */
    public List<ActeChirurgical> listerActesParPatient(String numeroPatient) {
        if (numeroPatient == null || numeroPatient.isBlank()) return new ArrayList<>();
        return registreActes.getTous().stream()
                .filter(a -> a.getNumeroPatient().equals(numeroPatient))
                .sorted(Comparator.comparing(Soin::getDateSoin).reversed())
                .collect(Collectors.toList());
    }

    // -----------------------------------------------------------------------
    // Urgences — gestion de la file
    // -----------------------------------------------------------------------

    /**
     * Retourne les urgences en attente dans l'ordre de priorité médicale
     * (niveau 1 = critique en premier, niveau 5 = non urgent en dernier).
     * Ne retire aucun élément de la file.
     */
    public List<ActeChirurgical> listerUrgencesEnAttente() {
        return fileUrgences.listerUrgencesTriees();
    }

    /**
     * Traite la prochaine urgence : retire l'acte de la file et le marque comme réalisé.
     * Retourne l'acte traité, ou null si la file est vide.
     */
    public ActeChirurgical traiterProchaineUrgence() {
        ActeChirurgical acte = fileUrgences.traiterProchainUrgence();
        if (acte != null) {
            acte.marquerRealise();
            registreActes.mettreAJour(acte);
        }
        return acte;
    }

    /**
     * Consulte la prochaine urgence sans la retirer de la file.
     * Utile pour afficher "prochaine urgence" sur le tableau de bord sans la consommer.
     */
    public ActeChirurgical voirProchaineUrgence() {
        return fileUrgences.voirProchaineUrgence();
    }

    public int getNombreUrgencesEnAttente() {
        return fileUrgences.getNombreUrgences();
    }

    // -----------------------------------------------------------------------
    // Recherche transversale sur tous les soins
    // -----------------------------------------------------------------------

    /**
     * Retourne tous les soins d'un patient (consultations + actes), du plus récent au plus ancien.
     * Stream.concat() fusionne les deux flux en un seul.
     * On travaille avec le type commun Soin pour le tri par date.
     */
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

    /**
     * Retourne tous les soins effectués par un médecin donné, du plus récent au plus ancien.
     * Utile pour afficher le dossier d'activité d'un médecin.
     */
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

    // -----------------------------------------------------------------------
    // Statistiques
    // -----------------------------------------------------------------------

    public int getNombreConsultations() {
        return registreConsultations.getNombre();
    }

    public int getNombreActes() {
        return registreActes.getNombre();
    }

    /**
     * Compte les actes chirurgicaux déjà réalisés (acte.isRealise() == true).
     */
    public long getNombreActesRealises() {
        return registreActes.compter(ActeChirurgical::isRealise);
    }

    /**
     * Calcule le coût total de l'ensemble des soins (consultations + actes chirurgicaux).
     * Deux streams séparés dont les sommes sont additionnées.
     */
    public double getCoutTotalSoins() {
        double totalConsultations = registreConsultations.getTous().stream()
                .mapToDouble(Soin::getCout)
                .sum();
        double totalActes = registreActes.getTous().stream()
                .mapToDouble(Soin::getCout)
                .sum();
        return totalConsultations + totalActes;
    }

    // Accès aux registres pour la persistance (Commit 8)
    public Registre<Consultation>    getRegistreConsultations() { return registreConsultations; }
    public Registre<ActeChirurgical> getRegistreActes()         { return registreActes;         }
    public FileUrgences              getFileUrgences()           { return fileUrgences;           }
}
