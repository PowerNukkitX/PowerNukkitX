package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.data.PlayerFlag;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.inventory.HumanOffHandInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.inventory.HumanEnderChestInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.utils.PersonaPiece;
import cn.nukkit.utils.PersonaPieceTint;
import cn.nukkit.utils.SerializedImage;
import cn.nukkit.utils.SkinAnimation;
import cn.nukkit.utils.Utils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public interface IHuman extends InventoryHolder {
    int NETWORK_ID = 257;

    default void initHumanEntity(Entity human) {
        human.setPlayerFlag(PlayerFlag.SLEEP);
        human.setDataFlag(EntityFlag.HAS_GRAVITY);
        human.setDataProperty(EntityDataTypes.BED_POSITION, new BlockVector3(0, 0, 0), false);

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

        this.setInventories(new Inventory[]{
                new HumanInventory(this),
                new HumanOffHandInventory(this),
                new HumanEnderChestInventory(this)
        });

        if (human.namedTag.containsNumber("SelectedInventorySlot")) {
            this.getInventory().setHeldItemSlot(NukkitMath.clamp(human.namedTag.getInt("SelectedInventorySlot"), 0, 8));
        }

        if (human.namedTag.contains("Inventory") && human.namedTag.get("Inventory") instanceof ListTag) {
            var inventory = this.getInventory();
            ListTag<CompoundTag> inventoryList = human.namedTag.getList("Inventory", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {
                int slot = item.getByte("Slot");
                inventory.setItem(slot, NBTIO.getItemHelper(item));//inventory 0-39
            }
        }
        if (human.namedTag.containsCompound("OffInventory")) {
            HumanOffHandInventory offhandInventory = getOffhandInventory();
            CompoundTag offHand = human.namedTag.getCompound("OffInventory");
            offhandInventory.setItem(0, NBTIO.getItemHelper(offHand));//offinventory index 0
        }
        if (human.namedTag.contains("EnderItems") && human.namedTag.get("EnderItems") instanceof ListTag) {
            ListTag<CompoundTag> inventoryList = human.namedTag.getList("EnderItems", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {//enderItems index 0-26
                ((EntityHumanType) human).getEnderChestInventory().setItem(item.getByte("Slot"), NBTIO.getItemHelper(item));
            }
        }
    }

    default void saveHumanEntity(Entity human) {
        //EntityHumanType
        ListTag<CompoundTag> inventoryTag = null;
        if (this.getInventory() != null) {
            inventoryTag = new ListTag<>();
            human.namedTag.putList("Inventory", inventoryTag);

            for (var entry : getInventory().getContents().entrySet()) {
                inventoryTag.add(NBTIO.putItemHelper(entry.getValue(), entry.getKey()));
            }

            human.namedTag.putInt("SelectedInventorySlot", this.getInventory().getHeldItemIndex());
        }

        if (this.getOffhandInventory() != null) {
            Item item = this.getOffhandInventory().getItem(0);
            if (!item.isNull()) {
                human.namedTag.putCompound("OffInventory", NBTIO.putItemHelper(item, 0));
            }
        }

        human.namedTag.putList("EnderItems", new ListTag<CompoundTag>());
        if (this.getEnderChestInventory() != null) {
            ListTag<CompoundTag> enderItems = human.namedTag.getList("EnderItems", CompoundTag.class);
            for (int slot = 0; slot < this.getEnderChestInventory().getSize(); ++slot) {
                Item item = this.getEnderChestInventory().getItem(slot);
                if (!item.isNull()) {
                    enderItems.add(NBTIO.putItemHelper(item, slot));
                }
            }
            human.namedTag.putList("EnderItems", enderItems);
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
                ListTag<CompoundTag> animationsTag = new ListTag<>();
                for (SkinAnimation animation : animations) {
                    animationsTag.add(new CompoundTag()
                            .putFloat("Frames", animation.frames)
                            .putInt("Type", animation.type)
                            .putInt("ImageWidth", animation.image.width)
                            .putInt("ImageHeight", animation.image.height)
                            .putInt("AnimationExpression", animation.expression)
                            .putByteArray("Image", animation.image.data));
                }
                skinTag.putList("AnimatedImageData", animationsTag);
            }

            List<PersonaPiece> personaPieces = skin.getPersonaPieces();
            if (!personaPieces.isEmpty()) {
                ListTag<CompoundTag> piecesTag = new ListTag<>();
                for (PersonaPiece piece : personaPieces) {
                    piecesTag.add(new CompoundTag().putString("PieceId", piece.id)
                            .putString("PieceType", piece.type)
                            .putString("PackId", piece.packId)
                            .putBoolean("IsDefault", piece.isDefault)
                            .putString("ProductId", piece.productId));
                }
                skinTag.putList("PersonaPieces", piecesTag);
            }
            List<PersonaPieceTint> tints = skin.getTintColors();
            if (!tints.isEmpty()) {
                ListTag<CompoundTag> tintsTag = new ListTag<>();
                for (PersonaPieceTint tint : tints) {
                    ListTag<StringTag> colors = new ListTag<>();
                    colors.setAll(tint.colors.stream().map(StringTag::new).collect(Collectors.toList()));
                    tintsTag.add(new CompoundTag()
                            .putString("PieceType", tint.pieceType)
                            .putList("Colors", colors));
                }
                skinTag.putList("PieceTintColors", tintsTag);
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

    HumanInventory getInventory();

    HumanOffHandInventory getOffhandInventory();

    HumanEnderChestInventory getEnderChestInventory();

    Level getLevel();

    default Entity getEntity() {
        return (Entity) this;
    }
}
