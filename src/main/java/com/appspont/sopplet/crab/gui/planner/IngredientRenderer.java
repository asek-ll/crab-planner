package com.appspont.sopplet.crab.gui.planner;

import com.appspont.sopplet.crab.gui.planner.renderer.FluidStackRenderer;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class IngredientRenderer {

    private final IIngredientManager ingredientRegistry;
    private final Minecraft mc;
    private final FluidStackRenderer fluidStackRenderer;

    public IngredientRenderer(IIngredientManager ingredientRegistry, Minecraft mc) {
        this.ingredientRegistry = ingredientRegistry;
        this.mc = mc;
        fluidStackRenderer = new FluidStackRenderer();
    }

    public void render(int x, int y, PlannerIngredientStack stack, DrawContext context) {
        final Object rawStack = stack.getIngredient().getRawStack();

        if (rawStack instanceof ItemStack) {
            renderItemStack(x, y, (ItemStack) rawStack);
        } else if (rawStack instanceof FluidStack) {
            IIngredientRenderer<Object> renderer = CrabJeiPlugin.getJeiRuntime().getIngredientManager()
                    .getIngredientRenderer(rawStack);
            renderer.render(context.ms, x, y, rawStack);
            FontRenderer font = renderer.getFontRenderer(mc, stack);
            renderSizeLabel(font, FluidStackRenderer.formatAmount(((FluidStack) rawStack).getAmount()), x, y);
        } else {
            IIngredientRenderer<Object> renderer = CrabJeiPlugin.getJeiRuntime().getIngredientManager()
                    .getIngredientRenderer(rawStack);
            renderer.render(context.ms, x, y, rawStack);
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

        mc.getItemRenderer().renderGuiItem(stack, x, y);

        int count = stack.getCount();
        if (count > 1) {
            FontRenderer font = renderer.getFontRenderer(mc, stack);
            renderSizeLabel(font, compactCount(count), x, y);
        }

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

    public static void renderSizeLabel(FontRenderer fontRenderer, String text, float xPos, float yPos) {
        MatrixStack matrixstack = new MatrixStack();
        matrixstack.translate(0.0D, 0.0D, 200.0F);
        final int offset = -1;

        final float scaleFactor = 0.6f;
        matrixstack.scale(scaleFactor, scaleFactor, scaleFactor);
        final float inverseScaleFactor = 1.0f / scaleFactor;

        final float X = ((xPos + offset + 16.0f - fontRenderer.width(text) * scaleFactor) * inverseScaleFactor);
        final float Y = ((yPos + offset + 16.0f - 7.0f * scaleFactor) * inverseScaleFactor);

        IRenderTypeBuffer.Impl buffer = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
        fontRenderer.drawInBatch(text, X, Y, 0xFFFFFF, true, matrixstack.last().pose(), buffer, false, 0, 15728880);
        buffer.endBatch();
    }

}
