package com.appspont.sopplet.crab.gui.planner;

import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;

public class IngredientRenderer {

    private final IIngredientRegistry ingredientRegistry;
    private final Minecraft mc;

    public IngredientRenderer(IIngredientRegistry ingredientRegistry, Minecraft mc) {
        this.ingredientRegistry = ingredientRegistry;
        this.mc = mc;
    }

    public void render(int x, int y, PlannerIngredientStack stack, DrawContext context) {
        final Object rawStack = stack.getIngredient().getRawStack();
        final IIngredientRenderer<Object> renderer = ingredientRegistry.getIngredientRenderer(rawStack);
        renderer.render(mc, x, y, rawStack);

        if (context.mouseX > x && context.mouseX < x + 18 && context.mouseY > y && context.mouseY < y + 18) {

            context.hoverStack = stack.getIngredient();

//            final List<String> tooltip = renderer.getTooltip(mc, rawStack, ITooltipFlag.TooltipFlags.NORMAL);
//            final FontRenderer fontRenderer = renderer.getFontRenderer(mc, rawStack);
//
//            GuiUtils.drawHoveringText(tooltip, mouseX, mouseY, 600, 400, -1, fontRenderer);

//        protected void renderToolTip(ItemStack p_renderToolTip_1_, int p_renderToolTip_2_, int p_renderToolTip_3_) {
//            FontRenderer font = p_renderToolTip_1_.getItem().getFontRenderer(p_renderToolTip_1_);
//            GuiUtils.preItemToolTip(p_renderToolTip_1_);
//            this.drawHoveringText(this.getItemToolTip(p_renderToolTip_1_), p_renderToolTip_2_, p_renderToolTip_3_, font == null ? this.fontRenderer : font);
//            GuiUtils.postItemToolTip();
//        }

        }
    }
}
