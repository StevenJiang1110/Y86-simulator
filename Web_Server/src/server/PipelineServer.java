package server;
import java.awt.Event;
import java.awt.EventQueue;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigestSpi;

import kernel.Pipeline;

public class PipelineServer implements Runnable{
	@Override
	public void run(){
		try{
			ServerSocket s=new ServerSocket(8189);
			
			while(true){
				Socket incoming=s.accept();
				Runnable r=new PipelineServerHandler(incoming);
				Thread thread=new Thread(r);
				thread.start();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void main(String...strings) {
		EventQueue.invokeLater(new PipelineServer());
	}
}

class PipelineServerHandler implements Runnable{
	
	private Socket incoming;
	private int counter;
	@Override
	public void run(){
		try{
			try{
				InputStream inputStream=incoming.getInputStream();
				OutputStream outputStream=incoming.getOutputStream();
				Pipeline pipeline=new Pipeline();
				pipeline.ReadFile(inputStream);
				int count=0;
				
				while(pipeline.end!=1){
					pipeline.stepin(outputStream, count);
					count++;
				}
			}
			finally{
				incoming.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public PipelineServerHandler(Socket i){
		incoming=i;
	}
}
