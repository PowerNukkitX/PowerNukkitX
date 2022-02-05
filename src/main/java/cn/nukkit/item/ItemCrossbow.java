package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.Since;
import cn.nukkit.entity.item.EntityCrossbowFirework;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityShootCrossbowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.*;

import java.util.Random;

@Since("1.6.0.0-PNX")
public class ItemCrossbow extends ItemTool {
    private static final Random RANDOM = new Random();
    private int loadTick;

    @Since("1.4.0.0-PN")
    public ItemCrossbow() {
        this(0, 1);
    }

    @Since("1.4.0.0-PN")
    public ItemCrossbow(Integer meta) {
        this(meta, 1);
    }

    @Since("1.4.0.0-PN")
    public ItemCrossbow(Integer meta, int count) {
        super(CROSSBOW, meta, count, "Crossbow");
    }

    @Override
    @Since("1.6.0.0-PN")
    public int getMaxDurability() {
        return ItemTool.DURABILITY_CROSSBOW;
    }

    @Since("1.6.0.0-PNX")
    public boolean onUse(Player player, int ticksUsed) {
        int needTickUsed = 20;
        Enchantment enchantment = this.getEnchantment(Enchantment.ID_CROSSBOW_QUICK_CHARGE);
        if (enchantment != null) {
            needTickUsed -= enchantment.getLevel() * 5; //0.25s
        }

        if (ticksUsed < needTickUsed) {
            return true;
        } else {
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
                        this.loadArrow(player, Item.get(262));
                    }

                    return true;
                }
            }

            if (!this.isLoaded()) {
                if (!player.isCreative()) {
                    if (!this.isUnbreakable()) {
                        Enchantment durability = this.getEnchantment(17);
                        if (durability == null || durability.getLevel() <= 0 || 100 / (durability.getLevel() + 1) > RANDOM.nextInt(100)) {
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

            return true;
        }
    }

    @Since("1.6.0.0-PNX")
    protected boolean canLoad(Item item) {
        switch(item.getId()) {
            case 262:
            case 401:
                return true;
            default:
                return false;
        }
    }

    @Since("1.6.0.0-PNX")
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return !this.launchArrow(player);
    }

    @Since("1.6.0.0-PNX")
    public boolean onRelease(Player player, int ticksUsed) {
        return true;
    }

    @Since("1.6.0.0-PNX")
    public void loadArrow(Player player, Item arrow) {
        if (arrow != null) {
            CompoundTag tag = this.getNamedTag() == null ? new CompoundTag() : this.getNamedTag();
            tag.putBoolean("Charged", true).putCompound("chargedItem", (new CompoundTag("chargedItem")).putByte("Count", arrow.getCount()).putShort("Damage", arrow.getDamage()).putString("Name", "minecraft:" + arrow.getName()));
            this.setCompoundTag(tag);
            this.loadTick = Server.getInstance().getTick();
            player.getInventory().setItemInHand(this);
            player.getLevel().addLevelSoundEvent(player, 247);
        }
    }

    @Since("1.6.0.0-PNX")
    public void useArrow(Player player) {
        this.setCompoundTag(this.getNamedTag().putBoolean("Charged", false).remove("chargedItem"));
        player.getInventory().setItemInHand(this);
    }

    @Since("1.6.0.0-PNX")
    public boolean isLoaded() {
        Tag itemInfo = this.getNamedTagEntry("chargedItem");
        if (itemInfo == null) {
            return false;
        } else {
            CompoundTag tag = (CompoundTag)itemInfo;
            return tag.getByte("Count") > 0 && tag.getString("Name") != null;
        }
    }

    @Since("1.6.0.0-PNX")
    public boolean launchArrow(Player player) {
        if (this.isLoaded() && Server.getInstance().getTick() - this.loadTick > 20) {
            double mX;
            double mY;
            double mZ;
            CompoundTag nbt = (new CompoundTag()).putList((new ListTag<>("Pos")).add(new DoubleTag("", player.x)).add(new DoubleTag("", player.y + (double)player.getEyeHeight())).add(new DoubleTag("", player.z))).putList((new ListTag("Motion")).add(new DoubleTag("", mX = -Math.sin(player.yaw / 180.0D * 3.141592653589793D) * Math.cos(player.pitch / 180.0D * 3.141592653589793D))).add(new DoubleTag("", mY = -Math.sin(player.pitch / 180.0D * 3.141592653589793D))).add(new DoubleTag("", mZ = Math.cos(player.yaw / 180.0D * 3.141592653589793D) * Math.cos(player.pitch / 180.0D * 3.141592653589793D)))).putList((new ListTag("Rotation")).add(new FloatTag("", (float)(player.yaw > 180.0D ? 360 : 0) - (float)player.yaw)).add(new FloatTag("", (float)(-player.pitch))));
            Item item = Item.fromString(this.getNamedTag().getCompound("chargedItem").getString("Name"));
            if (item.getId() == 401) {
                EntityCrossbowFirework entity = new EntityCrossbowFirework(player.chunk, nbt);
                entity.setMotion(new Vector3(mX, mY, mZ));
                entity.spawnToAll();
                player.getLevel().addLevelSoundEvent(player, 248);
                this.useArrow(player);
            } else {
                EntityArrow entity = new EntityArrow(player.chunk, nbt, player, false);
                EntityShootCrossbowEvent entityShootBowEvent = new EntityShootCrossbowEvent(player, this, entity);
                Server.getInstance().getPluginManager().callEvent(entityShootBowEvent);
                if (entityShootBowEvent.isCancelled()) {
                    entityShootBowEvent.getProjectile(0).close();
                    player.getInventory().sendContents(player);
                } else {
                    entityShootBowEvent.getProjectile(0).setMotion(entityShootBowEvent.getProjectile(0).getMotion().multiply(3.5D));
                    if (entityShootBowEvent.getProjectile(0) != null) {
                        EntityProjectile proj = entityShootBowEvent.getProjectile(0);
                        ProjectileLaunchEvent projectile = new ProjectileLaunchEvent(proj);
                        Server.getInstance().getPluginManager().callEvent(projectile);
                        if (projectile.isCancelled()) {
                            proj.close();
                        } else {
                            proj.spawnToAll();
                            player.getLevel().addLevelSoundEvent(player, 248);
                            this.useArrow(player);
                        }
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getEnchantAbility() {
        return 1;
    }

}
