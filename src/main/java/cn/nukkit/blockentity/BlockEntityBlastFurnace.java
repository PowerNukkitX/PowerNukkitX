package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.inventory.BlastFurnaceInventory;
import cn.nukkit.inventory.SmeltingInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.recipe.SmeltingRecipe;


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
