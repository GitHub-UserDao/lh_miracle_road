package dev.lhkongyu.lhmiracleroad.tool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileTool {

    public static void creationOccupationInitItem(ServerPlayer player,String occupationId){
        String jsonString = playerItemsConversionJson(player,occupationId);
        try {
            // 创建文件夹
            File folder = new File("lhInitItem");
            if (!folder.exists()) if (!folder.mkdir()) return;
            //创建json文件
            File file = new File(folder,occupationId+".json");
            FileWriter writer = new FileWriter(file);
            writer.write(jsonString);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String playerItemsConversionJson(ServerPlayer player,String occupationId){
        JsonObject initItem = new JsonObject();
        initItem.addProperty("id",occupationId);

        JsonArray items = new JsonArray();
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.isEmpty()) continue;
            JsonObject itemObj = new JsonObject();
            ResourceLocation resourceLocation = stack.getItem().builtInRegistryHolder().key().location();
            itemObj.addProperty("item",resourceLocation.toString());
            itemObj.addProperty("quantity", stack.getCount());
            if (stack.getTag() != null) {
                itemObj.addProperty("tag", stack.getTag().getAsString());
            }
            items.add(itemObj);
        }
        initItem.add("items", items);
        // 将 JSON 对象写入文件
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(initItem);
    }
}
