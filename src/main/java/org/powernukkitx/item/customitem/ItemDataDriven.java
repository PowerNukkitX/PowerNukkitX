package org.powernukkitx.item.customitem;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityLiving;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.MovingObjectPosition;
import org.powernukkitx.level.particle.GenericParticle;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;

import org.cloudburstmc.protocol.bedrock.data.ParticleType;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * An item that exists only as data, with no class of its own.
 * <p>
 * Items loaded from a behavior pack have no dedicated Java class: everything about them
 * lives in the {@link CustomItemDefinition} built from the pack's JSON, which the
 * {@link Item} core already reads at runtime for food, durability, wearability, cooldowns
 * and the rest. One instance of this class therefore backs one pack item, and many
 * instances with different definitions back a whole pack.
 */
public class ItemDataDriven extends Item implements CustomItem {

    /**
     * @param id         the item identifier, which must match the definition's
     * @param definition the definition describing the item; null only for the short-lived
     *                   prototype used while the definition is still being built, since
     *                   the builder needs an instance to read the identifier from
     */
    public ItemDataDriven(@NotNull String id, @Nullable CustomItemDefinition definition) {
        this(id, definition, ServerComponents.NONE);
    }

    /**
     * @param serverComponents the behaviour only the server acts on, which is not sent to clients
     */
    public ItemDataDriven(@NotNull String id, @Nullable CustomItemDefinition definition, @NotNull ServerComponents serverComponents) {
        super(id, definition);
        this.serverComponents = serverComponents;
    }

    /**
     * The music an item plays in a jukebox, from {@code minecraft:record}.
     *
     * @param soundEvent       the sound played, such as {@code record.cat}
     * @param duration         how long the record plays, in seconds
     * @param comparatorSignal the signal a comparator reads from a jukebox holding the record, 1 to 13
     */
    public record Record(@NotNull String soundEvent, float duration, int comparatorSignal) {
    }

    /**
     * One threshold of {@code minecraft:durability_sensor}.
     *
     * @param durability the remaining durability at or below which the effects are emitted
     * @param particle   the particle emitted, or null for none
     * @param soundEvent the level sound event played, or null for none
     */
    public record DurabilityThreshold(int durability, @Nullable ParticleType particle, @Nullable SoundEvent soundEvent) {
    }

    /**
     * What an item stores, from {@code minecraft:storage_item} and {@code minecraft:storage_weight_limit}.
     *
     * @param maxSlots     how many different stacks it stores
     * @param maxWeight    the total weight its contents may reach
     * @param allowNested  whether it stores other storage items
     * @param allowedItems the only items it stores, or empty for any
     * @param bannedItems  the items it never stores
     */
    public record Storage(int maxSlots, int maxWeight, boolean allowNested,
                          @NotNull Set<String> allowedItems, @NotNull Set<String> bannedItems) {

        public Storage {
            allowedItems = Set.copyOf(allowedItems);
            bannedItems = Set.copyOf(bannedItems);
        }
    }

    /**
     * The reach of a {@code minecraft:piercing_weapon}, in blocks along the attacker's view.
     *
     * @param min           the closest distance a target is hit at
     * @param max           the farthest distance a target is hit at
     * @param creativeMin   {@code min} for attackers in creative mode
     * @param creativeMax   {@code max} for attackers in creative mode
     * @param hitboxMargin  how far each target's box is grown before the view is tested against it
     */
    public record PiercingWeapon(float min, float max, float creativeMin, float creativeMax, float hitboxMargin) {
    }

    /**
     * When one effect of a {@code minecraft:kinetic_weapon} applies.
     *
     * @param maxDuration      the ticks after the weapon's delay during which the effect applies, or a negative
     *                         value for as long as the weapon is used
     * @param minRelativeSpeed the least speed, in blocks per second along the view, at which the user closes in
     *                         on the target
     * @param minSpeed         the least speed, in blocks per second along the view, of the user
     */
    public record KineticConditions(int maxDuration, float minRelativeSpeed, float minSpeed) {

        boolean met(int activeTicks, double speed, double relativeSpeed) {
            return (maxDuration < 0 || activeTicks <= maxDuration) && speed >= minSpeed && relativeSpeed >= minRelativeSpeed;
        }
    }

    /**
     * A {@code minecraft:kinetic_weapon}, which hits what is in front of its user every tick while it is used.
     *
     * @param reach               where targets are hit along the view; see {@link PiercingWeapon}
     * @param delay               the ticks of use before any effect applies
     * @param damageMultiplier    multiplies the closing speed into damage
     * @param damageModifier      is added to the multiplied closing speed
     * @param damageConditions    when damage applies, or null for never
     * @param knockbackConditions when targets are knocked back, or null for never
     * @param dismountConditions  when targets are thrown off what they ride, or null for never
     */
    public record KineticWeapon(@NotNull PiercingWeapon reach, int delay, float damageMultiplier, float damageModifier,
                                @Nullable KineticConditions damageConditions,
                                @Nullable KineticConditions knockbackConditions,
                                @Nullable KineticConditions dismountConditions) {
    }

    /**
     * Behaviour pack components the server acts on and clients do not need.
     *
     * @param record               the record the item is, or null when it is not one
     * @param absorbableCauses     the damage causes the item absorbs into its durability while worn, from
     *                             {@code minecraft:damage_absorption}; {@code all} absorbs every cause
     * @param durabilityThresholds the thresholds of {@code minecraft:durability_sensor}, empty when the item
     *                             has none
     * @param plantAtFace          the face of the target block a {@code minecraft:seed} plants on, or null when
     *                             the item is not a seed
     * @param storage              what the item stores, or null when it stores nothing
     * @param storageWeight        the weight of one of this item inside a storage item, 0 when it may not be
     *                             stored, or -1 for the weight of its share of a stack of 64
     * @param piercingWeapon       the reach of its piercing attack, or null when it is not a piercing weapon
     * @param kineticWeapon        what it does every tick while used, or null when it is not a kinetic weapon
     */
    public record ServerComponents(@Nullable Record record, @NotNull Set<String> absorbableCauses,
                                   @NotNull List<DurabilityThreshold> durabilityThresholds,
                                   @Nullable BlockFace plantAtFace, @Nullable Storage storage, int storageWeight,
                                   @Nullable PiercingWeapon piercingWeapon, @Nullable KineticWeapon kineticWeapon) {

        public static final ServerComponents NONE = new ServerComponents(null, Set.of(), List.of(), null, null, -1, null, null);

        public ServerComponents {
            absorbableCauses = Set.copyOf(absorbableCauses);
            durabilityThresholds = List.copyOf(durabilityThresholds);
        }
    }

    private final ServerComponents serverComponents;

    /**
     * @return the record the item is, or null when it does not play in a jukebox
     */
    public @Nullable Record getRecord() {
        return serverComponents.record();
    }

    /**
     * @return whether the item, while worn, takes damage of this cause into its durability instead of the wearer
     */
    public boolean absorbs(@NotNull EntityDamageEvent.DamageCause cause) {
        Set<String> causes = serverComponents.absorbableCauses();
        return !causes.isEmpty() && (causes.contains("all") || causes.contains(cause.name().toLowerCase(Locale.ENGLISH)));
    }

    /**
     * @return the weight of one of this item inside a storage item, 0 when it may not be stored, or -1 for
     * the weight of its share of a stack of 64
     */
    public int getStorageWeight() {
        return serverComponents.storageWeight();
    }

    /**
     * @return the behaviour only the server acts on
     */
    protected @NotNull ServerComponents getServerComponents() {
        return serverComponents;
    }

    /**
     * @return whether the item takes colour in a cauldron of dyed water, from {@code minecraft:dyeable}
     */
    public boolean isDyeable() {
        CustomItemDefinition definition = getDefinition();
        return definition != null && definition.nbt().getCompound("components").contains("minecraft:dyeable");
    }

    /**
     * @return whether the item attacks as a {@code minecraft:piercing_weapon}
     */
    public boolean isPiercingWeapon() {
        return serverComponents.piercingWeapon() != null;
    }

    /**
     * Performs a piercing attack: every living entity whose box, grown by the hitbox margin, the player's
     * view crosses within reach is attacked for the item's damage. The first block in the way that blocks
     * sight cuts the reach short.
     * <p>
     * Call it on the thread that owns the player's level.
     *
     * @param player the attacking player, holding this item
     * @return how many entities were attacked; 0 when the item is not a piercing weapon
     */
    public int pierce(@NotNull Player player) {
        PiercingWeapon weapon = serverComponents.piercingWeapon();
        if (weapon == null) {
            return 0;
        }
        List<EntityLiving> targets = targetsInView(player, weapon);
        for (EntityLiving target : targets) {
            target.attack(new EntityDamageByEntityEvent(player, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK,
                    getAttackDamage(target)));
        }
        return targets.size();
    }

    /**
     * {@inheritDoc}
     * <p>
     * A {@code minecraft:kinetic_weapon} is used by holding it, so using it in the air starts that use.
     */
    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return serverComponents.kineticWeapon() != null || super.onClickAir(player, directionVector);
    }

    /**
     * {@inheritDoc}
     * <p>
     * A {@code minecraft:kinetic_weapon} hits the targets in its reach once its delay has passed. Speeds are
     * taken along the player's view in blocks per second: the player's own, from its last movement or from
     * what it rides, and the speed at which it closes in on each target. Damage is that closing speed times
     * the multiplier plus the modifier, rounded down, and only applies when it is positive.
     */
    @Override
    public void whileUsing(Player player) {
        KineticWeapon weapon = serverComponents.kineticWeapon();
        if (weapon == null) {
            return;
        }
        int activeTicks = player.getLevel().getTick() - player.getLastUseTick(getId()) - weapon.delay();
        if (activeTicks < 0) {
            return;
        }
        Vector3 view = player.getDirectionVector().normalize();
        Entity riding = player.getRiding();
        double speed;
        if (riding != null) {
            speed = riding.getMotion().dot(view) * 20;
        } else {
            speed = player.speed == null ? 0 : -player.speed.dot(view) * 20;
        }
        for (EntityLiving target : targetsInView(player, weapon.reach())) {
            double relativeSpeed = speed - target.getMotion().dot(view) * 20;
            KineticConditions damageConditions = weapon.damageConditions();
            if (damageConditions != null && damageConditions.met(activeTicks, speed, relativeSpeed)) {
                int damage = (int) Math.floor(relativeSpeed * weapon.damageMultiplier() + weapon.damageModifier());
                if (damage > 0) {
                    target.attack(new EntityDamageByEntityEvent(player, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage));
                }
            }
            KineticConditions knockbackConditions = weapon.knockbackConditions();
            if (knockbackConditions != null && knockbackConditions.met(activeTicks, speed, relativeSpeed)) {
                target.knockBack(player, 0, view.x, view.z);
            }
            KineticConditions dismountConditions = weapon.dismountConditions();
            if (dismountConditions != null && dismountConditions.met(activeTicks, speed, relativeSpeed)
                    && target.getRiding() != null) {
                target.getRiding().dismountEntity(target);
            }
        }
    }

    /**
     * @return the living entities whose box, grown by the hitbox margin, the player's view crosses within reach;
     * the first block in the way that blocks sight cuts the reach short
     */
    private static List<EntityLiving> targetsInView(Player player, PiercingWeapon weapon) {
        float min = player.isCreative() ? weapon.creativeMin() : weapon.min();
        float max = player.isCreative() ? weapon.creativeMax() : weapon.max();
        Level level = player.getLevel();
        Vector3 eye = player.getPosition().add(0, player.getEyeHeight(), 0);
        Vector3 end = eye.add(player.getDirectionVector().normalize().multiply(max));
        double reach = max;
        List<Block> blocks = level.raycastBlocks(eye, end, true, false, 0.25, false, false, true);
        if (!blocks.isEmpty() && level.blocksBlockSight(blocks.getLast(), false, false)) {
            AxisAlignedBB box = blocks.getLast().getBoundingBox();
            MovingObjectPosition hit = box == null ? null : box.calculateIntercept(eye, end);
            if (hit != null) {
                reach = eye.distance(hit.hitVector);
            }
        }
        List<EntityLiving> targets = new ArrayList<>();
        Entity riding = player.getRiding();
        for (Entity entity : level.getNearbyEntities(player.getBoundingBox().grow(max, max, max), player)) {
            if (entity == riding || !(entity instanceof EntityLiving living) || !living.isAlive()) {
                continue;
            }
            AxisAlignedBB box = entity.getBoundingBox().grow(weapon.hitboxMargin(), weapon.hitboxMargin(), weapon.hitboxMargin());
            double distance;
            if (box.isVectorInside(eye)) {
                distance = 0;
            } else {
                MovingObjectPosition hit = box.calculateIntercept(eye, end);
                if (hit == null) {
                    continue;
                }
                distance = eye.distance(hit.hitVector);
            }
            if (distance >= min && distance <= reach) {
                targets.add(living);
            }
        }
        return targets;
    }

    /**
     * @param face the face of the target block that was clicked
     * @return whether the item may place its block from that face; only a seed limits the face
     */
    public boolean canPlantAt(@NotNull BlockFace face) {
        BlockFace plantAtFace = serverComponents.plantAtFace();
        return plantAtFace == null || plantAtFace == face;
    }

    /**
     * Emits the effects of the {@code minecraft:durability_sensor} threshold this item's durability has just
     * fallen to. When the damage passes several thresholds, only the one with the lowest durability that is
     * still met counts, and a threshold already met before the damage emits nothing again.
     * <p>
     * Call it on the thread that owns the holder, after the item replaced {@code before} in the holder's inventory.
     *
     * @param before the stack this item replaced; nothing is emitted unless it is the same item
     * @param holder the entity holding or wearing the item, where the effects are emitted
     */
    public void senseDurability(@NotNull Item before, @NotNull Entity holder) {
        List<DurabilityThreshold> thresholds = serverComponents.durabilityThresholds();
        if (thresholds.isEmpty() || !getId().equals(before.getId()) || holder.getLevel() == null) {
            return;
        }
        int remainingBefore = getMaxDurability() - before.getDamage();
        int remaining = getMaxDurability() - getDamage();
        if (remaining >= remainingBefore) {
            return;
        }
        DurabilityThreshold met = null;
        for (DurabilityThreshold threshold : thresholds) {
            if (remaining <= threshold.durability() && (met == null || threshold.durability() < met.durability())) {
                met = threshold;
            }
        }
        if (met == null || remainingBefore <= met.durability()) {
            return;
        }
        if (met.particle() != null) {
            holder.getLevel().addParticle(new GenericParticle(holder.getPosition(), met.particle()));
        }
        if (met.soundEvent() != null) {
            holder.getLevel().addLevelSoundEvent(holder.getPosition(), met.soundEvent());
        }
    }

    /**
     * @return the definition this item was built with, or null for the prototype that
     * exists only while that definition is being built
     */
    @Override
    public @Nullable CustomItemDefinition getDefinition() {
        return definition() instanceof CustomItemDefinition custom ? custom : null;
    }

    @Override
    public ItemDataDriven clone() {
        return (ItemDataDriven) super.clone();
    }
}
