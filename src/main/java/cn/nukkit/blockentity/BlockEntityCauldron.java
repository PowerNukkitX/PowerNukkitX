package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.BlockCauldron;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.BlockColor;
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
        if (!namedTag.contains("PotionId")) {
            namedTag.putShort("PotionId", 0xffff);
        }
        potionId = namedTag.getShort("PotionId");
        int potionType = (potionId & 0xFFFF) == 0xFFFF ? PotionType.EMPTY.potionTypeData : PotionType.NORMAL.potionTypeData;
        if (namedTag.getBoolean("SplashPotion")) {
            potionType = PotionType.SPLASH.potionTypeData;
            namedTag.remove("SplashPotion");
        }

        if (!namedTag.contains("PotionType")) {
            namedTag.putShort("PotionType", potionType);
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        namedTag.putShort("PotionId", getPotionId());
        int potionId = namedTag.getShort("PotionId");
        int potionType = (potionId & 0xFFFF) == 0xFFFF ? PotionType.EMPTY.potionTypeData : PotionType.NORMAL.potionTypeData;
        namedTag.putShort("PotionType", potionType);
    }

    public int getPotionId() {
        return namedTag.getShort("PotionId");
    }

    public void setPotionId(int potionId) {
        namedTag.putShort("PotionId", potionId);
        this.spawnToAll();
    }

    public boolean hasPotion() {
        return (getPotionId() & 0xffff) != 0xffff;
    }

    public void setPotionType(int potionType) {
        this.namedTag.putShort("PotionType", (short) (potionType & 0xFFFF));
    }

    public int getPotionType() {
        return (short) (this.namedTag.getShort("PotionType") & 0xFFFF);
    }

    public PotionType getType() {
        return PotionType.getByTypeData(getPotionType());
    }

    public void setType(PotionType type) {
        setPotionType(type.potionTypeData);
    }

    public boolean isSplashPotion() {
        return namedTag.getShort("PotionType") == PotionType.SPLASH.potionTypeData;
    }

    public BlockColor getCustomColor() {
        if (isCustomColor()) {
            int color = namedTag.getInt("CustomColor");

            int red = (color >> 16) & 0xff;
            int green = (color >> 8) & 0xff;
            int blue = (color) & 0xff;

            return new BlockColor(red, green, blue);
        }

        return null;
    }

    public boolean isCustomColor() {
        return namedTag.contains("CustomColor");
    }

    public void setCustomColor(BlockColor color) {
        setCustomColor(color.getRed(), color.getGreen(), color.getBlue());
    }

    public void setCustomColor(int r, int g, int b) {
        int color = (r << 16 | g << 8 | b) & 0xffffff;

        namedTag.putInt("CustomColor", color);

        spawnToAll();
    }

    public void clearCustomColor() {
        namedTag.remove("CustomColor");
        spawnToAll();
    }

    @Override
    public void spawnToAll() {
        if (!this.isBlockEntityValid()) {
            return;
        }
        BlockCauldron block = (BlockCauldron) getBlock();
        Player[] viewers = this.level.getChunkPlayers(getChunkX(), getChunkZ()).values().toArray(Player.EMPTY_ARRAY);
        this.level.sendBlocks(viewers, new Vector3[]{block});
        super.spawnToAll();
        Location location = getLocation();
        Server.getInstance().getScheduler().scheduleTask(null, () -> {
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
        CompoundTag compoundTag = super.getSpawnCompound()
                .putBoolean("isMovable", this.isMovable())
                .putList("Items", new ListTag<>())
                .putShort("PotionId", (short) namedTag.getShort("PotionId"))
                .putShort("PotionType", (short) namedTag.getShort("PotionType"));
        if (namedTag.contains("CustomColor")) {
            compoundTag.putInt("CustomColor", namedTag.getInt("CustomColor") << 8 >> 8);
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
