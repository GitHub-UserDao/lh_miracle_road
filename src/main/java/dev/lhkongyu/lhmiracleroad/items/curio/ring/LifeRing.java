package dev.lhkongyu.lhmiracleroad.items.curio.ring;

import com.google.common.collect.ImmutableMultimap;
import dev.lhkongyu.lhmiracleroad.items.curio.RingItem;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.UUID;

/**
 * 生命戒指 的饰品功能
 */
public class LifeRing {

    public static RingItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.MAX_HEALTH, new AttributeModifier(UUID.fromString("e8e52ef2-a768-b716-ac00-6e77077b1f8a"), "", .25, AttributeModifier.Operation.MULTIPLY_BASE));
        return new RingItem(new Item.Properties().rarity(Rarity.RARE),builder.build());
    }

    public static void hpSynchronous(Player player){
        player.setHealth((float) player.getAttribute(Attributes.MAX_HEALTH).getValue());
    }
}
