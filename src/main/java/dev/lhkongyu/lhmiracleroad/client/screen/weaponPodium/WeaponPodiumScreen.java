package dev.lhkongyu.lhmiracleroad.client.screen.weaponPodium;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class WeaponPodiumScreen extends ItemCombinerScreen<WeaponPodiumMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(
                    LHMiracleRoad.MODID,
                    "textures/gui/weapon_podium.png"
            );

    public WeaponPodiumScreen(WeaponPodiumMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, TEXTURE);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pX, int pY) {
        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;

        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);

        if (menu.getError() != WeaponPodiumError.NONE) {
            graphics.blit(TEXTURE, leftPos + 99, topPos + 38, this.imageWidth, 0, 28, 21);
        }

        // 武器槽空时
        if (menu.getSlot(0).getItem().isEmpty()) {
            graphics.blit(TEXTURE, leftPos + 26, topPos + 40,
                    176, 21, 16, 16
            );
        }

        // 宝石槽
        if (menu.getSlot(1).getItem().isEmpty()) {
            graphics.blit(TEXTURE, leftPos + 75, topPos + 40,
                    192, 21, 16, 16
            );
        }

        // 锤子槽
        if (menu.getSlot(2).getItem().isEmpty()) {
            graphics.blit(TEXTURE, leftPos + 51, topPos + 20,
                    208, 21, 16, 16
            );
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {

        super.renderTooltip(graphics, mouseX, mouseY);

        //武器槽
        if (isHovering(26, 40, 16, 16, mouseX, mouseY) && this.menu.getSlot(0).getItem().isEmpty()) {
            graphics.renderTooltip(this.font, Component.translatable("tooltip.hint.lhmiracleroad.weapon_podium.weapon"), mouseX, mouseY);
        }

        //宝石槽
        if (isHovering(75, 40, 16, 16, mouseX, mouseY) && this.menu.getSlot(1).getItem().isEmpty()) {
            graphics.renderTooltip(this.font, Component.translatable("tooltip.hint.lhmiracleroad.weapon_podium.gem"), mouseX, mouseY);
        }

        //锤子槽
        if (isHovering(51, 18, 16, 16, mouseX, mouseY) && this.menu.getSlot(2).getItem().isEmpty()) {
            graphics.renderTooltip(this.font, Component.translatable("tooltip.hint.lhmiracleroad.weapon_podium.hammer"), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
//        super.renderLabels(guiGraphics, mouseX, mouseY);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);

        guiGraphics.drawString(this.font,menu.getSoutCount()+"",112,24,4210752, false);

        Component msg = null;  // 使用 Component 类型
        switch (menu.getError()) {
            case HAMMER_LEVEL_LOW -> msg = Component.translatable("tooltip.lhmiracleroad.weapon_podium.hammer_level_low");
            case MAX_LEVEL -> msg = Component.translatable("tooltip.lhmiracleroad.weapon_podium.max_level");
            case STRENGTHEN_LV_DEFICIENCY -> msg = Component.translatable("tooltip.lhmiracleroad.weapon_podium.strengthen_lv_deficiency");
            case GEM_NOT_ENOUGH -> msg = Component.translatable("tooltip.lhmiracleroad.weapon_podium.gem_not_enough");
            case NOT_STRENGTHEN -> msg = Component.translatable("tooltip.lhmiracleroad.weapon_podium.not_strengthen");
            case NOT_METAMORPHOSIS -> msg = Component.translatable("tooltip.lhmiracleroad.weapon_podium.not_metamorphosis");
            case REPEAT_METAMORPHOSIS -> msg = Component.translatable("tooltip.lhmiracleroad.weapon_podium.repeat_metamorphosis");
            case SOUL_NOT_SUFFICIENT -> msg = Component.translatable("tooltip.lhmiracleroad.weapon_podium.soul_not_sufficient");
            case NONE -> {
                return;
            }
        }

        guiGraphics.drawString(this.font, msg, 30, 60, 0xff5555, false);
    }

    @Override
    protected void renderErrorIcon(GuiGraphics p_281990_, int p_266822_, int p_267045_) {
    }

}

