package org.powernukkit.tools;

import cn.nukkit.Server;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import lombok.Data;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RuntimeItemIdUpdater {
    public static void main(String[] args) throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        List<RuntimeItem> runtimeItems;
        try(InputStream resourceAsStream = Server.class.getClassLoader().getResourceAsStream("runtime_item_ids.json");
            Reader reader = new InputStreamReader(Objects.requireNonNull(resourceAsStream), StandardCharsets.UTF_8);
            JsonReader jsonReader = new JsonReader(reader);
        ) {
            runtimeItems = gson.fromJson(jsonReader, LIST);
        }


        Map<String, RuntimeItem> itemNameToNukkitRegistry = new LinkedHashMap<>();
        for (RuntimeItem runtimeItem : runtimeItems) {
            itemNameToNukkitRegistry.put(runtimeItem.name, runtimeItem);
        }

        List<RuntimeItem> requiredItems;
        try(InputStream resourceAsStream = Server.class.getClassLoader().getResourceAsStream("runtime_item_states.json");
            Reader reader = new InputStreamReader(Objects.requireNonNull(resourceAsStream), StandardCharsets.UTF_8);
            JsonReader jsonReader = new JsonReader(reader);
        ) {
            requiredItems = gson.fromJson(jsonReader, LIST);
        }

        for (RuntimeItem entry : requiredItems) {
            RuntimeItem runtimeItem = itemNameToNukkitRegistry.get(entry.name);
            if (runtimeItem == null) {
                itemNameToNukkitRegistry.put(entry.name, entry);
                continue;
            }
            runtimeItem.id = entry.id;
        }

        runtimeItems = new ArrayList<>(itemNameToNukkitRegistry.values());

        try (FileWriter writer = new FileWriter("src/main/resources/runtime_item_ids.json");
            BufferedWriter bufferedWriter = new BufferedWriter(writer)
        ) {
            gson.toJson(runtimeItems, LIST, bufferedWriter);
        }
    }

    private static Type LIST = new TypeToken<List<RuntimeItem>>(){}.getType();

    @Data class RuntimeItem {
        private String name;
        private Integer id;
        private Integer oldId;
        private Integer oldData;
        private Boolean deprecated;
    }
}
