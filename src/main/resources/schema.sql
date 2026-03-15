CREATE TABLE IF NOT EXISTS clips (
                                     id         INTEGER PRIMARY KEY AUTOINCREMENT,
                                     text       TEXT    NOT NULL,
                                     source     TEXT,
                                     copied_at  TEXT    NOT NULL
);