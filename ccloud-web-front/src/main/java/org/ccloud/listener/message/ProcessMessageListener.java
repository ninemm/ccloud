package org.ccloud.listener.message;

import org.ccloud.message.Actions;
import org.ccloud.message.Message;
import org.ccloud.message.MessageListener;
import org.ccloud.message.annotation.Listener;

@Listener(action = Actions.ProcessMessage.PROCESS_MESSAGE_SAVE)
public class ProcessMessageListener implements MessageListener {

	@Override
	public void onMessage(Message message) {

		Object obj = message.getData();
		
		if (obj instanceof org.ccloud.model.Message) {
			org.ccloud.model.Message pmessage = (org.ccloud.model.Message) obj;
			pmessage.saveOrUpdate();
		}
		
	}

}
