package com.appspont.sopplet.crab.gui.planner.renderer;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FluidStackRenderer implements IIngredientRenderer<FluidStack> {
    private static final int TEX_WIDTH = 16;
    private static final int TEX_HEIGHT = 16;
    private static final int MIN_FLUID_HEIGHT = 1; // ensure tiny amounts of fluid are still visible

    private final int capacityMb;
    private final TooltipMode tooltipMode;
    private final int width;
    private final int height;

    enum TooltipMode {
        SHOW_AMOUNT,
        SHOW_AMOUNT_AND_CAPACITY,
        ITEM_LIST
    }

    public FluidStackRenderer() {
        this(Fluid.BUCKET_VOLUME, TooltipMode.SHOW_AMOUNT, TEX_WIDTH, TEX_HEIGHT);
    }

    public FluidStackRenderer(int capacityMb, boolean showCapacity, int width, int height) {
        this(capacityMb, showCapacity ? TooltipMode.SHOW_AMOUNT_AND_CAPACITY : TooltipMode.SHOW_AMOUNT, width, height);
    }

    public FluidStackRenderer(int capacityMb, TooltipMode tooltipMode, int width, int height) {
        this.capacityMb = capacityMb;
        this.tooltipMode = tooltipMode;
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(Minecraft minecraft, final int xPosition, final int yPosition, @Nullable FluidStack fluidStack) {
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();

        drawFluid(minecraft, xPosition, yPosition, fluidStack);
        drawAmount(minecraft.fontRenderer, formatAmount(fluidStack.amount), xPosition, yPosition);

        GlStateManager.color(1, 1, 1, 1);

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
    }

    private String formatAmount(Integer amount) {

        if (amount < 100) {
            return amount + "mB";
        }

        if (amount < 1_000_000) {
            return limit(amount, 1_000, 3) + "B";
        }

        return limit(amount, 1_000_000, 2) + "KB";
    }

    private String limit(int amount, int bound, int limit) {
        int result = amount / bound;
        String part = String.valueOf(result);
        int secondPartSize = limit - part.length() - 1;
        if (secondPartSize <= 0) {
            return part;
        }

        int mult = (int) (bound / Math.pow(10, secondPartSize));
        int decimalPart = (amount - result * bound) / mult;
        if (decimalPart == 0) {
            return part;
        }
        return part + "." + decimalPart;
    }

    public void drawAmount(FontRenderer fontRenderer, String amount, int xPos, int yPos) {
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
        final int X = (int) (((float) xPos + offset + 16.0f - fontRenderer.getStringWidth(amount) * scaleFactor) * inverseScaleFactor);
        final int Y = (int) (((float) yPos + offset + 16.0f - 7.0f * scaleFactor) * inverseScaleFactor);
        fontRenderer.drawStringWithShadow(amount, X, Y, 16777215);
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();

        fontRenderer.setUnicodeFlag(unicodeFlag);
    }

    private void drawFluid(Minecraft minecraft, final int xPosition, final int yPosition, @Nullable FluidStack fluidStack) {
        if (fluidStack == null) {
            return;
        }
        Fluid fluid = fluidStack.getFluid();
        if (fluid == null) {
            return;
        }

        TextureAtlasSprite fluidStillSprite = getStillFluidSprite(minecraft, fluid);

        int fluidColor = fluid.getColor(fluidStack);

        drawTiledSprite(minecraft, xPosition, yPosition, width, height, fluidColor, height, fluidStillSprite);
    }

    private void drawTiledSprite(Minecraft minecraft, final int xPosition, final int yPosition, final int tiledWidth, final int tiledHeight, int color, int scaledAmount, TextureAtlasSprite sprite) {
        minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        setGLColorFromInt(color);

        final int xTileCount = tiledWidth / TEX_WIDTH;
        final int xRemainder = tiledWidth - (xTileCount * TEX_WIDTH);
        final int yTileCount = scaledAmount / TEX_HEIGHT;
        final int yRemainder = scaledAmount - (yTileCount * TEX_HEIGHT);

        final int yStart = yPosition + tiledHeight;

        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int width = (xTile == xTileCount) ? xRemainder : TEX_WIDTH;
                int height = (yTile == yTileCount) ? yRemainder : TEX_HEIGHT;
                int x = xPosition + (xTile * TEX_WIDTH);
                int y = yStart - ((yTile + 1) * TEX_HEIGHT);
                if (width > 0 && height > 0) {
                    int maskTop = TEX_HEIGHT - height;
                    int maskRight = TEX_WIDTH - width;

                    drawTextureWithMasking(x, y, sprite, maskTop, maskRight, 100);
                }
            }
        }
    }

    private static TextureAtlasSprite getStillFluidSprite(Minecraft minecraft, Fluid fluid) {
        TextureMap textureMapBlocks = minecraft.getTextureMapBlocks();
        ResourceLocation fluidStill = fluid.getStill();
        TextureAtlasSprite fluidStillSprite = null;
        if (fluidStill != null) {
            fluidStillSprite = textureMapBlocks.getTextureExtry(fluidStill.toString());
        }
        if (fluidStillSprite == null) {
            fluidStillSprite = textureMapBlocks.getMissingSprite();
        }
        return fluidStillSprite;
    }

    private static void setGLColorFromInt(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        GlStateManager.color(red, green, blue, 1.0F);
    }

    private static void drawTextureWithMasking(double xCoord, double yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, double zLevel) {
        double uMin = (double) textureSprite.getMinU();
        double uMax = (double) textureSprite.getMaxU();
        double vMin = (double) textureSprite.getMinV();
        double vMax = (double) textureSprite.getMaxV();
        uMax = uMax - (maskRight / 16.0 * (uMax - uMin));
        vMax = vMax - (maskTop / 16.0 * (vMax - vMin));

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(xCoord, yCoord + 16, zLevel).tex(uMin, vMax).endVertex();
        bufferBuilder.pos(xCoord + 16 - maskRight, yCoord + 16, zLevel).tex(uMax, vMax).endVertex();
        bufferBuilder.pos(xCoord + 16 - maskRight, yCoord + maskTop, zLevel).tex(uMax, vMin).endVertex();
        bufferBuilder.pos(xCoord, yCoord + maskTop, zLevel).tex(uMin, vMin).endVertex();
        tessellator.draw();
    }

    @Override
    public List<String> getTooltip(Minecraft minecraft, FluidStack fluidStack, ITooltipFlag tooltipFlag) {
        List<String> tooltip = new ArrayList<>();
        Fluid fluidType = fluidStack.getFluid();
        if (fluidType == null) {
            return tooltip;
        }

        String fluidName = fluidType.getLocalizedName(fluidStack);
        tooltip.add(fluidName);

        if (tooltipMode == TooltipMode.SHOW_AMOUNT_AND_CAPACITY) {
            String amount = Translator.translateToLocalFormatted("jei.tooltip.liquid.amount.with.capacity", fluidStack.amount, capacityMb);
            tooltip.add(TextFormatting.GRAY + amount);
        } else if (tooltipMode == TooltipMode.SHOW_AMOUNT) {
            String amount = Translator.translateToLocalFormatted("jei.tooltip.liquid.amount", fluidStack.amount);
            tooltip.add(TextFormatting.GRAY + amount);
        }

        return tooltip;
    }
}
