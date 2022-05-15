package cn.nukkit.entity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.control.Control;
import cn.nukkit.entity.control.JumpControl;
import cn.nukkit.entity.control.ShoreControl;
import cn.nukkit.entity.control.WalkMoveNearControl;
import cn.nukkit.entity.path.AStarPathFinder;
import cn.nukkit.entity.path.Node;
import cn.nukkit.entity.path.PathThinker;
import cn.nukkit.entity.path.SearchShape;
import cn.nukkit.entity.path.shape.CommonWalkerSearchShape;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.CriticalParticle;
import cn.nukkit.level.particle.HappyVillagerParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public abstract class EntityIntelligent extends EntityPhysical implements PathThinker {

    /**
     * 这个AABB是用来快速计算在某个不定点的碰撞箱的
     */
    protected final AxisAlignedBB fixedSizeBB = new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0);

    public boolean isJumping = false;
    public boolean isShoring = false;
    public Vector3 movingNearDestination = null;

    public Vector3 previousMoveNearMotion = Vector3.ZERO;

    protected Control<?> jumpControl = null;
    protected Control<?> shoreControl = null;
    protected Control<? extends Vector3> moveNearControl = null;

    public EntityIntelligent(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, true);
    }

    public EntityIntelligent(FullChunk chunk, CompoundTag nbt, boolean init) {
        super(chunk, nbt);
        if (init) {
            this.jumpControl = new JumpControl(this);
            this.shoreControl = new ShoreControl(this);
            this.moveNearControl = new WalkMoveNearControl(this);
        }
        {
            final double dx = this.getWidth() * 0.5;
            final double dz = this.getHeight() * 0.5;
            this.fixedSizeBB.setMinX(-dx);
            this.fixedSizeBB.setMaxX(dx);
            this.fixedSizeBB.setMinY(0);
            this.fixedSizeBB.setMaxY(this.getHeight());
            this.fixedSizeBB.setMinZ(-dz);
            this.fixedSizeBB.setMaxZ(dz);
        }
    }

    @Override
    public void asyncPrepare(int currentTick) {
        // 处理运动
        super.asyncPrepare(currentTick);
        if (moveNearControl != null) previousMoveNearMotion = moveNearControl.control(currentTick, needsRecalcMovement);
        addTmpMoveMotionXZ(previousMoveNearMotion);
        if (jumpControl != null) jumpControl.control(currentTick, needsRecalcMovement);
        if (shoreControl != null) shoreControl.control(currentTick, needsRecalcMovement);
    }

    /**
     * 让实体跳跃
     *
     * @return 是否成功开始跳跃，如果实体在空中或因其他原因无法开始跳跃则返回false
     */
    public boolean jump() {
        if (!this.isOnGround() || isJumping) {
            return false;
        }
        isJumping = true;
        return true;
    }

    /**
     * 让实体跳上岸，不会添加水平移动
     *
     * @return 此处是否能从水中尝试跳上岸
     */
    public boolean shore() {
        if (!this.isFloating() || isShoring) {
            return false;
        }
        isShoring = true;
        return true;
    }

    public float getJumpingHeight() {
        return 1.25f;
    }

    /**
     * 移动至临近的目标点，目标点距离距离应小于1.733，不强制要求
     *
     * @param destination 目标点
     * @return 该目标点是否能够到达
     */
    public boolean moveNear(Vector3 destination) {
        if (level.getCollisionBlocks(new SimpleAxisAlignedBB(destination.x - getWidth() * 0.5, destination.y, destination.z - getWidth() * 0.5,
                        destination.x + getWidth() * 0.5, destination.y + getHeight(), destination.z + getWidth() * 0.5),
                true, false, Block::isSolid).length == 0) {
            movingNearDestination = destination;
            return true;
        }
        return false;
    }

    /**
     * 尝试寻路到目标位置
     *
     * @return 是否能寻路到目标位置
     */
    public boolean tryPath(Vector3 pathDestination, boolean displayParticle) {
        pathDestination = pathDestination.clone();
        var pathFinder = new AStarPathFinder();
        var destinationNode = new Node(pathDestination.x, pathDestination.y, pathDestination.z, null);
        destinationNode.setParent(destinationNode);
        var startNode = new Node(this.x, this.y, this.z, destinationNode);
        pathFinder.setPathThinker(this);
        pathFinder.setDestination(destinationNode);
        pathFinder.setStart(startNode);
        pathFinder.prepareSearch();
        var result = pathFinder.search();
        if (result && displayParticle) {
            var current = destinationNode;
            while (current != null) {
                level.addParticle(new HappyVillagerParticle(current.toRealVector()));
                current = current.getParent();
            }
        }
        return result;
    }

    /**
     * 计算两点间移动的代价，通常水平移动1格为10，0.5格为5，斜向移动一格为14。
     * 两个点不保证相邻，有可能会发生坠落，攀爬，跳跃导致不相邻。
     * 如果两个点之间是直接不可达的，应返回{@link Long#MAX_VALUE} (9223372036854775807L)。
     * <p>
     * TODO: 2022/5/12 这是个简陋的实现，仍需更多判断逻辑以完善
     * TODO: 2022/5/13 尚未完美实现跳跃和坠落
     *
     * @param from 起点
     * @param to   终点
     * @return 代价
     */
    @Override
    public long calcCost(@NotNull Node from, @NotNull Node to) {
        var dx = from.doubleRealX() - to.doubleRealX();
        var dy = from.doubleRealY() - to.doubleRealY();
        var dz = from.doubleRealZ() - to.doubleRealZ();
        // TODO: 2022/5/15 这个计算方式开销太大了，应该优化
        // 但是由于高端CPU有硬件平方根和SIMD，很可能sqrt计算速度会很快，优化的时候要千万注意别负优化，一定要测试
        var cost = (int) Math.sqrt(dx * dx + dy * dy + dz * dz) * 5;
        if (cost >= 10 && !canPassThrough0((from.realX() + to.realX()) * 0.5, to.realY(), (from.realZ() + to.realZ()) * 0.5)) {
            return Long.MAX_VALUE;
        }
        if (level.getBlock(to.toRealVector().add(0, -0.5, 0)) instanceof BlockWater) {
            cost += cost > 9 ? 8 : 4; // 水上走的代价更大，加入惩罚机制，让实体倾向于更多走岸上。
        }
        return cost;
    }

    /**
     * 计算这个位置是否能经过，需要考虑的点包括但不限于此处是否有方块阻挡，脚下是否是空气能站立，梯子/藤蔓是否能攀爬等。
     *
     * @param node 要计算可通过性的点
     * @return 是否可通过
     */
    @Override
    public boolean canPassThrough(@NotNull Node node) {
        return canPassThrough0(node.realX(), node.realY(), node.realZ());
    }

    private boolean canPassThrough0(double x, double y, double z) {
        var tmpBB = fixedSizeBB.getOffsetBoundingBox(x, y, z);
        if (this.level.fastCollisionBlocks(tmpBB, true, false,
                block -> block.isSolid() || !isSafeBlock(block)).size() > 0) {
            return false;
        }
        var offsetTmpBB = tmpBB.getOffsetBoundingBox(0, -0.5, 0);
        return this.level.fastCollisionBlocks(offsetTmpBB, true, true,
                block -> {
                    if (!isSafeBlock(block)) return false;
                    if (block.isSolid()) {
                        return block.collidesWithBB(offsetTmpBB);
                    } else if (block instanceof BlockLiquid) {
                        return tmpBB.getMinY() % 1 <= block.getMaxY();
                    }
                    return false;
                }).size() > 0;
    }

    protected boolean isSafeBlock(@NotNull Block block) {
        final var bid = block.getId();
        return bid != BlockID.FLOWING_LAVA && bid != BlockID.STILL_LAVA && bid != BlockID.MAGMA && bid != BlockID.FIRE;
    }

    /**
     * @return 搜索形状
     * @see SearchShape
     */
    @NotNull
    @Override
    public SearchShape getSearchShape() {
        return new CommonWalkerSearchShape();
    }
}
