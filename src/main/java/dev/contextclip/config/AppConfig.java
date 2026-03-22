package dev.contextclip.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

public class AppConfig {
    private final Path configFile;

    public AppConfig(Path folder) {
        this.configFile = folder.resolve("config.properties");
    }

    private int retentionDays = 30;
    private int maxEntries    = 5000;
    private List<String> blockedApps = List.of("KeePass", "1Password", "Bitwarden");

    public void load() throws Exception {
        var props = new Properties();

        if (!Files.exists(configFile)) {

            props.setProperty("retentionDays", String.valueOf(retentionDays));
            props.setProperty("maxEntries",    String.valueOf(maxEntries));
            props.setProperty("blockedApps",   String.join(",", blockedApps));
            try (var out = Files.newOutputStream(configFile)) {
                props.store(out, "ContextClip configuration");
            }
            return;
        }

        try (var in = Files.newInputStream(configFile)) {
            props.load(in);
        }

        retentionDays = Integer.parseInt(props.getProperty("retentionDays", "30"));
        maxEntries    = Integer.parseInt(props.getProperty("maxEntries",    "5000"));
        blockedApps   = List.of(props.getProperty("blockedApps", "KeePass,1Password,Bitwarden").split(","));
    }

    public int retentionDays()        { return retentionDays; }
    public int maxEntries()           { return maxEntries; }
    public List<String> blockedApps() { return blockedApps; }
}
