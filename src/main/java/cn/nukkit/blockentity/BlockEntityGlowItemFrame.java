package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.utils.ItemHelper;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;

public class BlockEntityGlowItemFrame extends BlockEntityItemFrame {

    public BlockEntityGlowItemFrame(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Glow Item Frame";
    }

    public boolean hasName() {
        return namedTag.containsKey("CustomName");
    }

    @Override
    public NbtMap getSpawnCompound() {
        if (!this.namedTag.containsKey("Item")) {
            this.setItem(new ItemBlock(Block.get(BlockID.AIR)), false);
        }
        Item item = getItem();
        NbtMapBuilder tag = super.getSpawnCompound().toBuilder();

        if (!item.isNull()) {
            NbtMapBuilder builder = ItemHelper.write(item,null).toBuilder();
            int networkDamage = item.getDamage();
            String namespacedId = item.getId();
            if (namespacedId != null) {
                builder.remove("id");
                builder.putShort("Damage", (short) networkDamage);
                builder.putString("Name", namespacedId);
            }
            if (item.isBlock()) {
                builder.putCompound("Block", item.getBlockUnsafe().getBlockState().getBlockStateTag());
            }
            tag.putCompound("Item", builder.build())
                    .putByte("ItemRotation", (byte) this.getItemRotation());
        }
        return tag.build();
    }
}
