package org.powernukkitx.blockentity;

import org.powernukkitx.block.Block;
import org.powernukkitx.inventory.SmeltingInventory;
import org.powernukkitx.inventory.SmokerInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.recipe.SmeltingRecipe;


public class BlockEntitySmoker extends BlockEntityFurnace {

    public BlockEntitySmoker(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected String getFurnaceName() {
        return "Smoker";
    }

    @Override
    protected String getClientName() {
        return SMOKER;
    }

    @Override
    protected String getIdleBlockId() {
        return Block.SMOKER;
    }

    @Override
    protected String getBurningBlockId() {
        return Block.LIT_SMOKER;
    }

    @Override
    protected SmeltingInventory createInventory() {
        return new SmokerInventory(this);
    }

    @Override
    protected SmeltingRecipe matchRecipe(Item raw) {
        return this.server.getRecipeRegistry().findSmokerRecipe(raw);
    }

    @Override
    protected int getSpeedMultiplier() {
        return 2;
    }
}
