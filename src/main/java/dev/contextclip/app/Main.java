package dev.contextclip.app;

import dev.contextclip.capture.ClipboardWatcher;
import dev.contextclip.capture.HotkeyManager;
import dev.contextclip.capture.WindowsCaptor;
import dev.contextclip.config.AppConfig;
import dev.contextclip.repository.DbMigration;
import dev.contextclip.repository.SqliteRepository;
import dev.contextclip.ui.HistoryPopup;
import dev.contextclip.ui.SystemTrayManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

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

        var config = new AppConfig(folder);
        config.load();
        var cutoff = Instant.now().minus(config.retentionDays(), ChronoUnit.DAYS);
        repository.deleteOlderThan(cutoff);

        var historyPopup  = new HistoryPopup(repository);
        var trayManager   = new SystemTrayManager(historyPopup);
        trayManager.init();
        var hotkeyManager = new HotkeyManager(historyPopup);
        hotkeyManager.register();
        var watcher    = new ClipboardWatcher(repository, contextCaptor);

        watcher.start();

        System.out.println("ContextClip rodando — pressione Ctrl+C para parar.");

        Thread.currentThread().join();
    }
}
