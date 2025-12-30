package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.OptionalValue;

public class BiomeCappedSurfaceData implements IBiomeDefinitionListObject {

    public Integer[] floorBlocks;
    public Integer[] ceilingBlocks;
    public OptionalValue<Integer> seaBlock = OptionalValue.empty();
    public OptionalValue<Integer> foundationBlock = OptionalValue.empty();
    public OptionalValue<Integer> beachBlock = OptionalValue.empty();


    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeArray(floorBlocks, byteBuf::writeIntLE);
        byteBuf.writeArray(ceilingBlocks, byteBuf::writeIntLE);
        byteBuf.writeOptional(seaBlock, byteBuf::writeIntLE);
        byteBuf.writeOptional(foundationBlock, byteBuf::writeIntLE);
        byteBuf.writeOptional(beachBlock, byteBuf::writeIntLE);
    }

    @Override
    public void parse(CompoundTag tag) {
        floorBlocks = tag.getList("floorBlocks", IntTag.class).getAll().stream().map(v -> v.data).toArray(Integer[]::new);
        ceilingBlocks = tag.getList("ceilingBlocks", IntTag.class).getAll().stream().map(v -> v.data).toArray(Integer[]::new);
        foundationBlock = tag.containsInt("foundationBlock") ? OptionalValue.of(tag.getInt("foundationBlock")) : OptionalValue.empty();
        seaBlock = tag.containsInt("seaBlock") ? OptionalValue.of(tag.getInt("seaBlock")) : OptionalValue.empty();
        beachBlock = tag.containsInt("beachBlock") ? OptionalValue.of(tag.getInt("beachBlock")) : OptionalValue.empty();
    }

}
