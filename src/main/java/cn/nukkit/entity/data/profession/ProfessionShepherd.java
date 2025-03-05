package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TradeRecipeBuildUtils;

import java.util.Random;

public class ProfessionShepherd extends Profession {

    public ProfessionShepherd() {
        super(3, BlockID.LOOM, "entity.villager.shepherd", Sound.BLOCK_LOOM_USE);
    }

    @Override
    public ListTag<CompoundTag> buildTrades(int seed) {
        ListTag<CompoundTag> recipes = new ListTag<>();
        Random random = new Random(seed);

        Item rod = Item.get(Item.FISHING_ROD);
        Enchantment rodEnchantment = Enchantment.getEnchantment(new int[]{Enchantment.ID_DURABILITY, Enchantment.ID_LURE, Enchantment.ID_FORTUNE_FISHING}[random.nextInt(2)]);
        rodEnchantment.setLevel(random.nextInt(3) + 1);
        rod.addEnchantment(rodEnchantment);

        recipes.add(TradeRecipeBuildUtils.of(Item.get("minecraft:" + new DyeColor[]{DyeColor.WHITE, DyeColor.BROWN, DyeColor.BLACK, DyeColor.GRAY}[random.nextInt(4)].name().toLowerCase() + "_wool", 0, 18), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 2), Item.get(Item.SHEARS))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(1)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get("minecraft:" + DyeColor.values()[new int[]{DyeColor.WHITE.getDyeData(), DyeColor.LIGHT_BLUE.getDyeData(), DyeColor.LIME.getDyeData(), DyeColor.BLACK.getDyeData(), DyeColor.GRAY.getDyeData()}[random.nextInt(5)]].name().toLowerCase() + "_dye", 0, 12), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(10)
                        .setPriceMultiplierA(0.05f)
                        .build());
        if (random.nextBoolean()) {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD), Item.get("minecraft:" + DyeColor.values()[random.nextInt(DyeColor.values().length)].getName().toLowerCase() + "_wool"))
                    .setMaxUses(16)
                    .setRewardExp((byte) 1)
                    .setTier(2)
                    .setTraderExp(5)
                    .setPriceMultiplierA(0.05f)
                    .build());
        } else {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get("minecraft:" + DyeColor.values()[random.nextInt(DyeColor.values().length)].name().toLowerCase() + "_carpet"))
                    .setMaxUses(16)
                    .setRewardExp((byte) 1)
                    .setTier(2)
                    .setTraderExp(5)
                    .setPriceMultiplierA(0.05f)
                    .build());
        }
        recipes.add(TradeRecipeBuildUtils.of(Item.get("minecraft:" + DyeColor.values()[new int[]{DyeColor.YELLOW.getDyeData(), DyeColor.LIGHT_GRAY.getDyeData(), DyeColor.ORANGE.getDyeData(), DyeColor.RED.getDyeData(), DyeColor.PINK.getDyeData()}[random.nextInt(5)]].name().toLowerCase() + "_dye", 0, 12), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(20)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 3), Item.get("minecraft:" + DyeColor.values()[random.nextInt(DyeColor.values().length)].name() + "_bed"))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(10)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get("minecraft:" + new DyeColor[]{DyeColor.BROWN, DyeColor.PURPLE, DyeColor.BLUE, DyeColor.GREEN, DyeColor.MAGENTA, DyeColor.CYAN}[random.nextInt(6)].name().toLowerCase() + "_dye", 0, 12), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(30)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD), Item.get(Item.BANNER, random.nextInt(DyeColor.values().length)))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(15)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 2), Item.get(Item.PAINTING, 0, 3))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(30)
                        .setPriceMultiplierA(0.05f)
                        .build());
        return recipes;
    }

}
