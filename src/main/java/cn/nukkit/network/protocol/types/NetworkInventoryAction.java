package cn.nukkit.network.protocol.types;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.*;
import cn.nukkit.inventory.transaction.action.*;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;

/**
 * @author CreeperFace
 */
@ToString
@Log4j2
public class NetworkInventoryAction {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final NetworkInventoryAction[] EMPTY_ARRAY = new NetworkInventoryAction[0];
    public static final int SOURCE_CONTAINER = 0;
    public static final int SOURCE_WORLD = 2; //drop/pickup item entity
    public static final int SOURCE_CREATIVE = 3;
    public static final int SOURCE_TODO = 99999;
    public static final int SOURCE_CRAFT_SLOT = 100;
    /**
     * Fake window IDs for the SOURCE_TODO type (99999)
     * <p>
     * These identifiers are used for inventory source types which are not currently implemented server-side in MCPE.
     * As a general rule of thumb, anything that doesn't have a permanent inventory is client-side. These types are
     * to allow servers to track what is going on in client-side windows.
     * <p>
     * Expect these to change in the future.
     */
    public static final int SOURCE_TYPE_CRAFTING_ADD_INGREDIENT = -2;
    public static final int SOURCE_TYPE_CRAFTING_REMOVE_INGREDIENT = -3;
    public static final int SOURCE_TYPE_CRAFTING_RESULT = -4;
    public static final int SOURCE_TYPE_CRAFTING_USE_INGREDIENT = -5;
    public static final int SOURCE_TYPE_ANVIL_INPUT = -10;
    public static final int SOURCE_TYPE_ANVIL_MATERIAL = -11;
    public static final int SOURCE_TYPE_ANVIL_RESULT = -12;
    public static final int SOURCE_TYPE_ANVIL_OUTPUT = -13;
    public static final int SOURCE_TYPE_ENCHANT_INPUT = -15;
    public static final int SOURCE_TYPE_ENCHANT_MATERIAL = -16;
    public static final int SOURCE_TYPE_ENCHANT_OUTPUT = -17;
    public static final int SOURCE_TYPE_TRADING_OUTPUT = -30;//输出物品
    public static final int SOURCE_TYPE_TRADING_INPUT_1 = -31;//实际上不管是交易物品A还是交易物品B都是这个window id
    //下面这两个没啥用
    public static final int SOURCE_TYPE_TRADING_INPUT_2 = -32;
    public static final int SOURCE_TYPE_TRADING_USE_INPUTS = -33;
    public static final int SOURCE_TYPE_BEACON = -24;
    /**
     * Any client-side window dropping its contents when the player closes it
     */
    public static final int SOURCE_TYPE_CONTAINER_DROP_CONTENTS = -100;

    @Deprecated
    @DeprecationDetails(since = "1.19.80-r2", reason = "replace", replaceWith = "InventorySource")
    @ToString.Exclude
    public int sourceType;
    @Deprecated
    @DeprecationDetails(since = "1.19.80-r2", reason = "replace", replaceWith = "InventorySource")
    @ToString.Exclude
    public int windowId;
    @Deprecated
    @DeprecationDetails(since = "1.19.80-r2", reason = "replace", replaceWith = "InventorySource")
    @ToString.Exclude
    public long unknown;
    @Deprecated
    @Since("1.3.0.0-PN")
    @ToString.Exclude
    public int stackNetworkId = 0;
    @PowerNukkitXOnly
    @Since("1.19.80-r2")
    private InventorySource inventorySource;
    public int inventorySlot;
    public Item oldItem;
    public Item newItem;

    @PowerNukkitXOnly
    @Since("1.19.80-r2")
    public InventorySource getInventorySource() {
        return inventorySource;
    }

    @PowerNukkitXOnly
    @Since("1.19.80-r2")
    public void setInventorySource(InventorySource inventorySource) {
        this.inventorySource = inventorySource;
    }

    public NetworkInventoryAction read(InventoryTransactionPacket packet) {
        //read InventorySource
        InventorySource.Type type = InventorySource.Type.byId((int) packet.getUnsignedVarInt());
        switch (type) {
            case UNTRACKED_INTERACTION_UI ->
                    inventorySource = InventorySource.fromUntrackedInteractionUI(packet.getVarInt());
            case CONTAINER -> {
                this.windowId = packet.getVarInt();
                inventorySource = InventorySource.fromContainerWindowId(this.windowId);
            }
            case GLOBAL -> inventorySource = InventorySource.fromGlobalInventory();
            case WORLD_INTERACTION -> {
                this.unknown = packet.getUnsignedVarInt();
                InventorySource.Flag flag = InventorySource.Flag.values()[(int) this.unknown];
                inventorySource = InventorySource.fromWorldInteraction(flag);
            }
            case CREATIVE -> inventorySource = InventorySource.fromCreativeInventory();
            case NON_IMPLEMENTED_TODO -> {
                this.windowId = packet.getVarInt();
                switch (this.windowId) {
                    case SOURCE_TYPE_CRAFTING_RESULT, SOURCE_TYPE_CRAFTING_USE_INGREDIENT ->
                            packet.isCraftingPart = true;
                    case SOURCE_TYPE_ENCHANT_INPUT, SOURCE_TYPE_ENCHANT_OUTPUT, SOURCE_TYPE_ENCHANT_MATERIAL ->
                            packet.isEnchantingPart = true;
                    case SOURCE_TYPE_ANVIL_INPUT, SOURCE_TYPE_ANVIL_MATERIAL, SOURCE_TYPE_ANVIL_RESULT ->
                            packet.isRepairItemPart = true;
                    case SOURCE_TYPE_TRADING_INPUT_1, SOURCE_TYPE_TRADING_INPUT_2, SOURCE_TYPE_TRADING_USE_INPUTS, SOURCE_TYPE_TRADING_OUTPUT ->
                            packet.isTradeItemPart = true;
                }
                inventorySource = InventorySource.fromNonImplementedTodo(this.windowId);
            }
            default -> inventorySource = InventorySource.fromInvalid();
        }
        this.inventorySlot = (int) packet.getUnsignedVarInt();
        this.oldItem = packet.getSlot();
        this.newItem = packet.getSlot();
        return this;
    }

    public void write(InventoryTransactionPacket packet) {
        packet.putUnsignedVarInt(this.inventorySource.getType().id());
        switch (inventorySource.getType()) {
            case CONTAINER, UNTRACKED_INTERACTION_UI, NON_IMPLEMENTED_TODO ->
                    packet.putVarInt(inventorySource.getContainerId());
            case WORLD_INTERACTION -> packet.putUnsignedVarInt(inventorySource.getFlag().ordinal());
        }
        packet.putUnsignedVarInt(this.inventorySlot);
        packet.putSlot(this.oldItem);
        packet.putSlot(this.newItem);
    }

    /**
     * 用于将从网络解析出来的{@link NetworkInventoryAction}解析成核心内部实现的{@link InventoryAction}
     * 通过从{@link InventorySource#getType()} 和 {@link InventorySource#getContainerId()} 来判断inventory的类型
     */
    public InventoryAction createInventoryAction(Player player) {
        InventorySource.Type type;
        int id;
        if (inventorySource != null) {
            type = this.getInventorySource().getType();
            id = this.getInventorySource().getContainerId();
        } else {
            //保持兼容性
            type = InventorySource.Type.byId(this.sourceType);
            id = windowId;
        }

        switch (type) {
            case CONTAINER -> {
                if (id == ContainerIds.ARMOR) {
                    //TODO: HACK!
                    this.inventorySlot += 36;
                    id = ContainerIds.INVENTORY;
                } else if (id == ContainerIds.UI) {
                    switch (this.inventorySlot) {
                        //124:1 -> 2:0
                        case AnvilInventory.ANVIL_INPUT_UI_SLOT -> {
                            if (player.getWindowById(Player.ANVIL_WINDOW_ID) == null) {
                                log.error("Player {} does not have anvil window open", player.getName());
                                return null;
                            }
                            id = Player.ANVIL_WINDOW_ID;
                            this.inventorySlot = 0;
                        }
                        //124:2 -> 2:1
                        case AnvilInventory.ANVIL_MATERIAL_UI_SLOT -> {
                            if (player.getWindowById(Player.ANVIL_WINDOW_ID) == null) {
                                log.error("Player {} does not have anvil window open", player.getName());
                                return null;
                            }
                            id = Player.ANVIL_WINDOW_ID;
                            this.inventorySlot = 1;
                        }
                        //124:50 -> 2:2
                        case PlayerUIComponent.CREATED_ITEM_OUTPUT_UI_SLOT -> {
                            if (player.getWindowById(Player.ANVIL_WINDOW_ID) != null) {
                                id = Player.ANVIL_WINDOW_ID;
                                this.inventorySlot = 2;
                            }
                        }
                        //124:14 -> 3:0
                        case EnchantInventory.ENCHANT_INPUT_ITEM_UI_SLOT -> {
                            if (player.getWindowById(Player.ENCHANT_WINDOW_ID) == null) {
                                log.error("Player {} does not have enchant window open", player.getName());
                                return null;
                            }
                            id = Player.ENCHANT_WINDOW_ID;
                            this.inventorySlot = 0;
                        }
                        //124:15 -> 3:1
                        // TODO, check if unenchanted item and send EnchantOptionsPacket
                        case EnchantInventory.ENCHANT_REAGENT_UI_SLOT -> {
                            if (player.getWindowById(Player.ENCHANT_WINDOW_ID) == null) {
                                log.error("Player {} does not have enchant window open", player.getName());
                                return null;
                            }
                            id = Player.ENCHANT_WINDOW_ID;
                            this.inventorySlot = 1;
                        }
                        //124:51 -> 6:0
                        case SmithingInventory.SMITHING_EQUIPMENT_UI_SLOT -> {
                            if (player.getWindowById(Player.SMITHING_WINDOW_ID) == null) {
                                log.error("Player {} does not have smithing table window open", player.getName());
                                return null;
                            }
                            id = Player.SMITHING_WINDOW_ID;
                            this.inventorySlot = 0;
                        }
                        //124:52 -> 6:1
                        case SmithingInventory.SMITHING_INGREDIENT_UI_SLOT -> {
                            if (player.getWindowById(Player.SMITHING_WINDOW_ID) == null) {
                                log.error("Player {} does not have smithing table window open", player.getName());
                                return null;
                            }
                            id = Player.SMITHING_WINDOW_ID;
                            this.inventorySlot = 1;
                        }
                        //124:16 -> 5:0
                        case GrindstoneInventory.GRINDSTONE_EQUIPMENT_UI_SLOT -> {
                            if (player.getWindowById(Player.GRINDSTONE_WINDOW_ID) == null) {
                                log.error("Player {} does not have grindstone window open", player.getName());
                                return null;
                            }
                            id = Player.GRINDSTONE_WINDOW_ID;
                            this.inventorySlot = 0;
                        }
                        //124:17 -> 5:1
                        case GrindstoneInventory.GRINDSTONE_INGREDIENT_UI_SLOT -> {
                            if (player.getWindowById(Player.GRINDSTONE_WINDOW_ID) == null) {
                                log.error("Player {} does not have grindstone window open", player.getName());
                                return null;
                            }
                            id = Player.GRINDSTONE_WINDOW_ID;
                            this.inventorySlot = 1;
                        }
                        //124:4 -> 500:0
                        case TradeInventory.TRADE_INPUT1_UI_SLOT -> {
                            if (player.getWindowById(Player.TRADE_WINDOW_ID) == null) {
                                log.error("Player {} does not have Trade window open", player.getName());
                                return null;
                            }
                            id = Player.TRADE_WINDOW_ID;
                            this.inventorySlot = 0;
                        }
                        //124:5 -> 500:1
                        case TradeInventory.TRADE_INPUT2_UI_SLOT -> {
                            if (player.getWindowById(Player.TRADE_WINDOW_ID) == null) {
                                log.error("Player {} does not have Trade window open", player.getName());
                                return null;
                            }
                            id = Player.TRADE_WINDOW_ID;
                            this.inventorySlot = 1;
                        }
                    }
                }
                Inventory window = player.getWindowById(id);
                if (window != null) {
                    return new SlotChangeAction(window, this.inventorySlot, this.oldItem, this.newItem);
                }
                log.debug("Player {} has no open container with window ID {}", player.getName(), id);
                return null;
            }
            case WORLD_INTERACTION -> {
                if (this.inventorySlot != InventoryTransactionPacket.ACTION_MAGIC_SLOT_DROP_ITEM) {
                    log.debug("Only expecting drop-item world actions from the client!");
                    return null;
                }
                return new DropItemAction(this.oldItem, this.newItem);
            }
            case CREATIVE -> {
                int createType;
                switch (this.inventorySlot) {
                    case InventoryTransactionPacket.ACTION_MAGIC_SLOT_CREATIVE_DELETE_ITEM ->
                            createType = CreativeInventoryAction.TYPE_DELETE_ITEM;
                    case InventoryTransactionPacket.ACTION_MAGIC_SLOT_CREATIVE_CREATE_ITEM ->
                            createType = CreativeInventoryAction.TYPE_CREATE_ITEM;
                    default -> {
                        log.debug("Unexpected creative action type {}", this.inventorySlot);
                        return null;
                    }
                }
                return new CreativeInventoryAction(this.oldItem, this.newItem, createType);
            }
            case UNTRACKED_INTERACTION_UI, NON_IMPLEMENTED_TODO -> {
                //These types need special handling.
                switch (id) {
                    case SOURCE_TYPE_CRAFTING_ADD_INGREDIENT, SOURCE_TYPE_CRAFTING_REMOVE_INGREDIENT -> {
                        return new SlotChangeAction(player.getCraftingGrid(), this.inventorySlot, this.oldItem, this.newItem);
                    }
                    case SOURCE_TYPE_CONTAINER_DROP_CONTENTS -> {
                        Optional<Inventory> inventory = player.getTopWindow();
                        if (inventory.isEmpty()) {
                            // No window open?
                            return null;
                        }
                        return new SlotChangeAction(inventory.get(), this.inventorySlot, this.oldItem, this.newItem);
                    }
                    case SOURCE_TYPE_CRAFTING_RESULT -> {
                        return new CraftingTakeResultAction(this.oldItem, this.newItem);
                    }
                    case SOURCE_TYPE_CRAFTING_USE_INGREDIENT -> {
                        return new CraftingTransferMaterialAction(this.oldItem, this.newItem, this.inventorySlot);
                    }
                }

                //-13 -10 anvil actions
                if (id >= SOURCE_TYPE_ANVIL_OUTPUT && id <= SOURCE_TYPE_ANVIL_INPUT) {
                    Inventory inv = player.getWindowById(Player.ANVIL_WINDOW_ID);
                    if (inv instanceof AnvilInventory anvil) {
                        return switch (id) {
                            case SOURCE_TYPE_ANVIL_INPUT, SOURCE_TYPE_ANVIL_MATERIAL, SOURCE_TYPE_ANVIL_RESULT ->
                                    new RepairItemAction(this.oldItem, this.newItem, id);
                            default -> new SlotChangeAction(anvil, this.inventorySlot, this.oldItem, this.newItem);
                        };
                    } else if ((inv = player.getWindowById(Player.GRINDSTONE_WINDOW_ID)) instanceof GrindstoneInventory) {
                        return switch (id) {
                            case SOURCE_TYPE_ANVIL_INPUT, SOURCE_TYPE_ANVIL_MATERIAL, SOURCE_TYPE_ANVIL_RESULT ->
                                    new GrindstoneItemAction(this.oldItem, this.newItem, id,
                                            id != SOURCE_TYPE_ANVIL_RESULT ? 0 : ((GrindstoneInventory) inv).getResultExperience()
                                    );
                            default -> new SlotChangeAction(inv, this.inventorySlot, this.oldItem, this.newItem);
                        };
                    } else if (player.getWindowById(Player.SMITHING_WINDOW_ID) instanceof SmithingInventory) {
                        switch (id) {
                            case SOURCE_TYPE_ANVIL_INPUT, SOURCE_TYPE_ANVIL_MATERIAL, SOURCE_TYPE_ANVIL_OUTPUT, SOURCE_TYPE_ANVIL_RESULT -> {
                                return new SmithingItemAction(this.oldItem, this.newItem, this.inventorySlot);
                            }
                        }
                    } else {
                        log.debug("Player {} has no open anvil or grindstone inventory", player.getName());
                        return null;
                    }
                }
                //-17 -15
                else if (id >= SOURCE_TYPE_ENCHANT_OUTPUT && id <= SOURCE_TYPE_ENCHANT_INPUT) {
                    Inventory inv = player.getWindowById(Player.ENCHANT_WINDOW_ID);
                    //verify
                    if (!(inv instanceof EnchantInventory enchant)) {
                        log.debug("Player {} has no open enchant inventory", player.getName());
                        return null;
                    }
                    return switch (id) {
                        case SOURCE_TYPE_ENCHANT_INPUT ->
                                new EnchantingAction(this.oldItem, this.newItem, SOURCE_TYPE_ENCHANT_INPUT);
                        case SOURCE_TYPE_ENCHANT_MATERIAL ->
                                new EnchantingAction(this.newItem, this.oldItem, SOURCE_TYPE_ENCHANT_MATERIAL); // Mojang ish backwards?
                        case SOURCE_TYPE_ENCHANT_OUTPUT ->
                                new EnchantingAction(this.oldItem, this.newItem, SOURCE_TYPE_ENCHANT_OUTPUT);
                        default -> new SlotChangeAction(enchant, this.inventorySlot, this.oldItem, this.newItem);
                    };
                } else if (id == SOURCE_TYPE_BEACON) {
                    Inventory inv = player.getWindowById(Player.BEACON_WINDOW_ID);
                    //verify
                    if (!(inv instanceof BeaconInventory beacon)) {
                        log.debug("Player {} has no open beacon inventory", player.getName());
                        return null;
                    }
                    this.inventorySlot = 0;
                    return new SlotChangeAction(beacon, this.inventorySlot, this.oldItem, this.newItem);
                } else if (id >= SOURCE_TYPE_TRADING_USE_INPUTS && id <= SOURCE_TYPE_TRADING_OUTPUT) {
                    Inventory inv = player.getWindowById(Player.TRADE_WINDOW_ID);
                    //verify
                    if (!(inv instanceof TradeInventory trade)) {
                        log.debug("Player {} has no open trade inventory", player.getName());
                        return null;
                    }
                    return new TradeAction(this.oldItem, this.newItem, id, trade.getHolder());
                }
                //TODO: more stuff
                log.debug("Player {} has no open container with window ID {}", player.getName(), id);
                return null;
            }
            default -> {
                log.debug("Unknown inventory source type {}", type);
                return null;
            }
        }
    }
}
