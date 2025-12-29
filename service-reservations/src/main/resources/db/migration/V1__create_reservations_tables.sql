-- Migration V1 : tables Reservations
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Types ENUM recommandés
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
