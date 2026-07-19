package org.powernukkitx.entity.passive;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockBed;
import org.powernukkitx.block.BlockDoor;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.FluctuateController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.DistanceEvaluator;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import org.powernukkitx.entity.ai.evaluator.PassByTimeEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.AnimalGrowExecutor;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.FleeFromTargetExecutor;
import org.powernukkitx.entity.ai.executor.MoveToTargetExecutor;
import org.powernukkitx.entity.ai.executor.NearbyFlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.executor.villager.DoorExecutor;
import org.powernukkitx.entity.ai.executor.villager.GossipExecutor;
import org.powernukkitx.entity.ai.executor.villager.SleepExecutor;
import org.powernukkitx.entity.ai.executor.villager.VillagerBreedingExecutor;
import org.powernukkitx.entity.ai.executor.villager.WillingnessExecutor;
import org.powernukkitx.entity.ai.executor.villager.WorkExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.DoorCapableWalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.BlockSensor;
import org.powernukkitx.entity.ai.sensor.NearestEntitySensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.data.profession.Profession;
import org.powernukkitx.entity.item.EntityItem;
import org.powernukkitx.entity.mob.EntityIronGolem;
import org.powernukkitx.entity.mob.EntityZombie;
import org.powernukkitx.entity.mob.EntityZombieVillagerV2;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.event.entity.EntityTransformEvent;
import org.powernukkitx.inventory.EntityEquipmentInventory;
import org.powernukkitx.inventory.InventoryHolder;
import org.powernukkitx.inventory.InventorySlice;
import org.powernukkitx.inventory.TradeInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.ParticleEffect;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.poi.PoiManager;
import org.powernukkitx.level.poi.PoiTypeRegistry;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.IntTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.ItemHelper;
import org.powernukkitx.utils.TradeRecipeBuildUtils;
import org.powernukkitx.utils.Utils;
import org.powernukkitx.utils.random.NukkitRandom;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.packet.ActorEventPacket;
import org.cloudburstmc.protocol.bedrock.packet.TakeItemActorPacket;
import org.cloudburstmc.protocol.bedrock.packet.UpdateTradePacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

// TODO: Rework villagers, it seems to be broken, movement stucked in walls, breeding logic not reliable, professions not changing when breaking blocks, etc...
public class EntityVillagerV2 extends EntityIntelligent implements InventoryHolder, IEntityNPC {
    @Override
    @NotNull
    public String getIdentifier() {
        return VILLAGER_V2;
    }

    private List<Integer> tradeNetId;

    public List<Integer> getTradeNetIds() {
        if (tradeNetId == null) {
            tradeNetId = new ArrayList<>();
        }
        return tradeNetId;
    }

    public List<CompoundTag> getRecipes() {
        return new ObjectArrayList<>(TradeRecipeBuildUtils.RECIPE_MAP.entrySet().stream().filter(t -> getTradeNetIds().contains(t.getKey())).toList().stream().map(Map.Entry::getValue).toList());
    }

    public int[] tierExpRequirement;

    protected TradeInventory tradeInventory;

    private EntityEquipmentInventory inventory;

    protected Boolean canTrade;

    protected String displayName;

    protected int tradeTier;

    protected int maxTradeTier;

    protected int tradeExp;

    protected int tradeSeed;

    /**
     * 0 generic
     * 1 farmer
     * 2 fisherman
     * 3 shepherd
     * 4 fletcher
     * 5 librarian
     * 6 cartographer
     * 7 cleric
     * 8 armor
     * 9 weapon
     * 10 tool
     * 11 butcher
     * 12 butcher
     * 13 mason
     * 14 nitwit
     */
    protected int profession;

    public EntityVillagerV2(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        this.tierExpRequirement = new int[]{0, 10, 70, 150, 250};

        applyProfession();
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        new Behavior(new DoorExecutor(), all(
                                entity -> {
                                    Block block = getMemoryStorage().get(CoreMemoryTypes.NEAREST_BLOCK_2);
                                    if (block == null || getMoveDirectionEnd() == null) return false;
                                    return getLevel().raycastBlocks(this, getMoveDirectionEnd(), true, false, 0.5d).contains(block);
                                }
                        ), 4, 1),
                        new Behavior(
                                new WillingnessExecutor(),
                                all(
                                        entity -> getFoodPoints() >= 12,
                                        entity -> !isBaby(),
                                        entity -> !getMemoryStorage().get(CoreMemoryTypes.WILLING),
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_IN_LOVE_TIME, 6000, Integer.MAX_VALUE)
                                ), 3, 1, 1, false
                        ),
                        //grow
                        new Behavior(
                                new AnimalGrowExecutor(),
                                all(
                                        new PassByTimeEvaluator(CoreMemoryTypes.ENTITY_SPAWN_TIME, 20 * 60 * 20, Integer.MAX_VALUE),
                                        entity -> entity instanceof EntityAnimal animal && animal.isBaby()
                                ), 2, 1, 1200
                        ),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_VILLAGER_IDLE, isBaby() ? 1.3f : 0.8f, isBaby() ? 1.7f : 1.2f, 1, 1), new RandomSoundEvaluator(), 1, 1)
                ),
                Set.of(
                        new Behavior(entity -> {
                            setMoveTarget(null);
                            setLookTarget(getTradeInventory().getViewers().stream().findFirst().get());
                            return true;
                        }, entity -> getTradeInventory() != null && !getTradeInventory().getViewers().isEmpty(), 9, 1),
                        new Behavior(new FleeFromTargetExecutor(CoreMemoryTypes.NEAREST_ZOMBIE, 0.5f, true, 8), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_ZOMBIE),
                                new DistanceEvaluator(CoreMemoryTypes.NEAREST_ZOMBIE, 8),
                                entity -> getMemoryStorage().notEmpty(CoreMemoryTypes.NEAREST_ZOMBIE) && getMemoryStorage().get(CoreMemoryTypes.NEAREST_ZOMBIE) instanceof EntityIntelligent i && i.getMemoryStorage().notEmpty(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET) && i.getMemoryStorage().get(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET) == this,
                                entity -> getMemoryStorage().notEmpty(CoreMemoryTypes.NEAREST_ZOMBIE) && getLevel().raycastBlocks(this, getMemoryStorage().get(CoreMemoryTypes.NEAREST_ZOMBIE)).isEmpty()
                        ), 8, 1),
                        new Behavior(new SleepExecutor(), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.OCCUPIED_BED),
                                new DistanceEvaluator(CoreMemoryTypes.OCCUPIED_BED, 2),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 100),
                                entity -> getLevel().getDayTime() >= 12000 && entity.getLevel().getDayTime() < Level.TIME_FULL
                        ), 7, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.OCCUPIED_BED, 0.3f, true), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.OCCUPIED_BED),
                                any(
                                        entity -> getLevel().getDayTime() >= 12000 && entity.getLevel().getDayTime() < Level.TIME_FULL,
                                        all(
                                                entity -> getLevel().getDayTime() >= 11000 && entity.getLevel().getDayTime() < 12000,
                                                not(new DistanceEvaluator(CoreMemoryTypes.OCCUPIED_BED, 5))
                                        )
                                )
                        ), 6, 1),
                        new Behavior(new NearbyFlatRandomRoamExecutor(CoreMemoryTypes.OCCUPIED_BED, 0.2f, 5, 100, false, -1, true, 10), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.OCCUPIED_BED),
                                entity -> getLevel().getDayTime() >= 11000 && entity.getLevel().getDayTime() < 12000
                        ), 5, 1),
                        new Behavior(new WorkExecutor(), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.SITE_BLOCK),
                                any(
                                        entity -> getLevel().getDayTime() >= 0 && entity.getLevel().getDayTime() < 8000,
                                        entity -> getLevel().getDayTime() >= 10000 && entity.getLevel().getDayTime() < 11000
                                )
                        ), 4, 1, 1),
                        new Behavior(new GossipExecutor(CoreMemoryTypes.GOSSIP_TARGET), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.GOSSIP_TARGET),
                                entity -> !isBaby()
                        ), 3, 1),
                        new Behavior(
                                new VillagerBreedingExecutor(16, 100, 0.5f),
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.ENTITY_SPOUSE),
                                2, 1
                        ),
                        new Behavior(new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10), (entity -> true), 1, 1)
                ),
                Set.of(
                        entity -> {
                            if (getLevel().getTick() % 120 == 0) {
                                if (getMemoryStorage().isEmpty(CoreMemoryTypes.OCCUPIED_BED)) {
                                    getLevel().getPoiManager().findClosest(type -> type == PoiTypeRegistry.HOME, this, 48, PoiManager.Occupancy.HAS_SPACE)
                                            .ifPresent(record -> {
                                                BlockVector3 pos = record.getPos();
                                                if (getLevel().getBlock(pos.x, pos.y, pos.z) instanceof BlockBed bed && !bed.isOccupied()) {
                                                    setBed(bed.getFootPart());
                                                }
                                            });
                                } else if (!getMemoryStorage().get(CoreMemoryTypes.OCCUPIED_BED).isBedValid()) {
                                    getLevel().getPoiManager().release(getMemoryStorage().get(CoreMemoryTypes.OCCUPIED_BED).asBlockVector3());
                                    this.nbt.remove("bed");
                                    getMemoryStorage().clear(CoreMemoryTypes.OCCUPIED_BED);
                                } else {
                                    getLevel().getPoiManager().ensureTicket(getMemoryStorage().get(CoreMemoryTypes.OCCUPIED_BED).asBlockVector3());
                                }
                            }
                        },
                        entity -> {
                            if (getLevel().getTick() % 60 == 0) {
                                Stream<EntityVillagerV2> entities = Arrays.stream(entity.getLevel().getCollidingEntities(entity.getBoundingBox().grow(64, 3, 64))).filter(entity1 -> entity1 instanceof EntityVillagerV2 && entity1 != this).map(entity1 -> ((EntityVillagerV2) entity1));
                                if (getLevel().getDayTime() > 8000 && getLevel().getDayTime() < 10000) {
                                    if (getMemoryStorage().isEmpty(CoreMemoryTypes.GOSSIP_TARGET)) {
                                        double minDistance = Float.MAX_VALUE;
                                        EntityVillagerV2 nearest = null;
                                        for (EntityVillagerV2 entity1 : entities.toList()) {
                                            double distance = entity1.distance(this);
                                            if (distance < minDistance) {
                                                minDistance = distance;
                                                nearest = entity1;
                                            }
                                        }
                                        if (nearest != null) {
                                            getMemoryStorage().put(CoreMemoryTypes.GOSSIP_TARGET, nearest);
                                        } else getMemoryStorage().clear(CoreMemoryTypes.GOSSIP_TARGET);
                                    }
                                } else {
                                    if (shouldShareFood()) {
                                        entities.filter(entity1 -> entity1.isHungry() && entity1.distance(this) < 16).findAny().ifPresentOrElse(entity1 -> {
                                            getMemoryStorage().put(CoreMemoryTypes.GOSSIP_TARGET, entity1);
                                            entity1.getMemoryStorage().put(CoreMemoryTypes.GOSSIP_TARGET, this);
                                        }, () -> getMemoryStorage().clear(CoreMemoryTypes.GOSSIP_TARGET));
                                    } else if (!isHungry()) getMemoryStorage().clear(CoreMemoryTypes.GOSSIP_TARGET);
                                }
                            }
                        },
                        entity -> {
                            if (getLevel().getTick() % 30 == 0 && !isBaby()) {
                                Block siteBlock = getMemoryStorage().get(CoreMemoryTypes.SITE_BLOCK);
                                if (siteBlock != null && !siteBlock.getLevelBlock().getId().equals(siteBlock.getId())) {
                                    getLevel().getPoiManager().release(siteBlock.asBlockVector3());
                                    getMemoryStorage().clear(CoreMemoryTypes.SITE_BLOCK);
                                    if (getTradeExp() == 0) {
                                        setTradeSeed(new NukkitRandom().nextInt(Integer.MAX_VALUE - 1));
                                    }
                                } else if (siteBlock != null) {
                                    getLevel().getPoiManager().ensureTicket(siteBlock.asBlockVector3());
                                }

                                if (getMemoryStorage().isEmpty(CoreMemoryTypes.SITE_BLOCK)) {
                                    Profession currentProfession = Profession.getProfession(getProfession());
                                    String requiredBlockId = getTradeExp() != 0 && currentProfession != null ? currentProfession.getBlockID() : null;
                                    var poi = getLevel().getPoiManager().findClosest(
                                            type -> type.isJobSite() && (requiredBlockId == null || requiredBlockId.equals(type.jobSiteBlockId())),
                                            this, 16, PoiManager.Occupancy.HAS_SPACE);
                                    boolean acquired = false;
                                    if (poi.isPresent()) {
                                        BlockVector3 pos = poi.get().getPos();
                                        acquired = setProfessionBlock(getLevel().getBlock(pos.x, pos.y, pos.z));
                                    }
                                    if (!acquired && getTradeExp() == 0) setProfession(0, true);
                                }
                            }
                        },
                        entity -> {
                            if (getLevel().getTick() % 100 == 0 && getMemoryStorage().get(CoreMemoryTypes.WILLING)) {
                                var entities = entity.level.getEntities();
                                var maxDistanceSquared = -1d;
                                EntityVillagerV2 nearestInLove = null;
                                for (Entity e : entities) {
                                    double newDistance = e.distanceSquared(entity);
                                    if (e instanceof EntityVillagerV2 another
                                            && e != entity
                                            && !another.isBaby()
                                            && another.getMemoryStorage().get(CoreMemoryTypes.WILLING)
                                            && another.getMemoryStorage().isEmpty(CoreMemoryTypes.ENTITY_SPOUSE)
                                            && (maxDistanceSquared == -1 || newDistance < maxDistanceSquared)) {
                                        maxDistanceSquared = newDistance;
                                        nearestInLove = another;
                                    }
                                }
                                if (nearestInLove != null) {
                                    nearestInLove.getMemoryStorage().put(CoreMemoryTypes.ENTITY_SPOUSE, this);
                                    getMemoryStorage().put(CoreMemoryTypes.ENTITY_SPOUSE, nearestInLove);
                                }
                            }
                        },
                        new BlockSensor(BlockDoor.class, CoreMemoryTypes.NEAREST_BLOCK_2, 1, 0, 10),
                        new NearestEntitySensor(EntityZombie.class, CoreMemoryTypes.NEAREST_ZOMBIE, 8, 0)
                ),
                Set.of(new WalkController(), new LookController(true, true), new FluctuateController()),
                new SimpleFlatAStarRouteFinder(new DoorCapableWalkingPosEvaluator(), this),
                this
        );
    }

    public float getFloatingHeight() {
        return super.getFloatingHeight() * 0.7f;
    }

    public boolean isHungry() {
        return getFoodPoints() < 12;
    }

    public boolean shouldShareFood() {
        for (Item item : getInventory().getContents().values()) {
            if ((item.getId().equals(Item.BREAD) && item.getCount() >= 6)
                    || ((item.getId().equals(Item.CARROT) || item.getId().equals(Block.BEETROOT)) && item.getCount() >= 24)
                    || (item.getId().equals(Block.WHEAT) && item.getCount() >= 18 && getProfession() == 1)) return true;
        }
        return false;
    }

    public int getFoodPoints() {
        int points = 0;
        for (Item item : getInventory().getContents().values()) {
            points += switch (item.getId()) {
                case Item.BREAD -> 4;
                case Item.CARROT,
                     Item.POTATO,
                     Block.BEETROOT -> 1;
                default -> 0;
            } * item.getCount();
        }
        return points;
    }

    public void setBed(BlockBed bed) {
        if (bed.isBedValid() && getLevel().getPoiManager().takeAt(bed.getFootPart().asBlockVector3())) {
            getMemoryStorage().put(CoreMemoryTypes.OCCUPIED_BED, bed);
            for (int i = 0; i < 5; i++) {
                float randX = Utils.rand(0f, 0.5f);
                float randY = Utils.rand(0f, 0.3f);
                float randZ = Utils.rand(0f, 0.5f);
                this.getLevel().addParticleEffect(this.add(randX, this.getEyeHeight() + randY, randZ), ParticleEffect.VILLAGER_HAPPY);
                this.getLevel().addParticleEffect(bed.add(randX, 0.5625f + randY, randZ), ParticleEffect.VILLAGER_HAPPY);
            }
        }
    }

    public BlockBed getBed() {
        return getMemoryStorage().get(CoreMemoryTypes.OCCUPIED_BED);
    }

    public Block getSite() {
        return getMemoryStorage().get(CoreMemoryTypes.SITE_BLOCK);
    }

    @Override
    public boolean isAgeable() {
        return true;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public float getHeight() {
        if (isSleeping()) return getWidthR();
        return getHeightR();
    }

    @Override
    public float getWidth() {
        return getWidthR();
    }

    @Override
    public float getLength() {
        if (isSleeping()) return getHeightR();
        return super.getLength();
    }

    private float getWidthR() {
        if (this.isBaby()) {
            return 0.3f;
        }
        return 0.6f;
    }

    private float getHeightR() {
        if (this.isBaby()) {
            return 0.95f;
        }
        return 1.9f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(20);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.1f);
    }

    public boolean isSleeping() {
        return getDataFlag(ActorFlags.SLEEPING);
    }

    @Override
    public String getOriginalName() {
        return "VillagerV2";
    }

    @Override
    public Set<String> typeFamily() {
        return switch (profession) {
            case 1 -> Set.of("villager", "peasant", "farmer", "mob");
            case 2 -> Set.of("villager", "peasant", "fisherman", "mob");
            case 3 -> Set.of("villager", "peasant", "shepherd", "mob");
            case 4 -> Set.of("villager", "peasant", "fletcher", "mob");
            case 5 -> Set.of("villager", "librarian", "mob");
            case 6 -> Set.of("villager", "cartographer", "mob");
            case 7 -> Set.of("villager", "priest", "cleric", "mob");
            case 8 -> Set.of("villager", "blacksmith", "armorer", "mob");
            case 9 -> Set.of("villager", "blacksmith", "weaponsmith", "mob");
            case 10 -> Set.of("villager", "blacksmith", "toolsmith", "mob");
            case 11 -> Set.of("villager", "artisan", "butcher", "mob");
            case 12 -> Set.of("villager", "artisan", "leatherworker", "mob");
            case 13 -> Set.of("villager", "artisan", "stone_mason", "mob");
            case 14 -> Set.of("villager", "peasant", "nitwit", "mob");
            default -> Set.of("villager", "peasant", "unskilled", "mob");
        };
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setPersistent(true);
        this.setTradingPlayer(0L);
        final CompoundTag nbtMap = this.getNbt();
        if (!nbtMap.contains("tradeSeed")) {
            this.setTradeSeed(new NukkitRandom().nextInt(Integer.MAX_VALUE - 1));
        } else {
            this.tradeSeed =nbtMap.getInt("tradeSeed");
        }
        if (!nbtMap.contains("canTrade")) {
            this.setCanTrade(!(profession == 0 || profession == 14));
        } else {
            this.canTrade =nbtMap.getBoolean("canTrade");
        }
        if (!nbtMap.contains("displayName") && profession != 0) {
            this.setDisplayName(getProfessionName(profession));
        } else {
            this.displayName = nbtMap.getString("displayName");
        }
        if (!nbtMap.contains("tradeTier")) {
            this.setTradeTier(1);
        } else {
            this.tradeTier = nbtMap.getInt("tradeTier");
        }
        if (!nbtMap.contains("maxTradeTier")) {
            this.setMaxTradeTier(5);
        } else {
            var maxTradeTier = nbtMap.getInt("maxTradeTier");
            this.maxTradeTier = maxTradeTier;
            this.setDataProperty(ActorDataTypes.MAX_TRADE_TIER, maxTradeTier);
        }
        if (!nbtMap.contains("tradeExp")) {
            this.setTradeExp(0);
        } else {
            var tradeExp = nbtMap.getInt("tradeExp");
            this.tradeExp = tradeExp;
            this.setDataProperty(ActorDataTypes.TRADE_EXPERIENCE, tradeExp);
        }
        if (nbtMap.contains("clothing")) {
            this.setDataProperty(ActorDataTypes.MARK_VARIANT, nbtMap.getInt("clothing"));
        } else {
            BlockVector3 bv = asBlockVector3();
            this.setDataProperty(ActorDataTypes.MARK_VARIANT, Clothing.getClothing(getLevel().getBiomeId(bv.x, bv.y, bv.z)).ordinal());
        }
        if (nbtMap.contains("bed")) {
            CompoundTag compound = nbtMap.getCompound("bed");
            Vector3 vector = new Vector3(compound.getInt("x"), compound.getInt("y"), compound.getInt("z"));
            if (getLevel().getBlock(vector) instanceof BlockBed bed) {
                setBed(bed);
            }
        }
        getMemoryStorage().put(CoreMemoryTypes.GOSSIP, new Object2ObjectArrayMap<>());
        if (nbtMap.contains("gossip")) {
            var gossipMap = getMemoryStorage().get(CoreMemoryTypes.GOSSIP);
            CompoundTag gossipTag = nbtMap.getCompound("gossip");
            for (String key : gossipTag.getTags().keySet()) {
                ListTag<IntTag> gossipValues = gossipTag.getList(key, IntTag.class);
                IntArrayList valueMap = new IntArrayList();
                gossipValues.getAll().forEach(value -> valueMap.addLast(value.data));
                gossipMap.put(key, valueMap);
            }
        }
        if (nbtMap.contains("purifyPlayer")) {
            String xuid = this.nbt.getString("purifyPlayer");
            this.nbt.remove("purifyPlayer");
            this.addGossip(xuid, Gossip.MAJOR_POSITIVE, 20);
            this.addGossip(xuid, Gossip.MINOR_POSITIVE, 25);
        }
        if (nbtMap.contains("siteBlock")) {
            CompoundTag tag = nbtMap.getCompound("siteBlock");
            Vector3 vector3 = new Vector3(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
            Block block = getLevel().getBlock(vector3);
            setProfessionBlock(block);
        }
        if (nbtMap.contains("profession")) {
            setProfession(nbtMap.getInt("profession"), false);
        }
        this.inventory = new EntityEquipmentInventory(this);
        if (nbtMap.contains("Inventory") && nbtMap.get("Inventory") instanceof ListTag<?>) {
            var inventory = this.getInventory();
            ListTag<CompoundTag> inventoryList = nbtMap.getList("Inventory", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {
                int slot = item.getByte("Slot");
                inventory.setItem(slot, ItemHelper.read(item));//inventory 0-39
            }
        }
        if (canTrade) {
            tradeInventory = new TradeInventory(this);
        }
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (super.attack(source)
                && source instanceof EntityDamageByEntityEvent event
                && event.getDamager() instanceof Player player) {
            addGossip(player.getXUID(), Gossip.MINOR_NEGATIVE, 25);
            final ActorEventPacket pk = new ActorEventPacket();
            pk.setTargetRuntimeID(this.getId());
            pk.setType(ActorEvent.VILLAGER_ANGRY);
            Server.broadcastPacket(getViewers().values(), pk);
            for (Entity e : getLevel().getCollidingEntities(getBoundingBox().grow(48, 8, 48))) {
                if (e instanceof EntityIronGolem golem && golem.isAlive() && !golem.hasOwner(false)) {
                    golem.getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, player);
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public void kill() {
        if (getLastDamageCause() instanceof EntityDamageByEntityEvent source) {
            if(source.getDamager() instanceof Player player) {
                Arrays.stream(this.getLevel().getCollidingEntities(this.getBoundingBox().grow(16, 16, 16)))
                        .filter(entity -> entity instanceof EntityVillagerV2)
                        .forEach(entity -> ((EntityVillagerV2) entity).addGossip(player.getXUID(), Gossip.MAJOR_NEGATIVE, 25));
            } else if (source.getDamager() instanceof EntityZombie && shouldTransformToZombieVillager() && transformZombie()) return;
        }
        super.kill();
    }

    private boolean shouldTransformToZombieVillager() {
        return switch (Server.getInstance().getDifficulty()) {
            case 2 -> ThreadLocalRandom.current().nextBoolean();
            case 3 -> true;
            default -> false;
        };
    }

    public void addGossip(String xuid, Gossip gossip, int value) {
        var gossipMap = getMemoryStorage().get(CoreMemoryTypes.GOSSIP);
        if (!gossipMap.containsKey(xuid))
            gossipMap.put(xuid, new IntArrayList(Collections.nCopies(Gossip.VALUES.length, 0)));
        IntArrayList values = gossipMap.get(xuid);
        int ordinal = gossip.ordinal();
        values.set(ordinal, Math.min(gossip.max, values.getInt(ordinal) + value));
        getLevel().getPlayers().values().stream()
                .filter(player -> player.getXUID().equals(xuid))
                .findFirst()
                .ifPresent(this::updateTrades);
    }

    public void spreadGossip() {
        Arrays.stream(getLevel().getCollidingEntities(getBoundingBox().grow(2, 0, 2))).filter(entity2 -> entity2 instanceof EntityVillagerV2).map(entity2 -> ((EntityVillagerV2) entity2)).forEach(target -> {
            var gossipMap = getMemoryStorage().get(CoreMemoryTypes.GOSSIP);
            var targetGossipMap = target.getMemoryStorage().get(CoreMemoryTypes.GOSSIP);
            for (var entry : gossipMap.object2ObjectEntrySet()) {
                String xuid = entry.getKey();
                if (!targetGossipMap.containsKey(xuid))
                    targetGossipMap.put(xuid, new IntArrayList(Collections.nCopies(Gossip.VALUES.length, 0)));
                IntArrayList targetValues = targetGossipMap.get(xuid);
                for (Gossip gossip : Gossip.VALUES) {
                    int ordinal = gossip.ordinal();
                    targetValues.set(ordinal, Math.max(targetValues.getInt(ordinal), entry.getValue().getInt(ordinal) - gossip.penalty));
                }
            }
        });
    }

    public int getGossip(String xuid, Gossip gossip) {
        var gossipMap = getMemoryStorage().get(CoreMemoryTypes.GOSSIP);
        if (!gossipMap.containsKey(xuid))
            gossipMap.put(xuid, new IntArrayList(Collections.nCopies(Gossip.VALUES.length, 0)));
        IntArrayList values = gossipMap.get(xuid);
        int ordinal = gossip.ordinal();
        return values.getInt(ordinal) * gossip.multiplier;
    }

    public int getReputation(Player player) {
        int reputation = 0;
        IntArrayList values = getMemoryStorage().get(CoreMemoryTypes.GOSSIP).get(player.getXUID());
        if (values != null) {
            for (Gossip gossip : Gossip.VALUES) {
                reputation += (values.getInt(gossip.ordinal()) * gossip.multiplier);
            }
        }
        return reputation;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putInt("profession", this.getProfession())
                .putBoolean("isTrade", this.canTrade())
                .putString("displayName", this.getDisplayName())
                .putInt("tradeTier", this.getTradeTier())
                .putInt("maxTradeTier", this.getMaxTradeTier())
                .putInt("tradeExp", this.getTradeExp())
                .putInt("tradeSeed", this.getTradeSeed())
                .putInt("clothing", this.getDataProperty(ActorDataTypes.MARK_VARIANT));
        CompoundTag gossipTag = new CompoundTag();
        for (var v : getMemoryStorage().get(CoreMemoryTypes.GOSSIP).object2ObjectEntrySet()) {
            ListTag<IntTag> gossipValues = new ListTag<>();
            for (int value : v.getValue()) {
                gossipValues.add(new IntTag(value));
            }
            gossipTag.putList(v.getKey(), gossipValues);
        }
        this.nbt.putCompound("gossip", gossipTag);
        if (getMemoryStorage().notEmpty(CoreMemoryTypes.OCCUPIED_BED)) {
            BlockBed bed = getMemoryStorage().get(CoreMemoryTypes.OCCUPIED_BED);
            this.nbt.putCompound("bed", new CompoundTag()
                    .putInt("x", bed.getFloorX())
                    .putInt("y", bed.getFloorY())
                    .putInt("z", bed.getFloorZ()));
        }
        if (getMemoryStorage().notEmpty(CoreMemoryTypes.SITE_BLOCK)) {
            Block site = getMemoryStorage().get(CoreMemoryTypes.SITE_BLOCK);
            this.nbt.putCompound("siteBlock", new CompoundTag()
                    .putInt("x", site.getFloorX())
                    .putInt("y", site.getFloorY())
                    .putInt("z", site.getFloorZ()));
        }
        if (this.getInventory() != null) {
            final ListTag<CompoundTag> inventoryTag = new ListTag<>();
            for (var entry : getInventory().getContents().entrySet()) {
                inventoryTag.add(ItemHelper.write(entry.getValue(), entry.getKey()));
            }
            this.nbt.putList("Inventory", inventoryTag);
        }
    }

    /**
     * Gets the hardcoded displayName corresponding to the villager profession id.
     */
    private String getProfessionName(int profession) {
        return switch (profession) {
            case 1 -> "entity.villager.farmer";
            case 2 -> "entity.villager.fisherman";
            case 3 -> "entity.villager.shepherd";
            case 4 -> "entity.villager.fletcher";
            case 5 -> "entity.villager.librarian";
            case 6 -> "entity.villager.cartographer";
            case 7 -> "entity.villager.cleric";
            case 8 -> "entity.villager.armor";
            case 9 -> "entity.villager.weapon";
            case 10 -> "entity.villager.tool";
            case 11 -> "entity.villager.butcher";
            case 12 -> "entity.villager.leather";
            case 13 -> "entity.villager.mason";
            case 14 -> "entity.villager.nitwit";
            default -> null;
        };
    }

    /**
     * @return the villager's profession id
     */
    public int getProfession() {
        return profession;
    }


    public void setProfession(int profession, boolean apply) {
        this.profession = profession;
        this.setDataProperty(ActorDataTypes.VARIANT, profession);
        if (apply) applyProfession();
    }

    public boolean setProfessionBlock(Block block) {
        for (Profession profession : Profession.getProfessions().values()) {
            if (getTradeExp() != 0 && profession.getIndex() != getProfession()) continue;
            if (block.getId().equals(profession.getBlockID())) {
                if (!getLevel().getPoiManager().takeAt(block.asBlockVector3())) return false;
                getMemoryStorage().put(CoreMemoryTypes.SITE_BLOCK, block);
                setProfession(profession.getIndex(), true);
                return true;
            }
        }
        return false;
    }

    /**
     * This method is generally not used by plugins.
     */
    public void setTradingPlayer(Long eid) {
        this.setDataProperty(ActorDataTypes.TRADE_TARGET, eid);
    }

    /**
     * @return whether this villager can trade
     */
    public boolean canTrade() {
        return canTrade;
    }

    /**
     * Sets whether the villager can trade.
     *
     * @param canTrade true if trading is allowed
     */
    public void setCanTrade(boolean canTrade) {
        this.canTrade = canTrade;
        this.nbt.putBoolean("canTrade", canTrade);
    }

    /**
     * @return the display name of the trading UI
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName the display name of the trading UI to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.nbt.putString("displayName", displayName);
    }

    /**
     * @return the villager's trade tier
     */
    public int getTradeTier() {
        return tradeTier;
    }

    /**
     * @param tradeTier <p>the villager's trade tier (1-{@link EntityVillagerV2#maxTradeTier})</p>
     */
    public void setTradeTier(int tradeTier) {
        this.tradeTier = --tradeTier;
        this.nbt.putInt("tradeTier", this.tradeTier);
    }

    public void updateTrades(Player player) {
        if (player.getTopWindow().isEmpty() || player.getTopWindow().get() != getTradeInventory()) return;
        var updateTradePacket = new UpdateTradePacket();
        updateTradePacket.setContainerId((byte) player.getWindowId(getTradeInventory()));
        updateTradePacket.setTradeTier(this.getTradeTier());
        updateTradePacket.setEntityUniqueId(this.getId());
        updateTradePacket.setLastTradingPlayer(player.getId());
        updateTradePacket.setDisplayName(this.getDisplayName());
        updateTradePacket.setType(ContainerType.TRADE);

        final List<NbtMap> tierExpRequirements = new ObjectArrayList<>();
        for (int i = 0, len = tierExpRequirement.length; i < len; ++i) {
            tierExpRequirements.add(i, NbtMap.builder().putInt(String.valueOf(i), tierExpRequirement[i]).build());
        }
        final List<CompoundTag> recipes = this.getRecipes();
        final int reputation = this.getReputation(player);
        final List<NbtMap> updatedRecipes = new ObjectArrayList<>();
        for (CompoundTag recipe : recipes) {
            CompoundTag tag = recipe.copy();
            if (tag.contains("buyA")) {
                CompoundTag buyA = tag.getCompound("buyA").copy();
                float multiplier = 0;
                if (tag.contains("priceMultiplierA")) multiplier = tag.getFloat("priceMultiplierA");
                buyA.putByte("Count", Math.max(buyA.getByte("Count") - (int) (reputation * multiplier), 1));
                tag.putCompound("buyA", buyA);
            }
            if (tag.contains("buyB")) {
                CompoundTag buyB = tag.getCompound("buyB").copy();
                float multiplier = 0;
                if (tag.contains("priceMultiplierB")) multiplier = tag.getFloat("priceMultiplierB");
                buyB.putByte("Count", Math.max(buyB.getByte("Count") - (int) (reputation * multiplier), 1));
                tag.putCompound("buyB", buyB);
            }
            updatedRecipes.add(tag.toNetwork());
        }
        updateTradePacket.setOffers(NbtMap.builder()
                .putList("Recipes", NbtType.COMPOUND, updatedRecipes)
                .putList("TierExpRequirements", NbtType.COMPOUND, tierExpRequirements)
                .build());
        updateTradePacket.setUseNewTradeScreen(true);
        updateTradePacket.setUsingEconomyTrade(true);
        player.sendPacket(updateTradePacket);
    }

    /**
     * @return the maximum trade tier allowed for the villager
     */
    public int getMaxTradeTier() {
        return maxTradeTier;
    }

    /**
     * @param maxTradeTier the maximum trade tier to allow for the villager
     */
    public void setMaxTradeTier(int maxTradeTier) {
        this.maxTradeTier = maxTradeTier;
        this.setDataProperty(ActorDataTypes.MAX_TRADE_TIER, 5);
        this.nbt.putInt("maxTradeTier", this.tradeTier);
    }

    @Override
    public void close() {
        if (getLevel() != null && getMemoryStorage() != null) {
            BlockBed bed = getMemoryStorage().get(CoreMemoryTypes.OCCUPIED_BED);
            if (bed != null) getLevel().getPoiManager().release(bed.asBlockVector3());
            Block site = getMemoryStorage().get(CoreMemoryTypes.SITE_BLOCK);
            if (site != null) getLevel().getPoiManager().release(site.asBlockVector3());
        }
        this.getTradeNetIds().forEach(TradeRecipeBuildUtils.RECIPE_MAP::remove);
        super.close();
    }

    /**
     * @return the villager's current experience value
     */
    public int getTradeExp() {
        return tradeExp;
    }

    /**
     * @param tradeExp the villager's current experience value to set
     */
    public void setTradeExp(int tradeExp) {
        this.tradeExp = tradeExp;
        this.setDataProperty(ActorDataTypes.TRADE_EXPERIENCE, 10);
        this.nbt.putInt("tradeExp", this.tradeTier);
    }


    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (this.canTrade()) {
            player.addWindow(getTradeInventory());
            return true;
        } else return false;
    }

    public TradeInventory getTradeInventory() {
        return tradeInventory;
    }

    @Override
    public EntityEquipmentInventory getInventory() {
        return inventory;
    }

    public int getTradeSeed() {
        return tradeSeed;
    }

    protected void setTradeSeed(int tradeSeed) {
        this.tradeSeed = tradeSeed;
        this.nbt.putInt("tradeSeed", tradeSeed);
    }

    public void addExperience(int xp) {
        this.tradeExp += xp;
        this.setDataProperty(ActorDataTypes.TRADE_EXPERIENCE, this.tradeExp);
        int next = getTradeTier() + 1;
        if (next < this.tierExpRequirement.length && tradeExp >= this.tierExpRequirement[next]) {
            setTradeTier(next + 1);
        }
    }

    @Override
    public boolean onUpdate(int tick) {

        if (ticksLived % 24000 == 23999) {
            for (var v : getMemoryStorage().get(CoreMemoryTypes.GOSSIP).object2ObjectEntrySet()) {
                IntArrayList values = v.getValue();
                for (Gossip gossip : Gossip.VALUES) {
                    values.set(gossip.ordinal(), Math.max(0, values.getInt(gossip.ordinal()) - gossip.decay));
                }
            }
        }

        if (tick % 20 == 0) {
            for (Entity i : getLevel().getNearbyEntities(getBoundingBox().grow(1, 0.5, 1))) {
                if (i instanceof EntityItem entityItem) {
                    Item item = entityItem.getItem();
                    if (switch (item.getId()) {
                        case Item.BREAD,
                             Item.CARROT,
                             Item.POTATO,
                             BlockID.WHEAT,
                             Item.WHEAT_SEEDS,
                             Item.BEETROOT_SEEDS,
                             BlockID.BEETROOT,
                             Item.TORCHFLOWER_SEEDS,
                             Item.PITCHER_POD,
                             Item.BONE_MEAL -> true;
                        default -> false;
                    }) {
                        InventorySlice slice = new InventorySlice(getInventory(), 1, getInventory().getSize());
                        if (slice.canAddItem(item)) {
                            if (!slice.callPickupItemEvent(entityItem)) {
                                continue;
                            }
                            final TakeItemActorPacket pk = new TakeItemActorPacket();
                            pk.setActorRuntimeID(this.getId());
                            pk.setItemRuntimeID(i.getId());
                            Server.broadcastPacket(getViewers().values(), pk);
                            slice.addItem(item);
                            i.close();
                        }
                    }
                }
            }
        }
        return super.onUpdate(tick);
    }

    public void applyProfession() {
        if (this.profession == 0) {
            this.setCanTrade(false);
        } else {
            this.getTradeNetIds().forEach(TradeRecipeBuildUtils.RECIPE_MAP::remove);
            Profession profession = Profession.getProfession(this.profession);
            setDisplayName(profession.getName());
            for (CompoundTag trade : profession.buildTrades(getTradeSeed()).getAll()) {
                this.getTradeNetIds().add(trade.getInt("netId"));
            }
            this.setCanTrade(true);
            if (tradeInventory == null) {
                tradeInventory = new TradeInventory(this);
            }
        }
    }

    @Override
    public Integer getExperienceDrops() {
        return 0;
    }

    protected boolean transformZombie() {
        this.saveNBT();
        Entity zombie = new EntityZombieVillagerV2(this.getChunk(), this.getNbt().copy().remove("Health"));
        EntityTransformEvent event = new EntityTransformEvent(this, zombie);
        server.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            zombie.close();
            return false;
        } else {
            this.close();
            zombie.spawnToAll();
            this.level.addSound(this, Sound.MOB_VILLAGER_DEATH);
            return true;
        }
    }

    public enum Clothing {
        PLAINS,
        DESERT,
        JUNGLE,
        SAVANNA,
        SNOW,
        SWAMP,
        TAIGA;

        public static Clothing getClothing(int biomeId) {
            List<String> tags = Registries.BIOME.getTags(biomeId);
            if (tags.contains("desert") || tags.contains("mesa")) return DESERT;
            if (tags.contains("jungle")) return JUNGLE;
            if (tags.contains("savanna")) return SAVANNA;
            if (tags.contains("frozen")) return SNOW;
            if (tags.contains("swamp")) return SWAMP;
            if (tags.contains("taiga") || tags.contains("extreme_hills")) return TAIGA;
            return PLAINS;
        }
    }

    public enum Gossip {
        MAJOR_POSITIVE(20, 0, 100, 20, 5),
        MINOR_POSITIVE(25, 1, 5, 200, 1),
        MINOR_NEGATIVE(25, 20, 20, 200, -1),
        MAJOR_NEGATIVE(25, 10, 10, 100, -5),
        TRADING(2, 2, 20, 25, 1);

        public static final Gossip[] VALUES = values();

        final int gain;
        final int decay;
        final int penalty;
        final int max;
        final int multiplier;

        Gossip(int gain, int decay, int penalty, int max, int multiplier) {
            this.gain = gain;
            this.decay = decay;
            this.penalty = penalty;
            this.max = max;
            this.multiplier = multiplier;
        }
    }
}
