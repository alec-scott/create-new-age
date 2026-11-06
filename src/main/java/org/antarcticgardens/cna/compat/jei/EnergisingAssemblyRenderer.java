package org.antarcticgardens.cna.compat.jei;

import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.client.catnip.gui.element.GuiGameElement;
import com.zurrtum.create.client.compat.jei.category.SequencedAssemblyCategory.SequencedRenderer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.antarcticgardens.cna.CNABlocks;
import org.antarcticgardens.cna.content.energising.recipe.EnergisingRecipe;

import java.util.Optional;

/**
 * PORT-NOTE(26.1): energising step of a sequenced assembly (was JeiEnergisingSubcategory + RecipeViewerEnergiserRenderer):
 * an energiser over a depot; the energy needed is in the step name. The shaft animation of the old renderer is not reproduced. The
 * step name comes from AllAssemblyRecipeNames ({@code recipe.assembly.energising}).
 */
public class EnergisingAssemblyRenderer extends SequencedRenderer<EnergisingRecipe> {
    private static final int BLOCK = 15;
    private static final int PADDING = 12;
    private static final int BOX = BLOCK + PADDING;

    private final GuiGameElement.GuiBlockStateRenderBuilder energiser = GuiGameElement.of(CNABlocks.BASIC_ENERGISER.get().defaultBlockState())
            .rotate(-15.5f, 22.5f, 0)
            .scale(BLOCK / 16f)
            .padding(PADDING);

    private final GuiGameElement.GuiBlockStateRenderBuilder depot = GuiGameElement.of(AllBlocks.DEPOT.defaultBlockState())
            .rotate(-15.5f, 22.5f, 0)
            .scale(BLOCK / 16f)
            .padding(PADDING);

    @Override
    public void render(GuiGraphicsExtractor graphics, int i, int x, int y, Optional<IRecipeSlotView> slot) {
        energiser.at(x + 9 - BOX / 2f, y + 12 - BOX / 2f).render(graphics);
        depot.at(x + 9 - BOX / 2f, y + 12 + BLOCK * 1.6f - BOX / 2f).render(graphics);
    }

    @Override
    public IRecipeSlotBuilder addSlot(IRecipeLayoutBuilder builder, int x, int y, EnergisingRecipe recipe) {
        return null;
    }
}
