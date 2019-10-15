package com.appspont.sopplet.crab.gui.planner;

import com.appspont.sopplet.crab.CraftingPlan;
import com.appspont.sopplet.crab.gui.PlannerGui;
import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

public class PlanItemWidget extends RectangleWidget {

    private final String name;
    private final GuiButton loadButton;
    private final GuiButton removeButton;
    private final PlannerGui parent;
    private boolean isActive;

    public PlanItemWidget(String name, PlannerGui parent) {
        this.name = name;
        this.parent = parent;

        loadButton = new GuiButton(1, 0, 0, 40, 20, "Load");
        removeButton = new GuiButton(1, 0, 0, 40, 20, "Remove");
    }

    @Override
    public void draw(DrawContext context) {
        mc.fontRenderer.drawStringWithShadow(name, x + 1, y + 5, Color.white.getRGB());

        int x = this.x + this.width - 90;

        loadButton.y = y;
        loadButton.x = x;
        loadButton.drawButton(mc, context.mouseX, context.mouseY, context.partialTicks);

        x += 44;

        removeButton.y = y;
        removeButton.x = x;
        removeButton.drawButton(mc, context.mouseX, context.mouseY, context.partialTicks);
    }

    @Override
    public boolean mouseClicked(int x, int y, int button) {
        if (loadButton.isMouseOver()) {
            final CraftingPlan plan = CrabJeiPlugin.getPlanStoreManager().load(name);
            parent.setPlan(plan);
            return true;
        }
        return false;
    }
}
