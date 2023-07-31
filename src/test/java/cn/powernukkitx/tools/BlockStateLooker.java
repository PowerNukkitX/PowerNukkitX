package cn.powernukkitx.tools;

import cn.nukkit.block.Block;
import cn.nukkit.block.impl.BlockWood2;
import cn.nukkit.block.property.ArrayBlockProperty;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.value.WoodType;
import cn.nukkit.dispenser.DispenseBehaviorRegister;
import cn.nukkit.entity.Attribute;
import cn.nukkit.item.Item;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.math.BlockFace;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;

public class BlockStateLooker {
    public static void main(String[] args) {
        Block.init();
        Enchantment.init();
        RuntimeItems.init();
        Potion.init();
        Item.init();
        EnumBiome.values(); // load class, this also registers biomes
        Effect.init();
        Attribute.init();
        DispenseBehaviorRegister.init();
        GlobalBlockPalette.getOrCreateRuntimeId(0, 0); // Force it to load

        BlockWood2 blockWood2 = new BlockWood2();
        for (var type : ((ArrayBlockProperty<?>) BlockWood2.NEW_LOG_TYPE).getUniverse()) {
            for (var type2 : ((ArrayBlockProperty<?>) CommonBlockProperties.PILLAR_AXIS).getUniverse()) {
                blockWood2.setWoodType((WoodType) type);
                blockWood2.setPillarAxis((BlockFace.Axis) type2);
                System.out.println(blockWood2.getId());
                System.out.println(blockWood2.getDamage());
                System.out.println(blockWood2.getWoodType() + ":" + blockWood2.getPillarAxis() + "\n");
            }
        }
        System.exit(1);
    }
}
