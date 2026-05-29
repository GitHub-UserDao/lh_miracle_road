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
        this.titleLabelX = 108;
        this.titleLabelY = 24;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pX, int pY) {
        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;

        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);

        if (((this.menu.getSlot(0).hasItem() && this.menu.getSlot(1).hasItem()) && !this.menu.getSlot(2).hasItem())) {
            graphics.blit(TEXTURE, leftPos + 99, topPos + 45, this.imageWidth, 0, 28, 21);
        }

        // 武器槽空时
        if (menu.getSlot(0).getItem().isEmpty()) {
            graphics.blit(TEXTURE, leftPos + 26, topPos + 47,
                    176, 21, 16, 16
            );
        }

        // 宝石槽
        if (menu.getSlot(1).getItem().isEmpty()) {
            graphics.blit(TEXTURE, leftPos + 75, topPos + 47,
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
    protected void renderTooltip(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {

        super.renderTooltip(graphics, mouseX, mouseY);

        int x = this.leftPos;
        int y = this.topPos;

        //武器槽
        if (isHovering(26, 47, 16, 16, mouseX, mouseY) && this.menu.getSlot(0).getItem().isEmpty()) {
            graphics.renderTooltip(this.font, Component.literal("放入武器、装备、盾牌或工具"), mouseX, mouseY);
        }

        //宝石槽
        if (isHovering(75, 47, 16, 16, mouseX, mouseY) && this.menu.getSlot(1).getItem().isEmpty()) {
            graphics.renderTooltip(this.font, Component.literal("放入强化宝石或属性宝石"), mouseX, mouseY);
        }

        //锤子槽
        if (isHovering(51, 18, 16, 16, mouseX, mouseY) && this.menu.getSlot(2).getItem().isEmpty()) {
            graphics.renderTooltip(this.font, Component.literal("放入重锤"), mouseX, mouseY);
        }
    }

    @Override
    protected void renderErrorIcon(GuiGraphics p_281990_, int p_266822_, int p_267045_) {
    }

}

