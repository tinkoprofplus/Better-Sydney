package me.aidan.sydney.modules.impl.player;

import lombok.Getter;
import lombok.Setter;
import me.aidan.sydney.Sydney;
import me.aidan.sydney.events.SubscribeEvent;
import me.aidan.sydney.events.impl.*;
import me.aidan.sydney.modules.Module;
import me.aidan.sydney.modules.RegisterModule;
import me.aidan.sydney.settings.impl.*;
import me.aidan.sydney.utils.color.ColorUtils;
import me.aidan.sydney.utils.graphics.Renderer3D;
import me.aidan.sydney.utils.minecraft.HoleUtils;
import me.aidan.sydney.utils.minecraft.InventoryUtils;
import me.aidan.sydney.utils.minecraft.WorldUtils;
import me.aidan.sydney.utils.rotations.RotationUtils;
import me.aidan.sydney.utils.system.Timer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.GameMode;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RegisterModule(name = "SpeedMine", description = "Automatically mines blocks at a faster speed using packets.", category = Module.Category.PLAYER)
public class SpeedMineModule extends Module {
    public ModeSetting switchMode = new ModeSetting("Switch", "The mode that will be used for automatically switching to the fastest item.", "Silent", InventoryUtils.SWITCH_MODES);
    public NumberSetting range = new NumberSetting("Range", "The maximum distance at which blocks will be mined.", 6.0, 0.0, 8.0);
    public NumberSetting speed = new NumberSetting("Speed", "The speed at which the module will mine blocks.", 1.0, 0.7, 1.0);
    public ModeSetting rotate = new ModeSetting("Rotate", "Automatically rotates to the block when mining it.", "Packet", new String[]{"None", "Normal", "Packet"});

    public BooleanSetting auto = new BooleanSetting("Auto", "Automatically mines blocks deemed optimal for defeating your opponents.", false);
    public BooleanSetting cityOnly = new BooleanSetting("CityOnly", "Only mines the target's city positions.", new BooleanSetting.Visibility(auto, true), false);
    public BooleanSetting holeCheck = new BooleanSetting("HoleCheck", "Only mine the player in hole.", new BooleanSetting.Visibility(auto, true), false);
    public BooleanSetting switchReset = new BooleanSetting("SwitchReset", "Resets the mining when switching slots.", new ModeSetting.Visibility(switchMode, "None", "AltSwap", "AltPickup"), true);
    public BooleanSetting doubleMine = new BooleanSetting("Double", "Allows the mining of 2 blocks at the same time.", false);
    public ModeSetting sequence = new ModeSetting("Sequence", "Sequence of mining for double mine", new BooleanSetting.Visibility(doubleMine, true), "Surround", new String[]{"Surround", "Phase"});
    public BooleanSetting instant = new BooleanSetting("Instant", "Instantly mines blocks once they have been replaced.", false);
    public NumberSetting instantDelay = new NumberSetting("InstantDelay", "The amount of time that has to pass before instantly mining blocks.", new BooleanSetting.Visibility(instant, true), 0, 0, 20);
    public NumberSetting instantTimeout = new NumberSetting("InstantTimeout", "The amount of time that cancel instantly mine while no block to mine.", new BooleanSetting.Visibility(instant, true), 60, 0, 100);
    public BooleanSetting grim = new BooleanSetting("Grim", "Adds a bypass catered to the Grim anticheat.", false);
    public BooleanSetting strict = new BooleanSetting("Strict", "Waits for the server to tick you before switching back.", false);
    public BooleanSetting whileEating = new BooleanSetting("WhileEating", "Mines blocks while eating.", true);
    public WhitelistSetting whitelist = new WhitelistSetting("Whitelist", "Mines only the blocks that are on this list. If empty, every block will be mined.", WhitelistSetting.Type.BLOCKS);

    public CategorySetting renderCategory = new CategorySetting("Render", "The category containing all settings related to rendering.");
    public ModeSetting render = new ModeSetting("Render", "Mode", "The rendering that will be applied to the blocks highlighted.", new CategorySetting.Visibility(renderCategory), "Both", new String[]{"None", "Fill", "Outline", "Both"});
    public ModeSetting animation = new ModeSetting("Animation", "The animation that will be used when rendering the block mining progress.", new ModeSetting.Visibility(render, "Fill", "Outline", "Both"), "Expand", new String[]{"None", "Expand", "Rise"});
    public ModeSetting color = new ModeSetting("Color", "The color that will be used when rendering the block mining.", new ModeSetting.Visibility(render, "Fill", "Outline", "Both"), "Smooth", new String[]{"Static", "Smooth", "Custom"});
    public ColorSetting fillColor = new ColorSetting("FillColor", "The color used for the fill rendering.", new ModeSetting.Visibility(render, "Fill", "Both"), ColorUtils.getDefaultFillColor());
    public ColorSetting outlineColor = new ColorSetting("OutlineColor", "The color used for the outline rendering.", new ModeSetting.Visibility(render, "Outline", "Both"), ColorUtils.getDefaultOutlineColor());
    public ModeSetting instantRender = new ModeSetting("InstantRender", "Instant", "The color that will be used for rendering instantly mined blocks.", new CategorySetting.Visibility(renderCategory), "None", new String[]{"None", "Default", "Custom"});
    public ColorSetting instantColor = new ColorSetting("InstantColor", "The custom color used for instantly mined blocks.", new ModeSetting.Visibility(instantRender, "Custom"), new ColorSetting.Color(new Color(148, 0, 211), false, false));

    @Getter private Action primary = null;
    @Getter private Action secondary = null;

    private SwitchAction switchAction = null;

    private final Timer instantTimer = new Timer();
    private final Timer mineTimer = new Timer();

    @SubscribeEvent
    public void onPlayerUpdate(PlayerUpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (doubleMine.getValue() && secondary != null && secondary.process()) secondary = null;
        if (primary != null && primary.process()) primary = null;

        if (!auto.getValue()) return;
        if ((primary != null && primary.getPriority() > 0 && !WorldUtils.isReplaceable(primary.getPosition())) || (secondary != null && secondary.getPriority() > 0 && !WorldUtils.isReplaceable(secondary.getPosition())))
            return;

        Target target = getTarget();

        if (doubleMine.getValue()) {
            if (!mineTimer.hasTimeElapsed(350L)) return;

            if (mc.player.isCrawling()) {
                BlockPos position;
                BlockPos playerPosition = mc.player.getBlockPos();

                if (WorldUtils.canBreak(playerPosition.down()) && !WorldUtils.isReplaceable(playerPosition.down()) && (!WorldUtils.isReplaceable(playerPosition.down(2)) || HoleUtils.getSingleHole(playerPosition.down(2), 1, false) != null)) {
                    position = playerPosition.down();
                } else {
                    position = playerPosition.up();
                }

                if (isValid(position) && !isOutOfRange(position)) {
                    if (!isInvalid(position)) handle(position, 0);
                    return;
                }
            }

            if ((primary != null && primary.isInstantMine() && !instantTimer.hasTimeElapsed(instantTimeout.getValue().longValue() * 50L) && primary.getAttempts() != 0) || secondary != null) return;

            if (target != null) {
                Runnable inside = () -> {
                    List<BlockPos> insidePositions = HoleUtils.getInsidePositions(target.player()).stream().filter(insidePosition -> !mc.world.getBlockState(insidePosition).isReplaceable()).toList();;
                    for (BlockPos position : insidePositions) {
                        if (primary != null && secondary != null) break;
                        if (isInvalid(position) || isOutOfRange(position)) continue;
                        handle(position, 0);
                    }
                };
                Runnable outside = () -> {
                    List<BlockPos> surroundPositions = HoleUtils.getFeetPositions(target.player(), true, false, true).stream().filter(pos -> !mc.world.getBlockState(pos).isReplaceable()).toList();
                    if (HoleUtils.isPlayerInHole(target.player()) || !holeCheck.getValue()) {
                        for (BlockPos position : surroundPositions) {
                            if (primary != null && secondary != null) break;
                            if (isMining(position)) continue;
                            if (isInvalid(position) || isOutOfRange(position)) continue;
                            handle(position, 0);
                        }
                    }
                };
                if (sequence.getValue().equals("Surround")) {
                    outside.run();
                    inside.run();
                } else if (sequence.getValue().equals("Phase")) {
                    inside.run();
                    outside.run();
                }
            }
        } else {
            BlockPos position = null;

            if (target == null) {
                return;
            } else {
                if (!WorldUtils.isReplaceable(target.player.getBlockPos()) && !WorldUtils.getBlock(target.player().getBlockPos()).equals(Blocks.COBWEB)) {
                    position = target.player().getBlockPos();
                } else if (HoleUtils.isPlayerInHole(target.player()) || !holeCheck.getValue()) {
                    position = target.position();
                }
            }
            if (position == null) return;
            if (primary != null && position.equals(primary.getPosition()))
                return;

            handle(position, 0);
        }
    }

    @SubscribeEvent(priority = Integer.MAX_VALUE)
    public void onTick(TickEvent event) {
        if (switchAction == null) return;
        if (System.currentTimeMillis() - switchAction.time() < 100L)
            return;

        if (mc.player != null && mc.world != null && (switchAction.slot() != -1 && switchAction.previousSlot() != -1)) {
            InventoryUtils.switchBack(switchMode.getValue(), switchAction.slot(), switchAction.previousSlot());
        }

        switchAction = null;
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (doubleMine.getValue() && secondary != null) secondary.render(event.getMatrices());
        if (primary != null) primary.render(event.getMatrices());
    }

    @SubscribeEvent
    public void onPacketSend(PacketSendEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket && switchReset.getValue() && (switchMode.getValue().equalsIgnoreCase("AltSwap") || switchMode.getValue().equalsIgnoreCase("AltPickup"))) {
            if (secondary != null) {
                secondary.cancel();
                secondary.start();
            }

            if (primary != null) {
                primary.cancel();
                primary.start();
            }
        }
    }

    @SubscribeEvent
    public void onAttackBlock(AttackBlockEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (handle(event.getPosition(), 1)) {
            event.setCancelled(true);
        }
    }

    @Override
    public String getMetaData() {
        String primaryProgress = primary == null ? "0.0" : new DecimalFormat("0.0").format(primary.getProgress() / primary.getSpeed());
        String secondaryProgress = secondary == null || !doubleMine.getValue() ? "" : ", " + new DecimalFormat("0.0").format(secondary.getProgress() / secondary.getSpeed());
        return primaryProgress + secondaryProgress;
    }

    private boolean handle(BlockPos position, int priority) {
        if (mc.interactionManager.getCurrentGameMode() == GameMode.CREATIVE || mc.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) return false;
        if (mc.world.getBlockState(position).getBlock().getHardness() == -1) return false;
        if (!whitelist.getWhitelist().isEmpty() && !whitelist.isWhitelistContains(mc.world.getBlockState(position).getBlock())) return false;
        if (mc.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(position)) > MathHelper.square(range.getValue().doubleValue()))
            return false;

        if ((primary != null && primary.getPosition().equals(position)) || (secondary != null && secondary.getPosition().equals(position))) return true;

        if (doubleMine.getValue()) {
            if (secondary != null) {
                primary = new Action(position, priority);
            } else {
                if (primary != null) {
                    if (!primary.isInstantMine()) secondary = primary;
                    primary = new Action(position, priority);
                } else {
                    primary = new Action(position, priority);
                }
            }
        } else {
            if (primary != null) primary.cancel();
            primary = new Action(position, priority);
        }

        return true;
    }

    private boolean isInvalid(BlockPos position) {
        if (!isValid(position)) return true;
        return isMining(position);
    }

    private boolean isValid(BlockPos position) {
        if (position == null) return false;
        if (mc.world.getBlockState(position).getBlock().getHardness() == -1) return false;
        return !mc.world.getBlockState(position).getBlock().equals(Blocks.COBWEB);
    }

    private boolean isMining(BlockPos position) {
        if (position == null) return true;
        if (primary != null && primary.getPosition().equals(position)) return true;
        return secondary != null && secondary.getPosition().equals(position);
    }

    private boolean isOutOfRange(BlockPos position) {
        if (position == null) return true;
        return mc.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(position)) > MathHelper.square(range.getValue().doubleValue());
    }

    private Target getTarget() {
        Target optimalTarget = null;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            if (!player.isAlive() || player.getHealth() <= 0.0f) continue;
            if (mc.player.squaredDistanceTo(player) > MathHelper.square(range.getValue().doubleValue() + 2.0)) continue;
            if (Sydney.FRIEND_MANAGER.contains(player.getName().getString())) continue;

            List<Position> feetPositions = getPositions(player);
            BlockPos position = getTargetPosition(feetPositions);

            if (!doubleMine.getValue()) {
                if (feetPositions.isEmpty()) continue;
                if (position == null) continue;
            }

            if (optimalTarget == null) {
                optimalTarget = new Target(player, feetPositions, position);
                continue;
            }

            if (mc.player.squaredDistanceTo(player) < mc.player.squaredDistanceTo(optimalTarget.player())) {
                optimalTarget = new Target(player, feetPositions, position);
            }
        }

        return optimalTarget;
    }

    private BlockPos getTargetPosition(List<Position> positions) {
        BlockPos optimalPosition = null;
        double optimalScore = 0.0;

        for (Position position : positions) {
            if ((doubleMine.getValue() || cityOnly.getValue()) && !position.feetPosition()) continue;
            if (!isValidPosition(position.position())) continue;
            if (HoleUtils.isPlayerInHole(mc.player) && HoleUtils.getFeetPositions(mc.player, true, false, true).contains(position.position())) continue;

            double score = 0.0;

            if (position.feetPosition()) {
                score += 0.05;

                if (mc.world.getBlockState(position.position()).getBlock() == Blocks.ENDER_CHEST) score += 0.95;
                else if (WorldUtils.isCrystalPlaceable(position.position().add(0, 1, 0))) score += 0.35;
                if (hasCityPosition(position.position())) score += 0.6;
            } else {
                if (mc.world.getBlockState(position.position()).getBlock() == Blocks.ENDER_CHEST) {
                    score -= 2.0;
                } else {
                    if (WorldUtils.isCrystalPlaceable(position.position().add(0, 1, 0))) score += 0.75;
                    else score -= 2.0;
                }
            }

            if (score >= optimalScore) {
                optimalPosition = position.position();
                optimalScore = score;
            }
        }

        return optimalPosition;
    }

    private List<Position> getPositions(PlayerEntity player) {
        List<Position> positions = new ArrayList<>();

        for (BlockPos position : HoleUtils.getFeetPositions(player, true, false, true)) {
            positions.add(new Position(position, true));
            if (!doubleMine.getValue()) positions.add(new Position(position.add(0, 1, 0), false));
        }

        if (!doubleMine.getValue()) positions.add(new Position(player.getBlockPos().add(0, 2, 0), false));
        return positions;
    }

    private boolean isValidPosition(BlockPos position) {
        if (mc.world.getBlockState(position).isReplaceable()) return false;
        if (mc.world.getBlockState(position).getBlock().getHardness() == -1) return false;
        return !isOutOfRange(position);
    }

    private boolean hasCityPosition(BlockPos position) {
        Vec3i[] offsets = new Vec3i[]{new Vec3i(1, 0, 0), new Vec3i(-1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(0, 0, -1)};

        for (Vec3i vec3i : offsets) {
            BlockPos offsetPosition = position.add(vec3i);
            if (WorldUtils.isPlaceable(offsetPosition)) return true;
        }

        return false;
    }

    @Getter
    public class Action {
        private final BlockPos position;
        private BlockState state;
        private final int priority;

        @Setter private float progress;
        private float prevProgress;
        private int attempts;
        private boolean mining;

        private boolean instantMine;

        public Action(BlockPos position, int priority) {
            this.position = position;
            this.state = mc.world.getBlockState(position);
            this.priority = priority;

            start();
        }

        public boolean process() {
            if (isOutOfRange(position)) {
                cancel();
                return true;
            }

            boolean secondary = getSecondary() != null && position.equals(getSecondary().getPosition());
            if (secondary) instantMine = false;

            if (secondary && mc.world.getBlockState(position).isReplaceable()) {
                cancel();
                return true;
            }

            Direction direction = WorldUtils.getClosestDirection(position, true);
            BlockState state = mc.world.getBlockState(position);

            if (!state.isReplaceable() && state.getBlock() != this.state.getBlock()) {
                this.state = state;
            }

            if (mining) {
                int slot = switchMode.getValue().equalsIgnoreCase("None") ? -1 : InventoryUtils.findFastestItem(this.state, InventoryUtils.HOTBAR_START, switchMode.getValue().equalsIgnoreCase("AltSwap") || switchMode.getValue().equalsIgnoreCase("AltPickup") ? InventoryUtils.INVENTORY_END : InventoryUtils.HOTBAR_END);
                if (slot == -1) slot = mc.player.getInventory().selectedSlot;

                float delta = WorldUtils.getMineSpeed(this.state, slot) / Sydney.WORLD_MANAGER.getTimerMultiplier();

                prevProgress = progress;
                progress = MathHelper.clamp(progress + delta, 0.0f, getSpeed());

                if (rotate.getValue().equalsIgnoreCase("Normal") && progress + (delta * 2) >= getSpeed()) {
                    Sydney.ROTATION_MANAGER.rotate(RotationUtils.getRotations(WorldUtils.getHitVector(position, direction)), Sydney.ROTATION_MANAGER.getModulePriority(Sydney.MODULE_MANAGER.getModule(SpeedMineModule.class)));
                }

                if (progress >= getSpeed() && !state.isReplaceable() && (whileEating.getValue() || !mc.player.isUsingItem())) {
                    if (!instantMine || instantTimer.hasTimeElapsed(instantDelay.getValue().longValue() * 50L)) {
                        Sydney.EVENT_HANDLER.post(new DestroyBlockEvent(position));

                        if (rotate.getValue().equalsIgnoreCase("Packet")) Sydney.ROTATION_MANAGER.packetRotate(RotationUtils.getRotations(WorldUtils.getHitVector(position, direction)));

                        int previousSlot = mc.player.getInventory().selectedSlot;
                        InventoryUtils.switchSlot(switchMode.getValue(), slot, previousSlot);

                        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, position, direction));
                        if (grim.getValue()) mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, position.up(500), direction));
                        mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));

                        if (strict.getValue() || (doubleMine.getValue() && secondary)) switchAction = new SwitchAction(slot, previousSlot, System.currentTimeMillis());
                        else if (switchAction == null) InventoryUtils.switchBack(switchMode.getValue(), slot, previousSlot);

                        if (!instantMine || secondary) mineTimer.reset();
                    }

                    attempts++;
                    if (!secondary) {
                        if (!instant.getValue()) {
                            start();
                        } else {
                            this.instantMine = true;
                            instantTimer.reset();
                        }
                    }

                    return doubleMine.getValue() && secondary;
                }
            } else {
                start();
            }

            return false;
        }

        public void render(MatrixStack matrices) {
            if (mc.world.getBlockState(position).isReplaceable() && (!instantMine || instantRender.getValue().equalsIgnoreCase("None")))
                return;

            Box box = new Box(position);
            double progress = MathHelper.lerp(mc.getRenderTickCounter().getTickDelta(false), prevProgress / getSpeed(), this.progress / getSpeed());

            if (animation.getValue().equalsIgnoreCase("Expand")) box = new Box(position).contract(0.5).expand(MathHelper.clamp(progress / 2.0, 0.0, 0.5));
            if (animation.getValue().equalsIgnoreCase("Rise")) box = new Box(position.getX(), position.getY(), position.getZ(), position.getX() + 1.0, position.getY() + progress, position.getZ() + 1.0);

            Color fill = fillColor.getColor();
            Color outline = outlineColor.getColor();

            if (color.getValue().equalsIgnoreCase("Static")) {
                fill = progress >= 0.9 ? new Color(0, 255, 0, fillColor.getAlpha()) : new Color(255, 0, 0, fillColor.getAlpha());
                outline = progress >= 0.9 ? new Color(0, 255, 0, outlineColor.getAlpha()) : new Color(255, 0, 0, outlineColor.getAlpha());
            } else if (color.getValue().equalsIgnoreCase("Smooth")) {
                fill = new Color(255 - (int) (MathHelper.clamp(progress, 0.0f, 1.0f) * 255), (int) (MathHelper.clamp(progress, 0.0f, 1.0f) * 255), 0, fillColor.getAlpha());
                outline = new Color(255 - (int) (MathHelper.clamp(progress, 0.0f, 1.0f) * 255), (int) (MathHelper.clamp(progress, 0.0f, 1.0f) * 255), 0, outlineColor.getAlpha());
            }

            if (progress >= getSpeed() && instantMine && instantRender.getValue().equalsIgnoreCase("Custom")) {
                fill = ColorUtils.getColor(instantColor.getColor(), fillColor.getAlpha());
                outline = ColorUtils.getColor(instantColor.getColor(), outlineColor.getAlpha());
            }

            if (render.getValue().equalsIgnoreCase("Fill") || render.getValue().equalsIgnoreCase("Both")) Renderer3D.renderBox(matrices, box, fill);
            if (render.getValue().equalsIgnoreCase("Outline") || render.getValue().equalsIgnoreCase("Both")) Renderer3D.renderBoxOutline(matrices, box, outline);
        }

        public void start() {
            Direction direction = WorldUtils.getClosestDirection(position, true);
            if (doubleMine.getValue()) {
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, position, direction));
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, position, direction));
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, position, direction));
            } else {
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, position, direction));
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, position, direction));
            }

            mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));

            this.progress = 0.0f;
            this.prevProgress = 0.0f;
            this.attempts = 0;
            this.mining = true;

            this.instantMine = false;
        }

        public void cancel() {
            if (!doubleMine.getValue()) {
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, position, WorldUtils.getClosestDirection(position, true)));
                mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            }

            this.progress = 0.0f;
            this.prevProgress = 0.0f;
            this.attempts = 0;
            this.mining = false;

            this.instantMine = false;
        }

        private float getSpeed() {
            return getSecondary() != null && position.equals(getSecondary().getPosition()) ? 1.0f : speed.getValue().floatValue();
        }
    }

    private record SwitchAction(int slot, int previousSlot, long time) { }

    private record Target(PlayerEntity player, java.util.List<Position> feetPositions, BlockPos position) { }
    private record Position(BlockPos position, boolean feetPosition) { }
}
