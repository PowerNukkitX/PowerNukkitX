package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.item.Item;
import cn.nukkit.item.MinecraftItemID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntitySquid extends EntityAnimal implements EntitySwimmable {

    

    public EntitySquid(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    public float getWidth() {
        return 0.95f;
    }

    @Override
    public float getHeight() {
        return 0.95f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{MinecraftItemID.INK_SAC.get(1)};
    }


    @Override
    public String getOriginalName() {
        return "Squid";
    }
}
