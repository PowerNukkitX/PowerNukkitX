package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityEnderPearl;

public class ItemEnderPearl extends ProjectileItem {
    /**
     * @deprecated 
     */
    

    public ItemEnderPearl() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemEnderPearl(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemEnderPearl(Integer meta, int count) {
        super(ENDER_PEARL, 0, count, "Ender Pearl");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getProjectileEntityType() {
        return ENDER_PEARL;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getThrowForce() {
        return 1.5f;
    }

    @Override
    protected Entity correctProjectile(Player player, Entity projectile) {
        if (projectile instanceof EntityEnderPearl) {
            if (!player.isItemCoolDownEnd(this.getIdentifier())) {
                projectile.kill();
                return null;
            }
            player.setItemCoolDown(20, this.getIdentifier());
            return projectile;
        }
        return null;
    }
}
