package dev.lhkongyu.lhmiracleroad.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.config.initItem.InitItemConfig;
import dev.lhkongyu.lhmiracleroad.config.occupation.Occupation;
import dev.lhkongyu.lhmiracleroad.config.occupation.OccupationConfig;
import dev.lhkongyu.lhmiracleroad.config.punishment.PunishmentConfig;
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
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 设置指令
 */
public class GetPlayerOccupationLevelCommand {

    private static int createPunishment(CommandSourceStack source, ResourceLocation itemId, String punishmentId, int heavy) {
        if (PunishmentConfig.getPunishment(punishmentId) == null) {
            source.sendFailure(Component.translatable("lhmiracleroad.instructions.punishment.invalid_preset", punishmentId));
            return 0;
        }
        try {
            PunishmentConfig.createFromIronSword(itemId, punishmentId, heavy);
        } catch (IllegalArgumentException e) {
            source.sendFailure(Component.translatable("lhmiracleroad.instructions.punishment.invalid_item", itemId.toString()));
            return 0;
        } catch (IOException e) {
            LHMiracleRoad.LOGGER.error("Failed to create punishment config for {}", itemId, e);
            source.sendFailure(Component.translatable("lhmiracleroad.instructions.punishment.failed", itemId.toString()));
            return 0;
        }
        source.sendSuccess(() -> Component.translatable("lhmiracleroad.instructions.punishment.created", itemId.toString()), false);
        return 1;
    }

    private static int addPunishmentPreset(CommandSourceStack source, String id, double value, int needPoints, String attributes) {
        if (id.isBlank()) {
            source.sendFailure(Component.translatable("lhmiracleroad.instructions.punishment.empty_preset"));
            return 0;
        }
        Map<Attribute, String> attributesByType = new LinkedHashMap<>();
        for (String name : attributes.strip().split("\\s+")) {
            Attribute attribute = LHMiracleRoadTool.stringConversionAttribute(name);
            if (attribute == null) {
                source.sendFailure(Component.translatable("lhmiracleroad.instructions.punishment.invalid_attribute", name));
                return 0;
            }
            attributesByType.putIfAbsent(attribute, name);
        }
        try {
            if (!PunishmentConfig.addPreset(id, value, needPoints, attributesByType.values())) {
                source.sendFailure(Component.translatable("lhmiracleroad.instructions.punishment.preset_exists", id));
                return 0;
            }
        } catch (IOException e) {
            LHMiracleRoad.LOGGER.error("Failed to add punishment preset {}", id, e);
            source.sendFailure(Component.translatable("lhmiracleroad.instructions.punishment.preset_failed", id));
            return 0;
        }
        source.sendSuccess(() -> Component.translatable("lhmiracleroad.instructions.punishment.preset_added", id), false);
        return 1;
    }

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
                  .then(Commands.literal("punishment")
                          .requires(source -> source.hasPermission(2))
                          .then(Commands.literal("create")
                                  .then(Commands.argument("item_id", ResourceLocationArgument.id())
                                          .suggests((context, builder) -> SharedSuggestionProvider.suggestResource(ForgeRegistries.ITEMS.getKeys(), builder))
                                          .then(Commands.argument("punishment_id", StringArgumentType.string())
                                                  .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                          PunishmentConfig.getPunishmentIds().stream().map(StringArgumentType::escapeIfRequired), builder))
                                                  .then(Commands.argument("heavy", IntegerArgumentType.integer(0))
                                                          .executes(context -> createPunishment(context.getSource(),
                                                                  ResourceLocationArgument.getId(context, "item_id"),
                                                                  StringArgumentType.getString(context, "punishment_id"),
                                                                  IntegerArgumentType.getInteger(context, "heavy")))))))
                          .then(Commands.literal("add")
                                  .then(Commands.argument("punishment_id", StringArgumentType.string())
                                          .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                  .then(Commands.argument("need_points", IntegerArgumentType.integer(0))
                                                          .then(Commands.argument("attributes", StringArgumentType.greedyString())
                                                                  .suggests((context, builder) -> SharedSuggestionProvider.suggestResource(
                                                                          ForgeRegistries.ATTRIBUTES.getKeys(), builder.createOffset(
                                                                                  Math.max(builder.getStart(), builder.getInput().lastIndexOf(' ') + 1))))
                                                                  .executes(context -> addPunishmentPreset(context.getSource(),
                                                                          StringArgumentType.getString(context, "punishment_id"),
                                                                          DoubleArgumentType.getDouble(context, "value"),
                                                                          IntegerArgumentType.getInteger(context, "need_points"),
                                                                          StringArgumentType.getString(context, "attributes")))))))))
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
