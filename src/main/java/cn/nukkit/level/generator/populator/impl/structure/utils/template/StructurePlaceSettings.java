package cn.nukkit.level.generator.populator.impl.structure.utils.template;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.function.Consumer;

@PowerNukkitXOnly
@Since("1.19.20-r6")
public class StructurePlaceSettings {

    public static final StructurePlaceSettings DEFAULT = new StructurePlaceSettings();

    private boolean ignoreEntities = true;
    private boolean ignoreAir;
    private int integrity = 100;
    private Consumer<CompoundTag> blockActorProcessor;

    public StructurePlaceSettings setIgnoreEntities(boolean ignoreEntities) {
        this.ignoreEntities = ignoreEntities;
        return this;
    }

    public StructurePlaceSettings setIgnoreAir(boolean ignoreAir) {
        this.ignoreAir = ignoreAir;
        return this;
    }

    public StructurePlaceSettings setIntegrity(int integrity) {
        this.integrity = integrity;
        return this;
    }

    public StructurePlaceSettings setBlockActorProcessor(Consumer<CompoundTag> blockActorProcessor) {
        this.blockActorProcessor = blockActorProcessor;
        return this;
    }

    public boolean isIgnoreEntities() {
        return this.ignoreEntities;
    }

    public boolean isIgnoreAir() {
        return this.ignoreAir;
    }

    public int getIntegrity() {
        return this.integrity;
    }

    public Consumer<CompoundTag> getBlockActorProcessor() {
        return this.blockActorProcessor;
    }
}
