/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nukkit.nbt;

import cn.nukkit.item.Item;
import cn.nukkit.item.MinecraftItemID;
import cn.nukkit.nbt.tag.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author joserobjr
 * @since 2021-02-14
 */
@ExtendWith(PowerNukkitExtension.class)
class NBTIOTest {
    @Test
    void gitHubIssue960() {
        Item badItem = new Item(879, 3, 12, "Cocoa Bean from a bad Alpha version");
        CompoundTag compoundTag = NBTIO.putItemHelper(badItem);
        Item recoveredItem = NBTIO.getItemHelper(compoundTag);
        Item correctItem = MinecraftItemID.COCOA_BEANS.get(12);
        assertEquals(correctItem, recoveredItem);
    }

    @Test
    void TestDeserializationSNBT() {
        var str =
                """
                        {"Air":300s,"Armor":[{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b},{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b},{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b},{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b}],"AttackTime":0s,"Attributes":[{"Base":0f,"Current":0f,"DefaultMax":1024f,"DefaultMin":-1024f,"Max":1024f,"Min":-1024f,"Name":"minecraft:luck"},{"Base":10f,"Current":-8f,"DefaultMax":10f,"DefaultMin":0f,"Max":10f,"Min":0f,"Name":"minecraft:health"},{"Base":0f,"Current":0f,"DefaultMax":16f,"DefaultMin":0f,"Max":16f,"Min":0f,"Name":"minecraft:absorption"},{"Base":0f,"Current":0f,"DefaultMax":1f,"DefaultMin":0f,"Max":1f,"Min":0f,"Name":"minecraft:knockback_resistance"},{"Base":0.25f,"Current":0.25f,"DefaultMax":3.40282e+38f,"DefaultMin":0f,"Max":3.40282e+38f,"Min":0f,"Name":"minecraft:movement"},{"Base":0.02f,"Current":0.02f,"DefaultMax":3.40282e+38f,"DefaultMin":0f,"Max":3.40282e+38f,"Min":0f,"Name":"minecraft:underwater_movement"},{"Base":0.02f,"Current":0.02f,"DefaultMax":3.40282e+38f,"DefaultMin":0f,"Max":3.40282e+38f,"Min":0f,"Name":"minecraft:lava_movement"},{"Base":16f,"Current":16f,"DefaultMax":2048f,"DefaultMin":0f,"Max":2048f,"Min":0f,"Name":"minecraft:follow_range"}],"BodyRot":-2.70421f,"BreedCooldown":0,"Chested":0b,"Color":0b,"Color2":0b,"Dead":0b,"DeathTime":0s,"FallDistance":0f,"HurtTime":0s,"InLove":0,"Invulnerable":0b,"IsAngry":0b,"IsAutonomous":0b,"IsBaby":0b,"IsEating":0b,"IsGliding":0b,"IsGlobal":0b,"IsIllagerCaptain":0b,"IsOrphaned":0b,"IsOutOfControl":0b,"IsPregnant":0b,"IsRoaring":0b,"IsScared":0b,"IsStunned":0b,"IsSwimming":0b,"IsTamed":0b,"IsTrusting":0b,"LastDimensionId":0,"LeasherID":-1L,"LootDropped":0b,"LoveCause":0L,"Mainhand":[{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b}],"MarkVariant":0,"NaturalSpawn":1b,"Offhand":[{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b}],"OnGround":1b,"OwnerNew":-1L,"PortalCooldown":0,"Pos":[0.442949f, 65f, -16.7079f],"Rotation":[-2.70421f, 0f],"Saddled":0b,"Sheared":0b,"ShowBottom":0b,"Sitting":0b,"SkinID":0,"Strength":0,"StrengthMax":0,"Surface":1b,"Tags":[],"TargetID":-1L,"TradeExperience":0,"TradeTier":0,"UniqueID":-4294967212L,"Variant":0,"boundX":0,"boundY":0,"boundZ":0,"canPickupItems":0b,"definitions":["+minecraft:pig","+","+minecraft:pig_adult","+minecraft:pig_unsaddled"],"expDropEnabled":1b,"hasBoundOrigin":0b,"hasSetCanPickupItems":1b,"identifier":"minecraft:pig","internalComponents":{"EntityStorageKeyComponent":{"StorageKey":"T"}}}
                        """;
        var parser = new SNBTParser(str);
        var NBT = parser.deserializationSNBT();
        var SNBT = NBT.toSnbt(4);
        System.out.println(SNBT);
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
                .putList(new ListTag<>()
                        .add(new ListTag<>()
                                .add(new IntTag("", 2))
                                .add(new IntTag("", 3)))
                        .add(new ListTag<>()
                                .add(new DoubleTag("", 3.33))
                                .add(new DoubleTag("", 4.44)))
                        .add(new ListTag<>()
                                .add(new ByteArrayTag("", new byte[]{9, 8, 7, 6, 5}))
                                .add(new ByteArrayTag("", new byte[]{11, 22, 33, 44, 55}))))
                .putList(new ListTag<>()
                        .add(new CompoundTag()
                                .putString("string1", "asdafsdf")
                                .putString("string2", "afasdfcc"))
                        .add(new CompoundTag()
                                .putString("string1", "asdafsdf")
                                .putString("string2", "afasdfcc")))
                .putCompound("nestCompoundTag", new CompoundTag()
                        .putString("string3", "123123123")
                        .putString("string4", "zxdzsfdsdf"));
        var oldSNBT = oldNBT.toSnbt(4);
        var newNBT = new SNBTParser(oldSNBT).deserializationSNBT();
        var newSNBT = newNBT.toSnbt(4);
        assertEquals(oldNBT, newNBT);
        System.out.println("通过测试1");
        assertEquals(oldSNBT, newSNBT);
        System.out.println("通过测试2");
    }
}
