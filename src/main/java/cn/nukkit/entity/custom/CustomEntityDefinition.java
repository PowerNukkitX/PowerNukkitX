package cn.nukkit.entity.custom;


import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.atomic.AtomicInteger;

public record CustomEntityDefinition(CompoundTag nbt) {
    public static AtomicInteger RUNTIME_ID = new AtomicInteger(10000);


    public int getRuntimeId() {
        return this.nbt.getInt("rid");
    }

    public String getStringId() {
        return this.nbt.getString("id");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final CompoundTag nbt = new CompoundTag();

        private Builder() {
        }

        public Builder spawnEgg(boolean spawnEgg) {
            this.nbt.putBoolean("hasspawnegg", spawnEgg);
            return this;
        }

        public Builder identifier(String identifier) {
            this.nbt.putString("id", identifier);
            return this;
        }

        public Builder summonable(boolean summonable) {
            this.nbt.putBoolean("summonable", summonable);
            return this;
        }

        public CustomEntityDefinition build() {
            // Vanilla registry information
            this.nbt.putString("bid", "");
            this.nbt.putInt("rid", RUNTIME_ID.getAndIncrement());
            this.nbt.putBoolean("experimental", false);

            return new CustomEntityDefinition(this.nbt);
        }
    }


}

