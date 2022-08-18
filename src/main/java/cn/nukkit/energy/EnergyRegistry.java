package cn.nukkit.energy;

import java.util.HashMap;
import java.util.Map;

public final class EnergyRegistry {
    public static final Map<String, EnergyType> registeredEnergyTypes = new HashMap<>();

    public static void registerEnergyType(EnergyType energyType) {
        registeredEnergyTypes.put(energyType.getName(), energyType);
    }

    public static EnergyType getEnergyType(String name) {
        return registeredEnergyTypes.get(name);
    }
}
