package cn.nukkit.item;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

@Since("1.4.0.0-PN")
@UtilityClass
@Log4j2
public class RuntimeItems {

    private static final Gson GSON = new Gson();
    private static final Type ENTRY_TYPE = new TypeToken<ArrayList<Entry>>(){}.getType();

    private static final RuntimeItemMapping itemPalette;

    static {
        log.debug("Loading runtime items...");
        Collection<Entry> entries;
        try(InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_item_ids.json")) {
            if (stream == null) {
                throw new AssertionError("Unable to load runtime_item_ids.json");
            }

            InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            entries = GSON.fromJson(reader, ENTRY_TYPE);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        itemPalette = new RuntimeItemMapping(entries);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static RuntimeItemMapping getRuntimeMapping() {
        return itemPalette;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static int getId(int fullId) {
        return (short) (fullId >> 16);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static int getData(int fullId) {
        return ((fullId >> 1) & 0x7fff);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static int getFullId(int id, int data) {
        return (((short) id) << 16) | ((data & 0x7fff) << 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static int getNetworkId(int networkFullId) {
        return networkFullId >> 1;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static boolean hasData(int id) {
        return (id & 0x1) != 0;
    }

    @ToString
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class Entry {
        String name;
        int id;
        Integer oldId;
        Integer oldData;
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        Boolean deprecated;
        @PowerNukkitXOnly
        @Since("1.6.0.0-PNX")
        Boolean isComponentItem = false;
    }
}
