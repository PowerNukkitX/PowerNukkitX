package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

import static cn.nukkit.potion.Effect.getEffect;

/**
 * @author CoolLoong
 * @since 02.13.2022
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockPointedDripstone extends BlockFallableMeta {
    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    private static final ArrayBlockProperty<String> DRIPSTONE_THICKNESS = new ArrayBlockProperty<>("dripstone_thickness", false,
            new String[]{
                    "base",
                    "frustum",
                    "merge",
                    "middle",
                    "tip"
            }
    );

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    private static final IntBlockProperty HANGING = new IntBlockProperty("hanging", false, 1, 0);

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    public static final BlockProperties PROPERTIES = new BlockProperties(DRIPSTONE_THICKNESS, HANGING);

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    public int isHanging(){
        return getPropertyValue(HANGING);
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    public void setHanging(int value){
        setPropertyValue(HANGING, value);
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    public void setThickness(String value){
        setPropertyValue(DRIPSTONE_THICKNESS, value);
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    public String getThickness(){
        return getPropertyValue(DRIPSTONE_THICKNESS);
    }

    public BlockPointedDripstone() {
    }

    public BlockPointedDripstone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POINTED_DRIPSTONE;
    }

    @Override
    public String getName() {
        return "Pointed Drip Stone";
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    @Nonnull
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

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @PowerNukkitOnly
    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM && this.getThickness().equals("tip")){
            Random rand = new Random();
            double nextDouble = rand.nextDouble();
            if (0 <= nextDouble && nextDouble <= 0.011377778){
                this.grow();
            }

            drippingLiquid();
        }
        int hanging = getPropertyValue(HANGING);
        if (hanging == 0) {
            Block down = down();
            if (!down.isSolid()) {
                this.getLevel().useBreakOn(this);
            }
        }
        tryDrop(hanging);
        return 0;
    }

    @PowerNukkitOnly
    public void tryDrop(int hanging) {
        if (hanging == 0) return;
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
    public boolean place(@Nullable Item item, @Nonnull Block block, @Nullable Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
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
        int hanging = 0;
        switch (state) {
            case 1 -> {
                setMergeBlock(placeX, placeY, placeZ, 0);
                setBlockThicknessStateAt(placeX, placeY + 1, placeZ, 1, "merge");
            }
            case 2 -> {
                if (level.getBlockIdAt(placeX, placeY - 1, placeZ) != AIR) {
                    if (face.equals(BlockFace.UP)) {
                        setBlockThicknessStateAt(placeX, placeY + 1, placeZ, 1, "merge");
                        setMergeBlock(placeX, placeY, placeZ, 0);
                    } else {
                        setTipBlock(placeX, placeY, placeZ, 1);
                        setAddChange(placeX, placeY, placeZ, 1);
                    }
                    return true;
                }
                hanging = 1;
            }
            case 3 -> {
                if (downBlockID != AIR) {
                    setTipBlock(placeX, placeY, placeZ, 0);
                } else {
                    setTipBlock(placeX, placeY, placeZ, 1);
                }
                return true;
            }
            case 4 -> {
                if (level.getBlockIdAt(placeX, placeY + 1, placeZ) != AIR) {
                    if (face.equals(BlockFace.DOWN)) {
                        setMergeBlock(placeX, placeY, placeZ, 1);
                        setBlockThicknessStateAt(placeX, placeY - 1, placeZ, 0, "merge");
                    } else {
                        setTipBlock(placeX, placeY, placeZ, 0);
                        setAddChange(placeX, placeY, placeZ, 0);
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
        int hanging = getPropertyValue(HANGING);
        String thickness = getPropertyValue(DRIPSTONE_THICKNESS);
        if (thickness.equals("merge")) {
            if (hanging == 0) {
                setBlockThicknessStateAt(x, y + 1, z, 1, "tip");
            } else setBlockThicknessStateAt(x, y - 1, z, 0, "tip");
        }
        if (hanging == 0) {
            int length = getPointedDripStoneLength(x, y, z, 0);
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
        if (hanging == 1) {
            int length = getPointedDripStoneLength(x, y, z, 1);
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
        if (this.level.gameRules.getBoolean(GameRule.FALL_DAMAGE) && this.getPropertyValue(DRIPSTONE_THICKNESS).equals("tip") && this.getPropertyValue(HANGING) == 0) {
            int jumpBoost = entity.hasEffect(Effect.JUMP_BOOST) ? (getEffect(Effect.JUMP_BOOST).getAmplifier() + 1) : 0;
            float damage = (fallDistance - jumpBoost) * 2 - 2;
            if (damage > 0)
                entity.attack(new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.FALL, damage));
        }
    }

    @Since("1.6.0.0-PNX")
    @PowerNukkitOnly
    @Override
    public boolean useDefaultFallDamage() {
        return false;
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    protected void setTipBlock(int x, int y, int z, int hanging) {
        this.setPropertyValue(DRIPSTONE_THICKNESS, "tip");
        this.setPropertyValue(HANGING, hanging);
        this.getLevel().setBlock(x, y, z, this, true, true);
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    protected void setMergeBlock(int x, int y, int z, int hanging) {
        this.setPropertyValue(DRIPSTONE_THICKNESS, "merge");
        this.setPropertyValue(HANGING, hanging);
        this.getLevel().setBlock(x, y, z, this, true, true);
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    protected void setBlockThicknessStateAt(int x, int y, int z, int hanging, String thickness) {
        BlockState blockState;
        this.setPropertyValue(DRIPSTONE_THICKNESS, thickness);
        this.setPropertyValue(HANGING, hanging);
        blockState = this.getCurrentState();
        level.setBlockStateAt(x, y, z, blockState);
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    protected int getPointedDripStoneLength(int x, int y, int z, int hanging) {
        if (hanging == 1) {
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

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    protected void setAddChange(int x, int y, int z, int hanging) {
        int length = getPointedDripStoneLength(x, y, z, hanging);
        int k2 = (hanging == 0) ? -2 : 2;
        int k1 = (hanging == 0) ? -1 : 1;
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


    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    public void grow() {
        this.place(null, this.getSide(this.isHanging() == 1 ? BlockFace.DOWN : BlockFace.UP), null, this.isHanging() == 1 ? BlockFace.DOWN : BlockFace.UP, 0, 0, 0, null);
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    public void drippingLiquid(){//features according to https://minecraft.fandom.com/zh/wiki/%E6%BB%B4%E6%B0%B4%E7%9F%B3%E9%94%A5
        if (this.getBlock(this,1) instanceof BlockLiquid || !this.getThickness().equals("tip") || this.isHanging() != 1) {
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
        BlockCauldron cauldron = null;
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
        switch (filledWith.getId()){
            case FLOWING_LAVA:
                nextDouble = rand.nextDouble();
                if ((cauldron.getCauldronLiquid() == CauldronLiquid.LAVA || cauldron.isEmpty()) && cauldron.getFillLevel() < 6 && nextDouble >= 0 && nextDouble <= 15.0/256.0) {
                    CauldronFilledByDrippingLiquidEvent event = new CauldronFilledByDrippingLiquidEvent(cauldron, CauldronLiquid.LAVA,1);
                    Server.getInstance().getPluginManager().callEvent(event);
                    if(event.isCancelled())
                        return;
                    cauldron.setCauldronLiquid(event.getLiquid());
                    cauldron.setFillLevel(cauldron.getFillLevel() + event.getLiquidLevelIncrement());
                    cauldron.level.setBlock(cauldron,cauldron,true,true);
                    this.getLevel().addSound(this.add(0.5, 1, 0.5), Sound.CAULDRON_DRIP_LAVA_POINTED_DRIPSTONE);
                }
                break;
            case FLOWING_WATER:
                nextDouble = rand.nextDouble();
                if ((cauldron.getCauldronLiquid() == CauldronLiquid.WATER || cauldron.isEmpty()) && cauldron.getFillLevel() < 6 && nextDouble >= 0 && nextDouble <= 45.0/256.0) {
                    CauldronFilledByDrippingLiquidEvent event = new CauldronFilledByDrippingLiquidEvent(cauldron, CauldronLiquid.WATER,1);
                    Server.getInstance().getPluginManager().callEvent(event);
                    if(event.isCancelled())
                        return;
                    cauldron.setCauldronLiquid(event.getLiquid());
                    cauldron.setFillLevel(cauldron.getFillLevel() + event.getLiquidLevelIncrement());
                    cauldron.level.setBlock(cauldron,cauldron,true,true);
                    this.getLevel().addSound(this.add(0.5, 1, 0.5), Sound.CAULDRON_DRIP_WATER_POINTED_DRIPSTONE);
                }
                break;
        }
    }
}
