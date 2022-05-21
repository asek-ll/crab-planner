package com.appspont.sopplet.crab.planner.ingredient;

import net.minecraftforge.fluids.FluidStack;

import java.util.Objects;

public class PlannerFluidStack extends PlannerIngredientStack {
    private final FluidStack fluidStack;

    public PlannerFluidStack(FluidStack fluidStack) {
        super(new PlannerFluid(fluidStack));
        this.fluidStack = fluidStack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return fluidStack.isFluidStackIdentical(((PlannerFluidStack) o).fluidStack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fluidStack);
    }

    @Override
    public int getAmount() {
        return fluidStack.getAmount();
    }

    @Override
    public void setAmount(int amount) {
        fluidStack.setAmount(amount);
    }
}
