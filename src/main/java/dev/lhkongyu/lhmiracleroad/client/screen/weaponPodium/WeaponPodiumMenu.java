package dev.lhkongyu.lhmiracleroad.client.screen.weaponPodium;

import com.mojang.datafixers.kinds.IdF;
import dev.lhkongyu.lhmiracleroad.items.gem.AttributeGem;
import dev.lhkongyu.lhmiracleroad.items.gem.StrengthenGem;
import dev.lhkongyu.lhmiracleroad.registry.BlockRegistry;
import dev.lhkongyu.lhmiracleroad.registry.ItemsRegistry;
import dev.lhkongyu.lhmiracleroad.registry.MenuRegistry;
import dev.lhkongyu.lhmiracleroad.registry.TagsRegistry;
import dev.lhkongyu.lhmiracleroad.tool.GemTool;
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

    private WeaponPodiumError error = WeaponPodiumError.NONE;

    private int soutCount = 0;

    public WeaponPodiumError getError() {
        return error;
    }

    public int getSoutCount() {
        return soutCount;
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

        if (soutCount > 0){
            GemTool.deductSoul(player,soutCount);
        }

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
        error = WeaponPodiumError.NONE;
        soutCount = 0;
        if (baseItemStack.isEmpty() || gemItemStack.isEmpty() || hammerItemStack.isEmpty()){
            resultSlots.setItem(0, ItemStack.EMPTY);
            return;
        }

        //判断重锤等级是否符合要求
        if (gemItemStack.is(TagsRegistry.GEM) && hammerItemStack.is(ItemsRegistry.HAMMER_IRON.get())) {
            error = WeaponPodiumError.HAMMER_LEVEL_LOW;
            resultSlots.setItem(0, ItemStack.EMPTY);
            return;
        }

        //根据宝石进行强化
        if (gemItemStack.is(TagsRegistry.STRENGTHEN_GEM)){
            CompoundTag tag = baseItemStack.getOrCreateTag().getCompound("lh_gem");
            int strengthenLV = tag.getInt("strengthen_lv");

            //判断强化等级是否超过最大等级
            if (strengthenLV > 9) {
                error = WeaponPodiumError.MAX_LEVEL;
                resultSlots.setItem(0, ItemStack.EMPTY);
                return;
            }

            //判断强化宝石数量是否达标,并且判断是否需要更高级的强化宝石
            Integer needed = StrengthenGem.getStrengthenGemCount(gemItemStack, strengthenLV);
            int have = gemItemStack.getCount();
            if (needed == null){
                error = WeaponPodiumError.STRENGTHEN_LV_DEFICIENCY;
                resultSlots.setItem(0, ItemStack.EMPTY);
                return;
            }else if (have < needed) {
                error = WeaponPodiumError.GEM_NOT_ENOUGH;
                resultSlots.setItem(0, ItemStack.EMPTY);
                return;
            }

            //判断是否能进行强化
            if (!(LHMiracleRoadTool.itemIsWeaponsAndEquipmentAll(baseItemStack))) {
                error = WeaponPodiumError.NOT_STRENGTHEN;
                resultSlots.setItem(0, ItemStack.EMPTY);
                return;
            }

            soutCount = GemTool.getStrengthenGemSoulCount(strengthenLV);
            if (GemTool.isSoulSufficient(player,soutCount)){
                error = WeaponPodiumError.SOUL_NOT_SUFFICIENT;
                resultSlots.setItem(0, ItemStack.EMPTY);
                return;
            }

            result = StrengthenGem.strengthen(baseItemStack,gemItemStack);
        }else if (gemItemStack.is(TagsRegistry.GEM)) {
            //判断是否属于武器
            if (!LHMiracleRoadTool.itemIsWeaponsAll(baseItemStack)) {
                error = WeaponPodiumError.NOT_METAMORPHOSIS;
                resultSlots.setItem(0, ItemStack.EMPTY);
                return;
            }

            //判断是否已质变如果已质变并且不为砥石（消除质变） 时将无法进行质变。
            CompoundTag tag = baseItemStack.getOrCreateTag().getCompound("lh_gem");
            if (tag.contains("type") && !gemItemStack.is(ItemsRegistry.WHETSTONE.get())){
                error = WeaponPodiumError.REPEAT_METAMORPHOSIS;
                resultSlots.setItem(0, ItemStack.EMPTY);
                return;
            }

            soutCount = GemTool.getAttributeGemSoulCount(gemItemStack);
            if (GemTool.isSoulSufficient(player,soutCount)){
                error = WeaponPodiumError.SOUL_NOT_SUFFICIENT;
                resultSlots.setItem(0, ItemStack.EMPTY);
                return;
            }

            result = AttributeGem.attributeStrengthen(baseItemStack,gemItemStack);
        }

        resultSlots.setItem(0, result);
    }


    @Override
    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
                .withSlot(0, 26, 40, LHMiracleRoadTool::itemIsWeaponsAndEquipmentAll)
                .withSlot(1, 75, 40,  stack -> stack.is(TagsRegistry.GEM) || stack.is(TagsRegistry.STRENGTHEN_GEM))
                .withSlot(2, 51, 20, stack -> stack.is(TagsRegistry.HAMMERS))
                .withResultSlot(3, 133, 40).build();
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot pSlot) {
        return pSlot.container != this.resultSlots && super.canTakeItemForPickAll(stack, pSlot);
    }
}