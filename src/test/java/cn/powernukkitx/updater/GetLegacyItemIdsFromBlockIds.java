package cn.powernukkitx.updater;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;


public class GetLegacyItemIdsFromBlockIds {
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) throws IOException {
        try (InputStream stream = GetLegacyItemIdsFromBlockIds.class.getClassLoader().getResourceAsStream("block_ids.csv")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block_ids.csv");
            }
            LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
            int count = 0;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    count++;
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] parts = line.split(",");
                    if (parts.length > 1 && parts[1].startsWith("minecraft:")) {
                        int id = Integer.parseInt(parts[0]);
                        String name = parts[1];
                        result.put(name, 255 - id);
                    }
                }
            } catch (Exception e) {
                throw new IOException("Error reading the line " + count + " of the block_ids.csv", e);
            }
            LinkedHashMap<String, Integer> reversedMap = new LinkedHashMap<>();
            List<Map.Entry<String, Integer>> entries = new ArrayList<>(result.entrySet());
            Collections.reverse(entries);

            for (Map.Entry<String, Integer> entry : entries) {
                reversedMap.put(entry.getKey(), entry.getValue());
            }
            File file = Path.of("target/legacyItemIdsFromBlockIds.json").toFile();
            if (!file.exists()) file.createNewFile();
            Files.writeString(file.toPath(), gson.toJson(reversedMap), StandardCharsets.UTF_8, StandardOpenOption.WRITE);
        }
    }
}
