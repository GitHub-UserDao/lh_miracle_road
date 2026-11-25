package dev.lhkongyu.lhmiracleroad.client.screen.occupation;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.config.initItem.StarterItemPreview;
import dev.lhkongyu.lhmiracleroad.config.occupation.Occupation;
import dev.lhkongyu.lhmiracleroad.config.occupation.OccupationAttribute;
import dev.lhkongyu.lhmiracleroad.data.ClientData;
import dev.lhkongyu.lhmiracleroad.packet.PlayerChannel;
import dev.lhkongyu.lhmiracleroad.packet.PlayerOccupationMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class OccupationChooseScreen extends OccupationTextureScreen {

    private static final int DETAILS_TEXT_COLOR = 0xC56F49;

    private static final ResourceLocation BACKGROUND =
            new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/new/occupation_background.png");

    private String selectedOccupationId;
    private LocalPlayer modelOwner;
    private RemotePlayer mannequin;
    private StarterItemPreview appliedPreview;

    private OccupationImageButton confirmButton;

    public OccupationChooseScreen() {
        super(BACKGROUND, List.of(
                pageLeftButton(14, 110),
                pageRightButton(60, 110)
        ), List.of(
                titleImage(75, 21),
                titleFrameImage(75, 56)
        ), Component.translatable("lhmiracleroad.gui.titles.init_item"),
                Component.translatable("lhmiracleroad.gui.titles.init_attribute"));
    }

    @Override
    protected void init() {
        super.init();
        confirmButton = addImageButton(selectButton(SELECT_X, SELECT_Y, button -> confirmOccupation()));
        confirmButton.active = canConfirmOccupation();
    }

    @Override
    public void tick() {
        confirmButton.active = canConfirmOccupation();
    }

    private boolean canConfirmOccupation() {
        return minecraft != null && minecraft.player != null && displayedOccupation() != null
                && minecraft.player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER)
                .map(attributes -> attributes.getOccupationId() == null || attributes.getOccupationId().isBlank())
                .orElse(false);
    }

    private void confirmOccupation() {
        Occupation occupation = displayedOccupation();
        if (occupation == null || !canConfirmOccupation()) return;
        PlayerChannel.sendToServer(new PlayerOccupationMessage(occupation.getId()));
        onClose();
    }

    @Override
    protected Component occupationDescription() {
        Occupation occupation = displayedOccupation();
        return occupation == null ? Component.empty() : OccupationText.description(occupation.getId());
    }

    private StarterItemPreview currentPreview() {
        Occupation occupation = displayedOccupation();
        return occupation == null ? StarterItemPreview.EMPTY
                : ClientData.STARTER_ITEM_PREVIEWS.getOrDefault(occupation.getId(), StarterItemPreview.EMPTY);
    }

    @Override
    protected Player displayedPlayer() {
        LocalPlayer owner = minecraft.player;
        if (owner == null || minecraft.level == null) return null;
        if (owner != modelOwner) {
            modelOwner = owner;
            mannequin = new RemotePlayer(minecraft.level, owner.getGameProfile()) {
                @Override
                public boolean isModelPartShown(PlayerModelPart part) {
                    return owner.isModelPartShown(part);
                }

                @Override
                public boolean isSpectator() {
                    return false;
                }
            };
            mannequin.setSilent(true);
            appliedPreview = null;
        }
        StarterItemPreview preview = currentPreview();
        if (appliedPreview != preview) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                mannequin.setItemSlot(slot, preview.equipment().getOrDefault(slot, ItemStack.EMPTY).copy());
            }
            appliedPreview = preview;
        }
        mannequin.setMainArm(owner.getMainArm());
        return mannequin;
    }

    @Override
    protected List<ItemStack> additionalItems() {
        return currentPreview().inventory();
    }

    @Override
    protected void renderAdditionalContent(GuiGraphics graphics, int mouseX, int mouseY) {
        Occupation occupation = displayedOccupation();
        if (occupation == null || occupation.getOccupationAttributes() == null) return;

        Component details = Component.translatable("lhmiracleroad.gui.attribute.text.details");
        int detailsWidth = font.width(details);
        int detailsX = screenX(ATTRIBUTE_RIGHT_X) - detailsWidth;
        int rowIndex = 0;
        for (OccupationAttribute attribute : occupation.getOccupationAttributes()) {
            if (attribute == null) continue;

            int y = attributeRowY(rowIndex++);
            Component name = attribute.displayName();
            int level = attribute.getInit_lv();
            int maxLevel = attribute.getMax_lv();
            drawAttributeNameAndLevel(graphics, name, level, maxLevel, y, mouseX, mouseY);
            graphics.drawString(font, details, detailsX, y, DETAILS_TEXT_COLOR, false);

            if (isTextHovered(mouseX, mouseY, detailsX, y, detailsWidth)) {
                setAttributeHover(attribute, level,
                        Component.translatable("lhmiracleroad.gui.attribute.text.max_level", maxLevel));
            }
        }
    }

    @Override
    protected void onPageLeft() {
        if (displayedOccupation() == null) return;
        String previous = ClientData.OCCUPATION_CATALOG.lowerKey(selectedOccupationId);
        selectedOccupationId = previous != null ? previous : ClientData.OCCUPATION_CATALOG.lastKey();
    }

    @Override
    protected void onPageRight() {
        if (displayedOccupation() == null) return;
        String next = ClientData.OCCUPATION_CATALOG.higherKey(selectedOccupationId);
        selectedOccupationId = next != null ? next : ClientData.OCCUPATION_CATALOG.firstKey();
    }

    @Override
    protected Occupation displayedOccupation() {
        if (ClientData.OCCUPATION_CATALOG.isEmpty()) return null;
        if (selectedOccupationId == null || !ClientData.OCCUPATION_CATALOG.containsKey(selectedOccupationId)) {
            selectedOccupationId = ClientData.OCCUPATION_CATALOG.firstKey();
        }
        return ClientData.OCCUPATION_CATALOG.get(selectedOccupationId);
    }
}
