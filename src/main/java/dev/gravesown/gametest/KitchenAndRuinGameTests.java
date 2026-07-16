package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.block.ReliquaryCrateBlock;
import dev.gravesown.blockentity.FieldKitchenBlockEntity;
import dev.gravesown.blockentity.ReliquaryCrateBlockEntity;
import dev.gravesown.menu.FieldKitchenMenu;
import dev.gravesown.recipe.FieldKitchenRecipe;
import dev.gravesown.recipe.FieldKitchenRecipeInput;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModItems;
import dev.gravesown.registry.ModMenus;
import dev.gravesown.registry.ModRecipes;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Gravesown.MOD_ID)
@PrefixGameTestTemplate(false)
public final class KitchenAndRuinGameTests {
    private KitchenAndRuinGameTests() {
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 30)
    public static void fieldKitchenOwnsItsServerRecipeAndSixSlotMenu(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        FieldKitchenMenu menu = new FieldKitchenMenu(71, player.getInventory());
        helper.assertTrue(menu.getType() == ModMenus.FIELD_KITCHEN.get(),
                "Field Kitchen must use its dedicated menu type");
        helper.assertTrue(menu.slots.size() == 42,
                "Field Kitchen must expose five inputs, one result and 36 player slots");

        List<String> ids = List.of("mirebean_stew", "charred_marrow_pot", "gloam_chowder");
        for (String id : ids) {
            RecipeHolder<?> holder = helper.getLevel().getServer().getRecipeManager()
                    .byKey(Gravesown.id(id))
                    .orElseThrow(() -> new IllegalStateException("Missing Field Kitchen recipe " + id));
            helper.assertTrue(holder.value() instanceof FieldKitchenRecipe,
                    id + " must use the Field Kitchen serializer");
            helper.assertTrue(holder.value().getType() == ModRecipes.FIELD_KITCHEN_TYPE.get(),
                    id + " must use the Field Kitchen recipe type");
        }
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 30)
    public static void fieldKitchenConsumesFoodReturnsBucketAndWearsUtensils(GameTestHelper helper) {
        BlockPos position = new BlockPos(2, 1, 2);
        helper.setBlock(position, ModBlocks.FIELD_KITCHEN.get());
        helper.assertTrue(
                helper.getLevel().getBlockEntity(helper.absolutePos(position)) instanceof FieldKitchenBlockEntity,
                "Placed Field Kitchen must create its registered server block entity"
        );
        FieldKitchenBlockEntity kitchen = (FieldKitchenBlockEntity)helper.getLevel()
                .getBlockEntity(helper.absolutePos(position));
        kitchen.setItem(0, new ItemStack(ModItems.MIREBEAN.get()));
        kitchen.setItem(1, new ItemStack(ModItems.ASHGRAIN.get()));
        kitchen.setItem(2, new ItemStack(ModItems.GLOAMWATER_BUCKET.get()));
        kitchen.setItem(3, new ItemStack(ModItems.BONE_CLEAVER.get()));
        kitchen.setItem(4, new ItemStack(ModItems.STIRRING_HOOK.get()));
        kitchen.updateResult();

        helper.assertTrue(kitchen.getItem(FieldKitchenBlockEntity.RESULT_SLOT).is(ModItems.MIREBEAN_STEW.get()),
                "Reviewed ingredient order must prepare Mirebean Stew");
        helper.assertTrue(kitchen.getItem(FieldKitchenBlockEntity.RESULT_SLOT).getCount() == 2,
                "One Field Kitchen process must return two meal portions");

        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        kitchen.takeResult(player);
        helper.assertTrue(kitchen.getItem(0).isEmpty() && kitchen.getItem(1).isEmpty(),
                "Food ingredients must be consumed server-side");
        helper.assertTrue(kitchen.getItem(2).is(Items.BUCKET),
                "Gloamwater Bucket must return a reusable empty bucket");
        helper.assertTrue(kitchen.getItem(3).getDamageValue() == 1 && kitchen.getItem(4).getDamageValue() == 1,
                "Both persistent utensils must wear by exactly one durability point");
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void reliquaryCrateHasExactlyOneExtraStorageRow(GameTestHelper helper) {
        BlockPos position = new BlockPos(2, 1, 2);
        helper.setBlock(position, ModBlocks.RELIQUARY_CRATE.get());
        helper.assertTrue(
                helper.getLevel().getBlockEntity(helper.absolutePos(position)) instanceof ReliquaryCrateBlockEntity,
                "Reliquary Crate must create its registered server block entity"
        );
        ReliquaryCrateBlockEntity crate = (ReliquaryCrateBlockEntity)helper.getLevel()
                .getBlockEntity(helper.absolutePos(position));
        helper.assertTrue(ReliquaryCrateBlockEntity.ROWS == 4 && crate.getContainerSize() == 36,
                "Reliquary Crate must provide four rows / 36 slots");
        helper.assertTrue(ModBlocks.RELIQUARY_CRATE.get().getStateDefinition().getPossibleStates().stream()
                        .map(state -> state.getValue(ReliquaryCrateBlock.FACING))
                        .collect(java.util.stream.Collectors.toSet())
                        .containsAll(List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)),
                "Reliquary Crate must retain all four horizontal placement orientations");
        ServerPlayer opener = helper.makeMockServerPlayerInLevel();
        crate.startOpen(opener);
        helper.assertTrue(crate.getOpenerCount() == 1,
                "Opening the Reliquary Crate must enter its server-side open/sound lifecycle");
        crate.stopOpen(opener);
        helper.assertTrue(crate.getOpenerCount() == 0,
                "Closing the Reliquary Crate must enter its server-side close/sound lifecycle");
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void fishingCatchIsExclusiveAndKitchenInputRejectsWrongOrder(GameTestHelper helper) {
        helper.assertTrue(ModItems.NEEDLE_SPRAT.get().getFoodProperties(
                        new ItemStack(ModItems.NEEDLE_SPRAT.get()), null) != null,
                "Needle Sprat must remain an edible fishing-only survival catch");
        FieldKitchenRecipeInput wrongOrder = new FieldKitchenRecipeInput(List.of(
                new ItemStack(ModItems.ASHGRAIN.get()),
                new ItemStack(ModItems.MIREBEAN.get()),
                new ItemStack(ModItems.GLOAMWATER_BUCKET.get()),
                new ItemStack(ModItems.BONE_CLEAVER.get()),
                new ItemStack(ModItems.STIRRING_HOOK.get())
        ));
        helper.assertTrue(helper.getLevel().getRecipeManager()
                        .getRecipeFor(ModRecipes.FIELD_KITCHEN_TYPE.get(), wrongOrder, helper.getLevel())
                        .isEmpty(),
                "Field Kitchen recipes must reject swapped ingredient order");
        helper.succeed();
    }
}
