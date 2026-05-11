# HospitApp — Application Web de Gestion Hospitalière

## Groupe

| Prénom Nom | Rôle principal |
|---|---|
| [Prénom 1] [Nom 1] | Modèle métier & Services |
| [Prénom 2] [Nom 2] | Servlets & Architecture MVC |
| [Prénom 3] [Nom 3] | Interface JSP & CSS |

---

## Description

**HospitApp** est une application web Java de gestion hospitalière développée dans le cadre du projet de **POO Avancée en Bachelor 3 Informatique**.

Elle permet de gérer les **patients** (admission, dossier médical, sortie), le **personnel médical** (médecins, infirmiers, spécialités, plannings), les **soins** (consultations, prescriptions, actes chirurgicaux), la **disponibilité des salles et lits**, et la **file d'attente des urgences** ordonnée par priorité médicale. L'interface web est simple, intuitive et responsive grâce à Bootstrap 5. Les données sont persistées en CSV pour faciliter la compréhension et la portabilité.

---

## Technologie choisie : Option B — Servlet/JSP (Tomcat 10.1+)

**Justification :** L'option Servlet/JSP a été choisie pour appliquer le pattern **MVC côté serveur** de manière explicite et pédagogique :
- Les **Servlets** jouent le rôle de **contrôleurs** (reçoivent les requêtes HTTP, appellent les services, transmettent les données aux vues).
- Les **JSP** jouent le rôle de **vues** (affichent les données sans contenir de logique métier).
- Les **classes Java métier** forment le **modèle**, totalement indépendant du web.

Cette architecture prépare à des frameworks plus avancés (Spring MVC) rencontrés en master.

---

## Prérequis

| Outil | Version minimale |
|---|---|
| Java JDK | 17+ |
| Apache Maven | 3.8+ |
| Apache Tomcat | **10.1+** (important : pas 9.x) |
| Navigateur | Tout navigateur moderne |

> **Important :** Tomcat 10+ utilise le namespace `jakarta.*` (Jakarta EE).  
> Tomcat 9 et avant utilisaient `javax.*` (Java EE). Les deux sont **incompatibles**.

---

## Instructions de compilation et déploiement

### 1. Cloner le projet

```bash
git clone <url-du-repo>
cd HospitApp
```

### 2. Compiler et packager

```bash
mvn clean package
```

Cela génère `target/HospitApp.war`.

### 3a. Déploiement manuel sur Tomcat

```bash
# Copier le WAR dans webapps/ de Tomcat
cp target/HospitApp.war /chemin/vers/apache-tomcat-10.1.x/webapps/

# Démarrer Tomcat (Linux/Mac)
/chemin/vers/apache-tomcat-10.1.x/bin/startup.sh

# Démarrer Tomcat (Windows)
/chemin/vers/apache-tomcat-10.1.x/bin/startup.bat
```

### 3b. Déploiement via IntelliJ IDEA

1. Aller dans **Run > Edit Configurations**
2. Cliquer **+** → **Tomcat Server > Local**
3. Onglet **Server** : indiquer le chemin de Tomcat 10.1
4. Onglet **Deployment** : cliquer **+** → **Artifact** → sélectionner `HospitApp:war exploded`
5. Définir **Application context** : `/HospitApp`
6. Lancer avec le bouton **Run** (triangle vert)

### 4. Accéder à l'application

```
http://localhost:8080/HospitApp/
```

---

## Structure du projet

```
HospitApp/
├── src/
│   └── main/
│       ├── java/com/hospitapp/
│       │   ├── model/      ← Classes métier (Personne, Patient, Medecin, Soin…)
│       │   ├── servlet/    ← Servlets = contrôleurs HTTP
│       │   ├── service/    ← Logique applicative (PatientService, PersonnelService…)
│       │   └── util/       ← Utilitaires (Registre générique, CsvUtil…)
│       └── webapp/
│           ├── WEB-INF/
│           │   ├── web.xml         ← Descripteur de déploiement Tomcat
│           │   └── views/          ← Fichiers JSP (vues MVC — non accessibles directement)
│           ├── css/
│           │   └── style.css       ← Styles personnalisés
│           └── index.jsp           ← Page d'accueil
├── resources/                      ← Données CSV de test
├── pom.xml                         ← Configuration Maven
├── README.md
└── rapport_conception.md
```

---

## Architecture POO

### Classes abstraites
| Classe | Rôle |
|---|---|
| `Personne` | Classe de base pour tout être humain du système |
| `Personnel` | Extension de Personne pour le personnel médical |
| `Soin` | Classe de base pour tous les actes médicaux |

### Interfaces métier
| Interface | Rôle |
|---|---|
| `Soignable` | Peut recevoir des soins (Patient, Médecin) |
| `Planifiable` | A un planning gérable (Médecin, Infirmier) |
| `Facturable` | Génère une facturation (Patient) |
| `Urgence` | Priorité médicale mesurable (ActeChirurgical) |

### Héritage
```
Personne (abstract)
├── Personnel (abstract)
│   ├── Medecin    implements Planifiable, Soignable
│   └── Infirmier  implements Planifiable
└── Patient        implements Soignable, Facturable

Soin (abstract)
├── Consultation
└── ActeChirurgical  implements Urgence
```

### Generics
- `Registre<T extends Entite>` — collection générique bornée pour toute entité du système
- Utilisation de wildcards `? extends` pour les statistiques

### Collections utilisées
| Collection | Usage dans le projet |
|---|---|
| `List<Patient>` | Liste ordonnée des patients admis |
| `Map<String, Medecin>` | Index des médecins par spécialité |
| `Set<String>` | Unicité des identifiants |
| `PriorityQueue<Patient>` | File d'attente urgences par priorité |
| `TreeMap<LocalDate, List<Soin>>` | Planning trié par date |

---

## Fonctionnalités implémentées

- [x] Initialisation du projet Maven web Jakarta EE
- [ ] Gestion des patients (CRUD, admission, sortie, dossier médical)
- [ ] Gestion du personnel (médecins, infirmiers, spécialités, plannings)
- [ ] Gestion des soins (consultations, prescriptions, actes médicaux)
- [ ] Gestion des salles et lits (occupation, disponibilité)
- [ ] File d'attente urgences avec priorité médicale (PriorityQueue)
- [ ] Filtrage multicritères (nom, état, spécialité)
- [ ] Tri dynamique (nom, date, priorité)
- [ ] Statistiques dynamiques (patients, lits, urgences, spécialités)
- [ ] Persistance CSV (sauvegarde/chargement)
- [ ] Exceptions métier personnalisées

---

## Répartition des tâches

| Membre | Commits | Responsabilités |
|---|---|---|
| [Prénom 1] | Commits 2, 3, 4, 5 | Modèle POO, interfaces, generics |
| [Prénom 2] | Commits 6, 7, 8, 9 | Services, exceptions, Servlets |
| [Prénom 3] | Commits 10, 11, 12, 13, 14 | JSP, CSS, Bootstrap, filtres |
| Commun | Commits 1, 15 | Init projet, README, rapport |
