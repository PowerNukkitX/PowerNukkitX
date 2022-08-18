package cn.nukkit.energy;

import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Defines the interface for energy holders.
 * If a block, an item, a block-entity implements this interface, it can store energy, accept input or provide output.
 *
 * @author superice666
 */
public interface EnergyHolder {
    /**
     * @return The name of this energy holder.
     */
    @NotNull
    String getName();

    /**
     * @param energyType The type of energy to accept.
     * @return If this energy holder can accept input.
     */
    boolean canAcceptInput(EnergyType energyType);

    /**
     * @param energyType The type of energy to accept.
     * @param face       The face the energy is coming from.
     * @return If this energy holder can accept inout from the given face, usually used by blocks.
     */
    default boolean canAcceptInput(EnergyType energyType, BlockFace face) {
        return canAcceptInput(energyType);
    }

    /**
     * @param energyType The type of energy to provide.
     * @return If this energy holder can provide output.
     */
    boolean canProvideOutput(EnergyType energyType);

    /**
     * @param energyType The type of energy to provide.
     * @param face       The face the energy is going to.
     * @return If this energy holder can provide output to the given face, usually used by blocks.
     */
    default boolean canProvideOutput(EnergyType energyType, BlockFace face) {
        return canProvideOutput(energyType);
    }

    /**
     * @param energyType The type of energy to input into this energy holder.
     * @param amount     The amount of energy to input.
     */
    void inputInto(EnergyType energyType, double amount);

    /**
     * @param energyType The type of energy to output from this energy holder.
     * @param amount     The amount of energy to output.
     */
    void outputFrom(EnergyType energyType, double amount);

    /**
     * @return The type of the energy stored in this energy holder. If it can't store any energy, it returns null.
     */
    @Nullable
    EnergyType getStoredEnergyType();

    /**
     * @return The max amount of energy that can stored in this energy holder. If it can't store any energy, it returns 0.
     */
    double getMaxStorage();

    /**
     * @return The amount of energy stored in this energy holder. If it can't store any energy, it returns 0.
     */
    double getStoredEnergy();
}
