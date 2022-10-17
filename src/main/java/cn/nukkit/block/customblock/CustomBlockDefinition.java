package cn.nukkit.block.customblock;

import cn.nukkit.block.customblock.data.BlockCreativeCategory;
import cn.nukkit.block.customblock.data.Materials;
import cn.nukkit.block.customblock.data.Permutations;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.util.Locale;
import java.util.function.Consumer;

@Log4j2
public record CustomBlockDefinition(String identifier, CompoundTag nbt) {

    /**
     * 将几何文件中的face(面)名称映射到实际的材质实例
     * 控制自定义方块的命名空间<br>(例如 wiki:test_block)
     * 控制自定义方块在创造栏中的分类,默认值construction
     * 控制自定义方块所用的材质名称<br>(例如材质图片test.png设置test)
     *
     * @see <a href="https://wiki.bedrock.dev/documentation/creative-categories.html">wiki.bedrock.dev</a>
     */
    public static CustomBlockDefinition.Builder builder(CustomBlock customBlock, String texture) {
        return builder(customBlock, Materials.builder().any(Materials.RenderMethod.OPAQUE, texture), BlockCreativeCategory.CONSTRUCTION);
    }

    public static CustomBlockDefinition.Builder builder(CustomBlock customBlock, String texture, BlockCreativeCategory blockCreativeCategory) {
        return builder(customBlock, Materials.builder().any(Materials.RenderMethod.OPAQUE, texture), blockCreativeCategory);
    }

    public static CustomBlockDefinition.Builder builder(CustomBlock customBlock, Materials materials) {
        return builder(customBlock, materials, BlockCreativeCategory.CONSTRUCTION);
    }

    public static CustomBlockDefinition.Builder builder(@NonNull CustomBlock customBlock, @NonNull Materials materials, BlockCreativeCategory blockCreativeCategory) {
        return new CustomBlockDefinition.Builder(customBlock, materials, blockCreativeCategory);
    }

    public static class Builder {
        protected final String identifier;

        protected CompoundTag nbt = new CompoundTag()
                .putCompound("components", new CompoundTag());

        protected Builder(CustomBlock customBlock, Materials materials, BlockCreativeCategory blockCreativeCategory) {
            this.identifier = customBlock.getNamespaceId();

            var components = this.nbt.getCompound("components");

            //设置一些与PNX内部对应的方块属性
            components.putCompound("minecraft:friction", new CompoundTag()
                            .putFloat("value", (float) customBlock.getFrictionFactor()))
                    .putCompound("minecraft:explosion_resistance", new CompoundTag()
                            .putInt("value", (int) customBlock.getResistance()))
                    .putCompound("minecraft:block_light_filter", new CompoundTag()
                            .putByte("lightLevel", (byte) customBlock.getLightFilter()))
                    .putCompound("minecraft:light_emission", new CompoundTag()
                            .putByte("emission", (byte) customBlock.getLightLevel()))
                    .putCompound("minecraft:destructible_by_mining", new CompoundTag()
                            .putFloat("value", (float) (customBlock.calculateBreakTime() * 2 / 3)));
            //设置材质
            components.putCompound("minecraft:material_instances", new CompoundTag()
                    .putCompound("mappings", new CompoundTag())
                    .putCompound("materials", materials.build()));
            //默认单位立方体方块
            components.putCompound("minecraft:unit_cube", new CompoundTag());
            //设置方块在创造栏的分类
            this.nbt.putCompound("menu_category", new CompoundTag()
                    .putString("category", blockCreativeCategory.name().toLowerCase(Locale.ENGLISH)));
            //molang版本
            this.nbt.putInt("molangVersion", 6);

            //设置方块的properties
            var propertiesNBT = customBlock.getPropertiesNBT();
            if (propertiesNBT != null) {
                propertiesNBT.setName("properties");
                nbt.putList(propertiesNBT);
            }
        }

        /**
         * 控制自定义方块在创造栏中的组
         *
         * @see <a href="https://wiki.bedrock.dev/documentation/creative-categories.html">wiki.bedrock.dev</a>
         */
        public Builder creativeGroup(String creativeGroup) {
            if (creativeGroup.isBlank()) {
                System.out.println("creativeGroup has an invalid value!");
                return this;
            }
            this.nbt.getCompound("components").getCompound("menu_category").putString("group", creativeGroup.toLowerCase(Locale.ENGLISH));
            return this;
        }

        /**
         * 以度为单位设置块围绕立方体中心的旋转,旋转顺序为 xyz.角度必须是90的倍数。
         */
        public Builder rotation(@NonNull Vector3f rotation) {
            this.nbt.putCompound("minecraft:rotation", new CompoundTag()
                    .putFloat("x", rotation.x)
                    .putFloat("y", rotation.y)
                    .putFloat("z", rotation.z));
            return this;
        }

        /**
         * 控制自定义方块的形状<br>
         * Geometry identifier from geo file in 'RP/models/blocks' folder
         */
        public Builder geometry(String geometry) {
            if (geometry.isBlank()) {
                System.out.println("geometry has an invalid value!");
                return this;
            }
            var components = this.nbt.getCompound("components");
            //默认单位立方体方块，如果定义几何模型需要移除
            if (components.contains("minecraft:unit_cube")) components.remove("minecraft:unit_cube");
            //设置方块对应的几何模型
            components.putCompound("minecraft:geometry", new CompoundTag()
                    .putString("value", geometry.toLowerCase(Locale.ENGLISH)));
            return this;
        }

        /**
         * 控制自定义方块的客户端状态
         *
         * @return Permutations NBT Tag
         */
        public Builder permutations(@NonNull Permutations permutations) {
            var per = permutations.data();
            per.setName("permutations");
            this.nbt.putList(per);
            return this;
        }

       /* public Builder blockTags(String creativeGroup) {
            if (creativeGroup.isBlank()) {
                System.out.println("creativeGroup has an invalid value!");
                return this;
            }
            this.nbt.getCompound("components").getCompound("menu_category").putString("group", creativeGroup.toLowerCase(Locale.ENGLISH));
            return this;
        }*/

        /**
         * 对自动生成的ComponentNBT进行处理
         *
         * @param componentNBT 自动生成的component NBT
         * @return 处理后的ComponentNBT
         */
        public CustomBlockDefinition customBuild(@NonNull Consumer<CompoundTag> nbt) {
            var def = this.build();
            nbt.accept(def.nbt);
            return def;
        }

        public CustomBlockDefinition build() {
            return new CustomBlockDefinition(this.identifier, this.nbt);
        }
    }
}
