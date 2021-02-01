package tech.jriascos.runtime;

import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import com.google.gson.Gson;

import tech.jriascos.data.Event;
import tech.jriascos.controller.Requests;
import tech.jriascos.controller.DBUtils;

public class Fix {
    public static void main(String args[]) throws IOException, SQLException {
        //this literally just makes it so that I can start the autoit3 script from java
        //code somewhere here eventually is supposed to talk to canvas, fill in database with info, grab proper username and password, and place the values there
        //the numbers and the code are to be replaced with variables
        /* String meetingid = "209 185 1085";
        String passcode = "0aD1jm";
        Runtime.getRuntime().exec("autoit3 LAStart.au3 \"" + meetingid + "\" \"" + passcode + "\"", null, new File("C:\\Users\\josep\\Desktop\\vigilant-waffle")); */
        //Requests canvas = new Requests("https://canvas.instructure.com/api/v1/calendar_events?access_token=1030~xhdVvx0v89kdnUwcFVGKZHNo6let4P7F7Pj7dlu7lV41fQXoMGSkaPuab4Plz2V1&all_events=1&per_page=100&context_codes[]=" + "course_10300000000040031");

        //Gets all the courses and events and creates database (if not already stored in database, this is done every so often to keep stuff fresh)
        //gets the appropriate classes for the current day of the week (from DATABASE, makes array or something of event classes)
        //runs loop which runs script for each event class with a proper parameter (this includes waiting for start and stop)


        if (DBUtils.checkDB()) {
            DBUtils.initialize();
        }

        ArrayList<Event> dailyCourses = DBUtils.getCourseDay();

        for (Event e : dailyCourses) {
            System.out.println("Title: " + e.getTitle());
            System.out.println("Start time: " + e.getStart());
            System.out.println("End time: " + e.getEnd());
            System.out.println("_______________");
        }

        ArrayList<Long> millis = new ArrayList<Long>();

        for (Event e : dailyCourses) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ss");
            try {
                java.util.Date start = sdf.parse(e.getStart());
                java.util.Date end = sdf.parse(e.getEnd());
                millis.add(start.getTime());
                millis.add(end.getTime());
            }catch(ParseException f) {
                f.printStackTrace();
            }
            
        }

        for (Long l : millis) {
            System.out.println(l);
        }

        //DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        



        /* long timestamp = 1610310840000L;
        long millis = timestamp - System.currentTimeMillis();
        System.out.println(timestamp);
        System.out.println(System.currentTimeMillis());
        try{
            Thread.sleep(millis);
            System.out.println("waited until now!");
        } catch (InterruptedException e) {
            System.out.println(e);
        } */

        /* DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse("2020-10-30"); */


        
        
        
        
    }

    
}

//canvas api token
//1030~xhdVvx0v89kdnUwcFVGKZHNo6let4P7F7Pj7dlu7lV41fQXoMGSkaPuab4Plz2V1