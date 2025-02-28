package dev.lhkongyu.lhmiracleroad;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.logging.LogUtils;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.data.reloader.*;
import dev.lhkongyu.lhmiracleroad.registry.*;
import dev.lhkongyu.lhmiracleroad.packet.PlayerAttributeChannel;
import dev.lhkongyu.lhmiracleroad.shaders.LHInternalShaders;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.io.IOException;


@Mod(LHMiracleRoad.MODID)
public class LHMiracleRoad
{
    public static final String MODID = "lhmiracleroad";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LHMiracleRoad()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, LHMiracleRoadConfig.COMMON_SPEC);
        MinecraftForge.EVENT_BUS.addListener(this::reloadListnerEvent);
        PlayerAttributeChannel.register();
        LHMiracleRoadAttributes.register();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(LHMiracleRoadAttributes::registerPlayerAttribute);
        ItemsRegistry.register(bus);
        TabsRegistry.register(bus);
        EffectRegistry.register(bus);
        EntityRegistry.register(bus);
        ParticleRegistry.register(bus);
        bus.addListener(this::registerShaders);
    }

    private void reloadListnerEvent(final AddReloadListenerEvent event) {
        event.addListener(new AttributeReloadListener());
        event.addListener(new OccupationReloadListener());
        event.addListener(new AttributePointsRewardsReloadListener());
        event.addListener(new EquipmentReloadListener());
        event.addListener(new ShowGuiAttributeReloadListener());
        event.addListener(new PunishmentReloadListener());
        event.addListener(new InitItemResourceReloadListener());
    }

    private void registerShaders(final RegisterShadersEvent e) {
        try {
            e.registerShader(new ShaderInstance(e.getResourceProvider(), new ResourceLocation(MODID, "rendertype_magic"), DefaultVertexFormat.NEW_ENTITY), LHInternalShaders::setRenderTypeMagic);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
