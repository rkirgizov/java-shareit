DROP TABLE IF EXISTS public.comments;
DROP TABLE IF EXISTS public.bookings;
DROP TYPE IF EXISTS public.status;
DROP TABLE IF EXISTS public.items;
DROP TABLE IF EXISTS public.requests;
DROP TABLE IF EXISTS public.users;


CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  description VARCHAR(512) NOT NULL,
  requestor_id BIGINT REFERENCES users (id) ON DELETE CASCADE ON UPDATE RESTRICT,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT pk_request PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(512) NOT NULL,
  is_available BOOLEAN NOT NULL,
  owner_id BIGINT REFERENCES users (id) ON DELETE CASCADE ON UPDATE RESTRICT,
  request_id BIGINT REFERENCES requests (id),
  CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  start_date TIMESTAMP WITHOUT TIME ZONE  NOT NULL,
  end_date TIMESTAMP WITHOUT TIME ZONE  NOT NULL,
  item_id BIGINT REFERENCES items (id) ON DELETE CASCADE ON UPDATE RESTRICT,
  booker_id BIGINT REFERENCES users (id) ON DELETE CASCADE ON UPDATE RESTRICT,
  --status STATUS,
  status VARCHAR(15),
  CONSTRAINT pk_booking PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  text VARCHAR(512) NOT NULL,
  item_id BIGINT REFERENCES items (id) ON DELETE CASCADE ON UPDATE RESTRICT,
  author_id BIGINT REFERENCES users (id) ON DELETE CASCADE ON UPDATE RESTRICT,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT pk_comment PRIMARY KEY (id)
);