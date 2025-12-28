package cn.nukkit.command.utils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.selector.EntitySelectorAPI;
import cn.nukkit.entity.Entity;
import cn.nukkit.scoreboard.scorer.EntityScorer;
import cn.nukkit.scoreboard.scorer.FakeScorer;
import cn.nukkit.scoreboard.scorer.IScorer;
import cn.nukkit.scoreboard.scorer.PlayerScorer;
import cn.nukkit.utils.JSONUtils;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a Minecraft Bedrock Edition rawtext structure and provides parsing, manipulation,
 * and serialization utilities for PowerNukkitX commands and messages.
 * <p>
 * This class allows parsing JSON-formatted rawtext, pre-processing selectors and scoreboard scores,
 * and converting to and from JSON. It supports all Bedrock rawtext component types, including text,
 * selectors, translations, scores, and nested rawtext arrays. The class is used to build rich, dynamic
 * chat messages and command outputs that can include player names, entity selectors, translated strings,
 * and scoreboard values.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Parses JSON rawtext into a component tree structure.</li>
 *   <li>Pre-processes selectors and scoreboard scores to resolve dynamic values for a given sender.</li>
 *   <li>Supports all Bedrock rawtext component types: text, selector, translate, score, rawtext, etc.</li>
 *   <li>Serializes the component tree back to JSON for network transmission or storage.</li>
 *   <li>Provides a nested {@code Component} class for representing each rawtext element.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Use {@link #fromRawText(String)} to parse a JSON rawtext string.</li>
 *   <li>Call {@link #preParse(CommandSender)} to resolve selectors and scores for a specific sender.</li>
 *   <li>Use {@link #toRawText()} or {@link #toString()} to serialize back to JSON.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * RawText raw = RawText.fromRawText('{"rawtext":[{"text":"Hello "},{"selector":"@a"}]}');
 * raw.preParse(sender);
 * String json = raw.toRawText();
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see CommandSender
 * @see EntitySelectorAPI
 * @see JSONUtils
 * @since PowerNukkitX 1.19.50
 */
@Getter
public class RawText {
    private Component base = null;

    private RawText(Component base) {
        this.base = base;
    }

    public static RawText fromRawText(String rawText) {
        Component base = JSONUtils.from(rawText, Component.class);
        return new RawText(base);
    }

    public void preParse(CommandSender sender) {
        preParse(sender, base);
    }

    private static void preParse(CommandSender sender, Component cps) {
        if (cps.getType() != Component.ComponentType.RAWTEXT)
            return;
        List<Component> components = cps.component_rawtext;
        for (Component component : components.toArray(new Component[0])) {
            if (component.getType() == Component.ComponentType.SCORE) {
                Component newComponent = preParseScore(component, sender);
                if (newComponent != null)
                    components.set(components.indexOf(component), newComponent);
                else
                    components.remove(component);
            }
            if (component.getType() == Component.ComponentType.SELECTOR) {
                Component newComponent = preParseSelector(component, sender);
                if (newComponent != null)
                    components.set(components.indexOf(component), newComponent);
                else
                    components.remove(component);
            }
            if (component.getType() == Component.ComponentType.RAWTEXT) {
                preParse(sender, component);
            }
            if (component.getType() == Component.ComponentType.TRANSLATE_WITH) {
                if (component.component_translate_with instanceof Map<?, ?>) {
                    Component cp = JSONUtils.from(JSONUtils.to(component.component_translate_with), Component.class);
                    preParse(sender, cp);
                    component.component_translate_with = cp;
                }
            }
        }
    }

    @SneakyThrows
    private static Component preParseScore(Component component, CommandSender sender) {
        var scoreboard = Server.getInstance().getScoreboardManager().getScoreboard(component.component_score.objective);
        if (scoreboard == null)
            return null;
        String name_str = component.component_score.name;
        IScorer scorer = null;
        Integer value = component.component_score.value;

        if (name_str.equals("*")) {
            if (!sender.isEntity())
                return null;
            scorer = sender.isPlayer() ? new PlayerScorer(sender.asPlayer()) : new EntityScorer(sender.asEntity());
        } else if (EntitySelectorAPI.getAPI().checkValid(name_str)) {
            List<IScorer> scorers = EntitySelectorAPI.getAPI().matchEntities(sender, name_str).stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).toList();
            if (scorers.isEmpty())
                return null;
            scorer = scorers.get(0);
        } else if (Server.getInstance().getPlayer(name_str) != null) {
            scorer = new PlayerScorer(Server.getInstance().getPlayer(name_str));
        } else {
            scorer = new FakeScorer(name_str);
        }

        if (scorer == null)
            return null;
        if (value == null)
            value = scoreboard.getLine(scorer).getScore();
        Component newComponent = new Component();
        newComponent.setComponent_text(String.valueOf(value));
        return newComponent;
    }

    private static Component preParseSelector(Component component, CommandSender sender) {
        List<Entity> entities;
        try {
            entities = EntitySelectorAPI.getAPI().matchEntities(sender, component.component_selector);
        } catch (Exception e) {
            return null;
        }
        if (entities.isEmpty())
            return null;
        String entities_str = entities.stream().map(Entity::getName).collect(Collectors.joining(", "));
        Component newComponent = new Component();
        newComponent.setComponent_text(entities_str);
        return newComponent;
    }

    public String toRawText() {
        return JSONUtils.to(base);
    }

    @Getter
    @Setter
    public static class Component {

        @SerializedName("text")
        private String component_text;

        @SerializedName("selector")
        private String component_selector;

        @SerializedName("translate")
        private String component_translate;

        @SerializedName("with")
        private Object component_translate_with;

        @SerializedName("score")
        private ScoreComponent component_score;

        @SerializedName("rawtext")
        private List<Component> component_rawtext;

        private static class ScoreComponent {
            @SerializedName("name")
            private String name;
            @SerializedName("objective")
            private String objective;
            @SerializedName("value")
            private Integer value;
        }

        public enum ComponentType {
            TEXT,
            SELECTOR,
            TRANSLATE,
            TRANSLATE_WITH,
            SCORE,
            RAWTEXT
        }

        public ComponentType getType() {
            if (component_text != null) {
                return ComponentType.TEXT;
            }
            if (component_selector != null) {
                return ComponentType.SELECTOR;
            }
            if (component_translate != null) {
                if (component_translate_with != null) {
                    return ComponentType.TRANSLATE_WITH;
                }
                return ComponentType.TRANSLATE;
            }
            if (component_score != null) {
                if (component_score.name != null && component_score.objective != null) {
                    return ComponentType.SCORE;
                }
            }
            if (component_rawtext != null) {
                return ComponentType.RAWTEXT;
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return JSONUtils.to(this.base);
    }
}