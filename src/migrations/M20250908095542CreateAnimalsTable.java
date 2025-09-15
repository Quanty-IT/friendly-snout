package migrations;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class M20250908095542CreateAnimalsTable implements Migration {

    @Override
    public String name() {
        return "20250908095542_createanimalstable";
    }

    @Override
    public void up(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("""
                CREATE EXTENSION IF NOT EXISTS "pgcrypto";
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS public.animals (
                    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                    name text NOT NULL,
                    sex text NOT NULL CHECK (sex IN ('male','female')),
                    species text NOT NULL CHECK (species IN ('dog','cat')),
                    breed text NOT NULL CHECK (breed IN (
                        'mixed-breed',
                        'shih-tzu',
                        'yorkshire-terrier',
                        'german-spitz',
                        'french-bulldog',
                        'poodle',
                        'lhasa-apso',
                        'golden-retriever',
                        'rottweiler',
                        'labrador-retriever',
                        'pug',
                        'german-shepherd',
                        'border-collie',
                        'long-haired-chihuahua',
                        'belgian-malinois',
                        'maltese'
                    )),
                    size text NOT NULL CHECK (size IN ('small','medium','large')),
                    color text NOT NULL CHECK (color IN ('black','white','gray','brown','golden','cream','tan','speckled')),
                    birthdate date,
                    microchip text,
                    rga text,
                    castrated boolean DEFAULT false,
                    fiv text CHECK (fiv IN ('yes','no','not-tested')) ,
                    felv text CHECK (felv IN ('yes','no','not-tested')),
                    status text CHECK (status IN ('quarantine','sheltered','adopted','lost')),
                    notes text,
                    created_at timestamptz NOT NULL DEFAULT now(),
                    updated_at timestamptz NOT NULL DEFAULT now()
                );
            """);
        }
    }

    @Override
    public void down(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS public.animals;");
        }
    }
}