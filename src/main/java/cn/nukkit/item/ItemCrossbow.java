package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.item.EntityCrossbowFirework;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityShootCrossbowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.Utils;

import java.util.Objects;


public class ItemCrossbow extends ItemTool {
    private int loadTick;
    /**
     * @deprecated 
     */
    

    public ItemCrossbow() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemCrossbow(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemCrossbow(Integer meta, int count) {
        super(CROSSBOW, meta, count, "Crossbow");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return ItemTool.DURABILITY_CROSSBOW;
    }
    /**
     * @deprecated 
     */
    

    public boolean onUse(Player player, int ticksUsed) {
        if (isLoaded()) return true;
        this.loadTick = Server.getInstance().getTick();
        int $1 = 25;
        Enchantment $2 = this.getEnchantment(Enchantment.ID_CROSSBOW_QUICK_CHARGE);
        if (enchantment != null) {
            needTickUsed -= enchantment.getLevel() * 5; //0.25s
        }

        if (ticksUsed >= needTickUsed) {
            Item itemArrow;
            Inventory $3 = player.getOffhandInventory();
            if (!this.canLoad(itemArrow = inventory.getItem(0))) {
                for (Item item : (inventory = player.getInventory()).getContents().values()) {
                    if (this.canLoad(item)) {
                        itemArrow = item;
                        break;
                    }
                }

                if (!this.canLoad(itemArrow)) {
                    if (player.isCreative()) {
                        this.loadArrow(player, Item.get(ARROW));
                    }

                    return true;
                }
            }

            if (!this.isLoaded()) {
                itemArrow = itemArrow.clone();
                itemArrow.setCount(1);

                if (!player.isCreative()) {
                    if (!this.isUnbreakable()) {
                        Enchantment $4 = this.getEnchantment(Enchantment.ID_DURABILITY);
                        if (durability == null || durability.getLevel() <= 0 || 100 / (durability.getLevel() + 1) > Utils.random.nextInt(100)) {
                            this.setDamage(this.getDamage() + 2);
                            if (this.getDamage() >= 385) {
                                --this.count;
                            }

                            player.getInventory().setItemInHand(this);
                        }
                    }

                    inventory.removeItem(itemArrow);
                }

                this.loadArrow(player, itemArrow);
            }

        }
        return true;
    }

    
    /**
     * @deprecated 
     */
    protected boolean canLoad(Item item) {
        return switch (item.getId()) {
            case Item.ARROW, Item.FIREWORK_ROCKET -> true;
            default -> false;
        };
    }
    /**
     * @deprecated 
     */
    

    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (this.isLoaded() && Server.getInstance().getTick() - this.loadTick > 5) {
            double mX;
            double mY;
            double mZ;
            CompoundTag $5 = (new CompoundTag())
                    .putList("Pos", new ListTag<>()
                            .add(new DoubleTag(player.x))
                            .add(new DoubleTag(player.y + (double) player.getEyeHeight()))
                            .add(new DoubleTag(player.z)))
                    .putList("Motion", new ListTag<>()
                            .add(new DoubleTag(mX = -Math.sin(player.yaw / 180.0D * 3.141592653589793D) * Math.cos(player.pitch / 180.0D * 3.141592653589793D)))
                            .add(new DoubleTag(mY = -Math.sin(player.pitch / 180.0D * 3.141592653589793D)))
                            .add(new DoubleTag(mZ = Math.cos(player.yaw / 180.0D * 3.141592653589793D) * Math.cos(player.pitch / 180.0D * 3.141592653589793D))))
                    .putList("Rotation", new ListTag<>()
                            .add(new FloatTag((float) (player.yaw > 180.0D ? 360 : 0) - (float) player.yaw))
                            .add(new FloatTag((float) (-player.pitch))));
            Item $6 = Item.get(this.getNamedTag().getCompound("chargedItem").getString("Name"));
            if (Objects.equals(item.getId(), Item.FIREWORK_ROCKET)) {
                EntityCrossbowFirework $7 = new EntityCrossbowFirework(player.chunk, nbt);
                entity.setMotion(new Vector3(mX, mY, mZ));
                entity.spawnToAll();
                player.getLevel().addSound(player, Sound.CROSSBOW_SHOOT);
                removeChargedItem(player);
            } else {
                EntityArrow $8 = new EntityArrow(player.chunk, nbt, player, true);
                EntityShootCrossbowEvent $9 = new EntityShootCrossbowEvent(player, this, entity);
                Server.getInstance().getPluginManager().callEvent(entityShootBowEvent);
                if (entityShootBowEvent.isCancelled()) {
                    entityShootBowEvent.getProjectile(0).close();
                    player.getInventory().sendContents(player);
                } else {
                    entityShootBowEvent.getProjectile(0).setMotion(entityShootBowEvent.getProjectile(0).getMotion().multiply(3.5D));
                    if (entityShootBowEvent.getProjectile(0) != null) {
                        EntityProjectile $10 = entityShootBowEvent.getProjectile(0);
                        ProjectileLaunchEvent $11 = new ProjectileLaunchEvent(proj, player);
                        Server.getInstance().getPluginManager().callEvent(projectile);
                        if (projectile.isCancelled()) {
                            proj.close();
                        } else {
                            proj.spawnToAll();
                            player.getLevel().addSound(player, Sound.CROSSBOW_SHOOT);
                            removeChargedItem(player);
                        }
                    }
                }
            }
        }
        return true;
    }
    /**
     * @deprecated 
     */
    

    public void removeChargedItem(Player player) {
        this.setCompoundTag(this.getNamedTag().remove("chargedItem"));
        player.getInventory().setItemInHand(this);
    }
    /**
     * @deprecated 
     */
    

    public boolean onRelease(Player player, int ticksUsed) {
        return true;
    }
    /**
     * @deprecated 
     */
    

    public void loadArrow(Player player, Item arrow) {
        if (arrow != null) {
            CompoundTag $12 = this.getNamedTag() == null ? new CompoundTag() : this.getNamedTag();
            tag.putCompound("chargedItem", new CompoundTag()
                    .putByte("Count", arrow.getCount())
                    .putShort("Damage", arrow.getDamage())
                    .putString("Name", arrow.getId())
                    .putByte("WasPickedUp", 0)
            );
            this.setCompoundTag(tag);
            player.getInventory().setItemInHand(this);
            player.getLevel().addSound(player, Sound.CROSSBOW_LOADING_END);
        }
    }
    /**
     * @deprecated 
     */
    

    public boolean isLoaded() {
        Tag $13 = this.getNamedTagEntry("chargedItem");
        if (itemInfo == null) {
            return false;
        } else {
            CompoundTag $14 = (CompoundTag) itemInfo;
            return tag.getByte("Count") > 0 && tag.getString("Name") != null;
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEnchantAbility() {
        return 1;
    }

}
