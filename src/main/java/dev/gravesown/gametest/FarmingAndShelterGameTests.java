package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModFluids;
import dev.gravesown.registry.ModItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Gravesown.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FarmingAndShelterGameTests {
    private FarmingAndShelterGameTests() {
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 30)
    public static void hushstoneHoeCreatesOnlyCustomFarmland(GameTestHelper helper) {
        BlockPos soil = new BlockPos(2, 1, 2);
        helper.setBlock(soil, ModBlocks.ASHEN_SOD.get());
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.HUSHSTONE_HOE.get()));
        BlockPos absolute = helper.absolutePos(soil);
        UseOnContext context = new UseOnContext(
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(absolute), Direction.UP, absolute, false)
        );
        ModItems.HUSHSTONE_HOE.get().useOn(context);
        helper.assertBlockPresent(ModBlocks.GLOAM_FARMLAND.get(), soil);
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void farmlandAcceptsGloamwaterAndRejectsVanillaWater(GameTestHelper helper) {
        BlockPos soil = helper.absolutePos(new BlockPos(2, 1, 2));
        var farmland = ModBlocks.GLOAM_FARMLAND.get();
        var state = farmland.defaultBlockState();
        helper.assertTrue(
                farmland.canBeHydrated(state, helper.getLevel(), soil,
                        ModFluids.GLOAMWATER.get().defaultFluidState(), soil.east()),
                "Gloam farmland must accept the registered custom fluid"
        );
        helper.assertTrue(
                !farmland.canBeHydrated(state, helper.getLevel(), soil,
                        Fluids.WATER.defaultFluidState(), soil.east()),
                "Gloam farmland must reject vanilla water"
        );
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 30)
    public static void gravebloomDustGrowsBothCustomCrops(GameTestHelper helper) {
        BlockPos soil = new BlockPos(2, 1, 2);
        BlockPos crop = soil.above();
        helper.setBlock(soil, ModBlocks.GLOAM_FARMLAND.get().defaultBlockState()
                .setValue(net.minecraft.world.level.block.FarmBlock.MOISTURE, 7));
        helper.setBlock(crop, ModBlocks.ASHGRAIN_CROP.get());
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.GRAVEBLOOM_DUST.get(), 2));
        BlockPos absolute = helper.absolutePos(crop);
        ModItems.GRAVEBLOOM_DUST.get().useOn(new UseOnContext(
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(absolute), Direction.UP, absolute, false)
        ));
        helper.assertTrue(
                helper.getBlockState(crop).getValue(net.minecraft.world.level.block.CropBlock.AGE) > 0,
                "Gravebloom Dust must accelerate Ashgrain"
        );
        helper.setBlock(crop, ModBlocks.MIREBEAN_CROP.get());
        ModItems.GRAVEBLOOM_DUST.get().useOn(new UseOnContext(
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(absolute), Direction.UP, absolute, false)
        ));
        helper.assertTrue(
                helper.getBlockState(crop).getValue(net.minecraft.world.level.block.CropBlock.AGE) > 0,
                "Gravebloom Dust must accelerate Mirebean"
        );
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void shelterAndFoodRecipesAreServerVisible(GameTestHelper helper) {
        List<String> recipeIds = List.of(
                "ribroot_stairs", "ribroot_slab", "ribroot_fence", "ribroot_fence_gate",
                "ribroot_door", "ribroot_trapdoor", "tallow_lantern",
                "emberbark_stairs", "emberbark_slab", "emberbark_fence", "emberbark_fence_gate",
                "emberbark_door", "emberbark_trapdoor",
                "palevine_stairs", "palevine_slab", "palevine_fence", "palevine_fence_gate",
                "palevine_door", "palevine_trapdoor",
                "ashgrain_seeds", "mirebean_seeds", "ashgrain_loaf", "field_ration"
        );
        for (String id : recipeIds) {
            helper.assertTrue(
                    helper.getLevel().getServer().getRecipeManager().byKey(Gravesown.id(id)).isPresent(),
                    "Missing reviewed recipe " + id
            );
        }
        helper.succeed();
    }
}
