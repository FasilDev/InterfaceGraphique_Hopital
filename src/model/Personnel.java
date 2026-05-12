/*
 * Personnel.java - Classe abstraite représentant tout membre du personnel hospitalier.
 * Medecin et Infirmier héritent de cette classe.
 * abstract car on ne recrute jamais un "Personnel" générique, toujours un Médecin ou Infirmier.
 */

package model;

import java.time.LocalDate;

public abstract class Personnel extends Personne {

    private String matricule;
    private String service;
    private LocalDate dateEmbauche;
    private boolean disponible;

    public Personnel(String nom, String prenom, LocalDate dateNaissance,
                     String matricule, String service) {
        super(nom, prenom, dateNaissance);
        this.matricule = matricule;
        this.service = service;
        this.dateEmbauche = LocalDate.now();
        this.disponible = true;
    }

    // Constructeur de rechargement CSV
    public Personnel(String id, String nom, String prenom, LocalDate dateNaissance,
                     String matricule, String service) {
        super(id, nom, prenom, dateNaissance);
        this.matricule = matricule;
        this.service = service;
        this.dateEmbauche = LocalDate.now();
        this.disponible = true;
    }

    // Chaque sous-classe définit son propre rôle ("Médecin", "Infirmier")
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
