package tech.jriascos.data;

import java.sql.ResultSet;

public class Course {
    private String id;
    private String name;
    private String course_code;
    private String start_at;
    private String end_at;

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getCourseCode() {
        return this.course_code;
    }

    public String getStart() {
        return this.start_at;
    }

    public String getEnd() {
        return this.end_at;
    }
}
