package tech.jriascos.data;

public class Event {
    private String id;
    private String title;
    private String start_at;
    private String end_at;
    private String all_day_date;
    private String description;

    public Event(String id, String title, String start_at, String end_at, String all_day_date, String description) {
        this.id = id;
        this.title = title;
        this.start_at = start_at;
        this.end_at = end_at;
        this.all_day_date = all_day_date;
        this.description = description;
    }

    public String getId() {
        return this.id;
    }

	public String getDescription() {
		return this.description;
    }
    
    public String getTitle() {
        return this.title;
    }

    public String getStart() {
        return this.start_at;
    }
    
    public String getEnd() {
        return this.end_at;
    }

    public String getDay() {
        return this.all_day_date;
    }
}
