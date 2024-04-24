package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.event.level.StructureGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.HugeTreesGenerator;
import cn.nukkit.level.generator.object.NewJungleTree;
import cn.nukkit.level.generator.object.ObjectDarkOakTree;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.level.generator.object.ObjectJungleBigTree;
import cn.nukkit.level.generator.object.ObjectSavannaTree;
import cn.nukkit.level.generator.object.legacytree.LegacyBigSpruceTree;
import cn.nukkit.level.generator.object.legacytree.LegacyTreeGenerator;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.AGE_BIT;

/**
 * @author Angelic47 (Nukkit Project)
 */
public abstract class BlockSapling extends BlockFlowable implements BlockFlowerPot.FlowerPotBlock {
    public BlockSapling(BlockState blockstate) {
        super(blockstate);
    }

    public abstract WoodType getWoodType();

    public boolean isAged() {
        return getPropertyValue(AGE_BIT);
    }

    public void setAged(boolean aged) {
        setPropertyValue(AGE_BIT, aged);
    }

    @Override
    public String getName() {
        return getWoodType().name() + " Sapling";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (BlockFlower.isSupportValid(down())) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }

        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isFertilizer()) { // BoneMeal
            if (player != null && !player.isCreative()) {
                item.count--;
            }

            this.level.addParticle(new BoneMealParticle(this));
            if (ThreadLocalRandom.current().nextFloat() >= 0.45) {
                return true;
            }

            this.grow();

            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!BlockFlower.isSupportValid(down())) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) { //Growth
            if (ThreadLocalRandom.current().nextInt(1, 8) == 1 && getLevel().getFullLight(add(0, 1, 0)) >= BlockCrops.MINIMUM_LIGHT_LEVEL) {
                if (isAged()) {
                    this.grow();
                } else {
                    setAged(true);
                    this.getLevel().setBlock(this, this, true);
                    return Level.BLOCK_UPDATE_RANDOM;
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        return Level.BLOCK_UPDATE_NORMAL;
    }

    private void grow() {
        ObjectGenerator generator = null;
        boolean bigTree = false;

        Vector3 vector3 = new Vector3(this.x, this.y - 1, this.z);

        switch (getWoodType()) {
            case JUNGLE:
                Vector2 vector2;
                if ((vector2 = this.findSaplings(WoodType.JUNGLE)) != null) {
                    vector3 = this.add(vector2.getFloorX(), 0, vector2.getFloorY());
                    generator = new ObjectJungleBigTree(10, 20,
                            BlockJungleLog.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS, BlockFace.Axis.Y),
                            BlockJungleLeaves.PROPERTIES.getDefaultState()
                    );
                    bigTree = true;
                }

                if (!bigTree) {
                    generator = new NewJungleTree(4, 7);
                    vector3 = this.add(0, 0, 0);
                }
                break;
            case ACACIA:
                generator = new ObjectSavannaTree();
                vector3 = this.add(0, 0, 0);
                break;
            case DARK_OAK:
                if ((vector2 = this.findSaplings(WoodType.DARK_OAK)) != null) {
                    vector3 = this.add(vector2.getFloorX(), 0, vector2.getFloorY());
                    generator = new ObjectDarkOakTree();
                    bigTree = true;
                }

                if (!bigTree) {
                    return;
                }
                break;
            case SPRUCE:
                if ((vector2 = this.findSaplings(WoodType.SPRUCE)) != null) {
                    vector3 = this.add(vector2.getFloorX(), 0, vector2.getFloorY());
                    generator = new HugeTreesGenerator(0, 0, null, null) {
                        @Override
                        public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {
                            var object = new LegacyBigSpruceTree(0.75f, 4);
                            object.setRandomTreeHeight(rand);
                            if (!this.ensureGrowable(level, rand, position, object.getTreeHeight())) {
                                return false;
                            }
                            object.placeObject(level, position.getFloorX(), position.getFloorY(), position.getFloorZ(), rand);
                            return true;
                        }
                    };
                    bigTree = true;
                }

                if (bigTree) {
                    break;
                }
            default:
                BlockManager blockManager = new BlockManager(this.level);
                LegacyTreeGenerator.growTree(blockManager, this.getFloorX(), this.getFloorY(), this.getFloorZ(), RandomSourceProvider.create(), getWoodType(), false);
                StructureGrowEvent ev = new StructureGrowEvent(this, blockManager.getBlocks());
                this.level.getServer().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return;
                }
                if (this.level.getBlock(vector3).getId().equals(BlockID.DIRT_WITH_ROOTS)) {
                    this.level.setBlock(vector3, Block.get(BlockID.DIRT));
                }
                blockManager.applySubChunkUpdate(ev.getBlockList());
//                for (Block block : ev.getBlockList()) {
//                    this.level.setBlock(block, block);
//                }
                return;
        }

        if (bigTree) {
            this.level.setBlock(vector3, get(AIR), true, false);
            this.level.setBlock(vector3.add(1, 0, 0), get(AIR), true, false);
            this.level.setBlock(vector3.add(0, 0, 1), get(AIR), true, false);
            this.level.setBlock(vector3.add(1, 0, 1), get(AIR), true, false);
        } else {
            this.level.setBlock(this, get(AIR), true, false);
        }

        BlockManager blockManager = new BlockManager(this.level);
        boolean success = generator.generate(blockManager, RandomSourceProvider.create(), vector3);
        StructureGrowEvent ev = new StructureGrowEvent(this, blockManager.getBlocks());
        this.level.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled() || !success) {
            if (bigTree) {
                this.level.setBlock(vector3, this, true, false);
                this.level.setBlock(vector3.add(1, 0, 0), this, true, false);
                this.level.setBlock(vector3.add(0, 0, 1), this, true, false);
                this.level.setBlock(vector3.add(1, 0, 1), this, true, false);
            } else {
                this.level.setBlock(this, this, true, false);
            }
            return;
        }

        if (this.level.getBlock(vector3).getId().equals(BlockID.DIRT_WITH_ROOTS)) {
            this.level.setBlock(vector3, Block.get(BlockID.DIRT));
        }
        blockManager.applySubChunkUpdate(ev.getBlockList());
    }

    private Vector2 findSaplings(WoodType type) {
        List<List<Vector2>> validVectorsList = new ArrayList<>();
        validVectorsList.add(Arrays.asList(new Vector2(0, 0), new Vector2(1, 0), new Vector2(0, 1), new Vector2(1, 1)));
        validVectorsList.add(Arrays.asList(new Vector2(0, 0), new Vector2(-1, 0), new Vector2(0, -1), new Vector2(-1, -1)));
        validVectorsList.add(Arrays.asList(new Vector2(0, 0), new Vector2(1, 0), new Vector2(0, -1), new Vector2(1, -1)));
        validVectorsList.add(Arrays.asList(new Vector2(0, 0), new Vector2(-1, 0), new Vector2(0, 1), new Vector2(-1, 1)));
        for (List<Vector2> validVectors : validVectorsList) {
            boolean correct = true;
            for (Vector2 vector2 : validVectors) {
                if (!this.isSameType(this.add(vector2.x, 0, vector2.y), type))
                    correct = false;
            }
            if (correct) {
                int lowestX = 0;
                int lowestZ = 0;
                for (Vector2 vector2 : validVectors) {
                    if (vector2.getFloorX() < lowestX)
                        lowestX = vector2.getFloorX();
                    if (vector2.getFloorY() < lowestZ)
                        lowestZ = vector2.getFloorY();
                }
                return new Vector2(lowestX, lowestZ);
            }
        }
        return null;
    }

    public boolean isSameType(Vector3 pos, WoodType type) {
        Block block = this.level.getBlock(pos);
        return block.getId().equals(this.getId()) && ((BlockSapling) block).getWoodType() == type;
    }

    @Override
    public boolean isFertilizable() {
        return true;
    }

}
