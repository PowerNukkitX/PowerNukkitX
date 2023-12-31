package cn.nukkit.registry;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.item.*;
import cn.nukkit.entity.mob.*;
import cn.nukkit.entity.passive.*;
import cn.nukkit.entity.projectile.*;
import cn.nukkit.entity.weather.EntityLightningBolt;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.OK;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

@Slf4j
public class EntityRegistry extends BaseRegistry<EntityRegistry.EntityDefinition, Class<? extends Entity>, Class<? extends Entity>> implements EntityID {
    private static final Object2ObjectOpenHashMap<String, Class<? extends Entity>> CLASS = new Object2ObjectOpenHashMap<>();
    private static final Int2ObjectOpenHashMap<String> RID2ID = new Int2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, EntityRegistry.EntityDefinition> DEFINITIONS = new Object2ObjectOpenHashMap<>();

    @Override
    public void init() {
        register(new EntityDefinition(CHICKEN, "", 10, true, true), EntityChicken.class);
        register(new EntityDefinition(COW, "", 11, true, true), EntityCow.class);
        register(new EntityDefinition(PIG, "", 12, true, true), EntityPig.class);
        register(new EntityDefinition(SHEEP, "", 13, true, true), EntitySheep.class);
        register(new EntityDefinition(WOLF, "", 14, true, true), EntityWolf.class);
        register(new EntityDefinition(VILLAGER, "", 15, false, true), EntityVillager.class);
        register(new EntityDefinition(MOOSHROOM, "", 16, true, true), EntityMooshroom.class);
        register(new EntityDefinition(SQUID, "", 17, true, true), EntitySquid.class);
        register(new EntityDefinition(RABBIT, "", 18, true, true), EntityRabbit.class);
        register(new EntityDefinition(BAT, "", 19, true, true), EntityBat.class);
        register(new EntityDefinition(IRON_GOLEM, "", 20, true, true), EntityIronGolem.class);
        register(new EntityDefinition(SNOW_GOLEM, "", 21, true, true), EntitySnowGolem.class);
        register(new EntityDefinition(OCELOT, "", 22, true, true), EntityOcelot.class);
        register(new EntityDefinition(HORSE, "", 23, true, true), EntityHorse.class);
        register(new EntityDefinition(DONKEY, "", 24, true, true), EntityDonkey.class);
        register(new EntityDefinition(MULE, "", 25, true, true), EntityMule.class);
        register(new EntityDefinition(SKELETON_HORSE, "", 26, true, true), EntitySkeletonHorse.class);
        register(new EntityDefinition(ZOMBIE_HORSE, "", 27, true, true), EntityZombieHorse.class);
        register(new EntityDefinition(POLAR_BEAR, "", 28, true, true), EntityPolarBear.class);
        register(new EntityDefinition(LLAMA, "", 29, true, true), EntityLlamaSpit.class);
        register(new EntityDefinition(PARROT, "", 30, true, true), EntityParrot.class);
        register(new EntityDefinition(DOLPHIN, "", 31, true, true), EntityDolphin.class);
        register(new EntityDefinition(ZOMBIE, "", 32, true, true), EntityZombie.class);
        register(new EntityDefinition(CREEPER, "", 33, true, true), EntityCreeper.class);
        register(new EntityDefinition(SKELETON, "", 34, true, true), EntitySkeleton.class);
        register(new EntityDefinition(SPIDER, "", 35, true, true), EntitySpider.class);
        register(new EntityDefinition(ZOMBIE_PIGMAN, "", 36, true, true), EntityZombiePigman.class);
        register(new EntityDefinition(SLIME, "", 37, true, true), EntitySlime.class);
        register(new EntityDefinition(ENDERMAN, "", 38, true, true), EntityEnderman.class);
        register(new EntityDefinition(SILVERFISH, "", 39, true, true), EntitySilverfish.class);
        register(new EntityDefinition(CAVE_SPIDER, "", 40, true, true), EntityCaveSpider.class);
        register(new EntityDefinition(GHAST, "", 41, true, true), EntityGhast.class);
        register(new EntityDefinition(MAGMA_CUBE, "", 42, true, true), EntityMagmaCube.class);
        register(new EntityDefinition(BLAZE, "", 43, true, true), EntityBlaze.class);
        register(new EntityDefinition(ZOMBIE_VILLAGER, "", 44, false, true), EntityZombieVillager.class);
        register(new EntityDefinition(WITCH, "", 45, true, true), EntityWitch.class);
        register(new EntityDefinition(STRAY, "", 46, true, true), EntityStray.class);
        register(new EntityDefinition(HUSK, "", 47, true, true), EntityHusk.class);
        register(new EntityDefinition(WITHER_SKELETON, "", 48, true, true), EntityWitherSkeleton.class);
        register(new EntityDefinition(GUARDIAN, "", 49, true, true), EntityGuardian.class);
        register(new EntityDefinition(ELDER_GUARDIAN, "", 50, true, true), EntityElderGuardian.class);
        register(new EntityDefinition(NPC, "", 51, true, true), EntityNpc.class);
        register(new EntityDefinition(WITHER, "", 52, true, true), EntityWither.class);
        register(new EntityDefinition(ENDER_DRAGON, "", 53, true, true), EntityEnderDragon.class);
        register(new EntityDefinition(SHULKER, "", 54, true, true), EntityShulker.class);
        register(new EntityDefinition(ENDERMITE, "", 55, true, true), EntityEndermite.class);
//        register(new EntityDefinition(AGENT, "", 56, false, false), EntityAgent.class);
        register(new EntityDefinition(VINDICATOR, "", 57, true, true), EntityVindicator.class);
        register(new EntityDefinition(PHANTOM, "", 58, true, true), EntityPhantom.class);
        register(new EntityDefinition(RAVAGER, "", 59, true, true), EntityRavager.class);
        register(new EntityDefinition(ARMOR_STAND, "", 61, false, true), EntityArmorStand.class);
//        register(new EntityDefinition(TRIPOD_CAMERA, "", 62, false, false), EntityTripodCamera.class);
        register(new EntityDefinition(ITEM, "", 64, false, false), EntityItem.class);
        register(new EntityDefinition(TNT, "", 65, false, true), EntityTnt.class);
        register(new EntityDefinition(FALLING_BLOCK, "", 66, false, false), EntityFallingBlock.class);
        register(new EntityDefinition(XP_BOTTLE, "", 68, false, true), EntityXpBottle.class);
        register(new EntityDefinition(XP_ORB, "", 69, false, true), EntityXpOrb.class);
//        register(new EntityDefinition(EYE_OF_ENDER_SIGNAL, "", 70, false, false), EntityEyeOfEnderSignal.class);
        register(new EntityDefinition(ENDER_CRYSTAL, "", 71, false, true), EntityEnderCrystal.class);
        register(new EntityDefinition(FIREWORKS_ROCKET, "", 72, false, true), EntityFireworksRocket.class);
        register(new EntityDefinition(THROWN_TRIDENT, "", 73, false, false), EntityThrownTrident.class);
        register(new EntityDefinition(TURTLE, "", 74, true, true), EntityTurtle.class);
        register(new EntityDefinition(CAT, "", 75, true, true), EntityCat.class);
//        register(new EntityDefinition(SHULKER_BULLET, "", 76, false, false), EntityShulkerBullet.class);
        register(new EntityDefinition(FISHING_HOOK, "", 77, false, false), EntityFishingHook.class);
//        register(new EntityDefinition(DRAGON_FIREBALL, "", 79, false, false), EntityDragonFireball.class);
        register(new EntityDefinition(ARROW, "", 80, false, true), EntityArrow.class);
        register(new EntityDefinition(SNOWBALL, "", 81, false, true), EntitySnowball.class);
        register(new EntityDefinition(EGG, "", 82, false, true), EntityEgg.class);
        register(new EntityDefinition(PAINTING, "", 83, false, false), EntityPainting.class);
        register(new EntityDefinition(MINECART, "", 84, false, true), EntityMinecart.class);
//        register(new EntityDefinition(FIREBALL, "", 85, false, false), EntityFireball.class);
        register(new EntityDefinition(SPLASH_POTION, "", 86, false, true), EntitySplashPotion.class);
        register(new EntityDefinition(ENDER_PEARL, "", 87, false, false), EntityEnderPearl.class);
//        register(new EntityDefinition(LEASH_KNOT, "", 88, false, true), EntityLeashKnot.class);
//        register(new EntityDefinition(WITHER_SKULL, "", 89, false, false), EntityWitherSkull.class);//This is the skull fired by Wither
        register(new EntityDefinition(BOAT, "", 90, false, true), EntityBoat.class);
//        register(new EntityDefinition(WITHER_SKULL_DANGEROUS, "", 91, false, false), EntityWitherSkullDangerous.class);
        register(new EntityDefinition(LIGHTNING_BOLT, "", 93, false, true), EntityLightningBolt.class);
        register(new EntityDefinition(SMALL_FIREBALL, "", 94, false, false), EntitySmallFireball.class);
        register(new EntityDefinition(AREA_EFFECT_CLOUD, "", 95, false, false), EntityAreaEffectCloud.class);
        register(new EntityDefinition(HOPPER_MINECART, "", 96, false, true), EntityHopperMinecart.class);
        register(new EntityDefinition(TNT_MINECART, "", 97, false, true), EntityTntMinecart.class);
        register(new EntityDefinition(CHEST_MINECART, "", 98, false, true), EntityChestMinecart.class);
//        register(new EntityDefinition(COMMAND_BLOCK_MINECART, "", 100, false, true), EntityCommandBlockMinecart.class);
        register(new EntityDefinition(LINGERING_POTION, "", 101, false, false), EntityLingeringPotion.class);
        register(new EntityDefinition(LLAMA_SPIT, "", 102, false, false), EntityLlamaSpit.class);
//        register(new EntityDefinition(EVOCATION_FANG, "", 103, false, true), EntityEvocationFang.class);
//        register(new EntityDefinition(EVOCATION_ILLAGER, "", 104, true, true), EntityEvocationIllager.class);
        register(new EntityDefinition(VEX, "", 105, true, true), EntityVex.class);
//        register(new EntityDefinition(ICE_BOMB, "", 106, false, false), EntityIceBomb.class);
//        register(new EntityDefinition(BALLOON, "", 107, false, false), EntityBalloon.class);
        register(new EntityDefinition(PUFFERFISH, "", 108, true, true), EntityPufferfish.class);
        register(new EntityDefinition(SALMON, "", 109, true, true), EntitySalmon.class);
        register(new EntityDefinition(DROWNED, "", 110, true, true), EntityDrowned.class);
        register(new EntityDefinition(TROPICALFISH, "", 111, true, true), EntityTropicalfish.class);
        register(new EntityDefinition(COD, "", 112, true, true), EntityCod.class);
        register(new EntityDefinition(PANDA, "", 113, true, true), EntityPanda.class);
        register(new EntityDefinition(PILLAGER, "", 114, true, true), EntityPillager.class);
        register(new EntityDefinition(VILLAGER_V2, "", 115, true, false), EntityVillagerV2.class);
        register(new EntityDefinition(ZOMBIE_VILLAGER_V2, "", 116, true, false), EntityZombieVillagerV2.class);
        register(new EntityDefinition(WANDERING_TRADER, "", 118, true, true), EntityTraderLlama.class);
//        register(new EntityDefinition(ELDER_GUARDIAN_GHOST, "", 120, false, true), EntityElderGuardianGhost.class);
        register(new EntityDefinition(FOX, "", 121, true, true), EntityFox.class);
        register(new EntityDefinition(BEE, "", 122, true, true), EntityBee.class);
        register(new EntityDefinition(PIGLIN, "", 123, true, true), EntityPiglin.class);
        register(new EntityDefinition(HOGLIN, "", 124, true, true), EntityHoglin.class);
        register(new EntityDefinition(STRIDER, "", 125, true, true), EntityStrider.class);
        register(new EntityDefinition(ZOGLIN, "", 126, true, true), EntityZoglin.class);
        register(new EntityDefinition(PIGLIN_BRUTE, "", 127, true, true), EntityPiglinBrute.class);
        register(new EntityDefinition(GOAT, "", 128, true, true), EntityGoat.class);
        register(new EntityDefinition(GLOW_SQUID, "", 129, true, true), EntityGlowSquid.class);
        register(new EntityDefinition(AXOLOTL, "", 130, true, true), EntityAxolotl.class);
        register(new EntityDefinition(WARDEN, "", 131, true, true), EntityWarden.class);
        register(new EntityDefinition(FROG, "", 132, true, true), EntityFrog.class);
        register(new EntityDefinition(TADPOLE, "", 133, true, true), EntityTadpole.class);
        register(new EntityDefinition(ALLAY, "", 134, true, true), EntityAllay.class);
        register(new EntityDefinition(CAMEL, "", 138, true, true), EntityCamel.class);
//        register(new EntityDefinition(SNIFFER, "", 139, true, true), EntitySniffer.class);
        register(new EntityDefinition(TRADER_LLAMA, "", 157, true, true), EntityTraderLlama.class);
        register(new EntityDefinition(CHEST_BOAT, "", 218, false, true), EntityChestBoat.class);
        register(new EntityDefinition(PLAYER, "minecraft:", 257, false, false), Player.class);
    }

    public Class<? extends Entity> getEntityClass(String id) {
        return CLASS.get(id);
    }

    public String getEntityIdFromNetworkId(int networkId) {
        return RID2ID.get(networkId);
    }

    public EntityRegistry.EntityDefinition getEntityDefinition(String id) {
        return DEFINITIONS.get(id);
    }

    public EntityDefinition[] getEntityDefinitions() {
        return DEFINITIONS.values().toArray(EntityDefinition[]::new);
    }

    /**
     * 获得全部实体的网络id
     * <p>
     * Get the network id of all entities
     *
     * @return the known entity ids
     */
    @UnmodifiableView
    public Set<Integer> getKnownEntityIds() {
        return Collections.unmodifiableSet(RID2ID.keySet());
    }

    /**
     * 获取全部已经注册的实体，包括自定义实体
     * <p>
     * Get all registered entities, including custom entities
     *
     * @return the known entities
     */
    @UnmodifiableView
    public static Map<String, Class<? extends Entity>> getKnownEntities() {
        return Collections.unmodifiableMap(CLASS);
    }

    public Entity provideEntity(String id, @NotNull IChunk chunk, @NotNull CompoundTag nbt, @Nullable Object... args) {
        Class<? extends Entity> clazz = getEntityClass(id);
        Preconditions.checkNotNull(clazz);

        Entity entity = null;
        List<Exception> exceptions = null;
        for (var constructor : clazz.getConstructors()) {
            if (entity != null) {
                break;
            }

            if (constructor.getParameterCount() != (args == null ? 2 : args.length + 2)) {
                continue;
            }

            try {
                if (args == null || args.length == 0) {
                    entity = (Entity) constructor.newInstance(chunk, nbt);
                } else {
                    Object[] objects = new Object[args.length + 2];

                    objects[0] = chunk;
                    objects[1] = nbt;
                    System.arraycopy(args, 0, objects, 2, args.length);
                    entity = (Entity) constructor.newInstance(objects);

                }
            } catch (Exception e) {
                if (exceptions == null) {
                    exceptions = new ArrayList<>();
                }
                exceptions.add(e);
            }

        }

        if (entity == null) {
            Exception cause = new IllegalArgumentException("Could not create an entity of identifier " + id, exceptions != null && !exceptions.isEmpty() ? exceptions.get(0) : null);
            if (exceptions != null && exceptions.size() > 1) {
                for (int i = 1; i < exceptions.size(); i++) {
                    cause.addSuppressed(exceptions.get(i));
                }
            }
            log.debug("Could not create an entity of type {} with {} args", id, args == null ? 0 : args.length, cause);
        } else {
            return entity;
        }
        return null;
    }

    @Override
    public Class<? extends Entity> get(EntityDefinition key) {
        return CLASS.get(key.id);
    }

    @Override
    public void trim() {
        CLASS.trim();
        RID2ID.trim();
        DEFINITIONS.trim();
    }

    @Override
    public OK<?> register(EntityDefinition key, Class<? extends Entity> value) {
        if (CLASS.putIfAbsent(key.id(), value) == null) {
            RID2ID.put(key.rid, key.id);
            DEFINITIONS.put(key.id, key);
            return OK.TRUE;
        } else {
            return new OK<>(false, new IllegalArgumentException("This block has already been registered with the identifier: " + key.id));
        }
    }

    public record EntityDefinition(String id, String bid, int rid, boolean hasSpawnegg, boolean summonable) {
    }
}
