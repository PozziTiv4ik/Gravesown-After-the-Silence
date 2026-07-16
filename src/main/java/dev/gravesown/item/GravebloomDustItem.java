package dev.gravesown.item;

import dev.gravesown.registry.ModBlocks;
import dev.gravesown.worldgen.GravesownTreeFeature;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Gravesown's renewable, server-authoritative alternative to vanilla bone meal. */
public final class GravebloomDustItem extends Item {
    private static final List<Block> SPREADABLE_FLORA = List.of(
            ModBlocks.THREADGRASS.get(),
            ModBlocks.PALLID_BULB.get(),
            ModBlocks.CINDER_BLOOM.get(),
            ModBlocks.SINEW_FERN.get(),
            ModBlocks.MARROW_REED.get(),
            ModBlocks.RIFT_THORN.get(),
            ModBlocks.MIRE_FROND.get(),
            ModBlocks.MOSSVEIL.get(),
            ModBlocks.AMBER_BLOOM.get()
    );

    public GravebloomDustItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }
        BlockPos clicked = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clicked);
        boolean changed;
        if (clickedState.is(ModBlocks.RIBROOT_SHOOT.get())) {
            changed = growRibroot(level, clicked);
        } else if (clickedState.is(ModBlocks.EMBERBARK_SHOOT.get())) {
            changed = growTree(level, clicked, ModBlocks.EMBERBARK_STEM.get(), ModBlocks.EMBERBARK_FOLIAGE.get(),
                    GravesownTreeFeature.Shape.MOSSWAKE_BROAD);
        } else if (clickedState.is(ModBlocks.PALEVINE_SHOOT.get())) {
            changed = growTree(level, clicked, ModBlocks.PALEVINE_STEM.get(), ModBlocks.PALEVINE_FOLIAGE.get(),
                    GravesownTreeFeature.Shape.SUNVEIL_CROWN);
        } else if (clickedState.is(ModBlocks.CAIRNWOOD_SHOOT.get())) {
            changed = growTree(level, clicked, ModBlocks.CAIRNWOOD_STEM.get(), ModBlocks.CAIRNWOOD_FOLIAGE.get(),
                    GravesownTreeFeature.Shape.CAIRNWOOD_SHRUB);
        } else if (clickedState.is(ModBlocks.SUTUREWOOD_SHOOT.get())) {
            changed = growTree(level, clicked, ModBlocks.SUTUREWOOD_STEM.get(), ModBlocks.SUTUREWOOD_FOLIAGE.get(),
                    GravesownTreeFeature.Shape.SUTUREWOOD_ROOTED);
        } else if (clickedState.is(ModBlocks.MOSSWAKE_SHOOT.get())) {
            changed = growTree(level, clicked, ModBlocks.MOSSWAKE_STEM.get(), ModBlocks.MOSSWAKE_FOLIAGE.get(),
                    GravesownTreeFeature.Shape.MOSSWAKE_BROAD);
        } else if (clickedState.is(ModBlocks.SUNVEIL_SHOOT.get())) {
            changed = growTree(level, clicked, ModBlocks.SUNVEIL_STEM.get(), ModBlocks.SUNVEIL_FOLIAGE.get(),
                    GravesownTreeFeature.Shape.SUNVEIL_CROWN);
        } else if (clickedState.getBlock() instanceof BonemealableBlock growable
                && growable.isValidBonemealTarget(level, clicked, clickedState)) {
            changed = growable.isBonemealSuccess(level, level.getRandom(), clicked, clickedState);
            if (changed) {
                growable.performBonemeal(level, level.getRandom(), clicked, clickedState);
            }
        } else {
            changed = spreadFlora(level, clicked.relative(context.getClickedFace()));
        }
        if (!changed) {
            return InteractionResult.PASS;
        }
        Player player = context.getPlayer();
        if (player == null || !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        level.levelEvent(1505, clicked, 0);
        return InteractionResult.CONSUME;
    }

    public static boolean growRibroot(ServerLevel level, BlockPos base) {
        int height = 5 + level.getRandom().nextInt(3);
        for (int y = 0; y <= height + 1; y++) {
            BlockState state = level.getBlockState(base.above(y));
            if (y == 0 && state.is(ModBlocks.RIBROOT_SHOOT.get())) {
                continue;
            }
            if (!state.isAir() && !state.canBeReplaced()) {
                return false;
            }
        }
        for (int y = 0; y < height; y++) {
            level.setBlock(base.above(y), ModBlocks.RIBROOT_STEM.get().defaultBlockState(), Block.UPDATE_ALL);
        }
        BlockState leaves = ModBlocks.VEIL_FOLIAGE.get().defaultBlockState()
                .setValue(LeavesBlock.PERSISTENT, true)
                .setValue(LeavesBlock.DISTANCE, 1);
        BlockPos crown = base.above(height - 1);
        for (int dy = -2; dy <= 1; dy++) {
            int radius = dy == 1 ? 1 : 2;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) + Math.abs(dz) > radius + 1) {
                        continue;
                    }
                    BlockPos foliagePos = crown.offset(dx, dy, dz);
                    BlockState existing = level.getBlockState(foliagePos);
                    if (existing.isAir() || existing.canBeReplaced()) {
                        level.setBlock(foliagePos, leaves, Block.UPDATE_ALL);
                    }
                }
            }
        }
        return true;
    }

    private static boolean growTree(
            ServerLevel level,
            BlockPos base,
            Block stem,
            Block foliage,
            GravesownTreeFeature.Shape shape
    ) {
        return GravesownTreeFeature.placeTree(level, level.getRandom(), base, stem, foliage, shape);
    }

    public static boolean spreadFlora(ServerLevel level, BlockPos origin) {
        int placed = 0;
        for (int attempt = 0; attempt < 28; attempt++) {
            BlockPos candidate = origin.offset(
                    level.getRandom().nextInt(9) - 4,
                    level.getRandom().nextInt(5) - 2,
                    level.getRandom().nextInt(9) - 4
            );
            if (!level.getBlockState(candidate).isAir()) {
                continue;
            }
            Block block = SPREADABLE_FLORA.get(level.getRandom().nextInt(SPREADABLE_FLORA.size()));
            BlockState plant = block.defaultBlockState();
            if (plant.canSurvive(level, candidate)) {
                level.setBlock(candidate, plant, Block.UPDATE_ALL);
                placed++;
            }
        }
        return placed > 0;
    }
}
