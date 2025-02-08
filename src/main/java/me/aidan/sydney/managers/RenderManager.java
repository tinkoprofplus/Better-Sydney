package me.aidan.sydney.managers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.aidan.sydney.Sydney;
import me.aidan.sydney.events.SubscribeEvent;
import me.aidan.sydney.events.impl.RenderOverlayEvent;
import me.aidan.sydney.events.impl.RenderWorldEvent;
import me.aidan.sydney.modules.impl.combat.AutoCrystalModule;
import me.aidan.sydney.modules.impl.core.RendersModule;
import me.aidan.sydney.utils.IMinecraft;
import me.aidan.sydney.utils.animations.Easing;
import me.aidan.sydney.utils.graphics.Renderer2D;
import me.aidan.sydney.utils.graphics.Renderer3D;
import me.aidan.sydney.utils.minecraft.WorldUtils;
import me.aidan.sydney.utils.miscellaneous.RenderPosition;
import me.aidan.sydney.utils.system.Counter;
import me.aidan.sydney.utils.system.MathUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class RenderManager implements IMinecraft {
    private final Counter counter = new Counter();
    @Getter private int fps;
    @Getter public CopyOnWriteArrayList<RenderPosition> renderPositions = new CopyOnWriteArrayList<>();

    private Target crystalTarget;
    private BlockPos prevPosition = null;
    private Vec3d renderPosition = null;

    private long animationStart = 0;

    public RenderManager() {
        Sydney.EVENT_HANDLER.subscribe(this);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderOverlayEvent event) {
        fps = counter.getCount();
        counter.increment();
    }

    @SubscribeEvent
    public void onRenderWorld$placePositions(RenderWorldEvent event) {
        if (mc.player == null || mc.world == null || renderPositions.isEmpty()) return;
        RendersModule module = Sydney.MODULE_MANAGER.getModule(RendersModule.class);

        for (RenderPosition position : renderPositions) {
            float scale = position.get();
            Box box = new Box(position.getPos());
            if (module.mode.getValue().equals("Shrink")) box = new Box(position.getPos()).contract(0.5).expand(MathHelper.clamp(scale / 2.0, 0.0, 0.5));

            if (module.renderMode.getValue().equalsIgnoreCase("Fill") || module.renderMode.getValue().equalsIgnoreCase("Both")) Renderer3D.renderBox(event.getMatrices(), box, module.getColor(module.mode.getValue(), module.fillColor.getColor(), scale));
            if (module.renderMode.getValue().equalsIgnoreCase("Outline") || module.renderMode.getValue().equalsIgnoreCase("Both")) Renderer3D.renderBoxOutline(event.getMatrices(), box, module.getColor(module.mode.getValue(), module.outlineColor.getColor(), scale));
        }

        renderPositions.removeIf(p -> p.get() <= 0);
    }

    @SubscribeEvent
    public void onRenderWorld$autoCrystal(RenderWorldEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (crystalTarget == null || crystalTarget.getPosition() == null) return;

        AutoCrystalModule autoCrystalModule = Sydney.MODULE_MANAGER.getModule(AutoCrystalModule.class);

        float scale;
        if (crystalTarget.getTarget() == 1) scale = Easing.ease(Easing.toDelta(crystalTarget.getTime(), autoCrystalModule.duration.getValue().intValue()), Easing.Method.EASE_OUT_CUBIC);
        else scale = 1.0f - Easing.ease(Easing.toDelta(crystalTarget.getTime(), autoCrystalModule.duration.getValue().intValue()), Easing.Method.EASE_IN_CUBIC);

        Box box = new Box(crystalTarget.getPosition());
        if (autoCrystalModule.mode.getValue().equals("Shrink")) box = new Box(crystalTarget.getPosition()).contract(0.5).expand(MathHelper.clamp(scale / 2.0, 0.0, 0.5));

        if(autoCrystalModule.animationMode.getValue().equals("Slide")) {
            if(renderPosition == null) renderPosition = MathUtils.getVec(crystalTarget.getPosition());

            if(!WorldUtils.equals(crystalTarget.getPosition(), prevPosition)) {
                animationStart = System.currentTimeMillis();
                prevPosition = crystalTarget.getPosition();
            }

            float easing = Easing.ease(Easing.toDelta(animationStart, (int) (Math.pow(autoCrystalModule.slideSmoothness.getValue().doubleValue(), 1.4) * 1000)), Easing.Method.EASE_OUT_QUART);
            renderPosition = renderPosition.add(MathUtils.scale(MathUtils.getVec(crystalTarget.getPosition()).subtract(renderPosition), easing));

            box = MathUtils.getBox(renderPosition);
            if (autoCrystalModule.mode.getValue().equals("Shrink")) box = MathUtils.getBox(renderPosition).contract(0.5).expand(MathHelper.clamp(scale / 2.0, 0.0, 0.5));
        }

        if (autoCrystalModule.renderMode.getValue().equalsIgnoreCase("Fill") || autoCrystalModule.renderMode.getValue().equalsIgnoreCase("Both"))
            Renderer3D.renderGradientBox(event.getMatrices(), box, Sydney.MODULE_MANAGER.getModule(RendersModule.class).getColor(autoCrystalModule.mode.getValue(), autoCrystalModule.fillColorUp.getColor(), scale), Sydney.MODULE_MANAGER.getModule(RendersModule.class).getColor(autoCrystalModule.mode.getValue(), autoCrystalModule.fillColorDown.getColor(), scale));
        if (autoCrystalModule.renderMode.getValue().equalsIgnoreCase("Outline") || autoCrystalModule.renderMode.getValue().equalsIgnoreCase("Both"))
            Renderer3D.renderGradientBoxOutline(event.getMatrices(), box, Sydney.MODULE_MANAGER.getModule(RendersModule.class).getColor(autoCrystalModule.mode.getValue(), autoCrystalModule.outlineColorUp.getColor(), scale), Sydney.MODULE_MANAGER.getModule(RendersModule.class).getColor(autoCrystalModule.mode.getValue(), autoCrystalModule.outlineColorDown.getColor(), scale));
    }

    @SubscribeEvent
    public void onRenderWorld$autoCrystalExtra(RenderWorldEvent.Post event) {
        if (mc.player == null || mc.world == null) return;
        if (crystalTarget == null || crystalTarget.getPosition() == null) return;
        if (crystalTarget.getTarget() != 1) return;

        MatrixStack matrices = event.getMatrices();
        AutoCrystalModule module = Sydney.MODULE_MANAGER.getModule(AutoCrystalModule.class);

        Vec3d vec3d = new Vec3d(crystalTarget.getPosition().toCenterPos().x - mc.getEntityRenderDispatcher().camera.getPos().x, crystalTarget.getPosition().toCenterPos().y - mc.getEntityRenderDispatcher().camera.getPos().y, crystalTarget.getPosition().toCenterPos().z - mc.getEntityRenderDispatcher().camera.getPos().z);
        if(module.animationMode.getValue().equals("Slide")) vec3d = new Vec3d(renderPosition.x + 0.5 - mc.getEntityRenderDispatcher().camera.getPos().x, renderPosition.y + 0.5 - mc.getEntityRenderDispatcher().camera.getPos().y, renderPosition.z + 0.5 - mc.getEntityRenderDispatcher().camera.getPos().z);

        if (module.icon.getValue()) {
            float scaling = module.iconScale.getValue().floatValue() / 100.0f;

            matrices.push();
            matrices.translate(vec3d.x, vec3d.y, vec3d.z);
            matrices.multiply(mc.getEntityRenderDispatcher().getRotation());
            matrices.scale(scaling, -scaling, scaling);

            Renderer2D.renderCircle(matrices, 0, 0, 12.f, new Color(0, 0, 0, 100));
            Renderer2D.renderCircle(matrices, 0, 0, 12.0f - module.iconRadius.getValue().floatValue(), module.iconColor.getColor());

            if (module.renderDamage.getValue()) {
                Renderer2D.renderTexture(matrices, -5.5f, -8.5f, 5.5f, 2.5f, Identifier.of(Sydney.MOD_ID, "textures/crystal.png"), Color.WHITE);

                matrices.push();
                matrices.scale(0.45f, 0.45f, 0.45f);

                String text = module.getCalculationDamage();
                Sydney.FONT_MANAGER.drawTextWithShadow(matrices, text, -Sydney.FONT_MANAGER.getWidth(text) / 2 - 1, 7, mc.getBufferBuilders().getEntityVertexConsumers(), Color.WHITE);

                matrices.pop();
            } else {
                Renderer2D.renderTexture(matrices, -6.5f, -6.5f, 6.5f, 6.5f, Identifier.of(Sydney.MOD_ID, "textures/crystal.png"), Color.WHITE);
            }

            matrices.pop();
        } else {
            if (module.renderDamage.getValue()) {
                matrices.push();
                matrices.translate(vec3d.x, vec3d.y, vec3d.z);
                matrices.multiply(mc.getEntityRenderDispatcher().getRotation());
                matrices.scale(0.025f, -0.025f, 0.025f);

                String text = module.getCalculationDamage();
                Sydney.FONT_MANAGER.drawTextWithShadow(matrices, text, -Sydney.FONT_MANAGER.getWidth(text) / 2, -Sydney.FONT_MANAGER.getHeight() / 2, mc.getBufferBuilders().getEntityVertexConsumers(), Color.WHITE);

                matrices.pop();
            }
        }
    }

    public void setRenderPosition(BlockPos position) {
        if (!Sydney.MODULE_MANAGER.getModule(AutoCrystalModule.class).isToggled()) position = null;

        if (position == null) {
            if (crystalTarget != null) {
                if (crystalTarget.getTarget() != 0) {
                    crystalTarget.setTarget(0);
                    crystalTarget.setTime(System.currentTimeMillis());
                }
            } else {
                crystalTarget = new Target(null, 0, System.currentTimeMillis());
            }
        } else {
            if (crystalTarget == null || crystalTarget.getTarget() == 0) {
                crystalTarget = new Target(position, 1, System.currentTimeMillis());
            } else {
                crystalTarget.setPosition(position);
            }
        }
    }

    @AllArgsConstructor @Getter @Setter
    public static class Target {
        private BlockPos position;
        private int target;
        private long time;
    }
}
