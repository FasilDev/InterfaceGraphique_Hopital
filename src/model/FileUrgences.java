/*
 * FileUrgences.java - File d'attente des urgences médicales.
 * Encapsule une PriorityQueue<ActeChirurgical> qui trie automatiquement par niveau de priorité.
 *
 * PriorityQueue vs ArrayList : ArrayList garderait l'ordre d'arrivée, pas l'ordre de gravité.
 * PriorityQueue appelle compareTo() d'ActeChirurgical pour trier (niveau 1 = sorti en premier).
 * poll() retire le plus urgent, peek() consulte sans retirer, offer() ajoute et retrie.
 */

package model;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class FileUrgences {

    private final PriorityQueue<ActeChirurgical> file;

    public FileUrgences() {
        this.file = new PriorityQueue<>();
    }

    // Ajoute un acte à la file (retrie automatiquement par priorité)
    public void ajouterUrgence(ActeChirurgical acte) {
        if (acte != null) {
            file.offer(acte);
        }
    }

    // Retire et retourne l'acte le plus urgent ; null si la file est vide
    public ActeChirurgical traiterProchainUrgence() {
        return file.poll();
    }

    // Consulte le prochain sans le retirer
    public ActeChirurgical voirProchaineUrgence() {
        return file.peek();
    }

    // Retourne la liste triée par priorité sans vider la file réelle
    public List<ActeChirurgical> listerUrgencesTriees() {
        PriorityQueue<ActeChirurgical> copie = new PriorityQueue<>(file);
        List<ActeChirurgical> resultat = new ArrayList<>();
        while (!copie.isEmpty()) {
            resultat.add(copie.poll());
        }
        return resultat;
    }

    public int getNombreUrgences() { return file.size(); }

    public boolean estVide() { return file.isEmpty(); }

    public boolean supprimerUrgence(String idActe) {
        return file.removeIf(acte -> acte.getId().equals(idActe));
    }
}
