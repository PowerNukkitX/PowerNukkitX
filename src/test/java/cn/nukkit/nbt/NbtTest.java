package cn.nukkit.nbt;

import cn.nukkit.nbt.tag.*;
import cn.nukkit.utils.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufOutputStream;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NbtTest {
    @Test
    void testIntTag() {
        IntTag tag = new IntTag();
        Assertions.assertEquals(0, tag.data);
        tag.setData(1);
        Assertions.assertEquals(1, tag.data);
        Tag tag2 = Tag.newTag(Tag.TAG_Int);
        Assertions.assertEquals(IntTag.class, tag2.getClass());
        Assertions.assertEquals(Tag.TAG_Int, tag2.getId());
    }

    @Test
    void testFloatTag() {
        FloatTag tag = new FloatTag();
        Assertions.assertEquals(0, tag.data);
        tag.setData(1.5F);
        Assertions.assertEquals(1.5F, tag.data);
        Tag tag2 = Tag.newTag(Tag.TAG_Float);
        Assertions.assertEquals(FloatTag.class, tag2.getClass());
        Assertions.assertEquals(Tag.TAG_Float, tag2.getId());
    }

    @Test
    void testIntListTag() {
        IntArrayTag tag = new IntArrayTag();
        Assertions.assertArrayEquals(new int[0], tag.getData());
        tag.setData(new int[]{1, 2, 3, 4, 5});
        Assertions.assertArrayEquals(new int[]{1, 2, 3, 4, 5}, tag.data);
        Assertions.assertEquals(IntArrayTag.class, tag.getClass());
        Assertions.assertEquals(Tag.TAG_Int_Array, tag.getId());
    }

    @Test
    void testListTag() {
        ListTag<IntTag> tag = new ListTag<>();
        tag.add(new IntTag(1));
        tag.add(new IntTag(2));
        tag.add(new IntTag(3));
        Assertions.assertEquals(ListTag.class, tag.getClass());
        Assertions.assertEquals(2, tag.get(1).data);
    }

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
                .putList("list1", new ListTag<>()
                        .add(new ListTag<>()
                                .add(new IntTag(2))
                                .add(new IntTag(3)))
                        .add(new ListTag<>()
                                .add(new DoubleTag(3.33))
                                .add(new DoubleTag(4.44)))
                        .add(new ListTag<>()
                                .add(new ByteArrayTag(new byte[]{9, 8, 7, 6, 5}))
                                .add(new ByteArrayTag(new byte[]{11, 22, 33, 44, 55}))))
                .putList("list2", new ListTag<>()
                        .add(new CompoundTag()
                                .putString("string1", "asdafsdf")
                                .putString("string2", "afasdfcc"))
                        .add(new CompoundTag()
                                .putString("string1", "asdafsdf")
                                .putString("string2", "afasdfcc")))
                .putCompound("nestCompoundTag", new CompoundTag()
                        .putString("string3", "123123123")
                        .putString("string4", "zxdzsfdsdf"))
                .put("endTag", new EndTag());
        var oldSNBT = oldNBT.toSNBT();
        System.out.println(oldSNBT);
        var newNbt = SNBTParser.parse(oldSNBT);
        assertEquals(oldSNBT, newNbt.toSNBT());
    }

    @Test
    @SneakyThrows
    void testReadNbt() {
        try (var stream = NbtTest.class.getClassLoader().getResourceAsStream("block_palette.nbt")) {
            CompoundTag nbt = NBTIO.readCompressed(stream);
            ListTag<CompoundTag> blocks = nbt.getList("blocks", CompoundTag.class);
            Assertions.assertTrue(() -> {
                long count = blocks.getAll().stream().filter(c -> c.getString("name").equalsIgnoreCase("minecraft:air")).count();
                return count == 1;
            });
        }
    }

    @Test
    @SneakyThrows
    void testWriteNbt() {
        var write = new LinkedCompoundTag()
                .putInt("test1", 1)
                .putString("test2", "hahaha")
                .putBoolean("test3", false)
                .putByte("test4", 12)
                .putFloat("test5", (float) 1.22)
                .putDouble("test6", 2.333);
        File file = new File("src/test/resources/test.nbt");
        NBTIO.write(write, file);
        CompoundTag read = NBTIO.read(file);
        Assertions.assertEquals(1, read.getInt("test1"));
        FileUtils.delete(file);
    }

    @Test
    @SneakyThrows
    void testWriteReadGzipNbt() {
        var write = new LinkedCompoundTag()
                .putInt("test1", 1)
                .putString("test2", "hahaha")
                .putBoolean("test3", false)
                .putByte("test4", 12)
                .putFloat("test5", (float) 1.22)
                .putDouble("test6", 2.333);
        byte[] bytes = NBTIO.writeGZIPCompressed(write, ByteOrder.BIG_ENDIAN);
        BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(bytes));
        CompoundTag read = NBTIO.readCompressed(inputStream);
        inputStream.close();
        Assertions.assertEquals(1, read.getInt("test1"));
    }

    @Test
    @SneakyThrows
    void testWriteByteBufOutputStream() {
        var write = new LinkedCompoundTag()
                .putInt("test1", 1)
                .putString("test2", "hahaha")
                .putBoolean("test3", false)
                .putByte("test4", 12)
                .putFloat("test5", (float) 1.22)
                .putDouble("test6", 2.333);
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();
        ByteBufOutputStream stream = new ByteBufOutputStream(byteBuf);
        NBTIO.write(write, stream, ByteOrder.LITTLE_ENDIAN, true);
        stream.close();
        byte[] data = Utils.convertByteBuf2Array(byteBuf);
        CompoundTag read = NBTIO.read(data, ByteOrder.LITTLE_ENDIAN, true);
        Assertions.assertEquals(write, read);
    }

    @Test
    @SneakyThrows
    void testWriteReadGzipPlayerNbt() {
        CompoundTag parse = SNBTParser.parse("{\"Invulnerable\":0b,\"lastPlayed\":1707229766L,\"FallDistance\":0.0f,\"TimeSinceRest\":0,\"fogIdentifiers\":[],\"playerGameType\":0,\"Motion\":[0.0d,0.0d,0.0d],\"UUIDLeast\":-7192577760381195715L,\"Health\":20.0f,\"foodSaturationLevel\":20.0f,\"Scale\":1.0f,\"Air\":300s,\"OnGround\":1b,\"Rotation\":[0.0f,0.0f],\"NameTag\":\"CoolLoong2247\",\"UUIDMost\":-6918519038489903261L,\"Pos\":[129.77989953269983d,5.0d,127.44398166306108d],\"firstPlayed\":1707229766L,\"Achievements\":{},\"Fire\":0s,\"HasSeenCredits\":0b,\"Level\":\"world Dim0\",\"foodLevel\":20,\"userProvidedFogIds\":[],\"Inventory\":[]}");
        byte[] bytes = NBTIO.writeGZIPCompressed(parse, ByteOrder.BIG_ENDIAN);
        BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(bytes));
        CompoundTag read1 = NBTIO.readCompressed(bytes);
        CompoundTag read2 = NBTIO.readCompressed(inputStream);
        inputStream.close();
        assertFalse(read1.getBoolean("Invulnerable"));
        assertFalse(read2.getBoolean("Invulnerable"));
    }

    @Test
    @SneakyThrows
    void testContain() {
        var write = new LinkedCompoundTag()
                .putInt("test1", 1)
                .putString("test2", "hahaha")
                .putBoolean("test3", false)
                .putByte("test4", 12)
                .putFloat("test5", (float) 1.22)
                .putDouble("test6", 2.333)
                .putByteArray("test7",new byte[]{1,2,3})
                .putCompound("test8",new CompoundTag())
                .putList("test9",new ListTag<>())
                .putShort("test10",213)
                .putIntArray("test11",new int[]{12,12,12});
        assertTrue(write.contains("test1"));
        assertTrue(write.containsInt("test1"));
        assertTrue(write.containsByte("test4"));
        assertTrue(write.containsDouble("test6"));
        assertFalse(write.containsDouble("test5"));
        assertTrue(write.containsFloat("test5"));

        assertTrue(write.containsByteArray("test7"));
        assertTrue(write.containsCompound("test8"));
        assertTrue(write.containsList("test9"));
        assertTrue(write.containsShort("test10"));
        assertTrue(write.containsIntArray("test11"));
        assertTrue(write.containsNumber("test6"));
    }
}
