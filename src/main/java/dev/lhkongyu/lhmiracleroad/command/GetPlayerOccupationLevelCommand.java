package dev.lhkongyu.lhmiracleroad.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.data.loot.nbt.CreedTalismanData;
import dev.lhkongyu.lhmiracleroad.data.loot.nbt.MiraculousTalismanData;
import dev.lhkongyu.lhmiracleroad.registry.ItemsRegistry;
import dev.lhkongyu.lhmiracleroad.tool.FileTool;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.PlayerAttributeTool;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * 设置指令
 */
public class GetPlayerOccupationLevelCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
          dispatcher.register(Commands.literal("lh_miracle_road")
                  .then(Commands.literal("points")
                          .requires(e -> e.hasPermission(2))
                          .then(Commands.literal("add")
                                  .then(Commands.argument("players", EntityArgument.players())
                                          .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                  .executes(context -> {
                                                      var players = EntityArgument.getPlayers(context, "players");
                                                      var amount = IntegerArgumentType.getInteger(context, "amount");
                                                      for (Player player : players){
                                                          player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(playerOccupationAttribute-> {
                                                              int points = playerOccupationAttribute.getPoints();
                                                              points += amount;
                                                              playerOccupationAttribute.setPoints(points);
                                                              context.getSource().sendSuccess(() -> Component.translatable("lhmiracleroad.instructions.points.add",player.getScoreboardName(),amount),false);
                                                          });
                                                      }
                                                      return 0;
                                                  })
                                          )
                                  )
                          )
                          .then(Commands.literal("clear")
                                  .then(Commands.argument("players", EntityArgument.players())
                                          .executes(context -> {
                                              var players = EntityArgument.getPlayers(context, "players");
                                              for (Player player : players){
                                                  player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(playerOccupationAttribute-> {
                                                      playerOccupationAttribute.setPoints(0);
                                                      context.getSource().sendSuccess(() -> Component.translatable("lhmiracleroad.instructions.points.clear",player.getScoreboardName()),false);
                                                  });
                                              }
                                              return 0;
                                          })
                                  )
                          )
                  )
                  .then(Commands.literal("reset")
                          .requires(e -> e.hasPermission(2))
                          .then(Commands.literal("level")
                              .then(Commands.argument("players", EntityArgument.players())
                                      .executes(context -> {
                                          var players = EntityArgument.getPlayers(context, "players");
                                          for (ServerPlayer player : players){
                                              Optional<PlayerOccupationAttribute> occupationAttribute = player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve();
                                              if (occupationAttribute.isPresent()){
                                                  PlayerOccupationAttribute playerOccupationAttribute = occupationAttribute.get();
                                                  if (playerOccupationAttribute.getOccupationId() == null)
                                                      context.getSource().sendSuccess(() -> Component.translatable("lhmiracleroad.instructions.prompt",player.getScoreboardName()).withStyle(ChatFormatting.RED), false);
                                                  PlayerAttributeTool.resetLevel(player,playerOccupationAttribute);
                                                  context.getSource().sendSuccess(() -> Component.translatable("lhmiracleroad.instructions.reset.level",player.getScoreboardName()).withStyle(ChatFormatting.GREEN), false);
                                              }
                                          }
                                          return 0;
                                      })
                              )
                          )
                          .then(Commands.literal("occupation")
                              .then(Commands.argument("players", EntityArgument.players())
                                      .executes(context -> {
                                          var players = EntityArgument.getPlayers(context, "players");
                                          for (ServerPlayer player : players){
                                              Optional<PlayerOccupationAttribute> occupationAttribute = player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve();
                                              if (occupationAttribute.isPresent()){
                                                  PlayerOccupationAttribute playerOccupationAttribute = occupationAttribute.get();
                                                  if (playerOccupationAttribute.getOccupationId() == null)
                                                      context.getSource().sendSuccess(() -> Component.translatable("lhmiracleroad.instructions.prompt",player.getScoreboardName()).withStyle(ChatFormatting.RED), false);
                                                  PlayerAttributeTool.resetOccupation(player,playerOccupationAttribute);
                                                  context.getSource().sendSuccess(() -> Component.translatable("lhmiracleroad.instructions.reset.occupation",player.getScoreboardName()).withStyle(ChatFormatting.GREEN), false);
                                              }
                                          }
                                          return 0;
                                      })
                              )
                          )
                  )
                  .then(Commands.literal("attribute_max_level")
                          .requires(e -> e.hasPermission(2))
                          .then(Commands.literal("set")
                                  .then(Commands.argument("players", EntityArgument.players())
                                          .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                  .executes(context -> {
                                                      var players = EntityArgument.getPlayers(context, "players");
                                                      var amount = IntegerArgumentType.getInteger(context, "amount");
                                                      for (ServerPlayer player : players){
                                                          player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(playerOccupationAttribute-> {
                                                              playerOccupationAttribute.setAttributeMaxLevel(amount);
                                                              context.getSource().sendSuccess(() -> Component.translatable("lhmiracleroad.instructions.attribute_max_level.set",player.getScoreboardName(),amount),false);
                                                              LHMiracleRoadTool.synchronizationClient(playerOccupationAttribute, player);
                                                          });
                                                      }
                                                      return 0;
                                                  })
                                          )
                                  )
                          )
                  )
                  .then(Commands.literal("equipment")
                          .requires(e -> e.hasPermission(2))
                          .then(Commands.literal("set")
                                  .then(Commands.argument("occupation_id", StringArgumentType.greedyString())
                                      .executes(context -> {
                                          ServerPlayer player = context.getSource().getPlayer();
                                          String occupationId = StringArgumentType.getString(context, "occupation_id");
                                          FileTool.creationOccupationInitItem(player,occupationId);
                                          return 0;
                                      })
                                  )
                          )
                  )
                  .then(Commands.literal("talisman")
                          .requires(e -> e.hasPermission(2))
                          .then(Commands.literal("CreedTalisman")
                                  .executes(context -> {
                                      ServerPlayer player = context.getSource().getPlayer();
                                      ItemStack itemStack = ItemsRegistry.CREED_TALISMAN.get().getDefaultInstance();
                                      CreedTalismanData.setCreedTalismanData(itemStack);
                                      if (player != null) {
                                          LHMiracleRoadTool.addItemStack(player,itemStack);
                                      }
                                      return 0;
                                  })
                          )
                          .then(Commands.literal("MiraculousTalisman")
                                  .executes(context -> {
                                      ServerPlayer player = context.getSource().getPlayer();
                                      ItemStack itemStack = ItemsRegistry.MIRACULOUS_TALISMAN.get().getDefaultInstance();
                                      MiraculousTalismanData.setMiraculousTalismanData(itemStack);
                                      if (player != null) {
                                          LHMiracleRoadTool.addItemStack(player,itemStack);
                                      }
                                      return 0;
                                  })
                          )
                  )

          );
    }
}
