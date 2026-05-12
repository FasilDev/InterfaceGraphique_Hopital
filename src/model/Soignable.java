/*
 * Fichier : Soignable.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Interface marquant toute entité liée à des actes de soin.
 *        Implémentée par Patient (reçoit des soins) et Medecin (effectue des soins).
 *        Le nom "Soignable" indique une relation avec les soins médicaux,
 *        que ce soit en tant que soignant ou soigné.
 *
 * Interactions : Patient, Medecin, SoinService (commits suivants)
 *
 * Pourquoi une interface et pas une classe abstraite ?
 * Parce que Patient et Medecin héritent déjà de classes différentes.
 * Java n'autorise pas l'héritage multiple — les interfaces permettent de contourner ça.
 */

package model;

import java.util.List;

public interface Soignable {

    // Retourne la liste des soins associés à cette entité
    // (soins reçus pour un patient, soins effectués pour un médecin)
    List<String> getHistoriqueSoins();

    // Ajoute un soin à l'historique
    // On utilise String ici en attendant la classe Soin (Commit 4)
    void ajouterSoin(String descriptionSoin);

    // Indique si l'entité a des antécédents médicaux connus
    boolean aDesAntecedents();
}
