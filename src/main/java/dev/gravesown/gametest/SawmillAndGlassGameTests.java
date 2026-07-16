package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.block.SawmillBlock;
import dev.gravesown.menu.SawmillMenu;
import dev.gravesown.recipe.GraveworkRecipe;
import dev.gravesown.recipe.SawmillRecipe;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModItems;
import dev.gravesown.registry.ModMenus;
import dev.gravesown.registry.ModRecipes;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Gravesown.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SawmillAndGlassGameTests {
    private static final TagKey<Block> CUT_PLANK_BLOCKS = TagKey.create(
            Registries.BLOCK,
            Gravesown.id("cut_planks")
    );
    private static final TagKey<Item> CUT_PLANK_ITEMS = TagKey.create(
            Registries.ITEM,
            Gravesown.id("cut_planks")
    );
    private static final TagKey<Block> GLASS_BLOCKS = TagKey.create(
            Registries.BLOCK,
            Gravesown.id("glass")
    );
    private static final TagKey<Item> GLASS_ITEMS = TagKey.create(
            Registries.ITEM,
            Gravesown.id("glass")
    );

    private SawmillAndGlassGameTests() {
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 30)
    public static void sawmillOwnsDedicatedServerMenuAndTaggedBlocks(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        SawmillMenu menu = new SawmillMenu(71, player.getInventory());
        helper.assertTrue(menu.getType() == ModMenus.SAWMILL.get(),
                "Sawmill must retain its dedicated menu type");
        helper.assertTrue(menu.slots.size() == 38,
                "Sawmill must expose one input, one result and 36 player slots");
        helper.assertTrue(
                Gravesown.id("sawmill").equals(BuiltInRegistries.BLOCK.getKey(ModBlocks.SAWMILL.get())),
                "Sawmill block id must remain stable"
        );

        for (SawmillCase family : sawmillCases()) {
            assertCutPlank(helper, family);
        }
        helper.assertTrue(ModBlocks.SAWMILL.get().defaultBlockState().is(BlockTags.MINEABLE_WITH_AXE),
                "Sawmill must be axe-mineable");
        helper.assertTrue(ModBlocks.SAWMILL.get().getStateDefinition().getPossibleStates().stream()
                        .map(state -> state.getValue(SawmillBlock.FACING))
                        .collect(java.util.stream.Collectors.toSet())
                        .containsAll(List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)),
                "Sawmill must retain all four horizontal placement orientations");
        assertSingleDrop(helper, ModBlocks.SAWMILL.get(), ModItems.SAWMILL.get(), ItemStack.EMPTY);
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 30)
    public static void sawmillRecipesAreExactOneToOneAndConsumeOnServer(GameTestHelper helper) {
        List<SawmillCase> families = sawmillCases();
        for (SawmillCase family : families) {
            assertSawmillRecipe(helper, family, families);
        }

        RecipeHolder<?> station = recipe(helper, "sawmill");
        helper.assertTrue(station.value() instanceof GraveworkRecipe,
                "Sawmill station must be constructed at the 4x4 Gravework Bench");

        BlockPos position = new BlockPos(2, 1, 2);
        helper.setBlock(position, ModBlocks.SAWMILL.get());
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        for (int index = 0; index < families.size(); index++) {
            SawmillCase family = families.get(index);
            SawmillMenu menu = new SawmillMenu(
                    72 + index,
                    player.getInventory(),
                    ContainerLevelAccess.create(helper.getLevel(), helper.absolutePos(position))
            );
            menu.getSlot(0).set(new ItemStack(family.basePlanks()));
            menu.slotsChanged(menu.getSlot(0).container);
            helper.assertTrue(menu.getSlot(1).getItem().is(family.cutPlanksItem()),
                    family.id() + " must calculate its species-matched Sawmill result on the server");
            ItemStack taken = menu.getSlot(1).remove(1);
            menu.getSlot(1).onTake(player, taken);
            helper.assertTrue(menu.getSlot(0).getItem().isEmpty(),
                    family.id() + " Sawmill result must consume exactly one server input");
            helper.assertTrue(taken.is(family.cutPlanksItem()) && taken.getCount() == 1,
                    family.id() + " Sawmill result must contain exactly one cut plank");
        }
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 30)
    public static void kilnGlassProgressionHasReviewedStrengthAndDropSemantics(GameTestHelper helper) {
        assertSmeltingRecipe(
                helper,
                "gravesown_glass",
                ModItems.GLOAM_SAND.get(),
                ModItems.GRAVESOWN_GLASS.get(),
                Items.SAND
        );
        assertSmeltingRecipe(
                helper,
                "tempered_glass",
                ModItems.GRAVESOWN_GLASS.get(),
                ModItems.TEMPERED_GLASS.get(),
                ModItems.GLOAM_SAND.get()
        );

        Block ordinary = ModBlocks.GRAVESOWN_GLASS.get();
        Block tempered = ModBlocks.TEMPERED_GLASS.get();
        helper.assertTrue(BuiltInRegistries.BLOCK.getKey(ordinary).equals(Gravesown.id("gravesown_glass"))
                        && BuiltInRegistries.ITEM.getKey(ModItems.GRAVESOWN_GLASS.get())
                        .equals(Gravesown.id("gravesown_glass")),
                "Gloam Glass block and item ids must remain stable");
        helper.assertTrue(BuiltInRegistries.BLOCK.getKey(tempered).equals(Gravesown.id("tempered_glass"))
                        && BuiltInRegistries.ITEM.getKey(ModItems.TEMPERED_GLASS.get())
                        .equals(Gravesown.id("tempered_glass")),
                "Tempered Gloam Glass block and item ids must remain stable");
        helper.assertTrue(ordinary.defaultBlockState().is(GLASS_BLOCKS),
                "Gloam Glass must be in the Gravesown glass block tag");
        helper.assertTrue(tempered.defaultBlockState().is(GLASS_BLOCKS),
                "Tempered Gloam Glass must be in the Gravesown glass block tag");
        helper.assertTrue(new ItemStack(ModItems.GRAVESOWN_GLASS.get()).is(GLASS_ITEMS),
                "Gloam Glass item must be in the Gravesown glass item tag");
        helper.assertTrue(new ItemStack(ModItems.TEMPERED_GLASS.get()).is(GLASS_ITEMS),
                "Tempered Gloam Glass item must be in the Gravesown glass item tag");
        helper.assertTrue(Math.abs(ordinary.defaultDestroyTime() - 0.3F) < 1.0E-6F,
                "Ordinary Gloam Glass must keep its reviewed hardness");
        helper.assertTrue(Math.abs(tempered.defaultDestroyTime() - ordinary.defaultDestroyTime() * 3.0F) < 1.0E-6F,
                "Tempered glass hardness must be exactly three times ordinary glass");
        helper.assertTrue(Math.abs(tempered.getExplosionResistance() - ordinary.getExplosionResistance() * 3.0F)
                        < 1.0E-6F,
                "Tempered glass blast resistance must be exactly three times ordinary glass");

        helper.assertTrue(drops(helper, ordinary, ItemStack.EMPTY).isEmpty(),
                "Ordinary Gloam Glass must not self-drop without Silk Touch");
        ItemStack silkTouchTool = new ItemStack(Items.DIAMOND_PICKAXE);
        Holder<Enchantment> silkTouch = helper.getLevel().registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.SILK_TOUCH);
        silkTouchTool.enchant(silkTouch, 1);
        assertSingleDrop(helper, ordinary, ModItems.GRAVESOWN_GLASS.get(), silkTouchTool);
        assertSingleDrop(helper, tempered, ModItems.TEMPERED_GLASS.get(), ItemStack.EMPTY);
        helper.succeed();
    }

    private static void assertCutPlank(GameTestHelper helper, SawmillCase family) {
        Block block = family.cutPlanksBlock();
        Item item = family.cutPlanksItem();
        helper.assertTrue(BuiltInRegistries.BLOCK.getKey(block).equals(Gravesown.id(family.id() + "_cut_planks"))
                        && BuiltInRegistries.ITEM.getKey(item).equals(Gravesown.id(family.id() + "_cut_planks")),
                family.id() + " cut-plank block and item ids must remain stable");
        helper.assertTrue(item instanceof BlockItem blockItem && blockItem.getBlock() == block,
                family.id() + " cut-plank item must place its matching block");
        helper.assertTrue(block.defaultBlockState().is(CUT_PLANK_BLOCKS),
                BuiltInRegistries.BLOCK.getKey(block) + " must use the cut-plank block tag");
        helper.assertTrue(block.defaultBlockState().is(BlockTags.PLANKS),
                BuiltInRegistries.BLOCK.getKey(block) + " must remain a general plank");
        helper.assertTrue(block.defaultBlockState().is(BlockTags.MINEABLE_WITH_AXE),
                BuiltInRegistries.BLOCK.getKey(block) + " must be axe-mineable");
        helper.assertTrue(new ItemStack(item).is(CUT_PLANK_ITEMS),
                BuiltInRegistries.ITEM.getKey(item) + " must use the cut-plank item tag");
        helper.assertTrue(new ItemStack(item).is(ItemTags.PLANKS),
                BuiltInRegistries.ITEM.getKey(item) + " must remain a general plank item");
        assertSingleDrop(helper, block, item, ItemStack.EMPTY);
    }

    private static void assertSawmillRecipe(
            GameTestHelper helper,
            SawmillCase family,
            List<SawmillCase> allFamilies
    ) {
        String recipeId = family.id() + "_cut_planks_from_sawmill";
        RecipeHolder<?> holder = recipe(helper, recipeId);
        helper.assertTrue(holder.value() instanceof SawmillRecipe,
                recipeId + " must use the dedicated Sawmill serializer");
        SawmillRecipe recipe = (SawmillRecipe)holder.value();
        helper.assertTrue(recipe.getType() == ModRecipes.SAWMILL_TYPE.get(),
                recipeId + " must use the dedicated Sawmill recipe type");
        helper.assertTrue(recipe.getIngredients().size() == 1
                        && recipe.getIngredients().getFirst().test(new ItemStack(family.basePlanks())),
                recipeId + " must accept exactly its own base planks");
        for (SawmillCase other : allFamilies) {
            if (other != family) {
                helper.assertTrue(
                        !recipe.getIngredients().getFirst().test(new ItemStack(other.basePlanks())),
                        recipeId + " must reject " + other.id() + " planks"
                );
            }
        }
        ItemStack result = recipe.getResultItem(helper.getLevel().registryAccess());
        helper.assertTrue(result.is(family.cutPlanksItem()) && result.getCount() == 1,
                recipeId + " must produce exactly one species-matched cut plank");
    }

    private static void assertSmeltingRecipe(
            GameTestHelper helper,
            String id,
            Item input,
            Item expected,
            Item rejectedInput
    ) {
        RecipeHolder<?> holder = recipe(helper, id);
        helper.assertTrue(holder.value().getType() == RecipeType.SMELTING,
                id + " must remain a standard Pitch Kiln-compatible smelting recipe");
        helper.assertTrue(holder.value().getIngredients().size() == 1
                        && holder.value().getIngredients().getFirst().test(new ItemStack(input)),
                id + " must accept exactly the reviewed previous material tier");
        helper.assertTrue(!holder.value().getIngredients().getFirst().test(new ItemStack(rejectedInput)),
                id + " must reject the wrong or vanilla material tier");
        ItemStack result = holder.value().getResultItem(helper.getLevel().registryAccess());
        helper.assertTrue(result.is(expected) && result.getCount() == 1,
                id + " produced the wrong glass tier");
    }

    private static List<SawmillCase> sawmillCases() {
        return List.of(
                new SawmillCase("ribroot", ModItems.RIBROOT_PLANKS.get(),
                        ModBlocks.RIBROOT_CUT_PLANKS.get(), ModItems.RIBROOT_CUT_PLANKS.get()),
                new SawmillCase("emberbark", ModItems.EMBERBARK_PLANKS.get(),
                        ModBlocks.EMBERBARK_CUT_PLANKS.get(), ModItems.EMBERBARK_CUT_PLANKS.get()),
                new SawmillCase("palevine", ModItems.PALEVINE_PLANKS.get(),
                        ModBlocks.PALEVINE_CUT_PLANKS.get(), ModItems.PALEVINE_CUT_PLANKS.get()),
                new SawmillCase("cairnwood", ModItems.CAIRNWOOD_PLANKS.get(),
                        ModBlocks.CAIRNWOOD_CUT_PLANKS.get(), ModItems.CAIRNWOOD_CUT_PLANKS.get()),
                new SawmillCase("suturewood", ModItems.SUTUREWOOD_PLANKS.get(),
                        ModBlocks.SUTUREWOOD_CUT_PLANKS.get(), ModItems.SUTUREWOOD_CUT_PLANKS.get()),
                new SawmillCase("mosswake", ModItems.MOSSWAKE_PLANKS.get(),
                        ModBlocks.MOSSWAKE_CUT_PLANKS.get(), ModItems.MOSSWAKE_CUT_PLANKS.get()),
                new SawmillCase("sunveil", ModItems.SUNVEIL_PLANKS.get(),
                        ModBlocks.SUNVEIL_CUT_PLANKS.get(), ModItems.SUNVEIL_CUT_PLANKS.get())
        );
    }

    private static RecipeHolder<?> recipe(GameTestHelper helper, String id) {
        return helper.getLevel().getServer().getRecipeManager()
                .byKey(Gravesown.id(id))
                .orElseThrow(() -> new IllegalStateException("Missing recipe " + id));
    }

    private static void assertSingleDrop(
            GameTestHelper helper,
            Block block,
            Item expected,
            ItemStack tool
    ) {
        List<ItemStack> drops = drops(helper, block, tool);
        helper.assertTrue(drops.size() == 1 && drops.getFirst().is(expected) && drops.getFirst().getCount() == 1,
                BuiltInRegistries.BLOCK.getKey(block) + " must drop exactly one matching block item");
    }

    private static List<ItemStack> drops(GameTestHelper helper, Block block, ItemStack tool) {
        LootTable table = helper.getLevel().getServer().reloadableRegistries().getLootTable(block.getLootTable());
        LootParams params = new LootParams.Builder(helper.getLevel())
                .withParameter(LootContextParams.BLOCK_STATE, block.defaultBlockState())
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(BlockPos.ZERO))
                .withParameter(LootContextParams.TOOL, tool)
                .create(LootContextParamSets.BLOCK);
        return table.getRandomItems(params, 1L);
    }

    private record SawmillCase(String id, Item basePlanks, Block cutPlanksBlock, Item cutPlanksItem) {
    }
}
