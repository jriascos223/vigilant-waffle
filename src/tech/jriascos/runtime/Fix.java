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
    /**
     * This is my zoom autojoin using autoit3 for Windows to automate clicking and opening zoom, and the Canvas api for finding events, passwords, and join times.
     * The proccess is as follows:
     * 1. Get all the courses and events for the day from the database (which has been populated with the canvas api and my personal calendar.);
     * 2. Check to see which day of the week it is, then read from json which classes are in that day and make an array from those.
     * 3. Runs a loop that timesout until each start time for each class, then uses autoit and command line arguments to properly connect with code and passcode.
     * @param args Command line arguments lmao
     * @throws IOException
     * @throws SQLException
     * @throws ParseException
     */
    public static void main(String args[]) throws IOException, SQLException, ParseException {


        if (DBUtils.checkDB()) {
            DBUtils.initialize();
        }
        
        ArrayList<Event> dailyCourses = DBUtils.getCourseDay();

        //These are today's classes! yaay :(
        for (Event e : dailyCourses) {
            System.out.println("Title: " + e.getTitle());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            java.util.Date start = sdf.parse(e.getStart());
            String startString = sdf.format(start.getTime() - (18000L * 1000L));
            System.out.println("Start time: " + startString);
            java.util.Date end = sdf.parse(e.getEnd());
            String endString = sdf.format(start.getTime() - (18000L * 1000L));
            System.out.println("End time: " + endString);
            System.out.println("_______________");
        }

        //Waiting loop for each class.
        for (Event e : dailyCourses) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                java.util.Date start = sdf.parse(e.getStart());
                //java.util.Date end = sdf.parse(e.getEnd());
                //millis.add(start.getTime());
                //millis.add(end.getTime()); 
                Long startR = (start.getTime() - (18000L * 1000L)) - System.currentTimeMillis();
                if (startR < 0) {
                    continue;
                }
                try{
                    Thread.sleep(startR);
                    System.out.println("TIME TO JOIN!");
                    e.joinMeeting();
                    //Long endR = (end.getTime() - (18000L * 1000L)) - System.currentTimeMillis();
                    //try{
                    //    Thread.sleep(endR);
                    //    System.out.println("TIME TO LEAVE!");
                    //} catch (InterruptedException f) {
                    //    f.printStackTrace();
                    //}
                } catch(InterruptedException f) {
                    f.printStackTrace();
                }
            }catch(ParseException f) {
                f.printStackTrace();
            }
            
        }


    


        
        
        
        
    }

    
}

//canvas api token
//1030~xhdVvx0v89kdnUwcFVGKZHNo6let4P7F7Pj7dlu7lV41fQXoMGSkaPuab4Plz2V1