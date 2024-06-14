package cn.nukkit.registry;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.*;
import cn.nukkit.item.customitem.CustomItem;
import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.Plugin;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.sunlan.fastreflection.FastConstructor;
import me.sunlan.fastreflection.FastMemberLoader;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Cool_Loong
 */
public final class ItemRegistry implements ItemID, IRegistry<String, Item, Class<? extends Item>> {
    private static final Object2ObjectOpenHashMap<String, FastConstructor<? extends Item>> CACHE_CONSTRUCTORS = new Object2ObjectOpenHashMap<>();
    private static final Map<String, CustomItemDefinition> CUSTOM_ITEM_DEFINITIONS = new HashMap<>();
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);

    @UnmodifiableView
    public Map<String, CustomItemDefinition> getCustomItemDefinition() {
        return Collections.unmodifiableMap(CUSTOM_ITEM_DEFINITIONS);
    }

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        try {
            register(ACACIA_BOAT, ItemAcaciaBoat.class);
            register(ACACIA_CHEST_BOAT, ItemAcaciaChestBoat.class);
            register(ACACIA_SIGN, ItemAcaciaSign.class);
            register(AGENT_SPAWN_EGG, ItemAgentSpawnEgg.class);
            register(ALLAY_SPAWN_EGG, ItemAllaySpawnEgg.class);
            register(AMETHYST_SHARD, ItemAmethystShard.class);
            register(ANGLER_POTTERY_SHERD, ItemAnglerPotterySherd.class);
            register(APPLE, ItemApple.class);
            register(ARCHER_POTTERY_SHERD, ItemArcherPotterySherd.class);
            register(ARMADILLO_SCUTE, ItemArmadilloScute.class);
            register(ARMADILLO_SPAWN_EGG, ItemArmadilloSpawnEgg.class);
            register(ARMOR_STAND, ItemArmorStand.class);
            register(ARMS_UP_POTTERY_SHERD, ItemArmsUpPotterySherd.class);
            register(ARROW, ItemArrow.class);
            register(AXOLOTL_BUCKET, ItemAxolotlBucket.class);
            register(AXOLOTL_SPAWN_EGG, ItemAxolotlSpawnEgg.class);
            register(BAKED_POTATO, ItemBakedPotato.class);
            register(BALLOON, ItemBalloon.class);
            register(BAMBOO_CHEST_RAFT, ItemBambooChestRaft.class);
            register(BAMBOO_RAFT, ItemBambooRaft.class);
            register(BAMBOO_SIGN, ItemBambooSign.class);
            register(BANNER, ItemBanner.class);
            register(BANNER_PATTERN, ItemBannerPattern.class);
            register(BAT_SPAWN_EGG, ItemBatSpawnEgg.class);
            register(BEE_SPAWN_EGG, ItemBeeSpawnEgg.class);
            register(BEEF, ItemBeef.class);
            register(BEETROOT_SEEDS, ItemBeetrootSeeds.class);
            register(BEETROOT_SOUP, ItemBeetrootSoup.class);
            register(BIRCH_BOAT, ItemBirchBoat.class);
            register(BIRCH_CHEST_BOAT, ItemBirchChestBoat.class);
            register(BIRCH_SIGN, ItemBirchSign.class);
            register(BLACK_DYE, ItemBlackDye.class);
            register(BLADE_POTTERY_SHERD, ItemBladePotterySherd.class);
            register(BLAZE_POWDER, ItemBlazePowder.class);
            register(BLAZE_ROD, ItemBlazeRod.class);
            register(BLAZE_SPAWN_EGG, ItemBlazeSpawnEgg.class);
            register(BLEACH, ItemBleach.class);
            register(BLUE_DYE, ItemBlueDye.class);
            register(BOAT, ItemBoat.class);
            register(BOGGED_SPAWN_EGG, ItemBoggedSpawnEgg.class);
            register(BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, ItemBoltArmorTrimSmithingTemplate.class);
            register(BONE, ItemBone.class);
            register(BONE_MEAL, ItemBoneMeal.class);
            register(BOOK, ItemBook.class);
            register(BORDURE_INDENTED_BANNER_PATTERN, ItemBordureIndentedBannerPattern.class);
            register(BOW, ItemBow.class);
            register(BOWL, ItemBowl.class);
            register(BREAD, ItemBread.class);
            register(BREEZE_ROD, ItemBreezeRod.class);
            register(BREEZE_SPAWN_EGG, ItemBreezeSpawnEgg.class);
            register(BREWER_POTTERY_SHERD, ItemBrewerPotterySherd.class);
            register(BRICK, ItemBrick.class);
            register(BROWN_DYE, ItemBrownDye.class);
            register(BRUSH, ItemBrush.class);
            register(BUCKET, ItemBucket.class);
            register(BURN_POTTERY_SHERD, ItemBurnPotterySherd.class);
            register(CAMEL_SPAWN_EGG, ItemCamelSpawnEgg.class);
            register(CARPET, ItemCarpet.class);
            register(CARROT, ItemCarrot.class);
            register(CARROT_ON_A_STICK, ItemCarrotOnAStick.class);
            register(CAT_SPAWN_EGG, ItemCatSpawnEgg.class);
            register(CAVE_SPIDER_SPAWN_EGG, ItemCaveSpiderSpawnEgg.class);
            register(CHAINMAIL_BOOTS, ItemChainmailBoots.class);
            register(CHAINMAIL_CHESTPLATE, ItemChainmailChestplate.class);
            register(CHAINMAIL_HELMET, ItemChainmailHelmet.class);
            register(CHAINMAIL_LEGGINGS, ItemChainmailLeggings.class);
            register(CHARCOAL, ItemCharcoal.class);
            register(CHERRY_BOAT, ItemCherryBoat.class);
            register(CHERRY_CHEST_BOAT, ItemCherryChestBoat.class);
            register(CHERRY_SIGN, ItemCherrySign.class);
            register(CHEST_BOAT, ItemChestBoat.class);
            register(CHEST_MINECART, ItemChestMinecart.class);
            register(CHICKEN, ItemChicken.class);
            register(CHICKEN_SPAWN_EGG, ItemChickenSpawnEgg.class);
            register(CHORUS_FRUIT, ItemChorusFruit.class);
            register(CLAY_BALL, ItemClayBall.class);
            register(CLOCK, ItemClock.class);
            register(COAL, ItemCoal.class);
            register(COAST_ARMOR_TRIM_SMITHING_TEMPLATE, ItemCoastArmorTrimSmithingTemplate.class);
            register(COCOA_BEANS, ItemCocoaBeans.class);
            register(COD, ItemCod.class);
            register(COD_BUCKET, ItemCodBucket.class);
            register(COD_SPAWN_EGG, ItemCodSpawnEgg.class);
            register(COMMAND_BLOCK_MINECART, ItemCommandBlockMinecart.class);
            register(COMPARATOR, ItemComparator.class);
            register(COMPASS, ItemCompass.class);
            register(COMPOUND, ItemCompound.class);
            register(CONCRETE, ItemConcrete.class);
            register(CONCRETE_POWDER, ItemConcretePowder.class);
            register(COOKED_BEEF, ItemCookedBeef.class);
            register(COOKED_CHICKEN, ItemCookedChicken.class);
            register(COOKED_COD, ItemCookedCod.class);
            register(COOKED_MUTTON, ItemCookedMutton.class);
            register(COOKED_PORKCHOP, ItemCookedPorkchop.class);
            register(COOKED_RABBIT, ItemCookedRabbit.class);
            register(COOKED_SALMON, ItemCookedSalmon.class);
            register(COOKIE, ItemCookie.class);
            register(COPPER_INGOT, ItemCopperIngot.class);
            register(CORAL, ItemCoral.class);
            register(CORAL_BLOCK, ItemCoralBlock.class);
            register(CORAL_FAN, ItemCoralFan.class);
            register(CORAL_FAN_DEAD, ItemCoralFanDead.class);
            register(COW_SPAWN_EGG, ItemCowSpawnEgg.class);
            register(CREEPER_BANNER_PATTERN, ItemCreeperBannerPattern.class);
            register(CREEPER_SPAWN_EGG, ItemCreeperSpawnEgg.class);
            register(CRIMSON_SIGN, ItemCrimsonSign.class);
            register(CROSSBOW, ItemCrossbow.class);
            register(CYAN_DYE, ItemCyanDye.class);
            register(DANGER_POTTERY_SHERD, ItemDangerPotterySherd.class);
            register(DARK_OAK_BOAT, ItemDarkOakBoat.class);
            register(DARK_OAK_CHEST_BOAT, ItemDarkOakChestBoat.class);
            register(DARK_OAK_SIGN, ItemDarkOakSign.class);
            register(DIAMOND, ItemDiamond.class);
            register(DIAMOND_AXE, ItemDiamondAxe.class);
            register(DIAMOND_BOOTS, ItemDiamondBoots.class);
            register(DIAMOND_CHESTPLATE, ItemDiamondChestplate.class);
            register(DIAMOND_HELMET, ItemDiamondHelmet.class);
            register(DIAMOND_HOE, ItemDiamondHoe.class);
            register(DIAMOND_HORSE_ARMOR, ItemDiamondHorseArmor.class);
            register(DIAMOND_LEGGINGS, ItemDiamondLeggings.class);
            register(DIAMOND_PICKAXE, ItemDiamondPickaxe.class);
            register(DIAMOND_SHOVEL, ItemDiamondShovel.class);
            register(DIAMOND_SWORD, ItemDiamondSword.class);
            register(DISC_FRAGMENT_5, ItemDiscFragment5.class);
            register(DOLPHIN_SPAWN_EGG, ItemDolphinSpawnEgg.class);
            register(DONKEY_SPAWN_EGG, ItemDonkeySpawnEgg.class);
            register(DOUBLE_PLANT, ItemDoublePlant.class);
            register(DRAGON_BREATH, ItemDragonBreath.class);
            register(DRIED_KELP, ItemDriedKelp.class);
            register(DROWNED_SPAWN_EGG, ItemDrownedSpawnEgg.class);
            register(DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemDuneArmorTrimSmithingTemplate.class);
            register(DYE, ItemDye.class);
            register(ECHO_SHARD, ItemEchoShard.class);
            register(EGG, ItemEgg.class);
            register(ELDER_GUARDIAN_SPAWN_EGG, ItemElderGuardianSpawnEgg.class);
            register(ELYTRA, ItemElytra.class);
            register(EMERALD, ItemEmerald.class);
            register(EMPTY_MAP, ItemEmptyMap.class);
            register(ENCHANTED_BOOK, ItemEnchantedBook.class);
            register(ENCHANTED_GOLDEN_APPLE, ItemEnchantedGoldenApple.class);
            register(END_CRYSTAL, ItemEndCrystal.class);
            register(ENDER_DRAGON_SPAWN_EGG, ItemEnderDragonSpawnEgg.class);
            register(ENDER_EYE, ItemEnderEye.class);
            register(ENDER_PEARL, ItemEnderPearl.class);
            register(ENDERMAN_SPAWN_EGG, ItemEndermanSpawnEgg.class);
            register(ENDERMITE_SPAWN_EGG, ItemEndermiteSpawnEgg.class);
            register(EVOKER_SPAWN_EGG, ItemEvokerSpawnEgg.class);
            register(EXPERIENCE_BOTTLE, ItemExperienceBottle.class);
            register(EXPLORER_POTTERY_SHERD, ItemExplorerPotterySherd.class);
            register(EYE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemEyeArmorTrimSmithingTemplate.class);
            register(FEATHER, ItemFeather.class);
            register(FENCE, ItemFence.class);
            register(FERMENTED_SPIDER_EYE, ItemFermentedSpiderEye.class);
            register(FIELD_MASONED_BANNER_PATTERN, ItemFieldMasonedBannerPattern.class);
            register(FILLED_MAP, ItemFilledMap.class);
            register(FIRE_CHARGE, ItemFireCharge.class);
            register(FIREWORK_ROCKET, ItemFireworkRocket.class);
            register(FIREWORK_STAR, ItemFireworkStar.class);
            register(FISHING_ROD, ItemFishingRod.class);
            register(FLINT, ItemFlint.class);
            register(FLINT_AND_STEEL, ItemFlintAndSteel.class);
            register(FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, ItemFlowArmorTrimSmithingTemplate.class);
            register(FLOW_BANNER_PATTERN, ItemFlowBannerPattern.class);
            register(FLOW_POTTERY_SHERD, ItemFlowPotterySherd.class);
            register(FLOWER_BANNER_PATTERN, ItemFlowerBannerPattern.class);
            register(FOX_SPAWN_EGG, ItemFoxSpawnEgg.class);
            register(FRIEND_POTTERY_SHERD, ItemFriendPotterySherd.class);
            register(FROG_SPAWN_EGG, ItemFrogSpawnEgg.class);
            register(GHAST_SPAWN_EGG, ItemGhastSpawnEgg.class);
            register(GHAST_TEAR, ItemGhastTear.class);
            register(GLASS_BOTTLE, ItemGlassBottle.class);
            register(GLISTERING_MELON_SLICE, ItemGlisteringMelonSlice.class);
            register(GLOBE_BANNER_PATTERN, ItemGlobeBannerPattern.class);
            register(GLOW_BERRIES, ItemGlowBerries.class);
            register(GLOW_INK_SAC, ItemGlowInkSac.class);
            register(GLOW_SQUID_SPAWN_EGG, ItemGlowSquidSpawnEgg.class);
            register(GLOW_STICK, ItemGlowStick.class);
            register(GLOWSTONE_DUST, ItemGlowstoneDust.class);
            register(GOAT_HORN, ItemGoatHorn.class);
            register(GOAT_SPAWN_EGG, ItemGoatSpawnEgg.class);
            register(GOLD_INGOT, ItemGoldIngot.class);
            register(GOLD_NUGGET, ItemGoldNugget.class);
            register(GOLDEN_APPLE, ItemGoldenApple.class);
            register(GOLDEN_AXE, ItemGoldenAxe.class);
            register(GOLDEN_BOOTS, ItemGoldenBoots.class);
            register(GOLDEN_CARROT, ItemGoldenCarrot.class);
            register(GOLDEN_CHESTPLATE, ItemGoldenChestplate.class);
            register(GOLDEN_HELMET, ItemGoldenHelmet.class);
            register(GOLDEN_HOE, ItemGoldenHoe.class);
            register(GOLDEN_HORSE_ARMOR, ItemGoldenHorseArmor.class);
            register(GOLDEN_LEGGINGS, ItemGoldenLeggings.class);
            register(GOLDEN_PICKAXE, ItemGoldenPickaxe.class);
            register(GOLDEN_SHOVEL, ItemGoldenShovel.class);
            register(GOLDEN_SWORD, ItemGoldenSword.class);
            register(GRAY_DYE, ItemGrayDye.class);
            register(GREEN_DYE, ItemGreenDye.class);
            register(GUARDIAN_SPAWN_EGG, ItemGuardianSpawnEgg.class);
            register(GUNPOWDER, ItemGunpowder.class);
            register(GUSTER_BANNER_PATTERN, ItemGusterBannerPattern.class);
            register(GUSTER_POTTERY_SHERD, ItemGusterPotterySherd.class);
            register(HARD_STAINED_GLASS, ItemHardStainedGlass.class);
            register(HARD_STAINED_GLASS_PANE, ItemHardStainedGlassPane.class);
            register(HEART_OF_THE_SEA, ItemHeartOfTheSea.class);
            register(HEART_POTTERY_SHERD, ItemHeartPotterySherd.class);
            register(HEARTBREAK_POTTERY_SHERD, ItemHeartbreakPotterySherd.class);
            register(HOGLIN_SPAWN_EGG, ItemHoglinSpawnEgg.class);
            register(HONEY_BOTTLE, ItemHoneyBottle.class);
            register(HONEYCOMB, ItemHoneycomb.class);
            register(HOPPER_MINECART, ItemHopperMinecart.class);
            register(HORSE_SPAWN_EGG, ItemHorseSpawnEgg.class);
            register(HOST_ARMOR_TRIM_SMITHING_TEMPLATE, ItemHostArmorTrimSmithingTemplate.class);
            register(HOWL_POTTERY_SHERD, ItemHowlPotterySherd.class);
            register(HUSK_SPAWN_EGG, ItemHuskSpawnEgg.class);
            register(ICE_BOMB, ItemIceBomb.class);
            register(INK_SAC, ItemInkSac.class);
            register(IRON_AXE, ItemIronAxe.class);
            register(IRON_BOOTS, ItemIronBoots.class);
            register(IRON_CHESTPLATE, ItemIronChestplate.class);
            register(IRON_GOLEM_SPAWN_EGG, ItemIronGolemSpawnEgg.class);
            register(IRON_HELMET, ItemIronHelmet.class);
            register(IRON_HOE, ItemIronHoe.class);
            register(IRON_HORSE_ARMOR, ItemIronHorseArmor.class);
            register(IRON_INGOT, ItemIronIngot.class);
            register(IRON_LEGGINGS, ItemIronLeggings.class);
            register(IRON_NUGGET, ItemIronNugget.class);
            register(IRON_PICKAXE, ItemIronPickaxe.class);
            register(IRON_SHOVEL, ItemIronShovel.class);
            register(IRON_SWORD, ItemIronSword.class);
            register(JUNGLE_BOAT, ItemJungleBoat.class);
            register(JUNGLE_CHEST_BOAT, ItemJungleChestBoat.class);
            register(JUNGLE_SIGN, ItemJungleSign.class);
            register(LAPIS_LAZULI, ItemLapisLazuli.class);
            register(LAVA_BUCKET, ItemLavaBucket.class);
            register(LEAD, ItemLead.class);
            register(LEATHER, ItemLeather.class);
            register(LEATHER_BOOTS, ItemLeatherBoots.class);
            register(LEATHER_CHESTPLATE, ItemLeatherChestplate.class);
            register(LEATHER_HELMET, ItemLeatherHelmet.class);
            register(LEATHER_HORSE_ARMOR, ItemLeatherHorseArmor.class);
            register(LEATHER_LEGGINGS, ItemLeatherLeggings.class);
            register(LEAVES, ItemLeaves.class);
            register(LEAVES2, ItemLeaves2.class);
            register(LIGHT_BLUE_DYE, ItemLightBlueDye.class);
            register(LIGHT_GRAY_DYE, ItemLightGrayDye.class);
            register(LIME_DYE, ItemLimeDye.class);
            register(LINGERING_POTION, ItemLingeringPotion.class);
            register(LLAMA_SPAWN_EGG, ItemLlamaSpawnEgg.class);
            register(LODESTONE_COMPASS, ItemLodestoneCompass.class);
            register(LOG, ItemLog.class);
            register(LOG2, ItemLog2.class);
            register(MACE, ItemMace.class);
            register(MAGENTA_DYE, ItemMagentaDye.class);
            register(MAGMA_CREAM, ItemMagmaCream.class);
            register(MAGMA_CUBE_SPAWN_EGG, ItemMagmaCubeSpawnEgg.class);
            register(MANGROVE_BOAT, ItemMangroveBoat.class);
            register(MANGROVE_CHEST_BOAT, ItemMangroveChestBoat.class);
            register(MANGROVE_SIGN, ItemMangroveSign.class);
            register(MEDICINE, ItemMedicine.class);
            register(MELON_SEEDS, ItemMelonSeeds.class);
            register(MELON_SLICE, ItemMelonSlice.class);
            register(MILK_BUCKET, ItemMilkBucket.class);
            register(MINECART, ItemMinecart.class);
            register(MINER_POTTERY_SHERD, ItemMinerPotterySherd.class);
            register(MOJANG_BANNER_PATTERN, ItemMojangBannerPattern.class);
            register(MOOSHROOM_SPAWN_EGG, ItemMooshroomSpawnEgg.class);
            register(MOURNER_POTTERY_SHERD, ItemMournerPotterySherd.class);
            register(MULE_SPAWN_EGG, ItemMuleSpawnEgg.class);
            register(MUSHROOM_STEW, ItemMushroomStew.class);
            register(MUSIC_DISC_11, ItemMusicDisc11.class);
            register(MUSIC_DISC_13, ItemMusicDisc13.class);
            register(MUSIC_DISC_5, ItemMusicDisc5.class);
            register(MUSIC_DISC_BLOCKS, ItemMusicDiscBlocks.class);
            register(MUSIC_DISC_CAT, ItemMusicDiscCat.class);
            register(MUSIC_DISC_CHIRP, ItemMusicDiscChirp.class);
            register(MUSIC_DISC_CREATOR, ItemMusicDiscCreator.class);
            register(MUSIC_DISC_CREATOR_MUSIC_BOX, ItemMusicDiscCreatorMusicBox.class);
            register(MUSIC_DISC_FAR, ItemMusicDiscFar.class);
            register(MUSIC_DISC_MALL, ItemMusicDiscMall.class);
            register(MUSIC_DISC_MELLOHI, ItemMusicDiscMellohi.class);
            register(MUSIC_DISC_OTHERSIDE, ItemMusicDiscOtherside.class);
            register(MUSIC_DISC_PIGSTEP, ItemMusicDiscPigstep.class);
            register(MUSIC_DISC_PRECIPICE, ItemMusicDiscPrecipice.class);
            register(MUSIC_DISC_RELIC, ItemMusicDiscRelic.class);
            register(MUSIC_DISC_STAL, ItemMusicDiscStal.class);
            register(MUSIC_DISC_STRAD, ItemMusicDiscStrad.class);
            register(MUSIC_DISC_WAIT, ItemMusicDiscWait.class);
            register(MUSIC_DISC_WARD, ItemMusicDiscWard.class);
            register(MUTTON, ItemMutton.class);
            register(NAME_TAG, ItemNameTag.class);
            register(NAUTILUS_SHELL, ItemNautilusShell.class);
            register(NETHER_STAR, ItemNetherStar.class);
            register(NETHERBRICK, ItemNetherbrick.class);
            register(NETHERITE_AXE, ItemNetheriteAxe.class);
            register(NETHERITE_BOOTS, ItemNetheriteBoots.class);
            register(NETHERITE_CHESTPLATE, ItemNetheriteChestplate.class);
            register(NETHERITE_HELMET, ItemNetheriteHelmet.class);
            register(NETHERITE_HOE, ItemNetheriteHoe.class);
            register(NETHERITE_INGOT, ItemNetheriteIngot.class);
            register(NETHERITE_LEGGINGS, ItemNetheriteLeggings.class);
            register(NETHERITE_PICKAXE, ItemNetheritePickaxe.class);
            register(NETHERITE_SCRAP, ItemNetheriteScrap.class);
            register(NETHERITE_SHOVEL, ItemNetheriteShovel.class);
            register(NETHERITE_SWORD, ItemNetheriteSword.class);
            register(NETHERITE_UPGRADE_SMITHING_TEMPLATE, ItemNetheriteUpgradeSmithingTemplate.class);
            register(NPC_SPAWN_EGG, ItemNpcSpawnEgg.class);
            register(OAK_BOAT, ItemOakBoat.class);
            register(OAK_CHEST_BOAT, ItemOakChestBoat.class);
            register(OAK_SIGN, ItemOakSign.class);
            register(OCELOT_SPAWN_EGG, ItemOcelotSpawnEgg.class);
            register(OMINOUS_BOTTLE, ItemOminousBottle.class);
            register(OMINOUS_TRIAL_KEY, ItemOminousTrialKey.class);
            register(ORANGE_DYE, ItemOrangeDye.class);
            register(PAINTING, ItemPainting.class);
            register(PANDA_SPAWN_EGG, ItemPandaSpawnEgg.class);
            register(PAPER, ItemPaper.class);
            register(PARROT_SPAWN_EGG, ItemParrotSpawnEgg.class);
            register(PHANTOM_MEMBRANE, ItemPhantomMembrane.class);
            register(PHANTOM_SPAWN_EGG, ItemPhantomSpawnEgg.class);
            register(PIG_SPAWN_EGG, ItemPigSpawnEgg.class);
            register(PIGLIN_BANNER_PATTERN, ItemPiglinBannerPattern.class);
            register(PIGLIN_BRUTE_SPAWN_EGG, ItemPiglinBruteSpawnEgg.class);
            register(PIGLIN_SPAWN_EGG, ItemPiglinSpawnEgg.class);
            register(PILLAGER_SPAWN_EGG, ItemPillagerSpawnEgg.class);
            register(PINK_DYE, ItemPinkDye.class);
            register(PITCHER_POD, ItemPitcherPod.class);
            register(PLANKS, ItemPlanks.class);
            register(PLENTY_POTTERY_SHERD, ItemPlentyPotterySherd.class);
            register(POISONOUS_POTATO, ItemPoisonousPotato.class);
            register(POLAR_BEAR_SPAWN_EGG, ItemPolarBearSpawnEgg.class);
            register(POPPED_CHORUS_FRUIT, ItemPoppedChorusFruit.class);
            register(PORKCHOP, ItemPorkchop.class);
            register(POTATO, ItemPotato.class);
            register(POTION, ItemPotion.class);
            register(POWDER_SNOW_BUCKET, ItemPowderSnowBucket.class);
            register(PRISMARINE_CRYSTALS, ItemPrismarineCrystals.class);
            register(PRISMARINE_SHARD, ItemPrismarineShard.class);
            register(PRIZE_POTTERY_SHERD, ItemPrizePotterySherd.class);
            register(PUFFERFISH, ItemPufferfish.class);
            register(PUFFERFISH_BUCKET, ItemPufferfishBucket.class);
            register(PUFFERFISH_SPAWN_EGG, ItemPufferfishSpawnEgg.class);
            register(PUMPKIN_PIE, ItemPumpkinPie.class);
            register(PUMPKIN_SEEDS, ItemPumpkinSeeds.class);
            register(PURPLE_DYE, ItemPurpleDye.class);
            register(QUARTZ, ItemQuartz.class);
            register(RABBIT, ItemRabbit.class);
            register(RABBIT_FOOT, ItemRabbitFoot.class);
            register(RABBIT_HIDE, ItemRabbitHide.class);
            register(RABBIT_SPAWN_EGG, ItemRabbitSpawnEgg.class);
            register(RABBIT_STEW, ItemRabbitStew.class);
            register(RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, ItemRaiserArmorTrimSmithingTemplate.class);
            register(RAPID_FERTILIZER, ItemRapidFertilizer.class);
            register(RAVAGER_SPAWN_EGG, ItemRavagerSpawnEgg.class);
            register(RAW_COPPER, ItemRawCopper.class);
            register(RAW_GOLD, ItemRawGold.class);
            register(RAW_IRON, ItemRawIron.class);
            register(RECOVERY_COMPASS, ItemRecoveryCompass.class);
            register(RED_DYE, ItemRedDye.class);
            register(RED_FLOWER, ItemRedFlower.class);
            register(REDSTONE, ItemRedstone.class);
            register(REPEATER, ItemRepeater.class);
            register(RIB_ARMOR_TRIM_SMITHING_TEMPLATE, ItemRibArmorTrimSmithingTemplate.class);
            register(ROTTEN_FLESH, ItemRottenFlesh.class);
            register(SADDLE, ItemSaddle.class);
            register(SALMON, ItemSalmon.class);
            register(SALMON_BUCKET, ItemSalmonBucket.class);
            register(SALMON_SPAWN_EGG, ItemSalmonSpawnEgg.class);
            register(SAPLING, ItemSapling.class);
            register(SCRAPE_POTTERY_SHERD, ItemScrapePotterySherd.class);
            register(SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSentryArmorTrimSmithingTemplate.class);
            register(SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, ItemShaperArmorTrimSmithingTemplate.class);
            register(SHEAF_POTTERY_SHERD, ItemSheafPotterySherd.class);
            register(SHEARS, ItemShears.class);
            register(SHEEP_SPAWN_EGG, ItemSheepSpawnEgg.class);
            register(SHELTER_POTTERY_SHERD, ItemShelterPotterySherd.class);
            register(SHIELD, ItemShield.class);
            register(SHULKER_BOX, ItemShulkerBox.class);
            register(SHULKER_SHELL, ItemShulkerShell.class);
            register(SHULKER_SPAWN_EGG, ItemShulkerSpawnEgg.class);
            register(SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSilenceArmorTrimSmithingTemplate.class);
            register(SILVERFISH_SPAWN_EGG, ItemSilverfishSpawnEgg.class);
            register(SKELETON_HORSE_SPAWN_EGG, ItemSkeletonHorseSpawnEgg.class);
            register(SKELETON_SPAWN_EGG, ItemSkeletonSpawnEgg.class);
            register(SKULL_BANNER_PATTERN, ItemSkullBannerPattern.class);
            register(SKULL_POTTERY_SHERD, ItemSkullPotterySherd.class);
            register(SLIME_BALL, ItemSlimeBall.class);
            register(SLIME_SPAWN_EGG, ItemSlimeSpawnEgg.class);
            register(SNIFFER_SPAWN_EGG, ItemSnifferSpawnEgg.class);
            register(SNORT_POTTERY_SHERD, ItemSnortPotterySherd.class);
            register(SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSnoutArmorTrimSmithingTemplate.class);
            register(SNOW_GOLEM_SPAWN_EGG, ItemSnowGolemSpawnEgg.class);
            register(SNOWBALL, ItemSnowball.class);
            register(SPARKLER, ItemSparkler.class);
            register(SPAWN_EGG, ItemSpawnEgg.class);
            register(SPIDER_EYE, ItemSpiderEye.class);
            register(SPIDER_SPAWN_EGG, ItemSpiderSpawnEgg.class);
            register(SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSpireArmorTrimSmithingTemplate.class);
            register(SPLASH_POTION, ItemSplashPotion.class);
            register(SPRUCE_BOAT, ItemSpruceBoat.class);
            register(SPRUCE_CHEST_BOAT, ItemSpruceChestBoat.class);
            register(SPRUCE_SIGN, ItemSpruceSign.class);
            register(SPYGLASS, ItemSpyglass.class);
            register(SQUID_SPAWN_EGG, ItemSquidSpawnEgg.class);
            register(STAINED_GLASS, ItemStainedGlass.class);
            register(STAINED_GLASS_PANE, ItemStainedGlassPane.class);
            register(STAINED_HARDENED_CLAY, ItemStainedHardenedClay.class);
            register(STICK, ItemStick.class);
            register(STONE_AXE, ItemStoneAxe.class);
            register(STONE_BLOCK_SLAB, ItemStoneBlockSlab.class);
            register(STONE_HOE, ItemStoneHoe.class);
            register(STONE_PICKAXE, ItemStonePickaxe.class);
            register(STONE_SHOVEL, ItemStoneShovel.class);
            register(STONE_SWORD, ItemStoneSword.class);
            register(STRAY_SPAWN_EGG, ItemStraySpawnEgg.class);
            register(STRIDER_SPAWN_EGG, ItemStriderSpawnEgg.class);
            register(STRING, ItemString.class);
            register(SUGAR, ItemSugar.class);
            register(SUGAR_CANE, ItemSugarCane.class);
            register(SUSPICIOUS_STEW, ItemSuspiciousStew.class);
            register(SWEET_BERRIES, ItemSweetBerries.class);
            register(TADPOLE_BUCKET, ItemTadpoleBucket.class);
            register(TADPOLE_SPAWN_EGG, ItemTadpoleSpawnEgg.class);
            register(TALLGRASS, ItemTallgrass.class);
            register(TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemTideArmorTrimSmithingTemplate.class);
            register(TNT_MINECART, ItemTntMinecart.class);
            register(TORCHFLOWER_SEEDS, ItemTorchflowerSeeds.class);
            register(TOTEM_OF_UNDYING, ItemTotemOfUndying.class);
            register(TRADER_LLAMA_SPAWN_EGG, ItemTraderLlamaSpawnEgg.class);
            register(TRIAL_KEY, ItemTrialKey.class);
            register(TRIDENT, ItemTrident.class);
            register(TROPICAL_FISH, ItemTropicalFish.class);
            register(TROPICAL_FISH_BUCKET, ItemTropicalFishBucket.class);
            register(TROPICAL_FISH_SPAWN_EGG, ItemTropicalFishSpawnEgg.class);
            register(TURTLE_HELMET, ItemTurtleHelmet.class);
            register(TURTLE_SCUTE, ItemTurtleScute.class);
            register(TURTLE_SPAWN_EGG, ItemTurtleSpawnEgg.class);
            register(VEX_ARMOR_TRIM_SMITHING_TEMPLATE, ItemVexArmorTrimSmithingTemplate.class);
            register(VEX_SPAWN_EGG, ItemVexSpawnEgg.class);
            register(VILLAGER_SPAWN_EGG, ItemVillagerSpawnEgg.class);
            register(VINDICATOR_SPAWN_EGG, ItemVindicatorSpawnEgg.class);
            register(WANDERING_TRADER_SPAWN_EGG, ItemWanderingTraderSpawnEgg.class);
            register(WARD_ARMOR_TRIM_SMITHING_TEMPLATE, ItemWardArmorTrimSmithingTemplate.class);
            register(WARDEN_SPAWN_EGG, ItemWardenSpawnEgg.class);
            register(WARPED_FUNGUS_ON_A_STICK, ItemWarpedFungusOnAStick.class);
            register(WARPED_SIGN, ItemWarpedSign.class);
            register(WATER_BUCKET, ItemWaterBucket.class);
            register(WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, ItemWayfinderArmorTrimSmithingTemplate.class);
            register(WHEAT_SEEDS, ItemWheatSeeds.class);
            register(WHITE_DYE, ItemWhiteDye.class);
            register(WILD_ARMOR_TRIM_SMITHING_TEMPLATE, ItemWildArmorTrimSmithingTemplate.class);
            register(WIND_CHARGE, ItemWindCharge.class);
            register(WITCH_SPAWN_EGG, ItemWitchSpawnEgg.class);
            register(WITHER_SKELETON_SPAWN_EGG, ItemWitherSkeletonSpawnEgg.class);
            register(WITHER_SPAWN_EGG, ItemWitherSpawnEgg.class);
            register(WOLF_ARMOR, ItemWolfArmor.class);
            register(WOLF_SPAWN_EGG, ItemWolfSpawnEgg.class);
            register(WOOD, ItemWood.class);
            register(WOODEN_AXE, ItemWoodenAxe.class);
            register(WOODEN_HOE, ItemWoodenHoe.class);
            register(WOODEN_PICKAXE, ItemWoodenPickaxe.class);
            register(WOODEN_SHOVEL, ItemWoodenShovel.class);
            register(WOODEN_SLAB, ItemWoodenSlab.class);
            register(WOODEN_SWORD, ItemWoodenSword.class);
            register(WOOL, ItemWool.class);
            register(WRITABLE_BOOK, ItemWritableBook.class);
            register(WRITTEN_BOOK, ItemWrittenBook.class);
            register(YELLOW_DYE, ItemYellowDye.class);
            register(ZOGLIN_SPAWN_EGG, ItemZoglinSpawnEgg.class);
            register(ZOMBIE_HORSE_SPAWN_EGG, ItemZombieHorseSpawnEgg.class);
            register(ZOMBIE_PIGMAN_SPAWN_EGG, ItemZombiePigmanSpawnEgg.class);
            register(ZOMBIE_SPAWN_EGG, ItemZombieSpawnEgg.class);
            register(ZOMBIE_VILLAGER_SPAWN_EGG, ItemZombieVillagerSpawnEgg.class);
            registerBlockItem();
        } catch (RegisterException ignore) {
        }
    }

    private void registerBlockItem() throws RegisterException {
        register(BlockID.BED, ItemBed.class);
        register(BlockID.SKULL, ItemSkull.class);
        register(BlockID.BIRCH_HANGING_SIGN, ItemBirchHangingSign.class);
        register(BlockID.ACACIA_HANGING_SIGN, ItemAcaciaHangingSign.class);
        register(BlockID.BAMBOO_HANGING_SIGN, ItemBambooHangingSign.class);
        register(BlockID.CHERRY_HANGING_SIGN, ItemCherryHangingSign.class);
        register(BlockID.CRIMSON_HANGING_SIGN, ItemCrimsonHangingSign.class);
        register(BlockID.DARK_OAK_HANGING_SIGN, ItemDarkOakHangingSign.class);
        register(BlockID.JUNGLE_HANGING_SIGN, ItemJungleHangingSign.class);
        register(BlockID.MANGROVE_HANGING_SIGN, ItemMangroveHangingSign.class);
        register(BlockID.OAK_HANGING_SIGN, ItemOakHangingSign.class);
        register(BlockID.SPRUCE_HANGING_SIGN, ItemSpruceHangingSign.class);
        register(BlockID.WARPED_HANGING_SIGN, ItemWarpedHangingSign.class);
        register(BlockID.BEETROOT, ItemBeetroot.class);
    }

    @Override
    public Item get(String key) {
        try {
            FastConstructor<? extends Item> fastConstructor = CACHE_CONSTRUCTORS.get(key);
            if (fastConstructor == null) return null;
            return (Item) fastConstructor.invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Item get(String id, int meta) {
        try {
            var c = CACHE_CONSTRUCTORS.get(id);
            if (c == null) return null;
            Item item = (Item) c.invoke();
            item.setDamage(meta);
            return item;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Item get(String id, int meta, int count) {
        try {
            var c = CACHE_CONSTRUCTORS.get(id);
            if (c == null) return null;
            Item item = (Item) c.invoke();
            item.setCount(count);
            item.setDamage(meta);
            return item;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Item get(String id, int meta, int count, CompoundTag tags) {
        try {
            var c = CACHE_CONSTRUCTORS.get(id);
            if (c == null) return null;
            Item item = (Item) c.invoke();
            item.setCount(count);
            item.setCompoundTag(tags);
            item.setDamage(meta);
            return item;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Item get(String id, int meta, int count, byte[] tags) {
        try {
            var c = CACHE_CONSTRUCTORS.get(id);
            if (c == null) return null;
            Item item = (Item) c.invoke();
            item.setCount(count);
            if (tags != null) {
                item.setCompoundTag(tags);
            }
            item.setDamage(meta);
            return item;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void trim() {
        CACHE_CONSTRUCTORS.trim();
    }

    @Override
    public void reload() {
        isLoad.set(false);
        CACHE_CONSTRUCTORS.clear();
        CUSTOM_ITEM_DEFINITIONS.clear();
        init();
    }

    @Override
    public void register(String key, Class<? extends Item> value) throws RegisterException {
        try {
            FastConstructor<? extends Item> c = FastConstructor.create(value.getConstructor());
            if (CACHE_CONSTRUCTORS.putIfAbsent(key, c) != null) {
                throw new RegisterException("This item has already been registered with the identifier: " + key);
            }
        } catch (NoSuchMethodException e) {
            throw new RegisterException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * register custom item
     */
    @SafeVarargs
    public final void registerCustomItem(Plugin plugin, Class<? extends Item>... values) throws RegisterException {
        for (var c : values) {
            registerCustomItem(plugin, c);
        }
    }


    public void registerCustomItem(Plugin plugin, Class<? extends Item> value) throws RegisterException {
        try {
            if (CustomItem.class.isAssignableFrom(value)) {
                FastMemberLoader memberLoader = fastMemberLoaderCache.computeIfAbsent(plugin.getName(), p -> new FastMemberLoader(plugin.getPluginClassLoader()));
                FastConstructor<? extends Item> c = FastConstructor.create(value.getConstructor(), memberLoader, false);
                CustomItem customItem = (CustomItem) c.invoke((Object) null);
                String key = customItem.getDefinition().identifier();
                if (CACHE_CONSTRUCTORS.putIfAbsent(key, c) == null) {
                    CUSTOM_ITEM_DEFINITIONS.put(key, customItem.getDefinition());
                    Registries.ITEM_RUNTIMEID.registerCustomRuntimeItem(new ItemRuntimeIdRegistry.RuntimeEntry(key, customItem.getDefinition().getRuntimeId(), true));
                    Item ci = (Item) customItem;
                    ci.setNetId(null);
                    Registries.CREATIVE.addCreativeItem(ci);
                } else {
                    throw new RegisterException("This item has already been registered with the identifier: " + key);
                }
            } else {
                throw new RegisterException("This class does not implement the CustomItem interface and cannot be registered as a custom item!");
            }
        } catch (NoSuchMethodException e) {
            throw new RegisterException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void register0(String key, Class<? extends Item> value) {
        try {
            register(key, value);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }
}
