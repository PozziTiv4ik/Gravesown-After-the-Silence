package dev.gravesown.block;

import java.util.function.Supplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/** Shared age-seven crop restricted to Gloam farmland. */
public final class GravesownCropBlock extends CropBlock {
    private final Supplier<? extends ItemLike> seed;

    public GravesownCropBlock(BlockBehaviour.Properties properties, Supplier<? extends ItemLike> seed) {
        super(properties);
        this.seed = seed;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, net.minecraft.core.BlockPos pos) {
        return state.getBlock() instanceof GloamFarmlandBlock;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return this.seed.get();
    }
}
