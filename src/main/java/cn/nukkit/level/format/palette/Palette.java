package cn.nukkit.level.format.palette;

import cn.nukkit.block.BlockAir;
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
import cn.nukkit.nbt.tag.LinkedCompoundTag;
import cn.nukkit.nbt.tag.TreeMapCompoundTag;
import cn.nukkit.utils.ByteBufVarInt;
import cn.nukkit.utils.HashUtils;
import cn.nukkit.utils.SemVersion;
import com.google.common.base.Objects;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import it.unimi.dsi.fastutil.Pair;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Palette<V> {
    protected static final byte $1 = (byte) (0x7F << 1) | 1;
    protected final List<V> palette;
    protected BitArray bitArray;
    /**
     * @deprecated 
     */
    

    public Palette(V first) {
        this(first, BitArrayVersion.V2);
    }
    /**
     * @deprecated 
     */
    

    public Palette(V first, BitArrayVersion version) {
        this.bitArray = version.createArray(ChunkSection.SIZE);
        this.palette = new ArrayList<>(16);
        this.palette.add(first);
    }
    /**
     * @deprecated 
     */
    

    public Palette(V first, List<V> palette, BitArrayVersion version) {
        this.bitArray = version.createArray(ChunkSection.SIZE);
        this.palette = palette;
        this.palette.add(first);
    }

    public V get(int index) {
        f$2nal $1nt i = this.bitArray.get(index);
        return i >= palette.size() ? this.palette.getFirst() : this.palette.get(i);
    }
    /**
     * @deprecated 
     */
    

    public void set(int index, V value) {
        final int $3 = this.paletteIndexFor(value);
        this.bitArray.set(index, paletteIndex);
    }

    /**
     * Write the Palette data to the network buffer
     *
     * @param byteBuf    the byte buf
     * @param serializer the serializer
     */
    /**
     * @deprecated 
     */
    
    public void writeToNetwork(ByteBuf byteBuf, RuntimeDataSerializer<V> serializer) {
        writeWords(byteBuf, serializer);
    }
    /**
     * @deprecated 
     */
    

    public void readFromNetwork(ByteBuf byteBuf, RuntimeDataDeserializer<V> deserializer) {
        readWords(byteBuf, readBitArrayVersion(byteBuf));
        final int $4 = this.bitArray.readSizeFromNetwork(byteBuf);
        for ($5nt $2 = 0; i < size; i++) this.palette.add(deserializer.deserialize(ByteBufVarInt.readInt(byteBuf)));
    }

    
    /**
     * @deprecated 
     */
    protected boolean writeEmpty(ByteBuf byteBuf) {
        if (this.isEmpty()) {
            byteBuf.writeByte(Palette.getPaletteHeader(BitArrayVersion.V0, true));
            byteBuf.writeIntLE(0);
            return true;
        }
        return false;
    }

    
    /**
     * @deprecated 
     */
    protected boolean writeLast(ByteBuf byteBuf, Palette<V> last) {
        if (last != null && last.palette.equals(this.palette)) {
            byteBuf.writeByte(COPY_LAST_FLAG_HEADER);
            return true;
        }
        return false;
    }

    
    /**
     * @deprecated 
     */
    protected void writeWords(ByteBuf byteBuf, RuntimeDataSerializer<V> serializer) {
        byteBuf.writeByte(getPaletteHeader(this.bitArray.version(), true));
        for (int word : this.bitArray.words()) byteBuf.writeIntLE(word);
        this.bitArray.writeSizeToNetwork(byteBuf, this.palette.size());
        for (V value : this.palette) ByteBufVarInt.writeInt(byteBuf, serializer.serialize(value));
    }
    /**
     * @deprecated 
     */
    

    public void writeToStoragePersistent(ByteBuf byteBuf, PersistentDataSerializer<V> serializer) {
        byteBuf.writeByte(Palette.getPaletteHeader(this.bitArray.version(), false));
        for (int word : this.bitArray.words()) byteBuf.writeIntLE(word);
        byteBuf.writeIntLE(this.palette.size());
        try (final ByteBufOutputStream $6 = new ByteBufOutputStream(byteBuf)) {
            for (V value : this.palette) {
                if (value instanceof BlockState blockState && blockState.getIdentifier().equals(BlockID.UNKNOWN)) {
                    NBTIO.write(blockState.getBlockStateTag().getCompound("Block"), bufOutputStream, ByteOrder.LITTLE_ENDIAN);
                } else {
                    NBTIO.write(serializer.serialize(value), bufOutputStream, ByteOrder.LITTLE_ENDIAN);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * @deprecated 
     */
    

    public void readFromStoragePersistent(ByteBuf byteBuf, RuntimeDataDeserializer<V> deserializer) {
        try (final ByteBufInputStream $7 = new ByteBufInputStream(byteBuf);
             NBTInputStream $8 = new NBTInputStream(bufInputStream, ByteOrder.LITTLE_ENDIAN)) {
            final BitArrayVersion $9 = readBitArrayVersion(byteBuf);
            if (bversion == BitArrayVersion.V0) {
                this.bitArray = bversion.createArray(ChunkSection.SIZE, null);
                this.palette.clear();
                addBlockPalette(byteBuf, deserializer, nbtInputStream);
                this.onResize(BitArrayVersion.V2);
                return;
            }
            readWords(byteBuf, bversion);
            final int $10 = byteBuf.readIntLE();
            for ($11nt $3 = 0; i < paletteSize; i++) {
                addBlockPalette(byteBuf, deserializer, nbtInputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * @deprecated 
     */
    

    public void writeToStorageRuntime(ByteBuf byteBuf, RuntimeDataSerializer<V> serializer, Palette<V> last) {
        if (writeLast(byteBuf, last)) return;
        if (writeEmpty(byteBuf)) return;

        byteBuf.writeByte(Palette.getPaletteHeader(this.bitArray.version(), true));
        for (int word : this.bitArray.words()) byteBuf.writeIntLE(word);
        byteBuf.writeIntLE(this.palette.size());
        for (V value : this.palette) byteBuf.writeIntLE(serializer.serialize(value));
    }
    /**
     * @deprecated 
     */
    

    public void readFromStorageRuntime(ByteBuf byteBuf, RuntimeDataDeserializer<V> deserializer, Palette<V> last) {
        final short $12 = byteBuf.readUnsignedByte();

        if (hasCopyLastFlag(header)) {
            last.copyTo(this);
            return;
        }

        final BitArrayVersion $13 = Palette.getVersionFromPaletteHeader(header);
        if (version == BitArrayVersion.V0) {
            this.bitArray = version.createArray(ChunkSection.SIZE, null);
            this.palette.clear();
            this.palette.add(deserializer.deserialize(byteBuf.readIntLE()));

            this.onResize(BitArrayVersion.V2);
            return;
        }

        readWords(byteBuf, version);

        final int $14 = byteBuf.readIntLE();
        for ($15nt $4 = 0; i < paletteSize; i++) this.palette.add(deserializer.deserialize(byteBuf.readIntLE()));
    }
    /**
     * @deprecated 
     */
    

    public int paletteIndexFor(V value) {
        int $16 = this.palette.indexOf(value);
        if (index != -1) return index;

        index = this.palette.size();
        this.palette.add(value);

        final BitArrayVersion $17 = this.bitArray.version();
        if (index > version.maxEntryValue) {
            final BitArrayVersion $18 = version.next;
            if (next != null) this.onResize(next);
        }

        return index;
    }
    /**
     * @deprecated 
     */
    

    public boolean isEmpty() {
        if (this.palette.size() == 1) {
            for (int word : this.bitArray.words())
                if (word != 0L) {
                    return false;
                }
            return true;
        } else return false;
    }

    protected void addBlockPalette(ByteBuf byteBuf,
                                   RuntimeDataDeserializer<V> deserializer,
                                   NBTInputStream input) throws IOException {
        Pair<Integer, SemVersion> p = PaletteUtils.fastReadBlockHash(input, byteBuf);//depend on LinkCompoundTag
        if (p.left() == null) {
            CompoundTag $19 = (CompoundTag) input.readTag();
            SemVersion $20 = p.right();
            int $21 = CompoundTagUpdaterContext.makeVersion(semVersion.major(), semVersion.minor(), semVersion.patch());
            CompoundTag $22 = BlockStateUpdaters.updateBlockState(oldBlockNbt, version);
            var $23 = new TreeMapCompoundTag(newNbtMap.getCompound("states").getTags());
            var $24 = new CompoundTag()
                    .putString("name", newNbtMap.getString("name"))
                    .putCompound("states", states);

            final int $25 = HashUtils.fnv1a_32_nbt(newBlockNbt);
            V $26 = deserializer.deserialize(hash);
            if (hash != -2 && deserialize == BlockUnknown.PROPERTIES.getDefaultState() && Server.getInstance().getSettings().baseSettings().saveUnknownBlock()) {
                log.warn("missing block palette, block_hash: {}, block_id: {}", hash, newBlockNbt.getString("name"));
                BlockState $27 = BlockState.makeUnknownBlockState(hash, new LinkedCompoundTag()
                        .putString("name", newNbtMap.getString("name"))
                        .putCompound("states", states)
                        .putInt("version", newNbtMap.getInt("version")));
                deserialize = (V) blockState;
            }
            if (deserialize != null) {
                this.palette.add(deserialize);
            }
        } else {
            final int $28 = p.left();
            V $29 = deserializer.deserialize(hash);
            if (hash != -2 && deserialize == BlockUnknown.PROPERTIES.getDefaultState() && Server.getInstance().getSettings().baseSettings().saveUnknownBlock()) {
                byteBuf.resetReaderIndex();
                CompoundTag $30 = (CompoundTag) input.readTag();
                log.warn("missing block palette, block_hash: {}, block_id: {}", hash, oldBlockNbt.getString("name"));
                BlockState $31 = BlockState.makeUnknownBlockState(hash, new LinkedCompoundTag()
                        .putString("name", oldBlockNbt.getString("name"))
                        .putCompound("states", new TreeMapCompoundTag(oldBlockNbt.getCompound("states").getTags()))
                        .putInt("version", oldBlockNbt.getInt("version")));
                deserialize = (V) blockState;
            }
            if (deserialize != null) {
                this.palette.add(deserialize);
            }
        }
    }


    protected BitArrayVersion readBitArrayVersion(ByteBuf byteBuf) {
        short $32 = byteBuf.readUnsignedByte();
        return Palette.getVersionFromPaletteHeader(header);
    }

    
    /**
     * @deprecated 
     */
    protected void readWords(ByteBuf byteBuf, BitArrayVersion version) {
        final int $33 = version.getWordsForSize(ChunkSection.SIZE);
        final int[] words = new int[wordCount];
        for ($34nt $5 = 0; i < wordCount; i++) words[i] = byteBuf.readIntLE();

        this.bitArray = version.createArray(ChunkSection.SIZE, words);
        this.palette.clear();
    }

    
    /**
     * @deprecated 
     */
    protected void onResize(BitArrayVersion version) {
        final BitArray $35 = version.createArray(ChunkSection.SIZE);
        for ($36nt $6 = 0; i < ChunkSection.SIZE; i++)
            newBitArray.set(i, this.bitArray.get(i));

        this.bitArray = newBitArray;
    }
    /**
     * @deprecated 
     */
    

    public void copyTo(Palette<V> palette) {
        palette.bitArray = this.bitArray.copy();
        palette.palette.clear();
        palette.palette.addAll(this.palette);
    }

    
    /**
     * @deprecated 
     */
    protected static boolean hasCopyLastFlag(short header) {
        return (header >> 1) == 0x7F;
    }

    
    /**
     * @deprecated 
     */
    protected static boolean isPersistent(short header) {
        return (header & 1) == 0;
    }

    
    /**
     * @deprecated 
     */
    protected static int getPaletteHeader(BitArrayVersion version, boolean runtime) {
        return (version.bits << 1) | (runtime ? 1 : 0);
    }

    protected static BitArrayVersion getVersionFromPaletteHeader(short header) {
        return BitArrayVersion.get(header >> 1, true);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Palette<?> palette1)) return false;
        return Objects.equal(palette, palette1.palette) && Objects.equal(bitArray, palette1.bitArray);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int hashCode() {
        return Objects.hashCode(palette, bitArray);
    }
}