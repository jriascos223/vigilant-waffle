package tech.jriascos.data;

public class Event {
    private int id;
    private String title;
    private String start_at;
    private String end_at;
    private String all_day_date;
    private String description;

    public Event(int id, String title, String start_at, String end_at, String all_day_date, String description) {
        this.id = id;
        this.title = title;
        this.start_at = start_at;
        this.end_at = end_at;
        this.all_day_date = all_day_date;
        this.description = description;
    }
}
