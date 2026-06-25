package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.block.BlockBed;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockTurtleEgg;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.components.HealthComponent;
import cn.nukkit.entity.components.MovementComponent;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.passive.EntityVillagerV2;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityTransformEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemGoldenApple;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.Xoroshiro128;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class EntityZombieVillagerV2 extends EntityZombie implements EntityWalkable, EntitySmite {

    @Override
    @NotNull public String getIdentifier() {
        return ZOMBIE_VILLAGER_V2;
    }

    private int curingTick = 0;

    public EntityZombieVillagerV2(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        this.diffHandDamage = new float[]{2.5f, 3f, 4.5f};
        super.initEntity();
        this.curingTick = this.nbt.getInt("curing", 0);
        this.setDataProperty(ActorDataTypes.VARIANT, nbt.getInt("profession", 0));
        this.setDataProperty(ActorDataTypes.MARK_VARIANT, nbt.getInt("clothing", EntityVillagerV2.Clothing.getClothing(getLevel().getBiomeId(getFloorX(), getFloorY(), getFloorZ())).ordinal()));
        getMemoryStorage().put(CoreMemoryTypes.LOOKING_BLOCK, BlockTurtleEgg.class);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putInt("profession", this.getDataProperty(ActorDataTypes.VARIANT, 0));
        this.nbt.putInt("clothing", this.getDataProperty(ActorDataTypes.MARK_VARIANT, 0));
        this.nbt.putInt("curing", curingTick);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 v) {
        if (item instanceof ItemGoldenApple) {
            if (hasEffect(EffectType.WEAKNESS)) {
                if (!getDataFlag(ActorFlags.SHAKING)) {
                    setDataFlag(ActorFlags.SHAKING);
                    curingTick = new NukkitRandom().nextInt(3600, 6000);
                    if (!player.isCreative()) {
                        this.nbt.putString("purifyPlayer", player.getXUID());
                        player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
                    }
                    getLevel().addSound(this, Sound.MOB_ZOMBIE_REMEDY);
                }
            }
        }
        return false;
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(20);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        float ageMovement = this.isBaby() ? 0.35f : 0.23f;
        return MovementComponent.value(ageMovement);
    }

    @Override
    public String getOriginalName() {
        return "Zombie VillagerV2";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("zombie", "zombie_villager", "undead", "monster", "mob");
    }

    @Override
    public boolean isUndead() {
        return true;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if(source.getCause() == EntityDamageEvent.DamageCause.DROWNING) return false;
        return super.attack(source);
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        burn(this);
        if (getDataFlag(ActorFlags.SHAKING)) {
            if (curingTick > 0) {
                curingTick--;
                Xoroshiro128 rnd = new Xoroshiro128(currentTick + currentTick);
                curingTick -= Math.min(14, level.getCollisionBlocks(this.getBoundingBox().grow(4, 4, 4), false, false, block -> switch (block.getId()) {
                    case BlockID.BED,
                         BlockID.IRON_BARS -> rnd.nextFloat() < 0.3f;
                    default -> false;
                }).length);
            } else transformVillager();
        }
        return super.onUpdate(currentTick);
    }

     protected void transformVillager() {
         this.saveNBT();
         Entity villager = new EntityVillagerV2(this.getChunk(), this.getNbt().copy().remove("profession", "Health"));
         EntityTransformEvent event = new EntityTransformEvent(this, villager);
         server.getPluginManager().callEvent(event);
         if(event.isCancelled()) {
             villager.close();
         } else {
             this.close();
             getArmorInventory().getContents().values().forEach(i -> getLevel().dropItem(this, i));
             getEquipmentInventory().getContents().values().forEach(i -> getLevel().dropItem(this, i));
             villager.addEffect(Effect.get(EffectType.NAUSEA).setDuration(200));
             villager.spawnToAll();
             villager.level.addSound(villager, Sound.MOB_ZOMBIE_UNFECT);
        }
    }
}
