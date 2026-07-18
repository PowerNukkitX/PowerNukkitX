package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.shelf.AbstractBlockShelf;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityMusic;
import org.powernukkitx.event.player.PlayerInteractEvent;
import org.powernukkitx.event.player.PlayerInteractEvent.Action;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationType;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.RedstoneComponent;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.packet.BlockEventPacket;
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

    /**
     * A note block can play when the block directly above it is air or a skull.
     * Any other block above silences it entirely.
     */
    private boolean canPlay() {
        Block above = this.up();
        return above.isAir() || above instanceof BlockHead;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player != null && player.isSneaking() || (this.up().isAir() && blockFace.equals(BlockFace.UP) && item.isBlock() && item.getBlock() instanceof BlockHead)) {
            return false;
        }
        if (!canPlay()) {
            return true;
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
            if (canPlay()) {
                this.emitSound(player);
            }
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
        return switch (this.up()) {
            case BlockSkeletonSkull ignored -> Instrument.SKELETON;
            case BlockWitherSkeletonSkull ignored -> Instrument.WITHER_SKELETON;
            case BlockZombieHead ignored -> Instrument.ZOMBIE;
            case BlockCreeperHead ignored -> Instrument.CREEPER;
            case BlockDragonHead ignored -> Instrument.ENDER_DRAGON;
            case BlockPiglinHead ignored -> Instrument.PIGLIN;
            case BlockPlayerHead ignored -> Instrument.HARP;
            default -> switch (this.down()) {
                case BlockConcretePowder ignored -> Instrument.SNARE_DRUM;
                case BlockSand ignored -> Instrument.SNARE_DRUM;
                case BlockGravel ignored -> Instrument.SNARE_DRUM;
                case BlockGlass ignored -> Instrument.CLICKS_AND_STICKS;
                case BlockSeaLantern ignored -> Instrument.CLICKS_AND_STICKS;
                case BlockBeacon ignored -> Instrument.CLICKS_AND_STICKS;
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
                case BlockWool ignored -> Instrument.GUITAR;
                case BlockBoneBlock ignored -> Instrument.XYLOPHONE;
                case BlockIronBlock ignored -> Instrument.IRON_XYLOPHONE;
                case BlockSoulSand ignored -> Instrument.COW_BELL;
                case BlockPumpkin ignored -> Instrument.DIDGERIDOO;
                case BlockEmeraldBlock ignored -> Instrument.BIT;
                case BlockHayBlock ignored -> Instrument.BANJO;
                case BlockGlowstone ignored -> Instrument.PLING;
                case BlockLog ignored -> Instrument.BASS;
                case BlockPlanks ignored -> Instrument.BASS;
                case BlockChest ignored -> Instrument.BASS;
                case BlockCraftingTable ignored -> Instrument.BASS;
                case BlockBookshelf ignored -> Instrument.BASS;
                case BlockWoodenSlab ignored -> Instrument.BASS;
                case AbstractBlockShelf ignored -> Instrument.BASS;
                case BlockExposedCopper ignored -> Instrument.TRUMPET_EXPOSED;
                case BlockWeatheredCopper ignored -> Instrument.TRUMPET_WEATHERED;
                case BlockOxidizedCopper ignored -> Instrument.TRUMPET_OXIDIZED;
                case BlockCopperBlock ignored -> Instrument.TRUMPET;
                default -> Instrument.HARP;
            };
        };
    }

    public void emitSound() {
        emitSound(null);
    }

    public void emitSound(@Nullable Player player) {

        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(player != null ? player.getLocation() : this, this.add(0.5, 0.5, 0.5).clone(), VibrationType.BLOCK_CHANGE));

        Instrument instrument = this.getInstrument();

        this.level.addLevelSoundEvent(this, SoundEvent.NOTE, instrument.getId() << 8 | this.getStrength());

        final BlockEventPacket pk = new BlockEventPacket();
        pk.setBlockPosition(Vector3i.from(this.getFloorX(), this.getFloorY(), this.getFloorZ()));
        pk.setEventType(instrument.getId());
        pk.setEventValue(this.getStrength());
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
                    if (canPlay()) {
                        this.emitSound();
                    }
                }
                music.setPowered(true);
            } else {
                music.setPowered(false);
            }
        }
        return super.onUpdate(type);
    }

    public enum Instrument {
        HARP(0, Sound.NOTE_HARP),
        BASS_DRUM(1, Sound.NOTE_BD),
        SNARE_DRUM(2, Sound.NOTE_SNARE),
        CLICKS_AND_STICKS(3, Sound.NOTE_HAT),
        BASS(4, Sound.NOTE_BASS),
        FLUTE(5, Sound.NOTE_FLUTE),
        BELLS(6, Sound.NOTE_BELL),
        GUITAR(7, Sound.NOTE_GUITAR),
        CHIMES(8, Sound.NOTE_CHIME),
        XYLOPHONE(9, Sound.NOTE_XYLOPHONE),
        IRON_XYLOPHONE(10, Sound.NOTE_IRON_XYLOPHONE),
        COW_BELL(11, Sound.NOTE_COW_BELL),
        DIDGERIDOO(12, Sound.NOTE_DIDGERIDOO),
        BIT(13, Sound.NOTE_BIT),
        BANJO(14, Sound.NOTE_BANJO),
        PLING(15, Sound.NOTE_PLING),
        TRUMPET(16, Sound.NOTE_TRUMPET),
        TRUMPET_EXPOSED(17, Sound.NOTE_TRUMPET),
        TRUMPET_WEATHERED(18, Sound.NOTE_TRUMPET),
        TRUMPET_OXIDIZED(19, Sound.NOTE_TRUMPET),
        ZOMBIE(20, Sound.NOTE_ZOMBIE),
        SKELETON(21, Sound.NOTE_SKELETON),
        CREEPER(22, Sound.NOTE_CREEPER),
        ENDER_DRAGON(23, Sound.NOTE_ENDERDRAGON),
        WITHER_SKELETON(24, Sound.NOTE_WITHERSKELETON),
        PIGLIN(25, Sound.NOTE_PIGLIN);

        private final int id;
        private final Sound sound;

        Instrument(int id, Sound sound) {
            this.id = id;
            this.sound = sound;
        }

        public int getId() {
            return id;
        }

        public Sound getSound() {
            return sound;
        }
    }

}
