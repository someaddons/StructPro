package com.ternsip.structpro.universe.general.network;

import com.ternsip.structpro.universe.utils.Report;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.io.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Packet implements IMessage, Serializable {

    private Type type;
    private Object data;

    @Override
    public void fromBytes(ByteBuf buf) {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
        try (ObjectInputStream in = new ObjectInputStream(byteIn)) {
            Packet packet = (Packet) in.readObject();
            setType(packet.getType());
            setData(packet.getData());
        } catch (Throwable e) {
            new Report().post("PACKET ERROR", e.getMessage()).print();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(byteOut)) {
            out.writeObject(this);
            buf.writeBytes(byteOut.toByteArray());
        } catch (Throwable e) {
            new Report().post("PACKET ERROR", e.getMessage()).print();
        }
    }

    public enum Type implements Serializable {
        RELOAD_CHUNKS,
        UNKNOWN
    }

}
