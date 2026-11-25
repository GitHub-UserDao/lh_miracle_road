package dev.lhkongyu.lhmiracleroad.client.screen.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import dev.lhkongyu.lhmiracleroad.client.screen.occupation.OccupationText;
import dev.lhkongyu.lhmiracleroad.config.editor.ConfigEditorPackets;
import dev.lhkongyu.lhmiracleroad.config.editor.ConfigSection;
import dev.lhkongyu.lhmiracleroad.tool.ResourceLocationTool;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static dev.lhkongyu.lhmiracleroad.client.screen.config.ConfigEditorPickerScreen.words;

public final class OccupationEditorScreen extends Screen {
    private static final ConfigSection SECTION = ConfigSection.OCCUPATION;
    private static final int GOLD = ConfigEditorScreen.GOLD;
    private static final int MUTED = ConfigEditorScreen.MUTED;
    private static final int WARNING = 0xFAC476;
    private static final int NORMAL_ROW = 0xFF302D30;
    private static final int DETAIL_ROW = 0xFF27282D;
    private static final int REWARD_ROW = 0xFF32302A;
    private static final int TOP = ConfigEditorScreen.HEADER_BOTTOM + 2;
    private static final int ROW = 27;
    private static final int GAP = 3;
    private static final List<String> OPERATIONS = List.of("addition", "multiply_base", "multiply_total");

    private final ConfigEditorScreen editor;
    private final ConfigEditorDraft draft = new ConfigEditorDraft();
    private final Map<JsonObject, Set<String>> invalidNumbers = new IdentityHashMap<>();
    private final Set<JsonObject> expanded = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Pane jobPane = new Pane();
    private final Pane attributePane = new Pane();
    private String selectedId;
    private Operation pending = Operation.NONE;
    private Component message = Component.empty();
    private boolean error;
    private boolean dirty;
    private boolean conflicted;
    private RewardEdit rewardEdit;
    private Button selectJob;
    private Button createJob;
    private Button attributeAction;
    private Button reload;
    private Button discard;
    private Button restore;
    private Button save;
    private Button close;
    private int left;
    private int right;
    private int divider;
    private int panelBottom;
    private Pane draggingPane;

    OccupationEditorScreen(ConfigEditorScreen editor) {
        super(words("Config editor", "配置编辑器"));
        this.editor = editor;
    }

    boolean hasDraft() {
        return draft.dirty();
    }

    boolean hasPendingWrite() {
        return pending == Operation.SAVE || pending == Operation.RESTORE;
    }

    void receiveSnapshot(String revision, String json, String status) {
        applySnapshot(revision, json, status);
        if (minecraft != null && minecraft.screen == this) refresh();
    }

    private void applySnapshot(String revision, String json, String status) {
        Operation operation = pending;
        pending = Operation.NONE;
        error = !status.isBlank();
        message = error ? Component.literal(status) : Component.empty();
        if (json.isBlank()) return;
        try {
            JsonObject snapshot = JsonParser.parseString(json).getAsJsonObject();
            JsonObject previous = draft.document;
            boolean accept = !error && (operation == Operation.SAVE || operation == Operation.RESTORE);
            draft.receive(revision, snapshot, accept);
            if (draft.document != previous) {
                invalidNumbers.clear();
                expanded.clear();
                resetRewardEdit();
            }
            if (accept) {
                message = operation == Operation.SAVE
                        ? words("Saved to server.", "已保存到服务器。")
                        : words("Previous version restored.", "已恢复上一版。");
            } else if (!error && operation == Operation.LOAD) {
                message = draft.dirty()
                        ? words("Server version read. Your draft is still open.", "已读取服务器版本，草稿已保留。")
                        : words("Server version loaded.", "已载入服务器版本。");
            }
        } catch (IllegalStateException | JsonParseException exception) {
            error = true;
            message = words("Could not read the server snapshot.", "无法读取服务器快照。");
        }
    }

    @Override
    protected void init() {
        int panelWidth = Math.min(width - 16, 960);
        left = (width - panelWidth) / 2;
        right = left + panelWidth;
        divider = left + Math.min(310, panelWidth * 39 / 100);
        panelBottom = height - 52;
        draggingPane = null;
        editor.addSectionTabs(width, font, SECTION, this::addRenderableWidget);
        jobPane.layout(left + 5, divider - 7, TOP + 26, panelBottom - 5);
        attributePane.layout(divider + 9, right - 5, TOP + 26, panelBottom - 5);
        Component create = words("+ Job", "新增职业");
        int createWidth = Math.max(54, font.width(create) + 12);
        selectJob = button(Component.empty(), Component.empty(), left + 5, TOP + 4,
                divider - left - 12 - createWidth - GAP, 19, this::chooseOccupation);
        createJob = button(create, words("Add an occupation", "新增职业"),
                divider - 7 - createWidth, TOP + 4, createWidth, 19, this::createOccupation);
        int actionWidth = Math.max(font.width(addTypeLabel()), font.width(backLabel())) + 14;
        attributeAction = button(addTypeLabel(), addTypeLabel(), right - actionWidth - 5, TOP + 4, actionWidth, 19,
                () -> {
                    if (rewardEdit == null) addAttribute();
                    else closeRewardEdit();
                });

        int footerX = left + 5;
        int footerY = height - 29;
        int stride = (panelWidth - 7) / 5;
        reload = footer(words("Reload", "读取"), words("Read the latest server version", "读取服务器当前版本"),
                footerX, footerY, stride, () -> request(Operation.LOAD));
        discard = footer(words("Discard", "丢弃"), words("Discard the draft and load the last server version", "丢弃草稿并载入最近读取的服务器版本"),
                footerX + stride, footerY, stride, this::discard);
        restore = footer(words("Restore", "恢复上一版"), words("Restore the previous version on the server", "在服务器上恢复上一版"),
                footerX + 2 * stride, footerY, stride, this::restore);
        save = footer(words("Save", "保存"), words("Save all edited occupations", "保存所有已修改的职业"),
                footerX + 3 * stride, footerY, stride, this::save);
        close = footer(words("Close", "关闭"), words("Close the editor", "关闭编辑器"),
                footerX + 4 * stride, footerY, stride, this::requestCloseEditor);
        refresh();
    }

    private Button footer(Component label, Component tooltip, int x, int y, int stride, Runnable action) {
        return button(label, tooltip, x, y, stride - GAP, 19, action);
    }

    private void refresh() {
        JsonObject occupations = draft.document;
        if (object(occupations, selectedId) == null) {
            selectedId = occupations == null ? null : occupations.entrySet().stream()
                    .filter(entry -> entry.getValue().isJsonObject()).map(Map.Entry::getKey).sorted()
                    .findFirst().orElse(null);
        }
        updateOccupationLabel();
        refreshJobs();
        refreshAttributes();
        updateActions();
    }

    private void chooseOccupation() {
        JsonObject occupations = draft.document;
        if (occupations == null) return;
        List<ConfigEditorPickerScreen.Choice> choices = occupations.entrySet().stream()
                .filter(entry -> entry.getValue().isJsonObject())
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new ConfigEditorPickerScreen.Choice(entry.getKey(),
                        occupationLabel(entry.getKey(), entry.getValue().getAsJsonObject())))
                .toList();
        choose(words("Select occupation", "选择职业"), choices, id -> {
            if (id.equals(selectedId)) return;
            selectOccupation(id);
        });
    }

    private void createOccupation() {
        JsonObject occupations = draft.document;
        minecraft.setScreen(new OccupationEditorCreateScreen(this, occupations::has, (id, occupation) -> {
            occupations.add(id, occupation);
            selectOccupation(id);
        }));
    }

    private void selectOccupation(String id) {
        resetRewardEdit();
        selectedId = id;
        jobPane.offset = 0;
        attributePane.offset = 0;
    }

    private void choose(Component title, List<ConfigEditorPickerScreen.Choice> choices, Consumer<String> accept) {
        minecraft.setScreen(new ConfigEditorPickerScreen(this, title, choices, false, accept));
    }

    private Component occupationLabel(String id, JsonObject occupation) {
        String name = string(occupation, "name", id);
        return name.isBlank() || name.equals(id)
                ? Component.translatableWithFallback(ResourceLocationTool.OCCUPATION_NAME_PREFIX + id, id)
                : Component.literal(name);
    }

    private void updateOccupationLabel() {
        Component choice = selectedId == null ? words("Select occupation", "选择职业")
                : occupationLabel(selectedId, selectedOccupation());
        selectJob.setMessage(fit(Component.literal(choice.getString() + "  ▾"), selectJob.getWidth() - 8));
        selectJob.setTooltip(Tooltip.create(choice));
    }

    private JsonObject selectedOccupation() {
        return object(draft.document, selectedId);
    }

    private void refreshJobs() {
        jobPane.rows.clear();
        JsonObject occupation = selectedOccupation();
        if (occupation != null) {
            jobPane.add(occupation, DETAIL_ROW, (x, y, w) -> {
                jobPane.line(words("Occupation ID", "职业 ID"), x, y + 1, w, MUTED);
                jobPane.line(Component.literal(selectedId), x, y + 13, w, 0xFFFFFF);
            });
            jobPane.add(occupation, NORMAL_ROW, (x, y, w) -> {
                int label = Math.min(w / 3, 70);
                jobPane.line(words("Name", "名称"), x, y + 9, label, MUTED);
                jobPane.input(occupation, "name", words("Occupation name", "职业名称"),
                        selectedId, x + label, y + 4, w - label, 19, NumberKind.TEXT);
            });
        }
        jobPane.showRows();
    }

    private Component addTypeLabel() {
        return words("+ Type", "+ 属性类型");
    }

    private Component backLabel() {
        return words("Back to list", "返回列表");
    }

    private void refreshAttributes() {
        attributePane.rows.clear();
        Component action = rewardEdit == null ? addTypeLabel() : backLabel();
        attributeAction.setMessage(action);
        attributeAction.setTooltip(Tooltip.create(action));
        if (rewardEdit != null) {
            rewardEditorRows(rewardEdit);
            attributePane.showRows();
            return;
        }
        JsonArray attributes = array(selectedOccupation(), "occupationAttributes");
        if (attributes == null || attributes.isEmpty()) {
            attributePane.add(null, DETAIL_ROW, (x, y, w) ->
                    attributePane.line(words("No attribute types", "暂无属性类型"), x, y + 9, w, MUTED));
        } else {
            for (JsonElement element : attributes) {
                if (element instanceof JsonObject attribute) attributeRows(attributes, attribute);
            }
        }
        attributePane.showRows();
    }

    private void addAttribute() {
        JsonArray attributes = ensureArray(selectedOccupation(), "occupationAttributes");
        JsonObject added = ConfigEditorPages.newOccupationAttribute(attributes);
        attributes.add(added);
        refreshAttributes();
        attributePane.reveal(added);
        updateActions();
    }

    private void attributeRows(JsonArray attributes, JsonObject attribute) {
        JsonArray rewards = array(attribute, "pointsRewards");
        boolean open = expanded.contains(attribute);
        int rewardCount = rewards == null ? 0 : rewards.size();
        attributePane.add(attribute, NORMAL_ROW, (x, y, w) -> {
            int toggle = 25;
            int remove = 17;
            int level = Math.max(24, Math.min(55, w / 9));
            int textWidth = w - toggle - remove - level * 2 - GAP * 5;
            int nameWidth = textWidth / 2;
            int idWidth = textWidth - nameWidth;
            attributePane.action(Component.literal((open ? "−" : "+") + rewardCount),
                    open ? words("Collapse rewards", "收起属性奖励") : words("Expand rewards", "展开属性奖励"),
                    x, y + 7, toggle, 18, () -> {
                        if (open) expanded.remove(attribute);
                        else expanded.add(attribute);
                        refreshAttributes();
                        updateActions();
                    });
            int nameX = x + toggle + GAP;
            int idX = nameX + nameWidth + GAP;
            int initX = idX + idWidth + GAP;
            int maxX = initX + level + GAP;
            String typeId = string(attribute, "type_id", "");
            EditBox name = attributePane.labelled(attribute, "name", words("Name", "名称"), "",
                    nameX, y, nameWidth, NumberKind.TEXT);
            name.setHint(fit(Component.translatableWithFallback(ResourceLocationTool.ATTRIBUTE_NAME_PREFIX + typeId,
                    typeId), name.getInnerWidth()));
            attributePane.labelled(attribute, "type_id", words("Type ID", "类型 ID"), "", idX, y, idWidth, NumberKind.TEXT);
            attributePane.labelled(attribute, "init_lv", words("Start", "初始"), "0", initX, y, level, NumberKind.INTEGER);
            attributePane.labelled(attribute, "max_lv", words("Max", "上限"), "0", maxX, y, level, NumberKind.INTEGER);
            attributePane.action(Component.literal("×"), words("Remove attribute type", "移除属性类型"),
                    x + w - remove, y + 7, remove, 18, () -> confirm(
                            words("Remove this attribute type?", "移除此属性类型？"),
                            words("Its reward rows will also be removed.", "该类型下的奖励也将移除。"),
                            () -> {
                                if (rewards != null) {
                                    for (JsonElement reward : rewards) invalidNumbers.remove(reward);
                                }
                                remove(attributes, attribute);
                                expanded.remove(attribute);
                                refreshAttributes();
                                updateActions();
                            }));
        });
        if (!open) return;
        if (rewards != null) {
            for (JsonElement element : rewards) {
                if (element instanceof JsonObject reward) rewardSummary(attribute, rewards, reward);
            }
        }
        attributePane.add(null, DETAIL_ROW, (x, y, w) ->
                attributePane.action(words("+ Reward", "+ 奖励"), words("Choose an attribute to reward", "选择要奖励的具体属性"),
                        x + 7, y + 4, Math.min(94, w - 7), 19, () -> pickReward(attribute, null)));
    }

    private void rewardSummary(JsonObject type, JsonArray rewards, JsonObject reward) {
        String id = rewardId(reward);
        Component name = rewardName(id);
        attributePane.add(reward, REWARD_ROW, (x, y, w) -> {
            Component edit = words("Edit", "编辑");
            int editWidth = Math.max(39, font.width(edit) + 10);
            int textWidth = w - editWidth - 10;
            attributePane.line(name, x + 6, y + 3, textWidth, 0xFFFFFF);
            attributePane.line(Component.literal(id), x + 6, y + 15, textWidth, MUTED);
            attributePane.action(edit, withValue(name, id), x + w - editWidth, y + 4, editWidth, 19, () -> {
                openRewardEdit(type, rewards, reward);
                refreshAttributes();
                updateActions();
            });
        });
    }

    private void openRewardEdit(JsonObject type, JsonArray rewards, JsonObject reward) {
        rewardEdit = new RewardEdit(type, rewards, reward, attributePane.offset);
        attributePane.offset = 0;
    }

    private void resetRewardEdit() {
        if (rewardEdit == null) return;
        attributePane.offset = rewardEdit.listOffset();
        rewardEdit = null;
    }

    private void closeRewardEdit() {
        resetRewardEdit();
        refreshAttributes();
        updateActions();
    }

    private void rewardEditorRows(RewardEdit edit) {
        JsonObject reward = edit.reward();
        attributePane.add(reward, REWARD_ROW, (x, y, w) -> {
            Component find = words("Choose attribute", "选择属性");
            int findWidth = font.width(find) + 12;
            String id = rewardId(reward);
            attributePane.line(rewardName(id), x + 1, y + 3, w - findWidth - GAP, 0xFFFFFF);
            attributePane.line(Component.literal(id), x + 1, y + 15, w - findWidth - GAP, MUTED);
            attributePane.action(find, find, x + w - findWidth, y + 4, findWidth, 19,
                    () -> pickReward(edit.type(), reward));
        });
        attributePane.add(reward, NORMAL_ROW, (x, y, w) -> {
            int namespaceWidth = (w - GAP) / 3;
            attributePane.labelled(reward, "mod_id", words("Mod ID", "模组 ID"), "", x, y, namespaceWidth, NumberKind.TEXT);
            attributePane.labelled(reward, "attribute_id", words("Attribute ID", "属性 ID"), "",
                    x + namespaceWidth + GAP, y, w - namespaceWidth - GAP, NumberKind.TEXT);
        });
        attributePane.add(reward, NORMAL_ROW, (x, y, w) -> {
            Component label = words("Operation", "运算方式");
            int labelWidth = font.width(label) + 12;
            Component current = operationLabel(string(reward, "operation", "addition"));
            attributePane.line(label, x + 1, y + 9, labelWidth, MUTED);
            attributePane.action(current, label, x + labelWidth, y + 4, w - labelWidth, 19, () -> {
                List<ConfigEditorPickerScreen.Choice> operations = OPERATIONS.stream()
                        .map(key -> new ConfigEditorPickerScreen.Choice(key, operationLabel(key))).toList();
                choose(label, operations, key -> reward.addProperty("operation", key));
            });
        });
        attributePane.add(reward, NORMAL_ROW, (x, y, w) -> {
            int leftWidth = (w - GAP) / 2;
            attributePane.labelled(reward, "value", words("Base value", "基础值"), "0", x, y, leftWidth, NumberKind.DECIMAL);
            attributePane.labelled(reward, "min", words("Minimum value", "最小值"), "0",
                    x + leftWidth + GAP, y, w - leftWidth - GAP, NumberKind.DECIMAL);
        });
        attributePane.add(reward, NORMAL_ROW, (x, y, w) -> {
            int leftWidth = (w - GAP) / 2;
            attributePane.labelled(reward, "lv_promote", words("Levels per tier", "分段等级"), "0",
                    x, y, leftWidth, NumberKind.INTEGER);
            attributePane.labelled(reward, "lv_promote_value", words("Change per tier", "每段变化量"), "0",
                    x + leftWidth + GAP, y, w - leftWidth - GAP, NumberKind.DECIMAL);
        });
        attributePane.add(reward, DETAIL_ROW, (x, y, w) -> {
            Component label = words("Remove reward", "删除奖励");
            attributePane.action(label, label, x, y + 4, Math.min(w, font.width(label) + 16), 19, () ->
                    confirm(words("Remove this reward?", "删除此奖励？"), Component.literal(rewardId(reward)), () -> {
                        remove(edit.rewards(), reward);
                        closeRewardEdit();
                    }));
        });
    }

    private static String rewardId(JsonObject reward) {
        String id = string(reward, "attribute_id", "");
        String namespace = string(reward, "mod_id", "");
        return id.contains(":") ? id : (namespace.isBlank() ? "minecraft" : namespace) + ":" + id;
    }

    private static Component rewardName(String id) {
        return OccupationText.rewardName(ResourceLocation.tryParse(id), words("Unknown attribute", "未知属性"));
    }

    private Component operationLabel(String operation) {
        return switch (operation) {
            case "addition" -> words("Add", "加法");
            case "multiply_base" -> words("Multiply base", "乘基础");
            case "multiply_total" -> words("Multiply total", "乘总值");
            default -> Component.literal(operation);
        };
    }

    private void pickReward(JsonObject attribute, JsonObject reward) {
        JsonArray rewards = array(attribute, "pointsRewards");
        Set<String> existing = new LinkedHashSet<>();
        if (rewards != null) {
            for (JsonElement entry : rewards) {
                if (entry instanceof JsonObject row && row != reward) existing.add(rewardId(row));
            }
        }
        List<ConfigEditorPickerScreen.Choice> choices = ForgeRegistries.ATTRIBUTES.getKeys().stream()
                .map(ResourceLocation::toString).filter(id -> !existing.contains(id)).sorted()
                .map(ConfigEditorPickerScreen::namedChoice).toList();
        choose(words("Select reward attribute", "选择奖励属性"), choices, value -> {
            ResourceLocation id = ResourceLocation.tryParse(value);
            if (id == null) return;
            if (reward == null) {
                JsonArray target = ensureArray(attribute, "pointsRewards");
                JsonObject added = ConfigEditorPages.newOccupationReward(id);
                target.add(added);
                openRewardEdit(attribute, target, added);
            } else {
                reward.addProperty("mod_id", id.getNamespace());
                reward.addProperty("attribute_id", id.getPath());
            }
        });
    }

    private void setNumber(JsonObject owner, String key, String input, NumberKind type) {
        try {
            if (type == NumberKind.INTEGER) {
                owner.addProperty(key, Integer.parseInt(input));
            } else {
                BigDecimal number = new BigDecimal(input);
                if (!Double.isFinite(number.doubleValue())) throw new NumberFormatException();
                owner.addProperty(key, number);
            }
            clearNumberError(owner, key);
        } catch (NumberFormatException exception) {
            owner.addProperty(key, input);
            invalidNumbers.computeIfAbsent(owner, unused -> new LinkedHashSet<>()).add(key);
        }
    }

    private void clearNumberError(JsonObject owner, String key) {
        Set<String> invalid = invalidNumbers.get(owner);
        if (invalid == null) return;
        invalid.remove(key);
        if (invalid.isEmpty()) invalidNumbers.remove(owner);
    }

    private void remove(JsonArray array, JsonObject object) {
        for (int index = 0; index < array.size(); index++) {
            if (array.get(index) == object) {
                array.remove(index);
                invalidNumbers.remove(object);
                return;
            }
        }
    }

    private static void text(JsonObject owner, String key, String value, boolean optional) {
        if (optional && value.isBlank()) owner.remove(key);
        else owner.addProperty(key, value);
    }

    private static JsonObject object(JsonObject parent, String key) {
        if (parent == null || key == null) return null;
        return parent.get(key) instanceof JsonObject object ? object : null;
    }

    private static JsonArray array(JsonObject parent, String key) {
        return parent != null && parent.get(key) instanceof JsonArray array ? array : null;
    }

    private static JsonArray ensureArray(JsonObject parent, String key) {
        JsonArray array = array(parent, key);
        if (array != null) return array;
        array = new JsonArray();
        parent.add(key, array);
        return array;
    }

    private static String string(JsonObject owner, String key, String fallback) {
        JsonElement value = owner == null ? null : owner.get(key);
        return value != null && value.isJsonPrimitive() ? value.getAsString() : fallback;
    }

    private void request(Operation operation) {
        pending = operation;
        updateActions();
        switch (operation) {
            case LOAD -> ConfigEditorPackets.request(SECTION);
            case SAVE -> ConfigEditorPackets.save(SECTION, draft.revision, draft.document.deepCopy());
            case RESTORE -> ConfigEditorPackets.restore(SECTION, draft.revision);
            default -> throw new IllegalArgumentException("No operation");
        }
    }

    private void discard() {
        confirm(words("Discard this draft?", "丢弃此草稿？"),
                words("The most recently read server version will be loaded.", "将载入最近一次读取的服务器版本。"), () -> {
                    draft.reset();
                    invalidNumbers.clear();
                    expanded.clear();
                    resetRewardEdit();
                    error = false;
                    message = Component.empty();
                    refresh();
                });
    }

    private void restore() {
        confirm(words("Restore the previous version?", "恢复上一版配置？"),
                words("The server will restore the previous version. Any local draft will be discarded.",
                        "服务器将恢复上一版配置，本地草稿会被丢弃。"), () -> request(Operation.RESTORE));
    }

    private void save() {
        confirm(words("Save occupation changes?", "保存职业修改？"),
                words("The server will validate and apply the occupation changes.",
                        "服务器将校验并应用职业修改。"), () -> request(Operation.SAVE));
    }

    private void confirm(Component title, Component detail, Runnable action) {
        minecraft.setScreen(new ConfirmScreen(confirmed -> {
            minecraft.setScreen(this);
            if (confirmed) action.run();
        }, title, detail));
    }

    private void updateActions() {
        boolean ready = pending == Operation.NONE;
        boolean loaded = draft.document != null;
        dirty = draft.dirty();
        conflicted = draft.conflicted();
        selectJob.active = ready && selectedId != null;
        createJob.active = ready && loaded;
        attributeAction.active = ready && selectedId != null;
        reload.active = ready;
        discard.active = ready && dirty;
        restore.active = ready && loaded && !conflicted;
        save.active = ready && dirty && invalidNumbers.isEmpty() && !conflicted;
        close.active = !hasPendingWrite();
        jobPane.setReady(ready);
        attributePane.setReady(ready);
    }

    private Component status() {
        if (pending != Operation.NONE) return words("Waiting for the server…", "等待服务器响应…");
        if (conflicted) return words("Server version changed. Discard the draft to load it.",
                "服务器版本已变更，丢弃草稿即可载入。");
        if (!invalidNumbers.isEmpty()) return words("Check the highlighted numeric fields.", "请检查标红的数字字段。");
        return message;
    }

    @Override
    public void tick() {
        jobPane.inputs.keySet().forEach(EditBox::tick);
        attributePane.inputs.keySet().forEach(EditBox::tick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta != 0) {
            Pane pane = jobPane.contains(mouseX, mouseY) ? jobPane
                    : attributePane.contains(mouseX, mouseY) ? attributePane : null;
            if (pane != null) {
                pane.scroll(delta > 0 ? -1 : 1);
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            Pane pane = jobPane.onScrollbar(mouseX, mouseY) ? jobPane
                    : attributePane.onScrollbar(mouseX, mouseY) ? attributePane : null;
            if (pane != null) {
                draggingPane = pane;
                pane.startDrag(mouseY);
                return true;
            }
        }
        boolean consumed = super.mouseClicked(mouseX, mouseY, button);
        if (getFocused() != null && (!consumed || !children().contains(getFocused()))) setFocused(null);
        return consumed;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && draggingPane != null) {
            draggingPane.drag(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && draggingPane != null) {
            draggingPane = null;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        if (hasPendingWrite()) return;
        if (rewardEdit != null) closeRewardEdit();
        else requestCloseEditor();
    }

    private void requestCloseEditor() {
        editor.requestClose(this);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        int headerWidth = ConfigEditorScreen.headerWidth(width);
        int headerLeft = (width - headerWidth) / 2;
        int headerRight = headerLeft + headerWidth;
        ConfigEditorScreen.panel(graphics, headerLeft, headerRight, 8, ConfigEditorScreen.HEADER_BOTTOM);
        ConfigEditorScreen.panel(graphics, left, divider - 2, TOP, panelBottom);
        ConfigEditorScreen.panel(graphics, divider + 3, right, TOP, panelBottom);
        ConfigEditorScreen.panel(graphics, left, right, panelBottom + 4, height - 5);
        graphics.drawString(font, title, headerLeft + 10, 17, GOLD, false);
        if (dirty) {
            Component draftLabel = words("Unsaved", "未保存");
            graphics.drawString(font, draftLabel, headerRight - 10 - font.width(draftLabel), 17, WARNING, false);
        }
        Component attributes = rewardEdit == null ? words("Types and rewards", "属性类型与奖励")
                : words("Edit reward", "编辑属性奖励");
        graphics.drawString(font, fit(attributes, attributeAction.getX() - divider - 12), divider + 9, TOP + 10, GOLD, false);
        jobPane.render(graphics);
        attributePane.render(graphics);
        Component status = status();
        int color = error || conflicted || !invalidNumbers.isEmpty() ? WARNING : MUTED;
        graphics.drawString(font, fit(status, right - left - 14), left + 7, panelBottom + 9, color, false);
        super.render(graphics, mouseX, mouseY, partialTick);
        if (font.width(status) > right - left - 14 && mouseX >= left && mouseX < right
                && mouseY >= panelBottom + 7 && mouseY < panelBottom + 20) {
            var wrapped = font.split(status, Math.min(460, width - 35));
            graphics.renderTooltip(font, wrapped.subList(0, Math.min(wrapped.size(), Math.max(1, (height - 28) / 10))),
                    mouseX, mouseY);
        }
    }

    private Button button(Component title, Component tooltip, int x, int y, int w, int h, Runnable action) {
        return addRenderableWidget(Button.builder(fit(title, w - 6), unused -> action.run())
                .bounds(x, y, w, h).tooltip(Tooltip.create(tooltip)).build());
    }

    private Component fit(Component text, int limit) {
        if (font.width(text) <= limit) return text;
        if (limit < font.width("…")) return Component.empty();
        return Component.literal(font.plainSubstrByWidth(text.getString(), limit - font.width("…")) + "…");
    }

    private static Component withValue(Component label, String value) {
        return value.isBlank() ? label : Component.literal(label.getString() + "\n" + value);
    }

    private enum NumberKind { TEXT, INTEGER, DECIMAL }

    private enum Operation { NONE, LOAD, SAVE, RESTORE }

    @FunctionalInterface
    private interface RowContent {
        void place(int x, int y, int width);
    }

    private record Row(JsonObject owner, int color, RowContent content) {}

    private record Caption(Component text, int x, int y, int width, int color) {}

    private record RewardEdit(JsonObject type, JsonArray rewards, JsonObject reward, int listOffset) {}

    private record Binding(JsonObject owner, String key) {}

    private final class Pane {
        final List<Row> rows = new ArrayList<>();
        final List<AbstractWidget> widgets = new ArrayList<>();
        final List<Caption> captions = new ArrayList<>();
        final Map<EditBox, Binding> inputs = new IdentityHashMap<>();
        int x;
        int right;
        int top;
        int bottom;
        int offset;
        double dragGrab;
        EditBox retainedInput;
        Binding retainedBinding;

        void layout(int x, int right, int top, int bottom) {
            this.x = x;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
        }

        int capacity() {
            return Math.max(0, (bottom - top) / ROW);
        }

        int maxOffset() {
            return Math.max(0, rows.size() - capacity());
        }

        boolean contains(double x, double y) {
            return x >= this.x && x < right && y >= top && y < bottom;
        }

        boolean onScrollbar(double x, double y) {
            return capacity() > 0 && maxOffset() > 0 && contains(x, y) && x >= right - 8;
        }

        int thumbHeight() {
            return Math.max(12, (bottom - top) * capacity() / Math.max(1, rows.size()));
        }

        int thumbTop() {
            return top + (int) Math.round((bottom - top - thumbHeight()) * (double) offset / Math.max(1, maxOffset()));
        }

        void startDrag(double y) {
            int thumbTop = thumbTop();
            int thumbHeight = thumbHeight();
            dragGrab = y >= thumbTop && y < thumbTop + thumbHeight ? y - thumbTop : thumbHeight / 2.0;
            drag(y);
        }

        void drag(double y) {
            int travel = Math.max(1, bottom - top - thumbHeight());
            setOffset((int) Math.round((y - top - dragGrab) * maxOffset() / travel));
        }

        void scroll(int delta) {
            setOffset(offset + delta);
        }

        void setOffset(int target) {
            int clamped = Math.max(0, Math.min(target, maxOffset()));
            if (offset == clamped) return;
            offset = clamped;
            showRows();
            setReady(pending == Operation.NONE);
        }

        void reveal(JsonObject owner) {
            for (int index = 0; index < rows.size(); index++) {
                if (rows.get(index).owner == owner) {
                    if (index < offset) setOffset(index);
                    if (index >= offset + capacity()) setOffset(index - capacity() + 1);
                    return;
                }
            }
        }

        void add(JsonObject owner, int color, RowContent content) {
            rows.add(new Row(owner, color, content));
        }

        void showRows() {
            var focused = getFocused();
            retainedInput = focused instanceof EditBox edit && inputs.containsKey(edit) ? edit : null;
            retainedBinding = retainedInput == null ? null : inputs.get(retainedInput);
            if (widgets.contains(focused)) setFocused(null);
            widgets.forEach(OccupationEditorScreen.this::removeWidget);
            widgets.clear();
            captions.clear();
            inputs.clear();
            offset = Math.max(0, Math.min(offset, maxOffset()));
            for (int index = offset; index < Math.min(rows.size(), offset + capacity()); index++) {
                rows.get(index).content.place(x + 4, top + (index - offset) * ROW, right - x - 15);
            }
            if (retainedInput != null && inputs.containsKey(retainedInput)) setFocused(retainedInput);
            retainedInput = null;
            retainedBinding = null;
        }

        void render(GuiGraphics graphics) {
            if (bottom <= top) return;
            graphics.enableScissor(x, top, right, bottom);
            for (int index = offset; index < Math.min(rows.size(), offset + capacity()); index++) {
                int y = top + (index - offset) * ROW;
                graphics.fill(x, y, right - 8, y + ROW - 1, rows.get(index).color);
            }
            for (Caption caption : captions) {
                graphics.drawString(font, fit(caption.text, caption.width), caption.x, caption.y, caption.color, false);
            }
            if (capacity() > 0 && maxOffset() > 0) {
                int thumb = thumbTop();
                graphics.fill(right - 7, top, right - 1, bottom, 0xFF101114);
                graphics.fill(right - 7, thumb, right - 1, thumb + thumbHeight(), 0xFF9E8155);
                graphics.fill(right - 6, thumb + 1, right - 3, thumb + thumbHeight() - 1, 0xFFC4A46E);
            }
            graphics.disableScissor();
        }

        void setReady(boolean ready) {
            for (AbstractWidget widget : widgets) widget.active = ready;
            for (Map.Entry<EditBox, Binding> entry : inputs.entrySet()) {
                EditBox edit = entry.getKey();
                Binding binding = entry.getValue();
                edit.setEditable(ready);
                edit.setTextColor(invalidNumbers.getOrDefault(binding.owner, Set.of()).contains(binding.key)
                        ? 0xFF7777 : EditBox.DEFAULT_TEXT_COLOR);
            }
        }

        void line(Component text, int x, int y, int width, int color) {
            captions.add(new Caption(text, x, y, width, color));
        }

        void action(Component title, Component tooltip, int x, int y, int w, int h, Runnable action) {
            widgets.add(button(title, tooltip, x, y, w, h, action));
        }

        EditBox labelled(JsonObject owner, String key, Component label, String fallback,
                         int x, int y, int w, NumberKind kind) {
            line(label, x + 1, y + 1, w - 1, MUTED);
            return input(owner, key, label, fallback, x, y + 10, w, 16, kind);
        }

        EditBox input(JsonObject owner, String key, Component label, String fallback,
                      int x, int y, int w, int h, NumberKind kind) {
            EditBox edit;
            if (retainedBinding != null && retainedBinding.owner == owner && retainedBinding.key.equals(key)) {
                edit = retainedInput;
                edit.setX(x);
                edit.setY(y);
                edit.setWidth(w);
                edit.setHeight(h);
            } else {
                String value = string(owner, key, fallback);
                edit = new EditBox(font, x, y, w, h, label);
                edit.setTooltip(Tooltip.create(withValue(label, value)));
                edit.setMaxLength(Math.max(kind == NumberKind.TEXT ? 256 : 32, value.length()));
                edit.setValue(value);
                edit.setResponder(input -> {
                    edit.setTooltip(Tooltip.create(withValue(label, input)));
                    if (kind == NumberKind.TEXT) text(owner, key, input, "name".equals(key) && owner != selectedOccupation());
                    else setNumber(owner, key, input, kind);
                    if (owner == selectedOccupation() && "name".equals(key)) updateOccupationLabel();
                    updateActions();
                });
            }
            inputs.put(edit, new Binding(owner, key));
            widgets.add(addRenderableWidget(edit));
            return edit;
        }
    }
}
