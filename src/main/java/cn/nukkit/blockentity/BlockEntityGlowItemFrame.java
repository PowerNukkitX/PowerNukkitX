package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityGlowItemFrame extends BlockEntityItemFrame {

    public BlockEntityGlowItemFrame(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getName() {
        return "Glow Item Frame";
    }

    @Override
    public CompoundTag getSpawnCompound() {
        if (!this.namedTag.contains("Item")) {
            this.setItem(new ItemBlock(Block.get(BlockID.AIR)), false);
        }
        Item item = getItem();
        CompoundTag tag = new CompoundTag()
                .putString("id", BlockEntity.GLOW_ITEM_FRAME)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        if (!item.isNull()) {
            CompoundTag itemTag = NBTIO.putItemHelper(item);
            int networkDamage = item.getDamage();
            String namespacedId = item.getNamespaceId();
            if (namespacedId != null) {
                itemTag.remove("id");
                itemTag.putShort("Damage", networkDamage);
                itemTag.putString("Name", namespacedId);
            }
            if (item instanceof ItemBlock itemBlock) {
                itemTag.putCompound("Block", NBTIO.putBlockHelper(itemBlock.getBlock()));
            }
            tag.putCompound("Item", itemTag)
                    .putByte("ItemRotation", this.getItemRotation());
        }
        return tag;
    }
}
