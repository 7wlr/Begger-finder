package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Mod(modid = BeggingMod.MODID, name = BeggingMod.NAME, version = BeggingMod.VERSION)
public class BeggingMod {
    public static final String MODID = "beggingmod";
    public static final String NAME = "Begging Mod";
    public static final String VERSION = "1.0";

    private static class ChatMessage {
        String message;
        int displayTicks;
        int fadeTicks;

        ChatMessage(String message, int displayTicks) {
            this.message = message;
            this.displayTicks = displayTicks;
            this.fadeTicks = 200;
        }
    }

    private final List<ChatMessage> messages = new ArrayList<ChatMessage>();
    private final String[] flaggedWords = {"plz", "pls", "vip", "please"}; 
    private final Random random = new Random(); 

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText().toLowerCase();

        for (String word : flaggedWords) {
            if (message.contains(word)) {
                String rank = "Default";
                String username = "Unknown";
                if (message.contains("]")) {
                    int start = message.indexOf("[");
                    int end = message.indexOf("]");
                    if (start != -1 && end != -1) {
                        rank = message.substring(start + 1, end).trim();
                    }
                    username = message.split(":")[0].substring(end + 1).trim();
                } else {
                    username = message.split(":")[0].trim();
                }

                String formattedMessage = EnumChatFormatting.RED + "[" +
                        EnumChatFormatting.RED + EnumChatFormatting.BOLD + "WLR" +
                        EnumChatFormatting.RESET + EnumChatFormatting.RED + "]" +
                        EnumChatFormatting.YELLOW + " " + EnumChatFormatting.AQUA + rank +
                        EnumChatFormatting.RESET + " " + username + " has begged";

                messages.add(new ChatMessage(formattedMessage, 2000));
                if (messages.size() > 5) {
                    messages.remove(0); 
                }
                break; 
            }
        }

        if (message.contains("gifted")) {
            String gifter = "Unknown";
            String recipient = "Unknown";

            if (message.contains("gifted")) {
                gifter = message.split(" ")[0]; 
                String[] parts = message.split(" to "); 
                if (parts.length > 1) {
                    recipient = parts[1].split("!")[0]; 
                }
            }

            StringBuilder randomCode = new StringBuilder();
            String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            for (int i = 0; i < 6; i++) {
                randomCode.append(alphanumeric.charAt(random.nextInt(alphanumeric.length())));
            }

            String ggMessage = "GG (" + randomCode + ")";
            Minecraft.getMinecraft().thePlayer.sendChatMessage(ggMessage);
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (messages.isEmpty()) return;

        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int x = 10; 
        int y = 10; 
        int lineHeight = mc.fontRendererObj.FONT_HEIGHT + 2;

        Iterator<ChatMessage> iterator = messages.iterator();
        while (iterator.hasNext()) {
            ChatMessage chatMessage = iterator.next();

            int alpha = 255;
            if (chatMessage.displayTicks < chatMessage.fadeTicks) {
                alpha = (int) (255 * (chatMessage.displayTicks / (float) chatMessage.fadeTicks));
            }

            int color = (alpha << 24) | 0xFFFFFF; 
            mc.fontRendererObj.drawStringWithShadow(chatMessage.message, x, y, color);

            y += lineHeight;

            chatMessage.displayTicks--;
            if (chatMessage.displayTicks <= 0) {
                iterator.remove();
            }
        }
    }
}
