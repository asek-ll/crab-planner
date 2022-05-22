package com.appspont.sopplet.crab.gui.planner.widget;

import com.appspont.sopplet.crab.gui.planner.DrawContext;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.AbstractList;

import java.util.Objects;

public class ListWidget<T extends RectangleWidget> extends AbstractList<RectangleWidget> implements Widget {
    private DrawContext context;

    public ListWidget(Minecraft mc, int width, int height, int top, int bottom, int slotHeight) {
        super(mc, width, height, top, bottom, slotHeight);
    }

    @Override
    public int getRowWidth() {
        return width;
    }

    @Override
    protected int getScrollbarPosition() {
        return getLeft() + width;
    }

    @Override
    public boolean mouseScrolled(double p_231043_1_, double p_231043_3_, double scrollAmount) {
        this.setScrollAmount(this.getScrollAmount() - scrollAmount * (double)this.itemHeight);
        return true;
    }

    @Override
    protected void renderList(MatrixStack ms, int left, int top, int mouseX, int mouseY, float partial) {
        int i = this.getItemCount();

        for(int j = 0; j < i; ++j) {
            int rowTop = getRowTop(j);
            int rowBottom = rowTop + itemHeight;
            if (rowBottom <= y1 && rowTop >= y0) {
                int rowHeight = itemHeight - 4;
                RectangleWidget e = getEntry(j);
                int rowWidth = getRowWidth();

                int rowLeft = getRowLeft();
                e.render(ms, j, rowTop, rowLeft, rowWidth, rowHeight, mouseX, mouseY,
                        isMouseOver(mouseX, mouseY) && Objects.equals(getEntryAtPosition(mouseX, mouseY), e),
                        partial);
            }
        }

    }

    //    @Override
//    protected int getSize() {
//        return childWidgets.size();
//    }
//
//    @Override
//    protected void elementClicked(int index, boolean selected, int mouseX, int mouseY) {
//        final Widget goal = childWidgets.get(index);
//        goal.mouseClicked(mouseX, mouseY, 0);
//    }
//
//    @Override
//    protected boolean isSelected(int i) {
//        return false;
//    }
//
//    @Override
//    protected void drawBackground() {
//
//    }
//
//    @Override
//    protected void drawSlot(int index, int x, int y, int height, int mouseX, int mouseY, float partialTicks) {
//        if (y + (slotHeight-4) > this.top && y < this.bottom) {
//            final RectangleWidget goal = childWidgets.get(index);
//            goal.setLocation(x, y);
//            goal.draw(context);
//        }
//    }
//
//    @Override
//    protected int getScrollBarX() {
//        return this.left + this.width - 6;
//    }
//
//    @Override
//    public int getListWidth() {
//        return this.width;
//    }
//
//    @Override
//    protected void overlayBackground(int p_overlayBackground_1_, int p_overlayBackground_2_, int p_overlayBackground_3_, int p_overlayBackground_4_) {
//    }

    @Override
    public void draw(DrawContext context) {
        this.context = context;
        render(context.ms, context.mouseX, context.mouseY, context.partialTicks);
    }

    public DrawContext getContext() {
        return context;
    }

    @Override
    public boolean mouseClicked(int x, int y, int button) {
        return false;
    }

//    @Override
//    public void handleMouseInput() {
//        if (this.isMouseYWithinSlotBounds(this.mouseY)) {
//            if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.mouseY >= this.top && this.mouseY <= this.bottom) {
//                int leftCorner = left + (this.width - this.getListWidth()) / 2;
//                int rightCorner = left + (this.width + this.getListWidth()) / 2;
//                int relativeMouseY = this.mouseY - this.top - this.headerPadding + (int) this.amountScrolled - 4;
//                int relativeMouseItemIndex = relativeMouseY / this.slotHeight;
//
//                if (relativeMouseItemIndex < this.getSize()
//                        && relativeMouseItemIndex >= 0
//                        && this.mouseX >= leftCorner
//                        && this.mouseX <= rightCorner) {
//
//                    this.selectedElement = relativeMouseItemIndex;
//
//                    this.elementClicked(relativeMouseItemIndex, false, this.mouseX, this.mouseY);
//
//                } else if (this.mouseX >= leftCorner && this.mouseX <= rightCorner && relativeMouseY < 0) {
//                    this.clickedHeader(this.mouseX - leftCorner, this.mouseY - this.top + (int) this.amountScrolled - 4);
//                }
//            }
//
//            if (Mouse.isButtonDown(0) && this.getEnabled()) {
//                if (this.initialClickY != -1) {
//                    if (this.initialClickY >= 0) {
//                        int diff = (int)((this.mouseY - this.initialClickY) * this.scrollMultiplier / slotHeight);
//
//                        if (diff != 0) {
//                            this.amountScrolled -= diff * slotHeight;
//                            this.initialClickY = this.mouseY;
//                        }
//                    }
//                } else {
//                    boolean flag1 = true;
//                    if (this.mouseY >= this.top && this.mouseY <= this.bottom) {
//                        int listOffset = (this.width - this.getListWidth()) / 2;
//                        int listWidth = (this.width + this.getListWidth()) / 2;
//                        int relativeMouseY = this.mouseY - this.top - this.headerPadding + (int) this.amountScrolled - 4;
//
//                        int relativeMouseItemIndex = relativeMouseY / this.slotHeight;
//
//                        if (relativeMouseItemIndex < this.getSize()
//                                && relativeMouseItemIndex >= 0
//                                && this.mouseX >= listOffset
//                                && this.mouseX <= listWidth
//                        ) {
//                            boolean flag = relativeMouseItemIndex == this.selectedElement && Minecraft.getSystemTime() - this.lastClicked < 250L;
//                            this.elementClicked(relativeMouseItemIndex, flag, this.mouseX, this.mouseY);
//                            this.selectedElement = relativeMouseItemIndex;
//                            this.lastClicked = Minecraft.getSystemTime();
//                        } else if (this.mouseX >= listOffset && this.mouseX <= listWidth && relativeMouseY < 0) {
//                            this.clickedHeader(this.mouseX - listOffset, this.mouseY - this.top + (int) this.amountScrolled - 4);
//                            flag1 = false;
//                        }
//
//                        int scrollBarLeft = this.getScrollBarX();
//                        int scrollBarRight = scrollBarLeft + 6;
//
//                        if (this.mouseX >= scrollBarLeft && this.mouseX <= scrollBarRight) {
//                            this.scrollMultiplier = -1.0F;
//                            int maxScroll = this.getMaxScroll();
//                            if (maxScroll < 1) {
//                                maxScroll = 1;
//                            }
//
//                            int l1 = (int) ((float) ((this.bottom - this.top) * (this.bottom - this.top)) / (float) this.getContentHeight());
//                            l1 = MathHelper.clamp(l1, 32, this.bottom - this.top - 8);
//                            this.scrollMultiplier /= (float) (this.bottom - this.top - l1) / (float) maxScroll;
//                        } else {
//                            this.scrollMultiplier = 1.0F;
//                        }
//
//                        if (flag1) {
//                            this.initialClickY = this.mouseY;
//                        } else {
//                            this.initialClickY = -2;
//                        }
//                    } else {
//                        this.initialClickY = -2;
//                    }
//                }
//            } else {
//                this.initialClickY = -1;
//            }
//
//            int leftCorner = Mouse.getEventDWheel();
//            if (leftCorner != 0) {
//                if (leftCorner > 0) {
//                    leftCorner = -1;
//                } else if (leftCorner < 0) {
//                    leftCorner = 1;
//                }
//
//                this.amountScrolled += (float) (leftCorner * this.slotHeight);
//            }
//
//        }
//
////        super.handleMouseInput();
//    }
//
//    @Override
//    public void setDimensions(int width, int height, int top, int bottom) {
//        super.setDimensions(width, height, top, bottom);
//        for (int i = 0; i < childWidgets.size(); i++) {
//            final T t = childWidgets.get(i);
//            t.width = width;
//        }
//    }
}
