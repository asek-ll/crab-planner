package com.appspont.sopplet.crab.plugin;

import com.appspont.sopplet.crab.PlanStoreManager;
import com.appspont.sopplet.crab.PlannerContainerTransferHandler;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;

@JEIPlugin
public class CrabJeiPlugin implements IModPlugin {

    private static IJeiRuntime jeiRuntime;
    private static IModRegistry modRegistry;
    private static PlanStoreManager planStoreManager = new PlanStoreManager();

    @Override
    public void register(IModRegistry registry) {
        final IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
        recipeTransferRegistry.addUniversalRecipeTransferHandler(new PlannerContainerTransferHandler());
        modRegistry = registry;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        CrabJeiPlugin.jeiRuntime = jeiRuntime;
    }

    public static IJeiRuntime getJeiRuntime() {
        return jeiRuntime;
    }

    public static IModRegistry getModRegistry() {
        return modRegistry;
    }

    public static PlanStoreManager getPlanStoreManager() {
        return planStoreManager;
    }
}
