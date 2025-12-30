package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.CraftingTableInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.SpecialWindowId;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.item.ItemBundle;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
@UtilityClass
public class NetworkMapping {

    public Inventory getInventory(Player player, ContainerSlotType containerSlotType, Integer dynamicId) {
        return switch (containerSlotType) {
            case HORSE_EQUIP -> {
                Entity riding = player.getRiding();
                if (riding instanceof InventoryHolder inventoryHolder) {
                    yield inventoryHolder.getInventory();
                } else {
                    throw new IllegalArgumentException("Can't handle horse inventory: %s when an ItemStackRequest is received!".formatted(containerSlotType.name().toUpperCase(Locale.ENGLISH)));
                }
            }
            case CREATED_OUTPUT -> player.getCreativeOutputInventory();
            case CURSOR -> player.getCursorInventory();
            case INVENTORY, HOTBAR, HOTBAR_AND_INVENTORY -> player.getInventory();
            case ARMOR -> player.getInventory().getArmorInventory();
            case OFFHAND -> player.getOffhandInventory();
            case BEACON_PAYMENT,
                 TRADE2_INGREDIENT_1, TRADE2_INGREDIENT_2, TRADE2_RESULT,
                 LOOM_DYE, LOOM_MATERIAL, LOOM_INPUT, LOOM_RESULT,
                 BARREL, BREWING_RESULT, BREWING_FUEL, BREWING_INPUT,
                 FURNACE_FUEL, FURNACE_INGREDIENT, FURNACE_RESULT, SMOKER_INGREDIENT, BLAST_FURNACE_INGREDIENT,
                 ENCHANTING_INPUT, ENCHANTING_MATERIAL,
                 SMITHING_TABLE_TEMPLATE, SMITHING_TABLE_INPUT, SMITHING_TABLE_MATERIAL, SMITHING_TABLE_RESULT,
                 ANVIL_INPUT, ANVIL_MATERIAL, ANVIL_RESULT,
                 STONECUTTER_INPUT, STONECUTTER_RESULT,
                 GRINDSTONE_ADDITIONAL, GRINDSTONE_INPUT, GRINDSTONE_RESULT,
                 CARTOGRAPHY_INPUT, CARTOGRAPHY_ADDITIONAL, CARTOGRAPHY_RESULT,
                 LEVEL_ENTITY, SHULKER_BOX -> {
                if (player.getFakeInventoryOpen() && player.getTopWindow().isPresent() && player.getTopWindow().get() instanceof FakeInventory) {
                    yield player.getTopWindow().get();
                } else if (player.getEnderChestOpen()) {
                    yield player.getEnderChestInventory();
                } else if (player.getTopWindow().isPresent()) {
                    yield player.getTopWindow().get();
                } else {
                    throw new IllegalArgumentException("Can't handle inventory: %s when an ItemStackRequest is received!".formatted(containerSlotType.name().toUpperCase(Locale.ENGLISH)));
                }
            }
            case CRAFTING_INPUT -> {
                if (player.getFakeInventoryOpen() && player.getTopWindow().isPresent() && player.getTopWindow().get() instanceof FakeInventory) {
                    yield player.getTopWindow().get();
                } else if (player.getTopWindow().isPresent() && player.getTopWindow().get() instanceof CraftingTableInventory) {
                    yield player.getTopWindow().get();
                } else {
                    yield player.getCraftingGrid();
                }
            }
            case DYNAMIC_CONTAINER -> {
                //If player is looking in container. If not, check the players inventory.
                var item = player.getTopWindow().orElse(player.getInventory()).getContents().values().stream().filter(itm -> itm instanceof ItemBundle bundle && bundle.getBundleId() == dynamicId).findFirst();
                if(item.isPresent()) {
                    yield ((ItemBundle) item.get()).getInventory();
                } else {
                    //If player is looking in container, but bundle is not inside the container.
                    item = player.getInventory().getContents().values().stream().filter(itm -> itm instanceof ItemBundle bundle && bundle.getBundleId() == dynamicId).findFirst();
                    if(item.isPresent()) {
                        yield ((ItemBundle) item.get()).getInventory();
                    } else yield ((ItemBundle) player.getCursorInventory().getUnclonedItem()).getInventory();
                }
            }
            default -> {
                throw new IllegalArgumentException("Can't handle containerSlotType: %s when an ItemStackRequest is received!".formatted(containerSlotType.name().toUpperCase(Locale.ENGLISH)));
            }
        };
    }
}
