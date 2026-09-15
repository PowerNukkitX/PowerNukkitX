package org.powernukkitx.resourcepacks.impl;

import org.powernukkitx.resourcepacks.AbstractEducationPack;
import org.powernukkitx.resourcepacks.PackType;

import java.util.UUID;

public class ChemistryBehaviorPack extends AbstractEducationPack {

    public static final UUID CHEMISTRY_BEHAVIOR_PACK_ID = UUID.fromString("6baf8b62-8948-4c99-bb1e-a0cb35dc4579");

    @Override
    public UUID getPackId() {
        return CHEMISTRY_BEHAVIOR_PACK_ID;
    }

    @Override
    public PackType getType() {
        return PackType.BEHAVIOR;
    }
}