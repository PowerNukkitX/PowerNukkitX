package cn.nukkit.entity.ai.memory.codec;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

@PowerNukkitXOnly
@Since("1.19.63-r1")
@Getter
public class MemoryCodec<Data> implements IMemoryCodec<Data> {
    private final Function<CompoundTag, Data> decoder;
    private final BiConsumer<Data, CompoundTag> encoder;
    @Nullable
    private BiConsumer<Data, EntityIntelligent> onInit = null;

    public MemoryCodec(
            Function<CompoundTag, Data> decoder,
            BiConsumer<Data, CompoundTag> encoder
    ) {
        this.decoder = decoder;
        this.encoder = encoder;
    }

    public MemoryCodec<Data> onInit(BiConsumer<Data, EntityIntelligent> onInit) {
        this.onInit = onInit;
        return this;
    }

    @Override
    public void init(Data data, EntityIntelligent entity) {
        if (onInit != null) {
            onInit.accept(data, entity);
        }
    }
}
