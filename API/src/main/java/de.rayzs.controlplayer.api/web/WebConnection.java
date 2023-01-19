package de.rayzs.controlplayer.api.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

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
            while (scanner.hasNextLine()) builder.append(scanner.next());
            result = builder.toString();
        }catch (ConnectException | FileNotFoundException unknownException) {
            result = "unknown";
        } catch (IOException exception) {
            result = "exception";
            exception.printStackTrace();
        }
        return this;
    }

    public String getResult() { return result; }
}
