package cn.nukkit.registry;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.custom.CustomEntity;
import cn.nukkit.entity.item.*;
import cn.nukkit.entity.mob.*;
import cn.nukkit.entity.passive.*;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityEgg;
import cn.nukkit.entity.projectile.EntityEnderPearl;
import cn.nukkit.entity.projectile.EntitySmallFireball;
import cn.nukkit.entity.projectile.EntitySnowball;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.entity.weather.EntityLightningBolt;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.Plugin;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.sunlan.fastreflection.FastConstructor;
import me.sunlan.fastreflection.FastMemberLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class EntityRegistry implements EntityID, IRegistry<EntityRegistry.EntityDefinition, Class<? extends Entity>, Class<? extends Entity>> {
    private static final Object2ObjectOpenHashMap<String, Class<? extends Entity>> CLASS = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, FastConstructor<? extends Entity>> FAST_NEW = new Object2ObjectOpenHashMap<>();
    private static final Object2IntOpenHashMap<String> ID2RID = new Object2IntOpenHashMap<>();
    private static final Int2ObjectArrayMap<String> RID2ID = new Int2ObjectArrayMap<>();
    private static final Object2ObjectOpenHashMap<String, EntityRegistry.EntityDefinition> DEFINITIONS = new Object2ObjectOpenHashMap<>();
    private static final List<EntityRegistry.EntityDefinition> CUSTOM_ENTITY_DEFINITIONS = new ArrayList<>();
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        registerInternal(new EntityDefinition(CHICKEN, "", 10, true, true), EntityChicken.class);
        registerInternal(new EntityDefinition(COW, "", 11, true, true), EntityCow.class);
        registerInternal(new EntityDefinition(PIG, "", 12, true, true), EntityPig.class);
        registerInternal(new EntityDefinition(SHEEP, "", 13, true, true), EntitySheep.class);
        registerInternal(new EntityDefinition(WOLF, "", 14, true, true), EntityWolf.class);
        registerInternal(new EntityDefinition(VILLAGER, "", 15, false, true), EntityVillager.class);
        registerInternal(new EntityDefinition(MOOSHROOM, "", 16, true, true), EntityMooshroom.class);
        registerInternal(new EntityDefinition(SQUID, "", 17, true, true), EntitySquid.class);
        registerInternal(new EntityDefinition(RABBIT, "", 18, true, true), EntityRabbit.class);
        registerInternal(new EntityDefinition(BAT, "", 19, true, true), EntityBat.class);
        registerInternal(new EntityDefinition(IRON_GOLEM, "", 20, true, true), EntityIronGolem.class);
        registerInternal(new EntityDefinition(SNOW_GOLEM, "", 21, true, true), EntitySnowGolem.class);
        registerInternal(new EntityDefinition(OCELOT, "", 22, true, true), EntityOcelot.class);
        registerInternal(new EntityDefinition(HORSE, "", 23, true, true), EntityHorse.class);
        registerInternal(new EntityDefinition(DONKEY, "", 24, true, true), EntityDonkey.class);
        registerInternal(new EntityDefinition(MULE, "", 25, true, true), EntityMule.class);
        registerInternal(new EntityDefinition(SKELETON_HORSE, "", 26, true, true), EntitySkeletonHorse.class);
        registerInternal(new EntityDefinition(ZOMBIE_HORSE, "", 27, true, true), EntityZombieHorse.class);
        registerInternal(new EntityDefinition(POLAR_BEAR, "", 28, true, true), EntityPolarBear.class);
        registerInternal(new EntityDefinition(LLAMA, "", 29, true, true), EntityLlamaSpit.class);
        registerInternal(new EntityDefinition(PARROT, "", 30, true, true), EntityParrot.class);
        registerInternal(new EntityDefinition(DOLPHIN, "", 31, true, true), EntityDolphin.class);
        registerInternal(new EntityDefinition(ZOMBIE, "", 32, true, true), EntityZombie.class);
        registerInternal(new EntityDefinition(CREEPER, "", 33, true, true), EntityCreeper.class);
        registerInternal(new EntityDefinition(SKELETON, "", 34, true, true), EntitySkeleton.class);
        registerInternal(new EntityDefinition(SPIDER, "", 35, true, true), EntitySpider.class);
        registerInternal(new EntityDefinition(ZOMBIE_PIGMAN, "", 36, true, true), EntityZombiePigman.class);
        registerInternal(new EntityDefinition(SLIME, "", 37, true, true), EntitySlime.class);
        registerInternal(new EntityDefinition(ENDERMAN, "", 38, true, true), EntityEnderman.class);
        registerInternal(new EntityDefinition(SILVERFISH, "", 39, true, true), EntitySilverfish.class);
        registerInternal(new EntityDefinition(CAVE_SPIDER, "", 40, true, true), EntityCaveSpider.class);
        registerInternal(new EntityDefinition(GHAST, "", 41, true, true), EntityGhast.class);
        registerInternal(new EntityDefinition(MAGMA_CUBE, "", 42, true, true), EntityMagmaCube.class);
        registerInternal(new EntityDefinition(BLAZE, "", 43, true, true), EntityBlaze.class);
        registerInternal(new EntityDefinition(ZOMBIE_VILLAGER, "", 44, false, true), EntityZombieVillager.class);
        registerInternal(new EntityDefinition(WITCH, "", 45, true, true), EntityWitch.class);
        registerInternal(new EntityDefinition(STRAY, "", 46, true, true), EntityStray.class);
        registerInternal(new EntityDefinition(HUSK, "", 47, true, true), EntityHusk.class);
        registerInternal(new EntityDefinition(WITHER_SKELETON, "", 48, true, true), EntityWitherSkeleton.class);
        registerInternal(new EntityDefinition(GUARDIAN, "", 49, true, true), EntityGuardian.class);
        registerInternal(new EntityDefinition(ELDER_GUARDIAN, "", 50, true, true), EntityElderGuardian.class);
        registerInternal(new EntityDefinition(NPC, "", 51, true, true), EntityNpc.class);
        registerInternal(new EntityDefinition(WITHER, "", 52, true, true), EntityWither.class);
        registerInternal(new EntityDefinition(ENDER_DRAGON, "", 53, true, true), EntityEnderDragon.class);
        registerInternal(new EntityDefinition(SHULKER, "", 54, true, true), EntityShulker.class);
        registerInternal(new EntityDefinition(ENDERMITE, "", 55, true, true), EntityEndermite.class);
//        registerInternal(new EntityDefinition(AGENT, "", 56, false, false), EntityAgent.class);
        registerInternal(new EntityDefinition(VINDICATOR, "", 57, true, true), EntityVindicator.class);
        registerInternal(new EntityDefinition(PHANTOM, "", 58, true, true), EntityPhantom.class);
        registerInternal(new EntityDefinition(RAVAGER, "", 59, true, true), EntityRavager.class);
        registerInternal(new EntityDefinition(ARMOR_STAND, "", 61, false, true), EntityArmorStand.class);
//        registerInternal(new EntityDefinition(TRIPOD_CAMERA, "", 62, false, false), EntityTripodCamera.class);
        registerInternal(new EntityDefinition(ITEM, "", 64, false, false), EntityItem.class);
        registerInternal(new EntityDefinition(TNT, "", 65, false, true), EntityTnt.class);
        registerInternal(new EntityDefinition(FALLING_BLOCK, "", 66, false, false), EntityFallingBlock.class);
        registerInternal(new EntityDefinition(XP_BOTTLE, "", 68, false, true), EntityXpBottle.class);
        registerInternal(new EntityDefinition(XP_ORB, "", 69, false, true), EntityXpOrb.class);
//        registerInternal(new EntityDefinition(EYE_OF_ENDER_SIGNAL, "", 70, false, false), EntityEyeOfEnderSignal.class);
        registerInternal(new EntityDefinition(ENDER_CRYSTAL, "", 71, false, true), EntityEnderCrystal.class);
        registerInternal(new EntityDefinition(FIREWORKS_ROCKET, "", 72, false, true), EntityFireworksRocket.class);
        registerInternal(new EntityDefinition(THROWN_TRIDENT, "", 73, false, false), EntityThrownTrident.class);
        registerInternal(new EntityDefinition(TURTLE, "", 74, true, true), EntityTurtle.class);
        registerInternal(new EntityDefinition(CAT, "", 75, true, true), EntityCat.class);
//        registerInternal(new EntityDefinition(SHULKER_BULLET, "", 76, false, false), EntityShulkerBullet.class);
        registerInternal(new EntityDefinition(FISHING_HOOK, "", 77, false, false), EntityFishingHook.class);
//        registerInternal(new EntityDefinition(DRAGON_FIREBALL, "", 79, false, false), EntityDragonFireball.class);
        registerInternal(new EntityDefinition(ARROW, "", 80, false, true), EntityArrow.class);
        registerInternal(new EntityDefinition(SNOWBALL, "", 81, false, true), EntitySnowball.class);
        registerInternal(new EntityDefinition(EGG, "", 82, false, true), EntityEgg.class);
        registerInternal(new EntityDefinition(PAINTING, "", 83, false, false), EntityPainting.class);
        registerInternal(new EntityDefinition(MINECART, "", 84, false, true), EntityMinecart.class);
//        registerInternal(new EntityDefinition(FIREBALL, "", 85, false, false), EntityFireball.class);
        registerInternal(new EntityDefinition(SPLASH_POTION, "", 86, false, true), EntitySplashPotion.class);
        registerInternal(new EntityDefinition(ENDER_PEARL, "", 87, false, false), EntityEnderPearl.class);
//        registerInternal(new EntityDefinition(LEASH_KNOT, "", 88, false, true), EntityLeashKnot.class);
//        registerInternal(new EntityDefinition(WITHER_SKULL, "", 89, false, false), EntityWitherSkull.class);//This is the skull fired by Wither
        registerInternal(new EntityDefinition(BOAT, "", 90, false, true), EntityBoat.class);
//        registerInternal(new EntityDefinition(WITHER_SKULL_DANGEROUS, "", 91, false, false), EntityWitherSkullDangerous.class);
        registerInternal(new EntityDefinition(LIGHTNING_BOLT, "", 93, false, true), EntityLightningBolt.class);
        registerInternal(new EntityDefinition(SMALL_FIREBALL, "", 94, false, false), EntitySmallFireball.class);
        registerInternal(new EntityDefinition(AREA_EFFECT_CLOUD, "", 95, false, false), EntityAreaEffectCloud.class);
        registerInternal(new EntityDefinition(HOPPER_MINECART, "", 96, false, true), EntityHopperMinecart.class);
        registerInternal(new EntityDefinition(TNT_MINECART, "", 97, false, true), EntityTntMinecart.class);
        registerInternal(new EntityDefinition(CHEST_MINECART, "", 98, false, true), EntityChestMinecart.class);
//        registerInternal(new EntityDefinition(COMMAND_BLOCK_MINECART, "", 100, false, true), EntityCommandBlockMinecart.class);
        registerInternal(new EntityDefinition(LINGERING_POTION, "", 101, false, false), EntityLingeringPotion.class);
        registerInternal(new EntityDefinition(LLAMA_SPIT, "", 102, false, false), EntityLlamaSpit.class);
//        registerInternal(new EntityDefinition(EVOCATION_FANG, "", 103, false, true), EntityEvocationFang.class);
        registerInternal(new EntityDefinition(EVOCATION_ILLAGER, "", 104, true, true), EntityEvocationIllager.class);
        registerInternal(new EntityDefinition(VEX, "", 105, true, true), EntityVex.class);
//        registerInternal(new EntityDefinition(ICE_BOMB, "", 106, false, false), EntityIceBomb.class);
//        registerInternal(new EntityDefinition(BALLOON, "", 107, false, false), EntityBalloon.class);
        registerInternal(new EntityDefinition(PUFFERFISH, "", 108, true, true), EntityPufferfish.class);
        registerInternal(new EntityDefinition(SALMON, "", 109, true, true), EntitySalmon.class);
        registerInternal(new EntityDefinition(DROWNED, "", 110, true, true), EntityDrowned.class);
        registerInternal(new EntityDefinition(TROPICALFISH, "", 111, true, true), EntityTropicalfish.class);
        registerInternal(new EntityDefinition(COD, "", 112, true, true), EntityCod.class);
        registerInternal(new EntityDefinition(PANDA, "", 113, true, true), EntityPanda.class);
        registerInternal(new EntityDefinition(PILLAGER, "", 114, true, true), EntityPillager.class);
        registerInternal(new EntityDefinition(VILLAGER_V2, "", 115, true, false), EntityVillagerV2.class);
        registerInternal(new EntityDefinition(ZOMBIE_VILLAGER_V2, "", 116, true, false), EntityZombieVillagerV2.class);
        registerInternal(new EntityDefinition(WANDERING_TRADER, "", 118, true, true), EntityTraderLlama.class);
//        registerInternal(new EntityDefinition(ELDER_GUARDIAN_GHOST, "", 120, false, true), EntityElderGuardianGhost.class);
        registerInternal(new EntityDefinition(FOX, "", 121, true, true), EntityFox.class);
        registerInternal(new EntityDefinition(BEE, "", 122, true, true), EntityBee.class);
        registerInternal(new EntityDefinition(PIGLIN, "", 123, true, true), EntityPiglin.class);
        registerInternal(new EntityDefinition(HOGLIN, "", 124, true, true), EntityHoglin.class);
        registerInternal(new EntityDefinition(STRIDER, "", 125, true, true), EntityStrider.class);
        registerInternal(new EntityDefinition(ZOGLIN, "", 126, true, true), EntityZoglin.class);
        registerInternal(new EntityDefinition(PIGLIN_BRUTE, "", 127, true, true), EntityPiglinBrute.class);
        registerInternal(new EntityDefinition(GOAT, "", 128, true, true), EntityGoat.class);
        registerInternal(new EntityDefinition(GLOW_SQUID, "", 129, true, true), EntityGlowSquid.class);
        registerInternal(new EntityDefinition(AXOLOTL, "", 130, true, true), EntityAxolotl.class);
        registerInternal(new EntityDefinition(WARDEN, "", 131, true, true), EntityWarden.class);
        registerInternal(new EntityDefinition(FROG, "", 132, true, true), EntityFrog.class);
        registerInternal(new EntityDefinition(TADPOLE, "", 133, true, true), EntityTadpole.class);
        registerInternal(new EntityDefinition(ALLAY, "", 134, true, true), EntityAllay.class);
        registerInternal(new EntityDefinition(CAMEL, "", 138, true, true), EntityCamel.class);
        registerInternal(new EntityDefinition(SNIFFER, "", 139, true, true), EntitySniffer.class);
        registerInternal(new EntityDefinition(TRADER_LLAMA, "", 157, true, true), EntityTraderLlama.class);
        registerInternal(new EntityDefinition(CHEST_BOAT, "", 218, false, true), EntityChestBoat.class);
        registerInternal(new EntityDefinition(ARMADILLO, "", 142, true, true), EntityArmadillo.class);
    }

    public Class<? extends Entity> getEntityClass(String id) {
        return CLASS.get(id);
    }

    public Class<? extends Entity> getEntityClass(int id) {
        return getEntityClass(RID2ID.get(id));
    }

    public int getEntityNetworkId(String entityID) {
        return ID2RID.getInt(entityID);
    }

    public String getEntityIdentifier(int networkID) {
        return RID2ID.get(networkID);
    }

    public EntityRegistry.EntityDefinition getEntityDefinition(String id) {
        return DEFINITIONS.get(id);
    }

    @UnmodifiableView
    public List<EntityDefinition> getCustomEntityDefinitions() {
        return Collections.unmodifiableList(CUSTOM_ENTITY_DEFINITIONS);
    }

    /**
     * 获得全部实体的网络id
     * <p>
     * Get the network id of all entities
     *
     * @return the known entity ids
     */
    public IntCollection getKnownEntityIds() {
        return ID2RID.values();
    }

    public Map<Integer, String> getEntityId2NetworkIdMap() {
        return Collections.unmodifiableMap(RID2ID);
    }

    /**
     * 获取全部已经注册的实体，包括自定义实体
     * <p>
     * Get all registered entities, including custom entities
     *
     * @return the known entities
     */
    @UnmodifiableView
    public Map<String, Class<? extends Entity>> getKnownEntities() {
        return Collections.unmodifiableMap(CLASS);
    }

    public Entity provideEntity(String id, @NotNull IChunk chunk, @NotNull CompoundTag nbt, @Nullable Object... args) {
        Class<? extends Entity> clazz = getEntityClass(id);
        if (clazz == null) return null;

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
                    FastConstructor<? extends Entity> fastConstructor = FAST_NEW.get(id);
                    entity = (Entity) fastConstructor.invoke(chunk, nbt);
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
            } catch (Throwable e) {
                if (exceptions == null) {
                    exceptions = new ArrayList<>();
                }
                exceptions.add(new RuntimeException(e));
            }
        }

        if (entity == null) {
            Exception cause = new IllegalArgumentException("Could not create an entity of identifier " + id, exceptions != null && !exceptions.isEmpty() ? exceptions.get(0) : null);
            if (exceptions != null && exceptions.size() > 1) {
                for (int i = 1; i < exceptions.size(); i++) {
                    cause.addSuppressed(exceptions.get(i));
                }
            }
            log.error("Could not create an entity of type {} with {} args", id, args == null ? 0 : args.length, cause);
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
        FAST_NEW.trim();
        ID2RID.trim();
        DEFINITIONS.trim();
    }

    @Override
    public void reload() {
        isLoad.set(false);
        CLASS.clear();
        FAST_NEW.clear();
        ID2RID.clear();
        RID2ID.clear();
        DEFINITIONS.clear();
        CUSTOM_ENTITY_DEFINITIONS.clear();
        init();
    }

    @Override
    public void register(EntityDefinition key, Class<? extends Entity> value) throws RegisterException {
        if (CLASS.putIfAbsent(key.id(), value) == null) {
            try {
                FAST_NEW.put(key.id, FastConstructor.create(value.getConstructor(IChunk.class, CompoundTag.class)));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            ID2RID.put(key.id, key.rid);
            RID2ID.put(key.rid, key.id);
            DEFINITIONS.put(key.id, key);
        } else {
            throw new RegisterException("This Entity has already been registered with the identifier: " + key.id);
        }
    }

    /**
     * Register an entity to override internal entity.
     *
     * @param plugin   the plugin
     * @param entityId the entity id {@link EntityID}
     * @param value    the entity class,must extends internal entity
     * @throws RegisterException the register exception
     */
    public void registerOverrideEntity(Plugin plugin, String entityId, Class<? extends Entity> value) throws RegisterException {
        EntityDefinition key = getEntityDefinition(entityId);
        Class<? extends Entity> entityClass = getEntityClass(entityId);
        if (entityClass == null) {
            throw new RegisterException("This entity class does not override because can't find entity class from entityId {}", entityId);
        }
        if (!entityClass.isAssignableFrom(value)) {
            throw new RegisterException("This entity class {} does not override the {} because is not assignable from {}!", entityClass.getSimpleName(), value.getSimpleName(), value.getSimpleName());
        }
        try {
            FastMemberLoader memberLoader = fastMemberLoaderCache.computeIfAbsent(plugin.getName(), p -> new FastMemberLoader(plugin.getPluginClassLoader()));
            FAST_NEW.put(key.id, FastConstructor.create(value.getConstructor(IChunk.class, CompoundTag.class), memberLoader, false));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        ID2RID.put(key.id, key.rid);
        RID2ID.put(key.rid, key.id);
        DEFINITIONS.put(key.id, key);
    }

    /**
     * register custom entity
     */
    public void registerCustomEntity(Plugin plugin, CustomEntityDefinition key, Class<? extends Entity> value) throws RegisterException {
        if (CustomEntity.class.isAssignableFrom(value)) {
            if (CLASS.putIfAbsent(key.id, value) == null) {
                try {
                    FastMemberLoader memberLoader = fastMemberLoaderCache.computeIfAbsent(plugin.getName(), p -> new FastMemberLoader(plugin.getPluginClassLoader()));
                    FAST_NEW.put(key.id, FastConstructor.create(value.getConstructor(IChunk.class, CompoundTag.class), memberLoader, false));
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                int rid = RUNTIME_ID.getAndIncrement();
                ID2RID.put(key.id, rid);
                RID2ID.put(rid, key.id);
                EntityDefinition entityDefinition = new EntityDefinition(key.id, key.bid, rid, key.hasSpawnegg, key.summonable);
                DEFINITIONS.put(key.id, entityDefinition);
                CUSTOM_ENTITY_DEFINITIONS.add(entityDefinition);
            } else {
                throw new RegisterException("This Entity has already been registered with the identifier: " + key.id);
            }
        } else {
            throw new RegisterException("This class does not implement the CustomEntity interface and cannot be registered as a custom entity!");
        }
    }

    private void registerInternal(EntityDefinition key, Class<? extends Entity> value) {
        try {
            register(key, value);
        } catch (RegisterException e) {
            log.error("{}", e.getCause().getMessage());
        }
    }

    private static AtomicInteger RUNTIME_ID = new AtomicInteger(10000);

    @Getter
    public static final class CustomEntityDefinition {
        private final String id;
        private final String bid;
        private final boolean hasSpawnegg;
        private final boolean summonable;

        public CustomEntityDefinition(String id, String bid, boolean hasSpawnegg, boolean summonable) {
            this.id = id;
            this.bid = bid;
            this.hasSpawnegg = hasSpawnegg;
            this.summonable = summonable;
        }
    }

    public record EntityDefinition(String id, String bid, int rid, boolean hasSpawnegg, boolean summonable) {
        public CompoundTag toNBT() {
            return new CompoundTag()
                    .putString("bid", bid)
                    .putBoolean("hasspawnegg", hasSpawnegg)
                    .putString("id", id)
                    .putInt("rid", rid)
                    .putBoolean("summonable", summonable);
        }
    }
}
