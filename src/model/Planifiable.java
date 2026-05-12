/*
 * Planifiable.java - Interface pour tout membre du personnel ayant un planning.
 * Implémentée par Medecin et Infirmier.
 * Personnel implémente déjà isDisponible() via son champ boolean — les sous-classes en héritent.
 */

package model;

import java.time.LocalDate;
import java.util.Map;

public interface Planifiable {

    boolean estDisponible();

    void setDisponible(boolean disponible);

    // Ajoute un créneau horaire au planning : date -> description (ex : "08h00-14h00 Cardiologie")
    void ajouterCreneau(LocalDate date, String horaire);

    Map<LocalDate, String> getPlanning();
}
