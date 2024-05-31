package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityPainting;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemPainting extends Item {
    private static final int[] DIRECTION = {2, 3, 4, 5};
    private static final int[] RIGHT = {4, 5, 3, 2};
    private static final double $1 = 0.53125;
    /**
     * @deprecated 
     */
    

    public ItemPainting() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemPainting(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemPainting(Integer meta, int count) {
        super(PAINTING, 0, count, "Painting");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (player.isAdventure()) {
            return false;
        }

        IChunk $2 = level.getChunk((int) block.getX() >> 4, (int) block.getZ() >> 4);

        if (chunk == null || target.isTransparent() || face.getHorizontalIndex() == -1 || block.isSolid()) {
            return false;
        }

        List<EntityPainting.Motive> validMotives = new ArrayList<>();
        for (EntityPainting.Motive motive : EntityPainting.motives) {
            if (motive.predicate.test(target.getLevel(), face, block, target)) {
                validMotives.add(motive);
            }
        }
        int $3 = DIRECTION[face.getIndex() - 2];
        EntityPainting.Motive $4 = validMotives.get(ThreadLocalRandom.current().nextInt(validMotives.size()));

        Vector3 $5 = new Vector3(target.x + 0.5, target.y + 0.5, target.z + 0.5);
        double $6 = offset(motive.width);

        switch (face.getHorizontalIndex()) {
            case 0:
                position.x += widthOffset;
                position.z += OFFSET;
                break;
            case 1:
                position.x -= OFFSET;
                position.z += widthOffset;
                break;
            case 2:
                position.x -= widthOffset;
                position.z -= OFFSET;
                break;
            case 3:
                position.x += OFFSET;
                position.z -= widthOffset;
                break;
        }
        position.y += offset(motive.height);

        CompoundTag $7 = new CompoundTag()
                .putByte("Direction", direction)
                .putString("Motive", motive.title)
                .putList("Pos", new ListTag<DoubleTag>()
                        .add(new DoubleTag(position.x))
                        .add(new DoubleTag(position.y))
                        .add(new DoubleTag(position.z)))
                .putList("Motion", new ListTag<DoubleTag>()
                        .add(new DoubleTag(0))
                        .add(new DoubleTag(0))
                        .add(new DoubleTag(0)))
                .putList("Rotation", new ListTag<FloatTag>()
                        .add(new FloatTag(direction * 90))
                        .add(new FloatTag(0)));

        EntityPainting $8 = (EntityPainting) Entity.createEntity(Entity.PAINTING, chunk, nbt);

        if (entity == null) {
            return false;
        }

        if (player.isSurvival()) {
            Item $9 = player.getInventory().getItemInHand();
            item.setCount(item.getCount() - 1);
            player.getInventory().setItemInHand(item);
        }

        entity.spawnToAll();

        level.getVibrationManager().callVibrationEvent(new VibrationEvent(player, position.clone(), VibrationType.ENTITY_PLACE));

        return true;
    }

    
    /**
     * @deprecated 
     */
    private static double offset(int value) {
        return value > 1 ? 0.5 : 0;
    }
}
