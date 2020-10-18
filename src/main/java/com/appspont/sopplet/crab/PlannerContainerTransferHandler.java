package com.appspont.sopplet.crab;

import com.appspont.sopplet.crab.gui.RecipeGui;
import com.appspont.sopplet.crab.gui.planner.IngredientRenderer;
import com.appspont.sopplet.crab.planner.ingredient.PlannerFluidStack;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredient;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import com.appspont.sopplet.crab.planner.ingredient.PlannerItemStack;
import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.gui.elements.DrawableIngredient;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlannerContainerTransferHandler implements IRecipeTransferHandler<CraftingPlanContainer> {

    private RecipeGui recipeGui;
    private final Minecraft mc;

    public PlannerContainerTransferHandler() {
        mc = Minecraft.getMinecraft();
    }

    @Override
    @Nonnull
    public Class<CraftingPlanContainer> getContainerClass() {
        return CraftingPlanContainer.class;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(@Nonnull CraftingPlanContainer container,
                                               @Nonnull IRecipeLayout iRecipeLayout,
                                               @Nonnull EntityPlayer entityPlayer, boolean maxTransfer, boolean doTransfer) {

        if (doTransfer) {


            final List<PlannerIngredientStack> outputs = Lists.newArrayList();
            final List<PlannerIngredientStack> ingredients = Lists.newArrayList();

            final IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
            final Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients = itemStacks.getGuiIngredients();
            for (IGuiIngredient<ItemStack> itemStackIGuiIngredient : guiIngredients.values()) {
                final List<ItemStack> allIngredients = itemStackIGuiIngredient.getAllIngredients();
                if (!allIngredients.isEmpty()) {
                    final ItemStack displayedIngredient = allIngredients.get(0);
                    if (itemStackIGuiIngredient.isInput()) {
                        ingredients.add(new PlannerItemStack(displayedIngredient));
                    } else {
                        outputs.add(new PlannerItemStack(displayedIngredient));
                    }
                }
            }

            final Collection<? extends IGuiIngredient<FluidStack>> guiFluidStack =
                    iRecipeLayout.getFluidStacks().getGuiIngredients().values();

            for (IGuiIngredient<FluidStack> fluidStackIGuiIngredient : guiFluidStack) {
                final List<FluidStack> allIngredients = fluidStackIGuiIngredient.getAllIngredients();
                if (!allIngredients.isEmpty()) {
                    final PlannerFluidStack fluidStack = new PlannerFluidStack(allIngredients.get(0));
                    if (fluidStackIGuiIngredient.isInput()) {
                        ingredients.add(fluidStack);
                    } else {
                        outputs.add(fluidStack);
                    }
                }
            }
            IRecipeRegistry recipeRegistry = CrabJeiPlugin.getJeiRuntime().getRecipeRegistry();
            List<Object> recipeCatalysts = recipeRegistry.getRecipeCatalysts(iRecipeLayout.getRecipeCategory());
            List<PlannerIngredientStack> catalysts = new ArrayList<>();

            for (Object recipeCatalyst : recipeCatalysts) {
                if (recipeCatalyst instanceof ItemStack) {
                    catalysts.add(new PlannerItemStack((ItemStack) recipeCatalyst));
                    break;
                }
            }


            RecipeGui recipeGui = getRecipeGui();

            if (mc.currentScreen instanceof RecipesGui) {
                recipeGui.setParentScreen(((RecipesGui) mc.currentScreen).getParentScreen());
            }

            recipeGui.setRecipe(iRecipeLayout);
            recipeGui.setPlan(container.getPlan());
            mc.displayGuiScreen(recipeGui);
//            PlannerRecipe plannerRecipe = new PlannerRecipe(compactItems(outputs), compactItems(ingredients), catalysts);
//            container.getPlan().addRecipe(plannerRecipe);
        }

        return null;
    }

    private static List<PlannerIngredientStack> compactItems(Collection<PlannerIngredientStack> ingredientStacks) {
        final Multiset<PlannerIngredient> compacted = HashMultiset.create();
        for (PlannerIngredientStack ingredient : ingredientStacks) {
            compacted.add(ingredient.getIngredient(), ingredient.getAmount());
        }

        return compacted.entrySet().stream()
                .map(e -> e.getElement().createStack(e.getCount()))
                .collect(Collectors.toList());
    }

    private RecipeGui getRecipeGui() {
        if (recipeGui == null)  {
            IIngredientRegistry ingredientRegistry = CrabJeiPlugin.getModRegistry().getIngredientRegistry();
            recipeGui = new RecipeGui(ingredientRegistry, mc);
        }
        return recipeGui;
    }
}
