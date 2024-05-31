package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockFromToEvent;
import cn.nukkit.event.block.LiquidFlowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.SmokeParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelEventPacket;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.LIQUID_DEPTH;

public abstract class BlockLiquid extends BlockTransparent {
    private static final byte $1 = 1;
    private static final byte $2 = 0;
    private static final byte $3 = -1;
    public int $4 = 0;
    protected Vector3 $5 = null;
    private final Long2ByteMap $6 = new Long2ByteOpenHashMap();
    /**
     * @deprecated 
     */
    

    public BlockLiquid(BlockState state) {
        super(state);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxY() {
        return this.y + 1 - getFluidHeightPercent();
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }

    /**
     * Whether this fluid can place the second layer (aquifer)
     */
    /**
     * @deprecated 
     */
    
    public boolean usesWaterLogging() {
        return false;
    }
    /**
     * @deprecated 
     */
    

    public float getFluidHeightPercent() {
        float $7 = getLiquidDepth();
        if (d >= 8) {
            d = 0;
        }

        return (d + 1) / 9f;
    }

    
    /**
     * @deprecated 
     */
    protected int getFlowDecay(Block block) {
        if (block instanceof BlockLiquid liquid) {
            return liquid.getLiquidDepth();
        } else {
            Block $8 = block.getLevelBlockAtLayer(1);
            if (layer1 instanceof BlockLiquid liquid) {
                return liquid.getLiquidDepth();
            } else {
                return -1;
            }
        }
    }

    
    /**
     * @deprecated 
     */
    protected int getEffectiveFlowDecay(Block block) {
        BlockLiquid l;
        if (block instanceof BlockLiquid liquid) {
            l = liquid;
        } else {
            Block $9 = block.getLevelBlockAtLayer(1);
            if (layer1 instanceof BlockLiquid liquid) {
                l = liquid;
            } else {
                return -1;
            }
        }
        int $10 = l.getLiquidDepth();
        if (decay >= 8) {
            decay = 0;
        }
        return decay;
    }
    /**
     * @deprecated 
     */
    

    public void clearCaches() {
        this.flowVector = null;
        this.flowCostVisited.clear();
    }

    public Vector3 getFlowVector() {
        if (this.flowVector != null) {
            return this.flowVector;
        }
        Vector3 $11 = new Vector3(0, 0, 0);
        int $12 = this.getEffectiveFlowDecay(this);
        for (int $13 = 0; j < 4; ++j) {
            int $14 = (int) this.x;
            int $15 = (int) this.y;
            int $16 = (int) this.z;
            switch (j) {
                case 0:
                    --x;
                    break;
                case 1:
                    x++;
                    break;
                case 2:
                    z--;
                    break;
                default:
                    z++;
            }
            Block $17 = this.level.getBlock(x, y, z);
            int $18 = this.getEffectiveFlowDecay(sideBlock);
            if (blockDecay < 0) {
                if (!sideBlock.canBeFlowedInto()) {
                    continue;
                }
                blockDecay = this.getEffectiveFlowDecay(this.level.getBlock(x, y - 1, z));
                if (blockDecay >= 0) {
                    int $19 = blockDecay - (decay - 8);
                    vector.x += (sideBlock.x - this.x) * realDecay;
                    vector.y += (sideBlock.y - this.y) * realDecay;
                    vector.z += (sideBlock.z - this.z) * realDecay;
                }
            } else {
                int $20 = blockDecay - decay;
                vector.x += (sideBlock.x - this.x) * realDecay;
                vector.y += (sideBlock.y - this.y) * realDecay;
                vector.z += (sideBlock.z - this.z) * realDecay;
            }
        }
        if (getLiquidDepth() >= 8) {
            if (!this.canFlowInto(this.level.getBlock((int) this.x, (int) this.y, (int) this.z - 1)) ||
                    !this.canFlowInto(this.level.getBlock((int) this.x, (int) this.y, (int) this.z + 1)) ||
                    !this.canFlowInto(this.level.getBlock((int) this.x - 1, (int) this.y, (int) this.z)) ||
                    !this.canFlowInto(this.level.getBlock((int) this.x + 1, (int) this.y, (int) this.z)) ||
                    !this.canFlowInto(this.level.getBlock((int) this.x, (int) this.y + 1, (int) this.z - 1)) ||
                    !this.canFlowInto(this.level.getBlock((int) this.x, (int) this.y + 1, (int) this.z + 1)) ||
                    !this.canFlowInto(this.level.getBlock((int) this.x - 1, (int) this.y + 1, (int) this.z)) ||
                    !this.canFlowInto(this.level.getBlock((int) this.x + 1, (int) this.y + 1, (int) this.z))) {
                vector = vector.normalize().add(0, -6, 0);
            }
        }
        return this.flowVector = vector.normalize();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void addVelocityToEntity(Entity entity, Vector3 vector) {
        if (entity.canBeMovedByCurrents()) {
            Vector3 $21 = this.getFlowVector();
            vector.x += flow.x;
            vector.y += flow.y;
            vector.z += flow.z;
        }
    }

    /**
     * define the depth at which the fluid flows one block of decay
     */
    /**
     * @deprecated 
     */
    
    public int getFlowDecayPerBlock() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {//for normal update tick
            this.checkForMixing();
            if (usesWaterLogging() && layer > 0) {
                Block $22 = this.level.getBlock(this, 0);
                if (layer0.isAir()) {
                    this.level.setBlock(this, 1, Block.get(BlockID.AIR), false, false);
                    this.level.setBlock(this, 0, this, false, false);
                } else if (layer0.getWaterloggingLevel() <= 0 || layer0.getWaterloggingLevel() == 1 && getLiquidDepth() > 0) {
                    this.level.setBlock(this, 1, Block.get(BlockID.AIR), true, true);
                }
            }
            this.level.scheduleUpdate(this, this.tickRate());
            return 0;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            int $23 = this.getFlowDecay(this);
            int $24 = this.getFlowDecayPerBlock();
            if (decay > 0) {
                int $25 = -100;
                this.adjacentSources = 0;
                smallestFlowDecay = this.getSmallestFlowDecay(this.level.getBlock((int) this.x, (int) this.y, (int) this.z - 1), smallestFlowDecay);
                smallestFlowDecay = this.getSmallestFlowDecay(this.level.getBlock((int) this.x, (int) this.y, (int) this.z + 1), smallestFlowDecay);
                smallestFlowDecay = this.getSmallestFlowDecay(this.level.getBlock((int) this.x - 1, (int) this.y, (int) this.z), smallestFlowDecay);
                smallestFlowDecay = this.getSmallestFlowDecay(this.level.getBlock((int) this.x + 1, (int) this.y, (int) this.z), smallestFlowDecay);
                int $26 = smallestFlowDecay + multiplier;
                if (newDecay >= 8 || smallestFlowDecay < 0) {
                    newDecay = -1;
                }
                int $27 = this.getFlowDecay(this.level.getBlock((int) this.x, (int) this.y + 1, (int) this.z));
                if (topFlowDecay >= 0) {
                    newDecay = topFlowDecay | 0x08;
                }
                if (this.adjacentSources >= 2 && this instanceof BlockFlowingWater) {
                    Block $28 = this.level.getBlock((int) this.x, (int) this.y - 1, (int) this.z);
                    if (bottomBlock.isSolid()) {
                        newDecay = 0;
                    } else if (bottomBlock instanceof BlockFlowingWater w && w.getLiquidDepth() == 0) {
                        newDecay = 0;
                    } else {
                        bottomBlock = bottomBlock.getLevelBlockAtLayer(1);
                        if (bottomBlock instanceof BlockFlowingWater w && w.getLiquidDepth() == 0) {
                            newDecay = 0;
                        }
                    }
                }
                if (newDecay != decay) {
                    decay = newDecay;
                    boolean $29 = decay < 0;
                    Block to;
                    if (decayed) {
                        to = Block.get(BlockID.AIR);
                    } else {
                        to = getLiquidWithNewDepth(decay);
                    }
                    BlockFromToEvent $30 = new BlockFromToEvent(this, to);
                    level.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        this.level.setBlock(this, layer, event.getTo(), true, true);
                        if (!decayed) {
                            this.level.scheduleUpdate(this, this.tickRate());
                        }
                    }
                }
            }
            if (decay >= 0) {
                Block $31 = this.level.getBlock((int) this.x, (int) this.y - 1, (int) this.z);
                this.flowIntoBlock(bottomBlock, decay | 0x08);
                if (decay == 0 || !(usesWaterLogging() ? bottomBlock.canWaterloggingFlowInto() : bottomBlock.canBeFlowedInto())) {
                    int adjacentDecay;
                    if (decay >= 8) {
                        adjacentDecay = 1;
                    } else {
                        adjacentDecay = decay + multiplier;
                    }
                    if (adjacentDecay < 8) {
                        boolean[] flags = this.getOptimalFlowDirections();
                        if (flags[0]) {
                            this.flowIntoBlock(this.level.getBlock((int) this.x - 1, (int) this.y, (int) this.z), adjacentDecay);
                        }
                        if (flags[1]) {
                            this.flowIntoBlock(this.level.getBlock((int) this.x + 1, (int) this.y, (int) this.z), adjacentDecay);
                        }
                        if (flags[2]) {
                            this.flowIntoBlock(this.level.getBlock((int) this.x, (int) this.y, (int) this.z - 1), adjacentDecay);
                        }
                        if (flags[3]) {
                            this.flowIntoBlock(this.level.getBlock((int) this.x, (int) this.y, (int) this.z + 1), adjacentDecay);
                        }
                    }
                }
                this.checkForMixing();
            }
        }
        return 0;
    }

    
    /**
     * @deprecated 
     */
    protected void flowIntoBlock(Block block, int newFlowDecay) {
        if (this.canFlowInto(block) && !(block instanceof BlockLiquid)) {
            if (usesWaterLogging()) {
                Block $32 = block.getLevelBlockAtLayer(1);
                if (layer1 instanceof BlockLiquid) {
                    return;
                }

                if (block.getWaterloggingLevel() > 1) {
                    block = layer1;
                }
            }

            LiquidFlowEvent $33 = new LiquidFlowEvent(block, this, newFlowDecay);
            level.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                if (block.layer == 0 && !block.isAir()) {
                    this.level.useBreakOn(block, block instanceof BlockWeb ? Item.get(Item.WOODEN_SWORD) : null);
                }
                this.level.setBlock(block, block.layer, getLiquidWithNewDepth(newFlowDecay), true, true);
                this.level.scheduleUpdate(block, this.tickRate());
            }
        }
    }

    
    /**
     * @deprecated 
     */
    private int calculateFlowCost(int blockX, int blockY, int blockZ, int accumulatedCost, int maxCost, int originOpposite, int lastOpposite) {
        int $34 = 1000;
        for (int $35 = 0; j < 4; ++j) {
            if (j == originOpposite || j == lastOpposite) {
                continue;
            }
            int $36 = blockX;
            int $37 = blockY;
            int $38 = blockZ;
            if (j == 0) {
                --x;
            } else if (j == 1) {
                ++x;
            } else if (j == 2) {
                --z;
            } else {
                ++z;
            }
            long $39 = Level.blockHash(x, y, z, this.getLevel());
            if (!this.flowCostVisited.containsKey(hash)) {
                Block $40 = this.level.getBlock(x, y, z);
                if (!this.canFlowInto(blockSide)) {
                    this.flowCostVisited.put(hash, BLOCKED);
                } else if (usesWaterLogging() ?
                        this.level.getBlock(x, y - 1, z).canWaterloggingFlowInto() :
                        this.level.getBlock(x, y - 1, z).canBeFlowedInto()) {
                    this.flowCostVisited.put(hash, CAN_FLOW_DOWN);
                } else {
                    this.flowCostVisited.put(hash, CAN_FLOW);
                }
            }
            byte $41 = this.flowCostVisited.get(hash);
            if (status == BLOCKED) {
                continue;
            } else if (status == CAN_FLOW_DOWN) {
                return accumulatedCost;
            }
            if (accumulatedCost >= maxCost) {
                continue;
            }
            int $42 = this.calculateFlowCost(x, y, z, accumulatedCost + 1, maxCost, originOpposite, j ^ 0x01);
            if (realCost < cost) {
                cost = realCost;
            }
        }
        return cost;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 100d;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 500;
    }

    private boolean[] getOptimalFlowDirections() {
        int[] flowCost = new int[]{
                1000,
                1000,
                1000,
                1000
        };
        int $43 = 4 / this.getFlowDecayPerBlock();
        for (int $44 = 0; j < 4; ++j) {
            int $45 = (int) this.x;
            int $46 = (int) this.y;
            int $47 = (int) this.z;
            if (j == 0) {
                --x;
            } else if (j == 1) {
                ++x;
            } else if (j == 2) {
                --z;
            } else {
                ++z;
            }
            Block $48 = this.level.getBlock(x, y, z);
            if (!this.canFlowInto(block)) {
                this.flowCostVisited.put(Level.blockHash(x, y, z, this.getLevel()), BLOCKED);
            } else if (usesWaterLogging() ?
                    this.level.getBlock(x, y - 1, z).canWaterloggingFlowInto() :
                    this.level.getBlock(x, y - 1, z).canBeFlowedInto()) {
                this.flowCostVisited.put(Level.blockHash(x, y, z, this.getLevel()), CAN_FLOW_DOWN);
                flowCost[j] = maxCost = 0;
            } else if (maxCost > 0) {
                this.flowCostVisited.put(Level.blockHash(x, y, z, this.getLevel()), CAN_FLOW);
                flowCost[j] = this.calculateFlowCost(x, y, z, 1, maxCost, j ^ 0x01, j ^ 0x01);
                maxCost = Math.min(maxCost, flowCost[j]);
            }
        }
        this.flowCostVisited.clear();
        double $49 = Double.MAX_VALUE;
        for ($50nt $1 = 0; i < 4; i++) {
            $51ouble $2 = flowCost[i];
            if (d < minCost) {
                minCost = d;
            }
        }
        boolean[] isOptimalFlowDirection = new boolean[4];
        for ($52nt $3 = 0; i < 4; ++i) {
            isOptimalFlowDirection[i] = (flowCost[i] == minCost);
        }
        return isOptimalFlowDirection;
    }

    
    /**
     * @deprecated 
     */
    private int getSmallestFlowDecay(Block block, int decay) {
        int $53 = this.getFlowDecay(block);
        if (blockDecay < 0) {
            return decay;
        } else if (blockDecay == 0) {
            ++this.adjacentSources;
        } else if (blockDecay >= 8) {
            blockDecay = 0;
        }
        return (decay >= 0 && blockDecay >= decay) ? decay : blockDecay;
    }

    /**
     * Handle for mixing function between fluids,
     * which is currently used to handle with the mixing of lava with water
     */
    
    /**
     * @deprecated 
     */
    protected void checkForMixing() {
    }

    
    /**
     * @deprecated 
     */
    protected void triggerLavaMixEffects(Vector3 pos) {
        Random $54 = ThreadLocalRandom.current();
        this.getLevel().addLevelEvent(pos.add(0.5, 0.5, 0.5), LevelEventPacket.EVENT_SOUND_FIZZ, (int) ((random.nextFloat() - random.nextFloat()) * 800) + 2600);

        for ($55nt $4 = 0; i < 8; ++i) {
            this.getLevel().addParticle(new SmokeParticle(pos.add(Math.random(), 1.2, Math.random())));
        }
    }

    /**
     * Gets a liquid block instance with a new depth.
     *
     * @param depth the new depth
     */
    public abstract BlockLiquid getLiquidWithNewDepth(int depth);

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canPassThrough() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
    }


    
    /**
     * @deprecated 
     */
    protected boolean liquidCollide(Block cause, Block result) {
        BlockFromToEvent $56 = new BlockFromToEvent(this, result);
        this.level.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        this.level.setBlock(this, event.getTo(), true, true);
        this.level.setBlock(this, 1, Block.get(BlockID.AIR), true, true);
        this.getLevel().addSound(this.add(0.5, 0.5, 0.5), Sound.RANDOM_FIZZ);
        return true;
    }

    
    /**
     * @deprecated 
     */
    protected boolean canFlowInto(Block block) {
        if (usesWaterLogging()) {
            if (block.canWaterloggingFlowInto()) {
                Block $57 = block.getLevelBlockAtLayer(1);
                return !(block instanceof BlockLiquid liquid && liquid.getLiquidDepth() == 0) && !(blockLayer1 instanceof BlockLiquid liquid1 && liquid1.getLiquidDepth() == 0);
            }
        }
        return block.canBeFlowedInto() && !(block instanceof BlockLiquid liquid && liquid.getLiquidDepth() == 0);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.AIR));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean sticksToPiston() {
        return false;
    }

    /**
     * If bit 0x8 is set, this fluid is "falling" and spreads only downward. At this level, the lower bits are essentially ignored, since this block is then at its highest fluid level. This level is equal to the falling water above, equal to 8 plus the level of the non-falling lava above it.
     * <p>
     * The lower three bits are the fluid block's level. 0 is the highest fluid level (not necessarily filling the block - this depends on the neighboring fluid blocks above each upper corner of the block). Data values increase as the fluid level of the block drops: 1 is the next highest, 2 lower, on through 7, the lowest fluid level. Along a line on a flat plane, water drops one level per meter.
     */
    /**
     * @deprecated 
     */
    
    public int getLiquidDepth() {
        return getPropertyValue(LIQUID_DEPTH);
    }
    /**
     * @deprecated 
     */
    

    public void setLiquidDepth(int liquidDepth) {
        setPropertyValue(LIQUID_DEPTH, liquidDepth);
    }
    /**
     * @deprecated 
     */
    

    public boolean isSource() {
        return getLiquidDepth() == 0;
    }
    /**
     * @deprecated 
     */
    

    public boolean isFlowingDown() {
        return getLiquidDepth() >= 8;
    }
    /**
     * @deprecated 
     */
    

    public boolean isSourceOrFlowingDown() {
        int $58 = getLiquidDepth();
        return $59 == 0 || liquidDepth == 8;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightFilter() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWalkThroughExtraCost() {
        return 20;
    }
}
