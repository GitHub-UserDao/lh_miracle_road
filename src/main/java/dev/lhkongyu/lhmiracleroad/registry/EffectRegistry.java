package dev.lhkongyu.lhmiracleroad.registry;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.effect.HunterMarkEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class EffectRegistry {

    public static final DeferredRegister<MobEffect> EFFECT_DEFERRED_REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, LHMiracleRoad.MODID);

    public static final RegistryObject<MobEffect> HUNTER_MARK = EFFECT_DEFERRED_REGISTER.register("hunter_mark", () -> new HunterMarkEffect(MobEffectCategory.BENEFICIAL, 0xFFF58D,0.002).addAttributeModifier(LHMiracleRoadAttributes.DAMAGE_ADDITION,"d632b5b4-62cf-6d99-b959-6de3cd1ee21e",0.002, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static void register(IEventBus eventBus) {
        EFFECT_DEFERRED_REGISTER.register(eventBus);
    }
}
