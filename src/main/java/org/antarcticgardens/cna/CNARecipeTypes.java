package org.antarcticgardens.cna;

import com.zurrtum.create.AllAssemblyRecipeNames;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.antarcticgardens.cna.content.energising.recipe.EnergisingRecipe;

/**
 * PORT-NOTE(26.1): was an IRecipeTypeInfo enum registered through the platform registrar; now plain registry fields,
 * as Create Fly's AllRecipeTypes/AllRecipeSerializers and CC&A's CARecipes.
 */
public class CNARecipeTypes {
    public static final RecipeType<EnergisingRecipe> ENERGISING = registerType("energising");
    public static final RecipeSerializer<EnergisingRecipe> ENERGISING_SERIALIZER = registerSerializer("energising", EnergisingRecipe.SERIALIZER);

    private static <T extends Recipe<?>> RecipeType<T> registerType(String id) {
        String name = CreateNewAge.MOD_ID + ":" + id;
        return Registry.register(BuiltInRegistries.RECIPE_TYPE, Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, id), new RecipeType<T>() {
            @Override
            public String toString() {
                return name;
            }
        });
    }

    private static <T extends Recipe<?>> RecipeSerializer<T> registerSerializer(String id, RecipeSerializer<T> serializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, id), serializer);
    }

    public static void register() {
        AllAssemblyRecipeNames.register(ENERGISING, EnergisingRecipe::getDescriptionForAssembly);
        // Recipe viewers read client-synced recipes (as Create Fly's RecipeCommonPlugin does for its own types)
        if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("jei"))
            net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization.synchronizeRecipeSerializer(ENERGISING_SERIALIZER);
    }
}
