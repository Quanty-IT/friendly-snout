package migrations;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class M20250908093708CreateUsersTable implements Migration {

    @Override
    public String name() {
        return "20250908093708_createuserstable";
    }

    @Override
    public void up (Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("""
                CREATE EXTENSION IF NOT EXISTS "pgcrypto";
                CREATE EXTENSION IF NOT EXISTS "citext";
                CREATE EXTENSION IF NOT EXISTS "pgcrypto"; -- ensure bcrypt via crypt()
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS public.users (
                    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                    name text NOT NULL,
                    email citext NOT NULL UNIQUE,
                    password text NOT NULL, -- password hash (bcrypt)
                    reset_code text,
                    reset_code_expires_at timestamptz,
                    created_at timestamptz NOT NULL DEFAULT now(),
                    updated_at timestamptz NOT NULL DEFAULT now(),
                    CONSTRAINT users_email_not_empty CHECK (email <> ''),
                    CONSTRAINT users_name_not_empty CHECK (name <> '')
                );
            """);

            st.execute("""
                INSERT INTO public.users (name, email, password)
                VALUES (
                    'QuantIT',
                    'quantit.focinhoamigo@gmail.com',
                    crypt('QuantIT@007', gen_salt('bf'))
                )
                ON CONFLICT (email) DO NOTHING;
            """);
        }
    }

    @Override
    public void down (Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS public.users;");
        }
    }
}