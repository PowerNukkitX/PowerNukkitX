package org.powernukkitx.item;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.passive.EntitySulfurCube;
import org.powernukkitx.level.Position;

public class ItemSulfurCubeBucket extends ItemBucket {
    public ItemSulfurCubeBucket() {
        super(SULFUR_CUBE_BUCKET);
    }

    @Override
    public void setDamage(int meta) {

    }

    @Override
    public void spawnFishEntity(Position spawnPos) {
        Entity entity = Entity.createEntity(EntityID.SULFUR_CUBE, spawnPos);
        if (!(entity instanceof EntitySulfurCube cube)) {
            return;
        }

        cube.setVariant(EntitySulfurCube.SIZE_LARGE);
        cube.setFromBucket(true);
        if (this.getNbt() != null && this.getNbt().contains(EntitySulfurCube.TAG_ABSORBED_BLOCK)) {
            cube.setAbsorbedBlock(this.getNbt().getString(EntitySulfurCube.TAG_ABSORBED_BLOCK));
        }
        cube.spawnToAll();
    }
}
