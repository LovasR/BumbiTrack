package hu.tibipi.bumbitrack.core;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NetUtils {
    private NetUtils() {
        throw new IllegalStateException("Utility class");
    }
    public static String downloadJsonFromURL(String urlString) throws IOException {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlString);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        try (InputStreamReader isr = new InputStreamReader(connection.getInputStream())){

            BufferedReader reader = new BufferedReader(isr);
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } finally {
            connection.disconnect();
        }

        return result.toString();
    }
}
