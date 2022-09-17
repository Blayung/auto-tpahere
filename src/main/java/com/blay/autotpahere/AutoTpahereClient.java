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
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.lang.Thread;
import java.lang.InterruptedException;
import java.util.concurrent.TimeUnit;

public class AutoTpahereClient implements ClientModInitializer {
    public static KeyBinding allTpaHereKeybind;

    private static int count;
    private static ClientPlayNetworkHandler networkHandler;
    private static Collection<PlayerListEntry> playerList;
    private static ClientPlayerEntity clientPlayer;
    private static String name;
    private static List<String> names;
    private static boolean isRunning;
    private static Thread thread;

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            for(int i=0;i<count;i++){
                clientPlayer.sendMessage(Text.literal("Trying /tpahere "+names.get(i)+"..."));
                clientPlayer.sendCommand("tpahere "+names.get(i),null);
                try{
                    TimeUnit.MILLISECONDS.sleep(1100);
                }catch(InterruptedException e){
                    isRunning=false;
                    clientPlayer.sendMessage(Text.literal("Something went wrong..."));
                }
            }
            isRunning=false;
        }
    }

    @Override
    public void onInitializeClient() {
        isRunning=false;
        allTpaHereKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "auto-tpahere.key.tpa",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_DOWN,
            "key.categories.misc"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(allTpaHereKeybind.wasPressed()) {
                if(isRunning){
                    client.player.sendMessage(Text.literal("Global /tpahere is already in progress!"),true);
                }else{
                    isRunning=true;
                    thread = new Thread(new MyRunnable());
                    networkHandler=MinecraftClient.getInstance().getNetworkHandler();
                    playerList=networkHandler.getPlayerList();
                    clientPlayer=client.player;

                    count=0;
                    names=new ArrayList<String>();
                    for(PlayerListEntry player:playerList){
                        name=player.getProfile().getName();
                        count++;
                        names.add(name);
                    }
                    thread.start();
                }
            }
        });
    }
}
