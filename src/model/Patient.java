/*
 * Fichier : Patient.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Représente un patient de l'hôpital.
 *        Gère son dossier médical : admission, antécédents, chambre et sortie.
 *        C'est l'entité centrale autour de laquelle tourne l'application.
 *
 * Interactions : Personne (parent), PatientService, PatientServlet,
 *                JSP liste-patients.jsp, detail-patient.jsp, formulaire-patient.jsp
 *
 * TODO Commit 3 : ajouter "implements Soignable, Facturable" sur la déclaration de classe
 */

package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Patient extends Personne {

    // Numéro lisible unique dans l'hôpital (ex : "P-2024-001").
    // Différent de l'UUID : c'est l'identifiant métier utilisé par le personnel.
    private String numeroPatient;

    // Groupe sanguin (ex : "A+", "O-", "AB+"). Stocké en String, peu de valeurs possibles.
    private String groupeSanguin;

    // Liste des antécédents médicaux (ex : "Diabète type 2", "Allergie pénicilline").
    // List<String> = liste ordonnée, on garde l'ordre de saisie.
    // ArrayList est l'implémentation standard : efficace pour lire et ajouter en fin de liste.
    // Pas de raw type "List" sans <String> — interdit dans ce projet.
    private List<String> antecedents;

    // Date d'hospitalisation. null si le patient n'a jamais été admis.
    private LocalDate dateAdmission;

    // Date de sortie. null si le patient est encore hospitalisé.
    private LocalDate dateSortie;

    // true = actuellement hospitalisé, false = sorti ou jamais admis
    private boolean admis;

    // Notes libres du dossier médical.
    // Dans une vraie application, ce serait une entité séparée avec historique.
    private String notes;

    // Chambre occupée (ex : "B203"). null si le patient n'est pas hospitalisé.
    private String chambre;


    // Constructeur pour créer un nouveau patient.

    public Patient(String nom, String prenom, LocalDate dateNaissance, String numeroPatient) {
        super(nom, prenom, dateNaissance); // appelle Personne → Entite (génère UUID)
        this.numeroPatient = numeroPatient;
        // On initialise toujours la liste pour éviter les NullPointerException
        this.antecedents = new ArrayList<>();
        this.admis = false;
        this.notes = "";
    }

    /**
     * Constructeur avec id existant, utilisé lors du rechargement depuis le fichier CSV.
     * On passe l'UUID déjà connu pour ne pas en recréer un nouveau.
     */
    public Patient(String id, String nom, String prenom, LocalDate dateNaissance, String numeroPatient) {
        super(id, nom, prenom, dateNaissance);
        this.numeroPatient = numeroPatient;
        this.antecedents = new ArrayList<>();
        this.admis = false;
        this.notes = "";
    }

    // -----------------------------------------------------------------------
    // Méthodes métier
    // -----------------------------------------------------------------------

    /**
     * Admet le patient dans l'hôpital et l'affecte à une chambre.
     * Enregistre automatiquement la date du jour comme date d'admission.
     *
     * @param chambre Numéro de chambre (ex : "A101")
     */
    public void admettre(String chambre) {
        this.admis = true;
        this.dateAdmission = LocalDate.now();
        this.chambre = chambre;
        // On efface une éventuelle date de sortie précédente (réadmission possible)
        this.dateSortie = null;
    }

    /**
     * Fait sortir le patient de l'hôpital.
     * Libère la chambre et enregistre la date de sortie.
     */
    public void sortir() {
        this.admis = false;
        this.dateSortie = LocalDate.now();
        // La chambre est libérée — null = disponible pour un autre patient
        this.chambre = null;
    }

    /**
     * Ajoute un antécédent médical au dossier du patient.
     * Ignore les valeurs null ou vides pour ne pas polluer la liste.
     *
     * @param antecedent Description de l'antécédent (ex : "Hypertension")
     */
    public void ajouterAntecedent(String antecedent) {
        // isBlank() retourne true si la chaîne est vide ou ne contient que des espaces
        if (antecedent != null && !antecedent.isBlank()) {
            this.antecedents.add(antecedent);
        }
    }

    /**
     * Retourne les antécédents sous forme d'une chaîne lisible.
     * Exemple : "Diabète type 2, Allergie pénicilline, Hypertension"
     * Utilisé dans les JSP pour l'affichage en tableau.
     */
    public String getAntecedentsFormates() {
        if (antecedents == null || antecedents.isEmpty()) {
            return "Aucun antécédent connu";
        }
        // String.join(séparateur, collection) construit "A, B, C" à partir d'une liste
        return String.join(", ", antecedents);
    }

    /**
     * Retourne l'état d'admission sous forme de texte lisible pour l'affichage.
     */
    public String getStatutAdmission() {
        return admis ? "Hospitalisé" : "Non hospitalisé";
    }

    @Override
    public String toString() {
        return "Patient[" + numeroPatient + "] " + getNomComplet()
                + " (" + getAge() + " ans) - " + getStatutAdmission();
    }



    public String getNumeroPatient() { return numeroPatient; }
    public void setNumeroPatient(String numeroPatient) { this.numeroPatient = numeroPatient; }

    public String getGroupeSanguin() { return groupeSanguin; }
    public void setGroupeSanguin(String groupeSanguin) { this.groupeSanguin = groupeSanguin; }

    public List<String> getAntecedents() { return antecedents; }
    public void setAntecedents(List<String> antecedents) { this.antecedents = antecedents; }

    public LocalDate getDateAdmission() { return dateAdmission; }
    public void setDateAdmission(LocalDate dateAdmission) { this.dateAdmission = dateAdmission; }

    public LocalDate getDateSortie() { return dateSortie; }
    public void setDateSortie(LocalDate dateSortie) { this.dateSortie = dateSortie; }

    public boolean isAdmis() { return admis; }
    public void setAdmis(boolean admis) { this.admis = admis; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getChambre() { return chambre; }
    public void setChambre(String chambre) { this.chambre = chambre; }
}