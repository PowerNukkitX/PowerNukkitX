package org.powernukkitx.blockentity;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockCreakingHeart;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.CreakingHeartState;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.mob.EntityCreaking;
import org.powernukkitx.event.entity.CreatureSpawnEvent;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;
import lombok.Getter;

public class BlockEntityCreakingHeart extends BlockEntitySpawnable {

    private static final int PLAYER_RANGE = 32;
    private static final int CREAKING_MAX_DISTANCE = 34;
    private static final int UPDATE_TICKS = 20;
    private static final int RANDOM_UPDATE_TICKS_VARIANCE = 5;
    private static final int SPAWN_ATTEMPTS = 5;

    @Getter
    private EntityCreaking linkedCreaking;

    public double spawnRangeHorizontal = 16;
    public double spawnRangeVertical = 8;
    private int nextUpdateTick;

    public BlockEntityCreakingHeart(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        movable = true;
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        if(getLevel().getDimension() == Level.DIMENSION_OVERWORLD) {
            scheduleUpdate();
        }
    }

    @Override
    public boolean isBlockEntityValid() {
        try {
            return this.getBlock().getId().equals(Block.CREAKING_HEART);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getName() {
        return "Creaking Heart";
    }

    public BlockCreakingHeart getHeart() {
        return (BlockCreakingHeart) getBlock();
    }

    public void setLinkedCreaking(EntityCreaking creaking) {
        if(getLinkedCreaking() != null) {
            getLinkedCreaking().setCreakingHeart(null);
        }
        if(creaking != null) {
            creaking.setCreakingHeart(this);
        }
        linkedCreaking = creaking;
    }

    @Override
    public boolean onUpdate() {
        if(!isValid() || closed) return false;

        if(getLevel().getTick() % 40 == 0 && isBlockEntityValid() && getHeart().isActive()) {
            getLevel().addSound(this, Sound.BLOCK_CREAKING_HEART_AMBIENT);
        }

        if (getLevel().getTick() < nextUpdateTick || !isBlockEntityValid()) {
            return true;
        }
        nextUpdateTick = getLevel().getTick() + UPDATE_TICKS + Utils.rand(0, RANDOM_UPDATE_TICKS_VARIANCE);

        this.updateHeartState();

        EntityCreaking creaking = getLinkedCreaking();
        if (creaking != null && (!creaking.isAlive() || creaking.isClosed())) {
            setLinkedCreaking(null);
            creaking = null;
        }

        if (creaking != null) {
            if (!isCreakingActive() || distance(creaking) > CREAKING_MAX_DISTANCE) {
                creaking.kill();
                setLinkedCreaking(null);
            }
            return true;
        }

        if (getHeart().isActive()
                && isCreakingActive()
                && getLevel().getGameRules().getBoolean(GameRule.DO_MOB_SPAWNING)
                && getLevel().getServer().getDifficulty() != 0
                && hasNearbyPlayer()) {
            for (int attempt = 0; attempt < SPAWN_ATTEMPTS; attempt++) {
                Position spawnPos = findSpawnPosition();
                if (spawnPos != null && spawnCreaking(spawnPos)) {
                    break;
                }
            }
        }
        return true;
    }

    private boolean isCreakingActive() {
        return !getLevel().isDay() || getLevel().isRaining() || getLevel().isThundering();
    }

    private boolean hasNearbyPlayer() {
        double rangeSq = PLAYER_RANGE * PLAYER_RANGE;
        for (Player player : getLevel().getPlayers().values()) {
            if (player.isAlive() && player.distanceSquared(this) <= rangeSq) {
                return true;
            }
        }
        return false;
    }

    private void updateHeartState() {
        CreakingHeartState state;
        if (!hasRequiredLogs() && getLinkedCreaking() == null) {
            state = CreakingHeartState.UPROOTED;
        } else {
            state = isCreakingActive() ? CreakingHeartState.AWAKE : CreakingHeartState.DORMANT;
        }

        BlockCreakingHeart heart = getHeart();
        if (heart.getState() != state) {
            heart.setPropertyValue(CommonBlockProperties.CREAKING_HEART_STATE, state);
            getLevel().setBlock(this, heart);
            heart.updateAroundRedstone(BlockFace.UP, BlockFace.DOWN);
        }
    }

    private boolean hasRequiredLogs() {
        for (BlockFace face : BlockFace.values()) {
            if (!getHeart().getPillarAxis().test(face)) {
                continue;
            }

            Block block = getSide(face).getLevelBlock();
            if (!(block instanceof org.powernukkitx.block.BlockPaleOakLog log) || log.getPillarAxis() != getHeart().getPillarAxis()) {
                return false;
            }
        }
        return true;
    }

    private Position findSpawnPosition() {
        Position base = new Position(
                this.x + Utils.rand(-this.spawnRangeHorizontal, this.spawnRangeHorizontal),
                this.y,
                this.z + Utils.rand(-this.spawnRangeHorizontal, this.spawnRangeHorizontal),
                this.level
        );

        for (int yOffset = (int) -spawnRangeVertical; yOffset <= spawnRangeVertical; yOffset++) {
            Position ground = base.add(0, yOffset, 0);
            if (ground.getLevelBlock().isAir()) {
                continue;
            }

            Position feet = ground.add(0, 1, 0);
            Position head = ground.add(0, 2, 0);
            if (feet.getLevelBlock().isAir() && head.getLevelBlock().isAir()) {
                return feet;
            }
        }

        return null;
    }

    private boolean spawnCreaking(Position pos) {
        if (!isValid() || pos.getChunk() == null) {
            return false;
        }

        EntityCreaking creaking = (EntityCreaking) Entity.createEntity(Entity.CREAKING, pos);

        CreatureSpawnEvent ev = new CreatureSpawnEvent(creaking.getNetworkId(), pos, new CompoundTag(), CreatureSpawnEvent.SpawnReason.CREAKING_HEART);
        level.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            creaking.close();
            return false;
        }

        setLinkedCreaking(creaking);
        this.getLevel().addSound(this, Sound.BLOCK_CREAKING_HEART_MOB_SPAWN, 1, 1);
        creaking.spawnToAll();
        return true;
    }

    @Override
    public void onBreak(boolean isSilkTouch) {
        if(getLinkedCreaking() != null) {
            getLinkedCreaking().kill();
        }
        super.onBreak(isSilkTouch);
    }
}
