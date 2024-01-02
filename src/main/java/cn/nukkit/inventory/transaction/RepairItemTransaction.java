package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAnvil;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockState;
import cn.nukkit.event.block.AnvilDamageEvent;
import cn.nukkit.event.block.AnvilDamageEvent.DamageCause;
import cn.nukkit.event.inventory.RepairItemEvent;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.inventory.FakeBlockMenu;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.RepairItemAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RepairItemTransaction extends InventoryTransaction {

    private Item inputItem;
    private Item materialItem;
    private Item outputItem;

    private int cost;

    public RepairItemTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions);
        for (InventoryAction action : actions) {
            if (action instanceof SlotChangeAction slotChangeAction) {
                if (slotChangeAction.getInventory() instanceof PlayerInventory) {
                    this.outputItem = slotChangeAction.getTargetItem();
                }
            }
        }
    }

    @Override
    public boolean canExecute() {
        Inventory inventory = getSource().getWindowById(Player.ANVIL_WINDOW_ID);
        if (inventory == null) {
            return false;
        }
        AnvilInventory anvilInventory = (AnvilInventory) inventory;
        return this.inputItem != null && this.outputItem != null && this.inputItem.equals(anvilInventory.getInputSlot(), true, true)
                && (!this.hasMaterial() || this.materialItem.equals(anvilInventory.getMaterialSlot(), true, true))
                && this.inputItem.getId().equals(this.outputItem.getId())
                && this.inputItem.getCount() == this.outputItem.getCount()
                && this.checkRecipeValid();
    }

    @Override
    public boolean execute() {
        if (this.hasExecuted() || !this.canExecute()) {
            this.source.removeAllWindows(false);
            this.sendInventories();
            return false;
        }
        AnvilInventory inventory = (AnvilInventory) getSource().getWindowById(Player.ANVIL_WINDOW_ID);

        if (inventory.getCost() != this.cost && !this.source.isCreative()) {
            this.source.getServer().getLogger().debug("Got unexpected cost " + inventory.getCost() + " from " + this.source.getName() + "(expected " + this.cost + ")");
        }

        RepairItemEvent event = new RepairItemEvent(inventory, this.inputItem, this.outputItem, this.materialItem, this.cost, this.source);
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

        FakeBlockMenu holder = inventory.getHolder();
        Block block = this.source.level.getBlock(holder.getFloorX(), holder.getFloorY(), holder.getFloorZ());
        if (block.getId().equals(Block.ANVIL)) {
            int oldDamage = block.getDamage() >= 8 ? 2 : block.getDamage() >= 4 ? 1 : 0;
            int newDamage = !this.source.isCreative() && ThreadLocalRandom.current().nextInt(100) < 12 ? oldDamage + 1 : oldDamage;

            AnvilDamageEvent ev = new AnvilDamageEvent(block, oldDamage, newDamage, DamageCause.USE, this.source);
            ev.setCancelled(oldDamage == newDamage);
            this.source.getServer().getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                BlockState newState = ev.getNewBlockState();
                if (newState.getBlockId() == BlockID.AIR
                        || newState.getBlockId() == BlockID.ANVIL && newState.getPropertyValue(BlockAnvil.DAMAGE).equals(AnvilDamage.BROKEN)) {
                    this.source.level.setBlock(block, Block.get(Block.AIR), true);
                    this.source.level.addLevelEvent(block, LevelEventPacket.EVENT_SOUND_ANVIL_BREAK);
                } else {
                    if (!newState.equals(ev.getOldBlockState())) {
                        Block newBlock = newState.getBlockRepairing(block);
                        this.source.level.setBlock(block, newBlock, true);
                    }
                    this.source.level.addLevelEvent(block, LevelEventPacket.EVENT_SOUND_ANVIL_USE);
                }
            } else {
                this.source.level.addLevelEvent(block, LevelEventPacket.EVENT_SOUND_ANVIL_USE);
            }
        }

        if (!this.source.isCreative()) {
            this.source.setExperience(this.source.getExperience(), this.source.getExperienceLevel() - event.getCost());
        }
        return true;
    }

    @Override
    public void addAction(InventoryAction action) {
        super.addAction(action);
        if (action instanceof RepairItemAction) {
            switch (((RepairItemAction) action).getType()) {
                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_INPUT -> this.inputItem = action.getTargetItem();
                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_RESULT -> this.outputItem = action.getSourceItem();
                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_MATERIAL -> this.materialItem = action.getTargetItem();
            }
        }
    }

    private boolean checkRecipeValid() {
        int cost = 0;
        int baseRepairCost = this.inputItem.getRepairCost();

        if (this.isMapRecipe()) {
            if (!this.matchMapRecipe()) {
                return false;
            }
            baseRepairCost = 0;
        } else if (this.hasMaterial()) {
            baseRepairCost += this.materialItem.getRepairCost();

            if (this.inputItem.getMaxDurability() != -1 && this.matchRepairItem()) {
                int maxRepairDamage = this.inputItem.getMaxDurability() / 4;
                int repairDamage = Math.min(this.inputItem.getAux(), maxRepairDamage);
                if (repairDamage <= 0) {
                    return false;
                }

                int damage = this.inputItem.getAux();
                for (; repairDamage > 0 && cost < this.materialItem.getCount(); cost++) {
                    damage = damage - repairDamage;
                    repairDamage = Math.min(damage, maxRepairDamage);
                }
                if (this.outputItem.getAux() != damage) {
                    return false;
                }
            } else {
                boolean consumeEnchantedBook = this.materialItem.getId() == Item.ENCHANTED_BOOK && this.materialItem.hasEnchantments();
                if (!consumeEnchantedBook && (this.inputItem.getMaxDurability() == -1 || this.inputItem.getId() != this.materialItem.getId())) {
                    return false;
                }

                if (!consumeEnchantedBook && this.inputItem.getMaxDurability() != -1) {
                    int damage = this.inputItem.getAux() - this.inputItem.getMaxDurability() + this.materialItem.getAux() - this.inputItem.getMaxDurability() * 12 / 100 + 1;
                    if (damage < 0) {
                        damage = 0;
                    }

                    if (damage < this.inputItem.getAux()) {
                        if (this.outputItem.getAux() != damage) {
                            return false;
                        }
                        cost += 2;
                    }
                }

                Int2IntMap enchantments = new Int2IntOpenHashMap();
                enchantments.defaultReturnValue(-1);
                for (Enchantment enchantment : this.inputItem.getEnchantments()) {
                    enchantments.put(enchantment.getId(), enchantment.getLevel());
                }

                boolean hasCompatibleEnchantments = false;
                boolean hasIncompatibleEnchantments = false;
                for (Enchantment materialEnchantment : this.materialItem.getEnchantments()) {
                    Enchantment enchantment = this.inputItem.getEnchantment(materialEnchantment.getId());
                    int inputLevel = enchantment != null ? enchantment.getLevel() : 0;
                    int materialLevel = materialEnchantment.getLevel();
                    int outputLevel = inputLevel == materialLevel ? materialLevel + 1 : Math.max(materialLevel, inputLevel);

                    boolean canEnchant = materialEnchantment.canEnchant(this.inputItem) || this.inputItem.getId() == Item.ENCHANTED_BOOK;
                    for (Enchantment inputEnchantment : this.inputItem.getEnchantments()) {
                        if (inputEnchantment.getId() != materialEnchantment.getId() && !materialEnchantment.isCompatibleWith(inputEnchantment)) {
                            canEnchant = false;
                            cost++;
                        }
                    }

                    if (!canEnchant) {
                        hasIncompatibleEnchantments = true;
                    } else {
                        hasCompatibleEnchantments = true;
                        if (outputLevel > materialEnchantment.getMaxLevel()) {
                            outputLevel = materialEnchantment.getMaxLevel();
                        }

                        enchantments.put(materialEnchantment.getId(), outputLevel);
                        int rarityFactor = switch (materialEnchantment.getRarity()) {
                            case COMMON -> 1;
                            case UNCOMMON -> 2;
                            case RARE -> 4;
                            default -> 8;
                        };

                        if (consumeEnchantedBook) {
                            rarityFactor = Math.max(1, rarityFactor / 2);
                        }

                        cost += rarityFactor * Math.max(0, outputLevel - inputLevel);
                        if (this.inputItem.getCount() > 1) {
                            cost = 40;
                        }
                    }
                }

                Enchantment[] outputEnchantments = this.outputItem.getEnchantments();
                if (hasIncompatibleEnchantments && !hasCompatibleEnchantments || enchantments.size() != outputEnchantments.length) {
                    return false;
                }

                for (Enchantment enchantment : outputEnchantments) {
                    if (enchantments.get(enchantment.getId()) != enchantment.getLevel()) {
                        return false;
                    }
                }
            }
        }

        int renameCost = 0;
        if (!this.inputItem.getCustomName().equals(this.outputItem.getCustomName())) {
            if (this.outputItem.getCustomName().length() > 30) {
                return false;
            }
            renameCost = 1;
            cost += renameCost;
        }

        this.cost = baseRepairCost + cost;
        if (renameCost == cost && renameCost > 0 && this.cost >= 40) {
            this.cost = 39;
        }
        if (baseRepairCost < 0 || cost < 0 || cost == 0 && !this.isMapRecipe() || this.cost > 39 && !this.source.isCreative()) {
            return false;
        }

        int nextBaseRepairCost = this.inputItem.getRepairCost();
        if (!this.isMapRecipe()) {
            if (this.hasMaterial() && nextBaseRepairCost < this.materialItem.getRepairCost()) {
                nextBaseRepairCost = this.materialItem.getRepairCost();
            }
            if (renameCost == 0 || renameCost != cost) {
                nextBaseRepairCost = 2 * nextBaseRepairCost + 1;
            }
        }
        if (this.outputItem.getRepairCost() != nextBaseRepairCost) {
            this.source.getServer().getLogger().debug("Got unexpected base cost " + this.outputItem.getRepairCost() + " from " + this.source.getName() + "(expected " + nextBaseRepairCost + ")");
            return false;
        }

        return true;
    }

    private boolean hasMaterial() {
        return this.materialItem != null && !this.materialItem.isNull();
    }

    private boolean isMapRecipe() {
        return this.hasMaterial() && (this.inputItem.getId().equals(Item.FILLED_MAP) || this.inputItem.getId().equals(Item.EMPTY_MAP))
                && (this.materialItem.getId().equals(Item.EMPTY_MAP) || this.materialItem.getId().equals(Item.PAPER) || this.materialItem.getId().equals(Item.COMPASS));
    }

    private boolean matchMapRecipe() {
        if (this.inputItem.getId().equals(Item.EMPTY_MAP)) {
            return this.inputItem.getAux() != 2 && this.materialItem.getId().equals(Item.COMPASS) // locator
                    && this.outputItem.getId().equals(Item.EMPTY_MAP) && this.outputItem.getAux() == 2 && this.outputItem.getCount() == 1;
        } else if (this.inputItem.getId().equals(Item.FILLED_MAP) && this.outputItem.getAux() == this.inputItem.getAux()) {
            if (this.materialItem.getId().equals(Item.COMPASS)) { // locator
                return this.inputItem.getAux() != 2 && this.outputItem.getId().equals(Item.FILLED_MAP) && this.outputItem.getCount() == 1;
            } else if (this.materialItem.getId().equals(Item.EMPTY_MAP)) { // clone
                return this.outputItem.getId().equals(Item.FILLED_MAP) && this.outputItem.getCount() == 2;
            } else if (this.materialItem.getId().equals(Item.PAPER) && this.materialItem.getCount() >= 8) { // zoom out
                return this.inputItem.getAux() < 3 && this.outputItem.getId().equals(Item.FILLED_MAP) && this.outputItem.getCount() == 1;
            }
        }
        return false;
    }

    private boolean matchRepairItem() {
        return switch (this.inputItem.getId()) {
            case Item.LEATHER_HELMET, Item.LEATHER_CHESTPLATE, Item.LEATHER_LEGGINGS, Item.LEATHER_BOOTS ->
                    this.materialItem.getId().equals(Item.LEATHER);
            case Item.WOODEN_SWORD, Item.WOODEN_PICKAXE, Item.WOODEN_SHOVEL, Item.WOODEN_AXE, Item.WOODEN_HOE, Item.SHIELD ->
                    this.materialItem.getId().equals(Item.PLANKS);
            case Item.STONE_SWORD, Item.STONE_PICKAXE, Item.STONE_SHOVEL, Item.STONE_AXE, Item.STONE_HOE ->
                    this.materialItem.getId().equals(BlockID.COBBLESTONE);
            case Item.IRON_SWORD, Item.IRON_PICKAXE, Item.IRON_SHOVEL, Item.IRON_AXE, Item.IRON_HOE, Item.IRON_HELMET, Item.IRON_CHESTPLATE, Item.IRON_LEGGINGS, Item.IRON_BOOTS, Item.CHAINMAIL_HELMET, Item.CHAINMAIL_CHESTPLATE, Item.CHAINMAIL_LEGGINGS, Item.CHAINMAIL_BOOTS ->
                    this.materialItem.getId().equals(Item.IRON_INGOT);
            case Item.GOLDEN_SWORD, Item.GOLDEN_PICKAXE, Item.GOLDEN_SHOVEL, Item.GOLDEN_AXE, Item.GOLDEN_HOE, Item.GOLDEN_HELMET, Item.GOLDEN_CHESTPLATE, Item.GOLDEN_LEGGINGS, Item.GOLDEN_BOOTS ->
                    this.materialItem.getId().equals(Item.GOLD_INGOT);
            case Item.DIAMOND_SWORD, Item.DIAMOND_PICKAXE, Item.DIAMOND_SHOVEL, Item.DIAMOND_AXE, Item.DIAMOND_HOE, Item.DIAMOND_HELMET, Item.DIAMOND_CHESTPLATE, Item.DIAMOND_LEGGINGS, Item.DIAMOND_BOOTS ->
                    this.materialItem.getId().equals(Item.DIAMOND);
            case Item.NETHERITE_SWORD, Item.NETHERITE_PICKAXE, Item.NETHERITE_SHOVEL, Item.NETHERITE_AXE, Item.NETHERITE_HOE, Item.NETHERITE_HELMET, Item.NETHERITE_CHESTPLATE, Item.NETHERITE_LEGGINGS, Item.NETHERITE_BOOTS ->
                    this.materialItem.getId().equals(Item.NETHERITE_INGOT);
            case Item.TURTLE_HELMET -> this.materialItem.getId().equals(Item.SCUTE);
            case Item.ELYTRA -> this.materialItem.getId().equals(Item.PHANTOM_MEMBRANE);
            default -> false;
        };
    }

    public Item getInputItem() {
        return this.inputItem;
    }

    public Item getMaterialItem() {
        return this.materialItem;
    }

    public Item getOutputItem() {
        return this.outputItem;
    }

    public int getCost() {
        return this.cost;
    }

    public static boolean checkForRepairItemPart(List<InventoryAction> actions) {
        for (InventoryAction action : actions) {
            if (action instanceof RepairItemAction) {
                return true;
            }
        }
        return false;
    }
}
