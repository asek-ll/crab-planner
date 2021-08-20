package com.appspont.sopplet.crab;

import com.appspont.sopplet.crab.planner.ingredient.PlannerGoal;
import com.appspont.sopplet.crab.planner.ingredient.PlannerIngredientStack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PlanStoreManager {
    private static CraftingPlan currentPlan = new CraftingPlan();
    private final File baseDir;
    private final Gson gson;

    public PlanStoreManager() {
        baseDir = new File(".", "plans");
        baseDir.mkdirs();

        gson = new GsonBuilder()
                .registerTypeAdapter(CraftingPlan.class, new CraftingPlan.JsonHelper())
                .registerTypeAdapter(CraftingRecipe.class, new CraftingRecipe.JsonHelper())
                .registerTypeAdapter(PlannerRecipe.class, new PlannerRecipe.JsonHelper())
                .registerTypeAdapter(PlannerGoal.class, new PlannerGoal.JsonHelper())
                .registerTypeAdapter(PlannerIngredientStack.class, new PlannerIngredientStack.JsonHelper())
                .setPrettyPrinting()
                .create();
    }

    public CraftingPlan getCurrentPlan() {
        return currentPlan;
    }

    public void setCurrentPlan(CraftingPlan plan) {
        currentPlan = plan;
    }

    public void savePlan(CraftingPlan plan) throws IOException {
        final File file = new File(baseDir, plan.getName());
        if (!file.exists()) {
            if (!file.createNewFile()) {
                return;
            }
        }
        writeToFile(file, plan);
    }

    private void writeToFile(File file, CraftingPlan plan) throws IOException {
        try (final Writer writer = new FileWriter(file)) {
            gson.toJson(plan, writer);
        }
    }

    public CraftingPlan load(String planName) {
        final File file = new File(baseDir, planName);
        if (file.exists()) {
            setCurrentPlan(readFromFile(file));
        }
        return currentPlan;
    }

    public boolean remove(String planName) {
        final File file = new File(baseDir, planName);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    private CraftingPlan readFromFile(File file) {
        CraftingPlan craftingPlan;
        try (final Reader reader = new FileReader(file)) {
            craftingPlan = gson.fromJson(reader, CraftingPlan.class);
        } catch (Exception ignored) {
            craftingPlan = new CraftingPlan();
        }
        craftingPlan.setName(file.getName());
        return craftingPlan;
    }

    public List<String> getPlanNames() {
        final File[] array = baseDir.listFiles();
        if (array == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(array)
                .map(File::getName)
                .collect(Collectors.toList());
    }
}
