package dev.lhkongyu.lhmiracleroad.packet;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.data.ClientData;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public record ClientOccupationMessage(JsonObject playerOccupationAttributeObject) {

    public ClientOccupationMessage(FriendlyByteBuf buf){
        this(getPlayerOccupationAttributeObject(buf));
    }

    private static JsonObject getPlayerOccupationAttributeObject(FriendlyByteBuf buf){
        String jsonStr = buf.readUtf();
        return JsonParser.parseString(jsonStr).getAsJsonObject();
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeUtf(playerOccupationAttributeObject.toString());
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            ClientLevel clientLevel = mc.level;
            if (clientLevel == null) return;
            String playerUUID = LHMiracleRoadTool.isAsString(playerOccupationAttributeObject.get("playerUUID"));
            Player clientPlayer = clientLevel.getPlayerByUUID(UUID.fromString(playerUUID));
            if (clientPlayer != null) {
                PlayerOccupationAttribute playerOccupationAttribute = clientPlayer.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve().get();
                String occupationId = LHMiracleRoadTool.isAsString(playerOccupationAttributeObject.get("occupationId"));
                if (occupationId == null || occupationId.isEmpty()){
                    playerOccupationAttribute.setOccupationId(null);
                    return;
                }
                int occupationLevel = LHMiracleRoadTool.isAsInt(playerOccupationAttributeObject.get("occupationLevel"));
                int occupationExperience = LHMiracleRoadTool.isAsInt(playerOccupationAttributeObject.get("occupationExperience"));
                int points = LHMiracleRoadTool.isAsInt(playerOccupationAttributeObject.get("points"));
                int burden = LHMiracleRoadTool.isAsInt(playerOccupationAttributeObject.get("burden"));
                int attributeMaxLevel = LHMiracleRoadTool.isAsInt(playerOccupationAttributeObject.get("attributeMaxLevel"));
                double offhandHeavy = LHMiracleRoadTool.isAsDouble(playerOccupationAttributeObject.get("offhandHeavy"));
                String empiricalCalculationFormula = LHMiracleRoadTool.isAsString(playerOccupationAttributeObject.get("empiricalCalculationFormula"));

//                JsonObject attributeModifierObject = LHMiracleRoadTool.isAsJsonObject(playerOccupationAttributeObject.get("attributeModifier"));
//                Map<String, AttributeModifier> attributeModifierMap = jsonObjectConversionAttributeModifier(attributeModifierObject);

//                JsonObject punishmentAttributeModifierObject = LHMiracleRoadTool.isAsJsonObject(playerOccupationAttributeObject.get("punishmentAttributeModifier"));
//                Map<String, AttributeModifier> punishmentAttributeModifierMap = jsonObjectConversionAttributeModifier(punishmentAttributeModifierObject);

                JsonObject occupationAttributeLevelObject = LHMiracleRoadTool.isAsJsonObject(playerOccupationAttributeObject.get("occupationAttributeLevel"));
                Map<String, Integer> occupationAttributeLevelMap = Maps.newHashMap();
                if (occupationAttributeLevelObject != null) {
                    for (Map.Entry<String, JsonElement> entry : occupationAttributeLevelObject.entrySet()) {
                        occupationAttributeLevelMap.put(entry.getKey(), LHMiracleRoadTool.isAsInt(entry.getValue()));
                    }
                }

//                JsonObject heavyAttributeModifierObject = LHMiracleRoadTool.isAsJsonObject(playerOccupationAttributeObject.get("heavyAttributeModifier"));
//                AttributeModifier heavyAttributeModifier = null;
//                if (heavyAttributeModifierObject != null) {
//                    UUID uuid = UUID.fromString(LHMiracleRoadTool.isAsString(heavyAttributeModifierObject.get("uuid")));
//                    String name = LHMiracleRoadTool.isAsString(heavyAttributeModifierObject.get("name"));
//                    double amount = LHMiracleRoadTool.isAsDouble(heavyAttributeModifierObject.get("amount"));
//                    AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(LHMiracleRoadTool.isAsInt(heavyAttributeModifierObject.get("operation")));
//                    heavyAttributeModifier = new AttributeModifier(uuid, name, amount, operation);
//                }

                JsonObject showAttributeObject = LHMiracleRoadTool.isAsJsonObject(playerOccupationAttributeObject.get("showAttribute"));
                Map<String, JsonObject> showAttributeMap = Maps.newHashMap();
                if (showAttributeObject != null){
                    for (Map.Entry<String, JsonElement> entry:showAttributeObject.entrySet()){
                        showAttributeMap.put(entry.getKey(),LHMiracleRoadTool.isAsJsonObject(entry.getValue()));
                    }
                }

                playerOccupationAttribute.setOccupationLevel(occupationLevel);
                playerOccupationAttribute.setOccupationExperience(occupationExperience);
                playerOccupationAttribute.setOccupationId(occupationId);
                playerOccupationAttribute.setOffhandHeavy(offhandHeavy);
//                playerOccupationAttribute.setAttributeModifier(attributeModifierMap);
//                playerOccupationAttribute.setPunishmentAttributeModifier(punishmentAttributeModifierMap);
                playerOccupationAttribute.setOccupationAttributeLevel(occupationAttributeLevelMap);
//                playerOccupationAttribute.setHeavyAttributeModifier(heavyAttributeModifier);
                playerOccupationAttribute.setEmpiricalCalculationFormula(empiricalCalculationFormula);
                playerOccupationAttribute.setPoints(points);
                playerOccupationAttribute.setBurden(burden);
                playerOccupationAttribute.setAttributeMaxLevel(attributeMaxLevel);
            }
        });
        context.setPacketHandled(true);
    }
}
