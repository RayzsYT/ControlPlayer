package de.rayzs.controlplayer.api.web;

import java.net.HttpURLConnection;
import java.io.IOException;
import java.util.Scanner;
import java.net.URL;

public class WebConnection {

    private String result = null;

    public WebConnection connect(String rawUrl) {
        try {
            URL url = new URL(rawUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            connection.setRequestProperty("Accept", "text/html");
            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder builder = new StringBuilder();
            while(scanner.hasNextLine()) builder.append(scanner.next());
            result = builder.toString();
        } catch (IOException exception) {
            result = "error";
            exception.printStackTrace();
        }
        return this;
    }

    public String getResult() { return result; }
}
