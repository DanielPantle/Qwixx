package de.pantle.qwixx.utils;

import de.pantle.qwixx.networking.Message;

public interface ServerListener extends GameListener {
	void onMoveFinished(Message message);
}
