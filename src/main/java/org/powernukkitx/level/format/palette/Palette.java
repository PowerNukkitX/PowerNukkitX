package org.powernukkitx.level.format.palette;

import org.powernukkitx.Server;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockUnknown;
import org.powernukkitx.level.format.ChunkSection;
import org.powernukkitx.level.format.bitarray.BitArray;
import org.powernukkitx.level.format.bitarray.BitArrayVersion;
import org.powernukkitx.level.updater.block.BlockStateUpdaters;
import org.powernukkitx.level.updater.util.tagupdater.CompoundTagUpdaterContext;
import org.powernukkitx.network.NetworkConstants;
import org.powernukkitx.utils.HashUtils;
import org.powernukkitx.utils.SemVersion;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtUtils;
import org.cloudburstmc.protocol.common.util.VarInts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

@Slf4j
public class Palette<V> {
    protected static final byte COPY_LAST_FLAG_HEADER = (byte) (0x7F << 1) | 1;
    private static final int INDEX_MAP_THRESHOLD = 16;

    protected final List<V> palette;
    protected Object2IntOpenHashMap<V> paletteIndex;
    protected BitArray bitArray;

    public Palette(V first) {
        this(first, BitArrayVersion.V2);
    }

    public Palette(V first, BitArrayVersion version) {
        this.bitArray = version.createArray(ChunkSection.SIZE);
        this.palette = new ArrayList<>(16);
        this.addToPalette(first);
    }

    public Palette(V first, List<V> palette, BitArrayVersion version) {
        this.bitArray = version.createArray(ChunkSection.SIZE);
        this.palette = palette;
        this.addToPalette(first);
    }

    protected void addToPalette(V value) {
        if (this.paletteIndex != null) {
            if (!this.paletteIndex.containsKey(value)) {
                this.paletteIndex.put(value, this.palette.size());
            }
            this.palette.add(value);
        } else {
            this.palette.add(value);
            if (this.palette.size() > INDEX_MAP_THRESHOLD) {
                buildPaletteIndex();
            }
        }
    }

    protected void clearPalette() {
        this.palette.clear();
        this.paletteIndex = null;
    }

    private void buildPaletteIndex() {
        final Object2IntOpenHashMap<V> map = new Object2IntOpenHashMap<>(this.palette.size() * 2);
        map.defaultReturnValue(-1);
        for (int i = 0; i < this.palette.size(); i++) {
            final V v = this.palette.get(i);
            if (!map.containsKey(v)) {
                map.put(v, i);
            }
        }
        this.paletteIndex = map;
    }

    protected void rebuildPaletteIndex() {
        if (this.palette.size() > INDEX_MAP_THRESHOLD) {
            buildPaletteIndex();
        } else {
            this.paletteIndex = null;
        }
    }

    public V get(int index) {
        final int i = this.bitArray.get(index);
        return i >= palette.size() ? this.palette.getFirst() : this.palette.get(i);
    }

    public void set(int index, V value) {
        final int paletteIndex = this.paletteIndexFor(value);
        this.bitArray.set(index, paletteIndex);
    }

    /**
     * Write the Palette data to the network buffer
     *
     * @param byteBuf    the byte buf
     * @param serializer the serializer
     */
    public void writeToNetwork(ByteBuf byteBuf, RuntimeDataSerializer<V> serializer) {
        writeWords(byteBuf, serializer);
    }

    public void readFromNetwork(ByteBuf byteBuf, RuntimeDataDeserializer<V> deserializer) {
        readWords(byteBuf, readBitArrayVersion(byteBuf));
        final int size = this.bitArray.readSizeFromNetwork(byteBuf);
        for (int i = 0; i < size; i++) this.addToPalette(deserializer.deserialize(VarInts.readInt(byteBuf)));
    }

    protected boolean writeEmpty(ByteBuf byteBuf, RuntimeDataSerializer<V> serializer) {
        if (this.isEmpty()) {
            byteBuf.writeByte(Palette.getPaletteHeader(BitArrayVersion.V0, true));
            byteBuf.writeIntLE(serializer.serialize(this.palette.getFirst()));
            return true;
        }
        return false;
    }

    protected boolean writeLast(ByteBuf byteBuf, Palette<V> last) {
        if (last != null && last.palette.equals(this.palette)) {
            byteBuf.writeByte(COPY_LAST_FLAG_HEADER);
            return true;
        }
        return false;
    }

    protected void writeWords(ByteBuf byteBuf, RuntimeDataSerializer<V> serializer) {
        byteBuf.writeByte(getPaletteHeader(this.bitArray.version(), true));
        for (int word : this.bitArray.words()) byteBuf.writeIntLE(word);
        this.bitArray.writeSizeToNetwork(byteBuf, this.palette.size());
        for (V value : this.palette) {
            VarInts.writeInt(byteBuf, serializer.serialize(value));
        }
    }

    public void writeToStoragePersistent(ByteBuf byteBuf, PersistentDataSerializer<V> serializer) {
        byteBuf.writeByte(Palette.getPaletteHeader(this.bitArray.version(), false));
        for (int word : this.bitArray.words()) byteBuf.writeIntLE(word);
        byteBuf.writeIntLE(this.palette.size());
        try (final ByteBufOutputStream bufOutputStream = new ByteBufOutputStream(byteBuf);
             final NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(bufOutputStream)) {
            for (V value : this.palette) {
                if (value == null) {
                    continue;
                }

                if (value instanceof BlockState blockState && blockState.getIdentifier().equals(BlockID.UNKNOWN)) {
                    nbtOutputStream.writeTag(blockState.getBlockStateTag().getCompound("Block"));
                } else {
                    nbtOutputStream.writeTag(serializer.serialize(value));
                }
            }
        } catch (IOException e) {
            log.error("Failed to write palette to storage", e);
        }
    }

    public void readFromStoragePersistent(ByteBuf byteBuf, RuntimeDataDeserializer<V> deserializer) {
        try (final ByteBufInputStream bufInputStream = new ByteBufInputStream(byteBuf)) {
            final BitArrayVersion bversion = readBitArrayVersion(byteBuf);
            if (bversion == BitArrayVersion.V0) {
                this.bitArray = bversion.createArray(ChunkSection.SIZE, null);
                this.clearPalette();
                addBlockPalette(byteBuf, deserializer);
                this.onResize(BitArrayVersion.V2);
                return;
            }
            readWords(byteBuf, bversion);
            final int paletteSize = byteBuf.readIntLE();
            for (int i = 0; i < paletteSize; i++) {
                addBlockPalette(byteBuf, deserializer);
            }
        } catch (IOException e) {
            log.error("Failed to read palette from storage", e);
        }
    }

    public void writeToStorageRuntime(ByteBuf byteBuf, RuntimeDataSerializer<V> serializer, Palette<V> last) {
        if (writeLast(byteBuf, last)) return;
        if (writeEmpty(byteBuf, serializer)) return;

        byteBuf.writeByte(Palette.getPaletteHeader(this.bitArray.version(), true));
        for (int word : this.bitArray.words()) byteBuf.writeIntLE(word);
        byteBuf.writeIntLE(this.palette.size());
        for (V value : this.palette) byteBuf.writeIntLE(serializer.serialize(value));
    }

    public void readFromStorageRuntime(ByteBuf byteBuf, RuntimeDataDeserializer<V> deserializer, Palette<V> last) {
        final short header = byteBuf.readUnsignedByte();

        if (hasCopyLastFlag(header)) {
            last.copyTo(this);
            return;
        }

        final BitArrayVersion version = Palette.getVersionFromPaletteHeader(header);
        if (version == BitArrayVersion.V0) {
            this.bitArray = version.createArray(ChunkSection.SIZE, null);
            this.clearPalette();
            this.addToPalette(deserializer.deserialize(byteBuf.readIntLE()));

            this.onResize(BitArrayVersion.V2);
            return;
        }

        readWords(byteBuf, version);

        final int paletteSize = byteBuf.readIntLE();
        for (int i = 0; i < paletteSize; i++) this.addToPalette(deserializer.deserialize(byteBuf.readIntLE()));
    }

    public int paletteIndexFor(V value) {
        int index = this.paletteIndex != null ? this.paletteIndex.getInt(value) : this.palette.indexOf(value);
        if (index != -1) return index;

        index = this.palette.size();
        this.palette.add(value);
        if (this.paletteIndex != null) {
            this.paletteIndex.put(value, index);
        } else if (this.palette.size() > INDEX_MAP_THRESHOLD) {
            buildPaletteIndex();
        }

        final BitArrayVersion version = this.bitArray.version();
        if (index > version.maxEntryValue) {
            final BitArrayVersion next = version.next;
            if (next != null) this.onResize(next);
        }

        return index;
    }

    public boolean isEmpty() {
        if (this.palette.size() == 1) {
            for (int word : this.bitArray.words())
                if (word != 0L) {
                    return false;
                }
            return true;
        } else return false;
    }

    @SuppressWarnings("unchecked")
    protected void addBlockPalette(ByteBuf byteBuf, RuntimeDataDeserializer<V> deserializer) throws IOException {
        try (final ByteBufInputStream inputStream = new ByteBufInputStream(byteBuf);
             final NBTInputStream nbtInputStream = NbtUtils.createReaderLE(inputStream)) {
            NbtMap blockTag = (NbtMap) nbtInputStream.readTag();
            final int storedVersion = blockTag.getInt("version");
            final NbtMapBuilder builder = blockTag.toBuilder();
            builder.remove("version");
            blockTag = builder.build();
            final int blockRuntimeId = HashUtils.fnv1a_32_nbt(blockTag);
            final Pair<Integer, SemVersion> p = Pair.of(blockRuntimeId, null);

            final V unknownState = (V) BlockUnknown.PROPERTIES.getDefaultState();

            if (p == null) {
                this.addToPalette(unknownState);
                return;
            }

            V resultingBlockState = unknownState;

            int version;
            if (storedVersion != 0) {
                version = storedVersion;
            } else {
                SemVersion semVersion = SemVersion.fromString(NetworkConstants.CODEC.getMinecraftVersion());
                version = CompoundTagUpdaterContext.makeVersion(semVersion.major(), semVersion.minor(), semVersion.patch());
            }

            boolean isBlockOutdated = false;

            if (p.left() == null) {     // is blockStateHash null
                isBlockOutdated = true;
            } else {
                V currentState = deserializer.deserialize(blockRuntimeId);
                if (blockRuntimeId != -2 && Objects.equals(currentState, unknownState)) {
                    byteBuf.resetReaderIndex();
                    isBlockOutdated = true;
                } else {
                    resultingBlockState = currentState;
                }
            }

            if (isBlockOutdated) {
                try (final ByteBufInputStream bufInputStream = new ByteBufInputStream(byteBuf);
                     final NBTInputStream input = NbtUtils.createReaderLE(bufInputStream)) {
                    NbtMap oldBlockNbt = (NbtMap) input.readTag();
                    NbtMap newNbtMap = BlockStateUpdaters.updateBlockState(oldBlockNbt, version);
                    final TreeMap<String, Object> states = new TreeMap<>(newNbtMap.getCompound("states"));
                    final NbtMap newBlockNbt = NbtMap.builder()
                            .putString("name", newNbtMap.getString("name"))
                            .putCompound("states", NbtMap.fromMap(states))
                            .build();

                    int hash = HashUtils.fnv1a_32_nbt(newBlockNbt);

                    resultingBlockState = deserializer.deserialize(hash);

                    // we send a warning if the resultingBlockState is null or unknown after updating it.
                    // this way the only possibility is that the block hash is not represented in block_palette.nbt
                    if (resultingBlockState == null || Objects.equals(resultingBlockState, unknownState)) {
                        resultingBlockState = unknownState;
                        log.debug("Block State updating failed, blockHash: {}, oldBlockState: {}, newBlockState: {}, blockTag: {}", HashUtils.fnv1a_32_nbt(oldBlockNbt), oldBlockNbt, newBlockNbt, blockTag);
                    }
                }
            }

            if (Objects.equals(resultingBlockState, unknownState)) {
                boolean replaceWithUnknown = Server.getInstance().getSettings().baseSettings().saveUnknownBlock();
                if (replaceWithUnknown) {
                    this.addToPalette(resultingBlockState);
                }
            } else if (resultingBlockState != null) {
                this.addToPalette(resultingBlockState);
            }
        }
    }


    protected BitArrayVersion readBitArrayVersion(ByteBuf byteBuf) {
        short header = byteBuf.readUnsignedByte();
        return Palette.getVersionFromPaletteHeader(header);
    }

    protected void readWords(ByteBuf byteBuf, BitArrayVersion version) {
        final int wordCount = version.getWordsForSize(ChunkSection.SIZE);
        final int[] words = new int[wordCount];
        for (int i = 0; i < wordCount; i++) words[i] = byteBuf.readIntLE();

        this.bitArray = version.createArray(ChunkSection.SIZE, words);
        this.clearPalette();
    }

    protected void onResize(BitArrayVersion version) {
        final BitArray newBitArray = version.createArray(ChunkSection.SIZE);
        for (int i = 0; i < ChunkSection.SIZE; i++)
            newBitArray.set(i, this.bitArray.get(i));

        this.bitArray = newBitArray;
    }

    public void copyTo(Palette<V> palette) {
        palette.bitArray = this.bitArray.copy();
        palette.palette.clear();
        palette.palette.addAll(this.palette);
        palette.rebuildPaletteIndex();
    }

    protected static boolean hasCopyLastFlag(short header) {
        return (header >> 1) == 0x7F;
    }

    protected static boolean isPersistent(short header) {
        return (header & 1) == 0;
    }

    protected static int getPaletteHeader(BitArrayVersion version, boolean runtime) {
        return (version.bits << 1) | (runtime ? 1 : 0);
    }

    protected static BitArrayVersion getVersionFromPaletteHeader(short header) {
        return BitArrayVersion.get(header >> 1, true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Palette<?> palette1)) return false;
        return Objects.equals(palette, palette1.palette) && Objects.equals(bitArray, palette1.bitArray);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{palette, bitArray});
    }
}
