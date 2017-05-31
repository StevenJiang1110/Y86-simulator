package ui;

/**
 * This class is work for UI background.
 * This class will automatically get results through sockets
 * and can auto run the thread step by step.
 * So you can focus on UI thread.
 * @author dell
 */

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

import kernel.PipelineResult;

public class PipelineTask implements Runnable{
	
	public static final int STOP=1;
	public static final int STEPIN=2;
	public static final int STEPBACK=3;
	
	Vector<PipelineResult> pipelineHistory;
	String ip="192.168.1.104";
	final static String HostName="DELL-PC";
	final static int port=8189;
	public String fileName;
	public int run_mode;
	public int current_index;
	public boolean read_finished;
	public int interval;
	public boolean needNet;
	
	public PipelineTask() {
		// TODO Auto-generated constructor stub
		pipelineHistory=new Vector<>();
		interval=500;
		run_mode=STOP;
		current_index=-1;
		needNet=true;
		try{
			InetAddress address=InetAddress.getByName("DELL-PC");
			//ip=address.getHostAddress();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void OpenFile(String name){
		fileName=name;
	}
	
	
	@Override
	public void run(){
		Runnable getRusultFromServer=new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Socket socket;
				try{
					socket=new Socket(ip, port);
					OutputStream os=socket.getOutputStream();
					InputStream is=socket.getInputStream();
					PrintWriter pw=new PrintWriter(os,false);
					
					InputStream fs=new FileInputStream(fileName);
					Scanner in=new Scanner(fs);
					while(in.hasNextLine()){
						String line=in.nextLine();
						pw.println(line);
					}
					in.close();
					
					pw.flush();
					socket.shutdownOutput();
					
					ObjectInputStream iStream=new ObjectInputStream(is);
					PipelineResult result;
					
					while(true){
						result=(PipelineResult)iStream.readObject();
						pipelineHistory.addElement(result);
						//System.out.println(result.REAX);
					}
				}catch(Exception e){
					e.printStackTrace();
				}finally {
					read_finished=true;
				}
			}
		};
		
		if(needNet){
			Thread netThread=new Thread(getRusultFromServer);
			netThread.start();
		}
		
		try{
			int waittime=0;
			while(true){
				switch (run_mode) {
				case STOP:
					waittime=0;
					Thread.sleep(2000);
					break;
				case STEPIN:
					int size=pipelineHistory.size();
					if(current_index<size-1){
						current_index++;
						Thread.sleep(interval);
					}else{
						//System.out.println("waittime="+waittime);
						Thread.sleep(interval/3);
						waittime++;
						if(waittime>=10){
							waittime=0;
							run_mode=STOP;
						}
					}
					break;
				case STEPBACK:
					waittime=0;
					if(current_index>0){
						current_index--;
						Thread.sleep(interval);
					}else {
						run_mode=STOP;
					}
					break;
				default:
					Thread.sleep(2000);
					run_mode=STOP;
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean stepin(){
		run_mode=STOP;
		int size=pipelineHistory.size();
		if(current_index<size-1){
			current_index++;
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean stepback(){
		run_mode=STOP;
		if(current_index>0){
			current_index--;
			return true;
		}else {
			return false;
		}
	}
	
	public static void main(String...strings){
		PipelineTask runnable=new PipelineTask();
		runnable.OpenFile("E://Y86testfile/test1.y86");
		Thread thread=new Thread(runnable);
		thread.start();
	}
	
}
