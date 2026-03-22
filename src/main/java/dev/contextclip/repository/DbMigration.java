package dev.contextclip.repository;

import java.sql.Connection;

public class DbMigration {
    public void run(Connection connection) throws Exception {
        try (var stmt = connection.createStatement()) {
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS clips (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "text TEXT NOT NULL, " +
                            "source TEXT, " +
                            "copied_at TEXT NOT NULL" +
                            ")"
            );
            stmt.execute(
                    "CREATE VIRTUAL TABLE IF NOT EXISTS clips_fts " +
                            "USING fts5(text, content=clips)"
            );
            stmt.execute(
                    "CREATE TRIGGER IF NOT EXISTS clips_ai " +
                            "AFTER INSERT ON clips BEGIN " +
                            "INSERT INTO clips_fts(rowid, text) VALUES (new.id, new.text); " +
                            "END"
            );
        }

        try (var stmt = connection.createStatement()) {
            try {
                stmt.execute("ALTER TABLE clips ADD COLUMN starred INTEGER DEFAULT 0");
            } catch (Exception e) {
            }
        }
    }
}
