package cn.nukkit.entity.custom;

import cn.nukkit.entity.custom.CustomEntityDefinition.Meta.*;

import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import lombok.extern.slf4j.Slf4j;


/**
 * CustomEntityDefinition defines custom entities from behavior packs.
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
            float w = width  < 0f ? 0f : width;
            float h = height < 0f ? 0f : height;
            return withObject(CustomEntityComponents.COLLISION_BOX, new CollisionBox(w, h));
        }

        /**
         * Set the default max health of the entity.
         * @param maxHealth Int value
         */
        public SimpleBuilder maxHealth(int maxHealth) {
            return withInt(CustomEntityComponents.MAX_HEALTH, Math.max(1, maxHealth));
        }

        /**
         * Set movement speed of the entity and it`s multipliyer when rushing.
         * @param movement Float value
         * @param speedMultiplier Float value
         */
        public SimpleBuilder movement(float moveSpeed) {
            return movement(moveSpeed, 1.0f);
        }

        /**
         * Set movement speed of the entity and it`s multipliyer when rushing.
         * @param movement Float value
         * @param speedMultiplier Float value
         */
        public SimpleBuilder movement(float moveSpeed, float speedMultiplier) {
            return withObject(CustomEntityComponents.MOVEMENT, new Movement(moveSpeed, speedMultiplier));
        }

        /**
         * Defines the range, in blocks, that a mob will pursue a target.
         * @param radius Int value, the radius of the area of blocks the entity will attempt to stay within around a target.
         * @param max Int value, the maximum distance the mob will go from a target.
         */
        public SimpleBuilder followRange(int radius, int max) {
            return withObject(CustomEntityComponents.FOLLOW, new FollowRange(radius, max));
        }

        /**
         * Set the entity's melee attack.
         * @param attackPower Int value
         */
        public SimpleBuilder attack(int attackPower) {
            return withInt(CustomEntityComponents.ATTACK, Math.max(0, attackPower));
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
         * Set family types of the entity, can be one or multiple strings
         * @param values Set of strings ex: .typeFamily("type1", "type2", "type3")
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
