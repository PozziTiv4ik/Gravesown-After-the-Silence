package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import dev.gravesown.entity.HollowGrazer;
import dev.gravesown.registry.ModEntities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Gravesown.MOD_ID)
@PrefixGameTestTemplate(false)
public final class HollowGrazerGameTests {
    private static final ResourceLocation HOLLOW_GRAZER_ID =
            ResourceLocation.fromNamespaceAndPath(Gravesown.MOD_ID, "hollow_grazer");

    private HollowGrazerGameTests() {
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 40)
    public static void hollowGrazerSpawnsAndTicks(GameTestHelper helper) {
        HollowGrazer grazer = helper.spawnWithNoFreeWill(ModEntities.HOLLOW_GRAZER.get(), 1, 1, 1);

        helper.succeedOnTickWhen(5, () -> {
            helper.assertTrue(grazer.isAlive(), "Hollow Grazer must remain alive after spawning");
            helper.assertTrue(grazer.tickCount >= 5, "Hollow Grazer must receive server ticks");
            helper.assertTrue(grazer.getMaxHealth() == 28.0F, "Hollow Grazer max health must be 28");
            helper.assertTrue(
                    grazer.getType().getCategory() == MobCategory.CREATURE,
                    "Hollow Grazer must use the CREATURE spawn category"
            );
            helper.assertTrue(
                    HOLLOW_GRAZER_ID.equals(BuiltInRegistries.ENTITY_TYPE.getKey(grazer.getType())),
                    "Hollow Grazer registry id must stay stable"
            );
        });
    }
}
