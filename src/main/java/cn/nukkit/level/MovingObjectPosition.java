package cn.nukkit.level;

import cn.nukkit.entity.Entity;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

import javax.annotation.Nullable;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class MovingObjectPosition {

    /**
     * 0 = block, 1 = entity
     */
    public int typeOfHit;

    public int blockX;
    public int blockY;
    public int blockZ;

    /**
     * Which side was hit. If its -1 then it went the full length of the ray trace.
     * Bottom = 0, Top = 1, East = 2, West = 3, North = 4, South = 5.
     */
    public int sideHit;

    public Vector3 hitVector;

    public Entity entityHit;


    public static MovingObjectPosition fromBlock(int x, int y, int z, BlockFace face, Vector3 hitVector) {
        MovingObjectPosition objectPosition = new MovingObjectPosition();
        objectPosition.typeOfHit = 0;
        objectPosition.blockX = x;
        objectPosition.blockY = y;
        objectPosition.blockZ = z;
        objectPosition.hitVector = new Vector3(hitVector.x, hitVector.y, hitVector.z);
        objectPosition.setFaceHit(face);
        return objectPosition;
    }

    
    public static MovingObjectPosition fromEntity(Entity entity) {
        MovingObjectPosition objectPosition = new MovingObjectPosition();
        objectPosition.typeOfHit = 1;
        objectPosition.entityHit = entity;
        objectPosition.hitVector = new Vector3(entity.x, entity.y, entity.z);
        return objectPosition;
    }

    @Nullable
    @SuppressWarnings("java:S1874")
    public BlockFace getFaceHit() {
        return switch (sideHit) {
            case 0 -> BlockFace.DOWN;
            case 1 -> BlockFace.UP;
            case 2 -> BlockFace.EAST;
            case 3 -> BlockFace.WEST;
            case 4 -> BlockFace.NORTH;
            case 5 -> BlockFace.SOUTH;
            default -> null;
        };
    }

    @SuppressWarnings("java:S1874")
    public void setFaceHit(@Nullable BlockFace face) {
        if (face == null) {
            sideHit = -1;
            return;
        }

        switch (face) {
            case DOWN -> sideHit = 0;
            case UP -> sideHit = 1;
            case NORTH -> sideHit = 4;
            case SOUTH -> sideHit = 5;
            case WEST -> sideHit = 3;
            case EAST -> sideHit = 2;
            default -> sideHit = -1;
        }
    }

    @Override
    public String toString() {
        return "MovingObjectPosition{" +
                "typeOfHit=" + typeOfHit +
                ", blockX=" + blockX +
                ", blockY=" + blockY +
                ", blockZ=" + blockZ +
                ", sideHit=" + sideHit +
                ", hitVector=" + hitVector +
                ", entityHit=" + entityHit +
                '}';
    }
}
