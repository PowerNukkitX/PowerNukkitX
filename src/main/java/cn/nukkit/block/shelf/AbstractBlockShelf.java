package cn.nukkit.block.shelf;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.block.property.CommonPropertyMap;
import cn.nukkit.blockentity.BlockEntityID;
import cn.nukkit.blockentity.BlockEntityShelf;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.nukkit.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.POWERED_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.POWERED_SHELF_TYPE;

/**
 * @author buddelbubi
 * @since 2025/11/12
 */
public abstract class AbstractBlockShelf extends BlockTransparent implements Faceable, BlockEntityHolder<BlockEntityShelf> {

    public AbstractBlockShelf(BlockState blockState) {
        super(blockState);
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        setBlockFace(player != null ? player.getDirection().getOpposite() : BlockFace.SOUTH);
        this.getLevel().setBlock(this, this, true);
        this.getOrCreateBlockEntity();
        this.updateConnection(this);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if(player != null && !player.isSneaking() && blockFace == getBlockFace() && fy > 0.25f && fy < 0.75f) {
            if(!isGettingPower()) {
                Vector3 faceValues = blockFace.rotateYCCW().getUnitVector();
                double distance = (fx * faceValues.x) + (fz * faceValues.z);
                if(distance < 0) distance++;
                int slot = distance < (1/3f) ? 0 : distance < (2/3f) ? 1 : 2;
                BlockEntityShelf blockEntity = this.getOrCreateBlockEntity();
                Inventory inventory = blockEntity.getInventory();
                if (!player.isCreative()) {
                    player.getInventory().setItemInHand(inventory.getItem(slot)); //Overwrites the players item. So no need to remove it.
                }
                inventory.setItem(slot, item);
                blockEntity.setDirty();
            } else {
                List<AbstractBlockShelf> shelves = getConnectedBlocks();
                for(int i = 0; i < shelves.size(); i++) {
                    BlockEntityShelf shelf = shelves.get(i).getOrCreateBlockEntity();
                    for(int j = 0; j < shelf.getSize(); j++) {
                        Item shelfItem = shelf.getItem(j);
                        int playerSlot = (i * shelf.getSize()) + j;
                        Item playerItem = player.getInventory().getItem(playerSlot);
                        shelf.getInventory().setItem(j, playerItem);
                        player.getInventory().setItem(playerSlot, shelfItem);
                    }
                    shelf.setDirty();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void setBlockFace(BlockFace face) {
        this.setPropertyValue(MINECRAFT_CARDINAL_DIRECTION, CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(face));
    }

    @Override
    public BlockFace getBlockFace() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(getPropertyValue(MINECRAFT_CARDINAL_DIRECTION));
    }

    @Override
    public @NotNull Class<? extends BlockEntityShelf> getBlockEntityClass() {
        return BlockEntityShelf.class;
    }

    @Override
    public @NotNull String getBlockEntityType() {
        return BlockEntityID.SHELF;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        Map<Integer, Item> items = this.getOrCreateBlockEntity().getInventory().getContents();
        int overwrite = 0;
        for(var entry : items.entrySet()) {
            if(!entry.getValue().isNull()) overwrite |= (1 << entry.getKey());
        }
        return overwrite;
    }

    @Override
    public int onUpdate(int type) {
        if(type == Level.BLOCK_UPDATE_REDSTONE) {
            updateConnection(this);
            this.setPropertyValue(POWERED_BIT, this.isGettingPower());
            this.level.setBlock(this, this);
        }
        return super.onUpdate(type);
    }

    public boolean canConnect(@NotNull AbstractBlockShelf shelf) {
        if (!isGettingPower()) return false;

        return switch (getType()) {
            case LEFT -> canConnectToSide(shelf, getBlockFace().rotateYCCW(), PoweredShelfType.RIGHT);
            case RIGHT -> canConnectToSide(shelf, getBlockFace().rotateY(), PoweredShelfType.LEFT);
            default -> true;
        };
    }

    private boolean canConnectToSide(AbstractBlockShelf shelf, BlockFace sideFace, PoweredShelfType expectedType) {
        Block sideBlock = getSide(sideFace);
        if (shelf.equals(sideBlock)) return true;
        if (sideBlock instanceof AbstractBlockShelf other) {
            return other.getType() == expectedType;
        }
        return false;
    }

    public void updateConnection(@NotNull Block origin) {
        PoweredShelfType newType = PoweredShelfType.UNCONNECTED;

        BlockFace face = getBlockFace().rotateY();
        Block right = getSide(face);
        Block left = getSide(face, -1);

        if (isGettingPower()) {
            boolean connectRight = right instanceof AbstractBlockShelf s && s.canConnect(this);
            boolean connectLeft = left instanceof AbstractBlockShelf s && s.canConnect(this);

            if (connectLeft && !connectRight) newType = PoweredShelfType.LEFT;
            else if (!connectLeft && connectRight) newType = PoweredShelfType.RIGHT;
            else if (connectLeft) newType = determineCenterType(left, right);
        }

        if (newType != getType()) {
            setPropertyValue(POWERED_SHELF_TYPE, newType.ordinal());
            level.setBlock(this, this);

            if (right != origin && right instanceof AbstractBlockShelf s) s.updateConnection(this);
            if (left != origin && left instanceof AbstractBlockShelf s) s.updateConnection(this);
        }
    }

    private PoweredShelfType determineCenterType(Block left, Block right) {
        if (right instanceof AbstractBlockShelf rs && rs.getType() == PoweredShelfType.UNCONNECTED && rs.canConnect(this))
            return PoweredShelfType.LEFT;
        if (left instanceof AbstractBlockShelf ls && ls.getType() == PoweredShelfType.UNCONNECTED && ls.canConnect(this))
            return PoweredShelfType.RIGHT;

        boolean rightIsRight = right instanceof AbstractBlockShelf rs2 && rs2.getType() == PoweredShelfType.RIGHT;
        boolean leftIsLeft = left instanceof AbstractBlockShelf ls2 && ls2.getType() == PoweredShelfType.LEFT;
        if (rightIsRight && leftIsLeft) return PoweredShelfType.RIGHT;

        boolean rightIsCenter = right instanceof AbstractBlockShelf rs3 && rs3.getType() == PoweredShelfType.CENTER;
        boolean leftIsCenter = left instanceof AbstractBlockShelf ls3 && ls3.getType() == PoweredShelfType.CENTER;
        if (rightIsCenter) return PoweredShelfType.RIGHT;
        if (leftIsCenter) return PoweredShelfType.LEFT;

        return PoweredShelfType.CENTER;
    }

    protected List<AbstractBlockShelf> getConnectedBlocks() {
        if(this.getType() == PoweredShelfType.UNCONNECTED || !this.isGettingPower()) {
            return new ArrayList<>(Collections.singletonList(this));
        } else {
            List<AbstractBlockShelf> shelves = new ArrayList<>();
            BlockFace face = getBlockFace().rotateY();
            Block right = getSide(face);
            Block left = getSide(face, -1);
           switch (getType()) {
                case CENTER -> {
                    if(right instanceof AbstractBlockShelf shelf) shelves.add(shelf);
                    shelves.add(this);
                    if(left instanceof AbstractBlockShelf shelf) shelves.add(shelf);
                }
               case RIGHT -> {
                    shelves.add(this);
                    if(right instanceof AbstractBlockShelf shelf) {
                        shelves.add(shelf);
                        if(shelf.getType() == PoweredShelfType.CENTER) {
                            Block right1 = getSide(face, 2);
                            if (right1 instanceof AbstractBlockShelf shelf1) shelves.add(shelf1);
                        }
                    }
                   Collections.reverse(shelves);
               }
               case LEFT -> {
                    shelves.add(this);
                    if(left instanceof AbstractBlockShelf shelf) {
                        shelves.add(shelf);
                        if(shelf.getType() == PoweredShelfType.CENTER) {
                            Block left1 = getSide(face, -2);
                            if (left1 instanceof AbstractBlockShelf shelf1) shelves.add(shelf1);
                        }
                    }
                }
            }
            return shelves;
        }
    }

    public PoweredShelfType getType() {
        return PoweredShelfType.values()[getPropertyValue(POWERED_SHELF_TYPE)];
    }

    public enum PoweredShelfType {
        UNCONNECTED,
        RIGHT,
        CENTER,
        LEFT
    }
}
