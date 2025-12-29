-- Migration V1 : tables Clients, Orders, Payments
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Types ENUM recommandés pour ce service (Clients / Commandes / Paiements)
CREATE TYPE order_status AS ENUM ('NEW','IN_PROGRESS','COMPLETED','CANCELLED');
CREATE TYPE payment_method AS ENUM ('CASH','CARD','ONLINE');
CREATE TYPE payment_status AS ENUM ('PENDING','OK','FAILED');

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
