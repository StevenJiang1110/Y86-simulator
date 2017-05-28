package server;

/**
 * This class is for server use.
 * When run, server can supply service for multiple requests.
 * As exists the limits of hardware, you'd better limit the number of requests
 * at the same time.
 * @author dell
 */


import java.awt.EventQueue;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import kernel.Pipeline;
import kernel.PipelineResult;

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
				ArrayList<PipelineResult> results=new ArrayList<>();
				InputStream inputStream=incoming.getInputStream();
				OutputStream outputStream=incoming.getOutputStream();
				Pipeline pipeline=new Pipeline();
				pipeline.ReadFile(inputStream);
				inputStream.close();
				int count=0;
				
				PipelineResult result=pipeline.getResult();
				results.add(result);
				
				while(pipeline.end!=1){
					pipeline.stepin(null, count);
					count++;
					PipelineResult currentResult=pipeline.getResult();
					results.add(currentResult);
				}
				
				ObjectOutputStream os=new ObjectOutputStream(outputStream);
				os.writeObject(results);
				os.flush();
				outputStream.flush();
				outputStream.close();
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
