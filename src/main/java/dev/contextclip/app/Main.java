package dev.contextclip.app;

import dev.contextclip.capture.ClipboardWatcher;
import dev.contextclip.capture.WindowsCaptor;
import dev.contextclip.repository.DbMigration;
import dev.contextclip.repository.SqliteRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) throws Exception {

        // 1. caminho do banco
        var home   = System.getProperty("user.home");
        var folder = Path.of(home, ".contextclip");
        var dbPath = folder.resolve("clips.db").toString();

        // 2. cria a pasta ~/.contextclip se não existir
        Files.createDirectories(folder);

        // 3. roda a migration — cria a tabela se não existir
        try (var conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            new DbMigration().run(conn);
        }

        // 4. cria o repository e o watcher
        var repository = new SqliteRepository(dbPath);
        var contextCaptor = new WindowsCaptor();
        var watcher    = new ClipboardWatcher(repository, contextCaptor);

        // 5. inicia o watcher
        watcher.start();

        System.out.println("ContextClip rodando — pressione Ctrl+C para parar.");

        // 6. mantém a JVM viva
        Thread.currentThread().join();
    }
}
