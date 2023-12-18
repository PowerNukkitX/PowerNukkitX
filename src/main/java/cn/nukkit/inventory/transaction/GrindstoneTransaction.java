/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2021  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.inventory.GrindstoneEvent;
import cn.nukkit.inventory.GrindstoneInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.transaction.action.GrindstoneItemAction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;

import java.util.List;

/**
 * @author joserobjr
 * @since 2021-03-21
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class GrindstoneTransaction extends InventoryTransaction {

    private Item firstItem;
    private Item secondItem;
    private Item outputItem;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public GrindstoneTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions);
    }

    @Override
    public void addAction(InventoryAction action) {
        super.addAction(action);
        if (action instanceof GrindstoneItemAction grindstoneItemAction) {
            switch (grindstoneItemAction.getType()) {
                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_INPUT -> this.firstItem = action.getTargetItem();
                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_RESULT -> this.outputItem = action.getSourceItem();
                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_MATERIAL -> this.secondItem = action.getTargetItem();
            }
        }
    }

    @Override
    public boolean canExecute() {
        Inventory inventory = getSource().getWindowById(Player.GRINDSTONE_WINDOW_ID);
        if (inventory == null) {
            return false;
        }
        GrindstoneInventory grindstoneInventory = (GrindstoneInventory) inventory;
        if (outputItem == null || outputItem.isNull() ||
                ((firstItem == null || firstItem.isNull()) && (secondItem == null || secondItem.isNull()))) {
            return false;
        }

        Item first = firstItem != null ? firstItem : Item.AIR_ITEM;
        Item second = secondItem != null ? secondItem : Item.AIR_ITEM;

        boolean firstEquals = first.equals(grindstoneInventory.getFirstItem(), true, true);
        boolean secondEquals = second.equals(grindstoneInventory.getSecondItem(), true, true);
        boolean outputItemEquals = outputItem.equals(grindstoneInventory.getResult(), true, false);
        boolean isNotEnchanted = outputItemEquals ? !grindstoneInventory.getResult().hasEnchantments() : !outputItem.hasEnchantments();

        // The GrindstoneTransaction and RepairItemTransaction receive items with a difference in the NBT "RepairCost".
        // However, it is not necessary to check this difference. So, we relax the check here.
        return firstEquals && secondEquals && outputItemEquals && isNotEnchanted;
    }

    @Override
    public boolean execute() {
        if (this.hasExecuted() || !this.canExecute()) {
            this.source.removeAllWindows(false);
            this.sendInventories();
            return false;
        }
        GrindstoneInventory inventory = (GrindstoneInventory) getSource().getWindowById(Player.GRINDSTONE_WINDOW_ID);
        int exp = inventory.getResultExperience();
        Item first = firstItem != null ? firstItem : Item.AIR_ITEM;
        Item second = secondItem != null ? secondItem : Item.AIR_ITEM;
        GrindstoneEvent event = new GrindstoneEvent(inventory, first, outputItem, second, exp, source);
        this.source.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            this.source.removeAllWindows(false);
            this.sendInventories();
            return true;
        }

        for (InventoryAction action : this.actions) {
            if (action.execute(this.source)) {
                action.onExecuteSuccess(this.source);
            } else {
                action.onExecuteFail(this.source);
            }
        }
        return true;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Item getFirstItem() {
        return firstItem == null? null : firstItem.clone();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Item getSecondItem() {
        return secondItem == null? null : secondItem.clone();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Item getOutputItem() {
        return outputItem == null? null : outputItem.clone();
    }

    /**
     * Checks if any action in the given list is of type `GrindstoneItemAction`.
     *
     * @param  actions   the list of inventory actions to check
     * @return           true if any action is of type `GrindstoneItemAction`, false otherwise
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static boolean checkForItemPart(List<InventoryAction> actions) {
        return actions.stream().anyMatch(action -> action instanceof GrindstoneItemAction);
    }
}
