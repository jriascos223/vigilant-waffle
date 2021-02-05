package tech.jriascos.controller;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

/**
 * This file is in charge of making connections to the database, as well as setting it up in the first place (in the case that it doesn't exist).
 * It also populates the database with the courses and the events that the user is taking, by making JSON api calls to the Canvas api.
 * @author Joseph Riascos
 */

public class DBUtils {
    /**
     * This method sets up the database by reading the config/setup.sql file line for line and splitting it up into commands by the ";" character.
     * It then populates the created database tables with events and courses with the populateCourses() and populateEvents() methods.
     * @throws SQLException
     * @throws IOException
     */
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

    /**
     * Fills the database file found in data/semester.db with the courses that the user might be taking. 
     * Makes an api call to canvas API.
     * @throws IOException
     * @throws SQLException
     */
    public static void populateCourses() throws IOException, SQLException {
        Requests rq = new Requests("https://canvas.instructure.com/api/v1/courses?access_token=1030~xhdVvx0v89kdnUwcFVGKZHNo6let4P7F7Pj7dlu7lV41fQXoMGSkaPuab4Plz2V1&per_page=100");
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

    /**
     * Fills the database file found in data/semester.db with the events that the user has in each day.
     * Makes an api call to canvas API.
     * @throws IOException
     * @throws SQLException
     */
    public static void populateEvents() throws IOException, SQLException {
        //append to end of link to use the course calendars instead of my own personal one
                    //&context_codes[]=course_" + Long.toString(rs.getLong("course_id"))
                    Connection semester = DBUtils.getConnection();
                    Requests rq = new Requests("https://canvas.instructure.com/api/v1/calendar_events?access_token=1030~xhdVvx0v89kdnUwcFVGKZHNo6let4P7F7Pj7dlu7lV41fQXoMGSkaPuab4Plz2V1&all_events=1&per_page=150");
                    String data = rq.makeRequest();
                    Gson gson = new Gson();
                    Event[] courseEvents = gson.fromJson(data, Event[].class);
                    
                    for (Event e : courseEvents) {
                        System.out.println(e.getDescription());
                        PreparedStatement insertion = semester.prepareStatement("INSERT INTO events VALUES (?, ?, ?, ?, ?, ?)");
                        insertion.setLong(1, Long.parseLong(e.getId()));
                        insertion.setString(2, e.getTitle().replaceAll("\\s", ""));
                        insertion.setString(3, e.getStart());
                        insertion.setString(4, e.getEnd());
                        //Pattern pattern = Pattern.compile(".*blank\">[^\\d]+([\\d\\s]*).*");  old, and for course calendars
                        Pattern pattern = Pattern.compile(".*j\\/(\\d+).*");
                        Matcher matcher = pattern.matcher(e.getDescription());
                        if (matcher.matches()) {
                            insertion.setString(5, matcher.group(1));
                        }else {
                            insertion.setString(5, "-1");
                        }
                        Pattern pattern2 = Pattern.compile(".*Passcode:(.*);<.*");
                        Matcher matcher2 = pattern2.matcher(e.getDescription());
                        if (matcher2.matches()) {
                            insertion.setString(6, matcher2.group(1));
                        }else {
                            insertion.setString(6, "");
                        }
                        
                        insertion.executeUpdate();
                    }
        /* try (Connection semester = DBUtils.getConnection()) {
            PreparedStatement stmt = semester.prepareStatement("SELECT * FROM courses");
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    
                }
            }
            
        }catch (SQLException e) {
            System.out.println(e);
        } */
        System.out.println("events!");
    }

    /**
     * Gets a connection to the database file, data/semester.db
     * @return Connection object that allows for communication with database file in question.
     * @throws SQLException
     * @throws IOException
     */
    public static Connection getConnection() throws SQLException, IOException {
        if (new File("data/semester.db").exists()) {
            return DriverManager.getConnection("jdbc:sqlite:data/semester.db");
        }else {
            File directory = new File("data");
            boolean success = directory.mkdir();
            File f = new File("data/semester.db");
            boolean success2 = f.createNewFile();
            return DriverManager.getConnection("jdbc:sqlite:data/semester.db");
        }
        
    }

    /**
     * This is supposed to conditionally refresh the database (cause it doesn't make sense to refresh the database every day).
     * So the first day of every month or every 2 weeks would make sense (but I've been too lazy to actually get this working).
     * @return
     */
	public static boolean checkDB() {
		return true;
    }
    
    /**
     * Gets the courses for the current day
     * @return
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
	public static ArrayList<Event> getCourseDay() throws SQLException, FileNotFoundException, IOException {
        Connection conn = getConnection();
        Gson gson = new Gson();
        ArrayList<Event> courseday = new ArrayList<Event>();
        String classpathDirectory = DBUtils.getClasspathDir();
        BufferedReader br = new BufferedReader(new FileReader(classpathDirectory + "\\config\\week.json"));
        
        Day[] week = gson.fromJson(br, Day[].class);


        //I have no clue how to do time/date stuff
        //this is my best guess
        Day today = new Day();
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter format1 = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH); //this is for creating the yyyy-MM-dd format for the sql statement
        Calendar calendar = Calendar.getInstance();
        java.util.Date date = calendar.getTime();
        String numericalDate = format1.format(ldt);
        //This is for creating a string to compare to the day names in week.json
        String td = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date);

        for (Day d : week) {
            if (d.getDay().equals(td)) {
                System.out.println("Today is " + numericalDate + "!");
                System.out.println("Today is a " + td + "!");
                today = d;
            }
        }

        


        System.out.println("Here are today's events...");

        try {
            //put this in a loop for the times, and append the time to the end of the sql statement
            for (int i = 0; i < today.getCourses().length; i++) {
                String statement = "SELECT * FROM events WHERE start_at LIKE '%" + numericalDate + "T" + today.getCourses()[i].getStart() + "%'";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(statement);
                while (rs.next()) {
                    courseday.add(new Event(rs));
                }
            }
            
        

        }catch (SQLException e) {
            System.out.println(e);
        }




        return courseday;
    }

    private static String getClasspathDir() {
        String classpath = System.getProperty("user.dir");
        /* String classpath = System.getProperty("java.class.path", ".");
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
        } */
        return classpath;
    }
}
