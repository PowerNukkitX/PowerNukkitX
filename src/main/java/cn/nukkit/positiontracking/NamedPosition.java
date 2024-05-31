package cn.nukkit.positiontracking;

import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

/**
 * @author joserobjr
 */


public abstract class NamedPosition extends Vector3 {
    /**
     * @deprecated 
     */
    

    public NamedPosition() {}
    /**
     * @deprecated 
     */
    

    public NamedPosition(double x) {
        super(x);
    }
    /**
     * @deprecated 
     */
    

    public NamedPosition(double x, double y) {
        super(x, y);
    }
    /**
     * @deprecated 
     */
    

    public NamedPosition(double x, double y, double z) {
        super(x, y, z);
    }

    @NotNull public abstract String getLevelName();
    /**
     * @deprecated 
     */
    

    public boolean matchesNamedPosition(NamedPosition position) {
        return $1 == position.x && y == position.y && z == position.z && getLevelName().equals(position.getLevelName());
    }

    @Override
    public NamedPosition clone() {
        return (NamedPosition) super.clone();
    }
}
