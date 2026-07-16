package dev.gravesown.block;

import com.mojang.serialization.MapCodec;
import dev.gravesown.menu.GraveworkMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class GraveworkBenchBlock extends Block {
    public static final MapCodec<GraveworkBenchBlock> CODEC = simpleCodec(GraveworkBenchBlock::new);
    private static final Component TITLE = Component.translatable("container.gravesown.gravework");

    public GraveworkBenchBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, ignored) -> new GraveworkMenu(
                        containerId,
                        inventory,
                        ContainerLevelAccess.create(level, pos)
                ),
                TITLE
        ));
        return InteractionResult.CONSUME;
    }
}
