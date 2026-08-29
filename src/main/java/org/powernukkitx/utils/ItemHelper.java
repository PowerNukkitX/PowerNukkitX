package org.powernukkitx.utils;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockUnknown;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemUnknown;
import org.powernukkitx.level.updater.block.BlockStateUpdaters;
import org.powernukkitx.level.updater.item.ItemUpdaters;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.network.NetworkConstants;
import org.powernukkitx.registry.Registries;
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

        if (tag.contains("version")) {
            int ver = tag.getInt("version");
            if (ver < NetworkConstants.BLOCK_STATE_VERSION_NO_REVISION) {
                tag = ItemUpdaters.updateItem(tag, NetworkConstants.BLOCK_STATE_VERSION_NO_REVISION);
                name = tag.getString("Name");
            }
        }

        int damage = !tag.contains("Damage") ? 0 : tag.getShort("Damage");
        int amount = tag.getByte("Count");
        Item item = Item.get(name, damage, amount);
        if (item == Item.AIR) {
            item = new ItemUnknown(name, damage, amount, null);
        }
        Tag tagTag = tag.get("tag");
        if (tagTag instanceof CompoundTag compoundTag && !compoundTag.isEmpty()) {
            item.setNbt(compoundTag);
        }

        if (tag.contains("Block")) {
            CompoundTag block = tag.getCompound("Block");
            boolean isUnknownBlock = block.getString("name").equals(BlockID.UNKNOWN) && block.contains("Block");
            if (isUnknownBlock) {
                block = block.getCompound("Block");
            }
            if (block.contains("version")) {
                int ver = block.getInt("version");
                if (ver < NetworkConstants.BLOCK_STATE_VERSION_NO_REVISION) {
                    block = CompoundTag.fromNetwork(BlockStateUpdaters.updateBlockState(block.toNetwork(), NetworkConstants.BLOCK_STATE_VERSION_NO_REVISION));
                }
            }
            BlockState blockState = getBlockStateHelper(block);

            boolean wasUnknownItem = item instanceof ItemUnknown;

            if (blockState != null) {
                if (isUnknownBlock || wasUnknownItem) {
                    Item resolvedItem = wasUnknownItem
                        ? Item.get(item.getId(), damage, amount)
                        : blockState.toItem();

                    item = resolvedItem != Item.AIR ? resolvedItem : blockState.toItem();

                    if (damage != 0) {
                        item.setDamage(damage);
                    }
                    item.setCount(amount);
                }
                item.setBlockUnsafe(blockState.toBlock());
            } else if (item.isNull() || wasUnknownItem) {
                item = new ItemUnknown(BlockID.UNKNOWN, damage, amount, null);
                NbtMap compoundTag = NbtMap.builder()
                        .putString("name", block.getString("name"))
                        .putCompound("states", NbtMap.fromMap(new TreeMap<>(block.getCompound("states").toNetwork())))
                        .build();
                int hash = HashUtils.fnv1a_32_nbt(compoundTag);
                compoundTag = compoundTag.toBuilder().putInt("version", block.getInt("version")).build();
                BlockState unknownBlockState = BlockState.makeUnknownBlockState(hash, compoundTag);
                item.setBlockUnsafe(new BlockUnknown(unknownBlockState));
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
