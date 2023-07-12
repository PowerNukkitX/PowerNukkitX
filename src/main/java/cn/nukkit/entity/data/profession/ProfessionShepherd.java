package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.MinecraftItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.RecipeBuildUtils;

import java.util.Random;

public class ProfessionShepherd extends Profession {

    public ProfessionShepherd() {
        super(3, BlockID.LOOM, "entity.villager.shepherd");
    }

    @Override
    public ListTag<Tag> buildTrades(int seed) {
        ListTag<Tag> recipes = new ListTag<>("Recipes");
        Random random = new Random(seed);

        Item rod = MinecraftItemID.FISHING_ROD.get(1);
        Enchantment rodEnchantment = Enchantment.get(new int[] {Enchantment.ID_DURABILITY, Enchantment.ID_LURE, Enchantment.ID_FORTUNE_FISHING}[random.nextInt(2)]);
        rodEnchantment.setLevel(random.nextInt(3) +1);
        rod.addEnchantment(rodEnchantment);

        recipes.add(RecipeBuildUtils.of(Item.get(Item.WOOL,random.nextInt(16),18), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD,0,2), Item.get(Item.SHEARS))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.DYE,random.nextInt(5),12), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD,0,1), Item.get(Item.WOOL))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD,0,1), Item.get(Item.CARPET))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.DYE,5 + random.nextInt(5),12), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD,0,3), Item.get(Item.BED, 0 , DyeColor.BLUE.getItemDyeMeta()))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.DYE,10 + random.nextInt(6),12), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD,0,1), Item.get(Item.BANNER))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD,0,2), Item.get(Item.PAINTING))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(2)
                        .build());
            return recipes;
    }

}
