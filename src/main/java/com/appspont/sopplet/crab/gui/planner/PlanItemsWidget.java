package com.appspont.sopplet.crab.gui.planner;

import com.appspont.sopplet.crab.gui.PlannerGui;
import net.minecraft.client.Minecraft;

import java.util.List;

public class PlanItemsWidget extends ListWidget<PlanItemWidget> {

    private final PlannerGui parent;

    public PlanItemsWidget(Minecraft mc, PlannerGui parent) {
        super(mc, 124, 96, 2, 100, 20);
        this.parent = parent;
    }

    public void setNames(List<String> names) {
        clearEntries();
        for (String name : names) {
            final PlanItemWidget widget = new PlanItemWidget(name, parent);
            addEntry(widget);
        }
    }
}
