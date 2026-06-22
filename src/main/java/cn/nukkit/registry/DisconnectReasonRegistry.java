package cn.nukkit.registry;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.cloudburstmc.protocol.bedrock.data.DisconnectFailReason;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Kaooot
 */
public class DisconnectReasonRegistry implements IRegistry<DisconnectFailReason, String, String> {

    static final AtomicBoolean isLoad = new AtomicBoolean(false);
    static final Object2ObjectMap<DisconnectFailReason, String> MAP = new Object2ObjectOpenHashMap<>();
    static final Converter<String, String> CONVERTER = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.UPPER_UNDERSCORE);

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) {
            return;
        }

        try (var stream = DisconnectReasonRegistry.class.getClassLoader().getResourceAsStream("gamedata/kaooot/disconnection_error_messaging.json")) {
            if (stream == null) {
                throw new IllegalStateException("Failed to find disconnection error messaging file");
            }
            final JsonObject jsonObject = new Gson().fromJson(new String(stream.readAllBytes()), JsonObject.class);
            final JsonArray themes = jsonObject.getAsJsonArray("Themes");
            for (JsonElement element : themes) {
                final JsonObject theme = element.getAsJsonObject();
                if (!theme.has("Errors")) {
                    continue;
                }
                final JsonArray errors = theme.getAsJsonArray("Errors");
                for (JsonElement e : errors) {
                    final JsonObject error = e.getAsJsonObject();
                    if (!error.has("FailReason")) {
                        continue;
                    }
                    final String s = error.get("FailReason").getAsString();
                    try {
                        final DisconnectFailReason failReason = DisconnectFailReason.valueOf(CONVERTER.convert(s));
                        String value = "";
                        if (error.has("OverrideStrings")) {
                            final JsonObject overrideStrings = error.getAsJsonObject("OverrideStrings");
                            if (overrideStrings.has("Body")) {
                                final String body = overrideStrings.get("Body").getAsString();
                                if (!body.isEmpty()) {
                                    value = body;
                                }
                            }
                            if (value.isEmpty() && overrideStrings.has("Title")) {
                                value = overrideStrings.get("Title").getAsString();
                            }
                        }
                        this.register(failReason, value);
                    } catch (IllegalArgumentException ignored) {
                    } catch (RegisterException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize DisconnectReasonRegistry");
        }
    }

    @Override
    public String get(DisconnectFailReason key) {
        return MAP.get(key);
    }

    @Override
    public void trim() {

    }

    @Override
    public void reload() {

    }

    @Override
    public void register(DisconnectFailReason key, String value) throws RegisterException {
        MAP.put(key, value);
    }
}