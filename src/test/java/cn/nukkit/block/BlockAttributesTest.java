package cn.nukkit.block;

import cn.nukkit.blockstate.BlockState;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Author: daoge_cmd <br>
 * Date: 2023/5/27 <br>
 * PowerNukkitX Project <br>
 */
@ExtendWith(PowerNukkitExtension.class)
@Log4j2
public class BlockAttributesTest {

    static final Map<Long, JsonObject> BLOCK_ATTRIBUTES_REFERENCE = new HashMap<>();
    static final Map<Long, Block> BLOCKS = new HashMap<>();
    static final String BLOCK_ATTRIBUTES_REFERENCE_FILE_PATH = "cn/powernukkitx/block/block_attributes.json";
    static List<String> error = new ArrayList<>();

    @SneakyThrows
    @BeforeAll
    static void loadBlockAttributesReference() {
        var reader = new InputStreamReader(new BufferedInputStream(Objects.requireNonNull(BlockAttributesTest.class.getClassLoader().getResourceAsStream(BLOCK_ATTRIBUTES_REFERENCE_FILE_PATH))));
        var parser = JsonParser.parseReader(reader);
        for (var jsonElement : parser.getAsJsonArray()) {
            var obj = jsonElement.getAsJsonObject();
            var blockStateHash = obj.get("blockStateHash").getAsLong();
            BLOCK_ATTRIBUTES_REFERENCE.put(blockStateHash, obj);

            var name = obj.get("name").getAsString();
            var strIdBuilder = new StringBuilder(name);
            for (var entry : obj.get("states").getAsJsonObject().entrySet()) {
                strIdBuilder
                        .append(";")
                        .append(entry.getKey())
                        .append("=")
                        .append(entry.getValue().getAsJsonPrimitive().toString().replaceAll("\"", ""));
            }
            try {
                //TODO: 羊毛，木头等被拆分了的方块不能正常工作
                var block = BlockState.of(strIdBuilder.toString()).getBlock();
                if (block instanceof BlockUnknown) {
                    log.warn("Missing block: " + strIdBuilder);
                    continue;
                }
                BLOCKS.put(blockStateHash, block);
            } catch (Throwable e) {
                log.error("Failed to load block " + strIdBuilder);
                error.add(strIdBuilder.toString());
            }
        }
    }

    @Test
    void testBlockHash() {
        for (var block : BLOCKS.values()) {
            var ref = BLOCK_ATTRIBUTES_REFERENCE.get(block.computeUnsignedBlockStateHash());
            assertNotNull(ref);
        }
    }

//    @Test
//    void testBlockColor() {
//        boolean failed = false;
//        for (var block : BLOCKS.values()) {
//            var ref = BLOCK_ATTRIBUTES_REFERENCE.get(block.computeUnsignedBlockStateHash()).get("color").getAsJsonObject();
//            var currentColor = block.getColor().toAwtColor();
//            var refColor = new Color(ref.get("r").getAsInt(), ref.get("g").getAsInt(), ref.get("b").getAsInt(), ref.get("a").getAsInt());
//            if (!currentColor.equals(refColor)) {
//                log.warn("Incorrect block color! Block: " + block.getPersistenceName() + ", ref: " + refColor + ", current: " + currentColor);
//                failed = true;
//            }
//        }
//        assertFalse(failed);
//    }
}
