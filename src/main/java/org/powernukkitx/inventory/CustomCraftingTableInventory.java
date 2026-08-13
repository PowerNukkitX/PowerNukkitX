package org.powernukkitx.inventory;

import com.google.common.collect.BiMap;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.packet.ContainerOpenPacket;
import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.recipe.Input;

import java.util.Map;


/**
 * Default inventory implementation for custom blocks using the
 * {@code minecraft:crafting_table} component.
 * <p>
 * Provides a standard 3x3 crafting grid and maps its slots to the Bedrock
 * workbench container so custom crafting tables can use normal crafting
 * recipe processing.
 *
 * @author Curse
 */
public class CustomCraftingTableInventory extends BaseInventory implements CraftTypeInventory, InputInventory {

    private final Block block;

    private CustomCraftingTableInventory(BlockInventoryHolderAdapter holder) {
        super(holder, ContainerType.WORKBENCH, 9);
        this.block = holder.block;
        holder.inventory = this;
    }

    /**
     * Creates a crafting table inventory for the specified block.
     *
     * @param block the block providing the crafting table
     * @return a new custom crafting table inventory
     */
    public static CustomCraftingTableInventory create(Block block) {
        return new CustomCraftingTableInventory(
                new BlockInventoryHolderAdapter(block)
        );
    }

    /**
     * Initializes the network slot and container mappings used by the
     * Bedrock workbench interface.
     */
    @Override
    public void init() {
        BiMap<Integer, Integer> map = super.networkSlotMap();
        Map<Integer, ContainerEnumName> map2 = super.slotTypeMap();

        for (int i = 0; i < getSize(); i++) {
            map.put(i, 32 + i);
            map2.put(i, ContainerEnumName.CRAFTING_INPUT_CONTAINER);
            map2.put(i + 32, ContainerEnumName.CRAFTING_INPUT_CONTAINER);
        }
    }

    /**
     * Opens the workbench container for the specified player.
     *
     * @param player the player opening the inventory
     */
    @Override
    public void onOpen(Player player) {
        super.onOpen(player);

        ContainerOpenPacket packet = new ContainerOpenPacket();
        packet.setContainerID((byte) player.getWindowId(this));
        packet.setContainerType(getType());
        packet.setPosition(Vector3i.from(block.getFloorX(), block.getFloorY(), block.getFloorZ()));

        player.sendPacket(packet);
        sendContents(player);
    }

    /**
     * Returns the block associated with this inventory.
     *
     * @return the crafting table block
     */
    public Block getBlock() {
        return block;
    }

    /**
     * Returns the current 3x3 crafting grid as recipe input.
     *
     * @return the crafting input
     */
    @Override
    public Input getInput() {
        return new Input(3, 3, new Item[][]{
                {getItem(0), getItem(1), getItem(2)},
                {getItem(3), getItem(4), getItem(5)},
                {getItem(6), getItem(7), getItem(8)}
        });
    }

    /**
     * Adapter allowing a block to act as an {@link InventoryHolder} for the
     * crafting table inventory {@link InventoryHolder}.
     */
    private static final class BlockInventoryHolderAdapter implements InventoryHolder {

        private final Block block;
        private Inventory inventory;

        private BlockInventoryHolderAdapter(Block block) {
            this.block = block;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }

        @Override
        public Level getLevel() {
            return block.getLevel();
        }

        @Override
        public double getX() {
            return block.getX();
        }

        @Override
        public double getY() {
            return block.getY();
        }

        @Override
        public double getZ() {
            return block.getZ();
        }

        @Override
        public Vector3 getVector3() {
            return block.getVector3();
        }
    }
}
