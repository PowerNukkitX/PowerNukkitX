package cn.nukkit.nbt;

import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.item.Item;
import cn.nukkit.item.UnknownItem;
import cn.nukkit.level.updater.block.BlockStateUpdaters;
import cn.nukkit.level.updater.item.ItemUpdaters;
import cn.nukkit.nbt.stream.FastByteArrayOutputStream;
import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import cn.nukkit.nbt.stream.PGZIPOutputStream;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.LinkedCompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.nbt.tag.TreeMapCompoundTag;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.HashUtils;
import cn.nukkit.utils.ThreadCache;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;

import static cn.nukkit.network.protocol.ProtocolInfo.BLOCK_STATE_VERSION_NO_REVISION;

/**
 * A Named Binary Tag library for Nukkit Project
 */
@Slf4j
public class NBTIO {

    public static CompoundTag putItemHelper(Item item) {
        return putItemHelper(item, null);
    }

    public static CompoundTag putItemHelper(Item item, Integer slot) {
        CompoundTag tag = new CompoundTag()
                .putByte("Count", item.getCount())
                .putShort("Damage", item.getDamage());
        tag.putString("Name", item.getId());
        if (slot != null) {
            tag.putByte("Slot", slot);
        }
        if (item.hasCompoundTag()) {
            tag.putCompound("tag", item.getNamedTag());
        }
        //ItemID same with blockID, represent this is a pure blockitem,
        //and only blockitem need to be written `Block` to NBT,
        //whereas for `minecraft:potato` item, the corresponding block is `minecraft:potatos`
        //these items do not need to be written
        if (item.isBlock() && item.getBlockId().equals(item.getId())) {
            tag.putCompound("Block", item.getBlockUnsafe().getBlockState().getBlockStateTag().copy());
        }
        tag.putInt("version", BLOCK_STATE_VERSION_NO_REVISION);
        return tag;
    }

    public static Item getItemHelper(CompoundTag tag) {
        String name = tag.getString("Name");
        if (name == null || name.isBlank() || name.equals(BlockID.AIR)) {
            return Item.AIR;
        }
        if (!tag.containsByte("Count")) {
            return Item.AIR;
        }

        //upgrade item
        if (tag.contains("version")) {
            int ver = tag.getInt("version");
            if (ver < BLOCK_STATE_VERSION_NO_REVISION) {
                tag = ItemUpdaters.updateItem(tag, BLOCK_STATE_VERSION_NO_REVISION);
            }
        }

        int damage = !tag.containsShort("Damage") ? 0 : tag.getShort("Damage");
        int amount = tag.getByte("Count");
        Item item = Item.get(name, damage, amount);
        Tag tagTag = tag.get("tag");
        if (!item.isNull() && tagTag instanceof CompoundTag compoundTag && !compoundTag.isEmpty()) {
            item.setNamedTag(compoundTag);
        }

        if (tag.containsCompound("Block")) {
            CompoundTag block = tag.getCompound("Block");
            boolean isUnknownBlock = block.getString("name").equals(BlockID.UNKNOWN) && block.containsCompound("Block");
            if (isUnknownBlock) {
                block = block.getCompound("Block");//originBlock
            }
            //upgrade block
            if (block.contains("version")) {
                int ver = block.getInt("version");
                if (ver < BLOCK_STATE_VERSION_NO_REVISION) {
                    block = BlockStateUpdaters.updateBlockState(block, BLOCK_STATE_VERSION_NO_REVISION);
                }
            }
            BlockState blockState = getBlockStateHelper(block);
            if (blockState != null) {
                if (isUnknownBlock) {//restore unknown block item
                    item = blockState.toItem();
                    if (damage != 0) {
                        item.setDamage(damage);
                    }
                    item.setCount(amount);
                }
                item.setBlockUnsafe(blockState.toBlock());
            } else if (item.isNull()) {//write unknown block item
                item = new UnknownItem(BlockID.UNKNOWN, damage, amount);
                CompoundTag compoundTag = new LinkedCompoundTag()
                        .putString("name", block.getString("name"))
                        .putCompound("states", new TreeMapCompoundTag(block.getCompound("states").getTags()));
                int hash = HashUtils.fnv1a_32_nbt(compoundTag);
                compoundTag.putInt("version", block.getInt("version"));
                BlockState unknownBlockState = BlockState.makeUnknownBlockState(hash, compoundTag);
                item.setBlockUnsafe(new BlockUnknown(unknownBlockState));
            }
        } else {
            if (item.isNull()) {//write unknown item
                item = new UnknownItem(BlockID.UNKNOWN, damage, amount);
                item.getOrCreateNamedTag().putCompound("Item", new CompoundTag().putString("Name", name));
            } else if (item.getId().equals(BlockID.UNKNOWN) && item.getOrCreateNamedTag().containsCompound("Item")) {//restore unknown item
                CompoundTag removeTag = item.getNamedTag().removeAndGet("Item");
                String originItemName = removeTag.getString("Name");
                Item originItem = Item.get(originItemName, damage, amount);
                originItem.setNamedTag(item.getNamedTag());
                item = originItem;
            }
        }
        return item;
    }

    public static BlockState getBlockStateHelper(CompoundTag tag) {
        return Registries.BLOCKSTATE.get(HashUtils.fnv1a_32_nbt_palette(tag));
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

    public static CompoundTag read(InputStream inputStream, ByteOrder endianness, boolean network) throws IOException {
        Object tag = new NBTInputStream(inputStream, endianness, network).readTag();
        if (tag instanceof CompoundTag) {
            return (CompoundTag) tag;
        }
        throw new IOException("Root tag must be a named compound tag");
    }

    public static CompoundTag readCompressed(byte[] data) throws IOException {
        return readCompressed(data, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag readCompressed(byte[] data, ByteOrder endianness) throws IOException {
        try (InputStream bytes = new ByteArrayInputStream(data);
             InputStream buffered = new BufferedInputStream(bytes);
             InputStream gzip = new GZIPInputStream(buffered)) {
            return read(gzip, endianness, false);
        }
    }

    public static CompoundTag readCompressed(InputStream inputStream) throws IOException {
        return readCompressed(inputStream, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag readCompressed(InputStream inputStream, ByteOrder endianness) throws IOException {
        InputStream gzip = new GZIPInputStream(inputStream);
        InputStream buffered = new BufferedInputStream(gzip);
        return read(buffered, endianness);
    }

    public static CompoundTag readNetworkCompressed(InputStream inputStream) throws IOException {
        return readNetworkCompressed(inputStream, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag readNetworkCompressed(InputStream inputStream, ByteOrder endianness) throws IOException {
        InputStream gzip = new GZIPInputStream(inputStream);
        InputStream buffered = new BufferedInputStream(gzip);
        return read(buffered, endianness);
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
                stream.writeTag(tag);
            }
            return baos.toByteArray();
        }
    }

    public static byte[] write(CompoundTag tag, ByteOrder endianness, boolean network) throws IOException {
        FastByteArrayOutputStream baos = ThreadCache.fbaos.get().reset();
        try (NBTOutputStream stream = new NBTOutputStream(baos, endianness, network)) {
            stream.writeTag(tag);
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
        NBTOutputStream stream = new NBTOutputStream(outputStream, endianness, network);
        stream.writeTag(tag);
    }

    public static void write(Collection<CompoundTag> tags, OutputStream outputStream, ByteOrder endianness, boolean network) throws IOException {
        try (NBTOutputStream stream = new NBTOutputStream(outputStream, endianness, network)) {
            for (CompoundTag tag : tags) {
                stream.writeTag(tag);
            }
        }
    }

    public static byte[] writeNetwork(CompoundTag tag) throws IOException {
        FastByteArrayOutputStream baos = ThreadCache.fbaos.get().reset();
        try (NBTOutputStream stream = new NBTOutputStream(baos, ByteOrder.LITTLE_ENDIAN, true)) {
            stream.writeTag(tag);
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
    public static byte[] writeValue(CompoundTag tag) throws IOException {
        return writeValue(tag, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] writeValue(CompoundTag tag, ByteOrder endianness) throws IOException {
        return writeValue(tag, endianness, false);
    }

    public static byte[] writeValue(CompoundTag tag, ByteOrder endianness, boolean network) throws IOException {
        FastByteArrayOutputStream baos = ThreadCache.fbaos.get().reset();
        try (NBTOutputStream stream = new NBTOutputStream(baos, endianness, network)) {
            stream.writeValue(tag);
            return baos.toByteArray();
        }
    }

    public static CompoundTag readValue(InputStream inputStream, ByteOrder endianness, boolean network) throws IOException {
        NBTInputStream nbtInputStream = new NBTInputStream(inputStream, endianness, network);
        return nbtInputStream.readValue(Tag.TAG_Compound);
    }

    public static TreeMapCompoundTag readTreeMapCompoundTag(InputStream inputStream, ByteOrder endianness, boolean network) throws IOException {
        NBTInputStream nbtInputStream = new NBTInputStream(inputStream, endianness, network);
        Object nbt = nbtInputStream.readTag();
        if (nbt instanceof CompoundTag tag) {
            return new TreeMapCompoundTag(tag.getTags());
        }
        throw new IOException("Root tag must be a named compound tag");
    }

    public static TreeMapCompoundTag readCompressedTreeMapCompoundTag(InputStream inputStream, ByteOrder endianness) throws IOException {
        try (InputStream gzip = new GZIPInputStream(inputStream);
             InputStream buffered = new BufferedInputStream(gzip)) {
            return readTreeMapCompoundTag(buffered, endianness, false);
        }
    }
}
