package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityCreature;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author Pub4Game
 * @since 21.06.2016
 */

public class EntityVillager extends EntityCreature implements IEntityNPC {
    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return VILLAGER;
    }
    public static final int $1 = 0;
    public static final int $2 = 1;
    public static final int $3 = 2;
    public static final int $4 = 3;
    public static final int $5 = 4;
    public static final int $6 = 5;
    /**
     * @deprecated 
     */
    
    

    public EntityVillager(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        if (this.isBaby()) {
            return 0.3f;
        }
        return 0.6f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        if (this.isBaby()) {
            return 0.95f;
        }
        return 1.9f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Villager";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();

        if (!this.namedTag.contains("Profession")) {
            this.setProfession(PROFESSION_GENERIC);
        }
    }
    /**
     * @deprecated 
     */
    

    public int getProfession() {
        return this.namedTag.getInt("Profession");
    }
    /**
     * @deprecated 
     */
    

    public void setProfession(int profession) {
        this.namedTag.putInt("Profession", profession);
    }
}
