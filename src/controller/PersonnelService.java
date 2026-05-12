/*
 * PersonnelService.java - Couche service pour le personnel hospitalier (médecins et infirmiers).
 * Deux registres distincts pour éviter les casts constants et rendre le code lisible.
 * Les méthodes globales (listerTout, rechercherParNom) fusionnent les deux via Stream.concat().
 */

package controller;

import model.DonneeInvalideException;
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
        if (instance == null) instance = new PersonnelService();
        return instance;
    }

    // -------------------------------------------------------------------
    // Médecins — CRUD
    // -------------------------------------------------------------------

    public void ajouterMedecin(Medecin medecin) {
        if (medecin == null) throw new DonneeInvalideException("L'objet médecin ne peut pas être null.");
        registreMedecins.ajouter(medecin);
    }

    public boolean supprimerMedecin(String id)  { return registreMedecins.supprimer(id); }

    public void modifierMedecin(Medecin medecin) {
        if (medecin == null) throw new DonneeInvalideException("L'objet médecin ne peut pas être null.");
        registreMedecins.mettreAJour(medecin);
    }

    public Medecin trouverMedecinParId(String id) { return registreMedecins.trouverParId(id); }

    // Recherche par matricule (ex : "MED-001") quand le formulaire envoie le matricule plutôt que l'UUID
    public Medecin trouverMedecinParMatricule(String matricule) {
        if (matricule == null || matricule.isBlank()) return null;
        return registreMedecins.getTous().stream()
                .filter(m -> m.getMatricule().equals(matricule))
                .findFirst()
                .orElse(null);
    }

    public List<Medecin> listerMedecins() { return registreMedecins.getTous(); }

    // Recherche par nom/prénom et spécialité (critères optionnels), triée par nom
    public List<Medecin> rechercherMedecins(String nom, String specialite, boolean croissant) {
        List<Medecin> resultats = registreMedecins.getTous().stream()
                .filter(m -> nom == null || nom.isBlank()
                        || m.getNom().toLowerCase().contains(nom.toLowerCase())
                        || m.getPrenom().toLowerCase().contains(nom.toLowerCase()))
                .filter(m -> specialite == null || specialite.isBlank()
                        || m.getSpecialite().toLowerCase().contains(specialite.toLowerCase()))
                .collect(Collectors.toList());
        Comparator<Medecin> comp = Comparator.comparing(Medecin::getNom, String.CASE_INSENSITIVE_ORDER);
        resultats.sort(croissant ? comp : comp.reversed());
        return resultats;
    }

    // -------------------------------------------------------------------
    // Infirmiers — CRUD
    // -------------------------------------------------------------------

    public void ajouterInfirmier(Infirmier infirmier) {
        if (infirmier == null) throw new DonneeInvalideException("L'objet infirmier ne peut pas être null.");
        registreInfirmiers.ajouter(infirmier);
    }

    public boolean supprimerInfirmier(String id) { return registreInfirmiers.supprimer(id); }

    public void modifierInfirmier(Infirmier infirmier) {
        if (infirmier == null) throw new DonneeInvalideException("L'objet infirmier ne peut pas être null.");
        registreInfirmiers.mettreAJour(infirmier);
    }

    public Infirmier trouverInfirmierParId(String id) { return registreInfirmiers.trouverParId(id); }

    public List<Infirmier> listerInfirmiers() { return registreInfirmiers.getTous(); }

    public List<Infirmier> rechercherInfirmiers(String nom, String service, boolean croissant) {
        List<Infirmier> resultats = registreInfirmiers.getTous().stream()
                .filter(i -> nom == null || nom.isBlank()
                        || i.getNom().toLowerCase().contains(nom.toLowerCase())
                        || i.getPrenom().toLowerCase().contains(nom.toLowerCase()))
                .filter(i -> service == null || service.isBlank()
                        || i.getService().toLowerCase().contains(service.toLowerCase()))
                .collect(Collectors.toList());
        Comparator<Infirmier> comp = Comparator.comparing(Infirmier::getNom, String.CASE_INSENSITIVE_ORDER);
        resultats.sort(croissant ? comp : comp.reversed());
        return resultats;
    }

    // -------------------------------------------------------------------
    // Personnel global
    // -------------------------------------------------------------------

    // Stream.concat() fusionne les deux streams sans créer de liste intermédiaire
    public List<Personnel> listerToutLePersonnel() {
        return Stream.concat(
                registreMedecins.getTous().stream(),
                registreInfirmiers.getTous().stream()
        ).collect(Collectors.toList());
    }

    public List<Personnel> rechercherParNom(String nom) {
        if (nom == null || nom.isBlank()) return listerToutLePersonnel();
        String recherche = nom.toLowerCase();
        return listerToutLePersonnel().stream()
                .filter(p -> p.getNom().toLowerCase().contains(recherche)
                        || p.getPrenom().toLowerCase().contains(recherche))
                .collect(Collectors.toList());
    }

    public List<Medecin> rechercherMedecinsParSpecialite(String specialite) {
        if (specialite == null || specialite.isBlank()) return listerMedecins();
        return registreMedecins.filtrer(
                m -> m.getSpecialite().toLowerCase().contains(specialite.toLowerCase()));
    }

    public List<Personnel> trierParNom(boolean croissant) {
        Comparator<Personnel> parNom = Comparator.comparing(Personnel::getNom, String.CASE_INSENSITIVE_ORDER);
        List<Personnel> tous = listerToutLePersonnel();
        tous.sort(croissant ? parNom : parNom.reversed());
        return tous;
    }

    public List<Medecin> listerMedecinsDisponibles() {
        return registreMedecins.filtrer(Personnel::isDisponible);
    }

    // -------------------------------------------------------------------
    // Statistiques
    // -------------------------------------------------------------------

    public int getNombreMedecins()       { return registreMedecins.getNombre(); }
    public int getNombreInfirmiers()     { return registreInfirmiers.getNombre(); }
    public int getNombreTotalPersonnel() { return registreMedecins.getNombre() + registreInfirmiers.getNombre(); }

    // groupingBy regroupe par spécialité, counting() compte dans chaque groupe
    public Map<String, Long> getRepartitionParSpecialite() {
        return registreMedecins.getTous().stream()
                .collect(Collectors.groupingBy(Medecin::getSpecialite, Collectors.counting()));
    }

    // Liste de spécialités sans doublons, triées alphabétiquement (pour les menus déroulants)
    public List<String> getSpecialitesDisponibles() {
        return registreMedecins.getTous().stream()
                .map(Medecin::getSpecialite)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public Registre<Medecin>   getRegistreMedecins()   { return registreMedecins;   }
    public Registre<Infirmier> getRegistreInfirmiers() { return registreInfirmiers; }
}
