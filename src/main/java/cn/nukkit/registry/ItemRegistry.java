package cn.nukkit.registry;

import cn.nukkit.item.*;
import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.nbt.tag.CompoundTag;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.sunlan.fastreflection.FastConstructor;
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
    private static final HashMap<String, CustomItemDefinition> CUSTOM_ITEM_DEFINITIONS = new HashMap<>();

    private static final AtomicBoolean isLoad = new AtomicBoolean(false);

    @UnmodifiableView
    public Map<String, CustomItemDefinition> getCustomItemDefinition() {
        return Collections.unmodifiableMap(CUSTOM_ITEM_DEFINITIONS);
    }

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        register0(ACACIA_BOAT, ItemAcaciaBoat.class);
        register0(ACACIA_CHEST_BOAT, ItemAcaciaChestBoat.class);
        register0(ACACIA_DOOR, ItemAcaciaDoor.class);
        register0(ACACIA_HANGING_SIGN, ItemAcaciaHangingSign.class);
        register0(ACACIA_SIGN, ItemAcaciaSign.class);
        register0(AGENT_SPAWN_EGG, ItemAgentSpawnEgg.class);
        register0(ALLAY_SPAWN_EGG, ItemAllaySpawnEgg.class);
        register0(AMETHYST_SHARD, ItemAmethystShard.class);
        register0(ANGLER_POTTERY_SHERD, ItemAnglerPotterySherd.class);
        register0(APPLE, ItemApple.class);
        register0(ARCHER_POTTERY_SHERD, ItemArcherPotterySherd.class);
        register0(ARMOR_STAND, ItemArmorStand.class);
        register0(ARMS_UP_POTTERY_SHERD, ItemArmsUpPotterySherd.class);
        register0(ARROW, ItemArrow.class);
        register0(AXOLOTL_BUCKET, ItemAxolotlBucket.class);
        register0(AXOLOTL_SPAWN_EGG, ItemAxolotlSpawnEgg.class);
        register0(BAKED_POTATO, ItemBakedPotato.class);
//        register0(BALLOON, ItemBalloon.class);//edu
        register0(BAMBOO_CHEST_RAFT, ItemBambooChestRaft.class);
        register0(BAMBOO_DOOR, ItemBambooDoor.class);
        register0(BAMBOO_HANGING_SIGN, ItemBambooHangingSign.class);
        register0(BAMBOO_RAFT, ItemBambooRaft.class);
        register0(BAMBOO_SIGN, ItemBambooSign.class);
        register0(BANNER, ItemBanner.class);
        register0(BANNER_PATTERN, ItemBannerPattern.class);
        register0(BAT_SPAWN_EGG, ItemBatSpawnEgg.class);
        register0(BED, ItemBed.class);
        register0(BEE_SPAWN_EGG, ItemBeeSpawnEgg.class);
        register0(BEEF, ItemBeef.class);
        register0(BEETROOT, ItemBeetroot.class);
        register0(BEETROOT_SEEDS, ItemBeetrootSeeds.class);
        register0(BEETROOT_SOUP, ItemBeetrootSoup.class);
        register0(BIRCH_BOAT, ItemBirchBoat.class);
        register0(BIRCH_CHEST_BOAT, ItemBirchChestBoat.class);
        register0(BIRCH_DOOR, ItemBirchDoor.class);
        register0(BIRCH_HANGING_SIGN, ItemBirchHangingSign.class);
        register0(BIRCH_SIGN, ItemBirchSign.class);
        register0(BLACK_DYE, ItemBlackDye.class);
        register0(BLADE_POTTERY_SHERD, ItemBladePotterySherd.class);
        register0(BLAZE_POWDER, ItemBlazePowder.class);
        register0(BLAZE_ROD, ItemBlazeRod.class);
        register0(BLAZE_SPAWN_EGG, ItemBlazeSpawnEgg.class);
//        register0(BLEACH, ItemBleach.class); //edu
        register0(BLUE_DYE, ItemBlueDye.class);
        register0(BOAT, ItemBoat.class);
        register0(BONE, ItemBone.class);
        register0(BONE_MEAL, ItemBoneMeal.class);
        register0(BOOK, ItemBook.class);
        register0(BORDURE_INDENTED_BANNER_PATTERN, ItemBordureIndentedBannerPattern.class);
        register0(BOW, ItemBow.class);
        register0(BOWL, ItemBowl.class);
        register0(BREAD, ItemBread.class);
        register0(BREWER_POTTERY_SHERD, ItemBrewerPotterySherd.class);
        register0(BREWING_STAND, ItemBrewingStand.class);
        register0(BRICK, ItemBrick.class);
        register0(BROWN_DYE, ItemBrownDye.class);
        register0(BRUSH, ItemBrush.class);
        register0(BUCKET, ItemBucket.class);
        register0(BURN_POTTERY_SHERD, ItemBurnPotterySherd.class);
        register0(CAKE, ItemCake.class);
        register0(CAMEL_SPAWN_EGG, ItemCamelSpawnEgg.class);
        register0(CAMPFIRE, ItemCampfire.class);
        register0(CARPET, ItemCarpet.class);
        register0(CARROT, ItemCarrot.class);
        register0(CARROT_ON_A_STICK, ItemCarrotOnAStick.class);
        register0(CAT_SPAWN_EGG, ItemCatSpawnEgg.class);
        register0(CAULDRON, ItemCauldron.class);
        register0(CAVE_SPIDER_SPAWN_EGG, ItemCaveSpiderSpawnEgg.class);
        register0(CHAIN, ItemChain.class);
        register0(CHAINMAIL_BOOTS, ItemChainmailBoots.class);
        register0(CHAINMAIL_CHESTPLATE, ItemChainmailChestplate.class);
        register0(CHAINMAIL_HELMET, ItemChainmailHelmet.class);
        register0(CHAINMAIL_LEGGINGS, ItemChainmailLeggings.class);
        register0(CHARCOAL, ItemCharcoal.class);
        register0(CHERRY_BOAT, ItemCherryBoat.class);
        register0(CHERRY_CHEST_BOAT, ItemCherryChestBoat.class);
        register0(CHERRY_DOOR, ItemCherryDoor.class);
        register0(CHERRY_HANGING_SIGN, ItemCherryHangingSign.class);
        register0(CHERRY_SIGN, ItemCherrySign.class);
        register0(CHEST_BOAT, ItemChestBoat.class);
        register0(CHEST_MINECART, ItemChestMinecart.class);
        register0(CHICKEN, ItemChicken.class);
        register0(CHICKEN_SPAWN_EGG, ItemChickenSpawnEgg.class);
        register0(CHORUS_FRUIT, ItemChorusFruit.class);
        register0(CLAY_BALL, ItemClayBall.class);
        register0(CLOCK, ItemClock.class);
        register0(COAL, ItemCoal.class);
        register0(COAST_ARMOR_TRIM_SMITHING_TEMPLATE, ItemCoastArmorTrimSmithingTemplate.class);
        register0(COCOA_BEANS, ItemCocoaBeans.class);
        register0(COD, ItemCod.class);
        register0(COD_BUCKET, ItemCodBucket.class);
        register0(COD_SPAWN_EGG, ItemCodSpawnEgg.class);
        register0(COMMAND_BLOCK_MINECART, ItemCommandBlockMinecart.class);
        register0(COMPARATOR, ItemComparator.class);
        register0(COMPASS, ItemCompass.class);
//        register0(COMPOUND, ItemCompound.class);//edu
        register0(CONCRETE, ItemConcrete.class);
        register0(CONCRETE_POWDER, ItemConcretePowder.class);
        register0(COOKED_BEEF, ItemCookedBeef.class);
        register0(COOKED_CHICKEN, ItemCookedChicken.class);
        register0(COOKED_COD, ItemCookedCod.class);
        register0(COOKED_MUTTON, ItemCookedMutton.class);
        register0(COOKED_PORKCHOP, ItemCookedPorkchop.class);
        register0(COOKED_RABBIT, ItemCookedRabbit.class);
        register0(COOKED_SALMON, ItemCookedSalmon.class);
        register0(COOKIE, ItemCookie.class);
        register0(COPPER_DOOR, ItemCopperDoor.class);
        register0(COPPER_INGOT, ItemCopperIngot.class);
        register0(CORAL, ItemCoral.class);
        register0(COW_SPAWN_EGG, ItemCowSpawnEgg.class);
        register0(CREEPER_BANNER_PATTERN, ItemCreeperBannerPattern.class);
        register0(CREEPER_SPAWN_EGG, ItemCreeperSpawnEgg.class);
        register0(CRIMSON_DOOR, ItemCrimsonDoor.class);
        register0(CRIMSON_HANGING_SIGN, ItemCrimsonHangingSign.class);
        register0(CRIMSON_SIGN, ItemCrimsonSign.class);
        register0(CROSSBOW, ItemCrossbow.class);
        register0(CYAN_DYE, ItemCyanDye.class);
        register0(DANGER_POTTERY_SHERD, ItemDangerPotterySherd.class);
        register0(DARK_OAK_BOAT, ItemDarkOakBoat.class);
        register0(DARK_OAK_CHEST_BOAT, ItemDarkOakChestBoat.class);
        register0(DARK_OAK_DOOR, ItemDarkOakDoor.class);
        register0(DARK_OAK_HANGING_SIGN, ItemDarkOakHangingSign.class);
        register0(DARK_OAK_SIGN, ItemDarkOakSign.class);
        register0(DIAMOND, ItemDiamond.class);
        register0(DIAMOND_AXE, ItemDiamondAxe.class);
        register0(DIAMOND_BOOTS, ItemDiamondBoots.class);
        register0(DIAMOND_CHESTPLATE, ItemDiamondChestplate.class);
        register0(DIAMOND_HELMET, ItemDiamondHelmet.class);
        register0(DIAMOND_HOE, ItemDiamondHoe.class);
        register0(DIAMOND_HORSE_ARMOR, ItemDiamondHorseArmor.class);
        register0(DIAMOND_LEGGINGS, ItemDiamondLeggings.class);
        register0(DIAMOND_PICKAXE, ItemDiamondPickaxe.class);
        register0(DIAMOND_SHOVEL, ItemDiamondShovel.class);
        register0(DIAMOND_SWORD, ItemDiamondSword.class);
        register0(DISC_FRAGMENT_5, ItemDiscFragment5.class);
        register0(DOLPHIN_SPAWN_EGG, ItemDolphinSpawnEgg.class);
        register0(DONKEY_SPAWN_EGG, ItemDonkeySpawnEgg.class);
        register0(DRAGON_BREATH, ItemDragonBreath.class);
        register0(DRIED_KELP, ItemDriedKelp.class);
        register0(DROWNED_SPAWN_EGG, ItemDrownedSpawnEgg.class);
        register0(DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemDuneArmorTrimSmithingTemplate.class);
        register0(DYE, ItemDye.class);
        register0(ECHO_SHARD, ItemEchoShard.class);
        register0(EGG, ItemEgg.class);
        register0(ELDER_GUARDIAN_SPAWN_EGG, ItemElderGuardianSpawnEgg.class);
        register0(ELYTRA, ItemElytra.class);
        register0(EMERALD, ItemEmerald.class);
        register0(EMPTY_MAP, ItemEmptyMap.class);
        register0(ENCHANTED_BOOK, ItemEnchantedBook.class);
        register0(ENCHANTED_GOLDEN_APPLE, ItemEnchantedGoldenApple.class);
        register0(END_CRYSTAL, ItemEndCrystal.class);
        register0(ENDER_DRAGON_SPAWN_EGG, ItemEnderDragonSpawnEgg.class);
        register0(ENDER_EYE, ItemEnderEye.class);
        register0(ENDER_PEARL, ItemEnderPearl.class);
        register0(ENDERMAN_SPAWN_EGG, ItemEndermanSpawnEgg.class);
        register0(ENDERMITE_SPAWN_EGG, ItemEndermiteSpawnEgg.class);
        register0(EVOKER_SPAWN_EGG, ItemEvokerSpawnEgg.class);
        register0(EXPERIENCE_BOTTLE, ItemExperienceBottle.class);
        register0(EXPLORER_POTTERY_SHERD, ItemExplorerPotterySherd.class);
        register0(EXPOSED_COPPER_DOOR, ItemExposedCopperDoor.class);
        register0(EYE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemEyeArmorTrimSmithingTemplate.class);
        register0(FEATHER, ItemFeather.class);
        register0(FENCE, ItemFence.class);
        register0(FERMENTED_SPIDER_EYE, ItemFermentedSpiderEye.class);
        register0(FIELD_MASONED_BANNER_PATTERN, ItemFieldMasonedBannerPattern.class);
        register0(FILLED_MAP, ItemFilledMap.class);
        register0(FIRE_CHARGE, ItemFireCharge.class);
        register0(FIREWORK_ROCKET, ItemFireworkRocket.class);
        register0(FIREWORK_STAR, ItemFireworkStar.class);
        register0(FISHING_ROD, ItemFishingRod.class);
        register0(FLINT, ItemFlint.class);
        register0(FLINT_AND_STEEL, ItemFlintAndSteel.class);
        register0(FLOWER_BANNER_PATTERN, ItemFlowerBannerPattern.class);
        register0(FLOWER_POT, ItemFlowerPot.class);
        register0(FOX_SPAWN_EGG, ItemFoxSpawnEgg.class);
        register0(FRAME, ItemFrame.class);
        register0(FRIEND_POTTERY_SHERD, ItemFriendPotterySherd.class);
        register0(FROG_SPAWN_EGG, ItemFrogSpawnEgg.class);
        register0(GHAST_SPAWN_EGG, ItemGhastSpawnEgg.class);
        register0(GHAST_TEAR, ItemGhastTear.class);
        register0(GLASS_BOTTLE, ItemGlassBottle.class);
        register0(GLISTERING_MELON_SLICE, ItemGlisteringMelonSlice.class);
        register0(GLOBE_BANNER_PATTERN, ItemGlobeBannerPattern.class);
        register0(GLOW_BERRIES, ItemGlowBerries.class);
        register0(GLOW_FRAME, ItemGlowFrame.class);
        register0(GLOW_INK_SAC, ItemGlowInkSac.class);
        register0(GLOW_SQUID_SPAWN_EGG, ItemGlowSquidSpawnEgg.class);
        //register0(GLOW_STICK, ItemGlowStick.class); //edu
        register0(GLOWSTONE_DUST, ItemGlowstoneDust.class);
        register0(GOAT_HORN, ItemGoatHorn.class);
        register0(GOAT_SPAWN_EGG, ItemGoatSpawnEgg.class);
        register0(GOLD_INGOT, ItemGoldIngot.class);
        register0(GOLD_NUGGET, ItemGoldNugget.class);
        register0(GOLDEN_APPLE, ItemGoldenApple.class);
        register0(GOLDEN_AXE, ItemGoldenAxe.class);
        register0(GOLDEN_BOOTS, ItemGoldenBoots.class);
        register0(GOLDEN_CARROT, ItemGoldenCarrot.class);
        register0(GOLDEN_CHESTPLATE, ItemGoldenChestplate.class);
        register0(GOLDEN_HELMET, ItemGoldenHelmet.class);
        register0(GOLDEN_HOE, ItemGoldenHoe.class);
        register0(GOLDEN_HORSE_ARMOR, ItemGoldenHorseArmor.class);
        register0(GOLDEN_LEGGINGS, ItemGoldenLeggings.class);
        register0(GOLDEN_PICKAXE, ItemGoldenPickaxe.class);
        register0(GOLDEN_SHOVEL, ItemGoldenShovel.class);
        register0(GOLDEN_SWORD, ItemGoldenSword.class);
        register0(GRAY_DYE, ItemGrayDye.class);
        register0(GREEN_DYE, ItemGreenDye.class);
        register0(GUARDIAN_SPAWN_EGG, ItemGuardianSpawnEgg.class);
        register0(GUNPOWDER, ItemGunpowder.class);
        register0(HEART_OF_THE_SEA, ItemHeartOfTheSea.class);
        register0(HEART_POTTERY_SHERD, ItemHeartPotterySherd.class);
        register0(HEARTBREAK_POTTERY_SHERD, ItemHeartbreakPotterySherd.class);
        register0(HOGLIN_SPAWN_EGG, ItemHoglinSpawnEgg.class);
        register0(HONEY_BOTTLE, ItemHoneyBottle.class);
        register0(HONEYCOMB, ItemHoneycomb.class);
        register0(HOPPER, ItemHopper.class);
        register0(HOPPER_MINECART, ItemHopperMinecart.class);
        register0(HORSE_SPAWN_EGG, ItemHorseSpawnEgg.class);
        register0(HOST_ARMOR_TRIM_SMITHING_TEMPLATE, ItemHostArmorTrimSmithingTemplate.class);
        register0(HOWL_POTTERY_SHERD, ItemHowlPotterySherd.class);
        register0(HUSK_SPAWN_EGG, ItemHuskSpawnEgg.class);
//        register0(ICE_BOMB, ItemIceBomb.class); //EDU
        register0(INK_SAC, ItemInkSac.class);
        register0(IRON_AXE, ItemIronAxe.class);
        register0(IRON_BOOTS, ItemIronBoots.class);
        register0(IRON_CHESTPLATE, ItemIronChestplate.class);
        register0(IRON_DOOR, ItemIronDoor.class);
        register0(IRON_GOLEM_SPAWN_EGG, ItemIronGolemSpawnEgg.class);
        register0(IRON_HELMET, ItemIronHelmet.class);
        register0(IRON_HOE, ItemIronHoe.class);
        register0(IRON_HORSE_ARMOR, ItemIronHorseArmor.class);
        register0(IRON_INGOT, ItemIronIngot.class);
        register0(IRON_LEGGINGS, ItemIronLeggings.class);
        register0(IRON_NUGGET, ItemIronNugget.class);
        register0(IRON_PICKAXE, ItemIronPickaxe.class);
        register0(IRON_SHOVEL, ItemIronShovel.class);
        register0(IRON_SWORD, ItemIronSword.class);
        register0(JUNGLE_BOAT, ItemJungleBoat.class);
        register0(JUNGLE_CHEST_BOAT, ItemJungleChestBoat.class);
        register0(JUNGLE_DOOR, ItemJungleDoor.class);
        register0(JUNGLE_HANGING_SIGN, ItemJungleHangingSign.class);
        register0(JUNGLE_SIGN, ItemJungleSign.class);
        register0(KELP, ItemKelp.class);
        register0(LAPIS_LAZULI, ItemLapisLazuli.class);
        register0(LAVA_BUCKET, ItemLavaBucket.class);
        register0(LEAD, ItemLead.class);
        register0(LEATHER, ItemLeather.class);
        register0(LEATHER_BOOTS, ItemLeatherBoots.class);
        register0(LEATHER_CHESTPLATE, ItemLeatherChestplate.class);
        register0(LEATHER_HELMET, ItemLeatherHelmet.class);
        register0(LEATHER_HORSE_ARMOR, ItemLeatherHorseArmor.class);
        register0(LEATHER_LEGGINGS, ItemLeatherLeggings.class);
        register0(LIGHT_BLUE_DYE, ItemLightBlueDye.class);
        register0(LIGHT_GRAY_DYE, ItemLightGrayDye.class);
        register0(LIME_DYE, ItemLimeDye.class);
        register0(LINGERING_POTION, ItemLingeringPotion.class);
        register0(LLAMA_SPAWN_EGG, ItemLlamaSpawnEgg.class);
        register0(LODESTONE_COMPASS, ItemLodestoneCompass.class);
        register0(LOG, ItemLog.class);
        register0(LOG2, ItemLog2.class);
        register0(MAGENTA_DYE, ItemMagentaDye.class);
        register0(MAGMA_CREAM, ItemMagmaCream.class);
        register0(MAGMA_CUBE_SPAWN_EGG, ItemMagmaCubeSpawnEgg.class);
        register0(MANGROVE_BOAT, ItemMangroveBoat.class);
        register0(MANGROVE_CHEST_BOAT, ItemMangroveChestBoat.class);
        register0(MANGROVE_DOOR, ItemMangroveDoor.class);
        register0(MANGROVE_HANGING_SIGN, ItemMangroveHangingSign.class);
        register0(MANGROVE_SIGN, ItemMangroveSign.class);
//        register0(MEDICINE, ItemMedicine.class);//edu
        register0(MELON_SEEDS, ItemMelonSeeds.class);
        register0(MELON_SLICE, ItemMelonSlice.class);
        register0(MILK_BUCKET, ItemMilkBucket.class);
        register0(MINECART, ItemMinecart.class);
        register0(MINER_POTTERY_SHERD, ItemMinerPotterySherd.class);
        register0(MOJANG_BANNER_PATTERN, ItemMojangBannerPattern.class);
        register0(MOOSHROOM_SPAWN_EGG, ItemMooshroomSpawnEgg.class);
        register0(MOURNER_POTTERY_SHERD, ItemMournerPotterySherd.class);
        register0(MULE_SPAWN_EGG, ItemMuleSpawnEgg.class);
        register0(MUSHROOM_STEW, ItemMushroomStew.class);
        register0(MUSIC_DISC_11, ItemMusicDisc11.class);
        register0(MUSIC_DISC_13, ItemMusicDisc13.class);
        register0(MUSIC_DISC_5, ItemMusicDisc5.class);
        register0(MUSIC_DISC_BLOCKS, ItemMusicDiscBlocks.class);
        register0(MUSIC_DISC_CAT, ItemMusicDiscCat.class);
        register0(MUSIC_DISC_CHIRP, ItemMusicDiscChirp.class);
        register0(MUSIC_DISC_FAR, ItemMusicDiscFar.class);
        register0(MUSIC_DISC_MALL, ItemMusicDiscMall.class);
        register0(MUSIC_DISC_MELLOHI, ItemMusicDiscMellohi.class);
        register0(MUSIC_DISC_OTHERSIDE, ItemMusicDiscOtherside.class);
        register0(MUSIC_DISC_PIGSTEP, ItemMusicDiscPigstep.class);
        register0(MUSIC_DISC_RELIC, ItemMusicDiscRelic.class);
        register0(MUSIC_DISC_STAL, ItemMusicDiscStal.class);
        register0(MUSIC_DISC_STRAD, ItemMusicDiscStrad.class);
        register0(MUSIC_DISC_WAIT, ItemMusicDiscWait.class);
        register0(MUSIC_DISC_WARD, ItemMusicDiscWard.class);
        register0(MUTTON, ItemMutton.class);
        register0(NAME_TAG, ItemNameTag.class);
        register0(NAUTILUS_SHELL, ItemNautilusShell.class);
        register0(NETHER_SPROUTS, ItemNetherSprouts.class);
        register0(NETHER_STAR, ItemNetherStar.class);
        register0(NETHER_WART, ItemNetherWart.class);
        register0(NETHERBRICK, ItemNetherbrick.class);
        register0(NETHERITE_AXE, ItemNetheriteAxe.class);
        register0(NETHERITE_BOOTS, ItemNetheriteBoots.class);
        register0(NETHERITE_CHESTPLATE, ItemNetheriteChestplate.class);
        register0(NETHERITE_HELMET, ItemNetheriteHelmet.class);
        register0(NETHERITE_HOE, ItemNetheriteHoe.class);
        register0(NETHERITE_INGOT, ItemNetheriteIngot.class);
        register0(NETHERITE_LEGGINGS, ItemNetheriteLeggings.class);
        register0(NETHERITE_PICKAXE, ItemNetheritePickaxe.class);
        register0(NETHERITE_SCRAP, ItemNetheriteScrap.class);
        register0(NETHERITE_SHOVEL, ItemNetheriteShovel.class);
        register0(NETHERITE_SWORD, ItemNetheriteSword.class);
        register0(NETHERITE_UPGRADE_SMITHING_TEMPLATE, ItemNetheriteUpgradeSmithingTemplate.class);
        register0(NPC_SPAWN_EGG, ItemNpcSpawnEgg.class);
        register0(OAK_BOAT, ItemOakBoat.class);
        register0(OAK_CHEST_BOAT, ItemOakChestBoat.class);
        register0(OAK_HANGING_SIGN, ItemOakHangingSign.class);
        register0(OAK_SIGN, ItemOakSign.class);
        register0(OCELOT_SPAWN_EGG, ItemOcelotSpawnEgg.class);
        register0(ORANGE_DYE, ItemOrangeDye.class);
        register0(OXIDIZED_COPPER_DOOR, ItemOxidizedCopperDoor.class);
        register0(PAINTING, ItemPainting.class);
        register0(PANDA_SPAWN_EGG, ItemPandaSpawnEgg.class);
        register0(PAPER, ItemPaper.class);
        register0(PARROT_SPAWN_EGG, ItemParrotSpawnEgg.class);
        register0(PHANTOM_MEMBRANE, ItemPhantomMembrane.class);
        register0(PHANTOM_SPAWN_EGG, ItemPhantomSpawnEgg.class);
        register0(PIG_SPAWN_EGG, ItemPigSpawnEgg.class);
        register0(PIGLIN_BANNER_PATTERN, ItemPiglinBannerPattern.class);
        register0(PIGLIN_BRUTE_SPAWN_EGG, ItemPiglinBruteSpawnEgg.class);
        register0(PIGLIN_SPAWN_EGG, ItemPiglinSpawnEgg.class);
        register0(PILLAGER_SPAWN_EGG, ItemPillagerSpawnEgg.class);
        register0(PINK_DYE, ItemPinkDye.class);
        //register0(PITCHER_POD, ItemPitcherPod.class); todo
        register0(PLANKS, ItemPlanks.class);
        register0(PLENTY_POTTERY_SHERD, ItemPlentyPotterySherd.class);
        register0(POISONOUS_POTATO, ItemPoisonousPotato.class);
        register0(POLAR_BEAR_SPAWN_EGG, ItemPolarBearSpawnEgg.class);
        register0(POPPED_CHORUS_FRUIT, ItemPoppedChorusFruit.class);
        register0(PORKCHOP, ItemPorkchop.class);
        register0(POTATO, ItemPotato.class);
        register0(POTION, ItemPotion.class);
        register0(POWDER_SNOW_BUCKET, ItemPowderSnowBucket.class);
        register0(PRISMARINE_CRYSTALS, ItemPrismarineCrystals.class);
        register0(PRISMARINE_SHARD, ItemPrismarineShard.class);
        register0(PRIZE_POTTERY_SHERD, ItemPrizePotterySherd.class);
        register0(PUFFERFISH, ItemPufferfish.class);
        register0(PUFFERFISH_BUCKET, ItemPufferfishBucket.class);
        register0(PUFFERFISH_SPAWN_EGG, ItemPufferfishSpawnEgg.class);
        register0(PUMPKIN_PIE, ItemPumpkinPie.class);
        register0(PUMPKIN_SEEDS, ItemPumpkinSeeds.class);
        register0(PURPLE_DYE, ItemPurpleDye.class);
        register0(QUARTZ, ItemQuartz.class);
        register0(RABBIT, ItemRabbit.class);
        register0(RABBIT_FOOT, ItemRabbitFoot.class);
        register0(RABBIT_HIDE, ItemRabbitHide.class);
        register0(RABBIT_SPAWN_EGG, ItemRabbitSpawnEgg.class);
        register0(RABBIT_STEW, ItemRabbitStew.class);
        register0(RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, ItemRaiserArmorTrimSmithingTemplate.class);
        //register0(RAPID_FERTILIZER, ItemRapidFertilizer.class); //edu
        register0(RAVAGER_SPAWN_EGG, ItemRavagerSpawnEgg.class);
        register0(RAW_COPPER, ItemRawCopper.class);
        register0(RAW_GOLD, ItemRawGold.class);
        register0(RAW_IRON, ItemRawIron.class);
        register0(RECOVERY_COMPASS, ItemRecoveryCompass.class);
        register0(RED_DYE, ItemRedDye.class);
        register0(REDSTONE, ItemRedstone.class);
        register0(REPEATER, ItemRepeater.class);
        register0(RIB_ARMOR_TRIM_SMITHING_TEMPLATE, ItemRibArmorTrimSmithingTemplate.class);
        register0(ROTTEN_FLESH, ItemRottenFlesh.class);
        register0(SADDLE, ItemSaddle.class);
        register0(SALMON, ItemSalmon.class);
        register0(SALMON_BUCKET, ItemSalmonBucket.class);
        register0(SALMON_SPAWN_EGG, ItemSalmonSpawnEgg.class);
        register0(SCUTE, ItemScute.class);
        register0(SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSentryArmorTrimSmithingTemplate.class);
        register0(SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, ItemShaperArmorTrimSmithingTemplate.class);
        register0(SHEAF_POTTERY_SHERD, ItemSheafPotterySherd.class);
        register0(SHEARS, ItemShears.class);
        register0(SHEEP_SPAWN_EGG, ItemSheepSpawnEgg.class);
        register0(SHELTER_POTTERY_SHERD, ItemShelterPotterySherd.class);
        register0(SHIELD, ItemShield.class);
        register0(SHULKER_BOX, ItemShulkerBox.class);
        register0(SHULKER_SHELL, ItemShulkerShell.class);
        register0(SHULKER_SPAWN_EGG, ItemShulkerSpawnEgg.class);
        register0(SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSilenceArmorTrimSmithingTemplate.class);
        register0(SILVERFISH_SPAWN_EGG, ItemSilverfishSpawnEgg.class);
        register0(SKELETON_HORSE_SPAWN_EGG, ItemSkeletonHorseSpawnEgg.class);
        register0(SKELETON_SPAWN_EGG, ItemSkeletonSpawnEgg.class);
        register0(SKULL, ItemSkull.class);
        register0(SKULL_BANNER_PATTERN, ItemSkullBannerPattern.class);
        register0(SKULL_POTTERY_SHERD, ItemSkullPotterySherd.class);
        register0(SLIME_BALL, ItemSlimeBall.class);
        register0(SLIME_SPAWN_EGG, ItemSlimeSpawnEgg.class);
        register0(SNIFFER_SPAWN_EGG, ItemSnifferSpawnEgg.class);
        register0(SNORT_POTTERY_SHERD, ItemSnortPotterySherd.class);
        register0(SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSnoutArmorTrimSmithingTemplate.class);
        register0(SNOW_GOLEM_SPAWN_EGG, ItemSnowGolemSpawnEgg.class);
        register0(SNOWBALL, ItemSnowball.class);
        register0(SOUL_CAMPFIRE, ItemSoulCampfire.class);
//        register0(SPARKLER, ItemSparkler.class);//edu
        register0(SPAWN_EGG, ItemSpawnEgg.class);
        register0(SPIDER_EYE, ItemSpiderEye.class);
        register0(SPIDER_SPAWN_EGG, ItemSpiderSpawnEgg.class);
        register0(SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSpireArmorTrimSmithingTemplate.class);
        register0(SPLASH_POTION, ItemSplashPotion.class);
        register0(SPRUCE_BOAT, ItemSpruceBoat.class);
        register0(SPRUCE_CHEST_BOAT, ItemSpruceChestBoat.class);
        register0(SPRUCE_DOOR, ItemSpruceDoor.class);
        register0(SPRUCE_HANGING_SIGN, ItemSpruceHangingSign.class);
        register0(SPRUCE_SIGN, ItemSpruceSign.class);
        register0(SPYGLASS, ItemSpyglass.class);
        register0(SQUID_SPAWN_EGG, ItemSquidSpawnEgg.class);
        register0(STAINED_GLASS, ItemStainedGlass.class);
        register0(STAINED_GLASS_PANE, ItemStainedGlassPane.class);
        register0(STAINED_HARDENED_CLAY, ItemStainedHardenedClay.class);
        register0(STICK, ItemStick.class);
        register0(STONE_AXE, ItemStoneAxe.class);
        register0(STONE_HOE, ItemStoneHoe.class);
        register0(STONE_PICKAXE, ItemStonePickaxe.class);
        register0(STONE_SHOVEL, ItemStoneShovel.class);
        register0(STONE_SWORD, ItemStoneSword.class);
        register0(STRAY_SPAWN_EGG, ItemStraySpawnEgg.class);
        register0(STRIDER_SPAWN_EGG, ItemStriderSpawnEgg.class);
        register0(STRING, ItemString.class);
        register0(SUGAR, ItemSugar.class);
        register0(SUGAR_CANE, ItemSugarCane.class);
        register0(SUSPICIOUS_STEW, ItemSuspiciousStew.class);
        register0(SWEET_BERRIES, ItemSweetBerries.class);
        register0(TADPOLE_BUCKET, ItemTadpoleBucket.class);
        register0(TADPOLE_SPAWN_EGG, ItemTadpoleSpawnEgg.class);
        register0(TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemTideArmorTrimSmithingTemplate.class);
        register0(TNT_MINECART, ItemTntMinecart.class);
        register0(TORCHFLOWER_SEEDS, ItemTorchflowerSeeds.class);
        register0(TOTEM_OF_UNDYING, ItemTotemOfUndying.class);
        register0(TRADER_LLAMA_SPAWN_EGG, ItemTraderLlamaSpawnEgg.class);
        register0(TRIDENT, ItemTrident.class);
        register0(TROPICAL_FISH, ItemTropicalFish.class);
        register0(TROPICAL_FISH_BUCKET, ItemTropicalFishBucket.class);
        register0(TROPICAL_FISH_SPAWN_EGG, ItemTropicalFishSpawnEgg.class);
        register0(TURTLE_HELMET, ItemTurtleHelmet.class);
        register0(TURTLE_SPAWN_EGG, ItemTurtleSpawnEgg.class);
        register0(VEX_ARMOR_TRIM_SMITHING_TEMPLATE, ItemVexArmorTrimSmithingTemplate.class);
        register0(VEX_SPAWN_EGG, ItemVexSpawnEgg.class);
        register0(VILLAGER_SPAWN_EGG, ItemVillagerSpawnEgg.class);
        register0(VINDICATOR_SPAWN_EGG, ItemVindicatorSpawnEgg.class);
        register0(WANDERING_TRADER_SPAWN_EGG, ItemWanderingTraderSpawnEgg.class);
        register0(WARD_ARMOR_TRIM_SMITHING_TEMPLATE, ItemWardArmorTrimSmithingTemplate.class);
        register0(WARDEN_SPAWN_EGG, ItemWardenSpawnEgg.class);
        register0(WARPED_DOOR, ItemWarpedDoor.class);
        register0(WARPED_FUNGUS_ON_A_STICK, ItemWarpedFungusOnAStick.class);
        register0(WARPED_HANGING_SIGN, ItemWarpedHangingSign.class);
        register0(WARPED_SIGN, ItemWarpedSign.class);
        register0(WATER_BUCKET, ItemWaterBucket.class);
        register0(WAXED_COPPER_DOOR, ItemWaxedCopperDoor.class);
        register0(WAXED_EXPOSED_COPPER_DOOR, ItemWaxedExposedCopperDoor.class);
        register0(WAXED_OXIDIZED_COPPER_DOOR, ItemWaxedOxidizedCopperDoor.class);
        register0(WAXED_WEATHERED_COPPER_DOOR, ItemWaxedWeatheredCopperDoor.class);
        register0(WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, ItemWayfinderArmorTrimSmithingTemplate.class);
        register0(WEATHERED_COPPER_DOOR, ItemWeatheredCopperDoor.class);
        register0(WHEAT, ItemWheat.class);
        register0(WHEAT_SEEDS, ItemWheatSeeds.class);
        register0(WHITE_DYE, ItemWhiteDye.class);
        register0(WILD_ARMOR_TRIM_SMITHING_TEMPLATE, ItemWildArmorTrimSmithingTemplate.class);
        register0(WITCH_SPAWN_EGG, ItemWitchSpawnEgg.class);
        register0(WITHER_SKELETON_SPAWN_EGG, ItemWitherSkeletonSpawnEgg.class);
        register0(WITHER_SPAWN_EGG, ItemWitherSpawnEgg.class);
        register0(WOLF_SPAWN_EGG, ItemWolfSpawnEgg.class);
        register0(WOODEN_AXE, ItemWoodenAxe.class);
        register0(WOODEN_DOOR, ItemWoodenDoor.class);
        register0(WOODEN_HOE, ItemWoodenHoe.class);
        register0(WOODEN_PICKAXE, ItemWoodenPickaxe.class);
        register0(WOODEN_SHOVEL, ItemWoodenShovel.class);
        register0(WOODEN_SWORD, ItemWoodenSword.class);
        register0(WOOL, ItemWool.class);
        register0(WRITABLE_BOOK, ItemWritableBook.class);
        register0(WRITTEN_BOOK, ItemWrittenBook.class);
        register0(YELLOW_DYE, ItemYellowDye.class);
        register0(ZOGLIN_SPAWN_EGG, ItemZoglinSpawnEgg.class);
        register0(ZOMBIE_HORSE_SPAWN_EGG, ItemZombieHorseSpawnEgg.class);
        register0(ZOMBIE_PIGMAN_SPAWN_EGG, ItemZombiePigmanSpawnEgg.class);
        register0(ZOMBIE_SPAWN_EGG, ItemZombieSpawnEgg.class);
        register0(ZOMBIE_VILLAGER_SPAWN_EGG, ItemZombieVillagerSpawnEgg.class);
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

    public Item get(String id, Integer meta) {
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

    public Item get(String id, Integer meta, int count) {
        try {
            var c = CACHE_CONSTRUCTORS.get(id);
            if (c == null) return null;
            Item item = (Item) c.invoke();
            item.setDamage(meta);
            item.setCount(count);
            return item;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Item get(String id, Integer meta, int count, CompoundTag tags) {
        try {
            var c = CACHE_CONSTRUCTORS.get(id);
            if (c == null) return null;
            Item item = (Item) c.invoke();
            item.setDamage(meta);
            item.setCount(count);
            item.setCompoundTag(tags);
            return item;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Item get(String id, Integer meta, int count, byte[] tags) {
        try {
            var c = CACHE_CONSTRUCTORS.get(id);
            if (c == null) return null;
            Item item = (Item) c.invoke();
            item.setDamage(meta);
            item.setCount(count);
            item.setCompoundTag(tags);
            return item;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void trim() {
        CACHE_CONSTRUCTORS.trim();
    }

    @Override
    public void register(String key, Class<? extends Item> value) throws RegisterException {
        try {
            FastConstructor<? extends Item> c = FastConstructor.create(value.getConstructor());
            if (CACHE_CONSTRUCTORS.putIfAbsent(key, c) == null) {
            } else {
                throw new RegisterException("This item has already been registered with the identifier: " + key);
            }
        } catch (NoSuchMethodException e) {
            throw new RegisterException(e);
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
