package com.appspont.sopplet.crab;

import com.appspont.sopplet.crab.gui.PlannerGui;
import com.google.common.collect.Lists;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.config.Constants;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class PlannerContainerTransferHandler implements IRecipeTransferHandler<PlannerContainer> {
    @Override
    @Nonnull
    public Class<PlannerContainer> getContainerClass() {
        return PlannerContainer.class;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(@Nonnull PlannerContainer container,
                                               @Nonnull IRecipeLayout iRecipeLayout,
                                               @Nonnull EntityPlayer entityPlayer, boolean maxTransfer, boolean doTransfer) {

        if (doTransfer) {

            final IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();

            final Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients = itemStacks.getGuiIngredients();

            final List<ItemStack> outputs = Lists.newArrayList();
            final List<ItemStack> ingredients = Lists.newArrayList();
            for (IGuiIngredient<ItemStack> itemStackIGuiIngredient : guiIngredients.values()) {
                final ItemStack displayedIngredient = itemStackIGuiIngredient.getDisplayedIngredient();
                if (displayedIngredient != null) {
                    if (itemStackIGuiIngredient.isInput()) {
                        ingredients.add(displayedIngredient);
                    } else {
                        outputs.add(displayedIngredient);
                    }
                }
            }

            container.addRecipe(outputs, ingredients);
        }

        return null;
    }
}
