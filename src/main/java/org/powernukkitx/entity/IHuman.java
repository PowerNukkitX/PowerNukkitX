package org.powernukkitx.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.data.skin.AnimatedTextureType;
import org.cloudburstmc.protocol.bedrock.data.skin.AnimationData;
import org.cloudburstmc.protocol.bedrock.data.skin.AnimationExpressionType;
import org.cloudburstmc.protocol.bedrock.data.skin.ImageData;
import org.cloudburstmc.protocol.bedrock.data.skin.PersonaPieceData;
import org.cloudburstmc.protocol.bedrock.data.skin.PersonaPieceTintData;
import org.powernukkitx.Player;
import org.powernukkitx.entity.data.human.Skin;
import org.powernukkitx.inventory.HumanEnderChestInventory;
import org.powernukkitx.inventory.HumanInventory;
import org.powernukkitx.inventory.HumanOffHandInventory;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.inventory.InventoryHolder;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.NukkitMath;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.StringTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.utils.ItemHelper;
import org.powernukkitx.utils.Utils;

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
                final org.cloudburstmc.protocol.bedrock.data.skin.Skin.Builder builder = org.cloudburstmc.protocol.bedrock.data.skin.Skin.builder();

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
                boolean trusted = skinTag.getBoolean("IsTrustedSkin");
                this.setSkin(new Skin(builder.build(), trusted));
            }

            if (this.getSkin() == null) {
                this.setSkin(new Skin(org.cloudburstmc.protocol.bedrock.data.skin.Skin.builder().skinData(ImageData.EMPTY).build()));
            }
            this.setUniqueId(Utils.dataToUUID(String.valueOf(human.runtimeId()).getBytes(StandardCharsets.UTF_8),
                this.getSkin().getSkin().getSkinData().getImage(), human.getNameTag().getBytes(StandardCharsets.UTF_8)));
        }

        this.setInventories(new Inventory[]{
            new HumanInventory(this),
            new HumanOffHandInventory(this),
            new HumanEnderChestInventory(this)
        });

        final CompoundTag nbtMap = human.getNbt();

        if (human.nbt.contains("SelectedInventorySlot")) {
            this.getInventory().setHeldItemIndex(
                NukkitMath.clamp(nbtMap.getInt("SelectedInventorySlot"), 0, 8),
                false
            );
        }

        if (human instanceof Player) {
            HumanInventory inventory = this.getInventory();

            if (nbtMap.containsList("Inventory")) {
                ListTag<CompoundTag> inventoryList = nbtMap.getList("Inventory", CompoundTag.class);
                for (CompoundTag item : inventoryList.getAll()) {
                    int slot = item.getByte("Slot") & 0xff;
                    if (slot < 36) {
                        inventory.setItem(slot, ItemHelper.read(item));
                    }
                }
            }

            if (nbtMap.containsList("Armor")) {
                ListTag<CompoundTag> armorList = nbtMap.getList("Armor", CompoundTag.class);
                for (int slot = 0; slot < Math.min(4, armorList.size()); slot++) {
                    inventory.setArmorItem(slot, ItemHelper.read(armorList.get(slot)));
                }
            }

            if (nbtMap.containsList("Offhand")) {
                ListTag<CompoundTag> offhandList = nbtMap.getList("Offhand", CompoundTag.class);
                if (offhandList.size() > 0) {
                    this.getOffhandInventory().setItem(0, ItemHelper.read(offhandList.get(0)));
                }
            }

            if (nbtMap.containsList("EnderChestInventory")) {
                ListTag<CompoundTag> enderList = nbtMap.getList("EnderChestInventory", CompoundTag.class);
                for (CompoundTag item : enderList.getAll()) {
                    int slot = item.getByte("Slot") & 0xff;
                    if (slot < this.getEnderChestInventory().getSize()) {
                        this.getEnderChestInventory().setItem(slot, ItemHelper.read(item));
                    }
                }
            }

            if (nbtMap.containsList("Mainhand") && inventory.getItemInMainHand().isNull()) {
                ListTag<CompoundTag> mainhandList = nbtMap.getList("Mainhand", CompoundTag.class);
                if (mainhandList.size() > 0) {
                    inventory.setItemInMainHand(ItemHelper.read(mainhandList.get(0)), false);
                }
            }
        } else {
            if (nbtMap.containsList("Inventory")) {
                var inventory = this.getInventory();
                ListTag<CompoundTag> inventoryList = nbtMap.getList("Inventory", CompoundTag.class);
                for (CompoundTag item : inventoryList.getAll()) {
                    int slot = item.getByte("Slot");
                    inventory.setItem(slot, ItemHelper.read(item));
                }
            }

            if (nbtMap.contains("OffInventory")) {
                HumanOffHandInventory offhandInventory = getOffhandInventory();
                CompoundTag offHand = nbtMap.getCompound("OffInventory");
                offhandInventory.setItem(0, ItemHelper.read(offHand));
            }

            if (nbtMap.containsList("EnderItems")) {
                ListTag<CompoundTag> inventoryList = nbtMap.getList("EnderItems", CompoundTag.class);
                for (CompoundTag item : inventoryList.getAll()) {
                    ((EntityHumanType) human).getEnderChestInventory().setItem(item.getByte("Slot"), ItemHelper.read(item));
                }
            }
        }
    }

    default void saveHumanEntity(Entity human) {
        //EntityHumanType
        if (human instanceof Player) {
            HumanInventory inventory = this.getInventory();

            ListTag<CompoundTag> previousInventory = human.nbt.getList("Inventory", CompoundTag.class);
            ListTag<CompoundTag> inventoryTag = new ListTag<>(Tag.TAG_Compound);

            for (int slot = 0; slot < 36; slot++) {
                Item item = inventory.getItem(slot);
                CompoundTag previous = previousInventory.size() > slot ? previousInventory.get(slot) : null;
                inventoryTag.add(ItemHelper.write(item, slot, previous));
            }

            human.nbt.putList("Inventory", inventoryTag);

            ListTag<CompoundTag> previousArmor = human.nbt.getList("Armor", CompoundTag.class);
            ListTag<CompoundTag> armorTag = new ListTag<>(Tag.TAG_Compound);

            for (int slot = 0; slot < 4; slot++) {
                Item item = inventory.getArmorItem(slot);
                CompoundTag previous = previousArmor.size() > slot ? previousArmor.get(slot) : null;
                armorTag.add(ItemHelper.write(item, previous));
            }

            for (int slot = 4; slot < previousArmor.size(); slot++) {
                armorTag.add((CompoundTag) previousArmor.get(slot).copy());
            }

            human.nbt.putList("Armor", armorTag);

            ListTag<CompoundTag> previousMainhand = human.nbt.getList("Mainhand", CompoundTag.class);
            CompoundTag previousMain = previousMainhand.size() > 0 ? previousMainhand.get(0) : null;
            human.nbt.putList("Mainhand", new ListTag<CompoundTag>(Tag.TAG_Compound)
                    .add(ItemHelper.write(inventory.getItemInMainHand(), previousMain)));

            ListTag<CompoundTag> previousOffhand = human.nbt.getList("Offhand", CompoundTag.class);
            CompoundTag previousOff = previousOffhand.size() > 0 ? previousOffhand.get(0) : null;
            human.nbt.putList("Offhand", new ListTag<CompoundTag>(Tag.TAG_Compound)
                    .add(ItemHelper.write(this.getOffhandInventory().getItem(0), previousOff)));

            ListTag<CompoundTag> previousEnder = human.nbt.getList("EnderChestInventory", CompoundTag.class);
            ListTag<CompoundTag> enderTag = new ListTag<>(Tag.TAG_Compound);

            for (int slot = 0; slot < this.getEnderChestInventory().getSize(); slot++) {
                Item item = this.getEnderChestInventory().getItem(slot);
                CompoundTag previous = previousEnder.size() > slot ? previousEnder.get(slot) : null;
                enderTag.add(ItemHelper.write(item, slot, previous));
            }

            human.nbt.putList("EnderChestInventory", enderTag);
            human.nbt.putInt("SelectedInventorySlot", inventory.getHeldItemIndex());
            human.nbt.remove("OffInventory", "EnderItems");
        } else {
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
        }

        //EntityHuman
        var skin = getSkin();
        var serializedSkin = skin.getSkin();
        if (serializedSkin != null) {
            CompoundTag skinTag = new CompoundTag()
                .putByteArray("Data", serializedSkin.getSkinData().getImage())
                .putInt("SkinImageWidth", serializedSkin.getSkinData().getWidth())
                .putInt("SkinImageHeight", serializedSkin.getSkinData().getHeight())
                .putString("ModelId", serializedSkin.getSkinId())
                .putString("CapeId", serializedSkin.getCapeId())
                .putByteArray("CapeData", serializedSkin.getCapeData().getImage())
                .putInt("CapeImageWidth", serializedSkin.getCapeData().getWidth())
                .putInt("CapeImageHeight", serializedSkin.getCapeData().getHeight())
                .putByteArray("SkinResourcePatch", serializedSkin.getSkinResourcePatch().getBytes(StandardCharsets.UTF_8))
                .putByteArray("GeometryData", serializedSkin.getGeometryData().getBytes(StandardCharsets.UTF_8))
                .putByteArray("SkinAnimationData", serializedSkin.getAnimationData().getBytes(StandardCharsets.UTF_8))
                .putBoolean("PremiumSkin", serializedSkin.isPremium())
                .putBoolean("PersonaSkin", serializedSkin.isPersona())
                .putBoolean("CapeOnClassicSkin", serializedSkin.isCapeOnClassic())
                .putString("ArmSize", serializedSkin.getArmSize())
                .putString("SkinColor", serializedSkin.getSkinColor())
                .putBoolean("IsTrustedSkin", skin.isTrusted());

            List<AnimationData> animations = serializedSkin.getAnimations();
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

            List<PersonaPieceData> personaPieces = serializedSkin.getPersonaPieces();
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
            List<PersonaPieceTintData> tints = serializedSkin.getTintColors();
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

            if (!serializedSkin.getPlayFabId().isEmpty()) {
                skinTag.putString("PlayFabId", serializedSkin.getPlayFabId());
            }

            human.getNbt().putCompound("Skin", skinTag);
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
