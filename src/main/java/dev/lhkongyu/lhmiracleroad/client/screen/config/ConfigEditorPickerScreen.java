package dev.lhkongyu.lhmiracleroad.client.screen.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

final class ConfigEditorPickerScreen extends Screen {
    private static final int ROW_HEIGHT = 23;

    private final Screen parent;
    private final List<Choice> choices;
    private final Consumer<String> accept;
    private final boolean items;
    private final Runnable useHeld;
    private final List<Button> buttons = new ArrayList<>();
    private final List<Choice> visible = new ArrayList<>();
    private String query = "";
    private EditBox search;
    private Button previous;
    private Button next;
    private Button held;
    private int left;
    private int right;
    private int top;
    private int bottom;
    private int offset;
    private int resultCount;

    ConfigEditorPickerScreen(Screen parent, Component title, List<Choice> choices,
                             boolean items, Consumer<String> accept) {
        this(parent, title, choices, items, accept, null);
    }

    private ConfigEditorPickerScreen(Screen parent, Component title, List<Choice> choices,
                                     boolean items, Consumer<String> accept, Runnable useHeld) {
        super(title);
        this.parent = parent;
        this.choices = choices;
        this.accept = accept;
        this.items = items;
        this.useHeld = useHeld;
    }

    static ConfigEditorPickerScreen forItems(ConfigEditorScreen parent, Consumer<String> accept) {
        return forItems(parent, accept, () -> {
            String id = parent.heldItemId();
            if (id != null) accept.accept(id);
        });
    }

    static ConfigEditorPickerScreen forItems(Screen parent, Consumer<String> accept, Runnable useHeld) {
        List<Choice> choices = BuiltInRegistries.ITEM.keySet().stream()
                .filter(id -> BuiltInRegistries.ITEM.get(id) != Items.AIR)
                .map(id -> new Choice(id.toString(), BuiltInRegistries.ITEM.get(id).getDescription()))
                .sorted(Comparator.comparing(choice -> choice.value))
                .toList();
        return new ConfigEditorPickerScreen(parent, words("Select item", "选择物品"), choices, true, accept, useHeld);
    }

    static Component words(String english, String chinese) {
        return Component.literal("zh_cn".equals(Minecraft.getInstance().getLanguageManager().getSelected())
                ? chinese : english);
    }

    static ItemStack heldItem() {
        var player = Minecraft.getInstance().player;
        return player == null ? ItemStack.EMPTY : player.getMainHandItem();
    }

    static Choice namedChoice(String value) {
        ResourceLocation id = ResourceLocation.tryParse(value);
        var attribute = id == null ? null : ForgeRegistries.ATTRIBUTES.getValue(id);
        Component label = attribute == null ? Component.literal(value)
                : Component.translatableWithFallback(attribute.getDescriptionId(), value);
        return new Choice(value, label);
    }

    @Override
    protected void init() {
        int panelWidth = Math.min(width - 16, 560);
        left = (width - panelWidth) / 2;
        right = left + panelWidth;
        top = 60;
        bottom = height - 33;
        int reserved = useHeld == null ? 0 : 132;
        search = addRenderableWidget(new EditBox(font, left + 9, 31, panelWidth - 18 - reserved, 20,
                words("Search", "搜索")));
        search.setMaxLength(256);
        search.setValue(query);
        search.setHint(words("Name, ID or namespace", "名称、ID 或命名空间"));
        search.setResponder(text -> {
            query = text;
            offset = 0;
            refreshRows();
        });
        if (useHeld != null) {
            held = addRenderableWidget(Button.builder(words("Use main hand", "使用主手物品"),
                            button -> {
                                if (heldItem().isEmpty()) return;
                                useHeld.run();
                                onClose();
                            })
                    .bounds(right - 133, 31, 124, 20).build());
            held.active = !heldItem().isEmpty();
        }
        previous = addRenderableWidget(Button.builder(Component.literal("▲"), button -> scroll(-rowsPerPage()))
                .bounds(right - 80, bottom + 4, 32, 20).build());
        next = addRenderableWidget(Button.builder(Component.literal("▼"), button -> scroll(rowsPerPage()))
                .bounds(right - 44, bottom + 4, 35, 20).build());
        addRenderableWidget(Button.builder(words("Back", "返回"), button -> onClose())
                .bounds(left + 9, bottom + 4, 60, 20).build());
        refreshRows();
        setInitialFocus(search);
    }

    private void refreshRows() {
        for (Button button : buttons) {
            if (getFocused() == button) {
                setFocused(null);
            }
            removeWidget(button);
        }
        buttons.clear();
        visible.clear();
        String[] filter = ConfigEditorScreen.searchTerms(query);
        List<Choice> filtered = choices.stream().filter(choice ->
                ConfigEditorScreen.matches(filter, choice.value + " " + choice.label.getString())).toList();
        resultCount = filtered.size();
        offset = Math.max(0, Math.min(offset, Math.max(0, resultCount - rowsPerPage())));
        for (int index = offset; index < Math.min(resultCount, offset + rowsPerPage()); index++) {
            Choice choice = filtered.get(index);
            int y = top + (index - offset) * ROW_HEIGHT;
            int x = left + (items ? 33 : 9);
            String label = choice.label.getString();
            String text = choice.value.equals(label) ? label : label + "  •  " + choice.value;
            Button row = addRenderableWidget(Button.builder(
                            Component.literal(font.plainSubstrByWidth(text, right - x - 27)),
                            button -> select(choice.value))
                    .bounds(x, y, right - x - 9, 20)
                    .tooltip(Tooltip.create(Component.literal(text))).build());
            buttons.add(row);
            visible.add(choice);
        }
        previous.active = offset > 0;
        next.active = offset + rowsPerPage() < resultCount;
    }

    private int rowsPerPage() {
        return Math.max(1, (bottom - top) / ROW_HEIGHT);
    }

    private void scroll(int amount) {
        int newOffset = Math.min(Math.max(0, offset + amount), Math.max(0, resultCount - rowsPerPage()));
        if (newOffset != offset) {
            offset = newOffset;
            refreshRows();
        }
    }

    private void select(String value) {
        if (value != null) {
            accept.accept(value);
            onClose();
        }
    }

    @Override
    public void tick() {
        search.tick();
        if (held != null) held.active = !heldItem().isEmpty();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX >= left && mouseX < right && mouseY >= top && mouseY < bottom && delta != 0) {
            scroll(delta > 0 ? -3 : 3);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        ConfigEditorScreen.panel(graphics, left, right, 9, height - 7);
        graphics.drawString(font, title, left + 9, 16, ConfigEditorScreen.GOLD, false);
        graphics.drawString(font, Component.literal(Integer.toString(resultCount)), left + 79, bottom + 10,
                ConfigEditorScreen.MUTED, false);
        if (items) {
            for (int index = 0; index < visible.size(); index++) {
                ItemStack item = ConfigEditorScreen.item(visible.get(index).value);
                if (!item.isEmpty()) {
                    graphics.renderItem(item, left + 12, top + index * ROW_HEIGHT + 2);
                }
            }
        }
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    record Choice(String value, Component label) {}
}
