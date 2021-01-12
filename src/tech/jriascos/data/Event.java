package tech.jriascos.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Event {
    private String id;
    private String title;
    private String start_at;
    private String end_at;
    private String description;

    public Event(String id, String title, String start_at, String end_at, String all_day_date, String description) {
        this.id = id;
        this.title = title;
        this.start_at = start_at;
        this.end_at = end_at;
        this.description = description;
    }

    public Event(ResultSet rs) throws SQLException {
        this.id = String.valueOf(rs.getLong("event_id"));
        this.title = rs.getString("title");
        this.start_at = rs.getString("start_at");
        this.end_at = rs.getString("end_at");
        this.description = rs.getString("descript");
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
}
