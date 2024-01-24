package cn.nukkit.positiontracking;

import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

/**
 * @author joserobjr
 */


public abstract class NamedPosition extends Vector3 {

    public NamedPosition() {}

    public NamedPosition(double x) {
        super(x);
    }

    public NamedPosition(double x, double y) {
        super(x, y);
    }

    public NamedPosition(double x, double y, double z) {
        super(x, y, z);
    }

    @NotNull public abstract String getLevelName();

    public boolean matchesNamedPosition(NamedPosition position) {
        return x == position.x && y == position.y && z == position.z && getLevelName().equals(position.getLevelName());
    }

    @Override
    public NamedPosition clone() {
        return (NamedPosition) super.clone();
    }
}
