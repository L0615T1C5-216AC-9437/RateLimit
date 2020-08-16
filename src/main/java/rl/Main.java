package rl;

import arc.Events;
import arc.util.Log;
import arc.util.Time;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.RemoteReadServer;
import mindustry.net.Packets;
import mindustry.net.ValidateException;
import mindustry.plugin.Plugin;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static mindustry.Vars.*;

public class Main extends Plugin {
    public static HashMap<String, Long> messageBlockRL = new HashMap<>();
    public static ConcurrentHashMap<String, AtomicInteger> packet38RL = new ConcurrentHashMap<>(); //rate limit for editing blocks
    public static Thread cycle;

    public Main() {
        Events.on(EventType.ServerLoadEvent.class, serverLoadEvent -> {
            cycle c = new cycle(Thread.currentThread());
            c.setDaemon(false);
            c.start();
        });
        Events.on(EventType.ServerLoadEvent.class, event -> {
            net.handleServer(Packets.InvokePacket.class, (con, packet) -> {
                if(con.player == null) return;
                try{
                    if (packet.type == 38) {
                        packet38RL.putIfAbsent(con.address, new AtomicInteger(0));
                        if (packet38RL.get(con.address).incrementAndGet() > 100) {
                            con.kick("Auto Moderator - Griefing (p38)");
                            Call.sendMessage("[#"+con.player.color+"]"+con.player.name+" [white]Griefed! Mass drivers, sorters and bridges may be affected.");
                        }
                    }
                    if (packet.type == 57) {
                        if (messageBlockRL.containsKey(con.address)) {
                            if (messageBlockRL.get(con.address) > Time.millis()) {
                                Call.onKick(con, "Auto Moderator - Message Block Rate Limit");
                            } else {
                                messageBlockRL.put(con.address, Time.millis()+500);
                            }
                        } else {
                            messageBlockRL.put(con.address, Time.millis()+500);
                        }
                    }

                    RemoteReadServer.readPacket(packet.writeBuffer, packet.type, con.player);
                }catch(ValidateException e){
                    Log.debug("Validation failed for '{0}': {1}", e.player, e.getMessage());
                }catch(RuntimeException e){
                    if(e.getCause() instanceof ValidateException){
                        ValidateException v = (ValidateException)e.getCause();
                        Log.debug("Validation failed for '{0}': {1}", v.player, v.getMessage());
                    }else{
                        throw e;
                    }
                }
            });
        });
        Events.on(EventType.PlayerLeave.class, event -> {
            messageBlockRL.remove(event.player.con.address);
        });
    }
}