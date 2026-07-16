package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.entity.Rotfin;
import dev.gravesown.entity.Rootskimmer;
import dev.gravesown.entity.SiltRay;
import dev.gravesown.entity.Veilfin;
import dev.gravesown.entity.GloamSkiff;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModEntities;
import dev.gravesown.registry.ModFeatures;
import dev.gravesown.registry.ModFluids;
import dev.gravesown.registry.ModItems;
import dev.gravesown.worldgen.GloamSeaGrowthFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@GameTestHolder(Gravesown.MOD_ID)
@PrefixGameTestTemplate(false)
public final class AquaticGameTests {
    private AquaticGameTests() {
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void gloamwaterFamilyKeepsItsRegistryAndSourceContract(GameTestHelper helper) {
        helper.assertTrue(
                Gravesown.id("gloamwater").equals(NeoForgeRegistries.FLUID_TYPES.getKey(ModFluids.GLOAMWATER_TYPE.get())),
                "Gloamwater FluidType id must remain stable"
        );
        helper.assertTrue(
                Gravesown.id("gloamwater").equals(BuiltInRegistries.FLUID.getKey(ModFluids.GLOAMWATER.get())),
                "Gloamwater source id must remain stable"
        );
        helper.assertTrue(
                Gravesown.id("flowing_gloamwater").equals(
                        BuiltInRegistries.FLUID.getKey(ModFluids.FLOWING_GLOAMWATER.get())
                ),
                "Flowing Gloamwater id must remain stable"
        );
        helper.assertTrue(
                Gravesown.id("gloamwater").equals(BuiltInRegistries.BLOCK.getKey(ModBlocks.GLOAMWATER.get())),
                "Gloamwater liquid block must remain in the Gravesown namespace"
        );
        helper.assertTrue(
                Gravesown.id("gloamwater_bucket").equals(BuiltInRegistries.ITEM.getKey(ModItems.GLOAMWATER_BUCKET.get())),
                "Gloamwater bucket id must remain stable"
        );
        var state = ModBlocks.GLOAMWATER.get().defaultBlockState();
        helper.assertTrue(state.getValue(LiquidBlock.LEVEL) == 0, "Gloamwater's default block must be a source");
        helper.assertTrue(state.getFluidState().isSource(), "Gloamwater's default fluid state must be a source");
        helper.assertTrue(
                ModFluids.GLOAMWATER.get().isSame(state.getFluidState().getType()),
                "Gloamwater block must expose the registered fluid family"
        );
        helper.succeed();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void threadkelpRetainsGloamwaterAndPondFeatureContract(GameTestHelper helper) {
        BlockPos floor = new BlockPos(1, 1, 1);
        BlockPos plant = floor.above();
        helper.setBlock(floor, ModBlocks.SUTURE_SILT.get());
        helper.setBlock(plant, ModBlocks.THREADKELP.get());
        helper.assertTrue(
                ModBlocks.THREADKELP.get().defaultBlockState().getFluidState().isSource(),
                "Threadkelp must preserve a full source fluid state"
        );
        helper.assertTrue(
                ModBlocks.THREADKELP.get().defaultBlockState().canSurvive(helper.getLevel(), helper.absolutePos(plant)),
                "Threadkelp must survive on Suture Silt while submerged"
        );
        helper.assertTrue(
                Gravesown.id("gloamwater_pond").equals(BuiltInRegistries.FEATURE.getKey(ModFeatures.GLOAMWATER_POND.get())),
                "The custom pond feature id must remain stable"
        );
        helper.assertTrue(
                Gravesown.id("gloam_sea_growth").equals(
                        BuiltInRegistries.FEATURE.getKey(ModFeatures.GLOAM_SEA_GROWTH.get())
                ),
                "The Gloam Sea growth feature id must remain stable"
        );
        helper.setBlock(floor, ModBlocks.ABYSSAL_SILT.get());
        for (var growth : new net.minecraft.world.level.block.Block[] {
                ModBlocks.VEILWEED.get(),
                ModBlocks.DROWNED_ROOTS.get(),
                ModBlocks.BLADDERPOD.get(),
                ModBlocks.LUMEN_KELP.get()
        }) {
            helper.setBlock(plant, growth);
            helper.assertTrue(
                    growth.defaultBlockState().canSurvive(helper.getLevel(), helper.absolutePos(plant)),
                    BuiltInRegistries.BLOCK.getKey(growth) + " must survive on the custom ocean floor"
            );
        }
        helper.succeed();
    }

    @GameTest(template = "gloamwater_pond_platform", timeoutTicks = 80)
    public static void realPondFeaturePlacesOnlyReviewedContentAndRemainsStable(GameTestHelper helper) {
        BlockPos origin = helper.absolutePos(new BlockPos(5, 1, 5));
        for (int dz = -4; dz <= 4; dz++) {
            for (int dx = -4; dx <= 4; dx++) {
                helper.getLevel().setBlockAndUpdate(origin.offset(dx, -1, dz), ModBlocks.SUTURE_SILT.get().defaultBlockState());
                for (int y = 0; y <= 3; y++) {
                    helper.getLevel().setBlockAndUpdate(origin.offset(dx, y, dz), Blocks.AIR.defaultBlockState());
                }
            }
        }
        int originalSurfaceY = origin.getY();
        boolean placed = ModFeatures.GLOAMWATER_POND.get().placeReviewedBasinForTest(
                helper.getLevel(),
                origin,
                originalSurfaceY
        );
        helper.assertTrue(placed, "The real Gloamwater pond feature must place on its reviewed Mire foundation");

        int waterY = originalSurfaceY - 1;
        int fluidBlocks = 0;
        int plants = 0;
        for (int dz = -4; dz <= 4; dz++) {
            for (int dx = -4; dx <= 4; dx++) {
                if (!isPondColumn(dx, dz)) {
                    continue;
                }
                int depth = dx * dx + dz * dz <= 8 ? 2 : 1;
                for (int y = waterY - depth; y <= waterY + 2; y++) {
                    var state = helper.getLevel().getBlockState(origin.offset(dx, y - origin.getY(), dz));
                    var block = state.getBlock();
                    helper.assertTrue(
                            block == ModBlocks.GLOAMWATER.get()
                                    || block == ModBlocks.THREADKELP.get()
                                    || block == ModBlocks.VEILWEED.get()
                                    || block == ModBlocks.DROWNED_ROOTS.get()
                                    || block == ModBlocks.BLADDERPOD.get()
                                    || block == ModBlocks.LUMEN_KELP.get()
                                    || block == ModBlocks.SUTURE_SILT.get()
                                    || block == ModBlocks.GLOAM_MUCK.get()
                                    || block == ModBlocks.GLOAM_SAND.get()
                                    || block == Blocks.AIR,
                            "Pond feature introduced foreign content: " + BuiltInRegistries.BLOCK.getKey(block)
                    );
                    if (ModFluids.GLOAMWATER.get().isSame(state.getFluidState().getType())) {
                        fluidBlocks++;
                    }
                    if (isAquaticPlant(block)) {
                        plants++;
                    }
                }
            }
        }
        helper.assertTrue(fluidBlocks >= 30, "The pond must contain a readable body of Gloamwater");
        helper.assertTrue(
                plants == 0,
                "The basin feature must leave planting to the shared 4x4 ecology grid instead of adding a second carpet"
        );

        helper.runAtTickTime(5, () -> {
            int stableSources = 0;
            int stablePlants = 0;
            for (int dz = -4; dz <= 4; dz++) {
                for (int dx = -4; dx <= 4; dx++) {
                    if (!isPondColumn(dx, dz)) {
                        continue;
                    }
                    for (int y = waterY - 2; y <= waterY; y++) {
                        var state = helper.getLevel().getBlockState(origin.offset(dx, y - origin.getY(), dz));
                        if (state.getFluidState().isSource()
                                && ModFluids.GLOAMWATER.get().isSame(state.getFluidState().getType())) {
                            stableSources++;
                        }
                        if (isAquaticPlant(state.getBlock())) {
                            stablePlants++;
                        }
                    }
                }
            }
            helper.assertTrue(stableSources >= 30, "Gloamwater sources must survive scheduled fluid ticks");
            helper.assertTrue(stablePlants == 0, "The basin feature must not add a second vegetation pass");
            helper.succeed();
        });
    }

    @GameTest(template = "gloamwater_pond_platform", timeoutTicks = 60)
    public static void seaGrowthCoversEveryFourByFourCellOfItsOwningChunk(GameTestHelper helper) {
        assertOrganicBedDistribution(helper);
        BlockPos reference = helper.absolutePos(new BlockPos(5, 1, 5));
        int chunkMinX = SectionPos.blockToSectionCoord(reference.getX()) << 4;
        int chunkMinZ = SectionPos.blockToSectionCoord(reference.getZ()) << 4;
        int floorY = reference.getY();
        var ordinaryBiomeFloors = new net.minecraft.world.level.block.Block[] {
                ModBlocks.ASHEN_SOD.get(),
                ModBlocks.GRAVE_LOAM.get(),
                ModBlocks.HUSHSTONE.get(),
                ModBlocks.DEEP_HUSHSTONE.get(),
                ModBlocks.ROOTFELT.get(),
                ModBlocks.FIBROUS_LOAM.get(),
                ModBlocks.SCAR_SHALE.get(),
                ModBlocks.MARROWSTONE.get(),
                ModBlocks.SUTURE_SILT.get(),
                ModBlocks.DRIED_ICHOR.get()
        };
        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                helper.getLevel().setBlockAndUpdate(
                        new BlockPos(chunkMinX + x, floorY, chunkMinZ + z),
                        ordinaryBiomeFloors[(x + z * 3) % ordinaryBiomeFloors.length].defaultBlockState()
                );
                helper.getLevel().setBlockAndUpdate(
                        new BlockPos(chunkMinX + x, floorY + 1, chunkMinZ + z),
                        ModBlocks.GLOAMWATER.get().defaultBlockState()
                );
                helper.getLevel().setBlockAndUpdate(
                        new BlockPos(chunkMinX + x, floorY + 2, chunkMinZ + z),
                        ModBlocks.GLOAMWATER.get().defaultBlockState()
                );
            }
        }

        int placed = ModFeatures.GLOAM_SEA_GROWTH.get().placeReviewedChunkGardenForTest(
                helper.getLevel(),
                RandomSource.create(0x6A7264656EL),
                reference,
                floorY
        );
        helper.assertTrue(placed == 16, "The sea-growth feature must place exactly one plant per 4x4 cell");

        boolean[][] firstBedWasMuck = new boolean[16][16];
        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                var floor = helper.getLevel().getBlockState(new BlockPos(chunkMinX + x, floorY, chunkMinZ + z));
                helper.assertTrue(
                        floor.is(ModBlocks.GLOAM_SAND.get()) || floor.is(ModBlocks.GLOAM_MUCK.get()),
                        "Every natural Gloamwater column must receive a reviewed aquatic floor"
                );
                firstBedWasMuck[z][x] = floor.is(ModBlocks.GLOAM_MUCK.get());
            }
        }

        for (int cellZ = 0; cellZ < 4; cellZ++) {
            for (int cellX = 0; cellX < 4; cellX++) {
                int plants = 0;
                for (int z = 0; z < 4; z++) {
                    for (int x = 0; x < 4; x++) {
                        var block = helper.getLevel().getBlockState(new BlockPos(
                                chunkMinX + cellX * 4 + x,
                                floorY + 1,
                                chunkMinZ + cellZ * 4 + z
                        )).getBlock();
                        if (isAquaticPlant(block)) {
                            plants++;
                        }
                    }
                }
                helper.assertTrue(plants == 1, "Every 4x4 water-bed cell must contain exactly one reviewed plant");
            }
        }

        // Bed material is a pure world-coordinate decision: replaying the same
        // chunk with a different feature RNG may change plant choices, never the
        // underlying Sand/Muck shelves.
        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                helper.getLevel().setBlockAndUpdate(
                        new BlockPos(chunkMinX + x, floorY, chunkMinZ + z),
                        ordinaryBiomeFloors[(x + z * 3) % ordinaryBiomeFloors.length].defaultBlockState()
                );
                helper.getLevel().setBlockAndUpdate(
                        new BlockPos(chunkMinX + x, floorY + 1, chunkMinZ + z),
                        ModBlocks.GLOAMWATER.get().defaultBlockState()
                );
                helper.getLevel().setBlockAndUpdate(
                        new BlockPos(chunkMinX + x, floorY + 2, chunkMinZ + z),
                        ModBlocks.GLOAMWATER.get().defaultBlockState()
                );
            }
        }
        int replayPlaced = ModFeatures.GLOAM_SEA_GROWTH.get().placeReviewedChunkGardenForTest(
                helper.getLevel(),
                RandomSource.create(0x6D756C7469736361L),
                reference,
                floorY
        );
        helper.assertTrue(replayPlaced == 16, "A replayed full water chunk must still cover all ecology cells");
        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                boolean replayMuck = helper.getLevel()
                        .getBlockState(new BlockPos(chunkMinX + x, floorY, chunkMinZ + z))
                        .is(ModBlocks.GLOAM_MUCK.get());
                helper.assertTrue(
                        replayMuck == firstBedWasMuck[z][x],
                        "Aquatic bed shelves must not depend on feature RNG or placement order"
                );
            }
        }

        // A shoreline cell must not stay empty merely because its first random
        // coordinate is dry. Keep only one water column at a corner that the old
        // +1/+2 candidate logic could never reach, then prove all sixteen cells
        // still receive one plant.
        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                helper.getLevel().setBlockAndUpdate(
                        new BlockPos(chunkMinX + x, floorY, chunkMinZ + z),
                        ordinaryBiomeFloors[(x + z * 3) % ordinaryBiomeFloors.length].defaultBlockState()
                );
                helper.getLevel().setBlockAndUpdate(
                        new BlockPos(chunkMinX + x, floorY + 1, chunkMinZ + z),
                        Blocks.AIR.defaultBlockState()
                );
                helper.getLevel().setBlockAndUpdate(
                        new BlockPos(chunkMinX + x, floorY + 2, chunkMinZ + z),
                        Blocks.AIR.defaultBlockState()
                );
            }
        }
        for (int cellZ = 0; cellZ < 4; cellZ++) {
            for (int cellX = 0; cellX < 4; cellX++) {
                int x = chunkMinX + cellX * 4;
                int z = chunkMinZ + cellZ * 4;
                helper.getLevel().setBlockAndUpdate(
                        new BlockPos(x, floorY + 1, z),
                        ModBlocks.GLOAMWATER.get().defaultBlockState()
                );
                helper.getLevel().setBlockAndUpdate(
                        new BlockPos(x, floorY + 2, z),
                        ModBlocks.GLOAMWATER.get().defaultBlockState()
                );
            }
        }

        int shorelinePlaced = ModFeatures.GLOAM_SEA_GROWTH.get().placeReviewedChunkGardenForTest(
                helper.getLevel(),
                RandomSource.create(0x73686F72654CL),
                reference,
                floorY
        );
        helper.assertTrue(
                shorelinePlaced == 16,
                "Every partly flooded 4x4 shoreline cell must find its eligible Gloamwater column"
        );
        for (int cellZ = 0; cellZ < 4; cellZ++) {
            for (int cellX = 0; cellX < 4; cellX++) {
                int x = chunkMinX + cellX * 4;
                int z = chunkMinZ + cellZ * 4;
                var floor = helper.getLevel().getBlockState(new BlockPos(x, floorY, z));
                var plant = helper.getLevel().getBlockState(new BlockPos(x, floorY + 1, z));
                helper.assertTrue(
                        floor.is(ModBlocks.GLOAM_SAND.get()) || floor.is(ModBlocks.GLOAM_MUCK.get()),
                        "The surviving shoreline water column must receive an aquatic bed"
                );
                helper.assertTrue(
                        isAquaticPlant(plant.getBlock()),
                        "The surviving shoreline water column must receive one reviewed plant"
                );
            }
        }
        helper.succeed();
    }

    private static void assertOrganicBedDistribution(GameTestHelper helper) {
        int sampleSize = 96;
        int sampleMin = -48;
        int muck = 0;
        int matchingEdges = 0;
        int totalEdges = 0;
        int transitionsAwayFromFourBlockGrid = 0;

        for (int z = sampleMin; z < sampleMin + sampleSize; z++) {
            for (int x = sampleMin; x < sampleMin + sampleSize; x++) {
                boolean current = GloamSeaGrowthFeature.isReviewedMuckBedForTest(x, z);
                helper.assertTrue(
                        current == GloamSeaGrowthFeature.isReviewedMuckBedForTest(x, z),
                        "Aquatic floor noise must be deterministic"
                );
                if (current) {
                    muck++;
                }
                if (x > sampleMin) {
                    boolean west = GloamSeaGrowthFeature.isReviewedMuckBedForTest(x - 1, z);
                    totalEdges++;
                    if (current == west) {
                        matchingEdges++;
                    } else if (Math.floorMod(x, 4) != 0) {
                        transitionsAwayFromFourBlockGrid++;
                    }
                }
                if (z > sampleMin) {
                    boolean north = GloamSeaGrowthFeature.isReviewedMuckBedForTest(x, z - 1);
                    totalEdges++;
                    if (current == north) {
                        matchingEdges++;
                    } else if (Math.floorMod(z, 4) != 0) {
                        transitionsAwayFromFourBlockGrid++;
                    }
                }
            }
        }

        int total = sampleSize * sampleSize;
        helper.assertTrue(
                muck * 5 > total && muck * 5 < total * 4,
                "The reviewed bed field must contain meaningful regions of both Gloam Sand and Gloam Muck"
        );
        helper.assertTrue(
                matchingEdges * 100 > totalEdges * 90,
                "Aquatic floor materials must form broad contiguous shelves instead of pixel noise"
        );
        helper.assertTrue(
                transitionsAwayFromFourBlockGrid > 40,
                "Aquatic floor boundaries must be irregular rather than the old 4x4 mosaic"
        );
    }

    private static boolean isAquaticPlant(net.minecraft.world.level.block.Block block) {
        return block == ModBlocks.THREADKELP.get()
                || block == ModBlocks.VEILWEED.get()
                || block == ModBlocks.DROWNED_ROOTS.get()
                || block == ModBlocks.BLADDERPOD.get()
                || block == ModBlocks.LUMEN_KELP.get();
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 40)
    public static void rotfinHasServerAttributesAndDedicatedSafeTestPath(GameTestHelper helper) {
        Rotfin rotfin = helper.spawnWithNoFreeWill(ModEntities.ROTFIN.get(), 1, 2, 1);
        helper.succeedOnTickWhen(5, () -> {
            helper.assertTrue(rotfin.isAlive(), "Rotfin must remain alive during its server smoke test");
            helper.assertTrue(rotfin.tickCount >= 5, "Rotfin must receive server ticks");
            helper.assertTrue(rotfin.getType().getCategory() == MobCategory.WATER_CREATURE, "Rotfin must be aquatic");
            helper.assertTrue(
                    Gravesown.id("rotfin").equals(BuiltInRegistries.ENTITY_TYPE.getKey(rotfin.getType())),
                    "Rotfin entity id must remain stable"
            );
            assertNear(helper, rotfin.getMaxHealth(), 10.0D, "Rotfin max health");
            assertNear(helper, rotfin.getAttributeValue(Attributes.MOVEMENT_SPEED), 0.82D, "Rotfin movement speed");
            assertNear(helper, rotfin.getAttributeValue(Attributes.FOLLOW_RANGE), 16.0D, "Rotfin follow range");
            assertNear(helper, rotfin.getAttributeValue(Attributes.ATTACK_DAMAGE), 3.0D, "Rotfin attack damage");
        });
    }

    @GameTest(template = "gloamwater_pond_platform", timeoutTicks = 180)
    public static void rotfinSwimsAndBreathesInTheRegisteredFluid(GameTestHelper helper) {
        for (int x = 1; x <= 9; x++) {
            for (int z = 1; z <= 9; z++) {
                helper.setBlock(new BlockPos(x, 0, z), ModBlocks.SUTURE_SILT.get());
                helper.setBlock(new BlockPos(x, 1, z), ModBlocks.GLOAMWATER.get());
                helper.setBlock(new BlockPos(x, 2, z), ModBlocks.GLOAMWATER.get());
            }
        }

        Rotfin rotfin = ModEntities.ROTFIN.get().create(helper.getLevel());
        helper.assertTrue(rotfin != null, "Rotfin entity factory must create the aquatic test subject");
        BlockPos startBlock = helper.absolutePos(new BlockPos(5, 1, 5));
        rotfin.moveTo(startBlock.getX() + 0.5D, startBlock.getY() + 0.35D, startBlock.getZ() + 0.5D, 0.0F, 0.0F);
        rotfin.setPersistenceRequired();
        helper.getLevel().addFreshEntity(rotfin);
        Vec3 start = rotfin.position();

        helper.runAtTickTime(140, () -> {
            helper.assertTrue(rotfin.isAlive(), "Rotfin must not drown while submerged in Gloamwater");
            helper.assertTrue(rotfin.isInGloamwater(), "Rotfin must recognize the custom fluid family directly");
            helper.assertTrue(rotfin.getAirSupply() >= 280, "Gloamwater must restore aquatic air supply");
            helper.assertTrue(
                    rotfin.position().distanceToSqr(start) >= 0.36D,
                    "Rotfin idle AI must visibly swim instead of remaining at its spawn point"
            );
            helper.succeed();
        });
    }

    @GameTest(template = "gloamwater_pond_platform", timeoutTicks = 180)
    public static void peacefulSeaFishSwimBreatheAndKeepTheirServerAttributes(GameTestHelper helper) {
        fillPool(helper);
        Veilfin veilfin = ModEntities.VEILFIN.get().create(helper.getLevel());
        Rootskimmer rootskimmer = ModEntities.ROOTSKIMMER.get().create(helper.getLevel());
        SiltRay siltRay = ModEntities.SILT_RAY.get().create(helper.getLevel());
        helper.assertTrue(veilfin != null && rootskimmer != null && siltRay != null,
                "All peaceful fish factories must create entities");
        placePersistentFish(helper, veilfin, new BlockPos(3, 1, 4));
        placePersistentFish(helper, rootskimmer, new BlockPos(7, 1, 6));
        placePersistentFish(helper, siltRay, new BlockPos(5, 1, 7));
        Vec3 veilStart = veilfin.position();
        Vec3 rootStart = rootskimmer.position();
        Vec3 rayStart = siltRay.position();

        helper.runAtTickTime(140, () -> {
            helper.assertTrue(veilfin.isAlive() && rootskimmer.isAlive() && siltRay.isAlive(),
                    "Peaceful fish must not die in Gloamwater");
            helper.assertTrue(veilfin.isInGloamwater() && rootskimmer.isInGloamwater() && siltRay.isInGloamwater(),
                    "Peaceful fish must recognize Gloamwater directly");
            helper.assertTrue(veilfin.getAirSupply() >= 280 && rootskimmer.getAirSupply() >= 280
                            && siltRay.getAirSupply() >= 280,
                    "Peaceful fish air must be restored by Gloamwater");
            helper.assertTrue(veilfin.position().distanceToSqr(veilStart) >= 0.25D,
                    "Veilfin must visibly swim under server AI");
            helper.assertTrue(rootskimmer.position().distanceToSqr(rootStart) >= 0.25D,
                    "Rootskimmer must visibly swim under server AI");
            helper.assertTrue(siltRay.position().distanceToSqr(rayStart) >= 0.25D,
                    "Silt Ray must visibly patrol the bottom under server AI");
            helper.assertTrue(veilfin.getType().getCategory() == MobCategory.WATER_AMBIENT,
                    "Veilfin must use the peaceful water ambient category");
            helper.assertTrue(rootskimmer.getType().getCategory() == MobCategory.WATER_AMBIENT,
                    "Rootskimmer must use the peaceful water ambient category");
            helper.assertTrue(siltRay.getType().getCategory() == MobCategory.WATER_AMBIENT,
                    "Silt Ray must use the peaceful water ambient category");
            assertNear(helper, veilfin.getMaxHealth(), 6.0D, "Veilfin max health");
            assertNear(helper, rootskimmer.getMaxHealth(), 8.0D, "Rootskimmer max health");
            assertNear(helper, siltRay.getMaxHealth(), 12.0D, "Silt Ray max health");
            assertNear(helper, siltRay.getAttributeValue(Attributes.MOVEMENT_SPEED), 0.48D,
                    "Silt Ray movement speed");
            helper.succeed();
        });
    }

    @GameTest(template = "gloamwater_pond_platform", timeoutTicks = 40)
    public static void fishingAndSkiffKeepTheirCustomContentContract(GameTestHelper helper) {
        helper.setBlock(new BlockPos(4, 1, 4), ModBlocks.GLOAMWATER.get());
        GloamSkiff skiff = ModEntities.GLOAM_SKIFF.get().create(helper.getLevel());
        helper.assertTrue(skiff != null, "Gloam Skiff factory must create the vehicle");
        BlockPos position = helper.absolutePos(new BlockPos(4, 1, 4));
        skiff.setPos(position.getX() + 0.5D, position.getY() + 0.2D, position.getZ() + 0.5D);
        helper.getLevel().addFreshEntity(skiff);
        helper.assertTrue(skiff.getDropItem() == ModItems.GLOAM_SKIFF.get(),
                "Gloam Skiff must drop its own custom item");
        helper.assertTrue(ModFluids.GLOAMWATER_TYPE.get().supportsBoating(
                        helper.getLevel().getFluidState(position), skiff),
                "Gloamwater must opt into boat support");
        helper.assertTrue(ModItems.GLOAMLINE_ROD.get() instanceof FishingRodItem,
                "Gloamline Rod must retain vanilla's server-safe fishing behavior");
        helper.assertTrue(ModItems.GLOAMLINE_ROD.get().getDefaultInstance().getMaxDamage() == 96,
                "Gloamline Rod durability must remain 96");
        helper.succeed();
    }

    private static void fillPool(GameTestHelper helper) {
        for (int x = 1; x <= 9; x++) {
            for (int z = 1; z <= 9; z++) {
                helper.setBlock(new BlockPos(x, 0, z), ModBlocks.ABYSSAL_SILT.get());
                helper.setBlock(new BlockPos(x, 1, z), ModBlocks.GLOAMWATER.get());
                helper.setBlock(new BlockPos(x, 2, z), ModBlocks.GLOAMWATER.get());
            }
        }
    }

    private static void placePersistentFish(
            GameTestHelper helper,
            dev.gravesown.entity.GloamwaterFish fish,
            BlockPos relativePos
    ) {
        BlockPos position = helper.absolutePos(relativePos);
        fish.moveTo(position.getX() + 0.5D, position.getY() + 0.35D, position.getZ() + 0.5D, 0.0F, 0.0F);
        fish.setPersistenceRequired();
        helper.getLevel().addFreshEntity(fish);
    }

    private static void assertNear(GameTestHelper helper, double actual, double expected, String label) {
        helper.assertTrue(Math.abs(actual - expected) < 1.0E-6D, label + ": expected " + expected + ", got " + actual);
    }

    private static boolean isPondColumn(int dx, int dz) {
        int stretchedDistance = dx * dx * 3 + dz * dz * 4;
        int roughness = Math.abs(dx * 17 + dz * 31 + dx * dz * 7) % 19;
        return stretchedDistance <= 82 + roughness;
    }
}
