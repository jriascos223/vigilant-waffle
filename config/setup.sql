DROP TABLE IF EXISTS events;
CREATE TABLE IF NOT EXISTS events (
    event_id INTEGER PRIMARY KEY,
    title TEXT NOT NULL,
    start_at TEXT NOT NULL,
    end_at TEXT NOT NULL,
    descript TEXT NOT NULL,
    course_id INTEGER NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses (course_id)
);

DROP TABLE IF EXISTS courses;
CREATE TABLE IF NOT EXISTS courses (
    course_id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    code TEXT NOT NULL
);