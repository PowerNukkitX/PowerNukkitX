package cn.nukkit.item.customitem.data;

import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

public class RenderOffsets {
    public final CompoundTag nbt = new CompoundTag();

    public RenderOffsets(Vector3f mainHandFirstPerson, Vector3f mainHandThirdPerson, Vector3f offHandHandFirstPerson, Vector3f offHandHandThirdPerson) {
        this.nbt.putCompound("main_hand", new CompoundTag()
                        .putCompound("first_person", xyzToCompoundTag(mainHandFirstPerson.x, mainHandFirstPerson.y, mainHandFirstPerson.z))
                        .putCompound("third_person", xyzToCompoundTag(mainHandThirdPerson.x, mainHandThirdPerson.y, mainHandThirdPerson.z)))
                .putCompound("off_hand", new CompoundTag()
                        .putCompound("first_person", xyzToCompoundTag(offHandHandFirstPerson.x, offHandHandFirstPerson.y, offHandHandFirstPerson.z))
                        .putCompound("third_person", xyzToCompoundTag(offHandHandThirdPerson.x, offHandHandThirdPerson.y, offHandHandThirdPerson.z))
                );
    }

    private CompoundTag xyzToCompoundTag(float x, float y, float z) {
        var listTag = new ListTag<FloatTag>("scale");
        listTag.add(new FloatTag("", x));
        listTag.add(new FloatTag("", y));
        listTag.add(new FloatTag("", z));
        return new CompoundTag().putList(listTag);
    }
}
