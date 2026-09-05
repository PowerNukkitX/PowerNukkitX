package org.powernukkitx.level.format.anvil;

import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.powernukkitx.block.BlockChest;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityChest;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author iYozem
 */

@Slf4j
public final class AnvilChunkConverter {

    private static final int SECTION_SIZE = 16 * 16 * 16;

    private static final Map<String, String> BLOCK_ENTITY_IDS = Map.ofEntries(
            Map.entry("Chest", "Chest"),
            Map.entry("Trap", "Dispenser"),
            Map.entry("Dropper", "Dropper"),
            Map.entry("Furnace", "Furnace"),
            Map.entry("Hopper", "Hopper"),
            Map.entry("Cauldron", "BrewingStand"), // Java's "Cauldron" tile entity is the brewing stand
            Map.entry("Sign", "Sign"),
            Map.entry("MobSpawner", "MobSpawner"),
            Map.entry("EnchantTable", "EnchantTable"),
            Map.entry("Beacon", "Beacon"),
            Map.entry("Skull", "Skull"),
            Map.entry("Banner", "Banner"),
            Map.entry("Comparator", "Comparator"),
            Map.entry("FlowerPot", "FlowerPot"),
            Map.entry("RecordPlayer", "Jukebox"),
            Map.entry("DLDetector", "DaylightDetector"),
            Map.entry("EndGateway", "EndGateway"),
            Map.entry("Control", "CommandBlock"));

    private static final Set<String> CONTAINERS = Set.of("Chest", "Dispenser", "Dropper", "Furnace", "Hopper", "BrewingStand");

    private static final Pattern JSON_TEXT = Pattern.compile("\"text\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");

    private final JavaBlockMapper blockMapper;
    private final JavaItemMapper itemMapper;

    public AnvilChunkConverter(JavaBlockMapper blockMapper, JavaItemMapper itemMapper) {
        this.blockMapper = blockMapper;
        this.itemMapper = itemMapper;
    }

    public void convert(NbtMap javaChunkRoot, IChunk target) {
        NbtMap level = javaChunkRoot.getCompound("Level");
        if (level == null || level.isEmpty()) {
            return;
        }
        convertBlocks(level, target);
        convertBlockEntities(level, target);
    }

    private void convertBlocks(NbtMap level, IChunk target) {
        List<NbtMap> sections = level.getList("Sections", NbtType.COMPOUND);
        if (sections == null) {
            return;
        }
        for (NbtMap section : sections) {
            int sectionY = section.getByte("Y") & 0xFF;
            byte[] blocks = section.getByteArray("Blocks");
            if (blocks.length < SECTION_SIZE) {
                continue;
            }
            byte[] data = section.getByteArray("Data");
            byte[] add = section.containsKey("Add") ? section.getByteArray("Add") : null;

            int baseY = sectionY << 4;
            for (int i = 0; i < SECTION_SIZE; i++) {
                int id = blocks[i] & 0xFF;
                if (add != null) {
                    id |= nibble(add, i) << 8;
                }
                if (id == 0) {
                    continue;
                }
                int meta = nibble(data, i);

                BlockState state = this.blockMapper.map(id, meta);
                if (state == null) {
                    state = this.blockMapper.fallback();
                }

                int x = i & 0x0F;
                int z = (i >> 4) & 0x0F;
                int y = baseY + (i >> 8);
                target.setBlockState(x, y, z, state, 0);
            }
        }
    }

    private void convertBlockEntities(NbtMap level, IChunk target) {
        List<NbtMap> tileEntities = level.getList("TileEntities", NbtType.COMPOUND);
        if (tileEntities == null) {
            return;
        }
        List<Map.Entry<String, CompoundTag>> built = new ArrayList<>();
        for (NbtMap te : tileEntities) {
            String bedrockId = BLOCK_ENTITY_IDS.get(te.getString("id"));
            if (bedrockId == null) {
                continue;
            }
            try {
                CompoundTag tag = new CompoundTag()
                        .putString("id", bedrockId)
                        .putInt("x", te.getInt("x"))
                        .putInt("y", te.getInt("y"))
                        .putInt("z", te.getInt("z"))
                        .putByte("isMovable", 1);

                if (CONTAINERS.contains(bedrockId)) {
                    tag.putList("Items", translateItems(te));
                } else if ("Sign".equals(bedrockId)) {
                    applySignText(te, tag);
                }
                built.add(Map.entry(bedrockId, tag));
            } catch (Exception e) {
                log.debug("[AnvilConverter] failed to build tile entity '{}': {}", te.getString("id"), e.toString());
            }
        }

        List<BlockEntityChest> chestEntities = new ArrayList<>();
        for (Map.Entry<String, CompoundTag> entry : built) {
            try {
                BlockEntity be = BlockEntity.createBlockEntity(entry.getKey(), target, entry.getValue());
                if (be instanceof BlockEntityChest chest) {
                    chestEntities.add(chest);
                }
            } catch (Exception e) {
                log.debug("[AnvilConverter] failed to create block entity '{}': {}", entry.getKey(), e.toString());
            }
        }

        pairChests(chestEntities);
    }

    private static void pairChests(List<BlockEntityChest> chests) {
        for (int a = 0; a < chests.size(); a++) {
            BlockEntityChest ca = chests.get(a);
            if (ca.isPaired() || !(ca.getBlock() instanceof BlockChest ba)) {
                continue;
            }
            BlockFace faceA = ba.getBlockFace();
            for (int b = a + 1; b < chests.size(); b++) {
                BlockEntityChest cb = chests.get(b);
                if (cb.isPaired() || !(cb.getBlock() instanceof BlockChest bb)) {
                    continue;
                }
                int dx = (int) (ca.x - cb.x);
                int dy = (int) (ca.y - cb.y);
                int dz = (int) (ca.z - cb.z);
                if (dy != 0 || Math.abs(dx) + Math.abs(dz) != 1) {
                    continue;
                }
                BlockFace.Axis adjacencyAxis = dx != 0 ? BlockFace.Axis.X : BlockFace.Axis.Z;
                if (bb.getBlockFace() == faceA && adjacencyAxis != faceA.getAxis()) {
                    ca.pairWith(cb);
                    break;
                }
            }
        }
    }

    private ListTag<CompoundTag> translateItems(NbtMap te) {
        ListTag<CompoundTag> items = new ListTag<>(Tag.TAG_Compound);
        if (this.itemMapper == null) {
            return items;
        }
        List<NbtMap> javaItems = te.getList("Items", NbtType.COMPOUND);
        if (javaItems == null) {
            return items;
        }
        for (NbtMap javaItem : javaItems) {
            String javaItemId = javaItem.getString("id");
            if (javaItemId == null || javaItemId.isEmpty()) {
                continue;
            }
            int damage = javaItem.getShort("Damage");
            JavaItemMapper.BedrockItem bedrock = this.itemMapper.map(javaItemId, damage);
            if (bedrock == null) {
                continue;
            }
            items.add(new CompoundTag()
                    .putString("Name", bedrock.name())
                    .putShort("Damage", bedrock.damage())
                    .putByte("Count", javaItem.getByte("Count"))
                    .putByte("Slot", javaItem.getByte("Slot")));
        }
        return items;
    }

    private static void applySignText(NbtMap te, CompoundTag tag) {
        StringBuilder text = new StringBuilder();
        for (int line = 1; line <= 4; line++) {
            if (line > 1) {
                text.append('\n');
            }
            text.append(plainText(te.getString("Text" + line)));
        }
        tag.putCompound("FrontText", new CompoundTag().putString("Text", text.toString()));
        tag.putCompound("BackText", new CompoundTag().putString("Text", ""));
    }

    private static String plainText(String raw) {
        if (raw == null || raw.isEmpty() || "null".equals(raw)) {
            return "";
        }
        if (raw.charAt(0) == '{') {
            Matcher m = JSON_TEXT.matcher(raw);
            if (m.find()) {
                return m.group(1).replace("\\\"", "\"").replace("\\\\", "\\");
            }
            return "";
        }
        if (raw.length() >= 2 && raw.charAt(0) == '"' && raw.charAt(raw.length() - 1) == '"') {
            return raw.substring(1, raw.length() - 1);
        }
        return raw;
    }

    private static int nibble(byte[] arr, int i) {
        if (arr == null || (i >> 1) >= arr.length) {
            return 0;
        }
        int b = arr[i >> 1] & 0xFF;
        return (i & 1) == 0 ? (b & 0x0F) : (b >> 4);
    }
}
