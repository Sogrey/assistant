package edu.feicui.assistant.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 执行Linux命令行
 * 
 * @author Sogrey
 * 
 */
public final class ShellExcuter {

	public final static String exec(String... cmds) {
		String result = "";
		DataInputStream dis = null;
		// 获取Linux命令行环境，执行虚拟机以外的功能
		Runtime runtime = Runtime.getRuntime();
		try {
			// 获得超级用户权限
			runtime.exec("su");
			// 传递命令行参数，生成相应进程
			Process process = runtime.exec(cmds);
			// 命令的输出相当于我们程序输入，用输入流
			InputStream is = process.getInputStream();
			// 对字节输入流进行封装，以便生成可读字符串
			dis = new DataInputStream(is);
			// 读生成的字符串
			result = dis.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// is由Process管理，不需要我们去关闭
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
