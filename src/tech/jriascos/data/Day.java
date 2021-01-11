package tech.jriascos.data;

public class Day {
    private String day;
    private String[] course_ids;

    public String[] getCourses() {
        return this.course_ids;
    }

    public String getDay() {
        return this.day;
    }
}
