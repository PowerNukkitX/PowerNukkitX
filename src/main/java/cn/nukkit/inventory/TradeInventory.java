package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.passive.EntityVillager;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.UpdateTradePacket;

@PowerNukkitXOnly
@Since("1.19.20-r7")
public class TradeInventory extends BaseInventory {
    //hack实现
    public static final int TRADE_INPUT1_UI_SLOT = 4;
    public static final int TRADE_INPUT2_UI_SLOT = 5;

    protected EntityVillager holder;
    public String displayName;

    public TradeInventory(EntityVillager holder) {
        super(holder, InventoryType.TRADING);
        this.holder = holder;
    }

    @Override
    public EntityVillager getHolder() {
        return this.holder;
    }

    @Override
    public int getMaxStackSize() {
        return 3;
    }

    @Override
    public String getName() {
        return "Trade";
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        var villager = this.getHolder();
        villager.setTradingPlayer(who.getId());
        var pk1 = new UpdateTradePacket();
        pk1.containerId = (byte) who.getWindowId(this);
        pk1.tradeTier = villager.getTradeTier();
        pk1.traderUniqueEntityId = villager.getId();
        pk1.playerUniqueEntityId = who.getId();
        pk1.displayName = villager.getDisplayName();
        var tierExpRequirements = new ListTag<CompoundTag>("TierExpRequirements");
        for (int i = 0, len = villager.tierExpRequirement.length; i < len; ++i) {
            tierExpRequirements.add(i, new CompoundTag().putInt(String.valueOf(i), villager.tierExpRequirement[i]));
        }
        pk1.offers = new CompoundTag()
                .putList(villager.recipes)
                .putList(tierExpRequirements);
        pk1.newTradingUi = true;
        pk1.usingEconomyTrade = true;
        who.dataPacket(pk1);
    }

    @Override
    public void onClose(Player who) {
        this.getHolder().setTradingPlayer(0L);
        super.onClose(who);
    }
}
