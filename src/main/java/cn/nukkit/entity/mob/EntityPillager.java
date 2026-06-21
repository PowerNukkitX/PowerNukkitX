package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.executor.CrossBowShootExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.NearbyFlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.entity.components.HealthComponent;
import cn.nukkit.entity.components.MovementComponent;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EntityPillager extends EntityIllager implements EntityWalkable {
    @Override
    @NotNull public String getIdentifier() {
        return PILLAGER;
    }

    private static final int CAPTAIN_SPAWN_RARITY = 10;
    private static final int CAPTAIN_BAD_OMEN_DURATION_TICKS = 6000 * 20;

    @Getter
    private boolean isIllagerCaptain = false;

    public EntityPillager(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public void setIllagerCaptain(boolean captain) {
        this.isIllagerCaptain = captain;
        this.nbt.putBoolean("IsIllagerCaptain", captain);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new PlaySoundExecutor(Sound.MOB_PILLAGER_IDLE, 0.8f, 1.2f, 0.8f, 0.8f), new RandomSoundEvaluator(), 5, 1),
                        new Behavior(new CrossBowShootExecutor(this::getItemInHand, CoreMemoryTypes.ATTACK_TARGET, 0.3f, 15, true, 30, 80), new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 4, 1),
                        new Behavior(new CrossBowShootExecutor(this::getItemInHand, CoreMemoryTypes.NEAREST_PLAYER, 0.3f, 15, true, 30, 80), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 3, 1),
                        new Behavior(new CrossBowShootExecutor(this::getItemInHand, CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.3f, 15, true, 30, 80), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), 2, 1),
                        new Behavior(new NearbyFlatRandomRoamExecutor(CoreMemoryTypes.STAY_NEARBY, 0.3f, 24, 100, false, -1, true, 10), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.STAY_NEARBY), 1, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 0, 1)
                ),
                Set.of(
                        new NearestPlayerSensor(40, 0, 20),
                        new NearestTargetEntitySensor<>(0, 16, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget)
                ),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    protected void initEntity() {
        this.diffHandDamage = new float[]{2.5f, 3f, 4.5f};
        super.initEntity();
        setItemInHand(Item.get(Item.CROSSBOW));
        if (this.nbt.containsByte("IsIllagerCaptain")) {
            this.isIllagerCaptain = this.nbt.getBoolean("IsIllagerCaptain");
        } else if (Utils.rand(0, CAPTAIN_SPAWN_RARITY - 1) == 0) {
            setIllagerCaptain(true);
        }
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        List<Item> drops = new ArrayList<>();
        int arrows = Utils.rand(0, 2 + looting);
        if (arrows > 0) drops.add(Item.get(Item.ARROW, 0, arrows));
        if (isRaider()) {
            addRaidDrops(drops, weapon);
        }
        return drops.toArray(Item.EMPTY_ARRAY);
    }

    @Override
    public void kill() {
        if (isIllagerCaptain && this.getLastDamageCause() instanceof EntityDamageByEntityEvent ev) {
            Entity damager = ev.getDamager();
            if (damager instanceof Player killer) {
                int looting = killer.getItemInHand().getEnchantmentLevel(Enchantment.ID_LOOTING);
                Effect badOmen = Effect.get(EffectType.BAD_OMEN);
                badOmen.setDuration(CAPTAIN_BAD_OMEN_DURATION_TICKS);
                badOmen.setAmplifier(Math.min(looting, 4));
                killer.addEffect(badOmen);
            }
        }
        super.kill();
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
        return HealthComponent.value(24);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.35f);
    }

    @Override
    public String getOriginalName() {
        return "Pillager";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("pillager", "monster", "illager", "mob");
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return super.attackTarget(entity) || entity instanceof EntityGolem;
    }
}
