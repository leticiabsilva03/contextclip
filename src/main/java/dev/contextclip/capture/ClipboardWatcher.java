package dev.contextclip.capture;

import dev.contextclip.domain.ClipEntry;
import dev.contextclip.repository.ClipRepository;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.time.Instant;

public class ClipboardWatcher {

    private final ClipRepository repository;

    public ClipboardWatcher(ClipRepository repository) {
        this.repository = repository;
    }

    public void start() {
        Thread.ofVirtual().start(() -> {
            String lastSeen = "";
            var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            while (true) {
                try {
                    var contents = clipboard.getContents(null);
                    if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        var text = (String) contents.getTransferData(DataFlavor.stringFlavor);
                        if (!text.equals(lastSeen)) {
                            repository.save(new ClipEntry(text, "unknown", Instant.now()));
                            lastSeen = text;
                        }
                    }
                } catch (IllegalStateException e) {
                    System.err.println("Clipboard em uso, tentando novamente...");
                } catch (Exception e) {
                    System.err.println("Erro no watcher: " + e.getMessage());
                }

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

}
