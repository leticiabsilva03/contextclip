package dev.contextclip.repository;

import java.sql.Connection;

public class DbMigration {
    public void run(Connection connection) throws Exception {
        try (var input = getClass().getResourceAsStream("/schema.sql");
             var stmt = connection.createStatement()) {
            var sql = new String(input.readAllBytes());
            stmt.execute(sql);
        }
    }
}
