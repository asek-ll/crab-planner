package com.appspont.sopplet.crab.planner.ingredient;

import mezz.jei.api.ingredients.IIngredientType;

public class IngredientTypeImpl<T> implements IIngredientType<T> {
    private final Class<? extends T> tClass;

    public IngredientTypeImpl(Class<? extends T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public Class<? extends T> getIngredientClass() {
        return tClass;
    }
}
