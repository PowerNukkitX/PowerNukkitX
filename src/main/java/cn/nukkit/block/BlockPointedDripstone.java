package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.blockproperty.value.CauldronLiquid;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockFallEvent;
import cn.nukkit.event.block.CauldronFilledByDrippingLiquidEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Random;

import static cn.nukkit.potion.Effect.getEffect;

/**
 * @author CoolLoong
 * @since 02.13.2022
 */


public class BlockPointedDripstone extends BlockFallableMeta {


    private static final ArrayBlockProperty<String> DRIPSTONE_THICKNESS = new ArrayBlockProperty<>("dripstone_thickness", false,
            new String[]{
                    "base",
                    "frustum",
                    "merge",
                    "middle",
                    "tip"
            }
    );


    private static final BooleanBlockProperty HANGING = new BooleanBlockProperty("hanging", false);


    public static final BlockProperties PROPERTIES = new BlockProperties(DRIPSTONE_THICKNESS, HANGING);


    public boolean isHanging(){
        return getPropertyValue(HANGING);
    }


    public void setHanging(boolean value){
        setPropertyValue(HANGING, value);
    }


    public void setThickness(String value){
        setPropertyValue(DRIPSTONE_THICKNESS, value);
    }


    public String getThickness(){
        return getPropertyValue(DRIPSTONE_THICKNESS);
    }

    public BlockPointedDripstone() {
    }

    public BlockPointedDripstone(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return POINTED_DRIPSTONE;
    }

    @Override
    public String getName() {
        return "Pointed Drip Stone";
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getResistance() {
        return 3;
    }


    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }


    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM && this.getThickness().equals("tip")){
            Random rand = new Random();
            double nextDouble = rand.nextDouble();
            if (nextDouble <= 0.011377778){
                this.grow();
            }

            drippingLiquid();
        }
        boolean hanging = getPropertyValue(HANGING);
        if (!hanging) {
            Block down = down();
            if (!down.isSolid()) {
                this.getLevel().useBreakOn(this);
            }
        }
        tryDrop(hanging);
        return 0;
    }


    public void tryDrop(boolean hanging) {
        if (!hanging) return;
        boolean AirUp = false;
        Block blockUp = this.getBlock();
        while (blockUp.getSide(BlockFace.UP).getId() == POINTED_DRIPSTONE) {
            blockUp = blockUp.getSide(BlockFace.UP);
        }
        if (!blockUp.getSide(BlockFace.UP).isSolid())
            AirUp = true;
        if (AirUp) {
            BlockFallEvent event = new BlockFallEvent(this);
            Server.getInstance().getPluginManager().callEvent(event);
            if (event.isCancelled()){
                return;
            }
            BlockPointedDripstone block = (BlockPointedDripstone) blockUp;
            block.drop(new CompoundTag().putBoolean("BreakOnGround", true));
            while (block.getSide(BlockFace.DOWN).getId() == POINTED_DRIPSTONE) {
                block = (BlockPointedDripstone) block.getSide(BlockFace.DOWN);
                block.drop(new CompoundTag().putBoolean("BreakOnGround", true));
            }
        }
    }

    @Override
    public boolean place(@Nullable Item item, @NotNull Block block, @Nullable Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        int placeX = block.getFloorX();
        int placeY = block.getFloorY();
        int placeZ = block.getFloorZ();
        int upBlockID = level.getBlockIdAt(placeX, placeY + 1, placeZ);
        int downBlockID = level.getBlockIdAt(placeX, placeY - 1, placeZ);
        if (upBlockID == AIR && downBlockID == AIR) return false;
        /*    "up" define is exist drip stone in block above,"down" is Similarly.
              up   down
          1   yes   yes
          2   yes   no
          3   no    no
          4   no    yes
        */
        int state = (upBlockID == POINTED_DRIPSTONE) ? (downBlockID == POINTED_DRIPSTONE ? 1 : 2) : (downBlockID != POINTED_DRIPSTONE ? 3 : 4);
        boolean hanging = false;
        switch (state) {
            case 1 -> {
                setMergeBlock(placeX, placeY, placeZ, false);
                setBlockThicknessStateAt(placeX, placeY + 1, placeZ, true, "merge");
            }
            case 2 -> {
                if (level.getBlockIdAt(placeX, placeY - 1, placeZ) != AIR) {
                    if (face.equals(BlockFace.UP)) {
                        setBlockThicknessStateAt(placeX, placeY + 1, placeZ, true, "merge");
                        setMergeBlock(placeX, placeY, placeZ, false);
                    } else {
                        setTipBlock(placeX, placeY, placeZ, true);
                        setAddChange(placeX, placeY, placeZ, true);
                    }
                    return true;
                }
                hanging = true;
            }
            case 3 -> {
                if (face.equals(BlockFace.UP)) {
                    setTipBlock(placeX, placeY, placeZ, false);
                } else {
                    setTipBlock(placeX, placeY, placeZ, true);
                }
                return true;
            }
            case 4 -> {
                if (level.getBlockIdAt(placeX, placeY + 1, placeZ) != AIR) {
                    if (face.equals(BlockFace.DOWN)) {
                        setMergeBlock(placeX, placeY, placeZ, true);
                        setBlockThicknessStateAt(placeX, placeY - 1, placeZ, false, "merge");
                    } else {
                        setTipBlock(placeX, placeY, placeZ, false);
                        setAddChange(placeX, placeY, placeZ, false);
                    }
                    return true;
                }
            }
        }
        setAddChange(placeX, placeY, placeZ, hanging);

        if (state == 1) return true;
        setTipBlock(placeX, placeY, placeZ, hanging);
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        int x = this.getFloorX();
        int y = this.getFloorY();
        int z = this.getFloorZ();
        level.setBlock(x, y, z, Block.get(AIR), true, true);
        boolean hanging = getPropertyValue(HANGING);
        String thickness = getPropertyValue(DRIPSTONE_THICKNESS);
        if (thickness.equals("merge")) {
            if (!hanging) {
                setBlockThicknessStateAt(x, y + 1, z, true, "tip");
            } else setBlockThicknessStateAt(x, y - 1, z, false, "tip");
        }
        if (!hanging) {
            int length = getPointedDripStoneLength(x, y, z, false);
            if (length > 0) {
                Block downBlock = down();
                for (int i = 0; i <= length - 1; ++i) {
                    level.setBlock(downBlock.down(i), Block.get(AIR), false, false);
                }
                for (int i = length - 1; i >= 0; --i) {
                    place(null, downBlock.down(i), null, BlockFace.DOWN, 0, 0, 0, null);
                }
            }
        }
        if (hanging) {
            int length = getPointedDripStoneLength(x, y, z, true);
            if (length > 0) {
                Block upBlock = up();
                for (int i = 0; i <= length - 1; ++i) {
                    level.setBlock(upBlock.up(i), Block.get(AIR), false, false);
                }
                for (int i = length - 1; i >= 0; --i) {
                    place(null, upBlock.up(i), null, BlockFace.DOWN, 0, 0, 0, null);
                }
            }
        }

        return true;
    }

    @Override
    public void onEntityFallOn(Entity entity, float fallDistance) {
        if (this.level.gameRules.getBoolean(GameRule.FALL_DAMAGE) && this.getPropertyValue(DRIPSTONE_THICKNESS).equals("tip") && !this.getPropertyValue(HANGING)) {
            int jumpBoost = entity.hasEffect(Effect.JUMP_BOOST) ? (getEffect(Effect.JUMP_BOOST).getAmplifier() + 1) : 0;
            float damage = (fallDistance - jumpBoost) * 2 - 2;
            if (damage > 0)
                entity.attack(new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.FALL, damage));
        }
    }


    @Override
    public boolean useDefaultFallDamage() {
        return false;
    }


    protected void setTipBlock(int x, int y, int z, boolean hanging) {
        this.setPropertyValue(DRIPSTONE_THICKNESS, "tip");
        this.setPropertyValue(HANGING, hanging);
        this.getLevel().setBlock(x, y, z, this, true, true);
    }


    protected void setMergeBlock(int x, int y, int z, boolean hanging) {
        this.setPropertyValue(DRIPSTONE_THICKNESS, "merge");
        this.setPropertyValue(HANGING, hanging);
        this.getLevel().setBlock(x, y, z, this, true, true);
    }


    protected void setBlockThicknessStateAt(int x, int y, int z, boolean hanging, String thickness) {
        BlockState blockState;
        this.setPropertyValue(DRIPSTONE_THICKNESS, thickness);
        this.setPropertyValue(HANGING, hanging);
        blockState = this.getBlockState();
        level.setBlockStateAt(x, y, z, blockState);
    }


    protected int getPointedDripStoneLength(int x, int y, int z, boolean hanging) {
        if (hanging) {
            for (int j = y + 1; j < 320; ++j) {
                int blockId = level.getBlockIdAt(x, j, z);
                if (blockId != POINTED_DRIPSTONE) {
                    return j - y - 1;
                }
            }
        } else {
            for (int j = y - 1; j > -64; --j) {
                int blockId = level.getBlockIdAt(x, j, z);
                if (blockId != POINTED_DRIPSTONE) {
                    return y - j - 1;
                }
            }
        }
        return 0;
    }


    protected void setAddChange(int x, int y, int z, boolean hanging) {
        int length = getPointedDripStoneLength(x, y, z, hanging);
        int k2 = !hanging ? -2 : 2;
        int k1 = !hanging ? -1 : 1;
        if (length == 1) {
            setBlockThicknessStateAt(x, y + k1, z, hanging, "frustum");
        }
        if (length == 2) {
            setBlockThicknessStateAt(x, y + k2, z, hanging, "base");
            setBlockThicknessStateAt(x, y + k1, z, hanging, "frustum");
        }
        if (length >= 3) {
            setBlockThicknessStateAt(x, y + k2, z, hanging, "middle");
            setBlockThicknessStateAt(x, y + k1, z, hanging, "frustum");
        }
    }


    public void grow() {
        BlockFace face = this.isHanging() ? BlockFace.DOWN : BlockFace.UP;
        Block target = this.getSide(face);
        if (target.getId() == Block.AIR) {
            this.place(null, target, null, face, 0, 0, 0, null);
        }
    }


    public void drippingLiquid(){//features according to https://zh.minecraft.wiki/w/%E6%BB%B4%E6%B0%B4%E7%9F%B3%E9%94%A5
        if (this.getBlock(this,1) instanceof BlockLiquid || !this.getThickness().equals("tip") || !this.isHanging()) {
            return;
        }
        Block highestPDS = this;
        int height = 1;
        while(highestPDS.getSide(BlockFace.UP) instanceof BlockPointedDripstone){
            highestPDS = highestPDS.getSide(BlockFace.UP);
            height++;
        }

        boolean isWaterloggingBlock = false;
        if (height >= 11 ||
            !(highestPDS.getSide(BlockFace.UP,2) instanceof BlockLiquid ||
            highestPDS.getSide(BlockFace.UP,2).getLevelBlockAtLayer(1).getId() == BlockID.FLOWING_WATER)
        ){
            return;
        }

        if (highestPDS.getSide(BlockFace.UP,2).getLevelBlockAtLayer(1).getId() == BlockID.FLOWING_WATER){
            isWaterloggingBlock = true;
        }

        Block tmp = this;
        BlockCauldron cauldron;
        while(tmp.getSide(BlockFace.DOWN) instanceof BlockAir){
            tmp = tmp.getSide(BlockFace.DOWN);
        }
        if (tmp.getSide(BlockFace.DOWN) instanceof BlockCauldron){
            cauldron = (BlockCauldron) tmp.getSide(BlockFace.DOWN);
        }else{
            return;
        }

        Random rand = new Random();
        double nextDouble;
        Block filledWith = isWaterloggingBlock ? highestPDS.getSideAtLayer(1,BlockFace.UP,2) : highestPDS.getSide(BlockFace.UP,2);
        switch (filledWith.getId()) {
            case FLOWING_LAVA -> {
                nextDouble = rand.nextDouble();
                if ((cauldron.getCauldronLiquid() == CauldronLiquid.LAVA || cauldron.isEmpty()) && cauldron.getFillLevel() < 6 && nextDouble <= 15.0 / 256.0) {
                    CauldronFilledByDrippingLiquidEvent event = new CauldronFilledByDrippingLiquidEvent(cauldron, CauldronLiquid.LAVA, 1);
                    Server.getInstance().getPluginManager().callEvent(event);
                    if (event.isCancelled())
                        return;
                    cauldron.setCauldronLiquid(event.getLiquid());
                    cauldron.setFillLevel(cauldron.getFillLevel() + event.getLiquidLevelIncrement());
                    cauldron.level.setBlock(cauldron, cauldron, true, true);
                    this.getLevel().addSound(this.add(0.5, 1, 0.5), Sound.CAULDRON_DRIP_LAVA_POINTED_DRIPSTONE);
                }
            }
            case FLOWING_WATER -> {
                nextDouble = rand.nextDouble();
                if ((cauldron.getCauldronLiquid() == CauldronLiquid.WATER || cauldron.isEmpty()) && cauldron.getFillLevel() < 6 && nextDouble <= 45.0 / 256.0) {
                    CauldronFilledByDrippingLiquidEvent event = new CauldronFilledByDrippingLiquidEvent(cauldron, CauldronLiquid.WATER, 1);
                    Server.getInstance().getPluginManager().callEvent(event);
                    if (event.isCancelled())
                        return;
                    cauldron.setCauldronLiquid(event.getLiquid());
                    cauldron.setFillLevel(cauldron.getFillLevel() + event.getLiquidLevelIncrement());
                    cauldron.level.setBlock(cauldron, cauldron, true, true);
                    this.getLevel().addSound(this.add(0.5, 1, 0.5), Sound.CAULDRON_DRIP_WATER_POINTED_DRIPSTONE);
                }
            }
        }
    }

}
