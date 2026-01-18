# Guide Complet : Implémentation du Service Menu

## 📋 Table des matières

1. [Phase 1 : Configuration NetBeans](#phase-1--configuration-netbeans)
2. [Phase 2 : Implémentation dans VS Code](#phase-2--implémentation-dans-vs-code)
3. [Phase 3 : Tests complets](#phase-3--tests-complets)
4. [Résumé des fichiers créés](#résumé-des-fichiers-créés)

---

## Phase 1 : Configuration NetBeans

### Étape 1 : Créer le projet Jakarta EE

1. Ouvrez **NetBeans IDE**
2. Menu : **File → New Project** (Ctrl+Shift+N)
3. Catégorie : **Java with Maven**
4. Projet : **Web Application**
5. Cliquez **Next**

### Étape 2 : Configurer le projet

- **Project Name** : `service-menu`
- **Project Location** : `d:\ALMA\Gael\STUDIES\DEV APP ENTREPRISE\REST-NAMAAZ-DINING\`
- **Group Id** : `com.namaaz`
- **Artifact Id** : `service-menu`
- **Version** : `1.0-SNAPSHOT`
- **Package** : `com.namaaz.service.menu`

Cliquez **Next**

### Étape 3 : Sélectionner le serveur et Jakarta EE

- **Server** : **GlassFish Server**
- **Java EE Version** : **Jakarta EE 10 Web** (ou 9.1)
- **Context Path** : `/service-menu`

Cliquez **Finish**

### Étape 4 : Configurer pom.xml

Ouvrez **pom.xml** et ajoutez ces dépendances dans `<dependencies>` :

```xml
<dependency>
    <groupId>jakarta.platform</groupId>
    <artifactId>jakarta.jakartaee-api</artifactId>
    <version>10.0.0</version>
    <scope>provided</scope>
</dependency>

<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.1</version>
</dependency>
```

**Sauvegardez** et lancez **Clean and Build**

### Étape 5 : Configuration de la base de données (Prisma)

**Informations de connexion** :
- **Host** : `db.prisma.io`
- **Port** : `5432`
- **Database** : `postgres`
- **Username** : `69610a9ad49790a1cc872aa4ab648a6156e1610f614a220e523184b7e588bd51`
- **Password** : `sk_y46DIcbEp4skbLA3T_Zrm`
- **SSL** : Requis

### Étape 6 : Configurer DataSource JNDI

Créez le fichier : **src/main/webapp/WEB-INF/glassfish-resources.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE resources PUBLIC "-//GlassFish.org//DTD GlassFish Application Server 3.1 Resource Definitions//EN" 
    "http://glassfish.org/dtds/glassfish-resources_1_5.dtd">
<resources>
    <jdbc-connection-pool 
        name="MenuPool" 
        res-type="javax.sql.DataSource" 
        datasource-classname="org.postgresql.ds.PGSimpleDataSource">
        <property name="serverName" value="db.prisma.io"/>
        <property name="portNumber" value="5432"/>
        <property name="databaseName" value="postgres"/>
        <property name="user" value="69610a9ad49790a1cc872aa4ab648a6156e1610f614a220e523184b7e588bd51"/>
        <property name="password" value="sk_y46DIcbEp4skbLA3T_Zrm"/>
        <property name="ssl" value="true"/>
        <property name="sslmode" value="require"/>
    </jdbc-connection-pool>
    
    <jdbc-resource 
        enabled="true" 
        jndi-name="jdbc/namaaz_menu" 
        pool-name="MenuPool"/>
</resources>
```

### Étape 7 : Créer persistence.xml

Créez : **src/main/resources/META-INF/persistence.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="3.0" 
    xmlns="https://jakarta.ee/xml/ns/persistence">
    <persistence-unit name="MenuPU" transaction-type="JTA">
        <jta-data-source>jdbc/namaaz_menu</jta-data-source>
        <properties>
            <property name="jakarta.persistence.schema-generation.database.action" value="none"/>
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.format_sql" value="true"/>
        </properties>
    </persistence-unit>
</persistence>
```

### Étape 8 : Créer les packages

Créez ces packages dans **Source Packages** :
- `com.namaaz.service.menu.entities`
- `com.namaaz.service.menu.business`
- `com.namaaz.service.menu.bean`
- `com.namaaz.service.menu.rest`
- `com.namaaz.service.menu.repository`

### Étape 9 : Créer répertoire migrations

Dans **src/main/resources**, créez les dossiers : **db/migration/**

### Étape 10 : Ajouter le driver PostgreSQL à GlassFish

1. Téléchargez **postgresql-42.7.1.jar** depuis votre projet Maven
2. Copiez-le dans : `C:\glassfish7\glassfish\domains\domain1\lib\`
3. Redémarrez GlassFish

### Étape 11 : Vérification finale

- Lancez **Clean and Build** → BUILD SUCCESS ✅
- Démarrez GlassFish → Status green ✅

---

## Phase 2 : Implémentation dans VS Code

### Étape 12 : Ouvrir le projet dans VS Code

1. Ouvrez **VS Code**
2. **File → Open Folder**
3. Sélectionnez : `service-menu`

### Étape 13 : Créer les entités JPA

#### **Fichier 1 : Category.java**

Créez : `src/main/java/com/namaaz/service/menu/entities/Category.java`

```java
package com.namaaz.service.menu.entities;

import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "category")
public class Category {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
    
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
    
    // Constructeurs
    public Category() {
    }
    
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
    
    // Getters et Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    
    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
```

#### **Fichier 2 : MenuItem.java**

Créez : `src/main/java/com/namaaz/service/menu/entities/MenuItem.java`

```java
package com.namaaz.service.menu.entities;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "menu_item")
public class MenuItem {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @NotBlank(message = "Le nom du plat est obligatoire")
    @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
    @Column(nullable = false, length = 150)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;
    
    @NotNull(message = "La catégorie est obligatoire")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Column(nullable = false)
    private Boolean available = true;
    
    @Column(name = "prep_time")
    private Integer prepTime;
    
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
    
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
    
    // Constructeurs
    public MenuItem() {
    }
    
    public MenuItem(String name, String description, BigDecimal price, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
    
    // Getters et Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public Boolean getAvailable() {
        return available;
    }
    
    public void setAvailable(Boolean available) {
        this.available = available;
    }
    
    public Integer getPrepTime() {
        return prepTime;
    }
    
    public void setPrepTime(Integer prepTime) {
        this.prepTime = prepTime;
    }
    
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    
    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
```

### Étape 14 : Créer la migration SQL

Créez : `src/main/resources/db/migration/V1__create_menu_tables.sql`

```sql
-- Migration V1 : tables Menu
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE category (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name VARCHAR(100) NOT NULL UNIQUE,
  description TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE menu_item (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name VARCHAR(150) NOT NULL,
  description TEXT,
  price NUMERIC(10,2) NOT NULL,
  category_id UUID NOT NULL REFERENCES category(id),
  available BOOLEAN NOT NULL DEFAULT true,
  prep_time INT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE INDEX idx_menu_item_category ON menu_item(category_id);
CREATE INDEX idx_menu_item_available ON menu_item(available);
```

### Étape 15 : Créer les Repositories

#### **Fichier 1 : CategoryRepository.java**

Créez : `src/main/java/com/namaaz/service/menu/repository/CategoryRepository.java`

```java
package com.namaaz.service.menu.repository;

import com.namaaz.service.menu.entities.Category;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CategoryRepository {
    
    @PersistenceContext(unitName = "MenuPU")
    private EntityManager em;
    
    public Category save(Category category) {
        if (category.getId() == null) {
            em.persist(category);
            return category;
        } else {
            return em.merge(category);
        }
    }
    
    public Optional<Category> findById(UUID id) {
        Category category = em.find(Category.class, id);
        return Optional.ofNullable(category);
    }
    
    public List<Category> findAll() {
        TypedQuery<Category> query = em.createQuery(
            "SELECT c FROM Category c ORDER BY c.name", Category.class);
        return query.getResultList();
    }
    
    public Optional<Category> findByName(String name) {
        TypedQuery<Category> query = em.createQuery(
            "SELECT c FROM Category c WHERE c.name = :name", Category.class);
        query.setParameter("name", name);
        return query.getResultStream().findFirst();
    }
    
    public void delete(Category category) {
        if (!em.contains(category)) {
            category = em.merge(category);
        }
        em.remove(category);
    }
}
```

#### **Fichier 2 : MenuItemRepository.java**

Créez : `src/main/java/com/namaaz/service/menu/repository/MenuItemRepository.java`

```java
package com.namaaz.service.menu.repository;

import com.namaaz.service.menu.entities.MenuItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class MenuItemRepository {
    
    @PersistenceContext(unitName = "MenuPU")
    private EntityManager em;
    
    public MenuItem save(MenuItem menuItem) {
        if (menuItem.getId() == null) {
            em.persist(menuItem);
            return menuItem;
        } else {
            return em.merge(menuItem);
        }
    }
    
    public Optional<MenuItem> findById(UUID id) {
        MenuItem menuItem = em.find(MenuItem.class, id);
        return Optional.ofNullable(menuItem);
    }
    
    public List<MenuItem> findAll() {
        TypedQuery<MenuItem> query = em.createQuery(
            "SELECT m FROM MenuItem m ORDER BY m.name", MenuItem.class);
        return query.getResultList();
    }
    
    public List<MenuItem> findByCategory(UUID categoryId) {
        TypedQuery<MenuItem> query = em.createQuery(
            "SELECT m FROM MenuItem m WHERE m.category.id = :categoryId ORDER BY m.name", 
            MenuItem.class);
        query.setParameter("categoryId", categoryId);
        return query.getResultList();
    }
    
    public List<MenuItem> findByAvailability(Boolean available) {
        TypedQuery<MenuItem> query = em.createQuery(
            "SELECT m FROM MenuItem m WHERE m.available = :available ORDER BY m.name", 
            MenuItem.class);
        query.setParameter("available", available);
        return query.getResultList();
    }
    
    public void delete(MenuItem menuItem) {
        if (!em.contains(menuItem)) {
            menuItem = em.merge(menuItem);
        }
        em.remove(menuItem);
    }
}
```

### Étape 16 : Créer le Service métier

Créez : `src/main/java/com/namaaz/service/menu/business/MenuService.java`

```java
package com.namaaz.service.menu.business;

import com.namaaz.service.menu.entities.Category;
import com.namaaz.service.menu.entities.MenuItem;
import com.namaaz.service.menu.repository.CategoryRepository;
import com.namaaz.service.menu.repository.MenuItemRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Stateless
public class MenuService {
    
    @Inject
    private CategoryRepository categoryRepository;
    
    @Inject
    private MenuItemRepository menuItemRepository;
    
    // === CATEGORY OPERATIONS ===
    
    @Transactional
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }
    
    public Optional<Category> getCategoryById(UUID id) {
        return categoryRepository.findById(id);
    }
    
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    @Transactional
    public Optional<Category> updateCategory(UUID id, Category updatedCategory) {
        Optional<Category> existing = categoryRepository.findById(id);
        if (existing.isPresent()) {
            Category category = existing.get();
            category.setName(updatedCategory.getName());
            category.setDescription(updatedCategory.getDescription());
            return Optional.of(categoryRepository.save(category));
        }
        return Optional.empty();
    }
    
    @Transactional
    public boolean deleteCategory(UUID id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            categoryRepository.delete(category.get());
            return true;
        }
        return false;
    }
    
    // === MENU ITEM OPERATIONS ===
    
    @Transactional
    public MenuItem createMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }
    
    public Optional<MenuItem> getMenuItemById(UUID id) {
        return menuItemRepository.findById(id);
    }
    
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }
    
    public List<MenuItem> getMenuItemsByCategory(UUID categoryId) {
        return menuItemRepository.findByCategory(categoryId);
    }
    
    public List<MenuItem> getAvailableMenuItems() {
        return menuItemRepository.findByAvailability(true);
    }
    
    @Transactional
    public Optional<MenuItem> updateMenuItem(UUID id, MenuItem updatedMenuItem) {
        Optional<MenuItem> existing = menuItemRepository.findById(id);
        if (existing.isPresent()) {
            MenuItem menuItem = existing.get();
            menuItem.setName(updatedMenuItem.getName());
            menuItem.setDescription(updatedMenuItem.getDescription());
            menuItem.setPrice(updatedMenuItem.getPrice());
            menuItem.setCategory(updatedMenuItem.getCategory());
            menuItem.setAvailable(updatedMenuItem.getAvailable());
            menuItem.setPrepTime(updatedMenuItem.getPrepTime());
            return Optional.of(menuItemRepository.save(menuItem));
        }
        return Optional.empty();
    }
    
    @Transactional
    public boolean deleteMenuItem(UUID id) {
        Optional<MenuItem> menuItem = menuItemRepository.findById(id);
        if (menuItem.isPresent()) {
            menuItemRepository.delete(menuItem.get());
            return true;
        }
        return false;
    }
}
```

### Étape 17 : Créer les Endpoints REST

#### **Fichier 1 : RestApplication.java**

Créez : `src/main/java/com/namaaz/service/menu/rest/RestApplication.java`

```java
package com.namaaz.service.menu.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class RestApplication extends Application {
    // Configuration JAX-RS
}
```

#### **Fichier 2 : CategoryResource.java**

Créez : `src/main/java/com/namaaz/service/menu/rest/CategoryResource.java`

```java
package com.namaaz.service.menu.rest;

import com.namaaz.service.menu.business.MenuService;
import com.namaaz.service.menu.entities.Category;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {
    
    @Inject
    private MenuService menuService;
    
    @GET
    public Response getAllCategories() {
        List<Category> categories = menuService.getAllCategories();
        return Response.ok(categories).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getCategoryById(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return menuService.getCategoryById(uuid)
                .map(category -> Response.ok(category).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
    
    @POST
    public Response createCategory(@Valid Category category) {
        Category created = menuService.createCategory(category);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
    
    @PUT
    @Path("/{id}")
    public Response updateCategory(@PathParam("id") String id, @Valid Category category) {
        try {
            UUID uuid = UUID.fromString(id);
            return menuService.updateCategory(uuid, category)
                .map(updated -> Response.ok(updated).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteCategory(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            boolean deleted = menuService.deleteCategory(uuid);
            if (deleted) {
                return Response.noContent().build();
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
}
```

#### **Fichier 3 : MenuResource.java**

Créez : `src/main/java/com/namaaz/service/menu/rest/MenuResource.java`

```java
package com.namaaz.service.menu.rest;

import com.namaaz.service.menu.business.MenuService;
import com.namaaz.service.menu.entities.MenuItem;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/menu-items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MenuResource {
    
    @Inject
    private MenuService menuService;
    
    @GET
    public Response getAllMenuItems(
            @QueryParam("categoryId") String categoryId,
            @QueryParam("available") Boolean available) {
        
        List<MenuItem> menuItems;
        
        if (categoryId != null) {
            try {
                UUID categoryUuid = UUID.fromString(categoryId);
                menuItems = menuService.getMenuItemsByCategory(categoryUuid);
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid category UUID format\"}").build();
            }
        } else if (available != null && available) {
            menuItems = menuService.getAvailableMenuItems();
        } else {
            menuItems = menuService.getAllMenuItems();
        }
        
        return Response.ok(menuItems).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getMenuItemById(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return menuService.getMenuItemById(uuid)
                .map(menuItem -> Response.ok(menuItem).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
    
    @POST
    public Response createMenuItem(@Valid MenuItem menuItem) {
        try {
            MenuItem created = menuService.createMenuItem(menuItem);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response updateMenuItem(@PathParam("id") String id, @Valid MenuItem menuItem) {
        try {
            UUID uuid = UUID.fromString(id);
            return menuService.updateMenuItem(uuid, menuItem)
                .map(updated -> Response.ok(updated).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteMenuItem(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            boolean deleted = menuService.deleteMenuItem(uuid);
            if (deleted) {
                return Response.noContent().build();
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
}
```

### Étape 18 : Exécuter le script SQL

1. Ouvrez **pgAdmin**
2. Créez une connexion à `db.prisma.io` (avec SSL requis)
3. Ouvrez **Query Tool** sur la base `postgres`
4. Exécutez le contenu du fichier `V1__create_menu_tables.sql`
5. Vérifiez que les tables `category` et `menu_item` sont créées

### Étape 19 : Build et déploiement

1. Dans **NetBeans**, clic droit sur **service-menu** → **Clean and Build**
2. Vérifiez : **BUILD SUCCESS** ✅
3. Clic droit → **Run**
4. Vérifiez : Déploiement réussi ✅

---

## Phase 3 : Tests complets

### Test 1 : Créer des catégories

Utilisez **Postman** avec ces requêtes :

**POST** `http://localhost:8080/service-menu/api/categories`

Créez 4 catégories :
1. **Entrées** - "Plats d'entrée pour commencer le repas"
2. **Plats principaux** - "Plats de résistance"
3. **Desserts** - "Douceurs sucrées"
4. **Boissons** - "Boissons chaudes et froides"

### Test 2 : Lister toutes les catégories

**GET** `http://localhost:8080/service-menu/api/categories`

**Résultat attendu** : Array de 4 catégories

### Test 3 : Créer des plats

Créez 6 plats dans les différentes catégories :
1. Salade César (Entrée) - 12.50
2. Soupe à l'oignon (Entrée) - 9.00
3. Steak frites (Plat principal) - 24.00
4. Saumon grillé (Plat principal) - 22.50
5. Tiramisu (Dessert) - 8.00
6. Tarte tatin (Dessert - Indisponible) - 7.50

### Test 4 : Lister tous les plats

**GET** `http://localhost:8080/service-menu/api/menu-items`

### Test 5 : Filtrer les plats disponibles

**GET** `http://localhost:8080/service-menu/api/menu-items?available=true`

**Résultat attendu** : 5 plats (sans la Tarte tatin)

### Test 6 : Filtrer par catégorie

**GET** `http://localhost:8080/service-menu/api/menu-items?categoryId={ID_ENTREES}`

**Résultat attendu** : 2 entrées

### Test 7 : Modifier un plat

**PUT** `http://localhost:8080/service-menu/api/menu-items/{ID_TIRAMISU}`

Changez le prix à 9.00

### Test 8 : Supprimer un plat

**DELETE** `http://localhost:8080/service-menu/api/menu-items/{ID_TARTE_TATIN}`

---

## Résumé des fichiers créés

### Entités JPA (2 fichiers)
- `src/main/java/com/namaaz/service/menu/entities/Category.java`
- `src/main/java/com/namaaz/service/menu/entities/MenuItem.java`

### Repositories (2 fichiers)
- `src/main/java/com/namaaz/service/menu/repository/CategoryRepository.java`
- `src/main/java/com/namaaz/service/menu/repository/MenuItemRepository.java`

### Service métier (1 fichier)
- `src/main/java/com/namaaz/service/menu/business/MenuService.java`

### Endpoints REST (3 fichiers)
- `src/main/java/com/namaaz/service/menu/rest/RestApplication.java`
- `src/main/java/com/namaaz/service/menu/rest/CategoryResource.java`
- `src/main/java/com/namaaz/service/menu/rest/MenuResource.java`

### Configuration (3 fichiers)
- `src/main/resources/META-INF/persistence.xml`
- `src/main/webapp/WEB-INF/glassfish-resources.xml`
- `src/main/resources/db/migration/V1__create_menu_tables.sql`

### Total : 11 fichiers

---

## ✅ Endpoints disponibles

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/categories` | Lister toutes les catégories |
| GET | `/api/categories/{id}` | Récupérer une catégorie |
| POST | `/api/categories` | Créer une catégorie |
| PUT | `/api/categories/{id}` | Modifier une catégorie |
| DELETE | `/api/categories/{id}` | Supprimer une catégorie |
| GET | `/api/menu-items` | Lister tous les plats |
| GET | `/api/menu-items?categoryId={uuid}` | Filtrer par catégorie |
| GET | `/api/menu-items?available=true` | Plats disponibles |
| GET | `/api/menu-items/{id}` | Récupérer un plat |
| POST | `/api/menu-items` | Créer un plat |
| PUT | `/api/menu-items/{id}` | Modifier un plat |
| DELETE | `/api/menu-items/{id}` | Supprimer un plat |

---

## 🎯 Prochaines étapes

1. Service Réservations (même approche)
2. Service Clients/Commandes/Paiements
3. Intégration inter-services via API REST

**Fin du guide — Service Menu implémenté avec succès ! ✅**
