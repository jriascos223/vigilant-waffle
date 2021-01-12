package tech.jriascos.data;

public class Day {
    private String day;
    private Course[] courseday;

    public Course[] getCourses() {
        return this.courseday;
    }

    public String getDay() {
        return this.day;
    }
}
