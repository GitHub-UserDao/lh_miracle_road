package dev.lhkongyu.lhmiracleroad.packet;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.PlayerAttributeTool;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.Set;
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
            Player clientPlayer = Minecraft.getInstance().player;
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
                playerOccupationAttribute.setShowAttribute(showAttributeMap);
                playerOccupationAttribute.setEmpiricalCalculationFormula(empiricalCalculationFormula);
                playerOccupationAttribute.setPoints(points);
                playerOccupationAttribute.setBurden(burden);
            }
        });
        context.setPacketHandled(true);
    }
}
