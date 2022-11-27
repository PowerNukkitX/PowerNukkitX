package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityAgeable;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.TradeInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.SNBTParser;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.RecipeBuildUtils;

import java.util.concurrent.ThreadLocalRandom;

public class EntityVillager extends EntityCreature implements InventoryHolder, EntityNPC, EntityAgeable {

    public static final int NETWORK_ID = 115;
    /**
     * 代表交易配方
     */
    public final ListTag<Tag> recipes = new ListTag<>("Recipes");
    /**
     * 用于控制村民的等级成长所需要的经验
     * 例如[0,10,20,30,40] 村民达到1级所需经验0,2级为10,这里的经验是{@link EntityVillager#tradeExp}.
     */
    public int[] tierExpRequirement;
    @PowerNukkitXOnly
    @Since("1.19.21-r1")
    protected TradeInventory inventory;
    /**
     * 用于控制该村民是否可以交易
     */
    protected Boolean canTrade;
    /**
     * 代表交易UI上方所显示的名称,在原版为村民的职业名
     */
    protected String displayName;
    /**
     * 代表村民当前的交易等级
     */
    protected int tradeTier;
    /**
     * 代表村民所允许的最大交易等级
     */
    protected int maxTradeTier;
    /**
     * 代表当前村民的经验,不允许为负数
     */
    protected int tradeExp;
    /**
     * 代表村民的职业<br>
     * 0 generic 普通<br>
     * 1 farmer 农民<br>
     * 2 fisherman 渔民<br>
     * 3 shepherd 牧羊人<br>
     * 4 fletcher 制箭师<br>
     * 5 librarian 图书管理员<br>
     * 6 cartographer 制图师<br>
     * 7 cleric 牧师<br>
     * 8 armor 盔甲匠<br>
     * 9 weapon 武器匠<br>
     * 10 tool 工具匠<br>
     * 11 butcher 屠夫<br>
     * 12 butcher 皮匠<br>
     * 13 mason 石匠<br>
     * 14 nitwit 傻子<br>
     */
    protected int profession;

    {//todo 移除这些，实现原版随机村民
        var input1 = Item.fromString("minecraft:string");
        var input12 = Item.fromString("minecraft:emerald");
        input1.setCount(20);
        input12.setCount(2);
        var output1 = Item.fromString("minecraft:iron_sword");
        output1.setCount(1);
        output1.setNamedTag(SNBTParser.parseSNBT("""
                        {
                          "Damage": 0i,
                          "ench": [
                            {
                              "id": 17s,
                              "lvl": 2s
                            },
                            {
                              "id": 9s,
                              "lvl": 2s
                            }
                          ]
                        }
                """));
        var input2 = Item.fromString("minecraft:emerald");
        input2.setCount(1);
        var output2 = Item.fromString("minecraft:string");
        output2.setCount(20);
        this.recipes.add(RecipeBuildUtils.of(input1, input12, output1)
                        .setMaxUses(16)
                        .setPriceMultiplierA(0.05f)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(input2, output2)
                        .setMaxUses(16)
                        .setPriceMultiplierA(0.05f)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(input1, output1)
                        .setMaxUses(16)
                        .setPriceMultiplierA(0.05f)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(2)
                        .build());
        this.tierExpRequirement = new int[]{0, 10, 70, 150, 250};
    }

    public EntityVillager(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    //todo 实现不同群系的村民
    @Override
    public int getNetworkId() {
        return NETWORK_ID;
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

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Villager";
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r1")
    @Override
    public void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
        setTradingPlayer(0L);
        int ran = randomProfession();
        if (!this.namedTag.contains("profession")) {
            this.setProfession(ran);
        } else {
            var profession = this.namedTag.getInt("profession");//todo 移除这些，实现原版随机村民
            this.profession = profession;
            this.setDataProperty(new IntEntityData(DATA_VARIANT, profession));
        }
        if (!this.namedTag.contains("canTrade")) {
            this.setCanTrade(!(ran == 0 || ran == 14));
        } else {
            this.canTrade = this.namedTag.getBoolean("canTrade");
        }
        if (!this.namedTag.contains("displayName") && ran != 0) {
            this.setDisplayName(getProfessionName(ran));
        } else {
            this.displayName = this.namedTag.getString("displayName");
        }
        if (!this.namedTag.contains("tradeTier")) {
            this.setTradeTier(2);
        } else {
            this.tradeTier = this.namedTag.getInt("tradeTier");
        }
        if (!this.namedTag.contains("maxTradeTier")) {
            this.setMaxTradeTier(2);
        } else {
            var maxTradeTier = this.namedTag.getInt("maxTradeTier");
            this.maxTradeTier = maxTradeTier;
            this.setDataProperty(new IntEntityData(DATA_MAX_TRADE_TIER, maxTradeTier));
        }
        if (!this.namedTag.contains("tradeExp")) {
            this.setTradeExp(2);
        } else {
            var tradeExp = this.namedTag.getInt("tradeExp");
            this.tradeExp = tradeExp;
            this.setDataProperty(new IntEntityData(DATA_TRADE_EXPERIENCE, tradeExp));
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putByte("Profession", this.getProfession());
        this.namedTag.putBoolean("isTrade", this.getCanTrade());
        this.namedTag.putString("displayName", this.getDisplayName());
        this.namedTag.putInt("tradeTier", this.getTradeTier());
        this.namedTag.putInt("maxTradeTier", this.getMaxTradeTier());
        this.namedTag.putInt("tradeExp", this.getTradeExp());
    }

    /**
     * 获取村民职业id对应的displayName硬编码
     */
    @PowerNukkitXOnly
    @Since("1.19.21-r1")
    private String getProfessionName(int profession) {
        return switch (profession) {
            case 1 -> "entity.villager.farmer";
            case 2 -> "entity.villager.fisherman";
            case 3 -> "entity.villager.shepherd";
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
    @PowerNukkitXOnly
    @Since("1.19.21-r1")
    public int getProfession() {
        return profession;
    }

    /**
     * 设置村民职业
     *
     * @param profession 请查看{@link EntityVillager#profession}
     */
    @PowerNukkitXOnly
    @Since("1.19.21-r1")
    public void setProfession(int profession) {
        this.profession = profession;
        this.setDataProperty(new IntEntityData(DATA_VARIANT, profession));
        this.namedTag.putInt("profession", this.profession);
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r1")
    private int randomProfession() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextInt(0, 14);
    }

    /**
     * 这个方法插件一般不用
     */
    @PowerNukkitXOnly
    @Since("1.19.21-r1")
    public void setTradingPlayer(Long eid) {
        this.setDataProperty(new LongEntityData(DATA_TRADING_PLAYER_EID, eid));
    }

    /**
     * @return 该村民是否可以交易
     */
    @PowerNukkitXOnly
    @Since("1.19.21-r1")
    public boolean getCanTrade() {
        return canTrade;
    }

    /**
     * 设置村民是否可以交易
     *
     * @param canTrade true 可以交易
     */
    @PowerNukkitXOnly
    @Since("1.19.21-r1")
    public void setCanTrade(boolean canTrade) {
        this.canTrade = canTrade;
        this.namedTag.putBoolean("canTrade", canTrade);
    }

    /**
     * @return 交易UI的显示名称
     */
    @PowerNukkitXOnly
    @Since("1.19.21-r1")
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName 设置交易UI的显示名称
     */
    @PowerNukkitXOnly
    @Since("1.19.21-r1")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.namedTag.putString("displayName", displayName);
    }

    /**
     * @return 该村民的交易等级
     */
    @PowerNukkitXOnly
    @Since("1.19.21-r1")
    public int getTradeTier() {
        return tradeTier;
    }

    /**
     * @param tradeTier <p>村民的交易等级(1-{@link EntityVillager#maxTradeTier})</p>
     */
    @PowerNukkitXOnly
    @Since("1.19.21-r1")
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
        this.setDataProperty(new IntEntityData(DATA_MAX_TRADE_TIER, 5));
        this.namedTag.putInt("maxTradeTier", this.tradeTier);
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
        this.setDataProperty(new IntEntityData(DATA_TRADE_EXPERIENCE, 10));
        this.namedTag.putInt("tradeExp", this.tradeTier);
    }

    @Override
    public boolean isBaby() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_BABY);
    }

    public void setBaby(boolean baby) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_BABY, baby);
        this.setScale(baby ? 0.5f : 1);
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r1")
    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (this.getCanTrade()) {
            var inv = new TradeInventory(this);
            player.addWindow(inv, Player.TRADE_WINDOW_ID);
            return true;
        } else return false;
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r1")
    @Override
    public TradeInventory getInventory() {
        return inventory;
    }
}
