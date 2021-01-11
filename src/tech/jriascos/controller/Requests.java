package tech.jriascos.controller;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Requests {
    private String url;

    public Requests(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public String makeRequest() throws IOException {
        URL url = new URL(this.url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        InputStream inputstream = conn.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputstream));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        conn.disconnect();

        return sb.toString();
    }

}

//System.out.println(conn.getResponseMessage());

        /* Ability limber = new Ability(new Subability("limber", "url"), false, 1);
        Ability imposter = new Ability(new Subability("imposter", "url"), true, 3);
        Pokemon test = new Pokemon(new Ability[]{limber, imposter}, 101);
        String json = gson.toJson(test);
        System.out.println(json); */
        /* Pokemon pokemon = gson.fromJson(sb.toString(), Pokemon.class);
        System.out.println(pokemon.getExperience()); */
