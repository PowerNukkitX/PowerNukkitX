package org.powernukkitx.blockentity;

import org.powernukkitx.Player;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.level.Location;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.utils.BlockColor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * @author CreeperFace (Nukkit Project)
 */
public class BlockEntityCauldron extends BlockEntitySpawnable {
    public BlockEntityCauldron(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        int potionId;
        if (!this.nbt.contains("PotionId")) {
            this.nbt.putShort("PotionId", (short) 0xffff);
        }
        potionId = getNbt().getShort("PotionId");
        int potionType = (potionId & 0xFFFF) == 0xFFFF ? PotionType.EMPTY.potionTypeData : PotionType.NORMAL.potionTypeData;
        if (getNbt().getBoolean("SplashPotion")) {
            potionType = PotionType.SPLASH.potionTypeData;
            this.nbt.remove("SplashPotion");
        }

        if (!this.nbt.contains("PotionType")) {
            this.nbt.putShort("PotionType", (short) potionType);
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        int potionId = getNbt().getShort("PotionId");
        int potionType = (potionId & 0xFFFF) == 0xFFFF ? PotionType.EMPTY.potionTypeData : PotionType.NORMAL.potionTypeData;
        this.nbt.putShort("PotionId", (short) getPotionId())
                .putShort("PotionType", (short) potionType);
    }

    public int getPotionId() {
        return getNbt().getShort("PotionId");
    }

    public void setPotionId(int potionId) {
        this.nbt.putShort("PotionId", (short) potionId);
        this.spawnToAll();
    }

    public boolean hasPotion() {
        return (getPotionId() & 0xffff) != 0xffff;
    }

    public void setPotionType(int potionType) {
        this.nbt.putShort("PotionType", (short) (potionType & 0xFFFF));
    }

    public int getPotionType() {
        return (short) (this.getNbt().getShort("PotionType") & 0xFFFF);
    }

    public PotionType getType() {
        return PotionType.getByTypeData(getPotionType());
    }

    public void setType(PotionType type) {
        setPotionType(type.potionTypeData);
    }

    public boolean isSplashPotion() {
        return getNbt().getShort("PotionType") == PotionType.SPLASH.potionTypeData;
    }

    public BlockColor getCustomColor() {
        if (isCustomColor()) {
            int color = getNbt().getInt("CustomColor");

            int red = (color >> 16) & 0xff;
            int green = (color >> 8) & 0xff;
            int blue = (color) & 0xff;

            return new BlockColor(red, green, blue);
        }

        return null;
    }

    public boolean isCustomColor() {
        return nbt.contains("CustomColor");
    }

    public void setCustomColor(BlockColor color) {
        setCustomColor(color.getRed(), color.getGreen(), color.getBlue());
    }

    public void setCustomColor(int r, int g, int b) {
        int color = (r << 16 | g << 8 | b) & 0xffffff;

        this.nbt.putInt("CustomColor", color);

        spawnToAll();
    }

    public void clearCustomColor() {
        nbt.remove("CustomColor");
        spawnToAll();
    }

    @Override
    public void spawnToAll() {
        if (!this.isBlockEntityValid()) {
            return;
        }
        Player[] viewers = this.level.getChunkPlayers(getChunkX(), getChunkZ()).values().toArray(Player.EMPTY_ARRAY);
        Location location = getLocation();
        getLevel().getScheduler().scheduleTask(null, () -> {
            if (isValid()) {
                BlockEntity cauldron = this.level.getBlockEntity(location);
                if (cauldron == BlockEntityCauldron.this) {
                    this.level.sendBlocks(viewers, new Vector3[]{location});
                    super.spawnToAll();
                }
            }
        });
    }

    @Override
    public boolean isBlockEntityValid() {
        String id = getBlock().getId();
        return id.equals(BlockID.CAULDRON);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        final CompoundTag nbtMap = getNbt();
        CompoundTag compoundTag = super.getSpawnCompound()
                .putBoolean("isMovable", this.isMovable())
                .putList("Items", new ListTag<>(Tag.TAG_Compound))
                .putShort("PotionId", nbtMap.getShort("PotionId"))
                .putShort("PotionType", nbtMap.getShort("PotionType"));
        if (nbt.contains("CustomColor")) {
            compoundTag.putInt("CustomColor", nbtMap.getInt("CustomColor") << 8 >> 8);
        }
        return compoundTag;
    }

    @RequiredArgsConstructor
    public enum PotionType {
        EMPTY(-1),
        NORMAL(0),
        SPLASH(1),
        LINGERING(2),
        LAVA(0xF19B),
        UNKNOWN(-2);

        private final int potionTypeData;
        private static final Int2ObjectMap<PotionType> BY_DATA;

        static {
            PotionType[] types = values();
            BY_DATA = new Int2ObjectOpenHashMap<>(types.length);
            for (PotionType type : types) {
                BY_DATA.put(type.potionTypeData, type);
            }
        }

        @NotNull
        public static PotionType getByTypeData(int typeData) {
            return BY_DATA.getOrDefault(typeData, PotionType.UNKNOWN);
        }
    }
}
