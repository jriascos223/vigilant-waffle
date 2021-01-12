package tech.jriascos.controller;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.spi.DirStateFactory.Result;

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
                stmt.setString(2, c.getName().replaceAll("\\s", ""));
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

                    Requests rq = new Requests("https://canvas.instructure.com/api/v1/calendar_events?access_token=1030~xhdVvx0v89kdnUwcFVGKZHNo6let4P7F7Pj7dlu7lV41fQXoMGSkaPuab4Plz2V1&all_events=1&per_page=100&context_codes[]=course_" + Long.toString(rs.getLong("course_id")));
                    String data = rq.makeRequest();
                    Gson gson = new Gson();
                    Event[] courseEvents = gson.fromJson(data, Event[].class);
                    for (Event e : courseEvents) {
                        PreparedStatement insertion = semester.prepareStatement("INSERT INTO events VALUES (?, ?, ?, ?, ?, ?)");
                        insertion.setLong(1, Long.parseLong(e.getId()));
                        insertion.setString(2, e.getTitle());
                        insertion.setString(3, e.getStart());
                        insertion.setString(4, e.getEnd());
                        Pattern pattern = Pattern.compile(".*blank\">[^\\d]+([\\d\\s]*).*");
                        Matcher matcher = pattern.matcher(e.getDescription());
                        matcher.matches();
                        insertion.setString(5, matcher.group(1));
                        insertion.setLong(6, rs.getLong("course_id"));
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

	public static ArrayList<Event> getCourseDay() throws SQLException, FileNotFoundException {
        Connection conn = getConnection();
        Gson gson = new Gson();
        ArrayList<Event> courseday = new ArrayList<Event>();
        String classpathDirectory = DBUtils.getClasspathDir();
        BufferedReader br = new BufferedReader(new FileReader(classpathDirectory + "\\data\\week.json"));
        Day today;
        Day[] week = gson.fromJson(br, Day[].class);
        /* Calendar calendar = Calendar.getInstance();
        java.util.Date date = calendar.getTime();
        String td = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime()); */
        String td = "Monday";

        for (int i = 0; i < week.length; i++) {
            for (int j = 0; j < week[i].getCourses().length; j++) {
                System.out.println(week[i].getCourses()[j].getName());
            }
            System.out.println("_____");
        }

        for (Day d : week) {
            if (d.getDay().equals(td)) {
                System.out.println("Today is a " + td + "!");
                today = d;
            }
        }

        System.out.println("Here are today's events...");

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM events WHERE start_at LIKE '%2020-10-30%'");
            int i = 0;
            while (rs.next()) {
                courseday.add(new Event(rs));
            }

        }catch (SQLException e) {
            System.out.println(e);
        }




        return courseday;
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
