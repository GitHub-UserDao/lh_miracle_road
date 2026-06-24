package dev.lhkongyu.lhmiracleroad.items.curio.bracelet;

import dev.lhkongyu.lhmiracleroad.capability.PlayerCurio;
import dev.lhkongyu.lhmiracleroad.capability.PlayerCurioProvider;
import dev.lhkongyu.lhmiracleroad.registry.ItemsRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;

public class AbyssbindBracelet {

    public static void equipAbyssbindBracelet(LivingEntity livingEntity, boolean isEquipAbyssbindBracelet){
        livingEntity.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).ifPresent(playerCurio -> {
            playerCurio.setEquipAbyssbindBracelet(isEquipAbyssbindBracelet);
        });
    }

    public static void consume(Player player){
        CuriosApi.getCuriosInventory(player).map(handler -> {
            IItemHandlerModifiable curios = handler.getEquippedCurios();
            for (int i = 0; i < curios.getSlots(); i++) {
                ItemStack stack = curios.getStackInSlot(i);
                if (stack.is(ItemsRegistry.ABYSSBIND_BRACELET.get())) {
                    stack.shrink(1);
                    if (stack.isEmpty()) curios.setStackInSlot(i, ItemStack.EMPTY);
                    return true;
                }
            }
            return false;
        });
    }

    public static boolean getIsEquipAbyssbindBracelet(Player player){
        Optional<PlayerCurio> playerCurioOptional = player.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).resolve();
        return playerCurioOptional.map(PlayerCurio::isEquipAbyssbindBracelet).orElse(false);
    }
}
