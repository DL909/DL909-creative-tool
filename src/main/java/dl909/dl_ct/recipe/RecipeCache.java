package dl909.dl_ct.recipe;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import dl909.dl_ct.block.entity.CrafterBlockEntity;
import dl909.dl_ct.screen.VoidScreenHandler;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class RecipeCache {
    private final CachedRecipe[] cache;
    private WeakReference<RecipeManager> recipeManagerRef = new WeakReference<>(null);

    public RecipeCache(int size) {
        this.cache = new CachedRecipe[size];
    }

    public Optional<CraftingRecipe> getRecipe(World world, CrafterBlockEntity recipeInputInventory) {
        if (recipeInputInventory.isEmpty()) {
            return Optional.empty();
        }
        this.validateRecipeManager(world);
        for (int i = 0; i < this.cache.length; ++i) {
            CachedRecipe cachedRecipe = this.cache[i];
            if (cachedRecipe == null || !cachedRecipe.matches(recipeInputInventory.getHeldStacks())) continue;
            this.sendToFront(i);
            return Optional.ofNullable(cachedRecipe.value());
        }
        CraftingInventory craftingInventory = new CraftingInventory(new VoidScreenHandler(ScreenHandlerType.GENERIC_3X3,0),3,3);
        craftingInventory.setStack(0,recipeInputInventory.getStack(0));
        craftingInventory.setStack(1,recipeInputInventory.getStack(1));
        craftingInventory.setStack(2,recipeInputInventory.getStack(2));
        craftingInventory.setStack(3,recipeInputInventory.getStack(3));
        craftingInventory.setStack(4,recipeInputInventory.getStack(4));
        craftingInventory.setStack(5,recipeInputInventory.getStack(5));
        craftingInventory.setStack(6,recipeInputInventory.getStack(6));
        craftingInventory.setStack(7,recipeInputInventory.getStack(7));
        craftingInventory.setStack(8,recipeInputInventory.getStack(8));
        return this.getAndCacheRecipe(craftingInventory, world);
    }

    private void validateRecipeManager(World world) {
        RecipeManager recipeManager = world.getRecipeManager();
        if (recipeManager != this.recipeManagerRef.get()) {
            this.recipeManagerRef = new WeakReference<RecipeManager>(recipeManager);
            Arrays.fill(this.cache, null);
        }
    }

    private Optional<CraftingRecipe> getAndCacheRecipe(CraftingInventory craftingInventory, World world) {
        Optional<CraftingRecipe> optional = world.getRecipeManager().getFirstMatch( RecipeType.CRAFTING,craftingInventory, world);
        DefaultedList<ItemStack> list = DefaultedList.ofSize(9, ItemStack.EMPTY);
        list.set(0,craftingInventory.getStack(0));
        list.set(1,craftingInventory.getStack(1));
        list.set(2,craftingInventory.getStack(2));
        list.set(3,craftingInventory.getStack(3));
        list.set(4,craftingInventory.getStack(4));
        list.set(5,craftingInventory.getStack(5));
        list.set(6,craftingInventory.getStack(6));
        list.set(7,craftingInventory.getStack(7));
        list.set(8,craftingInventory.getStack(8));
        this.cache(list, optional.orElse(null));
        return optional;
    }

    private void sendToFront(int index) {
        if (index > 0) {
            CachedRecipe cachedRecipe = this.cache[index];
            System.arraycopy(this.cache, 0, this.cache, 1, index);
            this.cache[0] = cachedRecipe;
        }
    }

    private void cache(List<ItemStack> inputStacks, CraftingRecipe recipe) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inputStacks.size(), ItemStack.EMPTY);
        for (int i = 0; i < inputStacks.size(); ++i) {
            defaultedList.set(i, inputStacks.get(i).copyWithCount(1));
        }
        System.arraycopy(this.cache, 0, this.cache, 1, this.cache.length - 1);
        this.cache[0] = new CachedRecipe(defaultedList, recipe);
    }

    record CachedRecipe(DefaultedList<ItemStack> key, CraftingRecipe value) {
        public boolean matches(List<ItemStack> inputs) {
            if (this.key.size() != inputs.size()) {
                return false;
            }
            for (int i = 0; i < this.key.size(); ++i) {
                if (CrafterBlockEntity.areItemsAndComponentsEqual(this.key.get(i), inputs.get(i))) continue;
                return false;
            }
            return true;
        }

        public CraftingRecipe value() {
            return this.value;
        }
    }
}

