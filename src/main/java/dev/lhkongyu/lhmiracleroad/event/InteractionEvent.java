package dev.lhkongyu.lhmiracleroad.event;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.items.gem.StrengthenGem;
import dev.lhkongyu.lhmiracleroad.packet.ClientPromptMessage;
import dev.lhkongyu.lhmiracleroad.packet.PlayerChannel;
import dev.lhkongyu.lhmiracleroad.registry.ItemsRegistry;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = LHMiracleRoad.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InteractionEvent {

    public static final TagKey<Item> WEAPONS =
            ItemTags.create(LHMiracleRoadTool.resourceLocationId("weapons"));

    public static final TagKey<Item> RANGED_WEAPONS =
            ItemTags.create(LHMiracleRoadTool.resourceLocationId("ranged_weapons"));

    public static final TagKey<Item> GEM =
            ItemTags.create(LHMiracleRoadTool.resourceLocationId("gem"));

    public static final TagKey<Item> STRENGTHEN_GEM =
            ItemTags.create(LHMiracleRoadTool.resourceLocationId("strengthen_gem"));

    @SubscribeEvent
    public static void onAnvil(AnvilUpdateEvent e) {
        ItemStack left  = e.getLeft();
        ItemStack right = e.getRight();

        if (right.is(GEM)) {
            if (!LHMiracleRoadTool.itemIsWeaponsAll(left)) return;
            CompoundTag tag = left.getOrCreateTag().getCompound("lh_gem");
            ItemStack out = left.copy();
            //如果存在质变的tag，就可以使用砥石清除质变
            if (tag.contains("type")) {
                if (right.getDescriptionId().equals(ItemsRegistry.WHETSTONE.get().getDescriptionId())) {
                    CompoundTag compoundTag = tag.copy();
                    compoundTag.remove("type");
                    out.getOrCreateTag().put("lh_gem",compoundTag);
                    e.setOutput(out);
                    e.setCost(10);
                    e.setMaterialCost(1);
                }
                return;
            }

            String type = LHMiracleRoadTool.getGemType(right);
            if (type == null) return;
            if (tag.isEmpty()) tag = new CompoundTag();

            CompoundTag compoundTag = tag.copy();
            compoundTag.putString("type", type);
            out.getOrCreateTag().put("lh_gem", compoundTag);
            e.setOutput(out);
            e.setCost(30);
            e.setMaterialCost(1);
        }else if (right.is(STRENGTHEN_GEM)){
            if (LHMiracleRoadTool.itemIsWeaponsAll(left) || left.getItem() instanceof ArmorItem || left.getItem() instanceof ElytraItem) {

                CompoundTag tag = left.getOrCreateTag().getCompound("lh_gem");
                int strengthenLV = tag.getInt("strengthen_lv");
                if (strengthenLV > 9) return;
                int have = right.getCount();
                Integer needed = StrengthenGem.getStrengthenGemCount(right, strengthenLV);
                if (needed == null || have < needed) {
                    if (e.getPlayer() instanceof ServerPlayer player) {
                        ClientPromptMessage clientPromptMessage = new ClientPromptMessage(StrengthenGem.getStrengthenGemTooltip(strengthenLV));
                        PlayerChannel.sendToClient(clientPromptMessage, player);
                    }
                    return;
                }

                ItemStack out = left.copy();
                CompoundTag compoundTag = tag.copy();

                int vanillaMaxDurability = left.getMaxDamage();
                int customMaxDurability = compoundTag.contains("max_durability")
                        ? compoundTag.getInt("max_durability")
                        : vanillaMaxDurability;

                int maxDurability = (int) (customMaxDurability * StrengthenGem.getStrengthenGemDurabilityRatio(strengthenLV + 1));

                compoundTag.putDouble("max_durability", maxDurability);
                compoundTag.putInt("strengthen_lv", strengthenLV + 1);

                out.getOrCreateTag().put("lh_gem", compoundTag);

                e.setOutput(out);
                e.setCost(30);
                e.setMaterialCost(needed);
            }

        }
    }
}
