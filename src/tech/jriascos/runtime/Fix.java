package tech.jriascos.runtime;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.Gson;

import tech.jriascos.data.Event;
import tech.jriascos.data.Requests;

public class Fix {
    public static void main(String args[]) throws IOException, SQLException {
        //this literally just makes it so that I can start the autoit3 script from java
        //code somewhere here eventually is supposed to talk to canvas, fill in database with info, grab proper username and password, and place the values there
        //the numbers and the code are to be replaced with variables
        /* String meetingid = "209 185 1085";
        String passcode = "0aD1jm";
        Runtime.getRuntime().exec("autoit3 LAStart.au3 \"" + meetingid + "\" \"" + passcode + "\"", null, new File("C:\\Users\\josep\\Desktop\\vigilant-waffle")); */
        //https://pokeapi.co/api/v2/pokemon/ditto
        Requests canvas = new Requests("https://canvas.instructure.com/api/v1/calendar_events?access_token=1030~xhdVvx0v89kdnUwcFVGKZHNo6let4P7F7Pj7dlu7lV41fQXoMGSkaPuab4Plz2V1&all_events=1&per_page=100&context_codes[]=", "course_10300000000040031");
        String data = canvas.makeRequest();
        Gson gson = new Gson();
        Event[] cal103 = gson.fromJson(data, Event[].class);

        long timestamp = 1610310840000L;
        long millis = timestamp - System.currentTimeMillis();
        System.out.println(timestamp);
        System.out.println(System.currentTimeMillis());
        try{
            Thread.sleep(millis);
            System.out.println("waited until now!");
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        /* private String id;
        private String title;
        private String start_at;
        private String end_at;
        private String all_day_date;
        private String description; */


        initialize();
        try(Connection semester = Fix.getConnection()) {
            for (int i = 0; i < cal103.length; i++) {
                PreparedStatement stmt = semester.prepareStatement("INSERT INTO events VALUES(?, ?, ?, ?, ?, ?)");
                stmt.setLong(1, Long.parseLong(cal103[i].getId()));
                stmt.setString(2, cal103[i].getTitle());
                stmt.setString(3, cal103[i].getStart());
                stmt.setString(4, cal103[i].getEnd());
                stmt.setString(5, cal103[i].getDay());
                stmt.setString(6, cal103[i].getDesc());
                stmt.executeUpdate();
            }
            
        }catch(SQLException e) {
            System.out.println(e);
        }
        
        
        
        
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:data/semester.db");
    }

    private static void initialize() throws SQLException, IOException {
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
    }
}

//canvas api token
//1030~xhdVvx0v89kdnUwcFVGKZHNo6let4P7F7Pj7dlu7lV41fQXoMGSkaPuab4Plz2V1