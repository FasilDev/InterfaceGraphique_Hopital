/*
 * Soignable.java - Interface pour toute entité liée à des actes de soin.
 * Implémentée par Patient (reçoit des soins) et Medecin (réalise des soins).
 * On utilise une interface car Patient et Medecin héritent déjà de classes différentes
 * (Java n'autorise pas l'héritage multiple).
 */

package model;

import java.util.List;

public interface Soignable {

    List<String> getHistoriqueSoins();

    void ajouterSoin(String descriptionSoin);

    boolean aDesAntecedents();
}
