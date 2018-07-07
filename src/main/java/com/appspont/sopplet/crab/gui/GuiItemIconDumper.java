package com.appspont.sopplet.crab.gui;

import com.appspont.sopplet.crab.StackUtils;
import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.IntBuffer;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class GuiItemIconDumper extends GuiScreen {

    private final Map<Class, IIngredientRenderer> renderers = new HashMap<>();
    private final List<Object> visibleStacks = new ArrayList<>();


    private int drawIndex;
    private int parseIndex;
    private File dir = new File(".", "dumps");
    private int iconSize;
    private int borderSize;
    private int boxSize;
    private final JsonArray items;

    public GuiItemIconDumper(int iconSize) {
        this.iconSize = iconSize;
        borderSize = iconSize / 16;
        boxSize = iconSize + borderSize * 2;
        dir.mkdirs();

        mc = Minecraft.getMinecraft();
        final IIngredientRegistry ingredientRegistry = CrabJeiPlugin.getModRegistry().getIngredientRegistry();

        for (Class ingredientType : INGREDIENT_TYPES.keySet()) {
            visibleStacks.addAll(ingredientRegistry.getAllIngredients(ingredientType));
            renderers.put(ingredientType, ingredientRegistry.getIngredientRenderer(ingredientType));
        }

        items = new JsonArray();
    }

    private void returnScreen() {
        saveItems();
        Minecraft.getMinecraft().displayGuiScreen(null);
    }

    private void saveItems() {
        final File file = new File("./dumps/items.json");

        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            final PrintWriter printWriter = new PrintWriter(file);
            printWriter.println(items.toString());
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void keyTyped(char c, int keycode) throws IOException {
        if (keycode == Keyboard.KEY_ESCAPE || keycode == Keyboard.KEY_BACK) {
            returnScreen();
            return;
        }
        super.keyTyped(c, keycode);
    }

    @Override
    public void drawScreen(int mousex, int mousey, float frame) {
        try {
            drawItems();
            exportItems();
        } catch (Exception ignored) {
        }
    }

    private void drawItems() {
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0, mc.displayWidth * 16D / iconSize,
                mc.displayHeight * 16D / iconSize, 0, 1000, 3000);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.clearColor(0, 0, 0, 0);
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        int rows = mc.displayHeight / boxSize;
        int cols = mc.displayWidth / boxSize;
        int fit = rows * cols;

        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1, 1, 1, 1);

        for (int i = 0; drawIndex < visibleStacks.size() && i < fit; drawIndex++, i++) {
            int x = i % cols * 18;
            int y = i / cols * 18;
            final Object stack = visibleStacks.get(drawIndex);
            final IIngredientRenderer renderer = renderers.get(stack.getClass());
            renderer.render(mc, x + 1, y + 1, stack);
        }

        GL11.glFlush();
    }

    private void exportItems() throws IOException {
        BufferedImage img = screenshot();
        int rows = img.getHeight() / boxSize;
        int cols = img.getWidth() / boxSize;
        int fit = rows * cols;
        for (int i = 0; parseIndex < visibleStacks.size() && i < fit; parseIndex++, i++) {
            int x = i % cols * boxSize;
            int y = i / cols * boxSize;
            exportImage(dir, img.getSubimage(x + borderSize, y + borderSize, iconSize, iconSize), visibleStacks.get(parseIndex));
        }

        if (parseIndex >= visibleStacks.size())
            returnScreen();
    }

    private static JsonObject itemStackToJsonObject(Object object) {
        final ItemStack stack = (ItemStack) object;

        final Item item = stack.getItem();
        if (item instanceof ItemBlock) {
            Block block = ((ItemBlock) item).getBlock();
            Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
            if (fluid != null) {
                final FluidStack fluidStack = new FluidStack(fluid, Fluid.BUCKET_VOLUME);
                return fluidStackToJsonObject(fluidStack);
            }
        }


        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("name", item.getUnlocalizedName());
        jsonObject.addProperty("id", Item.getIdFromItem(stack.getItem()));
        jsonObject.addProperty("meta", item.getDamage(stack));
        jsonObject.addProperty("sid", StackUtils.getItemId(stack));
        jsonObject.addProperty("displayName", item.getItemStackDisplayName(stack));

        return jsonObject;
    }

    private static JsonObject fluidStackToJsonObject(Object object) {
        final FluidStack stack = (FluidStack) object;
        final JsonObject jsonObject = new JsonObject();
        final Fluid fluid = stack.getFluid();

        jsonObject.addProperty("name", fluid.getUnlocalizedName());
        final String itemId = StackUtils.getItemId(stack);
        jsonObject.addProperty("id", itemId);
        jsonObject.addProperty("sid", itemId);
        jsonObject.addProperty("displayName", fluid.getLocalizedName(stack));

        return jsonObject;
    }

    private void exportImage(File dir, BufferedImage img, Object stack) throws IOException {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(img, "png", Base64.getEncoder().wrap(os));

        final Function<Object, JsonObject> mapper = INGREDIENT_TYPES.get(stack.getClass());
        final JsonObject jsonObject = mapper.apply(stack);

        jsonObject.addProperty("icon", os.toString("UTF-8"));

        items.add(jsonObject);
    }

    private IntBuffer pixelBuffer;
    private int[] pixelValues;

    private BufferedImage screenshot() {
        Framebuffer fb = mc.getFramebuffer();
        Dimension mcSize = new Dimension(mc.displayWidth, mc.displayHeight);
        Dimension texSize = mcSize;

        if (OpenGlHelper.isFramebufferEnabled())
            texSize = new Dimension(fb.framebufferTextureWidth, fb.framebufferTextureHeight);

        int k = texSize.width * texSize.height;
        if (pixelBuffer == null || pixelBuffer.capacity() < k) {
            pixelBuffer = BufferUtils.createIntBuffer(k);
            pixelValues = new int[k];
        }

        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        pixelBuffer.clear();

        if (OpenGlHelper.isFramebufferEnabled()) {
            GlStateManager.bindTexture(fb.framebufferTexture);
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        } else {
            GL11.glReadPixels(0, 0, texSize.width, texSize.height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        }

        pixelBuffer.get(pixelValues);
        TextureUtil.processPixelValues(pixelValues, texSize.width, texSize.height);

        BufferedImage img = new BufferedImage(mcSize.width, mcSize.height, BufferedImage.TYPE_INT_ARGB);
        if (OpenGlHelper.isFramebufferEnabled()) {
            int yOff = texSize.height - mcSize.height;
            for (int y = 0; y < mcSize.height; ++y)
                for (int x = 0; x < mcSize.width; ++x)
                    img.setRGB(x, y, pixelValues[(y + yOff) * texSize.width + x]);
        } else {
            img.setRGB(0, 0, texSize.width, height, pixelValues, 0, texSize.width);
        }

        return img;
    }

    private static final Map<Class, Function<Object, JsonObject>> INGREDIENT_TYPES = ImmutableMap.of(
            ItemStack.class, GuiItemIconDumper::itemStackToJsonObject,
            FluidStack.class, GuiItemIconDumper::fluidStackToJsonObject
    );
}
