package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityCreature;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class EntityTraderLlama extends EntityCreature implements IEntityNPC {
    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return TRADER_LLAMA;
    }
    /**
     * @deprecated 
     */
    

    public EntityTraderLlama(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 0.6f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 1.9f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Wandering Trader";
    }

    

    @Override
    /**
     * @deprecated 
     */
    
    public void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
    }
}
