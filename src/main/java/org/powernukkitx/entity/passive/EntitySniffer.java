package org.powernukkitx.entity.passive;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.FluctuateController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.PassByTimeEvaluator;
import org.powernukkitx.entity.ai.evaluator.ProbabilityEvaluator;
import org.powernukkitx.entity.ai.executor.IBehaviorExecutor;
import org.powernukkitx.entity.ai.executor.AnimalGrowExecutor;
import org.powernukkitx.entity.ai.executor.BreedingExecutor;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.LookAtTargetExecutor;
import org.powernukkitx.entity.ai.executor.LoveTimeoutExecutor;
import org.powernukkitx.entity.ai.executor.TemptExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.components.AgeableComponent;
import org.powernukkitx.entity.components.BreedableComponent;
import org.powernukkitx.entity.components.HealableComponent;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.data.property.EntityProperty;
import org.powernukkitx.entity.data.property.EnumEntityProperty;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.particle.TerrainParticle;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.tags.BlockTags;
import org.powernukkitx.utils.Utils;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class EntitySniffer extends EntityAnimal {
    private static final int DIG_COOLDOWN_TICKS = 20 * 60 * 8;
    private static final int SEARCH_START_TICK = 35;
    private static final int DIGGING_START_TICK = 70;
    private static final int DIGGING_PARTICLES_DELAY_TICKS = 34;
    private static final int DIGGING_DROP_SEED_OFFSET_TICKS = 120;
    private static final int DIGGING_PARTICLES_AMOUNT = 30;
    private static final int RISING_START_TICK = DIGGING_START_TICK + DIGGING_DROP_SEED_OFFSET_TICKS + 10;
    private static final int DIG_DURATION_TICKS = RISING_START_TICK + 20;
    private static final int MAX_RECENT_DIG_POSITIONS = 20;

    public static final EntityProperty[] PROPERTIES = new EntityProperty[]{
            new EnumEntityProperty("minecraft:sniffer_state", new String[]{
                    "idling",
                    "feeling_happy",
                    "scenting",
                    "sniffing",
                    "searching",
                    "digging",
                    "rising"
            }, "idling", true)
    };

    private int nextDigTick;
    private final Deque<BlockVector3> recentDigPositions = new ArrayDeque<>();

    public EntitySniffer(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public @NotNull String getIdentifier() {
        return SNIFFER;
    }

    @Override
    public float getWidth() {
        if (isBaby()) {
            return 0.855F;
        }
        return 1.9f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.7875F;
        }
        return 1.75f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(14);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.09f);
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public String getOriginalName() {
        return "Sniffer";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("sniffer", "mob");
    }

    @Override
    public @Nullable BreedableComponent getComponentBreedable() {
        return new BreedableComponent(
                null,
                null,
                null,
                null,
                Set.of(
                    ItemID.TORCHFLOWER_SEEDS
                ),
                List.of(
                    new BreedableComponent.BreedsWith(EntityID.SNIFFER, EntityID.SNIFFER)
                ),
                true,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                null
        );
    }

    @Override
    public HealableComponent getComponentHealable() {
        return new HealableComponent(
                List.of(
                    new HealableComponent.Item(ItemID.TORCHFLOWER_SEEDS, 2)
                )
        );
    }

    @Override
    public AgeableComponent getComponentAgeable() {
        return new AgeableComponent(
                null,
                2400f,
                List.of(
                    new AgeableComponent.FeedItem(ItemID.TORCHFLOWER_SEEDS)
                ),
                null,
                null,
                null
        );
    }

    private static final Set<String> TEMPT_ITEMS = Set.of(
        ItemID.TORCHFLOWER_SEEDS
    );

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                    new Behavior(
                        new LoveTimeoutExecutor(20 * 30),
                            e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                        2, 1
                    ),
                    new Behavior(
                        new AnimalGrowExecutor(),
                            all(
                                    Entity::isAgeable,
                                    Entity::isBaby,
                                e -> !e.isGrowthPaused(),
                                e -> e.getTicksGrowLeft() > 0
                            ),
                        1, 1, 1200
                    )
                ),
                Set.of(
                    new Behavior(
                        new SnifferDigExecutor(),
                            all(
                                e -> e instanceof EntitySniffer sniffer && sniffer.canStartDigging(),
                                new ProbabilityEvaluator(1, 20)
                            ),
                        5, 1, 1, false
                    ),
                    new Behavior(
                        new FlatRandomRoamExecutor(0.4f, 12, 40, true, 100, true, 10),
                            all(
                                e -> !((EntitySniffer) e).isDigging(),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 100)
                            ),
                        4, 1
                    ),
                    new Behavior(
                        new SnifferBreedingExecutor(16, 200, 0.25f),
                            all(
                                e -> !((EntitySniffer) e).isDigging(),
                                e -> !e.isBaby(),
                                e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)
                            ),
                        3, 1
                    ),
                    new Behavior(
                        new TemptExecutor(1.25f, TEMPT_ITEMS),
                            all(
                                e -> !((EntitySniffer) e).isDigging(),
                                e -> !e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                                e -> TemptExecutor.hasTemptingPlayer(e, false, 10, TEMPT_ITEMS)
                            ),
                        2, 1
                    ),
                    new Behavior(
                        new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100),
                            all(
                                e -> !((EntitySniffer) e).isDigging(),
                                new ProbabilityEvaluator(4, 10)
                            ),
                        1, 1, 100
                    ),
                    new Behavior(
                        new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10),
                            entity -> !((EntitySniffer) entity).isDigging(),
                        1, 1
                    )
                ),
                Set.of(
                    new NearestPlayerSensor(8, 0, 20)
                ),
                Set.of(
                    new WalkController(),
                    new LookController(true, true),
                    new FluctuateController()
                ),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.nextDigTick = level.getTick() + DIG_COOLDOWN_TICKS;
        setSnifferState(SnifferState.IDLING);
        setAmbientSoundEvent(Sound.MOB_SNIFFER_IDLE);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
    }

    public boolean isDigging() {
        return getDataFlag(ActorFlags.DIGGING);
    }

    private boolean canStartDigging() {
        if (isBaby() || isDigging() || !onGround || isInsideOfWater() || getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE) || level.getTick() < nextDigTick) {
            return false;
        }
        BlockVector3 pos = getDigPosition();
        return !recentDigPositions.contains(pos) && isDiggable(level.getBlock(pos.asVector3()));
    }

    private BlockVector3 getDigPosition() {
        double radians = Math.toRadians(yaw);
        int x = getFloorX() + (int) Math.round(-Math.sin(radians));
        int z = getFloorZ() + (int) Math.round(Math.cos(radians));
        return new BlockVector3(x, getFloorY() - 1, z);
    }

    private boolean isDiggable(Block block) {
        return block != null && block.hasTag(BlockTags.DIRT);
    }

    private void rememberDigPosition(BlockVector3 pos) {
        recentDigPositions.remove(pos);
        recentDigPositions.addLast(pos);
        while (recentDigPositions.size() > MAX_RECENT_DIG_POSITIONS) {
            recentDigPositions.removeFirst();
        }
    }

    private void dropAncientSeed() {
        Item seed = Item.get(Utils.rand(0, 1) == 0 ? ItemID.TORCHFLOWER_SEEDS : ItemID.PITCHER_POD);
        getLevel().dropItem(getPosition().add(0, 0.25, 0), seed);
        getLevel().addSound(this, Sound.MOB_SNIFFER_DROP_SEED);
        getLevel().addSound(this, Sound.MOB_SNIFFER_HAPPY);
    }

    private void emitDiggingParticles(BlockVector3 digPosition) {
        Block block = getLevel().getBlock(digPosition.asVector3());
        if (!isDiggable(block)) {
            return;
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < DIGGING_PARTICLES_AMOUNT; i++) {
            getLevel().addParticle(new TerrainParticle(
                    digPosition.add(
                            0.5 + random.nextDouble(-0.35, 0.35),
                            1.0,
                            0.5 + random.nextDouble(-0.35, 0.35)
                    ),
                    block
            ));
        }
    }

    private void setSnifferState(SnifferState state) {
        setEnumEntityProperty("minecraft:sniffer_state", state.value);
    }

    private enum SnifferState {
        IDLING("idling"),
        SNIFFING("sniffing"),
        SEARCHING("searching"),
        DIGGING("digging"),
        RISING("rising");

        private final String value;

        SnifferState(String value) {
            this.value = value;
        }
    }

    private static final class SnifferDigExecutor implements IBehaviorExecutor {
        private int startTick;
        private BlockVector3 digPosition;
        private boolean droppedSeed;
        private boolean startedDigging;
        private boolean startedSearching;
        private boolean startedRising;

        @Override
        public void onStart(EntityIntelligent entity) {
            EntitySniffer sniffer = (EntitySniffer) entity;
            this.startTick = sniffer.getLevel().getTick();
            this.digPosition = sniffer.getDigPosition();
            this.droppedSeed = false;
            this.startedDigging = false;
            this.startedSearching = false;
            this.startedRising = false;
            sniffer.setMovementSpeed(0);
            sniffer.setDataFlag(ActorFlags.BODY_ROTATION_BLOCKED, true);
            sniffer.setSnifferState(SnifferState.SNIFFING);
            sniffer.getLevel().addSound(sniffer, Sound.MOB_SNIFFER_LONG_SNIFF);
        }

        @Override
        public boolean execute(EntityIntelligent entity) {
            EntitySniffer sniffer = (EntitySniffer) entity;
            int elapsed = sniffer.getLevel().getTick() - this.startTick;
            int diggingElapsed = elapsed - DIGGING_START_TICK;

            if (!this.startedSearching && elapsed >= SEARCH_START_TICK) {
                this.startedSearching = true;
                sniffer.setSnifferState(SnifferState.SEARCHING);
                sniffer.getLevel().addSound(sniffer, Sound.MOB_SNIFFER_SEARCHING);
            }

            if (!this.startedDigging && elapsed >= DIGGING_START_TICK) {
                this.startedDigging = true;
                sniffer.setDataFlag(ActorFlags.DIGGING, true);
                sniffer.setSnifferState(SnifferState.DIGGING);
                sniffer.getLevel().addSound(sniffer, Sound.MOB_SNIFFER_DIGGING);
            }

            if (this.startedDigging && !this.droppedSeed && elapsed >= DIGGING_START_TICK + DIGGING_DROP_SEED_OFFSET_TICKS) {
                if (sniffer.isDiggable(sniffer.getLevel().getBlock(this.digPosition.asVector3()))) {
                    sniffer.dropAncientSeed();
                    sniffer.rememberDigPosition(this.digPosition);
                }
                this.droppedSeed = true;
            }

            if (!this.startedRising && elapsed >= RISING_START_TICK) {
                this.startedRising = true;
                sniffer.setSnifferState(SnifferState.RISING);
                sniffer.getLevel().addSound(sniffer, Sound.MOB_SNIFFER_STAND_UP);
            }

            if (diggingElapsed > DIGGING_PARTICLES_DELAY_TICKS && diggingElapsed < DIGGING_DROP_SEED_OFFSET_TICKS) {
                sniffer.emitDiggingParticles(this.digPosition);
            }

            return elapsed < DIG_DURATION_TICKS;
        }

        @Override
        public void onStop(EntityIntelligent entity) {
            stop((EntitySniffer) entity);
        }

        @Override
        public void onInterrupt(EntityIntelligent entity) {
            stop((EntitySniffer) entity);
        }

        private void stop(EntitySniffer sniffer) {
            if (this.startedDigging) {
                sniffer.setDataFlag(ActorFlags.DIGGING, false);
            }
            sniffer.setDataFlag(ActorFlags.BODY_ROTATION_BLOCKED, false);
            sniffer.setSnifferState(SnifferState.IDLING);
            sniffer.setMovementSpeed(sniffer.getMovementSpeedDefault());
            sniffer.nextDigTick = sniffer.getLevel().getTick() + DIG_COOLDOWN_TICKS;
        }
    }

    private static final class SnifferBreedingExecutor extends BreedingExecutor {
        private SnifferBreedingExecutor(int findingRange, int duration, float moveSpeed) {
            super(findingRange, duration, moveSpeed);
        }

        @Override
        protected void breed(EntityIntelligent parent1, EntityIntelligent parent2) {
            int now = parent1.getLevel().getTick();
            parent1.getLevel().dropItem(parent1.getPosition().add(0, 0.25, 0), Item.get(BlockID.SNIFFER_EGG));
            parent1.getLevel().addSound(parent1, Sound.MOB_SNIFFER_HAPPY);
            finishBreeding(parent1, parent2, now);
        }
    }
}
