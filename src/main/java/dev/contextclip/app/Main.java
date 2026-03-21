package dev.contextclip.app;

import dev.contextclip.capture.ClipboardWatcher;
import dev.contextclip.capture.WindowsCaptor;
import dev.contextclip.repository.DbMigration;
import dev.contextclip.repository.SqliteRepository;
import dev.contextclip.ui.HistoryPopup;
import dev.contextclip.ui.SystemTrayManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) throws Exception {

        var home   = System.getProperty("user.home");
        var folder = Path.of(home, ".contextclip");
        var dbPath = folder.resolve("clips.db").toString();

        Files.createDirectories(folder);

        try (var conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            new DbMigration().run(conn);
        }

        var repository = new SqliteRepository(dbPath);
        var contextCaptor = new WindowsCaptor();
        var historyPopup  = new HistoryPopup(repository);
        var trayManager   = new SystemTrayManager(historyPopup);
        trayManager.init();
        var watcher    = new ClipboardWatcher(repository, contextCaptor);

        watcher.start();

        System.out.println("ContextClip rodando — pressione Ctrl+C para parar.");

        Thread.currentThread().join();
    }
}
