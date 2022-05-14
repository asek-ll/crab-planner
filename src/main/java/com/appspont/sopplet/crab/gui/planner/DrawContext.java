package com.appspont.sopplet.crab.gui.planner;

import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredient;

import java.util.List;

public class DrawContext {
    public int mouseX;
    public int mouseY;
    public float partialTicks;
    public PlannerIngredient hoverStack;
    public List<String> hoverText;
}
