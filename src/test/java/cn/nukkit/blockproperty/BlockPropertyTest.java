package cn.nukkit.blockproperty;

import cn.nukkit.block.BlockWood2;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.blockproperty.value.WoodType;
import cn.nukkit.math.BlockFace;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static cn.nukkit.blockproperty.CommonBlockProperties.FACING_DIRECTION;
import static org.junit.jupiter.api.Assertions.*;

class BlockPropertyTest {
    BlockProperty<BlockFace> direction = FACING_DIRECTION;

    @Test
    void isDefaultPersistentValue() {
        var defaultValue = direction.getDefaultValue();
        var defaultMeta = direction.getMetaForValue(defaultValue);
        var defaultPersistenceValue = direction.getPersistenceValueForMeta(defaultMeta);
        assertTrue(direction.isDefaultPersistentValue(defaultPersistenceValue));
    }

    @Test
    void validateMeta() {
        assertThrows(InvalidBlockPropertyMetaException.class, () -> direction.validateMeta(7, 0));
        assertThrows(InvalidBlockPropertyMetaException.class, () -> direction.validateMeta(7L, 0));
        assertThrows(InvalidBlockPropertyMetaException.class, () -> direction.validateMeta(BigInteger.valueOf(7), 0));
    }

    @Test
    void getValue() {
        System.out.println(new BlockWood2().getId());
        for (var type : ((ArrayBlockProperty) BlockWood2.NEW_LOG_TYPE).getUniverse()) {
            for (var axis : BlockFace.Axis.values()) {
                BlockWood2 blockLog = new BlockWood2();
                blockLog.setWoodType((WoodType) type);
                blockLog.setPillarAxis(axis);
                System.out.println(blockLog.getDamage());
                System.out.println(type);
                System.out.println(axis);
                System.out.println();
            }
        }

//        assertEquals(BlockFace.EAST, direction.getValue(13, 0));
//        assertEquals(BlockFace.EAST, direction.getValue(13L, 0));
//        assertEquals(BlockFace.EAST, direction.getValue(BigInteger.valueOf(13), 0));
    }

    @Test
    void setValue() {
        assertEquals(12, direction.setValue(13, 0, BlockFace.WEST));
        assertEquals(12L, direction.setValue(13L, 0, BlockFace.WEST));
        assertEquals(BigInteger.valueOf(12), direction.setValue(BigInteger.valueOf(13), 0, BlockFace.WEST));
    }

    @Test
    void setIntValue() {
        assertEquals(2, direction.getIntValue(-21, 2));
        assertEquals(2, direction.getIntValue(-21L, 2));
        assertEquals(2, direction.getIntValue(BigInteger.valueOf(-21), 2));
    }
}
