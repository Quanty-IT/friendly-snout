package migrations;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MigrationRunner {

    public static void runAll(Connection conn) throws SQLException {
        runAll(conn, discover());
    }

    public static void runAll(Connection conn, List<Migration> migrations) throws SQLException {
        ensureMigrationsTable(conn);
        migrations.sort(Comparator.comparing(Migration::name));
        for (Migration m : migrations) {
            if (!isApplied(conn, m.name())) {
                try {
                    conn.setAutoCommit(false);
                    m.up(conn);
                    markApplied(conn, m.name());
                    conn.commit();
                    System.out.println("Applied: " + m.name());
                } catch (SQLException ex) {
                    conn.rollback();
                    throw ex;
                } finally {
                    conn.setAutoCommit(true);
                }
            }
        }
    }

    private static List<Migration> discover() {
        List<Migration> list = new ArrayList<>();
        String pkgPath = "migrations";
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            Enumeration<URL> urls = cl.getResources(pkgPath);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                switch (url.getProtocol()) {
                    case "file" -> loadFromDir(url, list, cl);
                    case "jar"  -> loadFromJar(url, list, cl);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to scan migrations", e);
        }
        return list;
    }

    private static void loadFromDir(URL url, List<Migration> out, ClassLoader cl) {
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(url.toURI()), "M*.class")) {
            for (Path p : ds) tryLoad("migrations." + stripClass(p.getFileName().toString()), out, cl);
        } catch (Exception ignored) {}
    }

    private static void loadFromJar(URL url, List<Migration> out, ClassLoader cl) {
        try {
            JarURLConnection jc = (JarURLConnection) url.openConnection();
            try (JarFile jar = jc.getJarFile()) {
                Enumeration<JarEntry> it = jar.entries();
                while (it.hasMoreElements()) {
                    JarEntry e = it.nextElement();
                    String n = e.getName();
                    if (n.startsWith("migrations/") && n.endsWith(".class")) {
                        String simple = stripClass(n.substring("migrations/".length()));
                        if (simple.startsWith("M")) tryLoad("migrations." + simple, out, cl);
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    private static String stripClass(String s) { return s.replaceAll("\\.class$", ""); }

    private static void tryLoad(String fqcn, List<Migration> out, ClassLoader cl) {
        try {
            Class<?> c = Class.forName(fqcn, true, cl);
            if (!Migration.class.isAssignableFrom(c)) return;
            if (Modifier.isAbstract(c.getModifiers())) return;
            Migration m = (Migration) c.getDeclaredConstructor().newInstance();
            out.add(m);
        } catch (Throwable ignored) {}
    }

    private static void ensureMigrationsTable(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS public.schema_migrations(
                    name text PRIMARY KEY,
                    applied_at timestamptz NOT NULL DEFAULT now()
                );
            """);
        }
    }

    private static boolean isApplied(Connection conn, String name) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM public.schema_migrations WHERE name = ?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private static void markApplied(Connection conn, String name) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO public.schema_migrations(name) VALUES (?)")) {
            ps.setString(1, name);
            ps.executeUpdate();
        }
    }
}
