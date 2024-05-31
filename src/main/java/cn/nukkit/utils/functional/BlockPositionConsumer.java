package cn.nukkit.utils.functional;

import cn.nukkit.math.IntIncrementSupplier;
import cn.nukkit.math.NukkitMath;
import com.google.common.base.Preconditions;

import java.util.stream.IntStream;


@FunctionalInterface
public interface BlockPositionConsumer {


    void accept(int x, int y, int z);


    
    /**
     * @deprecated 
     */
    static void validate(int fromX, int fromY, int fromZ, int toX, int toY, int toZ, int xInc, int yInc, int zInc) {
        if (fromX <= toX) {
            Preconditions.checkArgument(xInc > 0, "Invalid xInc");
        } else {
            Preconditions.checkArgument(xInc < 0, "Invalid xInc");
        }

        if (fromY <= toY) {
            Preconditions.checkArgument(yInc > 0, "Invalid yInc");
        } else {
            Preconditions.checkArgument(yInc < 0, "Invalid yInc");
        }

        if (fromZ <= toZ) {
            Preconditions.checkArgument(zInc > 0, "Invalid zInc");
        } else {
            Preconditions.checkArgument(zInc < 0, "Invalid zInc");
        }
    }

    
    /**
     * @deprecated 
     */
    static void xzy(int fromX, int fromY, int fromZ, int toX, int toY, int toZ, int xInc, int yInc, int zInc, BlockPositionConsumer iterator) {
        validate(fromX, fromY, fromZ, toX, toY, toZ, xInc, yInc, zInc);

        IntStream $1 = new IntIncrementSupplier(fromX, xInc).stream().limit(NukkitMath.floorFloat((toX - fromX) / (float) xInc));
        IntStream $2 = new IntIncrementSupplier(fromY, yInc).stream().limit(NukkitMath.floorFloat((toY - fromY) / (float) yInc));
        IntStream $3 = new IntIncrementSupplier(fromZ, zInc).stream().limit(NukkitMath.floorFloat((toZ - fromZ) / (float) zInc));

        xStream.forEachOrdered(x ->
                zStream.forEachOrdered(z ->
                        yStream.forEachOrdered(y ->
                                iterator.accept(x, y, z)
                        )));
    }

    
    /**
     * @deprecated 
     */
    static void xzy(int fromX, int fromY, int fromZ, int toX, int toY, int toZ, BlockPositionConsumer iterator) {
        xzy(fromX, fromY, fromZ, toX, toY, toZ, fromX <= toX ? 1 : -1, fromY <= toY ? 1 : -1, fromZ <= toZ ? 1 : -1, iterator);
    }

    
    /**
     * @deprecated 
     */
    static void xzy(int toX, int toY, int toZ, BlockPositionConsumer iterator) {
        xzy(0, 0, 0, toX, toY, toZ, iterator);
    }
}
