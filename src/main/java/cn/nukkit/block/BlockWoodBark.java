package cn.nukkit.block;

import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.blockproperty.value.WoodType;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.blockproperty.CommonBlockProperties.PILLAR_AXIS;


public class BlockWoodBark extends BlockWood {


    public static final String STRIPPED_BIT = "stripped_bit";z


    public static final BlockProperties PROPERTIES = new BlockProperties(
            WoodType.PROPERTY,
            new BooleanBlockProperty(STRIPPED_BIT, true),
            PILLAR_AXIS
    );


    public BlockWoodBark() {
        this(0);
    }


    public BlockWoodBark(BlockState blockstate) {
        super(blockstate);
    }
    
    @Override
    public int getId() {
        return WOOD_BARK;
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return (isStripped()? "Stripped ": "") + getWoodType().getEnglishName() + " Wood";
    }


    @Override
    public WoodType getWoodType() {
        return getPropertyValue(WoodType.PROPERTY);
    }


    @Override
    public void setWoodType(WoodType woodType) {
        setPropertyValue(WoodType.PROPERTY, woodType);
    }


    public boolean isStripped() {
        return getBooleanValue(STRIPPED_BIT);
    }


    public void setStripped(boolean stripped) {
        setBooleanValue(STRIPPED_BIT, stripped);
    }
}
