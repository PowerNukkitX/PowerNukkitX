package cn.nukkit.command.utils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.scoreboard.Scoreboard;
import cn.nukkit.scoreboard.interfaces.Scorer;
import cn.nukkit.scoreboard.scorer.EntityScorer;
import cn.nukkit.scoreboard.scorer.FakeScorer;
import cn.nukkit.scoreboard.scorer.PlayerScorer;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RawText {

    private static final Gson gson = new Gson();
    @SerializedName("rawtext")
    private List<Component> components = new ArrayList<>();
    private transient boolean needTranslate = false;
    private transient String[] params = null;

    private RawText(){}

    public static RawText fromRawText(String rawText){
        return gson.fromJson(rawText, RawText.class);
    }

    public void preParse(CommandSender sender){
        for(Component component : components.toArray(new Component[0])){
            if(component.getType() == Component.ComponentType.SCORE){
                Scoreboard scoreboard = Server.getInstance().getScoreboardManager().getScoreboard(component.component_score.objective);
                if (scoreboard == null)
                    continue;
                String name_str = component.component_score.name;
                Scorer scorer = null;
                Integer value = component.component_score.value;

                if (name_str.equals("*")) {
                    if (!sender.isEntity())
                        continue;
                    scorer = sender.isPlayer() ? new PlayerScorer(sender.asPlayer()) : new EntityScorer(sender.asEntity());
                } else if (EntitySelector.hasArguments(name_str)) {
                    List<Scorer> scorers = EntitySelector.matchEntities(sender, name_str).stream().map(t -> t instanceof Player ? new PlayerScorer((Player) t) : new EntityScorer(t)).toList();
                    if (scorers.isEmpty())
                        continue;
                    scorer = scorers.get(0);
                } else if (Server.getInstance().getPlayer(name_str) != null) {
                    scorer = new PlayerScorer(Server.getInstance().getPlayer(name_str));
                } else {
                    scorer = new FakeScorer(name_str);
                }

                if(scorer == null)
                    continue;
                if(value == null)
                    value = scoreboard.getLine(scorer).getScore();
                Component newComponent = new Component();
                newComponent.setComponent_text(String.valueOf(value));
                components.set(components.indexOf(component),newComponent);
            }
            if (component.getType() == Component.ComponentType.SELECTOR) {
                List<Entity> entities = EntitySelector.matchEntities(sender, component.component_selector);
                String entities_str = entities.stream().map(Entity::getName).collect(Collectors.joining(", "));
                Component newComponent = new Component();
                newComponent.setComponent_text(entities_str);
                components.set(components.indexOf(component),newComponent);
            }
            if (component.getType() == Component.ComponentType.TRANSLATE) {
                needTranslate = true;
            }
            if (component.getType() == Component.ComponentType.TRANSLATE_WITH) {
                params = component.component_translate_with;
            }
        }
    }

    public String toRawText(){
        return gson.toJson(this);
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
        private String[] component_translate_with;

        @SerializedName("score")
        private ScoreComponent component_score;

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
            SCORE
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
            return null;
        }
    }
}
