package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
public class EntityRabbit extends EntityAnimal implements EntityWalkable {

    

    public EntityRabbit(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.268f;
        }
        return 0.67f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.268f;
        }
        return 0.67f;
    }


    @Override
    public String getOriginalName() {
        return "Rabbit";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(((this.isOnFire()) ? Item.COOKED_RABBIT : Item.RAW_RABBIT)), Item.get(Item.RABBIT_HIDE), Item.get(Item.RABBIT_FOOT)};
    }

    

    @Override
    protected void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();
    }
}
