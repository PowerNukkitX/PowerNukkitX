package cn.nukkit.level.generator.object.structures.jigsaw.pool;

import cn.nukkit.level.structure.AbstractStructure;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.random.RandomSourceProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Range;

@AllArgsConstructor
public class StructurePool {

    @Getter
    private final String name;
    @Getter
    private final String fallback;
    public final Entry[] entries;

    public StructurePool(String name, Entry... entries) {
        this(name, null, entries);
    }

    public Entry getRandomEntry(RandomSourceProvider randomSourceProvider) {
        int totalWeight = 0;
        for (Entry entry : entries) {
            totalWeight += entry.weight;
        }

        int target = randomSourceProvider.nextBoundedInt(totalWeight - 1);
        for (Entry entry : entries) {
            if (target < entry.weight) {
                return entry;
            }
            target -= entry.weight;
        }

        throw new IllegalStateException("Failed to select structure from pool '%s'".formatted(name));
    }

    public String getStructureKey(RandomSourceProvider randomSourceProvider) {
        return getRandomEntry(randomSourceProvider).structureName;
    }

    public AbstractStructure getRandomStructure(RandomSourceProvider randomSourceProvider) {
        return Registries.STRUCTURE.get(getStructureKey(randomSourceProvider));
    }

    public record Entry(@NonNull String structureName, @Range(from = 0, to = Integer.MAX_VALUE) int weight,
                        @NonNull String projection) {
        public Entry(@NonNull String structureName, @Range(from = 0, to = Integer.MAX_VALUE) int weight) {
            this(structureName, weight, "rigid");
        }
    }
}
