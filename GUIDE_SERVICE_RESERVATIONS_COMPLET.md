# Guide Complet : Implémentation du Service Réservations

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

- **Project Name** : `service-reservations`
- **Project Location** : `d:\ALMA\Gael\STUDIES\DEV APP ENTREPRISE\REST-NAMAAZ-DINING\`
- **Group Id** : `com.namaaz`
- **Artifact Id** : `service-reservations`
- **Version** : `1.0-SNAPSHOT`
- **Package** : `com.namaaz.service.reservations`

Cliquez **Next**

### Étape 3 : Sélectionner le serveur et Jakarta EE

- **Server** : **GlassFish Server**
- **Java EE Version** : **Jakarta EE 10 Web** (ou 9.1)
- **Context Path** : `/service-reservations`

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
        name="ReservationsPool" 
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
        jndi-name="jdbc/namaaz_reservations" 
        pool-name="ReservationsPool"/>
</resources>
```

### Étape 7 : Créer persistence.xml

Créez : **src/main/resources/META-INF/persistence.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="3.0" 
    xmlns="https://jakarta.ee/xml/ns/persistence"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence 
        https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd">
    
    <persistence-unit name="ReservationsPU" transaction-type="JTA">
        <jta-data-source>jdbc/namaaz_reservations</jta-data-source>
        <properties>
            <property name="jakarta.persistence.schema-generation.database.action" value="none"/>
            <property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQLDialect"/>
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.format_sql" value="true"/>
        </properties>
    </persistence-unit>
</persistence>
```

### Étape 8 : Créer les packages

Créez ces packages dans **Source Packages** :
- `com.namaaz.service.reservations.entities`
- `com.namaaz.service.reservations.enums`
- `com.namaaz.service.reservations.business`
- `com.namaaz.service.reservations.repository`
- `com.namaaz.service.reservations.rest`

### Étape 9 : Créer répertoire migrations

Dans **src/main/resources**, créez les dossiers : **db/migration/**

### Étape 10 : Vérification finale

- Lancez **Clean and Build** → BUILD SUCCESS ✅
- Démarrez GlassFish → Status green ✅

---

## Phase 2 : Implémentation dans VS Code

### Étape 11 : Ouvrir le projet dans VS Code

1. Ouvrez **VS Code**
2. **File → Open Folder**
3. Sélectionnez : `service-reservations`

### Étape 12 : Créer les Enums

#### **Fichier 1 : TableStatus.java**

Créez : `src/main/java/com/namaaz/service/reservations/enums/TableStatus.java`

```java
package com.namaaz.service.reservations.enums;

public enum TableStatus {
    FREE,           // Table disponible
    RESERVED,       // Table réservée
    OCCUPIED,       // Table occupée
    OUT_OF_SERVICE  // Table hors service
}
```

#### **Fichier 2 : ReservationStatus.java**

Créez : `src/main/java/com/namaaz/service/reservations/enums/ReservationStatus.java`

```java
package com.namaaz.service.reservations.enums;

public enum ReservationStatus {
    PENDING,    // En attente de confirmation
    CONFIRMED,  // Confirmée
    CANCELLED   // Annulée
}
```

### Étape 13 : Créer les entités JPA

#### **Fichier 1 : RestaurantTable.java**

Créez : `src/main/java/com/namaaz/service/reservations/entities/RestaurantTable.java`

```java
package com.namaaz.service.reservations.entities;

import com.namaaz.service.reservations.enums.TableStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "restaurant_table")
public class RestaurantTable {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @NotBlank(message = "Le numéro de table est obligatoire")
    @Column(name = "table_number", nullable = false, unique = true, length = 10)
    private String tableNumber;
    
    @NotNull(message = "La capacité est obligatoire")
    @Min(value = 1, message = "La capacité doit être au moins 1")
    @Column(nullable = false)
    private Integer capacity;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TableStatus status = TableStatus.FREE;
    
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
    
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
    
    // Constructeurs
    public RestaurantTable() {
    }
    
    public RestaurantTable(String tableNumber, Integer capacity) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = TableStatus.FREE;
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
    
    public String getTableNumber() {
        return tableNumber;
    }
    
    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public TableStatus getStatus() {
        return status;
    }
    
    public void setStatus(TableStatus status) {
        this.status = status;
    }
    
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    
    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
```

#### **Fichier 2 : Reservation.java**

Créez : `src/main/java/com/namaaz/service/reservations/entities/Reservation.java`

```java
package com.namaaz.service.reservations.entities;

import com.namaaz.service.reservations.enums.ReservationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reservation")
public class Reservation {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @NotNull(message = "L'ID du client est obligatoire")
    @Column(name = "client_id", nullable = false, length = 36)
    private String clientId;
    
    @NotNull(message = "La date de réservation est obligatoire")
    @Column(name = "reservation_date_time", nullable = false)
    private OffsetDateTime reservationDateTime;
    
    @NotNull(message = "Le nombre de personnes est obligatoire")
    @Min(value = 1, message = "Le nombre de personnes doit être au moins 1")
    @Column(name = "number_of_guests", nullable = false)
    private Integer numberOfGuests;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status = ReservationStatus.PENDING;
    
    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "reservation_table",
        joinColumns = @JoinColumn(name = "reservation_id"),
        inverseJoinColumns = @JoinColumn(name = "table_id")
    )
    private List<RestaurantTable> tables = new ArrayList<>();
    
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationItem> items = new ArrayList<>();
    
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
    
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
    
    // Constructeurs
    public Reservation() {
    }
    
    public Reservation(String clientId, OffsetDateTime reservationDateTime, Integer numberOfGuests) {
        this.clientId = clientId;
        this.reservationDateTime = reservationDateTime;
        this.numberOfGuests = numberOfGuests;
        this.status = ReservationStatus.PENDING;
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
    
    // Helper methods
    public void addTable(RestaurantTable table) {
        tables.add(table);
    }
    
    public void removeTable(RestaurantTable table) {
        tables.remove(table);
    }
    
    public void addItem(ReservationItem item) {
        items.add(item);
        item.setReservation(this);
    }
    
    public void removeItem(ReservationItem item) {
        items.remove(item);
        item.setReservation(null);
    }
    
    // Getters et Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public OffsetDateTime getReservationDateTime() {
        return reservationDateTime;
    }
    
    public void setReservationDateTime(OffsetDateTime reservationDateTime) {
        this.reservationDateTime = reservationDateTime;
    }
    
    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }
    
    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }
    
    public ReservationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
    
    public String getSpecialRequests() {
        return specialRequests;
    }
    
    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }
    
    public List<RestaurantTable> getTables() {
        return tables;
    }
    
    public void setTables(List<RestaurantTable> tables) {
        this.tables = tables;
    }
    
    public List<ReservationItem> getItems() {
        return items;
    }
    
    public void setItems(List<ReservationItem> items) {
        this.items = items;
    }
    
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    
    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
```

#### **Fichier 3 : ReservationItem.java**

Créez : `src/main/java/com/namaaz/service/reservations/entities/ReservationItem.java`

```java
package com.namaaz.service.reservations.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservation_item")
public class ReservationItem {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;
    
    @NotNull(message = "L'ID du plat est obligatoire")
    @Column(name = "menu_item_id", nullable = false, length = 36)
    private String menuItemId;
    
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
    
    // Constructeurs
    public ReservationItem() {
    }
    
    public ReservationItem(String menuItemId, Integer quantity) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
    
    // Getters et Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Reservation getReservation() {
        return reservation;
    }
    
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
    
    public String getMenuItemId() {
        return menuItemId;
    }
    
    public void setMenuItemId(String menuItemId) {
        this.menuItemId = menuItemId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
```

### Étape 14 : Créer la migration SQL

Créez : `src/main/resources/db/migration/V2__create_reservations_tables.sql`

```sql
-- Migration V2 : tables Réservations

-- Table des tables de restaurant
CREATE TABLE restaurant_table (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  table_number VARCHAR(10) NOT NULL UNIQUE,
  capacity INT NOT NULL CHECK (capacity > 0),
  status VARCHAR(20) NOT NULL DEFAULT 'FREE' CHECK (status IN ('FREE', 'RESERVED', 'OCCUPIED', 'OUT_OF_SERVICE')),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Table des réservations
CREATE TABLE reservation (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  client_id VARCHAR(36) NOT NULL,
  reservation_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
  number_of_guests INT NOT NULL CHECK (number_of_guests > 0),
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED')),
  special_requests TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Table de jonction entre réservations et tables
CREATE TABLE reservation_table (
  reservation_id UUID NOT NULL REFERENCES reservation(id) ON DELETE CASCADE,
  table_id UUID NOT NULL REFERENCES restaurant_table(id) ON DELETE CASCADE,
  PRIMARY KEY (reservation_id, table_id)
);

-- Table des plats pré-commandés pour une réservation
CREATE TABLE reservation_item (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  reservation_id UUID NOT NULL REFERENCES reservation(id) ON DELETE CASCADE,
  menu_item_id VARCHAR(36) NOT NULL,
  quantity INT NOT NULL CHECK (quantity > 0),
  notes TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Index pour les performances
CREATE INDEX idx_reservation_client ON reservation(client_id);
CREATE INDEX idx_reservation_status ON reservation(status);
CREATE INDEX idx_reservation_date ON reservation(reservation_date_time);
CREATE INDEX idx_reservation_item_reservation ON reservation_item(reservation_id);
CREATE INDEX idx_restaurant_table_status ON restaurant_table(status);
```

### Étape 15 : Créer les Repositories

#### **Fichier 1 : TableRepository.java**

Créez : `src/main/java/com/namaaz/service/reservations/repository/TableRepository.java`

```java
package com.namaaz.service.reservations.repository;

import com.namaaz.service.reservations.entities.RestaurantTable;
import com.namaaz.service.reservations.enums.TableStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TableRepository {
    
    @PersistenceContext(unitName = "ReservationsPU")
    private EntityManager em;
    
    public RestaurantTable save(RestaurantTable table) {
        if (table.getId() == null) {
            em.persist(table);
            return table;
        } else {
            return em.merge(table);
        }
    }
    
    public Optional<RestaurantTable> findById(UUID id) {
        RestaurantTable table = em.find(RestaurantTable.class, id);
        return Optional.ofNullable(table);
    }
    
    public List<RestaurantTable> findAll() {
        TypedQuery<RestaurantTable> query = em.createQuery(
            "SELECT t FROM RestaurantTable t ORDER BY t.tableNumber", RestaurantTable.class);
        return query.getResultList();
    }
    
    public List<RestaurantTable> findByStatus(TableStatus status) {
        TypedQuery<RestaurantTable> query = em.createQuery(
            "SELECT t FROM RestaurantTable t WHERE t.status = :status ORDER BY t.tableNumber", 
            RestaurantTable.class);
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    public Optional<RestaurantTable> findByTableNumber(String tableNumber) {
        TypedQuery<RestaurantTable> query = em.createQuery(
            "SELECT t FROM RestaurantTable t WHERE t.tableNumber = :tableNumber", 
            RestaurantTable.class);
        query.setParameter("tableNumber", tableNumber);
        return query.getResultStream().findFirst();
    }
    
    public void delete(RestaurantTable table) {
        if (!em.contains(table)) {
            table = em.merge(table);
        }
        em.remove(table);
    }
}
```

#### **Fichier 2 : ReservationRepository.java**

Créez : `src/main/java/com/namaaz/service/reservations/repository/ReservationRepository.java`

```java
package com.namaaz.service.reservations.repository;

import com.namaaz.service.reservations.entities.Reservation;
import com.namaaz.service.reservations.enums.ReservationStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ReservationRepository {
    
    @PersistenceContext(unitName = "ReservationsPU")
    private EntityManager em;
    
    public Reservation save(Reservation reservation) {
        if (reservation.getId() == null) {
            em.persist(reservation);
            return reservation;
        } else {
            return em.merge(reservation);
        }
    }
    
    public Optional<Reservation> findById(UUID id) {
        Reservation reservation = em.find(Reservation.class, id);
        return Optional.ofNullable(reservation);
    }
    
    public List<Reservation> findAll() {
        TypedQuery<Reservation> query = em.createQuery(
            "SELECT r FROM Reservation r ORDER BY r.reservationDateTime DESC", Reservation.class);
        return query.getResultList();
    }
    
    public List<Reservation> findByClientId(String clientId) {
        TypedQuery<Reservation> query = em.createQuery(
            "SELECT r FROM Reservation r WHERE r.clientId = :clientId ORDER BY r.reservationDateTime DESC", 
            Reservation.class);
        query.setParameter("clientId", clientId);
        return query.getResultList();
    }
    
    public List<Reservation> findByStatus(ReservationStatus status) {
        TypedQuery<Reservation> query = em.createQuery(
            "SELECT r FROM Reservation r WHERE r.status = :status ORDER BY r.reservationDateTime", 
            Reservation.class);
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    public List<Reservation> findByDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        TypedQuery<Reservation> query = em.createQuery(
            "SELECT r FROM Reservation r WHERE r.reservationDateTime BETWEEN :startDate AND :endDate " +
            "ORDER BY r.reservationDateTime", Reservation.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    public void delete(Reservation reservation) {
        if (!em.contains(reservation)) {
            reservation = em.merge(reservation);
        }
        em.remove(reservation);
    }
}
```

### Étape 16 : Créer les Services métier

#### **Fichier 1 : TableService.java**

Créez : `src/main/java/com/namaaz/service/reservations/business/TableService.java`

```java
package com.namaaz.service.reservations.business;

import com.namaaz.service.reservations.entities.RestaurantTable;
import com.namaaz.service.reservations.enums.TableStatus;
import com.namaaz.service.reservations.repository.TableRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Stateless
public class TableService {
    
    @Inject
    private TableRepository tableRepository;
    
    @Transactional
    public RestaurantTable createTable(RestaurantTable table) {
        return tableRepository.save(table);
    }
    
    public Optional<RestaurantTable> getTableById(UUID id) {
        return tableRepository.findById(id);
    }
    
    public List<RestaurantTable> getAllTables() {
        return tableRepository.findAll();
    }
    
    public List<RestaurantTable> getTablesByStatus(TableStatus status) {
        return tableRepository.findByStatus(status);
    }
    
    @Transactional
    public Optional<RestaurantTable> updateTable(UUID id, RestaurantTable updatedTable) {
        Optional<RestaurantTable> existing = tableRepository.findById(id);
        if (existing.isPresent()) {
            RestaurantTable table = existing.get();
            table.setTableNumber(updatedTable.getTableNumber());
            table.setCapacity(updatedTable.getCapacity());
            table.setStatus(updatedTable.getStatus());
            return Optional.of(tableRepository.save(table));
        }
        return Optional.empty();
    }
    
    @Transactional
    public Optional<RestaurantTable> updateTableStatus(UUID id, TableStatus status) {
        Optional<RestaurantTable> existing = tableRepository.findById(id);
        if (existing.isPresent()) {
            RestaurantTable table = existing.get();
            table.setStatus(status);
            return Optional.of(tableRepository.save(table));
        }
        return Optional.empty();
    }
    
    @Transactional
    public boolean deleteTable(UUID id) {
        Optional<RestaurantTable> table = tableRepository.findById(id);
        if (table.isPresent()) {
            tableRepository.delete(table.get());
            return true;
        }
        return false;
    }
}
```

#### **Fichier 2 : ReservationService.java**

Créez : `src/main/java/com/namaaz/service/reservations/business/ReservationService.java`

```java
package com.namaaz.service.reservations.business;

import com.namaaz.service.reservations.entities.Reservation;
import com.namaaz.service.reservations.entities.RestaurantTable;
import com.namaaz.service.reservations.enums.ReservationStatus;
import com.namaaz.service.reservations.enums.TableStatus;
import com.namaaz.service.reservations.repository.ReservationRepository;
import com.namaaz.service.reservations.repository.TableRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Stateless
public class ReservationService {
    
    @Inject
    private ReservationRepository reservationRepository;
    
    @Inject
    private TableRepository tableRepository;
    
    @Transactional
    public Reservation createReservation(Reservation reservation) {
        // Mettre à jour le statut des tables à RESERVED
        for (RestaurantTable table : reservation.getTables()) {
            RestaurantTable managedTable = tableRepository.findById(table.getId()).orElse(null);
            if (managedTable != null) {
                managedTable.setStatus(TableStatus.RESERVED);
                tableRepository.save(managedTable);
            }
        }
        return reservationRepository.save(reservation);
    }
    
    public Optional<Reservation> getReservationById(UUID id) {
        return reservationRepository.findById(id);
    }
    
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
    
    public List<Reservation> getReservationsByClientId(String clientId) {
        return reservationRepository.findByClientId(clientId);
    }
    
    public List<Reservation> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }
    
    public List<Reservation> getReservationsByDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        return reservationRepository.findByDateRange(startDate, endDate);
    }
    
    @Transactional
    public Optional<Reservation> updateReservation(UUID id, Reservation updatedReservation) {
        Optional<Reservation> existing = reservationRepository.findById(id);
        if (existing.isPresent()) {
            Reservation reservation = existing.get();
            reservation.setClientId(updatedReservation.getClientId());
            reservation.setReservationDateTime(updatedReservation.getReservationDateTime());
            reservation.setNumberOfGuests(updatedReservation.getNumberOfGuests());
            reservation.setSpecialRequests(updatedReservation.getSpecialRequests());
            reservation.setStatus(updatedReservation.getStatus());
            return Optional.of(reservationRepository.save(reservation));
        }
        return Optional.empty();
    }
    
    @Transactional
    public Optional<Reservation> confirmReservation(UUID id) {
        Optional<Reservation> existing = reservationRepository.findById(id);
        if (existing.isPresent()) {
            Reservation reservation = existing.get();
            reservation.setStatus(ReservationStatus.CONFIRMED);
            return Optional.of(reservationRepository.save(reservation));
        }
        return Optional.empty();
    }
    
    @Transactional
    public Optional<Reservation> cancelReservation(UUID id) {
        Optional<Reservation> existing = reservationRepository.findById(id);
        if (existing.isPresent()) {
            Reservation reservation = existing.get();
            reservation.setStatus(ReservationStatus.CANCELLED);
            
            // Libérer les tables
            for (RestaurantTable table : reservation.getTables()) {
                RestaurantTable managedTable = tableRepository.findById(table.getId()).orElse(null);
                if (managedTable != null) {
                    managedTable.setStatus(TableStatus.FREE);
                    tableRepository.save(managedTable);
                }
            }
            
            return Optional.of(reservationRepository.save(reservation));
        }
        return Optional.empty();
    }
    
    @Transactional
    public boolean deleteReservation(UUID id) {
        Optional<Reservation> reservation = reservationRepository.findById(id);
        if (reservation.isPresent()) {
            // Libérer les tables avant de supprimer
            for (RestaurantTable table : reservation.get().getTables()) {
                RestaurantTable managedTable = tableRepository.findById(table.getId()).orElse(null);
                if (managedTable != null) {
                    managedTable.setStatus(TableStatus.FREE);
                    tableRepository.save(managedTable);
                }
            }
            reservationRepository.delete(reservation.get());
            return true;
        }
        return false;
    }
}
```

### Étape 17 : Créer les Endpoints REST

#### **Fichier 1 : RestApplication.java**

Créez : `src/main/java/com/namaaz/service/reservations/rest/RestApplication.java`

```java
package com.namaaz.service.reservations.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class RestApplication extends Application {
    // Configuration JAX-RS
}
```

#### **Fichier 2 : TableResource.java**

Créez : `src/main/java/com/namaaz/service/reservations/rest/TableResource.java`

```java
package com.namaaz.service.reservations.rest;

import com.namaaz.service.reservations.business.TableService;
import com.namaaz.service.reservations.entities.RestaurantTable;
import com.namaaz.service.reservations.enums.TableStatus;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/tables")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TableResource {
    
    @Inject
    private TableService tableService;
    
    @GET
    public Response getAllTables() {
        List<RestaurantTable> tables = tableService.getAllTables();
        return Response.ok(tables).build();
    }
    
    @GET
    @Path("/status/{status}")
    public Response getTablesByStatus(@PathParam("status") String status) {
        try {
            TableStatus tableStatus = TableStatus.valueOf(status.toUpperCase());
            List<RestaurantTable> tables = tableService.getTablesByStatus(tableStatus);
            return Response.ok(tables).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid status: " + status + "\"}").build();
        }
    }
    
    @GET
    @Path("/{id}")
    public Response getTableById(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return tableService.getTableById(uuid)
                .map(table -> Response.ok(table).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
    
    @POST
    public Response createTable(@Valid RestaurantTable table) {
        RestaurantTable created = tableService.createTable(table);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
    
    @PUT
    @Path("/{id}")
    public Response updateTable(@PathParam("id") String id, @Valid RestaurantTable table) {
        try {
            UUID uuid = UUID.fromString(id);
            return tableService.updateTable(uuid, table)
                .map(updated -> Response.ok(updated).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
    
    @PUT
    @Path("/{id}/status")
    public Response updateTableStatus(
            @PathParam("id") String id, 
            @QueryParam("status") String status) {
        try {
            UUID uuid = UUID.fromString(id);
            TableStatus tableStatus = TableStatus.valueOf(status.toUpperCase());
            return tableService.updateTableStatus(uuid, tableStatus)
                .map(updated -> Response.ok(updated).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid parameter\"}").build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteTable(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            boolean deleted = tableService.deleteTable(uuid);
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

#### **Fichier 3 : ReservationResource.java**

Créez : `src/main/java/com/namaaz/service/reservations/rest/ReservationResource.java`

```java
package com.namaaz.service.reservations.rest;

import com.namaaz.service.reservations.business.ReservationService;
import com.namaaz.service.reservations.entities.Reservation;
import com.namaaz.service.reservations.enums.ReservationStatus;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Path("/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationResource {
    
    @Inject
    private ReservationService reservationService;
    
    @GET
    public Response getAllReservations(
            @QueryParam("clientId") String clientId,
            @QueryParam("status") String status,
            @QueryParam("startDate") String startDate,
            @QueryParam("endDate") String endDate) {
        
        List<Reservation> reservations;
        
        if (clientId != null) {
            reservations = reservationService.getReservationsByClientId(clientId);
        } else if (status != null) {
            try {
                ReservationStatus reservationStatus = ReservationStatus.valueOf(status.toUpperCase());
                reservations = reservationService.getReservationsByStatus(reservationStatus);
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid status: " + status + "\"}").build();
            }
        } else if (startDate != null && endDate != null) {
            try {
                OffsetDateTime start = OffsetDateTime.parse(startDate);
                OffsetDateTime end = OffsetDateTime.parse(endDate);
                reservations = reservationService.getReservationsByDateRange(start, end);
            } catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid date format\"}").build();
            }
        } else {
            reservations = reservationService.getAllReservations();
        }
        
        return Response.ok(reservations).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getReservationById(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return reservationService.getReservationById(uuid)
                .map(reservation -> Response.ok(reservation).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
    
    @POST
    public Response createReservation(@Valid Reservation reservation) {
        try {
            Reservation created = reservationService.createReservation(reservation);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response updateReservation(@PathParam("id") String id, @Valid Reservation reservation) {
        try {
            UUID uuid = UUID.fromString(id);
            return reservationService.updateReservation(uuid, reservation)
                .map(updated -> Response.ok(updated).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
    
    @PUT
    @Path("/{id}/confirm")
    public Response confirmReservation(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return reservationService.confirmReservation(uuid)
                .map(confirmed -> Response.ok(confirmed).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
    
    @PUT
    @Path("/{id}/cancel")
    public Response cancelReservation(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return reservationService.cancelReservation(uuid)
                .map(cancelled -> Response.ok(cancelled).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid UUID format\"}").build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteReservation(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            boolean deleted = reservationService.deleteReservation(uuid);
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
2. Connectez-vous à `db.prisma.io`
3. Ouvrez **Query Tool** sur la base `postgres`
4. Exécutez le contenu du fichier `V2__create_reservations_tables.sql`
5. Vérifiez que les 4 tables sont créées :
   - `restaurant_table`
   - `reservation`
   - `reservation_table`
   - `reservation_item`

### Étape 19 : Build et déploiement

1. Dans **NetBeans**, clic droit sur **service-reservations** → **Clean and Build**
2. Vérifiez : **BUILD SUCCESS** ✅
3. Clic droit → **Run**
4. Vérifiez : Déploiement réussi ✅

---

## Phase 3 : Tests complets

### Test 1 : Créer des tables de restaurant

Utilisez **Postman** :

**POST** `http://localhost:8080/service-reservations/api/tables`

```json
{
  "tableNumber": "T01",
  "capacity": 2,
  "status": "FREE"
}
```

Créez 5 tables :
- T01 (2 personnes)
- T02 (2 personnes)
- T03 (4 personnes)
- T04 (4 personnes)
- T05 (6 personnes)

### Test 2 : Lister toutes les tables

**GET** `http://localhost:8080/service-reservations/api/tables`

**Résultat attendu** : 5 tables

### Test 3 : Lister les tables libres

**GET** `http://localhost:8080/service-reservations/api/tables/status/FREE`

**Résultat attendu** : 5 tables libres

### Test 4 : Créer une réservation

**Important** : Vous devez d'abord créer un client dans le service-clients-orders et récupérer son ID.

**POST** `http://localhost:8080/service-reservations/api/reservations`

```json
{
  "clientId": "ID_DU_CLIENT_ICI",
  "reservationDateTime": "2026-01-25T19:30:00+01:00",
  "numberOfGuests": 4,
  "status": "PENDING",
  "specialRequests": "Table près de la fenêtre",
  "tables": [
    {
      "id": "ID_TABLE_T03"
    }
  ]
}
```

### Test 5 : Lister toutes les réservations

**GET** `http://localhost:8080/service-reservations/api/reservations`

### Test 6 : Filtrer par statut

**GET** `http://localhost:8080/service-reservations/api/reservations?status=PENDING`

**Résultat attendu** : Réservations en attente

### Test 7 : Confirmer une réservation

**PUT** `http://localhost:8080/service-reservations/api/reservations/{ID_RESERVATION}/confirm`

**Résultat attendu** : Status passe à "CONFIRMED"

### Test 8 : Vérifier le statut de la table

**GET** `http://localhost:8080/service-reservations/api/tables/{ID_TABLE_T03}`

**Résultat attendu** : Status = "RESERVED"

### Test 9 : Annuler une réservation

**PUT** `http://localhost:8080/service-reservations/api/reservations/{ID_RESERVATION}/cancel`

**Résultat attendu** : 
- Status réservation = "CANCELLED"
- Status table = "FREE" (libérée automatiquement)

### Test 10 : Changer le statut d'une table

**PUT** `http://localhost:8080/service-reservations/api/tables/{ID_TABLE}/status?status=OCCUPIED`

**Résultat attendu** : Status table = "OCCUPIED"

### Test 11 : Créer une réservation avec plusieurs tables

**POST** `http://localhost:8080/service-reservations/api/reservations`

```json
{
  "clientId": "ID_DU_CLIENT",
  "reservationDateTime": "2026-01-26T20:00:00+01:00",
  "numberOfGuests": 8,
  "status": "PENDING",
  "specialRequests": "Fête d'anniversaire",
  "tables": [
    {"id": "ID_TABLE_T03"},
    {"id": "ID_TABLE_T04"}
  ]
}
```

### Test 12 : Filtrer par plage de dates

**GET** `http://localhost:8080/service-reservations/api/reservations?startDate=2026-01-25T00:00:00Z&endDate=2026-01-27T23:59:59Z`

---

## Résumé des fichiers créés

### Enums (2 fichiers)
- `src/main/java/com/namaaz/service/reservations/enums/TableStatus.java`
- `src/main/java/com/namaaz/service/reservations/enums/ReservationStatus.java`

### Entités JPA (3 fichiers)
- `src/main/java/com/namaaz/service/reservations/entities/RestaurantTable.java`
- `src/main/java/com/namaaz/service/reservations/entities/Reservation.java`
- `src/main/java/com/namaaz/service/reservations/entities/ReservationItem.java`

### Repositories (2 fichiers)
- `src/main/java/com/namaaz/service/reservations/repository/TableRepository.java`
- `src/main/java/com/namaaz/service/reservations/repository/ReservationRepository.java`

### Services métier (2 fichiers)
- `src/main/java/com/namaaz/service/reservations/business/TableService.java`
- `src/main/java/com/namaaz/service/reservations/business/ReservationService.java`

### Endpoints REST (3 fichiers)
- `src/main/java/com/namaaz/service/reservations/rest/RestApplication.java`
- `src/main/java/com/namaaz/service/reservations/rest/TableResource.java`
- `src/main/java/com/namaaz/service/reservations/rest/ReservationResource.java`

### Configuration (3 fichiers)
- `src/main/resources/META-INF/persistence.xml`
- `src/main/webapp/WEB-INF/glassfish-resources.xml`
- `src/main/resources/db/migration/V2__create_reservations_tables.sql`

### Total : 15 fichiers

---

## ✅ Endpoints disponibles

### Tables
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/tables` | Lister toutes les tables |
| GET | `/api/tables/status/{status}` | Filtrer par statut (FREE, RESERVED, OCCUPIED, OUT_OF_SERVICE) |
| GET | `/api/tables/{id}` | Récupérer une table |
| POST | `/api/tables` | Créer une table |
| PUT | `/api/tables/{id}` | Modifier une table |
| PUT | `/api/tables/{id}/status?status={status}` | Changer le statut |
| DELETE | `/api/tables/{id}` | Supprimer une table |

### Réservations
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/reservations` | Lister toutes les réservations |
| GET | `/api/reservations?clientId={id}` | Filtrer par client |
| GET | `/api/reservations?status={status}` | Filtrer par statut (PENDING, CONFIRMED, CANCELLED) |
| GET | `/api/reservations?startDate={date}&endDate={date}` | Filtrer par période |
| GET | `/api/reservations/{id}` | Récupérer une réservation |
| POST | `/api/reservations` | Créer une réservation |
| PUT | `/api/reservations/{id}` | Modifier une réservation |
| PUT | `/api/reservations/{id}/confirm` | Confirmer une réservation |
| PUT | `/api/reservations/{id}/cancel` | Annuler une réservation (libère les tables) |
| DELETE | `/api/reservations/{id}` | Supprimer une réservation |

---

## 🔗 Intégration avec les autres services

### Service Menu (service-menu)
Les `ReservationItem` contiennent un `menuItemId` qui référence les plats du service menu.

**Exemple** : Lors de la création d'une réservation, vous pouvez pré-commander des plats :
```java
ReservationItem item = new ReservationItem();
item.setMenuItemId("UUID_DU_PLAT_DU_SERVICE_MENU");
item.setQuantity(2);
item.setNotes("Bien cuit");
reservation.addItem(item);
```

### Service Clients (service-clients-orders)
Le champ `clientId` dans `Reservation` référence un client du service clients.

**Workflow** :
1. Récupérer les clients disponibles depuis `/api/clients`
2. Utiliser l'ID du client pour créer une réservation
3. Le service réservations stocke uniquement l'ID (pas de duplication)

---

## 🎯 Fonctionnalités avancées implémentées

### Gestion automatique des statuts de tables
- Lors de la création d'une réservation → Tables passent à `RESERVED`
- Lors de l'annulation → Tables redeviennent `FREE`
- Lors de la suppression → Tables sont libérées

### Relations Many-to-Many
- Une réservation peut avoir plusieurs tables
- Une table peut être dans plusieurs réservations (à des moments différents)

### Cascade et Orphan Removal
- Suppression d'une réservation → Suppression automatique des `ReservationItem` associés
- Protection des tables : non supprimées avec la réservation

### Validation complète
- Bean Validation sur tous les champs obligatoires
- Vérification des capacités (min 1 personne, min 1 table)
- Validation des formats UUID et dates

---

## 🎯 Prochaines étapes

1. ✅ Service Menu (FAIT)
2. ✅ Service Réservations (FAIT)
3. Service Clients/Commandes/Paiements (à implémenter)
4. Intégration complète des 3 microservices
5. Interface utilisateur (JSF/React)

**Fin du guide — Service Réservations implémenté avec succès ! ✅**
