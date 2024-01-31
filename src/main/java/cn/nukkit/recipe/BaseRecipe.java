package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecipe implements Recipe {
    protected final String id;
    protected final List<Item> results = new ArrayList<>();
    protected final List<ItemDescriptor> ingredients = new ArrayList<>();

    protected BaseRecipe(String id) {
        Preconditions.checkNotNull(id);
        this.id = id;
    }

    @Override
    public @NotNull String getRecipeId() {
        return id;
    }

    @Override
    public List<Item> getResults() {
        return results;
    }

    @Override
    public List<ItemDescriptor> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseRecipe that)) return false;
        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "id='" + id + '\'' +
                '}';
    }
}
