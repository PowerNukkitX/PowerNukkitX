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
import cn.nukkit.entity.ai.executor.AnimalGrowExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.FleeFromTargetExecutor;
import cn.nukkit.entity.ai.executor.InLoveExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.villager.DoorExecutor;
import cn.nukkit.entity.ai.executor.villager.GossipExecutor;
import cn.nukkit.entity.ai.executor.villager.SleepExecutor;
import cn.nukkit.entity.ai.executor.villager.VillagerBreedingExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.BlockSensor;
import cn.nukkit.entity.ai.sensor.NearestEntitySensor;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.data.profession.Profession;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.mob.EntityZombie;
import cn.nukkit.inventory.EntityEquipmentInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventorySlice;
import cn.nukkit.inventory.TradeInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFood;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.TakeItemEntityPacket;
import cn.nukkit.utils.TradeRecipeBuildUtils;
import cn.nukkit.utils.random.NukkitRandom;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
                        ), 10, 1),
                        new Behavior(
                                new VillagerInLoveExecutor(400),
                                all(
                                        entity -> getFoodPoints() >= 12,
                                        entity -> !isBaby(),
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_IN_LOVE_TIME, 6000, Integer.MAX_VALUE)
                                ),
                                1, 1, 1, false
                        ),
                        //生长
                        new Behavior(
                                new AnimalGrowExecutor(),
                                all(
                                        new PassByTimeEvaluator(CoreMemoryTypes.ENTITY_SPAWN_TIME, 20 * 60 * 20, Integer.MAX_VALUE),
                                        entity -> entity instanceof EntityAnimal animal && animal.isBaby()
                                )
                                , 1, 1, 1200
                        )
                ),
                Set.of(
                        new Behavior(new VillagerBreedingExecutor(EntityVillagerV2.class, 16, 100, 0.5f), entity -> entity.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE), 7, 1),
                        new Behavior(new SleepExecutor(), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.OCCUPIED_BED),
                                new DistanceEvaluator(CoreMemoryTypes.OCCUPIED_BED, 2),
                                entity -> getLevel().isNight()
                        ), 8, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.OCCUPIED_BED, 0.3f, true), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.OCCUPIED_BED),
                                entity -> getLevel().isNight()
                        ), 7, 1),
                        new Behavior(new FleeFromTargetExecutor(CoreMemoryTypes.NEAREST_ZOMBIE, 0.5f, true, 8), all(
                                new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_ZOMBIE)
                        ), 5, 1),
                        new Behavior(new GossipExecutor(CoreMemoryTypes.NEAREST_HUNGRY_VILLAGER), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_HUNGRY_VILLAGER),
                                new DistanceEvaluator(CoreMemoryTypes.NEAREST_HUNGRY_VILLAGER, 2.1f)
                        ), 4, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_HUNGRY_VILLAGER, 0.3f, true, 32, 2), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_HUNGRY_VILLAGER), 4, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_HUNGRY_VILLAGER, 0.3f, true, 32, 2), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_HUNGRY_VILLAGER), 4, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10), (entity -> true), 1, 1)
                ),
                Set.of(
                        entity -> {
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
                        },
                        entity -> {
                            if(shouldShareFood()) {
                                Arrays.stream(entity.getLevel().getCollidingEntities(entity.getBoundingBox().grow(16, 3, 16))).filter(entity1 -> entity1 instanceof EntityVillagerV2 villagerV2 && villagerV2.isHungry()).findAny().ifPresentOrElse(entity1 -> getMemoryStorage().put(CoreMemoryTypes.NEAREST_HUNGRY_VILLAGER, (EntityVillagerV2) entity1), () -> getMemoryStorage().clear(CoreMemoryTypes.NEAREST_HUNGRY_VILLAGER));
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
        }
    }

    public BlockBed getBed() {
        return getMemoryStorage().get(CoreMemoryTypes.OCCUPIED_BED);
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
    public void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
        setTradingPlayer(0L);
        if (this.namedTag.containsInt("profession")) {
            setProfession(this.namedTag.getInt("profession"), false);
        }
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
        if(this.namedTag.containsCompound("bed")) {
            CompoundTag compound = this.namedTag.getCompound("bed");
            Vector3 vector = new Vector3(compound.getInt("x"), compound.getInt("y"), compound.getInt("z"));
            if(getLevel().getBlock(vector) instanceof BlockBed bed) {
                setBed(bed);
            }
        }
        if(this.namedTag.containsList("gossip")) {
            Object2IntArrayMap<String> gossip = getMemoryStorage().get(CoreMemoryTypes.GOSSIP);
            for(CompoundTag tag : this.namedTag.getList("gossip", CompoundTag.class).getAll()) {
                gossip.put(tag.getString("k"), tag.getInt("v"));
            }
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
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putInt("profession", this.getProfession());
        this.namedTag.putBoolean("isTrade", this.canTrade());
        this.namedTag.putString("displayName", this.getDisplayName());
        this.namedTag.putInt("tradeTier", this.getTradeTier());
        this.namedTag.putInt("maxTradeTier", this.getMaxTradeTier());
        this.namedTag.putInt("tradeExp", this.getTradeExp());
        this.namedTag.putInt("tradeSeed", this.getTradeSeed());
        ListTag<CompoundTag> gossipTag = new ListTag<>();
        for(var v : getMemoryStorage().get(CoreMemoryTypes.GOSSIP).object2IntEntrySet()) {
            CompoundTag gossip = new CompoundTag();
            gossip.putString("k" ,v.getKey());
            gossip.putInt("v", v.getIntValue());
            gossipTag.add(gossip);
        }
        this.namedTag.putList("gossip", gossipTag);
        if(getMemoryStorage().notEmpty(CoreMemoryTypes.OCCUPIED_BED)) {
            BlockBed bed = getMemoryStorage().get(CoreMemoryTypes.OCCUPIED_BED);
            this.namedTag.putCompound("bed", new CompoundTag().putInt("x", bed.getFloorX()).putInt("y", bed.getFloorY()).putInt("z", bed.getFloorZ()));
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
            default -> null;
        };
    }

    /**
     * @return 村民的职业id
     */
    public int getProfession() {
        return profession;
    }

    /**
     * 设置村民职业
     *
     * @param profession 请查看{@link EntityVillagerV2#profession}
     */
    public void setProfession(int profession, boolean apply) {
        this.profession = profession;
        this.setDataProperty(VARIANT, profession);
        if(apply) applyProfession();
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
        if (tick % 100 == 0) {
            if (tradeExp == 0 && !this.namedTag.contains("traded") && getProfession() == 0) {
                boolean professionFound = false;
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        Block block = getLocation().add(x, 0, z).getLevelBlock();
                        String id = block.getId();
                        for (Profession profession : Profession.getProfessions().values()) {
                            if (id.equals(profession.getBlockID())) {
                                professionFound = true;
                                if (this.profession != profession.getIndex()) {
                                    this.setTradeSeed(new NukkitRandom().nextInt(Integer.MAX_VALUE - 1));
                                    this.setProfession(profession.getIndex(), true);

                                    this.namedTag.putInt("blockX", block.getFloorX());
                                    this.namedTag.putInt("blockY", block.getFloorY());
                                    this.namedTag.putInt("blockZ", block.getFloorZ());
                                }
                                break;
                            }
                        }
                    }
                }
                if (this.profession != 0 && !this.namedTag.contains("traded")) {
                    int x = this.namedTag.getInt("blockX");
                    int y = this.namedTag.getInt("blockY");
                    int z = this.namedTag.getInt("blockZ");
                    if (!Objects.equals(level.getBlockIdAt(x, y, z), Profession.getProfession(this.profession).getBlockID())) {
                        setProfession(0, true);
                        setCanTrade(false);
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

    private class VillagerInLoveExecutor extends InLoveExecutor {

        public VillagerInLoveExecutor(int duration) {
            super(duration);
        }

        @Override
        public boolean execute(EntityIntelligent entity) {
            if(super.execute(entity)) {
                int food = getFoodPoints();
                for(int j = 0; j < getInventory().getSize(); j++) {
                    Item item = getInventory().getUnclonedItem(j);
                    if(item instanceof ItemFood) {
                        for(int i = 0; i < item.count; i++) {
                            item.decrement(1);
                            if(food-getFoodPoints() <= 12) return true;
                        }
                    }
                }
                return true;
            }
            return false;
        }
    }
}
