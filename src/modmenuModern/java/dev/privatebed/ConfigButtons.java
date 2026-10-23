package dev.privatebed;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

final class ConfigButtons {
    static Button create(int x, int y, int width, Component label, Button.OnPress action) {
        return Button.builder(label, action).bounds(x, y, width, 20).build();
    }
}
