package cn.nukkit.energy;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class EnergyRegistry {
    public static final Map<String, EnergyType> registeredEnergyTypes = new HashMap<>();

    /**
     * Register a new energy type.
     * @param energyType The energy type to register.
     * @return If the energy type was registered successfully.
     */
    public static boolean registerEnergyType(@NotNull EnergyType energyType) {
        if (!registeredEnergyTypes.containsKey(energyType.getName())) {
            registeredEnergyTypes.put(energyType.getName(), energyType);
            return true;
        }
        return false;
    }

    public static EnergyType getEnergyType(String name) {
        return registeredEnergyTypes.get(name);
    }
}
