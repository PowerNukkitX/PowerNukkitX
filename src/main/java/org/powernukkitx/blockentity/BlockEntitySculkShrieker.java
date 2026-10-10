package org.powernukkitx.blockentity;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockLiquid;
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
import org.powernukkitx.level.vibration.VibrationListenerStorage;
import org.powernukkitx.level.vibration.VibrationType;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Kevims KCodeYT
 */
public class BlockEntitySculkShrieker extends BlockEntity implements VibrationListener {
    public BlockEntitySculkShrieker(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    private static final int SHRIEK_TICKS = 90;
    private static final int DARKNESS_DURATION_TICKS = 260;
    private static final int DARKNESS_RANGE = 40;
    private static final int WARNING_SHARE_RANGE = 16;
    private static final int WARNING_SOUND_RANGE = 10;
    private static final int WARDEN_SEARCH_RANGE = 24;
    private static final int WARDEN_SPAWN_ATTEMPTS = 20;
    private static final int WARDEN_SPAWN_HORIZONTAL_RANGE = 5;
    private static final int WARDEN_SPAWN_VERTICAL_RANGE = 6;

    private int queuedResponseThreatLevel;

    /**
     * Returns the persisted vibration-listener data.
     *
     * @return vibration-listener data
     */
    public CompoundTag getVibrationListenerData() {
        return VibrationListenerStorage.getListener(this.nbt);
    }

    /**
     * Returns whether a vibration event is currently stored.
     *
     * @return whether a stored event exists
     */
    public boolean hasStoredVibrationEvent() {
        return getVibrationListenerData().containsInt(VibrationListenerStorage.TAG_EVENT);
    }

    /**
     * Returns the stored vibration event ID.
     *
     * @return stored vibration event ID
     */
    public int getStoredVibrationEvent() {
        return getVibrationListenerData().getInt(VibrationListenerStorage.TAG_EVENT);
    }

    /**
     * Stores the current vibration event ID.
     *
     * @param event vibration event ID
     */
    public void setStoredVibrationEvent(int event) {
        getVibrationListenerData().putInt(VibrationListenerStorage.TAG_EVENT, event);
        setDirty();
    }

    /**
     * Clears the stored vibration event.
     */
    public void clearStoredVibrationEvent() {
        getVibrationListenerData().remove(VibrationListenerStorage.TAG_EVENT);
        setDirty();
    }

    /**
     * Returns whether pending vibration data is stored.
     *
     * @return whether a pending vibration exists
     */
    public boolean hasPendingVibration() {
        return getVibrationListenerData().containsCompound(VibrationListenerStorage.TAG_PENDING);
    }

    /**
     * Returns the pending vibration data.
     *
     * @return pending vibration data
     */
    public CompoundTag getPendingVibrationData() {
        return getVibrationListenerData().getCompound(VibrationListenerStorage.TAG_PENDING);
    }

    /**
     * Stores pending vibration data.
     *
     * @param pending pending vibration data
     */
    public void setPendingVibrationData(CompoundTag pending) {
        getVibrationListenerData().putCompound(VibrationListenerStorage.TAG_PENDING, pending);
        setDirty();
    }

    /**
     * Clears the pending vibration data.
     */
    public void clearPendingVibrationData() {
        getVibrationListenerData().remove(VibrationListenerStorage.TAG_PENDING);
        setDirty();
    }

    /**
     * Returns whether vibration travel ticks are stored.
     *
     * @return whether stored vibration ticks exist
     */
    public boolean hasStoredVibrationTicks() {
        return getVibrationListenerData().containsInt(VibrationListenerStorage.TAG_TICKS);
    }

    /**
     * Returns the stored vibration travel ticks.
     *
     * @return stored vibration ticks
     */
    public int getStoredVibrationTicks() {
        return getVibrationListenerData().getInt(VibrationListenerStorage.TAG_TICKS);
    }

    /**
     * Stores the vibration travel ticks.
     *
     * @param ticks vibration travel ticks
     */
    public void setStoredVibrationTicks(int ticks) {
        getVibrationListenerData().putInt(VibrationListenerStorage.TAG_TICKS, ticks);
        setDirty();
    }

    /**
     * Clears the stored vibration travel ticks.
     */
    public void clearStoredVibrationTicks() {
        getVibrationListenerData().remove(VibrationListenerStorage.TAG_TICKS);
        setDirty();
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        if (this.nbt.containsInt("QueuedResponseThreatLevel")) {
            this.queuedResponseThreatLevel = this.nbt.getInt("QueuedResponseThreatLevel");
        }
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
        if (getBlock() instanceof BlockSculkShrieker shrieker && shrieker.isShrieking()) {
            finishShrieking();
        }
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId() == BlockID.SCULK_SHRIEKER;
    }

    @Override
    public Position getListenerVector() {
        return this.getPosition().setLevel(this.level).floor().add(0.5f, 0.5f, 0.5f);
    }

    @Override
    public boolean onVibrationOccur(VibrationEvent event) {
        if (event.type() != VibrationType.SCULK_SENSOR_TENDRILS_CLICKING) return false;
        Player player = getResponsiblePlayer(event);
        return player != null && canTrigger(player);
    }

    @Override
    public void onVibrationArrive(VibrationEvent event) {
        Player player = getResponsiblePlayer(event);
        if (player != null) tryShriek(player);
    }

    private Player getResponsiblePlayer(VibrationEvent event) {
        if (event.projectileOwnerUniqueId() != 0) {
            Entity owner = level.getEntityByUniqueId(event.projectileOwnerUniqueId());
            if (owner instanceof Player player) return player;
        }
        return event.initiator() instanceof Player player ? player : null;
    }

    @Override
    public double getListenRange() {
        return 8;
    }

    @Override
    public boolean canReceiveOnlyIfAdjacentChunksAreTicking() {
        return true;
    }

    public void tryShriek(Player player) {
        if (!canTrigger(player)) {
            return;
        }

        Block block = getBlock();
        if (!(block instanceof BlockSculkShrieker shrieker)) {
            return;
        }

        this.queuedResponseThreatLevel = 0;

        if (canSummon(shrieker)) {
            this.queuedResponseThreatLevel = increaseWarning(player);
            this.nbt.putInt("QueuedResponseThreatLevel", this.queuedResponseThreatLevel);
            setDirty();
        }

        shrieker.setShrieking(true);
        level.scheduleUpdate(shrieker, SHRIEK_TICKS);
        level.addParticleEffect(this.add(0.5, 1.0, 0.5), ParticleEffect.SHRIEK);
        level.getVibrationManager().callVibrationEvent(new VibrationEvent(player, getListenerVector(), VibrationType.SHRIEK));
    }

    public void finishShrieking() {
        Block block = getBlock();
        if (!(block instanceof BlockSculkShrieker shrieker)) {
            return;
        }

        shrieker.setShrieking(false);
        if (canSummon(shrieker) && queuedResponseThreatLevel > 0) {
            addDarkness();
            if (queuedResponseThreatLevel >= WardenWarningData.MAX_LEVEL) {
                spawnWarden();
            } else {
                playWarningSound(queuedResponseThreatLevel);
            }
        }

        queuedResponseThreatLevel = 0;
        if (this.nbt.contains("QueuedResponseThreatLevel")) {
            this.nbt.remove("QueuedResponseThreatLevel");
            setDirty();
        }
    }

    private List<Player> getWarningPlayers(Player triggeringPlayer) {
        List<Player> players = new ArrayList<>();
        Vector3 center = getListenerVector();
        for (Player player : level.getPlayers().values()) {
            if (player == triggeringPlayer || !player.isAlive()) continue;
            if (player.distanceSquared(center) <= WARNING_SHARE_RANGE * WARNING_SHARE_RANGE) players.add(player);
        }
        players.add(triggeringPlayer);
        return players;
    }

    private boolean canIncreaseWarning(Player triggeringPlayer) {
        for (Player player : getWarningPlayers(triggeringPlayer)) {
            if (player.getWardenWarningData().increaseCooldown > 0) return false;
        }
        return true;
    }

    private int increaseWarning(Player triggeringPlayer) {
        List<Player> players = getWarningPlayers(triggeringPlayer);
        WardenWarningData selected = triggeringPlayer.getWardenWarningData();

        for (Player player : players) {
            WardenWarningData warning = player.getWardenWarningData();
            if (warning.warningLevel > selected.warningLevel) selected = warning;
        }

        int warningLevel = selected.increase();
        for (Player player : players) {
            WardenWarningData warning = player.getWardenWarningData();
            warning.warningLevel = selected.warningLevel;
            warning.increaseCooldown = selected.increaseCooldown;
            warning.decreaseTimer = selected.decreaseTimer;
        }
        return warningLevel;
    }

    private boolean canTrigger(Player player) {
        if (!isBlockEntityValid()) {
            return false;
        }
        Block block = getBlock();
        if (!(block instanceof BlockSculkShrieker shrieker) || shrieker.isShrieking()) {
            return false;
        }
        if (!canSummon(shrieker)) return true;
        return !hasNearbyWarden() && canIncreaseWarning(player);
    }

    private boolean canSummon(BlockSculkShrieker shrieker) {
        return shrieker.getPropertyValue(CommonBlockProperties.CAN_SUMMON);
    }

    private void playWarningSound(int threatLevel) {
        Sound sound = switch (threatLevel) {
            case 1 -> Sound.MOB_WARDEN_NEARBY_CLOSE;
            case 2 -> Sound.MOB_WARDEN_NEARBY_CLOSER;
            case 3 -> Sound.MOB_WARDEN_NEARBY_CLOSEST;
            case 4 -> Sound.MOB_WARDEN_LISTENING_ANGRY;
            default -> null;
        };
        if (sound == null) return;

        ThreadLocalRandom random = ThreadLocalRandom.current();
        level.addSound(new Vector3(getFloorX() + random.nextInt(-WARNING_SOUND_RANGE, WARNING_SOUND_RANGE + 1),
                getFloorY() + random.nextInt(-WARNING_SOUND_RANGE, WARNING_SOUND_RANGE + 1),
                getFloorZ() + random.nextInt(-WARNING_SOUND_RANGE, WARNING_SOUND_RANGE + 1)), sound);
    }

    private void addDarkness() {
        Vector3 center = getListenerVector();
        for (Player player : level.getPlayers().values()) {
            if (player.distanceSquared(center) <= DARKNESS_RANGE * DARKNESS_RANGE) {
                player.addEffect(Effect.get(EffectType.DARKNESS).setDuration(DARKNESS_DURATION_TICKS));
            }
        }
    }

    private void spawnWarden() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < WARDEN_SPAWN_ATTEMPTS; i++) {
            int x = getFloorX() + random.nextInt(-WARDEN_SPAWN_HORIZONTAL_RANGE, WARDEN_SPAWN_HORIZONTAL_RANGE + 1);
            int z = getFloorZ() + random.nextInt(-WARDEN_SPAWN_HORIZONTAL_RANGE, WARDEN_SPAWN_HORIZONTAL_RANGE + 1);
            for (int y = getFloorY() + WARDEN_SPAWN_VERTICAL_RANGE; y >= getFloorY() - WARDEN_SPAWN_VERTICAL_RANGE; y--) {
                if (!canSpawnAt(x, y, z)) {
                    continue;
                }

                Entity warden = Entity.createEntity(EntityID.WARDEN, new Position(x + 0.5, y, z + 0.5, level));
                if (warden != null) {
                    if (level.hasCollision(warden, warden.getBoundingBox(), true) || containsLiquid(warden)) {
                        warden.close();
                        continue;
                    }
                    if (warden instanceof EntityWarden entityWarden) entityWarden.startEmerging();
                    warden.spawnToAll();
                    return;
                }
            }
        }

        playWarningSound(WardenWarningData.MAX_LEVEL);
    }

    private boolean containsLiquid(Entity entity) {
        var box = entity.getBoundingBox();
        int minX = (int) Math.floor(box.getMinX());
        int minY = (int) Math.floor(box.getMinY());
        int minZ = (int) Math.floor(box.getMinZ());
        int maxX = (int) Math.ceil(box.getMaxX());
        int maxY = (int) Math.ceil(box.getMaxY());
        int maxZ = (int) Math.ceil(box.getMaxZ());

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    if (level.getBlock(x, y, z, 0, false) instanceof BlockLiquid || level.getBlock(x, y, z, 1, false) instanceof BlockLiquid) return true;
                }
            }
        }
        return false;
    }

    private boolean hasNearbyWarden() {
        Vector3 center = getListenerVector();
        var box = new SimpleAxisAlignedBB(center.x - WARDEN_SEARCH_RANGE, center.y - WARDEN_SEARCH_RANGE, center.z - WARDEN_SEARCH_RANGE,
                center.x + WARDEN_SEARCH_RANGE, center.y + WARDEN_SEARCH_RANGE, center.z + WARDEN_SEARCH_RANGE);
        for (Entity entity : level.getNearbyEntities(box)) {
            if (entity instanceof EntityWarden) {
                return true;
            }
        }
        return false;
    }

    private boolean canSpawnAt(int x, int y, int z) {
        Block feet = level.getBlock(new Vector3(x, y, z));
        Block below = level.getBlock(new Vector3(x, y - 1, z));
        return feet.getCollisionBoundingBox() == null && below.isSolid(BlockFace.UP);
    }
}
