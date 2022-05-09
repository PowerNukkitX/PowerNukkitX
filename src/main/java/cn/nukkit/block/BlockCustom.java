package cn.nukkit.block;

import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockPropertyData;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BlockCustom extends Block {
    private static final ConcurrentHashMap<String, Integer> INTERNAL_ALLOCATION_ID_MAP = new ConcurrentHashMap<>();

    private static int nextBlockId = MAX_BLOCK_ID;
    private final String namespace;

    public BlockCustom(String namespace) {
        this.namespace = namespace.toLowerCase(Locale.ENGLISH);
        if (!INTERNAL_ALLOCATION_ID_MAP.containsKey(namespace)) {
            INTERNAL_ALLOCATION_ID_MAP.put(this.namespace, nextBlockId);
            nextBlockId++;
        }
    }

    public String getNamespace() {
        return this.namespace;
    }

    private Optional<String> getGeometry() {
        return Optional.empty();
    }

    public BlockPropertyData getBlockPropertyData() {
        CompoundTag componentsNBT = new CompoundTag()
                .putCompound("minecraft:block_light_absorption", new CompoundTag()
                        .putInt("value", this.getLightFilter())
                        .putCompound("minecraft:block_light_emission", new CompoundTag()
                                .putFloat("emission", this.getLightLevel())
                                .putCompound("minecraft:friction", new CompoundTag()
                                        .putFloat("value", (float) this.getResistance())
                                        .putCompound("minecraft:rotation", new CompoundTag()
                                                .putFloat("x", 0)
                                                .putFloat("y", 0)
                                                .putFloat("z", 0)))));
        if (this.getGeometry().isPresent()) {
            componentsNBT.putCompound("minecraft:geometry", new CompoundTag()
                    .putString("value", this.getGeometry().get()));
        }

        return new BlockPropertyData(this.getNamespace(), new CompoundTag()
                .putCompound("components", componentsNBT));
    }

    @Override
    public int getId() {
        return INTERNAL_ALLOCATION_ID_MAP.get(this.namespace);
    }

    @Override
    public String getName() {
        return this.namespace.split(":")[1].toLowerCase(Locale.ENGLISH);
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.EMPTY_PROPERTIES;
    }
}
