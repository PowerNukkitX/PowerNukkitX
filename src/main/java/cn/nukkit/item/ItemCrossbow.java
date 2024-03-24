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

    public ItemCrossbow() {
        this(0, 1);
    }

    public ItemCrossbow(Integer meta) {
        this(meta, 1);
    }

    public ItemCrossbow(Integer meta, int count) {
        super(CROSSBOW, meta, count, "Crossbow");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_CROSSBOW;
    }

    public boolean onUse(Player player, int ticksUsed) {
        if (isLoaded()) return true;
        this.loadTick = Server.getInstance().getTick();
        int needTickUsed = 25;
        Enchantment enchantment = this.getEnchantment(Enchantment.ID_CROSSBOW_QUICK_CHARGE);
        if (enchantment != null) {
            needTickUsed -= enchantment.getLevel() * 5; //0.25s
        }

        if (ticksUsed >= needTickUsed) {
            Item itemArrow;
            Inventory inventory = player.getOffhandInventory();
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
                        Enchantment durability = this.getEnchantment(Enchantment.ID_DURABILITY);
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

    protected boolean canLoad(Item item) {
        return switch (item.getId()) {
            case Item.ARROW, Item.FIREWORK_ROCKET -> true;
            default -> false;
        };
    }

    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (this.isLoaded() && Server.getInstance().getTick() - this.loadTick > 5) {
            double mX;
            double mY;
            double mZ;
            CompoundTag nbt = (new CompoundTag())
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
            Item item = Item.get(this.getNamedTag().getCompound("chargedItem").getString("Name"));
            if (Objects.equals(item.getId(), Item.FIREWORK_ROCKET)) {
                EntityCrossbowFirework entity = new EntityCrossbowFirework(player.chunk, nbt);
                entity.setMotion(new Vector3(mX, mY, mZ));
                entity.spawnToAll();
                player.getLevel().addSound(player, Sound.CROSSBOW_SHOOT);
                removeChargedItem(player);
            } else {
                EntityArrow entity = new EntityArrow(player.chunk, nbt, player, true);
                EntityShootCrossbowEvent entityShootBowEvent = new EntityShootCrossbowEvent(player, this, entity);
                Server.getInstance().getPluginManager().callEvent(entityShootBowEvent);
                if (entityShootBowEvent.isCancelled()) {
                    entityShootBowEvent.getProjectile(0).close();
                    player.getInventory().sendContents(player);
                } else {
                    entityShootBowEvent.getProjectile(0).setMotion(entityShootBowEvent.getProjectile(0).getMotion().multiply(3.5D));
                    if (entityShootBowEvent.getProjectile(0) != null) {
                        EntityProjectile proj = entityShootBowEvent.getProjectile(0);
                        ProjectileLaunchEvent projectile = new ProjectileLaunchEvent(proj, player);
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

    public void removeChargedItem(Player player) {
        this.setCompoundTag(this.getNamedTag().remove("chargedItem"));
        player.getInventory().setItemInHand(this);
    }

    public boolean onRelease(Player player, int ticksUsed) {
        return true;
    }

    public void loadArrow(Player player, Item arrow) {
        if (arrow != null) {
            CompoundTag tag = this.getNamedTag() == null ? new CompoundTag() : this.getNamedTag();
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

    public boolean isLoaded() {
        Tag itemInfo = this.getNamedTagEntry("chargedItem");
        if (itemInfo == null) {
            return false;
        } else {
            CompoundTag tag = (CompoundTag) itemInfo;
            return tag.getByte("Count") > 0 && tag.getString("Name") != null;
        }
    }

    @Override
    public int getEnchantAbility() {
        return 1;
    }

}
