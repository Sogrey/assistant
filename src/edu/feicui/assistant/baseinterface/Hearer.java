package edu.feicui.assistant.baseinterface;

import android.content.Intent;
import edu.feicui.assistant.receiver.BaseReceiver;

/** ���ڽӿ� */
public interface Hearer {

	public void update(BaseReceiver receiver, Intent data);
}
