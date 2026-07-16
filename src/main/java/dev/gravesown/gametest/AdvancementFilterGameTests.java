package dev.gravesown.gametest;

import dev.gravesown.Gravesown;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Gravesown.MOD_ID)
@PrefixGameTestTemplate(false)
public final class AdvancementFilterGameTests {
    private AdvancementFilterGameTests() {
    }

    @GameTest(template = "hollow_grazer_platform", timeoutTicks = 20)
    public static void ordinaryAdvancementPresentationIsAbsent(GameTestHelper helper) {
        var advancements = helper.getLevel().getServer().getAdvancements();

        helper.assertTrue(
                advancements.get(ResourceLocation.withDefaultNamespace("story/root")) == null,
                "The vanilla story root must be filtered from the total-conversion data pack"
        );
        helper.assertTrue(advancements.get(Gravesown.id("survival/root")) == null,
                "Gravesown progression must live only in the custom Survival Hub");
        var recipes = helper.getLevel().getServer().getRecipeManager();
        helper.assertTrue(
                recipes.byKey(ResourceLocation.withDefaultNamespace("stick")).isEmpty(),
                "Vanilla recipes must be filtered from the total-conversion server data"
        );
        helper.assertTrue(
                recipes.byKey(Gravesown.id("crude_handpick")).isPresent(),
                "The built-in filter must preserve Gravesown recipes"
        );
        helper.succeed();
    }
}
