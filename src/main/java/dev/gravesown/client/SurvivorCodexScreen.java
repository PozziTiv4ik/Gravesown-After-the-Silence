package dev.gravesown.client;

import dev.gravesown.network.ClaimCodexQuestPayload;
import dev.gravesown.network.SurvivorCodexProgress;
import dev.gravesown.registry.ModBlocks;
import dev.gravesown.registry.ModItems;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

/** Full-screen survival hub. Quest state is a read-only snapshot owned by the server. */
public final class SurvivorCodexScreen extends Screen {
    private static final int COMPACT_WIDTH = 760;
    private static final int BACKDROP = GravesownUiPalette.BACKDROP;
    private static final int PANEL = GravesownUiPalette.PANEL;
    private static final int PANEL_LIGHT = GravesownUiPalette.PANEL_LIGHT;
    private static final int BORDER = GravesownUiPalette.BORDER;
    private static final int BORDER_DARK = GravesownUiPalette.BORDER_DARK;
    private static final int TEXT = GravesownUiPalette.TEXT;
    private static final int MUTED = GravesownUiPalette.MUTED;
    private static final int READY = GravesownUiPalette.READY;
    private static final int CLAIMED = GravesownUiPalette.SUCCESS;
    private static final int LOCKED = GravesownUiPalette.LOCKED;

    private static final int NODE_WIDTH = 138;
    private static final int NODE_HEIGHT = 52;

    private static final List<QuestNode> QUESTS = List.of(
            quest(0, 40, 150, "root", () -> new ItemStack(ModItems.SURVIVOR_CODEX.get())),
            quest(1, 230, 70, "gather", () -> new ItemStack(ModBlocks.RIBROOT_STEM.get())),
            quest(2, 420, 150, "handpick", () -> new ItemStack(ModItems.CRUDE_HANDPICK.get())),
            quest(3, 610, 70, "shard", () -> new ItemStack(ModItems.HUSHSTONE_SHARD.get())),
            quest(4, 800, 150, "knife", () -> new ItemStack(ModItems.BOUND_KNIFE.get())),
            quest(5, 990, 70, "grazer", () -> new ItemStack(ModItems.RAGGED_GRAZER_HIDE.get())),
            quest(6, 1180, 150, "quietskin", () -> new ItemStack(ModItems.QUIETSKIN_COAT.get())),
            quest(7, 1370, 70, "gravework", () -> new ItemStack(ModItems.GRAVEWORK_BENCH.get())),
            quest(8, 1560, 150, "three_woods", () -> new ItemStack(ModItems.EMBERBARK_PLANKS.get())),
            quest(9, 1750, 70, "kitchen", () -> new ItemStack(ModItems.FIELD_KITCHEN.get())),
            quest(10, 1940, 150, "utensils", () -> new ItemStack(ModItems.BONE_CLEAVER.get())),
            quest(11, 2130, 70, "meal", () -> new ItemStack(ModItems.GLOAM_CHOWDER.get())),
            quest(12, 2320, 150, "angler", () -> new ItemStack(ModItems.NEEDLE_SPRAT.get())),
            quest(13, 2510, 70, "ruin", () -> new ItemStack(ModItems.RELIQUARY_CRATE.get()))
    );

    private static final List<SurvivalGuideRecipes.RecipeEntry> RECIPES = SurvivalGuideRecipes.ALL;

    private int conditionMask;
    private int claimedMask;
    private Section section = Section.STORY;
    private int selectedQuest;
    private double panX;
    private double panY;
    private double zoom = 0.82D;
    private EditBox recipeSearch;
    private String recipeQuery = "";
    private int selectedRecipe = SurvivalGuideRecipes.indexOfOutput(ModItems.CRUDE_HANDPICK.get());
    private int chainRecipe = SurvivalGuideRecipes.indexOfOutput(ModItems.CRUDE_HANDPICK.get());
    private boolean chainPinned;
    private double recipeListScroll;
    private double recipeCategoryScroll;
    private RecipeCategory recipeCategory = RecipeCategory.ALL;
    private double chainPanX;
    private double chainPanY;
    private double chainZoom = 1.0D;
    private boolean chainDragging;
    private boolean chainViewInitialized;
    private GuidePage selectedGuidePage = GuidePage.BASICS;
    private double guideScroll;
    private double guideTopicScroll;
    private boolean guideDragging;
    private Item requestedCreationTarget;
    private ItemStack hoveredCraftStack = ItemStack.EMPTY;
    private boolean dragging;
    private int toastQuest = -1;
    private int toastTicks;

    public SurvivorCodexScreen(int conditionMask, int claimedMask) {
        this(conditionMask, claimedMask, null);
    }

    public SurvivorCodexScreen(int conditionMask, int claimedMask, Item creationTarget) {
        super(Component.translatable("item.gravesown.survivor_codex"));
        this.conditionMask = conditionMask & SurvivorCodexProgress.ALL_QUEST_BITS;
        this.claimedMask = claimedMask & SurvivorCodexProgress.ALL_QUEST_BITS;
        this.selectedQuest = firstUnclaimedQuest();
        this.requestedCreationTarget = creationTarget;
        if (creationTarget != null) {
            this.section = Section.CHAIN;
        }
    }

    public void updateProgress(int conditionMask, int claimedMask, int newlyClaimedQuest) {
        this.conditionMask = conditionMask & SurvivorCodexProgress.ALL_QUEST_BITS;
        this.claimedMask = claimedMask & SurvivorCodexProgress.ALL_QUEST_BITS;
        if (newlyClaimedQuest >= 0) {
            this.toastQuest = newlyClaimedQuest;
            this.toastTicks = 110;
            this.selectedQuest = Math.min(newlyClaimedQuest + 1, QUESTS.size() - 1);
        }
    }

    @Override
    protected void init() {
        Item previousSelection = this.selectedRecipe >= 0 && this.selectedRecipe < RECIPES.size()
                ? RECIPES.get(this.selectedRecipe).output().get().getItem()
                : ModItems.CRUDE_HANDPICK.get();
        Item previousChainSelection = this.chainRecipe >= 0 && this.chainRecipe < RECIPES.size()
                ? RECIPES.get(this.chainRecipe).output().get().getItem()
                : previousSelection;
        SurvivalGuideRecipes.refresh(this.minecraft);
        this.selectedRecipe = SurvivalGuideRecipes.indexOfOutput(previousSelection);
        if (this.selectedRecipe < 0 && !RECIPES.isEmpty()) {
            this.selectedRecipe = 0;
        }
        this.chainRecipe = SurvivalGuideRecipes.indexOfOutput(previousChainSelection);
        if (this.chainRecipe < 0) {
            this.chainRecipe = this.selectedRecipe;
        }
        if (this.requestedCreationTarget != null) {
            openCreationChain(this.requestedCreationTarget);
            this.requestedCreationTarget = null;
        }
        CraftLayout layout = craftLayout();
        this.recipeSearch = new EditBox(
                this.font,
                layout.listLeft(),
                layout.searchTop(),
                layout.listRight() - layout.listLeft(),
                layout.searchHeight(),
                Component.translatable("screen.gravesown.codex_hub.search")
        );
        this.recipeSearch.setMaxLength(48);
        this.recipeSearch.setHint(Component.translatable("screen.gravesown.codex_hub.search_hint"));
        this.recipeSearch.setValue(this.recipeQuery);
        this.recipeSearch.setResponder(value -> {
            this.recipeQuery = value;
            this.recipeListScroll = 0.0D;
            ensureVisibleRecipeSelection(filteredRecipeIndexes());
        });
        this.recipeSearch.setVisible(this.section == Section.CRAFTS);
        this.addWidget(this.recipeSearch);
    }

    @Override
    public void tick() {
        if (this.toastTicks > 0) {
            this.toastTicks--;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.hoveredCraftStack = ItemStack.EMPTY;
        graphics.fill(0, 0, this.width, this.height, BACKDROP);
        renderHeader(graphics, mouseX, mouseY);
        switch (this.section) {
            case STORY -> renderStory(graphics, mouseX, mouseY);
            case CRAFTS -> renderCrafts(graphics, mouseX, mouseY);
            case CHAIN -> renderChain(graphics, mouseX, mouseY);
            case GUIDE -> renderGuide(graphics, mouseX, mouseY);
        }
        if (this.recipeSearch != null && this.recipeSearch.visible) {
            this.recipeSearch.render(graphics, mouseX, mouseY, partialTick);
        }
        if (!this.hoveredCraftStack.isEmpty()) {
            graphics.renderTooltip(this.font, this.hoveredCraftStack, mouseX, mouseY);
        }
        renderToast(graphics, partialTick);
    }

    private void renderHeader(GuiGraphics graphics, int mouseX, int mouseY) {
        int headerHeight = headerHeight();
        int tabY = 11;
        int tabGap = compactLayout() ? 5 : 8;
        int tabWidth = compactLayout() ? Math.max(58, (this.width - 80) / 4) : 110;
        int tabsWidth = tabWidth * 4 + tabGap * 3;
        int storyTabX = compactLayout() ? 14 : (this.width - tabsWidth) / 2;
        int craftsTabX = storyTabX + tabWidth + tabGap;
        int chainTabX = craftsTabX + tabWidth + tabGap;
        int guideTabX = chainTabX + tabWidth + tabGap;
        graphics.fill(0, 0, this.width, headerHeight, GravesownUiPalette.HEADER);
        graphics.hLine(0, this.width, headerHeight - 1, BORDER);
        renderTab(graphics, storyTabX, tabY, tabWidth, 31, Section.STORY,
                Component.translatable("screen.gravesown.codex_hub.story"), mouseX, mouseY);
        renderTab(graphics, craftsTabX, tabY, tabWidth, 31, Section.CRAFTS,
                Component.translatable("screen.gravesown.codex_hub.crafts"), mouseX, mouseY);
        renderTab(graphics, chainTabX, tabY, tabWidth, 31, Section.CHAIN,
                Component.translatable("screen.gravesown.codex_hub.chain"), mouseX, mouseY);
        renderTab(graphics, guideTabX, tabY, tabWidth, 31, Section.GUIDE,
                Component.translatable("screen.gravesown.codex_hub.guide"), mouseX, mouseY);
        int closeY = 11;
        renderButton(graphics, this.width - 40, closeY, 26, 26, Component.literal("X"),
                inside(mouseX, mouseY, this.width - 40, closeY, 26, 26), true);
    }

    private void renderTab(
            GuiGraphics graphics, int x, int y, int width, int height, Section target, Component label,
            int mouseX, int mouseY
    ) {
        boolean selected = this.section == target;
        boolean hovered = inside(mouseX, mouseY, x, y, width, height);
        graphics.fill(x, y, x + width, y + height, selected ? BORDER : BORDER_DARK);
        graphics.fill(x + 2, y + 2, x + width - 2, y + height - 2,
                selected ? GravesownUiPalette.SELECTED : hovered ? PANEL_LIGHT : PANEL);
        graphics.drawCenteredString(this.font, label, x + width / 2, y + 11, selected ? TEXT : MUTED);
    }

    private void renderStory(GuiGraphics graphics, int mouseX, int mouseY) {
        clampStoryPan();
        int canvasX = 14;
        int canvasY = contentTop();
        int canvasRight = storyCanvasRight();
        int canvasBottom = this.height - 14;
        graphics.fill(canvasX, canvasY, canvasRight, canvasBottom, GravesownUiPalette.CANVAS);
        drawFrame(graphics, canvasX, canvasY, canvasRight, canvasBottom);
        graphics.enableScissor(canvasX + 2, canvasY + 2, canvasRight - 2, canvasBottom - 2);
        graphics.pose().pushPose();
        graphics.pose().translate(canvasX + this.panX, canvasY + this.panY + compactStoryOffsetY(), 0.0F);
        graphics.pose().scale((float) this.zoom, (float) this.zoom, 1.0F);
        renderQuestConnections(graphics);
        for (QuestNode quest : QUESTS) {
            int worldMouseX = Mth.floor((mouseX - canvasX - this.panX) / this.zoom);
            int worldMouseY = Mth.floor((mouseY - canvasY - this.panY - compactStoryOffsetY()) / this.zoom);
            renderQuestNode(graphics, quest, quest.contains(worldMouseX, worldMouseY));
        }
        graphics.pose().popPose();
        graphics.disableScissor();
        renderQuestDetails(graphics, canvasRight + 10, canvasY, this.width - 14, canvasBottom, mouseX, mouseY);
        graphics.fill(canvasX + 2, canvasBottom - 20, canvasRight - 2, canvasBottom - 2, GravesownUiPalette.BACKDROP);
        graphics.enableScissor(canvasX + 4, canvasBottom - 18, canvasRight - 4, canvasBottom - 3);
        graphics.drawString(this.font, Component.translatable("screen.gravesown.codex_hub.pan_hint"),
                canvasX + 8, canvasBottom - 15, MUTED, false);
        graphics.disableScissor();
    }

    private void renderQuestConnections(GuiGraphics graphics) {
        for (int i = 1; i < QUESTS.size(); i++) {
            QuestNode from = QUESTS.get(i - 1);
            QuestNode to = QUESTS.get(i);
            int color = isClaimed(i - 1) ? CLAIMED : BORDER_DARK;
            int startX = from.x() + NODE_WIDTH;
            int startY = from.y() + NODE_HEIGHT / 2;
            int endX = to.x();
            int endY = to.y() + NODE_HEIGHT / 2;
            int middleX = (startX + endX) / 2;
            graphics.hLine(startX, middleX, startY, color);
            graphics.vLine(middleX, Math.min(startY, endY), Math.max(startY, endY), color);
            graphics.hLine(middleX, endX, endY, color);
        }
    }

    private void renderQuestNode(GuiGraphics graphics, QuestNode quest, boolean hovered) {
        QuestStatus status = status(quest.index());
        int color = statusColor(status);
        int x = quest.x();
        int y = quest.y();
        if (hovered || this.selectedQuest == quest.index()) {
            graphics.fill(x - 3, y - 3, x + NODE_WIDTH + 3, y + NODE_HEIGHT + 3, READY);
        }
        graphics.fill(x, y, x + NODE_WIDTH, y + NODE_HEIGHT, color);
        graphics.fill(x + 2, y + 2, x + NODE_WIDTH - 2, y + NODE_HEIGHT - 2,
                status == QuestStatus.LOCKED ? GravesownUiPalette.PANEL_DARK : PANEL_LIGHT);
        graphics.renderItem(quest.icon().get(), x + 8, y + 17);
        List<FormattedCharSequence> lines = this.font.split(Component.translatable(quest.titleKey()), 101);
        for (int line = 0; line < Math.min(lines.size(), 2); line++) {
            graphics.drawString(this.font, lines.get(line), x + 30, y + 10 + line * 10,
                    status == QuestStatus.LOCKED ? MUTED : TEXT, false);
        }
        graphics.drawString(this.font, Component.translatable(status.key), x + 30, y + 35, color, false);
    }

    private void renderQuestDetails(
            GuiGraphics graphics, int left, int top, int right, int bottom, int mouseX, int mouseY
    ) {
        graphics.fill(left, top, right, bottom, PANEL);
        drawFrame(graphics, left, top, right, bottom);
        QuestNode quest = QUESTS.get(this.selectedQuest);
        QuestStatus status = status(quest.index());
        graphics.renderItem(quest.icon().get(), left + 14, top + 16);
        graphics.drawString(this.font, Component.translatable(quest.titleKey()), left + 39, top + 19, TEXT, false);
        graphics.hLine(left + 12, right - 12, top + 43, BORDER_DARK);
        List<FormattedCharSequence> description = this.font.split(
                Component.translatable(quest.descriptionKey()), Math.max(80, right - left - 24));
        int descriptionY = compactLayout() ? top + 69 : top + 56;
        int buttonY = compactLayout() ? bottom - 37 : bottom - 40;
        int buttonHeight = compactLayout() ? 25 : 27;
        int maxDescriptionLines = Math.max(1, (buttonY - descriptionY - 2) / 11);
        for (int line = 0; line < Math.min(description.size(), maxDescriptionLines); line++) {
            graphics.drawString(this.font, description.get(line), left + 12, descriptionY + line * 11, MUTED, false);
        }
        int statusY = compactLayout() ? top + 51 : bottom - 60;
        graphics.drawString(this.font, Component.translatable(status.key), left + 12, statusY,
                statusColor(status), false);
        boolean claimable = status == QuestStatus.READY;
        int buttonX = left + 12;
        renderButton(graphics, buttonX, buttonY, right - left - 24, buttonHeight,
                Component.translatable(claimable
                        ? "screen.gravesown.codex_hub.claim"
                        : status == QuestStatus.CLAIMED
                                ? "screen.gravesown.codex_hub.claimed"
                                : "screen.gravesown.codex_hub.conditions"),
                inside(mouseX, mouseY, buttonX, buttonY, right - left - 24, 27), claimable);
    }

    private void renderCrafts(GuiGraphics graphics, int mouseX, int mouseY) {
        CraftLayout layout = craftLayout();
        if (this.recipeSearch != null) {
            this.recipeSearch.setVisible(true);
        }
        graphics.fill(layout.outerLeft(), layout.outerTop(), layout.outerRight(), layout.outerBottom(), GravesownUiPalette.CANVAS);
        drawFrame(graphics, layout.outerLeft(), layout.outerTop(), layout.outerRight(), layout.outerBottom());
        renderRecipeResults(graphics, layout, mouseX, mouseY);
        renderSelectedRecipe(graphics, layout, mouseX, mouseY);
        renderRecipeCategories(graphics, layout, mouseX, mouseY);
    }

    private void renderRecipeResults(GuiGraphics graphics, CraftLayout layout, int mouseX, int mouseY) {
        graphics.fill(layout.listLeft() - 4, layout.searchTop() - 4,
                layout.listRight() + 4, layout.resultsBottom() + 4, PANEL);
        drawFrame(graphics, layout.listLeft() - 4, layout.searchTop() - 4,
                layout.listRight() + 4, layout.resultsBottom() + 4);

        List<Integer> filtered = filteredRecipeIndexes();
        ensureVisibleRecipeSelection(filtered);
        int rowHeight = compactCraftLayout() ? 24 : 34;
        int viewportHeight = layout.resultsBottom() - layout.resultsTop();
        double minimum = Math.min(0.0D, viewportHeight - filtered.size() * rowHeight);
        this.recipeListScroll = Mth.clamp(this.recipeListScroll, minimum, 0.0D);

        graphics.enableScissor(layout.listLeft(), layout.resultsTop(), layout.listRight(), layout.resultsBottom());
        if (filtered.isEmpty()) {
            List<FormattedCharSequence> lines = this.font.split(
                    Component.translatable("screen.gravesown.codex_hub.no_results"),
                    Math.max(60, layout.listRight() - layout.listLeft() - 8)
            );
            for (int line = 0; line < lines.size(); line++) {
                graphics.drawString(this.font, lines.get(line), layout.listLeft() + 4,
                        layout.resultsTop() + 5 + line * 11, MUTED, false);
            }
        }
        for (int visibleIndex = 0; visibleIndex < filtered.size(); visibleIndex++) {
            int recipeIndex = filtered.get(visibleIndex);
            int y = layout.resultsTop() + visibleIndex * rowHeight + (int) this.recipeListScroll;
            if (y + rowHeight < layout.resultsTop() || y > layout.resultsBottom()) {
                continue;
            }
            renderRecipeResultRow(
                    graphics,
                    RECIPES.get(recipeIndex),
                    recipeIndex,
                    layout.listLeft(),
                    y,
                    layout.listRight() - layout.listLeft(),
                    rowHeight,
                    mouseX,
                    mouseY
            );
        }
        graphics.disableScissor();
    }

    private void renderRecipeResultRow(
            GuiGraphics graphics,
            SurvivalGuideRecipes.RecipeEntry recipe,
            int recipeIndex,
            int x,
            int y,
            int width,
            int height,
            int mouseX,
            int mouseY
    ) {
        boolean hovered = inside(mouseX, mouseY, x, y, width, height);
        boolean selected = recipeIndex == this.selectedRecipe;
        int edge = selected ? READY : hovered ? BORDER : BORDER_DARK;
        graphics.fill(x, y, x + width, y + height, edge);
        graphics.fill(x + 2, y + 2, x + width - 2, y + height - 2,
                selected ? GravesownUiPalette.SELECTED : PANEL_LIGHT);
        ItemStack output = recipe.output().get();
        graphics.renderItem(output, x + 4, y + (height - 18) / 2);
        graphics.renderItemDecorations(this.font, output, x + 4, y + (height - 18) / 2);
        graphics.drawString(this.font, fitText(output.getHoverName(), width - 30), x + 25,
                y + (compactCraftLayout() ? 8 : 6), TEXT, false);
        if (!compactCraftLayout()) {
            graphics.drawString(this.font,
                    Component.translatable("screen.gravesown.codex_hub.station." + recipe.station()),
                    x + 25, y + 19, MUTED, false);
        }
        if (hovered) {
            this.hoveredCraftStack = output;
        }
    }

    private void renderSelectedRecipe(GuiGraphics graphics, CraftLayout layout, int mouseX, int mouseY) {
        int bottom = layout.detailBottom();
        graphics.fill(layout.detailLeft(), layout.detailTop(), layout.detailRight(), bottom, PANEL);
        drawFrame(graphics, layout.detailLeft(), layout.detailTop(), layout.detailRight(), bottom);
        if (this.selectedRecipe < 0 || this.selectedRecipe >= RECIPES.size()) {
            return;
        }

        SurvivalGuideRecipes.RecipeEntry recipe = RECIPES.get(this.selectedRecipe);
        int slotSize = compactCraftLayout() ? 14 : 20;
        int gridX = layout.detailLeft() + 8;
        int gridY = layout.detailTop() + 7;
        if (recipe.kind() == SurvivalGuideRecipes.RecipeKind.KITCHEN) {
            for (int column = 0; column < 3; column++) {
                renderGuideSlot(graphics, recipe.cell(column, 0),
                        gridX + column * slotSize, gridY, slotSize, mouseX, mouseY);
            }
            int utensilX = gridX + slotSize / 2;
            for (int column = 0; column < 2; column++) {
                renderGuideSlot(graphics, recipe.cell(column, 1),
                        utensilX + column * slotSize, gridY + slotSize, slotSize, mouseX, mouseY);
            }
        }
        else {
            for (int row = 0; row < recipe.gridHeight(); row++) {
                for (int column = 0; column < recipe.gridWidth(); column++) {
                    ItemStack ingredient = recipe.cell(column, row);
                    int x = gridX + column * slotSize;
                    int y = gridY + row * slotSize;
                    renderGuideSlot(graphics, ingredient, x, y, slotSize, mouseX, mouseY);
                }
            }
        }

        int gridRight = gridX + recipe.gridWidth() * slotSize;
        int gridHeight = recipe.gridHeight() * slotSize;
        int outputX = gridRight + (compactCraftLayout() ? 15 : 24);
        int outputY = gridY + Math.max(0, (gridHeight - 16) / 2);
        drawRightArrow(
                graphics,
                gridRight + 3,
                outputX - 3,
                gridY + Math.max(4, gridHeight / 2),
                READY
        );
        ItemStack output = recipe.output().get();
        graphics.renderItem(output, outputX, outputY);
        graphics.renderItemDecorations(this.font, output, outputX, outputY);
        if (inside(mouseX, mouseY, outputX, outputY, 16, 16)) {
            this.hoveredCraftStack = output;
        }

        int textX = outputX + 21;
        int textRight = layout.detailRight() - 7;
        graphics.enableScissor(textX, layout.detailTop() + 3, textRight, bottom - 3);
        graphics.drawString(this.font, output.getHoverName(), textX, layout.detailTop() + 7, TEXT, false);
        graphics.drawString(this.font,
                Component.translatable("screen.gravesown.codex_hub.station." + recipe.station()),
                textX, layout.detailTop() + 20, READY, false);
        graphics.drawString(this.font, Component.translatable(recipe.kind().translationKey()),
                textX, layout.detailTop() + 33, MUTED, false);
        graphics.disableScissor();
    }

    private void renderRecipeCategories(GuiGraphics graphics, CraftLayout layout, int mouseX, int mouseY) {
        graphics.fill(layout.categoryLeft(), layout.detailTop(), layout.categoryRight(), layout.detailBottom(), PANEL);
        drawFrame(graphics, layout.categoryLeft(), layout.detailTop(), layout.categoryRight(), layout.detailBottom());
        graphics.drawCenteredString(this.font,
                Component.translatable("screen.gravesown.codex_hub.categories"),
                (layout.categoryLeft() + layout.categoryRight()) / 2, layout.detailTop() + 8, READY);
        int buttonHeight = compactCraftLayout() ? 21 : 24;
        ScrollRail rail = new ScrollRail(
                layout.categoryLeft() + 5,
                layout.detailTop() + 23,
                layout.categoryRight() - 5,
                layout.detailBottom() - 5,
                buttonHeight,
                5,
                RecipeCategory.values().length
        );
        this.recipeCategoryScroll = rail.clampScroll(this.recipeCategoryScroll);
        int hoveredIndex = rail.itemIndexAt(mouseX, mouseY, this.recipeCategoryScroll);
        int buttonRight = rail.buttonRight();
        graphics.enableScissor(rail.left(), rail.top(), rail.right(), rail.bottom());
        RecipeCategory[] categories = RecipeCategory.values();
        for (int index = 0; index < categories.length; index++) {
            RecipeCategory category = categories[index];
            int buttonY = rail.itemY(index, this.recipeCategoryScroll);
            if (buttonY + buttonHeight <= rail.top() || buttonY >= rail.bottom()) {
                continue;
            }
            boolean selected = this.recipeCategory == category;
            boolean hovered = hoveredIndex == index;
            int edge = selected ? READY : hovered ? BORDER : BORDER_DARK;
            graphics.fill(rail.left(), buttonY, buttonRight, buttonY + buttonHeight, edge);
            graphics.fill(rail.left() + 2, buttonY + 2,
                    buttonRight - 2, buttonY + buttonHeight - 2,
                    selected ? GravesownUiPalette.SELECTED : PANEL_LIGHT);
            graphics.drawCenteredString(this.font, Component.translatable(category.translationKey),
                    (rail.left() + buttonRight) / 2,
                    buttonY + (buttonHeight - 8) / 2, selected ? TEXT : MUTED);
        }
        graphics.disableScissor();
        renderRailScrollbar(graphics, rail, this.recipeCategoryScroll);
    }

    private void renderGuideSlot(
            GuiGraphics graphics, ItemStack stack, int x, int y, int size, int mouseX, int mouseY
    ) {
        graphics.fill(x, y, x + size, y + size, BORDER_DARK);
        graphics.fill(x + 1, y + 1, x + size - 1, y + size - 1, GravesownUiPalette.PANEL_DARK);
        if (stack.isEmpty()) {
            return;
        }
        renderScaledItem(graphics, stack, x + 1, y + 1, size - 2);
        if (inside(mouseX, mouseY, x, y, size, size)) {
            this.hoveredCraftStack = stack;
        }
    }

    private void renderChain(GuiGraphics graphics, int mouseX, int mouseY) {
        ChainViewport viewport = chainViewport();
        graphics.fill(viewport.left(), viewport.top(), viewport.right(), viewport.bottom(), GravesownUiPalette.CANVAS);
        drawFrame(graphics, viewport.left(), viewport.top(), viewport.right(), viewport.bottom());
        if (this.chainRecipe < 0 || this.chainRecipe >= RECIPES.size()) {
            return;
        }

        SurvivalGuideRecipes.RecipeEntry selected = RECIPES.get(this.chainRecipe);
        CreationGraph graph = buildCreationGraph(selected);
        int titleBottom = viewport.top() + 34;
        graphics.fill(viewport.left() + 1, viewport.top() + 1, viewport.right() - 1, titleBottom, PANEL);
        graphics.drawString(this.font, Component.translatable("screen.gravesown.codex_hub.creation_path"),
                viewport.left() + 10, viewport.top() + 7, READY, false);
        graphics.drawString(this.font, fitText(selected.output().get().getHoverName(),
                        Math.max(80, viewport.right() - viewport.left() - 310)),
                viewport.left() + 10, viewport.top() + 19, TEXT, false);
        int pinWidth = compactLayout() ? 104 : 148;
        int pinLeft = viewport.right() - pinWidth - 8;
        boolean pinHovered = inside(mouseX, mouseY, pinLeft, viewport.top() + 6, pinWidth, 22);
        int pinEdge = this.chainPinned ? READY : pinHovered ? BORDER : BORDER_DARK;
        graphics.fill(pinLeft, viewport.top() + 6, viewport.right() - 8, viewport.top() + 28, pinEdge);
        graphics.fill(pinLeft + 2, viewport.top() + 8, viewport.right() - 10, viewport.top() + 26,
                this.chainPinned ? GravesownUiPalette.SELECTED : PANEL_LIGHT);
        graphics.drawCenteredString(this.font, Component.translatable(this.chainPinned
                        ? "screen.gravesown.codex_hub.chain.unpin"
                        : "screen.gravesown.codex_hub.chain.pin"),
                pinLeft + pinWidth / 2, viewport.top() + 13, this.chainPinned ? READY : TEXT);

        int graphTop = titleBottom + 1;
        int graphHeight = viewport.bottom() - graphTop - 1;
        if (!this.chainViewInitialized) {
            double availableWidth = Math.max(1.0D, viewport.right() - viewport.left() - 32.0D);
            double availableHeight = Math.max(1.0D, graphHeight - 32.0D);
            this.chainZoom = Mth.clamp(
                    Math.min(availableWidth / graph.width(), availableHeight / graph.height()),
                    0.42D,
                    1.0D
            );
            this.chainPanX = Math.max(16.0D,
                    (viewport.right() - viewport.left() - graph.width() * this.chainZoom) / 2.0D);
            this.chainPanY = Math.max(16.0D, (graphHeight - graph.height() * this.chainZoom) / 2.0D);
            this.chainViewInitialized = true;
        }

        int worldMouseX = Mth.floor((mouseX - viewport.left() - this.chainPanX) / this.chainZoom);
        int worldMouseY = Mth.floor((mouseY - graphTop - this.chainPanY) / this.chainZoom);
        graphics.enableScissor(viewport.left() + 1, graphTop, viewport.right() - 1, viewport.bottom() - 1);
        graphics.pose().pushPose();
        graphics.pose().translate(viewport.left() + this.chainPanX, graphTop + this.chainPanY, 0.0F);
        graphics.pose().scale((float)this.chainZoom, (float)this.chainZoom, 1.0F);
        renderCreationEdges(graphics, graph);
        for (CreationNode node : graph.nodes()) {
            renderCreationNode(graphics, node, node.contains(worldMouseX, worldMouseY));
        }
        graphics.pose().popPose();
        graphics.disableScissor();
    }

    private void renderCreationEdges(GuiGraphics graphics, CreationGraph graph) {
        for (CreationEdge edge : graph.edges()) {
            CreationNode from = graph.nodes().get(edge.from());
            CreationNode to = graph.nodes().get(edge.to());
            int startX = from.x() + CreationNode.WIDTH;
            int startY = from.y() + CreationNode.HEIGHT / 2;
            int endX = to.x();
            int endY = to.y() + CreationNode.HEIGHT / 2;
            int middleX = (startX + endX) / 2;
            graphics.hLine(startX, middleX, startY, BORDER);
            graphics.vLine(middleX, Math.min(startY, endY), Math.max(startY, endY), BORDER);
            graphics.hLine(middleX, endX - 1, endY, BORDER);
            drawArrowHead(graphics, endX - 1, endY, READY);
        }
    }

    private void renderCreationNode(GuiGraphics graphics, CreationNode node, boolean hovered) {
        int edge = node.target() ? READY : hovered ? BORDER : BORDER_DARK;
        graphics.fill(node.x(), node.y(), node.x() + CreationNode.WIDTH, node.y() + CreationNode.HEIGHT, edge);
        graphics.fill(node.x() + 2, node.y() + 2,
                node.x() + CreationNode.WIDTH - 2, node.y() + CreationNode.HEIGHT - 2,
                node.target() ? GravesownUiPalette.SELECTED : PANEL_LIGHT);
        renderScaledItem(graphics, node.stack(), node.x() + 7, node.y() + 10, 20);
        graphics.drawString(this.font, fitText(node.stack().getHoverName(), CreationNode.WIDTH - 39),
                node.x() + 34, node.y() + 9, TEXT, false);
        graphics.drawString(this.font, Component.translatable(node.craftable()
                        ? "screen.gravesown.codex_hub.chain.open_recipe"
                        : "screen.gravesown.codex_hub.chain.acquisition"),
                node.x() + 34, node.y() + 24, node.craftable() ? READY : MUTED, false);
        if (hovered) {
            this.hoveredCraftStack = node.stack();
        }
    }

    private void renderGuide(GuiGraphics graphics, int mouseX, int mouseY) {
        ChainViewport viewport = chainViewport();
        int sidebarWidth = compactLayout() ? 104 : 154;
        int sidebarRight = viewport.left() + sidebarWidth;
        graphics.fill(viewport.left(), viewport.top(), viewport.right(), viewport.bottom(), GravesownUiPalette.CANVAS);
        drawFrame(graphics, viewport.left(), viewport.top(), viewport.right(), viewport.bottom());
        graphics.fill(viewport.left() + 1, viewport.top() + 1, sidebarRight, viewport.bottom() - 1, PANEL);
        graphics.vLine(sidebarRight, viewport.top() + 1, viewport.bottom() - 2, BORDER_DARK);

        int buttonHeight = compactLayout() ? 34 : 42;
        ScrollRail topicRail = new ScrollRail(
                viewport.left() + 7,
                viewport.top() + 10,
                sidebarRight - 7,
                viewport.bottom() - 10,
                buttonHeight,
                6,
                GuidePage.values().length
        );
        this.guideTopicScroll = topicRail.clampScroll(this.guideTopicScroll);
        int hoveredTopic = topicRail.itemIndexAt(mouseX, mouseY, this.guideTopicScroll);
        int buttonRight = topicRail.buttonRight();
        graphics.enableScissor(topicRail.left(), topicRail.top(), topicRail.right(), topicRail.bottom());
        GuidePage[] pages = GuidePage.values();
        for (int index = 0; index < pages.length; index++) {
            GuidePage page = pages[index];
            int buttonY = topicRail.itemY(index, this.guideTopicScroll);
            if (buttonY + buttonHeight <= topicRail.top() || buttonY >= topicRail.bottom()) {
                continue;
            }
            boolean selected = page == this.selectedGuidePage;
            boolean hovered = hoveredTopic == index;
            int edge = selected ? READY : hovered ? BORDER : BORDER_DARK;
            graphics.fill(topicRail.left(), buttonY, buttonRight, buttonY + buttonHeight, edge);
            graphics.fill(topicRail.left() + 2, buttonY + 2,
                    buttonRight - 2, buttonY + buttonHeight - 2,
                    selected ? GravesownUiPalette.SELECTED : PANEL_LIGHT);
            ItemStack icon = page.icon.get();
            graphics.renderItem(icon, topicRail.left() + 7, buttonY + (buttonHeight - 16) / 2);
            graphics.drawString(this.font,
                    fitText(Component.translatable(page.titleKey), Math.max(24, buttonRight - topicRail.left() - 35)),
                    topicRail.left() + 28, buttonY + (buttonHeight - 8) / 2,
                    selected ? TEXT : MUTED, false);
        }
        graphics.disableScissor();
        renderRailScrollbar(graphics, topicRail, this.guideTopicScroll);

        int contentLeft = sidebarRight + 1;
        int contentRight = viewport.right() - 1;
        int contentTop = viewport.top() + 1;
        int contentBottom = viewport.bottom() - 1;
        int contentWidth = contentRight - contentLeft - 32;
        int estimatedHeight = guideContentHeight(this.selectedGuidePage, Math.max(120, contentWidth));
        double minimum = Math.min(0.0D, contentBottom - contentTop - estimatedHeight - 26.0D);
        this.guideScroll = Mth.clamp(this.guideScroll, minimum, 0.0D);
        graphics.enableScissor(contentLeft, contentTop, contentRight, contentBottom);
        int x = contentLeft + 16;
        int y = contentTop + 16 + (int)this.guideScroll;
        graphics.drawString(this.font, Component.translatable(this.selectedGuidePage.titleKey), x, y, READY, false);
        y += 18;
        y = drawWrappedGuideText(graphics, Component.translatable(this.selectedGuidePage.introKey), x, y,
                contentWidth, MUTED) + 12;
        for (int step = 1; step <= this.selectedGuidePage.steps; step++) {
            Component stepText = Component.translatable(this.selectedGuidePage.stepKey(step));
            List<FormattedCharSequence> lines = this.font.split(stepText, Math.max(80, contentWidth - 34));
            int cardHeight = Math.max(34, 16 + lines.size() * 11);
            graphics.fill(x, y, x + contentWidth, y + cardHeight, PANEL_LIGHT);
            drawFrame(graphics, x, y, x + contentWidth, y + cardHeight);
            graphics.drawCenteredString(this.font, Integer.toString(step), x + 14, y + (cardHeight - 8) / 2, READY);
            for (int line = 0; line < lines.size(); line++) {
                graphics.drawString(this.font, lines.get(line), x + 29, y + 8 + line * 11, TEXT, false);
            }
            y += cardHeight + 8;
        }
        y += 4;
        drawWrappedGuideText(graphics, Component.translatable(this.selectedGuidePage.tipKey),
                x, y, contentWidth, CLAIMED);
        graphics.disableScissor();
    }

    private int drawWrappedGuideText(
            GuiGraphics graphics, Component text, int x, int y, int width, int color
    ) {
        List<FormattedCharSequence> lines = this.font.split(text, width);
        for (int line = 0; line < lines.size(); line++) {
            graphics.drawString(this.font, lines.get(line), x, y + line * 11, color, false);
        }
        return y + lines.size() * 11;
    }

    private int guideContentHeight(GuidePage page, int width) {
        int height = 46 + this.font.split(Component.translatable(page.introKey), width).size() * 11;
        for (int step = 1; step <= page.steps; step++) {
            int lineCount = this.font.split(Component.translatable(page.stepKey(step)), Math.max(80, width - 34)).size();
            height += Math.max(34, 16 + lineCount * 11) + 8;
        }
        return height + this.font.split(Component.translatable(page.tipKey), width).size() * 11 + 24;
    }

    private void renderScaledItem(GuiGraphics graphics, ItemStack stack, int x, int y, int targetSize) {
        float scale = targetSize / 16.0F;
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0.0F);
        graphics.pose().scale(scale, scale, 1.0F);
        graphics.renderItem(stack, 0, 0);
        graphics.pose().popPose();
    }

    private void renderToast(GuiGraphics graphics, float partialTick) {
        if (this.toastTicks <= 0 || this.toastQuest < 0) {
            return;
        }
        float enter = Mth.clamp((110 - this.toastTicks + partialTick) / 10.0F, 0.0F, 1.0F);
        float leave = Mth.clamp((this.toastTicks + partialTick) / 10.0F, 0.0F, 1.0F);
        float visibility = Math.min(enter, leave);
        int width = 220;
        int x = this.width - Mth.floor(width * visibility);
        int y = contentTop();
        QuestNode quest = QUESTS.get(this.toastQuest);
        graphics.fill(x, y, x + width, y + 54, CLAIMED);
        graphics.fill(x + 2, y + 2, x + width, y + 52, PANEL_LIGHT);
        graphics.renderItem(quest.icon().get(), x + 10, y + 18);
        graphics.drawString(this.font, Component.translatable("screen.gravesown.codex_hub.completed"),
                x + 34, y + 10, CLAIMED, false);
        graphics.drawString(this.font, Component.translatable(quest.titleKey()), x + 34, y + 28, TEXT, false);
    }

    private void renderButton(
            GuiGraphics graphics, int x, int y, int width, int height, Component label, boolean hovered, boolean enabled
    ) {
        int edge = enabled ? (hovered ? READY : BORDER) : BORDER_DARK;
        graphics.fill(x, y, x + width, y + height, edge);
        graphics.fill(x + 2, y + 2, x + width - 2, y + height - 2, enabled ? PANEL_LIGHT : PANEL);
        graphics.drawCenteredString(this.font, label, x + width / 2, y + (height - 8) / 2, enabled ? TEXT : MUTED);
    }

    private static void drawRightArrow(GuiGraphics graphics, int startX, int endX, int y, int color) {
        graphics.hLine(startX, Math.max(startX, endX - 1), y, color);
        drawArrowHead(graphics, endX, y, color);
    }

    private static void drawArrowHead(GuiGraphics graphics, int x, int y, int color) {
        graphics.fill(x - 3, y - 2, x - 1, y - 1, color);
        graphics.fill(x - 2, y - 1, x, y, color);
        graphics.fill(x - 2, y + 1, x, y + 2, color);
        graphics.fill(x - 3, y + 2, x - 1, y + 3, color);
    }

    private void drawFrame(GuiGraphics graphics, int left, int top, int right, int bottom) {
        graphics.fill(left, top, right, top + 1, BORDER);
        graphics.fill(left, bottom - 1, right, bottom, BORDER);
        graphics.fill(left, top, left + 1, bottom, BORDER);
        graphics.fill(right - 1, top, right, bottom, BORDER);
    }

    private void renderRailScrollbar(GuiGraphics graphics, ScrollRail rail, double scroll) {
        if (!rail.overflowing()) {
            return;
        }
        int trackLeft = rail.right() - 3;
        int trackRight = rail.right() - 1;
        graphics.fill(trackLeft, rail.top(), trackRight, rail.bottom(), GravesownUiPalette.PANEL_DARK);
        int viewportHeight = rail.viewportHeight();
        int thumbHeight = Math.max(10,
                Mth.floor((double) viewportHeight * viewportHeight / rail.contentHeight()));
        double progress = rail.minimumScroll() == 0.0D ? 0.0D : scroll / rail.minimumScroll();
        int thumbTop = rail.top() + Mth.floor((viewportHeight - thumbHeight) * Mth.clamp(progress, 0.0D, 1.0D));
        graphics.fill(trackLeft, thumbTop, trackRight, thumbTop + thumbHeight, BORDER);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        if (this.recipeSearch != null
                && this.recipeSearch.visible
                && !inside(mouseX, mouseY, this.recipeSearch.getX(), this.recipeSearch.getY(),
                this.recipeSearch.getWidth(), this.recipeSearch.getHeight())) {
            this.recipeSearch.setFocused(false);
        }
        int closeY = 11;
        if (inside(mouseX, mouseY, this.width - 40, closeY, 26, 26)) {
            this.onClose();
            return true;
        }
        int tabY = 11;
        int tabGap = compactLayout() ? 5 : 8;
        int tabWidth = compactLayout() ? Math.max(58, (this.width - 80) / 4) : 110;
        int tabsWidth = tabWidth * 4 + tabGap * 3;
        int storyTabX = compactLayout() ? 14 : (this.width - tabsWidth) / 2;
        int craftsTabX = storyTabX + tabWidth + tabGap;
        int chainTabX = craftsTabX + tabWidth + tabGap;
        int guideTabX = chainTabX + tabWidth + tabGap;
        if (inside(mouseX, mouseY, storyTabX, tabY, tabWidth, 31)) {
            setSection(Section.STORY);
            return true;
        }
        if (inside(mouseX, mouseY, craftsTabX, tabY, tabWidth, 31)) {
            setSection(Section.CRAFTS);
            return true;
        }
        if (inside(mouseX, mouseY, chainTabX, tabY, tabWidth, 31)) {
            setSection(Section.CHAIN);
            return true;
        }
        if (inside(mouseX, mouseY, guideTabX, tabY, tabWidth, 31)) {
            setSection(Section.GUIDE);
            return true;
        }
        if (this.section == Section.STORY) {
            int canvasRight = storyCanvasRight();
            int detailsLeft = canvasRight + 10;
            int bottom = this.height - 14;
            int buttonY = compactLayout() ? bottom - 37 : bottom - 40;
            int buttonHeight = compactLayout() ? 25 : 27;
            QuestStatus selectedStatus = status(this.selectedQuest);
            if (selectedStatus == QuestStatus.READY
                    && inside(mouseX, mouseY, detailsLeft + 12, buttonY,
                    this.width - 14 - detailsLeft - 24, buttonHeight)) {
                PacketDistributor.sendToServer(new ClaimCodexQuestPayload(this.selectedQuest));
                return true;
            }
            int worldX = Mth.floor((mouseX - 14 - this.panX) / this.zoom);
            int worldY = Mth.floor((mouseY - contentTop() - this.panY - compactStoryOffsetY()) / this.zoom);
            for (QuestNode quest : QUESTS) {
                if (quest.contains(worldX, worldY)) {
                    this.selectedQuest = quest.index();
                    return true;
                }
            }
            if (inside(mouseX, mouseY, 14, contentTop(), canvasRight - 14, bottom - contentTop())) {
                this.dragging = true;
                return true;
            }
        } else if (this.section == Section.CRAFTS) {
            CraftLayout layout = craftLayout();
            int categoryHeight = compactCraftLayout() ? 21 : 24;
            ScrollRail categoryRail = new ScrollRail(
                    layout.categoryLeft() + 5,
                    layout.detailTop() + 23,
                    layout.categoryRight() - 5,
                    layout.detailBottom() - 5,
                    categoryHeight,
                    5,
                    RecipeCategory.values().length
            );
            this.recipeCategoryScroll = categoryRail.clampScroll(this.recipeCategoryScroll);
            int categoryIndex = categoryRail.itemIndexAt(mouseX, mouseY, this.recipeCategoryScroll);
            if (categoryIndex >= 0) {
                this.recipeCategory = RecipeCategory.values()[categoryIndex];
                this.recipeListScroll = 0.0D;
                ensureVisibleRecipeSelection(filteredRecipeIndexes());
                return true;
            }
            List<Integer> filtered = filteredRecipeIndexes();
            int rowHeight = compactCraftLayout() ? 24 : 34;
            if (inside(mouseX, mouseY, layout.listLeft(), layout.resultsTop(),
                    layout.listRight() - layout.listLeft(), layout.resultsBottom() - layout.resultsTop())) {
                int visibleIndex = Mth.floor((mouseY - layout.resultsTop() - this.recipeListScroll) / rowHeight);
                if (visibleIndex >= 0 && visibleIndex < filtered.size()) {
                    selectRecipe(filtered.get(visibleIndex));
                    return true;
                }
            }
        } else if (this.section == Section.CHAIN) {
            ChainViewport viewport = chainViewport();
            int pinWidth = compactLayout() ? 104 : 148;
            int pinLeft = viewport.right() - pinWidth - 8;
            if (inside(mouseX, mouseY, pinLeft, viewport.top() + 6, pinWidth, 22)) {
                this.chainPinned = !this.chainPinned;
                if (!this.chainPinned) {
                    this.chainRecipe = this.selectedRecipe;
                    this.chainViewInitialized = false;
                }
                return true;
            }
            CreationNode node = creationNodeAt(mouseX, mouseY);
            if (node != null && node.craftable()) {
                int recipeIndex = SurvivalGuideRecipes.indexOfOutput(node.stack().getItem());
                if (recipeIndex >= 0) {
                    if (this.recipeSearch != null) {
                        this.recipeSearch.setValue("");
                    }
                    this.recipeCategory = RecipeCategory.ALL;
                    selectRecipe(recipeIndex);
                    setSection(Section.CRAFTS);
                }
                return true;
            }
            if (inside(mouseX, mouseY, viewport.left(), viewport.top() + 35,
                    viewport.right() - viewport.left(), viewport.bottom() - viewport.top() - 35)) {
                this.chainDragging = true;
                return true;
            }
        } else {
            ChainViewport viewport = chainViewport();
            int sidebarWidth = compactLayout() ? 104 : 154;
            int buttonHeight = compactLayout() ? 34 : 42;
            ScrollRail topicRail = new ScrollRail(
                    viewport.left() + 7,
                    viewport.top() + 10,
                    viewport.left() + sidebarWidth - 7,
                    viewport.bottom() - 10,
                    buttonHeight,
                    6,
                    GuidePage.values().length
            );
            this.guideTopicScroll = topicRail.clampScroll(this.guideTopicScroll);
            int guideIndex = topicRail.itemIndexAt(mouseX, mouseY, this.guideTopicScroll);
            if (guideIndex >= 0) {
                this.selectedGuidePage = GuidePage.values()[guideIndex];
                this.guideScroll = 0.0D;
                return true;
            }
            if (inside(mouseX, mouseY, viewport.left() + sidebarWidth + 1, viewport.top() + 1,
                    viewport.right() - viewport.left() - sidebarWidth - 2,
                    viewport.bottom() - viewport.top() - 2)) {
                this.guideDragging = true;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.dragging && this.section == Section.STORY && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.panX += dragX;
            this.panY += dragY;
            clampStoryPan();
            return true;
        }
        if (this.chainDragging && this.section == Section.CHAIN
                && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.chainPanX += dragX;
            this.chainPanY += dragY;
            return true;
        }
        if (this.guideDragging && this.section == Section.GUIDE
                && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.guideScroll += dragY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.dragging = false;
        this.chainDragging = false;
        this.guideDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.section == Section.STORY) {
            int canvasX = 14;
            int canvasY = contentTop();
            int canvasRight = storyCanvasRight();
            int canvasBottom = this.height - 14;
            if (inside(mouseX, mouseY, canvasX, canvasY,
                    canvasRight - canvasX, canvasBottom - canvasY)) {
                double oldZoom = this.zoom;
                double newZoom = Mth.clamp(oldZoom + scrollY * 0.08D, 0.55D, 1.35D);
                double localX = mouseX - canvasX;
                double localY = mouseY - canvasY;
                double effectivePanY = this.panY + compactStoryOffsetY();
                this.panX = localX - (localX - this.panX) * newZoom / oldZoom;
                effectivePanY = localY - (localY - effectivePanY) * newZoom / oldZoom;
                this.panY = effectivePanY - compactStoryOffsetY();
                this.zoom = newZoom;
                clampStoryPan();
            }
        } else if (this.section == Section.CRAFTS) {
            CraftLayout layout = craftLayout();
            int categoryHeight = compactCraftLayout() ? 21 : 24;
            ScrollRail categoryRail = new ScrollRail(
                    layout.categoryLeft() + 5,
                    layout.detailTop() + 23,
                    layout.categoryRight() - 5,
                    layout.detailBottom() - 5,
                    categoryHeight,
                    5,
                    RecipeCategory.values().length
            );
            if (categoryRail.contains(mouseX, mouseY)) {
                this.recipeCategoryScroll = categoryRail.clampScroll(
                        this.recipeCategoryScroll + scrollY * categoryRail.stride()
                );
            } else if (inside(mouseX, mouseY, layout.listLeft(), layout.resultsTop(),
                    layout.listRight() - layout.listLeft(), layout.resultsBottom() - layout.resultsTop())) {
                int rowHeight = compactCraftLayout() ? 24 : 34;
                int count = filteredRecipeIndexes().size();
                double minimum = Math.min(0.0D,
                        layout.resultsBottom() - layout.resultsTop() - count * rowHeight);
                this.recipeListScroll = Mth.clamp(
                        this.recipeListScroll + scrollY * rowHeight,
                        minimum,
                        0.0D
                );
            }
        } else if (this.section == Section.CHAIN) {
            ChainViewport viewport = chainViewport();
            int graphTop = viewport.top() + 35;
            double oldZoom = this.chainZoom;
            double newZoom = Mth.clamp(oldZoom + scrollY * 0.10D, 0.42D, 1.65D);
            double localX = mouseX - viewport.left();
            double localY = mouseY - graphTop;
            this.chainPanX = localX - (localX - this.chainPanX) * newZoom / oldZoom;
            this.chainPanY = localY - (localY - this.chainPanY) * newZoom / oldZoom;
            this.chainZoom = newZoom;
        } else {
            ChainViewport viewport = chainViewport();
            int sidebarWidth = compactLayout() ? 104 : 154;
            int buttonHeight = compactLayout() ? 34 : 42;
            ScrollRail topicRail = new ScrollRail(
                    viewport.left() + 7,
                    viewport.top() + 10,
                    viewport.left() + sidebarWidth - 7,
                    viewport.bottom() - 10,
                    buttonHeight,
                    6,
                    GuidePage.values().length
            );
            if (topicRail.contains(mouseX, mouseY)) {
                this.guideTopicScroll = topicRail.clampScroll(
                        this.guideTopicScroll + scrollY * topicRail.stride()
                );
            } else if (inside(mouseX, mouseY, viewport.left() + sidebarWidth + 1, viewport.top() + 1,
                    viewport.right() - viewport.left() - sidebarWidth - 2,
                    viewport.bottom() - viewport.top() - 2)) {
                int contentWidth = Math.max(120, viewport.right() - viewport.left() - sidebarWidth - 34);
                int contentHeight = viewport.bottom() - viewport.top() - 2;
                double minimum = Math.min(0.0D,
                        contentHeight - guideContentHeight(this.selectedGuidePage, contentWidth) - 26.0D);
                this.guideScroll = Mth.clamp(this.guideScroll + scrollY * 28.0D, minimum, 0.0D);
            }
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (SurvivorCodexKeyHandler.OPEN_RECIPE_CHAIN.matches(keyCode, scanCode)
                && !this.hoveredCraftStack.isEmpty()
                && openCreationChain(this.hoveredCraftStack.getItem())) {
            return true;
        }
        if (this.section == Section.CRAFTS
                && Screen.hasControlDown()
                && keyCode == GLFW.GLFW_KEY_F
                && this.recipeSearch != null) {
            this.setFocused(this.recipeSearch);
            this.recipeSearch.setFocused(true);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_TAB
                && (this.recipeSearch == null || !this.recipeSearch.isFocused())) {
            setSection(switch (this.section) {
                case STORY -> Section.CRAFTS;
                case CRAFTS -> Section.CHAIN;
                case CHAIN -> Section.GUIDE;
                case GUIDE -> Section.STORY;
            });
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private QuestStatus status(int questIndex) {
        if (isClaimed(questIndex)) {
            return QuestStatus.CLAIMED;
        }
        if (questIndex > 0 && !isClaimed(questIndex - 1)) {
            return QuestStatus.LOCKED;
        }
        return SurvivorCodexProgress.isSet(this.conditionMask, questIndex)
                ? QuestStatus.READY
                : QuestStatus.ACTIVE;
    }

    private int firstUnclaimedQuest() {
        for (int index = 0; index < QUESTS.size(); index++) {
            if (!isClaimed(index)) {
                return index;
            }
        }
        return QUESTS.size() - 1;
    }

    private boolean isClaimed(int index) {
        return SurvivorCodexProgress.isSet(this.claimedMask, index);
    }

    private boolean compactLayout() {
        return this.width < COMPACT_WIDTH;
    }

    private int headerHeight() {
        return 54;
    }

    private int contentTop() {
        return headerHeight() + 12;
    }

    private int compactStoryOffsetY() {
        return compactLayout() ? -55 : 0;
    }

    private int storyCanvasRight() {
        return compactLayout() ? Math.max(214, this.width - 197) : Math.max(264, this.width - 232);
    }

    private void clampStoryPan() {
        int viewportWidth = Math.max(1, storyCanvasRight() - 14 - 4);
        int viewportHeight = Math.max(1, this.height - 14 - contentTop() - 22);
        int minX = QUESTS.stream().mapToInt(QuestNode::x).min().orElse(0);
        int maxX = QUESTS.stream().mapToInt(quest -> quest.x() + NODE_WIDTH).max().orElse(NODE_WIDTH);
        int minY = QUESTS.stream().mapToInt(QuestNode::y).min().orElse(0);
        int maxY = QUESTS.stream().mapToInt(quest -> quest.y() + NODE_HEIGHT).max().orElse(NODE_HEIGHT);
        this.panX = clampStoryAxis(this.panX, minX, maxX, viewportWidth, this.zoom, 24.0D);
        double effectivePanY = this.panY + compactStoryOffsetY();
        effectivePanY = clampStoryAxis(effectivePanY, minY, maxY, viewportHeight, this.zoom, 18.0D);
        this.panY = effectivePanY - compactStoryOffsetY();
    }

    private static double clampStoryAxis(
            double pan, double contentMin, double contentMax, double viewportSize, double scale, double padding
    ) {
        double scaledSize = (contentMax - contentMin) * scale;
        if (scaledSize + padding * 2.0D <= viewportSize) {
            return (viewportSize - scaledSize) / 2.0D - contentMin * scale;
        }
        double minimum = viewportSize - padding - contentMax * scale;
        double maximum = padding - contentMin * scale;
        return Mth.clamp(pan, minimum, maximum);
    }

    private boolean compactCraftLayout() {
        return this.width < COMPACT_WIDTH || this.height < 360;
    }

    private CraftLayout craftLayout() {
        int outerLeft = 14;
        int outerTop = contentTop();
        int outerRight = this.width - 14;
        int outerBottom = this.height - 14;
        int innerWidth = Math.max(220, outerRight - outerLeft - 16);
        int listWidth = compactCraftLayout()
                ? Math.min(128, Math.max(104, innerWidth / 3))
                : Mth.clamp(innerWidth / 3, 190, 280);
        int categoryWidth = compactCraftLayout() ? 80 : 122;
        int listLeft = outerLeft + 8;
        int listRight = listLeft + listWidth;
        int searchTop = outerTop + 8;
        int searchHeight = compactCraftLayout() ? 18 : 20;
        int resultsTop = searchTop + searchHeight + 5;
        int resultsBottom = outerBottom - 8;
        int detailLeft = listRight + 8;
        int categoryRight = outerRight - 8;
        int categoryLeft = categoryRight - categoryWidth;
        int detailRight = categoryLeft - 8;
        int detailTop = outerTop + 8;
        int detailBottom = outerBottom - 8;
        return new CraftLayout(
                outerLeft, outerTop, outerRight, outerBottom,
                listLeft, listRight, searchTop, searchHeight, resultsTop, resultsBottom,
                detailLeft, detailRight, detailTop, detailBottom, categoryLeft, categoryRight
        );
    }

    private ChainViewport chainViewport() {
        return new ChainViewport(14, contentTop(), this.width - 14, this.height - 14);
    }

    private void setSection(Section section) {
        this.section = section;
        if (this.recipeSearch != null) {
            this.recipeSearch.setVisible(section == Section.CRAFTS);
            if (section != Section.CRAFTS) {
                this.recipeSearch.setFocused(false);
            }
        }
        if (section == Section.CHAIN) {
            this.chainViewInitialized = false;
        }
    }

    private List<Integer> filteredRecipeIndexes() {
        String query = this.recipeQuery.trim().toLowerCase(Locale.ROOT);
        List<Integer> result = new ArrayList<>();
        for (int index = 0; index < RECIPES.size(); index++) {
            ItemStack output = RECIPES.get(index).output().get();
            String displayName = output.getHoverName().getString().toLowerCase(Locale.ROOT);
            String registryPath = BuiltInRegistries.ITEM.getKey(output.getItem()).getPath()
                    .toLowerCase(Locale.ROOT);
            String readableRegistryPath = registryPath.replace('_', ' ');
            if (this.recipeCategory.matches(output)
                    && (query.isEmpty()
                    || displayName.contains(query)
                    || registryPath.contains(query)
                    || readableRegistryPath.contains(query))) {
                result.add(index);
            }
        }
        return result;
    }

    private void ensureVisibleRecipeSelection(List<Integer> filtered) {
        if (!filtered.isEmpty() && !filtered.contains(this.selectedRecipe)) {
            selectRecipe(filtered.getFirst());
        }
    }

    private void selectRecipe(int recipeIndex) {
        if (recipeIndex >= 0 && recipeIndex < RECIPES.size()) {
            this.selectedRecipe = recipeIndex;
            if (!this.chainPinned) {
                this.chainRecipe = recipeIndex;
                this.chainViewInitialized = false;
            }
            this.triggerImmediateNarration(true);
        }
    }

    private Component fitText(Component text, int maxWidth) {
        if (this.font.width(text) <= maxWidth) {
            return text;
        }
        String value = text.getString();
        String suffix = "...";
        while (!value.isEmpty() && this.font.width(value + suffix) > maxWidth) {
            value = value.substring(0, value.length() - 1);
        }
        return Component.literal(value + suffix);
    }

    private CreationGraph buildCreationGraph(SurvivalGuideRecipes.RecipeEntry selected) {
        LinkedHashMap<Item, ItemStack> items = new LinkedHashMap<>();
        LinkedHashSet<ItemEdge> itemEdges = new LinkedHashSet<>();
        Map<Item, Integer> depths = new HashMap<>();
        ItemStack output = selected.output().get().copyWithCount(1);
        collectCreationGraph(output, selected, 0, items, itemEdges, depths, new LinkedHashSet<>());

        int maximumDepth = depths.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        Map<Integer, List<Item>> columns = new HashMap<>();
        for (Item item : items.keySet()) {
            columns.computeIfAbsent(depths.getOrDefault(item, 0), ignored -> new ArrayList<>()).add(item);
        }
        for (List<Item> column : columns.values()) {
            column.sort(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).toString()));
        }

        List<CreationNode> nodes = new ArrayList<>();
        Map<Item, Integer> nodeIndexes = new HashMap<>();
        int largestColumn = 1;
        for (int depth = maximumDepth; depth >= 0; depth--) {
            List<Item> column = columns.getOrDefault(depth, List.of());
            largestColumn = Math.max(largestColumn, column.size());
            int x = 24 + (maximumDepth - depth) * 220;
            for (int row = 0; row < column.size(); row++) {
                Item item = column.get(row);
                ItemStack stack = items.get(item);
                boolean target = item == output.getItem();
                boolean craftable = target || producerFor(item) != null;
                nodeIndexes.put(item, nodes.size());
                nodes.add(new CreationNode(stack, x, 24 + row * 72, craftable, target));
            }
        }

        List<CreationEdge> edges = new ArrayList<>();
        for (ItemEdge edge : itemEdges) {
            Integer from = nodeIndexes.get(edge.from());
            Integer to = nodeIndexes.get(edge.to());
            if (from != null && to != null) {
                edges.add(new CreationEdge(from, to));
            }
        }
        int width = 48 + (maximumDepth + 1) * 220;
        int height = 48 + largestColumn * 72;
        return new CreationGraph(List.copyOf(nodes), List.copyOf(edges), width, height);
    }

    private void collectCreationGraph(
            ItemStack stack,
            SurvivalGuideRecipes.RecipeEntry explicitProducer,
            int depth,
            LinkedHashMap<Item, ItemStack> items,
            LinkedHashSet<ItemEdge> edges,
            Map<Item, Integer> depths,
            Set<Item> visiting
    ) {
        if (stack.isEmpty()) {
            return;
        }
        Item item = stack.getItem();
        items.putIfAbsent(item, stack.copyWithCount(1));
        depths.merge(item, depth, Math::max);
        if (!visiting.add(item)) {
            return;
        }
        SurvivalGuideRecipes.RecipeEntry producer = explicitProducer != null ? explicitProducer : producerFor(item);
        if (producer != null) {
            for (ItemStack ingredient : producer.uniqueIngredients()) {
                if (!ingredient.isEmpty()) {
                    edges.add(new ItemEdge(ingredient.getItem(), item));
                    collectCreationGraph(ingredient, null, depth + 1, items, edges, depths, visiting);
                }
            }
        }
        visiting.remove(item);
    }

    private SurvivalGuideRecipes.RecipeEntry producerFor(Item item) {
        // Hushstone is normally mined. Treat its reversible four-shard recipe as an
        // alternate endpoint so dependency traversal cannot loop forever.
        if (item == ModItems.HUSHSTONE.get()) {
            return null;
        }
        int recipeIndex = SurvivalGuideRecipes.indexOfOutput(item);
        return recipeIndex >= 0 ? RECIPES.get(recipeIndex) : null;
    }

    private CreationNode creationNodeAt(double mouseX, double mouseY) {
        if (this.chainRecipe < 0 || this.chainRecipe >= RECIPES.size()) {
            return null;
        }
        ChainViewport viewport = chainViewport();
        int graphTop = viewport.top() + 35;
        int worldX = Mth.floor((mouseX - viewport.left() - this.chainPanX) / this.chainZoom);
        int worldY = Mth.floor((mouseY - graphTop - this.chainPanY) / this.chainZoom);
        CreationGraph graph = buildCreationGraph(RECIPES.get(this.chainRecipe));
        for (CreationNode node : graph.nodes()) {
            if (node.contains(worldX, worldY)) {
                return node;
            }
        }
        return null;
    }

    boolean openCreationChain(Item item) {
        int recipeIndex = SurvivalGuideRecipes.indexOfOutput(item);
        if (recipeIndex < 0) {
            return false;
        }
        this.chainPinned = false;
        this.selectedRecipe = recipeIndex;
        this.chainRecipe = recipeIndex;
        this.chainViewInitialized = false;
        this.triggerImmediateNarration(true);
        this.recipeCategory = RecipeCategory.ALL;
        setSection(Section.CHAIN);
        return true;
    }

    boolean isCreationChainOpenFor(Item item) {
        return this.section == Section.CHAIN
                && this.chainRecipe == SurvivalGuideRecipes.indexOfOutput(item);
    }

    /** Prepares a deterministic localized recipe-search capture for client smoke. */
    boolean prepareRecipeGuideSmoke(Item item) {
        int recipeIndex = SurvivalGuideRecipes.indexOfOutput(item);
        if (recipeIndex < 0 || this.recipeSearch == null) {
            return false;
        }
        setSection(Section.CRAFTS);
        selectRecipe(recipeIndex);
        this.recipeSearch.setValue("crude_hand");
        boolean registrySearchMatches = filteredRecipeIndexes().contains(recipeIndex);
        String outputName = RECIPES.get(recipeIndex).output().get().getHoverName().getString();
        String query = outputName.substring(0, Math.min(4, outputName.length()));
        this.recipeSearch.setValue(query);
        boolean localizedNameMatches = filteredRecipeIndexes().contains(recipeIndex);
        SurvivalGuideRecipes.RecipeEntry recipe = RECIPES.get(recipeIndex);
        CreationGraph graph = buildCreationGraph(recipe);
        SurvivalGuideRecipes.RecipeEntry kitchenRecipe = RECIPES.stream()
                .filter(entry -> entry.kind() == SurvivalGuideRecipes.RecipeKind.KITCHEN)
                .findFirst()
                .orElse(null);
        boolean kitchenLayoutWorks = kitchenRecipe != null
                && kitchenRecipe.gridWidth() == 3
                && kitchenRecipe.gridHeight() == 2
                && !kitchenRecipe.cell(0, 0).isEmpty()
                && !kitchenRecipe.cell(1, 0).isEmpty()
                && !kitchenRecipe.cell(2, 0).isEmpty()
                && kitchenRecipe.cell(0, 1).is(ModItems.BONE_CLEAVER.get())
                && kitchenRecipe.cell(1, 1).is(ModItems.STIRRING_HOOK.get())
                && !Component.translatable("screen.gravesown.codex_hub.station.kitchen")
                        .getString().equals("screen.gravesown.codex_hub.station.kitchen");
        this.recipeSearch.setValue("");
        this.recipeCategory = RecipeCategory.BUILDING;
        List<Integer> buildingRecipes = filteredRecipeIndexes();
        boolean categoriesWork = !buildingRecipes.isEmpty() && buildingRecipes.stream()
                .map(index -> RECIPES.get(index).output().get())
                .allMatch(this.recipeCategory::matches)
                && !buildingRecipes.contains(recipeIndex);
        this.recipeCategory = RecipeCategory.ALL;
        this.selectedGuidePage = GuidePage.FARMING;
        setSection(Section.GUIDE);
        boolean guideWorks = guideContentHeight(this.selectedGuidePage, 320) > 120;
        boolean scrollRailsWork = scrollRailContractWorks();
        setSection(Section.CHAIN);
        return RECIPES.size() >= 33
                && registrySearchMatches
                && localizedNameMatches
                && categoriesWork
                && guideWorks
                && scrollRailsWork
                && storyExtremesReachableAtMaxZoom()
                && kitchenLayoutWorks
                && recipe.gridSize() == 2
                && recipe.cell(0, 0).is(ModItems.RIBROOT_SPLINT.get())
                && recipe.cell(1, 0).is(ModItems.RIBROOT_SPLINT.get())
                && recipe.cell(0, 1).isEmpty()
                && recipe.cell(1, 1).is(ModItems.THREAD_BINDING.get())
                && graph.nodes().size() == 6
                && graph.edges().size() == 5
                && graph.nodes().stream().anyMatch(node -> node.stack().is(ModBlocks.RIBROOT_STEM.get().asItem()))
                && graph.nodes().stream().anyMatch(node -> node.target()
                        && node.stack().is(ModItems.CRUDE_HANDPICK.get()));
    }

    private static boolean scrollRailContractWorks() {
        ScrollRail rail = new ScrollRail(0, 0, 100, 70, 24, 5, 6);
        double bottomScroll = rail.clampScroll(-10_000.0D);
        return rail.overflowing()
                && bottomScroll == rail.minimumScroll()
                && rail.itemIndexAt(10, 10, 0.0D) == 0
                && rail.itemIndexAt(10, 60, bottomScroll) == 5
                && rail.itemIndexAt(10, 43, bottomScroll) == -1
                && rail.itemIndexAt(10, 70, bottomScroll) == -1;
    }

    private boolean storyExtremesReachableAtMaxZoom() {
        double smokeZoom = 1.35D;
        int viewportWidth = Math.max(1, storyCanvasRight() - 14 - 4);
        int minX = QUESTS.stream().mapToInt(QuestNode::x).min().orElse(0);
        int maxX = QUESTS.stream().mapToInt(quest -> quest.x() + NODE_WIDTH).max().orElse(NODE_WIDTH);
        double firstPan = clampStoryAxis(Double.MAX_VALUE, minX, maxX, viewportWidth, smokeZoom, 24.0D);
        double lastPan = clampStoryAxis(-Double.MAX_VALUE, minX, maxX, viewportWidth, smokeZoom, 24.0D);
        double firstLeft = firstPan + minX * smokeZoom;
        double lastRight = lastPan + maxX * smokeZoom;
        return firstLeft >= 23.0D && firstLeft <= 25.0D
                && lastRight >= viewportWidth - 25.0D
                && lastRight <= viewportWidth - 23.0D;
    }

    /** Selects a stable guide page for the FHD client acceptance capture. */
    boolean prepareGuideSmoke() {
        this.selectedGuidePage = GuidePage.FARMING;
        this.guideScroll = 0.0D;
        setSection(Section.GUIDE);
        return guideContentHeight(this.selectedGuidePage, 320) > 120;
    }

    private static int statusColor(QuestStatus status) {
        return switch (status) {
            case CLAIMED -> CLAIMED;
            case READY -> READY;
            case ACTIVE -> BORDER;
            case LOCKED -> LOCKED;
        };
    }

    private static QuestNode quest(int index, int x, int y, String key, Supplier<ItemStack> icon) {
        return new QuestNode(
                index, x, y,
                "quest.gravesown." + key + ".title",
                "quest.gravesown." + key + ".description",
                icon
        );
    }

    private static boolean inside(double x, double y, int left, int top, int width, int height) {
        return x >= left && x < left + width && y >= top && y < top + height;
    }

    private enum Section { STORY, CRAFTS, CHAIN, GUIDE }

    private enum RecipeCategory {
        ALL("screen.gravesown.codex_hub.category.all"),
        TOOLS("screen.gravesown.codex_hub.category.tools"),
        BUILDING("screen.gravesown.codex_hub.category.building"),
        FOOD("screen.gravesown.codex_hub.category.food"),
        EQUIPMENT("screen.gravesown.codex_hub.category.equipment"),
        MATERIALS("screen.gravesown.codex_hub.category.materials");

        private final String translationKey;

        RecipeCategory(String translationKey) {
            this.translationKey = translationKey;
        }

        boolean matches(ItemStack stack) {
            if (this == ALL) {
                return true;
            }
            String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
            RecipeCategory actual;
            if (path.contains("pick") || path.contains("shovel") || path.contains("axe")
                    || path.contains("hoe") || path.contains("knife") || path.contains("cleaver")
                    || path.contains("hook")) {
                actual = TOOLS;
            } else if (path.contains("meat") || path.contains("flesh") || path.contains("fillet")
                    || path.contains("smoked") || path.contains("charred") || path.contains("loaf")
                    || path.contains("bean") || path.contains("ration") || path.contains("grain")
                    || path.contains("stew") || path.contains("chowder") || path.contains("sprat")
                    || path.contains("marrow_pot")) {
                actual = FOOD;
            } else if (stack.getItem() instanceof BlockItem) {
                actual = path.contains("grass") || path.contains("fern") || path.contains("reed")
                        || path.contains("bloom") || path.contains("bulb") || path.contains("kelp")
                        || path.contains("weed") || path.contains("roots") || path.contains("pod")
                        || path.contains("crop") ? MATERIALS : BUILDING;
            } else if (path.contains("quietskin") || path.contains("rod") || path.contains("skiff")
                    || path.contains("bucket")) {
                actual = EQUIPMENT;
            } else {
                actual = MATERIALS;
            }
            return this == actual;
        }
    }

    private enum GuidePage {
        BASICS("basics", 4, () -> new ItemStack(ModItems.SURVIVOR_CODEX.get())),
        FARMING("farming", 4, () -> new ItemStack(ModItems.HUSHSTONE_HOE.get())),
        FOOD("food", 4, () -> new ItemStack(ModItems.CHARRED_GRAZER_MEAT.get())),
        WATER("water", 4, () -> new ItemStack(ModItems.GLOAMWATER_BUCKET.get())),
        BUILDING("building", 4, () -> new ItemStack(ModItems.GRAVEWORK_BENCH.get())),
        COOKING("cooking", 4, () -> new ItemStack(ModItems.FIELD_KITCHEN.get())),
        EXPLORATION("exploration", 4, () -> new ItemStack(ModItems.RELIQUARY_CRATE.get()));

        private final String titleKey;
        private final String introKey;
        private final String tipKey;
        private final int steps;
        private final Supplier<ItemStack> icon;

        GuidePage(String key, int steps, Supplier<ItemStack> icon) {
            this.titleKey = "screen.gravesown.codex_hub.guide." + key + ".title";
            this.introKey = "screen.gravesown.codex_hub.guide." + key + ".intro";
            this.tipKey = "screen.gravesown.codex_hub.guide." + key + ".tip";
            this.steps = steps;
            this.icon = icon;
        }

        String stepKey(int step) {
            return this.titleKey.substring(0, this.titleKey.length() - 5) + "step." + step;
        }
    }

    private enum QuestStatus {
        LOCKED("screen.gravesown.codex_hub.status.locked"),
        ACTIVE("screen.gravesown.codex_hub.status.active"),
        READY("screen.gravesown.codex_hub.status.ready"),
        CLAIMED("screen.gravesown.codex_hub.status.claimed");

        private final String key;

        QuestStatus(String key) {
            this.key = key;
        }
    }

    private record QuestNode(
            int index, int x, int y, String titleKey, String descriptionKey, Supplier<ItemStack> icon
    ) {
        boolean contains(int mouseX, int mouseY) {
            return inside(mouseX, mouseY, this.x, this.y, NODE_WIDTH, NODE_HEIGHT);
        }
    }

    private record ScrollRail(
            int left,
            int top,
            int right,
            int bottom,
            int itemHeight,
            int gap,
            int itemCount
    ) {
        int stride() {
            return this.itemHeight + this.gap;
        }

        int viewportHeight() {
            return Math.max(0, this.bottom - this.top);
        }

        int contentHeight() {
            if (this.itemCount <= 0) {
                return 0;
            }
            return this.itemCount * this.itemHeight + (this.itemCount - 1) * this.gap;
        }

        double minimumScroll() {
            return Math.min(0.0D, this.viewportHeight() - this.contentHeight());
        }

        double clampScroll(double scroll) {
            return Mth.clamp(scroll, this.minimumScroll(), 0.0D);
        }

        boolean overflowing() {
            return this.contentHeight() > this.viewportHeight();
        }

        int buttonRight() {
            return this.right - (this.overflowing() ? 5 : 0);
        }

        int itemY(int index, double scroll) {
            return this.top + index * this.stride() + Mth.floor(scroll);
        }

        boolean contains(double mouseX, double mouseY) {
            return inside(mouseX, mouseY, this.left, this.top,
                    this.right - this.left, this.bottom - this.top);
        }

        int itemIndexAt(double mouseX, double mouseY, double scroll) {
            if (!inside(mouseX, mouseY, this.left, this.top,
                    this.buttonRight() - this.left, this.bottom - this.top)) {
                return -1;
            }
            int contentY = Mth.floor(mouseY - this.top - scroll);
            if (contentY < 0) {
                return -1;
            }
            int index = contentY / this.stride();
            int withinItem = contentY % this.stride();
            return index >= 0 && index < this.itemCount && withinItem < this.itemHeight
                    ? index
                    : -1;
        }
    }

    private record CraftLayout(
            int outerLeft,
            int outerTop,
            int outerRight,
            int outerBottom,
            int listLeft,
            int listRight,
            int searchTop,
            int searchHeight,
            int resultsTop,
            int resultsBottom,
            int detailLeft,
            int detailRight,
            int detailTop,
            int detailBottom,
            int categoryLeft,
            int categoryRight
    ) {
    }

    private record ChainViewport(int left, int top, int right, int bottom) {
    }

    private record ItemEdge(Item from, Item to) {
    }

    private record CreationEdge(int from, int to) {
    }

    private record CreationGraph(List<CreationNode> nodes, List<CreationEdge> edges, int width, int height) {
    }

    private record CreationNode(ItemStack stack, int x, int y, boolean craftable, boolean target) {
        private static final int WIDTH = 168;
        private static final int HEIGHT = 42;

        boolean contains(int mouseX, int mouseY) {
            return inside(mouseX, mouseY, this.x, this.y, WIDTH, HEIGHT);
        }
    }
}
