package com.appspont.sopplet.crab.planner.ingredient;

import net.minecraftforge.fluids.FluidStack;

import java.util.Objects;

public class PlannerFluid extends PlannerIngredient<FluidStack> {

    public PlannerFluid(FluidStack fluidStack) {
        super(fluidStack);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlannerFluid that = (PlannerFluid) o;
        return stack.isFluidEqual(that.stack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stack.getFluid(), stack.getTag());
    }

    @Override
    public PlannerIngredientStack createStack(int amount) {
        return new PlannerFluidStack(new FluidStack(stack.getFluid(), amount));
    }

    @Override
    public int getAmount() {
        return stack.getAmount();
    }
}
