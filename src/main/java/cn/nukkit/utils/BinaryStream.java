package cn.nukkit.utils;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.*;
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
import cn.nukkit.recipe.ComplexAliasDescriptor;
import cn.nukkit.recipe.DeferredDescriptor;
import cn.nukkit.recipe.ItemDescriptor;
import cn.nukkit.recipe.ItemDescriptorType;
import cn.nukkit.recipe.ItemTagDescriptor;
import cn.nukkit.recipe.MolangDescriptor;
import cn.nukkit.registry.Registries;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Array;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
public class BinaryStream {
    public int offset;
    private byte[] buffer;
    protected int count;
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    public BinaryStream() {
        this.buffer = new byte[32];
        this.offset = 0;
        this.count = 0;
    }

    public BinaryStream(byte[] buffer) {
        this(buffer, 0);
    }

    public BinaryStream(byte[] buffer, int offset) {
        this.buffer = buffer;
        this.offset = offset;
        this.count = buffer.length;
    }

    public BinaryStream reset() {
        this.offset = 0;
        this.count = 0;
        return this;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
        this.count = buffer == null ? -1 : buffer.length;
    }

    public void setBuffer(byte[] buffer, int offset) {
        this.setBuffer(buffer);
        this.setOffset(offset);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public byte[] getBuffer() {
        if (count == 0) return null;
        return Arrays.copyOf(buffer, count);
    }

    public int getCount() {
        return count;
    }

    public byte[] get() {
        return this.get(this.count - this.offset);
    }

    public byte[] get(int len) {
        if (len < 0) {
            this.offset = this.count - 1;
            return EmptyArrays.EMPTY_BYTES;
        }
        len = Math.min(len, this.getCount() - this.offset);
        this.offset += len;
        return Arrays.copyOfRange(this.buffer, this.offset - len, this.offset);
    }

    public void put(byte[] bytes) {
        if (bytes == null) {
            return;
        }

        this.ensureCapacity(this.count + bytes.length);

        System.arraycopy(bytes, 0, this.buffer, this.count, bytes.length);
        this.count += bytes.length;
    }

    public long getLong() {
        return Binary.readLong(this.get(8));
    }

    public void putLong(long l) {
        this.put(Binary.writeLong(l));
    }

    public int getInt() {
        return Binary.readInt(this.get(4));
    }

    public void putInt(int i) {
        this.put(Binary.writeInt(i));
    }

    public void putMedium(int i) {
        putByte((byte) (i >>> 16));
        putByte((byte) (i >>> 8));
        putByte((byte) i);
    }

    public int getMedium() {
        int value = (getByte() & 0xff) << 16 |
                (getByte() & 0xff) << 8 |
                getByte() & 0xff;
        if ((value & 0x800000) != 0) {
            value |= 0xff000000;
        }
        return value;
    }

    public long getLLong() {
        return Binary.readLLong(this.get(8));
    }

    public void putLLong(long l) {
        this.put(Binary.writeLLong(l));
    }

    public int getLInt() {
        return Binary.readLInt(this.get(4));
    }

    public void putLInt(int i) {
        this.put(Binary.writeLInt(i));
    }

    public <T> void putNotNull(T data, Consumer<T> consumer) {
        boolean present = data != null;
        putBoolean(present);
        if (present) {
            consumer.accept(data);
        }
    }

    public <T> void putOptional(OptionalValue<T> data, Consumer<T> consumer) {
        boolean present = data.isPresent();
        putBoolean(present);
        if (present) {
            consumer.accept(data.get());
        }
    }

    public int getShort() {
        return Binary.readShort(this.get(2));
    }

    public void putShort(int s) {
        this.put(Binary.writeShort(s));
    }

    public int getLShort() {
        return Binary.readLShort(this.get(2));
    }

    public void putLShort(int s) {
        this.put(Binary.writeLShort(s));
    }

    public float getFloat() {
        return getFloat(-1);
    }

    public float getFloat(int accuracy) {
        return Binary.readFloat(this.get(4), accuracy);
    }

    public void putFloat(float v) {
        this.put(Binary.writeFloat(v));
    }

    public float getLFloat() {
        return getLFloat(-1);
    }

    public float getLFloat(int accuracy) {
        return Binary.readLFloat(this.get(4), accuracy);
    }

    public void putLFloat(float v) {
        this.put(Binary.writeLFloat(v));
    }

    public int getTriad() {
        return Binary.readTriad(this.get(3));
    }

    public void putTriad(int triad) {
        this.put(Binary.writeTriad(triad));
    }

    public int getLTriad() {
        return Binary.readLTriad(this.get(3));
    }

    public void putLTriad(int triad) {
        this.put(Binary.writeLTriad(triad));
    }

    public boolean getBoolean() {
        return this.getByte() == 0x01;
    }

    public void putBoolean(boolean bool) {
        this.putByte((byte) (bool ? 1 : 0));
    }

    public byte getByte() {
        return (byte) (this.buffer[this.offset++] & 0xff);
    }

    public void putByte(byte b) {
        this.put(new byte[]{b});
    }

    /**
     * Reads a list of Attributes from the stream.
     *
     * @return Attribute[]
     */
    public Attribute[] getAttributeList() throws Exception {
        List<Attribute> list = new ArrayList<>();
        long count = this.getUnsignedVarInt();

        for (int i = 0; i < count; ++i) {
            String name = this.getString();
            Attribute attr = Attribute.getAttributeByName(name);
            if (attr != null) {
                attr.setMinValue(this.getLFloat());
                attr.setValue(this.getLFloat());
                attr.setMaxValue(this.getLFloat());
                list.add(attr);
            } else {
                throw new Exception("Unknown attribute type \"" + name + "\"");
            }
        }

        return list.toArray(Attribute.EMPTY_ARRAY);
    }

    /**
     * Writes a list of Attributes to the packet buffer using the standard format.
     */
    public void putAttributeList(Attribute[] attributes) {
        this.putUnsignedVarInt(attributes.length);
        for (Attribute attribute : attributes) {
            this.putString(attribute.getName());
            this.putLFloat(attribute.getMinValue());
            this.putLFloat(attribute.getValue());
            this.putLFloat(attribute.getMaxValue());
        }
    }

    public void putUUID(UUID uuid) {
        this.put(Binary.writeUUID(uuid));
    }

    public UUID getUUID() {
        return Binary.readUUID(this.get(16));
    }

    public void putSkin(Skin skin) {
        this.putString(skin.getSkinId());
        this.putString(skin.getPlayFabId());
        this.putString(skin.getSkinResourcePatch());
        this.putImage(skin.getSkinData());

        List<SkinAnimation> animations = skin.getAnimations();
        this.putLInt(animations.size());
        for (SkinAnimation animation : animations) {
            this.putImage(animation.image);
            this.putLInt(animation.type);
            this.putLFloat(animation.frames);
            this.putLInt(animation.expression);
        }

        this.putImage(skin.getCapeData());
        this.putString(skin.getGeometryData());
        this.putString(skin.getGeometryDataEngineVersion());
        this.putString(skin.getAnimationData());
        this.putString(skin.getCapeId());
        this.putString(skin.getFullSkinId());
        this.putString(skin.getArmSize());
        this.putString(skin.getSkinColor());
        List<PersonaPiece> pieces = skin.getPersonaPieces();
        this.putLInt(pieces.size());
        for (PersonaPiece piece : pieces) {
            this.putString(piece.id);
            this.putString(piece.type);
            this.putString(piece.packId);
            this.putBoolean(piece.isDefault);
            this.putString(piece.productId);
        }

        List<PersonaPieceTint> tints = skin.getTintColors();
        this.putLInt(tints.size());
        for (PersonaPieceTint tint : tints) {
            this.putString(tint.pieceType);
            List<String> colors = tint.colors;
            this.putLInt(colors.size());
            for (String color : colors) {
                this.putString(color);
            }
        }

        this.putBoolean(skin.isPremium());
        this.putBoolean(skin.isPersona());
        this.putBoolean(skin.isCapeOnClassic());
        this.putBoolean(skin.isPrimaryUser());
        this.putBoolean(skin.isOverridingPlayerAppearance());
    }

    public Skin getSkin() {
        Skin skin = new Skin();
        skin.setSkinId(this.getString());
        skin.setPlayFabId(this.getString());
        skin.setSkinResourcePatch(this.getString());
        skin.setSkinData(this.getImage());

        int animationCount = this.getLInt();
        for (int i = 0; i < animationCount; i++) {
            SerializedImage image = this.getImage();
            int type = this.getLInt();
            float frames = this.getLFloat();
            int expression = this.getLInt();
            skin.getAnimations().add(new SkinAnimation(image, type, frames, expression));
        }

        skin.setCapeData(this.getImage());
        skin.setGeometryData(this.getString());
        skin.setGeometryDataEngineVersion(this.getString());
        skin.setAnimationData(this.getString());
        skin.setCapeId(this.getString());
        skin.setFullSkinId(this.getString());
        skin.setArmSize(this.getString());
        skin.setSkinColor(this.getString());

        int piecesLength = this.getLInt();
        for (int i = 0; i < piecesLength; i++) {
            String pieceId = this.getString();
            String pieceType = this.getString();
            String packId = this.getString();
            boolean isDefault = this.getBoolean();
            String productId = this.getString();
            skin.getPersonaPieces().add(new PersonaPiece(pieceId, pieceType, packId, isDefault, productId));
        }

        int tintsLength = this.getLInt();
        for (int i = 0; i < tintsLength; i++) {
            String pieceType = this.getString();
            List<String> colors = new ArrayList<>();
            int colorsLength = this.getLInt();
            for (int i2 = 0; i2 < colorsLength; i2++) {
                colors.add(this.getString());
            }
            skin.getTintColors().add(new PersonaPieceTint(pieceType, colors));
        }

        skin.setPremium(this.getBoolean());
        skin.setPersona(this.getBoolean());
        skin.setCapeOnClassic(this.getBoolean());
        skin.setPrimaryUser(this.getBoolean());
        skin.setOverridingPlayerAppearance(this.getBoolean());
        return skin;
    }

    public void putImage(SerializedImage image) {
        this.putLInt(image.width);
        this.putLInt(image.height);
        this.putByteArray(image.data);
    }

    public SerializedImage getImage() {
        int width = this.getLInt();
        int height = this.getLInt();
        byte[] data = this.getByteArray();
        return new SerializedImage(width, height, data);
    }

    public Item getSlot() {
        return this.getSlot(false);
    }

    public Item getSlot(boolean instanceItem) {
        int runtimeId = getVarInt();
        if (runtimeId == 0) {
            return Item.AIR;
        }

        int count = getLShort();
        int damage = (int) getUnsignedVarInt();

        Integer netId = null;
        if (!instanceItem) {
            boolean hasNetId = getBoolean();
            if (hasNetId) {
                netId = getVarInt();
            }
        }
        int blockRuntimeId = getVarInt();

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

        byte[] bytes = getByteArray();
        ByteBuf buf = ByteBufAllocator.DEFAULT.ioBuffer(bytes.length);
        buf.writeBytes(bytes);
        try (LittleEndianByteBufInputStream stream = new LittleEndianByteBufInputStream(buf)) {
            int nbtSize = stream.readShort();
            if (nbtSize > 0) {
                compoundTag = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN);
            } else if (nbtSize == -1) {
                int tagCount = stream.readUnsignedByte();
                if (tagCount != 1) throw new IllegalArgumentException("Expected 1 tag but got " + tagCount);
                compoundTag = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN);
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

    private Item readUnknownItem(Item item) {
        return null;//todo reimplement for customitem
    }

    private Item createFakeUnknownItem(Item item) {
        return null;//todo reimplement for customitem
    }

    public void putSlot(Item item) {
        this.putSlot(item, false);
    }

    public void putSlot(Item item, boolean instanceItem) {
        if (item == null || item.isNull()) {
            putByte((byte) 0);
            return;
        }

        int networkId = item.getRuntimeId();
        putVarInt(networkId);//write item runtimeId
        putLShort(item.getCount());//write item count
        putUnsignedVarInt(item.getDamage());//write damage value


        if (!instanceItem) {
            putBoolean(item.isUsingNetId()); // isUsingNetId
            if (item.isUsingNetId()) {
                putVarInt(item.getNetId()); // netId
            }
        }

        putVarInt(item.isBlock() ? item.getBlockUnsafe().getRuntimeId() : 0);

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
            putByteArray(bytes);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write item user data", e);
        } finally {
            userDataBuf.release();
        }
    }

    public void putRecipeIngredient(ItemDescriptor itemDescriptor) {
        ItemDescriptorType type = itemDescriptor.getType();
        this.putByte((byte) type.ordinal());
        switch (type) {
            case DEFAULT -> {
                var ingredient = itemDescriptor.toItem();
                if (ingredient == null || ingredient.isNull()) {
                    this.putLShort(0);
                    this.putVarInt(0); // item == null ? 0 : item.getCount()
                    return;
                }
                int networkId = ingredient.getRuntimeId();
                int damage = ingredient.hasMeta() ? ingredient.getDamage() : 0x7fff;
                this.putLShort(networkId);
                this.putLShort(damage);
            }
            case MOLANG -> {
                MolangDescriptor molangDescriptor = (MolangDescriptor) itemDescriptor;
                this.putString(molangDescriptor.getTagExpression());
                this.putByte((byte) molangDescriptor.getMolangVersion());
            }
            case COMPLEX_ALIAS -> {
                ComplexAliasDescriptor complexAliasDescriptor = (ComplexAliasDescriptor) itemDescriptor;
                this.putString(complexAliasDescriptor.getName());
            }
            case ITEM_TAG -> {
                ItemTagDescriptor tagDescriptor = (ItemTagDescriptor) itemDescriptor;
                this.putString(tagDescriptor.getItemTag());
            }
            case DEFERRED -> {
                DeferredDescriptor deferredDescriptor = (DeferredDescriptor) itemDescriptor;
                this.putString(deferredDescriptor.getFullName());
                this.putLShort(deferredDescriptor.getAuxValue());
            }
            /*case INVALID -> {
            }*/
        }

        this.putVarInt(itemDescriptor.getCount());
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

    public byte[] getByteArray() {
        return this.get((int) this.getUnsignedVarInt());
    }

    public void putByteArray(byte[] b) {
        this.putUnsignedVarInt(b.length);
        this.put(b);
    }

    public String getString() {
        return new String(this.getByteArray(), StandardCharsets.UTF_8);
    }

    public void putString(String string) {
        byte[] b = string.getBytes(StandardCharsets.UTF_8);
        this.putByteArray(b);
    }

    public long getUnsignedVarInt() {
        return VarInt.readUnsignedVarInt(this);
    }

    public void putUnsignedVarInt(long v) {
        VarInt.writeUnsignedVarInt(this, v);
    }

    public int getVarInt() {
        return VarInt.readVarInt(this);
    }

    public void putVarInt(int v) {
        VarInt.writeVarInt(this, v);
    }

    public long getVarLong() {
        return VarInt.readVarLong(this);
    }

    public void putVarLong(long v) {
        VarInt.writeVarLong(this, v);
    }

    public long getUnsignedVarLong() {
        return VarInt.readUnsignedVarLong(this);
    }

    public void putUnsignedVarLong(long v) {
        VarInt.writeUnsignedVarLong(this, v);
    }

    public BlockVector3 getBlockVector3() {
        return new BlockVector3(this.getVarInt(), (int) this.getUnsignedVarInt(), this.getVarInt());
    }

    public BlockVector3 getSignedBlockPosition() {
        return new BlockVector3(getVarInt(), getVarInt(), getVarInt());
    }

    public void putSignedBlockPosition(BlockVector3 v) {
        putVarInt(v.x);
        putVarInt(v.y);
        putVarInt(v.z);
    }

    public void putBlockVector3(BlockVector3 v) {
        this.putBlockVector3(v.x, v.y, v.z);
    }

    public void putBlockVector3(int x, int y, int z) {
        this.putVarInt(x);
        this.putUnsignedVarInt(Integer.toUnsignedLong(y));
        this.putVarInt(z);
    }

    public Vector3f getVector3f() {
        return new Vector3f(this.getLFloat(4), this.getLFloat(4), this.getLFloat(4));
    }

    public void putVector3f(Vector3f v) {
        this.putVector3f(v.x, v.y, v.z);
    }

    public void putVector3f(float x, float y, float z) {
        this.putLFloat(x);
        this.putLFloat(y);
        this.putLFloat(z);
    }

    public Vector2f getVector2f() {
        return new Vector2f(this.getLFloat(4), this.getLFloat(4));
    }

    public void putVector2f(Vector2f v) {
        this.putVector2f(v.x, v.y);
    }

    public void putVector2f(float x, float y) {
        this.putLFloat(x);
        this.putLFloat(y);
    }

    public void putGameRules(GameRules gameRules) {
        // LinkedHashMap gives mutability and is faster in iteration
        val rules = new LinkedHashMap<>(gameRules.getGameRules());
        rules.keySet().removeIf(GameRule::isDeprecated);

        this.putUnsignedVarInt(rules.size());
        rules.forEach((gameRule, value) -> {
            this.putString(gameRule.getName().toLowerCase());
            value.write(this);
        });
    }

    /**
     * Reads and returns an EntityUniqueID
     *
     * @return int
     */
    public long getEntityUniqueId() {
        return this.getVarLong();
    }

    /**
     * Writes an EntityUniqueID
     */
    public void putEntityUniqueId(long eid) {
        this.putVarLong(eid);
    }

    /**
     * Reads and returns an EntityRuntimeID
     */
    public long getEntityRuntimeId() {
        return this.getUnsignedVarLong();
    }

    /**
     * Writes an EntityUniqueID
     */
    public void putEntityRuntimeId(long eid) {
        this.putUnsignedVarLong(eid);
    }

    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(this.getVarInt());
    }

    public void putBlockFace(BlockFace face) {
        this.putVarInt(face.getIndex());
    }

    public void putEntityLink(EntityLink link) {
        putEntityUniqueId(link.fromEntityUniquieId);
        putEntityUniqueId(link.toEntityUniquieId);
        putByte(link.type);
        putBoolean(link.immediate);
        putBoolean(link.riderInitiated);
    }

    public EntityLink getEntityLink() {
        return new EntityLink(
                getEntityUniqueId(),
                getEntityUniqueId(),
                getByte(),
                getBoolean(),
                getBoolean()
        );
    }

    public <T> void putArray(Collection<T> collection, Consumer<T> writer) {
        if (collection == null) {
            putUnsignedVarInt(0);
            return;
        }
        putUnsignedVarInt(collection.size());
        collection.forEach(writer);
    }

    public <T> void putArray(T[] collection, Consumer<T> writer) {
        if (collection == null) {
            putUnsignedVarInt(0);
            return;
        }
        putUnsignedVarInt(collection.length);
        for (T t : collection) {
            writer.accept(t);
        }
    }

    public <T> void putArray(Collection<T> array, BiConsumer<BinaryStream, T> biConsumer) {
        this.putUnsignedVarInt(array.size());
        for (T val : array) {
            biConsumer.accept(this, val);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T[] getArray(Class<T> clazz, Function<BinaryStream, T> function) {
        ArrayDeque<T> deque = new ArrayDeque<>();
        int count = (int) getUnsignedVarInt();
        for (int i = 0; i < count; i++) {
            deque.add(function.apply(this));
        }
        return deque.toArray((T[]) Array.newInstance(clazz, 0));
    }

    public boolean feof() {
        return this.offset < 0 || this.offset >= this.buffer.length;
    }

    @SneakyThrows(IOException.class)
    public CompoundTag getTag() {
        ByteArrayInputStream is = new ByteArrayInputStream(buffer, offset, buffer.length);
        int initial = is.available();
        try {
            return NBTIO.read(is);
        } finally {
            offset += initial - is.available();
        }
    }

    @SneakyThrows(IOException.class)
    public void putTag(CompoundTag tag) {
        put(NBTIO.write(tag));
    }

    public ItemStackRequest readItemStackRequest() {
        int requestId = getVarInt();
        ItemStackRequestAction[] actions = getArray(ItemStackRequestAction.class, (s) -> {
            ItemStackRequestActionType itemStackRequestActionType = ItemStackRequestActionType.fromId(s.getByte());
            return readRequestActionData(itemStackRequestActionType);
        });
        String[] filteredStrings = getArray(String.class, BinaryStream::getString);

        int originVal = getLInt();
        TextProcessingEventOrigin origin = originVal == -1 ? null : TextProcessingEventOrigin.fromId(originVal);  // new for v552
        return new ItemStackRequest(requestId, actions, filteredStrings, origin);
    }

    protected ItemStackRequestAction readRequestActionData(ItemStackRequestActionType type) {
        return switch (type) {
            case CRAFT_REPAIR_AND_DISENCHANT -> new CraftGrindstoneAction((int) getUnsignedVarInt(), getVarInt());
            case CRAFT_LOOM -> new CraftLoomAction(getString());
            case CRAFT_RECIPE_AUTO -> new AutoCraftRecipeAction(
                    (int) getUnsignedVarInt(), getByte(), Collections.emptyList()
            );
            case CRAFT_RESULTS_DEPRECATED -> new CraftResultsDeprecatedAction(
                    getArray(Item.class, (s) -> s.getSlot(true)),
                    getByte()
            );
            case MINE_BLOCK -> new MineBlockAction(getVarInt(), getVarInt(), getVarInt());
            case CRAFT_RECIPE_OPTIONAL -> new CraftRecipeOptionalAction((int) getUnsignedVarInt(), getLInt());
            case TAKE -> new TakeAction(
                    getByte(),
                    readStackRequestSlotInfo(),
                    readStackRequestSlotInfo()
            );
            case PLACE -> new PlaceAction(
                    getByte(),
                    readStackRequestSlotInfo(),
                    readStackRequestSlotInfo()
            );
            case SWAP -> new SwapAction(
                    readStackRequestSlotInfo(),
                    readStackRequestSlotInfo()
            );
            case DROP -> new DropAction(
                    getByte(),
                    readStackRequestSlotInfo(),
                    getBoolean()
            );
            case DESTROY -> new DestroyAction(
                    getByte(),
                    readStackRequestSlotInfo()
            );
            case CONSUME -> new ConsumeAction(
                    getByte(),
                    readStackRequestSlotInfo()
            );
            case CREATE -> new CreateAction(
                    getByte()
            );
            case LAB_TABLE_COMBINE -> new LabTableCombineAction();
            case BEACON_PAYMENT -> new BeaconPaymentAction(
                    getVarInt(),
                    getVarInt()
            );
            case CRAFT_RECIPE -> new CraftRecipeAction(
                    (int) getUnsignedVarInt()
            );
            case CRAFT_CREATIVE -> new CraftCreativeAction(
                    (int) getUnsignedVarInt()
            );
            case CRAFT_NON_IMPLEMENTED_DEPRECATED -> new CraftNonImplementedAction();
            default -> throw new UnsupportedOperationException("Unhandled stack request action type: " + type);
        };
    }

    private ItemStackRequestSlotData readStackRequestSlotInfo() {
        return new ItemStackRequestSlotData(
                ContainerSlotType.fromId(getByte()),
                getByte(),
                getVarInt()
        );
    }


    private void ensureCapacity(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - buffer.length > 0) {
            grow(minCapacity);
        }
    }

    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = buffer.length;
        int newCapacity = oldCapacity << 1;

        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }

        if (newCapacity - MAX_ARRAY_SIZE > 0) {
            newCapacity = hugeCapacity(minCapacity);
        }
        this.buffer = Arrays.copyOf(buffer, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) { // overflow
            throw new OutOfMemoryError();
        }
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }
}
