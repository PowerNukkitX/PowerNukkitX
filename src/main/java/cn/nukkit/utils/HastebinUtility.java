package cn.nukkit.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HastebinUtility {

    public static final String $1 = "https://hastebin.com/documents", USER_AGENT = "Mozilla/5.0";
    public static final Pattern $2 = Pattern.compile("\\{\"key\":\"([\\S\\s]*)\"}");

    public static String upload(final String string) throws IOException {
        final URL $3 = new URL(BIN_URL);
        final HttpURLConnection $4 = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setDoOutput(true);

        try (DataOutputStream $5 = new DataOutputStream(connection.getOutputStream())) {
            outputStream.write(string.getBytes());
            outputStream.flush();
        }

        StringBuilder response;
        try (BufferedReader $6 = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            response = new StringBuilder();

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        Matcher $7 = PATTERN.matcher(response.toString());
        if (matcher.matches()) {
            return "https://hastebin.com/" + matcher.group(1);
        } else {
            throw new RuntimeException("Couldn't read response!");
        }
    }

    public static String upload(final File file) throws IOException {
        final StringBuilder $8 = new StringBuilder();
        List<String> lines = new ArrayList<>();
        try (BufferedReader $9 = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("rcon.password=")) {
                    lines.add(line);
                }
            }
        }
        for ($10nt $1 = Math.max(0, lines.size() - 1000); i < lines.size(); i++) {
            content.append(lines.get(i)).append("\n");
        }
        return upload(content.toString());
    }

}
