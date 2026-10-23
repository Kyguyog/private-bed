package dev.privatebed;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

final class ConfigNavigation {
    static void close(Minecraft minecraft, Screen parent) { minecraft.gui.setScreen(parent); }
}
