package tech.jriascos.runtime;

import java.io.*;
import tech.jriascos.data.Requests;

public class Fix {
    public static void main(String args[]) throws IOException{
        //this literally just makes it so that I can start the autoit3 script from java
        //code somewhere here eventually is supposed to talk to canvas, fill in database with info, grab proper username and password, and place the values there
        //the numbers and the code are to be replaced with variables
        /* String meetingid = "209 185 1085";
        String passcode = "0aD1jm";
        Runtime.getRuntime().exec("autoit3 LAStart.au3 \"" + meetingid + "\" \"" + passcode + "\"", null, new File("C:\\Users\\josep\\Desktop")); */
        //https://pokeapi.co/api/v2/pokemon/ditto
        Requests canvas = new Requests("https://canvas.instructure.com/api/v1/calendar_events?access_token=1030~xhdVvx0v89kdnUwcFVGKZHNo6let4P7F7Pj7dlu7lV41fQXoMGSkaPuab4Plz2V1&all_events=1");
        String data = canvas.makeRequest();
        System.out.println(data);
    }
}

//canvas api token
//1030~xhdVvx0v89kdnUwcFVGKZHNo6let4P7F7Pj7dlu7lV41fQXoMGSkaPuab4Plz2V1