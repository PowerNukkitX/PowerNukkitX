package org.powernukkitx.blockentity;

import org.powernukkitx.Player;
import org.powernukkitx.block.BlockCauldron;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.Location;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.utils.BlockColor;
import org.powernukkitx.utils.RuntimeBlockDefinition;

import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.ActorBlockSyncMessageId;
import org.cloudburstmc.protocol.bedrock.data.BlockChangeEntry;
import org.cloudburstmc.protocol.bedrock.packet.UpdateBlockPacket;
import org.cloudburstmc.protocol.bedrock.packet.UpdateSubChunkBlocksPacket;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.FILL_LEVEL;

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

        if (!this.nbt.containsShort("PotionId")) {
            this.nbt.putShort("PotionId", (short) 0xffff);
        }

        if (!this.nbt.containsShort("PotionType")) {
            this.nbt.putShort("PotionType", (short) PotionType.EMPTY.potionTypeData);
        }

        if (!this.nbt.containsList("Items")) {
            this.nbt.putList("Items", new ListTag<>());
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.nbt.putShort("PotionId", (short) getPotionId())
                .putShort("PotionType", (short) getPotionType());

        if (!this.nbt.containsList("Items")) {
            this.nbt.putList("Items", new ListTag<>());
        }
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
        setDirty();
        refreshCustomColorToAll();
    }

    public void clearCustomColor() {
        nbt.remove("CustomColor");
        setDirty();
        refreshCustomColorToAll();
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

    private void refreshCustomColorToAll() {
        if (!this.isBlockEntityValid()) return;

        Location location = getLocation();

        getLevel().getScheduler().scheduleTask(null, () -> {
            if (!isValid() || this.level.getBlockEntity(location) != this) return;
            if (!(getBlock() instanceof BlockCauldron cauldron)) return;

            Player[] viewers = this.level.getChunkPlayers(getChunkX(), getChunkZ()).values().toArray(Player.EMPTY_ARRAY);
            BlockState currentState = cauldron.getBlockState();
            int fillLevel = cauldron.getFillLevel();

            if (fillLevel > FILL_LEVEL.getMin()) {
                BlockState refreshState = currentState.setPropertyValue(BlockCauldron.PROPERTIES, FILL_LEVEL, fillLevel - 1);
                Vector3i position = Vector3i.from(getFloorX(), getFloorY(), getFloorZ());

                UpdateSubChunkBlocksPacket packet = new UpdateSubChunkBlocksPacket();
                packet.setSubChunkBlockPosition(Vector3i.from(
                        (getFloorX() >> 4) << 4,
                        (getFloorY() >> 4) << 4,
                        (getFloorZ() >> 4) << 4
                ));

                packet.getStandardBlocks().add(
                        new BlockChangeEntry(position, new RuntimeBlockDefinition((int) refreshState.unsignedBlockStateHash()), 3, -1, ActorBlockSyncMessageId.NONE)
                );

                packet.getStandardBlocks().add(
                        new BlockChangeEntry(position, new RuntimeBlockDefinition((int) currentState.unsignedBlockStateHash()), 3, -1, ActorBlockSyncMessageId.NONE)
                );

                for (Player viewer : viewers) {
                    if (viewer.spawned) viewer.sendPacket(packet);
                }

                super.spawnToAll();
                super.spawnToAll();
            } else {
                super.spawnToAll();
            }

            getLevel().getScheduler().scheduleTask(null, () -> {
                if (!isValid() || this.level.getBlockEntity(location) != this) return;
                Player[] currentViewers = this.level.getChunkPlayers(getChunkX(), getChunkZ()).values().toArray(Player.EMPTY_ARRAY);
                this.level.sendBlocks(currentViewers, new Vector3[]{location}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 0);
                super.spawnToAll();
            });
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
