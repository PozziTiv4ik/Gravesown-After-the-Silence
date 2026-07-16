package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.blockentity.PitchKilnBlockEntity;
import dev.gravesown.menu.GraveworkMenu;
import dev.gravesown.menu.PitchKilnMenu;
import dev.gravesown.recipe.GraveworkRecipe;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModItems;
import dev.gravesown.registry.ModMenus;
import dev.gravesown.registry.ModRecipes;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Gravesown.MOD_ID)
@PrefixGameTestTemplate(false)
public final class EarlyStationsGameTests {
    private static final Map<String, Item> GRAVEWORK_OUTPUTS = outputs();

    private EarlyStationsGameTests() {
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 30)
    public static void graveworkMenuOwnsARealFourByFourServerGrid(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        GraveworkMenu menu = new GraveworkMenu(41, player.getInventory());
        helper.assertTrue(menu.getType() == ModMenus.GRAVEWORK.get(), "Gravework menu type must remain custom");
        helper.assertTrue(menu.slots.size() == 53, "Gravework must expose result + 16 craft + 36 player slots");
        for (int slot = 1; slot <= 16; slot++) {
            helper.assertTrue(menu.getSlot(slot).container != player.getInventory(),
                    "Every Gravework grid slot must belong to the 4x4 crafting container");
        }
        helper.assertTrue(
                Gravesown.id("gravework_bench").equals(BuiltInRegistries.BLOCK.getKey(ModBlocks.GRAVEWORK_BENCH.get())),
                "Gravework Bench block id must remain stable"
        );
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 30)
    public static void allEarlyToolsAndWaterGearRequireExactFourByFourRecipes(GameTestHelper helper) {
        for (Map.Entry<String, Item> entry : GRAVEWORK_OUTPUTS.entrySet()) {
            RecipeHolder<?> holder = helper.getLevel().getServer().getRecipeManager()
                    .byKey(Gravesown.id(entry.getKey()))
                    .orElseThrow(() -> new IllegalStateException("Missing Gravework recipe " + entry.getKey()));
            helper.assertTrue(holder.value() instanceof GraveworkRecipe,
                    entry.getKey() + " must use the Gravework serializer");
            GraveworkRecipe recipe = (GraveworkRecipe)holder.value();
            helper.assertTrue(recipe.getType() == ModRecipes.GRAVEWORK_TYPE.get(),
                    entry.getKey() + " must use the Gravework recipe type");
            helper.assertTrue(recipe.canCraftInDimensions(4, 4) && !recipe.canCraftInDimensions(3, 3),
                    entry.getKey() + " must require the 4x4 station");

            List<ItemStack> stacks = new ArrayList<>(16);
            for (Ingredient ingredient : recipe.getIngredients()) {
                ItemStack[] choices = ingredient.getItems();
                ItemStack chosen = choices.length == 0 ? ItemStack.EMPTY : choices[0].copyWithCount(1);
                if (!chosen.isEmpty()) {
                    helper.assertTrue(
                            Gravesown.MOD_ID.equals(BuiltInRegistries.ITEM.getKey(chosen.getItem()).getNamespace()),
                            entry.getKey() + " accepts foreign ingredient "
                                    + BuiltInRegistries.ITEM.getKey(chosen.getItem())
                    );
                }
                stacks.add(chosen);
            }
            CraftingInput input = CraftingInput.of(4, 4, stacks);
            helper.assertTrue(recipe.matches(input, helper.getLevel()), entry.getKey() + " must match its exact pattern");
            ItemStack output = recipe.assemble(input, helper.getLevel().registryAccess());
            helper.assertTrue(output.is(entry.getValue()), entry.getKey() + " produced the wrong item: " + output);
        }

        helper.assertTrue(new ItemStack(ModItems.HUSHSTONE_PICKAXE.get()).is(ItemTags.PICKAXES),
                "Hushstone Pickaxe must use the standard pickaxe tag");
        helper.assertTrue(new ItemStack(ModItems.HUSHSTONE_SHOVEL.get()).is(ItemTags.SHOVELS),
                "Hushstone Shovel must use the standard shovel tag");
        helper.assertTrue(new ItemStack(ModItems.HUSHSTONE_AXE.get()).is(ItemTags.AXES),
                "Hushstone Axe must use the standard axe tag");
        helper.assertTrue(new ItemStack(ModItems.HUSHSTONE_HOE.get()).is(ItemTags.HOES),
                "Hushstone Hoe must use the standard hoe tag");
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 30)
    public static void pitchKilnUsesItsOwnBlockEntityFuelAndReviewedSmeltingRecipes(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        PitchKilnMenu menu = new PitchKilnMenu(42, player.getInventory());
        helper.assertTrue(menu.getType() == ModMenus.PITCH_KILN.get(),
                "Pitch Kiln must use its dedicated menu type for the shared Gravesown screen");
        helper.assertTrue(menu.slots.size() == 39,
                "Pitch Kiln must retain the furnace three-slot and player-inventory contract");
        BlockPos position = new BlockPos(2, 1, 2);
        helper.setBlock(position, ModBlocks.PITCH_KILN.get());
        helper.assertTrue(
                helper.getLevel().getBlockEntity(helper.absolutePos(position)) instanceof PitchKilnBlockEntity,
                "Pitch Kiln must create its registered furnace block entity on the server"
        );
        helper.assertTrue(
                ModItems.GRAVE_TALLOW.get().getBurnTime(
                        new ItemStack(ModItems.GRAVE_TALLOW.get()),
                        RecipeType.SMELTING
                ) == 800,
                "Grave Tallow must fuel the Pitch Kiln for 800 ticks"
        );
        assertRecipeOutput(helper, "charred_grazer_meat", ModItems.CHARRED_GRAZER_MEAT.get());
        assertRecipeOutput(helper, "smoked_rotfin", ModItems.SMOKED_ROTFIN.get());
        helper.succeed();
    }

    private static void assertRecipeOutput(GameTestHelper helper, String id, Item expected) {
        RecipeHolder<?> holder = helper.getLevel().getServer().getRecipeManager()
                .byKey(Gravesown.id(id))
                .orElseThrow(() -> new IllegalStateException("Missing smelting recipe " + id));
        helper.assertTrue(holder.value().getType() == RecipeType.SMELTING,
                id + " must remain a standard data-driven smelting recipe");
        helper.assertTrue(holder.value().getResultItem(helper.getLevel().registryAccess()).is(expected),
                id + " must produce " + BuiltInRegistries.ITEM.getKey(expected));
    }

    private static Map<String, Item> outputs() {
        Map<String, Item> outputs = new LinkedHashMap<>();
        outputs.put("pitch_kiln", ModItems.PITCH_KILN.get());
        outputs.put("hushstone_pickaxe", ModItems.HUSHSTONE_PICKAXE.get());
        outputs.put("hushstone_shovel", ModItems.HUSHSTONE_SHOVEL.get());
        outputs.put("hushstone_axe", ModItems.HUSHSTONE_AXE.get());
        outputs.put("hushstone_hoe", ModItems.HUSHSTONE_HOE.get());
        outputs.put("gloamline_rod", ModItems.GLOAMLINE_ROD.get());
        outputs.put("gloam_skiff", ModItems.GLOAM_SKIFF.get());
        outputs.put("quietskin_hood", ModItems.QUIETSKIN_HOOD.get());
        outputs.put("quietskin_coat", ModItems.QUIETSKIN_COAT.get());
        outputs.put("quietskin_legwraps", ModItems.QUIETSKIN_LEGWRAPS.get());
        outputs.put("quietskin_boots", ModItems.QUIETSKIN_BOOTS.get());
        return Map.copyOf(outputs);
    }
}
