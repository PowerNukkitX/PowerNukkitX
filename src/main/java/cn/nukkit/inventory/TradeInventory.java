package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.entity.passive.EntityVillagerV2;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.UpdateTradePacket;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import com.google.common.collect.BiMap;

import java.util.Map;


public class TradeInventory extends BaseInventory {
    protected EntityVillagerV2 holder;
    public String displayName;

    public TradeInventory(EntityVillagerV2 holder) {
        super(holder, InventoryType.TRADE, 3);
        this.holder = holder;
    }

    @Override
    public void init() {
        BiMap<Integer, Integer> networkedSlotMap = networkSlotMap();
        networkedSlotMap.put(0, 4);
        networkedSlotMap.put(1, 5);
        Map<Integer, ContainerSlotType> slotTypeMap = slotTypeMap();
        slotTypeMap.put(0, ContainerSlotType.TRADE2_INGREDIENT_1);
        slotTypeMap.put(1, ContainerSlotType.TRADE2_INGREDIENT_2);
    }

    @Override
    public EntityVillagerV2 getHolder() {
        return this.holder;
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
        var tierExpRequirements = new ListTag<CompoundTag>();
        for (int i = 0, len = villager.tierExpRequirement.length; i < len; ++i) {
            tierExpRequirements.add(i, new CompoundTag().putInt(String.valueOf(i), villager.tierExpRequirement[i]));
        }
        pk1.offers = new CompoundTag()
                .putList("Recipes", villager.getRecipes())
                .putList("TierExpRequirements", tierExpRequirements);
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
