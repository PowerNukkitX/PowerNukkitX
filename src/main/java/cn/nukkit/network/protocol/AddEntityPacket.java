package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.item.*;
import cn.nukkit.entity.mob.*;
import cn.nukkit.entity.passive.*;
import cn.nukkit.entity.projectile.*;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.network.protocol.types.EntityLink;
import cn.nukkit.network.protocol.types.PropertySyncData;
import cn.nukkit.utils.Binary;
import com.google.common.collect.ImmutableMap;
import lombok.ToString;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
public class AddEntityPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.ADD_ENTITY_PACKET;

    public static ImmutableMap<Integer, String> LEGACY_IDS = ImmutableMap.<Integer, String>builder()
            .put(EntityChestBoat.NETWORK_ID, "minecraft:chest_boat")//218

            .put(157, "minecraft:trader_llama")

            .put(EntityCamel.NETWORK_ID, "minecraft:camel")//138
            .put(EntityAllay.NETWORK_ID, "minecraft:allay")//134
            .put(EntityTadpole.NETWORK_ID, "minecraft:tadpole")//133
            .put(EntityFrog.NETWORK_ID, "minecraft:frog")
            .put(EntityWarden.NETWORK_ID, "minecraft:warden")
            .put(EntityAxolotl.NETWORK_ID, "minecraft:axolotl")
            .put(EntityGlowSquid.NETWORK_ID, "minecraft:glow_squid")//129
            .put(EntityGoat.NETWORK_ID, "minecraft:goat")//128
            .put(EntityPiglinBrute.NETWORK_ID, "minecraft:piglin_brute")
            .put(EntityZoglin.NETWORK_ID, "minecraft:zoglin")
            .put(EntityStrider.NETWORK_ID, "minecraft:strider")
            .put(EntityHoglin.NETWORK_ID, "minecraft:hoglin")
            .put(EntityPiglin.NETWORK_ID, "minecraft:piglin")
            .put(EntityBee.NETWORK_ID, "minecraft:bee")
            .put(EntityFox.NETWORK_ID, "minecraft:fox")//121

            .put(EntityWanderingTrader.NETWORK_ID, "minecraft:wandering_trader")//118
            .put(EntityZombieVillager.NETWORK_ID, "minecraft:zombie_villager_v2")
            .put(EntityVillager.NETWORK_ID, "minecraft:villager_v2")
            .put(EntityPillager.NETWORK_ID, "minecraft:pillager")
            .put(EntityPanda.NETWORK_ID, "minecraft:panda")
            .put(EntityCod.NETWORK_ID, "minecraft:cod")
            .put(EntityTropicalFish.NETWORK_ID, "minecraft:tropicalfish")//111
            .put(EntityDrowned.NETWORK_ID, "minecraft:drowned")
            .put(EntitySalmon.NETWORK_ID, "minecraft:salmon")
            .put(EntityPufferfish.NETWORK_ID, "minecraft:pufferfish")
            .put(107, "minecraft:balloon")
            .put(106, "minecraft:ice_bomb")
            .put(EntityVex.NETWORK_ID, "minecraft:vex")
            .put(EntityEvoker.NETWORK_ID, "minecraft:evocation_illager")
            .put(103, "minecraft:evocation_fang")
            .put(102, "minecraft:llama_spit")
            .put(EntityPotionLingering.NETWORK_ID, "minecraft:lingering_potion")
            .put(100, "minecraft:command_block_minecart")
            .put(EntityMinecartChest.NETWORK_ID, "minecraft:chest_minecart")//98
            .put(EntityMinecartTNT.NETWORK_ID, "minecraft:tnt_minecart")
            .put(EntityMinecartHopper.NETWORK_ID, "minecraft:hopper_minecart")
            .put(EntityAreaEffectCloud.NETWORK_ID, "minecraft:area_effect_cloud")
            .put(EntitySmallFireBall.NETWORK_ID, "minecraft:small_fireball")
            .put(EntityLightning.NETWORK_ID, "minecraft:lightning_bolt")
            .put(91, "minecraft:wither_skull_dangerous")
            .put(EntityBoat.NETWORK_ID, "minecraft:boat")
            .put(89, "minecraft:wither_skull")
            .put(88, "minecraft:leash_knot")
            .put(EntityEnderPearl.NETWORK_ID, "minecraft:ender_pearl")
            .put(EntityPotion.NETWORK_ID, "minecraft:splash_potion")
            .put(85, "minecraft:fireball")
            .put(EntityMinecartEmpty.NETWORK_ID, "minecraft:minecart")//84
            .put(EntityPainting.NETWORK_ID, "minecraft:painting")
            .put(EntityEgg.NETWORK_ID, "minecraft:egg")
            .put(EntitySnowball.NETWORK_ID, "minecraft:snowball")
            .put(EntityArrow.NETWORK_ID, "minecraft:arrow")
            .put(79, "minecraft:dragon_fireball")
            .put(EntityFishingHook.NETWORK_ID, "minecraft:fishing_hook")
            .put(76, "minecraft:shulker_bullet")
            .put(EntityCat.NETWORK_ID, "minecraft:cat")//75
            .put(EntityTurtle.NETWORK_ID, "minecraft:turtle")//74
            .put(EntityThrownTrident.NETWORK_ID, "minecraft:thrown_trident")
            .put(EntityFirework.NETWORK_ID, "minecraft:fireworks_rocket")
            .put(EntityEndCrystal.NETWORK_ID, "minecraft:ender_crystal")
            .put(70, "minecraft:eye_of_ender_signal")
            .put(EntityXPOrb.NETWORK_ID, "minecraft:xp_orb")//69
            .put(EntityExpBottle.NETWORK_ID, "minecraft:xp_bottle")
            .put(EntityFallingBlock.NETWORK_ID, "minecraft:falling_block")
            .put(EntityPrimedTNT.NETWORK_ID, "minecraft:tnt")
            .put(EntityItem.NETWORK_ID, "minecraft:item")
            .put(63, "minecraft:player")
            .put(62, "minecraft:tripod_camera")
            .put(EntityArmorStand.NETWORK_ID, "minecraft:armor_stand")//61
            .put(EntityRavager.NETWORK_ID, "minecraft:ravager")
            .put(EntityPhantom.NETWORK_ID, "minecraft:phantom")
            .put(EntityVindicator.NETWORK_ID, "minecraft:vindicator")//57
            .put(56, "minecraft:agent")
            .put(EntityEndermite.NETWORK_ID, "minecraft:endermite")
            .put(EntityShulker.NETWORK_ID, "minecraft:shulker")
            .put(EntityEnderDragon.NETWORK_ID, "minecraft:ender_dragon")
            .put(EntityWither.NETWORK_ID, "minecraft:wither")
            .put(EntityElderGuardian.NETWORK_ID, "minecraft:elder_guardian")
            .put(EntityGuardian.NETWORK_ID, "minecraft:guardian")
            .put(EntityNPCEntity.NETWORK_ID, "minecraft:npc")//51

            .put(EntityWitherSkeleton.NETWORK_ID, "minecraft:wither_skeleton")//48
            .put(EntityHusk.NETWORK_ID, "minecraft:husk")
            .put(EntityStray.NETWORK_ID, "minecraft:stray")
            .put(EntityWitch.NETWORK_ID, "minecraft:witch")
            .put(EntityZombieVillagerV1.NETWORK_ID, "minecraft:zombie_villager")
            .put(EntityBlaze.NETWORK_ID, "minecraft:blaze")
            .put(EntityMagmaCube.NETWORK_ID, "minecraft:magma_cube")
            .put(EntityGhast.NETWORK_ID, "minecraft:ghast")
            .put(EntityCaveSpider.NETWORK_ID, "minecraft:cave_spider")
            .put(EntitySilverfish.NETWORK_ID, "minecraft:silverfish")
            .put(EntityEnderman.NETWORK_ID, "minecraft:enderman")
            .put(EntitySlime.NETWORK_ID, "minecraft:slime")
            .put(EntityZombiePigman.NETWORK_ID, "minecraft:zombie_pigman")
            .put(EntitySpider.NETWORK_ID, "minecraft:spider")
            .put(EntitySkeleton.NETWORK_ID, "minecraft:skeleton")
            .put(EntityCreeper.NETWORK_ID, "minecraft:creeper")
            .put(EntityZombie.NETWORK_ID, "minecraft:zombie")
            .put(EntityDolphin.NETWORK_ID, "minecraft:dolphin")//31
            .put(EntityParrot.NETWORK_ID, "minecraft:parrot")
            .put(EntityLlama.NETWORK_ID, "minecraft:llama")
            .put(EntityPolarBear.NETWORK_ID, "minecraft:polar_bear")
            .put(EntityZombieHorse.NETWORK_ID, "minecraft:zombie_horse")
            .put(EntitySkeletonHorse.NETWORK_ID, "minecraft:skeleton_horse")
            .put(EntityMule.NETWORK_ID, "minecraft:mule")
            .put(EntityDonkey.NETWORK_ID, "minecraft:donkey")//24
            .put(EntityHorse.NETWORK_ID, "minecraft:horse")
            .put(EntityOcelot.NETWORK_ID, "minecraft:ocelot")
            .put(21, "minecraft:snow_golem")
            .put(20, "minecraft:iron_golem")
            .put(EntityBat.NETWORK_ID, "minecraft:bat")
            .put(EntityRabbit.NETWORK_ID, "minecraft:rabbit")
            .put(EntitySquid.NETWORK_ID, "minecraft:squid")//17
            .put(EntityMooshroom.NETWORK_ID, "minecraft:mooshroom")
            .put(EntityVillagerV1.NETWORK_ID, "minecraft:villager")
            .put(EntityWolf.NETWORK_ID, "minecraft:wolf")
            .put(EntitySheep.NETWORK_ID, "minecraft:sheep")
            .put(EntityPig.NETWORK_ID, "minecraft:pig")
            .put(EntityCow.NETWORK_ID, "minecraft:cow")
            .put(EntityChicken.NETWORK_ID, "minecraft:chicken")//10

            .build();

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public long entityUniqueId;
    public long entityRuntimeId;
    public int type;
    public String id;
    public float x;
    public float y;
    public float z;
    public float speedX = 0f;
    public float speedY = 0f;
    public float speedZ = 0f;
    public float yaw;
    public float pitch;
    public float headYaw;
    //todo: check what's the usage of this
    public float bodyYaw = -1;
    @PowerNukkitXOnly
    @Since("1.19.40-r1")
    public Attribute[] attributes = Attribute.EMPTY_ARRAY;
    public EntityMetadata metadata = new EntityMetadata();
    public PropertySyncData syncedProperties = new PropertySyncData(new int[]{}, new float[]{});
    public EntityLink[] links = EntityLink.EMPTY_ARRAY;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.entityUniqueId);
        this.putEntityRuntimeId(this.entityRuntimeId);
        if (id == null) {
            id = LEGACY_IDS.get(type);
        }
        this.putString(this.id);
        this.putVector3f(this.x, this.y, this.z);
        this.putVector3f(this.speedX, this.speedY, this.speedZ);
        this.putLFloat(this.pitch);
        this.putLFloat(this.yaw);
        this.putLFloat(this.headYaw);
        this.putLFloat(this.bodyYaw != -1 ? this.bodyYaw : this.yaw);
        this.putAttributeList(this.attributes);
        this.put(Binary.writeMetadata(this.metadata));
        //syncedProperties
        this.putUnsignedVarInt(this.syncedProperties.intProperties().length);
        for (int i = 0, len = this.syncedProperties.intProperties().length; i < len; ++i) {
            this.putUnsignedVarInt(i);
            this.putVarInt(this.syncedProperties.intProperties()[i]);
        }
        this.putUnsignedVarInt(this.syncedProperties.floatProperties().length);
        for (int i = 0, len = this.syncedProperties.floatProperties().length; i < len; ++i) {
            this.putUnsignedVarInt(i);
            this.putLFloat(this.syncedProperties.floatProperties()[i]);
        }
        this.putUnsignedVarInt(this.links.length);
        for (EntityLink link : links) {
            putEntityLink(link);
        }
    }

    public AddEntityPacket() {
    }
}
