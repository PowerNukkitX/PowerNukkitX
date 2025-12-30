package cn.nukkit.item.utils;

import cn.nukkit.nbt.tag.CompoundTag;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;


public final class DiggerEntry {
    private Integer speed;
    private String  blockId;
    private final Set<String> tags = new LinkedHashSet<>();
    private final CompoundTag states = new CompoundTag();

    private DiggerEntry() {}

    public static DiggerEntry block(String blockId, int speed) {
        DiggerEntry e = new DiggerEntry();
        e.blockId = blockId;
        e.speed   = speed;
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
        this.blockId = blockId;
        return this;
    }

    public DiggerEntry addTags(String... quotedTags) {
        if (quotedTags != null) {
            for (String t : quotedTags) {
                if (t != null && !t.isBlank()) {
                    this.tags.add(t.trim());
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
                "'stone'", "'metal'", "'diamond_pick_diggable'", "'mob_spawner'", "'rail'",
                "'slab_block'", "'stair_block'", "'smooth stone slab'", "'sandstone slab'",
                "'cobblestone slab'", "'brick slab'", "'stone bricks slab'", "'quartz slab'",
                "'nether brick slab'"
        );
    }

    public DiggerEntry addAllWooden() {
        return addTags("'wood'", "'pumpkin'", "'plant'");
    }

    public DiggerEntry addAllSand() {
        return addTags("'sand'", "'dirt'", "'gravel'", "'grass'", "'snow'");
    }

    public DiggerEntry addAllMetal() {
        return addTags("'metal'");
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

    public DiggerEntry states(Consumer<CompoundTag> builder) {
        if (builder != null) builder.accept(states);
        return this;
    }

    public CompoundTag toNbt() {
        CompoundTag block = new CompoundTag()
                .putString("name", blockId == null ? "" : blockId)
                .putCompound("states", states.isEmpty() ? new CompoundTag() : states.copy())
                .putString("tags", buildTagsExpression());

        return new CompoundTag()
                .putCompound("block", block)
                .putInt("speed", speed != null ? speed : 0);
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
}
