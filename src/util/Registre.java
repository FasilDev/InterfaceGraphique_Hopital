/*
 * Registre.java - Classe générique servant de collection universelle pour toute entité du modèle.
 * Un Registre<Patient> gère les patients, un Registre<Medecin> les médecins, etc.
 *
 * Concepts POO importants (à connaître pour la soutenance) :
 *
 *   Generic borné : <T extends Entite>
 *     T peut être n'importe quelle sous-classe d'Entite, ce qui garantit l'accès à getId().
 *
 *   Wildcard ? super T dans trierPar() :
 *     Comparator<? super T> accepte un Comparator de T ou d'une super-classe de T.
 *     Ex : un Comparator<Personne> peut trier des Patient (Patient extends Personne).
 *
 *   Wildcard ? extends dans compterEntites() :
 *     List<? extends Entite> accepte List<Patient>, List<Medecin>, etc.
 *     On peut lire les éléments mais pas en ajouter (type inconnu à la compilation).
 *
 * Collections utilisées :
 *   List<T>           -> ordre d'insertion, affichage
 *   Map<String, T>    -> accès par id en O(1)
 *   Set<String>       -> vérification d'unicité des ids en O(1)
 *   TreeMap<String,T> -> retourné par getIndexTrie(), trié alphabétiquement par id
 */

package util;

import model.Entite;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Registre<T extends Entite> {

    private final List<T>        entites; // liste ordonnée pour l'affichage
    private final Map<String, T> index;   // accès rapide par id
    private final Set<String>    ids;     // vérification d'unicité

    public Registre() {
        this.entites = new ArrayList<>();
        this.index   = new HashMap<>();
        this.ids     = new HashSet<>();
    }

    // -------------------------------------------------------------------
    // CRUD
    // -------------------------------------------------------------------

    // Ignore les doublons (même id) et les valeurs null
    public void ajouter(T entite) {
        if (entite == null || ids.contains(entite.getId())) return;
        entites.add(entite);
        index.put(entite.getId(), entite);
        ids.add(entite.getId());
    }

    public boolean supprimer(String id) {
        if (id == null || !ids.contains(id)) return false;
        T entite = index.get(id);
        entites.remove(entite);
        index.remove(id);
        ids.remove(id);
        return true;
    }

    public T trouverParId(String id) {
        if (id == null) return null;
        return index.get(id);
    }

    public void mettreAJour(T entite) {
        if (entite == null || !ids.contains(entite.getId())) return;
        for (int i = 0; i < entites.size(); i++) {
            if (entites.get(i).getId().equals(entite.getId())) {
                entites.set(i, entite);
                break;
            }
        }
        index.put(entite.getId(), entite);
    }

    // Retourne une vue non modifiable pour protéger les données internes
    public List<T> getTous() {
        return Collections.unmodifiableList(entites);
    }

    // -------------------------------------------------------------------
    // Recherche et tri via Streams
    // -------------------------------------------------------------------

    // Filtre les entités selon un critère lambda : registre.filtrer(p -> p.isAdmis())
    public List<T> filtrer(Predicate<T> critere) {
        return entites.stream()
                .filter(critere)
                .collect(Collectors.toList());
    }

    // Comparator<? super T> : accepte un comparateur de T ou d'une super-classe de T
    public List<T> trierPar(Comparator<? super T> comparateur) {
        return entites.stream()
                .sorted(comparateur)
                .collect(Collectors.toList());
    }

    public List<T> filtrerEtTrier(Predicate<T> critere, Comparator<? super T> comparateur) {
        return entites.stream()
                .filter(critere)
                .sorted(comparateur)
                .collect(Collectors.toList());
    }

    public long compter(Predicate<T> critere) {
        return entites.stream()
                .filter(critere)
                .count();
    }

    // -------------------------------------------------------------------
    // Accès trié via TreeMap
    // -------------------------------------------------------------------

    // TreeMap trie automatiquement ses clés alphabétiquement
    public TreeMap<String, T> getIndexTrie() {
        return new TreeMap<>(index);
    }

    // -------------------------------------------------------------------
    // Méthode statique avec wildcard ? extends
    // -------------------------------------------------------------------

    // Accepte List<Patient>, List<Medecin>, List<Soin>... — covariance via ? extends Entite
    public static int compterEntites(List<? extends Entite> liste) {
        if (liste == null) return 0;
        return liste.size();
    }

    // -------------------------------------------------------------------
    // Utilitaires
    // -------------------------------------------------------------------

    public int getNombre()             { return entites.size(); }
    public boolean contient(String id) { return ids.contains(id); }
    public boolean estVide()           { return entites.isEmpty(); }

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
