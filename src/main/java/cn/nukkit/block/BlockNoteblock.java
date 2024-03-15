package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityMusic;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.BlockEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author Snake1999
 * @since 2016/1/17
 */
public class BlockNoteblock extends BlockSolid implements RedstoneComponent, BlockEntityHolder<BlockEntityMusic> {

    public static final BlockProperties PROPERTIES = new BlockProperties(NOTEBLOCK);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockNoteblock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockNoteblock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Note Block";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityMusic> getBlockEntityClass() {
        return BlockEntityMusic.class;
    }

    @Override
    @NotNull
    public String getBlockEntityType() {
        return BlockEntity.MUSIC;
    }

    @Override
    public double getHardness() {
        return 0.8D;
    }

    @Override
    public double getResistance() {
        return 4D;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player != null && player.isSneaking()) {
            return false;
        }
        this.increaseStrength();
        this.emitSound(player);
        return true;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null;
    }

    @Override
    public void onTouch(@NotNull Vector3 vector, @NotNull Item item, @NotNull BlockFace face, float fx, float fy, float fz, @org.jetbrains.annotations.Nullable Player player, @NotNull PlayerInteractEvent.Action action) {
        onUpdate(Level.BLOCK_UPDATE_TOUCH);
        if (player != null && action == Action.LEFT_CLICK_BLOCK && player.isSurvival()) {
            this.emitSound(player);
        }
    }

    public int getStrength() {
        BlockEntityMusic blockEntity = this.getBlockEntity();
        return blockEntity != null ? blockEntity.getPitch() : 0;
    }

    public void increaseStrength() {
        getOrCreateBlockEntity().changePitch();
    }

    public Instrument getInstrument() {
        Block down = this.down();
        if (down instanceof BlockWool) {
            return Instrument.GUITAR;
        }

        if (down instanceof BlockConcretePowder) {
            return Instrument.DRUM;
        }

        if (down instanceof BlockGlass || down instanceof BlockGlassPane) {
            return Instrument.STICKS;
        }

        if (down instanceof BlockLog || down instanceof BlockPlanks || down instanceof BlockFence || down instanceof BlockFenceGate) {
            return Instrument.BASS;
        }

        if (down instanceof BlockBrickBlock || down instanceof BlockNetherBrick || down instanceof BlockConcrete
                || down instanceof BlockHardenedClay || down instanceof BlockDoubleStoneBlockSlab
                || down instanceof BlockDoubleStoneBlockSlab2 || down instanceof BlockDoubleStoneBlockSlab3 || down instanceof BlockDoubleStoneBlockSlab4) {
            return Instrument.BASS_DRUM;
        }

        if (down instanceof BlockWoodenSlab || down instanceof BlockDoubleWoodenSlab) {
            return Instrument.BASS;
        }

        return switch (down.getId()) {
            case GOLD_BLOCK -> Instrument.GLOCKENSPIEL;
            case CLAY, HONEYCOMB_BLOCK -> Instrument.FLUTE;
            case PACKED_ICE -> Instrument.CHIME;
            case BONE_BLOCK -> Instrument.XYLOPHONE;
            case IRON_BLOCK -> Instrument.VIBRAPHONE;
            case SOUL_SAND -> Instrument.COW_BELL;
            case PUMPKIN -> Instrument.DIDGERIDOO;
            case EMERALD_BLOCK -> Instrument.SQUARE_WAVE;
            case HAY_BLOCK -> Instrument.BANJO;
            case GLOWSTONE -> Instrument.ELECTRIC_PIANO;
            case OAK_STAIRS, SPRUCE_STAIRS, BIRCH_STAIRS, JUNGLE_STAIRS,
                    ACACIA_STAIRS, DARK_OAK_STAIRS, CRIMSON_STAIRS, WARPED_STAIRS,
                    WOODEN_DOOR, SPRUCE_DOOR, BIRCH_DOOR, JUNGLE_DOOR,
                    ACACIA_DOOR, DARK_OAK_DOOR, CRIMSON_DOOR, WARPED_DOOR,
                    WOODEN_PRESSURE_PLATE, TRAPDOOR, STANDING_SIGN, WALL_SIGN, NOTEBLOCK, BOOKSHELF, CHEST, TRAPPED_CHEST,
                    CRAFTING_TABLE, JUKEBOX, BROWN_MUSHROOM_BLOCK, RED_MUSHROOM_BLOCK, DAYLIGHT_DETECTOR, DAYLIGHT_DETECTOR_INVERTED, STANDING_BANNER, WALL_BANNER ->
                    Instrument.BASS;
            case SAND, GRAVEL -> Instrument.DRUM;
            case BEACON, SEA_LANTERN -> Instrument.STICKS;
            case STONE, SANDSTONE, RED_SANDSTONE, COBBLESTONE, MOSSY_COBBLESTONE, STONEBRICK, RED_NETHER_BRICK, QUARTZ_BLOCK,
                    STONE_STAIRS, BRICK_STAIRS, STONE_BRICK_STAIRS,
                    NETHER_BRICK_STAIRS, SANDSTONE_STAIRS, QUARTZ_STAIRS, RED_SANDSTONE_STAIRS, PURPUR_STAIRS, COBBLESTONE_WALL, NETHER_BRICK_FENCE,
                    BEDROCK, GOLD_ORE, IRON_ORE, COAL_ORE, LAPIS_ORE, DIAMOND_ORE, REDSTONE_ORE, LIT_REDSTONE_ORE, EMERALD_ORE, DROPPER, DISPENSER,
                    FURNACE, LIT_FURNACE, OBSIDIAN, GLOWINGOBSIDIAN, MOB_SPAWNER, STONE_PRESSURE_PLATE, NETHERRACK, QUARTZ_ORE, ENCHANTING_TABLE,
                    END_PORTAL_FRAME, END_STONE, END_BRICKS, ENDER_CHEST, PRISMARINE, COAL_BLOCK, PURPUR_BLOCK, MAGMA,
                    STONECUTTER, OBSERVER, CRIMSON_NYLIUM, WARPED_NYLIUM -> Instrument.BASS_DRUM;
            default -> Instrument.PIANO;
        };
    }

    public void emitSound() {
        emitSound(null);
    }

    public void emitSound(@Nullable Player player) {
        if (!this.up().isAir()) return;

        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(player != null ? player : this, this.add(0.5, 0.5, 0.5).clone(), VibrationType.BLOCK_CHANGE));

        Instrument instrument = this.getInstrument();

        this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_NOTE, instrument.ordinal() << 8 | this.getStrength());

        BlockEventPacket pk = new BlockEventPacket();
        pk.x = this.getFloorX();
        pk.y = this.getFloorY();
        pk.z = this.getFloorZ();
        pk.case1 = instrument.ordinal();
        pk.case2 = this.getStrength();
        this.getLevel().addChunkPacket(this.getFloorX() >> 4, this.getFloorZ() >> 4, pk);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            // We can't use getOrCreateBlockEntity(), because the update method is called on block place,
            // before the "real" BlockEntity is set. That means, if we'd use the other method here,
            // it would create two BlockEntities.
            BlockEntityMusic music = getBlockEntity();
            if (music == null)
                return 0;

            if (this.isGettingPower()) {
                if (!music.isPowered()) {
                    this.emitSound();
                }
                music.setPowered(true);
            } else {
                music.setPowered(false);
            }
        }
        return super.onUpdate(type);
    }

    public enum Instrument {
        PIANO(Sound.NOTE_HARP),
        BASS_DRUM(Sound.NOTE_BD),
        DRUM(Sound.NOTE_SNARE),
        STICKS(Sound.NOTE_HAT),
        BASS(Sound.NOTE_BASS),
        GLOCKENSPIEL(Sound.NOTE_BELL),
        FLUTE(Sound.NOTE_FLUTE),
        CHIME(Sound.NOTE_CHIME),
        GUITAR(Sound.NOTE_GUITAR),
        XYLOPHONE(Sound.NOTE_XYLOPHONE),
        VIBRAPHONE(Sound.NOTE_IRON_XYLOPHONE),
        COW_BELL(Sound.NOTE_COW_BELL),
        DIDGERIDOO(Sound.NOTE_DIDGERIDOO),
        SQUARE_WAVE(Sound.NOTE_BIT),
        BANJO(Sound.NOTE_BANJO),
        ELECTRIC_PIANO(Sound.NOTE_PLING);

        private final Sound sound;

        Instrument(Sound sound) {
            this.sound = sound;
        }

        public Sound getSound() {
            return sound;
        }
    }

}
