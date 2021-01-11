package tech.jriascos.controller;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import tech.jriascos.data.Course;
import tech.jriascos.data.Day;
import tech.jriascos.data.Event;

public class DBUtils {
    public static void initialize() throws SQLException, IOException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             BufferedReader br = new BufferedReader(new FileReader(new File("config/setup.sql")))) {
                 String line;
                 StringBuffer sql = new StringBuffer();

                 while ((line = br.readLine()) != null) {
                     sql.append(line);
                 }

                 for (String command : sql.toString().split(";")) {
                     if(!command.strip().isEmpty()) {
                         stmt.executeUpdate(command);
                     }
                 }
             } catch(Exception e) {
                 System.out.println(e);
             }
        populateCourses();
        populateEvents();
    }

    public static void populateCourses() throws IOException, SQLException {
        Requests rq = new Requests("https://canvas.instructure.com/api/v1/courses?access_token=1030~xhdVvx0v89kdnUwcFVGKZHNo6let4P7F7Pj7dlu7lV41fQXoMGSkaPuab4Plz2V1");
        String data = rq.makeRequest();
        Gson gson = new Gson();
        Course[] courses = gson.fromJson(data, Course[].class);

        try(Connection semester = DBUtils.getConnection()) {
            for (Course c : courses) {
                PreparedStatement stmt = semester.prepareStatement("INSERT INTO courses VALUES (?, ?, ?)");
                stmt.setLong(1, Long.parseLong(c.getId()));
                stmt.setString(2, c.getName());
                stmt.setString(3, c.getCourseCode());
                stmt.executeUpdate();
            }
        }
    }

    public static void populateEvents() throws IOException {
        try (Connection semester = DBUtils.getConnection()) {
            PreparedStatement stmt = semester.prepareStatement("SELECT * FROM courses");
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    //System.out.println("https://canvas.instructure.com/api/v1/calendar_events?access_token=1030~xhdVvx0v89kdnUwcFVGKZHNo6let4P7F7Pj7dlu7lV41fQXoMGSkaPuab4Plz2V1&all_events=1&per_page=100&context_codes[]=course_" + Long.toString(rs.getLong("course_id")));

                    Requests rq = new Requests("https://canvas.instructure.com/api/v1/calendar_events?access_token=1030~xhdVvx0v89kdnUwcFVGKZHNo6let4P7F7Pj7dlu7lV41fQXoMGSkaPuab4Plz2V1&all_events=1&per_page=100&context_codes[]=course_" + Long.toString(rs.getLong("course_id")));
                    String data = rq.makeRequest();
                    Gson gson = new Gson();
                    Event[] courseEvents = gson.fromJson(data, Event[].class);
                    for (Event e : courseEvents) {
                        PreparedStatement insertion = semester.prepareStatement("INSERT INTO events VALUES (?, ?, ?, ?, ?, ?, ?)");
                        insertion.setLong(1, Long.parseLong(e.getId()));
                        insertion.setString(2, e.getTitle());
                        insertion.setString(3, e.getStart());
                        insertion.setString(4, e.getEnd());
                        insertion.setString(5, e.getDay());
                        //System.out.println(e.getDescription().matches(".*blank\">[^\\d]+([\\d\\s]*).*"));
                        Pattern pattern = Pattern.compile(".*blank\">[^\\d]+([\\d\\s]*).*");
                        //Pattern pattern = Pattern.compile("(.*)");
                        Matcher matcher = pattern.matcher(e.getDescription());
                        matcher.matches();
                        insertion.setString(6, matcher.group(1));
                        insertion.setLong(7, rs.getLong("course_id"));
                        insertion.executeUpdate();
                    }
                    
                }
            }
            
        }catch (SQLException e) {
            System.out.println(e);
        }
        System.out.println("events!");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:data/semester.db");
    }

	public static boolean checkDB() {
		return true;
	}

	public static Event[] getCourseDay() throws SQLException, FileNotFoundException {
        Connection conn = getConnection();
        Gson gson = new Gson();
        String classpathDirectory = DBUtils.getClasspathDir();
        BufferedReader br = new BufferedReader(new FileReader(classpathDirectory + "\\data\\week.json"));
        Day[] week = gson.fromJson(br, Day[].class);

        return null;
    }

    private static String getClasspathDir() {
        String classpath = System.getProperty("java.class.path", ".");
        String[] splitClasspathDir = classpath.split(";");
        String classpathDirectory = "";
        for (String s : splitClasspathDir) {
            if (s.matches(".*lib\\\\.*")) {
                Pattern pattern = Pattern.compile("(.*)\\\\(lib\\\\)");
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    classpathDirectory = matcher.group(1);
                }
            }
        }
        return classpathDirectory;
    }
}
