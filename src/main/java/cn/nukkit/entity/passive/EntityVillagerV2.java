package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBed;
import cn.nukkit.block.BlockDoor;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.FluctuateController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.DistanceEvaluator;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.AnimalGrowExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.FleeFromTargetExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.NearbyFlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.executor.villager.DoorExecutor;
import cn.nukkit.entity.ai.executor.villager.GossipExecutor;
import cn.nukkit.entity.ai.executor.villager.SleepExecutor;
import cn.nukkit.entity.ai.executor.villager.VillagerBreedingExecutor;
import cn.nukkit.entity.ai.executor.villager.WillingnessExecutor;
import cn.nukkit.entity.ai.executor.villager.WorkExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.BlockSensor;
import cn.nukkit.entity.ai.sensor.NearestEntitySensor;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.data.profession.Profession;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.mob.EntityZombie;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.EntityEquipmentInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventorySlice;
import cn.nukkit.inventory.TradeInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.TakeItemEntityPacket;
import cn.nukkit.network.protocol.UpdateTradePacket;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.BiomeRegistry;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.TradeRecipeBuildUtils;
import cn.nukkit.utils.Utils;
import cn.nukkit.utils.random.NukkitRandom;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class EntityVillagerV2 extends EntityIntelligent implements InventoryHolder, IEntityNPC {
    @Override
    @NotNull
    public String getIdentifier() {
        return VILLAGER_V2;
    }

    private final List<Integer> tradeNetId = new ArrayList<>();

    public List<Integer> getTradeNetIds() {
        if(tradeNetId == null) return new ArrayList<>();
        return tradeNetId;
    }

    public ListTag<CompoundTag> getRecipes() {
        return new ListTag<>(Tag.TAG_Compound, TradeRecipeBuildUtils.RECIPE_MAP.entrySet().stream().filter(t -> getTradeNetIds().contains(t.getKey())).toList().stream().map(Map.Entry::getValue).toList());
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

    {
        this.tierExpRequirement = new int[]{0, 10, 70, 150, 250};
    }

    public EntityVillagerV2(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
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
                                    if(block == null || getMoveDirectionEnd() == null) return false;
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
                        //生长
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
                        new Behavior(entity -> {setMoveTarget(null); setLookTarget(getTradeInventory().getViewers().stream().findFirst().get()); return true;}, entity -> getTradeInventory() != null && !getTradeInventory().getViewers().isEmpty(), 9, 1),
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
                        new Behavior(new NearbyFlatRandomRoamExecutor(CoreMemoryTypes.OCCUPIED_BED ,0.2f, 5, 100, false, -1, true, 10), all(
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
                        new Behavior(new VillagerBreedingExecutor(EntityVillagerV2.class, 16, 100, 0.5f), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.ENTITY_SPOUSE), 2, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10), (entity -> true), 1, 1)
                ),
                Set.of(
                        entity -> {
                            if(getLevel().getTick()%120==0) {
                                if(getMemoryStorage().isEmpty(CoreMemoryTypes.OCCUPIED_BED)) {
                                    int range = 48;
                                    int lookY = 5;
                                    BlockBed block = null;
                                    for (int x = -range; x <= range; x++) {
                                        for (int z = -range; z <= range; z++) {
                                            for (int y = -lookY; y <= lookY; y++) {
                                                Location lookLocation = entity.add(x, y, z);
                                                Block lookBlock = lookLocation.getLevelBlock();
                                                if (lookBlock instanceof BlockBed bed) {
                                                    if (!bed.isHeadPiece() && Arrays.stream(getLevel().getEntities()).noneMatch(entity1 -> entity1 instanceof EntityVillagerV2 v && v.getMemoryStorage().notEmpty(CoreMemoryTypes.OCCUPIED_BED) && v.getBed().equals(bed))) {
                                                        block = bed.getFootPart();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if(block != null && !block.isOccupied()) setBed(block);
                                } else if(!getMemoryStorage().get(CoreMemoryTypes.OCCUPIED_BED).isBedValid()) {
                                    this.namedTag.remove("bed");
                                    getMemoryStorage().clear(CoreMemoryTypes.OCCUPIED_BED);
                                }
                            }
                        },
                        entity -> {
                            if(getLevel().getTick() % 60 == 0) {
                                Stream<EntityVillagerV2> entities = Arrays.stream(entity.getLevel().getCollidingEntities(entity.getBoundingBox().grow(64, 3, 64))).filter(entity1 -> entity1 instanceof EntityVillagerV2 && entity1 != this).map(entity1 -> ((EntityVillagerV2) entity1));
                                if(getLevel().getDayTime() > 8000 && getLevel().getDayTime() < 10000) {
                                    if(getMemoryStorage().isEmpty(CoreMemoryTypes.GOSSIP_TARGET)) {
                                        double minDistance = Float.MAX_VALUE;
                                        EntityVillagerV2 nearest = null;
                                        for(EntityVillagerV2 entity1 : entities.toList()) {
                                            double distance = entity1.distance(this);
                                            if(distance < minDistance) {
                                                minDistance = distance;
                                                nearest = entity1;
                                            }
                                        }
                                        if(nearest != null) {
                                            getMemoryStorage().put(CoreMemoryTypes.GOSSIP_TARGET, nearest);
                                        } else getMemoryStorage().clear(CoreMemoryTypes.GOSSIP_TARGET);
                                    }
                                } else {
                                    if(shouldShareFood()) {
                                        entities.filter(entity1 -> entity1.isHungry() && entity1.distance(this) < 16).findAny().ifPresentOrElse(entity1 ->  {
                                            getMemoryStorage().put(CoreMemoryTypes.GOSSIP_TARGET, entity1);
                                            entity1.getMemoryStorage().put(CoreMemoryTypes.GOSSIP_TARGET, this);
                                        }, () -> getMemoryStorage().clear(CoreMemoryTypes.GOSSIP_TARGET));
                                    } else if(!isHungry()) getMemoryStorage().clear(CoreMemoryTypes.GOSSIP_TARGET);
                                }
                            }
                        },
                        entity -> {
                            if(getLevel().getTick() % 30 == 0) {
                                if(!isBaby()) {
                                    Block siteBlock = getMemoryStorage().get(CoreMemoryTypes.SITE_BLOCK);
                                    if(siteBlock != null) if(!siteBlock.getLevelBlock().getId().equals(siteBlock.getId())) {
                                        getMemoryStorage().clear(CoreMemoryTypes.SITE_BLOCK);
                                    }
                                    if(getMemoryStorage().isEmpty(CoreMemoryTypes.SITE_BLOCK)) {
                                        for(Block block : getLevel().getCollisionBlocks(this.getBoundingBox().grow(16, 4, 16))) {
                                            if(Arrays.stream(getLevel().getEntities()).noneMatch(entity1 -> entity1 instanceof EntityVillagerV2 v && v.getMemoryStorage().notEmpty(CoreMemoryTypes.SITE_BLOCK) && v.getSite().equals(block)))
                                                if(setProfessionBlock(block)) return;
                                        }
                                        if(getTradeExp() == 0) setProfession(0, true);
                                    }
                                }
                            }
                        },
                        entity -> {
                            if(getLevel().getTick() % 100 == 0) {
                                if(getMemoryStorage().get(CoreMemoryTypes.WILLING)) {
                                    var entities = entity.level.getEntities();
                                    var maxDistanceSquared = -1d;
                                    EntityVillagerV2 nearestInLove = null;
                                    for (Entity e : entities) {
                                        double newDistance = e.distanceSquared(entity);
                                        if (e instanceof EntityVillagerV2 another && e != entity) {
                                            if (!another.isBaby() && another.getMemoryStorage().get(CoreMemoryTypes.WILLING) && another.getMemoryStorage().isEmpty(CoreMemoryTypes.ENTITY_SPOUSE) && (maxDistanceSquared == -1 || newDistance < maxDistanceSquared)) {
                                                maxDistanceSquared = newDistance;
                                                nearestInLove = another;
                                            }
                                        }
                                    }
                                    if(nearestInLove != null) {
                                        nearestInLove.getMemoryStorage().put(CoreMemoryTypes.ENTITY_SPOUSE, this);
                                        getMemoryStorage().put(CoreMemoryTypes.ENTITY_SPOUSE, nearestInLove);
                                    }
                                }
                            }
                        },
                        new BlockSensor(BlockDoor.class, CoreMemoryTypes.NEAREST_BLOCK_2, 1, 0, 10),
                        new NearestEntitySensor(EntityZombie.class, CoreMemoryTypes.NEAREST_ZOMBIE, 8, 0)
                ),
                Set.of(new WalkController(), new LookController(true, true), new FluctuateController()),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    public float getFloatingHeight() {
        return super.getFloatingHeight()*0.7f;
    }

    public boolean isHungry() {
        return getFoodPoints() < 12;
    }

    public boolean shouldShareFood() {
        for(Item item : getInventory().getContents().values()) {
            if((item.getId().equals(Item.BREAD) && item.getCount() >= 6)
            || ((item.getId().equals(Item.CARROT) || item.getId().equals(Block.BEETROOT)) && item.getCount() >= 24)
            || (item.getId().equals(Block.WHEAT) && item.getCount() >= 18 && getProfession() == 1)) return true;
        }
        return false;
    }

    public int getFoodPoints() {
        int points = 0;
        for(Item item : getInventory().getContents().values()) {
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
        if(bed.isBedValid()) {
            getMemoryStorage().put(CoreMemoryTypes.OCCUPIED_BED, bed);
            for(int i = 0; i < 5; i++) {
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
    public float getHeight() {
        if(isSleeping()) return getWidthR();
        return getHeightR();
    }

    @Override
    public float getWidth() {
        return getWidthR();
    }

    @Override
    public float getLength() {
        if(isSleeping()) return getHeightR();
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

    public boolean isSleeping() {
        return getDataFlag(EntityFlag.SLEEPING);
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
        this.setMaxHealth(20);
        super.initEntity();
        setTradingPlayer(0L);
        if (!this.namedTag.contains("tradeSeed")) {
            this.setTradeSeed(new NukkitRandom().nextInt(Integer.MAX_VALUE - 1));
        } else {
            this.tradeSeed = this.namedTag.getInt("tradeSeed");
        }
        if (!this.namedTag.contains("canTrade")) {
            this.setCanTrade(!(profession == 0 || profession == 14));
        } else {
            this.canTrade = this.namedTag.getBoolean("canTrade");
        }
        if (!this.namedTag.contains("displayName") && profession != 0) {
            this.setDisplayName(getProfessionName(profession));
        } else {
            this.displayName = this.namedTag.getString("displayName");
        }
        if (!this.namedTag.contains("tradeTier")) {
            this.setTradeTier(1);
        } else {
            this.tradeTier = this.namedTag.getInt("tradeTier");
        }
        if (!this.namedTag.contains("maxTradeTier")) {
            this.setMaxTradeTier(5);
        } else {
            var maxTradeTier = this.namedTag.getInt("maxTradeTier");
            this.maxTradeTier = maxTradeTier;
            this.setDataProperty(MAX_TRADE_TIER, maxTradeTier);
        }
        if (!this.namedTag.contains("tradeExp")) {
            this.setTradeExp(0);
        } else {
            var tradeExp = this.namedTag.getInt("tradeExp");
            this.tradeExp = tradeExp;
            this.setDataProperty(TRADE_EXPERIENCE, tradeExp);
        }
        if(this.namedTag.containsInt("clothing")) {
            this.setDataProperty(EntityDataTypes.MARK_VARIANT, this.namedTag.getInt("clothing"));
        } else {
            BlockVector3 bv = asBlockVector3();
            this.setDataProperty(EntityDataTypes.MARK_VARIANT, Clothing.getClothing(getLevel().getBiomeId(bv.x, bv.y, bv.z)).ordinal());
        }
        if(this.namedTag.containsCompound("bed")) {
            CompoundTag compound = this.namedTag.getCompound("bed");
            Vector3 vector = new Vector3(compound.getInt("x"), compound.getInt("y"), compound.getInt("z"));
            if(getLevel().getBlock(vector) instanceof BlockBed bed) {
                setBed(bed);
            }
        }
        getMemoryStorage().put(CoreMemoryTypes.GOSSIP, new Object2ObjectArrayMap<>());
        if(this.namedTag.containsCompound("gossip")) {
            var gossipMap = getMemoryStorage().get(CoreMemoryTypes.GOSSIP);
            CompoundTag gossipTag = this.namedTag.getCompound("gossip");
            for(String key : gossipTag.getTags().keySet()){
                ListTag<IntTag> gossipValues = gossipTag.getList(key, IntTag.class);
                IntArrayList valueMap = new IntArrayList();
                for(int i = 0; i < gossipValues.size(); i++) {
                    valueMap.add(i, gossipValues.get(i).getData());
                }
                gossipMap.put(key, valueMap);
            }
        }
        if(this.namedTag.containsString("purifyPlayer")) {
            String xuid = this.namedTag.removeAndGet("purifyPlayer").parseValue();
            this.addGossip(xuid, Gossip.MAJOR_POSITIVE, 20);
            this.addGossip(xuid, Gossip.MINOR_POSITIVE, 25);
        }
        if(this.namedTag.containsCompound("siteBlock")) {
            CompoundTag tag = this.namedTag.getCompound("siteBlock");
            Vector3 vector3 = new Vector3(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
            Block block = getLevel().getBlock(vector3);
            setProfessionBlock(block);
        }
        if (this.namedTag.containsInt("profession")) {
            setProfession(this.namedTag.getInt("profession"), false);
        }
        this.inventory = new EntityEquipmentInventory(this);
        if (this.namedTag.contains("Inventory") && this.namedTag.get("Inventory") instanceof ListTag) {
            var inventory = this.getInventory();
            ListTag<CompoundTag> inventoryList = this.namedTag.getList("Inventory", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {
                int slot = item.getByte("Slot");
                inventory.setItem(slot, NBTIO.getItemHelper(item));//inventory 0-39
            }
        }
        if (canTrade) {
            tradeInventory = new TradeInventory(this);
        }
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if(super.attack(source)) {
            if(source instanceof EntityDamageByEntityEvent event) {
                if(event.getDamager() instanceof Player player) {
                    addGossip(player.getLoginChainData().getXUID(), Gossip.MINOR_NEGATIVE, 25);
                    EntityEventPacket pk = new EntityEventPacket();
                    pk.eid = getId();
                    pk.event = EntityEventPacket.VILLAGER_ANGRY;
                    Server.broadcastPacket(getViewers().values(), pk);
                }
            }
            return true;
        } else return false;
    }

    @Override
    public void kill() {
        if(getLastDamageCause() instanceof EntityDamageByEntityEvent event) {
            if(event.getEntity() instanceof Player player) {
                Arrays.stream(this.getLevel().getCollidingEntities(this.getBoundingBox().grow(16, 16, 16))).filter(entity -> entity instanceof EntityVillagerV2).forEach(entity -> ((EntityVillagerV2) entity).addGossip(player.getLoginChainData().getXUID(), Gossip.MAJOR_NEGATIVE, 25));
            }
        }
        super.kill();
    }

    public void addGossip(String xuid, Gossip gossip, int value) {
        var gossipMap = getMemoryStorage().get(CoreMemoryTypes.GOSSIP);
        if(!gossipMap.containsKey(xuid)) gossipMap.put(xuid, new IntArrayList(Collections.nCopies(Gossip.VALUES.length, 0)));
        IntArrayList values = gossipMap.get(xuid);
        int ordinal = gossip.ordinal();
        values.set(ordinal, Math.min(gossip.max, values.getInt(ordinal) + value));
        getLevel().getPlayers().values().stream().filter(player -> player.getLoginChainData().getXUID().equals(xuid)).findFirst().ifPresent(this::updateTrades);
    }

    public void spreadGossip() {
        Arrays.stream(getLevel().getCollidingEntities(getBoundingBox().grow(2, 0, 2))).filter(entity2 -> entity2 instanceof EntityVillagerV2).map(entity2 -> ((EntityVillagerV2) entity2)).forEach(target -> {
            var gossipMap = getMemoryStorage().get(CoreMemoryTypes.GOSSIP);
            var targetGossipMap = target.getMemoryStorage().get(CoreMemoryTypes.GOSSIP);
            for(var entry : gossipMap.object2ObjectEntrySet()) {
                String xuid = entry.getKey();
                if(!targetGossipMap.containsKey(xuid)) targetGossipMap.put(xuid, new IntArrayList(Collections.nCopies(Gossip.VALUES.length, 0)));
                IntArrayList targetValues = targetGossipMap.get(xuid);
                for(Gossip gossip : Gossip.VALUES) {
                    int ordinal = gossip.ordinal();
                    targetValues.set(ordinal, Math.max(targetValues.getInt(ordinal), entry.getValue().getInt(ordinal) - gossip.penalty));
                }
            }
        });
    }

    public int getGossip(String xuid, Gossip gossip) {
        var gossipMap = getMemoryStorage().get(CoreMemoryTypes.GOSSIP);
        if(!gossipMap.containsKey(xuid)) gossipMap.put(xuid, new IntArrayList(Collections.nCopies(Gossip.VALUES.length, 0)));
        IntArrayList values = gossipMap.get(xuid);
        int ordinal = gossip.ordinal();
        return values.getInt(ordinal) * gossip.multiplier;
    }

    public int getReputation(Player player) {
        int reputation = 0;
        IntArrayList values = getMemoryStorage().get(CoreMemoryTypes.GOSSIP).get(player.getLoginChainData().getXUID());
        if(values != null) {
            for(Gossip gossip : Gossip.VALUES) {
                reputation += (values.getInt(gossip.ordinal()) * gossip.multiplier);
            }
        }
        return reputation;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putInt("profession", this.getProfession());
        this.namedTag.putBoolean("isTrade", this.canTrade());
        this.namedTag.putString("displayName", this.getDisplayName());
        this.namedTag.putInt("tradeTier", this.getTradeTier());
        this.namedTag.putInt("maxTradeTier", this.getMaxTradeTier());
        this.namedTag.putInt("tradeExp", this.getTradeExp());
        this.namedTag.putInt("tradeSeed", this.getTradeSeed());
        this.namedTag.putInt("clothing", this.getDataProperty(EntityDataTypes.MARK_VARIANT));
        CompoundTag gossipTag = new CompoundTag();
        for(var v : getMemoryStorage().get(CoreMemoryTypes.GOSSIP).object2ObjectEntrySet()) {
            ListTag<IntTag> gossipValues = new ListTag<>();
            for(int v2 : v.getValue()) {
                gossipValues.add(new IntTag(v2));
            }
            gossipTag.putList(v.getKey(), gossipValues);
        }
        this.namedTag.putCompound("gossip", gossipTag);
        if(getMemoryStorage().notEmpty(CoreMemoryTypes.OCCUPIED_BED)) {
            BlockBed bed = getMemoryStorage().get(CoreMemoryTypes.OCCUPIED_BED);
            this.namedTag.putCompound("bed", new CompoundTag().putInt("x", bed.getFloorX()).putInt("y", bed.getFloorY()).putInt("z", bed.getFloorZ()));
        }
        if(getMemoryStorage().notEmpty(CoreMemoryTypes.SITE_BLOCK)) {
            Block site = getMemoryStorage().get(CoreMemoryTypes.SITE_BLOCK);
            this.namedTag.putCompound("siteBlock", new CompoundTag().putInt("x", site.getFloorX()).putInt("y", site.getFloorY()).putInt("z", site.getFloorZ()));
        }
        ListTag<CompoundTag> inventoryTag = null;
        if (this.getInventory() != null) {
            inventoryTag = new ListTag<>();
            this.namedTag.putList("Inventory", inventoryTag);
            for (var entry : getInventory().getContents().entrySet()) {
                inventoryTag.add(NBTIO.putItemHelper(entry.getValue(), entry.getKey()));
            }
        }
    }

    /**
     * 获取村民职业id对应的displayName硬编码
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
     * @return 村民的职业id
     */
    public int getProfession() {
        return profession;
    }



    public void setProfession(int profession, boolean apply) {
        this.profession = profession;
        this.setDataProperty(VARIANT, profession);
        if(apply) applyProfession();
    }

    public boolean setProfessionBlock(Block block) {
        for (Profession profession : Profession.getProfessions().values()) {
            if(getTradeExp() != 0 && profession.getIndex() != getProfession()) continue;
            if (block.getId().equals(profession.getBlockID())) {
                getMemoryStorage().put(CoreMemoryTypes.SITE_BLOCK, block);
                setProfession(profession.getIndex(), true);
                return true;
            }
        }
        return false;
    }

    /**
     * 这个方法插件一般不用
     */
    public void setTradingPlayer(Long eid) {
        this.setDataProperty(TRADE_TARGET_EID, eid);
    }

    /**
     * @return 该村民是否可以交易
     */
    public boolean canTrade() {
        return canTrade;
    }

    /**
     * 设置村民是否可以交易
     *
     * @param canTrade true 可以交易
     */
    public void setCanTrade(boolean canTrade) {
        this.canTrade = canTrade;
        this.namedTag.putBoolean("canTrade", canTrade);
    }

    /**
     * @return 交易UI的显示名称
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName 设置交易UI的显示名称
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.namedTag.putString("displayName", displayName);
    }

    /**
     * @return 该村民的交易等级
     */
    public int getTradeTier() {
        return tradeTier;
    }

    /**
     * @param tradeTier <p>村民的交易等级(1-{@link EntityVillagerV2#maxTradeTier})</p>
     */
    public void setTradeTier(int tradeTier) {
        this.tradeTier = --tradeTier;
        this.namedTag.putInt("tradeTier", this.tradeTier);
    }

    public void updateTrades(Player player) {
        if(player.getTopWindow().isEmpty() || player.getTopWindow().get() != getTradeInventory()) return;
        var pk1 = new UpdateTradePacket();
        pk1.containerId = (byte) player.getWindowId(getTradeInventory());
        pk1.tradeTier = getTradeTier();
        pk1.traderUniqueEntityId = getId();
        pk1.playerUniqueEntityId = player.getId();
        pk1.displayName = getDisplayName();
        var tierExpRequirements = new ListTag<CompoundTag>();
        for (int i = 0, len = tierExpRequirement.length; i < len; ++i) {
            tierExpRequirements.add(i, new CompoundTag().putInt(String.valueOf(i), tierExpRequirement[i]));
        }
        ListTag<CompoundTag> recipes = (ListTag<CompoundTag>) getRecipes().copy();
        int reputation = getReputation(player);
        for(CompoundTag tag : recipes.getAll()) {
            if(tag.containsCompound("buyA")) {
                CompoundTag buyA = tag.getCompound("buyA");
                float multiplier = 0;
                if(tag.containsFloat("priceMultiplierA")) multiplier = tag.getFloat("priceMultiplierA");
                buyA.putByte("Count", Math.max(buyA.getByte("Count") - (int) (reputation * multiplier), 1));
            }
            if(tag.containsCompound("buyB")) {
                CompoundTag buyB = tag.getCompound("buyB");
                float multiplier = 0;
                if(tag.containsFloat("priceMultiplierB")) multiplier = tag.getFloat("priceMultiplierB");
                buyB.putByte("Count", Math.max(buyB.getByte("Count") - (int) (reputation * multiplier), 1));
            }
        }
        pk1.offers = new CompoundTag()
                .putList("Recipes", recipes)
                .putList("TierExpRequirements", tierExpRequirements);
        pk1.newTradingUi = true;
        pk1.usingEconomyTrade = true;
        player.dataPacket(pk1);
    }

    /**
     * @return 村民所允许的最大交易等级
     */
    public int getMaxTradeTier() {
        return maxTradeTier;
    }

    /**
     * @param maxTradeTier 设置村民所允许的最大交易等级
     */
    public void setMaxTradeTier(int maxTradeTier) {
        this.maxTradeTier = maxTradeTier;
        this.setDataProperty(MAX_TRADE_TIER, 5);
        this.namedTag.putInt("maxTradeTier", this.tradeTier);
    }

    @Override
    public void close() {
        this.getTradeNetIds().forEach(TradeRecipeBuildUtils.RECIPE_MAP::remove);
        super.close();
    }

    /**
     * @return 村民当前的经验值
     */
    public int getTradeExp() {
        return tradeExp;
    }

    /**
     * @param tradeExp 设置村民当前的经验值
     */
    public void setTradeExp(int tradeExp) {
        this.tradeExp = tradeExp;
        this.setDataProperty(TRADE_EXPERIENCE, 10);
        this.namedTag.putInt("tradeExp", this.tradeTier);
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
        this.namedTag.putInt("tradeSeed", tradeSeed);
    }

    public void addExperience(int xp) {
        this.tradeExp += xp;
        this.setDataProperty(TRADE_EXPERIENCE, this.tradeExp);
        int next = getTradeTier() + 1;
        if (next < this.tierExpRequirement.length) {
            if (tradeExp >= this.tierExpRequirement[next]) {
                setTradeTier(next + 1);
            }
        }
    }

    @Override
    public boolean onUpdate(int tick) {

        if(ticksLived % 24000 == 23999) {
            for(var v : getMemoryStorage().get(CoreMemoryTypes.GOSSIP).object2ObjectEntrySet()) {
                IntArrayList values = v.getValue();
                for(Gossip gossip : Gossip.VALUES) {
                    values.set(gossip.ordinal(), Math.max(0, values.getInt(gossip.ordinal()) - gossip.decay));
                }
            }
        }

        if(tick % 20 == 0) {
            for(Entity i : getLevel().getNearbyEntities(getBoundingBox().grow(1, 0.5, 1))) {
                if(i instanceof EntityItem entityItem) {
                    Item item = entityItem.getItem();
                    if(switch (item.getId()) {
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
                        if(slice.canAddItem(item)) {
                            TakeItemEntityPacket pk = new TakeItemEntityPacket();
                            pk.entityId = this.getId();
                            pk.target = i.getId();
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
        if(this.profession == 0) {
            this.setCanTrade(false);
        } else {
            this.getTradeNetIds().forEach(TradeRecipeBuildUtils.RECIPE_MAP::remove);
            Profession profession = Profession.getProfession(this.profession);
            setDisplayName(profession.getName());
            for(CompoundTag trade : profession.buildTrades(getTradeSeed()).getAll()) {
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

    public enum Clothing {
        PLAINS,
        DESERT,
        JUNGLE,
        SAVANNA,
        SNOW,
        SWAMP,
        TAIGA;

        public static Clothing getClothing(int biomeId) {
            BiomeDefinition definition = Registries.BIOME.get(biomeId);
            Set<String> tags = definition.getTags();
            if(tags.contains("desert") || tags.contains("mesa")) return DESERT;
            if(tags.contains("jungle")) return JUNGLE;
            if(tags.contains("savanna")) return SAVANNA;
            if(tags.contains("frozen")) return SNOW;
            if(tags.contains("swamp")) return SWAMP;
            if(tags.contains("taiga") || tags.contains("extreme_hills")) return TAIGA;
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
