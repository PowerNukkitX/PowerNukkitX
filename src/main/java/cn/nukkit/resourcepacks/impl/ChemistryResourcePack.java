package cn.nukkit.resourcepacks.impl;

import cn.nukkit.resourcepacks.AbstractEducationPack;

import java.util.UUID;

public class ChemistryResourcePack extends AbstractEducationPack {

    public static final UUID CHEMISTRY_RESOURCE_PACK_ID = UUID.fromString("0fba4063-dba1-4281-9b89-ff9390653530");

    @Override
    public UUID getPackId() {
        return CHEMISTRY_RESOURCE_PACK_ID;
    }
}