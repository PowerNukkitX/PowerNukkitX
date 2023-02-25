package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.data.IntPositionEntityData;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.inventory.*;
import cn.nukkit.item.Item;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.utils.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@PowerNukkitXOnly
@Since("1.19.63-r1")
public interface IHuman extends InventoryHolder {
    default void initHumanEntity(Entity human) {
        boolean isIntelligentHuman = this instanceof EntityIntelligentHuman;
        human.setDataFlag(Entity.DATA_PLAYER_FLAGS, Entity.DATA_PLAYER_FLAG_SLEEP, false);
        human.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_GRAVITY);
        human.setDataProperty(new IntPositionEntityData(Entity.DATA_PLAYER_BED_POSITION, 0, 0, 0), false);

        if (!(human instanceof Player)) {
            if (human.namedTag.contains("NameTag")) {
                human.setNameTag(human.namedTag.getString("NameTag"));
            }

            if (human.namedTag.contains("Skin") && human.namedTag.get("Skin") instanceof CompoundTag) {
                CompoundTag skinTag = human.namedTag.getCompound("Skin");
                if (!skinTag.contains("Transparent")) {
                    skinTag.putBoolean("Transparent", false);
                }
                Skin newSkin = new Skin();
                if (skinTag.contains("ModelId")) {
                    newSkin.setSkinId(skinTag.getString("ModelId"));
                }
                if (skinTag.contains("PlayFabId")) {
                    newSkin.setPlayFabId(skinTag.getString("PlayFabId"));
                }
                if (skinTag.contains("Data")) {
                    byte[] data = skinTag.getByteArray("Data");
                    if (skinTag.contains("SkinImageWidth") && skinTag.contains("SkinImageHeight")) {
                        int width = skinTag.getInt("SkinImageWidth");
                        int height = skinTag.getInt("SkinImageHeight");
                        newSkin.setSkinData(new SerializedImage(width, height, data));
                    } else {
                        newSkin.setSkinData(data);
                    }
                }
                if (skinTag.contains("CapeId")) {
                    newSkin.setCapeId(skinTag.getString("CapeId"));
                }
                if (skinTag.contains("CapeData")) {
                    byte[] data = skinTag.getByteArray("CapeData");
                    if (skinTag.contains("CapeImageWidth") && skinTag.contains("CapeImageHeight")) {
                        int width = skinTag.getInt("CapeImageWidth");
                        int height = skinTag.getInt("CapeImageHeight");
                        newSkin.setCapeData(new SerializedImage(width, height, data));
                    } else {
                        newSkin.setCapeData(data);
                    }
                }
                if (skinTag.contains("GeometryName")) {
                    newSkin.setGeometryName(skinTag.getString("GeometryName"));
                }
                if (skinTag.contains("SkinResourcePatch")) {
                    newSkin.setSkinResourcePatch(new String(skinTag.getByteArray("SkinResourcePatch"), StandardCharsets.UTF_8));
                }
                if (skinTag.contains("GeometryData")) {
                    newSkin.setGeometryData(new String(skinTag.getByteArray("GeometryData"), StandardCharsets.UTF_8));
                }
                if (skinTag.contains("SkinAnimationData")) {
                    newSkin.setAnimationData(new String(skinTag.getByteArray("SkinAnimationData"), StandardCharsets.UTF_8));
                } else if (skinTag.contains("AnimationData")) { // backwards compatible
                    newSkin.setAnimationData(new String(skinTag.getByteArray("AnimationData"), StandardCharsets.UTF_8));
                }
                if (skinTag.contains("PremiumSkin")) {
                    newSkin.setPremium(skinTag.getBoolean("PremiumSkin"));
                }
                if (skinTag.contains("PersonaSkin")) {
                    newSkin.setPersona(skinTag.getBoolean("PersonaSkin"));
                }
                if (skinTag.contains("CapeOnClassicSkin")) {
                    newSkin.setCapeOnClassic(skinTag.getBoolean("CapeOnClassicSkin"));
                }
                if (skinTag.contains("AnimatedImageData")) {
                    ListTag<CompoundTag> list = skinTag.getList("AnimatedImageData", CompoundTag.class);
                    for (CompoundTag animationTag : list.getAll()) {
                        float frames = animationTag.getFloat("Frames");
                        int type = animationTag.getInt("Type");
                        byte[] image = animationTag.getByteArray("Image");
                        int width = animationTag.getInt("ImageWidth");
                        int height = animationTag.getInt("ImageHeight");
                        int expression = animationTag.getInt("AnimationExpression");
                        newSkin.getAnimations().add(new SkinAnimation(new SerializedImage(width, height, image), type, frames, expression));
                    }
                }
                if (skinTag.contains("ArmSize")) {
                    newSkin.setArmSize(skinTag.getString("ArmSize"));
                }
                if (skinTag.contains("SkinColor")) {
                    newSkin.setSkinColor(skinTag.getString("SkinColor"));
                }
                if (skinTag.contains("PersonaPieces")) {
                    ListTag<CompoundTag> pieces = skinTag.getList("PersonaPieces", CompoundTag.class);
                    for (CompoundTag piece : pieces.getAll()) {
                        newSkin.getPersonaPieces().add(new PersonaPiece(
                                piece.getString("PieceId"),
                                piece.getString("PieceType"),
                                piece.getString("PackId"),
                                piece.getBoolean("IsDefault"),
                                piece.getString("ProductId")
                        ));
                    }
                }
                if (skinTag.contains("PieceTintColors")) {
                    ListTag<CompoundTag> tintColors = skinTag.getList("PieceTintColors", CompoundTag.class);
                    for (CompoundTag tintColor : tintColors.getAll()) {
                        newSkin.getTintColors().add(new PersonaPieceTint(
                                tintColor.getString("PieceType"),
                                tintColor.getList("Colors", StringTag.class).getAll().stream()
                                        .map(stringTag -> stringTag.data).collect(Collectors.toList())
                        ));
                    }
                }
                if (skinTag.contains("IsTrustedSkin")) {
                    newSkin.setTrusted(skinTag.getBoolean("IsTrustedSkin"));
                }
                this.setSkin(newSkin);
            }

            if (this.getSkin() == null) {
                this.setSkin(new Skin());
            }
            this.setUniqueId(Utils.dataToUUID(String.valueOf(human.getId()).getBytes(StandardCharsets.UTF_8),
                    this.getSkin().getSkinData().data, human.getNameTag().getBytes(StandardCharsets.UTF_8)));
        }

        if (isIntelligentHuman) {
            EntityIntelligentHuman entityIntelligentHuman = (EntityIntelligentHuman) this;
            this.setInventories(new Inventory[]{
                    new FakeHumanInventory(entityIntelligentHuman),
                    new FakeHumanOffhandInventory(entityIntelligentHuman),
                    new FakeHumanEnderChestInventory(entityIntelligentHuman)
            });
            if (human.namedTag.containsNumber("SelectedInventorySlot")) {
                entityIntelligentHuman.getInventory().setHeldItemSlot(NukkitMath.clamp(human.namedTag.getInt("SelectedInventorySlot"), 0, 8));
            }
        } else {
            EntityHumanType entityHumanType = (EntityHumanType) this;
            this.setInventories(new Inventory[]{
                    new PlayerInventory(entityHumanType),
                    new PlayerOffhandInventory(entityHumanType),
                    new PlayerEnderChestInventory(entityHumanType)
            });
            if (human.namedTag.containsNumber("SelectedInventorySlot")) {
                entityHumanType.getInventory().setHeldItemSlot(NukkitMath.clamp(human.namedTag.getInt("SelectedInventorySlot"), 0, 8));
            }
        }

        var inventory = this.getInventory();
        if (human.namedTag.contains("Inventory") && human.namedTag.get("Inventory") instanceof ListTag) {
            ListTag<CompoundTag> inventoryList = human.namedTag.getList("Inventory", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {
                int slot = item.getByte("Slot");
                if (slot >= 0 && slot < 9) { //hotbar
                    //Old hotbar saving stuff, remove it (useless now)
                    inventoryList.remove(item);
                } else if (slot >= 100 && slot < 104) {
                    inventory.setItem(inventory.getSize() + slot - 100, NBTIO.getItemHelper(item));
                } else if (slot == -106) {
                    this.getOffhandInventory().setItem(0, NBTIO.getItemHelper(item));
                } else {
                    inventory.setItem(slot - 9, NBTIO.getItemHelper(item));
                }
            }
        }
        if (human.namedTag.contains("EnderItems") && human.namedTag.get("EnderItems") instanceof ListTag) {
            ListTag<CompoundTag> inventoryList = human.namedTag.getList("EnderItems", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {
                ((EntityHumanType) human).getEnderChestInventory().setItem(item.getByte("Slot"), NBTIO.getItemHelper(item));
            }
        }
    }

    default void saveHumanEntity(Entity human) {
        boolean isIntelligentHuman = this instanceof EntityIntelligentHuman;

        //EntityHumanType
        ListTag<CompoundTag> inventoryTag = null;
        if (this.getInventory() != null) {
            inventoryTag = new ListTag<>("Inventory");
            human.namedTag.putList(inventoryTag);

            for (int slot = 0; slot < 9; ++slot) {
                inventoryTag.add(new CompoundTag()
                        .putByte("Count", 0)
                        .putShort("Damage", 0)
                        .putByte("Slot", slot)
                        .putByte("TrueSlot", -1)
                        .putShort("id", 0)
                );
            }

            int slotCount = Player.SURVIVAL_SLOTS + 9;
            for (int slot = 9; slot < slotCount; ++slot) {
                Item item = this.getInventory().getItem(slot - 9);
                inventoryTag.add(NBTIO.putItemHelper(item, slot));
            }

            for (int slot = 100; slot < 104; ++slot) {
                Item item = this.getInventory().getItem(this.getInventory().getSize() + slot - 100);
                if (item != null && item.getId() != Item.AIR) {
                    inventoryTag.add(NBTIO.putItemHelper(item, slot));
                }
            }
            if (isIntelligentHuman) {
                human.namedTag.putInt("SelectedInventorySlot", ((FakeHumanInventory) this.getInventory()).getHeldItemIndex());
            } else {
                human.namedTag.putInt("SelectedInventorySlot", ((PlayerInventory) this.getInventory()).getHeldItemIndex());
            }
        }

        if (this.getOffhandInventory() != null) {
            Item item = this.getOffhandInventory().getItem(0);
            if (item.getId() != Item.AIR) {
                if (inventoryTag == null) {
                    inventoryTag = new ListTag<>("Inventory");
                    human.namedTag.putList(inventoryTag);
                }
                inventoryTag.add(NBTIO.putItemHelper(item, -106));
            }
        }

        human.namedTag.putList(new ListTag<CompoundTag>("EnderItems"));
        if (this.getEnderChestInventory() != null) {
            for (int slot = 0; slot < 27; ++slot) {
                Item item = this.getEnderChestInventory().getItem(slot);
                if (item != null && item.getId() != Item.AIR) {
                    human.namedTag.getList("EnderItems", CompoundTag.class).add(NBTIO.putItemHelper(item, slot));
                }
            }
        }

        //EntityHuman
        var skin = getSkin();
        if (skin != null) {
            CompoundTag skinTag = new CompoundTag()
                    .putByteArray("Data", skin.getSkinData().data)
                    .putInt("SkinImageWidth", skin.getSkinData().width)
                    .putInt("SkinImageHeight", skin.getSkinData().height)
                    .putString("ModelId", skin.getSkinId())
                    .putString("CapeId", skin.getCapeId())
                    .putByteArray("CapeData", skin.getCapeData().data)
                    .putInt("CapeImageWidth", skin.getCapeData().width)
                    .putInt("CapeImageHeight", skin.getCapeData().height)
                    .putByteArray("SkinResourcePatch", skin.getSkinResourcePatch().getBytes(StandardCharsets.UTF_8))
                    .putByteArray("GeometryData", skin.getGeometryData().getBytes(StandardCharsets.UTF_8))
                    .putByteArray("SkinAnimationData", skin.getAnimationData().getBytes(StandardCharsets.UTF_8))
                    .putBoolean("PremiumSkin", skin.isPremium())
                    .putBoolean("PersonaSkin", skin.isPersona())
                    .putBoolean("CapeOnClassicSkin", skin.isCapeOnClassic())
                    .putString("ArmSize", skin.getArmSize())
                    .putString("SkinColor", skin.getSkinColor())
                    .putBoolean("IsTrustedSkin", skin.isTrusted());

            List<SkinAnimation> animations = skin.getAnimations();
            if (!animations.isEmpty()) {
                ListTag<CompoundTag> animationsTag = new ListTag<>("AnimatedImageData");
                for (SkinAnimation animation : animations) {
                    animationsTag.add(new CompoundTag()
                            .putFloat("Frames", animation.frames)
                            .putInt("Type", animation.type)
                            .putInt("ImageWidth", animation.image.width)
                            .putInt("ImageHeight", animation.image.height)
                            .putInt("AnimationExpression", animation.expression)
                            .putByteArray("Image", animation.image.data));
                }
                skinTag.putList(animationsTag);
            }

            List<PersonaPiece> personaPieces = skin.getPersonaPieces();
            if (!personaPieces.isEmpty()) {
                ListTag<CompoundTag> piecesTag = new ListTag<>("PersonaPieces");
                for (PersonaPiece piece : personaPieces) {
                    piecesTag.add(new CompoundTag().putString("PieceId", piece.id)
                            .putString("PieceType", piece.type)
                            .putString("PackId", piece.packId)
                            .putBoolean("IsDefault", piece.isDefault)
                            .putString("ProductId", piece.productId));
                }
            }

            List<PersonaPieceTint> tints = skin.getTintColors();
            if (!tints.isEmpty()) {
                ListTag<CompoundTag> tintsTag = new ListTag<>("PieceTintColors");
                for (PersonaPieceTint tint : tints) {
                    ListTag<StringTag> colors = new ListTag<>("Colors");
                    colors.setAll(tint.colors.stream().map(s -> new StringTag("", s)).collect(Collectors.toList()));
                    tintsTag.add(new CompoundTag()
                            .putString("PieceType", tint.pieceType)
                            .putList(colors));
                }
            }

            if (!skin.getPlayFabId().isEmpty()) {
                skinTag.putString("PlayFabId", skin.getPlayFabId());
            }

            human.namedTag.putCompound("Skin", skinTag);
        }
    }

    void setSkin(Skin skin);

    Skin getSkin();

    UUID getUniqueId();

    void setUniqueId(UUID uuid);

    void setInventories(Inventory[] inventory);

    Inventory getInventory();

    Inventory getOffhandInventory();

    Inventory getEnderChestInventory();
}
