package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
public class EntityOcelot extends EntityAnimal implements EntityWalkable {
    @Override
    @NotNull public String getIdentifier() {
        return OCELOT;
    }
    

    public EntityOcelot(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.3f;
        }
        return 0.6f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.35f;
        }
        return 0.7f;
    }

    @Override
    public String getOriginalName() {
        return "Ocelot";
    }

    

    @Override
    public void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();
    }

    @Override
    public boolean isBreedingItem(Item item) {
        return item.getId() == Item.COD;
    }
}
