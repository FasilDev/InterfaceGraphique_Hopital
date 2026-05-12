# HospitApp — Application Web de Gestion Hospitalière

## Groupe

| Prénom Nom | Rôle principal |
|---|---|
| [Prénom 1] [Nom 1] | Modèle métier & Services |
| [Prénom 2] [Nom 2] | Servlets & Architecture MVC |
| [Prénom 3] [Nom 3] | Interface JSP & CSS |

---

## Description

**HospitApp** est une application web Java de gestion hospitalière, développée dans le cadre du projet POO Avancée en Bachelor 3 Informatique.

L'application couvre la gestion des **patients** (admission, dossier médical, sortie), du **personnel médical** (médecins, infirmiers, spécialités, plannings), des **soins** (consultations, prescriptions, actes chirurgicaux), des **salles et lits** (occupation, disponibilité), et d'une **file d'attente des urgences** triée par priorité médicale. L'interface est responsive grâce à Bootstrap 5. Les données sont sauvegardées en CSV.

---

## Technologie choisie : Option B — Servlet/JSP (Tomcat 10.1+)

On a choisi Servlet/JSP pour avoir un MVC côté serveur clair et explicite :
- Les **Servlets** reçoivent les requêtes HTTP, appellent les services et transmettent les données aux vues.
- Les **JSP** affichent les données — elles ne contiennent pas de logique métier.
- Le **modèle Java** est totalement indépendant du web.

C'est une bonne base pour comprendre comment fonctionnent des frameworks comme Spring MVC.

---

## Prérequis

| Outil | Version minimale |
|---|---|
| Java JDK | 17+ |
| Apache Maven | 3.8+ |
| Apache Tomcat | **10.1+** (pas 9.x) |
| Navigateur | Tout navigateur moderne |

> Tomcat 10+ utilise `jakarta.*` (Jakarta EE). Tomcat 9 utilisait `javax.*` (Java EE). Les deux sont incompatibles — vérifier la version avant de déployer.

---

## Compilation et déploiement

### 1. Cloner le projet

```bash
git clone <url-du-repo>
cd HospitApp
```

### 2. Compiler et packager

```bash
mvn clean package
```

Génère `target/HospitApp.war`.

### 3a. Déploiement manuel sur Tomcat

```bash
cp target/HospitApp.war /chemin/vers/apache-tomcat-10.1.x/webapps/

# Linux/Mac
/chemin/vers/apache-tomcat-10.1.x/bin/startup.sh

# Windows
/chemin/vers/apache-tomcat-10.1.x/bin/startup.bat
```

### 3b. Déploiement via IntelliJ IDEA

1. **Run > Edit Configurations**
2. **+** → **Tomcat Server > Local**
3. Onglet **Server** : chemin vers Tomcat 10.1
4. Onglet **Deployment** : **+** → **Artifact** → `HospitApp:war exploded`
5. **Application context** : `/HospitApp`
6. Lancer avec **Run**

### 4. Accéder à l'application

```
http://localhost:8080/HospitApp/
```

---

## Structure du projet

```
HospitApp/
├── src/
│   ├── model/          ← Classes métier (Personne, Patient, Medecin, Soin…)
│   ├── view/           ← JSP + WEB-INF/web.xml + css/
│   │   ├── WEB-INF/
│   │   │   ├── web.xml         ← Descripteur de déploiement Tomcat
│   │   │   └── views/          ← Pages JSP (non accessibles directement)
│   │   ├── css/
│   │   │   └── style.css
│   │   └── index.jsp
│   ├── controller/     ← Servlets (contrôleurs HTTP)
│   └── util/           ← Utilitaires (Registre générique, CsvUtil…)
├── resources/          ← Données CSV de test
├── pom.xml
├── README.md
└── rapport_conception.md
```

---

## Architecture POO

### Classes abstraites
| Classe | Rôle |
|---|---|
| `Personne` | Base commune à tout individu du système |
| `Personnel` | Étend Personne pour le personnel médical |
| `Soin` | Base commune à tous les actes médicaux |

### Interfaces métier
| Interface | Rôle |
|---|---|
| `Soignable` | Peut recevoir des soins (Patient, Médecin) |
| `Planifiable` | A un planning gérable (Médecin, Infirmier) |
| `Facturable` | Génère une facturation (Patient) |
| `Urgence` | A une priorité médicale mesurable (ActeChirurgical) |

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
- Wildcards `? extends` utilisées dans les méthodes de statistiques

### Collections utilisées
| Collection | Usage |
|---|---|
| `List<Patient>` | Liste ordonnée des patients admis |
| `Map<String, Medecin>` | Index des médecins par spécialité |
| `Set<String>` | Unicité des identifiants |
| `PriorityQueue<Patient>` | File d'attente urgences par priorité |
| `TreeMap<LocalDate, List<Soin>>` | Planning trié par date |

---

## Fonctionnalités implémentées

- [x] Initialisation du projet Maven web Jakarta EE
- [x] Modèle métier de base (Entite, Personne, Patient, Personnel, Medecin, Infirmier)
- [ ] Interfaces métier (Soignable, Planifiable, Facturable, Urgence)
- [ ] Soins (Consultation, ActeChirurgical, file d'urgences)
- [ ] Classe générique Registre et collections avancées
- [ ] Services métier (PatientService, PersonnelService, SoinService)
- [ ] Exceptions personnalisées (CapaciteDepasseeException, EntiteIntrouvableException…)
- [ ] Persistance CSV
- [ ] Servlets (PatientServlet, PersonnelServlet, SoinServlet, UrgenceServlet, StatistiqueServlet)
- [ ] Pages JSP patients (liste, détail, formulaire)
- [ ] Pages JSP personnel, soins, urgences
- [ ] Recherche multicritères et tri dynamique
- [ ] Statistiques dynamiques
- [ ] Interface Bootstrap finalisée

---

## Répartition des tâches

| Membre | Commits | Responsabilités |
|---|---|---|
| [Prénom 1] | Commits 2, 3, 4, 5 | Modèle POO, interfaces, generics |
| [Prénom 2] | Commits 6, 7, 8, 9 | Services, exceptions, Servlets |
| [Prénom 3] | Commits 10, 11, 12, 13, 14 | JSP, CSS, Bootstrap, filtres |
| Commun | Commits 1, 15 | Init projet, README, rapport |
