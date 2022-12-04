package cn.nukkit.inventory;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.Config;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@PowerNukkitXOnly
@Since("1.19.50-r2")
public final class ItemTag {
    private static final Map<String, Set<String>> TAG_2_ITEMS = new HashMap<>();
    private static final Map<String, Set<String>> ITEM_2_TAGS = new HashMap<>();

    static {
        final var config = new Config(Config.JSON);
        try {
            config.load(Server.class.getModule().getResourceAsStream("tag_2_items.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        config.getAll().forEach((k, v) -> TAG_2_ITEMS.put(k, ((List<?>) v).stream().map(s -> (String) s).collect(Collectors.toSet())));

        try {
            config.load(Server.class.getModule().getResourceAsStream("item_2_tags.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        config.getAll().forEach((k, v) -> ITEM_2_TAGS.put(k, ((List<?>) v).stream().map(s -> (String) s).collect(Collectors.toSet())));
    }

    public static Map<String, Set<String>> getTag2Items() {
        return new HashMap<>(TAG_2_ITEMS);
    }

    public static Map<String, Set<String>> getItem2Tags() {
        return new HashMap<>(ITEM_2_TAGS);
    }

    public static List<String> getTags(String itemName) {
        var result = ITEM_2_TAGS.get(itemName);
        if (result == null) return null;
        return result.stream().toList();
    }

    public static List<String> getItems(String tag) {
        var result = TAG_2_ITEMS.get(tag);
        if (result == null) return null;
        return result.stream().toList();
    }
}
