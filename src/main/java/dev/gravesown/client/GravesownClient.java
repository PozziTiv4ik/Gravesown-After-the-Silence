package dev.gravesown.client;

import dev.gravesown.Gravesown;
import dev.gravesown.client.model.HollowGrazerModel;
import dev.gravesown.client.model.BuriedRemnantModel;
import dev.gravesown.client.model.QuietskinArmorModel;
import dev.gravesown.client.model.RibspringModel;
import dev.gravesown.client.model.StitchtuskModel;
import dev.gravesown.client.model.WoundscentModel;
import dev.gravesown.client.model.SpearModel;
import dev.gravesown.client.model.NativeFaunaModel;
import dev.gravesown.client.model.SiltRayModel;
import dev.gravesown.client.renderer.HollowGrazerRenderer;
import dev.gravesown.client.renderer.BuriedRemnantRenderer;
import dev.gravesown.client.renderer.RibspringRenderer;
import dev.gravesown.client.renderer.StitchtuskRenderer;
import dev.gravesown.client.renderer.WoundscentRenderer;
import dev.gravesown.client.renderer.ThrownSpearRenderer;
import dev.gravesown.client.renderer.NativeFaunaRenderer;
import dev.gravesown.client.renderer.SiltRayRenderer;
import dev.gravesown.entity.NativeFaunaSpecies;
import dev.gravesown.registry.ModEntities;
import dev.gravesown.registry.ModItems;
import dev.gravesown.registry.ModMenus;
import dev.gravesown.registry.ModRecipes;
import net.minecraft.client.RecipeBookCategories;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = Gravesown.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Gravesown.MOD_ID, value = Dist.CLIENT)
public final class GravesownClient {
    public GravesownClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        NeoForge.EVENT_BUS.addListener(GravesownTitleScreen::onScreenOpening);
        NeoForge.EVENT_BUS.addListener(SurvivorCodexKeyHandler::onScreenKeyPressed);
        NeoForge.EVENT_BUS.addListener(WorldCreationLockdown::onScreenInitialized);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.HOLLOW_GRAZER.get(), HollowGrazerRenderer::new);
        event.registerEntityRenderer(ModEntities.RIBSPRING.get(), RibspringRenderer::new);
        event.registerEntityRenderer(ModEntities.STITCHTUSK.get(), StitchtuskRenderer::new);
        event.registerEntityRenderer(ModEntities.WOUNDSCENT.get(), WoundscentRenderer::new);
        event.registerEntityRenderer(ModEntities.BURIED_REMNANT.get(), BuriedRemnantRenderer::new);
        event.registerEntityRenderer(ModEntities.THROWN_SPEAR.get(), ThrownSpearRenderer::new);
        for (ModEntities.NativeFaunaRegistration registration : ModEntities.nativeFauna()) {
            event.registerEntityRenderer(
                    registration.type().get(),
                    context -> new NativeFaunaRenderer(context, registration.species())
            );
        }
        event.registerEntityRenderer(ModEntities.SILT_RAY.get(), SiltRayRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(HollowGrazerModel.LAYER_LOCATION, HollowGrazerModel::createBodyLayer);
        event.registerLayerDefinition(RibspringModel.LAYER_LOCATION, RibspringModel::createBodyLayer);
        event.registerLayerDefinition(StitchtuskModel.LAYER_LOCATION, StitchtuskModel::createBodyLayer);
        event.registerLayerDefinition(WoundscentModel.LAYER_LOCATION, WoundscentModel::createBodyLayer);
        event.registerLayerDefinition(BuriedRemnantModel.LAYER_LOCATION, BuriedRemnantModel::createBodyLayer);
        event.registerLayerDefinition(SpearModel.LAYER_LOCATION, SpearModel::createBodyLayer);
        for (NativeFaunaSpecies species : NativeFaunaSpecies.values()) {
            event.registerLayerDefinition(
                    NativeFaunaModel.layer(species),
                    () -> NativeFaunaModel.createBodyLayer(species)
            );
        }
        event.registerLayerDefinition(SiltRayModel.LAYER_LOCATION, SiltRayModel::createBodyLayer);
        event.registerLayerDefinition(QuietskinArmorModel.HOOD_LAYER, QuietskinArmorModel::createHoodLayer);
        event.registerLayerDefinition(QuietskinArmorModel.COAT_LAYER, QuietskinArmorModel::createCoatLayer);
        event.registerLayerDefinition(QuietskinArmorModel.LEGWRAPS_LAYER, QuietskinArmorModel::createLegwrapsLayer);
        event.registerLayerDefinition(QuietskinArmorModel.BOOTS_LAYER, QuietskinArmorModel::createBootsLayer);
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        QuietskinArmorClientExtension extension = new QuietskinArmorClientExtension();
        event.registerItem(
                extension,
                ModItems.QUIETSKIN_HOOD.get(),
                ModItems.QUIETSKIN_COAT.get(),
                ModItems.QUIETSKIN_LEGWRAPS.get(),
                ModItems.QUIETSKIN_BOOTS.get()
        );
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.GRAVEWORK.get(), GraveworkScreen::new);
        event.register(ModMenus.PITCH_KILN.get(), PitchKilnScreen::new);
        event.register(ModMenus.FIELD_KITCHEN.get(), FieldKitchenScreen::new);
        event.register(ModMenus.SAWMILL.get(), SawmillScreen::new);
        event.register(ModMenus.RELIQUARY_CRATE.get(), ReliquaryCrateScreen::new);
    }

    @SubscribeEvent
    public static void registerRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
        event.registerRecipeCategoryFinder(
                ModRecipes.GRAVEWORK_TYPE.get(),
                recipe -> RecipeBookCategories.UNKNOWN
        );
        event.registerRecipeCategoryFinder(
                ModRecipes.FIELD_KITCHEN_TYPE.get(),
                recipe -> RecipeBookCategories.UNKNOWN
        );
        event.registerRecipeCategoryFinder(
                ModRecipes.SAWMILL_TYPE.get(),
                recipe -> RecipeBookCategories.UNKNOWN
        );
    }
}
