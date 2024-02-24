package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.enums.CrackedState;
import cn.nukkit.block.property.enums.TurtleEggCount;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.mob.EntityGhast;
import cn.nukkit.entity.mob.EntityPhantom;
import cn.nukkit.entity.passive.EntityBat;
import cn.nukkit.entity.passive.EntityChicken;
import cn.nukkit.entity.passive.EntityTurtle;
import cn.nukkit.event.Event;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.TurtleEggHatchEvent;
import cn.nukkit.event.entity.CreatureSpawnEvent;
import cn.nukkit.event.entity.EntityInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.*;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.registry.Registries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.CRACKED_STATE;
import static cn.nukkit.block.property.CommonBlockProperties.TURTLE_EGG_COUNT;
import static cn.nukkit.block.property.enums.TurtleEggCount.FOUR_EGG;


public class BlockTurtleEgg extends BlockFlowable {
    public static final BlockProperties PROPERTIES = new BlockProperties(TURTLE_EGG, CRACKED_STATE, TURTLE_EGG_COUNT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTurtleEgg() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTurtleEgg(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Turtle Egg";
    }

    public CrackedState getCracks() {
        return getPropertyValue(CRACKED_STATE);
    }

    public void setCracks(@Nullable CrackedState cracks) {
        setPropertyValue(CRACKED_STATE, cracks);
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    public TurtleEggCount getEggCount() {
        return getPropertyValue(TURTLE_EGG_COUNT);
    }

    public void setEggCount(TurtleEggCount eggCount) {
        setPropertyValue(TURTLE_EGG_COUNT, eggCount);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.getBlock() != null && Objects.equals(item.getBlockId(), TURTLE_EGG) && (player == null || !player.isSneaking())) {
            TurtleEggCount eggCount = getEggCount();
            if (eggCount == FOUR_EGG) {
                return false;
            }
            BlockTurtleEgg newState = new BlockTurtleEgg();
            newState.setEggCount(eggCount.next());
            BlockPlaceEvent placeEvent = new BlockPlaceEvent(
                    player,
                    newState,
                    this,
                    down(),
                    item
            );
            if (placeEvent.isCancelled()) {
                return false;
            }
            if (!this.level.setBlock(this, placeEvent.getBlock(), true, true)) {
                return false;
            }
            Block placeBlock = placeEvent.getBlock();
            this.level.addLevelSoundEvent(this,
                    LevelSoundEventPacket.SOUND_PLACE,
                    placeBlock.getRuntimeId());
            item.setCount(item.getCount() - 1);

            if (down().getId().equals(SAND)) {
                this.level.addParticle(new BoneMealParticle(this));
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public double getMinX() {
        return x + (3.0 / 16);
    }

    @Override
    public double getMinZ() {
        return z + (3.0 / 16);
    }

    @Override
    public double getMaxX() {
        return x + (12.0 / 16);
    }

    @Override
    public double getMaxZ() {
        return z + (12.0 / 16);
    }

    @Override
    public double getMaxY() {
        return y + (7.0 / 16);
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new SimpleAxisAlignedBB(getMinX(), getMinY(), getMinZ(), getMaxX(), getMaxY() + 0.25, getMaxZ());
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (down().getId().equals(BlockID.SAND)) {
                float celestialAngle = level.calculateCelestialAngle(level.getTime(), 1);
                ThreadLocalRandom random = ThreadLocalRandom.current();
                if (0.70 > celestialAngle && celestialAngle > 0.65 || random.nextInt(500) == 0) {
                    CrackedState crackState = getCracks();
                    if (crackState != CrackedState.MAX_CRACKED) {
                        BlockTurtleEgg newState = clone();
                        newState.setCracks(crackState.next());
                        BlockGrowEvent event = new BlockGrowEvent(this, newState);
                        this.level.getServer().getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            level.addSound(this, Sound.BLOCK_TURTLE_EGG_CRACK, 0.7f, 0.9f + random.nextFloat() * 0.2f);
                            this.level.setBlock(this, event.getNewState(), true, true);
                        }
                    } else {
                        hatch();
                    }
                }
            }
            return type;
        }
        return 0;
    }

    public void hatch() {
        hatch(getEggCount());
    }

    public void hatch(TurtleEggCount eggs) {
        hatch(eggs, new BlockAir());
    }

    public void hatch(TurtleEggCount eggs, Block newState) {
        TurtleEggHatchEvent turtleEggHatchEvent = new TurtleEggHatchEvent(this, eggs.ordinal() + 1, newState);
        //TODO Cancelled by default because EntityTurtle doesn't have AI yet, remove it when AI is added
        turtleEggHatchEvent.setCancelled(true);
        this.level.getServer().getPluginManager().callEvent(turtleEggHatchEvent);
        int eggsHatching = turtleEggHatchEvent.getEggsHatching();
        if (!turtleEggHatchEvent.isCancelled()) {
            level.addSound(this, Sound.BLOCK_TURTLE_EGG_CRACK);

            boolean hasFailure = false;
            for (int i = 0; i < eggsHatching; i++) {

                this.level.addSound(this, Sound.BLOCK_TURTLE_EGG_CRACK);

                CreatureSpawnEvent creatureSpawnEvent = new CreatureSpawnEvent(
                        Registries.ENTITY.getEntityNetworkId(EntityID.TURTLE),
                        add(0.3 + i * 0.2,
                                0,
                                0.3
                        ),
                        CreatureSpawnEvent.SpawnReason.TURTLE_EGG);
                this.level.getServer().getPluginManager().callEvent(creatureSpawnEvent);

                if (!creatureSpawnEvent.isCancelled()) {
                    EntityTurtle turtle = (EntityTurtle) Entity.createEntity(
                            creatureSpawnEvent.getEntityNetworkId(),
                            creatureSpawnEvent.getPosition());
                    if (turtle != null) {
                        turtle.setBreedingAge(-24000);
                        turtle.setHomePos(new Vector3(x, y, z));
                        turtle.setDataFlag(EntityFlag.BABY, true);
                        turtle.setScale(0.16f);
                        turtle.spawnToAll();
                        continue;
                    }
                }

                if (turtleEggHatchEvent.isRecalculateOnFailure()) {
                    turtleEggHatchEvent.setEggsHatching(turtleEggHatchEvent.getEggsHatching() - 1);
                    hasFailure = true;
                }
            }

            if (hasFailure) {
                turtleEggHatchEvent.recalculateNewState();
            }

            this.level.setBlock(this, turtleEggHatchEvent.getNewState(), true, true);
        }
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (entity instanceof EntityLiving
                && !(entity instanceof EntityChicken)
                && !(entity instanceof EntityBat)
                && !(entity instanceof EntityGhast)
                && !(entity instanceof EntityPhantom)
                && entity.getY() >= this.getMaxY()) {
            Event ev;

            if (entity instanceof Player) {
                ev = new PlayerInteractEvent((Player) entity, null, this, null, PlayerInteractEvent.Action.PHYSICAL);
            } else {
                ev = new EntityInteractEvent(entity, this);
            }

            ev.setCancelled(ThreadLocalRandom.current().nextInt(200) > 0);
            this.level.getServer().getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                this.level.useBreakOn(this, null, null, true);
            }
        }
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockTurtleEgg());
    }

    @Override
    public boolean onBreak(Item item) {
        TurtleEggCount eggCount = getEggCount();
        if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) == null) {
            this.level.addSound(this, Sound.BLOCK_TURTLE_EGG_CRACK);
        }
        if (eggCount == TurtleEggCount.ONE_EGG) {
            return super.onBreak(item);
        } else {
            setEggCount(eggCount.before());
            return this.level.setBlock(this, this, true, true);
        }
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (!isValidSupport(block.down(1, 0))) {
            return false;
        }

        if (this.level.setBlock(this, this, true, true)) {
            if (down().getId().equals(BlockID.SAND)) {
                this.level.addParticle(new BoneMealParticle(this));
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean isValidSupport(Block support) {
        return support.isSolid(BlockFace.UP) || support instanceof BlockWallBase;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    public BlockTurtleEgg clone() {
        return (BlockTurtleEgg) super.clone();
    }
}
