package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.item.EntityPainting;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationType;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.DoubleTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemPainting extends Item {
    private static final int[] DIRECTION = {2, 3, 4, 5};
    private static final int[] RIGHT = {4, 5, 3, 2};
    private static final double OFFSET = 0.53125;

    public ItemPainting() {
        this(0, 1);
    }

    public ItemPainting(Integer meta) {
        this(meta, 1);
    }

    public ItemPainting(Integer meta, int count) {
        super(PAINTING, 0, count, "Painting");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (player.isAdventure()) {
            return false;
        }

        IChunk chunk = level.getChunk((int) block.getX() >> 4, (int) block.getZ() >> 4);

        if (chunk == null || target.isTransparent() || face.getHorizontalIndex() == -1 || block.isSolid()) {
            return false;
        }

        List<EntityPainting.Motive> validMotives = new ArrayList<>();
        for (EntityPainting.Motive motive : EntityPainting.motives) {
            if (motive.predicate.test(target.getLevel(), face, block, target)) {
                validMotives.add(motive);
            }
        }
        if (validMotives.isEmpty()) return false;

        int direction = DIRECTION[face.getIndex() - 2];
        EntityPainting.Motive motive = validMotives.get(ThreadLocalRandom.current().nextInt(validMotives.size()));

        Vector3 position = new Vector3(target.x + 0.5, target.y + 0.5, target.z + 0.5);
        double widthOffset = offset(motive.width);

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

        CompoundTag nbt = new CompoundTag()
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

        EntityPainting entity = (EntityPainting) Entity.createEntity(Entity.PAINTING, chunk, nbt);

        if (entity == null) {
            return false;
        }

        if (player.isSurvival()) {
            Item item = player.getInventory().getItemInMainHand();
            item.setCount(item.getCount() - 1);
            player.getInventory().setItemInMainHand(item);
        }

        entity.spawnToAll();

        level.getVibrationManager().callVibrationEvent(new VibrationEvent(player, position.clone(), VibrationType.ENTITY_PLACE));

        return true;
    }

    private static double offset(int value) {
        if (value > 1 && value != 3) {
            return 0.5;
        }
        return 0;
    }
}
