package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ShortTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import lombok.Data;

import java.util.List;

@Data
public class BiomeReplacementData implements IBiomeDefinitionListObject {

    private short replacementBiome;
    private short dimension;
    private List<Short> targetBiomes = new ShortArrayList();
    private float amount;
    private float noiseFrequencyScale;
    private int replacementIndex;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeShortLE(this.replacementBiome);
        byteBuf.writeShortLE(this.dimension);
        byteBuf.writeArray(this.targetBiomes, (buf, value) -> buf.writeShortLE(value));
        byteBuf.writeFloatLE(this.amount);
        byteBuf.writeFloatLE(this.noiseFrequencyScale);
        byteBuf.writeIntLE(this.replacementIndex);
    }

    @Override
    public void parse(CompoundTag tag) {
        this.replacementBiome = tag.getShort("replacementBiome");
        this.dimension = tag.getShort("dimension");
        this.targetBiomes = tag.getList("targetBiomes", ShortTag.class).getAll().stream()
                .map(v -> v.getData().shortValue())
                .toList();
        this.amount = tag.getFloat("amount");
        this.noiseFrequencyScale = tag.getFloat("noiseFrequencyScale");
        this.replacementIndex = tag.getInt("replacementIndex");
    }
}