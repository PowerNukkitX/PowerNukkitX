package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityEnchantTable;
import cn.nukkit.blockentity.BlockEntityNameable;
import cn.nukkit.event.player.PlayerEnchantOptionsRequestEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.EnchantmentHelper;
import com.google.common.collect.BiMap;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemEnchantOption;
import org.cloudburstmc.protocol.bedrock.packet.PlayerEnchantOptionsPacket;

import java.util.List;
import java.util.Map;


/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantInventory extends ContainerInventory implements BlockEntityInventoryNameable, CraftTypeInventory, SoleInventory {
    public EnchantInventory(BlockEntityEnchantTable table) {
        super(table, ContainerType.ENCHANTMENT, 2);
    }

    @Override
    public BiMap<Integer, Integer> networkSlotMap() {
        BiMap<Integer, Integer> map = super.networkSlotMap();
        map.put(0, 14);//INPUT
        map.put(1, 15);//MATERIAL
        return map;
    }

    @Override
    public Map<Integer, ContainerEnumName> slotTypeMap() {
        Map<Integer, ContainerEnumName> map = super.slotTypeMap();
        map.put(0, ContainerEnumName.ENCHANTING_INPUT_CONTAINER);
        map.put(1, ContainerEnumName.ENCHANTING_MATERIAL_CONTAINER);
        return map;
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        if (index == 0) {
            if (before.isNull()) {
                for (final Player viewer : this.getViewers()) {
                    List<ItemEnchantOption> options = EnchantmentHelper.getEnchantOptions(this.getHolder(), this.getFirst(), viewer.getEnchantmentSeed());

                    PlayerEnchantOptionsRequestEvent event = new PlayerEnchantOptionsRequestEvent(viewer, this, options);
                    if (!event.isCancelled() && !event.getOptions().isEmpty()) {
                        final PlayerEnchantOptionsPacket pk = new PlayerEnchantOptionsPacket();
                        pk.getOptions().addAll(event.getOptions());
                        viewer.sendPacket(pk);
                    }
                }
            } else {
                for (final Player viewer : this.getViewers()) {
                    PlayerEnchantOptionsPacket pk = new PlayerEnchantOptionsPacket();
                    viewer.sendPacket(pk);
                }
            }
        }
        super.onSlotChange(index, before, false);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);
        Item[] drops = new Item[]{getItem(0), getItem(1)};
        drops = who.getInventory().addItem(drops);
        for (Item drop : drops) {
            if (!who.dropItem(drop)) {
                this.getHolder().getLevel().dropItem(this.getHolder().getVector3().add(0.5, 0.5, 0.5), drop);
            }
        }

        clear(0);
        clear(1);
    }

    public Item getFirst() {
        return this.getItem(0);
    }

    public Item getSecond() {
        return this.getItem(1);
    }

    @Override
    public BlockEntityEnchantTable getHolder() {
        return (BlockEntityEnchantTable) super.getHolder();
    }

    @Override
    public BlockEntityNameable getBlockEntityInventoryHolder() {
        return getHolder();
    }
}
