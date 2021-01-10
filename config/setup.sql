DROP TABLE IF EXISTS events;
CREATE TABLE IF NOT EXISTS events (
    event_id INTEGER PRIMARY KEY,
    title TEXT NOT NULL,
    start_at TEXT NOT NULL,
    end_at TEXT NOT NULL,
    all_day_date TEXT,
    descript TEXT NOT NULL
);