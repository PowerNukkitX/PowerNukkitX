package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlowingLava;
import cn.nukkit.block.BlockFlowingWater;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.block.BlockMagma;
import cn.nukkit.block.BlockPotentSulfur;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.PotentSulfurState;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityPotentSulfur extends BlockEntity {

    private static final int EFFECT_INTERVAL_TICKS = 10;
    private static final int EFFECT_DURATION_TICKS = 80;
    private static final int ALLOWED_WATER_BLOCKS_ABOVE = 4;
    private static final int GEYSER_INTERVAL_TICKS = 20;
    private static final double EFFECT_RANGE = 3.0d;
    private static final double EFFECT_SEARCH_RANGE = 2.5d;
    private static final double GEYSER_BASE_LAUNCH_SPEED = 0.3d;
    private static final double GEYSER_LAUNCH_FORCE = 0.2d;

    private int waitingCountdown = -1;
    private long eruptionTick = -1L;

    public BlockEntityPotentSulfur(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        if (this.eruptionTick == -1L && this.level != null) {
            this.eruptionTick = this.level.getCurrentTick();
        }
        scheduleUpdate();
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        this.waitingCountdown = this.nbt.getInt("countdown");
        this.eruptionTick = this.nbt.getLong("eruptionTick");
        if (!this.nbt.contains("countdown")) {
            this.waitingCountdown = -1;
        }
        if (!this.nbt.contains("eruptionTick")) {
            this.eruptionTick = -1L;
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putInt("countdown", this.waitingCountdown);
        this.nbt.putLong("eruptionTick", this.eruptionTick);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId().equals(BlockID.POTENT_SULFUR);
    }

    @Override
    public boolean onUpdate() {
        if (!super.onUpdate() && this.closed) {
            return false;
        }

        Block block = getLevelBlock();
        if (!(block instanceof BlockPotentSulfur potentSulfur)) {
            return false;
        }

        PotentSulfurState state = refreshState(potentSulfur);
        if (state == PotentSulfurState.DRY) {
            scheduleUpdate();
            return true;
        }

        long tick = this.level.getCurrentTick();
        if (tick % EFFECT_INTERVAL_TICKS == 0) {
            applyNauseaEffect();
        }

        if (isPeriodicGeyserState(state)) {
            updatePeriodicGeyser(potentSulfur, state);
        }
        if (isLaunchingGeyserState(state)) {
            launchEntities();
        }

        scheduleUpdate();
        return true;
    }

    private void resetCountdown() {
        this.waitingCountdown = -1;
        setDirty();
    }

    private void markEruptionStarted() {
        this.eruptionTick = this.level == null ? -1L : this.level.getCurrentTick();
        setDirty();
    }

    private PotentSulfurState refreshState(BlockPotentSulfur block) {
        PotentSulfurState before = getState(block);
        PotentSulfurState after = calculateState(block, before);
        if (before != after) {
            setState(block, after, false);
            if (isPeriodicGeyserState(after) && !isPeriodicGeyserState(before)) {
                resetCountdown();
            }
            if (isLaunchingGeyserState(after)) {
                markEruptionStarted();
            }
            return after;
        }
        return before;
    }

    private PotentSulfurState calculateState(BlockPotentSulfur block, PotentSulfurState current) {
        if (!isSourceWater(block.up())) {
            return PotentSulfurState.DRY;
        }
        Block below = block.down();
        if (causesContinuousGeyser(below) && isSourceIfLiquid(below)) {
            return PotentSulfurState.CONTINUOUS;
        }
        if (causesPeriodicGeyser(below) && isSourceIfLiquid(below)) {
            return current == PotentSulfurState.ERUPTING ? PotentSulfurState.ERUPTING : PotentSulfurState.DORMANT;
        }
        return PotentSulfurState.WET;
    }

    private PotentSulfurState getState(BlockPotentSulfur block) {
        return block.getPropertyValue(CommonBlockProperties.POTENT_SULFUR_STATE);
    }

    private void setState(BlockPotentSulfur block, PotentSulfurState state, boolean update) {
        block.setPropertyValue(CommonBlockProperties.POTENT_SULFUR_STATE, state);
        this.level.setBlock(block, block, true, update);
    }

    private void updatePeriodicGeyser(BlockPotentSulfur block, PotentSulfurState state) {
        if (this.level.getCurrentTick() % GEYSER_INTERVAL_TICKS != 0) {
            return;
        }

        int waterBlocks = countWaterBlocksAbove();
        if (waterBlocks <= 0) {
            return;
        }

        if (this.waitingCountdown <= 0) {
            this.waitingCountdown = nextWaitingCountdown(state, waterBlocks);
            setDirty();
        }

        if (this.waitingCountdown > 0) {
            this.waitingCountdown--;
        }

        if (this.waitingCountdown == 0) {
            PotentSulfurState next = state == PotentSulfurState.DORMANT ? PotentSulfurState.ERUPTING : PotentSulfurState.DORMANT;
            setState(block, next, true);
            if (next == PotentSulfurState.ERUPTING) {
                markEruptionStarted();
            }
        }
    }

    private int nextWaitingCountdown(PotentSulfurState state, int waterBlocks) {
        int positional = positionalRandom(15);
        int waterOffset = Math.max(0, waterBlocks - 1);
        if (state == PotentSulfurState.DORMANT) {
            return 10 * waterOffset + 15 + positional;
        }
        return waterOffset + 1 + positional % 2;
    }

    private void applyNauseaEffect() {
        Vector3 source = findNoxiousGasSourceBlock();
        if (source == null) {
            return;
        }

        Vector3 center = source.add(0.5, 0.5, 0.5);
        for (Entity entity : this.level.getNearbyEntitiesSafe(noxiousGasSearchBox(source))) {
            if (entity instanceof EntityLiving living && entity.isAlive() && center.distanceSquared(living.getPosition()) <= EFFECT_RANGE * EFFECT_RANGE) {
                living.addEffect(Effect.get(EffectType.NAUSEA).setDuration(EFFECT_DURATION_TICKS).setAmplifier(0));
            }
        }
    }

    private void launchEntities() {
        Vector3 source = findNoxiousGasSourceBlock();
        if (source == null) {
            return;
        }

        int waterBlocks = Math.max(1, (int) (source.y - this.y - 1));
        int height = getUnobstructedBlockCount(waterBlocks);
        for (Entity entity : this.level.getNearbyEntitiesSafe(geyserLaunchBox(height))) {
            if (!canLaunch(entity)) {
                continue;
            }
            if (entity.motionY < GEYSER_BASE_LAUNCH_SPEED + waterBlocks * 0.1d) {
                entity.setMotion(entity.getMotion().add(0, GEYSER_LAUNCH_FORCE, 0));
            }
        }
    }

    private SimpleAxisAlignedBB noxiousGasSearchBox(Vector3 source) {
        return new SimpleAxisAlignedBB(
                source.x - EFFECT_SEARCH_RANGE,
                source.y,
                source.z - EFFECT_SEARCH_RANGE,
                source.x + EFFECT_SEARCH_RANGE + 1,
                source.y + 1,
                source.z + EFFECT_SEARCH_RANGE + 1
        );
    }

    private SimpleAxisAlignedBB geyserLaunchBox(int height) {
        return new SimpleAxisAlignedBB(this.x, this.y + 1, this.z, this.x + 1, this.y + 1 + height, this.z + 1);
    }

    private static boolean canLaunch(Entity entity) {
        return entity.isAlive() && !(entity instanceof Player player && (player.isCreative() || player.isSpectator()));
    }

    private Vector3 findNoxiousGasSourceBlock() {
        int maxY = this.getFloorY() + ALLOWED_WATER_BLOCKS_ABOVE + 1;
        for (int y = this.getFloorY() + 1; y <= maxY; y++) {
            Block block = this.level.getBlock(this.getFloorX(), y, this.getFloorZ());
            if (isSourceWater(block)) {
                continue;
            }
            if (block.isAir() || block.canPassThrough()) {
                return new Vector3(this.getFloorX(), y, this.getFloorZ());
            }
            break;
        }
        return null;
    }

    private int countWaterBlocksAbove() {
        int count = 0;
        for (int y = this.getFloorY() + 1; y <= this.getFloorY() + ALLOWED_WATER_BLOCKS_ABOVE; y++) {
            if (!isSourceWater(this.level.getBlock(this.getFloorX(), y, this.getFloorZ()))) {
                break;
            }
            count++;
        }
        return count;
    }

    private int getUnobstructedBlockCount(int waterBlocks) {
        int max = 6 * waterBlocks;
        for (int i = 0; i < max; i++) {
            Block block = this.level.getBlock(this.getFloorX(), this.getFloorY() + 1 + i, this.getFloorZ());
            if (!block.isAir() && !isSourceWater(block) && !block.canPassThrough()) {
                return i;
            }
        }
        return max;
    }

    private int positionalRandom(int bound) {
        long mixed = this.level.getSeed() ^ ((long) this.getFloorX() * 341873128712L) ^ ((long) this.getFloorY() * 132897987541L) ^ ((long) this.getFloorZ() * 42317861L);
        return Math.floorMod((int) (mixed ^ (mixed >>> 32)), bound + 1);
    }

    private static boolean isSourceWater(Block block) {
        return block instanceof BlockFlowingWater water && water.isSource();
    }

    private static boolean isSourceIfLiquid(Block block) {
        return !(block instanceof BlockLiquid liquid) || liquid.isSource();
    }

    private static boolean causesContinuousGeyser(Block block) {
        return block instanceof BlockFlowingLava;
    }

    private static boolean causesPeriodicGeyser(Block block) {
        return block instanceof BlockMagma;
    }

    private static boolean isPeriodicGeyserState(PotentSulfurState state) {
        return state == PotentSulfurState.DORMANT || state == PotentSulfurState.ERUPTING;
    }

    private static boolean isLaunchingGeyserState(PotentSulfurState state) {
        return state == PotentSulfurState.ERUPTING || state == PotentSulfurState.CONTINUOUS;
    }
}
