package dev.lhkongyu.lhmiracleroad.client.screen.weaponPodium;

import dev.lhkongyu.lhmiracleroad.items.gem.AttributeGem;
import dev.lhkongyu.lhmiracleroad.items.gem.StrengthenGem;
import dev.lhkongyu.lhmiracleroad.registry.BlockRegistry;
import dev.lhkongyu.lhmiracleroad.registry.ItemsRegistry;
import dev.lhkongyu.lhmiracleroad.registry.MenuRegistry;
import dev.lhkongyu.lhmiracleroad.registry.TagsRegistry;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class WeaponPodiumMenu extends ItemCombinerMenu {
    public WeaponPodiumMenu(int pContainerId, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(MenuRegistry.WEAPON_PODIUM_MENU.get(), pContainerId, inventory, containerLevelAccess);
    }

    private final List<ItemStack> additionalDrops = new ArrayList<>();

    public WeaponPodiumMenu(int pContainerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(pContainerId, inventory, ContainerLevelAccess.NULL);
    }

    @Override
    protected boolean mayPickup(Player pPlayer, boolean pHasStack) {
        return true;
    }

    @Override
    protected void onTake(Player player, ItemStack itemStack) {
        ItemStack sword =inputSlots.getItem(0);
        sword.shrink(1);

        ItemStack gem = inputSlots.getItem(1);

        if (gem.is(TagsRegistry.STRENGTHEN_GEM)) {
            CompoundTag tag = sword.getOrCreateTag().getCompound("lh_gem");
            int strengthenLV = tag.getInt("strengthen_lv");

            Integer needed = StrengthenGem.getStrengthenGemCount(gem, strengthenLV);
            if (needed !=null) gem.shrink(needed);
            else gem.shrink(1);
        }

        ItemStack hammer = inputSlots.getItem(2);
        hammer.hurtAndBreak(200, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));

        this.access.execute((level, pos) -> {
            level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, .8f, 1.1f);
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS, 1f, 1f);
            additionalDrops.forEach(stack -> {
                if (!stack.isEmpty()) {
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5, stack));
                }
            });
            additionalDrops.clear();

        });
        createResult();
    }

    @Override
    protected boolean isValidBlock(BlockState pState) {
        return pState.is(BlockRegistry.WEAPON_PODIUM.get());
    }

    @Override
    public void createResult() {
        ItemStack result = ItemStack.EMPTY;
        this.additionalDrops.clear();
        ItemStack baseItemStack = inputSlots.getItem(0);
        ItemStack gemItemStack = inputSlots.getItem(1);
        ItemStack hammerItemStack = inputSlots.getItem(2);

        if (hammerItemStack.is(ItemsRegistry.HAMMER_IRON.get())){
            if (gemItemStack.is(TagsRegistry.STRENGTHEN_GEM)){
                result = StrengthenGem.strengthen(baseItemStack,gemItemStack);
            }
        }else if (hammerItemStack.is(ItemsRegistry.HAMMER_NETHERITE.get())){
            if (gemItemStack.is(TagsRegistry.STRENGTHEN_GEM)){
                result = StrengthenGem.strengthen(baseItemStack,gemItemStack);
            }else if (gemItemStack.is(TagsRegistry.GEM)) {
                result = AttributeGem.attributeStrengthen(baseItemStack,gemItemStack);
            }
        }else if (hammerItemStack.is(ItemsRegistry.HAMMER_ANCIENT.get())){
            if (gemItemStack.is(TagsRegistry.STRENGTHEN_GEM)){
                result = StrengthenGem.strengthen(baseItemStack,gemItemStack);
            }else if (gemItemStack.is(TagsRegistry.GEM)) {
                result = AttributeGem.attributeStrengthen(baseItemStack,gemItemStack);
            }
        }

        resultSlots.setItem(0, result);
    }


    @Override
    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
                .withSlot(0, 26, 47, LHMiracleRoadTool::itemIsWeaponsAndEquipmentAll)
                .withSlot(1, 75, 47,  stack -> stack.is(TagsRegistry.GEM) || stack.is(TagsRegistry.STRENGTHEN_GEM))
                .withSlot(2, 51, 20, stack -> stack.is(TagsRegistry.HAMMERS))
                .withResultSlot(3, 133, 47).build();
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot pSlot) {
        return pSlot.container != this.resultSlots && super.canTakeItemForPickAll(stack, pSlot);
    }
}