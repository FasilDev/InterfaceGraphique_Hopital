/*
 * Fichier : FileUrgences.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Gère la file d'attente des urgences médicales.
 *        Encapsule une PriorityQueue<ActeChirurgical> qui trie automatiquement
 *        les actes par niveau de priorité (1 = critique en tête de file).
 *
 * Interactions : ActeChirurgical, Urgence, UrgenceService, UrgenceServlet
 *
 * Pourquoi PriorityQueue et pas ArrayList ?
 *   ArrayList garderait l'ordre d'insertion — on traiterait les urgences dans l'ordre
 *   où elles arrivent, pas par gravité médicale. PriorityQueue trie automatiquement
 *   en utilisant la méthode compareTo() d'ActeChirurgical.
 *
 *   poll() → retire ET retourne l'élément de plus haute priorité (niveau 1 en premier)
 *   peek() → consulte sans retirer
 *   offer() → ajoute un élément (la queue se retrie automatiquement)
 */

package model;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class FileUrgences {

    // La PriorityQueue utilise compareTo() de ActeChirurgical pour trier.
    // Pas de raw type : on précise le type entre <>.
    private final PriorityQueue<ActeChirurgical> file;

    public FileUrgences() {
        // Capacité initiale 11 (valeur par défaut de PriorityQueue), pas de Comparator
        // car ActeChirurgical implémente Comparable
        this.file = new PriorityQueue<>();
    }

    // -----------------------------------------------------------------------
    // Opérations sur la file
    // -----------------------------------------------------------------------

    /**
     * Ajoute un acte chirurgical dans la file d'urgences.
     * La file se retrie automatiquement par niveau de priorité.
     */
    public void ajouterUrgence(ActeChirurgical acte) {
        if (acte != null) {
            file.offer(acte);
        }
    }

    /**
     * Retire et retourne l'acte le plus urgent (niveau de priorité le plus bas).
     * Retourne null si la file est vide.
     */
    public ActeChirurgical traiterProchainUrgence() {
        return file.poll();
    }

    /**
     * Consulte l'acte le plus urgent sans le retirer de la file.
     * Utile pour l'affichage sans consommer l'élément.
     */
    public ActeChirurgical voirProchaineUrgence() {
        return file.peek();
    }

    /**
     * Retourne la liste de toutes les urgences en attente, triées par priorité.
     * On crée une copie pour ne pas exposer la queue interne.
     * Utilise une PriorityQueue temporaire pour extraire dans le bon ordre.
     */
    public List<ActeChirurgical> listerUrgencesTriees() {
        // On crée une copie de la file pour ne pas la vider en listant
        PriorityQueue<ActeChirurgical> copie = new PriorityQueue<>(file);
        List<ActeChirurgical> resultat = new ArrayList<>();
        // poll() vide la copie dans l'ordre de priorité
        while (!copie.isEmpty()) {
            resultat.add(copie.poll());
        }
        return resultat;
    }

    public int getNombreUrgences() {
        return file.size();
    }

    public boolean estVide() {
        return file.isEmpty();
    }

    /**
     * Supprime une urgence spécifique de la file (ex : patient pris en charge entre-temps).
     * On compare par id pour être sûr de supprimer le bon objet.
     */
    public boolean supprimerUrgence(String idActe) {
        return file.removeIf(acte -> acte.getId().equals(idActe));
    }
}
