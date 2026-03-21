CREATE TABLE IF NOT EXISTS clips (
                                     id         INTEGER PRIMARY KEY AUTOINCREMENT,
                                     text       TEXT    NOT NULL,
                                     source     TEXT,
                                     copied_at  TEXT    NOT NULL
);

CREATE VIRTUAL TABLE IF NOT EXISTS clips_fts
    USING fts5(text, content=clips);

CREATE TRIGGER IF NOT EXISTS clips_ai
    AFTER INSERT ON clips BEGIN
        INSERT INTO clips_fts(rowid, text) VALUES (new.id, new.text);
END;