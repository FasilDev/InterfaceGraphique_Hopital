/*
 * Fichier : Registre.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Classe générique servant de collection universelle pour toute entité du système.
 *        Un Registre<Patient> gère les patients, un Registre<Medecin> gère les médecins, etc.
 *        On écrit une seule classe et elle fonctionne pour tous les types.
 *
 * Interactions : toutes les classes du modèle via T, tous les Services (Commit 6)
 *
 * Concepts POO utilisés ici (importants pour la soutenance) :
 *
 *   1. Generic borné : <T extends Entite>
 *      T peut être n'importe quelle sous-classe d'Entite (Patient, Medecin, Soin...).
 *      Le "extends Entite" garantit que T a toujours un getId() — on peut l'utiliser en interne.
 *
 *   2. Wildcard ? extends : Comparator<? super T>
 *      Un Comparator<Personne> peut trier des Patient car Patient extends Personne.
 *      Le "? super T" dit : "accepte un Comparator de T ou de n'importe quelle super-classe de T".
 *      C'est la contravariance — le principe PECS : Producer Extends, Consumer Super.
 *
 *   3. Wildcard ? extends dans une méthode statique :
 *      compterEntites(List<? extends Entite>) accepte une List<Patient>, une List<Medecin>,
 *      une List<Soin>... n'importe quelle liste de sous-types d'Entite.
 *
 * Collections utilisées ici :
 *   - List<T>          → ArrayList, liste ordonnée, garde l'ordre d'ajout
 *   - Map<String, T>   → HashMap, accès direct par id en O(1)
 *   - Set<String>      → HashSet, vérification rapide de l'unicité des ids
 *   - TreeMap<String,T>→ retourné par getIndexTrie(), trié alphabétiquement par id
 */

package util;

import model.Entite;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Registre<T extends Entite> {

    // Liste ordonnée — garde l'ordre d'insertion, utilisée pour l'affichage
    private final List<T> entites;

    // Index par id — accès instantané à un élément sans parcourir toute la liste
    private final Map<String, T> index;

    // Ensemble des ids présents — pour vérifier l'unicité en O(1)
    private final Set<String> ids;

    // -----------------------------------------------------------------------
    // Constructeur
    // -----------------------------------------------------------------------

    public Registre() {
        this.entites = new ArrayList<>();
        this.index   = new HashMap<>();
        this.ids     = new HashSet<>();
    }

    // -----------------------------------------------------------------------
    // CRUD de base
    // -----------------------------------------------------------------------

    /**
     * Ajoute une entité dans le registre.
     * Ignore les doublons (même id déjà présent) et les valeurs null.
     */
    public void ajouter(T entite) {
        if (entite == null) return;
        // On vérifie l'unicité via le Set — plus rapide qu'un contains() sur la List
        if (ids.contains(entite.getId())) return;

        entites.add(entite);
        index.put(entite.getId(), entite);
        ids.add(entite.getId());
    }

    /**
     * Supprime une entité du registre par son id.
     * Retourne true si la suppression a eu lieu, false si l'id n'existait pas.
     */
    public boolean supprimer(String id) {
        if (id == null || !ids.contains(id)) return false;

        T entite = index.get(id);
        entites.remove(entite);
        index.remove(id);
        ids.remove(id);
        return true;
    }

    /**
     * Retourne l'entité dont l'id correspond, ou null si elle n'existe pas.
     * Recherche en O(1) grâce à la Map.
     */
    public T trouverParId(String id) {
        if (id == null) return null;
        return index.get(id);
    }

    /**
     * Remplace une entité existante par une version mise à jour.
     * L'entité doit déjà être dans le registre (même id).
     */
    public void mettreAJour(T entite) {
        if (entite == null || !ids.contains(entite.getId())) return;

        // On cherche la position dans la liste pour la remplacer en place
        for (int i = 0; i < entites.size(); i++) {
            if (entites.get(i).getId().equals(entite.getId())) {
                entites.set(i, entite);
                break;
            }
        }
        // On met aussi à jour l'index
        index.put(entite.getId(), entite);
    }

    /**
     * Retourne une vue non modifiable de la liste complète.
     * Pas de Collections.unmodifiableList() = le code appelant pourrait accidentellement
     * modifier la liste interne. On protège les données du registre.
     */
    public List<T> getTous() {
        return Collections.unmodifiableList(entites);
    }

    // -----------------------------------------------------------------------
    // Recherche et tri via Streams et lambdas
    // -----------------------------------------------------------------------

    /**
     * Filtre les entités selon un critère donné sous forme de Predicate (lambda).
     *
     * Usage : registre.filtrer(p -> p.getNom().contains("Dupont"))
     *
     * @param critere Lambda ou méthode de référence qui retourne true pour garder l'élément
     * @return Nouvelle liste contenant uniquement les entités qui passent le filtre
     */
    public List<T> filtrer(Predicate<T> critere) {
        return entites.stream()
                .filter(critere)
                .collect(Collectors.toList());
    }

    /**
     * Trie les entités selon un Comparator.
     *
     * Comparator<? super T> : accepte un comparateur de T ou de n'importe quelle
     * super-classe de T. Ex : un Comparator<Personne> peut trier des Patient.
     *
     * Usage : registre.trierPar(Comparator.comparing(Personne::getNom))
     */
    public List<T> trierPar(Comparator<? super T> comparateur) {
        return entites.stream()
                .sorted(comparateur)
                .collect(Collectors.toList());
    }

    /**
     * Filtre puis trie en une seule passe — évite de créer deux listes intermédiaires.
     *
     * Usage :
     *   registre.filtrerEtTrier(
     *       p -> p.isAdmis(),
     *       Comparator.comparing(Personne::getNom)
     *   )
     */
    public List<T> filtrerEtTrier(Predicate<T> critere, Comparator<? super T> comparateur) {
        return entites.stream()
                .filter(critere)
                .sorted(comparateur)
                .collect(Collectors.toList());
    }

    /**
     * Compte les entités qui correspondent à un critère.
     *
     * Usage : long nbAdmis = registre.compter(p -> p.isAdmis())
     */
    public long compter(Predicate<T> critere) {
        return entites.stream()
                .filter(critere)
                .count();
    }

    // -----------------------------------------------------------------------
    // Accès trié via TreeMap
    // -----------------------------------------------------------------------

    /**
     * Retourne toutes les entités dans une TreeMap triée par id (ordre alphabétique).
     * TreeMap maintient ses clés triées naturellement (String → ordre alphabétique).
     * Utile pour un affichage trié ou une exportation ordonnée.
     */
    public TreeMap<String, T> getIndexTrie() {
        // On passe le HashMap existant au constructeur de TreeMap
        // → TreeMap va copier et trier automatiquement les entrées
        return new TreeMap<>(index);
    }

    // -----------------------------------------------------------------------
    // Méthode statique avec wildcard ? extends
    // -----------------------------------------------------------------------

    /**
     * Méthode statique générique : accepte une liste de n'importe quel sous-type d'Entite.
     *
     * List<? extends Entite> = covariance.
     * On peut passer List<Patient>, List<Medecin>, List<Soin>...
     * On ne peut pas faire liste.add() (type inconnu), mais on peut lire les éléments.
     *
     * @param liste N'importe quelle liste de sous-types d'Entite
     * @return Le nombre d'éléments dans la liste
     */
    public static int compterEntites(List<? extends Entite> liste) {
        if (liste == null) return 0;
        return liste.size();
    }

    // -----------------------------------------------------------------------
    // Utilitaires
    // -----------------------------------------------------------------------

    public int getNombre() {
        return entites.size();
    }

    public boolean contient(String id) {
        return ids.contains(id);
    }

    public boolean estVide() {
        return entites.isEmpty();
    }

    /** Vide complètement le registre. */
    public void vider() {
        entites.clear();
        index.clear();
        ids.clear();
    }

    @Override
    public String toString() {
        return "Registre[" + entites.size() + " entité(s)]";
    }
}
