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
import cn.nukkit.utils.NbtHelper;
import cn.nukkit.utils.Utils;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


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
        this.loadTick = player.getLevel().getTick();
        int needTickUsed = 25;
        Enchantment enchantment = this.getEnchantment(Enchantment.ID_CROSSBOW_QUICK_CHARGE);
        if (enchantment != null) {
            needTickUsed -= enchantment.getLevel() * 5; //0.25s
        }

        if (ticksUsed >= needTickUsed) {
            Item itemArrow;
            Optional<Map.Entry<Integer, Item>> inventoryOptional = player.getInventory().getContents().entrySet().stream().filter(item -> item.getValue() instanceof ItemArrow || item.getValue() instanceof ItemFireworkRocket).findFirst();
            Optional<Map.Entry<Integer, Item>> offhandOptional = player.getOffhandInventory().getContents().entrySet().stream().filter(item -> item.getValue() instanceof ItemArrow || item.getValue() instanceof ItemFireworkRocket).findFirst();
            Item item = null;
            Inventory inventory = null;
            if (offhandOptional.isPresent()) {
                inventory = player.getOffhandInventory();
                item = offhandOptional.get().getValue();
            } else if (inventoryOptional.isPresent()) {
                inventory = player.getInventory();
                item = inventoryOptional.get().getValue();
                ;
            } else if (player.isCreative()) {
                item = new ItemArrow();
            }
            if (item == null) return false;
            if (!this.canLoad(item)) {
                if (player.isCreative()) {
                    this.loadArrow(player, item);
                }

                return true;
            }

            if (!this.isLoaded()) {
                itemArrow = item.clone();
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
        if (this.isLoaded() && player.getLevel().getTick() - this.loadTick > 5) {
            double mX;
            double mY;
            double mZ;
            final NbtMap nbt = NbtMap.builder()
                    .putList("Pos", NbtType.DOUBLE, Arrays.asList(
                                    player.x,
                                    player.y + (double) player.getEyeHeight(),
                                    player.z
                            )
                    ).putList("Motion", NbtType.DOUBLE, Arrays.asList(
                                    mX = -Math.sin(player.yaw / 180.0D * 3.141592653589793D) * Math.cos(player.pitch / 180.0D * 3.141592653589793D),
                                    mY = -Math.sin(player.pitch / 180.0D * 3.141592653589793D),
                                    mZ = Math.cos(player.yaw / 180.0D * 3.141592653589793D) * Math.cos(player.pitch / 180.0D * 3.141592653589793D)
                            )
                    ).putList("Rotation", NbtType.FLOAT, Arrays.asList(
                                    (float) (player.yaw > 180.0D ? 360 : 0) - (float) player.yaw,
                                    (float) (-player.pitch)
                            )
                    ).build();
            Item item = Item.get(this.getNamedTag().getCompound("chargedItem").getString("Name"));
            if (Objects.equals(item.getId(), Item.FIREWORK_ROCKET)) {
                EntityCrossbowFirework entity = new EntityCrossbowFirework(player.chunk, nbt);
                entity.setMotion(new Vector3(mX, mY, mZ));
                entity.spawnToAll();
                player.getLevel().addSound(player, Sound.CROSSBOW_SHOOT);
                removeChargedItem(player);
            } else {
                EntityArrow entity = new EntityArrow(player.chunk, nbt, player, true);
                NbtMap chargedItem = this.getNamedTag().getCompound("chargedItem");
                entity.setItem((ItemArrow) Item.get(chargedItem.getString("Name"), chargedItem.getShort("Damage"), chargedItem.getByte("Count")));
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
        this.setCompoundTag(NbtHelper.remove(this.getNamedTag(), "chargedItem"));
        player.getInventory().setItemInHand(this);
    }

    @Override
    public int getUsingTicks() {
        return 72000;
    }

    public boolean onRelease(Player player, int ticksUsed) {
        return true;
    }

    public void loadArrow(Player player, Item arrow) {
        if (arrow != null) {
            NbtMapBuilder tag = this.getNamedTag() == null ? NbtMap.builder() : this.getNamedTag().toBuilder();
            tag.putCompound("chargedItem", NbtMap.builder()
                    .putByte("Count", (byte) arrow.getCount())
                    .putShort("Damage", (short) arrow.getDamage())
                    .putString("Name", arrow.getId())
                    .putByte("WasPickedUp", (byte) 0)
                    .build()
            );
            this.setCompoundTag(tag.build());
            player.getInventory().setItemInHand(this);
            player.getLevel().addSound(player, Sound.CROSSBOW_LOADING_END);
        }
    }

    public boolean isLoaded() {
        Object itemInfo = this.getNamedTagEntry("chargedItem");
        if (itemInfo == null) {
            return false;
        } else {
            NbtMap tag = (NbtMap) itemInfo;
            return tag.getByte("Count") > 0 && tag.getString("Name") != null;
        }
    }

    @Override
    public int getEnchantAbility() {
        return 1;
    }

}
