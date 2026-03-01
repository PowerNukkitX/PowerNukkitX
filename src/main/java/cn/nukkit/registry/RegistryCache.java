package cn.nukkit.registry;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.item.customitem.data.CreativeCategory;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemCategory;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemGroup;
import cn.nukkit.utils.BlockColor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

import java.nio.file.*;
import java.util.*;

/**
 * Manages a binary on-disk cache of parsed registry state so subsequent server
 * starts can skip expensive JSON/NBT parsing and rebuild steps.
 *
 * <p>Cached registries:
 * <ul>
 *   <li>{@link ItemRuntimeIdRegistry} – full state (maps + palette)</li>
 *   <li>{@link BlockStateRegistry}   – vanilla block colour map</li>
 *   <li>{@link CreativeItemRegistry} – groups, items, index constants</li>
 *   <li>{@link RecipeRegistry}       – encoded {@code CraftingDataPacket} buffer</li>
 *   <li>{@link BiomeRegistry}        – name map, string list, uncompressed definition NBT</li>
 * </ul>
 *
 * <p>Cache validity is keyed on the Minecraft version string.  A version
 * mismatch automatically invalidates the cache and triggers a full rebuild.
 */
@Slf4j
public final class RegistryCache {
    private static final byte[] MAGIC          = {'P', 'N', 'X', 'C'};
    private static final byte   SCHEMA_VERSION = 1;

    private static final byte SECTION_ITEM_RUNTIMEID     = 1;
    private static final byte SECTION_BLOCK_STATE_COLORS = 2;
    private static final byte SECTION_CREATIVE           = 3;
    private static final byte SECTION_RECIPE_PACKET      = 4;
    private static final byte SECTION_BIOME              = 5;

    private byte[] itemRtIdBytes;
    private byte[] blockColorBytes;
    private byte[] creativeBytes;
    private byte[] recipePktBytes;
    private byte[] biomeBytes;

    private RegistryCache() {}

    /**
     * Try to load a valid cache from {@code cachePath}.
     * Returns {@code null} if the file is absent, invalid, or stale.
     */
    public static RegistryCache tryLoad(Path cachePath) {
        if (!Files.exists(cachePath)) return null;

        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(Files.newInputStream(cachePath)))) {

            byte[] magic = new byte[4];
            in.readFully(magic);
            if (!Arrays.equals(magic, MAGIC)) {
                log.debug("Registry cache: bad magic bytes, ignoring");
                return null;
            }
            if (in.readByte() != SCHEMA_VERSION) {
                log.debug("Registry cache: schema version mismatch, ignoring");
                return null;
            }
            String cachedMcVersion = in.readUTF();
            if (!cachedMcVersion.equals(ProtocolInfo.MINECRAFT_VERSION_NETWORK)) {
                log.info("Registry cache: MC version changed ({} → {}), rebuilding",
                        cachedMcVersion, ProtocolInfo.MINECRAFT_VERSION_NETWORK);
                return null;
            }

            RegistryCache cache = new RegistryCache();
            int sectionId;
            while ((sectionId = in.read()) != -1) {
                int    len  = in.readInt();
                byte[] data = new byte[len];
                in.readFully(data);
                switch ((byte) sectionId) {
                    case SECTION_ITEM_RUNTIMEID     -> cache.itemRtIdBytes  = data;
                    case SECTION_BLOCK_STATE_COLORS -> cache.blockColorBytes = data;
                    case SECTION_CREATIVE           -> cache.creativeBytes   = data;
                    case SECTION_RECIPE_PACKET      -> cache.recipePktBytes  = data;
                    case SECTION_BIOME              -> cache.biomeBytes      = data;
                    default -> log.debug("Registry cache: unknown section id {}, skipping", sectionId);
                }
            }

            if (!cache.isComplete()) {
                log.debug("Registry cache: one or more sections are missing, ignoring");
                return null;
            }

            log.info("Registry cache loaded from {}", cachePath);
            return cache;

        } catch (Exception e) {
            log.warn("Failed to read registry cache, will rebuild: {}", e.getMessage());
            return null;
        }
    }

    private boolean isComplete() {
        return itemRtIdBytes != null && blockColorBytes != null
                && creativeBytes != null && recipePktBytes != null
                && biomeBytes != null;
    }

    public void restoreItemRuntimeId(ItemRuntimeIdRegistry reg) {
        try {
            reg.restoreCache(new DataInputStream(new ByteArrayInputStream(itemRtIdBytes)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void restoreBlockStateColors() {
        try {
            BlockStateRegistry.restoreColorsFromCache(
                    new DataInputStream(new ByteArrayInputStream(blockColorBytes)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void restoreCreative(CreativeItemRegistry reg) {
        try {
            reg.restoreCache(new DataInputStream(new ByteArrayInputStream(creativeBytes)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Returns the raw CraftingDataPacket bytes to pass to {@link RecipeRegistry#init(byte[])}. */
    public byte[] getRecipePktBytes() {
        return recipePktBytes;
    }

    public void restoreBiome(BiomeRegistry reg) {
        try {
            reg.restoreCache(new DataInputStream(new ByteArrayInputStream(biomeBytes)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Serialise the current state of all cacheable registries to {@code cachePath}.
     * Called once after a full cold-start init so the next boot can use the cache.
     */
    public static void save(Path cachePath) {
        try {
            Path parent = cachePath.getParent();
            if (parent != null) Files.createDirectories(parent);

            ByteArrayOutputStream baos = new ByteArrayOutputStream(1 << 20); // 1 MB initial
            DataOutputStream      out  = new DataOutputStream(baos);

            out.write(MAGIC);
            out.writeByte(SCHEMA_VERSION);
            out.writeUTF(ProtocolInfo.MINECRAFT_VERSION_NETWORK);

            writeSection(out, SECTION_ITEM_RUNTIMEID,     Registries.ITEM_RUNTIMEID::writeCache);
            writeSection(out, SECTION_BLOCK_STATE_COLORS, BlockStateRegistry::writeColorsToCache);
            writeSection(out, SECTION_CREATIVE,           Registries.CREATIVE::writeCache);
            writeSection(out, SECTION_RECIPE_PACKET,      Registries.RECIPE::writePktCache);
            writeSection(out, SECTION_BIOME,              Registries.BIOME::writeCache);

            out.flush();

            // Atomic write via a per-JVM temp file, then rename.
            // Using a unique name avoids corruption when multiple server processes
            // start simultaneously and all try to write the cache at once.
            // All processes compute identical data, so last-writer-wins is safe.
            Path tmp = cachePath.resolveSibling(
                    cachePath.getFileName() + ".tmp." + ProcessHandle.current().pid());
            Files.write(tmp, baos.toByteArray());
            try {
                Files.move(tmp, cachePath,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, cachePath, StandardCopyOption.REPLACE_EXISTING);
            } finally {
                Files.deleteIfExists(tmp); // clean up if move failed
            }

            log.info("Registry cache saved ({} KB) to {}", baos.size() / 1024, cachePath);

        } catch (IOException e) {
            log.warn("Failed to save registry cache: {}", e.getMessage());
        }
    }

    // ======== helpers ========

    @FunctionalInterface
    interface SectionWriter {
        void write(DataOutputStream out) throws IOException;
    }

    private static void writeSection(DataOutputStream main, byte id, SectionWriter writer)
            throws IOException {
        ByteArrayOutputStream sectionBuf = new ByteArrayOutputStream(64 * 1024);
        writer.write(new DataOutputStream(sectionBuf));
        byte[] bytes = sectionBuf.toByteArray();
        main.writeByte(id);
        main.writeInt(bytes.length);
        main.write(bytes);
    }

    // ======== block-state colour map (static helpers used by BlockStateRegistry) ========

    static void writeBlockColors(DataOutputStream out) throws IOException {
        var map = Block.VANILLA_BLOCK_COLOR_MAP;
        out.writeInt(map.size());
        for (var e : map.long2ObjectEntrySet()) {
            out.writeInt((int) e.getLongKey());
            BlockColor c        = e.getValue();
            BlockColor.Tint tint = c.getTint();
            out.writeByte(c.getRed());
            out.writeByte(c.getGreen());
            out.writeByte(c.getBlue());
            out.writeByte(c.getAlpha());
            out.writeByte(tint == null ? -1 : tint.ordinal());
        }
    }

    static void readBlockColors(DataInputStream in) throws IOException {
        int size = in.readInt();
        BlockColor.Tint[] tints = BlockColor.Tint.values();
        for (int i = 0; i < size; i++) {
            int hash         = in.readInt();
            int r            = in.readByte() & 0xFF;
            int g            = in.readByte() & 0xFF;
            int b            = in.readByte() & 0xFF;
            int a            = in.readByte() & 0xFF;
            int tintOrdinal  = in.readByte();
            BlockColor.Tint tint = (tintOrdinal == -1) ? null : tints[tintOrdinal];
            Block.VANILLA_BLOCK_COLOR_MAP.put((long) hash, new BlockColor(r, g, b, a, tint));
        }
    }

    // ======== creative item helpers (used by CreativeItemRegistry) ========

    static void writeCreativeGroups(DataOutputStream out,
            Iterable<CreativeItemGroup> groups) throws IOException {
        List<CreativeItemGroup> list = new ArrayList<>();
        groups.forEach(list::add);
        out.writeInt(list.size());
        for (CreativeItemGroup g : list) {
            out.writeByte(g.getCategory().ordinal());
            out.writeUTF(g.getName());
            Item icon = g.getIcon();
            out.writeUTF(icon == null || icon.isNull() ? "" : icon.getId());
            out.writeInt(icon == null || icon.isNull() ? 0  : icon.getDamage());
        }
    }

    static List<CreativeItemGroup> readCreativeGroups(DataInputStream in) throws IOException {
        int size = in.readInt();
        List<CreativeItemGroup> result = new ArrayList<>(size);
        CreativeItemCategory[] cats = CreativeItemCategory.VALUES;
        for (int i = 0; i < size; i++) {
            int    catOrd  = in.readByte() & 0xFF;
            String name    = in.readUTF();
            String iconId  = in.readUTF();
            int    iconDmg = in.readInt();
            Item   icon    = iconId.isEmpty() ? Item.AIR : Item.get(iconId, iconDmg);
            result.add(new CreativeItemGroup(cats[catOrd], name, icon));
        }
        return result;
    }

    static void writeCategoryGroupIndexMap(DataOutputStream out,
            Map<CreativeCategory, Map<String, Integer>> map) throws IOException {
        out.writeInt(map.size());
        for (var outer : map.entrySet()) {
            out.writeInt(outer.getKey().ordinal());
            Map<String, Integer> inner = outer.getValue();
            out.writeInt(inner.size());
            for (var e : inner.entrySet()) {
                out.writeUTF(e.getKey());
                out.writeInt(e.getValue());
            }
        }
    }

    static Map<CreativeCategory, Map<String, Integer>> readCategoryGroupIndexMap(DataInputStream in)
            throws IOException {
        int                                         outerSize = in.readInt();
        Map<CreativeCategory, Map<String, Integer>> result    = new HashMap<>(outerSize);
        CreativeCategory[]                          cats      = CreativeCategory.values();
        for (int i = 0; i < outerSize; i++) {
            CreativeCategory     cat       = cats[in.readInt()];
            int                  innerSize = in.readInt();
            Map<String, Integer> inner     = new HashMap<>(innerSize);
            for (int j = 0; j < innerSize; j++) {
                inner.put(in.readUTF(), in.readInt());
            }
            result.put(cat, inner);
        }
        return result;
    }

}
