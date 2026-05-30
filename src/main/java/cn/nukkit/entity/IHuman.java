package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.inventory.HumanEnderChestInventory;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.inventory.HumanOffHandInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.ItemHelper;
import cn.nukkit.utils.Utils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.data.skin.AnimatedTextureType;
import org.cloudburstmc.protocol.bedrock.data.skin.AnimationData;
import org.cloudburstmc.protocol.bedrock.data.skin.AnimationExpressionType;
import org.cloudburstmc.protocol.bedrock.data.skin.ImageData;
import org.cloudburstmc.protocol.bedrock.data.skin.PersonaPieceData;
import org.cloudburstmc.protocol.bedrock.data.skin.PersonaPieceTintData;
import org.cloudburstmc.protocol.bedrock.data.skin.SerializedSkin;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public interface IHuman extends InventoryHolder {
    int NETWORK_ID = 257;

    default void initHumanEntity(Entity human) {
        human.setDataFlag(ActorFlags.HAS_GRAVITY);
        human.setDataProperty(ActorDataTypes.BED_POSITION, new BlockVector3(0, 0, 0).toNetwork(), false);

        if (!(human instanceof Player)) {
            final CompoundTag nbtMap = human.getNbt();
            if (human.nbt.contains("NameTag")) {
                human.setNameTag(nbtMap.getString("NameTag"));
            }

            if (human.nbt.containsCompound("Skin")) {
                final SerializedSkin.Builder builder = SerializedSkin.builder();

                final CompoundTag skinTag = nbtMap.getCompound("Skin");
                if (!skinTag.contains("Transparent")) {
                    skinTag.putBoolean("Transparent", false);
                }
                if (skinTag.contains("ModelId")) {
                    builder.skinId(skinTag.getString("ModelId"));
                }
                if (skinTag.contains("PlayFabId")) {
                    builder.playFabId(skinTag.getString("PlayFabId"));
                }
                if (skinTag.contains("Data")) {
                    byte[] data = skinTag.getByteArray("Data");
                    if (skinTag.contains("SkinImageWidth") && skinTag.contains("SkinImageHeight")) {
                        int width = skinTag.getInt("SkinImageWidth");
                        int height = skinTag.getInt("SkinImageHeight");
                        builder.skinData(ImageData.of(width, height, data));
                    } else {
                        builder.skinData(ImageData.of(data));
                    }
                }
                if (skinTag.contains("CapeId")) {
                    builder.capeId(skinTag.getString("CapeId"));
                }
                if (skinTag.contains("CapeData")) {
                    byte[] data = skinTag.getByteArray("CapeData");
                    if (skinTag.contains("CapeImageWidth") && skinTag.contains("CapeImageHeight")) {
                        int width = skinTag.getInt("CapeImageWidth");
                        int height = skinTag.getInt("CapeImageHeight");
                        builder.capeData(ImageData.of(width, height, data));
                    } else {
                        builder.capeData(ImageData.of(data));
                    }
                }
                if (skinTag.contains("GeometryName")) {
                    builder.geometryName(skinTag.getString("GeometryName"));
                }
                if (skinTag.contains("SkinResourcePatch")) {
                    builder.skinResourcePatch(new String(skinTag.getByteArray("SkinResourcePatch"), StandardCharsets.UTF_8));
                }
                if (skinTag.contains("GeometryData")) {
                    builder.geometryData(new String(skinTag.getByteArray("GeometryData"), StandardCharsets.UTF_8));
                }
                if (skinTag.contains("SkinAnimationData")) {
                    builder.animationData(new String(skinTag.getByteArray("SkinAnimationData"), StandardCharsets.UTF_8));
                } else if (skinTag.contains("AnimationData")) { // backwards compatible
                    builder.animationData(new String(skinTag.getByteArray("AnimationData"), StandardCharsets.UTF_8));
                }
                if (skinTag.contains("PremiumSkin")) {
                    builder.premium(skinTag.getBoolean("PremiumSkin"));
                }
                if (skinTag.contains("PersonaSkin")) {
                    builder.persona(skinTag.getBoolean("PersonaSkin"));
                }
                if (skinTag.contains("CapeOnClassicSkin")) {
                    builder.capeOnClassic(skinTag.getBoolean("CapeOnClassicSkin"));
                }
                if (skinTag.contains("AnimatedImageData")) {
                    final List<AnimationData> animations = new ObjectArrayList<>();
                    ListTag<CompoundTag> list = skinTag.getList("AnimatedImageData", CompoundTag.class);
                    for (CompoundTag animationTag : list.getAll()) {
                        float frames = animationTag.getFloat("Frames");
                        int type = animationTag.getInt("Type");
                        byte[] image = animationTag.getByteArray("Image");
                        int width = animationTag.getInt("ImageWidth");
                        int height = animationTag.getInt("ImageHeight");
                        int expression = animationTag.getInt("AnimationExpression");
                        animations.add(
                                new AnimationData(
                                        ImageData.of(width, height, image),
                                        AnimatedTextureType.from(type),
                                        frames,
                                        AnimationExpressionType.from(expression)
                                )
                        );
                    }
                    builder.animations(animations);
                }
                if (skinTag.contains("ArmSize")) {
                    builder.armSize(skinTag.getString("ArmSize"));
                }
                if (skinTag.contains("SkinColor")) {
                    builder.skinColor(skinTag.getString("SkinColor"));
                }
                if (skinTag.contains("PersonaPieces")) {
                    ListTag<CompoundTag> pieces = skinTag.getList("PersonaPieces", CompoundTag.class);
                    final List<PersonaPieceData> personaPieces = new ObjectArrayList<>();
                    for (CompoundTag piece : pieces.getAll()) {
                        personaPieces.add(
                                new PersonaPieceData(
                                        piece.getString("PieceId"),
                                        piece.getString("PieceType"),
                                        piece.getString("PackId"),
                                        piece.getBoolean("IsDefault"),
                                        piece.getString("ProductId")
                                )
                        );
                    }
                    builder.personaPieces(personaPieces);
                }
                if (skinTag.contains("PieceTintColors")) {
                    final List<PersonaPieceTintData> pieceTintColors = new ObjectArrayList<>();
                    ListTag<CompoundTag> tintColors = skinTag.getList("PieceTintColors", CompoundTag.class);
                    for (CompoundTag tintColor : tintColors.getAll()) {
                        pieceTintColors.add(
                                new PersonaPieceTintData(
                                        tintColor.getString("PieceType"),
                                        tintColor.getList("Colors", StringTag.class).getAll().stream().map(StringTag::parseValue).toList()
                                )
                        );
                    }
                    builder.tintColors(pieceTintColors);
                }
                this.setSkin(builder.build());
            }

            if (this.getSkin() == null) {
                this.setSkin(SerializedSkin.builder().build());
            }
            this.setUniqueId(Utils.dataToUUID(String.valueOf(human.getId()).getBytes(StandardCharsets.UTF_8),
                    this.getSkin().getSkinData().getImage(), human.getNameTag().getBytes(StandardCharsets.UTF_8)));
        }

        this.setInventories(new Inventory[]{
                new HumanInventory(this),
                new HumanOffHandInventory(this),
                new HumanEnderChestInventory(this)
        });

        final CompoundTag nbtMap = human.getNbt();

        if (human.nbt.contains("SelectedInventorySlot")) {
            this.getInventory().setHeldItemSlot(NukkitMath.clamp(nbtMap.getInt("SelectedInventorySlot"), 0, 8));
        }

        if (nbtMap.containsList("Inventory")) {
            var inventory = this.getInventory();
            ListTag<CompoundTag> inventoryList = nbtMap.getList("Inventory", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {
                int slot = item.getByte("Slot");
                inventory.setItem(slot, ItemHelper.read(item));//inventory 0-39
            }
        }
        if (nbtMap.contains("OffInventory")) {
            HumanOffHandInventory offhandInventory = getOffhandInventory();
            CompoundTag offHand = nbtMap.getCompound("OffInventory");
            offhandInventory.setItem(0, ItemHelper.read(offHand));//offinventory index 0
        }
        if (nbtMap.containsList("EnderItems")) {
            ListTag<CompoundTag> inventoryList = nbtMap.getList("EnderItems", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {//enderItems index 0-26
                ((EntityHumanType) human).getEnderChestInventory().setItem(item.getByte("Slot"), ItemHelper.read(item));
            }
        }
    }

    default void saveHumanEntity(Entity human) {
        //EntityHumanType
        final ListTag<CompoundTag> inventoryTag = new ListTag<>(Tag.TAG_Compound);
        human.nbt.putList("Inventory", inventoryTag);
        if (this.getInventory() != null) {
            for (var entry : getInventory().getContents().entrySet()) {
                inventoryTag.add(ItemHelper.write(entry.getValue(), entry.getKey()));
            }
            human.nbt.putInt("SelectedInventorySlot", this.getInventory().getHeldItemIndex());
        }

        if (this.getOffhandInventory() != null) {
            Item item = this.getOffhandInventory().getItem(0);
            human.nbt.putCompound("OffInventory", ItemHelper.write(item, 0));
        }

        human.nbt.putList("EnderItems", new ListTag<>(Tag.TAG_Compound));
        if (this.getEnderChestInventory() != null) {
            ListTag<CompoundTag> enderItems = human.getNbt().getList("EnderItems", CompoundTag.class);
            for (int slot = 0; slot < this.getEnderChestInventory().getSize(); ++slot) {
                Item item = this.getEnderChestInventory().getItem(slot);
                if (!item.isNull()) {
                    enderItems.add(ItemHelper.write(item, slot));
                }
            }
        }

        //EntityHuman
        var skin = getSkin();
        if (skin != null) {
            CompoundTag skinTag = new CompoundTag()
                    .putByteArray("Data", skin.getSkinData().getImage())
                    .putInt("SkinImageWidth", skin.getSkinData().getWidth())
                    .putInt("SkinImageHeight", skin.getSkinData().getHeight())
                    .putString("ModelId", skin.getSkinId())
                    .putString("CapeId", skin.getCapeId())
                    .putByteArray("CapeData", skin.getCapeData().getImage())
                    .putInt("CapeImageWidth", skin.getCapeData().getWidth())
                    .putInt("CapeImageHeight", skin.getCapeData().getHeight())
                    .putByteArray("SkinResourcePatch", skin.getSkinResourcePatch().getBytes(StandardCharsets.UTF_8))
                    .putByteArray("GeometryData", skin.getGeometryData().getBytes(StandardCharsets.UTF_8))
                    .putByteArray("SkinAnimationData", skin.getAnimationData().getBytes(StandardCharsets.UTF_8))
                    .putBoolean("PremiumSkin", skin.isPremium())
                    .putBoolean("PersonaSkin", skin.isPersona())
                    .putBoolean("CapeOnClassicSkin", skin.isCapeOnClassic())
                    .putString("ArmSize", skin.getArmSize())
                    .putString("SkinColor", skin.getSkinColor());

            List<AnimationData> animations = skin.getAnimations();
            if (!animations.isEmpty()) {
                ListTag<CompoundTag> animationsTag = new ListTag<>();
                for (AnimationData animation : animations) {
                    animationsTag.add(new CompoundTag()
                            .putFloat("Frames", animation.getFrames())
                            .putInt("Type", animation.getTextureType().ordinal())
                            .putInt("ImageWidth", animation.getImage().getWidth())
                            .putInt("ImageHeight", animation.getImage().getHeight())
                            .putInt("AnimationExpression", animation.getExpressionType().ordinal())
                            .putByteArray("Image", animation.getImage().getImage()));
                }
                skinTag.putList("AnimatedImageData", animationsTag);
            }

            List<PersonaPieceData> personaPieces = skin.getPersonaPieces();
            if (!personaPieces.isEmpty()) {
                ListTag<CompoundTag> piecesTag = new ListTag<>();
                for (PersonaPieceData piece : personaPieces) {
                    piecesTag.add(new CompoundTag().putString("PieceId", piece.getId())
                            .putString("PieceType", piece.getType())
                            .putString("PackId", piece.getPackId())
                            .putBoolean("IsDefault", piece.isDefault())
                            .putString("ProductId", piece.getProductId()));
                }
                skinTag.putList("PersonaPieces", piecesTag);
            }
            List<PersonaPieceTintData> tints = skin.getTintColors();
            if (!tints.isEmpty()) {
                ListTag<CompoundTag> tintsTag = new ListTag<>();
                for (PersonaPieceTintData tint : tints) {
                    ListTag<StringTag> colors = new ListTag<>();
                    colors.setAll(tint.getColors().stream().map(StringTag::new).collect(Collectors.toList()));
                    tintsTag.add(new CompoundTag()
                            .putString("PieceType", tint.getType())
                            .putList("Colors", colors));
                }
                skinTag.putList("PieceTintColors", tintsTag);
            }

            if (!skin.getPlayFabId().isEmpty()) {
                skinTag.putString("PlayFabId", skin.getPlayFabId());
            }

            human.getNbt().putCompound("Skin", skinTag);
        }
    }

    void setSkin(SerializedSkin skin);

    SerializedSkin getSkin();

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
