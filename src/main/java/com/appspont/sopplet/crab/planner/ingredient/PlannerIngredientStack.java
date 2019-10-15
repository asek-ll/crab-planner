package com.appspont.sopplet.crab.planner.ingredient;

import com.appspont.sopplet.crab.JsonAware;
import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.*;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public abstract class PlannerIngredientStack {
    protected final PlannerIngredient ingredient;

    protected PlannerIngredientStack(PlannerIngredient ingredient) {
        this.ingredient = ingredient;
    }

    public PlannerIngredient getIngredient() {
        return ingredient;
    }

    public abstract int getAmount();


    public static class JsonHelper implements JsonSerializer<PlannerIngredientStack>, JsonDeserializer<PlannerIngredientStack> {

        private final LoadingCache<Class<?>, Map<String, Object>> uidCacheByType = CacheBuilder.newBuilder()
                .build(new CacheLoader<Class<?>, Map<String, Object>>() {
                    @Override
                    public Map<String, Object> load(Class<?> type) throws Exception {
                        final Collection<?> allIngredients = CrabJeiPlugin.getModRegistry().getIngredientRegistry().getAllIngredients(type);
                        final IIngredientHelper<Object> ingredientHelper = CrabJeiPlugin.getModRegistry().getIngredientRegistry().getIngredientHelper(type);

                        final HashMap<String, Object> result = new HashMap<>();
                        for (Object allIngredient : allIngredients) {
                            final String uniqueId = ingredientHelper.getUniqueId(allIngredient);
                            result.put(uniqueId, allIngredient);
                        }
                        return result;
                    }
                });

        private PlannerIngredient getByUid(String uid) {
            try {
                final ItemStack stack = (ItemStack) uidCacheByType.get(ItemStack.class).get(uid);
                if (stack != null) {
                    return new PlannerItem(stack);
                }
            } catch (ExecutionException ignored) {
            }
            try {
                final FluidStack fluidStack = (FluidStack) uidCacheByType.get(FluidStack.class).get(uid);
                if (fluidStack != null) {
                    return new PlannerFluid(fluidStack);
                }
            } catch (ExecutionException ignored) {
            }
            return null;
        }

        @Override
        public PlannerIngredientStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject = json.getAsJsonObject();
            final String uid = jsonObject.get("uid").getAsString();

            final PlannerIngredient ingredient = getByUid(uid);
            if (ingredient == null) {
                return null;
            }

            final int amount = jsonObject.get("amount").getAsInt();

            return ingredient.createStack(amount);
        }

        @Override
        public JsonElement serialize(PlannerIngredientStack src, Type typeOfSrc, JsonSerializationContext context) {
            final PlannerIngredient ingredient = src.getIngredient();

            final JsonObject jsonObject = new JsonObject();
            final String uniqueId = CrabJeiPlugin.getModRegistry().getIngredientRegistry().getIngredientHelper(ingredient.getRawStack()).getUniqueId(ingredient.getRawStack());
            jsonObject.addProperty("uid", uniqueId);
            jsonObject.addProperty("amount", src.getAmount());
            return jsonObject;
        }
    }
}
