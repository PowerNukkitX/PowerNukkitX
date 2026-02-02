package cn.nukkit.level.format.palette;

import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.bitarray.BitArray;
import cn.nukkit.level.format.bitarray.BitArrayVersion;
import cn.nukkit.level.updater.block.BlockStateUpdaters;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.TreeMapCompoundTag;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.utils.ByteBufVarInt;
import cn.nukkit.utils.HashUtils;
import cn.nukkit.utils.SemVersion;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import it.unimi.dsi.fastutil.Pair;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public class Palette<V> {
    protected static final byte COPY_LAST_FLAG_HEADER = (byte) (0x7F << 1) | 1;
    protected final List<V> palette;
    protected BitArray bitArray;

    public Palette(V first) {
        this(first, BitArrayVersion.V2);
    }

    public Palette(V first, BitArrayVersion version) {
        this.bitArray = version.createArray(ChunkSection.SIZE);
        this.palette = new ArrayList<>(16);
        this.palette.add(first);
    }

    public Palette(V first, List<V> palette, BitArrayVersion version) {
        this.bitArray = version.createArray(ChunkSection.SIZE);
        this.palette = palette;
        this.palette.add(first);
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
        for (int i = 0; i < size; i++) this.palette.add(deserializer.deserialize(ByteBufVarInt.readInt(byteBuf)));
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
        for (V value : this.palette) ByteBufVarInt.writeInt(byteBuf, serializer.serialize(value));
    }

    public void writeToStoragePersistent(ByteBuf byteBuf, PersistentDataSerializer<V> serializer) {
        byteBuf.writeByte(Palette.getPaletteHeader(this.bitArray.version(), false));
        for (int word : this.bitArray.words()) byteBuf.writeIntLE(word);
        byteBuf.writeIntLE(this.palette.size());
        try (final ByteBufOutputStream bufOutputStream = new ByteBufOutputStream(byteBuf)) {
            for (V value : this.palette) {

                if (value == null)
                    continue;

                if (value instanceof BlockState blockState && blockState.getIdentifier().equals(BlockID.UNKNOWN)) {
                    NBTIO.write(blockState.getBlockStateTag().getCompound("Block"), bufOutputStream, ByteOrder.LITTLE_ENDIAN);
                } else {
                    NBTIO.write(serializer.serialize(value), bufOutputStream, ByteOrder.LITTLE_ENDIAN);
                }
            }
        } catch (IOException e) {
            log.error("Failed to write palette to storage", e);
        }
    }

    public void readFromStoragePersistent(ByteBuf byteBuf, RuntimeDataDeserializer<V> deserializer) {
        try (final ByteBufInputStream bufInputStream = new ByteBufInputStream(byteBuf);
             NBTInputStream nbtInputStream = new NBTInputStream(bufInputStream, ByteOrder.LITTLE_ENDIAN)) {
            final BitArrayVersion bversion = readBitArrayVersion(byteBuf);
            if (bversion == BitArrayVersion.V0) {
                this.bitArray = bversion.createArray(ChunkSection.SIZE, null);
                this.palette.clear();
                addBlockPalette(byteBuf, deserializer, nbtInputStream);
                this.onResize(BitArrayVersion.V2);
                return;
            }
            readWords(byteBuf, bversion);
            final int paletteSize = byteBuf.readIntLE();
            for (int i = 0; i < paletteSize; i++) {
                addBlockPalette(byteBuf, deserializer, nbtInputStream);
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
            this.palette.clear();
            this.palette.add(deserializer.deserialize(byteBuf.readIntLE()));

            this.onResize(BitArrayVersion.V2);
            return;
        }

        readWords(byteBuf, version);

        final int paletteSize = byteBuf.readIntLE();
        for (int i = 0; i < paletteSize; i++) this.palette.add(deserializer.deserialize(byteBuf.readIntLE()));
    }

    public int paletteIndexFor(V value) {
        int index = this.palette.indexOf(value);
        if (index != -1) return index;

        index = this.palette.size();
        this.palette.add(value);

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
    protected void addBlockPalette(ByteBuf byteBuf,
                                   RuntimeDataDeserializer<V> deserializer,
                                   NBTInputStream input) throws IOException {
        Pair<Integer, SemVersion> p = PaletteUtils.fastReadBlockHash(input, byteBuf);//depend on LinkCompoundTag

        final V unknownState = (V) BlockUnknown.PROPERTIES.getDefaultState();

        if (p == null) {
            this.palette.add(unknownState);
            return;
        }

        V resultingBlockState = unknownState;
        SemVersion semVersion = p.right();

        if (semVersion == null) {
            semVersion = ProtocolInfo.MINECRAFT_SEMVERSION;
        }

        int version = CompoundTagUpdaterContext.makeVersion(semVersion.major(), semVersion.minor(), semVersion.patch());

        boolean isBlockOutdated = false;

        if (p.left() == null) {     // is blockStateHash null
            isBlockOutdated = true;
        } else {
            int hash = p.left();
            V currentState = deserializer.deserialize(hash);
            if (hash != -2 && Objects.equals(currentState, unknownState)) {
                byteBuf.resetReaderIndex();
                isBlockOutdated = true;
            } else {
                resultingBlockState = currentState;
            }
        }

        if (isBlockOutdated) {
            CompoundTag oldBlockNbt = (CompoundTag) input.readTag();
            CompoundTag newNbtMap = BlockStateUpdaters.updateBlockState(oldBlockNbt, version);
            TreeMapCompoundTag states = new TreeMapCompoundTag(newNbtMap.getCompound("states").getTags());

            CompoundTag newBlockNbt = new CompoundTag()
                    .putString("name", newNbtMap.getString("name"))
                    .putCompound("states", states);

            int hash = HashUtils.fnv1a_32_nbt(newBlockNbt);

            resultingBlockState = deserializer.deserialize(hash);

            // we send a warning if the resultingBlockState is null or unknown after updating it.
            // this way the only possibility is that the block hash is not represented in block_palette.nbt
            if (resultingBlockState == null || Objects.equals(resultingBlockState, unknownState)) {
                resultingBlockState = unknownState;
                log.warn("missing block palette, blockHash: {}, blockId {}", hash, oldBlockNbt.getString("name"));
            }
        }

        if (Objects.equals(resultingBlockState, unknownState)) {
            boolean replaceWithUnknown = Server.getInstance().getSettings().baseSettings().saveUnknownBlock();
            if (replaceWithUnknown) {
                this.palette.add(resultingBlockState);
            }
        } else if (resultingBlockState != null) {
            this.palette.add(resultingBlockState);
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
        this.palette.clear();
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
        return Arrays.hashCode(new Object[]{ palette, bitArray });
    }
}
