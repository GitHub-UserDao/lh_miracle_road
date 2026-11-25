package dev.lhkongyu.lhmiracleroad.client.screen.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import dev.lhkongyu.lhmiracleroad.config.editor.ConfigEditorPackets;
import dev.lhkongyu.lhmiracleroad.config.editor.ConfigSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public final class ConfigEditorScreen extends Screen {
    static final int GOLD = 0xE9C980;
    static final int MUTED = 0xB3AF9F;
    private static final int TEXT = 0xF2ECE0;
    private static final int WARNING = 0xFAC476;
    private static final ConfigSection[] SECTIONS = ConfigSection.values();
    static final int HEADER_BOTTOM = 55;
    private static final int ROW_HEIGHT = 24;
    private static final Pattern NUMBER = Pattern.compile("-?(?:0|[1-9][0-9]*)(?:\\.[0-9]+)?(?:[eE][+-]?[0-9]+)?");
    private static WeakReference<ConfigEditorScreen> lastEditor = new WeakReference<>(null);

    private final EnumMap<ConfigSection, Page> pages = new EnumMap<>(ConfigSection.class);
    private final OccupationEditorScreen occupationEditor = new OccupationEditorScreen(this);
    private final List<AbstractWidget> body = new ArrayList<>();
    private final List<EditBox> inputs = new ArrayList<>();
    private final List<Line> lines = new ArrayList<>();
    private ConfigSection section;
    private Mode mode = Mode.EDIT;
    private EditBox search;
    private Button addButton;
    private Button reloadButton;
    private Button previewButton;
    private Button changesButton;
    private Button discardButton;
    private Button restoreButton;
    private Button saveButton;
    private Button previousButton;
    private Button nextButton;
    private Button copyHeldButton;
    private int left;
    private int right;
    private int contentTop;
    private int contentBottom;
    private int rowCount;

    private ConfigEditorScreen() {
        super(Component.translatableWithFallback("lhmiracleroad.config_editor.title", "Config editor"));
        for (ConfigSection value : SECTIONS) {
            if (value != ConfigSection.OCCUPATION) pages.put(value, new Page());
        }
    }

    public static void receive(ConfigSection section, String revision, String json, String status, boolean open) {
        Minecraft minecraft = Minecraft.getInstance();
        ConfigEditorScreen editor = lastEditor.get();
        boolean opening = open && (editor == null || !editor.isEditorScreen(minecraft.screen));
        if (opening) {
            editor = new ConfigEditorScreen();
            lastEditor = new WeakReference<>(editor);
        }
        if (editor == null) return;

        if (section == ConfigSection.OCCUPATION) {
            editor.occupationEditor.receiveSnapshot(revision, json, status);
        } else {
            editor.receiveSnapshot(section, revision, json, status);
        }
        if (open) {
            editor.switchTo(section);
            if (opening && !json.isBlank()) {
                for (ConfigSection other : editor.pages.keySet()) {
                    if (other != section) editor.request(other);
                }
            }
        } else if (minecraft.screen == editor && editor.section == section) {
            editor.rebuildWidgets();
        }
    }

    private boolean isEditorScreen(Screen screen) {
        return screen == this || screen == occupationEditor;
    }

    private void receiveSnapshot(ConfigSection responseSection, String revision, String json, String status) {
        Page page = pages.get(responseSection);
        Operation operation = page.pending;
        page.pending = Operation.NONE;
        page.message = status.isBlank() ? Component.empty() : Component.literal(status);
        page.error = !status.isBlank();
        if (json.isBlank()) {
            return;
        }
        try {
            boolean accept = status.isBlank() && (operation == Operation.SAVE || operation == Operation.RESTORE);
            page.draft.receive(revision, JsonParser.parseString(json).getAsJsonObject(), accept);
            if (accept) {
                page.invalidNumbers.clear();
                page.message = operation == Operation.SAVE ? words("Saved to server.", "已保存到服务器。")
                        : words("Previous version restored.", "已恢复上一版。");
                if (responseSection == section) {
                    mode = Mode.EDIT;
                }
            }
            if (nodeAt(page.draft.document, page.path) == null) {
                page.path = List.of();
                page.offset = 0;
            }
        } catch (IllegalStateException | com.google.gson.JsonParseException exception) {
            page.message = words("Could not read the server snapshot.", "无法读取服务器快照。");
            page.error = true;
        }
    }

    @Override
    protected void init() {
        int panelWidth = headerWidth(width);
        left = (width - panelWidth) / 2;
        right = left + panelWidth;
        contentTop = 99;
        contentBottom = height - 56;
        inputs.clear();
        body.clear();

        addSectionTabs(width, font, section, this::addRenderableWidget);

        Page page = page();
        copyHeldButton = button(words("Copy held ID", "复制主手 ID"), right - 96, 12, 86, this::copyHeldId);
        copyHeldButton.setTooltip(Tooltip.create(words("Copy main-hand item ID to clipboard", "复制主手物品 ID 到剪贴板")));
        Button back = button(words("Up", "上一级"), left + 9, 57, 56, this::goUp);
        back.active = mode != Mode.EDIT || !page.path.isEmpty();
        previousButton = button(Component.literal("▲"), right - 66, 57, 26, () -> scroll(-rowsPerPage()));
        previousButton.setTooltip(Tooltip.create(words("Previous rows", "向上翻页")));
        nextButton = button(Component.literal("▼"), right - 36, 57, 26, () -> scroll(rowsPerPage()));
        nextButton.setTooltip(Tooltip.create(words("Next rows", "向下翻页")));

        search = addRenderableWidget(new EditBox(font, left + 9, 79, panelWidth - 18 - 130, 18,
                words("Search configuration", "搜索配置")));
        search.setMaxLength(256);
        search.setValue(page.search);
        search.setHint(words("Name, ID or namespace", "名称、ID 或命名空间"));
        search.setResponder(query -> {
            page.search = query;
            page.offset = 0;
            refreshRows();
        });
        search.active = mode == Mode.EDIT;
        addButton = button(words("Add", "添加"), right - 135, 78, 59, this::addEntry);
        reloadButton = button(words("Reload", "重新读取"), right - 72, 78, 62, () -> request(section));

        int footerWidth = (panelWidth - 18) / 5;
        int footerY = height - 31;
        previewButton = button(words("Preview", "预览"), left + 9, footerY, footerWidth - 3,
                () -> show(Mode.PREVIEW));
        changesButton = button(words("Changes", "变更"), left + 9 + footerWidth, footerY, footerWidth - 3,
                () -> show(Mode.CHANGES));
        discardButton = button(words("Discard", "丢弃"), left + 9 + footerWidth * 2, footerY, footerWidth - 3,
                this::discard);
        restoreButton = button(words("Restore", "恢复"), left + 9 + footerWidth * 3, footerY, footerWidth - 3,
                this::restore);
        saveButton = button(words("Save", "保存"), left + 9 + footerWidth * 4, footerY, footerWidth - 3, this::save);
        refreshRows();
    }

    static int headerWidth(int width) {
        return Math.min(width - 16, 690);
    }

    void addSectionTabs(int width, Font font, ConfigSection selected, Consumer<Button> add) {
        int panelWidth = headerWidth(width);
        int left = (width - panelWidth) / 2;
        int stride = (panelWidth - 18) / SECTIONS.length;
        for (int index = 0; index < SECTIONS.length; index++) {
            ConfigSection target = SECTIONS[index];
            Component label = target.label();
            Button tab = Button.builder(fit(font, label, stride - 9), press -> switchTo(target))
                    .bounds(left + 9 + index * stride, 34, stride - 3, 19)
                    .tooltip(Tooltip.create(label)).build();
            tab.active = target != selected;
            add.accept(tab);
        }
    }

    private void switchTo(ConfigSection target) {
        Minecraft minecraft = Minecraft.getInstance();
        if (target == ConfigSection.OCCUPATION) {
            if (minecraft.screen != occupationEditor) minecraft.setScreen(occupationEditor);
            return;
        }

        section = target;
        mode = Mode.EDIT;
        if (minecraft.screen == this) rebuildWidgets();
        else minecraft.setScreen(this);
        if (page().draft.document == null) request(target);
    }

    private void request(ConfigSection target) {
        Page state = pages.get(target);
        if (state.pending == Operation.NONE) {
            state.pending = Operation.LOAD;
            ConfigEditorPackets.request(target);
            if (target == section && Minecraft.getInstance().screen == this) {
                refreshRows();
            }
        }
    }

    private void goUp() {
        if (mode != Mode.EDIT) {
            show(Mode.EDIT);
        } else if (!page().path.isEmpty()) {
            List<String> path = page().path;
            navigate(path.subList(0, path.size() - 1));
        }
    }

    private void navigate(List<String> path) {
        page().path = List.copyOf(path);
        page().offset = 0;
        rebuildWidgets();
    }

    private void show(Mode next) {
        mode = next;
        page().offset = 0;
        rebuildWidgets();
    }

    private void refreshRows() {
        for (AbstractWidget widget : body) {
            if (getFocused() == widget) {
                setFocused(null);
            }
            removeWidget(widget);
        }
        body.clear();
        inputs.clear();
        lines.clear();
        Page page = page();
        JsonElement node = nodeAt(page.draft.document, page.path);
        if (node == null) {
            rowCount = 0;
            lines.add(new Line(words("Waiting for a server snapshot.", "等待服务器配置快照。"), left + 12,
                    contentTop + 8, MUTED, 0, null));
        } else if (mode == Mode.PREVIEW) {
            renderPreviewRows(page);
        } else if (mode == Mode.CHANGES) {
            renderChangeRows(page);
        } else {
            renderEditRows(page, node);
        }
        previousButton.active = page.offset > 0;
        nextButton.active = page.offset + rowsPerPage() < rowCount;
        updateActions();
    }

    private void renderEditRows(Page page, JsonElement node) {
        List<Row> rows = new ArrayList<>();
        String[] filter = searchTerms(page.search);
        if (filter.length > 0 && !page.path.isEmpty()
                && matches(filter, breadcrumb().getString())) {
            filter = new String[0];
        }
        if (node.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : node.getAsJsonObject().entrySet()) {
                addMatchingRow(rows, filter, page.path, entry.getKey(), entry.getValue());
            }
        } else if (node.isJsonArray()) {
            for (int index = 0; index < node.getAsJsonArray().size(); index++) {
                addMatchingRow(rows, filter, page.path, Integer.toString(index), node.getAsJsonArray().get(index));
            }
        }
        setRowCount(rows.size());
        if (rows.isEmpty()) {
            Component message = filter.length == 0 ? words("This list is empty.", "此处暂无条目。")
                    : words("No matching entries.", "没有匹配的条目。");
            lines.add(new Line(message, left + 12, contentTop + 8, MUTED, 0, null));
        }
        for (int index = page.offset; index < Math.min(rows.size(), page.offset + rowsPerPage()); index++) {
            Row row = rows.get(index);
            renderEntry(node, row, contentTop + ROW_HEIGHT * (index - page.offset));
        }
    }

    private void addMatchingRow(List<Row> rows, String[] filter, List<String> parent, String key, JsonElement value) {
        List<String> path = childPath(parent, key);
        ConfigEditorPages.Field field = ConfigEditorPages.field(section, path, value);
        Component display = ConfigEditorPages.displayName(section, path, value);
        Component label = display == null ? field.label() : display;
        if (filter.length == 0 || matches(filter, key + " " + field.label().getString() + " " + label.getString())
                || matchingTree(filter, value)) {
            rows.add(new Row(key, path, value, label, field));
        }
    }

    private boolean matchingTree(String[] filter, JsonElement node) {
        if (node.isJsonPrimitive()) {
            String value = node.getAsString();
            return matchingItem(filter, value);
        }
        if (node.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : node.getAsJsonObject().entrySet()) {
                if (matches(filter, entry.getKey() + " " + ConfigEditorPages.label(entry.getKey()).getString())
                        || matchingItem(filter, entry.getKey()) || matchingTree(filter, entry.getValue())) {
                    return true;
                }
            }
        } else if (node.isJsonArray()) {
            JsonArray array = node.getAsJsonArray();
            for (int index = 0; index < array.size(); index++) {
                if (matchingTree(filter, array.get(index))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matchingItem(String[] filter, String value) {
        if (matches(filter, value)) {
            return true;
        }
        ItemStack stack = value.indexOf(':') < 0 ? ItemStack.EMPTY : item(value);
        return !stack.isEmpty() && matches(filter, value + " " + stack.getHoverName().getString());
    }

    private void renderEntry(JsonElement parent, Row row, int y) {
        int controls = right - 10;
        boolean mutable = page().pending == Operation.NONE;
        List<String> parentPath = page().path;
        boolean arrayEntry = parent.isJsonArray();
        if (ConfigEditorPages.canRemoveField(section, parentPath, row.key)) {
            controls -= 22;
            bodyButton(Component.literal("×"), words("Remove entry", "删除条目"), controls, y, 20, mutable, () -> {
                if (arrayEntry) {
                    parent.getAsJsonArray().remove(Integer.parseInt(row.key));
                } else {
                    parent.getAsJsonObject().remove(row.key);
                }
                refreshRows();
            });
        }
        if (arrayEntry && ConfigEditorPages.canAddEntry(section, parentPath)) {
            JsonArray array = parent.getAsJsonArray();
            int index = Integer.parseInt(row.key);
            controls -= 23;
            bodyButton(Component.literal("+1"), words("Duplicate entry", "复制条目"), controls, y, 21, mutable,
                    () -> duplicate(array, index));
            controls -= 22;
            bodyButton(Component.literal("▼"), words("Move down", "下移"), controls, y, 20,
                    mutable && index + 1 < array.size(), () -> move(array, index, index + 1));
            controls -= 22;
            bodyButton(Component.literal("▲"), words("Move up", "上移"), controls, y, 20,
                    mutable && index > 0, () -> move(array, index, index - 1));
        }
        ItemStack icon = icon(row);
        int x = left + (icon.isEmpty() ? 11 : 34);
        if (!icon.isEmpty()) {
            lines.add(new Line(Component.empty(), left + 12, y + 2, TEXT, 0, icon));
        }
        if (row.value.isJsonObject() || row.value.isJsonArray()) {
            Component title = Component.literal(row.label.getString() + "   ›");
            bodyButton(fit(title, controls - x - 8), row.label, x, y, controls - x - 2, true,
                    () -> navigate(row.path));
            return;
        }
        int editX = Math.max(x + 30, Math.min(left + (right - left) / 3, controls - 76));
        lines.add(new Line(row.label, x, y + 6, TEXT, editX - x - 6, null));
        JsonPrimitive value = row.value.isJsonPrimitive() ? row.value.getAsJsonPrimitive() : null;
        if (row.field.kind() == ConfigEditorPages.Kind.READ_ONLY || value == null) {
            lines.add(new Line(Component.literal(row.value.toString()), editX, y + 6, MUTED,
                    controls - editX - 3, null));
            return;
        }
        Consumer<JsonElement> replace = replacement -> {
            if (arrayEntry) {
                parent.getAsJsonArray().set(Integer.parseInt(row.key), replacement);
            } else {
                parent.getAsJsonObject().add(row.key, replacement);
            }
        };
        if (value.isBoolean()) {
            bodyButton(value.getAsBoolean() ? words("Enabled", "启用") : words("Disabled", "禁用"),
                    row.label, editX, y, controls - editX - 2, mutable, () -> {
                        replace.accept(new JsonPrimitive(!value.getAsBoolean()));
                        refreshRows();
                    });
            return;
        }
        if (row.field.kind() == ConfigEditorPages.Kind.ITEM) {
            controls -= 24;
            bodyButton(Component.literal("H"), words("Use main-hand item ID", "填入主手物品 ID"), controls, y, 22,
                    mutable && heldItemId() != null, () -> {
                        String held = heldItemId();
                        if (held != null) {
                            replace.accept(new JsonPrimitive(held));
                            refreshRows();
                        }
                    });
            controls -= 25;
            bodyButton(Component.literal("…"), words("Find item", "查找物品"), controls, y, 23, mutable,
                    () -> minecraft.setScreen(ConfigEditorPickerScreen.forItems(this,
                            id -> replace.accept(new JsonPrimitive(id)))));
        } else if (!row.field.choices().isEmpty()) {
            controls -= 25;
            bodyButton(Component.literal("…"), words("Choose value", "选择值"), controls, y, 23, mutable,
                    () -> choose(row.field.label(), row.field.choices().stream()
                                    .map(ConfigEditorPickerScreen::namedChoice).toList(),
                            answer -> replace.accept(new JsonPrimitive(answer))));
        } else if (section == ConfigSection.TROPHY && row.key.equals("nbt")) {
            controls -= 24;
            bodyButton(Component.literal("H"), words("Copy main-hand SNBT into field", "填入主手物品 SNBT"),
                    controls, y, 22, mutable && heldItemId() != null, () -> {
                        if (heldItemId() != null) {
                            var tag = minecraft.player.getMainHandItem().getTag();
                            replace.accept(new JsonPrimitive(tag == null ? "" : tag.toString()));
                            refreshRows();
                        }
                    });
        }
        Set<JsonElement> invalidNumbers = page().invalidNumbers;
        boolean numeric = value.isNumber() || invalidNumbers.contains(value);
        EditBox input = bodyInput(row.label, value.getAsString(), editX, y, Math.max(20, controls - editX - 2),
                mutable, numeric ? 100 : 32767);
        input.setTextColor(invalidNumbers.contains(value) ? WARNING : TEXT);
        input.setResponder(text -> {
            boolean valid = true;
            JsonPrimitive next = new JsonPrimitive(text);
            if (numeric) {
                try {
                    valid = NUMBER.matcher(text).matches();
                    if (valid) {
                        next = new JsonPrimitive(new BigDecimal(text));
                    }
                } catch (NumberFormatException exception) {
                    valid = false;
                }
                if (!valid) {
                    invalidNumbers.add(next);
                }
            }
            replace.accept(next);
            input.setTextColor(valid ? TEXT : WARNING);
            updateActions();
        });
    }

    private void duplicate(JsonArray array, int index) {
        array.add(array.get(array.size() - 1));
        for (int position = array.size() - 1; position > index + 1; position--) {
            array.set(position, array.get(position - 1));
        }
        array.set(index + 1, array.get(index).deepCopy());
        refreshRows();
    }

    private void move(JsonArray array, int from, int to) {
        JsonElement value = array.get(from);
        array.set(from, array.get(to));
        array.set(to, value);
        refreshRows();
    }

    private void addEntry() {
        Page page = page();
        JsonElement node = nodeAt(page.draft.document, page.path);
        if (node == null) {
            return;
        }
        if (node.isJsonArray()) {
            JsonArray array = node.getAsJsonArray();
            JsonElement entry = ConfigEditorPages.newElement(section, page.path, array);
            if (entry != null && entry.isJsonPrimitive()
                    && ConfigEditorPages.field(section, childPath(page.path, Integer.toString(array.size())), entry)
                    .kind() == ConfigEditorPages.Kind.ITEM) {
                minecraft.setScreen(ConfigEditorPickerScreen.forItems(this, id -> {
                    array.add(id);
                    page.search = "";
                    page.offset = Math.max(0, array.size() - rowsPerPage());
                }));
            } else if (entry != null) {
                array.add(entry);
                clearFilterAtEnd();
            }
            return;
        }
        JsonObject object = node.getAsJsonObject();
        Map<String, JsonElement> optional = missingFields(object);
        if (!optional.isEmpty()) {
            choose(words("Add field", "添加字段"), optional.keySet().stream()
                    .map(key -> new ConfigEditorPickerScreen.Choice(key, ConfigEditorPages.label(key))).toList(),
                    key -> {
                        object.add(key, optional.get(key).deepCopy());
                        page.search = "";
                    });
            return;
        }
        Consumer<String> add = key -> {
            JsonElement entry = ConfigEditorPages.newObjectEntry(section, page.path, key);
            if (!key.isBlank() && !object.has(key) && entry != null) {
                addObjectEntry(object, key, entry);
            } else {
                invalidEntryId();
            }
        };
        if (section == ConfigSection.EQUIPMENT && page.path.equals(List.of("items"))) {
            minecraft.setScreen(ConfigEditorPickerScreen.forItems(this, add));
        } else {
            minecraft.setScreen(new EntryIdScreen(words("New entry ID", "新增条目 ID"), add));
        }
    }

    private void addObjectEntry(JsonObject object, String key, JsonElement entry) {
        object.add(key, entry);
        Page page = page();
        page.search = "";
        page.path = childPath(page.path, key);
        page.offset = 0;
    }

    private void invalidEntryId() {
        page().message = words("That ID is invalid or already exists.", "此 ID 无效或已存在。");
        page().error = true;
    }

    private void clearFilterAtEnd() {
        Page page = page();
        page.search = "";
        JsonElement node = nodeAt(page.draft.document, page.path);
        page.offset = node.isJsonArray() ? Math.max(0, node.getAsJsonArray().size() - rowsPerPage()) : 0;
        rebuildWidgets();
    }

    private Map<String, JsonElement> missingFields(JsonObject object) {
        Map<String, JsonElement> fields = new LinkedHashMap<>(ConfigEditorPages.optionalFields(section, page().path));
        fields.keySet().removeAll(object.keySet());
        return fields;
    }

    private void choose(Component title, List<ConfigEditorPickerScreen.Choice> choices, Consumer<String> setter) {
        minecraft.setScreen(new ConfigEditorPickerScreen(this, title, choices, false, setter));
    }

    private void renderPreviewRows(Page page) {
        List<ConfigEditorPages.PreviewInput> previewInputs = ConfigEditorPages.previewInputs(section);
        EnumMap<ConfigSection, JsonObject> documents = new EnumMap<>(ConfigSection.class);
        pages.forEach((key, value) -> {
            if (value.draft.document != null) {
                documents.put(key, value.draft.document);
            }
        });
        List<Component> output;
        try {
            output = ConfigEditorPages.preview(section, page.draft.document, page.path, minecraft,
                    documents, page.previewValues);
        } catch (RuntimeException exception) {
            output = List.of(words("Check the draft values to calculate the preview.", "请检查草稿中的数值后再预览。"));
        }
        setRowCount(previewInputs.size() + output.size());
        for (int index = page.offset; index < Math.min(rowCount, page.offset + rowsPerPage()); index++) {
            int y = contentTop + (index - page.offset) * ROW_HEIGHT;
            if (index >= previewInputs.size()) {
                lines.add(new Line(output.get(index - previewInputs.size()), left + 12, y + 6, TEXT,
                        right - left - 24, null));
            } else {
                ConfigEditorPages.PreviewInput input = previewInputs.get(index);
                int editX = left + (right - left) * 2 / 5;
                lines.add(new Line(input.label(), left + 12, y + 6, MUTED, editX - left - 18, null));
                EditBox edit = bodyInput(input.label(), page.previewValues.getOrDefault(input.id(),
                        input.initialValue()), editX, y, right - editX - 60, true, 256);
                edit.setResponder(text -> page.previewValues.put(input.id(), text));
                bodyButton(words("Run", "计算"), words("Update preview", "更新预览"), right - 61, y, 50, true,
                        this::refreshRows);
            }
        }
    }

    private void renderChangeRows(Page page) {
        List<ConfigEditorDraft.Change> changes = ConfigEditorDraft.differences(page.draft.baseline, page.draft.document);
        setRowCount(changes.size());
        if (changes.isEmpty()) {
            lines.add(new Line(words("No unsaved changes.", "没有未保存的变更。"), left + 12,
                    contentTop + 8, MUTED, 0, null));
        }
        for (int index = page.offset; index < Math.min(changes.size(), page.offset + rowsPerPage()); index++) {
            ConfigEditorDraft.Change change = changes.get(index);
            int y = contentTop + (index - page.offset) * ROW_HEIGHT;
            lines.add(new Line(Component.literal(change.path()), left + 12, y + 1, GOLD, right - left - 24, null));
            String text = displayValue(change.before()) + "  →  " + displayValue(change.after());
            lines.add(new Line(Component.literal(text), left + 12, y + 12, TEXT, right - left - 24, null));
        }
    }

    private String displayValue(JsonElement value) {
        if (value == null) {
            return "∅";
        }
        return value.toString();
    }

    private void discard() {
        Page page = page();
        confirm(words("Discard this draft?", "丢弃此页草稿？"),
                words("The page will load the latest server version.", "此页将载入服务器的最新版本。"), () -> {
                    page.draft.reset();
                    page.invalidNumbers.clear();
                    page.message = Component.empty();
                    page.error = false;
                    page.path = List.of();
                    page.offset = 0;
                    mode = Mode.EDIT;
                    rebuildWidgets();
                });
    }

    private void restore() {
        ConfigSection selected = section;
        Page page = page();
        confirm(words("Restore the previous version?", "恢复上一版配置？"),
                words("The server will replace the current version for this tab. Any draft on this tab will be discarded.",
                        "服务器将用上一版替换当前页的配置。此页草稿也将丢弃。"), () -> {
                    page.pending = Operation.RESTORE;
                    ConfigEditorPackets.restore(selected, page.draft.revision);
                    refreshRows();
                });
    }

    private void save() {
        if (mode != Mode.CHANGES) {
            show(Mode.CHANGES);
            return;
        }
        ConfigSection selected = section;
        Page page = page();
        confirm(words("Save these changes?", "保存这些变更？"),
                words("The server will validate and apply changes on this tab.", "服务器将校验并应用此页变更。"), () -> {
                    page.pending = Operation.SAVE;
                    ConfigEditorPackets.save(selected, page.draft.revision, page.draft.document.deepCopy());
                    refreshRows();
                });
    }

    private void confirm(Component title, Component details, Runnable action) {
        minecraft.setScreen(new ConfirmScreen(confirmed -> {
            minecraft.setScreen(this);
            if (confirmed) {
                action.run();
            }
        }, title, details));
    }

    private void updateActions() {
        Page page = page();
        JsonElement node = nodeAt(page.draft.document, page.path);
        boolean loaded = page.draft.document != null;
        boolean ready = loaded && page.pending == Operation.NONE;
        page.invalid = loaded && hasInvalid(page.draft.document, page.invalidNumbers);
        page.dirty = page.draft.dirty() || page.invalid;
        page.conflicted = page.draft.conflicted();
        addButton.active = ready && mode == Mode.EDIT && node != null
                && (ConfigEditorPages.canAddEntry(section, page.path)
                || node.isJsonObject() && !missingFields(node.getAsJsonObject()).isEmpty());
        reloadButton.active = mode == Mode.EDIT && page.pending == Operation.NONE;
        previewButton.active = loaded && mode != Mode.PREVIEW;
        changesButton.active = loaded && mode != Mode.CHANGES;
        discardButton.active = ready && page.dirty;
        restoreButton.active = ready && !page.conflicted;
        saveButton.active = ready && page.dirty && !page.invalid && !page.conflicted;
        copyHeldButton.active = heldItemId() != null;
    }

    private static boolean hasInvalid(JsonElement element, Set<JsonElement> invalid) {
        if (element == null || invalid.isEmpty()) {
            return false;
        }
        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                if (hasInvalid(child, invalid)) {
                    return true;
                }
            }
        } else if (element.isJsonObject()) {
            for (JsonElement child : element.getAsJsonObject().asMap().values()) {
                if (hasInvalid(child, invalid)) {
                    return true;
                }
            }
        }
        return invalid.contains(element);
    }

    @Override
    public void tick() {
        search.tick();
        inputs.forEach(EditBox::tick);
        copyHeldButton.active = heldItemId() != null;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX >= left && mouseX <= right && mouseY >= contentTop && mouseY < contentBottom && delta != 0) {
            scroll(delta > 0 ? -3 : 3);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private void scroll(int amount) {
        Page page = page();
        int target = Math.min(Math.max(0, page.offset + amount), Math.max(0, rowCount - rowsPerPage()));
        if (page.offset != target) {
            page.offset = target;
            refreshRows();
        }
    }

    private int rowsPerPage() {
        return Math.max(1, (contentBottom - contentTop) / ROW_HEIGHT);
    }

    private void setRowCount(int count) {
        rowCount = count;
        page().offset = Math.max(0, Math.min(page().offset, Math.max(0, rowCount - rowsPerPage())));
    }

    @Override
    public void onClose() {
        requestClose(this);
    }

    void requestClose(Screen current) {
        Minecraft minecraft = Minecraft.getInstance();
        if (occupationEditor.hasPendingWrite()
                || pages.values().stream().anyMatch(page -> page.pending == Operation.SAVE || page.pending == Operation.RESTORE)) {
            return;
        }
        if (occupationEditor.hasDraft() || pages.values().stream().anyMatch(page -> page.draft.dirty())) {
            minecraft.setScreen(new ConfirmScreen(confirmed -> {
                if (confirmed) closeEditor();
                else minecraft.setScreen(current);
            }, words("Close the configuration editor?", "关闭配置编辑器？"),
                    words("All unsaved drafts will be discarded.", "所有未保存的草稿将丢弃。")));
        } else {
            closeEditor();
        }
    }

    private void closeEditor() {
        lastEditor.clear();
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        panel(graphics, left, right, 8, height - 7);
        graphics.drawString(font, words("Config editor", "配置编辑器"), left + 10, 17, GOLD, false);
        Page page = page();
        if (page.dirty) {
            graphics.drawString(font, words("Draft", "草稿"), right - 136, 17, WARNING, false);
        }
        Component heading = switch (mode) {
            case EDIT -> breadcrumb();
            case PREVIEW -> words("Preview", "预览");
            case CHANGES -> words("Review changes", "核对变更");
        };
        graphics.drawString(font, fit(heading, right - left - 150), left + 72, 63, TEXT, false);
        int shown = Math.min(rowsPerPage(), rowCount - page.offset);
        for (int row = 0; row < shown; row++) {
            int y = contentTop + row * ROW_HEIGHT;
            graphics.fill(left + 7, y, right - 7, y + ROW_HEIGHT - 1,
                    row % 2 == 0 ? 0xFF2B2B30 : 0xFF25252A);
        }
        for (Line line : lines) {
            if (line.icon != null) {
                graphics.renderItem(line.icon, line.x, line.y);
            } else {
                graphics.drawString(font, line.width == 0 ? line.text : fit(line.text, line.width),
                        line.x, line.y, line.color, false);
            }
        }
        Component status = status(page);
        int statusColor = page.conflicted || page.error || page.invalid ? WARNING : MUTED;
        graphics.drawString(font, fit(status, right - left - 22), left + 10, height - 46, statusColor, false);
        super.render(graphics, mouseX, mouseY, partialTick);
        for (Line line : lines) {
            if (line.width > 0 && font.width(line.text) > line.width && mouseX >= line.x
                    && mouseX <= line.x + line.width && mouseY >= line.y && mouseY < line.y + 10) {
                renderWrappedTooltip(graphics, line.text, mouseX, mouseY);
                break;
            }
        }
        if (!status.getString().isEmpty() && mouseX >= left + 10 && mouseX < right - 10
                && mouseY >= height - 47 && mouseY <= height - 36) {
            renderWrappedTooltip(graphics, status, mouseX, mouseY);
        }
    }

    private void renderWrappedTooltip(GuiGraphics graphics, Component text, int mouseX, int mouseY) {
        var wrapped = font.split(text, Math.min(480, width - 36));
        int maxLines = Math.max(1, (height - 28) / 10);
        graphics.renderTooltip(font, wrapped.subList(0, Math.min(maxLines, wrapped.size())), mouseX, mouseY);
    }

    private Component status(Page page) {
        if (page.pending != Operation.NONE) {
            return words("Waiting for the server…", "等待服务器响应…");
        }
        if (page.conflicted) {
            return words("Server version changed. Discard this draft to load it.", "服务器版本已更新。丢弃此草稿后可载入新版。");
        }
        if (page.invalid) {
            return words("Enter a valid number in every numeric field.", "请在所有数字字段中输入有效数值。");
        }
        return page.message;
    }

    private Component breadcrumb() {
        StringBuilder result = new StringBuilder(section.label().getString());
        JsonElement node = page().draft.document;
        List<String> prefix = List.of();
        for (String key : page().path) {
            JsonElement child = childAt(node, key);
            prefix = childPath(prefix, key);
            Component name = child == null ? null : ConfigEditorPages.displayName(section, prefix, child);
            result.append("  /  ").append(name == null
                    ? ConfigEditorPages.label(key).getString() : name.getString());
            node = child;
        }
        return Component.literal(result.toString());
    }

    private Page page() {
        return pages.get(section);
    }

    private Button button(Component label, int x, int y, int width, Runnable action) {
        return addRenderableWidget(Button.builder(fit(label, width - 6), pressed -> action.run())
                .bounds(x, y, width, 19).tooltip(Tooltip.create(label)).build());
    }

    private void bodyButton(Component label, Component tooltip, int x, int y, int width, boolean active, Runnable action) {
        Button button = addRenderableWidget(Button.builder(label, pressed -> action.run())
                .bounds(x, y, width, 20).tooltip(Tooltip.create(tooltip)).build());
        button.active = active;
        body.add(button);
    }

    private EditBox bodyInput(Component label, String value, int x, int y, int width, boolean editable, int maxLength) {
        EditBox edit = addRenderableWidget(new EditBox(font, x, y, width, 20, label));
        edit.setMaxLength(Math.max(maxLength, value.length()));
        edit.setValue(value);
        edit.setEditable(editable);
        edit.setTooltip(Tooltip.create(label));
        body.add(edit);
        inputs.add(edit);
        return edit;
    }

    private Component fit(Component text, int limit) {
        return fit(font, text, limit);
    }

    private static Component fit(Font font, Component text, int limit) {
        if (font.width(text) <= limit) {
            return text;
        }
        return Component.literal(font.plainSubstrByWidth(text.getString(), Math.max(0, limit - font.width("…"))) + "…");
    }

    private ItemStack icon(Row row) {
        if (row.field.kind() == ConfigEditorPages.Kind.ITEM && row.value.isJsonPrimitive()) {
            return item(row.value.getAsString());
        }
        if (section == ConfigSection.EQUIPMENT && page().path.equals(List.of("items"))) {
            return item(row.key);
        }
        return ItemStack.EMPTY;
    }

    String heldItemId() {
        if (minecraft == null || minecraft.player == null) {
            return null;
        }
        ItemStack held = minecraft.player.getMainHandItem();
        return held.isEmpty() ? null : BuiltInRegistries.ITEM.getKey(held.getItem()).toString();
    }

    private void copyHeldId() {
        String id = heldItemId();
        if (id != null) {
            minecraft.keyboardHandler.setClipboard(id);
            page().message = words("Copied " + id, "已复制 " + id);
            page().error = false;
        }
    }

    Component words(String english, String chinese) {
        return Component.literal("zh_cn".equals(Minecraft.getInstance().getLanguageManager().getSelected())
                ? chinese : english);
    }

    static ItemStack item(String key) {
        ResourceLocation id = ResourceLocation.tryParse(key);
        if (id == null) {
            return ItemStack.EMPTY;
        }
        var item = BuiltInRegistries.ITEM.get(id);
        return item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
    }

    static String[] searchTerms(String filter) {
        String query = filter.strip().toLowerCase(Locale.ROOT);
        return query.isEmpty() ? new String[0] : query.split("\\s+");
    }

    static boolean matches(String[] filter, String value) {
        String search = value.toLowerCase(Locale.ROOT);
        for (String token : filter) {
            if (!search.contains(token)) {
                return false;
            }
        }
        return true;
    }

    static void panel(GuiGraphics graphics, int left, int right, int top, int bottom) {
        graphics.fill(left, top, right, bottom, 0xFF9E8155);
        graphics.fill(left + 1, top + 1, right - 1, bottom - 1, 0xFF17181B);
        graphics.fill(left + 3, top + 3, right - 3, bottom - 3, 0xFF222328);
        graphics.fill(left + 3, top + 3, right - 3, top + 4, 0xFF61513A);
    }

    private static List<String> childPath(List<String> path, String key) {
        List<String> result = new ArrayList<>(path);
        result.add(key);
        return List.copyOf(result);
    }

    private static JsonElement nodeAt(JsonElement root, List<String> path) {
        JsonElement node = root;
        for (String key : path) {
            node = childAt(node, key);
        }
        return node;
    }

    private static JsonElement childAt(JsonElement node, String key) {
        if (node != null && node.isJsonObject()) {
            return node.getAsJsonObject().get(key);
        }
        if (node != null && node.isJsonArray()) {
            try {
                int index = Integer.parseInt(key);
                JsonArray array = node.getAsJsonArray();
                return index < 0 || index >= array.size() ? null : array.get(index);
            } catch (NumberFormatException exception) {
                return null;
            }
        }
        return null;
    }

    private enum Mode { EDIT, PREVIEW, CHANGES }

    private enum Operation { NONE, LOAD, SAVE, RESTORE }

    private static final class Page {
        final ConfigEditorDraft draft = new ConfigEditorDraft();
        final Set<JsonElement> invalidNumbers = Collections.newSetFromMap(new IdentityHashMap<>());
        final Map<String, String> previewValues = new LinkedHashMap<>();
        List<String> path = List.of();
        String search = "";
        int offset;
        Operation pending = Operation.NONE;
        Component message = Component.empty();
        boolean error;
        boolean dirty;
        boolean conflicted;
        boolean invalid;
    }

    private record Row(String key, List<String> path, JsonElement value, Component label,
                       ConfigEditorPages.Field field) {}

    private record Line(Component text, int x, int y, int color, int width, ItemStack icon) {}

    private final class EntryIdScreen extends Screen {
        private final Consumer<String> accept;
        private EditBox entry;

        private EntryIdScreen(Component title, Consumer<String> accept) {
            super(title);
            this.accept = accept;
        }

        @Override
        protected void init() {
            int x = width / 2 - 105;
            int y = height / 2 - 8;
            entry = addRenderableWidget(new EditBox(font, x, y, 210, 20, title));
            entry.setMaxLength(256);
            Button add = addRenderableWidget(Button.builder(words("Add", "添加"), button -> {
                accept.accept(entry.getValue().strip());
                onClose();
            }).bounds(x + 110, y + 26, 100, 20).build());
            add.active = false;
            entry.setResponder(value -> add.active = !value.isBlank());
            addRenderableWidget(Button.builder(words("Back", "返回"), button -> onClose())
                    .bounds(x, y + 26, 100, 20).build());
            setInitialFocus(entry);
        }

        @Override
        public void tick() {
            entry.tick();
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            renderBackground(graphics);
            panel(graphics, width / 2 - 114, width / 2 + 114, height / 2 - 35, height / 2 + 47);
            graphics.drawCenteredString(font, title, width / 2, height / 2 - 23, GOLD);
            super.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public void onClose() {
            minecraft.setScreen(ConfigEditorScreen.this);
        }

        @Override
        public boolean isPauseScreen() {
            return false;
        }
    }
}
