package dev.contextclip.domain;

import java.time.Instant;

public record ClipEntry(String text, String source, Instant copiedAt) {

    public ClipEntry {
        if (text == null || text.isBlank()){
            throw new IllegalArgumentException("Texto copiado não pode ser nulo ou vazio, valor recebido: '" + text + "'");
        }
    }
}
