package cn.nukkit.entity.custom;

import cn.nukkit.entity.EntityFilter;
import cn.nukkit.entity.components.*;
import cn.nukkit.entity.custom.CustomEntityDefinition.Meta.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;


/**
 * CustomEntityDefinition defines custom entities from behavior packs.<p>
 *
 * Use {@link CustomEntityDefinition.SimpleBuilder} to declare all
 * properties and behaviors. The builder centralizes supported fields and
 * handles server side overrides automatically. <p>
 *
 * Override methods can be used for advanced or specialized logic not covered by the builder.
 */
@Slf4j
public record CustomEntityDefinition(String id, String eid, boolean hasSpawnEgg, boolean isSummonable) {

    public CustomEntityDefinition {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id is blank");
        if (eid == null) eid = "";
    }

    public static SimpleBuilder simpleBuilder(@NotNull String id) {
        return new SimpleBuilder(id);
    }

    /** Lightweight, immutable data definition. All gameplay knobs live in MetaStore below. */
    public static final class SimpleBuilder {
        private final String id;
        private String eid = "";
        private boolean hasSpawnEgg = true;
        private boolean isSummonable  = true;
        private final Meta meta = new Meta();

        public SimpleBuilder(@NotNull String id) {
            if (id == null || id.isBlank()) throw new IllegalArgumentException("id is blank");
            this.id = id;
        }

        public SimpleBuilder eid(String entityIdentifier) {
            if (entityIdentifier == null) entityIdentifier = "";
            this.eid = entityIdentifier;
            return this;
        }

        // --------Helpers component setters --------
        private SimpleBuilder withInt(String key, int value) {
            this.meta.put(key, Integer.valueOf(value));
            return this;
        }

        private SimpleBuilder withFloat(String key, float value) {
            if (!Float.isFinite(value)) value = 0.0f;
            this.meta.put(key, Float.valueOf(Math.max(0f, value)));
            return this;
        }

        private SimpleBuilder withBoolean(String key, boolean value) {
            this.meta.put(key, Boolean.valueOf(value));
            return this;
        }

        private SimpleBuilder withString(String key, String value) {
            this.meta.put(key, value == null ? "" : value);
            return this;
        }

        private SimpleBuilder withStringSet(String key, Set<String> values) {
            Set<String> safe = (values == null) ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet<>(values));
            this.meta.put(key, safe);
            return this;
        }

        private SimpleBuilder withObject(String key, Object value) {
            this.meta.put(key, value);
            return this;
        }

        private RideableComponent rideableBase() {
            RideableComponent base = this.meta.getRideableComponent(CustomEntityComponents.RIDEABLE);
            return base != null ? base : RideableComponent.defaults();
        }

        //////////////////////////////////////////////////////////////////
        // ---------------- Custom Entities Components ---------------- //
        //////////////////////////////////////////////////////////////////

        /**
         * Set if the entity will have a spawn egg on creative inventory.
         * @param hasSpawnEgg boolean true/false
         */
        public SimpleBuilder hasSpawnEgg(boolean hasSpawnEgg) {
            this.hasSpawnEgg = hasSpawnEgg;
            return this;
        }

        /**
         * Set if entity can be summoned by commands.
         * @param isSummonable boolean true/false
         */
        public SimpleBuilder isSummonable(boolean isSummonable) {
            this.isSummonable = isSummonable;
            return this;
        }

        /**
         * Set a default "pretty" name of the entity.
         * @param defaultName String value
         */
        public SimpleBuilder originalName(String defaultName) {
            return withString(CustomEntityComponents.ORIGINAL_NAME, defaultName);
        }

        /**
         * Sets the width and height of the Entity's collision box.
         * @param width Float value, width of the collision box in blocks. A negative value will be assumed to be 0.
         * @param height Float value, height of the collision box in blocks. A negative value will be assumed to be 0.
         */
        public SimpleBuilder collisionBox(float width, float height) {
            float a = width  < 0f ? 0f : width;
            float b = height < 0f ? 0f : height;
            return withObject(CustomEntityComponents.COLLISION_BOX, new CollisionBox(a, b));
        }

        /**
         * Defines a fixed max health value for the entity.
         * <p>
         * This stores a fixed health definition (no range).
         *
         * @param maxHealth max health value (>= 1)
         */
        public SimpleBuilder health(int maxHealth) {
            return withObject(
                    CustomEntityComponents.HEALTH,
                    new HealthComponent(maxHealth, null, null)
            );
        }

        /**
         * Defines a max health range for the entity.
         * <p>
         * When a range is defined, the final max health will be
         * resolved later (ex: genetics, breeding, spawn logic).
         * <p>
         * If {@code rangeMin == rangeMax}, the value is treated as fixed.
         *
         * <p><b>Example:</b></p>
         * <pre>{@code
         * .healthRange(20, 40)
         * }</pre>
         *
         * @param rangeMin minimum max health (inclusive, >= 1)
         * @param rangeMax maximum max health (inclusive, >= 1)
         */
        public SimpleBuilder healthRange(int rangeMin, int rangeMax) {
            Preconditions.checkArgument(rangeMax >= rangeMin, "rangeMax value must be higher or equal to min value.");
            return withObject(
                    CustomEntityComponents.HEALTH,
                    new HealthComponent(null, rangeMin, rangeMax)
            );
        }

        /**
         * Sets a fixed base movement speed (Bedrock: minecraft:movement.value).
         *
         * <p>This stores only the base movement speed in {@link MovementComponent}.
         *
         * @param moveSpeed base move speed (>= 0)
         */
        public SimpleBuilder movement(float moveSpeed) {
            return withObject(
                    CustomEntityComponents.MOVEMENT,
                    new MovementComponent(moveSpeed, null, null)
            );
        }

        /**
         * Defines a movement speed range for the entity.
         * <p>
         * When a range is defined, a random movement speed will be generated
         * within the provided range when the entity is first initialized.
         * <p>
         * If {@code rangeMin == rangeMax}, the movement speed becomes fixed.
         * <p>
         * If no explicit movement speed is set via {@link #movement(float, float)},
         * the range will be used to determine the base movement speed.
         *
         * <p><b>Example:</b></p>
         * <pre>{@code
         * .movementRange(0.1125f, 0.3375f)
         * }</pre>
         *
         * @param rangeMin minimum movement speed (inclusive)
         * @param rangeMax maximum movement speed (inclusive)
         * @return this builder for chaining
         */
        public SimpleBuilder movementRange(float rangeMin, float rangeMax) {
            Preconditions.checkArgument(rangeMax >= rangeMin, "rangeMax value must be higher or equal to min value.");
            return withObject(CustomEntityComponents.MOVEMENT, new MovementComponent(null, rangeMin, rangeMax));
        }

        /**
         * Defines the range, in blocks, that a mob will pursue a target.
         * @param radius Int value, the radius of the area blocks the entity will attempt to stay within around a target.
         * @param max Int value, the maximum distance the mob will go from a target.
         */
        public SimpleBuilder followRange(int radius, int max) {
            return withObject(CustomEntityComponents.FOLLOW_RANGE, new FollowRange(radius, max));
        }

        /**
         * Sets an entity's melee attack
         * @param damage Int value
         */
        public SimpleBuilder attack(int damage) {
            return attack(damage, damage);
        }

        /**
         * Sets an entity's melee attack, final value is a random between min and max.
         * @param min Int value
         * @param max Int value
         */
        public SimpleBuilder attack(int min, int max) {
            Preconditions.checkArgument(max >= min, "max value must be higher or equal to min value.");
            return withObject(CustomEntityComponents.ATTACK, new Attack(min, max));
        }

        /**
         * Compels an entity to resist being knocked backwards by a melee attack.
         * @param value Float value, percentage of knockback resistance that an entity has. Valid values 0.0f -> 100.0f
         */
        public SimpleBuilder knockbackResistance(float value) {
            float v = Math.min(100f, Math.max(0f, value));
            return withObject(CustomEntityComponents.KNOCKBACK_RESISTANCE, v);
        }

        /**
         * Set family types of the entity can be one or multiple strings
         * @param families Set of strings ex: .typeFamily("type1", "type2", "type3")
         */
        public SimpleBuilder typeFamily(String... families) {
            LinkedHashSet<String> out = new LinkedHashSet<>();
            if (families != null) {
                for (String v : families) {
                    if (v == null) continue;
                    String s = v.trim();
                    if (s.isEmpty()) continue;
                    out.add(s.toLowerCase());
                }
            }
            return withStringSet(CustomEntityComponents.TYPE_FAMILY, out);
        }

        /**
         * Entities with this component will have a maximum auto step height that is different depending <p>
         * on whether they are on a block that prevents jumping.
         * @param base Float value, the maximum auto step height when on any other block.
         * @param controlled Float value, the maximum auto step height when on any other block and controlled by the player.
         * @param jumpPrevented Float value, the maximum auto step height when on a block that prevents jumping.
         */
        public SimpleBuilder maxAutoStep(float base, float controlled, float jumpPrevented) {
            float a = base  < 0f ? 0f : base;
            float b = controlled < 0f ? 0f : controlled;
            float c = jumpPrevented < 0f ? 0f : jumpPrevented;
            return withObject(CustomEntityComponents.MAX_AUTO_STEP, new MaxAutoStep(a, b, c));
        }

        /**
         * Defines what can push an entity between other entities and pistons.
         * @param isPushable boolean value, whether the entity can be pushed by other entities.
         * @param isPushableByPiston boolean value, wehether the entity can be pushed by pistons safely.
         */
        public SimpleBuilder pushable(boolean isPushable, boolean isPushableByPiston) {
            return withObject(CustomEntityComponents.PUSHABLE, new Pushable(isPushable, isPushableByPiston));
        }

        /**
         * Defines physics properties of an actor, including if it is affected by gravity or if it collides with objects.
         * @param hasCollision boolean value, whether or not the object collides with things. (defaults to true)
         * @param hasGravity boolean value, whether or not the entity is affected by gravity. (defaults to true)
         * @param pushTowardsClosestSpace boolean value, whether or not the entity should be pushed towards the nearest open area when stuck inside a block. (defaults to true)
         */
        public SimpleBuilder physics(boolean hasCollision, boolean hasGravity, boolean pushTowardsClosestSpace) {
            return withObject(CustomEntityComponents.PHYSICS, new Physics(hasCollision, hasGravity, pushTowardsClosestSpace));
        }

        /**
         * Set if entity is persistent accroos chunk reload or server restart
         * @param value boolean value
         */
        public SimpleBuilder isPersistent(boolean value) {
            return withBoolean(CustomEntityComponents.PERSISTENT, value);
        }

        /**
         * Set if entity is fire immune
         * @param value boolean value
         */
        public SimpleBuilder isFireImmune(boolean value) {
            return withBoolean(CustomEntityComponents.FIRE_IMMUNE, value);
        }

        /**
         * Enables the {@code minecraft:rideable} component using default values.
         *
         * <p>This is the simplest way to make an entity rideable. A single seat
         * will be created automatically with default positioning.</p>
         *
         * <p>Defaults:</p>
         * <ul>
         *   <li>seat_count = 1</li>
         *   <li>controlling_seat = 0</li>
         *   <li>dismount_mode = default</li>
         * </ul>
         *
         * <p>Example:</p>
         * <pre>
         * .rideable(true)
         * </pre>
         *
         * @param enabled true to enable rideable behavior
         */
        public SimpleBuilder rideable(boolean enabled) {
            if (!enabled) return this;
            return rideable(RideableComponent.defaults());
        }

        /**
         * Defines a rideable component with common configuration.
         *
         * <p>This method allows specifying which entity families can ride
         * the entity and the seat positions.</p>
         *
         * <p>The first seat will automatically become the controlling seat.</p>
         *
         * <p>Example:</p>
         * <pre>
         * .rideable(
         *      Set.of("player"), // family_types — entities allowed to ride this entity
         *      List.of(
         *          new RideableComponent.Seat(
         *              0, // min_rider_count — minimum riders required for this seat
         *              1, // max_rider_count — maximum riders allowed in this seat
         *              new Vector3f(0f, 1.9f, 0f), // position — seat position relative to entity
         *              null, // lock_rider_rotation_degrees — restrict rider rotation (null = unlimited)
         *              null, // rotate_rider_by_degrees — rotation offset applied to the rider
         *              null, // third_person_camera_radius — camera distance when riding
         *              null  // camera_relax_distance_smoothing — camera smoothing factor
         *          )
         *      )
         * )
         * </pre>
         *
         * @param familyTypes entity family types allowed to ride this entity
         * @param seats seat definitions describing rider positions
         */
        public SimpleBuilder rideable(Set<String> familyTypes, List<RideableComponent.Seat> seats) {
            RideableComponent base = RideableComponent.defaults();

            return rideable(new RideableComponent(
                    base.controllingSeat(),
                    base.crouchingSkipInteract(),
                    base.dismountMode(),
                    familyTypes,
                    base.interactText(),
                    base.passengerMaxWidth(),
                    base.pullInEntities(),
                    base.riderCanInteract(),
                    seats == null ? base.seatCount() : Math.max(1, seats.size()),
                    seats
            ));
        }

        /**
         * Defines the full {@code minecraft:rideable} component.
         *
         * <p>This method allows configuring every property of the rideable
         * component, including seat counts, dismount mode, interaction text,
         * passenger restrictions, and detailed seat configuration.</p>
         *
         * <p>Use this when the simplified builder methods are not sufficient.</p>
         *
         * <p>Example:</p>
         * <pre>
         * .rideable(new RideableComponent(
         *      0, // controlling_seat — index of the seat that controls entity movement
         *      true, // crouching_skip_interact — crouching players cannot mount the entity
         *      RideableComponent.DismountMode.ON_TOP_CENTER, // where riders are placed when dismounting
         *      Set.of("player"), // family_types — entity families allowed to ride this entity
         *      "action.interact.ride.horse", // interact_text — localization key shown when interacting
         *      0.0f, // passenger_max_width — maximum width allowed for passengers (0 = ignore)
         *      false, // pull_in_entities — automatically mount entities matching family_types
         *      false, // rider_can_interact — riders can interact with the entity while mounted
         *      2, // seat_count — total number of seats available
         *      List.of(
         *          new RideableComponent.Seat(
         *              0, // min_rider_count — minimum riders required for this seat
         *              1, // max_rider_count — maximum riders allowed in this seat
         *              new Vector3f(0f, 1.9f, 0.5f), // position — seat position relative to entity
         *              null, // lock_rider_rotation_degrees — restrict rider rotation (null = unlimited)
         *              null, // rotate_rider_by_degrees — rotation offset applied to rider
         *              null, // third_person_camera_radius — camera distance while riding
         *              null  // camera_relax_distance_smoothing — camera smoothing factor
         *          ),
         *          new RideableComponent.Seat(
         *              0, // min_rider_count — minimum riders required for this seat
         *              1, // max_rider_count — maximum riders allowed in this seat
         *              new Vector3f(0f, 1.9f, 0f), // position — seat position relative to entity
         *              null, // lock_rider_rotation_degrees — restrict rider rotation (null = unlimited)
         *              null, // rotate_rider_by_degrees — rotation offset applied to the rider
         *              null, // third_person_camera_radius — camera distance when riding
         *              null  // camera_relax_distance_smoothing — camera smoothing factor
         *          )
         *      )
         * ))
         * </pre>
         *
         * @param rideable full rideable component definition
         */
        public SimpleBuilder rideable(@Nullable RideableComponent rideable) {
            if (rideable == null) return this;
            return withObject(CustomEntityComponents.RIDEABLE, rideable);
        }

        /**
         * If true, this entity supports saddling and will require a saddle to be considered "ride-ready".
         * This is a definition-time flag (what the entity supports), not the runtime state.
         *
         * If false, the entity will never save saddled state to NBT (because it doesn't use it).
         */
        public SimpleBuilder canBeSaddled(boolean value) {
            return withBoolean(CustomEntityComponents.PNX_CAN_BE_SADDLED, value);
        }

        /**
         * When rideable, enables ground-based WASD control when the entity is rideable
         * <p>Example:</p>
         * <pre>{@code
         * .rideableEnabled(true)
         * .inputGroundControlledEnabled(true)
         * }</pre>
         */
        public SimpleBuilder inputGroundControlledEnabled(boolean enabled) {
            if (!enabled) return this;
            return withObject(
                    CustomEntityComponents.INPUT_GROUND_CONTROLLED,
                    Meta.InputGroundControlled.ENABLED
            );
        }

        /**
         * When rideable, enables air-based WASD control when the entity is rideable
         * <p>Example:</p>
         * <pre>{@code
         * .rideableEnabled(true)
         * .inputAirControlled(true)
         * }</pre>
         */
        public SimpleBuilder inputAirControlled(boolean enabled) {
            if (!enabled) return this;
            return inputAirControlled(Meta.InputAirControlled.defaults());
        }

        public SimpleBuilder inputAirControlled(Meta.InputAirControlled air) {
            if (air == null) air = Meta.InputAirControlled.defaults();
            return withObject(CustomEntityComponents.INPUT_AIR_CONTROLLED, air);
        }

        /**
         * When rideable, enables air-based WASD control when the entity is rideable
         * <p>Example:</p>
         * <pre>{@code
         * .rideableEnabled(true)
         * .inputAirControlled(1.0f, 0.5f)
         * }</pre>
         */
        public SimpleBuilder inputAirControlled(float strafeSpeedModifier, float backwardsMovementModifier) {
            return inputAirControlled(
                    new Meta.InputAirControlled(strafeSpeedModifier, backwardsMovementModifier)
            );
        }

        /**
         * Enables dive/swimming movement control when the entity is rideable.
         *
         * <p>This method is provided as a convenience alias for aquatic entities
         * such as nautilus-like mobs.</p>
         *
         * <p>Internally, diving uses the same control system as
         * {@link #inputAirControlled(boolean)}.</p>
         *
         * <p>Example:</p>
         * <pre>{@code
         * .rideable(true)
         * .inputDiveControlled(true)
         * }</pre>
         *
         * @param enabled true to enable dive movement control
         */
        public SimpleBuilder inputDiveControlled(boolean enabled) {
            return inputAirControlled(enabled);
        }

        /**
         * Defines the {@code minecraft:horse.jump_strength} component using a fixed value.
         *
         * <p>This value controls the jump strength attribute used by rideable horse-type
         * entities such as donkeys or mules. A fixed value means every spawned entity
         * will have the same jump strength.</p>
         *
         * <p>If this method is never called, the attribute will not be injected
         * into the entity definition (recommended when the entity should not
         * have horse-style jump behavior).</p>
         *
         * <p>Example:</p>
         * <pre>{@code
         * .horseJumpStrength(
         *      0.5f // value — fixed jump strength for the entity
         * )
         * }</pre>
         *
         * @param value fixed jump strength value (must be greater than 0)
         */
        public SimpleBuilder horseJumpStrength(float value) {
            Preconditions.checkArgument(value > 0, "value value must be higher than 0.");
            return withObject(
                    CustomEntityComponents.HORSE_JUMP_STRENGTH,
                    new HorseJumpStrengthComponent(value, null, null)
            );
        }

        /**
         * Defines the {@code minecraft:horse.jump_strength} component using a value range.
         *
         * <p>This range allows the jump strength attribute to be randomized when the
         * entity spawns. Each entity instance will receive a value between
         * {@code rangeMin} and {@code rangeMax}.</p>
         *
         * <p>This behavior mirrors vanilla horses where each horse has slightly
         * different jump abilities.</p>
         *
         * <p>Example:</p>
         * <pre>{@code
         * .horseJumpStrength(
         *      0.4f, // rangeMin — minimum possible jump strength
         *      1.0f  // rangeMax — maximum possible jump strength
         * )
         * }</pre>
         *
         * @param rangeMin minimum possible jump strength (must be greater than 0)
         * @param rangeMax maximum possible jump strength (must be >= rangeMin)
         */
        public SimpleBuilder horseJumpStrength(float rangeMin, float rangeMax) {
            Preconditions.checkArgument(rangeMin > 0f, "rangeMin must be > 0.");
            Preconditions.checkArgument(rangeMax > 0f, "rangeMax must be > 0.");
            Preconditions.checkArgument(rangeMax >= rangeMin, "rangeMax must be >= rangeMin.");
            return withObject(
                    CustomEntityComponents.HORSE_JUMP_STRENGTH,
                    new HorseJumpStrengthComponent(null, rangeMin, rangeMax)
            );
        }

        /**
         * Enables dash action behavior on the entity. <p>
         * Dash applies an instantaneous momentum boost with an optional cooldown. </p>
         *
         * <p>Example (Camel-like dash):</p>
         * <pre>{@code
         * .dashAction(2.75f, 20.0f, 0.6f)
         * }</pre>
         *
         * @param cooldownTimeSeconds Dash cooldown in seconds.
         * @param horizontalMomentum  Horizontal momentum applied when dashing.
         * @param verticalMomentum    Vertical momentum applied when dashing.
         */
        public SimpleBuilder dashAction(float cooldownTimeSeconds, float horizontalMomentum, float verticalMomentum) {
            return withObject(
                    CustomEntityComponents.DASH_ACTION,
                    new DashActionComponent(false, cooldownTimeSeconds, DashActionComponent.Direction.ENTITY, horizontalMomentum, verticalMomentum)
            );
        }

        /**
         * Configures dash action behavior. <p>
         * All parameters are optional; any value left unset will fall back to Bedrock default behavior. </p>
         *
         * <p>Example:</p>
         * <pre>{@code
         * .dashAction(true, 2.0f, Direction.PASSENGER, 154.0f, 0.1f)
         * }</pre>
         *
         * @param canDashUnderwater Whether dash is allowed underwater.
         * @param cooldownTimeSeconds Dash cooldown in seconds.
         * @param direction Direction used to apply dash momentum.
         * @param horizontalMomentum Horizontal momentum applied.
         * @param verticalMomentum Vertical momentum applied.
         */
        public SimpleBuilder dashAction(
                @Nullable Boolean canDashUnderwater,
                @Nullable Float cooldownTimeSeconds,
                @Nullable DashActionComponent.Direction direction,
                @Nullable Float horizontalMomentum,
                @Nullable Float verticalMomentum) {
            return withObject(
                    CustomEntityComponents.DASH_ACTION,
                    new DashActionComponent(canDashUnderwater, cooldownTimeSeconds, direction, horizontalMomentum, verticalMomentum)
            );
        }

        /**
         * Configures boost behavior for rideable entities. <p>
         * All parameters are optional; any value left unset will fall back to Bedrock default behavior. </p>
         *
         * <p>Example (Pig):</p>
         * <pre>{@code
         * .boostable(
         *     1.35f,
         *     3.0f,
         *     List.of(
         *         new BoostableComponent.BoostItem("carrotOnAStick", 2, "fishing_rod")
         *     )
         * )
         * }</pre>
         *
         * <p>Example (Strider):</p>
         * <pre>{@code
         * .boostable(
         *     1.35f,
         *     16.0f,
         *     List.of(
         *         new BoostableComponent.BoostItem("warped_fungus_on_a_stick", 1, "fishing_rod")
         *     )
         * )
         * }</pre>
         *
         * @param speedMultiplier Multiplier applied to normal movement speed during boost.
         * @param durationSeconds Duration of the boost in seconds.
         * @param boostItems Items that can trigger boost while riding.
         */
        public SimpleBuilder boostable(
                @Nullable Float speedMultiplier,
                @Nullable Float durationSeconds,
                @Nullable List<BoostableComponent.BoostItem> boostItems) {
            return withObject(
                    CustomEntityComponents.BOOSTABLE,
                    new BoostableComponent(speedMultiplier, durationSeconds, boostItems)
            );
        }

        /**
         * Defines inventory properties for this entity (Bedrock minecraft:inventory component).
         *
         * <p>This configures container type, size and access rules. Strength-based expansion
         * and special behavior (like llama) are handled separately.</p>
         *
         * <p>Example:</p>
         * <pre>{@code
         * .inventory(InventoryComponent.ContainerType.HORSE, 16)
         * }</pre>
         *
         * @param containerType Type of container (horse, container, inventory, etc).
         * @param inventorySize Total container size.
         */
        public SimpleBuilder inventory(InventoryComponent.Type containerType, int inventorySize) {
            return withObject(
                    CustomEntityComponents.INVENTORY,
                    new InventoryComponent(
                            null,
                            null,
                            containerType,
                            inventorySize,
                            null,
                            null
                    )
            );
        }

        /**
         * Adds the "minecraft:item_controllable" component.
         *
         * Behavior pack format:
         * "minecraft:item_controllable": { "control_items": "carrotOnAStick" }
         *
         * @param controlItemIdentifier string identifier (ex: "carrotOnAStick" or "minecraft:carrot_on_a_stick")
         */
        public SimpleBuilder itemControllable(@Nullable String controlItemIdentifier) {
            if (controlItemIdentifier == null) return this;
            String v = controlItemIdentifier.trim();
            if (v.isEmpty()) return this;
            

            return withObject(CustomEntityComponents.ITEM_CONTROLLABLE, new Meta.ItemControllable(v));
        }

        /**
         * Enables the equippable component with defaults (empty slots list).
         * <p>
         * If {@code enabled} is false, this is a no-op and the component will not be stored.
         *
         * <p><b>Example:</b></p>
         * <pre>{@code
         * CustomEntityDefinition.simpleBuilder("my:custom_entity")
         *     .equippable(true)
         *     .build();
         * }</pre>
         *
         * @param enabled true to enable the component (false = no-op)
         */
        public SimpleBuilder equippable(boolean enabled) {
            if (!enabled) return this;
            return withObject(CustomEntityComponents.EQUIPPABLE, EquippableComponent.defaults());
        }

        /**
         * Defines the equippable component with the given slots.
         * <p>
         * This stores the component in MetaStore. If {@code slots} is null or empty, the component is still stored,
         * but it will have no slots.
         *
         * <p><b>Example (full component setup):</b></p>
         * <pre>{@code
         * CustomEntityDefinition.simpleBuilder("my:custom_entity")
         *     .equippable(List.of(
         *         new EquippableComponent.Slot(
         *             0, // Slot index
         *             "minecraft:saddle", // item ex: saddle
         *             Set.of("minecraft:chest", "minecraft:barrel"), // list of accepted items
         *             "action.interact.saddle" // interaction text
         *         ),
         *         new EquippableComponent.Slot(
         *             1, // Slot index
         *             "minecraft:horsearmoriron", // item ex: horse armors
         *             Set.of("minecraft:saddle"), // list of accepted items ex other armors
         *             null // interaction text
         *         )
         *     ))
         *     .build();
         * }</pre>
         *
         * @param slots list of slot definitions (null -> empty)
         */
        public SimpleBuilder equippable(List<EquippableComponent.Slot> slots) {
            return withObject(CustomEntityComponents.EQUIPPABLE, new EquippableComponent(slots));
        }

        /**
         * Defines home restriction behavior for this entity <p>
         * This component restricts entity movement relative to a home position. <p>
         * It can be enabled explicitly or inferred when any restriction property is set. <p>
         *
         * <p>Example:</p>
         * <pre>{@code
         * .home(
         *     Set.of("minecraft:sand"),
         *     16,
         *     Home.RestrictionType.RANDOM_MOVEMENT
         * )
         * }</pre>
         *
         * @param homeBlockList Optional list of blocks considered valid as home.
         * @param restrictionRadius Radius around home the entity is restricted to.
         * @param restrictionType Type of movement restriction.
         */
        public SimpleBuilder home(Set<String> homeBlockList, Integer restrictionRadius, HomeComponent.RestrictionType restrictionType) {
            return withObject(
                    CustomEntityComponents.HOME,
                    new HomeComponent(true, homeBlockList, restrictionRadius, restrictionType)
            );
        }

        /**
         * Defines home restriction behavior for this entity <p>
         * This component restricts entity movement relative to a home position. <p>
         * It can be enabled explicitly or inferred when any restriction property is set. <p>
         *
         * <p>Example:</p>
         * <pre>{@code
         * .home(
         *     true,
         *     Set.of("minecraft:sand"),
         *     16,
         *     HomeComponent.RestrictionType.RANDOM_MOVEMENT
         * )
         * }</pre>
         *
         * @param enabled Whether the home component is explicitly enabled.
         * @param homeBlockList Optional list of blocks considered valid as home.
         * @param restrictionRadius Radius around home the entity is restricted to.
         * @param restrictionType Type of movement restriction.
         */
        public SimpleBuilder home(boolean enabled, Set<String> homeBlockList, Integer restrictionRadius, HomeComponent.RestrictionType restrictionType) {
            return withObject(
                    CustomEntityComponents.HOME,
                    new HomeComponent(enabled, homeBlockList, restrictionRadius, restrictionType)
            );
        }

        /**
         * Configures basic breeding behavior. <p>
         * Defines tame requirement, compatible mates, and breeding items. </p>
         *
         * <p>Example:</p>
         * <pre>{@code
         * .breedableBasic(
         *     false,
         *     List.of(new BreedableComponent.BreedsWith("minecraft:cow", null)),
         *     Set.of("minecraft:wheat")
         * )
         * }</pre>
         *
         * @param requireTame Whether the entity must be tamed before breeding.
         * @param breedsWith List of compatible mate entity types.
         * @param breedItems Items that trigger love state.
         */
        public SimpleBuilder breedable(
                @Nullable Boolean requireTame,
                @Nullable List<BreedableComponent.BreedsWith> breedsWith,
                @Nullable Set<String> breedItems) {

            return withObject(
                    CustomEntityComponents.BREEDABLE,
                    new BreedableComponent(
                            null,
                            null,
                            null,
                            null,
                            breedItems,
                            breedsWith,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            requireTame,
                            null
                    )
            );
        }

        /**
         * Configures breeding behavior with attribute blending support. <p>
         * Defines tame requirement, compatible mates, breeding items,
         * and which attributes should be blended in the offspring. </p>
         *
         * <p>Example:</p>
         * <pre>{@code
         * .breedable(
         *     false,
         *     BreedableComponent.blendAttributesOf(
         *         "minecraft:health",
         *         "minecraft:movement",
         *         "minecraft:horse.jump_strength"
         *     ),
         *     List.of(new BreedableComponent.BreedsWith("minecraft:horse", null)),
         *     Set.of("golden_apple")
         * )
         * }</pre>
         *
         * <p>Example (Attribute ids):</p>
         * <pre>{@code
         * .breedable(
         *     false,
         *     BreedableComponent.blendAttributesOf(
         *         cn.nukkit.entity.Attribute.MAX_HEALTH,
         *         cn.nukkit.entity.Attribute.MOVEMENT_SPEED,
         *         cn.nukkit.entity.Attribute.HORSE_JUMP_STRENGTH
         *     ),
         *     List.of(new BreedableComponent.BreedsWith("minecraft:horse", null)),
         *     Set.of("golden_apple")
         * )
         * }</pre>
         *
         * @param requireTame Whether the entity must be tamed before breeding.
         * @param blendAttributes Attribute ids to blend in offspring.
         * @param breedsWith List of compatible mate entity types.
         * @param breedItems Items that trigger love state.
         */
        public SimpleBuilder breedable(
                @Nullable Boolean requireTame,
                @Nullable List<String> blendAttributes,
                @Nullable List<BreedableComponent.BreedsWith> breedsWith,
                @Nullable Set<String> breedItems) {

            return withObject(
                    CustomEntityComponents.BREEDABLE,
                    new BreedableComponent(
                            null,
                            null,
                            blendAttributes,
                            null,
                            breedItems,
                            breedsWith,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            requireTame,
                            null
                    )
            );
        }


        /**
         * Fully configures the minecraft:breedable component. <p>
         * All parameters are optional. Unset values fall back to Bedrock defaults. </p>
         *
         * <p>Example:</p>
         * <pre>{@code
         * .breedable(
         *     null, // global filters
         *     false,
         *     BreedableComponent.blendAttributesOf(
         *         "minecraft:health",
         *         "minecraft:movement",
         *         "minecraft:horse.jump_strength"
         *     ),
         *     60f,
         *     Set.of("wheat"),
         *     List.of(new BreedableComponent.BreedsWith("minecraft:cow", null)),
         *     false,
         *     null,
         *     null,
         *     0.05f,
         *     true,
         *     EntityFilter.builder()
         *         .all(
         *             EntityFilter.isTamed(),
         *             EntityFilter.isOnGround()
         *         )
         *         .build(),
         *     null,
         *     false,
         *     true
         * )
         * }</pre>
         */
        public SimpleBuilder breedable(
                @Nullable EntityFilter loveFilters,
                @Nullable Boolean allowSitting,
                @Nullable List<String> blendAttributes,
                @Nullable Float breedCooldown,
                @Nullable Set<String> breedItems,
                @Nullable List<BreedableComponent.BreedsWith> breedsWith,
                @Nullable Boolean causesPregnancy,
                @Nullable BreedableComponent.DenyParentsVariant denyParentsVariant,
                @Nullable List<BreedableComponent.EnvironmentRequirement> environmentRequirements,
                @Nullable Float extraBabyChance,
                @Nullable Boolean inheritTamed,
                @Nullable BreedableComponent.MutationFactor mutationFactor,
                @Nullable Boolean combineParentColors,
                @Nullable Boolean requireFullHealth,
                @Nullable Boolean requireTame,
                @Nullable Set<String> propertyInheritance) {

            return withObject(
                    CustomEntityComponents.BREEDABLE,
                    new BreedableComponent(
                            loveFilters,
                            allowSitting,
                            blendAttributes,
                            breedCooldown,
                            breedItems,
                            breedsWith,
                            causesPregnancy,
                            denyParentsVariant,
                            environmentRequirements,
                            extraBabyChance,
                            inheritTamed,
                            mutationFactor,
                            combineParentColors,
                            requireFullHealth,
                            requireTame,
                            propertyInheritance
                    )
            );
        }

        /**
         * Enables minecraft:nameable using Bedrock defaults. <p>
         * Defaults:
         * allow_name_tag_renaming = true
         * always_show = false </p>
         *
         * <p>Example:</p>
         * <pre>{@code
         * .nameable()
         * }</pre>
         */
        public SimpleBuilder nameable() {
            return withObject(CustomEntityComponents.NAMEABLE, NameableComponent.defaults());
        }

        /**
         * Configures basic naming behavior for this entity. <p>
         * Controls whether the entity can be renamed using name tags
         * and whether the custom name is always visible. </p>
         *
         * <p>Bedrock component: minecraft:nameable</p>
         *
         * <p>Example:</p>
         * <pre>{@code
         * .nameable(
         *     true,   // allow name tag renaming
         *     false   // always show name
         * )
         * }</pre>
         *
         * @param allowNameTagRenaming If true, the entity can be renamed using name tags (default: true)
         * @param alwaysShow If true, the entity's custom name is always visible (default: false)
         */
        public SimpleBuilder nameable(
                @Nullable Boolean allowNameTagRenaming,
                @Nullable Boolean alwaysShow) {

            return withObject(CustomEntityComponents.NAMEABLE, new NameableComponent(allowNameTagRenaming, alwaysShow));
        }

        /**
         * Enables minecraft:healable list of items. <p>
         * <p>Bedrock component: minecraft:healable</p>
         *
         * <p>Example:</p>
         * <pre>{@code
         * .healable(
         *     List.of(
         *         new HealableComponent.Item("minecraft:wheat", 2, null),
         *         new HealableComponent.Item(
         *             "minecraft:hay_block",
         *             10,
         *             List.of(
         *                 new HealableComponent.Effect(
         *                     "fatal_poison", 1.0f, 1000.0f, 0.0f
         *                 )
         *             )
         *         )
         *     )
         * )
         * }</pre>
         *
         * @param items List of healable item definitions
         */
        public SimpleBuilder healable(List<HealableComponent.Item> items) {
            return withObject(
                    CustomEntityComponents.HEALABLE,
                    new HealableComponent(null, null, items)
            );
        }

        /**
         * Configures minecraft:healable behavior for this entity. <p>
         * Allows defining filter conditions, force-use behavior,
         * and the list of items that can heal the entity. </p>
         *
         * <p>Bedrock component: minecraft:healable</p>
         *
         * <p>Example:</p>
         * <pre>{@code
         * .healable(
         *     EntityFilter.all(
         *         (self, ctx) -> !self.isRiding()
         *     ),
         *     true, // force use
         *     List.of(
         *         new HealableComponent.Item(
         *             "minecraft:cookie",
         *             0,
         *             List.of(
         *                 new HealableComponent.Effect("fatal_poison", 1.0f, 1000.0f, 0.0f)
         *             )
         *         )
         *     )
         * )
         * }</pre>
         *
         * @param filters EntityFilter gate logic (nullable)
         * @param forceUse If true, item can be used even at full health (default: false)
         * @param items List of healable item definitions
         */
        public SimpleBuilder healable(@Nullable EntityFilter filters, @Nullable Boolean forceUse, List<HealableComponent.Item> items) {

            return withObject(
                    CustomEntityComponents.HEALABLE,
                    new HealableComponent(filters, forceUse, items)
            );
        }

        /**
         * Configures tameable behavior for this entity. <p>
         * Defines the taming probability per item use and the list
         * of items that can tame the entity. </p>
         *
         * <p>Example:</p>
         * <pre>{@code
         * .tameable(
         *     0.33f, // 33% chance per use
         *     Set.of(
         *         "minecraft:wheat_seeds",
         *         "minecraft:pumpkin_seeds",
         *         "minecraft:melon_seeds"
         *     )
         * )
         * }</pre>
         *
         * @param probability Chance of taming per item use (0.0–1.0). Default: 1.0
         * @param tameItems Set of item identifiers that can tame the entity (nullable)
         */
        public SimpleBuilder tameable(@Nullable Float probability, @Nullable Set<String> tameItems) {

            return withObject(
                    CustomEntityComponents.TAMEABLE,
                    new TameableComponent(probability, tameItems)
            );
        }

        /**
         * Enables ageable with basic configuration. <p>
         * Defines the growth duration and the list of items that can
         * accelerate aging. </p>
         *
         * <p>Example:</p>
         * <pre>{@code
         * .ageable(
         *     1200f, // grows up after 1200 seconds
         *     List.of(
         *         new AgeableComponent.FeedItem("minecraft:wheat", 0.016667f),
         *         new AgeableComponent.FeedItem("minecraft:beetroot_seeds", 0.016667f)
         *     )
         * )
         * }</pre>
         *
         * @param duration Time in seconds before the entity grows up (-1 to always stay a baby, default: 1200)
         * @param feedItems Feed items with optional growth percentage (nullable growth uses default boost logic)
         */
        public SimpleBuilder ageable(float duration, List<AgeableComponent.FeedItem> feedItems) {

            return withObject(
                    CustomEntityComponents.AGEABLE,
                    new AgeableComponent(
                            null,
                            duration,
                            feedItems,
                            null,
                            null,
                            null
                    )
            );
        }


        /**
         * Configures full ageable behavior for this entity. <p>
         * Allows defining interaction filters, growth duration,
         * feed items (with per-item growth percentage), pause/reset growth items,
         * and grow-up drop items. </p>
         *
         * <p>Example:</p>
         * <pre>{@code
         * .ageable(
         *     EntityFilter.all(
         *         (self, ctx) -> self.isBaby()
         *     ),
         *     1200f,
         *     List.of(
         *         new AgeableComponent.FeedItem("minecraft:wheat", 0.016667f),
         *         new AgeableComponent.FeedItem("minecraft:hay_block", 0.15f)
         *     ),
         *     Set.of("minecraft:golden_dandelion"),
         *     Set.of("minecraft:golden_dandelion"),
         *     Set.of("minecraft:feather")
         * )
         * }</pre>
         *
         * @param filters EntityFilter gate logic (nullable)
         * @param duration Time in seconds before the entity grows up (-1 to always stay a baby, default: 1200)
         * @param feedItems Feed items with optional growth percentage (nullable)
         * @param pauseGrowthItems Items that pause growth (nullable)
         * @param resetGrowthItems Items that reset growth (nullable)
         * @param dropItems Items dropped when the entity grows up (nullable)
         */
        public SimpleBuilder ageable(
                @Nullable EntityFilter filters,
                @Nullable Float duration,
                @Nullable List<AgeableComponent.FeedItem> feedItems,
                @Nullable Set<String> pauseGrowthItems,
                @Nullable Set<String> resetGrowthItems,
                @Nullable Set<String> dropItems) {

            return withObject(
                    CustomEntityComponents.AGEABLE,
                    new AgeableComponent(
                            filters,
                            duration,
                            feedItems,
                            pauseGrowthItems,
                            resetGrowthItems,
                            dropItems
                    )
            );
        }



        /////////////////////////////////////////////////
        // Deprecated setters, for removal
        /////////////////////////////////////////////////

        /**
         * @deprecated Use {@link #rideable(boolean)} instead.
         *
         * <p>
         * This method is kept for backward compatibility only.
         * Ride control configuration is now handled through the
         * {@link #rideable(boolean)} builder method.
         * </p>
         *
         * <p>
         * Calling this method simply delegates to {@link #rideable(boolean)}.
         * </p>
         *
         * Planned removal: after 6 months (>= 2026-09-05).
         */
        @Deprecated(since = "2.0.0", forRemoval = true)
        public SimpleBuilder isRideControllable(boolean value) {
            return rideable(value);
        }


        /**
         * @deprecated Use {@link #health(int)} or {@link #healthRange(int, int)} instead.
         *
         * <p>
         * This method is kept for backward compatibility only.
         * Health configuration has been consolidated into the
         * {@link #health(int)} and {@link #healthRange(int, int)} builder methods.
         * </p>
         *
         * <p>
         * Calling this method simply delegates to {@link #health(int)}.
         * </p>
         *
         * Planned removal: after 6 months (>= 2026-09-05).
         */
        @Deprecated(since = "2.0.0", forRemoval = true)
        public SimpleBuilder maxHealth(int maxHealth) {
            return health(maxHealth);
        }

        /**
         * @deprecated Use {@link #rideable(boolean)} instead.
         *
         * <p>
         * This method is kept for backward compatibility only.
         * Previous implementations stored a boolean value under the
         * {@code minecraft:rideable} component key, which could overwrite
         * the real {@link RideableComponent} created by the builder.
         * </p>
         *
         * <p>
         * The method now safely delegates to {@link #rideable(boolean)}.
         * </p>
         *
         * Planned removal: after 6 months (>= 2026-09-05).
         */
        @Deprecated(since = "2.0.0", forRemoval = true)
        public SimpleBuilder isRideable(boolean value) {
            return rideable(value);
        }

        /**
         * @deprecated Use {@link #movement(float)} or {@link #movementRange(float, float)} instead.
         *
         * <p>
         * This method is kept for backward compatibility only.
         * Bedrock does not define movement multipliers inside the
         * {@code minecraft:movement} component. Movement scaling is
         * behavior-driven and applied dynamically by executors.
         * </p>
         *
         * <p>
         * This method exists temporarily for legacy custom entities that still
         * rely on definition-time movement multipliers.
         * </p>
         *
         * Planned removal: after behavior parity is complete (>= 2026-09-05).
         */
        @Deprecated(since = "2.0.0", forRemoval = true)
        public SimpleBuilder movement(float moveSpeed, float multiplier) {
            movement(moveSpeed);
            return defaultMovementMultiplier(multiplier);
        }

        /**
         * @deprecated Movement multipliers should be implemented in behavior executors.
         *
         * <p>
         * This method is kept for backward compatibility only.
         * Bedrock entity definitions do not store movement multipliers;
         * speed scaling is controlled by runtime behaviors such as
         * follow, tempt, boost, or rider input.
         * </p>
         *
         * <p>
         * This field is currently used as a server-side tuning knob for
         * legacy entities and will be removed once all movement behaviors
         * implement proper runtime multipliers.
         * </p>
         *
         * Planned removal: after behavior parity is complete (>= 2026-09-05).
         */
        @Deprecated(since = "2.0.0", forRemoval = true)
        public SimpleBuilder defaultMovementMultiplier(float speedMultiplier) {
            if (!Float.isFinite(speedMultiplier)) speedMultiplier = 1.0f;
            speedMultiplier = Math.max(0f, speedMultiplier);
            return withObject(CustomEntityComponents.DEFAULT_MOVEMENT_MULTIPLIER, speedMultiplier);
        }
        /////////////////////////////////////////////////
        /////////////////////////////////////////////////





        public CustomEntityDefinition build() {
            MetaStore.put(this.id, this.meta.copy());
            return new CustomEntityDefinition(this.id, this.eid, this.hasSpawnEgg, this.isSummonable);
        }
    }


    /** Entity cached config (defaults). */
    public static final class Meta {
        private final ConcurrentHashMap<String, Object> components = new ConcurrentHashMap<>();

        void put(String key, Object value) {
            if (key != null && !key.isBlank()) components.put(key, value);
        }

        public boolean has(String key) { return components.containsKey(key); }
        public Object getRaw(String key) { return components.get(key); }

        public int getInt(String key, int def) {
            Object o = components.get(key);
            return (o instanceof Number) ? ((Number) o).intValue() : def;
        }

        public float getFloat(String key, float def) {
            Object o = components.get(key);
            if (o instanceof Number) {
                float v = ((Number) o).floatValue();
                return Float.isFinite(v) ? v : def;
            }
            return def;
        }

        public boolean getBoolean(String key, boolean def) {
            Object o = components.get(key);
            return (o instanceof Boolean) ? (Boolean) o : def;
        }

        @SuppressWarnings("unchecked")
        public Set<String> getStringSet(String key) {
            Object o = components.get(key);
            if (o instanceof Set) return (Set<String>) o;
            return Collections.emptySet();
        }

        public String getString(String key, String def) {
            Object o = components.get(key);
            return (o instanceof String) ? (String) o : def;
        }

        public Map<String, Object> view() {
            return Map.copyOf(components);
        }

        public Meta copy() {
            Meta m = new Meta();
            m.components.putAll(this.components);
            return m;
        }


        // Follow Range Meta
        public static record FollowRange(int radius, int max) {
            public FollowRange {
                radius = Math.max(0, radius);
                max = Math.max(0, max);
            }
            public int value() { return radius; }
            public int max() { return max; }
        }

        public FollowRange getFollowRange(String key) {
            Object obj = components.get(key);
            return (obj instanceof FollowRange data) ? data : new FollowRange(0, 0);
        }


        // Collision-box Meta
        public static record CollisionBox(float width, float height) {
            public CollisionBox {
                float a = Float.isFinite(width) ? Math.max(0f, width) : 0f;
                float b = Float.isFinite(height) ? Math.max(0f, height) : 0f;
                width = a;
                height = b;
            }
            public float width() { return width; }
            public float height() { return height; }
        }

        public CollisionBox getCollisionBox(String key) {
            Object obj = components.get(key);
            return (obj instanceof CollisionBox data) ? data : new CollisionBox(1.0f, 1.0f);
        }


        // Attack Meta
        public static record Attack(int min, int max) {
            public Attack {
                min = Math.max(0, min);
                max = Math.max(0, max);
            }
            public int value() { return min; }
            public int max() { return max; }
        }

        public Attack getAttack(String key) {
            Object obj = components.get(key);
            return (obj instanceof Attack data) ? data : new Attack(1, 1);
        }


        // Auto-step Meta
        public static record MaxAutoStep(float base, float controlled, float jumpPrevented) {
            public MaxAutoStep {
                float a = Float.isFinite(base) ? Math.max(0f, base) : 0f;
                float b = Float.isFinite(controlled) ? Math.max(0f, controlled) : 0f;
                float c = Float.isFinite(jumpPrevented) ? Math.max(0f, jumpPrevented) : 0f;
                base = a;
                controlled = b;
                jumpPrevented = c;
            }
            public float base() { return base; }
            public float controlled() { return controlled; }
            public float jumpPrevented() { return jumpPrevented; }
        }

        public MaxAutoStep getMaxAutoStep(String key) {
            Object obj = components.get(key);
            return (obj instanceof MaxAutoStep data) ? data : new MaxAutoStep(0.5625f, 0.5625f, 0.5625f);
        }


        // Pushable Meta
        public static record Pushable(boolean isPushable, boolean isPushableByPiston) {
            public boolean isPushable() { return isPushable; }
            public boolean isPushableByPiston() { return isPushableByPiston; }
        }

        public Pushable getPushable(String key) {
            Object obj = components.get(key);
            return (obj instanceof Pushable data) ? data : new Pushable(true, true);
        }


        // Physics Meta
        public static record Physics(boolean hasCollision, boolean hasGravity, boolean pushTowardsClosestSpace) {
            public boolean hasCollision() { return hasCollision; }
            public boolean hasGravity() { return hasGravity; }
            public boolean pushTowardsClosestSpace() { return pushTowardsClosestSpace; } // TODO: implement on core
        }

        public Physics getPhysics(String key) {
            Object obj = components.get(key);
            return (obj instanceof Physics data) ? data : new Physics(true, true, true);
        }


        // Input Ground Controlled Meta (no fields)
        public static record InputGroundControlled() {
            public static final InputGroundControlled ENABLED = new InputGroundControlled();
        }

        public InputGroundControlled getInputGroundControlled(String key) {
            Object obj = components.get(key);
            return (obj instanceof InputGroundControlled data) ? data : null;
        }


        // Input Air Controlled Meta
        public static record InputAirControlled(
                float strafeSpeedModifier,
                float backwardsMovementModifier
        ) {
            public InputAirControlled {
                if (!Float.isFinite(strafeSpeedModifier)) strafeSpeedModifier = 0.4f;
                if (!Float.isFinite(backwardsMovementModifier)) backwardsMovementModifier = 0.5f;

                strafeSpeedModifier = Math.max(0.0f, strafeSpeedModifier);
                backwardsMovementModifier = Math.max(0.0f, backwardsMovementModifier);
            }

            public static InputAirControlled defaults() {
                return new InputAirControlled(0.4f, 0.5f);
            }
        }

        public InputAirControlled getInputAirControlled(String key) {
            Object obj = components.get(key);
            return (obj instanceof InputAirControlled data) ? data : null;
        }



        // Item Controllable Meta
        public static record ItemControllable(String controlItems) {
            public ItemControllable {
                controlItems = (controlItems == null) ? "" : controlItems.trim();
            }

            public boolean isValid() {
                return controlItems != null && !controlItems.isBlank();
            }
        }

        public @Nullable ItemControllable getItemControllable(String key) {
            Object obj = components.get(key);
            return (obj instanceof ItemControllable data) ? data : null;
        }


        // Health Meta
        public @Nullable HealthComponent getDefinitionHealthComponent(String key) {
            Object obj = components.get(key);
            return (obj instanceof HealthComponent data) ? data : null;
        }

        // Movement Meta
        public @Nullable MovementComponent getDefinitionMovementComponent(String key) {
            Object obj = components.get(key);
            return (obj instanceof MovementComponent data) ? data : null;
        }

        // Rideable Meta
        public @Nullable RideableComponent getRideableComponent(String key) {
            Object obj = components.get(key);
            return (obj instanceof RideableComponent data) ? data : null;
        }

        // Equippable Meta
        public @Nullable EquippableComponent getEquippableComponent(String key) {
            Object obj = components.get(key);
            return (obj instanceof EquippableComponent data) ? data : null;
        }

        // Horse Jump Strength Meta
        public @Nullable HorseJumpStrengthComponent getDefinitionJumpStrengthComponent(String key) {
            Object obj = components.get(key);
            return (obj instanceof HorseJumpStrengthComponent data) ? data : null;
        }

        // Dash Action Meta
        public @Nullable DashActionComponent getDefinitionDashActionComponent(String key) {
            Object obj = components.get(key);
            return (obj instanceof DashActionComponent data) ? data : null;
        }

        // Bosstable Meta
        public @Nullable BoostableComponent getDefinitionBoostableComponent(String key) {
            Object obj = components.get(key);
            return (obj instanceof BoostableComponent data) ? data : null;
        }

        // Inventory Meta
        public @Nullable InventoryComponent getDefinitionInventoryComponent(String key) {
            Object obj = components.get(key);
            return (obj instanceof InventoryComponent data) ? data : null;
        }

        // Home Meta
        public @Nullable HomeComponent getDefinitionHomeComponent(String key) {
            Object obj = components.get(key);
            return (obj instanceof HomeComponent data) ? data : null;
        }

        // Breedable Meta
        public @Nullable BreedableComponent getDefinitionBreedableComponent(String key) {
            Object obj = components.get(key);
            return (obj instanceof BreedableComponent data) ? data : null;
        }

        // Nameable Meta
        public @Nullable NameableComponent getDefinitionNameableComponent(String key) {
            Object obj = components.get(key);
            return (obj instanceof NameableComponent data) ? data : null;
        }

        // Healable Meta
        public @Nullable HealableComponent getDefinitionHealableComponent(String key) {
            Object obj = components.get(key);
            return (obj instanceof HealableComponent data) ? data : null;
        }

        // Tameable Meta
        public @Nullable TameableComponent getDefinitionTameableComponent(String key) {
            Object obj = components.get(key);
            return (obj instanceof TameableComponent data) ? data : null;
        }

        // Ageable Meta
        public @Nullable AgeableComponent getDefinitionAgeableComponent(String key) {
            Object obj = components.get(key);
            return (obj instanceof AgeableComponent data) ? data : null;
        }




        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Float getSpeedMultiplier(String key) {
            Object obj = components.get(key);
            return (obj instanceof Number n) ? n.floatValue() : null;
        }

    }

    public static final class MetaStore {
        private static final ConcurrentHashMap<String, Meta> MAP = new ConcurrentHashMap<>();

        private MetaStore() {}

        public static void put(String id, Meta meta) {
            if (id != null && !id.isBlank() && meta != null) MAP.put(id, meta);
        }

        public static Meta get(String id) {
            return MAP.get(id);
        }

        public static Meta getOrDefault(String id) {
            Meta m = MAP.get(id);
            return m != null ? m : new Meta();
        }
    }

    public static Meta metaOf(String id) {
        Meta m = MetaStore.getOrDefault(id);
        return m.copy();
    }
}
