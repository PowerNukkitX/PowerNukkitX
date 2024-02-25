package cn.nukkit.network.connection.util;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.GameRules;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector2f;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.network.protocol.types.EntityLink;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import cn.nukkit.network.protocol.types.itemstack.request.ItemStackRequest;
import cn.nukkit.network.protocol.types.itemstack.request.ItemStackRequestSlotData;
import cn.nukkit.network.protocol.types.itemstack.request.TextProcessingEventOrigin;
import cn.nukkit.network.protocol.types.itemstack.request.action.*;
import cn.nukkit.recipe.descriptor.ComplexAliasDescriptor;
import cn.nukkit.recipe.descriptor.DeferredDescriptor;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.recipe.descriptor.ItemDescriptorType;
import cn.nukkit.recipe.descriptor.ItemTagDescriptor;
import cn.nukkit.recipe.descriptor.MolangDescriptor;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.ByteBufVarInt;
import cn.nukkit.utils.LittleEndianByteBufOutputStream;
import cn.nukkit.utils.OptionalValue;
import cn.nukkit.utils.PersonaPiece;
import cn.nukkit.utils.PersonaPieceTint;
import cn.nukkit.utils.SerializedImage;
import cn.nukkit.utils.SkinAnimation;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import lombok.SneakyThrows;
import lombok.val;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToLongFunction;

public class HandleByteBuf extends ByteBuf {
    protected final ByteBuf buf;

    public static HandleByteBuf of(ByteBuf buf) {
        return new HandleByteBuf(buf);
    }

    protected HandleByteBuf(ByteBuf buf) {
        this.buf = ObjectUtil.checkNotNull(buf, "buf");
    }

    @Override
    public final boolean hasMemoryAddress() {
        return buf.hasMemoryAddress();
    }

    @Override
    public boolean isContiguous() {
        return buf.isContiguous();
    }

    @Override
    public final long memoryAddress() {
        return buf.memoryAddress();
    }

    @Override
    public final int capacity() {
        return buf.capacity();
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        buf.capacity(newCapacity);
        return this;
    }

    @Override
    public final int maxCapacity() {
        return buf.maxCapacity();
    }

    @Override
    public final ByteBufAllocator alloc() {
        return buf.alloc();
    }

    @Override
    public final ByteOrder order() {
        return buf.order();
    }

    @Override
    public ByteBuf order(ByteOrder endianness) {
        return buf.order(endianness);
    }

    @Override
    public final ByteBuf unwrap() {
        return buf;
    }

    @Override
    public ByteBuf asReadOnly() {
        return buf.asReadOnly();
    }

    @Override
    public boolean isReadOnly() {
        return buf.isReadOnly();
    }

    @Override
    public final boolean isDirect() {
        return buf.isDirect();
    }

    @Override
    public final int readerIndex() {
        return buf.readerIndex();
    }

    @Override
    public final ByteBuf readerIndex(int readerIndex) {
        buf.readerIndex(readerIndex);
        return this;
    }

    @Override
    public final int writerIndex() {
        return buf.writerIndex();
    }

    @Override
    public final ByteBuf writerIndex(int writerIndex) {
        buf.writerIndex(writerIndex);
        return this;
    }

    @Override
    public ByteBuf setIndex(int readerIndex, int writerIndex) {
        buf.setIndex(readerIndex, writerIndex);
        return this;
    }

    @Override
    public final int readableBytes() {
        return buf.readableBytes();
    }

    @Override
    public final int writableBytes() {
        return buf.writableBytes();
    }

    @Override
    public final int maxWritableBytes() {
        return buf.maxWritableBytes();
    }

    @Override
    public int maxFastWritableBytes() {
        return buf.maxFastWritableBytes();
    }

    @Override
    public final boolean isReadable() {
        return buf.isReadable();
    }

    @Override
    public final boolean isWritable() {
        return buf.isWritable();
    }

    @Override
    public final ByteBuf clear() {
        buf.clear();
        return this;
    }

    @Override
    public final ByteBuf markReaderIndex() {
        buf.markReaderIndex();
        return this;
    }

    @Override
    public final ByteBuf resetReaderIndex() {
        buf.resetReaderIndex();
        return this;
    }

    @Override
    public final ByteBuf markWriterIndex() {
        buf.markWriterIndex();
        return this;
    }

    @Override
    public final ByteBuf resetWriterIndex() {
        buf.resetWriterIndex();
        return this;
    }

    @Override
    public ByteBuf discardReadBytes() {
        buf.discardReadBytes();
        return this;
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        buf.discardSomeReadBytes();
        return this;
    }

    @Override
    public ByteBuf ensureWritable(int minWritableBytes) {
        buf.ensureWritable(minWritableBytes);
        return this;
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        return buf.ensureWritable(minWritableBytes, force);
    }

    @Override
    public boolean getBoolean(int index) {
        return buf.getBoolean(index);
    }

    @Override
    public byte getByte(int index) {
        return buf.getByte(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        return buf.getUnsignedByte(index);
    }

    @Override
    public short getShort(int index) {
        return buf.getShort(index);
    }

    @Override
    public short getShortLE(int index) {
        return buf.getShortLE(index);
    }

    @Override
    public int getUnsignedShort(int index) {
        return buf.getUnsignedShort(index);
    }

    @Override
    public int getUnsignedShortLE(int index) {
        return buf.getUnsignedShortLE(index);
    }

    @Override
    public int getMedium(int index) {
        return buf.getMedium(index);
    }

    @Override
    public int getMediumLE(int index) {
        return buf.getMediumLE(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return buf.getUnsignedMedium(index);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        return buf.getUnsignedMediumLE(index);
    }

    @Override
    public int getInt(int index) {
        return buf.getInt(index);
    }

    @Override
    public int getIntLE(int index) {
        return buf.getIntLE(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        return buf.getUnsignedInt(index);
    }

    @Override
    public long getUnsignedIntLE(int index) {
        return buf.getUnsignedIntLE(index);
    }

    @Override
    public long getLong(int index) {
        return buf.getLong(index);
    }

    @Override
    public long getLongLE(int index) {
        return buf.getLongLE(index);
    }

    @Override
    public char getChar(int index) {
        return buf.getChar(index);
    }

    @Override
    public float getFloat(int index) {
        return buf.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        return buf.getDouble(index);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst) {
        buf.getBytes(index, dst);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int length) {
        buf.getBytes(index, dst, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        buf.getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst) {
        buf.getBytes(index, dst);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        buf.getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        buf.getBytes(index, dst);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        buf.getBytes(index, out, length);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return buf.getBytes(index, out, length);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return buf.getBytes(index, out, position, length);
    }

    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        return buf.getCharSequence(index, length, charset);
    }

    @Override
    public ByteBuf setBoolean(int index, boolean value) {
        buf.setBoolean(index, value);
        return this;
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        buf.setByte(index, value);
        return this;
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        buf.setShort(index, value);
        return this;
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        buf.setShortLE(index, value);
        return this;
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        buf.setMedium(index, value);
        return this;
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        buf.setMediumLE(index, value);
        return this;
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        buf.setInt(index, value);
        return this;
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        buf.setIntLE(index, value);
        return this;
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        buf.setLong(index, value);
        return this;
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        buf.setLongLE(index, value);
        return this;
    }

    @Override
    public ByteBuf setChar(int index, int value) {
        buf.setChar(index, value);
        return this;
    }

    @Override
    public ByteBuf setFloat(int index, float value) {
        buf.setFloat(index, value);
        return this;
    }

    @Override
    public ByteBuf setDouble(int index, double value) {
        buf.setDouble(index, value);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src) {
        buf.setBytes(index, src);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int length) {
        buf.setBytes(index, src, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        buf.setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        buf.setBytes(index, src);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        buf.setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        buf.setBytes(index, src);
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return buf.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return buf.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        return buf.setBytes(index, in, position, length);
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        buf.setZero(index, length);
        return this;
    }

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        return buf.setCharSequence(index, sequence, charset);
    }

    @Override
    public boolean readBoolean() {
        return buf.readBoolean();
    }

    @Override
    public byte readByte() {
        return buf.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return buf.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return buf.readShort();
    }

    @Override
    public short readShortLE() {
        return buf.readShortLE();
    }

    @Override
    public int readUnsignedShort() {
        return buf.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return buf.readUnsignedShortLE();
    }

    @Override
    public int readMedium() {
        return buf.readMedium();
    }

    @Override
    public int readMediumLE() {
        return buf.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        return buf.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return buf.readUnsignedMediumLE();
    }

    @Override
    public int readInt() {
        return buf.readInt();
    }

    @Override
    public int readIntLE() {
        return buf.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        return buf.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return buf.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        return buf.readLong();
    }

    @Override
    public long readLongLE() {
        return buf.readLongLE();
    }

    @Override
    public char readChar() {
        return buf.readChar();
    }

    @Override
    public float readFloat() {
        return buf.readFloat();
    }

    @Override
    public double readDouble() {
        return buf.readDouble();
    }

    @Override
    public ByteBuf readBytes(int length) {
        return buf.readBytes(length);
    }

    @Override
    public ByteBuf readSlice(int length) {
        return buf.readSlice(length);
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        return buf.readRetainedSlice(length);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst) {
        buf.readBytes(dst);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int length) {
        buf.readBytes(dst, length);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        buf.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf readBytes(byte[] dst) {
        buf.readBytes(dst);
        return this;
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        buf.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        buf.readBytes(dst);
        return this;
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        buf.readBytes(out, length);
        return this;
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        return buf.readBytes(out, length);
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        return buf.readBytes(out, position, length);
    }

    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        return buf.readCharSequence(length, charset);
    }

    @Override
    public ByteBuf skipBytes(int length) {
        buf.skipBytes(length);
        return this;
    }

    @Override
    public ByteBuf writeBoolean(boolean value) {
        buf.writeBoolean(value);
        return this;
    }

    @Override
    public ByteBuf writeByte(int value) {
        buf.writeByte(value);
        return this;
    }

    @Override
    public ByteBuf writeShort(int value) {
        buf.writeShort(value);
        return this;
    }

    @Override
    public ByteBuf writeShortLE(int value) {
        buf.writeShortLE(value);
        return this;
    }

    @Override
    public ByteBuf writeMedium(int value) {
        buf.writeMedium(value);
        return this;
    }

    @Override
    public ByteBuf writeMediumLE(int value) {
        buf.writeMediumLE(value);
        return this;
    }

    @Override
    public ByteBuf writeInt(int value) {
        buf.writeInt(value);
        return this;
    }

    @Override
    public ByteBuf writeIntLE(int value) {
        buf.writeIntLE(value);
        return this;
    }

    @Override
    public ByteBuf writeLong(long value) {
        buf.writeLong(value);
        return this;
    }

    @Override
    public ByteBuf writeLongLE(long value) {
        buf.writeLongLE(value);
        return this;
    }

    @Override
    public ByteBuf writeChar(int value) {
        buf.writeChar(value);
        return this;
    }

    @Override
    public ByteBuf writeFloat(float value) {
        buf.writeFloat(value);
        return this;
    }

    @Override
    public ByteBuf writeDouble(double value) {
        buf.writeDouble(value);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src) {
        buf.writeBytes(src);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int length) {
        buf.writeBytes(src, length);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        buf.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        buf.writeBytes(src);
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        buf.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        buf.writeBytes(src);
        return this;
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        return buf.writeBytes(in, length);
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        return buf.writeBytes(in, length);
    }

    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        return buf.writeBytes(in, position, length);
    }

    @Override
    public ByteBuf writeZero(int length) {
        buf.writeZero(length);
        return this;
    }

    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        return buf.writeCharSequence(sequence, charset);
    }

    public String readString() {
        byte[] bytes = new byte[this.readUnsignedVarInt()];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public ByteBuf writeString(String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        this.writeUnsignedVarInt(bytes.length);
        return buf.writeBytes(bytes);
    }

    public <T> void writeNotNull(T data, Consumer<T> consumer) {
        boolean present = data != null;
        writeBoolean(present);
        if (present) {
            consumer.accept(data);
        }
    }

    public <T> void writeOptional(OptionalValue<T> data, Consumer<T> consumer) {
        boolean present = data.isPresent();
        writeBoolean(present);
        if (present) {
            consumer.accept(data.get());
        }
    }

    /**
     * Writes a list of Attributes to the packet buffer using the standard format.
     */
    public void writeAttributeList(Attribute[] attributes) {
        this.writeUnsignedVarInt(attributes.length);
        for (Attribute attribute : attributes) {
            this.writeString(attribute.getName());
            this.writeFloatLE(attribute.getMinValue());
            this.writeFloatLE(attribute.getValue());
            this.writeFloatLE(attribute.getMaxValue());
        }
    }

    public void writeUUID(UUID uuid) {
        this.writeBytes(Binary.writeUUID(uuid));
    }

    public UUID readUUID() {
        byte[] bytes = new byte[16];
        this.readBytes(bytes);
        return Binary.readUUID(bytes);
    }

    public void writeImage(SerializedImage image) {
        this.writeIntLE(image.width);
        this.writeIntLE(image.height);
        this.writeUnsignedVarInt(image.data.length);
        this.writeBytes(image.data);
    }

    public SerializedImage readImage() {
        int width = this.readIntLE();
        int height = this.readIntLE();
        byte[] bytes = new byte[this.readUnsignedVarInt()];
        this.readBytes(bytes);
        return new SerializedImage(width, height, bytes);
    }

    public byte[] readByteArray() {
        byte[] bytes = new byte[readUnsignedVarInt()];
        readBytes(bytes);
        return bytes;
    }

    public void writeByteArray(byte[] bytes) {
        writeUnsignedVarInt(bytes.length);
        this.writeBytes(bytes);
    }

    public void writeSkin(Skin skin) {
        this.writeString(skin.getSkinId());
        this.writeString(skin.getPlayFabId());
        this.writeString(skin.getSkinResourcePatch());
        this.writeImage(skin.getSkinData());

        List<SkinAnimation> animations = skin.getAnimations();
        this.writeIntLE(animations.size());
        for (SkinAnimation animation : animations) {
            this.writeImage(animation.image);
            this.writeIntLE(animation.type);
            this.writeFloatLE(animation.frames);
            this.writeIntLE(animation.expression);
        }

        this.writeImage(skin.getCapeData());
        this.writeString(skin.getGeometryData());
        this.writeString(skin.getGeometryDataEngineVersion());
        this.writeString(skin.getAnimationData());
        this.writeString(skin.getCapeId());
        this.writeString(skin.getFullSkinId());
        this.writeString(skin.getArmSize());
        this.writeString(skin.getSkinColor());
        List<PersonaPiece> pieces = skin.getPersonaPieces();
        this.writeIntLE(pieces.size());
        for (PersonaPiece piece : pieces) {
            this.writeString(piece.id);
            this.writeString(piece.type);
            this.writeString(piece.packId);
            this.writeBoolean(piece.isDefault);
            this.writeString(piece.productId);
        }

        List<PersonaPieceTint> tints = skin.getTintColors();
        this.writeIntLE(tints.size());
        for (PersonaPieceTint tint : tints) {
            this.writeString(tint.pieceType);
            List<String> colors = tint.colors;
            this.writeIntLE(colors.size());
            for (String color : colors) {
                this.writeString(color);
            }
        }

        this.writeBoolean(skin.isPremium());
        this.writeBoolean(skin.isPersona());
        this.writeBoolean(skin.isCapeOnClassic());
        this.writeBoolean(skin.isPrimaryUser());
        this.writeBoolean(skin.isOverridingPlayerAppearance());
    }

    public Skin readSkin() {
        Skin skin = new Skin();
        skin.setSkinId(this.readString());
        skin.setPlayFabId(this.readString());
        skin.setSkinResourcePatch(this.readString());
        skin.setSkinData(this.readImage());

        int animationCount = this.readIntLE();
        for (int i = 0; i < animationCount; i++) {
            SerializedImage image = this.readImage();
            int type = this.readIntLE();
            float frames = this.readFloatLE();
            int expression = this.readIntLE();
            skin.getAnimations().add(new SkinAnimation(image, type, frames, expression));
        }

        skin.setCapeData(this.readImage());
        skin.setGeometryData(this.readString());
        skin.setGeometryDataEngineVersion(this.readString());
        skin.setAnimationData(this.readString());
        skin.setCapeId(this.readString());
        skin.setFullSkinId(this.readString());
        skin.setArmSize(this.readString());
        skin.setSkinColor(this.readString());

        int piecesLength = this.readIntLE();
        for (int i = 0; i < piecesLength; i++) {
            String pieceId = this.readString();
            String pieceType = this.readString();
            String packId = this.readString();
            boolean isDefault = this.readBoolean();
            String productId = this.readString();
            skin.getPersonaPieces().add(new PersonaPiece(pieceId, pieceType, packId, isDefault, productId));
        }

        int tintsLength = this.readIntLE();
        for (int i = 0; i < tintsLength; i++) {
            String pieceType = this.readString();
            List<String> colors = new ArrayList<>();
            int colorsLength = this.readIntLE();
            for (int i2 = 0; i2 < colorsLength; i2++) {
                colors.add(this.readString());
            }
            skin.getTintColors().add(new PersonaPieceTint(pieceType, colors));
        }

        skin.setPremium(this.readBoolean());
        skin.setPersona(this.readBoolean());
        skin.setCapeOnClassic(this.readBoolean());
        skin.setPrimaryUser(this.readBoolean());
        skin.setOverridingPlayerAppearance(this.readBoolean());
        return skin;
    }

    public void writeUnsignedVarInt(int value) {
        ByteBufVarInt.writeUnsignedInt(this, value);
    }

    public int readUnsignedVarInt() {
        return ByteBufVarInt.readUnsignedInt(this);
    }

    public void writeVarInt(int value) {
        ByteBufVarInt.writeInt(this, value);
    }

    public int readVarInt() {
        return ByteBufVarInt.readInt(this);
    }

    public void writeUnsignedVarLong(long value) {
        ByteBufVarInt.writeUnsignedLong(this, value);
    }

    public long readUnsignedVarLong() {
        return ByteBufVarInt.readUnsignedLong(this);
    }

    public void writeVarLong(long value) {
        ByteBufVarInt.writeLong(this, value);
    }

    public long readVarLong() {
        return ByteBufVarInt.readLong(this);
    }

    public Item readSlot() {
        return this.readSlot(false);
    }

    public Item readSlot(boolean instanceItem) {
        int runtimeId = this.readVarInt();
        if (runtimeId == 0) {
            return Item.AIR;
        }

        int count = readShortLE();
        int damage = (int) readUnsignedVarInt();

        Integer netId = null;
        if (!instanceItem) {
            boolean hasNetId = readBoolean();
            if (hasNetId) {
                netId = this.readVarInt();
            }
        }
        int blockRuntimeId = this.readVarInt();

        long blockingTicks = 0;
        CompoundTag compoundTag = null;
        String[] canPlace;
        String[] canBreak;
        Item item;
        if (blockRuntimeId == 0) {
            item = Item.get(Registries.ITEM_RUNTIMEID.getIdentifier(runtimeId), damage, count);
        } else {
            item = Item.get(Registries.ITEM_RUNTIMEID.getIdentifier(runtimeId), damage, count);
            BlockState blockState = Registries.BLOCKSTATE.get(blockRuntimeId);
            if (blockState != null) {
                item.setBlockUnsafe(blockState.toBlock());
            }
        }

        if (netId != null) {
            item.setNetId(netId);
        }

        byte[] bytes = new byte[readUnsignedVarInt()];
        readBytes(bytes);
        ByteBuf buf = ByteBufAllocator.DEFAULT.ioBuffer(bytes.length);
        buf.writeBytes(bytes);
        try (ByteBufInputStream stream = new ByteBufInputStream(buf)) {
            int nbtSize = stream.readShort();
            if (nbtSize > 0) {
                compoundTag = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN, false);
            } else if (nbtSize == -1) {
                int tagCount = stream.readUnsignedByte();
                if (tagCount != 1) throw new IllegalArgumentException("Expected 1 tag but got " + tagCount);
                compoundTag = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN, false);
            }

            canPlace = new String[stream.readInt()];
            for (int i = 0; i < canPlace.length; i++) {
                canPlace[i] = stream.readUTF();
            }

            canBreak = new String[stream.readInt()];
            for (int i = 0; i < canBreak.length; i++) {
                canBreak[i] = stream.readUTF();
            }

            if (Objects.equals(item.getId(), ItemID.SHIELD)) {
                blockingTicks = stream.readLong();//blockingTicks
            }
            if (compoundTag != null) {
                item.setCompoundTag(compoundTag);
            }
            Block[] canPlaces = new Block[canPlace.length];
            for (int i = 0; i < canPlace.length; i++) {
                canPlaces[i] = Block.get(canPlace[i]);
            }
            if (canPlaces.length > 0) {
                item.setCanDestroy(canPlaces);
            }
            Block[] canBreaks = new Block[canBreak.length];
            for (int i = 0; i < canBreak.length; i++) {
                canBreaks[i] = Block.get(canBreak[i]);
            }
            if (canBreaks.length > 0) {
                item.setCanPlaceOn(canBreaks);
            }
            return item;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read item user data", e);
        } finally {
            buf.release();
        }
    }

    public void writeSlot(Item item) {
        this.writeSlot(item, false);
    }

    public void writeSlot(Item item, boolean instanceItem) {
        if (item == null || item.isNull()) {
            writeByte((byte) 0);
            return;
        }

        int networkId = item.getRuntimeId();
        writeVarInt(networkId);//write item runtimeId
        writeShortLE(item.getCount());//write item count
        writeUnsignedVarInt(item.getDamage());//write damage value


        if (!instanceItem) {
            writeBoolean(item.isUsingNetId()); // isUsingNetId
            if (item.isUsingNetId()) {
                writeVarInt(item.getNetId()); // netId
            }
        }

        writeVarInt(item.isBlock() ? item.getBlockUnsafe().getRuntimeId() : 0);

        ByteBuf userDataBuf = ByteBufAllocator.DEFAULT.ioBuffer();
        try (LittleEndianByteBufOutputStream stream = new LittleEndianByteBufOutputStream(userDataBuf)) {
            if (item.hasCompoundTag()) {
                stream.writeShort(-1);
                stream.writeByte(1); // Hardcoded in current version
                stream.write(NBTIO.write(item.getNamedTag(), ByteOrder.LITTLE_ENDIAN));
            } else {
                userDataBuf.writeShortLE(0);
            }

            List<String> canPlaceOn = extractStringList(item, "CanPlaceOn");//write canPlace
            stream.writeInt(canPlaceOn.size());
            for (String string : canPlaceOn) {
                stream.writeUTF(string);
            }

            List<String> canDestroy = extractStringList(item, "CanDestroy");//write canBreak
            stream.writeInt(canDestroy.size());
            for (String string : canDestroy) {
                stream.writeUTF(string);
            }

            if (Objects.equals(item.getId(), ItemID.SHIELD)) {
                stream.writeLong(0);//BlockingTicks // todo add BlockingTicks to Item Class. Find out what Blocking Ticks are
            }

            byte[] bytes = new byte[userDataBuf.readableBytes()];
            userDataBuf.readBytes(bytes);
            writeByteArray(bytes);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write item user data", e);
        } finally {
            userDataBuf.release();
        }
    }

    public void writeRecipeIngredient(ItemDescriptor itemDescriptor) {
        ItemDescriptorType type = itemDescriptor.getType();
        this.writeByte((byte) type.ordinal());
        switch (type) {
            case DEFAULT -> {
                var ingredient = itemDescriptor.toItem();
                if (ingredient == null || ingredient.isNull()) {
                    this.writeShortLE(0);
                    this.writeVarInt(0); // item == null ? 0 : item.getCount()
                    return;
                }
                int networkId = ingredient.getRuntimeId();
                int damage = ingredient.hasMeta() ? ingredient.getDamage() : 0x7fff;
                this.writeShortLE(networkId);
                this.writeShortLE(damage);
            }
            case MOLANG -> {
                MolangDescriptor molangDescriptor = (MolangDescriptor) itemDescriptor;
                this.writeString(molangDescriptor.getTagExpression());
                this.writeByte((byte) molangDescriptor.getMolangVersion());
            }
            case COMPLEX_ALIAS -> {
                ComplexAliasDescriptor complexAliasDescriptor = (ComplexAliasDescriptor) itemDescriptor;
                this.writeString(complexAliasDescriptor.getName());
            }
            case ITEM_TAG -> {
                ItemTagDescriptor tagDescriptor = (ItemTagDescriptor) itemDescriptor;
                this.writeString(tagDescriptor.getItemTag());
            }
            case DEFERRED -> {
                DeferredDescriptor deferredDescriptor = (DeferredDescriptor) itemDescriptor;
                this.writeString(deferredDescriptor.getFullName());
                this.writeShortLE(deferredDescriptor.getAuxValue());
            }
            /*case INVALID -> {
            }*/
        }

        this.writeVarInt(itemDescriptor.getCount());
    }

    private List<String> extractStringList(Item item, String tagName) {
        CompoundTag namedTag = item.getNamedTag();
        if (namedTag == null) {
            return Collections.emptyList();
        }

        ListTag<StringTag> listTag = namedTag.getList(tagName, StringTag.class);
        if (listTag == null) {
            return Collections.emptyList();
        }

        int size = listTag.size();
        List<String> values = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            StringTag stringTag = listTag.get(i);
            if (stringTag != null) {
                values.add(stringTag.data);
            }
        }
        return values;
    }

    public BlockVector3 readSignedBlockPosition() {
        return new BlockVector3(readVarInt(), readVarInt(), readVarInt());
    }

    public void writeSignedBlockPosition(BlockVector3 v) {
        writeVarInt(v.x);
        writeVarInt(v.y);
        writeVarInt(v.z);
    }

    public BlockVector3 readBlockVector3() {
        return new BlockVector3(this.readVarInt(), (int) this.readUnsignedVarInt(), this.readVarInt());
    }

    public void writeBlockVector3(BlockVector3 v) {
        this.writeBlockVector3(v.x, v.y, v.z);
    }

    public void writeBlockVector3(int x, int y, int z) {
        this.writeVarInt(x);
        this.writeUnsignedVarInt((int) Integer.toUnsignedLong(y));
        this.writeVarInt(z);
    }

    public Vector3f readVector3f() {
        return new Vector3f(this.readFloatLE(), this.readFloatLE(), this.readFloatLE());
    }

    public void writeVector3f(Vector3f v) {
        this.writeVector3f(v.x, v.y, v.z);
    }

    public void writeVector3f(float x, float y, float z) {
        this.writeFloatLE(x);
        this.writeFloatLE(y);
        this.writeFloatLE(z);
    }

    public Vector2f readVector2f() {
        return new Vector2f(this.readFloatLE(), this.readFloatLE());
    }

    public void writeVector2f(Vector2f v) {
        this.writeVector2f(v.x, v.y);
    }

    public void writeVector2f(float x, float y) {
        this.writeFloatLE(x);
        this.writeFloatLE(y);
    }

    public void writeGameRules(GameRules gameRules) {
        // LinkedHashMap gives mutability and is faster in iteration
        val rules = new LinkedHashMap<>(gameRules.getGameRules());
        rules.keySet().removeIf(GameRule::isDeprecated);

        this.writeUnsignedVarInt(rules.size());
        rules.forEach((gameRule, value) -> {
            this.writeString(gameRule.getName().toLowerCase());
            value.write(this);
        });
    }

    /**
     * Reads and returns an EntityUniqueID
     *
     * @return int
     */
    public long readEntityUniqueId() {
        return this.readVarLong();
    }

    /**
     * Writes an EntityUniqueID
     */
    public void writeEntityUniqueId(long eid) {
        this.writeVarLong(eid);
    }

    /**
     * Reads and returns an EntityRuntimeID
     */
    public long readEntityRuntimeId() {
        return this.readUnsignedVarLong();
    }

    /**
     * Writes an EntityUniqueID
     */
    public void writeEntityRuntimeId(long eid) {
        this.writeUnsignedVarLong(eid);
    }

    public BlockFace readBlockFace() {
        return BlockFace.fromIndex(this.readVarInt());
    }

    public void writeBlockFace(BlockFace face) {
        this.writeVarInt(face.getIndex());
    }

    public void writeEntityLink(EntityLink link) {
        writeEntityUniqueId(link.fromEntityUniquieId);
        writeEntityUniqueId(link.toEntityUniquieId);
        writeByte(link.type);
        writeBoolean(link.immediate);
        writeBoolean(link.riderInitiated);
    }

    public EntityLink readEntityLink() {
        return new EntityLink(
                readEntityUniqueId(),
                readEntityUniqueId(),
                EntityLink.Type.values()[readByte()],
                readBoolean(),
                readBoolean()
        );
    }

    public <T> void writeArray(Collection<T> collection, Consumer<T> writer) {
        if (collection == null) {
            writeUnsignedVarInt(0);
            return;
        }
        writeUnsignedVarInt(collection.size());
        collection.forEach(writer);
    }

    public <T> void writeArray(T[] collection, Consumer<T> writer) {
        if (collection == null) {
            writeUnsignedVarInt(0);
            return;
        }
        writeUnsignedVarInt(collection.length);
        for (T t : collection) {
            writer.accept(t);
        }
    }

    public <T> void writeArray(Collection<T> array, BiConsumer<HandleByteBuf, T> biConsumer) {
        this.writeUnsignedVarInt(array.size());
        for (T val : array) {
            biConsumer.accept(this, val);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T[] readArray(Class<T> clazz, Function<HandleByteBuf, T> function) {
        ArrayDeque<T> deque = new ArrayDeque<>();
        int count = readUnsignedVarInt();
        for (int i = 0; i < count; i++) {
            deque.add(function.apply(this));
        }
        return deque.toArray((T[]) Array.newInstance(clazz, 0));
    }

    public <T> void readArray(Collection<T> array, Function<HandleByteBuf, T> function) {
        readArray(array, HandleByteBuf::readUnsignedVarInt, function);
    }

    public <T> void readArray(Collection<T> array, ToLongFunction<HandleByteBuf> lengthReader, Function<HandleByteBuf, T> function) {
        long length = lengthReader.applyAsLong(this);
        for (int i = 0; i < length; i++) {
            array.add(function.apply(this));
        }
    }

    @SneakyThrows(IOException.class)
    public CompoundTag readTag() {
        try (ByteBufInputStream is = new ByteBufInputStream(this)) {
            return NBTIO.read(is);
        }
    }

    @SneakyThrows(IOException.class)
    public void writeTag(CompoundTag tag) {
        writeBytes(NBTIO.write(tag));
    }

    public ItemStackRequest readItemStackRequest() {
        int requestId = readVarInt();
        ItemStackRequestAction[] actions = readArray(ItemStackRequestAction.class, (s) -> {
            ItemStackRequestActionType itemStackRequestActionType = ItemStackRequestActionType.fromId(s.readByte());
            return readRequestActionData(itemStackRequestActionType);
        });
        String[] filteredStrings = readArray(String.class, HandleByteBuf::readString);

        int originVal = readIntLE();
        TextProcessingEventOrigin origin = originVal == -1 ? null : TextProcessingEventOrigin.fromId(originVal);  // new for v552
        return new ItemStackRequest(requestId, actions, filteredStrings, origin);
    }

    protected ItemStackRequestAction readRequestActionData(ItemStackRequestActionType type) {
        return switch (type) {
            case CRAFT_REPAIR_AND_DISENCHANT -> new CraftGrindstoneAction((int) readUnsignedInt(), readVarInt());
            case CRAFT_LOOM -> new CraftLoomAction(readString());
            case CRAFT_RECIPE_AUTO -> new AutoCraftRecipeAction(
                    (int) readUnsignedInt(), readUnsignedByte(), Collections.emptyList()
            );
            case CRAFT_RESULTS_DEPRECATED -> new CraftResultsDeprecatedAction(
                    readArray(Item.class, (s) -> s.readSlot(true)),
                    readUnsignedByte()
            );
            case MINE_BLOCK -> new MineBlockAction(readVarInt(), readVarInt(), readVarInt());
            case CRAFT_RECIPE_OPTIONAL -> new CraftRecipeOptionalAction((int) readUnsignedInt(), readIntLE());
            case TAKE -> new TakeAction(
                    readUnsignedByte(),
                    readStackRequestSlotInfo(),
                    readStackRequestSlotInfo()
            );
            case PLACE -> new PlaceAction(
                    readUnsignedByte(),
                    readStackRequestSlotInfo(),
                    readStackRequestSlotInfo()
            );
            case SWAP -> new SwapAction(
                    readStackRequestSlotInfo(),
                    readStackRequestSlotInfo()
            );
            case DROP -> new DropAction(
                    readUnsignedByte(),
                    readStackRequestSlotInfo(),
                    readBoolean()
            );
            case DESTROY -> new DestroyAction(
                    readUnsignedByte(),
                    readStackRequestSlotInfo()
            );
            case CONSUME -> new ConsumeAction(
                    readUnsignedByte(),
                    readStackRequestSlotInfo()
            );
            case CREATE -> new CreateAction(
                    readUnsignedByte()
            );
            case LAB_TABLE_COMBINE -> new LabTableCombineAction();
            case BEACON_PAYMENT -> new BeaconPaymentAction(
                    readVarInt(),
                    readVarInt()
            );
            case CRAFT_RECIPE -> new CraftRecipeAction(
                    readUnsignedVarInt()
            );
            case CRAFT_CREATIVE -> new CraftCreativeAction(
                    readUnsignedVarInt()
            );
            case CRAFT_NON_IMPLEMENTED_DEPRECATED -> new CraftNonImplementedAction();
            default -> throw new UnsupportedOperationException("Unhandled stack request action type: " + type);
        };
    }

    private ItemStackRequestSlotData readStackRequestSlotInfo() {
        return new ItemStackRequestSlotData(
                ContainerSlotType.fromId(readByte()),
                readUnsignedByte(),
                readVarInt()
        );
    }


    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        return buf.indexOf(fromIndex, toIndex, value);
    }

    @Override
    public int bytesBefore(byte value) {
        return buf.bytesBefore(value);
    }

    @Override
    public int bytesBefore(int length, byte value) {
        return buf.bytesBefore(length, value);
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        return buf.bytesBefore(index, length, value);
    }

    @Override
    public int forEachByte(ByteProcessor processor) {
        return buf.forEachByte(processor);
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        return buf.forEachByte(index, length, processor);
    }

    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        return buf.forEachByteDesc(processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        return buf.forEachByteDesc(index, length, processor);
    }

    @Override
    public ByteBuf copy() {
        return buf.copy();
    }

    @Override
    public ByteBuf copy(int index, int length) {
        return buf.copy(index, length);
    }

    @Override
    public ByteBuf slice() {
        return buf.slice();
    }

    @Override
    public ByteBuf retainedSlice() {
        return buf.retainedSlice();
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return buf.slice(index, length);
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return buf.retainedSlice(index, length);
    }

    @Override
    public ByteBuf duplicate() {
        return buf.duplicate();
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return buf.retainedDuplicate();
    }

    @Override
    public int nioBufferCount() {
        return buf.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return buf.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return buf.nioBuffer(index, length);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return buf.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return buf.nioBuffers(index, length);
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        return buf.internalNioBuffer(index, length);
    }

    @Override
    public boolean hasArray() {
        return buf.hasArray();
    }

    @Override
    public byte[] array() {
        return buf.array();
    }

    @Override
    public int arrayOffset() {
        return buf.arrayOffset();
    }

    @Override
    public String toString(Charset charset) {
        return buf.toString(charset);
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        return buf.toString(index, length, charset);
    }

    @Override
    public int hashCode() {
        return buf.hashCode();
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        return buf.equals(obj);
    }

    @Override
    public int compareTo(ByteBuf buffer) {
        return buf.compareTo(buffer);
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + '(' + buf.toString() + ')';
    }

    @Override
    public ByteBuf retain(int increment) {
        buf.retain(increment);
        return this;
    }

    @Override
    public ByteBuf retain() {
        buf.retain();
        return this;
    }

    @Override
    public ByteBuf touch() {
        buf.touch();
        return this;
    }

    @Override
    public ByteBuf touch(Object hint) {
        buf.touch(hint);
        return this;
    }

    @Override
    public final boolean isReadable(int size) {
        return buf.isReadable(size);
    }

    @Override
    public final boolean isWritable(int size) {
        return buf.isWritable(size);
    }

    @Override
    public final int refCnt() {
        return buf.refCnt();
    }

    @Override
    public boolean release() {
        return buf.release();
    }

    @Override
    public boolean release(int decrement) {
        return buf.release(decrement);
    }
}
