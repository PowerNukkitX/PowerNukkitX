package cn.powernukkitx.updater;

import cn.nukkit.math.Vector2;
import cn.nukkit.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;


public class SpawnCommonBlockClass {
    static Vector2 range = new Vector2(755, 784);
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    static String targetPath = "src/main/java/cn/nukkit/block";
    static String version = "1.20.0-r2";

    public static void main(String[] args) throws IOException {
        try (InputStream stream = GetLegacyItemIdsFromBlockIds.class.getClassLoader().getResourceAsStream("block_ids.csv")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block_ids.csv");
            }
            Map<Integer, String> result = new TreeMap<>(Integer::compare);
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
                        if (inRange(id)) {
                            result.put(id, StringUtils.fastSplit(":", parts[1]).get(1));
                        }
                    }
                }
            } catch (Exception e) {
                throw new IOException("Error reading the line " + count + " of the block_ids.csv", e);
            }
            List<String> registerInfo = new ArrayList<>();
            List<String> blockIDInfo = new ArrayList<>();
            for (var entry : result.entrySet()) {
                String[] split = entry.getValue().split("_");
                StringBuilder className = new StringBuilder("Block");
                for (var s : split) {
                    className.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1));
                }
                StringBuilder name = new StringBuilder();
                for (int i = 0; i < split.length; i++) {
                    name.append(Character.toUpperCase(split[i].charAt(0))).append(split[i].substring(1));
                    if (i != split.length - 1) {
                        name.append(" ");
                    }
                }
                if (spawnBlockClass(className.toString(), entry.getValue().toUpperCase(Locale.ENGLISH), name.toString(), version)) {
                    registerInfo.add("list[" + entry.getValue().toUpperCase(Locale.ENGLISH) + "] = " + className + ".class;//" + entry.getKey());
                    blockIDInfo.add("""
                            @PowerNukkitXOnly
                            @Since(%s)
                            int %s = %s;
                            """.formatted(version, entry.getValue().toUpperCase(Locale.ENGLISH), entry.getKey()));
                }
            }
            System.out.println("BlockID.java: ");
            blockIDInfo.forEach(System.out::println);
            System.out.println("RegisterInfo: ");
            registerInfo.forEach(System.out::println);
        }
    }

    private static boolean inRange(int id) {
        return id <= range.y && id >= range.x;
    }

    private static boolean spawnBlockClass(String className, String idFieldName, String blockName, String version) throws IOException {
        String s = """
                package cn.nukkit.block;

                import cn.nukkit.api.Since;
                import cn.nukkit.api.PowerNukkitXOnly;
                                
                import static cn.nukkit.block.BlockID.[idFieldName];
                                
                @PowerNukkitXOnly
                @Since("[version]")
                public class [className]{
                    public [className]() {
                    }

                    public int getId() {
                        return [idFieldName];
                    }

                    public String getName() {
                        return "[blockName]";
                    }
                }""".replace("[className]", className).replace("[idFieldName]", idFieldName).replace("[blockName]", blockName).replace("[version]", version);
        Path path = Path.of(targetPath).resolve(className + ".java");
        File file = path.toFile();
        if (file.exists()) {
            System.out.println(path + " already exist");
            return false;
        } else {
            file.createNewFile();
            Files.writeString(path, s, StandardCharsets.UTF_8, StandardOpenOption.WRITE);
            System.out.println("success spawn class: " + path);
            return true;
        }
    }
}
