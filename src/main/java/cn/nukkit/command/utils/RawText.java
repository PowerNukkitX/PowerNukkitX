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
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Getter
public class RawText {

    private static final Gson gson = new Gson();
    private Component base = null;

    private RawText(Component base){
        this.base = base;
    }

    public static RawText fromRawText(String rawText){
        Component base = gson.fromJson(rawText,Component.class);
        return new RawText(base);
    }

    public void preParse(CommandSender sender){
        preParse(sender, base);
    }

    private static void preParse(CommandSender sender, Component cps){
        if (cps.getType() != Component.ComponentType.RAWTEXT)
            return;
        List<Component> components = cps.component_rawtext;
        for(Component component : components.toArray(new Component[0])){
            if(component.getType() == Component.ComponentType.SCORE){
                Component newComponent = preParseScore(component,sender);
                if(newComponent != null)
                    components.set(components.indexOf(component),newComponent);
                else
                    components.remove(component);
            }
            if (component.getType() == Component.ComponentType.SELECTOR) {
                Component newComponent = preParseSelector(component,sender);
                if(newComponent != null)
                    components.set(components.indexOf(component),newComponent);
                else
                    components.remove(component);
            }
            if (component.getType() == Component.ComponentType.RAWTEXT) {
                preParse(sender, component);
            }
            if (component.getType() == Component.ComponentType.TRANSLATE_WITH) {
                if (component.component_translate_with instanceof Map<?,?>) {
                    Component cp = gson.fromJson(gson.toJson(component.component_translate_with),Component.class);
                    preParse(sender, cp);
                    component.component_translate_with = cp;
                }
            }
        }
    }

    @SneakyThrows
    private static Component preParseScore(Component component, CommandSender sender){
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

        if(scorer == null)
            return null;
        if(value == null)
            value = scoreboard.getLine(scorer).getScore();
        Component newComponent = new Component();
        newComponent.setComponent_text(String.valueOf(value));
        return newComponent;
    }

    private static Component preParseSelector(Component component,CommandSender sender){
        List<Entity> entities;
        try {
            entities = EntitySelectorAPI.getAPI().matchEntities(sender, component.component_selector);
        }catch (Exception e){
            return null;
        }
        if (entities.isEmpty())
            return null;
        String entities_str = entities.stream().map(Entity::getName).collect(Collectors.joining(", "));
        Component newComponent = new Component();
        newComponent.setComponent_text(entities_str);
        return newComponent;
    }

    public String toRawText(){
        return gson.toJson(base);
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

        private static class ScoreComponent{
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

        public ComponentType getType(){
            if(component_text != null){
                return ComponentType.TEXT;
            }
            if(component_selector != null){
                return ComponentType.SELECTOR;
            }
            if(component_translate != null){
                if(component_translate_with != null){
                    return ComponentType.TRANSLATE_WITH;
                }
                return ComponentType.TRANSLATE;
            }
            if(component_score != null){
                if (component_score.name != null && component_score.objective != null) {
                    return ComponentType.SCORE;
                }
            }
            if(component_rawtext != null){
                return ComponentType.RAWTEXT;
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return gson.toJson(this.base);
    }
}