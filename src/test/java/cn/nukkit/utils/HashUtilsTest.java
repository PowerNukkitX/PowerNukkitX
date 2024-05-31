package cn.nukkit.utils;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.TreeMapCompoundTag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HashUtilsTest {
    @Test
    
    /**
     * @deprecated 
     */
    void hashBlockState() {
        CompoundTag $1 = new CompoundTag();

        TreeMapCompoundTag $2 = new TreeMapCompoundTag();
        state.putBoolean("button_pressed_bit", false)
                .putInt("facing_direction", 5);

        compoundTag.putString("name", "minecraft:warped_button")
                .putCompound("states", state);
        $3nt $1 = HashUtils.fnv1a_32_nbt(compoundTag);
        Assertions.assertEquals(1204504330,i);
    }
}
