package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.inventory.SmeltingInventory;
import cn.nukkit.inventory.SmokerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.recipe.SmeltingRecipe;


public class BlockEntitySmoker extends BlockEntityFurnace {
    /**
     * @deprecated 
     */
    

    public BlockEntitySmoker(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected String getFurnaceName() {
        return "Smoker";
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected String getClientName() {
        return SMOKER;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected String getIdleBlockId() {
        return Block.SMOKER;
    }

    @Override
    
    /**
     * @deprecated 
     */
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
    
    /**
     * @deprecated 
     */
    protected int getSpeedMultiplier() {
        return 2;
    }
}
