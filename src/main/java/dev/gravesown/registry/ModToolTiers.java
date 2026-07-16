package dev.gravesown.registry;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public enum ModToolTiers implements Tier {
    CRUDE(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL,
            48,
            2.5F,
            0.0F,
            3,
            () -> Ingredient.of(ModItems.RIBROOT_SPLINT.get())
    ),
    BOUND_HUSHSTONE(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL,
            96,
            3.0F,
            0.5F,
            4,
            () -> Ingredient.of(ModItems.HUSHSTONE_SHARD.get())
    ),
    GRAVEWORK_HUSHSTONE(
            BlockTags.INCORRECT_FOR_STONE_TOOL,
            192,
            4.8F,
            1.0F,
            7,
            () -> Ingredient.of(ModItems.HUSHSTONE_SHARD.get())
    );

    private final TagKey<Block> incorrectBlocksForDrops;
    private final int uses;
    private final float speed;
    private final float attackDamageBonus;
    private final int enchantmentValue;
    private final Supplier<Ingredient> repairIngredient;

    ModToolTiers(
            TagKey<Block> incorrectBlocksForDrops,
            int uses,
            float speed,
            float attackDamageBonus,
            int enchantmentValue,
            Supplier<Ingredient> repairIngredient
    ) {
        this.incorrectBlocksForDrops = incorrectBlocksForDrops;
        this.uses = uses;
        this.speed = speed;
        this.attackDamageBonus = attackDamageBonus;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = Suppliers.memoize(repairIngredient::get);
    }

    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return attackDamageBonus;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return incorrectBlocksForDrops;
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }
}
