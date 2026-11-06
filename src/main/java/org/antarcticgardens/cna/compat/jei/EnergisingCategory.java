package org.antarcticgardens.cna.compat.jei;

import com.zurrtum.create.client.compat.jei.CreateCategory;
import com.zurrtum.create.client.compat.jei.renderer.IconRenderer;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.antarcticgardens.cna.CNABlocks;
import org.antarcticgardens.cna.content.energising.recipe.EnergisingRecipe;
import org.antarcticgardens.cna.util.StringFormatUtil;

import java.util.List;

/** PORT-NOTE(26.1): was EnergisingJeiCategory on Create's CreateRecipeCategory; same layout on Create Fly's CreateCategory. */
public class EnergisingCategory extends CreateCategory<RecipeHolder<EnergisingRecipe>> {
    @Override
    public IRecipeType<RecipeHolder<EnergisingRecipe>> getRecipeType() {
        return CNAJeiPlugin.ENERGISING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("category.create_new_age.energising");
    }

    @Override
    public IDrawable getIcon() {
        return new IconRenderer(CNABlocks.ADVANCED_ENERGISER.get().asItem());
    }

    @Override
    public int getWidth() {
        return 130;
    }

    @Override
    public int getHeight() {
        return 25;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<EnergisingRecipe> entry, IFocusGroup focuses) {
        EnergisingRecipe recipe = entry.value();
        builder.addInputSlot(4, 4).setBackground(SLOT, -1, -1).add(recipe.ingredient());

        List<ProcessingOutput> results = recipe.results();
        for (int i = 0; i < results.size(); i++) {
            int xPos = 80 + (i % 5) * 19;
            int yPos = 4 + (i / 5) * -19;
            addChanceSlot(builder, xPos, yPos, results.get(i));
        }
    }

    @Override
    public void draw(RecipeHolder<EnergisingRecipe> entry, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics,
                     double mouseX, double mouseY) {
        AllGuiTextures.JEI_ARROW.render(graphics, 31, 8);
        Font font = Minecraft.getInstance().font;
        String text = StringFormatUtil.formatLong(entry.value().getEnergyNeeded()) + " ⚡";
        graphics.text(font, text, 50 - font.width(text) / 2, 18, 0xFF1166FF, false);
    }
}
