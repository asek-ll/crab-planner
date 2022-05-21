package com.appspont.sopplet.crab.plugin;

import com.appspont.sopplet.crab.CrabDumperMod;
import com.appspont.sopplet.crab.PlanStoreManager;
import com.appspont.sopplet.crab.PlannerContainerTransferHandler;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.MethodsReturnNonnullByDefault;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.util.ResourceLocation;

@MethodsReturnNonnullByDefault
@JeiPlugin
public class CrabJeiPlugin implements IModPlugin {

    private static IJeiRuntime jeiRuntime;
    private static PlanStoreManager planStoreManager = new PlanStoreManager();
    private static IJeiHelpers jeiHelpers;

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addUniversalRecipeTransferHandler(new PlannerContainerTransferHandler());
    }

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {
        jeiHelpers = registration.getJeiHelpers();
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(CrabDumperMod.MOD_ID, "jei_plugin");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        CrabJeiPlugin.jeiRuntime = jeiRuntime;
    }

    public static IJeiRuntime getJeiRuntime() {
        return jeiRuntime;
    }

    public static IJeiHelpers getJeiHelpers() {
        return jeiHelpers;
    }

    public static PlanStoreManager getPlanStoreManager() {
        return planStoreManager;
    }
}
