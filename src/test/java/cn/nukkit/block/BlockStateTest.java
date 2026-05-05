package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.type.BlockPropertyType;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.registry.Registries;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class BlockStateTest {


    @Test
    @SneakyThrows
    void BlockStateTest_initStates() {
        Registries.BLOCK.init();
        int blocksCounter = 0;
        try (var stream = this.getClass().getClassLoader().getResourceAsStream("gamedata/kaooot/block_palette.nbt")) {
            CompoundTag root = NBTIO.readCompressed(stream);
            final ListTag<CompoundTag> blocks = root.getList("blocks", CompoundTag.class);
            for (CompoundTag block : blocks.getAll()) {
                int hash = block.getInt("network_id");
                String name = block.getString("name");
                if (BlockRegistry.shouldSkip(name)) continue; //Skip blocks
                BlockState state = Registries.BLOCKSTATE.get(hash);
                if (state == null) {
                    Server.getInstance().getLogger().alert("failed to find block state for " + name + " (" + hash + ")");
                } else {
                    if (!state.getIdentifier().equals(name)) {
                        Server.getInstance().getLogger().alert("BlockState " + hash + " was not " + name + ". Instead it is " + state.getIdentifier());
                    }
                }
                blocksCounter++;
            }
            Assertions.assertEquals(blocksCounter, Registries.BLOCKSTATE.getAllState().size());
        } catch (IOException e) {
        }
    }

    @Test
    @SneakyThrows
    void BlockStateImpl_computeSpecialValue() {
        short i1 = BlockState.computeSpecialValue(new BlockPropertyType.BlockPropertyValue[]{
                CommonBlockProperties.DIRECTION.createValue(1),//2bit
                CommonBlockProperties.OPEN_BIT.createValue(false),//1bit
                CommonBlockProperties.UPSIDE_DOWN_BIT.createValue(false)//1bit
        });
        Assertions.assertEquals((1 << 2), i1);
    }
}
