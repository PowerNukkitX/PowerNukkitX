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
import cn.nukkit.utils.ItemHelper;
import cn.nukkit.utils.Utils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
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

public interface IHuman extends InventoryHolder {
    int NETWORK_ID = 257;

    default void initHumanEntity(Entity human) {
        human.setDataFlag(ActorFlags.HAS_GRAVITY);
        human.setDataProperty(ActorDataTypes.BED_POSITION, new BlockVector3(0, 0, 0).toNetwork(), false);

        if (!(human instanceof Player)) {
            if (human.namedTag.containsKey("NameTag")) {
                human.setNameTag(human.namedTag.getString("NameTag"));
            }

            if (human.namedTag.containsKey("Skin") && human.namedTag.get("Skin") instanceof NbtMap) {
                final SerializedSkin.Builder builder = SerializedSkin.builder();

                final NbtMap skinTag = human.namedTag.getCompound("Skin");
                NbtMapBuilder tagBuilder = skinTag.toBuilder();
                if (!tagBuilder.containsKey("Transparent")) {
                    tagBuilder.putBoolean("Transparent", false);
                }
                if (tagBuilder.containsKey("ModelId")) {
                    builder.skinId(skinTag.getString("ModelId"));
                }
                if (tagBuilder.containsKey("PlayFabId")) {
                    builder.playFabId(skinTag.getString("PlayFabId"));
                }
                if (tagBuilder.containsKey("Data")) {
                    byte[] data = skinTag.getByteArray("Data");
                    if (tagBuilder.containsKey("SkinImageWidth") && tagBuilder.containsKey("SkinImageHeight")) {
                        int width = skinTag.getInt("SkinImageWidth");
                        int height = skinTag.getInt("SkinImageHeight");
                        builder.skinData(ImageData.of(width, height, data));
                    } else {
                        builder.skinData(ImageData.of(data));
                    }
                }
                if (tagBuilder.containsKey("CapeId")) {
                    builder.capeId(skinTag.getString("CapeId"));
                }
                if (tagBuilder.containsKey("CapeData")) {
                    byte[] data = skinTag.getByteArray("CapeData");
                    if (tagBuilder.containsKey("CapeImageWidth") && tagBuilder.containsKey("CapeImageHeight")) {
                        int width = skinTag.getInt("CapeImageWidth");
                        int height = skinTag.getInt("CapeImageHeight");
                        builder.capeData(ImageData.of(width, height, data));
                    } else {
                        builder.capeData(ImageData.of(data));
                    }
                }
                if (tagBuilder.containsKey("GeometryName")) {
                    builder.geometryName(skinTag.getString("GeometryName"));
                }
                if (tagBuilder.containsKey("SkinResourcePatch")) {
                    builder.skinResourcePatch(new String(skinTag.getByteArray("SkinResourcePatch"), StandardCharsets.UTF_8));
                }
                if (tagBuilder.containsKey("GeometryData")) {
                    builder.geometryData(new String(skinTag.getByteArray("GeometryData"), StandardCharsets.UTF_8));
                }
                if (tagBuilder.containsKey("SkinAnimationData")) {
                    builder.animationData(new String(skinTag.getByteArray("SkinAnimationData"), StandardCharsets.UTF_8));
                } else if (tagBuilder.containsKey("AnimationData")) { // backwards compatible
                    builder.animationData(new String(skinTag.getByteArray("AnimationData"), StandardCharsets.UTF_8));
                }
                if (tagBuilder.containsKey("PremiumSkin")) {
                    builder.premium(skinTag.getBoolean("PremiumSkin"));
                }
                if (tagBuilder.containsKey("PersonaSkin")) {
                    builder.persona(skinTag.getBoolean("PersonaSkin"));
                }
                if (tagBuilder.containsKey("CapeOnClassicSkin")) {
                    builder.capeOnClassic(skinTag.getBoolean("CapeOnClassicSkin"));
                }
                if (tagBuilder.containsKey("AnimatedImageData")) {
                    final List<AnimationData> animations = new ObjectArrayList<>();
                    List<NbtMap> list = skinTag.getList("AnimatedImageData", NbtType.COMPOUND);
                    for (NbtMap animationTag : list) {
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
                if (tagBuilder.containsKey("ArmSize")) {
                    builder.armSize(skinTag.getString("ArmSize"));
                }
                if (tagBuilder.containsKey("SkinColor")) {
                    builder.skinColor(skinTag.getString("SkinColor"));
                }
                if (tagBuilder.containsKey("PersonaPieces")) {
                    List<NbtMap> pieces = skinTag.getList("PersonaPieces", NbtType.COMPOUND);
                    final List<PersonaPieceData> personaPieces = new ObjectArrayList<>();
                    for (NbtMap piece : pieces) {
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
                if (tagBuilder.containsKey("PieceTintColors")) {
                    final List<PersonaPieceTintData> pieceTintColors = new ObjectArrayList<>();
                    List<NbtMap> tintColors = skinTag.getList("PieceTintColors", NbtType.COMPOUND);
                    for (NbtMap tintColor : tintColors) {
                        pieceTintColors.add(
                                new PersonaPieceTintData(
                                        tintColor.getString("PieceType"),
                                        tintColor.getList("Colors", NbtType.STRING)
                                )
                        );
                    }
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

        if (human.namedTag.containsKey("SelectedInventorySlot")) {
            this.getInventory().setHeldItemSlot(NukkitMath.clamp(human.namedTag.getInt("SelectedInventorySlot"), 0, 8));
        }

        if (human.namedTag.containsKey("Inventory") && human.namedTag.get("Inventory") instanceof List<?>) {
            var inventory = this.getInventory();
            List<NbtMap> inventoryList = human.namedTag.getList("Inventory", NbtType.COMPOUND);
            for (NbtMap item : inventoryList) {
                int slot = item.getByte("Slot");
                inventory.setItem(slot, ItemHelper.read(item));//inventory 0-39
            }
        }
        if (human.namedTag.containsKey("OffInventory")) {
            HumanOffHandInventory offhandInventory = getOffhandInventory();
            NbtMap offHand = human.namedTag.getCompound("OffInventory");
            offhandInventory.setItem(0, ItemHelper.read(offHand));//offinventory index 0
        }
        if (human.namedTag.containsKey("EnderItems") && human.namedTag.get("EnderItems") instanceof List<?>) {
            List<NbtMap> inventoryList = human.namedTag.getList("EnderItems", NbtType.COMPOUND);
            for (NbtMap item : inventoryList) {//enderItems index 0-26
                ((EntityHumanType) human).getEnderChestInventory().setItem(item.getByte("Slot"), ItemHelper.read(item));
            }
        }
    }

    default void saveHumanEntity(Entity human) {
        //EntityHumanType
        List<NbtMap> inventoryTag = null;
        final NbtMapBuilder builder = human.namedTag.toBuilder();

        if (this.getInventory() != null) {
            inventoryTag = new ObjectArrayList<>();
            builder.putList("Inventory", NbtType.COMPOUND, inventoryTag).build();

            for (var entry : getInventory().getContents().entrySet()) {
                inventoryTag.add(ItemHelper.write(entry.getValue(), entry.getKey()));
            }

            builder.putInt("SelectedInventorySlot", this.getInventory().getHeldItemIndex());
        }

        if (this.getOffhandInventory() != null) {
            Item item = this.getOffhandInventory().getItem(0);
            builder.putCompound("OffInventory", ItemHelper.write(item, 0));
        }

        builder.putList("EnderItems", NbtType.COMPOUND, new ObjectArrayList<>());
        if (this.getEnderChestInventory() != null) {
            List<NbtMap> enderItems = new ObjectArrayList<>(human.namedTag.getList("EnderItems", NbtType.COMPOUND));
            for (int slot = 0; slot < this.getEnderChestInventory().getSize(); ++slot) {
                Item item = this.getEnderChestInventory().getItem(slot);
                if (!item.isNull()) {
                    enderItems.add(ItemHelper.write(item, slot));
                }
            }
            builder.putList("EnderItems", NbtType.COMPOUND, enderItems);
        }

        //EntityHuman
        var skin = getSkin();
        if (skin != null) {
            NbtMapBuilder skinTag = NbtMap.builder()
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
                List<NbtMap> animationsTag = new ObjectArrayList<>();
                for (AnimationData animation : animations) {
                    animationsTag.add(
                            NbtMap.builder()
                                    .putFloat("Frames", animation.getFrames())
                                    .putInt("Type", animation.getTextureType().ordinal())
                                    .putInt("ImageWidth", animation.getImage().getWidth())
                                    .putInt("ImageHeight", animation.getImage().getHeight())
                                    .putInt("AnimationExpression", animation.getExpressionType().ordinal())
                                    .putByteArray("Image", animation.getImage().getImage())
                                    .build()
                    );
                }
                skinTag.putList("AnimatedImageData", NbtType.COMPOUND, animationsTag);
            }

            List<PersonaPieceData> personaPieces = skin.getPersonaPieces();
            if (!personaPieces.isEmpty()) {
                List<NbtMap> piecesTag = new ObjectArrayList<>();
                for (PersonaPieceData piece : personaPieces) {
                    piecesTag.add(
                            NbtMap.builder()
                                    .putString("PieceId", piece.getId())
                                    .putString("PieceType", piece.getType())
                                    .putString("PackId", piece.getPackId())
                                    .putBoolean("IsDefault", piece.isDefault())
                                    .putString("ProductId", piece.getProductId())
                                    .build()
                    );
                }
                skinTag.putList("PersonaPieces", NbtType.COMPOUND, piecesTag);
            }
            List<PersonaPieceTintData> tints = skin.getTintColors();
            if (!tints.isEmpty()) {
                List<NbtMap> tintsTag = new ObjectArrayList<>();
                for (PersonaPieceTintData tint : tints) {
                    List<String> colors = new ObjectArrayList<>();
                    colors.addAll(tint.getColors());
                    tintsTag.add(
                            NbtMap.builder()
                                    .putString("PieceType", tint.getType())
                                    .putList("Colors", NbtType.STRING, colors)
                                    .build()
                    );
                }
                skinTag.putList("PieceTintColors", NbtType.COMPOUND, tintsTag);
            }

            if (!skin.getPlayFabId().isEmpty()) {
                skinTag.putString("PlayFabId", skin.getPlayFabId());
            }

            builder.putCompound("Skin", skinTag.build());
        }
        human.namedTag = builder.build();
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
