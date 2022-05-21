package com.appspont.sopplet.crab.gui.planner;

import com.appspont.sopplet.crab.CraftingPlan;
import com.appspont.sopplet.crab.gui.PlannerGui;
import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class PlanItemWidget extends RectangleWidget implements INestedGuiEventHandler {

    private final String name;
    private final Button loadButton;
    private final Button removeButton;
    private final PlannerGui parent;
    private boolean isActive;

    public PlanItemWidget(String name, PlannerGui parent) {
        this.name = name;
        this.parent = parent;

        loadButton = new Button(0, 0, 40, 20, new StringTextComponent("Load"), this::onPressLoad);
        removeButton = new Button(0, 0, 40, 20, new StringTextComponent("Remove"), this::onPressRemove);
    }

    private void onPressLoad(Button btn) {
        final CraftingPlan plan = CrabJeiPlugin.getPlanStoreManager().load(name);
        parent.setPlan(plan);
    }

    private void onPressRemove(Button btn) {
        CrabJeiPlugin.getPlanStoreManager().remove(name);
        parent.updatePlanItems();
    }

    @Override
    public void draw(DrawContext context) {
        mc.font.drawShadow(context.ms, name, rect.x + 1, rect.y + 5, Color.white.getRGB());

        int x = rect.x + rect.width - 90;

        loadButton.y = rect.y;
        loadButton.x = x;
        loadButton.render(context.ms, context.mouseX, context.mouseY, context.partialTicks);

        x += 44;

        removeButton.y = rect.y;
        removeButton.x = x;
        removeButton.render(context.ms, context.mouseX, context.mouseY, context.partialTicks);
    }

    @Override
    public List<? extends IGuiEventListener> children() {
        return Arrays.asList(loadButton, removeButton);
    }

    @Override
    public boolean isDragging() {
        return false;
    }

    @Override
    public void setDragging(boolean dragging) {

    }

    @Nullable
    @Override
    public IGuiEventListener getFocused() {
        return null;
    }

    @Override
    public void setFocused(@Nullable IGuiEventListener p_231035_1_) {
    }
}
