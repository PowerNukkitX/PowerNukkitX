/*
 * Copyright (c) 2019-2022 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package cn.nukkit.level.generator.terra.mappings.loader;

import cn.nukkit.level.generator.terra.mappings.RegistryLoader;
import cn.nukkit.utils.JSONUtils;
import com.google.common.collect.HashBiMap;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;

public class BiomeRegistryLoader implements RegistryLoader<String, HashBiMap<Integer, String>> {

    @Override
    public HashBiMap<Integer, String> load(String input) {
        try (var stream = new InputStreamReader(
                Objects.requireNonNull(BiomeRegistryLoader.class.getClassLoader().getResourceAsStream("mappings/biomes.json")))
        ) {
            final Map<String, Map<String, Number>> biomeEntries = JSONUtils.from(
                    stream,
                    new TypeToken<Map<String, Map<String, Number>>>() {
                    }
            );
            HashBiMap<Integer, String> biomes = HashBiMap.create(biomeEntries.size());
            biomeEntries.forEach((k, v) -> {
                final Number bedrockId = v.get("bedrock_id");
                final String biomeType = k;
                biomes.put(bedrockId.intValue(), biomeType);
            });
            return biomes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
