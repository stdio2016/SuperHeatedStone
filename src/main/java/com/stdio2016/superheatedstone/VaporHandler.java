package com.stdio2016.superheatedstone;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by User on 2017/8/27.
 * This handles vapors that liquid stones generate
 */
public class VaporHandler implements IMessageHandler<VaporMessage, IMessage> {
    public static class dummy extends VaporHandler {
        @Override
        public IMessage onMessage(final VaporMessage vaporMessage, MessageContext messageContext) {
            return null;
        }
    }

    @Override
    public IMessage onMessage(final VaporMessage vaporMessage, MessageContext messageContext) {
        Minecraft mc = Minecraft.getMinecraft();
        final WorldClient client = mc.world;
        mc.addScheduledTask(new Runnable()
        {
            public void run() {
                processMessage(client, vaporMessage);
            }
        });
        return null;
    }

    private void processMessage(WorldClient world, VaporMessage msg) {
        Vec3d pos = msg.pos;
        for (int i = 0; i < 10; i++)
            world.spawnParticle(EnumParticleTypes.LAVA, pos.x + .5f, pos.y + .5f, pos.z + .5f, 0f, 0f, 0f);
    }
}
