package edu.feicui.assistant.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ִ��Linux������
 * 
 * @author Sogrey
 * 
 */
public final class ShellExcuter {

	public final static String exec(String... cmds) {
		String result = "";
		DataInputStream dis = null;
		// ��ȡLinux�����л�����ִ�����������Ĺ���
		Runtime runtime = Runtime.getRuntime();
		try {
			// ��ó����û�Ȩ��
			runtime.exec("su");
			// ���������в�����������Ӧ����
			Process process = runtime.exec(cmds);
			// ���������൱�����ǳ������룬��������
			InputStream is = process.getInputStream();
			// ���ֽ����������з�װ���Ա����ɿɶ��ַ���
			dis = new DataInputStream(is);
			// �����ɵ��ַ���
			result = dis.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// is��Process��������Ҫ����ȥ�ر�
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
}
