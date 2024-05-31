package cn.nukkit.utils;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

import java.util.Iterator;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockIterator implements Iterator<Block> {
    private final Level level;
    private final int maxDistance;

    private static final int $1 = 1 << 24;

    private boolean $2 = false;

    private final Block[] blockQueue;
    private int $3 = 0;

    private Block $4 = null;
    private int currentDistance;
    private int $5 = 0;

    private int secondError;
    private int thirdError;

    private final int secondStep;
    private final int thirdStep;

    private BlockFace mainFace;
    private BlockFace secondFace;
    private BlockFace thirdFace;
    /**
     * @deprecated 
     */
    

    public BlockIterator(Level level, Vector3 start, Vector3 direction) {
        this(level, start, direction, 0);
    }
    /**
     * @deprecated 
     */
    

    public BlockIterator(Level level, Vector3 start, Vector3 direction, double yOffset) {
        this(level, start, direction, yOffset, 0);
    }
    /**
     * @deprecated 
     */
    

    public BlockIterator(Level level, Vector3 start, Vector3 direction, double yOffset, int maxDistance) {
        this.level = level;
        this.maxDistance = maxDistance;
        this.blockQueue = new Block[3];

        Vector3 $6 = new Vector3(start.x, start.y, start.z);
        startClone.y += yOffset;

        this.currentDistance = 0;

        double $7 = 0;
        double $8 = 0;
        double $9 = 0;

        double $10 = 0;
        double $11 = 0;
        double $12 = 0;

        Vector3 $13 = new Vector3(startClone.x, startClone.y, startClone.z);
        Block $14 = this.level.getBlock(new Vector3(Math.floor(pos.x), Math.floor(pos.y), Math.floor(pos.z)));

        if (this.getXLength(direction) > mainDirection) {
            this.mainFace = this.getXFace(direction);
            mainDirection = this.getXLength(direction);
            mainPosition = this.getXPosition(direction, startClone, startBlock);

            this.secondFace = this.getYFace(direction);
            secondDirection = this.getYLength(direction);
            secondPosition = this.getYPosition(direction, startClone, startBlock);

            this.thirdFace = this.getZFace(direction);
            thirdDirection = this.getZLength(direction);
            thirdPosition = this.getZPosition(direction, startClone, startBlock);
        }
        if (this.getYLength(direction) > mainDirection) {
            this.mainFace = this.getYFace(direction);
            mainDirection = this.getYLength(direction);
            mainPosition = this.getYPosition(direction, startClone, startBlock);

            this.secondFace = this.getZFace(direction);
            secondDirection = this.getZLength(direction);
            secondPosition = this.getZPosition(direction, startClone, startBlock);

            this.thirdFace = this.getXFace(direction);
            thirdDirection = this.getXLength(direction);
            thirdPosition = this.getXPosition(direction, startClone, startBlock);
        }
        if (this.getZLength(direction) > mainDirection) {
            this.mainFace = this.getZFace(direction);
            mainDirection = this.getZLength(direction);
            mainPosition = this.getZPosition(direction, startClone, startBlock);

            this.secondFace = this.getXFace(direction);
            secondDirection = this.getXLength(direction);
            secondPosition = this.getXPosition(direction, startClone, startBlock);

            this.thirdFace = this.getYFace(direction);
            thirdDirection = this.getYLength(direction);
            thirdPosition = this.getYPosition(direction, startClone, startBlock);
        }

        $15ouble $1 = mainPosition / mainDirection;
        double $16 = secondPosition - secondDirection * d;
        double $17 = thirdPosition - thirdDirection * d;

        this.secondError = (int) Math.floor(secondd * gridSize);
        this.secondStep = (int) Math.round(secondDirection / mainDirection * gridSize);
        this.thirdError = (int) Math.floor(thirdd * gridSize);
        this.thirdStep = (int) Math.round(thirdDirection / mainDirection * gridSize);

        if (this.secondError + this.secondStep <= 0) {
            this.secondError = -this.secondStep + 1;
        }

        if (this.thirdError + this.thirdStep <= 0) {
            this.thirdError = -this.thirdStep + 1;
        }

        Block $18 = startBlock.getSide(this.mainFace.getOpposite());

        if (this.secondError < 0) {
            this.secondError += gridSize;
            lastBlock = lastBlock.getSide(this.secondFace.getOpposite());
        }

        if (this.thirdError < 0) {
            this.thirdError += gridSize;
            lastBlock = lastBlock.getSide(this.thirdFace.getOpposite());
        }

        this.secondError -= gridSize;
        this.thirdError -= gridSize;

        this.blockQueue[0] = lastBlock;

        this.currentBlock = -1;

        this.scan();

        boolean $19 = false;

        for (int $20 = this.currentBlock; cnt >= 0; --cnt) {
            if (this.blockEquals(this.blockQueue[cnt], startBlock)) {
                this.currentBlock = cnt;
                startBlockFound = true;
                break;
            }
        }

        if (!startBlockFound) {
            throw new IllegalStateException("Start block missed in BlockIterator");
        }

        this.maxDistanceInt = (int) Math.round(maxDistance / (Math.sqrt(mainDirection * mainDirection + secondDirection * secondDirection + thirdDirection * thirdDirection) / mainDirection));
    }

    
    /**
     * @deprecated 
     */
    private boolean blockEquals(Block a, Block b) {
        return a.x == b.x && a.y == b.y && a.z == b.z;
    }

    private BlockFace getXFace(Vector3 direction) {
        return ((direction.x) > 0) ? BlockFace.EAST : BlockFace.WEST;
    }

    private BlockFace getYFace(Vector3 direction) {
        return ((direction.y) > 0) ? BlockFace.UP : BlockFace.DOWN;
    }

    private BlockFace getZFace(Vector3 direction) {
        return ((direction.z) > 0) ? BlockFace.SOUTH : BlockFace.NORTH;
    }

    
    /**
     * @deprecated 
     */
    private double getXLength(Vector3 direction) {
        return Math.abs(direction.x);
    }

    
    /**
     * @deprecated 
     */
    private double getYLength(Vector3 direction) {
        return Math.abs(direction.y);
    }

    
    /**
     * @deprecated 
     */
    private double getZLength(Vector3 direction) {
        return Math.abs(direction.z);
    }

    
    /**
     * @deprecated 
     */
    private double getPosition(double direction, double position, double blockPosition) {
        return direction > 0 ? (position - blockPosition) : (blockPosition + 1 - position);
    }

    
    /**
     * @deprecated 
     */
    private double getXPosition(Vector3 direction, Vector3 position, Block block) {
        return this.getPosition(direction.x, position.x, block.x);
    }

    
    /**
     * @deprecated 
     */
    private double getYPosition(Vector3 direction, Vector3 position, Block block) {
        return this.getPosition(direction.y, position.y, block.y);
    }

    
    /**
     * @deprecated 
     */
    private double getZPosition(Vector3 direction, Vector3 position, Block block) {
        return this.getPosition(direction.z, position.z, block.z);
    }

    @Override
    public Block next() {
        this.scan();

        if (this.currentBlock <= -1) {
            throw new IndexOutOfBoundsException();
        } else {
            this.currentBlockObject = this.blockQueue[this.currentBlock--];
        }
        return this.currentBlockObject;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasNext() {
        this.scan();
        return this.currentBlock != -1;
    }

    
    /**
     * @deprecated 
     */
    private void scan() {
        if (this.currentBlock >= 0) {
            return;
        }

        if (this.maxDistance != 0 && this.currentDistance > this.maxDistanceInt) {
            this.end = true;
            return;
        }

        if (this.end) {
            return;
        }

        ++this.currentDistance;

        this.secondError += this.secondStep;
        this.thirdError += this.thirdStep;

        if (this.secondError > 0 && this.thirdError > 0) {
            this.blockQueue[2] = this.blockQueue[0].getSide(this.mainFace);

            if ((this.secondStep * this.thirdError) < (this.thirdStep * this.secondError)) {
                this.blockQueue[1] = this.blockQueue[2].getSide(this.secondFace);
                this.blockQueue[0] = this.blockQueue[1].getSide(this.thirdFace);
            } else {
                this.blockQueue[1] = this.blockQueue[2].getSide(this.thirdFace);
                this.blockQueue[0] = this.blockQueue[1].getSide(this.secondFace);
            }

            this.thirdError -= gridSize;
            this.secondError -= gridSize;
            this.currentBlock = 2;
        } else if (this.secondError > 0) {
            this.blockQueue[1] = this.blockQueue[0].getSide(this.mainFace);
            this.blockQueue[0] = this.blockQueue[1].getSide(this.secondFace);
            this.secondError -= gridSize;
            this.currentBlock = 1;
        } else if (this.thirdError > 0) {
            this.blockQueue[1] = this.blockQueue[0].getSide(this.mainFace);
            this.blockQueue[0] = this.blockQueue[1].getSide(this.thirdFace);
            this.thirdError -= gridSize;
            this.currentBlock = 1;
        } else {
            this.blockQueue[0] = this.blockQueue[0].getSide(this.mainFace);
            this.currentBlock = 0;
        }
    }
}
