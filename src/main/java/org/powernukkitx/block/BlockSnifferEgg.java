package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.CrackedState;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class BlockSnifferEgg extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(SNIFFER_EGG, CommonBlockProperties.CRACKED_STATE);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(0.5)
            .resistance(0.5)
            .canSilkTouch(true)
            .build();
    private static final int REGULAR_HATCH_TIME_TICKS = 24000;
    private static final int BOOSTED_HATCH_TIME_TICKS = 12000;
    private static final int RANDOM_HATCH_OFFSET_TICKS = 300;

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSnifferEgg() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockSnifferEgg(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public String getName() {
        return "Sniffer Egg";
    }

    public CrackedState getCracks() {
        return getPropertyValue(CommonBlockProperties.CRACKED_STATE);
    }

    public void setCracks(CrackedState cracks) {
        setPropertyValue(CommonBlockProperties.CRACKED_STATE, cracks);
    }

    @Override
    public double getMinX() {
        return x + (1.0 / 16);
    }

    @Override
    public double getMinZ() {
        return z + (1.0 / 16);
    }

    @Override
    public double getMaxX() {
        return x + (15.0 / 16);
    }

    @Override
    public double getMaxZ() {
        return z + (15.0 / 16);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        this.setCracks(CrackedState.NO_CRACKS);
        if (!this.getLevel().setBlock(this, this, true, true)) {
            return false;
        }
        scheduleNextHatchTick();
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_RANDOM) {
            scheduleNextHatchTick();
            return type;
        }

        if (type != Level.BLOCK_UPDATE_SCHEDULED) {
            return type;
        }

        if (!getLevel().isChunkLoaded(getFloorX() >> 4, getFloorZ() >> 4)) {
            return type;
        }

        if (getCracks() == CrackedState.MAX_CRACKED) {
            hatch();
            return type;
        }

        setCracks(getCracks().next());
        playCrackSound();
        getLevel().setBlock(this, this, true, true);
        scheduleNextHatchTick();
        return type;
    }

    private void hatch() {
        Level level = getLevel();
        level.addSound(this, Sound.BLOCK_SNIFFER_EGG_HATCH, 0.7f, soundPitch());
        level.setBlock(this, Block.get(BlockID.AIR), true, true);

        Vector3 spawnAt = add(0.5, 0, 0.5);
        Entity sniffer = Entity.createEntity(EntityID.SNIFFER, level.getChunk(spawnAt.getChunkX(), spawnAt.getChunkZ()),
                Entity.getDefaultNBT(spawnAt, null, ThreadLocalRandom.current().nextFloat() * 360f, 0f));
        if (sniffer == null) {
            return;
        }

        sniffer.setBaby(true);
        sniffer.spawnToAll();
    }

    private void playCrackSound() {
        getLevel().addSound(this, Sound.BLOCK_SNIFFER_EGG_CRACK, 0.7f, soundPitch());
    }

    private float soundPitch() {
        return 0.9f + ThreadLocalRandom.current().nextFloat() * 0.2f;
    }

    private void scheduleNextHatchTick() {
        Level level = getLevel();
        if (level == null) {
            return;
        }

        Position pos = floor();
        if (level.isUpdateScheduled(pos, this)) {
            return;
        }

        int hatchTime = down().getId().equals(BlockID.MOSS_BLOCK) ? BOOSTED_HATCH_TIME_TICKS : REGULAR_HATCH_TIME_TICKS;
        int delay = Math.max(1, hatchTime / 3) + ThreadLocalRandom.current().nextInt(RANDOM_HATCH_OFFSET_TICKS);
        level.scheduleUpdate(this, pos, delay, 0, true, false);
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    
    @Override
    public BlockSnifferEgg clone() {
        return (BlockSnifferEgg) super.clone();
    }
}
