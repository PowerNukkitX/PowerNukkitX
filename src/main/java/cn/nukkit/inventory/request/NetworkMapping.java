package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.CraftingTableInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.item.ItemBundle;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;

import java.util.Locale;

@Slf4j
@UtilityClass
public class NetworkMapping {

    public Inventory getInventory(Player player, ContainerEnumName containerSlotType, Integer dynamicId) {
        return switch (containerSlotType) {
            case HORSE_EQUIP_CONTAINER -> {
                if (player.getTopWindow().isPresent()) {
                    Inventory top = player.getTopWindow().get();
                    if (top.getType() == ContainerType.HORSE) {
                        yield top;
                    }
                }

                Entity riding = player.getRiding();
                if (riding instanceof InventoryHolder inventoryHolder) {
                    yield inventoryHolder.getInventory();
                }

                throw new IllegalArgumentException("Can't handle horse inventory: %s when an ItemStackRequest is received!".formatted(containerSlotType.name().toUpperCase(Locale.ENGLISH)));
            }
            case CREATED_OUTPUT_CONTAINER -> player.getCreativeOutputInventory();
            case CURSOR_CONTAINER -> player.getCursorInventory();
            case INVENTORY_CONTAINER, HOTBAR_CONTAINER, COMBINED_HOTBAR_AND_INVENTORY_CONTAINER ->
                    player.getInventory();
            case ARMOR_CONTAINER -> player.getInventory().getArmorInventory();
            case OFFHAND_CONTAINER -> player.getOffhandInventory();
            case BEACON_PAYMENT_CONTAINER,
                 TRADE2_INGREDIENT1_CONTAINER, TRADE2_INGREDIENT2_CONTAINER, TRADE2_RESULT_PREVIEW_CONTAINER,
                 LOOM_DYE_CONTAINER, LOOM_MATERIAL_CONTAINER, LOOM_INPUT_CONTAINER, LOOM_RESULT_PREVIEW_CONTAINER,
                 BARREL_CONTAINER, BREWING_STAND_RESULT_CONTAINER, BREWING_STAND_FUEL_CONTAINER,
                 BREWING_STAND_INPUT_CONTAINER,
                 FURNACE_FUEL_CONTAINER, FURNACE_INGREDIENT_CONTAINER, FURNACE_RESULT_CONTAINER,
                 SMOKER_INGREDIENT_CONTAINER, BLAST_FURNACE_INGREDIENT_CONTAINER,
                 ENCHANTING_INPUT_CONTAINER, ENCHANTING_MATERIAL_CONTAINER,
                 SMITHING_TABLE_TEMPLATE_CONTAINER, SMITHING_TABLE_INPUT_CONTAINER, SMITHING_TABLE_MATERIAL_CONTAINER,
                 SMITHING_TABLE_RESULT_PREVIEW_CONTAINER,
                 ANVIL_INPUT_CONTAINER, ANVIL_MATERIAL_CONTAINER, ANVIL_RESULT_PREVIEW_CONTAINER,
                 STONECUTTER_INPUT_CONTAINER, STONECUTTER_RESULT_PREVIEW_CONTAINER,
                 GRINDSTONE_ADDITIONAL_CONTAINER, GRINDSTONE_INPUT_CONTAINER, GRINDSTONE_RESULT_PREVIEW_CONTAINER,
                 CARTOGRAPHY_INPUT_CONTAINER, CARTOGRAPHY_ADDITIONAL_CONTAINER, CARTOGRAPHY_RESULT_PREVIEW_CONTAINER,
                 LEVEL_ENTITY_CONTAINER, SHULKER_BOX_CONTAINER -> {
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
            case CRAFTING_INPUT_CONTAINER -> {
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
                if (item.isPresent()) {
                    yield ((ItemBundle) item.get()).getInventory();
                } else {
                    //If player is looking in container, but bundle is not inside the container.
                    item = player.getInventory().getContents().values().stream().filter(itm -> itm instanceof ItemBundle bundle && bundle.getBundleId() == dynamicId).findFirst();
                    if (item.isPresent()) {
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
