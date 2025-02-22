package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.FluctuateController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.data.profession.Profession;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.TradeInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.TradeRecipeBuildUtils;
import cn.nukkit.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
        return new ListTag<>(TradeRecipeBuildUtils.RECIPE_MAP.entrySet().stream().filter(t -> getTradeNetIds().contains(t.getKey())).toList().stream().map(Map.Entry::getValue).toList());
    }

    public int[] tierExpRequirement;

    protected TradeInventory inventory;

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
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10), (entity -> true), 1, 1)
                ),
                Set.of(),
                Set.of(new WalkController(), new LookController(true, true), new FluctuateController()),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.3f;
        }
        return 0.6f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.95f;
        }
        return 1.9f;
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
        if (!this.namedTag.contains("profession")) {
            this.setProfession(0);
        } else {
            var profession = this.namedTag.getInt("profession");
            this.profession = profession;
            this.setDataProperty(VARIANT, profession);
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
        Profession profession = Profession.getProfession(this.profession);
        if (profession != null) applyProfession(profession);
        if (canTrade) {
            inventory = new TradeInventory(this);
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putByte("profession", this.getProfession());
        this.namedTag.putBoolean("isTrade", this.getCanTrade());
        this.namedTag.putString("displayName", this.getDisplayName());
        this.namedTag.putInt("tradeTier", this.getTradeTier());
        this.namedTag.putInt("maxTradeTier", this.getMaxTradeTier());
        this.namedTag.putInt("tradeExp", this.getTradeExp());
        this.namedTag.putInt("tradeSeed", this.getTradeSeed());
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
    public void setProfession(int profession) {
        this.profession = profession;
        this.setDataProperty(VARIANT, profession);
        this.namedTag.putInt("profession", this.profession);
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
    public boolean getCanTrade() {
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
        if (this.getCanTrade()) {
            player.addWindow(inventory);
            return true;
        } else return false;
    }

    @Override
    public TradeInventory getInventory() {
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
        if (tick % 100 == 0) {
            if (tradeExp == 0 && !this.namedTag.contains("traded")) {
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
                                    this.setProfession(profession.getIndex());
                                    this.applyProfession(profession);

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
                        setProfession(0);
                        setCanTrade(false);
                    }
                }
            }
        }
        return super.onUpdate(tick);
    }

    public void applyProfession(Profession profession) {
        setDisplayName(profession.getName());
        for(CompoundTag trade : profession.buildTrades(getTradeSeed()).getAll()) {
            this.getTradeNetIds().add(trade.getInt("netId"));
        }
        this.setCanTrade(true);
        if (inventory == null) {
            inventory = new TradeInventory(this);
        }
    }

    @Override
    public Integer getExperienceDrops() {
        return 0;
    }

}
