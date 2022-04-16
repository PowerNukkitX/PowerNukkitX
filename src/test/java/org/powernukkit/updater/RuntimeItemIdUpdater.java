/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2021  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.powernukkit.updater;

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
        /*
        Pre-requisites:
        - Run src/test/java/org/powernukkit/updater/AllResourcesDownloader.java
        - Run mvn clean package
         */
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        List<RuntimeItem> runtimeItems;
        try (InputStream resourceAsStream = Server.class.getClassLoader().getResourceAsStream("runtime_item_ids.json");
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
        try (InputStream resourceAsStream = Server.class.getClassLoader().getResourceAsStream("runtime_item_states.json");
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

    private static Type LIST = new TypeToken<List<RuntimeItem>>() {
    }.getType();

    @Data
    class RuntimeItem {
        private String name;
        private Integer id;
        private Integer oldId;
        private Integer oldData;
        private Boolean deprecated;
    }
}
