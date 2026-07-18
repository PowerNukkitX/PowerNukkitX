package org.powernukkitx.blockentity;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockSculkShrieker;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.data.warden.WardenWarningData;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.entity.mob.EntityWarden;
import org.powernukkitx.level.ParticleEffect;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationListener;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Kevims KCodeYT
 */


public class BlockEntitySculkShrieker extends BlockEntity implements VibrationListener {

    private static final int SHRIEK_TICKS = 90;
    private static final int PLAYER_COOLDOWN_TICKS = 200;
    private static final int WARNING_DECAY_TICKS = 12000;
    private static final int DARKNESS_DURATION_TICKS = 260;
    private static final int DARKNESS_RANGE = 40;
    private static final int WARDEN_WARNING_LEVEL = 4;

    private UUID triggeringPlayer;
    private boolean summonAfterShriek;
    private int shriekEndTick;


    public BlockEntitySculkShrieker(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        this.level.getVibrationManager().addListener(this);
    }

    @Override
    public void close() {
        if (this.level != null) {
            this.level.getVibrationManager().removeListener(this);
        }
        super.close();
    }

    @Override
    public void onBreak(boolean isSilkTouch) {
        this.level.getVibrationManager().removeListener(this);
        if (summonAfterShriek && getBlock() instanceof BlockSculkShrieker shrieker && shrieker.isShrieking()) {
            spawnWarden();
        }
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId() == BlockID.SCULK_SHRIEKER;
    }

    @Override
    public boolean onUpdate() {
        if (!isBlockEntityValid()) {
            close();
            return false;
        }

        if (!(getBlock() instanceof BlockSculkShrieker shrieker) || !shrieker.isShrieking()) {
            return false;
        }

        if (level.getTick() >= shriekEndTick) {
            finishShrieking();
            return false;
        }

        return true;
    }

    @Override
    public Position getListenerVector() {
        return this.getPosition().setLevel(this.level).floor().add(0.5f, 0.5f, 0.5f);
    }

    @Override
    public boolean onVibrationOccur(VibrationEvent event) {
        return event.initiator() instanceof Player player && canTrigger(player);
    }

    @Override
    public void onVibrationArrive(VibrationEvent event) {
        if (event.initiator() instanceof Player player) {
            tryShriek(player);
        }
    }

    @Override
    public double getListenRange() {
        return 8;
    }

    public void tryShriek(Player player) {
        if (!canTrigger(player)) {
            return;
        }

        Block block = getBlock();
        if (!(block instanceof BlockSculkShrieker shrieker)) {
            return;
        }

        this.triggeringPlayer = player.getUniqueId();
        this.summonAfterShriek = false;

        if (canSummon(shrieker)) {
            WardenWarningData warning = warningFor(player);
            warning.warningLevel++;
            warning.lastWarningTick = level.getTick();
            warning.lastShriekTick = level.getTick();
            this.summonAfterShriek = warning.warningLevel >= WARDEN_WARNING_LEVEL;
        }

        shrieker.setShrieking(true);
        this.shriekEndTick = level.getTick() + SHRIEK_TICKS;
        level.addParticleEffect(this.add(0.5, 1.0, 0.5), ParticleEffect.SHRIEK);
        scheduleUpdate();
    }

    public void finishShrieking() {
        Block block = getBlock();
        if (!(block instanceof BlockSculkShrieker shrieker)) {
            return;
        }

        shrieker.setShrieking(false);
        if (canSummon(shrieker) && triggeringPlayer != null) {
            Player player = level.getServer().getPlayer(triggeringPlayer).orElse(null);
            if (player != null && player.distanceSquared(this) <= DARKNESS_RANGE * DARKNESS_RANGE) {
                addDarkness();
            }
            if (summonAfterShriek) {
                spawnWarden();
            }
        }

        triggeringPlayer = null;
        summonAfterShriek = false;
        shriekEndTick = 0;
    }

    private boolean canTrigger(Player player) {
        if (!isBlockEntityValid()) {
            return false;
        }
        Block block = getBlock();
        if (!(block instanceof BlockSculkShrieker shrieker) || shrieker.isShrieking()) {
            return false;
        }
        return !canSummon(shrieker) || level.getTick() - warningFor(player).lastShriekTick >= PLAYER_COOLDOWN_TICKS;
    }

    private boolean canSummon(BlockSculkShrieker shrieker) {
        return shrieker.getPropertyValue(CommonBlockProperties.CAN_SUMMON);
    }

    private WardenWarningData warningFor(Player player) {
        WardenWarningData warning = player.getWardenWarningData();
        int elapsed = level.getTick() - warning.lastWarningTick;
        if (elapsed >= WARNING_DECAY_TICKS && warning.warningLevel > 0) {
            warning.warningLevel = Math.max(0, warning.warningLevel - elapsed / WARNING_DECAY_TICKS);
            warning.lastWarningTick = level.getTick();
        }
        return warning;
    }

    private void addDarkness() {
        for (Player player : level.getPlayers().values()) {
            if ((player.isSurvival() || player.isAdventure()) && player.distanceSquared(this) <= DARKNESS_RANGE * DARKNESS_RANGE) {
                player.addEffect(Effect.get(EffectType.DARKNESS).setDuration(DARKNESS_DURATION_TICKS));
            }
        }
    }

    private void spawnWarden() {
        if (hasNearbyWarden()) {
            return;
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 20; i++) {
            int x = getFloorX() + random.nextInt(-5, 6);
            int z = getFloorZ() + random.nextInt(-5, 6);
            for (int y = getFloorY() + 6; y >= getFloorY() - 6; y--) {
                if (!canSpawnAt(x, y, z)) {
                    continue;
                }

                Entity warden = Entity.createEntity(EntityID.WARDEN, new Position(x + 0.5, y, z + 0.5, level));
                if (warden != null) {
                    warden.spawnToAll();
                    return;
                }
            }
        }

        level.addSound(this, Sound.MOB_WARDEN_ROAR);
    }

    private boolean hasNearbyWarden() {
        var box = new SimpleAxisAlignedBB(x - 24, y - 24, z - 24, x + 24, y + 24, z + 24);
        for (Entity entity : level.getNearbyEntities(box)) {
            if (entity instanceof EntityWarden) {
                return true;
            }
        }
        return false;
    }

    private boolean canSpawnAt(int x, int y, int z) {
        Block feet = level.getBlock(new Vector3(x, y, z));
        Block head = level.getBlock(new Vector3(x, y + 1, z));
        Block below = level.getBlock(new Vector3(x, y - 1, z));
        return feet.canPassThrough() && head.canPassThrough() && below.isSolid();
    }

}
