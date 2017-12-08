package com.appspont.sopplet.crab;

import com.appspont.sopplet.crab.plugin.CrabJeiPlugin;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.IntBuffer;
import java.util.Base64;

public class GuiItemIconDumper extends GuiScreen {
    private final ImmutableList<ItemStack> visibleStacks;

    private int drawIndex;
    private int parseIndex;
    private File dir = new File(".", "dumps/itempanel_icons");
    private int iconSize;
    private int borderSize;
    private int boxSize;
    private final IIngredientRenderer<ItemStack> ingredientRenderer;
    private final JsonArray items;

    public GuiItemIconDumper(int iconSize) {
        this.iconSize = iconSize;
        borderSize = iconSize / 16;
        boxSize = iconSize + borderSize * 2;

        if (dir.exists()) {
            for (File f : dir.listFiles())
                if (f.isFile()) f.delete();
        } else
            dir.mkdirs();

        mc = Minecraft.getMinecraft();
        final IIngredientRegistry ingredientRegistry = CrabJeiPlugin.getModRegistry().getIngredientRegistry();
        visibleStacks = ingredientRegistry.getIngredients(ItemStack.class);
        ingredientRenderer = ingredientRegistry.getIngredientRenderer(ItemStack.class);

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

    private void drawItem(int x, int y, ItemStack itemstack) {
        ingredientRenderer.render(mc, x, y, itemstack);
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
            drawItem(x + 1, y + 1, visibleStacks.get(drawIndex));
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

    private void exportImage(File dir, BufferedImage img, ItemStack stack) throws IOException {
        final int idFromItem = Item.getIdFromItem(stack.getItem());

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(img, "png", Base64.getEncoder().wrap(os));


        final JsonObject jsonObject = new JsonObject();
        final Item item = stack.getItem();

        jsonObject.addProperty("name", item.getUnlocalizedName());
        jsonObject.addProperty("id", idFromItem);
        jsonObject.addProperty("meta", item.getDamage(stack));
        jsonObject.addProperty("sid", StackUtils.getItemId(stack));
        jsonObject.addProperty("displayName", item.getItemStackDisplayName(stack));
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
}
