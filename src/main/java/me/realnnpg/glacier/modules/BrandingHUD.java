package com.example.addon.modules;

import meteordevelopment.meteorclient.systems.hud.HUD;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class BrandingHUD {
    public static final HudElementInfo<Element> INFO = new HudElementInfo<>(HUD.get().getGroup(), "glacier-branding", "Displays author credits.", Element::new);

    public static class Element extends HudElement {
        public Element() {
            super(INFO);
        }

        @Override
        public void update() {
            box.setSize(renderer.textWidth("made by pedropagani", true), renderer.textHeight(true));
        }

        @Override
        public void render(Color color) {
            renderer.text("made by pedropagani", box.getX(), box.getY(), Color.WHITE, true);
        }
    }
}
