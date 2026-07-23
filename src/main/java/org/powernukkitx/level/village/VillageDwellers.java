package org.powernukkitx.level.village;

import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.IntTag;
import org.powernukkitx.nbt.tag.ListTag;

import javax.annotation.Nullable;
import java.util.List;
import java.util.ArrayList;

public final class VillageDwellers {
    private final List<Dweller> dwellers;

    public VillageDwellers(List<Dweller> dwellers) {
        this.dwellers = new ArrayList<>(dwellers);
    }

    public List<Dweller> dwellers() { return dwellers; }

    public static VillageDwellers fromCompound(CompoundTag tag) {
        return new VillageDwellers(tag.getList("Dwellers", CompoundTag.class).getAll().stream()
                .map(Dweller::fromCompound).toList());
    }

    public CompoundTag toCompound() {
        ListTag<CompoundTag> dwellersTag = new ListTag<>();
        dwellers.forEach(dweller -> dwellersTag.add(dweller.toCompound()));
        return new CompoundTag().putList("Dwellers", dwellersTag);
    }

    public static final class Dweller {
        private final List<Actor> actors;

        public Dweller(List<Actor> actors) {
            this.actors = new ArrayList<>(actors);
        }

        public List<Actor> actors() { return actors; }

        public static Dweller fromCompound(CompoundTag tag) {
            return new Dweller(tag.getList("actors", CompoundTag.class).getAll().stream()
                    .map(Actor::fromCompound).toList());
        }

        public CompoundTag toCompound() {
            ListTag<CompoundTag> actorsTag = new ListTag<>();
            actors.forEach(actor -> actorsTag.add(actor.toCompound()));
            return new CompoundTag().putList("actors", actorsTag);
        }
    }

    public record Actor(long id, BlockVector3 lastSavedPosition, long timestamp, @Nullable Long lastWorked) {
        public static Actor fromCompound(CompoundTag tag) {
            var position = tag.getList("last_saved_pos");
            if (position.size() != 3) {
                throw new IllegalArgumentException("last_saved_pos must contain exactly three coordinates");
            }
            return new Actor(tag.getLong("ID"),
                    new BlockVector3(((Number) position.get(0).parseValue()).intValue(),
                            ((Number) position.get(1).parseValue()).intValue(),
                            ((Number) position.get(2).parseValue()).intValue()),
                    tag.getLong("TS"), tag.contains("last_worked") ? tag.getLong("last_worked") : null);
        }

        public CompoundTag toCompound() {
            CompoundTag result = new CompoundTag()
                    .putLong("ID", id)
                    .putList("last_saved_pos", new ListTag<IntTag>()
                            .add(new IntTag(lastSavedPosition.x))
                            .add(new IntTag(lastSavedPosition.y))
                            .add(new IntTag(lastSavedPosition.z)))
                    .putLong("TS", timestamp);
            if (lastWorked != null) {
                result.putLong("last_worked", lastWorked);
            }
            return result;
        }
    }
}
