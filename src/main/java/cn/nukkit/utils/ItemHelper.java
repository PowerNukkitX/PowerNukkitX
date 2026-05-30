package cn.nukkit.utils;

import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.item.Item;
import cn.nukkit.item.UnknownItem;
import cn.nukkit.level.updater.block.BlockStateUpdaters;
import cn.nukkit.level.updater.item.ItemUpdaters;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.NetworkConstants;
import cn.nukkit.registry.Registries;
import lombok.experimental.UtilityClass;
import org.cloudburstmc.nbt.NbtMap;

import java.util.TreeMap;

/**
 * @author Kaooot
 */
@UtilityClass
public class ItemHelper {

    public CompoundTag write(Item item) {
        return write(item, null);
    }

    public CompoundTag write(Item item, Integer slot) {
        CompoundTag tag = new CompoundTag()
                .putByte("Count", item.getCount())
                .putShort("Damage", item.getDamage())
                .putString("Name", item.getId());
        if (slot != null) {
            tag.putByte("Slot", slot);
        }
        if (item.hasNbt()) {
            tag.putCompound("tag", item.getNbt());
        }
        if (item.isBlock() && item.getBlockId().equals(item.getId())) {
            tag.putCompound("Block", CompoundTag.fromNetwork(item.getBlockUnsafe().getBlockState().getBlockStateTag()));
        }
        tag.putInt("version", NetworkConstants.BLOCK_STATE_VERSION_NO_REVISION);
        return tag;
    }

    public Item read(CompoundTag tag) {
        String name = tag.getString("Name");
        if (name == null || name.isBlank() || name.equals(BlockID.AIR)) {
            return Item.AIR;
        }
        if (!tag.contains("Count")) {
            return Item.AIR;
        }

        //upgrade item
        if (tag.contains("version")) {
            int ver = tag.getInt("version");
            if (ver < NetworkConstants.BLOCK_STATE_VERSION_NO_REVISION) {
                tag = CompoundTag.fromNetwork(ItemUpdaters.updateItem(tag.toNetwork(), NetworkConstants.BLOCK_STATE_VERSION_NO_REVISION));
                name = tag.getString("Name");
            }
        }

        int damage = !tag.contains("Damage") ? 0 : tag.getShort("Damage");
        int amount = tag.getByte("Count");
        Item item = Item.get(name, damage, amount);
        Tag tagTag = tag.get("tag");
        if (!item.isNull() && tagTag instanceof CompoundTag compoundTag && !compoundTag.isEmpty()) {
            item.setNbt(compoundTag);
        }

        if (tag.contains("Block")) {
            CompoundTag block = tag.getCompound("Block");
            boolean isUnknownBlock = block.getString("name").equals(BlockID.UNKNOWN) && block.contains("Block");
            if (isUnknownBlock) {
                block = block.getCompound("Block");//originBlock
            }
            //upgrade block
            if (block.contains("version")) {
                int ver = block.getInt("version");
                if (ver < NetworkConstants.BLOCK_STATE_VERSION_NO_REVISION) {
                    block = CompoundTag.fromNetwork(BlockStateUpdaters.updateBlockState(block.toNetwork(), NetworkConstants.BLOCK_STATE_VERSION_NO_REVISION));
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
                        .putCompound("states", NbtMap.fromMap(new TreeMap<>(block.getCompound("states").toNetwork())))
                        .build();
                int hash = HashUtils.fnv1a_32_nbt(compoundTag);
                compoundTag = compoundTag.toBuilder().putInt("version", block.getInt("version")).build();
                BlockState unknownBlockState = BlockState.makeUnknownBlockState(hash, compoundTag);
                item.setBlockUnsafe(new BlockUnknown(unknownBlockState));
            }
        } else {
            if (item.isNull()) {//write unknown item
                item = new UnknownItem(BlockID.UNKNOWN, damage, amount);
                item.getOrCreateNbt()
                        .putCompound("Item", new CompoundTag()
                                .putString("Name", name));
            } else if (item.getId().equals(BlockID.UNKNOWN) && item.getOrCreateNbt().contains("Item")) {//restore unknown item
                CompoundTag removeTag = item.getNbt().removeAndGet("Item");
                String originItemName = removeTag.getString("Name");
                Item originItem = Item.get(originItemName, damage, amount);
                originItem.setNbt(item.getNbt());
                item = originItem;
            }
        }
        return item;
    }

    public BlockState getBlockStateHelper(CompoundTag tag) {
        return getBlockStateHelper(tag.toNetwork());
    }

    public BlockState getBlockStateHelper(NbtMap tag) {
        return Registries.BLOCKSTATE.get(HashUtils.fnv1a_32_nbt_palette(tag));
    }
}
