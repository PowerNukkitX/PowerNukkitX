package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.TradeRecipeBuildUtils;

import java.util.Random;

public class ProfessionCartographer extends Profession {

    public ProfessionCartographer() {
        super(6, BlockID.CARTOGRAPHY_TABLE, "entity.villager.cartographer");
    }

    @Override
    public ListTag<CompoundTag> buildTrades(int seed) {
        ListTag<CompoundTag> recipes = new ListTag<>();
        Random random = new Random(seed);

        recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.PAPER, 0, 24), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 7), Item.get(Item.EMPTY_MAP))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(1)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(BlockID.GLASS_PANE, 0, 11), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(10)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 13), Item.get(Item.COMPASS), Item.get(Item.EMPTY_MAP, 0, 3))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(5)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.COMPASS, 0, 1), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(20)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 14), Item.get(Item.COMPASS), Item.get(Item.EMPTY_MAP, 0, 3))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(10)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 7), Item.get(BlockID.FRAME))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(15)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 3), Item.get(Item.BANNER, random.nextInt(16), 1))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 8), Item.get(Item.BANNER_PATTERN))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(0)
                        .build());
        return recipes;
    }

}
