package com.blay.autotpahere;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;

import java.util.Collection;

public class AutoTpahereClient implements ClientModInitializer {
    public static KeyBinding allTpaHereKeybind;

    @Override
    public void onInitializeClient() {
        allTpaHereKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "auto-tpahere.key.tpa",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_DOWN,
            "key.categories.misc"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(allTpaHereKeybind.wasPressed()) {
                int count=0;
                ClientPlayNetworkHandler networkHandler=MinecraftClient.getInstance().getNetworkHandler();
                Collection<PlayerListEntry> playerList=networkHandler.getPlayerList();

                for(PlayerListEntry player:playerList){
                    String name=player.getProfile().getName();
                    if(client.player.getEntityName()!=name){
                        count++;
                        client.player.sendMessage(Text.literal("Trying /tpahere "+name+" ..."));
                        client.player.sendCommand("tpahere "+name,null);
                    }
                }
                if(count==0){
                    client.player.sendMessage(Text.literal("You are the only one on the server!"),true);
                }
            }
        });
    }
}