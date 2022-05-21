package com.appspont.sopplet.crab.gui.planner;

import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredient;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class DrawContext {
    public int mouseX;
    public int mouseY;
    public float partialTicks;
    public PlannerIngredient hoverStack;
    public List<ITextComponent> hoverText;
    public MatrixStack ms;
}
