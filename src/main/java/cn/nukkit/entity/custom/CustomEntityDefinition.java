package cn.nukkit.entity.custom;

import org.jetbrains.annotations.NotNull;
import lombok.extern.slf4j.Slf4j;


/**
 * CustomEntityDefinition defines custom entities from behavior packs.
 *
 * Use {@link CustomEntityDefinition.SimpleBuilder} to declare all
 * properties and behaviors. The builder centralizes supported fields and
 * handles server side overrides automatically. <p>
 *
 * Override {@link Entity Entity} methods can be used for advanced or
 * specialized logic not covered by the builder.
 */
@Slf4j
public record CustomEntityDefinition(String id, String eid, boolean hasSpawnegg, boolean isSummonable) {

    public CustomEntityDefinition {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id is blank");
        if (eid == null) eid = "";
    }

    public static SimpleBuilder simpleBuilder(@NotNull String id) {
        return new SimpleBuilder(id);
    }

    public static final class SimpleBuilder {
        private final String id;
        private String eid = "";
        private boolean hasSpawnegg = true;
        private boolean isSummonable  = true;

        public SimpleBuilder(@NotNull String id) {
            if (id == null || id.isBlank()) throw new IllegalArgumentException("id is blank");
            this.id = id;
        }

        public SimpleBuilder eid(String entityIdentifier) {
            if (entityIdentifier == null) entityIdentifier = "";
            this.eid = entityIdentifier;
            return this;
        }

        public SimpleBuilder hasSpawnEgg(boolean value) {
            this.hasSpawnegg = value;
            return this;
        }

        public SimpleBuilder isSummonable(boolean value) {
            this.isSummonable = value;
            return this;
        }

        public CustomEntityDefinition build() {
            return new CustomEntityDefinition(this.id, this.eid, this.hasSpawnegg, this.isSummonable);
        }
    }
}