--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4 (Debian 17.4-1.pgdg120+2)
-- Dumped by pg_dump version 17.4 (Debian 17.4-1.pgdg120+2)

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
-- Name: billing_details; Type: TABLE; Schema: public; Owner: user
--

CREATE TABLE public.billing_details (
    id bigint NOT NULL,
    address_city character varying(255),
    address_country character varying(255),
    address_street character varying(255),
    address_zip character varying(255),
    user_id bigint
);


ALTER TABLE public.billing_details OWNER TO "user";

--
-- Name: billing_details_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE public.billing_details_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.billing_details_seq OWNER TO "user";

--
-- Name: images; Type: TABLE; Schema: public; Owner: user
--

CREATE TABLE public.images (
    id bigint NOT NULL,
    key character varying(255),
    url character varying(255),
    user_id bigint
);


ALTER TABLE public.images OWNER TO "user";

--
-- Name: images_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE public.images_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.images_seq OWNER TO "user";

--
-- Name: lesson_reservation; Type: TABLE; Schema: public; Owner: user
--

CREATE TABLE public.lesson_reservation (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone,
    status smallint NOT NULL,
    teacher_class_lesson_id bigint NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT lesson_reservation_status_check CHECK (((status >= 0) AND (status <= 2)))
);


ALTER TABLE public.lesson_reservation OWNER TO "user";

--
-- Name: lesson_reservation_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE public.lesson_reservation_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.lesson_reservation_seq OWNER TO "user";

--
-- Name: subjects; Type: TABLE; Schema: public; Owner: user
--

CREATE TABLE public.subjects (
    id bigint NOT NULL,
    is_verified boolean NOT NULL,
    subject character varying(255)
);


ALTER TABLE public.subjects OWNER TO "user";

--
-- Name: subjects_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE public.subjects_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.subjects_seq OWNER TO "user";

--
-- Name: teacher_class_lessons; Type: TABLE; Schema: public; Owner: user
--

CREATE TABLE public.teacher_class_lessons (
    id bigint NOT NULL,
    end_date timestamp(6) without time zone NOT NULL,
    start_date timestamp(6) without time zone NOT NULL,
    teacher_class_id bigint NOT NULL
);


ALTER TABLE public.teacher_class_lessons OWNER TO "user";

--
-- Name: teacher_class_lessons_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE public.teacher_class_lessons_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.teacher_class_lessons_seq OWNER TO "user";

--
-- Name: teacher_class_subjects; Type: TABLE; Schema: public; Owner: user
--

CREATE TABLE public.teacher_class_subjects (
    id bigint NOT NULL,
    subject_id bigint,
    teacher_class_id bigint
);


ALTER TABLE public.teacher_class_subjects OWNER TO "user";

--
-- Name: teacher_class_subjects_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE public.teacher_class_subjects_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.teacher_class_subjects_seq OWNER TO "user";

--
-- Name: teacher_classes; Type: TABLE; Schema: public; Owner: user
--

CREATE TABLE public.teacher_classes (
    id bigint NOT NULL,
    description character varying(255) NOT NULL,
    price integer NOT NULL,
    title character varying(255) NOT NULL,
    teacher_id bigint NOT NULL
);


ALTER TABLE public.teacher_classes OWNER TO "user";

--
-- Name: teacher_classes_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE public.teacher_classes_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.teacher_classes_seq OWNER TO "user";

--
-- Name: teachers; Type: TABLE; Schema: public; Owner: user
--

CREATE TABLE public.teachers (
    id bigint NOT NULL,
    contact_email character varying(255),
    contact_phone character varying(255),
    user_id bigint
);


ALTER TABLE public.teachers OWNER TO "user";

--
-- Name: teachers_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE public.teachers_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.teachers_seq OWNER TO "user";

--
-- Name: transactions; Type: TABLE; Schema: public; Owner: user
--

CREATE TABLE public.transactions (
    id bigint NOT NULL,
    date timestamp(6) without time zone NOT NULL,
    billing_details_id bigint NOT NULL,
    lesson_reservation_id bigint
);


ALTER TABLE public.transactions OWNER TO "user";

--
-- Name: transactions_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE public.transactions_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.transactions_seq OWNER TO "user";

--
-- Name: users; Type: TABLE; Schema: public; Owner: user
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    balance integer NOT NULL,
    date_of_birth date NOT NULL,
    description character varying(255),
    email character varying(255) NOT NULL,
    full_name character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    phone_number character varying(255),
    role smallint NOT NULL,
    profile_img_id bigint,
    CONSTRAINT users_role_check CHECK (((role >= 0) AND (role <= 2)))
);


ALTER TABLE public.users OWNER TO "user";

--
-- Name: users_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE public.users_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_seq OWNER TO "user";

--
-- Data for Name: billing_details; Type: TABLE DATA; Schema: public; Owner: user
--

COPY public.billing_details (id, address_city, address_country, address_street, address_zip, user_id) FROM stdin;
\.


--
-- Data for Name: images; Type: TABLE DATA; Schema: public; Owner: user
--

COPY public.images (id, key, url, user_id) FROM stdin;
\.


--
-- Data for Name: lesson_reservation; Type: TABLE DATA; Schema: public; Owner: user
--

COPY public.lesson_reservation (id, created_at, status, teacher_class_lesson_id, user_id) FROM stdin;
\.


--
-- Data for Name: subjects; Type: TABLE DATA; Schema: public; Owner: user
--

COPY public.subjects (id, is_verified, subject) FROM stdin;
1	t	Matematika
2	t	Fizika
3	t	Kémia
4	t	Biológia
5	t	Történelem
6	t	Földrajz
7	t	Irodalom
8	t	Nyelvtan
9	t	Informatika
10	t	Idegen nyelv
\.


--
-- Data for Name: teacher_class_lessons; Type: TABLE DATA; Schema: public; Owner: user
--

COPY public.teacher_class_lessons (id, end_date, start_date, teacher_class_id) FROM stdin;
\.


--
-- Data for Name: teacher_class_subjects; Type: TABLE DATA; Schema: public; Owner: user
--

COPY public.teacher_class_subjects (id, subject_id, teacher_class_id) FROM stdin;
\.


--
-- Data for Name: teacher_classes; Type: TABLE DATA; Schema: public; Owner: user
--

COPY public.teacher_classes (id, description, price, title, teacher_id) FROM stdin;
\.


--
-- Data for Name: teachers; Type: TABLE DATA; Schema: public; Owner: user
--

COPY public.teachers (id, contact_email, contact_phone, user_id) FROM stdin;
1	urburura@gmail.com	+36201284569	2
\.


--
-- Data for Name: transactions; Type: TABLE DATA; Schema: public; Owner: user
--

COPY public.transactions (id, date, billing_details_id, lesson_reservation_id) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: user
--

COPY public.users (id, balance, date_of_birth, description, email, full_name, password, phone_number, role, profile_img_id) FROM stdin;
1	20000	2005-02-20	\N	tesztdiak@gmail.com	Diák Arnold	$2a$10$VZyjfcNPpcLXqWYnzbPkwOatx4vlmj3z.e7AsFzKV5A1YiFZdSCk2	\N	0	\N
2	20000	2005-02-20	\N	teszttanar@gmail.com	Tanár Frigyes	$2a$10$P23JTM4YEyq4kkaZ2FbKEehnOJMvL1Im3EcDL7eY97SS1UtnYlfdu	\N	1	\N
3	20000	2005-02-20	\N	tesztadmin@gmail.com	Admin Benedict	$2a$10$HMlXyQ5jiDWFKIyeshwyMOz/.d6BMq0hQ7gHOu0nJaYQl442bh292	\N	2	\N
\.


--
-- Name: billing_details_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.billing_details_seq', 1, false);


--
-- Name: images_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.images_seq', 1, false);


--
-- Name: lesson_reservation_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.lesson_reservation_seq', 1, false);


--
-- Name: subjects_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.subjects_seq', 51, true);


--
-- Name: teacher_class_lessons_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.teacher_class_lessons_seq', 1, false);


--
-- Name: teacher_class_subjects_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.teacher_class_subjects_seq', 1, false);


--
-- Name: teacher_classes_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.teacher_classes_seq', 1, false);


--
-- Name: teachers_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.teachers_seq', 1, true);


--
-- Name: transactions_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.transactions_seq', 1, false);


--
-- Name: users_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.users_seq', 51, true);


--
-- Name: billing_details billing_details_pkey; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.billing_details
    ADD CONSTRAINT billing_details_pkey PRIMARY KEY (id);


--
-- Name: images images_pkey; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.images
    ADD CONSTRAINT images_pkey PRIMARY KEY (id);


--
-- Name: lesson_reservation lesson_reservation_pkey; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.lesson_reservation
    ADD CONSTRAINT lesson_reservation_pkey PRIMARY KEY (id);


--
-- Name: subjects subjects_pkey; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.subjects
    ADD CONSTRAINT subjects_pkey PRIMARY KEY (id);


--
-- Name: teacher_class_lessons teacher_class_lessons_pkey; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.teacher_class_lessons
    ADD CONSTRAINT teacher_class_lessons_pkey PRIMARY KEY (id);


--
-- Name: teacher_class_subjects teacher_class_subjects_pkey; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.teacher_class_subjects
    ADD CONSTRAINT teacher_class_subjects_pkey PRIMARY KEY (id);


--
-- Name: teacher_classes teacher_classes_pkey; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.teacher_classes
    ADD CONSTRAINT teacher_classes_pkey PRIMARY KEY (id);


--
-- Name: teachers teachers_pkey; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.teachers
    ADD CONSTRAINT teachers_pkey PRIMARY KEY (id);


--
-- Name: transactions transactions_pkey; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.transactions
    ADD CONSTRAINT transactions_pkey PRIMARY KEY (id);


--
-- Name: users uk6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- Name: teachers ukcd1k6xwg9jqtiwx9ybnxpmoh9; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.teachers
    ADD CONSTRAINT ukcd1k6xwg9jqtiwx9ybnxpmoh9 UNIQUE (user_id);


--
-- Name: transactions ukeffeugkehxls6wach84av7owl; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.transactions
    ADD CONSTRAINT ukeffeugkehxls6wach84av7owl UNIQUE (lesson_reservation_id);


--
-- Name: teacher_class_subjects ukhvjqauvwhhi79l55foom4swa5; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.teacher_class_subjects
    ADD CONSTRAINT ukhvjqauvwhhi79l55foom4swa5 UNIQUE (teacher_class_id, subject_id);


--
-- Name: billing_details uklwwkyl0x5w3t5g9qkrdb3jvem; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.billing_details
    ADD CONSTRAINT uklwwkyl0x5w3t5g9qkrdb3jvem UNIQUE (user_id);


--
-- Name: users uko5ge075m9c88dsmewdvxip6r1; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uko5ge075m9c88dsmewdvxip6r1 UNIQUE (profile_img_id);


--
-- Name: subjects ukqtoqqor08hk6561vb08jcs4b6; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.subjects
    ADD CONSTRAINT ukqtoqqor08hk6561vb08jcs4b6 UNIQUE (subject);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: idx_billing_details_user_id; Type: INDEX; Schema: public; Owner: user
--

CREATE INDEX idx_billing_details_user_id ON public.billing_details USING btree (user_id);


--
-- Name: idx_lesson_reservation_teacher_class_lesson_id; Type: INDEX; Schema: public; Owner: user
--

CREATE INDEX idx_lesson_reservation_teacher_class_lesson_id ON public.lesson_reservation USING btree (teacher_class_lesson_id);


--
-- Name: idx_lesson_reservation_user_id; Type: INDEX; Schema: public; Owner: user
--

CREATE INDEX idx_lesson_reservation_user_id ON public.lesson_reservation USING btree (user_id);


--
-- Name: idx_subject_name; Type: INDEX; Schema: public; Owner: user
--

CREATE INDEX idx_subject_name ON public.subjects USING btree (subject);


--
-- Name: idx_teacher_class_lesson_teacher_class_id; Type: INDEX; Schema: public; Owner: user
--

CREATE INDEX idx_teacher_class_lesson_teacher_class_id ON public.teacher_class_lessons USING btree (teacher_class_id);


--
-- Name: idx_teacher_class_subject_subject_id; Type: INDEX; Schema: public; Owner: user
--

CREATE INDEX idx_teacher_class_subject_subject_id ON public.teacher_class_subjects USING btree (subject_id);


--
-- Name: idx_teacher_class_subject_teacher_class_id; Type: INDEX; Schema: public; Owner: user
--

CREATE INDEX idx_teacher_class_subject_teacher_class_id ON public.teacher_class_subjects USING btree (teacher_class_id);


--
-- Name: idx_teacher_class_teacher_id; Type: INDEX; Schema: public; Owner: user
--

CREATE INDEX idx_teacher_class_teacher_id ON public.teacher_classes USING btree (teacher_id);


--
-- Name: idx_teacher_user_id; Type: INDEX; Schema: public; Owner: user
--

CREATE INDEX idx_teacher_user_id ON public.teachers USING btree (user_id);


--
-- Name: idx_transaction_billing_details_id; Type: INDEX; Schema: public; Owner: user
--

CREATE INDEX idx_transaction_billing_details_id ON public.transactions USING btree (billing_details_id);


--
-- Name: idx_user_email; Type: INDEX; Schema: public; Owner: user
--

CREATE INDEX idx_user_email ON public.users USING btree (email);


--
-- Name: images fk13ljqfrfwbyvnsdhihwta8cpr; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.images
    ADD CONSTRAINT fk13ljqfrfwbyvnsdhihwta8cpr FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: teacher_class_lessons fk4rlgq4sh3bn450iexwmpj9vyk; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.teacher_class_lessons
    ADD CONSTRAINT fk4rlgq4sh3bn450iexwmpj9vyk FOREIGN KEY (teacher_class_id) REFERENCES public.teacher_classes(id);


--
-- Name: transactions fk71i10aivoh9in2ns22dw4e61r; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.transactions
    ADD CONSTRAINT fk71i10aivoh9in2ns22dw4e61r FOREIGN KEY (billing_details_id) REFERENCES public.billing_details(id);


--
-- Name: teacher_class_subjects fk87ixuapmigsl6hke863tlt7bg; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.teacher_class_subjects
    ADD CONSTRAINT fk87ixuapmigsl6hke863tlt7bg FOREIGN KEY (subject_id) REFERENCES public.subjects(id);


--
-- Name: billing_details fk88k6p3c5d8jdybw39vqb3r9ql; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.billing_details
    ADD CONSTRAINT fk88k6p3c5d8jdybw39vqb3r9ql FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: teachers fkb8dct7w2j1vl1r2bpstw5isc0; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.teachers
    ADD CONSTRAINT fkb8dct7w2j1vl1r2bpstw5isc0 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: lesson_reservation fkcimpbkanu3h5byp7w8u0b7w6j; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.lesson_reservation
    ADD CONSTRAINT fkcimpbkanu3h5byp7w8u0b7w6j FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: users fkfrio5pppscwg8isd3t5310i14; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fkfrio5pppscwg8isd3t5310i14 FOREIGN KEY (profile_img_id) REFERENCES public.images(id);


--
-- Name: lesson_reservation fkjdmqkr1oqocnsg2gmahits5l8; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.lesson_reservation
    ADD CONSTRAINT fkjdmqkr1oqocnsg2gmahits5l8 FOREIGN KEY (teacher_class_lesson_id) REFERENCES public.teacher_class_lessons(id);


--
-- Name: teacher_class_subjects fkkvddm51t4nnockq3pu6e4x3k; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.teacher_class_subjects
    ADD CONSTRAINT fkkvddm51t4nnockq3pu6e4x3k FOREIGN KEY (teacher_class_id) REFERENCES public.teacher_classes(id);


--
-- Name: transactions fko7j6j0h5x0201jftfp77icvy1; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.transactions
    ADD CONSTRAINT fko7j6j0h5x0201jftfp77icvy1 FOREIGN KEY (lesson_reservation_id) REFERENCES public.lesson_reservation(id);


--
-- Name: teacher_classes fktfqjapwyb01dh5kb7dcx00p0p; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.teacher_classes
    ADD CONSTRAINT fktfqjapwyb01dh5kb7dcx00p0p FOREIGN KEY (teacher_id) REFERENCES public.teachers(id);


--
-- PostgreSQL database dump complete
--

