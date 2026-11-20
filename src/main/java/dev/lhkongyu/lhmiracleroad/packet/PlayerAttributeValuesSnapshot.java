package dev.lhkongyu.lhmiracleroad.packet;

import dev.lhkongyu.lhmiracleroad.client.screen.occupation.UpgradeInterfaceScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public record PlayerAttributeValuesSnapshot(List<AttributeValue> values) {

    public PlayerAttributeValuesSnapshot(FriendlyByteBuf buffer) {
        this(buffer.readList(AttributeValue::new));
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeCollection(values, (buf, attribute) -> {
            buf.writeResourceLocation(attribute.id());
            buf.writeDouble(attribute.value());
        });
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.apply(values));
        supplier.get().setPacketHandled(true);
    }

    public static void send(ServerPlayer player) {
        AttributeMap attributes = player.getAttributes();
        List<AttributeValue> values = new ArrayList<>();
        for (var entry : ForgeRegistries.ATTRIBUTES.getEntries()) {
            Attribute attribute = entry.getValue();
            if (attributes.hasAttribute(attribute)) {
                values.add(new AttributeValue(entry.getKey().location(), displayValue(attributes, attribute)));
            }
        }
        values.sort(Comparator.comparing(attribute -> attribute.id().toString()));
        PlayerChannel.sendToClient(new PlayerAttributeValuesSnapshot(values), player);
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

    public record AttributeValue(ResourceLocation id, double value) {
        private AttributeValue(FriendlyByteBuf buffer) {
            this(buffer.readResourceLocation(), buffer.readDouble());
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
