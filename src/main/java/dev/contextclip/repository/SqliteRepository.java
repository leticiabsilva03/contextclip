package dev.contextclip.repository;

import dev.contextclip.domain.ClipEntry;

import java.sql.DriverManager;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SqliteRepository implements ClipRepository{

    private final String dbPath;

    public SqliteRepository(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public void save(ClipEntry clipEntry) throws Exception {
        try (var conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             var ps = conn.prepareStatement("INSERT INTO clips (text, source, copied_at) VALUES (?, ?, ?)")) {
            ps.setString(1, clipEntry.text());
            ps.setString(2, clipEntry.source());
            ps.setString(3, clipEntry.copiedAt().toString());
            ps.executeUpdate();
        }
    }

    @Override
    public List<ClipEntry> findRecent(int limit) throws Exception {
        try (var conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             var ps   = conn.prepareStatement(
                     "SELECT text, source, copied_at FROM clips ORDER BY copied_at DESC LIMIT ?")) {
            ps.setInt(1, limit);
            var rs   = ps.executeQuery();
            var list = new ArrayList<ClipEntry>();
            while (rs.next()) {
                list.add(new ClipEntry(
                        rs.getString("text"),
                        rs.getString("source"),
                        Instant.parse(rs.getString("copied_at"))
                ));
            }
            return list;
        }
    }
}
