package myrrhion.identityorigins.recipies;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class EnchantCloneRecipie extends SpecialCraftingRecipe {

    public EnchantCloneRecipie(Identifier id) {
        super(id);
    }

    public boolean matches(CraftingInventory craftingInventory, World world) {
        int i = 0;
        ItemStack itemStack = ItemStack.EMPTY;

        for(int j = 0; j < craftingInventory.size(); ++j) {
            ItemStack itemStack2 = craftingInventory.getStack(j);
            if (!itemStack2.isEmpty()) {
                if (itemStack2.getItem() == Items.ENCHANTED_BOOK) {
                    if (!itemStack.isEmpty()) {
                        return false;
                    }

                    itemStack = itemStack2;
                } else {
                    if (itemStack2.getItem() != Items.WRITABLE_BOOK) {
                        return false;
                    }

                    ++i;
                }
            }
        }

        return !itemStack.isEmpty() && itemStack.hasTag() && i > 0;
    }

    public ItemStack craft(CraftingInventory craftingInventory) {
        int i = 0;
        ItemStack itemStack = ItemStack.EMPTY;

        for(int j = 0; j < craftingInventory.size(); ++j) {
            ItemStack itemStack2 = craftingInventory.getStack(j);
            if (!itemStack2.isEmpty()) {
                if (itemStack2.getItem() == Items.ENCHANTED_BOOK) {
                    if (!itemStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    itemStack = itemStack2;
                } else {
                    if (itemStack2.getItem() != Items.WRITABLE_BOOK) {
                        return ItemStack.EMPTY;
                    }

                    ++i;
                }
            }
        }

        if (!itemStack.isEmpty() && itemStack.hasTag() && i >= 1 && WrittenBookItem.getGeneration(itemStack) < 2) {
            ItemStack itemStack3 = new ItemStack(Items.WRITTEN_BOOK, i);
            return itemStack3;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public DefaultedList<ItemStack> getRemainingStacks(CraftingInventory craftingInventory) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(craftingInventory.size(), ItemStack.EMPTY);

        for(int i = 0; i < defaultedList.size(); ++i) {
            ItemStack itemStack = craftingInventory.getStack(i);
            if (itemStack.getItem().hasRecipeRemainder()) {
                defaultedList.set(i, new ItemStack(itemStack.getItem().getRecipeRemainder()));
            } else if (itemStack.getItem() instanceof WrittenBookItem) {
                ItemStack itemStack2 = itemStack.copy();
                itemStack2.setCount(1);
                defaultedList.set(i, itemStack2);
                break;
            }
        }

        return defaultedList;
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.BOOK_CLONING;
    }

    @Environment(EnvType.CLIENT)
    public boolean fits(int width, int height) {
        return width >= 3 && height >= 3;
    }
}
