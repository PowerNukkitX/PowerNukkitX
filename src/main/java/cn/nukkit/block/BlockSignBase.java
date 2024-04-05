package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.event.block.SignColorChangeEvent;
import cn.nukkit.event.block.SignGlowEvent;
import cn.nukkit.event.block.SignWaxedEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemGlowInkSac;
import cn.nukkit.item.ItemHoneycomb;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.particle.WaxOnParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.CompassRoseDirection;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;


public abstract class BlockSignBase extends BlockTransparent implements Faceable {
    public BlockSignBase(BlockState blockState) {
        super(blockState);
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public void onTouch(@NotNull Vector3 vector, @NotNull Item item, @NotNull BlockFace face, float fx, float fy, float fz, @Nullable Player player, PlayerInteractEvent.@NotNull Action action) {
        if(action== PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK){
            var blockEntity = this.getLevel().getBlockEntity(this);
            if (!(blockEntity instanceof BlockEntitySign sign)) {
                return;
            }
            // If a sign is waxed, it cannot be modified.
            if (sign.isWaxed() || (Objects.requireNonNull(player).isSneaking() && !Objects.equals(item.getId(), AIR))) {
                level.addLevelSoundEvent(this.add(0.5, 0.5, 0.5), LevelSoundEventPacket.SOUND_WAXED_SIGN_INTERACT_FAIL);
                return;
            }
            boolean front = switch (getSignDirection()) {
                case EAST -> face == BlockFace.EAST;
                case SOUTH -> face == BlockFace.SOUTH;
                case WEST -> face == BlockFace.WEST;
                case NORTH -> face == BlockFace.NORTH;
                case NORTH_EAST, NORTH_NORTH_EAST, EAST_NORTH_EAST -> face == BlockFace.EAST || face == BlockFace.NORTH;
                case NORTH_WEST, NORTH_NORTH_WEST, WEST_NORTH_WEST -> face == BlockFace.WEST || face == BlockFace.NORTH;
                case SOUTH_EAST, SOUTH_SOUTH_EAST, EAST_SOUTH_EAST -> face == BlockFace.EAST || face == BlockFace.SOUTH;
                case SOUTH_WEST, SOUTH_SOUTH_WEST, WEST_SOUTH_WEST -> face == BlockFace.WEST || face == BlockFace.SOUTH;
            };
            if (item instanceof ItemDye dye) {
                BlockColor color = dye.getDyeColor().getColor();
                if (color.equals(sign.getColor(front)) || sign.isEmpty(front)) {
                    player.openSignEditor(this, front);
                    return;
                }
                SignColorChangeEvent event = new SignColorChangeEvent(this, player, color);
                this.level.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    sign.spawnTo(player);
                    return;
                }
                sign.setColor(front, color);
                sign.spawnToAll();
                this.level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_DYE_USED);
                if ((player.getGamemode() & 0x01) == 0) {
                    item.count--;
                }
                return;
            } else if (item instanceof ItemGlowInkSac) {
                if (sign.isGlowing(front) || sign.isEmpty(front)) {
                    player.openSignEditor(this, front);
                    return;
                }
                SignGlowEvent event = new SignGlowEvent(this, player, true);
                this.level.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    sign.spawnTo(player);
                    return;
                }
                sign.setGlowing(front, true);
                sign.spawnToAll();
                this.level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_INK_SACE_USED);
                if ((player.getGamemode() & 0x01) == 0) {
                    item.count--;
                }
                return;
            } else if (item instanceof ItemHoneycomb) {
                SignWaxedEvent event = new SignWaxedEvent(this, player, true);
                this.level.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    sign.spawnTo(player);
                    return;
                }
                sign.setWaxed(true);
                sign.spawnToAll();
                this.getLevel().addParticle(new WaxOnParticle(this));
                if ((player.getGamemode() & 0x01) == 0) {
                    item.count--;
                }
                return;
            }
            player.openSignEditor(this, front);
        }
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    public CompassRoseDirection getSignDirection() {
        return CompassRoseDirection.from(getPropertyValue(CommonBlockProperties.GROUND_SIGN_DIRECTION));
    }

    public void setSignDirection(CompassRoseDirection direction) {
        setPropertyValue(CommonBlockProperties.GROUND_SIGN_DIRECTION, direction.getIndex());
    }

    @Override
    public BlockFace getBlockFace() {
        return getSignDirection().getClosestBlockFace();
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setSignDirection(face.getCompassRoseDirection());
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }
}
