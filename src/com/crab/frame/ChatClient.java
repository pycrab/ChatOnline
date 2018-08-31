package com.crab.frame;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/** 
* @author: pycrab
* @Date: 2018��8��30�� ����8:47:42 
*/
public class ChatClient extends Frame{

	private static final long serialVersionUID = 1L;
	private TextField tField = new TextField();
	private TextArea textArea = new TextArea();
	private Socket socket = null;
	private DataOutputStream dos = null;
	private DataInputStream dis = null;

	Thread reciveThread = new Thread(new Reciver());
	public static void main(String[] args) {
		
		new ChatClient().launchFrame();
	}

	// ���������
	private void launchFrame() {
		setLocation(400, 300);
		this.setSize(300, 300);
		add(tField, BorderLayout.SOUTH);
		add(textArea, BorderLayout.NORTH);
		pack();
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				disConnect();
				System.exit(0);

			}
		});
		tField.addActionListener(new TFListener());
		setVisible(true);
		
		connect();
		
		reciveThread.start();
	}
	
	// �ͻ�����������
	private void connect() {
		try {
			socket = new Socket("127.0.0.1", 8888);
			System.out.println("connected");
			
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// �Ͽ��ͻ�������
	private void disConnect() {
		try {
			if(dis != null) {
				dis.close();
			}
			if(dos != null) {
				dos.close();
			}
			if(socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// ���ü�������
	private class TFListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			
			// ��ȡ����
			String string = tField.getText().trim();
			//textArea.append(string + "\n");
			tField.setText("");
			
			// �������뵽������
			try {
				dos.writeUTF(string);
				dos.flush();
				//dos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	// ����һ���߳̽��շ��������
	private class Reciver implements Runnable{

		@Override
		public void run() {
			while (!socket.isClosed()) {
				try {
					textArea.append(dis.readUTF() + "\n");
				} catch (EOFException e) {
					System.out.println("exited");
				}catch (SocketException e) {
					System.out.println("exited");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
