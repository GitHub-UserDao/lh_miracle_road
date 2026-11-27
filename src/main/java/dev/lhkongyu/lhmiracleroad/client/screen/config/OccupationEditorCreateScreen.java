package dev.lhkongyu.lhmiracleroad.client.screen.config;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static dev.lhkongyu.lhmiracleroad.client.screen.config.ConfigEditorPickerScreen.words;

final class OccupationEditorCreateScreen extends Screen {
    private final Screen parent;
    private final Predicate<String> idExists;
    private final BiConsumer<String, JsonObject> accept;
    private EditBox id;
    private EditBox name;
    private Component error = Component.empty();
    private int left;
    private int top;
    private int panelWidth;

    OccupationEditorCreateScreen(Screen parent, Predicate<String> idExists, BiConsumer<String, JsonObject> accept) {
        super(words("New occupation", "新增职业"));
        this.parent = parent;
        this.idExists = idExists;
        this.accept = accept;
    }

    @Override
    protected void init() {
        String previousId = id == null ? "" : id.getValue();
        String previousName = name == null ? "" : name.getValue();
        panelWidth = Math.min(width - 16, 360);
        left = (width - panelWidth) / 2;
        top = (height - 156) / 2;
        id = field(words("Occupation ID", "职业 ID"), previousId, 38, 128);
        name = field(words("Occupation name", "职业名称"), previousName, 73, 256);
        addRenderableWidget(Button.builder(words("Cancel", "取消"), button -> onClose())
                .bounds(left + 9, top + 126, 78, 20).build());
        addRenderableWidget(Button.builder(words("Create", "创建"), button -> create())
                .bounds(left + panelWidth - 87, top + 126, 78, 20).build());
        setInitialFocus(id);
    }

    private EditBox field(Component label, String value, int offset, int limit) {
        EditBox field = addRenderableWidget(new EditBox(font, left + 9, top + offset, panelWidth - 18, 20, label));
        field.setMaxLength(limit);
        field.setValue(value);
        field.setResponder(text -> error = Component.empty());
        return field;
    }

    private void create() {
        String key = id.getValue().strip();
        String label = name.getValue().strip();
        if (idExists.test(key)) {
            error = words("This occupation ID is already in use.", "这个职业 ID 已被使用。");
            return;
        }
        if (label.isEmpty()) {
            error = words("Enter an occupation name.", "请输入职业名称。");
            return;
        }
        JsonObject occupation = ConfigEditorPages.newOccupation(key, label);
        if (occupation == null) {
            error = words("Use letters, digits, _ and -. A dot can follow the first character.",
                    "ID 可用字母、数字、下划线和连字符，首字符后也可用点。");
            return;
        }
        accept.accept(key, occupation);
        onClose();
    }

    @Override
    public void tick() {
        id.tick();
        name.tick();
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
        ConfigEditorScreen.panel(graphics, left, left + panelWidth, top, top + 156);
        graphics.drawString(font, title, left + 9, top + 11, ConfigEditorScreen.GOLD, false);
        graphics.drawString(font, words("Occupation ID", "职业 ID"), left + 9, top + 27, ConfigEditorScreen.MUTED, false);
        graphics.drawString(font, words("Occupation name", "职业名称"), left + 9, top + 62, ConfigEditorScreen.MUTED, false);
        graphics.drawWordWrap(font, error, left + 9, top + 99, panelWidth - 18, 0xFAC476);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
