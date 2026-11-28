package dev.lhkongyu.lhmiracleroad.client.screen.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import dev.lhkongyu.lhmiracleroad.client.screen.occupation.OccupationText;
import dev.lhkongyu.lhmiracleroad.config.ShowGuiAttributeConfig;
import dev.lhkongyu.lhmiracleroad.config.ShowGuiAttributeConfig.ValueType;
import dev.lhkongyu.lhmiracleroad.config.editor.ConfigEditorPackets;
import dev.lhkongyu.lhmiracleroad.config.editor.ConfigSection;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static dev.lhkongyu.lhmiracleroad.client.screen.config.ConfigEditorPickerScreen.words;

final class GuiAttributeEditorScreen extends Screen {
    private static final ConfigSection SECTION = ConfigSection.SHOW_GUI_ATTRIBUTE;
    private static final int PAGE_SIZE = 8;
    private static final int LIST_TOP = 51;
    private static final List<ConfigEditorPickerScreen.Choice> DISPLAY_TYPES = Arrays.stream(ValueType.values())
            .map(ValueType::configName)
            .map(name -> new ConfigEditorPickerScreen.Choice(name, Component.literal(name)))
            .toList();

    private final Screen parent;
    private final ConfigEditorDraft draft = new ConfigEditorDraft();
    private final List<Button> rowButtons = new ArrayList<>();
    private Operation pending = Operation.NONE;
    private Component message = Component.empty();
    private boolean error;
    private String query = "";
    private int page;
    private int pageCount = 1;
    private int shown;
    private int left;
    private int right;
    private int rowHeight;
    private int footerY;
    private EditBox search;
    private Button add;
    private Button save;
    private Button back;
    private Button previous;
    private Button next;

    GuiAttributeEditorScreen(Screen parent) {
        super(words("GUI attributes", "界面属性"));
        this.parent = parent;
    }

    boolean hasDraft() {
        return draft.dirty();
    }

    boolean isBusy() {
        return pending != Operation.NONE;
    }

    boolean isSaving() {
        return pending == Operation.SAVE;
    }

    void loadIfNeeded() {
        if (draft.document == null && !isBusy()) reload();
    }

    void reload() {
        pending = Operation.LOAD;
        ConfigEditorPackets.request(SECTION);
    }

    void receiveSnapshot(String revision, String json, String status) {
        Operation operation = pending;
        pending = Operation.NONE;
        error = !status.isBlank();
        message = error ? Component.literal(status) : Component.empty();
        if (!json.isBlank()) {
            try {
                JsonObject snapshot = JsonParser.parseString(json).getAsJsonObject();
                if (!(snapshot.get(ShowGuiAttributeConfig.KEY) instanceof JsonArray)) {
                    throw new JsonParseException("Missing GUI attributes");
                }
                draft.receive(revision, snapshot, !error);
                if (!error && operation == Operation.SAVE) message = words("Saved.", "已保存。");
            } catch (IllegalStateException | JsonParseException exception) {
                error = true;
                message = words("Could not read the GUI attribute configuration.", "无法读取界面属性配置。");
            }
        }
        if (minecraft != null && minecraft.screen == this) refreshRows();
    }

    @Override
    protected void init() {
        int panelWidth = Math.min(width - 16, 620);
        left = (width - panelWidth) / 2;
        right = left + panelWidth;
        footerY = height - 25;
        rowHeight = Math.max(3, Math.min(25, (footerY - LIST_TOP - 3) / PAGE_SIZE));
        rowButtons.clear();

        int y = 30;
        Component searchLabel = words("Search", "搜索");
        search = addRenderableWidget(new EditBox(font, left + 9, y, panelWidth - 145, 18, searchLabel));
        search.setMaxLength(256);
        search.setValue(query);
        search.setHint(words("Attribute name or ID", "属性名称或 ID"));
        search.setResponder(text -> {
            query = text;
            page = 0;
            refreshRows();
        });
        add = button(words("Add", "新增"), words("Choose a game attribute to add", "选择要新增的游戏属性"),
                right - 129, y, 58, 18, () -> pickAttribute(null));
        save = button(words("Save", "保存"), words("Save GUI attributes", "保存界面属性"),
                right - 67, y, 58, 18, this::confirmSave);

        back = button(words("Back", "返回"), words("Back to the occupation editor", "返回职业编辑页"),
                left + 9, footerY, 62, 19, this::onClose);
        previous = button(Component.literal("◀"), words("Previous page", "上一页"),
                right - 120, footerY, 30, 19, () -> {
                    page--;
                    refreshRows();
                });
        next = button(Component.literal("▶"), words("Next page", "下一页"),
                right - 39, footerY, 30, 19, () -> {
                    page++;
                    refreshRows();
                });
        refreshRows();
        setInitialFocus(search);
    }

    private void refreshRows() {
        if (rowButtons.contains(getFocused())) setFocused(null);
        rowButtons.forEach(this::removeWidget);
        rowButtons.clear();
        JsonArray entries = entries();
        List<Row> matches = new ArrayList<>();
        if (entries != null) {
            String[] filter = ConfigEditorScreen.searchTerms(query);
            for (int index = 0; index < entries.size(); index++) {
                JsonObject entry = entries.get(index) instanceof JsonObject object ? object : null;
                String configuredId = text(entry, "attribute").trim();
                String id = registeredId(configuredId);
                Component name = entry == null ? words("Invalid entry", "无效条目") : attributeName(id);
                String type = text(entry, "show_value_type").trim();
                String terms = name.getString() + " " + id + " " + configuredId + " " + type;
                if (ConfigEditorScreen.matches(filter, terms)) matches.add(new Row(index, entry, id, name, type));
            }
        }
        pageCount = Math.max(1, (matches.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        page = Math.max(0, Math.min(page, pageCount - 1));
        int start = page * PAGE_SIZE;
        shown = Math.min(PAGE_SIZE, matches.size() - start);
        for (int index = 0; index < shown; index++) {
            addRow(entries, matches.get(start + index), LIST_TOP + index * rowHeight);
        }
        boolean loaded = entries != null;
        search.setEditable(loaded);
        add.active = loaded && !isBusy();
        save.active = loaded && !isBusy() && hasDraft() && !draft.conflicted();
        back.active = !isSaving();
        previous.active = page > 0;
        next.active = page + 1 < pageCount;
    }

    private void addRow(JsonArray entries, Row row, int y) {
        int x = left + 9;
        int rowWidth = right - left - 18;
        int typeWidth = Math.min(148, rowWidth / 3);
        int editWidth = rowWidth - typeWidth - 28;
        int h = rowHeight - 2;

        Component attribute = row.id().isEmpty() && row.entry() != null ? words("Choose attribute", "选择属性") : row.name();
        if (!row.id().isEmpty() && !row.id().equals(attribute.getString())) {
            attribute = attribute.copy().append(" · ").append(row.id());
        }
        Button choose = button(attribute, attribute, x, y, editWidth, h, () -> pickAttribute(row.entry()));
        choose.active = !isBusy() && row.entry() != null;
        rowButtons.add(choose);

        if (row.entry() != null) {
            Component label = words("Display type", "显示类型");
            Component type = row.type().isEmpty() ? label : Component.literal(row.type());
            Button changeType = button(type, label.copy().append("\n").append(type),
                    x + editWidth + 4, y, typeWidth, h,
                    () -> choose(label, DISPLAY_TYPES, value -> row.entry().addProperty("show_value_type", value)));
            changeType.active = !isBusy();
            rowButtons.add(changeType);
        }
        Button remove = button(Component.literal("×"), words("Remove this entry", "删除此条"),
                right - 29, y, 20, h, () -> {
                    entries.remove(row.index());
                    refreshRows();
                });
        remove.active = !isBusy();
        rowButtons.add(remove);
    }

    private void pickAttribute(JsonObject entry) {
        JsonArray entries = entries();
        Set<String> used = new HashSet<>();
        for (JsonElement value : entries) {
            if (value instanceof JsonObject row && row != entry) used.add(registeredId(text(row, "attribute").trim()));
        }
        List<ConfigEditorPickerScreen.Choice> choices = ForgeRegistries.ATTRIBUTES.getKeys().stream()
                .sorted().map(ResourceLocation::toString)
                .filter(id -> !used.contains(id))
                .map(id -> new ConfigEditorPickerScreen.Choice(id, attributeName(id)))
                .toList();
        choose(words("Choose a game attribute", "选择游戏属性"), choices, id -> {
            if (entry != null) {
                entry.addProperty("attribute", id);
            } else {
                JsonObject added = new JsonObject();
                added.addProperty("attribute", id);
                added.addProperty("show_value_type", ValueType.BASE.configName());
                entries.add(added);
                query = "";
                page = (entries.size() - 1) / PAGE_SIZE;
            }
        });
    }

    private void choose(Component title, List<ConfigEditorPickerScreen.Choice> choices, Consumer<String> accept) {
        minecraft.setScreen(new ConfigEditorPickerScreen(this, title, choices, false, accept));
    }

    private void confirmSave() {
        minecraft.setScreen(new ConfirmScreen(confirmed -> {
            if (confirmed) pending = Operation.SAVE;
            minecraft.setScreen(this);
            if (confirmed) ConfigEditorPackets.save(SECTION, draft.revision, draft.document.deepCopy());
        }, words("Save GUI attributes?", "保存界面属性？"),
                words("The display list applies to all occupations.", "该显示列表对所有职业生效。")));
    }

    private JsonArray entries() {
        return draft.document == null ? null : draft.document.getAsJsonArray(ShowGuiAttributeConfig.KEY);
    }

    private static String text(JsonObject object, String key) {
        JsonElement value = object == null ? null : object.get(key);
        return value != null && value.isJsonPrimitive() ? value.getAsString() : "";
    }

    private static String registeredId(String configuredId) {
        if (configuredId.isEmpty()) return configuredId;
        var attribute = ShowGuiAttributeConfig.resolveAttribute(configuredId);
        ResourceLocation id = attribute == null ? null : ForgeRegistries.ATTRIBUTES.getKey(attribute);
        return id == null ? configuredId : id.toString();
    }

    private static Component attributeName(String id) {
        return OccupationText.rewardName(ResourceLocation.tryParse(id), words("Unknown attribute", "未知属性"));
    }

    private Component status() {
        if (isBusy()) return words("Waiting for the server…", "等待服务器响应…");
        if (draft.conflicted()) return words("Return to the occupation page and use Reload.", "请返回职业页并点击底部「读取」。");
        if (error) return message;
        return hasDraft() ? words("Unsaved", "未保存") : message;
    }

    private Button button(Component title, Component tooltip, int x, int y, int w, int h, Runnable action) {
        return addRenderableWidget(Button.builder(fit(title, w - 6), button -> action.run())
                .bounds(x, y, w, h).tooltip(Tooltip.create(tooltip)).build());
    }

    private Component fit(Component text, int limit) {
        if (font.width(text) <= limit) return text;
        if (limit < font.width("…")) return Component.empty();
        return Component.literal(font.plainSubstrByWidth(text.getString(), limit - font.width("…")) + "…");
    }

    @Override
    public void tick() {
        search.tick();
    }

    @Override
    public void onClose() {
        if (!isSaving()) minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        ConfigEditorScreen.panel(graphics, left, right, 7, height - 3);
        graphics.drawString(font, title, left + 9, 15, ConfigEditorScreen.GOLD, false);
        Component status = status();
        int statusX = left + 22 + font.width(title);
        int statusWidth = right - statusX - 9;
        int statusColor = error || draft.conflicted() ? 0xFAC476 : ConfigEditorScreen.MUTED;
        graphics.drawString(font, fit(status, statusWidth), statusX, 15, statusColor, false);

        if (shown == 0 && entries() != null) {
            Component empty = entries().isEmpty() ? words("No GUI attributes", "暂无界面属性")
                    : words("No matching attributes", "没有匹配的属性");
            graphics.drawString(font, empty, left + 9, LIST_TOP + 6, ConfigEditorScreen.MUTED, false);
        }

        String pages = (page + 1) + " / " + pageCount;
        int pageCenter = (previous.getX() + previous.getWidth() + next.getX()) / 2;
        graphics.drawString(font, pages, pageCenter - font.width(pages) / 2, footerY + 6, ConfigEditorScreen.MUTED, false);
        super.render(graphics, mouseX, mouseY, partialTick);
        if (font.width(status) > statusWidth && mouseX >= statusX && mouseX < right - 8
                && mouseY >= 11 && mouseY < 26) {
            var lines = font.split(status, Math.min(420, width - 32));
            graphics.renderTooltip(font, lines, mouseX, mouseY);
        }
    }

    private enum Operation { NONE, LOAD, SAVE }

    private record Row(int index, JsonObject entry, String id, Component name, String type) {}
}
