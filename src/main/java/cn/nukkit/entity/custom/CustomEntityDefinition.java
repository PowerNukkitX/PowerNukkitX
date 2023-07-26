package cn.nukkit.entity.custom;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.CompoundTag;
import java.util.concurrent.atomic.AtomicInteger;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public record CustomEntityDefinition(CompoundTag nbt) {
    public static AtomicInteger RUNTIME_ID = new AtomicInteger(10000);

    public static Builder builder() {
        return new Builder();
    }

    public int getRuntimeId() {
        return this.nbt.getInt("rid");
    }

    public String getBid() {
        return this.nbt.getString("bid");
    }

    public String getStringId() {
        return this.nbt.getString("id");
    }

    public static class Builder {

        private final CompoundTag nbt = new CompoundTag();

        private Builder() {}

        public Builder identifier(String identifier) {
            this.nbt.putString("id", identifier);
            return this;
        }

        /**
         * BID也就是行为包中的Runtime Identifiers,在原版实体中，用Network Type Id来代表实体类型，在自定义实体中，我们用BID也就是Runtime Identifiers来标识实体类型。
         * <p>
         * 它用于模仿一些原版实体的硬编码元素
         * <p>
         * BID is the Runtime Identifiers in the behavior pack, in the original entity, the Network Type ID is used to represent the entity type, and in the custom entity, we use the BID is the Runtime Identifiers to identify the entity type.<p>
         * It is used to mimic hard-coded elements of the vanilla entity
         *
         * @param bid the bid
         * @return the builder
         * @see <a href="https://wiki.bedrock.dev/entities/runtime-identifier.html">runtime-identifier</a>
         */
        public Builder bid(String bid) {
            this.nbt.putString("bid", bid);
            return this;
        }

        public Builder spawnEgg(boolean spawnEgg) {
            this.nbt.putBoolean("hasspawnegg", spawnEgg);
            return this;
        }

        public Builder summonable(boolean summonable) {
            this.nbt.putBoolean("summonable", summonable);
            return this;
        }

        public CustomEntityDefinition build() {
            // Vanilla registry information
            if (!this.nbt.contains("bid")) {
                this.nbt.putString("bid", "");
            }
            this.nbt.putInt("rid", RUNTIME_ID.getAndIncrement());
            this.nbt.putBoolean("experimental", false);

            return new CustomEntityDefinition(this.nbt);
        }
    }
}
