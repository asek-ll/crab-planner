package com.appspont.sopplet.crab.gui.planner;

import com.appspont.sopplet.crab.PlannerContainer;
import com.appspont.sopplet.crab.planner.ingredient.PlannerGoal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;

import java.awt.*;

public class GoalTooltip extends Tooltip<PlannerGoal> {

    private final GuiButton delete;
    private final PlannerContainer parent;
    private final GuiTextField guiTextField;

    public GoalTooltip(PlannerGoal source, int x, int y, PlannerContainer parent) {
        super(source, x, y, 100, 100);
        delete = new GuiButton(1, x + 4, y + 4, 100 - 8, 20, "Delete");
        guiTextField = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, x + 4, y + 28, 100 - 8, 18);
        this.parent = parent;
    }

    @Override
    public void draw(int x, int y) {
        Gui.drawRect(this.x, this.y, this.x + width, this.y + height, Color.red.getRGB());
        delete.drawButton(Minecraft.getMinecraft(), x, y, 0);
        guiTextField.drawTextBox();
    }

    @Override
    public void handleClick(int x, int y, int button) {
        if (delete.isMouseOver()) {
            parent.removeGoal(source);
            removed = true;
        }
    }
}
