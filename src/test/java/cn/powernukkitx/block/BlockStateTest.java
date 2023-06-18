package cn.powernukkitx.block;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockPumpkin;
import cn.nukkit.dispenser.DispenseBehaviorRegister;
import cn.nukkit.entity.Attribute;
import cn.nukkit.item.Item;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;


public class BlockStateTest {
    public static void main(String[] args) {
        Block.init();
        Enchantment.init();
        RuntimeItems.init();
        Potion.init();
        Item.init();
        EnumBiome.values(); //load class, this also registers biomes
        Effect.init();
        Attribute.init();
        DispenseBehaviorRegister.init();
        GlobalBlockPalette.getOrCreateRuntimeId(0, 0); //Force it to load

        BlockPumpkin block = new BlockPumpkin();
        for (var i : BlockPumpkin.CARDINAL_DIRECTION.getUniverse()) {
            block.setPropertyValue(BlockPumpkin.CARDINAL_DIRECTION, i);
            System.out.println(block.getDataStorage().intValue());
        }
        System.exit(0);
    }
}
