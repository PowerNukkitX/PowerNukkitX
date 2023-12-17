package cn.nukkit.block;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

import static cn.nukkit.utils.BlockColor.*;

/**
 * @author MagicDroidX (Nukkit Project)
 * @apiNote Implements BlockConnectable only on PowerNukkit
 */
@PowerNukkitDifference(info = "Extends BlockWallBase and implements BlockConnectable only on PowerNukkit", since = "1.4.0.0-PN")
public class BlockWall extends BlockWallBase {


    public static final BlockProperty<WallType> WALL_BLOCK_TYPE = new ArrayBlockProperty<>("wall_block_type", true, WallType.class);


    public static final BlockProperties PROPERTIES = new BlockProperties(
            WALL_BLOCK_TYPE,
            WALL_CONNECTION_TYPE_SOUTH,
            WALL_CONNECTION_TYPE_WEST,
            WALL_CONNECTION_TYPE_NORTH,
            WALL_CONNECTION_TYPE_EAST,
            WALL_POST_BIT
    );
    
    @Deprecated
    @DeprecationDetails(reason = "No longer matches the meta directly", replaceWith = "WallType.COBBLESTONE", since = "1.3.0.0-PN")
    public static final int NONE_MOSSY_WALL = 0;
    
    @Deprecated
    @DeprecationDetails(reason = "No longer matches the meta directly", replaceWith = "WallType.MOSSY_COBBLESTONE", since = "1.3.0.0-PN")
    public static final int MOSSY_WALL = 1;
    
    public BlockWall() {
        this(0);
    }

    public BlockWall(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STONE_WALL;
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }


    public WallType getWallType() {
        return getPropertyValue(WALL_BLOCK_TYPE);
    }


    public void setWallType(WallType type) {
        setPropertyValue(WALL_BLOCK_TYPE, type);
    }
    
    @Override
    public String getName() {
        return getWallType().getTypeName();
    }

    @Override

    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }


    public enum WallConnectionType {


    }


    public enum WallType {


        private final BlockColor color;
        private final String typeName;
        
        WallType(BlockColor color) {
            this.color = color;
            String name = Arrays.stream(name().split("_"))
                    .map(part-> part.substring(0,1) + part.substring(1).toLowerCase())
                    .collect(Collectors.joining(" "));
            
            // Concatenation separated to workaround https://bugs.openjdk.java.net/browse/JDK-8077605
            // https://www.reddit.com/r/learnprogramming/comments/32bfle/can_you_explain_this_strange_java8_error/
            typeName = name + " Wall";
        }

        WallType() {
            this(STONE_BLOCK_COLOR);
        }


        public BlockColor getColor() {
            return color;
        }


        public String getTypeName() {
            return typeName;
        }
    }
}
