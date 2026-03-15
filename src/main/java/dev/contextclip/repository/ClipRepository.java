package dev.contextclip.repository;

import dev.contextclip.domain.ClipEntry;

import java.util.List;

public interface ClipRepository {

    void save (ClipEntry clipEntry);
    List<ClipEntry> findRecent(int limit);
}
