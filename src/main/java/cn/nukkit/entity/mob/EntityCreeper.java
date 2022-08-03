package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.AllMatchEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.executor.EntityExplosionExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.RandomRoamExecutor;
import cn.nukkit.entity.ai.memory.AttackTargetMemory;
import cn.nukkit.entity.ai.memory.EntityExplodeMemory;
import cn.nukkit.entity.ai.memory.NearestPlayerMemory;
import cn.nukkit.entity.ai.route.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.weather.EntityLightningStrike;
import cn.nukkit.event.entity.CreeperPowerEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Box.
 */
public class EntityCreeper extends EntityWalkingMob implements EntityInteractable {

    public static final int NETWORK_ID = 33;

    public static final int DATA_SWELL_DIRECTION = 16;
    public static final int DATA_SWELL = 17;
    public static final int DATA_SWELL_OLD = 18;
    public static final int DATA_POWERED = 19;

    private IBehaviorGroup behaviorGroup;

    @Override
    public IBehaviorGroup getBehaviorGroup() {
        if (behaviorGroup == null) {
            behaviorGroup = new BehaviorGroup(
                    this.tickSpread,
                    Set.of(),
                    Set.of(new Behavior(
                                    new EntityExplosionExecutor(30, 3, EntityExplodeMemory.class),
                                    entity -> entity.getMemoryStorage().checkData(EntityExplodeMemory.class, true), 3, 1
                            ),
                            new Behavior(new MoveToTargetExecutor(NearestPlayerMemory.class, 0.15f, true, 16, 3), new AllMatchEvaluator(
                                    new MemoryCheckNotEmptyEvaluator(NearestPlayerMemory.class),
                                    entity -> {
                                        if (entity.getMemoryStorage().isEmpty(NearestPlayerMemory.class)) return true;
                                        Player player = entity.getMemoryStorage().get(NearestPlayerMemory.class).getData();
                                        return player.isSurvival();
                                    }
                            ), 2, 1),
                            new Behavior(new RandomRoamExecutor(0.15f, 12, 100, false, -1, true, 10), (entity -> true), 1, 1)
                    ),
                    Set.of(new NearestPlayerSensor(16, 0, 20), entity -> {
                        var memoryStorage = entity.getMemoryStorage();
                        Player player = memoryStorage.get(NearestPlayerMemory.class).getData();
                        if (player != null && player.isSurvival() && player.distanceSquared(entity) <= 3 * 3 && (memoryStorage.isEmpty(EntityExplodeMemory.class) || memoryStorage.checkData(EntityExplodeMemory.class, false))){
                            memoryStorage.setData(EntityExplodeMemory.class,true);
                            return;
                        }
                        if ((player == null || !player.isSurvival() || player.distanceSquared(entity) >= 7 * 7) && memoryStorage.checkData(EntityExplodeMemory.class, true) && memoryStorage.get(EntityExplodeMemory.class).isCancellable()){
                            memoryStorage.setData(EntityExplodeMemory.class,false);
                            return;
                        }
                    }),
                    Set.of(new WalkController(), new LookController(true, true)),
                    new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this)
            );
        }
        return behaviorGroup;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.8f;
    }

    public EntityCreeper(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public boolean isPowered() {
        return getDataPropertyBoolean(DATA_POWERED);
    }

    public void setPowered(EntityLightningStrike bolt) {
        CreeperPowerEvent ev = new CreeperPowerEvent(this, bolt, CreeperPowerEvent.PowerCause.LIGHTNING);
        this.getServer().getPluginManager().callEvent(ev);

        if (!ev.isCancelled()) {
            this.setDataProperty(new ByteEntityData(DATA_POWERED, 1));
            this.namedTag.putBoolean("powered", true);
        }
    }

    public void setPowered(boolean powered) {
        CreeperPowerEvent ev = new CreeperPowerEvent(this, powered ? CreeperPowerEvent.PowerCause.SET_ON : CreeperPowerEvent.PowerCause.SET_OFF);
        this.getServer().getPluginManager().callEvent(ev);

        if (!ev.isCancelled()) {
            this.setDataProperty(new ByteEntityData(DATA_POWERED, powered ? 1 : 0));
            this.namedTag.putBoolean("powered", powered);
        }
    }

    @Override
    public void onStruckByLightning(Entity entity) {
        this.setPowered(true);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag.getBoolean("powered") || this.namedTag.getBoolean("IsPowered")) {
            this.dataProperties.putBoolean(DATA_POWERED, true);
        }
        this.setMaxHealth(20);
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Creeper";
    }

    @Override
    public Item[] getDrops() {
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            return new Item[]{Item.get(Item.GUNPOWDER, ThreadLocalRandom.current().nextInt(2) + 1)};
        }
        return Item.EMPTY_ARRAY;
    }

    @PowerNukkitOnly
    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        var result = super.attack(source);
        if (source instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
            //更新仇恨目标
            getMemoryStorage().setData(AttackTargetMemory.class, entityDamageByEntityEvent.getDamager());
        }
        return result;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        var memoryStorage = this.getMemoryStorage();
        if (item.getId() == Item.FLINT_AND_STEEL && (memoryStorage.isEmpty(EntityExplodeMemory.class) || memoryStorage.checkData(EntityExplodeMemory.class, false))) {
            memoryStorage.setData(EntityExplodeMemory.class, true);
            memoryStorage.get(EntityExplodeMemory.class).setCancellable(false);
            this.level.addSound(this, Sound.FIRE_IGNITE);
            return true;
        }
        return super.onInteract(player, item, clickedPos);
    }


    @Override
    public String getInteractButtonText(Player player) {
        return "action.interact.creeper";
    }

    @Override
    public boolean canDoInteraction() {
        return true;
    }
}
