package dev.lhkongyu.lhmiracleroad.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.config.initItem.InitItemConfig;
import dev.lhkongyu.lhmiracleroad.config.occupation.Occupation;
import dev.lhkongyu.lhmiracleroad.config.occupation.OccupationConfig;
import dev.lhkongyu.lhmiracleroad.data.loot.nbt.CreedTalismanData;
import dev.lhkongyu.lhmiracleroad.data.loot.nbt.MiraculousTalismanData;
import dev.lhkongyu.lhmiracleroad.registry.ItemsRegistry;
import dev.lhkongyu.lhmiracleroad.packet.InitItemPreviewMessage;
import dev.lhkongyu.lhmiracleroad.packet.OccupationCatalogMessage;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.PlayerAttributeTool;
import dev.lhkongyu.lhmiracleroad.tool.SyncTool;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Optional;

/**
 * 设置指令
 */
public class GetPlayerOccupationLevelCommand {

    private static int createOccupation(CommandSourceStack source, String id) {
        Occupation created;
        try {
            created = OccupationConfig.createFromOutcast(id);
        } catch (FileAlreadyExistsException e) {
            source.sendFailure(Component.translatable("lhmiracleroad.instructions.occupation.exists", id));
            return 0;
        } catch (IllegalArgumentException e) {
            source.sendFailure(Component.translatable("lhmiracleroad.instructions.init_item.invalid_id", id));
            return 0;
        } catch (IOException e) {
            LHMiracleRoad.LOGGER.error("Failed to create occupation {}", id, e);
            source.sendFailure(Component.translatable("lhmiracleroad.instructions.occupation.failed", id));
            return 0;
        }
        InitItemPreviewMessage.broadcastUpdate(id);
        OccupationCatalogMessage.broadcastNew(created);
        source.sendSuccess(() -> Component.translatable("lhmiracleroad.instructions.occupation.created", id), false);
        return 1;
    }

    private static int saveInitItems(CommandSourceStack source, String occupationId) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        try {
            InitItemConfig.saveFromPlayer(player, occupationId);
        } catch (IllegalArgumentException e) {
            source.sendFailure(Component.translatable("lhmiracleroad.instructions.init_item.invalid_id", occupationId));
            return 0;
        } catch (IOException e) {
            LHMiracleRoad.LOGGER.error("Failed to save starter items for {}", occupationId, e);
            source.sendFailure(Component.translatable("lhmiracleroad.instructions.init_item.failed", occupationId));
            return 0;
        }
        InitItemPreviewMessage.broadcastUpdate(occupationId);
        source.sendSuccess(() -> Component.translatable("lhmiracleroad.instructions.init_item.saved", occupationId), false);
        return 1;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
          dispatcher.register(Commands.literal("lh_miracle_road")
                  .then(Commands.literal("occupation")
                          .requires(source -> source.hasPermission(2))
                          .then(Commands.literal("create")
                                  .then(Commands.argument("occupation_id", StringArgumentType.word())
                                          .executes(context -> createOccupation(context.getSource(),
                                                  StringArgumentType.getString(context, "occupation_id"))))))
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
                                                              SyncTool.synchronizationClient(playerOccupationAttribute, player);
                                                          });
                                                      }
                                                      return 0;
                                                  })
                                          )
                                  )
                          )
                  )
                  .then(Commands.literal("initItem")
                          .requires(e -> e.hasPermission(2))
                          .then(Commands.literal("set")
                                  .then(Commands.argument("occupation_id", StringArgumentType.word())
                                      .executes(context -> saveInitItems(context.getSource(),
                                              StringArgumentType.getString(context, "occupation_id")))
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
