package cn.nukkit.level;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.positiontracking.NamedPosition;
import cn.nukkit.utils.LevelException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author MagicDroidX (Nukkit Project)
 */

public class Position extends NamedPosition {
    public Level level;

    public Position() {
        this(0, 0, 0, null);
    }

    public Position(double x) {
        this(x, 0, 0, null);
    }

    public Position(double x, double y) {
        this(x, y, 0, null);
    }

    public Position(double x, double y, double z) {
        this(x, y, z, null);
    }

    public Position(double x, double y, double z, Level level) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.level = level;
    }

    public static Position fromObject(Vector3 pos) {
        return fromObject(pos, null);
    }

    public static Position fromObject(Vector3 pos, Level level) {
        return new Position(pos.x, pos.y, pos.z, level);
    }

    public Level getLevel() {
        return this.level;
    }

    public Position setLevel(Level level) {
        this.level = level;
        return this;
    }

    public boolean isValid() {
        return this.level != null;
    }

    public boolean setStrong() {
        return false;
    }

    public boolean setWeak() {
        return false;
    }

    @Override
    public Position getSide(BlockFace face) {
        return this.getSide(face, 1);
    }

    @Override
    public Position getSide(BlockFace face, int step) {
        return Position.fromObject(super.getSide(face, step), getValidLevel());
    }

    // Get as a Position for better performance. Do not override it!


    public Position getSidePos(BlockFace face) {
        return Position.fromObject(super.getSide(face, 1), getValidLevel());
    }

    @Override
    public String toString() {
        return "Position(level=" + (this.isValid() ? this.getLevel().getName() : "null") + ",x=" + this.x + ",y=" + this.y + ",z=" + this.z + ")";
    }

    @Override
    public Position setComponents(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    @Override
    public Position setComponents(Vector3 pos) {
        super.setComponents(pos);
        return this;
    }

    public @Nullable BlockEntity getLevelBlockEntity() {
        return getValidLevel().getBlockEntity(this);
    }

    public @Nullable final <T extends BlockEntity> T getTypedBlockEntity(@NotNull Class<T> type) {
        BlockEntity blockEntity = getValidLevel().getBlockEntity(this);
        return type.isInstance(blockEntity) ? type.cast(blockEntity) : null;
    }

    @NotNull public BlockState getLevelBlockState() {
        return getLevelBlockState(0);
    }

    @NotNull public BlockState getLevelBlockState(int layer) {
        return getValidLevel().getBlockStateAt(getFloorX(), getFloorY(), getFloorZ(), layer);
    }

    public Block getLevelBlock() {
        return getLevelBlock(true);
    }

    public Block getLevelBlock(boolean load) {
        return getValidLevel().getBlock(this, load);
    }

    public Block getLevelBlock(int layer) {
        return getValidLevel().getBlock(this, layer);
    }

    public Block getLevelBlock(int layer, boolean load) {
        return getValidLevel().getBlock(this, layer, load);
    }

    public Block getTickCachedLevelBlock() {
        return getValidLevel().getTickCachedBlock(this);
    }

    public Set<Block> getLevelBlockAround() {
        return getValidLevel().getBlockAround(this);
    }

    public Block getLevelBlockAtLayer(int layer) {
        return getValidLevel().getBlock(this, layer);
    }

    public Block getTickCachedLevelBlockAtLayer(int layer) {
        return getValidLevel().getTickCachedBlock(this, layer);
    }

    @NotNull public Location getLocation() {
        return new Location(this.x, this.y, this.z, 0, 0, getValidLevel());
    }

    @Override
    @NotNull public String getLevelName() {
        return getValidLevel().getName();
    }

    @NotNull public final Level getValidLevel() {
        Level level = this.level;
        if (level == null) {
            throw new LevelException("Undefined Level reference");
        }
        return level;
    }

    @Override
    public Position add(double x) {
        return this.add(x, 0, 0);
    }

    @Override
    public Position add(double x, double y) {
        return this.add(x, y, 0);
    }

    @Override
    public Position add(double x, double y, double z) {
        return new Position(this.x + x, this.y + y, this.z + z, this.level);
    }

    @Override
    public Position add(Vector3 x) {
        return new Position(this.x + x.getX(), this.y + x.getY(), this.z + x.getZ(), this.level);
    }

    @Override
    public Position subtract(double x) {
        return this.subtract(x, 0, 0);
    }

    @Override
    public Position subtract(double x, double y) {
        return this.subtract(x, y, 0);
    }

    @Override
    public Position subtract(double x, double y, double z) {
        return this.add(-x, -y, -z);
    }

    @Override
    public Position subtract(Vector3 x) {
        return this.add(-x.getX(), -x.getY(), -x.getZ());
    }

    @Override
    public Position multiply(double number) {
        return new Position(this.x * number, this.y * number, this.z * number, this.level);
    }

    @Override
    public Position divide(double number) {
        return new Position(this.x / number, this.y / number, this.z / number, this.level);
    }

    @Override
    public Position ceil() {
        return new Position((int) Math.ceil(this.x), (int) Math.ceil(this.y), (int) Math.ceil(this.z), this.level);
    }

    @Override
    public Position floor() {
        return new Position(this.getFloorX(), this.getFloorY(), this.getFloorZ(), this.level);
    }

    @Override
    public Position round() {
        return new Position(Math.round(this.x), Math.round(this.y), Math.round(this.z), this.level);
    }

    @Override
    public Position abs() {
        return new Position((int) Math.abs(this.x), (int) Math.abs(this.y), (int) Math.abs(this.z), this.level);
    }

    @Override
    public Position clone() {
        return (Position) super.clone();
    }

    public IChunk getChunk() {
        return isValid() ? level.getChunk(getChunkX(), getChunkZ()) : null;
    }
}
