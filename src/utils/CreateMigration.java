package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreateMigration {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java utils.CreateMigration MigrationName");
            return;
        }

        String baseName = args[0];
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String className = "M" + timestamp + baseName;
        String filePath = "src/migrations/" + className + ".java";

        String content = """
                package migrations;

                import java.sql.Connection;
                import java.sql.SQLException;
                import java.sql.Statement;

                public class %s implements Migration {

                    @Override
                    public String name() {
                        return "%s";
                    }

                    @Override
                    public void up(Connection conn) throws SQLException {
                        try (Statement st = conn.createStatement()) {
                            // TODO: Write the creation SQL here
                        }
                    }

                    @Override
                    public void down(Connection conn) throws SQLException {
                        try (Statement st = conn.createStatement()) {
                            // TODO: Write the rollback SQL here
                        }
                    }
                }
                """.formatted(className, timestamp + "_" + baseName.toLowerCase());

        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(content);
            writer.close();
            System.out.println("Migration created in: " + filePath);
        } catch (IOException e) {
            System.out.println("Error creating migration: " + e.getMessage());
        }
    }
}
