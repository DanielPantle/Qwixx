package de.pantle.qwixx.networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

/**
 * Created by Daniel on 19.10.2017.
 */

public class Message {
	public enum MessageType {
		START_GAME
	}
	
	private MessageType type;
	private Object object;
	
	public Message() {
	
	}
	
	public Message(MessageType type) {
		this.type = type;
		this.object = null;
	}
	
	public Message(MessageType type, Object object) {
		this.type = type;
		this.object = object;
	}
	
	public MessageType getType() {
		return type;
	}
	
	public Object getObject() {
		return object;
	}
	
	public static void registerClasses(Server server) {
		registerClasses(server.getKryo());
	}
	public static void registerClasses(Client client) {
		registerClasses(client.getKryo());
	}
	
	private static void registerClasses(Kryo kryo) {
		kryo.register(Message.class);
		kryo.register(MessageType.class);
	}
}
