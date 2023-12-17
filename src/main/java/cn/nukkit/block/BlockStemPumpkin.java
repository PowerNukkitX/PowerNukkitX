package cn.nukkit.block;

import cn.nukkit.item.ItemID;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;

/**
 * @author Pub4Game
 * @since 15.01.2016
 * 
 * @apiNote Implements {@link Faceable} only on PowerNukkit since 1.3.0.0-PN
 * and extends {@link BlockCropsStem} instead of {@link BlockCrops} only in PowerNukkit since 1.4.0.0-PN
 */



public class BlockStemPumpkin extends BlockCropsStem implements Faceable {

    public BlockStemPumpkin() {
        this(0);
    }

    public BlockStemPumpkin(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PUMPKIN_STEM;
    }


    @Override
    public int getFruitId() {
        return PUMPKIN;
    }


    @Override
    public int getSeedsId() {
        return ItemID.PUMPKIN_SEEDS;
    }

    @Override
    public String getName() {
        return "Pumpkin Stem";
    }

    ("Implements Faceable only on PowerNukkit since 1.3.0.0-PN")

    @Override
    public BlockFace getBlockFace() {
        return super.getBlockFace();
    }

    @Override


    public void setBlockFace(BlockFace face) {
        super.setBlockFace(face);
    }
}
