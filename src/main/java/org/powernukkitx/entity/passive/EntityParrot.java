package org.powernukkitx.entity.passive;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.cloudburstmc.protocol.bedrock.data.actor.MoveActorAbsoluteData;
import org.cloudburstmc.protocol.bedrock.packet.MoveActorAbsolutePacket;
import org.powernukkitx.blockentity.BlockEntityJukebox;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityCanSit;
import org.powernukkitx.entity.EntityVariant;
import org.powernukkitx.entity.EntityLiving;
import org.powernukkitx.entity.EntityFilter;
import org.powernukkitx.entity.ai.EntityAI;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.ParrotFlightController;
import org.powernukkitx.entity.ai.executor.ParrotMovementExecutor;
import org.powernukkitx.entity.ai.route.finder.impl.ParrotRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.ParrotPosEvaluator;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.powernukkitx.entity.EntityFlyable;
import org.powernukkitx.entity.components.HealableComponent;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.components.TameableComponent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.Level;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PikyCZ
 */
public class EntityParrot extends EntityAnimal implements EntityFlyable, EntityCanSit, EntityVariant {
    private int panicUntil;
    private int mountAfter;
    private float carrierHealth;
    private BlockEntityJukebox dancingJukebox;

    private static final Map<String, SoundEvent> IMITATIONS = Map.ofEntries(
            Map.entry(BLAZE, SoundEvent.IMITATE_BLAZE),
            Map.entry(CAVE_SPIDER, SoundEvent.IMITATE_CAVE_SPIDER),
            Map.entry(CREEPER, SoundEvent.IMITATE_CREEPER),
            Map.entry(ELDER_GUARDIAN, SoundEvent.IMITATE_ELDER_GUARDIAN),
            Map.entry(ENDER_DRAGON, SoundEvent.IMITATE_ENDER_DRAGON),
            Map.entry(ENDERMAN, SoundEvent.IMITATE_ENDERMAN),
            Map.entry(ENDERMITE, SoundEvent.IMITATE_ENDERMITE),
            Map.entry(EVOCATION_ILLAGER, SoundEvent.IMITATE_EVOCATION_ILLAGER),
            Map.entry(GHAST, SoundEvent.IMITATE_GHAST),
            Map.entry(HUSK, SoundEvent.IMITATE_HUSK),
            Map.entry(MAGMA_CUBE, SoundEvent.IMITATE_MAGMA_CUBE),
            Map.entry(POLAR_BEAR, SoundEvent.IMITATE_POLAR_BEAR),
            Map.entry(SHULKER, SoundEvent.IMITATE_SHULKER),
            Map.entry(SILVERFISH, SoundEvent.IMITATE_SILVERFISH),
            Map.entry(SKELETON, SoundEvent.IMITATE_SKELETON),
            Map.entry(SLIME, SoundEvent.IMITATE_SLIME),
            Map.entry(SPIDER, SoundEvent.IMITATE_SPIDER),
            Map.entry(STRAY, SoundEvent.IMITATE_STRAY),
            Map.entry(VEX, SoundEvent.IMITATE_VEX),
            Map.entry(VINDICATOR, SoundEvent.IMITATE_VINDICATION_ILLAGER),
            Map.entry(WITCH, SoundEvent.IMITATE_WITCH),
            Map.entry(WITHER, SoundEvent.IMITATE_WITHER),
            Map.entry(WITHER_SKELETON, SoundEvent.IMITATE_WITHER_SKELETON),
            Map.entry(WOLF, SoundEvent.IMITATE_WOLF),
            Map.entry(ZOMBIE, SoundEvent.IMITATE_ZOMBIE),
            Map.entry(ZOMBIE_PIGMAN, SoundEvent.IMITATE_ZOMBIE_PIGMAN),
            Map.entry(ZOMBIE_VILLAGER, SoundEvent.IMITATE_ZOMBIE_VILLAGER),
            Map.entry(ZOMBIE_VILLAGER_V2, SoundEvent.IMITATE_ZOMBIE_VILLAGER),
            Map.entry(WARDEN, SoundEvent.IMITATE_WARDEN),
            Map.entry(BREEZE, SoundEvent.IMITATE_BREEZE),
            Map.entry(BOGGED, SoundEvent.IMITATE_BOGGED),
            Map.entry(DROWNED, SoundEvent.IMITATE_DROWNED),
            Map.entry(CREAKING, SoundEvent.IMITATE_CREAKING),
            Map.entry(PHANTOM, SoundEvent.IMITATE_PHANTOM),
            Map.entry(ZOGLIN, SoundEvent.IMITATE_ZOGLIN),
            Map.entry(GUARDIAN, SoundEvent.IMITATE_GUARDIAN),
            Map.entry(PILLAGER, SoundEvent.IMITATE_PILLAGER),
            Map.entry(RAVAGER, SoundEvent.IMITATE_RAVAGER),
            Map.entry(HAPPY_GHAST, SoundEvent.IMITATE_HAPPY_GHAST),
            Map.entry(PARCHED, SoundEvent.IMITATE_PARCHED),
            Map.entry(CAMEL_HUSK, SoundEvent.IMITATE_CAMEL_HUSK)
    );
    @Override
    @NotNull public String getIdentifier() {
        return PARROT;
    }

    public EntityParrot(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        if (!hasVariant() || getVariant() < 0 || getVariant() > 4) setVariant(randomVariant());
        mountAfter = getLevel().getTick() + 100;
        if (isTamed()) setPersistent(true);
        setDataFlag(ActorFlags.CAN_FLY, true);
    }

    @Override
    public String getOriginalName() {
        return "Parrot";
    }

    @Override
    public @NotNull Set<String> typeFamily() {
        return isTamed() ? Set.of("parrot_tame", "mob") : Set.of("parrot_wild", "mob");
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 1.0f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(6);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.4f);
    }

    @Override
    public @Nullable TameableComponent getComponentTameable() {
        return new TameableComponent(
                0.33f,
                Set.of(
                    ItemID.WHEAT_SEEDS,
                    ItemID.PUMPKIN_SEEDS,
                    ItemID.MELON_SEEDS,
                    ItemID.BEETROOT_SEEDS,
                    ItemID.PITCHER_POD,
                    ItemID.TORCHFLOWER_SEEDS
                )
        );
    }

    @Override
    public HealableComponent getComponentHealable() {
        return new HealableComponent(
                EntityFilter.all(
                        (self, ctx) -> !self.isRiding()
                ),
                true,
                List.of(
                    new HealableComponent.Item(
                            Item.COOKIE,
                            0,
                            List.of(new HealableComponent.Effect("fatal_poison", 1.0f, 1000.0f, 0.0f))
                    )
                )
        );
    }


    /** @return the five Bedrock plumage variants */
    @Override
    public int @NotNull [] getAllVariant() {
        return new int[]{0, 1, 2, 3, 4};
    }

    @Override
    protected IBehaviorGroup requireBehaviorGroup() {
        return EntityAI.of(this)
                .behavior(new ParrotMovementExecutor())
                .controllers(new ParrotFlightController(), new LookController(true, true))
                .routeFinder(new ParrotRouteFinder(this))
                .build();
    }

    /** Preserves remaining panic and shoulder delays when the destination uses a different tick counter. */
    @Override
    protected boolean switchLevel(Level targetLevel) {
        int tick = getLevel().getTick();
        int panicRemaining = Math.max(0, panicUntil - tick);
        int mountRemaining = Math.max(0, mountAfter - tick);
        if (!super.switchLevel(targetLevel)) return false;
        int destinationTick = getLevel().getTick();
        panicUntil = destinationTick + panicRemaining;
        mountAfter = destinationTick + mountRemaining;
        return true;
    }

    /** @return whether damage or fire currently makes this parrot flee */
    public boolean isPanicking() {
        return isOnFire() || getLevel().getTick() < panicUntil;
    }

    /** @return whether a playing jukebox currently makes this parrot dance */
    public boolean isDancing() {
        return getDataFlag(ActorFlags.DANCING);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source.getCause() == EntityDamageEvent.DamageCause.FALL) return false;
        boolean damaged = super.attack(source);
        if (damaged && !source.isCancelled()) {
            panicUntil = getLevel().getTick() + 100;
            setSitting(false);
            leaveShoulder();
        }
        return damaged;
    }

    /** Keeps an adopted parrot persistent before saving its new owner. */
    @Override
    public void onTameSuccess(Player player) {
        setPersistent(true);
        super.onTameSuccess(player);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (player == null) return false;
        if (super.onInteract(player, item, clickedPos)) return true;
        if (isTamed() && player.getName().equals(getOwnerName()) && !isTouchingWater() && !isRiding()) {
            setSitting(!isSitting());
            return true;
        }
        return false;
    }

    @Override
    public void updateMovement() {
        if (isRiding()) {
            motionX = 0;
            motionY = 0;
            motionZ = 0;
            return;
        }
        boolean wasOnGround = onGround;
        super.updateMovement();
        if (wasOnGround != onGround) broadcastMovement(false);
    }

    /** Sends the ground state used by the client's wing animation with each movement. */
    @Override
    public void addMovement(double x, double y, double z, double yaw, double pitch, double headYaw) {
        MoveActorAbsoluteData data = new MoveActorAbsoluteData();
        data.setActorRuntimeID(runtimeId());
        data.setOnGround(onGround);
        data.setPos(org.cloudburstmc.math.vector.Vector3f.from(x, y, z));
        data.setRotation(org.cloudburstmc.math.vector.Vector3f.from(pitch, yaw, headYaw));
        MoveActorAbsolutePacket packet = new MoveActorAbsolutePacket();
        packet.setMoveData(data);
        Server.broadcastPacket(getViewers().values(), packet);
    }

    /** Restores the shoulder offset after a newly observing client receives the riding link. */
    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
        if (isRiding() && getViewers().containsKey(player.getLoaderId())) sendData(player);
    }

    @Override
    protected void handleGravity() {
        if (isRiding()) return;
        super.handleGravity();
        if (!onGround && motionY < 0) motionY *= 0.6;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (isAlive() && !closed) {
            if (isRiding()) updateShoulder();
            if (getServer().getSettings().gameplaySettings().enableMobAi() && !isImmobile()) {
                if (isTouchingWater() || isPanicking()) setSitting(false);
                if (currentTick % 20 == 0) updateDancing();
                if (!isRiding() && !isSitting() && isPanicking()) teleportToOwner();
                if (currentTick % 10 == 0 && currentTick >= mountAfter) tryMountShoulder();
                if (!getDataFlag(ActorFlags.SILENT) && ThreadLocalRandom.current().nextInt(800) == 0) imitateNearbyMob();
            }
        }
        return super.onUpdate(currentTick);
    }

    private void teleportToOwner() {
        Player owner = getOwner();
        if (!isTamed() || owner == null || !owner.isOnline() || owner.getLevel() != level
                || distanceSquared(owner) <= 144 || isRiding()
                || getDataProperty(ActorDataTypes.LEASH_HOLDER, -1L) != -1L) return;
        var random = ThreadLocalRandom.current();
        for (int i = 0; i < 10; i++) {
            Vector3 target = owner.add(random.nextInt(-3, 4), random.nextInt(0, 3), random.nextInt(-3, 4));
            if (level.isChunkLoaded(target.getFloorX() >> 4, target.getFloorZ() >> 4)
                    && new ParrotPosEvaluator().evalPos(this, target)) {
                teleport(target);
                break;
            }
        }
    }

    /**
     * Tests whether a player can carry this parrot, including available seats.
     * @param player the prospective carrier
     * @return true when the parrot may approach and mount this player
     */
    public boolean canRideShoulder(@NotNull Player player) {
        return isTamed() && level.getTick() >= mountAfter && !isSitting() && !isPanicking() && !isTouchingWater()
                && getDataProperty(ActorDataTypes.LEASH_HOLDER, -1L) == -1L
                && player.isOnline() && player.isAlive() && player.getLevel() == level
                && !player.isSpectator() && !player.isSleeping() && !player.isSneaking()
                && !player.isTouchingWater() && !player.isGliding() && !player.isFlying()
                && !player.isRiding() && player.motionY >= -0.5
                && player.getPassengers().size() < 2;
    }

    private void tryMountShoulder() {
        if (isRiding() || isDancing() || !isTamed() || isSitting()) return;
        Player owner = getOwner();
        // Following the owner has priority over finding a mount.
        if (owner != null && owner.isOnline() && owner.getLevel() == level && distanceSquared(owner) >= 25) return;
        for (Player player : level.getPlayers().values()) {
            if (distanceSquared(player) < 4 && canRideShoulder(player) && player.mountEntity(this)) {
                carrierHealth = player.getHealthCurrent();
                setMotion(new Vector3());
                setMoveTarget(null);
                setLookTarget(null);
                return;
            }
        }
    }

    private void updateShoulder() {
        if (!(getRiding() instanceof Player player)) return;
        if (!player.isOnline() || !player.isAlive() || player.getLevel() != level
                || player.isSneaking() || player.isTouchingWater() || player.isSleeping()
                || player.isGliding() || player.isFlying() || player.motionY < -0.5
                || player.getHealthCurrent() < carrierHealth) {
            leaveShoulder();
        } else {
            carrierHealth = player.getHealthCurrent();
        }
    }

    private void leaveShoulder() {
        Entity carrier = getRiding();
        if (carrier != null) carrier.dismountEntity(this, true, false);
    }

    /** Restarts the shoulder search delay after the carrier has detached this parrot. */
    public void onShoulderDismounted() {
        mountAfter = getLevel().getTick() + 100;
    }

    /**
     * Receives a nearby jukebox playback change.
     * @param jukebox the source jukebox
     * @param playing whether its record has started playing
     */
    public void onRecordPlaying(@NotNull BlockEntityJukebox jukebox, boolean playing) {
        if (playing && !isRiding() && jukebox.getLevel() == level && distanceSquared(jukebox.add(0.5, 0.5, 0.5)) <= 9) {
            dancingJukebox = jukebox;
        } else if (!playing && dancingJukebox == jukebox) {
            dancingJukebox = null;
        }
        updateDancing();
    }

    private void updateDancing() {
        if (dancingJukebox != null && (isRiding() || dancingJukebox.closed || dancingJukebox.getLevel() != level
                || distanceSquared(dancingJukebox.add(0.5, 0.5, 0.5)) > 9 || !dancingJukebox.isPlaying())) dancingJukebox = null;
        setDataFlag(ActorFlags.DANCING, dancingJukebox != null && !isRiding() && !isPanicking());
    }

    void imitateNearbyMob() {
        if (getDataFlag(ActorFlags.SILENT)) return;
        EntityLiving selected = null;
        int count = 0;
        var random = ThreadLocalRandom.current();
        for (Entity entity : level.fastNearbyEntities(getBoundingBox().grow(20, 20, 20), this)) {
            if (entity instanceof EntityLiving living && !(entity instanceof Player)
                    && entity.isAlive() && !entity.closed && random.nextInt(++count) == 0) selected = living;
        }
        if (selected == null) return;
        SoundEvent sound = IMITATIONS.get(selected.getIdentifier());
        if (sound != null) level.addLevelSoundEvent(this, sound, -1, getIdentifier(), false, false);
    }

    /** Detaches the shoulder link after this parrot successfully leaves the loaded world. */
    @Override
    public void close() {
        super.close();
        if (closed) leaveShoulder();
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        return new Item[]{
                Item.get(Item.FEATHER, 0, Utils.rand(1, 2))
        };
    }
}
