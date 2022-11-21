package cn.nukkit.nbt;

import cn.nukkit.Nukkit;
import cn.nukkit.nbt.tag.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SNBTTest {
    @Test
    void TestDeserializationSNBT() {
        var str =
                """
                        {"Air":300s,"Armor":[{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b},{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b},{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b},{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b}],"AttackTime":0s,"Attributes":[{"Base":0f,"Current":0f,"DefaultMax":1024f,"DefaultMin":-1024f,"Max":1024f,"Min":-1024f,"Name":"minecraft:luck"},{"Base":10f,"Current":-8f,"DefaultMax":10f,"DefaultMin":0f,"Max":10f,"Min":0f,"Name":"minecraft:health"},{"Base":0f,"Current":0f,"DefaultMax":16f,"DefaultMin":0f,"Max":16f,"Min":0f,"Name":"minecraft:absorption"},{"Base":0f,"Current":0f,"DefaultMax":1f,"DefaultMin":0f,"Max":1f,"Min":0f,"Name":"minecraft:knockback_resistance"},{"Base":0.25f,"Current":0.25f,"DefaultMax":3.40282e+38f,"DefaultMin":0f,"Max":3.40282e+38f,"Min":0f,"Name":"minecraft:movement"},{"Base":0.02f,"Current":0.02f,"DefaultMax":3.40282e+38f,"DefaultMin":0f,"Max":3.40282e+38f,"Min":0f,"Name":"minecraft:underwater_movement"},{"Base":0.02f,"Current":0.02f,"DefaultMax":3.40282e+38f,"DefaultMin":0f,"Max":3.40282e+38f,"Min":0f,"Name":"minecraft:lava_movement"},{"Base":16f,"Current":16f,"DefaultMax":2048f,"DefaultMin":0f,"Max":2048f,"Min":0f,"Name":"minecraft:follow_range"}],"BodyRot":-2.70421f,"BreedCooldown":0,"Chested":0b,"Color":0b,"Color2":0b,"Dead":0b,"DeathTime":0s,"FallDistance":0f,"HurtTime":0s,"InLove":0,"Invulnerable":0b,"IsAngry":0b,"IsAutonomous":0b,"IsBaby":0b,"IsEating":0b,"IsGliding":0b,"IsGlobal":0b,"IsIllagerCaptain":0b,"IsOrphaned":0b,"IsOutOfControl":0b,"IsPregnant":0b,"IsRoaring":0b,"IsScared":0b,"IsStunned":0b,"IsSwimming":0b,"IsTamed":0b,"IsTrusting":0b,"LastDimensionId":0,"LeasherID":-1L,"LootDropped":0b,"LoveCause":0L,"Mainhand":[{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b}],"MarkVariant":0,"NaturalSpawn":1b,"Offhand":[{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b}],"OnGround":1b,"OwnerNew":-1L,"PortalCooldown":0,"Pos":[0.442949f, 65f, -16.7079f],"Rotation":[-2.70421f, 0f],"Saddled":0b,"Sheared":0b,"ShowBottom":0b,"Sitting":0b,"SkinID":0,"Strength":0,"StrengthMax":0,"Surface":1b,"Tags":[],"TargetID":-1L,"TradeExperience":0,"TradeTier":0,"UniqueID":-4294967212L,"Variant":0,"boundX":0,"boundY":0,"boundZ":0,"canPickupItems":0b,"definitions":["+minecraft:pig","+","+minecraft:pig_adult","+minecraft:pig_unsaddled"],"expDropEnabled":1b,"hasBoundOrigin":0b,"hasSetCanPickupItems":1b,"identifier":"minecraft:pig","internalComponents":{"EntityStorageKeyComponent":{"StorageKey":"T"}}}
                        """;
        var NBT = SNBTParser.parseSNBT(str);
        var SNBT = NBT.toSNBT(4);
        System.out.println(SNBT);
        assertEquals(NBT, SNBTParser.parseSNBT(SNBT));
    }

    @Test
    void TestNBT2SNBT() {
        var oldNBT = new CompoundTag()
                .putInt("test1", 1)
                .putString("test2", "hahaha")
                .putBoolean("test3", false)
                .putByte("test4", 12)
                .putFloat("test5", (float) 1.22)
                .putDouble("test6", 2.333)
                .putShort("test7", 2131)
                .putByteArray("test8", new byte[]{1, 2, 3, 4, 5})
                .putList(new ListTag<>("test9")
                        .add(new ListTag<>()
                                .add(new IntTag("", 2))
                                .add(new IntTag("", 3)))
                        .add(new ListTag<>()
                                .add(new DoubleTag("", 3.33))
                                .add(new DoubleTag("", 4.44)))
                        .add(new ListTag<>()
                                .add(new ByteArrayTag("", new byte[]{9, 8, 7, 6, 5}))
                                .add(new ByteArrayTag("", new byte[]{11, 22, 33, 44, 55}))))
                .putList(new ListTag<>("test10")
                        .add(new CompoundTag()
                                .putString("string1", "asdafsdf")
                                .putString("string2", "afasdfcc"))
                        .add(new CompoundTag()
                                .putString("string1", "asdafsdf")
                                .putString("string2", "afasdfcc")))
                .putCompound("nestCompoundTag", new CompoundTag()
                        .putString("string3", "123123123")
                        .putString("string4", "zxdzsfdsdf"))
                .putList(new ListTag<>("testEndTag")
                        .add(new EndTag())
                        .add(new EndTag())
                        .add(new EndTag()));
        var oldSNBT = oldNBT.toSNBT(4);
        var newNBT = SNBTParser.parseSNBT(oldSNBT);
        var newSNBT = newNBT.toSNBT(4);
        assertEquals(oldNBT, newNBT);
        System.out.println("通过测试1");
        assertEquals(oldSNBT, newSNBT);
        System.out.println("通过测试2");
    }

    @Test
    void StructureNBTTest() throws IOException, URISyntaxException {
        File baseFile = new File(Nukkit.class.getClassLoader().getResource("structures").toURI());
        File[] files = baseFile.listFiles();
        end:
        for (File file : files) {
            if (file.isDirectory()) {
                for (var nbt : file.listFiles()) {
                    var oldNBT = NBTIO.readCompressed(new FileInputStream(nbt), ByteOrder.BIG_ENDIAN);
                    var oldSNBT = oldNBT.toSNBT();
                    if (((ListTag) oldNBT.get("entities")).size() == 0) {
                        oldNBT.put("entities", new ListTag<EndTag>());
                    }
                    var newNBT = SNBTParser.parseSNBT(oldSNBT);
                    ((ListTag<CompoundTag>) oldNBT.get("blocks")).getAll().forEach(compoundTag -> {
                        if (compoundTag.contains("nbt")) {
                            var x = (((CompoundTag) compoundTag.get("nbt")));
                            if (x.contains("Items") && ((ListTag) x.get("Items")).size() == 0) {
                                x.put("Items", new ListTag<EndTag>());
                            } else if (x.contains("Patterns") && ((ListTag) x.get("Patterns")).size() == 0) {
                                x.put("Patterns", new ListTag<EndTag>());
                            }
                        }
                    });
                    assertEquals(oldNBT, newNBT);
                }
            }
        }
    }
}
