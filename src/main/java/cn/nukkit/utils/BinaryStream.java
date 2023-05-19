package cn.nukkit.utils;

import cn.nukkit.api.*;
import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.BlockStateRegistry;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.inventory.recipe.*;
import cn.nukkit.item.*;
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
import cn.nukkit.network.LittleEndianByteBufInputStream;
import cn.nukkit.network.LittleEndianByteBufOutputStream;
import cn.nukkit.network.protocol.types.EntityLink;
import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.internal.EmptyArrays;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

    private static final int FALLBACK_ID = 248;

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

    public int getByte() {
        return this.buffer[this.offset++] & 0xff;
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

    @PowerNukkitXDifference(info = "Remove the name from the tag, this function will be removed in the future")
    public Item getSlot() {
        int networkId = getVarInt();
        if (networkId == 0) {
            return Item.get(0, 0, 0);
        }

        int count = getLShort();
        int damage = (int) getUnsignedVarInt();

        Integer id = null;
        String stringId = null;
        try {
            int fullId = RuntimeItems.getRuntimeMapping().getLegacyFullId(networkId);
            id = RuntimeItems.getId(fullId);

            boolean hasData = RuntimeItems.hasData(fullId);
            if (hasData) {
                damage = RuntimeItems.getData(fullId);
            }
        } catch (IllegalArgumentException unknownMapping) {
            stringId = RuntimeItems.getRuntimeMapping().getNamespacedIdByNetworkId(networkId);
            if (stringId == null) {
                throw unknownMapping;
            }
        }

        //instance item
        if (getBoolean()) { // hasNetId
            getVarInt(); // netId
        }

        int blockRuntimeId = getVarInt();//blockDefinition
        if (id != null && id <= 255 && id != FALLBACK_ID) {
            BlockState blockStateByRuntimeId = BlockStateRegistry.getBlockStateByRuntimeId(blockRuntimeId);
            if (blockStateByRuntimeId != null) {
                damage = blockStateByRuntimeId.asItemBlock().getDamage();
            }
        }

        byte[] bytes = getByteArray();
        ByteBuf buf = AbstractByteBufAllocator.DEFAULT.ioBuffer(bytes.length);
        buf.writeBytes(bytes);

        byte[] nbt = new byte[0];
        String[] canPlace;
        String[] canBreak;

        try (LittleEndianByteBufInputStream stream = new LittleEndianByteBufInputStream(buf)) {
            int nbtSize = stream.readShort();

            CompoundTag compoundTag = null;
            if (nbtSize > 0) {
                compoundTag = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN);
            } else if (nbtSize == -1) {
                int tagCount = stream.readUnsignedByte();
                if (tagCount != 1) throw new IllegalArgumentException("Expected 1 tag but got " + tagCount);
                compoundTag = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN);
            }

            if (compoundTag != null && compoundTag.getAllTags().size() > 0) {
                if (compoundTag.contains("Damage")) {
                    if (stringId != null || (id != null && id > 255)) {
                        damage = compoundTag.getInt("Damage");
                    }
                    compoundTag.remove("Damage");
                }
                if (compoundTag.contains("__DamageConflict__")) {
                    compoundTag.put("Damage", compoundTag.removeAndGet("__DamageConflict__"));
                }
                if (!compoundTag.isEmpty()) {
                    nbt = NBTIO.write(compoundTag, ByteOrder.LITTLE_ENDIAN);
                }
            }

            canPlace = new String[stream.readInt()];
            for (int i = 0; i < canPlace.length; i++) {
                canPlace[i] = stream.readUTF();
            }

            canBreak = new String[stream.readInt()];
            for (int i = 0; i < canBreak.length; i++) {
                canBreak[i] = stream.readUTF();
            }

            if (id != null && id == ItemID.SHIELD) {
                stream.readLong();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read item user data", e);
        } finally {
            buf.release();
        }

        Item item = null;
        if (id != null) {
            item = readUnknownItem(Item.get(id, damage, count, nbt));
        } else if (stringId != null) {
            final Item tmp = Item.fromString(stringId);
            tmp.setDamage(damage);
            tmp.setCount(count);
            tmp.setCompoundTag(nbt);
            item = readUnknownItem(tmp);
        }

        if (canBreak.length > 0 || canPlace.length > 0) {
            CompoundTag namedTag = item.getNamedTag();
            if (namedTag == null) {
                namedTag = new CompoundTag();
            }

            if (canBreak.length > 0) {
                ListTag<StringTag> listTag = new ListTag<>("CanDestroy");
                for (String blockName : canBreak) {
                    listTag.add(new StringTag("", blockName));
                }
                namedTag.put("CanDestroy", listTag);
            }

            if (canPlace.length > 0) {
                ListTag<StringTag> listTag = new ListTag<>("CanPlaceOn");
                for (String blockName : canPlace) {
                    listTag.add(new StringTag("", blockName));
                }
                namedTag.put("CanPlaceOn", listTag);
            }

            item.setNamedTag(namedTag);
        }

        return item;
    }

    private Item readUnknownItem(Item item) {
        if (item.getId() != FALLBACK_ID || !item.hasCompoundTag()) {
            return item;
        }

        CompoundTag tag = item.getNamedTag();
        if (!tag.containsCompound("PowerNukkitUnknown")) {
            return item;
        }

        CompoundTag pnTag = tag.getCompound("PowerNukkitUnknown");
        int itemId = pnTag.getInt("OriginalItemId");
        int meta = pnTag.getInt("OriginalMeta");
        boolean hasCustomName = pnTag.getBoolean("HasCustomName");
        boolean hasCompound = pnTag.getBoolean("HasCompound");
        boolean hasDisplayTag = pnTag.getBoolean("HasDisplayTag");
        String customName = pnTag.getString("OriginalCustomName");

        item = Item.get(itemId, meta, item.getCount());
        if (hasCompound) {
            tag.remove("PowerNukkitUnknown");
            if (!hasDisplayTag) {
                tag.remove("display");
            } else if (tag.containsCompound("display")) {
                if (!hasCustomName) {
                    tag.getCompound("display").remove("Name");
                } else {
                    tag.getCompound("display").putString("Name", customName);
                }
            }
            item.setNamedTag(tag);
        }

        return item;
    }

    private Item createFakeUnknownItem(Item item) {
        boolean hasCompound = item.hasCompoundTag();
        Item fallback = Item.getBlock(FALLBACK_ID, 0, item.getCount());
        CompoundTag tag = item.getNamedTag();
        if (tag == null) {
            tag = new CompoundTag();
        }
        tag.putCompound("PowerNukkitUnknown", new CompoundTag()
                .putInt("OriginalItemId", item.getId())
                .putInt("OriginalMeta", item.getDamage())
                .putBoolean("HasCustomName", item.hasCustomName())
                .putBoolean("HasDisplayTag", tag.contains("display"))
                .putBoolean("HasCompound", hasCompound)
                .putString("OriginalCustomName", item.getCustomName()));

        fallback.setNamedTag(tag);
        String suffix = "" + TextFormat.RESET + TextFormat.GRAY + TextFormat.ITALIC +
                " (" + item.getId() + ":" + item.getDamage() + ")";
        if (fallback.hasCustomName()) {
            fallback.setCustomName(fallback.getCustomName() + suffix);
        } else {
            fallback.setCustomName(TextFormat.RESET + "" + TextFormat.BOLD + TextFormat.RED + "Unknown" + suffix);
        }
        return fallback;
    }

    public void putSlot(Item item) {
        this.putSlot(item, false);
    }

    @PowerNukkitXDifference(info = "Remove the name from the tag, this function will be removed in the future")
    @Since("1.4.0.0-PN")
    public void putSlot(Item item, boolean instanceItem) {
        if (item == null || item.getId() == 0) {
            putByte((byte) 0);
            return;
        }

        int networkId;
        try {
            networkId = RuntimeItems.getRuntimeMapping().getNetworkId(item);
        } catch (IllegalArgumentException e) {
            log.trace(e);
            item = createFakeUnknownItem(item);
            networkId = RuntimeItems.getRuntimeMapping().getNetworkId(item);
        }
        putVarInt(networkId);//write runtimeId
        putLShort(item.getCount());//write item count

        int legacyData = 0;
        if (item.getId() > 256) { // Not a block
            //不是item_mappings.json中的物品才会写入damage值，因为item_mappings.json的作用是将旧的物品id:damage转换到最新的无damage值的物品
            if (item instanceof ItemDurable || !RuntimeItems.getRuntimeMapping().toRuntime(item.getId(), item.getDamage()).hasDamage()) {
                legacyData = item.getDamage();
            }
        } else if (item instanceof StringItem) {
            legacyData = item.getDamage();
        }

        putUnsignedVarInt(legacyData);//write damage value

        if (!instanceItem) {
            putBoolean(true); // hasNetId
            putVarInt(0); // netId
        }

        Block block = item.getBlockUnsafe();//write blockDefinition
        int blockRuntimeId = block == null ? 0 : block.getRuntimeId();
        putVarInt(blockRuntimeId);

        int data = 0;
        if (item instanceof ItemDurable || item.getId() < 256) {
            data = item.getDamage();
        }

        ByteBuf userDataBuf = ByteBufAllocator.DEFAULT.ioBuffer();
        try (LittleEndianByteBufOutputStream stream = new LittleEndianByteBufOutputStream(userDataBuf)) {
            if ((item instanceof ItemDurable && data != 0) || block != null && block.getDamage() > 0) {
                byte[] nbt = item.getCompoundTag();
                CompoundTag tag;
                if (nbt == null || nbt.length == 0) {
                    tag = new CompoundTag();
                } else {
                    tag = NBTIO.read(nbt, ByteOrder.LITTLE_ENDIAN);
                }
                if (tag.contains("Damage")) {
                    tag.put("__DamageConflict__", tag.removeAndGet("Damage"));
                }
                tag.putInt("Damage", data);
                stream.writeShort(-1);
                stream.writeByte(1); // Hardcoded in current version
                stream.write(NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN));
            } else if (item.hasCompoundTag()) {
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

            if (item.getId() == ItemID.SHIELD) {
                stream.writeLong(0);//BlockingTicks
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

    public Item getRecipeIngredient() {
        int networkId = this.getVarInt();
        if (networkId == 0) {
            return Item.get(0, 0, 0);
        }

        int legacyFullId = RuntimeItems.getRuntimeMapping().getLegacyFullId(networkId);
        int id = RuntimeItems.getId(legacyFullId);
        boolean hasData = RuntimeItems.hasData(legacyFullId);

        int damage = this.getVarInt();
        if (hasData) {
            damage = RuntimeItems.getData(legacyFullId);
        } else if (damage == 0x7fff) {
            damage = -1;
        }

        int count = this.getVarInt();
        return Item.get(id, damage, count);
    }

    @Deprecated
    @DeprecationDetails(since = "1.19.50-r2", reason = "Support more types of recipe input", replaceWith = "putRecipeIngredient(ItemDescriptor itemDescriptor)")
    public void putRecipeIngredient(Item ingredient) {
        if (ingredient == null || ingredient.getId() == 0) {
            this.putBoolean(false); // isValid? - false
            this.putVarInt(0); // item == null ? 0 : item.getCount()
            return;
        }
        this.putBoolean(true); // isValid? - true

        int networkId = RuntimeItems.getRuntimeMapping().getNetworkId(ingredient);
        int damage = ingredient.hasMeta() ? ingredient.getDamage() : 0x7fff;
        if (RuntimeItems.getRuntimeMapping().toRuntime(ingredient.getId(), ingredient.getDamage()).hasDamage()) {
            damage = 0;
        }

        this.putLShort(networkId);
        this.putLShort(damage);
        this.putVarInt(ingredient.getCount());
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r2")
    public void putRecipeIngredient(ItemDescriptor itemDescriptor) {
        ItemDescriptorType type = itemDescriptor.getType();
        this.putByte((byte) type.ordinal());
        switch (type) {
            case DEFAULT -> {
                var ingredient = itemDescriptor.toItem();
                if (ingredient == null || ingredient.getId() == 0) {
                    this.putLShort(0);
                    this.putVarInt(0); // item == null ? 0 : item.getCount()
                    return;
                }
                int networkId = RuntimeItems.getRuntimeMapping().getNetworkId(ingredient);
                int damage = ingredient.hasMeta() ? ingredient.getDamage() : 0x7fff;
                if (RuntimeItems.getRuntimeMapping().toRuntime(ingredient.getId(), ingredient.getDamage()).hasDamage()) {
                    damage = 0;
                }
                this.putLShort(networkId);
                this.putLShort(damage);
            }
            case MOLANG -> {
                MolangDescriptor molangDescriptor = (MolangDescriptor) itemDescriptor;
                this.putString(molangDescriptor.getTagExpression());
                this.putByte((byte) molangDescriptor.getMolangVersion());
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

    @Since("1.19.70-r1")
    @PowerNukkitXOnly
    public Vector2f getVector2f() {
        return new Vector2f(this.getLFloat(4), this.getLFloat(4));
    }

    @Since("1.19.70-r1")
    @PowerNukkitXOnly
    public void putVector2f(Vector2f v) {
        this.putVector2f(v.x, v.y);
    }

    @Since("1.19.70-r1")
    @PowerNukkitXOnly
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
                (byte) getByte(),
                getBoolean(),
                getBoolean()
        );
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public <T> void putArray(Collection<T> collection, Consumer<T> writer) {
        if (collection == null) {
            putUnsignedVarInt(0);
            return;
        }
        putUnsignedVarInt(collection.size());
        collection.forEach(writer);
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
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

    @PowerNukkitXOnly
    @Since("1.19.30-r1")
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
    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
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
    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public void putTag(CompoundTag tag) {
        put(NBTIO.write(tag));
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
