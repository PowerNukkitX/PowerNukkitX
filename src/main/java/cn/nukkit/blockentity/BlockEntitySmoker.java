package cn.nukkit.blockentity;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.Block;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.inventory.SmeltingRecipe;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@PowerNukkitOnly
public class BlockEntitySmoker extends BlockEntityFurnace {
    @PowerNukkitOnly
    public BlockEntitySmoker(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @PowerNukkitOnly
    @Override
    protected String getFurnaceName() {
        return "Smoker";
    }

    @PowerNukkitOnly
    @Override
    protected String getClientName() {
        return SMOKER;
    }

    @PowerNukkitOnly
    @Override
    protected int getIdleBlockId() {
        return Block.SMOKER;
    }

    @PowerNukkitOnly
    @Override
    protected int getBurningBlockId() {
        return Block.LIT_SMOKER;
    }

    @PowerNukkitOnly
    @Override
    protected InventoryType getInventoryType() {
        return InventoryType.SMOKER;
    }

    @PowerNukkitOnly
    @Override
    protected SmeltingRecipe matchRecipe(Item raw) {
        return this.server.getCraftingManager().matchSmokerRecipe(raw);
    }

    @PowerNukkitOnly
    @Override
    protected int getSpeedMultiplier() {
        return 2;
    }
}
