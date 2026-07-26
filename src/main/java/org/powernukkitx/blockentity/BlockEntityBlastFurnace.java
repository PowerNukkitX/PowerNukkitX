package org.powernukkitx.blockentity;

import org.powernukkitx.block.Block;
import org.powernukkitx.inventory.BlastFurnaceInventory;
import org.powernukkitx.inventory.SmeltingInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.recipe.SmeltingRecipe;


public class BlockEntityBlastFurnace extends BlockEntityFurnace {

    public BlockEntityBlastFurnace(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }


    @Override
    protected String getFurnaceName() {
        return "Blast Furnace";
    }


    @Override
    protected String getClientName() {
        return BLAST_FURNACE;
    }


    @Override
    protected String getIdleBlockId() {
        return Block.BLAST_FURNACE;
    }


    @Override
    protected String getBurningBlockId() {
        return Block.LIT_BLAST_FURNACE;
    }


    @Override
    protected SmeltingInventory createInventory() {
        return new BlastFurnaceInventory(this);
    }

    @Override
    protected SmeltingRecipe matchRecipe(Item raw) {
        return this.server.getRecipeRegistry().findBlastFurnaceRecipe(raw);
    }

    @Override
    protected int getSpeedMultiplier() {
        return 2;
    }
}
