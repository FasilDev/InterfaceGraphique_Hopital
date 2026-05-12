/*
 * Fichier : PatientService.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Couche service pour les patients. C'est ici que se trouve toute la logique
 *        métier liée aux patients : CRUD, admission, recherche multicritères, tri.
 *        Les servlets appellent ce service et ne font aucun traitement eux-mêmes.
 *
 * Interactions : Registre<Patient>, Patient, PatientServlet, CsvService (Commit 8)
 *
 * Architecture MVC :
 *   - PatientServlet (contrôleur web) appelle PatientService
 *   - PatientService (logique métier) manipule Registre<Patient>
 *   - JSP (vue) affiche les données transmises par le servlet via request.setAttribute()
 *
 * Pattern Singleton : une seule instance de PatientService dans toute l'appli.
 *   Cela garantit que toutes les parties du code travaillent sur les mêmes données en mémoire.
 *   getInstance() crée l'instance si elle n'existe pas encore, sinon retourne l'existante.
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

    // L'unique instance de ce service. private pour forcer l'accès via getInstance().
    private static PatientService instance;

    // Le registre stocke tous les patients en mémoire.
    // Registre<Patient> : on précise le type générique pour éviter les raw types.
    private final Registre<Patient> registre;

    // Constructeur privé : empêche le code extérieur de faire "new PatientService()".
    // Seule getInstance() peut créer l'instance.
    private PatientService() {
        this.registre = new Registre<>();
    }

    /**
     * Point d'accès unique à ce service.
     * synchronized : si plusieurs requêtes HTTP arrivent en même temps (multi-threading),
     * on évite de créer deux instances en parallèle.
     */
    public static synchronized PatientService getInstance() {
        if (instance == null) {
            instance = new PatientService();
        }
        return instance;
    }

    // -----------------------------------------------------------------------
    // CRUD — Créer, Lire, Mettre à jour, Supprimer
    // -----------------------------------------------------------------------

    /**
     * Ajoute un nouveau patient dans le registre.
     * On vérifie d'abord que l'objet n'est pas null pour éviter une NullPointerException.
     */
    public void ajouter(Patient patient) {
        if (patient == null) {
            throw new DonneeInvalideException("L'objet patient ne peut pas être null.");
        }
        registre.ajouter(patient);
    }

    /**
     * Supprime un patient par son identifiant unique (UUID).
     * Retourne true si la suppression a eu lieu, false si l'id n'existait pas.
     */
    public boolean supprimer(String id) {
        return registre.supprimer(id);
    }

    /**
     * Met à jour les données d'un patient existant.
     * Le patient passé doit avoir le même id que celui déjà dans le registre.
     */
    public void modifier(Patient patient) {
        if (patient == null) {
            throw new DonneeInvalideException("L'objet patient ne peut pas être null.");
        }
        registre.mettreAJour(patient);
    }

    /**
     * Recherche un patient par son id interne (UUID généré automatiquement).
     * Retourne null si aucun patient ne correspond — le servlet gère ce cas avec un if.
     */
    public Patient trouverParId(String id) {
        return registre.trouverParId(id);
    }

    /**
     * Recherche un patient par son numéro métier lisible (ex : "P-2024-001").
     * On parcourt la liste avec un stream et on retourne le premier résultat trouvé.
     * findFirst() retourne un Optional : orElse(null) donne null si rien n'est trouvé.
     */
    public Patient trouverParNumero(String numeroPatient) {
        if (numeroPatient == null || numeroPatient.isBlank()) return null;
        return registre.getTous().stream()
                .filter(p -> p.getNumeroPatient().equals(numeroPatient))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retourne la liste complète des patients dans l'ordre d'ajout.
     * La liste retournée par getTous() est non-modifiable — protège les données internes.
     */
    public List<Patient> listerTous() {
        return registre.getTous();
    }

    // -----------------------------------------------------------------------
    // Actions métier — Admission et sortie
    // -----------------------------------------------------------------------

    /**
     * Admet un patient dans une chambre de l'hôpital.
     * Lève EntiteIntrouvableException si le patient n'existe pas.
     * Lève DonneeInvalideException si le numéro de chambre est vide.
     */
    public void admettre(String id, String chambre) {
        Patient patient = trouverParId(id);
        if (patient == null) {
            throw new EntiteIntrouvableException("Patient", id);
        }
        if (chambre == null || chambre.isBlank()) {
            throw new DonneeInvalideException("chambre", "le numéro de chambre est obligatoire.");
        }
        patient.admettre(chambre);
    }

    /**
     * Enregistre la sortie d'un patient hospitalisé.
     * Lève EntiteIntrouvableException si le patient n'existe pas.
     */
    public void sortir(String id) {
        Patient patient = trouverParId(id);
        if (patient == null) {
            throw new EntiteIntrouvableException("Patient", id);
        }
        patient.sortir();
    }

    // -----------------------------------------------------------------------
    // Recherche multicritères avec Streams
    // -----------------------------------------------------------------------

    /**
     * Recherche des patients selon plusieurs critères combinés.
     * Chaque critère est optionnel : s'il est null ou vide, il est simplement ignoré.
     *
     * Exemple d'appel :
     *   rechercherMulticriteres("Dupont", true, "A+")
     *   → patients dont le nom contient "Dupont", actuellement admis, groupe sanguin A+
     *
     * La chaîne de filter() est plus lisible et extensible que des if/else imbriqués.
     *
     * @param nom           Partie du nom de famille, insensible à la casse (null = ignoré)
     * @param admis         true = admis seulement, false = non admis, null = tous
     * @param groupeSanguin Groupe sanguin exact ex "A+" (null = ignoré)
     */
    public List<Patient> rechercherMulticriteres(String nom, Boolean admis, String groupeSanguin) {
        return registre.getTous().stream()
                .filter(p -> nom == null || nom.isBlank()
                        || p.getNom().toLowerCase().contains(nom.toLowerCase()))
                .filter(p -> admis == null || p.isAdmis() == admis)
                .filter(p -> groupeSanguin == null || groupeSanguin.isBlank()
                        || groupeSanguin.equalsIgnoreCase(p.getGroupeSanguin()))
                .collect(Collectors.toList());
    }

    /**
     * Retourne uniquement les patients actuellement hospitalisés.
     * Utilise la méthode filtrer() du registre avec une référence de méthode.
     * Patient::isAdmis est équivalent à p -> p.isAdmis().
     */
    public List<Patient> listerPatientsAdmis() {
        return registre.filtrer(Patient::isAdmis);
    }

    // -----------------------------------------------------------------------
    // Tri dynamique
    // -----------------------------------------------------------------------

    /**
     * Retourne la liste des patients triée par nom de famille.
     * Comparator.comparing() construit un comparateur à partir d'une méthode de référence.
     * String.CASE_INSENSITIVE_ORDER : "dupont" et "Dupont" sont traités à égalité.
     *
     * @param croissant true = A→Z, false = Z→A
     */
    public List<Patient> trierParNom(boolean croissant) {
        Comparator<Patient> parNom = Comparator.comparing(
                Patient::getNom, String.CASE_INSENSITIVE_ORDER
        );
        return registre.trierPar(croissant ? parNom : parNom.reversed());
    }

    /**
     * Retourne la liste des patients triée par date d'admission.
     * Les patients sans date d'admission (non encore admis) sont placés en dernier.
     * Comparator.nullsLast() gère les valeurs null sans planter.
     *
     * @param croissant true = du plus ancien au plus récent, false = inverse
     */
    public List<Patient> trierParDateAdmission(boolean croissant) {
        Comparator<Patient> parDate = Comparator.comparing(
                Patient::getDateAdmission,
                Comparator.nullsLast(Comparator.naturalOrder())
        );
        return registre.trierPar(croissant ? parDate : parDate.reversed());
    }

    // -----------------------------------------------------------------------
    // Statistiques
    // -----------------------------------------------------------------------

    public int getNombre() {
        return registre.getNombre();
    }

    public long getNombreAdmis() {
        return registre.compter(Patient::isAdmis);
    }

    public long getNombreNonAdmis() {
        return registre.compter(p -> !p.isAdmis());
    }

    /**
     * Calcule le chiffre d'affaires total généré par les séjours des patients.
     * mapToDouble() transforme chaque patient en son montant de facturation.
     * sum() additionne toutes ces valeurs en un seul passage sur la liste.
     */
    public double getChiffreAffairesPatients() {
        return registre.getTous().stream()
                .mapToDouble(Patient::calculerMontantTotal)
                .sum();
    }

    // Accès direct au registre, utilisé par le service de persistance au Commit 8
    public Registre<Patient> getRegistre() {
        return registre;
    }
}
