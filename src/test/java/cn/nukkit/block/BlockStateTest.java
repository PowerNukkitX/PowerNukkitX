package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.type.BlockPropertyType;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BlockStateTest {
    @Test
    @SneakyThrows
    void BlockStateImpl_computeSpecialValue() {
        short i1 = BlockState.computeSpecialValue(new BlockPropertyType.BlockPropertyValue[]{
                CommonBlockProperties.DIRECTION.createValue(1),//2bit
                CommonBlockProperties.OPEN_BIT.createValue(false),//1bit
                CommonBlockProperties.UPSIDE_DOWN_BIT.createValue(false)//1bit
        });
        Assertions.assertEquals((1 << 2 | 0 << 1 | 0), i1);
    }
}
