package cn.nukkit.config;


import eu.okaeri.configs.configurer.Configurer;
import eu.okaeri.configs.postprocessor.ConfigLineInfo;
import eu.okaeri.configs.postprocessor.format.YamlSectionWalker;
import eu.okaeri.configs.schema.ConfigDeclaration;
import eu.okaeri.configs.schema.FieldDeclaration;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.SerdesContext;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Accessors(chain = true)
public class YamlSnakeYamlConfigurer extends Configurer {

    private Yaml yaml;
    private Map<String, Object> map = new LinkedHashMap<>();

    @Setter
    private String commentPrefix = "# ";

    public YamlSnakeYamlConfigurer(@NotNull Yaml yaml, @NotNull Map<String, Object> map) {
        this.yaml = yaml;
        this.map = map;
    }

    public YamlSnakeYamlConfigurer(@NotNull Yaml yaml) {
        this.yaml = yaml;
    }

    public YamlSnakeYamlConfigurer() {
        this(createYaml());
    }

    private static Yaml createYaml() {

        LoaderOptions loaderOptions = new LoaderOptions();
        Constructor constructor = new Constructor(loaderOptions);

        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Representer representer = new Representer(dumperOptions);
        representer.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Resolver resolver = new Resolver();

        return new Yaml(constructor, representer, dumperOptions, loaderOptions, resolver);
    }

    private static <T> T apply(T object, Consumer<T> consumer) {
        consumer.accept(object);
        return object;
    }

    @Override
    public List<String> getExtensions() {
        return Arrays.asList("yml", "yaml");
    }

    @Override
    public void setValue(@NotNull String key, Object value, GenericsDeclaration type, FieldDeclaration field) {
        Object simplified = this.simplify(value, type, SerdesContext.of(this, field), true);
        this.map.put(key, simplified);
    }

    @Override
    public void setValueUnsafe(@NotNull String key, Object value) {
        this.map.put(key, value);
    }

    @Override
    public Object getValue(@NotNull String key) {
        return this.map.get(key);
    }

    @Override
    public Object remove(@NotNull String key) {
        return this.map.remove(key);
    }

    @Override
    public boolean keyExists(@NotNull String key) {
        return this.map.containsKey(key);
    }

    @Override
    public List<String> getAllKeys() {
        return Collections.unmodifiableList(new ArrayList<>(this.map.keySet()));
    }

    @Override
    public void load(@NotNull InputStream inputStream, @NotNull ConfigDeclaration declaration) throws Exception {
        // try loading from input stream
        this.map = this.yaml.load(inputStream);
        // when no map was loaded reset with empty
        if (this.map == null) this.map = new LinkedHashMap<>();
    }

    @Override
    public void write(@NotNull OutputStream outputStream, @NotNull ConfigDeclaration declaration) throws Exception {
        // render to string
        String contents = this.yaml.dump(this.map);

        // postprocess
        TrConfigPostprocessor.of(contents)
                // remove all current top-level comments
                .removeLines((line) -> line.startsWith(this.commentPrefix.trim()))
                // add new comments
                .updateLinesKeys(new YamlSectionWalker() {
                    @Override
                    public String update(String line, ConfigLineInfo lineInfo, List<ConfigLineInfo> path) {

                        ConfigDeclaration currentDeclaration = declaration;
                        for (int i = 0; i < (path.size() - 1); i++) {
                            ConfigLineInfo pathElement = path.get(i);
                            Optional<FieldDeclaration> field = currentDeclaration.getField(pathElement.getName());
                            if (field.isEmpty()) {
                                return line;
                            }
                            GenericsDeclaration fieldType = field.get().getType();
                            if (!fieldType.isConfig()) {
                                return line;
                            }
                            currentDeclaration = ConfigDeclaration.of(fieldType.getType());
                        }

                        Optional<FieldDeclaration> lineDeclaration = currentDeclaration.getField(lineInfo.getName());
                        if (lineDeclaration.isEmpty()) {
                            return line;
                        }

                        String[] fieldComment = lineDeclaration.get().getComment();
                        if (fieldComment == null) {
                            return line;
                        }

                        String comment = TrConfigPostprocessor.createComment(YamlSnakeYamlConfigurer.this.commentPrefix, fieldComment);
                        return TrConfigPostprocessor.addIndent(comment, lineInfo.getIndent()) + line;
                    }
                })
                // add header if available
                .prependContextComment(this.commentPrefix, declaration.getHeader())
                // save
                .write(outputStream);
    }
}
