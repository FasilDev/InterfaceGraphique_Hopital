/*
 * Fichier : PersonnelService.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Couche service pour le personnel hospitalier (médecins et infirmiers).
 *        Gère deux registres distincts et fournit des méthodes de recherche,
 *        de tri et de statistiques sur l'ensemble du personnel.
 *
 * Interactions : Registre<Medecin>, Registre<Infirmier>, Medecin, Infirmier, Personnel,
 *                PersonnelServlet, StatistiqueService
 *
 * Pourquoi deux registres séparés plutôt qu'un seul Registre<Personnel> ?
 *   Avec un registre unique, récupérer la spécialité d'un médecin demanderait
 *   un cast systématique : (Medecin) personnel.trouverParId(id).
 *   Deux registres distincts évitent les casts et rendent le code plus lisible.
 *   La contrepartie : les méthodes globales (listerTout, rechercherParNom) doivent
 *   fusionner les deux flux via Stream.concat().
 */

package controller;

import model.Infirmier;
import model.Medecin;
import model.Personnel;
import util.Registre;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PersonnelService {

    private static PersonnelService instance;

    private final Registre<Medecin>   registreMedecins;
    private final Registre<Infirmier> registreInfirmiers;

    private PersonnelService() {
        this.registreMedecins   = new Registre<>();
        this.registreInfirmiers = new Registre<>();
    }

    public static synchronized PersonnelService getInstance() {
        if (instance == null) {
            instance = new PersonnelService();
        }
        return instance;
    }

    // -----------------------------------------------------------------------
    // Médecins — CRUD
    // -----------------------------------------------------------------------

    /**
     * Ajoute un médecin dans le registre.
     * TODO Commit 7 : remplacer IllegalArgumentException par DonneeInvalideException
     */
    public void ajouterMedecin(Medecin medecin) {
        if (medecin == null) throw new IllegalArgumentException("Médecin invalide.");
        registreMedecins.ajouter(medecin);
    }

    public boolean supprimerMedecin(String id) {
        return registreMedecins.supprimer(id);
    }

    public void modifierMedecin(Medecin medecin) {
        if (medecin == null) throw new IllegalArgumentException("Médecin invalide.");
        registreMedecins.mettreAJour(medecin);
    }

    public Medecin trouverMedecinParId(String id) {
        return registreMedecins.trouverParId(id);
    }

    /**
     * Recherche un médecin par son matricule (ex : "MED-001").
     * Utile quand on reçoit un matricule depuis un formulaire web plutôt que l'UUID interne.
     */
    public Medecin trouverMedecinParMatricule(String matricule) {
        if (matricule == null || matricule.isBlank()) return null;
        return registreMedecins.getTous().stream()
                .filter(m -> m.getMatricule().equals(matricule))
                .findFirst()
                .orElse(null);
    }

    public List<Medecin> listerMedecins() {
        return registreMedecins.getTous();
    }

    // -----------------------------------------------------------------------
    // Infirmiers — CRUD
    // -----------------------------------------------------------------------

    /**
     * Ajoute un infirmier dans le registre.
     * TODO Commit 7 : remplacer IllegalArgumentException par DonneeInvalideException
     */
    public void ajouterInfirmier(Infirmier infirmier) {
        if (infirmier == null) throw new IllegalArgumentException("Infirmier invalide.");
        registreInfirmiers.ajouter(infirmier);
    }

    public boolean supprimerInfirmier(String id) {
        return registreInfirmiers.supprimer(id);
    }

    public void modifierInfirmier(Infirmier infirmier) {
        if (infirmier == null) throw new IllegalArgumentException("Infirmier invalide.");
        registreInfirmiers.mettreAJour(infirmier);
    }

    public Infirmier trouverInfirmierParId(String id) {
        return registreInfirmiers.trouverParId(id);
    }

    public List<Infirmier> listerInfirmiers() {
        return registreInfirmiers.getTous();
    }

    // -----------------------------------------------------------------------
    // Personnel global — méthodes communes aux deux types
    // -----------------------------------------------------------------------

    /**
     * Retourne tout le personnel (médecins + infirmiers) dans une seule liste.
     * Stream.concat() fusionne deux streams en un seul sans créer de liste intermédiaire.
     * On travaille avec le type commun Personnel pour éviter les casts.
     */
    public List<Personnel> listerToutLePersonnel() {
        return Stream.concat(
                registreMedecins.getTous().stream(),
                registreInfirmiers.getTous().stream()
        ).collect(Collectors.toList());
    }

    /**
     * Recherche dans tout le personnel par nom ou prénom (insensible à la casse).
     * Si le nom est vide ou null, retourne tout le personnel.
     */
    public List<Personnel> rechercherParNom(String nom) {
        if (nom == null || nom.isBlank()) return listerToutLePersonnel();
        String recherche = nom.toLowerCase();
        return listerToutLePersonnel().stream()
                .filter(p -> p.getNom().toLowerCase().contains(recherche)
                        || p.getPrenom().toLowerCase().contains(recherche))
                .collect(Collectors.toList());
    }

    /**
     * Recherche les médecins d'une spécialité donnée (insensible à la casse).
     * Si la spécialité est vide, retourne tous les médecins.
     */
    public List<Medecin> rechercherMedecinsParSpecialite(String specialite) {
        if (specialite == null || specialite.isBlank()) return listerMedecins();
        return registreMedecins.filtrer(
                m -> m.getSpecialite().toLowerCase().contains(specialite.toLowerCase())
        );
    }

    /**
     * Trie tout le personnel par nom de famille.
     * listerToutLePersonnel() retourne une ArrayList modifiable,
     * donc on peut appeler sort() directement dessus.
     *
     * @param croissant true = A→Z, false = Z→A
     */
    public List<Personnel> trierParNom(boolean croissant) {
        Comparator<Personnel> parNom = Comparator.comparing(
                Personnel::getNom, String.CASE_INSENSITIVE_ORDER
        );
        List<Personnel> tous = listerToutLePersonnel();
        tous.sort(croissant ? parNom : parNom.reversed());
        return tous;
    }

    /**
     * Retourne les médecins disponibles (non affectés à une tâche bloquante).
     * On utilise isDisponible() de Personnel qui lit directement le champ boolean.
     */
    public List<Medecin> listerMedecinsDisponibles() {
        return registreMedecins.filtrer(Personnel::isDisponible);
    }

    // -----------------------------------------------------------------------
    // Statistiques
    // -----------------------------------------------------------------------

    public int getNombreMedecins() {
        return registreMedecins.getNombre();
    }

    public int getNombreInfirmiers() {
        return registreInfirmiers.getNombre();
    }

    public int getNombreTotalPersonnel() {
        return registreMedecins.getNombre() + registreInfirmiers.getNombre();
    }

    /**
     * Retourne le nombre de médecins par spécialité.
     * Collectors.groupingBy() regroupe les éléments par un critère (ici la spécialité).
     * Collectors.counting() compte le nombre d'éléments dans chaque groupe.
     *
     * Résultat : {"Cardiologie" -> 3, "Neurologie" -> 2, ...}
     */
    public Map<String, Long> getRepartitionParSpecialite() {
        return registreMedecins.getTous().stream()
                .collect(Collectors.groupingBy(Medecin::getSpecialite, Collectors.counting()));
    }

    /**
     * Retourne la liste des spécialités présentes dans l'hôpital, sans doublons.
     * distinct() élimine les doublons, sorted() trie alphabétiquement.
     * Utile pour remplir les listes déroulantes dans les formulaires.
     */
    public List<String> getSpecialitesDisponibles() {
        return registreMedecins.getTous().stream()
                .map(Medecin::getSpecialite)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // Accès aux registres pour la persistance (Commit 8)
    public Registre<Medecin>   getRegistreMedecins()   { return registreMedecins;   }
    public Registre<Infirmier> getRegistreInfirmiers() { return registreInfirmiers; }
}
