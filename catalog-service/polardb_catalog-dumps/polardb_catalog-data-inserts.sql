--
-- PostgreSQL database dump
--

-- Dumped from database version 15.4 (Debian 15.4-1.pgdg120+1)
-- Dumped by pg_dump version 15.4 (Debian 15.4-1.pgdg120+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: book; Type: TABLE DATA; Schema: public; Owner: user
--

INSERT INTO public.book VALUES (2, 'THOMAS VITALE', '9781617298424', 5.75, 'Cloud Native Spring in Action at the North Pole', '2024-08-24 16:24:43.214021', '2024-08-24 16:24:43.214021', 1, 'Artic Endeavor Books');
INSERT INTO public.book VALUES (4, 'THOMAS VITALE', '9781617298425', 19.9, 'Cloud Native Spring in Action at the Antartic', '2024-08-24 16:29:42.681757', '2024-08-24 16:29:42.681757', 1, 'Wacky Publishers for weirdo''s');
INSERT INTO public.book VALUES (3, 'Lyra Silverstar', '1234567891', 19.75, 'Northern Lights', '2024-08-24 16:28:52.935271', '2024-08-24 16:31:28.118619', 2, 'Polarsophia');
INSERT INTO public.book VALUES (1, 'Martin Štefanko and Jan Martiška', '9781633438958', 19.99, 'Artic & Antartic Quarkus in Action', '2024-08-24 16:15:13.119058', '2024-08-24 16:38:37.975843', 2, 'Manning Publications Co.');


--
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: user
--

INSERT INTO public.flyway_schema_history VALUES (1, '1', 'Initial schema', 'SQL', 'V1__Initial_schema.sql', -310890001, 'user', '2024-08-24 15:50:35.16525', 21, true);
INSERT INTO public.flyway_schema_history VALUES (2, '2', 'Add publisher column to book table', 'SQL', 'V2__Add_publisher_column_to_book_table.sql', 1134863653, 'user', '2024-08-24 15:50:35.209935', 2, true);
INSERT INTO public.flyway_schema_history VALUES (3, '3', 'Alter author column size to book table', 'SQL', 'V3__Alter_author_column_size_to_book_table.sql', 701399166, 'user', '2024-08-24 15:50:35.22857', 2, true);


--
-- Name: book_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.book_id_seq', 4, true);


--
-- PostgreSQL database dump complete
--

