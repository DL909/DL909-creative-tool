package dl909.dl_ct.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

public record RecipeEntry<T extends Recipe<?>>(Identifier id, T value) {

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RecipeEntry)) return false;
        RecipeEntry recipeEntry = (RecipeEntry)o;
        if (!this.id.equals(recipeEntry.id)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return this.id.toString();
    }
}

