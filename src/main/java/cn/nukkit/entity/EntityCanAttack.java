package cn.nukkit.entity;

/** Represents an entity that can attack other entities. */
public interface EntityCanAttack {

    float[] EMPTY_FLOAT_ARRAY = new float[]{0.0f, 0.0f, 0.0f};

    /**
     * Get the damage you can do without carrying items on all difficulties.
     * @return An array containing damage on all difficulties, 0 1 2 for easy, normal and hard difficulties respectively
     */
    default float[] getDiffHandDamage() {
        return EMPTY_FLOAT_ARRAY;
    }

    /**
     * Get the damage that can be dealt without carrying the item at the specified difficulty.
     * @param difficulty difficulty id
     * @return damage
     */
    default float getDiffHandDamage(int difficulty) {
        return difficulty != 0 ? getDiffHandDamage()[difficulty - 1] : 0;
    }

    boolean attackTarget(Entity entity);
}
