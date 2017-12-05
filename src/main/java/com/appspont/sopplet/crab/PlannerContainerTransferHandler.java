package com.appspont.sopplet.crab;

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
import java.util.Map;

public class PlannerContainerTransferHandler implements IRecipeTransferHandler {
    @Override
    @Nonnull
    public Class getContainerClass() {
        return PlannerContainer.class;
    }

    @Override
    @Nonnull
    public String getRecipeCategoryUid() {
        return Constants.UNIVERSAL_RECIPE_TRANSFER_UID;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(@Nonnull Container container,
                                               @Nonnull IRecipeLayout iRecipeLayout,
                                               @Nonnull EntityPlayer entityPlayer, boolean maxTransfer, boolean doTransfer) {

        if (doTransfer) {

            final IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();

            final Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients = itemStacks.getGuiIngredients();

            for (IGuiIngredient<ItemStack> itemStackIGuiIngredient : guiIngredients.values()) {
                if (itemStackIGuiIngredient.isInput()) {
                    final ItemStack displayedIngredient = itemStackIGuiIngredient.getDisplayedIngredient();
                }
            }

        }

        return null;
    }
}
