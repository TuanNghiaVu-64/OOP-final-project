--
-- PostgreSQL database dump
--

-- Dumped from database version 17.3
-- Dumped by pg_dump version 17.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: bookings; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bookings (
    id integer NOT NULL,
    user_id integer,
    flight_seat_assignment_id integer,
    booking_time timestamp without time zone DEFAULT now(),
    status character varying(20) DEFAULT 'PENDING'::character varying NOT NULL,
    payment_time timestamp without time zone,
    CONSTRAINT valid_status CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'PAID'::character varying, 'CANCELLED'::character varying])::text[])))
);


ALTER TABLE public.bookings OWNER TO postgres;

--
-- Name: bookings_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.bookings_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.bookings_id_seq OWNER TO postgres;

--
-- Name: bookings_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.bookings_id_seq OWNED BY public.bookings.id;


--
-- Name: flight_seat_assignments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.flight_seat_assignments (
    id integer NOT NULL,
    flight_id integer,
    seat_id integer,
    flight_seat_type_id integer,
    available boolean DEFAULT true
);


ALTER TABLE public.flight_seat_assignments OWNER TO postgres;

--
-- Name: flight_seat_assignments_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.flight_seat_assignments_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.flight_seat_assignments_id_seq OWNER TO postgres;

--
-- Name: flight_seat_assignments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.flight_seat_assignments_id_seq OWNED BY public.flight_seat_assignments.id;


--
-- Name: flight_seat_types; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.flight_seat_types (
    id integer NOT NULL,
    flight_id integer,
    seat_type_id integer,
    price numeric NOT NULL,
    approved boolean DEFAULT false
);


ALTER TABLE public.flight_seat_types OWNER TO postgres;

--
-- Name: flight_seat_types_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.flight_seat_types_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.flight_seat_types_id_seq OWNER TO postgres;

--
-- Name: flight_seat_types_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.flight_seat_types_id_seq OWNED BY public.flight_seat_types.id;


--
-- Name: flights; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.flights (
    id integer NOT NULL,
    plane_id integer,
    origin_id integer,
    destination_id integer,
    departure_time timestamp without time zone NOT NULL,
    approved boolean DEFAULT false,
    arrival_time timestamp without time zone NOT NULL,
    CONSTRAINT valid_times CHECK ((arrival_time > departure_time))
);


ALTER TABLE public.flights OWNER TO postgres;

--
-- Name: flights_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.flights_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.flights_id_seq OWNER TO postgres;

--
-- Name: flights_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.flights_id_seq OWNED BY public.flights.id;


--
-- Name: locations; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.locations (
    id integer NOT NULL,
    city text NOT NULL,
    country text
);


ALTER TABLE public.locations OWNER TO postgres;

--
-- Name: locations_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.locations_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.locations_id_seq OWNER TO postgres;

--
-- Name: locations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.locations_id_seq OWNED BY public.locations.id;


--
-- Name: planes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.planes (
    id integer NOT NULL,
    model text NOT NULL,
    size text NOT NULL,
    approved boolean DEFAULT false,
    CONSTRAINT planes_size_check CHECK ((size = ANY (ARRAY['small'::text, 'big'::text])))
);


ALTER TABLE public.planes OWNER TO postgres;

--
-- Name: planes_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.planes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.planes_id_seq OWNER TO postgres;

--
-- Name: planes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.planes_id_seq OWNED BY public.planes.id;


--
-- Name: seat_types; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.seat_types (
    id integer NOT NULL,
    name text NOT NULL,
    features text,
    approved boolean DEFAULT false
);


ALTER TABLE public.seat_types OWNER TO postgres;

--
-- Name: seat_types_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seat_types_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seat_types_id_seq OWNER TO postgres;

--
-- Name: seat_types_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.seat_types_id_seq OWNED BY public.seat_types.id;


--
-- Name: seats; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.seats (
    id integer NOT NULL,
    plane_id integer,
    seat_code text NOT NULL,
    "row" integer NOT NULL,
    col integer NOT NULL,
    seat_type_id integer
);


ALTER TABLE public.seats OWNER TO postgres;

--
-- Name: seats_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seats_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seats_id_seq OWNER TO postgres;

--
-- Name: seats_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.seats_id_seq OWNED BY public.seats.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id integer NOT NULL,
    username text NOT NULL,
    password text NOT NULL,
    role text NOT NULL,
    approved boolean DEFAULT false,
    CONSTRAINT users_role_check CHECK ((role = ANY (ARRAY['ADMIN'::text, 'MANAGER'::text, 'CUSTOMER'::text])))
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_seq OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: bookings id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bookings ALTER COLUMN id SET DEFAULT nextval('public.bookings_id_seq'::regclass);


--
-- Name: flight_seat_assignments id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flight_seat_assignments ALTER COLUMN id SET DEFAULT nextval('public.flight_seat_assignments_id_seq'::regclass);


--
-- Name: flight_seat_types id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flight_seat_types ALTER COLUMN id SET DEFAULT nextval('public.flight_seat_types_id_seq'::regclass);


--
-- Name: flights id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flights ALTER COLUMN id SET DEFAULT nextval('public.flights_id_seq'::regclass);


--
-- Name: locations id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.locations ALTER COLUMN id SET DEFAULT nextval('public.locations_id_seq'::regclass);


--
-- Name: planes id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.planes ALTER COLUMN id SET DEFAULT nextval('public.planes_id_seq'::regclass);


--
-- Name: seat_types id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.seat_types ALTER COLUMN id SET DEFAULT nextval('public.seat_types_id_seq'::regclass);


--
-- Name: seats id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.seats ALTER COLUMN id SET DEFAULT nextval('public.seats_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Data for Name: bookings; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: flight_seat_assignments; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: flight_seat_types; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: flights; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: locations; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: planes; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: seat_types; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: seats; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.users VALUES (1, 'admin', 'admin123', 'ADMIN', true);
INSERT INTO public.users VALUES (2, 'manager', 'manager123', 'MANAGER', true);
INSERT INTO public.users VALUES (3, 'customer', 'customer123', 'CUSTOMER', true);
INSERT INTO public.users VALUES (4, 'hacker', 'hacker123', 'ADMIN', false);


--
-- Name: bookings_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.bookings_id_seq', 1, false);


--
-- Name: flight_seat_assignments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.flight_seat_assignments_id_seq', 1, false);


--
-- Name: flight_seat_types_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.flight_seat_types_id_seq', 1, false);


--
-- Name: flights_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.flights_id_seq', 1, false);


--
-- Name: locations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.locations_id_seq', 1, false);


--
-- Name: planes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.planes_id_seq', 1, false);


--
-- Name: seat_types_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seat_types_id_seq', 1, false);


--
-- Name: seats_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seats_id_seq', 1, false);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_id_seq', 4, true);


--
-- Name: bookings bookings_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bookings
    ADD CONSTRAINT bookings_pkey PRIMARY KEY (id);


--
-- Name: flight_seat_assignments flight_seat_assignments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flight_seat_assignments
    ADD CONSTRAINT flight_seat_assignments_pkey PRIMARY KEY (id);


--
-- Name: flight_seat_types flight_seat_types_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flight_seat_types
    ADD CONSTRAINT flight_seat_types_pkey PRIMARY KEY (id);


--
-- Name: flights flights_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flights
    ADD CONSTRAINT flights_pkey PRIMARY KEY (id);


--
-- Name: locations locations_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.locations
    ADD CONSTRAINT locations_pkey PRIMARY KEY (id);


--
-- Name: planes planes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.planes
    ADD CONSTRAINT planes_pkey PRIMARY KEY (id);


--
-- Name: seat_types seat_types_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.seat_types
    ADD CONSTRAINT seat_types_pkey PRIMARY KEY (id);


--
-- Name: seats seats_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.seats
    ADD CONSTRAINT seats_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- Name: bookings bookings_flight_seat_assignment_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bookings
    ADD CONSTRAINT bookings_flight_seat_assignment_id_fkey FOREIGN KEY (flight_seat_assignment_id) REFERENCES public.flight_seat_assignments(id);


--
-- Name: bookings bookings_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bookings
    ADD CONSTRAINT bookings_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: flight_seat_assignments flight_seat_assignments_flight_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flight_seat_assignments
    ADD CONSTRAINT flight_seat_assignments_flight_id_fkey FOREIGN KEY (flight_id) REFERENCES public.flights(id) ON DELETE CASCADE;


--
-- Name: flight_seat_assignments flight_seat_assignments_flight_seat_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flight_seat_assignments
    ADD CONSTRAINT flight_seat_assignments_flight_seat_type_id_fkey FOREIGN KEY (flight_seat_type_id) REFERENCES public.flight_seat_types(id);


--
-- Name: flight_seat_assignments flight_seat_assignments_seat_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flight_seat_assignments
    ADD CONSTRAINT flight_seat_assignments_seat_id_fkey FOREIGN KEY (seat_id) REFERENCES public.seats(id) ON DELETE CASCADE;


--
-- Name: flight_seat_types flight_seat_types_flight_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flight_seat_types
    ADD CONSTRAINT flight_seat_types_flight_id_fkey FOREIGN KEY (flight_id) REFERENCES public.flights(id) ON DELETE CASCADE;


--
-- Name: flight_seat_types flight_seat_types_seat_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flight_seat_types
    ADD CONSTRAINT flight_seat_types_seat_type_id_fkey FOREIGN KEY (seat_type_id) REFERENCES public.seat_types(id) ON DELETE CASCADE;


--
-- Name: flights flights_destination_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flights
    ADD CONSTRAINT flights_destination_id_fkey FOREIGN KEY (destination_id) REFERENCES public.locations(id);


--
-- Name: flights flights_origin_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flights
    ADD CONSTRAINT flights_origin_id_fkey FOREIGN KEY (origin_id) REFERENCES public.locations(id);


--
-- Name: flights flights_plane_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flights
    ADD CONSTRAINT flights_plane_id_fkey FOREIGN KEY (plane_id) REFERENCES public.planes(id) ON DELETE CASCADE;


--
-- Name: seats seats_plane_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.seats
    ADD CONSTRAINT seats_plane_id_fkey FOREIGN KEY (plane_id) REFERENCES public.planes(id) ON DELETE CASCADE;


--
-- Name: seats seats_seat_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.seats
    ADD CONSTRAINT seats_seat_type_id_fkey FOREIGN KEY (seat_type_id) REFERENCES public.seat_types(id) ON DELETE SET NULL;


--
-- PostgreSQL database dump complete
--

