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
        return 0.8;
    }

    @Override
    public double getResistance() {
        return 0.8;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player != null && player.isSneaking() || (this.up().isAir() && blockFace.equals(BlockFace.UP) && item.isBlock() && item.getBlock() instanceof BlockHead)) {
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
        return switch (this.down()) {
            case BlockWool ignored -> Instrument.GUITAR;
            case BlockConcretePowder ignored -> Instrument.SNARE_DRUM;
            case BlockSand ignored -> Instrument.SNARE_DRUM;
            case BlockGravel ignored -> Instrument.SNARE_DRUM;
            case BlockGlass ignored -> Instrument.SNARE_DRUM;
            case BlockSeaLantern ignored -> Instrument.SNARE_DRUM;
            case BlockBeacon ignored -> Instrument.SNARE_DRUM;
            case BlockStone ignored -> Instrument.BASS_DRUM;
            case BlockBlackstone ignored -> Instrument.BASS_DRUM;
            case BlockNetherrack ignored -> Instrument.BASS_DRUM;
            case BlockNylium ignored -> Instrument.BASS_DRUM;
            case BlockObsidian ignored -> Instrument.BASS_DRUM;
            case BlockQuartzBlock ignored -> Instrument.BASS_DRUM;
            case BlockSandstone ignored -> Instrument.BASS_DRUM;
            case BlockOre ignored -> Instrument.BASS_DRUM;
            case BlockBrickBlock ignored -> Instrument.BASS_DRUM;
            case BlockCoral ignored -> Instrument.BASS_DRUM;
            case BlockRespawnAnchor ignored -> Instrument.BASS_DRUM;
            case BlockBedrock ignored -> Instrument.BASS_DRUM;
            case BlockConcrete ignored -> Instrument.BASS_DRUM;
            case BlockStonecutter ignored -> Instrument.BASS_DRUM;
            case BlockFurnace ignored -> Instrument.BASS_DRUM;
            case BlockObserver ignored -> Instrument.BASS_DRUM;
            case BlockHardenedClay ignored -> Instrument.BASS_DRUM;
            case BlockPrismarine ignored -> Instrument.BASS_DRUM;
            case BlockGoldBlock ignored -> Instrument.BELLS;
            case BlockClay ignored -> Instrument.FLUTE;
            case BlockHoneycombBlock ignored -> Instrument.FLUTE;
            case BlockInfestedChiseledStoneBricks ignored -> Instrument.FLUTE;
            case BlockInfestedMossyStoneBricks ignored -> Instrument.FLUTE;
            case BlockInfestedStone ignored -> Instrument.FLUTE;
            case BlockInfestedDeepslate ignored -> Instrument.FLUTE;
            case BlockInfestedCrackedStoneBricks ignored -> Instrument.FLUTE;
            case BlockInfestedStoneBricks ignored -> Instrument.FLUTE;
            case BlockPackedIce ignored -> Instrument.CHIMES;
            case BlockBoneBlock ignored -> Instrument.XYLOPHONE;
            case BlockIronBlock ignored -> Instrument.IRON_XYLOPHONE;
            case BlockSoulSand ignored -> Instrument.COW_BELL;
            case BlockPumpkin ignored -> Instrument.DIDGERIDOO;
            case BlockEmeraldBlock ignored -> Instrument.BIT;
            case BlockHayBlock ignored -> Instrument.BANJO;
            case BlockGlowstone ignored -> Instrument.PLING;
            default -> {
                if (this.up() instanceof BlockHead skull) {
                    int meta = 0;
                    if (skull.getBlockEntity() != null) {
                        meta = skull.getBlockEntity().namedTag.getByte("SkullType");
                    }
                    yield switch (meta) {
                        case 0, 3 ->
                                Instrument.SKELETON;   //skull with meta 3 is a steve head but the sound depends. More info at https://minecraft.wiki/w/Note_Block
                        case 1 -> Instrument.WITHER_SKELETON;
                        case 2 -> Instrument.ZOMBIE;
                        case 4 -> Instrument.CREEPER;
                        case 5 -> Instrument.ENDER_DRAGON;
                        default -> Instrument.PIGLIN;
                    };
                } else {
                    yield Instrument.HARP;
                }
            }
        };
    }

    public void emitSound() {
        emitSound(null);
    }

    public void emitSound(@Nullable Player player) {

        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(player != null ? player : this, this.add(0.5, 0.5, 0.5).clone(), VibrationType.BLOCK_CHANGE));

        Instrument instrument = this.getInstrument();

        this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_NOTE, instrument.ordinal() << 8 | this.getStrength());

        BlockEventPacket pk = new BlockEventPacket();
        pk.x = this.getFloorX();
        pk.y = this.getFloorY();
        pk.z = this.getFloorZ();
        pk.type = instrument.ordinal();
        pk.value = this.getStrength();
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
        HARP(Sound.NOTE_HARP),
        BASS_DRUM(Sound.NOTE_BD),
        SNARE_DRUM(Sound.NOTE_SNARE),
        CLICKS_AND_STICKS(Sound.NOTE_HAT),
        BASS(Sound.NOTE_BASS),
        BELLS(Sound.NOTE_BELL),
        FLUTE(Sound.NOTE_FLUTE),
        CHIMES(Sound.NOTE_CHIME),
        GUITAR(Sound.NOTE_GUITAR),
        XYLOPHONE(Sound.NOTE_XYLOPHONE),
        IRON_XYLOPHONE(Sound.NOTE_IRON_XYLOPHONE),
        COW_BELL(Sound.NOTE_COW_BELL),
        DIDGERIDOO(Sound.NOTE_DIDGERIDOO),
        BIT(Sound.NOTE_BIT),
        BANJO(Sound.NOTE_BANJO),
        PLING(Sound.NOTE_PLING),
        SKELETON(Sound.NOTE_SKELETON),
        WITHER_SKELETON(Sound.NOTE_WITHERSKELETON),
        ZOMBIE(Sound.NOTE_ZOMBIE),
        CREEPER(Sound.NOTE_CREEPER),
        ENDER_DRAGON(Sound.NOTE_ENDERDRAGON),
        PIGLIN(Sound.NOTE_PIGLIN);

        private final Sound sound;

        Instrument(Sound sound) {
            this.sound = sound;
        }

        public Sound getSound() {
            return sound;
        }
    }

}
