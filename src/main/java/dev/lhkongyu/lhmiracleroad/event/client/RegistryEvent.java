package dev.lhkongyu.lhmiracleroad.event.client;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.entity.renderer.PlayerSoulRenderer;
import dev.lhkongyu.lhmiracleroad.client.particle.SoulParticle;
import dev.lhkongyu.lhmiracleroad.registry.EntityRegistry;
import dev.lhkongyu.lhmiracleroad.registry.ParticleRegistry;
import dev.lhkongyu.lhmiracleroad.client.screen.overlay.SoulHudOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LHMiracleRoad.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RegistryEvent {

    /**
     * 粒子注册
     * @param event
     */
    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleRegistry.SOUL_PARTICLE.get(), SoulParticle.Provider::new);
    }

    /**
     * 实体注册
     * @param event
     */
    @SubscribeEvent
    public static void rendererRegister(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.PLAYER_SOUL.get(), (context) -> new PlayerSoulRenderer(context, 1f));
    }

    /**
     * 实体模型注册
     * @param event
     */
    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(PlayerSoulRenderer.MODEL_LAYER_LOCATION, PlayerSoulRenderer::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        // 在这里注册到右下角（HUD 之上）
        event.registerAboveAll("soul_hud", new SoulHudOverlay());
    }
}
