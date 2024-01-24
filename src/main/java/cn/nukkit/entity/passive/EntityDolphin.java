package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PetteriM1
 */
public class EntityDolphin extends EntityAnimal implements EntitySwimmable {
    @Override
    @NotNull public String getIdentifier() {
        return DOLPHIN;
    }
    

    public EntityDolphin(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    


    @Override
    public String getOriginalName() {
        return "Dolphin";
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 0.6f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.COD)};
    }
}
