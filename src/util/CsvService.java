/*
 * Fichier : CsvService.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Gère toute la persistance des données via des fichiers CSV.
 *        Fournit des méthodes statiques pour sauvegarder et charger chaque type d'entité.
 *        Un fichier CSV par type : patients.csv, medecins.csv, etc.
 *
 * Interactions : PatientService, PersonnelService, SoinService,
 *                AppListener (Commit 9 — configurera le chemin pour Tomcat)
 *
 * Format CSV utilisé :
 *   - Séparateur de colonnes : ";"
 *   - Séparateur interne pour les listes (antécédents...) : "|"
 *   - Encodage : UTF-8 (accents français préservés)
 *   - Première ligne : en-tête avec les noms des colonnes
 *   - Valeur nulle ou vide : colonne laissée vide (pas de "null" écrit)
 *   - Dates : format ISO yyyy-MM-dd (ex : 2024-03-15)
 *   - Booléens : "true" ou "false"
 *
 * Limitation connue : si un champ texte contient ";" ou "|", le parsing sera cassé.
 *   Pour un projet étudiant c'est acceptable. En production, il faudrait échapper ces caractères.
 *
 * Chemin par défaut : "resources/" relatif au répertoire de travail.
 *   En développement depuis l'IDE, ça pointe vers resources/ à la racine du projet.
 *   En déploiement Tomcat, l'AppListener (Commit 9) appellera setDossierCsv() avec le bon chemin absolu.
 */

package util;

import controller.PatientService;
import controller.PersonnelService;
import controller.SoinService;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CsvService {

    // Séparateur de colonnes dans le CSV
    private static final String SEP = ";";

    // Séparateur pour les listes stockées dans une seule colonne (ex : antécédents)
    private static final String SEP_LISTE = "\\|";       // pour split()
    private static final String SEP_LISTE_WRITE = "|";  // pour join()

    // Chemin du dossier contenant les CSV.
    // Peut être changé par l'AppListener au démarrage de Tomcat.
    private static String dossierCsv = "resources" + File.separator;

    // Noms des fichiers CSV
    private static final String F_PATIENTS      = "patients.csv";
    private static final String F_MEDECINS      = "medecins.csv";
    private static final String F_INFIRMIERS    = "infirmiers.csv";
    private static final String F_CONSULTATIONS = "consultations.csv";
    private static final String F_ACTES         = "actes_chirurgicaux.csv";

    // En-têtes des fichiers CSV (première ligne de chaque fichier)
    private static final String HEADER_PATIENTS =
            "id;nom;prenom;dateNaissance;telephone;email;numeroPatient;groupeSanguin;" +
            "antecedents;dateAdmission;dateSortie;admis;notes;chambre;numeroSecuriteSociale;prisEnCharge";

    private static final String HEADER_MEDECINS =
            "id;nom;prenom;dateNaissance;telephone;email;matricule;specialite;numeroOrdre;disponible;dateEmbauche";

    private static final String HEADER_INFIRMIERS =
            "id;nom;prenom;dateNaissance;telephone;email;matricule;service;qualification;gardeNuit;disponible;dateEmbauche";

    private static final String HEADER_CONSULTATIONS =
            "id;motif;diagnostic;ordonnance;dateSoin;numeroPatient;matriculeMedecin;cout";

    private static final String HEADER_ACTES =
            "id;typeActe;niveauPriorite;descriptionUrgence;salle;realise;dateSoin;numeroPatient;matriculeMedecin;cout";

    // Constructeur privé : classe utilitaire, pas besoin d'instancier
    private CsvService() {}

    /**
     * Permet de changer le dossier de stockage des CSV.
     * Appelé par l'AppListener au démarrage de Tomcat pour pointer vers un dossier persistant
     * en dehors du WAR (qui est recréé à chaque redéploiement).
     *
     * @param chemin Chemin absolu du dossier, avec ou sans séparateur final
     */
    public static void setDossierCsv(String chemin) {
        dossierCsv = chemin.endsWith(File.separator) ? chemin : chemin + File.separator;
    }

    // -----------------------------------------------------------------------
    // Méthodes utilitaires privées
    // -----------------------------------------------------------------------

    // Crée le dossier de données s'il n'existe pas encore
    private static void creerDossierSiNecessaire() {
        File dossier = new File(dossierCsv);
        if (!dossier.exists()) {
            dossier.mkdirs();
        }
    }

    // null → "" ; évite d'écrire le mot "null" dans le CSV
    private static String str(Object o) {
        return o == null ? "" : o.toString();
    }

    // "" → null ; pour les champs optionnels, on préfère null à une chaîne vide
    private static String nullSiVide(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    // Parse une date ISO ou retourne null si le champ est vide
    private static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDate.parse(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    // Parse un double ou retourne la valeur par défaut en cas d'erreur
    private static double parseDouble(String s, double defaut) {
        if (s == null || s.isBlank()) return defaut;
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return defaut;
        }
    }

    // Parse un int ou retourne la valeur par défaut en cas d'erreur
    private static int parseInt(String s, int defaut) {
        if (s == null || s.isBlank()) return defaut;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return defaut;
        }
    }

    // List<String> → "elem1|elem2|elem3" pour stockage dans une cellule CSV
    private static String joinListe(List<String> liste) {
        if (liste == null || liste.isEmpty()) return "";
        return liste.stream()
                .map(s -> s.replace(SEP_LISTE_WRITE, " ")) // on remplace les "|" éventuels dans le texte
                .collect(Collectors.joining(SEP_LISTE_WRITE));
    }

    // "elem1|elem2|elem3" → List<String>
    private static List<String> splitListe(String s) {
        if (s == null || s.isBlank()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(s.split(SEP_LISTE)));
    }

    // -----------------------------------------------------------------------
    // Patients — sauvegarde et chargement
    // -----------------------------------------------------------------------

    /**
     * Sauvegarde tous les patients dans patients.csv.
     * Écrase le fichier existant (pas d'append).
     */
    public static void sauvegarderPatients(List<Patient> patients) {
        creerDossierSiNecessaire();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(dossierCsv + F_PATIENTS), StandardCharsets.UTF_8))) {

            bw.write(HEADER_PATIENTS);
            bw.newLine();

            for (Patient p : patients) {
                // String.join() avec SEP pour assembler proprement chaque ligne
                String ligne = String.join(SEP,
                        str(p.getId()),
                        str(p.getNom()),
                        str(p.getPrenom()),
                        str(p.getDateNaissance()),
                        str(p.getTelephone()),
                        str(p.getEmail()),
                        str(p.getNumeroPatient()),
                        str(p.getGroupeSanguin()),
                        joinListe(p.getAntecedents()),      // liste → "ant1|ant2"
                        str(p.getDateAdmission()),
                        str(p.getDateSortie()),
                        str(p.isAdmis()),
                        str(p.getNotes()),
                        str(p.getChambre()),
                        str(p.getNumeroSecuriteSociale()),
                        str(p.estPrisEnCharge())
                );
                bw.write(ligne);
                bw.newLine();
            }

        } catch (IOException e) {
            System.err.println("[CsvService] Erreur sauvegarde patients : " + e.getMessage());
        }
    }

    /**
     * Charge les patients depuis patients.csv.
     * Retourne une liste vide si le fichier n'existe pas.
     * Les lignes corrompues sont ignorées avec un message d'avertissement.
     */
    public static List<Patient> chargerPatients() {
        List<Patient> patients = new ArrayList<>();
        File fichier = new File(dossierCsv + F_PATIENTS);
        if (!fichier.exists()) return patients;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(fichier), StandardCharsets.UTF_8))) {

            br.readLine(); // ignore l'en-tête

            String ligne;
            while ((ligne = br.readLine()) != null) {
                if (ligne.isBlank()) continue;
                // -1 dans split() conserve les champs vides en fin de ligne
                String[] c = ligne.split(SEP, -1);
                if (c.length < 16) {
                    System.err.println("[CsvService] Ligne patient ignorée (colonnes manquantes) : " + ligne);
                    continue;
                }
                try {
                    // Constructeur "rechargement" qui utilise l'id existant
                    Patient p = new Patient(c[0], c[1], c[2], LocalDate.parse(c[3].trim()), c[6]);
                    p.setTelephone(nullSiVide(c[4]));
                    p.setEmail(nullSiVide(c[5]));
                    p.setGroupeSanguin(nullSiVide(c[7]));
                    p.setAntecedents(splitListe(c[8]));
                    p.setDateAdmission(parseDate(c[9]));
                    p.setDateSortie(parseDate(c[10]));
                    p.setAdmis(Boolean.parseBoolean(c[11]));
                    p.setNotes(nullSiVide(c[12]));
                    p.setChambre(nullSiVide(c[13]));
                    p.setNumeroSecuriteSociale(nullSiVide(c[14]));
                    p.setPrisEnCharge(Boolean.parseBoolean(c[15]));
                    patients.add(p);
                } catch (Exception e) {
                    System.err.println("[CsvService] Ligne patient ignorée (format invalide) : " + ligne);
                }
            }

        } catch (IOException e) {
            System.err.println("[CsvService] Erreur chargement patients : " + e.getMessage());
        }

        return patients;
    }

    // -----------------------------------------------------------------------
    // Médecins — sauvegarde et chargement
    // -----------------------------------------------------------------------

    public static void sauvegarderMedecins(List<Medecin> medecins) {
        creerDossierSiNecessaire();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(dossierCsv + F_MEDECINS), StandardCharsets.UTF_8))) {

            bw.write(HEADER_MEDECINS);
            bw.newLine();

            for (Medecin m : medecins) {
                String ligne = String.join(SEP,
                        str(m.getId()),
                        str(m.getNom()),
                        str(m.getPrenom()),
                        str(m.getDateNaissance()),
                        str(m.getTelephone()),
                        str(m.getEmail()),
                        str(m.getMatricule()),
                        str(m.getSpecialite()),
                        str(m.getNumeroOrdre()),
                        str(m.isDisponible()),
                        str(m.getDateEmbauche())
                );
                bw.write(ligne);
                bw.newLine();
            }

        } catch (IOException e) {
            System.err.println("[CsvService] Erreur sauvegarde médecins : " + e.getMessage());
        }
    }

    public static List<Medecin> chargerMedecins() {
        List<Medecin> medecins = new ArrayList<>();
        File fichier = new File(dossierCsv + F_MEDECINS);
        if (!fichier.exists()) return medecins;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(fichier), StandardCharsets.UTF_8))) {

            br.readLine(); // ignore l'en-tête

            String ligne;
            while ((ligne = br.readLine()) != null) {
                if (ligne.isBlank()) continue;
                String[] c = ligne.split(SEP, -1);
                if (c.length < 9) continue;
                try {
                    Medecin m = new Medecin(c[0], c[1], c[2], LocalDate.parse(c[3].trim()),
                            c[6], c[7], c[8]);
                    m.setTelephone(nullSiVide(c[4]));
                    m.setEmail(nullSiVide(c[5]));
                    if (c.length > 9)  m.setDisponible(Boolean.parseBoolean(c[9]));
                    if (c.length > 10 && !c[10].isBlank()) m.setDateEmbauche(LocalDate.parse(c[10].trim()));
                    medecins.add(m);
                } catch (Exception e) {
                    System.err.println("[CsvService] Ligne médecin ignorée : " + ligne);
                }
            }

        } catch (IOException e) {
            System.err.println("[CsvService] Erreur chargement médecins : " + e.getMessage());
        }

        return medecins;
    }

    // -----------------------------------------------------------------------
    // Infirmiers — sauvegarde et chargement
    // -----------------------------------------------------------------------

    public static void sauvegarderInfirmiers(List<Infirmier> infirmiers) {
        creerDossierSiNecessaire();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(dossierCsv + F_INFIRMIERS), StandardCharsets.UTF_8))) {

            bw.write(HEADER_INFIRMIERS);
            bw.newLine();

            for (Infirmier inf : infirmiers) {
                String ligne = String.join(SEP,
                        str(inf.getId()),
                        str(inf.getNom()),
                        str(inf.getPrenom()),
                        str(inf.getDateNaissance()),
                        str(inf.getTelephone()),
                        str(inf.getEmail()),
                        str(inf.getMatricule()),
                        str(inf.getService()),
                        str(inf.getQualification()),
                        str(inf.isGardeNuit()),
                        str(inf.isDisponible()),
                        str(inf.getDateEmbauche())
                );
                bw.write(ligne);
                bw.newLine();
            }

        } catch (IOException e) {
            System.err.println("[CsvService] Erreur sauvegarde infirmiers : " + e.getMessage());
        }
    }

    public static List<Infirmier> chargerInfirmiers() {
        List<Infirmier> infirmiers = new ArrayList<>();
        File fichier = new File(dossierCsv + F_INFIRMIERS);
        if (!fichier.exists()) return infirmiers;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(fichier), StandardCharsets.UTF_8))) {

            br.readLine(); // ignore l'en-tête

            String ligne;
            while ((ligne = br.readLine()) != null) {
                if (ligne.isBlank()) continue;
                String[] c = ligne.split(SEP, -1);
                if (c.length < 9) continue;
                try {
                    Infirmier inf = new Infirmier(c[0], c[1], c[2], LocalDate.parse(c[3].trim()),
                            c[6], c[7], c[8]);
                    inf.setTelephone(nullSiVide(c[4]));
                    inf.setEmail(nullSiVide(c[5]));
                    if (c.length > 9)  inf.setGardeNuit(Boolean.parseBoolean(c[9]));
                    if (c.length > 10) inf.setDisponible(Boolean.parseBoolean(c[10]));
                    if (c.length > 11 && !c[11].isBlank()) inf.setDateEmbauche(LocalDate.parse(c[11].trim()));
                    infirmiers.add(inf);
                } catch (Exception e) {
                    System.err.println("[CsvService] Ligne infirmier ignorée : " + ligne);
                }
            }

        } catch (IOException e) {
            System.err.println("[CsvService] Erreur chargement infirmiers : " + e.getMessage());
        }

        return infirmiers;
    }

    // -----------------------------------------------------------------------
    // Consultations — sauvegarde et chargement
    // -----------------------------------------------------------------------

    public static void sauvegarderConsultations(List<Consultation> consultations) {
        creerDossierSiNecessaire();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(dossierCsv + F_CONSULTATIONS), StandardCharsets.UTF_8))) {

            bw.write(HEADER_CONSULTATIONS);
            bw.newLine();

            for (Consultation c : consultations) {
                String ligne = String.join(SEP,
                        str(c.getId()),
                        str(c.getMotif()),
                        str(c.getDiagnostic()),
                        str(c.getOrdonnance()),
                        str(c.getDateSoin()),
                        str(c.getNumeroPatient()),
                        str(c.getMatriculeMedecin()),
                        str(c.getCout())
                );
                bw.write(ligne);
                bw.newLine();
            }

        } catch (IOException e) {
            System.err.println("[CsvService] Erreur sauvegarde consultations : " + e.getMessage());
        }
    }

    public static List<Consultation> chargerConsultations() {
        List<Consultation> consultations = new ArrayList<>();
        File fichier = new File(dossierCsv + F_CONSULTATIONS);
        if (!fichier.exists()) return consultations;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(fichier), StandardCharsets.UTF_8))) {

            br.readLine(); // ignore l'en-tête

            String ligne;
            while ((ligne = br.readLine()) != null) {
                if (ligne.isBlank()) continue;
                String[] c = ligne.split(SEP, -1);
                if (c.length < 8) continue;
                try {
                    // Constructeur de rechargement : id, motif, diagnostic, ordonnance, dateSoin, ...
                    Consultation consultation = new Consultation(
                            c[0],                           // id
                            c[1],                           // motif
                            nullSiVide(c[2]),               // diagnostic
                            nullSiVide(c[3]),               // ordonnance (peut être null)
                            parseDate(c[4]),                // dateSoin
                            c[5],                           // numeroPatient
                            c[6],                           // matriculeMedecin
                            parseDouble(c[7], 25.0)         // cout
                    );
                    consultations.add(consultation);
                } catch (Exception e) {
                    System.err.println("[CsvService] Ligne consultation ignorée : " + ligne);
                }
            }

        } catch (IOException e) {
            System.err.println("[CsvService] Erreur chargement consultations : " + e.getMessage());
        }

        return consultations;
    }

    // -----------------------------------------------------------------------
    // Actes chirurgicaux — sauvegarde et chargement
    // -----------------------------------------------------------------------

    public static void sauvegarderActes(List<ActeChirurgical> actes) {
        creerDossierSiNecessaire();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(dossierCsv + F_ACTES), StandardCharsets.UTF_8))) {

            bw.write(HEADER_ACTES);
            bw.newLine();

            for (ActeChirurgical a : actes) {
                String ligne = String.join(SEP,
                        str(a.getId()),
                        str(a.getTypeActe()),
                        str(a.getNiveauPriorite()),
                        str(a.getDescriptionUrgence()),
                        str(a.getSalle()),
                        str(a.isRealise()),
                        str(a.getDateSoin()),
                        str(a.getNumeroPatient()),
                        str(a.getMatriculeMedecin()),
                        str(a.getCout())
                );
                bw.write(ligne);
                bw.newLine();
            }

        } catch (IOException e) {
            System.err.println("[CsvService] Erreur sauvegarde actes chirurgicaux : " + e.getMessage());
        }
    }

    public static List<ActeChirurgical> chargerActes() {
        List<ActeChirurgical> actes = new ArrayList<>();
        File fichier = new File(dossierCsv + F_ACTES);
        if (!fichier.exists()) return actes;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(fichier), StandardCharsets.UTF_8))) {

            br.readLine(); // ignore l'en-tête

            String ligne;
            while ((ligne = br.readLine()) != null) {
                if (ligne.isBlank()) continue;
                String[] c = ligne.split(SEP, -1);
                if (c.length < 10) continue;
                try {
                    ActeChirurgical acte = new ActeChirurgical(
                            c[0],                             // id
                            c[1],                             // typeActe
                            parseInt(c[2], 3),                // niveauPriorite
                            nullSiVide(c[3]),                 // descriptionUrgence
                            nullSiVide(c[4]),                 // salle
                            Boolean.parseBoolean(c[5]),       // realise
                            parseDate(c[6]),                  // dateSoin
                            c[7],                             // numeroPatient
                            c[8],                             // matriculeMedecin
                            parseDouble(c[9], 500.0)          // cout
                    );
                    actes.add(acte);
                } catch (Exception e) {
                    System.err.println("[CsvService] Ligne acte chirurgical ignorée : " + ligne);
                }
            }

        } catch (IOException e) {
            System.err.println("[CsvService] Erreur chargement actes chirurgicaux : " + e.getMessage());
        }

        return actes;
    }

    // -----------------------------------------------------------------------
    // Méthodes globales — convenience wrappers
    // -----------------------------------------------------------------------

    /**
     * Sauvegarde toutes les données de l'application en une seule fois.
     * Appelée par l'AppListener à l'arrêt du serveur et après chaque modification importante.
     */
    public static void sauvegarderTout(PatientService ps, PersonnelService pers, SoinService ss) {
        sauvegarderPatients(new ArrayList<>(ps.listerTous()));
        sauvegarderMedecins(new ArrayList<>(pers.listerMedecins()));
        sauvegarderInfirmiers(new ArrayList<>(pers.listerInfirmiers()));
        sauvegarderConsultations(new ArrayList<>(ss.listerConsultations()));
        sauvegarderActes(new ArrayList<>(ss.listerActes()));
    }

    /**
     * Charge toutes les données depuis les CSV et les injecte dans les services.
     * Appelée par l'AppListener au démarrage du serveur.
     * Si un CSV n'existe pas, la liste retournée est vide et le service reste vide.
     */
    public static void chargerTout(PatientService ps, PersonnelService pers, SoinService ss) {
        chargerPatients().forEach(ps::ajouter);
        chargerMedecins().forEach(pers::ajouterMedecin);
        chargerInfirmiers().forEach(pers::ajouterInfirmier);
        chargerConsultations().forEach(ss::ajouterConsultation);
        // ajouterActeChirurgical() ajoute automatiquement les actes non réalisés à la file d'urgences
        chargerActes().forEach(ss::ajouterActeChirurgical);
    }
}
