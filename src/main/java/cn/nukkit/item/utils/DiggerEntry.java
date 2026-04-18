package cn.nukkit.item.utils;

import cn.nukkit.utils.Identifier;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class DiggerEntry {
    private Integer speed;
    private String blockId;
    private final Set<String> tags = new LinkedHashSet<>();
    private final NbtMapBuilder states = NbtMap.builder();

    private DiggerEntry() {
    }

    public static DiggerEntry block(String blockId, int speed) {
        DiggerEntry e = new DiggerEntry();
        e.setBlockId(blockId);
        e.speed = speed;
        return e;
    }

    public static DiggerEntry create() {
        return new DiggerEntry();
    }

    public DiggerEntry speed(int speed) {
        this.speed = speed;
        return this;
    }

    public DiggerEntry setBlockId(String blockId) {
        if (blockId == null || blockId.isBlank()) {
            this.blockId = null;
            return this;
        }

        Identifier id = Identifier.tryParse(blockId.trim());
        this.blockId = id != null ? id.toString() : blockId.trim();
        return this;
    }

    public DiggerEntry addTags(String... quotedTags) {
        if (quotedTags != null) {
            for (String t : quotedTags) {
                String normalized = normalizeTag(t);
                if (normalized != null && !normalized.isBlank()) {
                    this.tags.add(normalized);
                }
            }
        }
        return this;
    }

    public DiggerEntry addAllTags() {
        return addAllStone().addAllWooden().addAllSand().addAllMetal();
    }

    public DiggerEntry addAllStone() {
        return addTags(
                "stone",
                "is_pickaxe_item_destructible"
        );
    }

    public DiggerEntry addAllWooden() {
        return addTags(
                "wood",
                "is_axe_item_destructible"
        );
    }

    public DiggerEntry addAllSand() {
        return addTags(
                "sand",
                "dirt",
                "gravel",
                "is_shovel_item_destructible"
        );
    }

    public DiggerEntry addAllMetal() {
        return addTags(
                "metal",
                "diamond_tier_destructible"
        );
    }

    public DiggerEntry state(String key, int value) {
        putState(key, value);
        return this;
    }

    public DiggerEntry state(String key, boolean value) {
        putState(key, value);
        return this;
    }

    public DiggerEntry state(String key, String value) {
        putState(key, value);
        return this;
    }

    public DiggerEntry states(Map<String, ?> map) {
        if (map == null) return this;
        map.forEach(this::putState);
        return this;
    }

    public DiggerEntry states(Consumer<NbtMap> builder) {
        if (builder != null) builder.accept(states.build());
        return this;
    }

    public NbtMap toNbt() {
        NbtMap block = NbtMap.builder()
                .putString("name", blockId == null ? "" : blockId)
                .putCompound("states", states.isEmpty() ? NbtMap.EMPTY : states.build())
                .putString("tags", buildTagsExpression())
                .build();
        return NbtMap.builder()
                .putCompound("block", block)
                .putInt("speed", speed != null ? speed : 0)
                .build();
    }

    private String buildTagsExpression() {
        if (tags.isEmpty()) return "";
        String joined = String.join(", ", tags);
        return "query.any_tag( " + joined + " )";
    }

    // Helpers
    private static boolean validKey(String key) {
        return key != null && !key.isBlank();
    }

    private static boolean validKV(String key, Object value) {
        return validKey(key) && value != null;
    }

    private void putState(String key, Object v) {
        if (!validKV(key, v)) return;
        if (v instanceof Number n) {
            states.putInt(key, n.intValue());
        } else if (v instanceof Boolean b) {
            states.putBoolean(key, b);
        } else if (v instanceof String s) {
            states.putString(key, s);
        }
    }

    private static String normalizeTag(String raw) {
        if (raw == null) return null;

        String tag = raw.trim();
        if (tag.isEmpty()) return null;

        boolean quoted = tag.length() >= 2 && tag.startsWith("'") && tag.endsWith("'");
        if (quoted) {
            tag = tag.substring(1, tag.length() - 1).trim();
        }

        if (tag.isEmpty()) return null;

        Identifier id = Identifier.tryParse(tag);
        if (id == null) return null;

        return "'" + id + "'";
    }
}
