package com.appspont.sopplet.crab.gui.planner;

import com.appspont.sopplet.crab.gui.planner.renderer.FluidStackRenderer;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class IngredientRenderer {

    private final IIngredientRegistry ingredientRegistry;
    private final Minecraft mc;
    private final FluidStackRenderer fluidStackRenderer;

    public IngredientRenderer(IIngredientRegistry ingredientRegistry, Minecraft mc) {
        this.ingredientRegistry = ingredientRegistry;
        this.mc = mc;
        fluidStackRenderer = new FluidStackRenderer();
    }

    public void render(int x, int y, PlannerIngredientStack stack, DrawContext context) {
        final Object rawStack = stack.getIngredient().getRawStack();

        if (rawStack instanceof ItemStack) {
            renderItemStack(x, y, (ItemStack) rawStack);
        } else if (rawStack instanceof FluidStack) {
            fluidStackRenderer.render(mc, x, y, (FluidStack) rawStack);
        } else {
            final IIngredientRenderer<Object> renderer = ingredientRegistry.getIngredientRenderer(rawStack);
            renderer.render(mc, x, y, rawStack);
        }

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

    private void renderItemStack(int x, int y, ItemStack stack) {
        final IIngredientRenderer<ItemStack> renderer = ingredientRegistry.getIngredientRenderer(stack);

        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        FontRenderer font = renderer.getFontRenderer(mc, stack);
        mc.getRenderItem().renderItemAndEffectIntoGUI(null, stack, x, y);

//        mc.getRenderItem().renderItemOverlayIntoGUI(font, stack, x, y, null);
        int count = stack.getCount();
        if (count > 1) {
            renderStackSize(font, compactCount(count), x, y);
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableDepth();

    }

    public static String compactCount(int count) {
        if (count < 10_000) {
            return String.valueOf(count);
        }
        if (count < 1_000_000) {
            return count / 1_000 + "K";
        }

        if (count < 1_000_000_000) {
            return count / 1_000_000 + "M";
        }
        return String.valueOf(count);
    }

    public void renderStackSize(FontRenderer fontRenderer, String stackSize, int xPos, int yPos) {
        final float scaleFactor = 0.5f;
        final float inverseScaleFactor = 1.0f / scaleFactor;
        final int offset = -1;

        final boolean unicodeFlag = fontRenderer.getUnicodeFlag();
        fontRenderer.setUnicodeFlag(false);

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.pushMatrix();
        GlStateManager.scale(scaleFactor, scaleFactor, scaleFactor);
        final int X = (int) (((float) xPos + offset + 16.0f - fontRenderer.getStringWidth(stackSize) * scaleFactor) * inverseScaleFactor);
        final int Y = (int) (((float) yPos + offset + 16.0f - 7.0f * scaleFactor) * inverseScaleFactor);
        fontRenderer.drawStringWithShadow(stackSize, X, Y, 16777215);
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();

        fontRenderer.setUnicodeFlag(unicodeFlag);
    }


}