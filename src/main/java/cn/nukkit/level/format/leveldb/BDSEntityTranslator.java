package cn.nukkit.level.format.leveldb;

import cn.nukkit.registry.Registries;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;

import java.util.Arrays;
import java.util.List;

public final class BDSEntityTranslator {
    public static NbtMap translate(NbtMap from) {
        NbtMapBuilder linkedCompoundTag = NbtMap.builder();
        if (from.containsKey("identifier")) {
            String identifier = from.getString("identifier");
            int entityNetworkId = Registries.ENTITY.getEntityNetworkId(identifier);
            if (entityNetworkId == 0) return null;
            linkedCompoundTag.putString("identifier", identifier);
        }
        if (from.containsKey("Pos", NbtType.LIST)) {
            final List<Float> pos = from.getList("Pos", NbtType.FLOAT);
            final List<Double> target = new DoubleArrayList();
            for (Float value : pos) {
                target.add(value.doubleValue());
            }
            linkedCompoundTag.putList("Pos", NbtType.DOUBLE, target);
        } else {
            linkedCompoundTag.putList("Pos", NbtType.DOUBLE, Arrays.asList(0.0, 0.0, 0.0));
        }
        if (from.containsKey("Motion", NbtType.LIST)) {
            final List<Float> pos = from.getList("Motion", NbtType.FLOAT);
            final List<Double> target = new DoubleArrayList();
            for (Float value : pos) {
                target.add(value.doubleValue());
            }
            from = from.toBuilder().putList("Motion", NbtType.DOUBLE, target).build();
            linkedCompoundTag.putList("Pos", NbtType.DOUBLE, target);
        } else {
            linkedCompoundTag.putList("Motion", NbtType.DOUBLE, Arrays.asList(0.0, 0.0, 0.0));
        }
        linkedCompoundTag.putList("Rotation", NbtType.DOUBLE, from.getList("Rotation", NbtType.DOUBLE));
        return linkedCompoundTag.build();
    }
}
