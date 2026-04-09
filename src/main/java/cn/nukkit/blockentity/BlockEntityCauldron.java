package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.RequiredArgsConstructor;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.jetbrains.annotations.NotNull;

/**
 * @author CreeperFace (Nukkit Project)
 */
public class BlockEntityCauldron extends BlockEntitySpawnable {
    public BlockEntityCauldron(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        int potionId;
        NbtMapBuilder builder = this.namedTag.toBuilder();
        if (!builder.containsKey("PotionId")) {
            builder.putShort("PotionId", (short) 0xffff);
        }
        potionId = namedTag.getShort("PotionId");
        int potionType = (potionId & 0xFFFF) == 0xFFFF ? PotionType.EMPTY.potionTypeData : PotionType.NORMAL.potionTypeData;
        if (namedTag.getBoolean("SplashPotion")) {
            potionType = PotionType.SPLASH.potionTypeData;
            builder.remove("SplashPotion");
        }

        if (!builder.containsKey("PotionType")) {
            builder.putShort("PotionType", (short) potionType);
        }
        this.namedTag = builder.build();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        int potionId = namedTag.getShort("PotionId");
        int potionType = (potionId & 0xFFFF) == 0xFFFF ? PotionType.EMPTY.potionTypeData : PotionType.NORMAL.potionTypeData;
        this.namedTag = namedTag.toBuilder().putShort("PotionId", (short) getPotionId())
                .putShort("PotionType", (short) potionType)
                .build();
    }

    public int getPotionId() {
        return namedTag.getShort("PotionId");
    }

    public void setPotionId(int potionId) {
        this.namedTag = namedTag.toBuilder().putShort("PotionId", (short) potionId).build();
        this.spawnToAll();
    }

    public boolean hasPotion() {
        return (getPotionId() & 0xffff) != 0xffff;
    }

    public void setPotionType(int potionType) {
        this.namedTag = this.namedTag.toBuilder().putShort("PotionType", (short) (potionType & 0xFFFF)).build();
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
        return namedTag.containsKey("CustomColor");
    }

    public void setCustomColor(BlockColor color) {
        setCustomColor(color.getRed(), color.getGreen(), color.getBlue());
    }

    public void setCustomColor(int r, int g, int b) {
        int color = (r << 16 | g << 8 | b) & 0xffffff;

        this.namedTag = namedTag.toBuilder().putInt("CustomColor", color).build();

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
    public NbtMap getSpawnCompound() {
        NbtMapBuilder compoundTag = super.getSpawnCompound().toBuilder()
                .putBoolean("isMovable", this.isMovable())
                .putList("Items", NbtType.COMPOUND, new ObjectArrayList<>())
                .putShort("PotionId", namedTag.getShort("PotionId"))
                .putShort("PotionType", namedTag.getShort("PotionType"));
        if (namedTag.containsKey("CustomColor")) {
            compoundTag.putInt("CustomColor", namedTag.getInt("CustomColor") << 8 >> 8);
        }
        return compoundTag.build();
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
