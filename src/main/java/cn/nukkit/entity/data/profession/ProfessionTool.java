package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.TradeRecipeBuildUtils;

import java.util.Random;

public class ProfessionTool extends Profession {

    public ProfessionTool() {
        super(10, BlockID.SMITHING_TABLE, "entity.villager.tool");
    }

    @Override
    public ListTag<CompoundTag> buildTrades(int seed) {
        ListTag<CompoundTag> recipes = new ListTag<>();
        Random random = new Random(seed);

        int[] ench = new int[] {Enchantment.ID_DURABILITY, Enchantment.ID_EFFICIENCY, Enchantment.ID_FORTUNE_DIGGING, Enchantment.ID_SILK_TOUCH};

        Item iaxe = Item.get(Item.IRON_AXE);
        Enchantment iaxee = Enchantment.getEnchantment(ench[random.nextInt(ench.length)]);
        iaxee.setLevel(1 + random.nextInt(iaxee.getMaxLevel()));
        iaxe.addEnchantment(iaxee);
        Item ishovel = Item.get(Item.IRON_SHOVEL);
        Enchantment ishovele = Enchantment.getEnchantment(ench[random.nextInt(ench.length)]);
        ishovele.setLevel(1 + random.nextInt(ishovele.getMaxLevel()));
        ishovel.addEnchantment(ishovele);
        Item ipickaxe = Item.get(Item.IRON_PICKAXE);
        Enchantment ipickaxee = Enchantment.getEnchantment(ench[random.nextInt(ench.length)]);
        ipickaxee.setLevel(1 + random.nextInt(ipickaxee.getMaxLevel()));
        ipickaxe.addEnchantment(ipickaxee);

        Item daxe = Item.get(Item.DIAMOND_AXE);
        Enchantment daxee = Enchantment.getEnchantment(ench[random.nextInt(ench.length)]);
        daxee.setLevel(1 + random.nextInt(daxee.getMaxLevel()));
        daxe.addEnchantment(daxee);
        Item dshovel = Item.get(Item.DIAMOND_SHOVEL);
        Enchantment dshovele = Enchantment.getEnchantment(ench[random.nextInt(ench.length)]);
        dshovele.setLevel(1 + random.nextInt(dshovele.getMaxLevel()));
        dshovel.addEnchantment(dshovele);
        Item dpickaxe = Item.get(Item.DIAMOND_PICKAXE);
        Enchantment dpickaxee = Enchantment.getEnchantment(ench[random.nextInt(ench.length)]);
        dpickaxee.setLevel(1 + random.nextInt(dpickaxee.getMaxLevel()));
        dpickaxe.addEnchantment(dpickaxee);

            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.COAL, 0 , 15), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0 , 1), Item.get(Item.STONE_AXE))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(1)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0 , 1), Item.get(Item.STONE_SHOVEL))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0 , 1), Item.get(Item.STONE_PICKAXE))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0 , 1), Item.get(Item.STONE_HOE))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.IRON_INGOT, 0 , 4), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(10)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0 , 36), Item.get(BlockID.BELL))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(5)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.FLINT, 0 , 24), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(20)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0 , 6 + random.nextInt(21-6)), iaxe)
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(10)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0 , 7 + random.nextInt(22-7)), ishovel)
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0 , 8 + random.nextInt(23-8)), ipickaxe)
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0 , 4), Item.get(Item.DIAMOND_HOE))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.DIAMOND, 0 , 1), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(30)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0 , 17 + random.nextInt(32-17)), daxe)
                        .setMaxUses(3)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(15)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0 , 10 + random.nextInt(25-10)), dshovel)
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0 , 18 + random.nextInt(33-18)), dpickaxe)
                        .setMaxUses(3)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(0)
                        .build());
            return recipes;
    }

}
