package cn.nukkit.utils;

import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.item.Item;
import cn.nukkit.item.UnknownItem;
import cn.nukkit.level.updater.block.BlockStateUpdaters;
import cn.nukkit.level.updater.item.ItemUpdaters;
import cn.nukkit.network.NetworkConstants;
import cn.nukkit.registry.Registries;
import lombok.experimental.UtilityClass;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.ItemDescriptor;

import java.util.TreeMap;

/**
 * @author Kaooot
 */
@UtilityClass
public class ItemHelper {

    public NbtMap write(Item item) {
        return write(item, null);
    }

    public NbtMap write(Item item, Integer slot) {
        NbtMap tag = NbtMap.builder()
                .putByte("Count", (byte) item.getCount())
                .putShort("Damage", (short) item.getDamage())
                .putString("Name", item.getId())
                .build();
        if (slot != null) {
            tag = tag.toBuilder().putByte("Slot", slot.byteValue()).build();
        }
        if (item.hasCompoundTag()) {
            tag = tag.toBuilder().putCompound("tag", item.getNamedTag()).build();
        }
        if (item.isBlock() && item.getBlockId().equals(item.getId())) {
            tag = tag.toBuilder().putCompound("Block", item.getBlockUnsafe().getBlockState().getBlockStateTag()).build();
        }
        tag = tag.toBuilder().putInt("version", NetworkConstants.BLOCK_STATE_VERSION_NO_REVISION).build();
        return tag;
    }

    public Item read(NbtMap tag) {
        String name = tag.getString("Name");
        if (name == null || name.isBlank() || name.equals(BlockID.AIR)) {
            return Item.AIR;
        }
        if (!tag.containsKey("Count")) {
            return Item.AIR;
        }

        //upgrade item
        if (tag.containsKey("version")) {
            int ver = tag.getInt("version");
            if (ver < NetworkConstants.BLOCK_STATE_VERSION_NO_REVISION) {
                tag = ItemUpdaters.updateItem(tag, NetworkConstants.BLOCK_STATE_VERSION_NO_REVISION);
                name = tag.getString("Name");
            }
        }

        int damage = !tag.containsKey("Damage") ? 0 : tag.getShort("Damage");
        int amount = tag.getByte("Count");
        Item item = Item.get(name, damage, amount);
        Object tagTag = tag.get("tag");
        if (!item.isNull() && tagTag instanceof NbtMap compoundTag && !compoundTag.isEmpty()) {
            item.setNamedTag(compoundTag);
        }

        if (tag.containsKey("Block")) {
            NbtMap block = tag.getCompound("Block");
            boolean isUnknownBlock = block.getString("name").equals(BlockID.UNKNOWN) && block.containsKey("Block");
            if (isUnknownBlock) {
                block = block.getCompound("Block");//originBlock
            }
            //upgrade block
            if (block.containsKey("version")) {
                int ver = block.getInt("version");
                if (ver < NetworkConstants.BLOCK_STATE_VERSION_NO_REVISION) {
                    block = BlockStateUpdaters.updateBlockState(block, NetworkConstants.BLOCK_STATE_VERSION_NO_REVISION);
                }
            }
            BlockState blockState = getBlockStateHelper(block);
            if (blockState != null) {
                if (isUnknownBlock) {//restore unknown block item
                    item = blockState.toItem();
                    if (damage != 0) {
                        item.setDamage(damage);
                    }
                    item.setCount(amount);
                }
                item.setBlockUnsafe(blockState.toBlock());
            } else if (item.isNull()) {//write unknown block item
                item = new UnknownItem(BlockID.UNKNOWN, damage, amount);
                NbtMap compoundTag = NbtMap.builder()
                        .putString("name", block.getString("name"))
                        .putCompound("states", NbtMap.fromMap(new TreeMap<>(block.getCompound("states"))))
                        .build();
                int hash = HashUtils.fnv1a_32_nbt(compoundTag);
                compoundTag = compoundTag.toBuilder().putInt("version", block.getInt("version")).build();
                BlockState unknownBlockState = BlockState.makeUnknownBlockState(hash, compoundTag);
                item.setBlockUnsafe(new BlockUnknown(unknownBlockState));
            }
        } else {
            if (item.isNull()) {//write unknown item
                item = new UnknownItem(BlockID.UNKNOWN, damage, amount);
                item.setNamedTag(item.getOrCreateNamedTag().toBuilder()
                        .putCompound("Item", NbtMap.builder()
                                .putString("Name", name)
                                .build())
                        .build());
            } else if (item.getId().equals(BlockID.UNKNOWN) && item.getOrCreateNamedTag().containsKey("Item")) {//restore unknown item
                NbtMap removeTag = (NbtMap) item.getNamedTag().remove("Item");
                String originItemName = removeTag.getString("Name");
                Item originItem = Item.get(originItemName, damage, amount);
                originItem.setNamedTag(item.getNamedTag());
                item = originItem;
            }
        }
        return item;
    }

    public BlockState getBlockStateHelper(NbtMap tag) {
        return Registries.BLOCKSTATE.get(HashUtils.fnv1a_32_nbt_palette(tag));
    }
}