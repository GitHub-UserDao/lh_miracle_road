package dev.lhkongyu.lhmiracleroad.tool.particle;

import dev.lhkongyu.lhmiracleroad.client.particle.soul.SoulParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.joml.Math;
import org.joml.Vector3f;

import java.util.List;

public class ParticleTool {

    public static void spawnServerParticles(Level level, ParticleOptions particle, boolean distant, double x, double y, double z, int count, double diffuseX, double diffuseY, double diffuseZ, double speed) {
        ServerLevel serverLevel = (ServerLevel) level;
        nearbyPlayers(level, x, y, z).forEach(player ->
                serverLevel.sendParticles(player, particle, distant, x, y, z, count, diffuseX, diffuseY, diffuseZ, speed)
        );
    }

    public static List<ServerPlayer> nearbyPlayers(Level level, double x, double y, double z) {
        double maxDistanceSqr = 32 * 32;  // 半径32格的平方距离
        return level.getServer().getPlayerList().getPlayers().stream()
                .filter(player -> player.distanceToSqr(x, y, z) <= maxDistanceSqr)
                .toList();
    }

    public static Vector3f RGBChangeVector3f(int red, int green, int blue) {
        float normalizedRed = (float)red / 255.0f;
        float normalizedGreen = (float)green / 255.0f;
        float normalizedBlue = (float)blue / 255.0f;
        return new Vector3f(normalizedRed, normalizedGreen, normalizedBlue);
    }

    public static void getSoulParticle(ServerLevel serverLevel, ServerPlayer player, int soulCount,int max){
        int particleCount = org.joml.Math.min(org.joml.Math.max(soulCount / 200,10),max);
        float speed = .1f + ((float) particleCount / max * .025f);
        serverLevel.sendParticles(player,new SoulParticleOption(player.getId()), true, player.getX(), player.getY() + player.getBbHeight() * 0.5, player.getZ(), particleCount, 0.1, 0.1, 0.1,speed);
    }

    public static void getSoulParticle(ServerLevel serverLevel, ServerPlayer player, int soulCount, int max,int soulCountDivisor, Entity target){
        if (target == null) return;
        int particleCount = org.joml.Math.min(org.joml.Math.max(soulCount / soulCountDivisor,10),max);
        float speed = .1f + ((float) particleCount / max * .025f);
        serverLevel.sendParticles(player,new SoulParticleOption(player.getId()), true, target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(), particleCount, 0.1, 0.1, 0.1,speed);
    }

    public static void getSoulParticle(ServerLevel serverLevel, ServerPlayer player, int soulCount, int max,int min,int soulCountDivisor, Entity target){
        if (target == null) return;
        int particleCount = org.joml.Math.min(Math.max(soulCount / soulCountDivisor,min),max);
        float speed = .1f + ((float) particleCount / max * .025f);
        serverLevel.sendParticles(player,new SoulParticleOption(player.getId()), true, target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(), particleCount, 0.1, 0.1, 0.1,speed);
    }
}
