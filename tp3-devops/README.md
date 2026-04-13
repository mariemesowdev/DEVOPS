# TP3 - DevOps - CI/CD GitLab


##  Prérequis

| Outil | Version | Lien |
|-------|---------|------|
| Git | latest | https://git-scm.com |
| Compte GitLab | - | https://gitlab.com |
| Token GitLab | - | Settings → Access Tokens |
| vagrant-scp | latest | `vagrant plugin install vagrant-scp` |

---

##  Architecture CI/CD

```
+------------------+       push        +------------------+
|   Dev (PC)       | ----------------> |   GitLab Repo    |
|   studentapi     |                   |   studentapi     |
+------------------+                   +--------+---------+
                                                |
                                         trigger pipeline
                                                |
                                   +------------v-----------+
                                   |    GitLab CI/CD        |
                                   |  +------------------+  |
                                   |  |  Stage: build    |  |
                                   |  +------------------+  |
                                   |  |  Stage: test     |  |
                                   |  +------------------+  |
                                   |  |  Stage: package  |  |
                                   |  +------------------+  |
                                   +------------------------+
```

---

## Étapes

### 1. Créer le repo sur GitLab

1. Aller sur https://gitlab.com
2. Cliquer sur **"New project"** → **"Create blank project"**
3. Nom : `studentapi`
4. Décocher **"Initialize repository with a README"**
5. Cliquer **"Create project"**

---

### 2. Copier le projet depuis la VM

```powershell
# Installer le plugin vagrant-scp
vagrant plugin install vagrant-scp

# Copier le projet depuis server-back
cd tp3-devops
vagrant scp server-back:/home/vagrant/studentapi .
```

---

### 3. Configurer Git et pusher

```powershell
cd studentapi

git init
git config --global user.name "Ton Nom"
git config --global user.email "ton@email.com"

# Créer .gitignore
cat > .gitignore << 'EOF'
target/
*.class
*.jar
*.war
.idea/
*.iml
app.log
EOF

git add .
git commit -m "Initial commit - StudentAPI Spring Boot"
git remote add origin https://gitlab.com/TON_USERNAME/studentapi.git
git branch -M main
git pull origin main --allow-unrelated-histories
git push -u origin main
```

> Quand Git demande le mot de passe, entrer le **token GitLab** et non le mot de passe du compte.

---

### 4. Créer le fichier .gitlab-ci.yml

```yaml
stages:
  - build
  - test
  - package

variables:
  MAVEN_OPTS: "-Dmaven.repo.local=.m2/repository"

cache:
  paths:
    - .m2/repository

build:
  stage: build
  image: maven:3.9.6-eclipse-temurin-17
  tags:
    - gitlab-org-medium
  script:
    - echo "🔨 Build du projet..."
    - mvn clean compile
  artifacts:
    paths:
      - target/

test:
  stage: test
  image: maven:3.9.6-eclipse-temurin-17
  tags:
    - gitlab-org-medium
  script:
    - echo " Lancement des tests..."
    - mvn test
  allow_failure: true

package:
  stage: package
  image: maven:3.9.6-eclipse-temurin-17
  tags:
    - gitlab-org-medium
  script:
    - echo " Packaging de l'application..."
    - mvn clean package -DskipTests
  artifacts:
    paths:
      - target/*.jar
    expire_in: 1 week
```

### 5. Pusher le fichier CI

```powershell
git add .gitlab-ci.yml
git commit -m "Add GitLab CI configuration"
git push origin main
```

---

##  Configuration des Runners

1. Aller dans **Settings** → **CI/CD** → **Runners**
2. Cliquer sur **"Other available project runners"**
3. Activer un runner avec le tag `gitlab-org-medium`

---

##  Vérification du Pipeline

1. Aller dans **CI/CD** → **Pipelines**
2. Vérifier que les 3 stages passent au vert :

```
build  → test  → package 
```

### Relancer un pipeline manuellement

```powershell
git commit --allow-empty -m "Trigger CI pipeline"
git push origin main
```

Ou depuis GitLab : **CI/CD** → **Pipelines** → **"Run pipeline"**

---

##  Stages du Pipeline

| Stage | Description | Image Docker |
|-------|-------------|--------------|
| build | Compilation du code | maven:3.9.6-eclipse-temurin-17 |
| test | Exécution des tests | maven:3.9.6-eclipse-temurin-17 |
| package | Création du JAR | maven:3.9.6-eclipse-temurin-17 |

---

##  Structure du projet

```
studentapi/
│
├── .gitlab-ci.yml
├── .gitignore
├── pom.xml
│
└── src/
    └── main/
        ├── java/com/tp3/
        │   ├── StudentApiApplication.java
        │   ├── model/Student.java
        │   ├── repository/StudentRepository.java
        │   └── controller/StudentController.java
        └── resources/
            └── application.properties
```
