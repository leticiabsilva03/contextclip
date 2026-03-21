package dev.contextclip.repository;

import dev.contextclip.domain.ClipEntry;

import java.util.List;

public interface ClipRepository {

    void save (ClipEntry clipEntry) throws Exception;
    List<ClipEntry> findRecent(int limit) throws Exception;

    List<ClipEntry> search(String query) throws Exception;
}
