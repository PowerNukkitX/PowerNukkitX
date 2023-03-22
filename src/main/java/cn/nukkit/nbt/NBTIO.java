package cn.nukkit.nbt;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitXDifference;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.blockproperty.UnknownRuntimeIdException;
import cn.nukkit.blockproperty.exception.BlockPropertyNotFoundException;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.BlockStateRegistry;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.nbt.stream.FastByteArrayOutputStream;
import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import cn.nukkit.nbt.stream.PGZIPOutputStream;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.ThreadCache;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;

/**
 * A Named Binary Tag library for Nukkit Project
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed resource leaks")
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "It's the caller responsibility to close the provided streams")
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed output streams not being finished correctly")
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Added defensive close invocations to byte array streams")
@Log4j2
public class NBTIO {

    public static CompoundTag putItemHelper(Item item) {
        return putItemHelper(item, null);
    }

    @PowerNukkitXDifference(info = "Remove the name from the tag, this function will be removed in the future")
    public static CompoundTag putItemHelper(Item item, Integer slot) {
        CompoundTag tag = new CompoundTag((String) null)
                .putByte("Count", item.getCount())
                .putShort("Damage", item.getDamage());
        int id = item.getId();
        if (id == ItemID.STRING_IDENTIFIED_ITEM) {
            tag.putString("Name", item.getNamespaceId());
        } else {
            tag.putShort("id", item.getId());
        }
        if (slot != null) {
            tag.putByte("Slot", slot);
        }

        if (item.hasCompoundTag()) {
            tag.putCompound("tag", item.getNamedTag());
        }

        return tag;
    }

    @PowerNukkitXDifference(info = "Remove the name from the tag, this function will be removed in the future")
    @PowerNukkitXDifference(info = "not limit name and id because the return value of fromString not null")
    public static Item getItemHelper(CompoundTag tag) {
        if (!tag.containsByte("Count")) {
            return Item.get(0);
        }

        int damage = !tag.containsShort("Damage") ? 0 : tag.getShort("Damage");
        int amount = tag.getByte("Count");
        Item item;
        if (tag.containsShort("id")) {
            int id = (short) tag.getShort("id");
            item = fixWoolItem(id, damage, amount);
            if (item == null) {
                try {
                    item = Item.get(id, damage, amount);
                } catch (Exception e) {
                    item = Item.fromString(tag.getString("id"));
                    if (item.getDamage() == 0) {
                        item.setDamage(damage);
                    }
                    item.setCount(amount);
                }
            }
        } else {
            item = Item.fromString(tag.getString("Name"));
            if (item.getDamage() == 0) {
                item.setDamage(damage);
            }
            item.setCount(amount);
        }

        Tag tagTag = tag.get("tag");
        if (tagTag instanceof CompoundTag compoundTag) {
            item.setNamedTag(compoundTag);
        }
        return item;
    }

    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    public static CompoundTag putBlockHelper(Block block) {
        var states = new CompoundTag();
        for (var str : block.getProperties().getNames()) {
            Class<?> type = block.getCurrentState().getProperty(str).getValueClass();
            if (type == Boolean.class) {
                states.putBoolean(str, block.getCurrentState().getBooleanValue(str));
            } else if (type == Integer.class) {
                states.putInt(str, block.getCurrentState().getIntValue(str));
            } else {
                states.putString(str, block.getCurrentState().getPersistenceValue(str));
            }
        }
        return new CompoundTag("Block")
                .putString("name", block.getPersistenceName())
                .putCompound("states", states)
                .putInt("version", 17959425);
    }

    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    @NotNull
    public static Block getBlockHelper(@NotNull CompoundTag block) {
        if (!block.containsString("name")) return Block.get(0);
        StringBuilder state = new StringBuilder(block.getString("name"));
        CompoundTag states = block.getCompound("states");
        states.getTags().forEach((k, v) -> state.append(';').append(k).append('=').append(v.parseValue()));
        var blockStateId = state.toString();
        try {
            var blockState = BlockState.of(blockStateId);
            return blockState.getBlock();
        } catch (BlockPropertyNotFoundException | UnknownRuntimeIdException e) {
            int runtimeId = BlockStateRegistry.getKnownRuntimeIdByBlockStateId(blockStateId);
            if (runtimeId == -1) {
                log.debug("Unsupported block found in creativeitems.json: {}", blockStateId);
                return BlockState.AIR.getBlock();
            }
            int blockId = BlockStateRegistry.getBlockIdByRuntimeId(runtimeId);
            BlockState defaultBlockState = BlockState.of(blockId);
            if (defaultBlockState.getProperties().equals(BlockUnknown.PROPERTIES)) {
                log.debug("Unsupported block found in creativeitems.json: {}", blockStateId);
                return BlockState.AIR.getBlock();
            }
            log.error("Failed to load the creative item with {}", blockStateId, e);
            return BlockState.AIR.getBlock();
        } catch (NoSuchElementException e) {
            log.debug("No Such Element in creativeitems.json: {}", blockStateId, e);
        } catch (Exception e) {
            log.error("Failed to load the creative item {}", blockStateId, e);
            return BlockState.AIR.getBlock();
        }
        return BlockState.AIR.getBlock();
    }


    private static Item fixWoolItem(int id, int damage, int count) {
        return Item.get(id, damage, count);
    }

    public static CompoundTag read(File file) throws IOException {
        return read(file, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag read(File file, ByteOrder endianness) throws IOException {
        if (!file.exists()) return null;
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return read(inputStream, endianness);
        }
    }

    public static CompoundTag read(InputStream inputStream) throws IOException {
        return read(inputStream, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag read(InputStream inputStream, ByteOrder endianness) throws IOException {
        return read(inputStream, endianness, false);
    }

    public static CompoundTag read(InputStream inputStream, ByteOrder endianness, boolean network) throws IOException {
        Tag tag = Tag.readNamedTag(new NBTInputStream(inputStream, endianness, network));
        if (tag instanceof CompoundTag) {
            return (CompoundTag) tag;
        }
        throw new IOException("Root tag must be a named compound tag");
    }

    public static Tag readTag(InputStream inputStream, ByteOrder endianness, boolean network) throws IOException {
        return Tag.readNamedTag(new NBTInputStream(inputStream, endianness, network));
    }

    public static CompoundTag read(byte[] data) throws IOException {
        return read(data, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag read(byte[] data, ByteOrder endianness) throws IOException {
        try (InputStream inputStream = new ByteArrayInputStream(data)) {
            return read(inputStream, endianness);
        }
    }

    public static CompoundTag read(byte[] data, ByteOrder endianness, boolean network) throws IOException {
        try (InputStream inputStream = new ByteArrayInputStream(data)) {
            return read(inputStream, endianness, network);
        }
    }

    public static CompoundTag readCompressed(InputStream inputStream) throws IOException {
        return readCompressed(inputStream, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag readCompressed(InputStream inputStream, ByteOrder endianness) throws IOException {
        try (InputStream gzip = new GZIPInputStream(inputStream);
             InputStream buffered = new BufferedInputStream(gzip)) {
            return read(buffered, endianness);
        }
    }

    public static CompoundTag readCompressed(byte[] data) throws IOException {
        return readCompressed(data, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag readCompressed(byte[] data, ByteOrder endianness) throws IOException {
        try (InputStream bytes = new ByteArrayInputStream(data);
             InputStream gzip = new GZIPInputStream(bytes);
             InputStream buffered = new BufferedInputStream(gzip)) {
            return read(buffered, endianness, true);
        }
    }

    public static CompoundTag readNetworkCompressed(InputStream inputStream) throws IOException {
        return readNetworkCompressed(inputStream, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag readNetworkCompressed(InputStream inputStream, ByteOrder endianness) throws IOException {
        try (InputStream gzip = new GZIPInputStream(inputStream);
             InputStream buffered = new BufferedInputStream(gzip)) {
            return read(buffered, endianness);
        }
    }

    public static CompoundTag readNetworkCompressed(byte[] data) throws IOException {
        return readNetworkCompressed(data, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag readNetworkCompressed(byte[] data, ByteOrder endianness) throws IOException {
        try (InputStream bytes = new ByteArrayInputStream(data);
             InputStream gzip = new GZIPInputStream(bytes);
             InputStream buffered = new BufferedInputStream(gzip)) {
            return read(buffered, endianness, true);
        }
    }

    public static byte[] write(CompoundTag tag) throws IOException {
        return write(tag, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] write(CompoundTag tag, ByteOrder endianness) throws IOException {
        return write(tag, endianness, false);
    }

    public static byte[] write(CompoundTag tag, ByteOrder endianness, boolean network) throws IOException {
        return write((Tag) tag, endianness, network);
    }

    public static byte[] write(Tag tag, ByteOrder endianness, boolean network) throws IOException {
        FastByteArrayOutputStream baos = ThreadCache.fbaos.get().reset();
        try (NBTOutputStream stream = new NBTOutputStream(baos, endianness, network)) {
            Tag.writeNamedTag(tag, stream);
            return baos.toByteArray();
        }
    }

    public static byte[] write(Collection<CompoundTag> tags) throws IOException {
        return write(tags, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] write(Collection<CompoundTag> tags, ByteOrder endianness) throws IOException {
        return write(tags, endianness, false);
    }

    public static byte[] write(Collection<CompoundTag> tags, ByteOrder endianness, boolean network) throws IOException {
        FastByteArrayOutputStream baos = ThreadCache.fbaos.get().reset();
        try (NBTOutputStream stream = new NBTOutputStream(baos, endianness, network)) {
            for (CompoundTag tag : tags) {
                Tag.writeNamedTag(tag, stream);
            }
            return baos.toByteArray();
        }
    }

    public static void write(CompoundTag tag, File file) throws IOException {
        write(tag, file, ByteOrder.BIG_ENDIAN);
    }

    public static void write(CompoundTag tag, File file, ByteOrder endianness) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            write(tag, outputStream, endianness);
        }
    }

    public static void write(CompoundTag tag, OutputStream outputStream) throws IOException {
        write(tag, outputStream, ByteOrder.BIG_ENDIAN);
    }

    public static void write(CompoundTag tag, OutputStream outputStream, ByteOrder endianness) throws IOException {
        write(tag, outputStream, endianness, false);
    }

    public static void write(CompoundTag tag, OutputStream outputStream, ByteOrder endianness, boolean network) throws IOException {
        Tag.writeNamedTag(tag, new NBTOutputStream(outputStream, endianness, network));
    }

    public static byte[] writeNetwork(Tag tag) throws IOException {
        FastByteArrayOutputStream baos = ThreadCache.fbaos.get().reset();
        try (NBTOutputStream stream = new NBTOutputStream(baos, ByteOrder.LITTLE_ENDIAN, true)) {
            Tag.writeNamedTag(tag, stream);
        }
        return baos.toByteArray();
    }

    public static byte[] writeGZIPCompressed(CompoundTag tag) throws IOException {
        return writeGZIPCompressed(tag, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] writeGZIPCompressed(CompoundTag tag, ByteOrder endianness) throws IOException {
        FastByteArrayOutputStream baos = ThreadCache.fbaos.get().reset();
        writeGZIPCompressed(tag, baos, endianness);
        return baos.toByteArray();
    }

    public static void writeGZIPCompressed(CompoundTag tag, OutputStream outputStream) throws IOException {
        writeGZIPCompressed(tag, outputStream, ByteOrder.BIG_ENDIAN);
    }

    public static void writeGZIPCompressed(CompoundTag tag, OutputStream outputStream, ByteOrder endianness) throws IOException {
        PGZIPOutputStream gzip = new PGZIPOutputStream(outputStream);
        write(tag, gzip, endianness);
        gzip.finish();
    }

    public static byte[] writeNetworkGZIPCompressed(CompoundTag tag) throws IOException {
        return writeNetworkGZIPCompressed(tag, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] writeNetworkGZIPCompressed(CompoundTag tag, ByteOrder endianness) throws IOException {
        FastByteArrayOutputStream baos = ThreadCache.fbaos.get().reset();
        writeNetworkGZIPCompressed(tag, baos, endianness);
        return baos.toByteArray();
    }

    public static void writeNetworkGZIPCompressed(CompoundTag tag, OutputStream outputStream) throws IOException {
        writeNetworkGZIPCompressed(tag, outputStream, ByteOrder.BIG_ENDIAN);
    }

    public static void writeNetworkGZIPCompressed(CompoundTag tag, OutputStream outputStream, ByteOrder endianness) throws IOException {
        PGZIPOutputStream gzip = new PGZIPOutputStream(outputStream);
        write(tag, gzip, endianness, true);
        gzip.finish();
    }

    public static void writeZLIBCompressed(CompoundTag tag, OutputStream outputStream) throws IOException {
        writeZLIBCompressed(tag, outputStream, ByteOrder.BIG_ENDIAN);
    }

    public static void writeZLIBCompressed(CompoundTag tag, OutputStream outputStream, ByteOrder endianness) throws IOException {
        writeZLIBCompressed(tag, outputStream, Deflater.DEFAULT_COMPRESSION, endianness);
    }

    public static void writeZLIBCompressed(CompoundTag tag, OutputStream outputStream, int level) throws IOException {
        writeZLIBCompressed(tag, outputStream, level, ByteOrder.BIG_ENDIAN);
    }

    public static void writeZLIBCompressed(CompoundTag tag, OutputStream outputStream, int level, ByteOrder endianness) throws IOException {
        DeflaterOutputStream out = new DeflaterOutputStream(outputStream, new Deflater(level));
        write(tag, out, endianness);
        out.finish();
    }

    public static void safeWrite(CompoundTag tag, File file) throws IOException {
        File tmpFile = new File(file.getAbsolutePath() + "_tmp");
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
        write(tag, tmpFile);
        Files.move(tmpFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    /**
     * The following methods
     * only used for LevelEventGenericPacket
     * which do not write/read tag id and name
     */

    @PowerNukkitXOnly
    @Since("1.19.21-r3")
    public static byte[] writeValue(CompoundTag tag) throws IOException {
        return writeValue(tag, ByteOrder.BIG_ENDIAN);
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r3")
    public static byte[] writeValue(CompoundTag tag, ByteOrder endianness) throws IOException {
        return writeValue(tag, endianness, false);
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r3")
    public static byte[] writeValue(CompoundTag tag, ByteOrder endianness, boolean network) throws IOException {
        return writeValue((Tag) tag, endianness, network);
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r3")
    public static byte[] writeValue(Tag tag, ByteOrder endianness, boolean network) throws IOException {
        FastByteArrayOutputStream baos = ThreadCache.fbaos.get().reset();
        try (NBTOutputStream stream = new NBTOutputStream(baos, endianness, network)) {
            Tag.writeValue(tag, stream);
            return baos.toByteArray();
        }
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r3")
    public static CompoundTag readCompoundValue(InputStream inputStream, ByteOrder endianness, boolean network) throws IOException {
        return Tag.readCompoundValue(new NBTInputStream(inputStream, endianness, network));
    }
}
