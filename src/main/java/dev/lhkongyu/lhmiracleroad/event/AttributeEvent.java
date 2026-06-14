package dev.lhkongyu.lhmiracleroad.event;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LHMiracleRoad.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AttributeEvent {

    @SubscribeEvent
    public static void modifyAttributes(EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> type : event.getTypes()) {
            //属性伤害
            event.add(type, LHMiracleRoadAttributes.MAGIC_ATTRIBUTE_DAMAGE);
            event.add(type, LHMiracleRoadAttributes.FLAME_ATTRIBUTE_DAMAGE);
            event.add(type, LHMiracleRoadAttributes.LIGHTNING_ATTRIBUTE_DAMAGE);
            event.add(type, LHMiracleRoadAttributes.DARK_ATTRIBUTE_DAMAGE);
            event.add(type, LHMiracleRoadAttributes.HOLY_ATTRIBUTE_DAMAGE);
            event.add(type, LHMiracleRoadAttributes.SOUL_ATTRIBUTE_DAMAGE);

            //异常累计值
            event.add(type, LHMiracleRoadAttributes.ABNORMAL_BLEED_BUILDUP);
            event.add(type, LHMiracleRoadAttributes.ABNORMAL_FROST_BUILDUP);
            event.add(type, LHMiracleRoadAttributes.ABNORMAL_POISON_BUILDUP);
            event.add(type, LHMiracleRoadAttributes.ABNORMAL_BURN_BUILDUP);
        }
    }
}
