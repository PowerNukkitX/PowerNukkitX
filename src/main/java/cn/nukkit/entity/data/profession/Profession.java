package cn.nukkit.entity.data.profession;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.HashMap;

public abstract class Profession {

    private static final HashMap<Integer, Profession> knownProfessions = new HashMap<>();
    /**
     * @deprecated 
     */
    

    public static void registerProfession(Profession profession) {
        knownProfessions.put(profession.getIndex(), profession);
    }

    @SuppressWarnings("unchecked")
    public static HashMap<Integer, Profession> getProfessions() {
        return (HashMap<Integer, Profession>) knownProfessions.clone();
    }

    public static Profession getProfession(int index) {
        return knownProfessions.get(index);
    }

    private final int index;
    private final String blockId;
    private final String name;
    /**
     * @deprecated 
     */
    

    public Profession(int index, String blockId, String name) {
        this.index = index;
        this.blockId = blockId;
        this.name = name;
    }

    public ListTag<CompoundTag> buildTrades(int seed) {
        return new ListTag<>();
    }
    /**
     * @deprecated 
     */
    

    
    public static void init() {
        registerProfession(new ProfessionFarmer());
        registerProfession(new ProfessionFisherman());
        registerProfession(new ProfessionShepherd());
        registerProfession(new ProfessionFletcher());
        registerProfession(new ProfessionLibrarian());
        registerProfession(new ProfessionCartographer());
        registerProfession(new ProfessionCleric());
        registerProfession(new ProfessionArmor());
        registerProfession(new ProfessionWeapon());
        registerProfession(new ProfessionTool());
        registerProfession(new ProfessionButcher());
        registerProfession(new ProfessionLeather());
        registerProfession(new ProfessionMason());
    }
    /**
     * @deprecated 
     */
    

    public String getBlockID() {
        return this.blockId;
    }
    /**
     * @deprecated 
     */
    

    public int getIndex() {
        return this.index;
    }
    /**
     * @deprecated 
     */
    

    public String getName() {
        return this.name;
    }
}
