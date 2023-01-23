package cn.nukkit.nbt;

import cn.nukkit.nbt.tag.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SNBTTest {
    @Test
    void TestDeserializationSNBT() {
        var str = """
                {"Air":300s,"Armor":[{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b},{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b},{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b},{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b}],"AttackTime":0s,"Attributes":[{"Base":0.0f,"Current":0.0f,"DefaultMax":1024.0f,"DefaultMin":-1024.0f,"Max":1024.0f,"Min":-1024.0f,"Name":"minecraft:luck"},{"Base":10.0f,"Current":-8.0f,"DefaultMax":10.0f,"DefaultMin":0.0f,"Max":10.0f,"Min":0.0f,"Name":"minecraft:health"},{"Base":0.0f,"Current":0.0f,"DefaultMax":16.0f,"DefaultMin":0.0f,"Max":16.0f,"Min":0.0f,"Name":"minecraft:absorption"},{"Base":0.0f,"Current":0.0f,"DefaultMax":1.0f,"DefaultMin":0.0f,"Max":1.0f,"Min":0.0f,"Name":"minecraft:knockback_resistance"},{"Base":0.25f,"Current":0.25f,"DefaultMax":3.40282E38f,"DefaultMin":0.0f,"Max":3.40282E38f,"Min":0.0f,"Name":"minecraft:movement"},{"Base":0.02f,"Current":0.02f,"DefaultMax":3.40282E38f,"DefaultMin":0.0f,"Max":3.40282E38f,"Min":0.0f,"Name":"minecraft:underwater_movement"},{"Base":0.02f,"Current":0.02f,"DefaultMax":3.40282E38f,"DefaultMin":0.0f,"Max":3.40282E38f,"Min":0.0f,"Name":"minecraft:lava_movement"},{"Base":16.0f,"Current":16.0f,"DefaultMax":2048.0f,"DefaultMin":0.0f,"Max":2048.0f,"Min":0.0f,"Name":"minecraft:follow_range"}],"BodyRot":-2.70421f,"BreedCooldown":0,"Chested":0b,"Color":0b,"Color2":0b,"Dead":0b,"DeathTime":0s,"FallDistance":0.0f,"HurtTime":0s,"InLove":0,"Invulnerable":0b,"IsAngry":0b,"IsAutonomous":0b,"IsBaby":0b,"IsEating":0b,"IsGliding":0b,"IsGlobal":0b,"IsIllagerCaptain":0b,"IsOrphaned":0b,"IsOutOfControl":0b,"IsPregnant":0b,"IsRoaring":0b,"IsScared":0b,"IsStunned":0b,"IsSwimming":0b,"IsTamed":0b,"IsTrusting":0b,"LastDimensionId":0,"LeasherID":-1L,"LootDropped":0b,"LoveCause":0L,"Mainhand":[{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b}],"MarkVariant":0,"NaturalSpawn":1b,"Offhand":[{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b}],"OnGround":1b,"OwnerNew":-1L,"PortalCooldown":0,"Pos":[0.442949f,65.0f,-16.7079f],"Rotation":[-2.70421f,0.0f],"Saddled":0b,"Sheared":0b,"ShowBottom":0b,"Sitting":0b,"SkinID":0,"Strength":0,"StrengthMax":0,"Surface":1b,"Tags":[],"TargetID":-1L,"TradeExperience":0,"TradeTier":0,"UniqueID":-4294967212L,"Variant":0,"boundX":0,"boundY":0,"boundZ":0,"canPickupItems":0b,"definitions":["+minecraft:pig","+","+minecraft:pig_adult","+minecraft:pig_unsaddled"],"expDropEnabled":1b,"hasBoundOrigin":0b,"hasSetCanPickupItems":1b,"identifier":"minecraft:pig","internalComponents":{"EntityStorageKeyComponent":{"StorageKey":"T"}}}
                """;
        str = str.substring(0, str.lastIndexOf('\n'));
        var NBT = SNBTParser.parse(str);
        var SNBT = NBT.toSNBT();
        assertEquals(str, SNBT);
    }

    @Test
    void TestNBT2SNBT() {
        var oldNBT = new LinkedCompoundTag()
                .putInt("test1", 1)
                .putString("test2", "hahaha")
                .putBoolean("test3", false)
                .putByte("test4", 12)
                .putFloat("test5", (float) 1.22)
                .putDouble("test6", 2.333)
                .putShort("test7", 2131)
                .putByteArray("test8", new byte[]{1, 2, 3, 4, 5})
                .putList(new ListTag<>("list1")
                        .add(new ListTag<>("1")
                                .add(new IntTag("", 2))
                                .add(new IntTag("", 3)))
                        .add(new ListTag<>("2")
                                .add(new DoubleTag("", 3.33))
                                .add(new DoubleTag("", 4.44)))
                        .add(new ListTag<>("2")
                                .add(new ByteArrayTag("", new byte[]{9, 8, 7, 6, 5}))
                                .add(new ByteArrayTag("", new byte[]{11, 22, 33, 44, 55}))))
                .putList(new ListTag<>("list2")
                        .add(new CompoundTag()
                                .putString("string1", "asdafsdf")
                                .putString("string2", "afasdfcc"))
                        .add(new CompoundTag()
                                .putString("string1", "asdafsdf")
                                .putString("string2", "afasdfcc")))
                .putCompound("nestCompoundTag", new CompoundTag()
                        .putString("string3", "123123123")
                        .putString("string4", "zxdzsfdsdf"));
        var oldSNBT = oldNBT.toSNBT();
        System.out.println(oldSNBT);
        var newNbt = SNBTParser.parse(oldSNBT);
        assertEquals(oldSNBT, newNbt.toSNBT());
    }
}
