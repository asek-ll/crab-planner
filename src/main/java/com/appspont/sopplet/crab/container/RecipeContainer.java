package com.appspont.sopplet.crab.container;

import mezz.jei.api.gui.IRecipeLayout;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

import java.util.Optional;

public class RecipeContainer extends Container {

    private Optional<IRecipeLayout> layout = Optional.empty();

    public RecipeContainer() {
        super(ContainerType.GENERIC_3x3, -1);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return false;
    }

    public void setRecipe(IRecipeLayout iRecipeLayout) {
        layout = Optional.of(iRecipeLayout);
    }

    public Optional<IRecipeLayout> getLayout() {
        return layout;
    }
}
