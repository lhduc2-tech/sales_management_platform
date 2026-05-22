-- WARNING: This schema is for context only and is not meant to be run.
-- Table order and constraints may not be valid for execution.

CREATE TABLE public.activation_tokens (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  token character varying NOT NULL UNIQUE,
  user_id uuid NOT NULL,
  expires_at timestamp with time zone NOT NULL,
  used boolean NOT NULL DEFAULT false,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT activation_tokens_pkey PRIMARY KEY (id),
  CONSTRAINT activation_tokens_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id)
);
CREATE TABLE public.categories (
  id uuid NOT NULL,
  category_name character varying NOT NULL,
  category_description character varying,
  created_at timestamp with time zone,
  created_by character varying,
  updated_at timestamp with time zone,
  updated_by character varying,
  shop_id uuid,
  CONSTRAINT categories_pkey PRIMARY KEY (id),
  CONSTRAINT categories_shops_id_fk FOREIGN KEY (shop_id) REFERENCES public.shops(id)
);
CREATE TABLE public.customers (
  id uuid NOT NULL,
  full_name character varying NOT NULL,
  phone_number character varying,
  email character varying,
  address character varying,
  shop_id uuid,
  created_at timestamp with time zone,
  created_by character varying,
  updated_at timestamp with time zone,
  updated_by character varying,
  CONSTRAINT customers_pkey PRIMARY KEY (id),
  CONSTRAINT customers_shops_id_fk FOREIGN KEY (shop_id) REFERENCES public.shops(id)
);
CREATE TABLE public.flyway_schema_history (
  installed_rank integer NOT NULL,
  version character varying,
  description character varying NOT NULL,
  type character varying NOT NULL,
  script character varying NOT NULL,
  checksum integer,
  installed_by character varying NOT NULL,
  installed_on timestamp without time zone NOT NULL DEFAULT now(),
  execution_time integer NOT NULL,
  success boolean NOT NULL,
  CONSTRAINT flyway_schema_history_pkey PRIMARY KEY (installed_rank)
);
CREATE TABLE public.inventories (
  product_id uuid NOT NULL,
  shop_id uuid NOT NULL,
  quantity integer NOT NULL DEFAULT 0,
  updated_at timestamp with time zone,
  CONSTRAINT inventories_pkey PRIMARY KEY (product_id),
  CONSTRAINT inventories_product_id_fkey FOREIGN KEY (product_id) REFERENCES public.products(id),
  CONSTRAINT inventories_shop_id_fkey FOREIGN KEY (shop_id) REFERENCES public.shops(id)
);
CREATE TABLE public.order_items (
  product_id uuid NOT NULL,
  quantity integer,
  order_id uuid NOT NULL,
  total_price numeric,
  unit_price numeric NOT NULL,
  created_by character varying,
  created_at timestamp with time zone,
  updated_at timestamp with time zone,
  updated_by character varying,
  id uuid NOT NULL,
  CONSTRAINT order_items_pkey PRIMARY KEY (id),
  CONSTRAINT order_items_orders_id_fk FOREIGN KEY (order_id) REFERENCES public.orders(id),
  CONSTRAINT order_items_products_id_fk FOREIGN KEY (product_id) REFERENCES public.products(id)
);
CREATE TABLE public.orders (
  id uuid NOT NULL,
  order_date timestamp with time zone NOT NULL,
  order_origin character varying NOT NULL,
  price numeric NOT NULL,
  payment_state character varying,
  process_state character varying,
  delivery_service character varying,
  shop_id uuid,
  created_at timestamp with time zone,
  created_by character varying,
  updated_at timestamp with time zone,
  updated_by character varying,
  customer_id uuid,
  order_name character varying,
  payment_method character varying,
  description character varying,
  CONSTRAINT orders_pkey PRIMARY KEY (id),
  CONSTRAINT orders_shops_id_fk FOREIGN KEY (shop_id) REFERENCES public.shops(id),
  CONSTRAINT orders_customers_id_fk FOREIGN KEY (customer_id) REFERENCES public.customers(id)
);
CREATE TABLE public.payments (
  id uuid NOT NULL,
  order_id uuid NOT NULL,
  payment_method character varying NOT NULL,
  amount numeric NOT NULL,
  payment_time timestamp with time zone NOT NULL,
  payment_status character varying NOT NULL,
  created_by character varying,
  CONSTRAINT payments_pkey PRIMARY KEY (id),
  CONSTRAINT payments_order_id_fkey FOREIGN KEY (order_id) REFERENCES public.orders(id)
);
CREATE TABLE public.permissions (
  id uuid NOT NULL,
  code character varying NOT NULL UNIQUE,
  description text,
  created_at timestamp with time zone,
  created_by character varying,
  updated_at timestamp with time zone,
  updated_by character varying,
  CONSTRAINT permissions_pkey PRIMARY KEY (id)
);
CREATE TABLE public.price_history (
  id uuid NOT NULL,
  product_id uuid NOT NULL,
  price numeric NOT NULL,
  effective_from timestamp with time zone NOT NULL,
  effective_to timestamp with time zone,
  created_by character varying,
  CONSTRAINT price_history_pkey PRIMARY KEY (id),
  CONSTRAINT product_price_history_product_id_fkey FOREIGN KEY (product_id) REFERENCES public.products(id)
);
CREATE TABLE public.products (
  id uuid NOT NULL,
  created_at timestamp with time zone,
  created_by character varying,
  updated_at timestamp with time zone,
  updated_by character varying,
  code character varying,
  name character varying,
  price numeric,
  shop_id uuid,
  category_id uuid,
  description character varying,
  img_url character varying,
  CONSTRAINT products_pkey PRIMARY KEY (id),
  CONSTRAINT products_shops_id_fk FOREIGN KEY (shop_id) REFERENCES public.shops(id),
  CONSTRAINT products_categories_id_fk FOREIGN KEY (category_id) REFERENCES public.categories(id)
);
CREATE TABLE public.purchase_order_items (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  purchase_order_id uuid NOT NULL,
  product_id uuid NOT NULL,
  quantity integer NOT NULL CHECK (quantity > 0),
  import_price numeric,
  CONSTRAINT purchase_order_items_pkey PRIMARY KEY (id),
  CONSTRAINT fk_poi_po FOREIGN KEY (purchase_order_id) REFERENCES public.purchase_orders(id),
  CONSTRAINT fk_poi_product FOREIGN KEY (product_id) REFERENCES public.products(id)
);
CREATE TABLE public.purchase_orders (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  supplier_id uuid NOT NULL,
  created_by character varying,
  status character varying NOT NULL DEFAULT 'DRAFT'::character varying,
  note text,
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  shop_id uuid,
  updated_at timestamp with time zone,
  updated_by character varying,
  CONSTRAINT purchase_orders_pkey PRIMARY KEY (id),
  CONSTRAINT fk_po_supplier FOREIGN KEY (supplier_id) REFERENCES public.suppliers(id)
);
CREATE TABLE public.role_permissions (
  role_id uuid NOT NULL,
  permission_id uuid NOT NULL,
  CONSTRAINT role_permissions_pkey PRIMARY KEY (role_id, permission_id),
  CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES public.roles(id),
  CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES public.permissions(id)
);
CREATE TABLE public.roles (
  id uuid NOT NULL,
  code character varying NOT NULL UNIQUE,
  name character varying NOT NULL,
  description text,
  status character varying NOT NULL DEFAULT 'ACTIVE'::character varying,
  created_at timestamp with time zone NOT NULL,
  created_by character varying,
  updated_at timestamp with time zone,
  updated_by character varying,
  CONSTRAINT roles_pkey PRIMARY KEY (id)
);
CREATE TABLE public.shops (
  id uuid NOT NULL,
  owner_user_id uuid,
  shop_name character varying,
  status character varying,
  created_at timestamp with time zone,
  created_by character varying,
  updated_at timestamp with time zone,
  updated_by character varying,
  address character varying,
  shop_type character varying,
  shop_code character varying UNIQUE,
  opened_at timestamp with time zone,
  CONSTRAINT shops_pkey PRIMARY KEY (id),
  CONSTRAINT shops_users_id_fk FOREIGN KEY (owner_user_id) REFERENCES public.users(id)
);
CREATE TABLE public.staffs (
  id uuid NOT NULL,
  created_at timestamp with time zone,
  created_by character varying,
  updated_at timestamp with time zone,
  updated_by character varying,
  shop_id uuid,
  user_id uuid,
  status character varying NOT NULL DEFAULT 'ACTIVE'::character varying,
  position character varying,
  CONSTRAINT staffs_pkey PRIMARY KEY (id),
  CONSTRAINT staffs_shops_id_fk FOREIGN KEY (shop_id) REFERENCES public.shops(id),
  CONSTRAINT staffs_users_id_fk FOREIGN KEY (user_id) REFERENCES public.users(id)
);
CREATE TABLE public.stock_movements (
  id uuid NOT NULL,
  product_id uuid NOT NULL,
  shop_id uuid NOT NULL,
  movement_type character varying NOT NULL,
  quantity integer NOT NULL,
  note character varying,
  created_at timestamp with time zone,
  created_by character varying,
  reference_type character varying,
  reference_id bigint,
  updated_at timestamp with time zone,
  updated_by character varying,
  CONSTRAINT stock_movements_pkey PRIMARY KEY (id),
  CONSTRAINT stock_movements_products_id_fk FOREIGN KEY (product_id) REFERENCES public.products(id),
  CONSTRAINT stock_movements_shops_id_fk FOREIGN KEY (shop_id) REFERENCES public.shops(id)
);
CREATE TABLE public.suppliers (
  id uuid NOT NULL,
  supplier_name character varying NOT NULL,
  code character varying NOT NULL,
  phone_number character varying,
  email character varying,
  website character varying,
  tax_code character varying,
  created_by character varying,
  created_at timestamp with time zone,
  updated_at timestamp with time zone,
  updated_by character varying,
  shop_id uuid,
  CONSTRAINT suppliers_pkey PRIMARY KEY (id),
  CONSTRAINT suppliers_shops_id_fk FOREIGN KEY (shop_id) REFERENCES public.shops(id)
);
CREATE TABLE public.user_roles (
  user_id uuid NOT NULL,
  role_id uuid NOT NULL,
  assigned_at timestamp with time zone NOT NULL,
  assigned_by character varying,
  CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES public.users(id),
  CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES public.roles(id)
);
CREATE TABLE public.users (
  id uuid NOT NULL,
  username character varying NOT NULL UNIQUE,
  email character varying UNIQUE,
  password_hash character varying NOT NULL,
  status character varying NOT NULL DEFAULT 'ACTIVE'::character varying,
  is_system boolean NOT NULL DEFAULT false,
  last_login_at timestamp with time zone,
  created_at timestamp with time zone NOT NULL,
  updated_at timestamp with time zone NOT NULL,
  google_id character varying,
  auth_provider character varying,
  phone_number character varying,
  shop_id uuid,
  created_by character varying,
  updated_by character varying,
  full_name character varying,
  CONSTRAINT users_pkey PRIMARY KEY (id),
  CONSTRAINT users_shops_id_fk FOREIGN KEY (shop_id) REFERENCES public.shops(id)
);
