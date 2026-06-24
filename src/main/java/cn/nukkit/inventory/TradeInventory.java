package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.entity.passive.EntityVillagerV2;
import com.google.common.collect.BiMap;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;

import java.util.Map;


public class TradeInventory extends BaseInventory {
    protected EntityVillagerV2 holder;
    public String displayName;

    public TradeInventory(EntityVillagerV2 holder) {
        super(holder, ContainerType.TRADE, 3);
        this.holder = holder;
    }

    @Override
    public void init() {
        BiMap<Integer, Integer> networkedSlotMap = networkSlotMap();
        networkedSlotMap.put(0, 4);
        networkedSlotMap.put(1, 5);
        Map<Integer, ContainerEnumName> slotTypeMap = slotTypeMap();
        slotTypeMap.put(0, ContainerEnumName.TRADE2_INGREDIENT1_CONTAINER);
        slotTypeMap.put(1, ContainerEnumName.TRADE2_INGREDIENT2_CONTAINER);
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
        villager.updateTrades(who);
    }

    @Override
    public void onClose(Player who) {
        this.getHolder().setTradingPlayer(0L);
        super.onClose(who);
    }
}