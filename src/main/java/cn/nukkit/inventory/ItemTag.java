package cn.nukkit.inventory;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.Config;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
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

    public static List<String> getTags(String namespaceId) {
        var result = ITEM_2_TAGS.get(namespaceId);
        if (result == null) return null;
        return result.stream().toList();
    }

    @NotNull
    public static Set<String> getTagSet(String namespaceId) {
        return Collections.unmodifiableSet(ITEM_2_TAGS.getOrDefault(namespaceId, Set.of()));
    }

    public static List<String> getItems(String tag) {
        var result = TAG_2_ITEMS.get(tag);
        if (result == null) return null;
        return result.stream().toList();
    }

    @NotNull
    public static Set<String> getItemSet(String tag) {
        return Collections.unmodifiableSet(TAG_2_ITEMS.getOrDefault(tag, Set.of()));
    }

    /**
     * Register item tags for the given item namespaceId.
     * This is a server-side only method, DO NOT affect the client.
     *
     * @param namespaceId The item namespaceId
     * @param tags        The tags to register
     */
    @Since("1.19.50-r3")
    @PowerNukkitXOnly
    public static void registerItemTag(String namespaceId, Collection<String> tags) {
        var tagSet = ITEM_2_TAGS.get(namespaceId);
        if (tagSet != null) tagSet.addAll(tags);
        else ITEM_2_TAGS.put(namespaceId, new HashSet<>(tags));
        for (var tag : tags) {
            var itemSet = TAG_2_ITEMS.get(tag);
            if (itemSet != null) itemSet.add(namespaceId);
            else TAG_2_ITEMS.put(tag, new HashSet<>(Collections.singleton(namespaceId)));
        }
    }

}
