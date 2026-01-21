# 🎨 Guide Complet - Application Web Namaaz Dining

## 📋 Vue d'ensemble

Application web JSF (Jakarta Faces 4.0) unifiée qui consomme les 3 microservices REST :
- **Service Menu** : Gestion des catégories et articles
- **Service Reservations** : Gestion des tables
- **Service Clients/Orders** : Gestion des clients et commandes

**Stack technique** :
- Jakarta EE 10
- JSF 4.0 (Jakarta Faces)
- JAX-RS Client API
- Tailwind CSS (CDN)
- GlassFish 7

**Design** : Palette rouge, noir et blanc

---

## 📁 Structure du projet

```
namaaz-dining-webapp/
├── src/main/java/com/namaaz/webapp/
│   ├── dto/                    # Data Transfer Objects
│   │   ├── ClientDTO.java
│   │   ├── CategoryDTO.java
│   │   ├── MenuItemDTO.java
│   │   ├── RestaurantTableDTO.java
│   │   ├── OrderDTO.java
│   │   └── OrderItemDTO.java
│   │
│   ├── client/                 # REST API Clients
│   │   ├── MenuClient.java
│   │   ├── ReservationClient.java
│   │   └── OrderClient.java
│   │
│   └── bean/                   # JSF Managed Beans
│       ├── CategoryBean.java
│       ├── MenuBean.java
│       ├── TableBean.java
│       ├── ClientBean.java
│       └── OrderBean.java
│
├── src/main/webapp/
│   ├── WEB-INF/
│   │   ├── web.xml            # Configuration JSF
│   │   ├── faces-config.xml   # Locale FR/EN
│   │   ├── beans.xml          # CDI
│   │   └── templates/
│   │       └── layout.xhtml   # Template principal
│   │
│   ├── index.xhtml            # Dashboard
│   ├── categories.xhtml       # Gestion catégories
│   ├── menu-items.xhtml       # Gestion articles
│   ├── tables.xhtml           # Gestion tables
│   ├── clients.xhtml          # Gestion clients
│   └── orders.xhtml           # Gestion commandes
│
└── pom.xml
```

---

## 🚀 Déploiement

### Prérequis
✅ Les 3 microservices doivent être déployés et actifs sur GlassFish :
- `http://localhost:8080/service-menu-1.0/api/`
- `http://localhost:8080/service-reservations-1.0/api/`
- `http://localhost:8080/service-clients-orders-1.0/api/`

### Étapes de déploiement

1. **Build le projet** :
```bash
cd "d:\ALMA\Gael\STUDIES\DEV APP ENTREPRISE\REST-NAMAAZ-DINING\namaaz-dining-webapp"
mvn clean package
```

2. **Déployer sur GlassFish** :
   - Via NetBeans : Clic droit sur le projet → **Run**
   - Ou manuellement : Copier `target/namaaz-dining-webapp-1.0.war` dans `glassfish7/glassfish/domains/domain1/autodeploy/`

3. **Accéder à l'application** :
```
http://localhost:8080/namaaz-dining-webapp-1.0/
```

---

## 🎯 Fonctionnalités par page

### 1. 🏠 **Dashboard** (`index.xhtml`)
- Vue d'ensemble du système
- Cartes statistiques (Menu, Tables, Commandes)
- Actions rapides vers chaque module
- Design : Gradient rouge-noir

### 2. 📂 **Catégories** (`categories.xhtml`)
- Ajouter une nouvelle catégorie
- Liste de toutes les catégories
- Supprimer une catégorie
- **Champs** : Nom, Description

### 3. 🍽️ **Articles du Menu** (`menu-items.xhtml`)
- Ajouter un nouvel article
- Filtrer par catégorie
- Affichage en grille (cards)
- Badge de disponibilité (vert/gris)
- **Champs** : Nom, Prix, Catégorie, Description, Disponible

### 4. 🪑 **Tables** (`tables.xhtml`)
- Ajouter une nouvelle table
- Filtrer par statut (FREE, RESERVED, OCCUPIED, OUT_OF_SERVICE)
- Affichage en grille
- Badge de statut coloré :
  - 🟢 FREE (vert)
  - 🔵 RESERVED (bleu)
  - 🔴 OCCUPIED (rouge)
  - ⚫ OUT_OF_SERVICE (gris)
- **Champs** : Numéro, Places, Emplacement, Statut

### 5. 👥 **Clients** (`clients.xhtml`)
- Ajouter un nouveau client
- Liste de tous les clients (tableau)
- Supprimer un client
- **Champs** : Prénom, Nom, Email, Téléphone, Adresse

### 6. 📋 **Commandes** (`orders.xhtml`)
- Créer une nouvelle commande
- Sélectionner un client
- Ajouter des articles avec quantité
- Calcul automatique du total
- Liste des commandes avec badge de statut :
  - 🔵 NEW (bleu)
  - 🟡 IN_PROGRESS (jaune)
  - 🟢 COMPLETED (vert)
  - 🔴 CANCELLED (rouge)
- **Workflow** :
  1. Sélectionner client
  2. Ajouter articles (menu déroulant + quantité)
  3. Voir récapitulatif avec total
  4. Créer la commande

---

## 🎨 Design Tailwind CSS

### Palette de couleurs
- **Rouge principal** : `#DC2626` (`bg-red-600`, `text-red-600`)
- **Noir** : `#000000` (`bg-black`)
- **Blanc** : `#FFFFFF` (`bg-white`, `text-white`)
- **Gris** : Pour backgrounds et bordures

### Composants stylisés

**Boutons** :
```html
<!-- Bouton rouge -->
<button class="bg-red-600 hover:bg-red-700 text-white font-semibold py-2 px-6 rounded-lg transition-colors">

<!-- Bouton noir -->
<button class="bg-black hover:bg-gray-800 text-white font-semibold py-2 px-6 rounded-lg transition-colors">
```

**Cards** :
```html
<div class="bg-white rounded-lg shadow-md p-6 border-l-4 border-red-600 hover:shadow-lg transition-shadow">
```

**Badges** :
```html
<span class="bg-red-600 text-white text-xs font-semibold px-3 py-1 rounded-full">
```

**Navigation** :
- Sidebar noir avec liens gris/blanc
- Hover : bordure rouge à gauche
- Active : background rouge semi-transparent

---

## 🧪 Tests fonctionnels

### Scénario complet

#### 1. **Initialiser les données**
1. Créer 2-3 catégories (Entrées, Plats, Desserts)
2. Créer 5-6 articles de menu dans différentes catégories
3. Créer 3-4 tables avec différents statuts
4. Créer 2-3 clients

#### 2. **Tester le workflow commande**
1. Aller sur "Commandes"
2. Sélectionner un client
3. Ajouter 2-3 articles avec quantités
4. Vérifier le calcul du total
5. Créer la commande
6. Vérifier qu'elle apparaît dans la liste

#### 3. **Tester les filtres**
- Menu items : Filtrer par catégorie
- Tables : Filtrer par statut

#### 4. **Tester les CRUD**
- Créer, modifier (si implémenté), supprimer pour chaque entité
- Vérifier les messages de succès/erreur

---

## 🐛 Troubleshooting

### Problème : Page blanche
**Solution** :
1. Vérifier les logs GlassFish : `glassfish7/glassfish/domains/domain1/logs/server.log`
2. Vérifier que les 3 microservices sont déployés et actifs
3. Tester manuellement les API REST avec Postman

### Problème : Erreur 404 sur les pages
**Solution** :
- Vérifier que l'URL contient `.xhtml` : `http://localhost:8080/namaaz-dining-webapp-1.0/index.xhtml`
- Vérifier `web.xml` : mapping JSF sur `*.xhtml`

### Problème : Données ne s'affichent pas
**Solution** :
1. Vérifier les URLs des services dans les clients REST :
   - `MenuClient.java` : `http://localhost:8080/service-menu-1.0/api`
   - `ReservationClient.java` : `http://localhost:8080/service-reservations-1.0/api`
   - `OrderClient.java` : `http://localhost:8080/service-clients-orders-1.0/api`
2. Tester directement les endpoints REST
3. Vérifier les logs pour exceptions

### Problème : Styles Tailwind ne s'appliquent pas
**Solution** :
- Vérifier la connexion Internet (Tailwind CSS via CDN)
- Vérifier dans `layout.xhtml` : `<script src="https://cdn.tailwindcss.com"></script>`

### Problème : Erreur CDI / Injection
**Solution** :
- Vérifier que `beans.xml` existe dans `WEB-INF/`
- Vérifier les annotations `@Named` et `@ViewScoped` sur les beans

---

## 📊 Architecture technique

### Communication REST
```
┌─────────────────┐
│  JSF Web App    │
│  (Port 8080)    │
└────────┬────────┘
         │
         ├──────────────────┐
         │                  │
         ▼                  ▼
┌──────────────┐   ┌──────────────┐
│ MenuClient   │   │ OrderClient  │
└──────┬───────┘   └──────┬───────┘
       │                  │
       ▼                  ▼
┌──────────────┐   ┌──────────────┐
│service-menu  │   │service-orders│
│   REST API   │   │   REST API   │
└──────────────┘   └──────────────┘
```

### Cycle de vie JSF
1. **User Request** → `*.xhtml`
2. **FacesServlet** → Traite la requête
3. **Managed Bean** → Appelle REST Client
4. **REST Client** → Appelle microservice
5. **Response** → Rendu JSF → HTML + Tailwind

---

## 🎓 Points clés

### Bonnes pratiques implémentées
✅ **Séparation des préoccupations** : DTOs, Clients, Beans, Pages
✅ **Architecture microservices** : Communication via REST
✅ **Design responsive** : Tailwind CSS avec grille adaptative
✅ **UX** : Messages de feedback, confirmations de suppression
✅ **CDI** : Injection de dépendances pour les clients REST
✅ **Scope ViewScoped** : État conservé pendant la navigation

### Technologies Jakarta EE utilisées
- **JSF (Faces)** : Framework UI
- **CDI** : Injection de dépendances
- **JAX-RS Client** : Consommation API REST
- **Bean Validation** : (dans les services backend)

---

## 📝 Améliorations futures

### Suggestions
1. **Sécurité** :
   - Ajouter authentification (JAAS, JWT)
   - Rôles utilisateurs (Admin, Serveur, Caissier)

2. **Fonctionnalités** :
   - Module Réservations complet (formulaire + calendrier)
   - Module Paiements
   - Dashboard avec vraies statistiques (nombre total, CA, etc.)
   - Recherche et pagination

3. **UI/UX** :
   - Modals pour édition (au lieu de formulaires)
   - Notifications toast
   - Animations (Alpine.js ou HTMX)
   - Mode sombre

4. **Technique** :
   - Cache pour réduire appels REST
   - Gestion d'erreurs HTTP plus fine
   - Tests unitaires (JUnit) et E2E (Selenium)
   - Internationalisation (i18n) FR/EN/AR

---

## ✅ Checklist de validation

Avant de considérer le projet terminé :

- [ ] Les 3 microservices sont déployés et testés
- [ ] L'application web se déploie sans erreur
- [ ] Dashboard s'affiche correctement
- [ ] CRUD Catégories fonctionne
- [ ] CRUD Articles fonctionne (avec filtre)
- [ ] CRUD Tables fonctionne (avec filtre et badges colorés)
- [ ] CRUD Clients fonctionne
- [ ] Création de commande complète fonctionne
- [ ] Calcul du total est correct
- [ ] Tous les styles Tailwind s'appliquent
- [ ] Navigation entre pages fonctionne
- [ ] Messages de succès/erreur s'affichent

---

## 🎉 Félicitations !

Vous avez maintenant une application web complète de gestion de restaurant intégrant :
- ✅ 3 microservices REST indépendants
- ✅ Une interface web unifiée et moderne
- ✅ Architecture Jakarta EE 10
- ✅ Design professionnel Tailwind CSS

**Prochaines étapes** : Déployer, tester et améliorer selon les besoins réels du restaurant Namaaz Dining !
