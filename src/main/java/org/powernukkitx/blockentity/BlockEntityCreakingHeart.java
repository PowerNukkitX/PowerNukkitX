package org.powernukkitx.blockentity;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockCreakingHeart;
import org.powernukkitx.block.BlockResinClump;
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
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationType;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Hash;
import org.powernukkitx.utils.Utils;
import lombok.Getter;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockEntityCreakingHeart extends BlockEntitySpawnable {
    public BlockEntityCreakingHeart(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public static final String TAG_COOLDOWN = "Cooldown";
    public static final String TAG_SPAWNED_CREAKING_ID = "SpawnedCreakingID";

    private static final int PLAYER_RANGE = 32;
    private static final int CREAKING_MAX_DISTANCE = 34;
    private static final int UPDATE_TICKS_MIN = 20;
    private static final int UPDATE_TICKS_MAX = 24;
    private static final int SPAWN_ATTEMPTS = 5;
    private static final int RESIN_COOLDOWN_TICKS = 100;
    private static final int RESIN_SEARCH_DISTANCE = 2;

    @Getter
    private EntityCreaking linkedCreaking;

    private int cooldown;
    private long spawnedCreakingId;
    private int resinCooldown;

    public double spawnRangeHorizontal = 16;
    public double spawnRangeVertical = 8;

    private record PaleOakNode(Block block, int distance) {
    }

    private record ResinPlacement(Block support, BlockFace face) {
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
        if (linkedCreaking == creaking) return;
        if (creaking != null && spawnedCreakingId != -1L && spawnedCreakingId != creaking.uniqueIdLong()) return;

        if (linkedCreaking != null) linkedCreaking.clearCreakingHeart(this);
        linkedCreaking = creaking;

        if (creaking != null) {
            spawnedCreakingId = creaking.uniqueIdLong();
            creaking.bindToCreakingHeart(this);
            creaking.setPersistent(true);
        } else {
            spawnedCreakingId = -1L;
        }

        setDirty();
    }

    private EntityCreaking resolveLinkedCreaking() {
        if (spawnedCreakingId == -1L) return null;

        Entity entity = getLevel().getEntityByUniqueId(spawnedCreakingId);
        if (entity instanceof EntityCreaking creaking) {
            setLinkedCreaking(creaking);
            return creaking;
        }

        return null;
    }

    /**
     * Clears this heart's linkage when its associated creaking begins crumbling.
     *
     * @param creaking crumbling creaking
     */
    public void onLinkedCreakingCrumbling(EntityCreaking creaking) {
        if (linkedCreaking != creaking && spawnedCreakingId != creaking.uniqueIdLong()) return;

        linkedCreaking = null;
        spawnedCreakingId = -1L;
        cooldown = 2;
        setDirty();
    }

    @Override
    public void loadNBT() {
        super.loadNBT();

        cooldown = getNbt().containsNumber(TAG_COOLDOWN) ? Math.max(0, getNbt().getInt(TAG_COOLDOWN)) : UPDATE_TICKS_MIN;

        if (getNbt().containsNumber(TAG_SPAWNED_CREAKING_ID)) {
            spawnedCreakingId = getNbt().getLong(TAG_SPAWNED_CREAKING_ID);
            if (spawnedCreakingId == 0L) spawnedCreakingId = -1L;
        } else {
            spawnedCreakingId = -1L;
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.nbt.putInt(TAG_COOLDOWN, cooldown);

        if (spawnedCreakingId != -1L) {
            this.nbt.putLong(TAG_SPAWNED_CREAKING_ID, spawnedCreakingId);
        } else {
            this.nbt.remove(TAG_SPAWNED_CREAKING_ID);
        }
    }

    @Override
    public boolean onUpdate() {
        if (!isValid() || closed || !isBlockEntityValid()) return false;

        if (resinCooldown > 0) {
            resinCooldown--;
        }

        if (getLevel().getTick() % 40 == 0 && getHeart().getState() == CreakingHeartState.AWAKE) {
            getLevel().addSound(this, Sound.BLOCK_CREAKING_HEART_AMBIENT);
        }

        EntityCreaking creaking = linkedCreaking;
        if (creaking == null && spawnedCreakingId != -1L) {
            creaking = resolveLinkedCreaking();
            if (creaking == null) {
                spawnedCreakingId = -1L;
                setDirty();
                return true;
            }
        }

        if (creaking != null && (!creaking.isAlive() || creaking.isClosed())) {
            setLinkedCreaking(null);
            creaking = null;
        }

        if (cooldown >= 2) {
            cooldown--;
            setDirty();
            return true;
        }

        cooldown = Utils.rand(UPDATE_TICKS_MIN, UPDATE_TICKS_MAX);
        setDirty();
        updateHeartState();

        if (creaking != null) {
            if (!hasRequiredLogs()) {
                creaking.startTwitching();
                return true;
            }

            if (!creaking.hasCustomName() && (!isCreakingActive() || distance(creaking) > CREAKING_MAX_DISTANCE)) {
                creaking.crumble();
            }

            return true;
        }

        if (spawnedCreakingId != -1L) return true;

        if (getHeart().isActive()
                && isCreakingActive()
                && getLevel().getGameRules().getBoolean(GameRule.DO_MOB_SPAWNING)
                && getLevel().getServer().getDifficulty() != 0
                && hasNearbyPlayer()) {
            for (int attempt = 0; attempt < SPAWN_ATTEMPTS; attempt++) {
                Position spawnPos = findSpawnPosition();
                if (spawnPos != null && spawnCreaking(spawnPos)) break;
            }
        }

        return true;
    }

    /**
     * Returns whether the block is a pale oak log or wood variant valid for a creaking heart.
     *
     * @param block block to test
     * @return whether the block is a valid pale oak heart log
     */
    public static boolean isPaleOakHeartLog(Block block) {
        return Block.PALE_OAK_LOG.equals(block.getId())
                || Block.PALE_OAK_WOOD.equals(block.getId())
                || Block.STRIPPED_PALE_OAK_LOG.equals(block.getId())
                || Block.STRIPPED_PALE_OAK_WOOD.equals(block.getId());
    }

    /**
     * Returns whether the block is a valid pale oak heart log aligned to the specified axis.
     *
     * @param block block to test
     * @param axis required pillar axis
     * @return whether the block and axis match
     */
    public static boolean isPaleOakHeartLog(Block block, BlockFace.Axis axis) {
        return isPaleOakHeartLog(block) && block.getPropertyValue(CommonBlockProperties.PILLAR_AXIS) == axis;
    }

    /**
     * Returns whether the current world time permits an active creaking.
     *
     * @return whether the creaking activity window is active
     */
    public boolean isCreakingActive() {
        long time = Math.floorMod(getLevel().getTime(), 24000);
        return time > 12600 && time <= 23400;
    }

    private boolean hasNearbyPlayer() {
        double rangeSq = PLAYER_RANGE * PLAYER_RANGE;
        for (Player player : getLevel().getPlayers().values()) {
            if (player.isAlive() && !player.isSpectator() && player.distanceSquared(this) <= rangeSq) {
                return true;
            }
        }
        return false;
    }

    private void updateHeartState() {
        CreakingHeartState state;
        if (!hasRequiredLogs()) {
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
            if (!getHeart().getPillarAxis().test(face)) continue;

            Block block = getSide(face).getLevelBlock();
            if (!isPaleOakHeartLog(block, getHeart().getPillarAxis())) return false;
        }
        return true;
    }

    /**
     * Handles a player damaging the heart's creaking, including trail feedback and resin spawning.
     *
     * @param creaking damaged creaking
     */
    public void onCreakingDamagedByPlayer(EntityCreaking creaking) {
        if (resinCooldown > 0) return;

        resinCooldown = RESIN_COOLDOWN_TICKS;
        creaking.sendParticleTrail();
        getLevel().addSound(this, Sound.BLOCK_CREAKING_HEART_TRAIL);

        if (!getHeart().isActive() || !isCreakingActive()) return;

        spawnResin(Utils.rand(2, 3));
    }

    private int spawnResin(int amount) {
        List<ResinPlacement> placements = new ArrayList<>();
        ArrayDeque<PaleOakNode> queue = new ArrayDeque<>();
        Set<Long> visited = new HashSet<>();

        for (BlockFace face : BlockFace.values()) {
            Block block = getSide(face).getLevelBlock();
            if (isPaleOakHeartLog(block)) queue.addLast(new PaleOakNode(block, 1));
        }

        while (!queue.isEmpty()) {
            PaleOakNode node = queue.removeFirst();
            Block log = node.block();
            long hash = Hash.hashBlock(log.getFloorX(), log.getFloorY(), log.getFloorZ());
            if (!visited.add(hash)) continue;

            for (BlockFace face : BlockFace.values()) {
                Block side = log.getSide(face).getLevelBlock();

                if (side.isAir() || side instanceof BlockResinClump clump && !clump.isGrowthToSide(face.getOpposite())) {
                    placements.add(new ResinPlacement(log, face));
                }

                if (node.distance() < RESIN_SEARCH_DISTANCE && isPaleOakHeartLog(side)) {
                    queue.addLast(new PaleOakNode(side, node.distance() + 1));
                }
            }
        }

        Collections.shuffle(placements);

        int spawned = 0;
        for (ResinPlacement placement : placements) {
            if (spawned >= amount) break;

            BlockFace resinFace = placement.face().getOpposite();
            Block target = placement.support().getSide(placement.face()).getLevelBlock();

            if (target.isAir()) {
                BlockResinClump clump = (BlockResinClump) Block.get(Block.RESIN_CLUMP);
                clump.setPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS, 0b000001 << resinFace.getDUSWNEIndex());
                getLevel().setBlock(target, clump, true, true);
                spawned++;
            } else if (target instanceof BlockResinClump clump && !clump.isGrowthToSide(resinFace)) {
                clump.growToSide(resinFace);
                spawned++;
            }
        }

        return spawned;
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
        if (!isValid() || pos.getChunk() == null) return false;

        Entity entity = Entity.createEntity(Entity.CREAKING, pos);
        if (!(entity instanceof EntityCreaking creaking)) return false;

        CreatureSpawnEvent ev = new CreatureSpawnEvent(creaking.getNetworkId(), pos,
                new CompoundTag(), CreatureSpawnEvent.SpawnReason.CREAKING_HEART);
        level.getServer().getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            creaking.close();
            return false;
        }

        setLinkedCreaking(creaking);
        getLevel().addSound(this, Sound.BLOCK_CREAKING_HEART_MOB_SPAWN, 1, 1);
        getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(creaking, creaking.getVector3(), VibrationType.ENTITY_PLACE));
        creaking.spawnToAll();

        int particleData = (int) Math.ceil(creaking.getWidth()) | ((int) Math.ceil(creaking.getHeight()) << 8);
        getLevel().addLevelEvent(pos, LevelEvent.PARTICLE_MOB_BLOCK_SPAWN, particleData);

        return true;
    }

    @Override
    public void onBreak(boolean isSilkTouch) {
        if (linkedCreaking != null) {
            linkedCreaking.startTwitching();
            linkedCreaking.clearCreakingHeart(this);
            linkedCreaking = null;
            spawnedCreakingId = -1L;
        }

        super.onBreak(isSilkTouch);
    }
}
