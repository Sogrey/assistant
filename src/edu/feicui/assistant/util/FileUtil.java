package edu.feicui.assistant.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * �ļ�����
 * 
 * @author Sogrey
 * 
 */
public final class FileUtil {
	/** �������С */
	public static final int BUFFERSIZE = 1024;

	/**
	 * ���ļ���src���Ƶ�dst<br>
	 * 
	 * @param src
	 *            Դ�ļ�
	 * @param dst
	 *            Ŀ���ļ�
	 * */
	public static boolean copyFile(File src, File dst) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			byte[] buffer = new byte[BUFFERSIZE];
			fis = new FileInputStream(src);
			fos = new FileOutputStream(dst);
			int len = 0;// ʵ�ʶ�ȡ��С
			/**
			 * fis.read(byte[] buffer, int byteOffset, int byteCount)
			 * 
			 * @param buffer
			 *            ���棨���ζ�д���ݣ�
			 * @param byteOffset
			 *            ƫ�������Զ��Ƶ��ϴζ�����λ�ã�
			 * @param byteCount
			 *            ����������
			 * @return ʵ�ʶ�ȡ��С
			 * */
			try {
				// while �������ж���û�ж����ļ��������ˣ�д�룬û������д
				while ((len = fis.read(buffer, 0, buffer.length)) > 0) {
					fos.write(buffer);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * ���ı��ļ�
	 * 
	 * @param path
	 *            �ļ�·��
	 * @return �ļ�����
	 * */
	public static String read(String path) {
		String str = null;
		FileReader fr = null;
		char[] buffer = new char[BUFFERSIZE];
		StringBuilder sb = new StringBuilder();
		try {
			fr = new FileReader(path);
			int count;
			// read ���ַ�������������ʵ����
			while ((count = fr.read(buffer)) > 0) {
				sb.append(buffer, 0, count);
			}
			str = sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (Exception e) {
				}
			}
		}
		return str;
	}

	/**
	 * д�����ļ�
	 * 
	 * @param output
	 *            ��Ҫд�����ļ����ַ���
	 * */
	public static void write(String output) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(Constants.INFO_PATH);
			byte[] bytes = output.getBytes();
			fos.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
