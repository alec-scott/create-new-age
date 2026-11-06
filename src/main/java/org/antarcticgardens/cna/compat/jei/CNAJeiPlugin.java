package org.antarcticgardens.cna.compat.jei;

import com.zurrtum.create.client.compat.jei.category.SequencedAssemblyCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.common.Internal;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import org.antarcticgardens.cna.CNABlocks;
import org.antarcticgardens.cna.CNARecipeTypes;
import org.antarcticgardens.cna.CreateNewAge;
import org.antarcticgardens.cna.content.energising.recipe.EnergisingRecipe;

/**
 * PORT-NOTE(26.1): registered through the {@code jei_mod_plugin} entrypoint in fabric.mod.json (was {@code @JeiPlugin}),
 * so the mod runs without JEI. Categories follow Create Fly's JeiClientPlugin (and CC&A's CreateAdditionJEI): they work
 * on {@link RecipeHolder}s. The energising sequenced-assembly step is a Create Fly SequencedRenderer (was a
 * SequencedAssemblySubCategory chosen through the platform layer). Energising recipes reach the client through Fabric's
 * recipe synchronization ({@link CNARecipeTypes#register()}).
 */
public class CNAJeiPlugin implements IModPlugin {
    private static final Identifier ID = CreateNewAge.asResource("jei_plugin");

    public static final IRecipeType<RecipeHolder<EnergisingRecipe>> ENERGISING = createRecipeHolderType("energising");

    @SuppressWarnings("unchecked")
    private static <T> IRecipeType<T> createRecipeHolderType(String path) {
        return (IRecipeType<T>) IRecipeType.create(CreateNewAge.asResource(path), RecipeHolder.class);
    }

    @Override
    public Identifier getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new EnergisingCategory());
        SequencedAssemblyCategory.registerRenderer(CNARecipeTypes.ENERGISING, new EnergisingAssemblyRenderer());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeMap recipes = Internal.getClientSyncedRecipes();
        registration.addRecipes(ENERGISING, recipes.byType(CNARecipeTypes.ENERGISING).stream().toList());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(ENERGISING, CNABlocks.BASIC_ENERGISER.get());
        registration.addCraftingStation(ENERGISING, CNABlocks.ADVANCED_ENERGISER.get());
        registration.addCraftingStation(ENERGISING, CNABlocks.REINFORCED_ENERGISER.get());
    }
}
