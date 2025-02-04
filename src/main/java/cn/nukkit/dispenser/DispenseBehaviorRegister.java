package cn.nukkit.dispenser;

import cn.nukkit.block.BlockID;
import cn.nukkit.entity.EntityID;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.registry.Registries;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author CreeperFace
 */
public final class DispenseBehaviorRegister {

    private static final Map<String, DispenseBehavior> behaviors = new HashMap<>();
    private static final DispenseBehavior defaultBehavior = new DefaultDispenseBehavior();

    public static void registerBehavior(String itemIdentifier, DispenseBehavior behavior) {
        behaviors.put(itemIdentifier, behavior);
    }

    public static DispenseBehavior getBehavior(String identifier) {
        return behaviors.getOrDefault(identifier, defaultBehavior);
    }

    public static void removeDispenseBehavior(String identifier) {
        behaviors.remove(identifier);
    }

    public static void init() {
        registerBehavior(ItemID.SHEARS, new ShearsDispenseBehavior());
        registerBehavior(ItemID.BUCKET, new BucketDispenseBehavior());
        registerBehavior(ItemID.WATER_BUCKET, new BucketDispenseBehavior());
        registerBehavior(ItemID.LAVA_BUCKET, new BucketDispenseBehavior());
        registerBehavior(ItemID.BONE_MEAL, new DyeDispenseBehavior());
        registerBehavior(ItemID.FIREWORK_ROCKET, new FireworksDispenseBehavior());
        registerBehavior(ItemID.FLINT_AND_STEEL, new FlintAndSteelDispenseBehavior());

        registerBehavior(ItemID.OAK_BOAT, new BoatDispenseBehavior());
        registerBehavior(ItemID.BIRCH_BOAT, new BoatDispenseBehavior());
        registerBehavior(ItemID.ACACIA_BOAT, new BoatDispenseBehavior());
        registerBehavior(ItemID.DARK_OAK_BOAT, new BoatDispenseBehavior());
        registerBehavior(ItemID.JUNGLE_BOAT, new BoatDispenseBehavior());
        registerBehavior(ItemID.SPRUCE_BOAT, new BoatDispenseBehavior());
        registerBehavior(ItemID.MANGROVE_BOAT, new BoatDispenseBehavior());
        registerBehavior(ItemID.CHERRY_BOAT, new BoatDispenseBehavior());
        registerBehavior(ItemID.PALE_OAK_BOAT, new BoatDispenseBehavior());
        registerBehavior(ItemID.BAMBOO_RAFT, new BoatDispenseBehavior());

        registerBehavior(ItemID.OAK_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemID.BIRCH_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemID.ACACIA_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemID.DARK_OAK_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemID.JUNGLE_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemID.SPRUCE_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemID.MANGROVE_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemID.CHERRY_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemID.PALE_OAK_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemID.BAMBOO_CHEST_RAFT, new ChestBoatDispenseBehavior());

        registerBehavior(BlockID.UNDYED_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(BlockID.WHITE_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(BlockID.ORANGE_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(BlockID.MAGENTA_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(BlockID.LIGHT_BLUE_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(BlockID.YELLOW_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(BlockID.LIME_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(BlockID.PINK_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(BlockID.GRAY_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(BlockID.LIGHT_GRAY_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(BlockID.CYAN_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(BlockID.PURPLE_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(BlockID.BLUE_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(BlockID.BROWN_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(BlockID.GREEN_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(BlockID.RED_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(BlockID.BLACK_SHULKER_BOX, new ShulkerBoxDispenseBehavior());

        Pattern pattern = Pattern.compile("minecraft:.*_spawn_egg");
        Registries.ITEM.getAll().stream().filter(pattern.asPredicate()).forEach(id -> {
            registerBehavior(id, new SpawnEggDispenseBehavior());
        });

        registerBehavior(BlockID.TNT, new TNTDispenseBehavior());
        registerBehavior(ItemID.ARROW, new ProjectileDispenseBehavior(EntityID.ARROW) {
            @Override
            protected double getMotion() {
                return super.getMotion() * 1.5;
            }
        });
        //TODO: tipped arrow
        //TODO: spectral arrow
        registerBehavior(ItemID.EGG, new ProjectileDispenseBehavior(EntityID.EGG));
        registerBehavior(ItemID.SNOWBALL, new ProjectileDispenseBehavior(EntityID.SNOWBALL));
        registerBehavior(ItemID.WIND_CHARGE, new ProjectileDispenseBehavior(EntityID.WIND_CHARGE_PROJECTILE));
        registerBehavior(ItemID.FIRE_CHARGE, new ProjectileDispenseBehavior(EntityID.SMALL_FIREBALL) {
            @Override
            protected float getAccuracy() {
                return 0;
            }

            @Override
            protected Vector3 initMotion(BlockFace face) {
                return new Vector3(
                        face.getXOffset(),
                        face.getYOffset()/* + 0.1f*/,
                        face.getZOffset())
                        .normalize();
            }

            @Override
            protected Sound getShootingSound() {
                return Sound.MOB_BLAZE_SHOOT;
            }
        });
        registerBehavior(ItemID.EXPERIENCE_BOTTLE, new ProjectileDispenseBehavior(EntityID.XP_BOTTLE) {
            @Override
            protected float getAccuracy() {
                return super.getAccuracy() * 0.5f;
            }

            @Override
            protected double getMotion() {
                return super.getMotion() * 1.25;
            }
        });
        registerBehavior(ItemID.SPLASH_POTION, new ProjectileDispenseBehavior(EntityID.SPLASH_POTION) {
            @Override
            protected float getAccuracy() {
                return super.getAccuracy() * 0.5f;
            }

            @Override
            protected double getMotion() {
                return super.getMotion() * 1.25;
            }
        });
//        registerBehavior(ItemID.LINGERING_POTION, new ProjectileDispenseBehavior("LingeringPotion")); //TODO
        registerBehavior(ItemID.TRIDENT, new ProjectileDispenseBehavior(EntityID.THROWN_TRIDENT) {
            @Override
            protected float getAccuracy() {
                return super.getAccuracy() * 0.5f;
            }

            @Override
            protected double getMotion() {
                return super.getMotion() * 1.25;
            }

            @Override
            protected Sound getShootingSound() {
                return Sound.ITEM_TRIDENT_THROW;
            }
        });
        registerBehavior(ItemID.GLASS_BOTTLE, new GlassBottleDispenseBehavior());
        registerBehavior(ItemID.POTION, new WaterBottleDispenseBehavior());
        registerBehavior(ItemID.MINECART, new MinecartDispenseBehavior(EntityID.MINECART));
        registerBehavior(ItemID.CHEST_MINECART, new MinecartDispenseBehavior(EntityID.CHEST_MINECART));
        registerBehavior(ItemID.HOPPER_MINECART, new MinecartDispenseBehavior(EntityID.HOPPER_MINECART));
        registerBehavior(ItemID.TNT_MINECART, new MinecartDispenseBehavior(EntityID.TNT_MINECART));
        //TODO: 命令方块矿车
    }
}
