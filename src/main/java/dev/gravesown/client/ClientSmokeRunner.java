package dev.gravesown.client;

import dev.gravesown.Gravesown;
import dev.gravesown.registry.ModAttachments;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModEntities;
import dev.gravesown.registry.ModItems;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.client.gui.CreativeTabsScreenPage;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

/**
 * Opt-in development runner for clienttest.cmd. The system property is never
 * set by normal client or dedicated-server runs.
 */
@EventBusSubscriber(modid = Gravesown.MOD_ID, value = Dist.CLIENT)
public final class ClientSmokeRunner {
    private static final String ENABLED_PROPERTY = "gravesown.clientSmoke.enabled";
    private static final String EXPECTED_SEED_PROPERTY = "gravesown.clientSmoke.expectedSeed";
    private static final Set<ResourceKey<Biome>> EXPECTED_BIOMES = Set.of(
            biomeKey("sown_grave"),
            biomeKey("mosswake_woods"),
            biomeKey("amberquiet_grove"),
            biomeKey("ribroot_groves"),
            biomeKey("marrow_rifts"),
            biomeKey("suture_mire"),
            biomeKey("gloam_sea"),
            biomeKey("ember_thicket"),
            biomeKey("pallid_weald")
    );
    private static final int PREPARE_TICK = 10;
    private static final int CODEX_OPEN_TICK = 30;
    private static final int CODEX_SHORTCUT_TICK = 40;
    private static final int CODEX_VERIFY_TICK = 50;
    private static final int CODEX_SCREENSHOT_TICK = 55;
    private static final int GUIDE_SCREENSHOT_TICK = 65;
    private static final int CREATIVE_OPEN_TICK = 80;
    private static final int CREATIVE_SCREENSHOT_TICK = 88;
    private static final int ENTITY_SCREENSHOT_TICK = 100;
    private static final int ARMOR_EQUIP_TICK = 120;
    private static final int ARMOR_CAMERA_TICK = 140;
    private static final int ARMOR_SCREENSHOT_TICK = 170;
    private static final int SETTLE_TICKS = 205;
    private static final String CODEX_SCREENSHOT = "gravesown-codex-smoke.png";
    private static final String GUIDE_SCREENSHOT = "gravesown-guide-smoke.png";
    private static final String CREATIVE_SCREENSHOT = "gravesown-creative-inventory-smoke.png";
    private static final String ENTITY_SCREENSHOT = "gravesown-creature-lineup-smoke.png";
    private static final String ARMOR_SCREENSHOT = "gravesown-quietskin-smoke.png";
    private static final Map<UUID, VisualMobExpectation> VISUAL_MOBS = new ConcurrentHashMap<>();

    private static int joinedTicks;
    private static boolean finished;
    private static boolean disposableBackupPromptHandled;
    private static volatile boolean serverSceneReady;
    private static volatile String serverFailure;

    private ClientSmokeRunner() {
    }

    /**
     * The world audit creates an intentionally disposable custom-preset save. The
     * ordinary client correctly asks whether to back it up; the opt-in smoke harness
     * selects "join without backup" so no owned test save or user world is copied.
     */
    @SubscribeEvent
    public static void onScreenInitialized(ScreenEvent.Init.Post event) {
        if (!Boolean.getBoolean(ENABLED_PROPERTY)
                || disposableBackupPromptHandled
                || !(event.getScreen() instanceof BackupConfirmScreen)) {
            return;
        }

        var buttons = event.getListenersList().stream()
                .filter(Button.class::isInstance)
                .map(Button.class::cast)
                .toList();
        if (buttons.size() < 2) {
            Gravesown.LOGGER.error(
                    "GRAVESOWN_CLIENT_SMOKE_RESULT status=FAIL reason=backup_prompt_buttons_missing count={}",
                    buttons.size()
            );
            Minecraft.getInstance().stop();
            return;
        }

        disposableBackupPromptHandled = true;
        Minecraft.getInstance().execute(buttons.get(1)::onPress);
        Gravesown.LOGGER.info("GRAVESOWN_CLIENT_SMOKE_BACKUP_PROMPT skipped_for_disposable_world=true");
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (finished || !Boolean.getBoolean(ENABLED_PROPERTY)) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null || client.getSingleplayerServer() == null) {
            return;
        }
        if (serverFailure != null) {
            failAndStop(client, serverFailure);
            return;
        }
        client.options.pauseOnLostFocus = false;
        joinedTicks++;
        if (joinedTicks == PREPARE_TICK) {
            prepareVisualSceneOnIntegratedServer(client);
        }
        if (joinedTicks >= CODEX_OPEN_TICK && !serverSceneReady) {
            // Wait for the integrated-server task instead of racing inventory sync.
            joinedTicks = CODEX_OPEN_TICK - 1;
            return;
        }
        if (joinedTicks == CODEX_OPEN_TICK) {
            openCodexOnIntegratedServer(client);
        }
        if (joinedTicks == CODEX_SHORTCUT_TICK) {
            SurvivorCodexClientPayloadHandler.requestCreationChain(ModItems.CRUDE_HANDPICK.get());
        }
        if (joinedTicks == CODEX_VERIFY_TICK) {
            if (!(client.screen instanceof SurvivorCodexScreen codexScreen)) {
                failAndStop(
                        client,
                        "codex_screen_not_open current_screen="
                                + (client.screen == null ? "null" : client.screen.getClass().getName())
                );
                return;
            }
            if (!codexScreen.isCreationChainOpenFor(ModItems.CRUDE_HANDPICK.get())) {
                failAndStop(client, "recipe_chain_shortcut_request_failed");
                return;
            }
            if (!codexScreen.prepareRecipeGuideSmoke(ModItems.CRUDE_HANDPICK.get())) {
                failAndStop(client, "recipe_guide_catalog_or_search_invalid");
                return;
            }
            Gravesown.LOGGER.info(
                    "GRAVESOWN_RECIPE_GUIDE verified=true recipes_at_least=33 exact_grid=true search=true categories=true guide=true graph_nodes=6 graph_edges=5 shortcut_request=true"
            );
        }
        if (joinedTicks == CODEX_SCREENSHOT_TICK) {
            Screenshot.grab(
                    client.gameDirectory,
                    CODEX_SCREENSHOT,
                    client.getMainRenderTarget(),
                    message -> Gravesown.LOGGER.info("GRAVESOWN_CODEX_VISUAL_CAPTURE {}", message.getString())
            );
        }
        if (joinedTicks == GUIDE_SCREENSHOT_TICK - 5) {
            if (!(client.screen instanceof SurvivorCodexScreen codexScreen)
                    || !codexScreen.prepareGuideSmoke()) {
                failAndStop(client, "codex_guide_visual_preparation_failed");
                return;
            }
        }
        if (joinedTicks == GUIDE_SCREENSHOT_TICK) {
            Screenshot.grab(
                    client.gameDirectory,
                    GUIDE_SCREENSHOT,
                    client.getMainRenderTarget(),
                    message -> Gravesown.LOGGER.info("GRAVESOWN_GUIDE_VISUAL_CAPTURE {}", message.getString())
            );
        }
        if (joinedTicks == GUIDE_SCREENSHOT_TICK + 5) {
            stowCodexHandOnIntegratedServer(client);
            setCreativeModeOnIntegratedServer(client);
        }
        if (joinedTicks == CREATIVE_OPEN_TICK) {
            openCreativeInventory(client);
        }
        if (joinedTicks == CREATIVE_SCREENSHOT_TICK) {
            if (!(client.screen instanceof CreativeModeInventoryScreen creativeScreen)
                    || !creativeScreen.isInventoryOpen()) {
                failAndStop(client, "creative_survival_inventory_not_open");
                return;
            }
            Screenshot.grab(
                    client.gameDirectory,
                    CREATIVE_SCREENSHOT,
                    client.getMainRenderTarget(),
                    message -> Gravesown.LOGGER.info("GRAVESOWN_CREATIVE_VISUAL_CAPTURE {}", message.getString())
            );
        }
        if ((joinedTicks > GUIDE_SCREENSHOT_TICK && joinedTicks < CREATIVE_OPEN_TICK)
                || (joinedTicks > CREATIVE_SCREENSHOT_TICK && joinedTicks < ARMOR_CAMERA_TICK)) {
            if (client.screen != null) {
                client.setScreen(null);
            }
            client.options.setCameraType(CameraType.FIRST_PERSON);
            client.player.setYRot(0.0F);
            client.player.setYHeadRot(0.0F);
            client.player.setXRot(8.0F);
        }
        if (joinedTicks == ENTITY_SCREENSHOT_TICK) {
            if (!clientTracksVisualMobs(client)) {
                failAndStop(client, "not_all_visual_mobs_reached_client tracked=" + VISUAL_MOBS.size());
                return;
            }
            Screenshot.grab(
                    client.gameDirectory,
                    ENTITY_SCREENSHOT,
                    client.getMainRenderTarget(),
                    message -> Gravesown.LOGGER.info("GRAVESOWN_ENTITY_VISUAL_CAPTURE {}", message.getString())
            );
        }
        if (joinedTicks == ARMOR_EQUIP_TICK) {
            equipQuietskinOnIntegratedServer(client);
        }
        if (joinedTicks >= ARMOR_CAMERA_TICK && joinedTicks <= ARMOR_SCREENSHOT_TICK) {
            if (client.screen != null) {
                client.setScreen(null);
            }
            client.options.setCameraType(CameraType.THIRD_PERSON_FRONT);
        }
        if (joinedTicks == ARMOR_SCREENSHOT_TICK) {
            Gravesown.LOGGER.info(
                    "GRAVESOWN_CLIENT_VISUAL_CAMERA type={}",
                    client.options.getCameraType()
            );
            Screenshot.grab(
                    client.gameDirectory,
                    ARMOR_SCREENSHOT,
                    client.getMainRenderTarget(),
                    message -> Gravesown.LOGGER.info("GRAVESOWN_CLIENT_VISUAL_CAPTURE {}", message.getString())
            );
        }
        if (joinedTicks < SETTLE_TICKS) {
            return;
        }

        finished = true;
        try {
            GravesownTitleScreen.verifyBackgroundResource(client);
            long expectedSeed = Long.parseLong(System.getProperty(EXPECTED_SEED_PROPERTY));
            long actualSeed = client.getSingleplayerServer().overworld().getSeed();
            if (actualSeed != expectedSeed) {
                throw new IllegalStateException("expected seed " + expectedSeed + ", got " + actualSeed);
            }
            Holder<Biome> joinedBiome = client.level.getBiome(client.player.blockPosition());
            ResourceKey<Biome> joinedBiomeKey = joinedBiome.unwrapKey()
                    .orElseThrow(() -> new IllegalStateException("player joined an unregistered biome"));
            if (!EXPECTED_BIOMES.contains(joinedBiomeKey)) {
                throw new IllegalStateException("player joined unexpected biome " + joinedBiomeKey.location());
            }

            Gravesown.LOGGER.info(
                    "GRAVESOWN_CLIENT_SMOKE_RESULT status=PASS seed={} biome={}",
                    actualSeed,
                    joinedBiomeKey.location()
            );
        }
        catch (RuntimeException exception) {
            Gravesown.LOGGER.error("GRAVESOWN_CLIENT_SMOKE_RESULT status=FAIL", exception);
        }
        finally {
            client.stop();
        }
    }

    private static ResourceKey<Biome> biomeKey(String path) {
        return ResourceKey.create(Registries.BIOME, Gravesown.id(path));
    }

    private static void prepareVisualSceneOnIntegratedServer(Minecraft client) {
        UUID playerId = client.player.getUUID();
        var server = client.getSingleplayerServer();
        server.execute(() -> {
            try {
                ServerPlayer player = server.getPlayerList().getPlayer(playerId);
                if (player == null) {
                    throw new IllegalStateException("integrated-server player is missing");
                }

                int codexCount = 0;
                int codexSlot = -1;
                for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
                    if (player.getInventory().getItem(slot).is(ModItems.SURVIVOR_CODEX.get())) {
                        codexCount++;
                        codexSlot = slot;
                    }
                }
                if (!player.getData(ModAttachments.RECEIVED_SURVIVOR_CODEX)) {
                    throw new IllegalStateException("automatic Codex attachment was not set on Survival login");
                }
                if (codexCount != 1) {
                    throw new IllegalStateException("expected exactly one automatically granted Codex, got " + codexCount);
                }

                int selectedSlot = player.getInventory().selected;
                if (codexSlot != selectedSlot) {
                    ItemStack selectedStack = player.getInventory().getItem(selectedSlot);
                    ItemStack grantedCodex = player.getInventory().getItem(codexSlot);
                    player.getInventory().setItem(selectedSlot, grantedCodex);
                    player.getInventory().setItem(codexSlot, selectedStack);
                }
                player.inventoryMenu.broadcastChanges();
                Gravesown.LOGGER.info(
                        "GRAVESOWN_CODEX_AUTO_GRANT verified=true count={} attachment=true",
                        codexCount
                );

                int stageX = player.blockPosition().getX();
                int stageZ = player.blockPosition().getZ();
                int terrainTop = player.serverLevel().getHeight(
                        Heightmap.Types.WORLD_SURFACE,
                        stageX,
                        stageZ
                );
                int stageY = Math.min(
                        player.serverLevel().getMaxBuildHeight() - 12,
                        Math.max(player.blockPosition().getY() + 12, terrainTop + 12)
                );
                for (int offsetX = -10; offsetX <= 10; offsetX++) {
                    for (int offsetZ = -3; offsetZ <= 15; offsetZ++) {
                        player.serverLevel().setBlock(
                                new BlockPos(stageX + offsetX, stageY - 1, stageZ + offsetZ),
                                ModBlocks.HUSHSTONE.get().defaultBlockState(),
                                3
                        );
                        for (int offsetY = 0; offsetY <= 8; offsetY++) {
                            player.serverLevel().setBlock(
                                    new BlockPos(stageX + offsetX, stageY + offsetY, stageZ + offsetZ),
                                    Blocks.AIR.defaultBlockState(),
                                    3
                            );
                        }
                    }
                }
                player.connection.teleport(
                        stageX + 0.5D,
                        stageY,
                        stageZ + 0.5D,
                        0.0F,
                        8.0F
                );
                player.setYRot(0.0F);
                player.setYHeadRot(0.0F);
                player.setYBodyRot(0.0F);
                player.setXRot(0.0F);
                player.serverLevel().setDayTime(6000L);
                player.serverLevel().setWeatherParameters(0, 0, false, false);
                player.serverLevel().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(32.0D))
                        .forEach(Entity::discard);
                VISUAL_MOBS.clear();
                double x = stageX + 0.5D;
                double z = stageZ + 0.5D;
                spawnVisualMob(player, ModEntities.HOLLOW_GRAZER.get().create(player.serverLevel()), x - 6.0D, z + 9.0D);
                spawnVisualMob(player, ModEntities.RIBSPRING.get().create(player.serverLevel()), x - 3.0D, z + 7.0D);
                spawnVisualMob(player, ModEntities.STITCHTUSK.get().create(player.serverLevel()), x, z + 10.0D);
                spawnVisualMob(player, ModEntities.WOUNDSCENT.get().create(player.serverLevel()), x + 3.0D, z + 7.0D);
                spawnVisualMob(player, ModEntities.BURIED_REMNANT.get().create(player.serverLevel()), x + 6.0D, z + 9.0D);
                serverSceneReady = true;
            }
            catch (RuntimeException exception) {
                recordServerFailure("scene_preparation_failed: " + exception.getMessage(), exception);
            }
        });
    }

    private static void equipQuietskinOnIntegratedServer(Minecraft client) {
        UUID playerId = client.player.getUUID();
        var server = client.getSingleplayerServer();
        server.execute(() -> {
            ServerPlayer player = server.getPlayerList().getPlayer(playerId);
            if (player == null) {
                Gravesown.LOGGER.error("Could not equip the Quietskin client visual: player {} is missing", playerId);
                return;
            }
            player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.QUIETSKIN_HOOD.get()));
            player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.QUIETSKIN_COAT.get()));
            player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.QUIETSKIN_LEGWRAPS.get()));
            player.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.QUIETSKIN_BOOTS.get()));
        });
    }

    private static void openCodexOnIntegratedServer(Minecraft client) {
        UUID playerId = client.player.getUUID();
        var server = client.getSingleplayerServer();
        server.execute(() -> {
            try {
                ServerPlayer player = server.getPlayerList().getPlayer(playerId);
                if (player == null) {
                    throw new IllegalStateException("integrated-server player is missing while opening Codex");
                }
                ItemStack codex = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (!codex.is(ModItems.SURVIVOR_CODEX.get())) {
                    throw new IllegalStateException("automatically granted Codex was not in the main hand");
                }
                codex.getItem().use(player.serverLevel(), player, InteractionHand.MAIN_HAND);
            }
            catch (RuntimeException exception) {
                recordServerFailure("codex_open_failed: " + exception.getMessage(), exception);
            }
        });
    }

    private static void stowCodexHandOnIntegratedServer(Minecraft client) {
        UUID playerId = client.player.getUUID();
        var server = client.getSingleplayerServer();
        server.execute(() -> {
            try {
                ServerPlayer player = server.getPlayerList().getPlayer(playerId);
                if (player == null) {
                    throw new IllegalStateException("integrated-server player is missing while stowing Codex");
                }
                ItemStack codex = player.getMainHandItem();
                if (!codex.is(ModItems.SURVIVOR_CODEX.get())) {
                    throw new IllegalStateException("main hand lost the automatically granted Codex before stow");
                }
                int selectedSlot = player.getInventory().selected;
                for (int slot = 0; slot < 36; slot++) {
                    if (slot != selectedSlot && player.getInventory().getItem(slot).isEmpty()) {
                        player.getInventory().setItem(slot, codex);
                        player.getInventory().setItem(selectedSlot, ItemStack.EMPTY);
                        player.inventoryMenu.broadcastChanges();
                        return;
                    }
                }
                throw new IllegalStateException("no empty inventory slot was available to preserve the granted Codex");
            }
            catch (RuntimeException exception) {
                recordServerFailure("codex_stow_failed: " + exception.getMessage(), exception);
            }
        });
    }

    private static void setCreativeModeOnIntegratedServer(Minecraft client) {
        UUID playerId = client.player.getUUID();
        var server = client.getSingleplayerServer();
        server.execute(() -> {
            ServerPlayer player = server.getPlayerList().getPlayer(playerId);
            if (player == null || !player.setGameMode(GameType.CREATIVE)) {
                recordServerFailure("creative_mode_setup_failed", new IllegalStateException("player is missing or rejected"));
            }
        });
    }

    private static void openCreativeInventory(Minecraft client) {
        CreativeModeTab inventoryTab = CreativeModeTabs.allTabs().stream()
                .filter(tab -> tab.getType() == CreativeModeTab.Type.INVENTORY)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("vanilla Creative inventory tab is missing"));
        CreativeModeInventoryScreen screen = new CreativeModeInventoryScreen(
                client.player,
                client.level.enabledFeatures(),
                client.options.operatorItemsTab().get()
        );
        client.setScreen(screen);
        Button nextPage = screen.children().stream()
                .filter(Button.class::isInstance)
                .map(Button.class::cast)
                .filter(button -> ">".equals(button.getMessage().getString()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Creative next-page button is missing"));
        for (int attempts = 0;
                attempts < 8 && !screen.getCurrentPage().getVisibleTabs().contains(inventoryTab);
                attempts++) {
            nextPage.onPress();
        }
        CreativeTabsScreenPage inventoryPage = screen.getCurrentPage();
        if (!inventoryPage.getVisibleTabs().contains(inventoryTab)) {
            throw new IllegalStateException("Creative inventory tab was not found on a real screen page");
        }
        int column = inventoryPage.getColumn(inventoryTab);
        int relativeX = 27 * column;
        if (inventoryTab.isAlignedRight()) {
            relativeX = 195 - 27 * (7 - column) + 1;
        }
        int relativeY = inventoryPage.isTop(inventoryTab) ? -32 : 136;
        int left = (client.getWindow().getGuiScaledWidth() - 195) / 2;
        int top = (client.getWindow().getGuiScaledHeight() - 136) / 2;
        screen.mouseClicked(left + relativeX + 13.0D, top + relativeY + 16.0D, 0);
        screen.mouseReleased(left + relativeX + 13.0D, top + relativeY + 16.0D, 0);
    }

    private static void spawnVisualMob(ServerPlayer player, Mob mob, double x, double z) {
        if (mob == null) {
            throw new IllegalStateException("Could not create a biome creature for the client visual smoke");
        }
        int y = player.serverLevel().getHeight(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (int)Math.floor(x),
                (int)Math.floor(z)
        );
        mob.moveTo(x, y, z, 180.0F, 0.0F);
        mob.setNoAi(true);
        mob.setInvulnerable(true);
        mob.setPersistenceRequired();
        if (!player.serverLevel().addFreshEntity(mob)) {
            throw new IllegalStateException("Could not add visual mob " + mob.getType());
        }
        VISUAL_MOBS.put(mob.getUUID(), new VisualMobExpectation(mob.getId(), mob.getType()));
    }

    private static boolean clientTracksVisualMobs(Minecraft client) {
        if (VISUAL_MOBS.size() != 5 || client.level == null) {
            return false;
        }
        for (Map.Entry<UUID, VisualMobExpectation> entry : VISUAL_MOBS.entrySet()) {
            VisualMobExpectation expected = entry.getValue();
            Entity entity = client.level.getEntity(expected.networkId());
            if (entity == null
                    || !entity.getUUID().equals(entry.getKey())
                    || entity.getType() != expected.type()) {
                return false;
            }
        }
        Gravesown.LOGGER.info("GRAVESOWN_ENTITY_TRACKING verified=true count={}", VISUAL_MOBS.size());
        return true;
    }

    private static void recordServerFailure(String reason, RuntimeException exception) {
        serverFailure = reason;
        Gravesown.LOGGER.error("GRAVESOWN_CLIENT_SMOKE_SERVER_FAILURE reason={}", reason, exception);
    }

    private static void failAndStop(Minecraft client, String reason) {
        finished = true;
        Gravesown.LOGGER.error("GRAVESOWN_CLIENT_SMOKE_RESULT status=FAIL reason={}", reason);
        client.stop();
    }

    private record VisualMobExpectation(int networkId, EntityType<?> type) {
    }
}
