package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityEnchantTable;
import cn.nukkit.blockentity.BlockEntityNameable;
import cn.nukkit.event.player.PlayerEnchantOptionsRequestEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.EnchantmentHelper;
import cn.nukkit.network.protocol.PlayerEnchantOptionsPacket;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import com.google.common.collect.BiMap;

import java.util.List;
import java.util.Map;


/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantInventory extends ContainerInventory implements BlockEntityInventoryNameable, CraftTypeInventory, SoleInventory {
    public EnchantInventory(BlockEntityEnchantTable table) {
        super(table, InventoryType.ENCHANTMENT, 2);
    }

    @Override
    public BiMap<Integer, Integer> networkSlotMap() {
        BiMap<Integer, Integer> map = super.networkSlotMap();
        map.put(0, 14);//INPUT
        map.put(1, 15);//MATERIAL
        return map;
    }

    @Override
    public Map<Integer, ContainerSlotType> slotTypeMap() {
        Map<Integer, ContainerSlotType> map = super.slotTypeMap();
        map.put(0, ContainerSlotType.ENCHANTING_INPUT);
        map.put(1, ContainerSlotType.ENCHANTING_MATERIAL);
        return map;
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        if (index == 0) {
            if (before.isNull()) {
                for (final Player viewer : this.getViewers()) {
                    List<PlayerEnchantOptionsPacket.EnchantOptionData> options = EnchantmentHelper.getEnchantOptions(this.getHolder(), this.getFirst(), viewer.getEnchantmentSeed());
                    PlayerEnchantOptionsRequestEvent event = new PlayerEnchantOptionsRequestEvent(viewer, this, options);
                    if (!event.isCancelled() && !event.getOptions().isEmpty()) {
                        PlayerEnchantOptionsPacket pk = new PlayerEnchantOptionsPacket();
                        pk.options = event.getOptions();
                        viewer.dataPacket(pk);
                    }
                }
            } else {
                for (final Player viewer : this.getViewers()) {
                    PlayerEnchantOptionsPacket pk = new PlayerEnchantOptionsPacket();
                    viewer.dataPacket(pk);
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
