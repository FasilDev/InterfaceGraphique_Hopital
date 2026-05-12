/*
 * Fichier : Personnel.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Classe abstraite représentant tout membre du personnel hospitalier.
 *        Médecin et Infirmier héritent de cette classe.
 *        Elle regroupe ce qui est commun aux deux : matricule, service, date d'embauche.
 *
 * Interactions : Personne (parent), Medecin (sous-classe), Infirmier (sous-classe),
 *                PersonnelService, PersonnelServlet
 *
 * Pourquoi abstract ? On ne recrute jamais un "Personnel" en général.
 * On recrute un Médecin ou un Infirmier. Cette classe n'est jamais instanciée directement.
 *
 * TODO Commit 3 : Planifiable sera implémentée dans Medecin et Infirmier
 */

package model;

import java.time.LocalDate;

public abstract class Personnel extends Personne {

    // Numéro de matricule unique dans l'établissement (ex : "MED-001", "INF-042")
    private String matricule;

    // Service dans lequel travaille ce membre du personnel (ex : "Cardiologie", "Urgences")
    private String service;

    // Date d'embauche dans l'établissement
    private LocalDate dateEmbauche;

    // Indique si ce membre du personnel est actuellement disponible
    // (pas en congé, pas déjà affecté à une tâche bloquante)
    private boolean disponible;
    
    //Constructeur pour créer un nouveau membre du personnel.

    public Personnel(String nom, String prenom, LocalDate dateNaissance,
                     String matricule, String service) {
        super(nom, prenom, dateNaissance);
        this.matricule = matricule;
        this.service = service;
        // À l'embauche, la date est enregistrée automatiquement
        this.dateEmbauche = LocalDate.now();
        // Disponible par défaut à l'embauche
        this.disponible = true;
    }

    /**
     * Constructeur avec id existant, pour le rechargement depuis un fichier CSV.
     */
    public Personnel(String id, String nom, String prenom, LocalDate dateNaissance,
                     String matricule, String service) {
        super(id, nom, prenom, dateNaissance);
        this.matricule = matricule;
        this.service = service;
        this.dateEmbauche = LocalDate.now();
        this.disponible = true;
    }


    /**
     * Retourne le type de poste occupé.
     * Chaque sous-classe définit son propre rôle : "Médecin", "Infirmier", etc.
     * C'est une méthode abstraite : Personnel ne sait pas quel rôle il a — ses sous-classes si.
     *
     * @return Le libellé du rôle (ex : "Médecin")
     */
    public abstract String getRole();


    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public LocalDate getDateEmbauche() { return dateEmbauche; }
    public void setDateEmbauche(LocalDate dateEmbauche) { this.dateEmbauche = dateEmbauche; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}