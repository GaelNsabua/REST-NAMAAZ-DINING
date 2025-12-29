# Modèles de bases de données — Namaaz Dining

Ce fichier rassemble les modèles BDD (DDL Postgres) et des extraits d'entités JPA pour chaque microservice : Service Menu, Service Réservations, Service Clients/Commandes/Paiements.

---

## Hypothèses générales
- Base relationnelle par service (Postgres recommandé).
- Clés primaires : UUID (`uuid_generate_v4()`).
- Timestamps : `created_at`, `updated_at` (timezone-aware).
- Pas de FK cross-service au niveau DB : on stocke les UUIDs externes.
- Conserver des snapshots de prix dans les commandes/réservations.

- Enums : préférer des types ENUM côté base (`CREATE TYPE ... AS ENUM`) ou des colonnes VARCHAR/STRING avec contrainte CHECK. En Java, utiliser des `enum` + `@Enumerated(EnumType.STRING)` pour conserver la lisibilité et la compatibilité.

---

## Service 1 — Gestion du Menu
Rôle : gérer les catégories et les plats.

### DDL (Postgres)
```sql
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
CREATE INDEX idx_menu_item_name ON menu_item USING gin (to_tsvector('french', name));
```

### Extraits JPA (squelettes)
```java
@Entity
public class Category {
  @Id
  private UUID id;

  @Column(unique = true, nullable = false)
  private String name;

  private String description;

  // getters/setters
}

@Entity
public class MenuItem {
  @Id
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(precision = 10, scale = 2, nullable = false)
  private BigDecimal price;

  @ManyToOne(optional = false)
  private Category category;

  private Boolean available = true;
  private Integer prepTime;

  // getters/setters
}
```

---

## Service 2 — Gestion des Réservations
Rôle : gérer tables et réservations, associer plats choisis.

### DDL (Postgres)
```sql
-- Types ENUM recommandés pour ce service (Réservations)
CREATE TYPE reservation_status AS ENUM ('PENDING','CONFIRMED','CANCELLED');
CREATE TYPE table_status AS ENUM ('FREE','RESERVED','OCCUPIED','OUT_OF_SERVICE');

CREATE TABLE restaurant_table (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  table_number INT NOT NULL UNIQUE,
  seats INT NOT NULL,
  location VARCHAR(100),
  status table_status DEFAULT 'FREE',
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE reservation (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  client_id UUID NOT NULL,
  num_people INT NOT NULL,
  start_time TIMESTAMP WITH TIME ZONE NOT NULL,
  end_time TIMESTAMP WITH TIME ZONE,
  status reservation_status NOT NULL DEFAULT 'PENDING',
  notes TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE reservation_table (
  reservation_id UUID REFERENCES reservation(id) ON DELETE CASCADE,
  table_id UUID REFERENCES restaurant_table(id),
  PRIMARY KEY (reservation_id, table_id)
);

-- Plats choisis pour une réservation (snapshot du prix)
CREATE TABLE reservation_item (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  reservation_id UUID REFERENCES reservation(id) ON DELETE CASCADE,
  menu_item_id UUID NOT NULL,
  quantity INT NOT NULL DEFAULT 1,
  price_snapshot NUMERIC(10,2),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE INDEX idx_reservation_start ON reservation(start_time);
CREATE INDEX idx_reservation_client ON reservation(client_id);
```

### Extraits JPA (squelettes)
```java
@Entity
public class RestaurantTable {
  @Id
  private UUID id;
  private Integer tableNumber;
  private Integer seats;
  private String location;
  @Enumerated(EnumType.STRING)
  private TableStatus status;
}

@Entity
public class Reservation {
  @Id
  private UUID id;
  private UUID clientId; // référence au Service Clients
  private Integer numPeople;
  private OffsetDateTime startTime;
  private OffsetDateTime endTime;
  @Enumerated(EnumType.STRING)
  private ReservationStatus status;
  private String notes;

  @ManyToMany
  @JoinTable(name = "reservation_table",
    joinColumns = @JoinColumn(name = "reservation_id"),
    inverseJoinColumns = @JoinColumn(name = "table_id"))
  private Set<RestaurantTable> tables;

  @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
  private List<ReservationItem> items;
}

@Entity
public class ReservationItem {
  @Id
  private UUID id;

  @ManyToOne(optional = false)
  private Reservation reservation;

  private UUID menuItemId; // référence au Service Menu
  private Integer quantity;
  private BigDecimal priceSnapshot;
}
```

// Enums Java (exemples)
```java
public enum ReservationStatus {
  PENDING,
  CONFIRMED,
  CANCELLED
}

public enum TableStatus {
  FREE,
  RESERVED,
  OCCUPIED,
  OUT_OF_SERVICE
}
```

---

## Service 3 — Clients, Commandes et Paiements
Rôle : centraliser clients, commandes, paiements et rapports.

### DDL (Postgres)
```sql
CREATE TABLE client (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  email VARCHAR(200) UNIQUE,
  phone VARCHAR(50),
  address TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Types ENUM recommandés pour ce service (Clients / Commandes / Paiements)
CREATE TYPE order_status AS ENUM ('NEW','IN_PROGRESS','COMPLETED','CANCELLED');
CREATE TYPE payment_method AS ENUM ('CASH','CARD','ONLINE');
CREATE TYPE payment_status AS ENUM ('PENDING','OK','FAILED');

CREATE TABLE orders (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  client_id UUID NOT NULL,
  reservation_id UUID,
  table_id UUID,
  status order_status NOT NULL DEFAULT 'NEW',
  total_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE order_item (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  order_id UUID REFERENCES orders(id) ON DELETE CASCADE,
  menu_item_id UUID NOT NULL,
  quantity INT NOT NULL,
  unit_price NUMERIC(10,2),
  total_price NUMERIC(12,2),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE payment (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  order_id UUID REFERENCES orders(id),
  amount NUMERIC(12,2) NOT NULL,
  method payment_method,
  status payment_status DEFAULT 'PENDING',
  transaction_ref VARCHAR(200),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE INDEX idx_order_client ON orders(client_id);
CREATE INDEX idx_payment_order ON payment(order_id);
```

### Extraits JPA (squelettes)
```java
@Entity
public class Client {
  @Id
  private UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private String address;
}

@Entity
@Table(name = "orders")
public class Order {
  @Id
  private UUID id;

  private UUID clientId; // lien vers service Clients
  private UUID reservationId; // optionnel
  private UUID tableId; // optionnel
  @Enumerated(EnumType.STRING)
  private OrderStatus status;
  private BigDecimal totalAmount;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  private List<OrderItem> items;
}

@Entity
public class OrderItem {
  @Id
  private UUID id;

  @ManyToOne(optional = false)
  private Order order;

  private UUID menuItemId; // référence au Service Menu
  private Integer quantity;
  private BigDecimal unitPrice;
  private BigDecimal totalPrice;
}

@Entity
public class Payment {
  @Id
  private UUID id;

  @ManyToOne
  private Order order;

  private BigDecimal amount;
  @Enumerated(EnumType.STRING)
  private PaymentMethod method;

  @Enumerated(EnumType.STRING)
  private PaymentStatus status;
  private String transactionRef;
}
```

// Enums Java (exemples)
```java
public enum OrderStatus {
  NEW,
  IN_PROGRESS,
  COMPLETED,
  CANCELLED
}

public enum PaymentMethod {
  CASH,
  CARD,
  ONLINE
}

public enum PaymentStatus {
  PENDING,
  OK,
  FAILED
}
```

---

## Notes d'intégration inter-services
- Les UUIDs externes (`client_id`, `menu_item_id`, `reservation_id`, etc.) servent de lien ; utiliser les APIs REST des services pour récupérer les détails.
- Stocker `unit_price`/`price_snapshot` pour conserver l'historique en cas de modification de prix.
- Indexer colonnes fréquemment filtrées (`start_time`, `client_id`, `status`).
- Pour la consistance, préférez des opérations idempotentes côté API et des validations côté service destinataire.

---

## Prochaine étape proposée
- Générer les classes Java complètes (JPA + DTO + Repositories) et les fichiers SQL de migration (Flyway/Liquibase) pour chaque service.

Fin du document.
