-- Migration V1 : Tables Clients, Orders, Payments

-- Table CLIENT
CREATE TABLE client (
  id VARCHAR(36) PRIMARY KEY,
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  email VARCHAR(200) UNIQUE NOT NULL,
  phone VARCHAR(50),
  address TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Table ORDERS
CREATE TABLE orders (
  id VARCHAR(36) PRIMARY KEY,
  client_id VARCHAR(36) NOT NULL,
  reservation_id VARCHAR(36),
  table_id VARCHAR(36),
  status VARCHAR(20) NOT NULL DEFAULT 'NEW' CHECK (status IN ('NEW','IN_PROGRESS','COMPLETED','CANCELLED')),
  total_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Table ORDER_ITEM
CREATE TABLE order_item (
  id VARCHAR(36) PRIMARY KEY,
  order_id VARCHAR(36) REFERENCES orders(id) ON DELETE CASCADE,
  menu_item_id VARCHAR(36) NOT NULL,
  quantity INT NOT NULL CHECK (quantity > 0),
  unit_price NUMERIC(10,2) NOT NULL,
  total_price NUMERIC(12,2) NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Table PAYMENT
CREATE TABLE payment (
  id VARCHAR(36) PRIMARY KEY,
  order_id VARCHAR(36) REFERENCES orders(id),
  amount NUMERIC(12,2) NOT NULL CHECK (amount > 0),
  method VARCHAR(20) NOT NULL CHECK (method IN ('CASH','CARD','ONLINE')),
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','OK','FAILED')),
  transaction_ref VARCHAR(200), 
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Index pour améliorer les performances
CREATE INDEX idx_order_client ON orders(client_id);
CREATE INDEX idx_order_status ON orders(status);
CREATE INDEX idx_order_item_order ON order_item(order_id);
CREATE INDEX idx_order_item_menu ON order_item(menu_item_id);
CREATE INDEX idx_payment_order ON payment(order_id);
CREATE INDEX idx_payment_status ON payment(status);
CREATE INDEX idx_client_email ON client(email);
