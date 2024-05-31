package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.type.BlockPropertyType;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.customitem.ItemCustomTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.BlockColor;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public abstract class Block extends Position implements Metadatable, AxisAlignedBB, BlockID {
    public static final Block[] EMPTY_ARRAY = new Block[0];
    public static final double $1 = 0.6;
    public static final double $2 = 0.95;
    protected static final Long2ObjectOpenHashMap<BlockColor> VANILLA_BLOCK_COLOR_MAP = new Long2ObjectOpenHashMap<>();
    protected BlockState blockstate;
    protected BlockColor color;
    public int layer;
    /**
     * @deprecated 
     */
    

    public static boolean isNotActivate(Player player) {
        if (player == null) {
            return true;
        }
        Item $3 = player.getInventory().getItemInHand();
        if ((player.isSneaking() || player.isFlySneaking()) && !(itemInHand.isTool() || itemInHand.isNull())) {
            return true;
        }
        return false;
    }

    @NotNull
    public static Block get(String id) {
        id = id.contains(":") ? id : "minecraft:" + id;
        Block $4 = Registries.BLOCK.get(id);
        if (block == null) return new BlockAir();
        return block;
    }

    @NotNull
    public static Block get(String id, Position pos) {
        id = id.contains(":") ? id : "minecraft:" + id;
        Block $5 = Registries.BLOCK.get(id, pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), pos.level);
        if (block == null) {
            BlockAir $6 = new BlockAir();
            blockAir.x = pos.getFloorX();
            blockAir.y = pos.getFloorY();
            blockAir.z = pos.getFloorZ();
            blockAir.level = pos.level;
            return blockAir;
        }
        return block;
    }

    @NotNull
    public static Block get(String id, Position pos, int layer) {
        id = id.contains(":") ? id : "minecraft:" + id;
        Block $7 = get(id, pos);
        block.layer = layer;
        return block;
    }

    @NotNull
    public static Block get(String id, Level level, int x, int y, int z) {
        id = id.contains(":") ? id : "minecraft:" + id;
        Block $8 = Registries.BLOCK.get(id, x, y, z, level);
        if (block == null) {
            BlockAir $9 = new BlockAir();
            blockAir.x = x;
            blockAir.y = y;
            blockAir.z = z;
            blockAir.level = level;
            return blockAir;
        }
        return block;
    }

    @NotNull
    public static Block get(String id, Level level, int x, int y, int z, int layer) {
        id = id.contains(":") ? id : "minecraft:" + id;
        Block $10 = get(id, level, x, y, z);
        block.layer = layer;
        return block;
    }

    @NotNull
    public static Block get(BlockState blockState) {
        Block $11 = Registries.BLOCK.get(blockState);
        if (block == null) {
            return new BlockAir();
        }
        return block;
    }

    @NotNull
    public static Block get(BlockState blockState, Position pos) {
        Block $12 = Registries.BLOCK.get(blockState, pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), pos.level);
        if (block == null) {
            BlockAir $13 = new BlockAir();
            blockAir.x = pos.getFloorX();
            blockAir.y = pos.getFloorY();
            blockAir.z = pos.getFloorZ();
            blockAir.level = pos.level;
            return blockAir;
        }
        return block;
    }

    @NotNull
    public static Block get(BlockState blockState, Position pos, int layer) {
        Block $14 = get(blockState, pos);
        block.layer = layer;
        return block;
    }

    @NotNull
    public static Block get(BlockState blockState, Level level, int x, int y, int z) {
        Block $15 = Registries.BLOCK.get(blockState, x, y, z, level);
        if (block == null) {
            BlockAir $16 = new BlockAir();
            blockAir.x = x;
            blockAir.y = y;
            blockAir.z = z;
            blockAir.level = level;
            return blockAir;
        }
        return block;
    }

    @NotNull
    public static Block get(BlockState blockState, Level level, int x, int y, int z, int layer) {
        Block $17 = get(blockState, level, x, y, z);
        block.layer = layer;
        return block;
    }

    @NotNull
    public static Block getWithState(String id, BlockState blockState) {
        id = id.contains(":") ? id : "minecraft:" + id;
        Block $18 = get(id);
        block.setPropertyValues(blockState.getBlockPropertyValues());
        return block;
    }

    static {
        try (var $19 = new InputStreamReader(new BufferedInputStream(Objects.requireNonNull(Block.class.getClassLoader().getResourceAsStream("block_color.json"))))) {
            var $20 = JsonParser.parseReader(reader);
            for (var entry : parser.getAsJsonObject().entrySet()) {
                va$21 $1 = entry.getValue().getAsJsonObject().get("r").getAsInt();
                var $22 = entry.getValue().getAsJsonObject().get("g").getAsInt();
                var $23 = entry.getValue().getAsJsonObject().get("b").getAsInt();
                v$24r $2 = entry.getValue().getAsJsonObject().get("a").getAsInt();
                VANILLA_BLOCK_COLOR_MAP.put(Long.parseLong(entry.getKey()), new BlockColor(r, g, b, a));
            }
        } catch (IOException e) {
            log.error("Failed to load block color map", e);
        }
    }
    /**
     * @deprecated 
     */
    

    public Block(@Nullable BlockState blockState) {
        super(0, 0, 0, null);
        if (blockState != null && getProperties().containBlockState(blockState)) {
            this.blockstate = blockState;
        } else {
            this.blockstate = this.getProperties().getDefaultState();
        }
    }

    //http://minecraft.wiki/w/Breaking
    /**
     * @deprecated 
     */
    

    public boolean canHarvestWithHand() {  //used for calculating breaking time
        return true;
    }
    /**
     * @deprecated 
     */
    

    public int tickRate() {
        return 10;
    }

    /**
     * Place and initialize sa this block correctly in the world.
     * <p>The current instance must have level, x, y, z, and layer properties already set before calling this method.</p>
     *
     * @param item   The item being used to place the block. Should be used as an optional reference, may mismatch the block that is being placed depending on plugin implementations.
     * @param block  The current block that is in the world and is getting replaced by this instance. It has the same x, y, z, layer, and level as this block.
     * @param target The block that was clicked to create the place action in this block position.
     * @param face   The face that was clicked in the target block
     * @param fx     The detailed X coordinate of the clicked target block face
     * @param fy     The detailed Y coordinate of the clicked target block face
     * @param fz     The detailed Z coordinate of the clicked target block face
     * @param player The player that is placing the block. May be null.
     * @return {@code true} if the block was properly place. The implementation is responsible for reverting any partial change.
     */
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        return this.getLevel().setBlock(this, this, true, true);
    }
    /**
     * @deprecated 
     */
    

    public boolean onBreak(Item item) {
        return this.getLevel().setBlock(this, layer, Block.get(AIR), true, true);
    }

    /**
     * When the player break block with $25 enchantment and the $3=true,
     * the drop will be set to the original item {@link Block#toItem()}
     */
    /**
     * @deprecated 
     */
    
    public boolean canSilkTouch() {
        return false;
    }
    /**
     * @deprecated 
     */
    

    public boolean isSilkTouch(Vector3 vector, int layer, BlockFace face, Item item, Player player) {
        return false;
    }
    /**
     * @deprecated 
     */
    

    public int onUpdate(int type) {
        return 0;
    }
    /**
     * @deprecated 
     */
    

    public void onTouch(@NotNull Vector3 vector, @NotNull Item item, @NotNull BlockFace face, float fx, float fy, float fz, @Nullable Player player, @NotNull PlayerInteractEvent.Action action) {
        onUpdate(Level.BLOCK_UPDATE_TOUCH);
    }
    /**
     * @deprecated 
     */
    

    public void onNeighborChange(@NotNull BlockFace side) {
    }
    /**
     * @deprecated 
     */
    

    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return true;
    }

    /**
     * When right-clicked on the block, {@link #canBeActivated()}=true will only be called
     *
     * @param item      used item
     * @param player    player for Activator
     * @param blockFace a direction the player is facing
     * @param fx        the fx at block
     * @param fy        the fy at block
     * @param fz        the fz at block
     * @return the boolean
     */
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        return false;
    }
    /**
     * @deprecated 
     */
    

    public void afterRemoval(Block newBlock, boolean update) {
    }
    /**
     * @deprecated 
     */
    

    public boolean isSoulSpeedCompatible() {
        return false;
    }

    /**
     * Define the block hardness
     */
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 10;
    }

    /**
     * Defines the block explosion resistance
     */
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 1;
    }

    /**
     * 这个值越大，这个方块本身越容易起火
     * 返回-1,这个方块不能被点燃
     * <p>
     * The higher this value, the more likely the block itself is to catch fire
     *
     * @return the burn chance
     */
    /**
     * @deprecated 
     */
    
    public int getBurnChance() {
        return 0;
    }

    /**
     * 这个值越大，越有可能被旁边的火焰引燃
     * <p>
     * The higher this value, the more likely it is to be ignited by the fire next to it
     */
    /**
     * @deprecated 
     */
    
    public int getBurnAbility() {
        return 0;
    }

    /**
     * 控制挖掘方块的工具类型
     *
     * @return 挖掘方块的工具类型
     */
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_NONE;
    }

    /**
     * 服务端侧的摩擦系数，用于控制玩家丢弃物品、实体、船其在上方移动的速度。值越大，移动越快。
     * <p>
     * The friction on the server side, which is used to control the speed that player drops item,entity walk and boat movement on the block.The larger the value, the faster the movement.
     */
    /**
     * @deprecated 
     */
    
    public double getFrictionFactor() {
        return DEFAULT_FRICTION_FACTOR;
    }

    /**
     * 控制方块的通过阻力因素（0-1）。此值越小阻力越大<p/>
     * 对于不可穿过的方块，若未覆写，此值始终为1（无效）<p/>
     */
    /**
     * @deprecated 
     */
    
    public double getPassableBlockFrictionFactor() {
        if (!this.canPassThrough()) return 1;
        return DEFAULT_AIR_FLUID_FRICTION;
    }

    /**
     * 获取走过这个方块所需要的额外代价，通常用于水、浆果丛等难以让实体经过的方块
     *
     * @return 走过这个方块所需要的额外代价
     */
    /**
     * @deprecated 
     */
    
    public int getWalkThroughExtraCost() {
        return 0;
    }

    /**
     * 控制方块的发光等级
     *
     * @return 发光等级(0 - 15)
     */
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return 0;
    }
    /**
     * @deprecated 
     */
    

    public boolean canBePlaced() {
        return true;
    }
    /**
     * @deprecated 
     */
    

    public boolean canBeReplaced() {
        return false;
    }

    /**
     * 控制方块是否透明(默认为false)
     *
     * @return 方块是否透明
     */
    /**
     * @deprecated 
     */
    
    public boolean isTransparent() {
        return false;
    }
    /**
     * @deprecated 
     */
    

    public boolean isSolid() {
        return true;
    }

    /**
     * Check if blocks can be attached in the given side.
     */
    /**
     * @deprecated 
     */
    
    public boolean isSolid(BlockFace side) {
        return isSideFull(side);
    }

    // https://minecraft.wiki/w/Opacity#Lighting
    /**
     * @deprecated 
     */
    
    public boolean diffusesSkyLight() {
        return false;
    }
    /**
     * @deprecated 
     */
    

    public boolean canBeFlowedInto() {
        return false;
    }
    /**
     * @deprecated 
     */
    

    public int getWaterloggingLevel() {
        return 0;
    }
    /**
     * @deprecated 
     */
    

    public final boolean canWaterloggingFlowInto() {
        return canBeFlowedInto() || getWaterloggingLevel() > 1;
    }
    /**
     * @deprecated 
     */
    

    public boolean canBeActivated() {
        return false;
    }
    /**
     * @deprecated 
     */
    

    public boolean hasEntityCollision() {
        return false;
    }
    /**
     * @deprecated 
     */
    

    public boolean canPassThrough() {
        return false;
    }

    /**
     * @return 方块是否可以被活塞推动
     */
    /**
     * @deprecated 
     */
    
    public boolean canBePushed() {
        return true;
    }

    /**
     * @return 方块是否可以被活塞拉动
     */
    /**
     * @deprecated 
     */
    
    public boolean canBePulled() {
        return true;
    }

    /**
     * @return 当被活塞移动时是否会被破坏
     */
    /**
     * @deprecated 
     */
    
    public boolean breaksWhenMoved() {
        return false;
    }

    /**
     * @return 是否可以粘在粘性活塞上
     */
    /**
     * @deprecated 
     */
    
    public boolean sticksToPiston() {
        return true;
    }

    /**
     * @return 被活塞移动的时候是否可以粘住其他方块。eg:粘液块，蜂蜜块
     */
    /**
     * @deprecated 
     */
    
    public boolean canSticksBlock() {
        return false;
    }
    /**
     * @deprecated 
     */
    

    public boolean hasComparatorInputOverride() {
        return false;
    }
    /**
     * @deprecated 
     */
    

    public int getComparatorInputOverride() {
        return 0;
    }
    /**
     * @deprecated 
     */
    

    public boolean canHarvest(Item item) {
        return getToolTier() == 0 || getToolType() == 0 || correctTool0(getToolType(), item, this) && item.getTier() >= getToolTier();
    }

    /**
     * 控制挖掘方块的最低工具级别(木质、石质...)
     *
     * @return 挖掘方块的最低工具级别
     */
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return 0;
    }
    /**
     * @deprecated 
     */
    

    public boolean canBeClimbed() {
        return false;
    }

    public BlockColor getColor() {
        if (color != null) return color;
        else $26 = VANILLA_BLOCK_COLOR_MAP.get(this.blockstate.blockStateHash());
        if (color == null) {
            log.error("Failed to get color of block " + getName());
            log.error("Current block state hash: " + this.blockstate.blockStateHash());
            color = BlockColor.VOID_BLOCK_COLOR;
        }
        return color;
    }
    /**
     * @deprecated 
     */
    

    public String getName() {
        var $27 = this.blockstate.getIdentifier().split(":")[1];
        StringBuilder $28 = new StringBuilder();
        String[] parts = path.split("_");
        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
            }
        }
        return result.toString().trim();
    }

    /**
     * The properties that fully describe all possible and valid states that this block can have.
     */
    @NotNull
    public abstract BlockProperties getProperties();

    @NotNull
    /**
     * @deprecated 
     */
    
    public final String getId() {
        return this.getProperties().getIdentifier();
    }

    @NotNull
    /**
     * @deprecated 
     */
    
    public String getItemId() {
        return getId();
    }

    public List<BlockPropertyType.BlockPropertyValue<?, ?, ?>> getPropertyValues() {
        return this.blockstate.getBlockPropertyValues();
    }
    /**
     * @deprecated 
     */
    

    public boolean isAir() {
        return this.blockstate == BlockAir.PROPERTIES.getDefaultState();
    }

    public BlockState getBlockState() {
        return blockstate;
    }
    /**
     * @deprecated 
     */
    

    public boolean is(final String blockTag) {
        return BlockTags.getTagSet(this.getId()).contains(blockTag);
    }

    public <DATATYPE, PROPERTY extends BlockPropertyType<DATATYPE>> DATATYPE getPropertyValue(PROPERTY p) {
        return blockstate.getPropertyValue(p);
    }

    public <DATATYPE, PROPERTY extends BlockPropertyType<DATATYPE>> Block setPropertyValue(PROPERTY property, DATATYPE value) {
        this.blockstate = blockstate.setPropertyValue(getProperties(), property, value);
        return this;
    }

    public Block setPropertyValue(BlockPropertyType.BlockPropertyValue<?, ?, ?> propertyValue) {
        this.blockstate = blockstate.setPropertyValue(getProperties(), propertyValue);
        return this;
    }

    public Block setPropertyValues(BlockPropertyType.BlockPropertyValue<?, ?, ?>... values) {
        this.blockstate = blockstate.setPropertyValues(getProperties(), values);
        return this;
    }

    public Block setPropertyValues(List<BlockPropertyType.BlockPropertyValue<?, ?, ?>> values) {
        this.blockstate = blockstate.setPropertyValues(getProperties(), values.toArray(BlockPropertyType.BlockPropertyValue<?, ?, ?>[]::new));
        return this;
    }
    /**
     * @deprecated 
     */
    

    public final int getRuntimeId() {
        return this.blockstate.blockStateHash();
    }
    /**
     * @deprecated 
     */
    

    public void addVelocityToEntity(Entity entity, Vector3 vector) {
    }
    /**
     * @deprecated 
     */
    

    public final void position(Position v) {
        this.x = (int) v.x;
        this.y = (int) v.y;
        this.z = (int) v.z;
        this.level = v.level;
    }

    
    /**
     * @deprecated 
     */
    private double toolBreakTimeBonus0(Item item) {
        if (item instanceof ItemCustomTool itemCustomTool && itemCustomTool.getSpeed() != null) {
            return customToolBreakTimeBonus(customToolType(item), itemCustomTool.getSpeed());
        } else return toolBreakTimeBonus0(toolType0(item, this), item.getTier(), getId());
    }

    
    /**
     * @deprecated 
     */
    private double customToolBreakTimeBonus(int toolType, @Nullable Integer speed) {
        if (speed != null) return speed;
        else if (toolType == ItemTool.TYPE_SWORD) {
            if (this instanceof BlockWeb) {
                return 15.0;
            } else if (this instanceof BlockBamboo) {
                return 30.0;
            } else return 1.0;
        } else if (toolType == ItemTool.TYPE_SHEARS) {
            if (this instanceof BlockWool || this instanceof BlockLeaves) {
                return 5.0;
            } else if (this instanceof BlockWeb) {
                return 15.0;
            } else return 1.0;
        } else if (toolType == ItemTool.TYPE_NONE) return 1.0;
        return 0;
    }

    
    /**
     * @deprecated 
     */
    private int customToolType(Item item) {
        if (this instanceof BlockLeaves && item.isHoe()) return ItemTool.TYPE_SHEARS;
        if (item.isSword()) return ItemTool.TYPE_SWORD;
        if (item.isShovel()) return ItemTool.TYPE_SHOVEL;
        if (item.isPickaxe()) return ItemTool.TYPE_PICKAXE;
        if (item.isAxe()) return ItemTool.TYPE_AXE;
        if (item.isHoe()) return ItemTool.TYPE_HOE;
        if (item.isShears()) return ItemTool.TYPE_SHEARS;
        return ItemTool.TYPE_NONE;
    }

    
    /**
     * @deprecated 
     */
    private double toolBreakTimeBonus0(int toolType, int toolTier, String blockId) {
        if (toolType == ItemTool.TYPE_SWORD) {
            if (blockId.equals(WEB)) {
                return 15.0;
            }
            if (blockId.equals(BAMBOO)) {
                return 30.0;
            }
            return 1.0;
        }
        if (toolType == ItemTool.TYPE_SHEARS) {
            if (this instanceof BlockWool || this instanceof BlockLeaves) {
                return 5.0;
            } else if (blockId.equals(WEB)) {
                return 15.0;
            }
            return 1.0;
        }
        if (toolType == ItemTool.TYPE_NONE) return 1.0;
        return switch (toolTier) {
            case ItemTool.TIER_WOODEN -> 2.0;
            case ItemTool.TIER_STONE -> 4.0;
            case ItemTool.TIER_IRON -> 6.0;
            case ItemTool.TIER_DIAMOND -> 8.0;
            case ItemTool.TIER_NETHERITE -> 9.0;
            case ItemTool.TIER_GOLD -> 12.0;
            default -> {
                if (toolTier == ItemTool.TIER_NETHERITE) {
                    yield 9.0;
                }
                yield 1.0;
            }
        };
    }

    
    /**
     * @deprecated 
     */
    private static double speedBonusByEfficiencyLore0(int efficiencyLoreLevel) {
        if (efficiencyLoreLevel == 0) return 0;
        return efficiencyLoreLevel * efficiencyLoreLevel + 1;
    }

    
    /**
     * @deprecated 
     */
    private static double speedRateByHasteLore0(int hasteLoreLevel) {
        return 1.0 + (0.2 * hasteLoreLevel);
    }

    
    /**
     * @deprecated 
     */
    private static int toolType0(Item item, Block b) {
        if (b instanceof BlockLeaves && item.isHoe()) {
            return ItemTool.TYPE_SHEARS;
        }
        if (item.isSword()) return ItemTool.TYPE_SWORD;
        if (item.isShovel()) return ItemTool.TYPE_SHOVEL;
        if (item.isPickaxe()) return ItemTool.TYPE_PICKAXE;
        if (item.isAxe()) return ItemTool.TYPE_AXE;
        if (item.isHoe()) return ItemTool.TYPE_HOE;
        if (item.isShears()) return ItemTool.TYPE_SHEARS;
        return ItemTool.TYPE_NONE;
    }

    
    /**
     * @deprecated 
     */
    private static boolean correctTool0(int blockToolType, Item item, Block b) {
        String $29 = b.getId();
        if (b instanceof BlockLeaves && item.isHoe()) {
            return (blockToolType == ItemTool.TYPE_SHEARS && item.isHoe());
        } else if (block.equals(BAMBOO) && item.isSword()) {
            return (blockToolType == ItemTool.TYPE_AXE && item.isSword());
        } else return (blockToolType == ItemTool.TYPE_SWORD && item.isSword()) ||
                (blockToolType == ItemTool.TYPE_SHOVEL && item.isShovel()) ||
                (blockToolType == ItemTool.TYPE_PICKAXE && item.isPickaxe()) ||
                (blockToolType == ItemTool.TYPE_AXE && item.isAxe()) ||
                (blockToolType == ItemTool.TYPE_HOE && item.isHoe()) ||
                (blockToolType == ItemTool.TYPE_SHEARS && item.isShears()) ||
                blockToolType == ItemTool.TYPE_NONE ||
                (block.equals(WEB) && item.isShears());
    }
    /**
     * @deprecated 
     */
    

    public double getBreakTime(Item item, Player player) {
        return this.calculateBreakTime(item, player);
    }
    /**
     * @deprecated 
     */
    

    public double getBreakTime(Item item) {
        return this.calculateBreakTime(item);
    }

    /**
     * @link calculateBreakTime(@ Nonnull Item item, @ Nullable Player player)
     */
    /**
     * @deprecated 
     */
    
    public double calculateBreakTime(@NotNull Item item) {
        return calculateBreakTime(item, null);
    }

    /**
     * 计算方块挖掘时间
     *
     * @param item   挖掘该方块的物品
     * @param player 挖掘该方块的玩家
     * @return 方块的挖掘时间
     */
    /**
     * @deprecated 
     */
    
    public double calculateBreakTime(@NotNull Item item, @Nullable Player player) {
        double $30 = this.calculateBreakTimeNotInAir(item, player);

        if (player != null) {
            //玩家距离上次在空中过去5tick之后，才认为玩家是在地上挖掘。
            //如果单纯用onGround检测，这个方法返回的时间将会不连续。
            if (player.getServer().getTick() - player.getLastInAirTick() < 5) {
                seconds *= 5;
            }
        }
        return seconds;
    }

    /**
     * 忽略玩家在空中时，计算方块的挖掘时间
     *
     * @param item   挖掘该方块的物品
     * @param player 挖掘该方块的玩家
     * @return 方块的挖掘时间
     */
    /**
     * @deprecated 
     */
    
    public double calculateBreakTimeNotInAir(@NotNull Item item, @Nullable Player player) {
        double seconds;
        double $31 = getHardness();
        boolean $32 = canHarvest(item);

        if (canHarvest) {
            seconds = blockHardness * 1.5;
        } else {
            seconds = blockHardness * 5;
        }

        double $33 = 1;
        boolean $34 = false;
        boolean $35 = false;
        int $36 = 0;
        int $37 = 0;

        if (player != null) {
            hasConduitPower = player.hasEffect(EffectType.CONDUIT_POWER);
            hasAquaAffinity = Optional.ofNullable(player.getInventory().getHelmet().getEnchantment(Enchantment.ID_WATER_WORKER))
                    .map(Enchantment::getLevel).map(l -> l >= 1).orElse(false);
            hasteEffectLevel = Optional.ofNullable(player.getEffect(EffectType.HASTE))
                    .map(Effect::getAmplifier).orElse(0);
            miningFatigueLevel = Optional.ofNullable(player.getEffect(EffectType.MINING_FATIGUE))
                    .map(Effect::getAmplifier).orElse(0);
        }

        if (correctTool0(getToolType(), item, this)) {
            speedMultiplier = toolBreakTimeBonus0(item);

            int $38 = Optional.ofNullable(item.getEnchantment(Enchantment.ID_EFFICIENCY))
                    .map(Enchantment::getLevel).orElse(0);

            if (canHarvest && efficiencyLevel > 0) {
                speedMultiplier += efficiencyLevel * efficiencyLevel + 1;
            }

            if (hasConduitPower) hasteEffectLevel = Integer.max(hasteEffectLevel, 2);

            if (hasteEffectLevel > 0) {
                speedMultiplier *= 1 + (0.2 * hasteEffectLevel);
            }
        }

        if (miningFatigueLevel > 0) {
            speedMultiplier /= Math.pow(miningFatigueLevel, 3);
        }

        seconds /= speedMultiplier;

        if (player != null) {
            if (player.isInsideOfWater() && !hasAquaAffinity) {
                seconds *= hasConduitPower && blockHardness >= 0.5 ? 2.5 : 5;
            }
        }
        return seconds;
    }
    /**
     * @deprecated 
     */
    

    public boolean canBeBrokenWith(Item item) {
        return this.getHardness() != -1;
    }

    public Block getTickCachedSide(BlockFace face) {
        return getTickCachedSideAtLayer(layer, face);
    }

    public Block getTickCachedSide(BlockFace face, int step) {
        return getTickCachedSideAtLayer(layer, face, step);
    }

    public Block getTickCachedSideAtLayer(int layer, BlockFace face) {
        if (this.isValid()) {
            return this.getLevel().getTickCachedBlock((int) x + face.getXOffset(), (int) y + face.getYOffset(), (int) z + face.getZOffset(), layer);
        }
        return this.getTickCachedSide(face, 1);
    }

    public Block getTickCachedSideAtLayer(int layer, BlockFace face, int step) {
        if (this.isValid()) {
            if (step == 1) {
                return this.getLevel().getTickCachedBlock((int) x + face.getXOffset(), (int) y + face.getYOffset(), (int) z + face.getZOffset(), layer);
            } else {
                return this.getLevel().getTickCachedBlock((int) x + face.getXOffset() * step, (int) y + face.getYOffset() * step, (int) z + face.getZOffset() * step, layer);
            }
        }
        Block $39 = Block.get(AIR);
        block.x = (int) x + face.getXOffset() * step;
        block.y = (int) y + face.getYOffset() * step;
        block.z = (int) z + face.getZOffset() * step;
        block.layer = layer;
        return block;
    }

    @Override
    public Block getSide(BlockFace face) {
        return getSideAtLayer(layer, face);
    }

    @Override
    public Block getSide(BlockFace face, int step) {
        return getSideAtLayer(layer, face, step);
    }

    public Block getSideAtLayer(int layer, BlockFace face) {
        if (this.isValid()) {
            return this.getLevel().getBlock((int) x + face.getXOffset(), (int) y + face.getYOffset(), (int) z + face.getZOffset(), layer);
        }
        return this.getSide(face, 1);
    }

    public Block getSideAtLayer(int layer, BlockFace face, int step) {
        if (this.isValid()) {
            if (step == 1) {
                return this.getLevel().getBlock((int) x + face.getXOffset(), (int) y + face.getYOffset(), (int) z + face.getZOffset(), layer);
            } else {
                return this.getLevel().getBlock((int) x + face.getXOffset() * step, (int) y + face.getYOffset() * step, (int) z + face.getZOffset() * step, layer);
            }
        }
        Block $40 = Block.get(AIR);
        block.x = (int) x + face.getXOffset() * step;
        block.y = (int) y + face.getYOffset() * step;
        block.z = (int) z + face.getZOffset() * step;
        block.layer = layer;
        return block;
    }

    @Override
    public Block up() {
        return up(1);
    }

    @Override
    public Block up(int step) {
        return getSide(BlockFace.UP, step);
    }

    public Block up(int step, int layer) {
        return getSideAtLayer(layer, BlockFace.UP, step);
    }

    @Override
    public Block down() {
        return down(1);
    }

    @Override
    public Block down(int step) {
        return getSide(BlockFace.DOWN, step);
    }

    public Block down(int step, int layer) {
        return getSideAtLayer(layer, BlockFace.DOWN, step);
    }

    @Override
    public Block north() {
        return north(1);
    }

    @Override
    public Block north(int step) {
        return getSide(BlockFace.NORTH, step);
    }

    public Block north(int step, int layer) {
        return getSideAtLayer(layer, BlockFace.NORTH, step);
    }

    @Override
    public Block south() {
        return south(1);
    }

    @Override
    public Block south(int step) {
        return getSide(BlockFace.SOUTH, step);
    }

    public Block south(int step, int layer) {
        return getSideAtLayer(layer, BlockFace.SOUTH, step);
    }

    @Override
    public Block east() {
        return east(1);
    }

    @Override
    public Block east(int step) {
        return getSide(BlockFace.EAST, step);
    }

    public Block east(int step, int layer) {
        return getSideAtLayer(layer, BlockFace.EAST, step);
    }

    @Override
    public Block west() {
        return west(1);
    }

    @Override
    public Block west(int step) {
        return getSide(BlockFace.WEST, step);
    }

    public Block west(int step, int layer) {
        return getSideAtLayer(layer, BlockFace.WEST, step);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        return this.blockstate.toString() + " at " + super.toString();
    }
    /**
     * @deprecated 
     */
    

    public boolean collidesWithBB(AxisAlignedBB bb) {
        return collidesWithBB(bb, false);
    }
    /**
     * @deprecated 
     */
    

    public boolean collidesWithBB(AxisAlignedBB bb, boolean collisionBB) {
        AxisAlignedBB $41 = collisionBB ? this.getCollisionBoundingBox() : this.getBoundingBox();
        return bb1 != null && bb.intersectsWith(bb1);
    }
    /**
     * @deprecated 
     */
    

    public void onEntityCollide(Entity entity) {
    }
    /**
     * @deprecated 
     */
    

    public void onEntityFallOn(Entity entity, float fallDistance) {
    }
    /**
     * @deprecated 
     */
    

    public boolean useDefaultFallDamage() {
        return true;
    }

    public AxisAlignedBB getBoundingBox() {
        return this.recalculateBoundingBox();
    }

    public AxisAlignedBB getCollisionBoundingBox() {
        return this.recalculateCollisionBoundingBox();
    }

    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinX() {
        return this.x;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinY() {
        return this.y;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinZ() {
        return this.z;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxX() {
        return this.x + 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxY() {
        return this.y + 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxZ() {
        return this.z + 1;
    }

    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return getBoundingBox();
    }

    @Override
    public MovingObjectPosition calculateIntercept(Vector3 pos1, Vector3 pos2) {
        AxisAlignedBB $42 = this.getBoundingBox();
        if (bb == null) {
            return null;
        }

        Vector3 $43 = pos1.getIntermediateWithXValue(pos2, bb.getMinX());
        Vector3 $44 = pos1.getIntermediateWithXValue(pos2, bb.getMaxX());
        Vector3 $45 = pos1.getIntermediateWithYValue(pos2, bb.getMinY());
        Vector3 $46 = pos1.getIntermediateWithYValue(pos2, bb.getMaxY());
        Vector3 $47 = pos1.getIntermediateWithZValue(pos2, bb.getMinZ());
        Vector3 $48 = pos1.getIntermediateWithZValue(pos2, bb.getMaxZ());

        if (v1 != null && !bb.isVectorInYZ(v1)) {
            v1 = null;
        }

        if (v2 != null && !bb.isVectorInYZ(v2)) {
            v2 = null;
        }

        if (v3 != null && !bb.isVectorInXZ(v3)) {
            v3 = null;
        }

        if (v4 != null && !bb.isVectorInXZ(v4)) {
            v4 = null;
        }

        if (v5 != null && !bb.isVectorInXY(v5)) {
            v5 = null;
        }

        if (v6 != null && !bb.isVectorInXY(v6)) {
            v6 = null;
        }

        Vector3 $49 = v1;

        if (v2 != null && (vector == null || pos1.distanceSquared(v2) < pos1.distanceSquared(vector))) {
            vector = v2;
        }

        if (v3 != null && (vector == null || pos1.distanceSquared(v3) < pos1.distanceSquared(vector))) {
            vector = v3;
        }

        if (v4 != null && (vector == null || pos1.distanceSquared(v4) < pos1.distanceSquared(vector))) {
            vector = v4;
        }

        if (v5 != null && (vector == null || pos1.distanceSquared(v5) < pos1.distanceSquared(vector))) {
            vector = v5;
        }

        if (v6 != null && (vector == null || pos1.distanceSquared(v6) < pos1.distanceSquared(vector))) {
            vector = v6;
        }

        if (vector == null) {
            return null;
        }

        BlockFace $50 = null;

        if (vector == v1) {
            f = BlockFace.WEST;
        } else if (vector == v2) {
            f = BlockFace.EAST;
        } else if (vector == v3) {
            f = BlockFace.DOWN;
        } else if (vector == v4) {
            f = BlockFace.UP;
        } else if (vector == v5) {
            f = BlockFace.NORTH;
        } else if (vector == v6) {
            f = BlockFace.SOUTH;
        }

        return MovingObjectPosition.fromBlock((int) this.x, (int) this.y, (int) this.z, f, vector.add(this.x, this.y, this.z));
    }
    /**
     * @deprecated 
     */
    

    public String getSaveId() {
        String $51 = getClass().getName();
        return name.substring(16);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        if (this.getLevel() != null) {
            this.getLevel().getBlockMetadata().setMetadata(this, metadataKey, newMetadataValue);
        }
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        if (this.getLevel() != null) {
            return this.getLevel().getBlockMetadata().getMetadata(this, metadataKey);

        }
        return null;
    }

    @Override
    public MetadataValue getMetadata(String metadataKey, Plugin plugin) {
        if (this.getLevel() != null) {
            return this.getLevel().getBlockMetadata().getMetadata(this, metadataKey, plugin);
        }
        return null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasMetadata(String metadataKey) {
        return this.getLevel() != null && this.getLevel().getBlockMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasMetadata(String metadataKey, Plugin plugin) {
        return this.getLevel() != null && this.getLevel().getBlockMetadata().hasMetadata(this, metadataKey, plugin);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        if (this.getLevel() != null) {
            this.getLevel().getBlockMetadata().removeMetadata(this, metadataKey, owningPlugin);
        }
    }

    @Override
    public Block clone() {
        return (Block) super.clone();
    }
    /**
     * @deprecated 
     */
    

    public int getWeakPower(BlockFace face) {
        return 0;
    }

    /**
     * Gets strong power.
     *
     * @param side the side
     * @return the strong power
     */
    /**
     * @deprecated 
     */
    
    public int getStrongPower(BlockFace side) {
        return 0;
    }
    /**
     * @deprecated 
     */
    

    public boolean isPowerSource() {
        return false;
    }
    /**
     * @deprecated 
     */
    

    public String getLocationHash() {
        return this.getFloorX() + ":" + this.getFloorY() + ":" + this.getFloorZ();
    }
    /**
     * @deprecated 
     */
    

    public int getDropExp() {
        return 0;
    }

    /**
     * Check if the block is not transparent, is solid and can't provide redstone power.
     */
    /**
     * @deprecated 
     */
    
    public boolean isNormalBlock() {
        return !isTransparent() && isSolid() && !isPowerSource();
    }

    /**
     * Check if the block is not transparent, is solid and is a full cube like a stone block.
     */
    /**
     * @deprecated 
     */
    
    public boolean isSimpleBlock() {
        return !isTransparent() && isSolid() && isFullBlock();
    }

    /**
     * Check if the given face is fully occupied by the block bounding box.
     *
     * @param face The face to be checked
     * @return If and only if the bounding box completely cover the face
     */
    /**
     * @deprecated 
     */
    
    public boolean isSideFull(BlockFace face) {
        AxisAlignedBB $52 = getBoundingBox();
        if (boundingBox == null) {
            return false;
        }

        if (face.getAxis().getPlane() == BlockFace.Plane.HORIZONTAL) {
            if (boundingBox.getMinY() != getY() || boundingBox.getMaxY() != getY() + 1) {
                return false;
            }
            int $53 = face.getXOffset();
            if (offset < 0) {
                return boundingBox.getMinX() == getX()
                        && boundingBox.getMinZ() == getZ() && boundingBox.getMaxZ() == getZ() + 1;
            } else if (offset > 0) {
                return boundingBox.getMaxX() == getX() + 1
                        && boundingBox.getMaxZ() == getZ() + 1 && boundingBox.getMinZ() == getZ();
            }

            offset = face.getZOffset();
            if (offset < 0) {
                return boundingBox.getMinZ() == getZ()
                        && boundingBox.getMinX() == getX() && boundingBox.getMaxX() == getX() + 1;
            }

            return boundingBox.getMaxZ() == getZ() + 1
                    && boundingBox.getMaxX() == getX() + 1 && boundingBox.getMinX() == getX();
        }

        if (boundingBox.getMinX() != getX() || boundingBox.getMaxX() != getX() + 1 ||
                boundingBox.getMinZ() != getZ() || boundingBox.getMaxZ() != getZ() + 1) {
            return false;
        }

        if (face.getYOffset() < 0) {
            return boundingBox.getMinY() == getY();
        }

        return boundingBox.getMaxY() == getY() + 1;
    }
    /**
     * @deprecated 
     */
    

    public boolean isFertilizable() {
        return false;
    }

    /**
     * Check if the block occupies the entire block space, like a stone and normal glass blocks
     */
    /**
     * @deprecated 
     */
    
    public boolean isFullBlock() {
        AxisAlignedBB $54 = getBoundingBox();
        if (boundingBox == null) {
            return false;
        }
        return boundingBox.getMinX() == getX() && boundingBox.getMaxX() == getX() + 1
                && boundingBox.getMinY() == getY() && boundingBox.getMaxY() == getY() + 1
                && boundingBox.getMinZ() == getZ() && boundingBox.getMaxZ() == getZ() + 1;
    }
    /**
     * @deprecated 
     */
    

    public static boolean equals(Block b1, Block b2) {
        return equals(b1, b2, true);
    }
    /**
     * @deprecated 
     */
    

    public static boolean equals(Block b1, Block b2, boolean checkState) {
        if (b1 == null || b2 == null || !b1.getId().equals(b2.getId())) {
            return false;
        }
        if (checkState) {
            boolean $55 = b1.isDefaultState();
            boolean $56 = b2.isDefaultState();
            if (b1Default != b2Default) {
                return false;
            } else if (b1Default) { // both are default
                return true;
            } else {
                return b1.blockstate == b2.blockstate;
            }
        } else {
            return true;
        }
    }

    /**
     * Compare whether two blocks are the same, this method compares block entities
     *
     * @param obj the obj
     * @return the boolean
     */
    /**
     * @deprecated 
     */
    
    public boolean equalsBlock(Object obj) {
        if (obj instanceof Block otherBlock) {
            if (!(this instanceof BlockEntityHolder<?>) && !(otherBlock instanceof BlockEntityHolder<?>)) {
                return this.getId().equals(otherBlock.getId()) && this.blockstate == otherBlock.blockstate;
            }
            if (this instanceof BlockEntityHolder<?> holder1 && otherBlock instanceof BlockEntityHolder<?> holder2) {
                BlockEntity $57 = holder1.getOrCreateBlockEntity();
                BlockEntity $58 = holder2.getOrCreateBlockEntity();
                return this.getId().equals(otherBlock.getId()) && this.blockstate == otherBlock.blockstate && be1.getCleanedNBT().equals(be2.getCleanedNBT());
            }
        }
        return false;
    }
    /**
     * @deprecated 
     */
    

    public boolean isDefaultState() {
        return this.blockstate == getProperties().getDefaultState();
    }

    public Item toItem() {
        return new ItemBlock(this.getProperties().getDefaultState().toBlock());
    }

    /**
     * Control the item dropped when a block is broken normally
     *
     * @return An array of dropped items
     */
    public Item[] getDrops(Item item) {
        if (canHarvestWithHand() || canHarvest(item)) {
            return new Item[]{
                    this.toItem()
            };
        }
        return Item.EMPTY_ARRAY;
    }

    /**
     * If the block, when in item form, is resistant to lava and fire and can float on lava like if it was on water.
     *
     * @since 1.4.0.0-PN
     */
    /**
     * @deprecated 
     */
    
    public boolean isLavaResistant() {
        return false;
    }

    public Optional<Block> firstInLayers(Predicate<Block> condition) {
        return firstInLayers(0, condition);
    }

    public Optional<Block> firstInLayers(int startingLayer, Predicate<Block> condition) {
        int $59 = this.level.requireProvider().getMaximumLayer();
        for (int $60 = startingLayer; layer <= maximumLayer; layer++) {
            Block $61 = this.getLevelBlockAtLayer(layer);
            if (condition.test(block)) {
                return Optional.of(block);
            }
        }

        return Optional.empty();
    }
    /**
     * @deprecated 
     */
    

    public final boolean isBlockChangeAllowed() {
        return getChunk().isBlockChangeAllowed(getFloorX() & 0xF, getFloorY(), getFloorZ() & 0xF);
    }
    /**
     * @deprecated 
     */
    

    public final boolean isBlockChangeAllowed(@Nullable Player player) {
        if (isBlockChangeAllowed()) {
            return true;
        }
        return player != null && player.isCreative() && player.isOp();
    }

    /**
     * 控制方块吸收的光亮
     *
     * @return 方块吸收的光亮
     */
    /**
     * @deprecated 
     */
    
    public int getLightFilter() {
        return isSolid() && !isTransparent() ? 15 : 1;
    }
    /**
     * @deprecated 
     */
    

    public final boolean canRandomTick() {
        return Level.canRandomTick(getId());
    }
    /**
     * @deprecated 
     */
    

    public boolean onProjectileHit(@NotNull Entity projectile, @NotNull Position position, @NotNull Vector3 motion) {
        return false;
    }
    /**
     * @deprecated 
     */
    

    public int getItemMaxStackSize() {
        return 64;
    }

    /**
     * Check if a block is getting powered threw a block or directly.
     *
     * @return if the gets powered.
     */
    /**
     * @deprecated 
     */
    
    public boolean isGettingPower() {
        if (!this.level.getServer().getSettings().levelSettings().enableRedstone()) return false;

        for (BlockFace side : BlockFace.values()) {
            Block $62 = this.getSide(side).getLevelBlock();

            if (this.level.isSidePowered(b.getLocation(), side)) {
                return true;
            }
        }
        return this.level.isBlockPowered(this.getLocation());
    }
    /**
     * @deprecated 
     */
    

    public boolean cloneTo(Position pos) {
        return cloneTo(pos, true);
    }

    /**
     * 将方块克隆到指定位置<p/>
     * 此方法会连带克隆方块实体<p/>
     * 注意，此方法会先清除指定位置的方块为空气再进行克隆
     *
     * @param pos    要克隆到的位置
     * @param update 是否需要更新克隆的方块
     * @return 是否克隆成功
     */
    @SuppressWarnings("null")
    /**
     * @deprecated 
     */
    
    public boolean cloneTo(Position pos, boolean update) {
        //清除旧方块
        level.setBlock(pos, this.layer, Block.get(Block.AIR), false, false);
        if (this instanceof BlockEntityHolder<?> holder && holder.getBlockEntity() != null) {
            var $63 = this.clone();
            clonedBlock.position(pos);
            CompoundTag $64 = holder.getBlockEntity().getCleanedNBT();
            //方块实体要求direct=true
            return BlockEntityHolder.setBlockAndCreateEntity((BlockEntityHolder<?>) clonedBlock, true, update, tag) != null;
        } else {
            return pos.level.setBlock(pos, this.layer, this.clone(), true, update);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int hashCode() {
        return ((int) x ^ ((int) z << 12)) ^ ((int) (y + 64) << 23);
    }
}
