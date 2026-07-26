package org.powernukkitx.blockentity;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.ItemHelper;

public class BlockEntityGlowItemFrame extends BlockEntityItemFrame {

    public BlockEntityGlowItemFrame(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getName() {
        return this.hasName() ? this.getNbt().getString("CustomName") : "Glow Item Frame";
    }

    public boolean hasName() {
        return nbt.contains("CustomName");
    }

    @Override
    public CompoundTag getSpawnCompound() {
        if (!this.nbt.contains("Item")) {
            this.setItem(new ItemBlock(Block.get(BlockID.AIR)), false);
        }
        Item item = getItem();
        CompoundTag tag = super.getSpawnCompound();

        if (!item.isNull()) {
            CompoundTag builder = ItemHelper.write(item,null);
            int networkDamage = item.getDamage();
            String namespacedId = item.getId();
            if (namespacedId != null) {
                builder.remove("id");
                builder.putShort("Damage", (short) networkDamage);
                builder.putString("Name", namespacedId);
            }
            if (item.isBlock()) {
                builder.putCompound("Block", CompoundTag.fromNetwork(item.getBlockUnsafe().getBlockState().getBlockStateTag()));
            }
            tag.putCompound("Item", builder)
                    .putByte("ItemRotation", (byte) this.getItemRotation());
        }
        return tag;
    }
}
