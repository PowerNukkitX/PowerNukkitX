package cn.nukkit.entity.custom;

import cn.nukkit.entity.custom.CustomEntityDefinition.Meta.*;

import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

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
         * Set the default max health of the entity.
         * @param maxHealth Int value
         */
        public SimpleBuilder maxHealth(int maxHealth) {
            return withInt(CustomEntityComponents.MAX_HEALTH, Math.max(1, maxHealth));
        }

        /**
         * Set movement speed of the entity and it's multiplayer when rushing.
         * @param moveSpeed Float value
         */
        public SimpleBuilder movement(float moveSpeed) {
            return movement(moveSpeed, 1.0f);
        }

        /**
         * Set movement speed of the entity and it's multiplayer when rushing.
         * @param moveSpeed Float value
         * @param speedMultiplier Float value
         */
        public SimpleBuilder movement(float moveSpeed, float speedMultiplier) {
            return withObject(CustomEntityComponents.MOVEMENT, new Movement(moveSpeed, speedMultiplier));
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
         * Set the default max health of the entity.
         * @param value Int value
         */
        public SimpleBuilder isPersistent(boolean value) {
            return withBoolean(CustomEntityComponents.PERSISTENT, value);
        }



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


        // Movement Meta
        public static record Movement(float moveSpeed, float multiplier) {
            public Movement {
                float a = Float.isFinite(moveSpeed) ? Math.max(0f, moveSpeed) : 0f;
                float b = Float.isFinite(multiplier) ? Math.max(0f, multiplier) : 0f;
                moveSpeed = a;
                multiplier = b;
            }
            public float moveSpeed() { return moveSpeed; }
            public float multiplier() { return multiplier; }
        }
        public Movement getMovement(String key) {
            Object obj = components.get(key);
            return (obj instanceof Movement data) ? data : new Movement(0.0f, 1.0f);
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
            public float multiplier() { return height; }
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
