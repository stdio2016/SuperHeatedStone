package com.stdio2016.superheatedstone;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Created by User on 2017/8/27.
 */
public class VaporMessage implements IMessage {
    public Vec3d pos;
    private boolean isValid;
    public VaporMessage() {
        isValid = false;
    }
    public VaporMessage(double x, double y, double z) {
        isValid = true;
        pos = new Vec3d(x, y, z);
    }
    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            double x,y,z;
            x = buf.readDouble();
            y = buf.readDouble();
            z = buf.readDouble();
            pos = new Vec3d(x,y,z);
            isValid = true;
        }
        catch (IndexOutOfBoundsException x) {
            System.err.println("VaporMessage is invalid");
        }
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        if (!isValid) return ;
        byteBuf.writeDouble(pos.xCoord);
        byteBuf.writeDouble(pos.yCoord);
        byteBuf.writeDouble(pos.zCoord);
    }
}
