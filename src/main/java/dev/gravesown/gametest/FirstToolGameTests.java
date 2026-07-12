package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModItems;
import dev.gravesown.registry.ModToolTiers;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Gravesown.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FirstToolGameTests {
    private static final TagKey<Item> HANDPICKS = ItemTags.create(Gravesown.id("handpicks"));
    private static final TagKey<Item> KNIVES = ItemTags.create(Gravesown.id("knives"));
    private static final TagKey<Item> FIRST_TOOL_MATERIALS = ItemTags.create(Gravesown.id("first_tool_materials"));
    private static final List<String> RECIPE_IDS = List.of(
            "ribroot_planks_from_stem",
            "ribroot_splint",
            "thread_binding",
            "crude_handpick",
            "hushstone_shard",
            "hushstone_from_shards",
            "bound_knife"
    );

    private FirstToolGameTests() {
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void firstToolItemsHaveStableIdsTagsAndTiers(GameTestHelper helper) {
        Item[] items = {
                ModItems.RIBROOT_SPLINT.get(),
                ModItems.THREAD_BINDING.get(),
                ModItems.HUSHSTONE_SHARD.get(),
                ModItems.CRUDE_HANDPICK.get(),
                ModItems.BOUND_KNIFE.get()
        };
        String[] ids = {
                "ribroot_splint",
                "thread_binding",
                "hushstone_shard",
                "crude_handpick",
                "bound_knife"
        };
        for (int index = 0; index < items.length; index++) {
            helper.assertTrue(
                    Gravesown.id(ids[index]).equals(BuiltInRegistries.ITEM.getKey(items[index])),
                    ids[index] + " item registry id must remain stable"
            );
        }

        ItemStack handpick = new ItemStack(ModItems.CRUDE_HANDPICK.get());
        ItemStack knife = new ItemStack(ModItems.BOUND_KNIFE.get());
        helper.assertTrue(handpick.getMaxDamage() == 48, "Crude Handpick must have 48 durability");
        helper.assertTrue(knife.getMaxDamage() == 96, "Bound Knife must have 96 durability");
        helper.assertTrue(handpick.is(ItemTags.PICKAXES), "Crude Handpick must be a standard pickaxe-tag tool");
        helper.assertTrue(handpick.is(HANDPICKS), "Crude Handpick must be in the Gravesown handpick tag");
        helper.assertTrue(knife.is(ItemTags.SWORDS), "Bound Knife must be a standard sword-tag tool");
        helper.assertTrue(knife.is(KNIVES), "Bound Knife must be in the Gravesown knife tag");
        helper.assertTrue(
                new ItemStack(ModItems.RIBROOT_SPLINT.get()).is(FIRST_TOOL_MATERIALS)
                        && new ItemStack(ModItems.THREAD_BINDING.get()).is(FIRST_TOOL_MATERIALS)
                        && new ItemStack(ModItems.HUSHSTONE_SHARD.get()).is(FIRST_TOOL_MATERIALS),
                "Every local first-tool material must be tagged"
        );

        helper.assertTrue(
                ModToolTiers.CRUDE.getRepairIngredient().test(new ItemStack(ModItems.RIBROOT_SPLINT.get())),
                "Crude Handpick must repair with Ribroot Splints"
        );
        helper.assertTrue(
                !ModToolTiers.CRUDE.getRepairIngredient().test(new ItemStack(Items.STICK)),
                "Crude Handpick must reject vanilla sticks"
        );
        helper.assertTrue(
                ModToolTiers.BOUND_HUSHSTONE.getRepairIngredient().test(new ItemStack(ModItems.HUSHSTONE_SHARD.get())),
                "Bound Knife must repair with Hushstone Shards"
        );
        helper.assertTrue(
                !ModToolTiers.BOUND_HUSHSTONE.getRepairIngredient().test(new ItemStack(Items.FLINT)),
                "Bound Knife must reject vanilla flint"
        );
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void crudeHandpickUnlocksHushstoneButNotTheDeepLayer(GameTestHelper helper) {
        ItemStack handpick = new ItemStack(ModItems.CRUDE_HANDPICK.get());
        BlockState hushstone = ModBlocks.HUSHSTONE.get().defaultBlockState();
        BlockState deepHushstone = ModBlocks.DEEP_HUSHSTONE.get().defaultBlockState();
        ItemStack knife = new ItemStack(ModItems.BOUND_KNIFE.get());
        BlockState threadgrass = ModBlocks.THREADGRASS.get().defaultBlockState();

        helper.assertTrue(handpick.isCorrectToolForDrops(hushstone), "Crude Handpick must harvest Hushstone");
        helper.assertTrue(!handpick.isCorrectToolForDrops(deepHushstone), "Crude Handpick must not harvest Deep Hushstone");
        helper.assertTrue(handpick.getDestroySpeed(hushstone) == 2.5F, "Crude Handpick must use its exact mining speed");
        helper.assertTrue(threadgrass.is(BlockTags.SWORD_EFFICIENT), "Threadgrass must be knife-efficient vegetation");
        helper.assertTrue(knife.getDestroySpeed(threadgrass) > 1.0F, "Bound Knife must cut Threadgrass efficiently");
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void localMaterialsCraftBothFirstToolsInTwoByTwoGrid(GameTestHelper helper) {
        for (String recipeId : RECIPE_IDS) {
            CraftingRecipe recipe = recipe(helper, recipeId);
            helper.assertTrue(recipe.canCraftInDimensions(2, 2), recipeId + " must fit the player 2x2 grid");
            for (Ingredient ingredient : recipe.getIngredients()) {
                for (ItemStack candidate : ingredient.getItems()) {
                    ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(candidate.getItem());
                    helper.assertTrue(
                            Gravesown.MOD_ID.equals(itemId.getNamespace()),
                            recipeId + " must not accept a vanilla ingredient: " + itemId
                    );
                }
            }
        }

        ItemStack planks = craft(
                helper,
                "ribroot_planks_from_stem",
                1,
                1,
                List.of(new ItemStack(ModItems.RIBROOT_STEM.get()))
        );
        assertOutput(helper, planks, ModItems.RIBROOT_PLANKS.get(), 4, "Ribroot conversion");

        ItemStack splints = craft(
                helper,
                "ribroot_splint",
                1,
                2,
                List.of(new ItemStack(ModItems.RIBROOT_PLANKS.get()), new ItemStack(ModItems.RIBROOT_PLANKS.get()))
        );
        assertOutput(helper, splints, ModItems.RIBROOT_SPLINT.get(), 4, "Splint recipe");

        ItemStack bindings = craft(
                helper,
                "thread_binding",
                2,
                2,
                List.of(
                        new ItemStack(ModItems.THREADGRASS.get()),
                        new ItemStack(ModItems.THREADGRASS.get()),
                        new ItemStack(ModItems.THREADGRASS.get()),
                        ItemStack.EMPTY
                )
        );
        assertOutput(helper, bindings, ModItems.THREAD_BINDING.get(), 2, "Thread Binding recipe");

        ItemStack handpick = craft(
                helper,
                "crude_handpick",
                2,
                2,
                List.of(
                        new ItemStack(ModItems.RIBROOT_SPLINT.get()),
                        new ItemStack(ModItems.RIBROOT_SPLINT.get()),
                        ItemStack.EMPTY,
                        new ItemStack(ModItems.THREAD_BINDING.get())
                )
        );
        assertOutput(helper, handpick, ModItems.CRUDE_HANDPICK.get(), 1, "Crude Handpick recipe");
        helper.assertTrue(
                handpick.isCorrectToolForDrops(ModBlocks.HUSHSTONE.get().defaultBlockState()),
                "The crafted Handpick must unlock Hushstone"
        );

        ItemStack shards = craft(
                helper,
                "hushstone_shard",
                1,
                1,
                List.of(new ItemStack(ModItems.HUSHSTONE.get()))
        );
        assertOutput(helper, shards, ModItems.HUSHSTONE_SHARD.get(), 4, "Hushstone splitting");

        ItemStack knife = craft(
                helper,
                "bound_knife",
                2,
                2,
                List.of(
                        new ItemStack(ModItems.HUSHSTONE_SHARD.get()),
                        ItemStack.EMPTY,
                        new ItemStack(ModItems.RIBROOT_SPLINT.get()),
                        new ItemStack(ModItems.THREAD_BINDING.get())
                )
        );
        assertOutput(helper, knife, ModItems.BOUND_KNIFE.get(), 1, "Bound Knife recipe");

        ItemStack rebuiltHushstone = craft(
                helper,
                "hushstone_from_shards",
                2,
                2,
                List.of(
                        new ItemStack(ModItems.HUSHSTONE_SHARD.get()),
                        new ItemStack(ModItems.HUSHSTONE_SHARD.get()),
                        new ItemStack(ModItems.HUSHSTONE_SHARD.get()),
                        new ItemStack(ModItems.HUSHSTONE_SHARD.get())
                )
        );
        assertOutput(helper, rebuiltHushstone, ModItems.HUSHSTONE.get(), 1, "Hushstone rebuilding");
        helper.succeed();
    }

    private static CraftingRecipe recipe(GameTestHelper helper, String id) {
        RecipeHolder<?> holder = helper.getLevel()
                .getServer()
                .getRecipeManager()
                .byKey(Gravesown.id(id))
                .orElseThrow(() -> new IllegalStateException("Missing recipe " + Gravesown.id(id)));
        if (!(holder.value() instanceof CraftingRecipe craftingRecipe)) {
            throw new IllegalStateException(holder.id() + " is not a crafting recipe");
        }
        return craftingRecipe;
    }

    private static ItemStack craft(
            GameTestHelper helper,
            String recipeId,
            int width,
            int height,
            List<ItemStack> inputStacks
    ) {
        CraftingRecipe recipe = recipe(helper, recipeId);
        CraftingInput input = CraftingInput.of(width, height, inputStacks);
        helper.assertTrue(recipe.matches(input, helper.getLevel()), recipeId + " must match the intended input");
        return recipe.assemble(input, helper.getLevel().registryAccess());
    }

    private static void assertOutput(
            GameTestHelper helper,
            ItemStack output,
            Item expectedItem,
            int expectedCount,
            String context
    ) {
        helper.assertTrue(
                output.is(expectedItem) && output.getCount() == expectedCount,
                context + " produced " + output + " instead of " + expectedCount + " "
                        + BuiltInRegistries.ITEM.getKey(expectedItem)
        );
    }
}
