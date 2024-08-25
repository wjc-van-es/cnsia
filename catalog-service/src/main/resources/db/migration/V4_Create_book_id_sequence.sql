-- This sequence is created implicitly when the table was empty and
-- all records were inserted through the application
-- when we want to restore an older version of the database with insert scripts we already need this sequence
-- in place to set the correct accumulating value for the id of the next record to be inserted.
CREATE SEQUENCE if not exists public.book_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.book_id_seq OWNED BY public.book.id;

ALTER TABLE ONLY public.book ALTER COLUMN id SET DEFAULT nextval('public.book_id_seq'::regclass);