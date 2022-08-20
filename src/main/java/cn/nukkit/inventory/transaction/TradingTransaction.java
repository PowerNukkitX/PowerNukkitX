package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.inventory.transaction.action.TradeAction;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;

import java.util.List;

@Since("1.19.20-r7")
public class TradingTransaction extends InventoryTransaction {
    private Item inputItem1;
    private Item inputItem2;
    private Item outputItem;

    public TradingTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions);
    }

    @Override
    public boolean execute() {
        for (InventoryAction action : this.actions) {
            System.out.println(action);
            if (action instanceof SlotChangeAction slotChangeAction) {
                if (slotChangeAction.execute(this.source)) {
                    slotChangeAction.onExecuteSuccess(this.source);
                } else {
                    slotChangeAction.onExecuteFail(this.source);
                }
            }
        }
        return true;
    }

    @Override
    public void addAction(InventoryAction action) {
        super.addAction(action);
        if (action instanceof TradeAction tradeAction) {
            switch (tradeAction.getType()) {
                case NetworkInventoryAction.SOURCE_TYPE_TRADING_INPUT_1 ->
                        this.inputItem1 = tradeAction.getTargetItem();
                case NetworkInventoryAction.SOURCE_TYPE_TRADING_OUTPUT -> this.outputItem = tradeAction.getSourceItem();
            }
        }
    }
}
