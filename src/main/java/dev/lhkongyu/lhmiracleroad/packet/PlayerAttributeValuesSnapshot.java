package dev.lhkongyu.lhmiracleroad.packet;

import dev.lhkongyu.lhmiracleroad.client.screen.occupation.UpgradeInterfaceScreen;
import dev.lhkongyu.lhmiracleroad.config.ShowGuiAttributeConfig;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public record PlayerAttributeValuesSnapshot(List<AttributeValue> values) {

    public PlayerAttributeValuesSnapshot(FriendlyByteBuf buffer) {
        this(buffer.readList(AttributeValue::new));
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeCollection(values, (buf, attribute) -> {
            buf.writeResourceLocation(attribute.id());
            buf.writeUtf(attribute.value());
        });
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.apply(values));
        supplier.get().setPacketHandled(true);
    }

    public static void send(ServerPlayer player) {
        AttributeMap attributes = player.getAttributes();
        AttributeSupplier defaults = DefaultAttributes.getSupplier(EntityType.PLAYER);
        Set<ResourceLocation> shownIds = new HashSet<>();
        List<AttributeValue> values = new ArrayList<>();
        for (ShowGuiAttributeConfig.Entry entry : ShowGuiAttributeConfig.entries()) {
            Attribute attribute = resolveAttribute(entry.attribute());
            if (attribute == null || !attributes.hasAttribute(attribute)) continue;

            ResourceLocation id = ForgeRegistries.ATTRIBUTES.getKey(attribute);
            if (id == null || !shownIds.add(id)) continue;

            double defaultValue = defaults != null && defaults.hasAttribute(attribute)
                    ? defaults.getBaseValue(attribute) : attribute.getDefaultValue();
            values.add(new AttributeValue(id, entry.valueType().format(displayValue(attributes, attribute), defaultValue)));
        }
        PlayerChannel.sendToClient(new PlayerAttributeValuesSnapshot(values), player);
    }

    private static Attribute resolveAttribute(String name) {
        Attribute attribute = LHMiracleRoadTool.stringConversionAttribute(name);
        if (attribute != null) return attribute;

        ResourceLocation id = ResourceLocation.tryParse(name);
        if (id != null && id.getNamespace().equals("minecraft") && !id.getPath().contains(".")) {
            return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("minecraft", "generic." + id.getPath()));
        }
        return null;
    }

    private static double displayValue(AttributeMap attributes, Attribute attribute) {
        double actualValue = attributes.getValue(attribute);
        if (actualValue != 0.0 || attributes.getBaseValue(attribute) != 0.0) return actualValue;

        AttributeInstance instance = attributes.getInstance(attribute);
        if (instance == null) return actualValue;

        double flatBonus = 0.0;
        for (AttributeModifier modifier : instance.getModifiers(AttributeModifier.Operation.ADDITION)) {
            flatBonus += modifier.getAmount();
        }
        if (flatBonus != 0.0) return actualValue;

        // Track the bonus above the neutral multiplier of 1 without subtracting it at the end.
        double bonus = 0.0;
        for (AttributeModifier modifier : instance.getModifiers(AttributeModifier.Operation.MULTIPLY_BASE)) {
            bonus += modifier.getAmount();
        }
        for (AttributeModifier modifier : instance.getModifiers(AttributeModifier.Operation.MULTIPLY_TOTAL)) {
            bonus += (1.0 + bonus) * modifier.getAmount();
        }
        return bonus;
    }

    public record AttributeValue(ResourceLocation id, String value) {
        private AttributeValue(FriendlyByteBuf buffer) {
            this(buffer.readResourceLocation(), buffer.readUtf());
        }
    }

    private static final class ClientHandler {
        private static void apply(List<AttributeValue> values) {
            if (Minecraft.getInstance().screen instanceof UpgradeInterfaceScreen screen) {
                screen.setAttributeValues(values);
            }
        }
    }
}
