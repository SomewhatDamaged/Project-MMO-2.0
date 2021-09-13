package harmonised.pmmo.network;

import harmonised.pmmo.proxy.ClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageTrigger
{
    int type;

    public MessageTrigger( int type )
    {
        this.type = type;
    }

    public MessageTrigger()
    {
    }

    public static MessageTrigger decode( FriendlyByteBuf buf )
    {
        MessageTrigger packet = new MessageTrigger();

        packet.type = buf.readInt();

        return packet;
    }

    public static void encode( MessageTrigger packet, FriendlyByteBuf buf )
    {
        buf.writeInt( packet.type );
    }

    public static void handlePacket( MessageTrigger packet, Supplier<NetworkEvent.Context> ctx )
    {
        ctx.get().enqueueWork(() ->
        {
            if( Minecraft.getInstance().player != null )
            {
                if( packet.type == 1 )
                    ClientHandler.openInfoMenu();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
