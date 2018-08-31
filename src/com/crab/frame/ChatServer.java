package com.crab.frame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/** 
* @author: pycrab
* @Date: 2018��8��30�� ����9:20:23 
*/
public class ChatServer {
	private ServerSocket ss = null;
	private static int cnt = 1;
	private List<Client> clients = new ArrayList<Client>();
	
	public static void main(String[] args) {
		new ChatServer().start();
	}
	
	// ��������˿�
	private void start() {
		try {
			ss = new ServerSocket(8888);
			
		}catch (BindException e) {
			System.out.println("�˿��ѱ�ռ��...");
			System.exit(0);
		}catch (IOException e) {
			e.printStackTrace();
		}
		try {
			while (!ss.isClosed()) {
				// ���տͻ�������
				Socket socket = ss.accept();
				
				// Ϊÿ���ͻ�������һ���߳�
				Client client = new Client(socket);
				clients.add(client);
				Thread tr = new Thread(client, "user"+cnt);
				tr.start();
				System.out.println(tr.getName() + " connected");
				cnt++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(ss != null) {
					ss.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	class Client implements Runnable{
		private Socket socket = null;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
	
		public Client(Socket socket) {
			this.socket =socket;
			try {
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Ϊ�̷߳���
		@Override
		public void run() {
			try {
				while (!socket.isClosed()) {
					String string = Thread.currentThread().getName() + ":" + dis.readUTF();
					System.out.println(string);
					
					for(Client client : clients) {
						client.send(string);
					}
				}
			} catch (EOFException e) {
				System.out.println("client closed");
			} catch (IOException e) {
				System.out.println(Thread.currentThread().getName() + " exited");
				//e.printStackTrace();
			} finally {
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
		}
		
		// ��ͻ��˷�������
		private void send(String string) {
			try {
				dos.writeUTF(string);
			} catch (SocketException e) {
				clients.remove(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}

