package ui;


/**
 * This class is UI thread.
 * You can open a file from hard disk and get result through socket
 * or you can directly load a file of PipelineResult. 
 * @author dell
 */

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import kernel.PipelineResult;

public class PipelineFrame extends JFrame{
	private PipelineTask current_task;
	private Thread thread;
	private Thread currentRefreshUI=null;
	
	//Some Constant Values
	String[] registers={"REAX","RECX","REDX","REBX","RESP","REBP","RESI","REDI","RNONE"};
	String[] stat={"","SAOK","SADR","SINS","SHLT","SBUB"};
	String[] alu={"ADDL","SUBL","ANDL","XORL"};
	
	//Menu part
	private JFileChooser chooser;
	private JFileChooser saveChooser;
	
	//Right Hand
	private JTextField cycle;
	private JTextField REAX;
	private JTextField RECX;
	private JTextField REDX;
	private JTextField REBX;
	private JTextField RESP;
	private JTextField REBP;
	private JTextField RESI;
	private JTextField REDI;
	
	//Line1
	private JTextField W_stat;
	private JTextField W_Bubble;
	private JTextField W_stall;
	private JSlider clockFrequency;
	
	//Line2
	private JTextField W_icode;
	private JTextField W_dstE;
	private JTextField W_dstM;
	private JTextField W_valE;
	private JTextField W_valM;
	
	//Line3
	private JTextField m_valM;
	private JTextField mem_addr;
	private JTextField mem_read;
	private JTextField mem_write;
	private JTextField dmem_error;
	
	//Line4
	private JTextField M_stat;
	private JTextField M_Bubble;
	private JTextField M_stall;
	private JTextField M_icode;
	private JTextField M_ifun;
	private JTextField M_valA;
	
	//Line5
	private JTextField M_dstE;
	private JTextField M_valE;
	private JTextField M_dstM;
	private JTextField M_Cnd;
	
	//Line6
	private JTextField e_valE;
	private JTextField e_Cnd;
	private JTextField e_dstE;
	private JTextField ZF;
	private JTextField OF;
	private JTextField SF;
	private JTextField alufun;
	
	//Line7
	private JTextField E_stat;
	private JTextField E_Bubble;
	private JTextField E_stall;
	private JTextField aluA;
	private JTextField aluB;
	private JTextField E_srcA;
	private JTextField E_srcB;
	
	
	//Line8
	private JTextField E_icode;
	private JTextField E_ifun;
	private JTextField E_valC;
	private JTextField E_valA;
	private JTextField E_valB;
	private JTextField E_dstE;
	private JTextField E_dstM;
	
	//Line9
	private JTextField d_srcA;
	private JTextField d_srcB;
	private JTextField d_dstE;
	private JTextField d_dstM;
	private JTextField d_rvalA;
	private JTextField d_rvalB;
	
	//Line10
	private JTextField D_stat;
	private JTextField D_stall;
	private JTextField D_Bubble;
	private JTextField D_icode;
	private JTextField D_ifun;
	private JTextField D_rA;
	private JTextField D_rB;
	
	//Line11
	private JTextField D_valC;
	private JTextField D_valP;
	private JTextField need_regids;
	private JTextField need_valC;
	
	//Line12
	private JTextField f_stat;
	private JTextField f_icode;
	private JTextField f_ifun;
	private JTextField f_pc;
	private JTextField f_predPC;
	private JTextField f_valC;
	private JTextField f_valP;
	
	//Line13
	private JTextField F_Bubble;
	private JTextField F_stall;
	private JTextField F_predPC;
	
	
	//constructor
	public PipelineFrame(){
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=kit.getScreenSize();
		
		int ScreenHeight=screenSize.height;
		int ScreenWidth=screenSize.width;
		
		setSize(ScreenWidth-20,ScreenHeight-20);
		setLocationByPlatform(true);
		
		setLayout(null);
		
		setBackgroundImage();
		InitMenubar();
		initMainView();
		
		SetDefault();
	}
	
	//set background
	public void setBackgroundImage(){
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=kit.getScreenSize();
		
		int ScreenHeight=screenSize.height;
		int ScreenWidth=screenSize.width;
		
		//System.out.println(ScreenHeight);
		//System.out.println(ScreenWidth);
		
		JPanel imagePanel=(JPanel)getContentPane();
		imagePanel.setOpaque(false);
		
		ImageIcon icon=new ImageIcon("E:\\background1.jpg");
		icon.setImage(icon.getImage().getScaledInstance(icon.getIconWidth(), icon.getIconHeight(), Image.SCALE_DEFAULT));
		JLabel background=new JLabel("");
		getLayeredPane().setLayout(null);
		getLayeredPane().add(background, new Integer(Integer.MIN_VALUE));
		background.setIcon(icon);
		background.setHorizontalAlignment(0);
		background.setBounds(0, 0, ScreenWidth, ScreenHeight-20);
	}
	
	//Initialize menu part
	public void InitMenubar(){
		chooser=new JFileChooser();
		FileNameExtensionFilter filter=new FileNameExtensionFilter("Y86 file", "y86");
		chooser.setFileFilter(filter);
		
		saveChooser=new JFileChooser();
		FileNameExtensionFilter saveFilter=new FileNameExtensionFilter("data file", "dat");
		saveChooser.setFileFilter(saveFilter);
		
		JMenuBar menuBar=new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu filemenu=new JMenu("file");
		menuBar.add(filemenu);
		
		JMenuItem openItem=new JMenuItem("open");
		filemenu.add(openItem);
		//open file
		openItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				chooser.setCurrentDirectory(new File("E:\\Y86testfile"));
				int result=chooser.showOpenDialog(PipelineFrame.this);
				if(result==JFileChooser.APPROVE_OPTION){
					SetDefault();
					String name=chooser.getSelectedFile().getPath();
					//System.out.println(name);
					current_task=new PipelineTask();
					current_task.OpenFile(name);
					
					thread=new Thread(current_task);
					thread.start();
				}
			}
		});
		
		filemenu.addSeparator();
		JMenuItem saveItem=new JMenuItem("save");
		filemenu.add(saveItem);
		saveItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//Yes:0 No:1			
				if(current_task!=null){
					if(current_task.read_finished==false){
						JOptionPane.showConfirmDialog(null, "request denied.", "Error", JOptionPane.PLAIN_MESSAGE);
					}else{
						saveChooser.setCurrentDirectory(new File("E:\\Y86savefile"));
						int result=saveChooser.showOpenDialog(PipelineFrame.this);
						if(result==JFileChooser.APPROVE_OPTION){
							String name=saveChooser.getSelectedFile().getPath();
							try{
								FileOutputStream os=new FileOutputStream(name);
								ObjectOutputStream out=new ObjectOutputStream(os);
								
								for(PipelineResult result1:current_task.pipelineHistory){
									out.writeObject(result1);
									out.flush();
								}
								out.close();
								os.close();
								
								JOptionPane.showConfirmDialog(null, "Save successfully!","Info",JOptionPane.PLAIN_MESSAGE);
							}catch(Exception e1){
								e1.printStackTrace();
							}
						}else{
							JOptionPane.showConfirmDialog(null, "Invalid file type","Error",JOptionPane.PLAIN_MESSAGE);
						}
					}
				}else{
					JOptionPane.showConfirmDialog(null, "Nothing to save","Error",JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		
		JMenuItem loadItem=new JMenuItem("load");
		filemenu.addSeparator();
		filemenu.add(loadItem);
		loadItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(current_task!=null){
					if(current_task.read_finished==false){
						JOptionPane.showConfirmDialog(null, "request denied.", "Error", JOptionPane.PLAIN_MESSAGE);
					}else{
						SetDefault();
						saveChooser.setCurrentDirectory(new File("E:\\Y86savefile"));
						int result=saveChooser.showOpenDialog(PipelineFrame.this);
						if(result==JFileChooser.APPROVE_OPTION){
							String name=saveChooser.getSelectedFile().getPath();
							current_task.current_index=-1;
							current_task.run_mode=PipelineTask.STOP;
							try{
								FileInputStream fs=new FileInputStream(name);
								ObjectInputStream in=new ObjectInputStream(fs);
							
								current_task.pipelineHistory.clear();
								PipelineResult result2;
								while(true){
									result2=(PipelineResult)in.readObject();
									current_task.pipelineHistory.addElement(result2);
								}
							}catch(Exception e1){
								e1.printStackTrace();
							}finally {
								JOptionPane.showConfirmDialog(null, "Save successfully!","Info",JOptionPane.PLAIN_MESSAGE);
							}
						}
					}
				}else{
					saveChooser.setCurrentDirectory(new File("E:\\Y86savefile"));
					int result=saveChooser.showOpenDialog(PipelineFrame.this);
					Vector<PipelineResult>pipelineResults = new Vector<>();
					if(result==JFileChooser.APPROVE_OPTION){
						String name=saveChooser.getSelectedFile().getPath();
						FileInputStream fs;
						ObjectInputStream in;
						try{
							fs=new FileInputStream(name);
							in=new ObjectInputStream(fs);
						
							PipelineResult result2;
							//int count=0;
							while((result2=(PipelineResult)in.readObject())!=null){
								//System.out.println(count);
								//count++;
								pipelineResults.addElement(result2);
							}
						}catch(Exception e1){
							e1.printStackTrace();
						}finally {
							JOptionPane.showConfirmDialog(null, "Save successfully!","Info",JOptionPane.PLAIN_MESSAGE);
							current_task=new PipelineTask();
							current_task.pipelineHistory=pipelineResults;
							current_task.needNet=false;
							thread=new Thread(current_task);
							thread.start();
							//System.out.println("start");
						}
					}
				}
			}
		});
		
		JMenu runmenu=new JMenu("run");
		menuBar.add(runmenu);
		
		JMenuItem stepin=new JMenuItem("step in");
		runmenu.add(stepin);
		stepin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(current_task!=null){
					current_task.stepin();
				}
			}
		});
		
		JMenuItem autorun=new JMenuItem("auto run");
		runmenu.addSeparator();
		runmenu.add(autorun);
		autorun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(current_task!=null){
					current_task.run_mode=PipelineTask.STEPIN;
				}
			}
		});
		
		JMenuItem stop=new JMenuItem("stop");
		runmenu.addSeparator();
		runmenu.add(stop);
		stop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(current_task!=null){
					current_task.run_mode=PipelineTask.STOP;
				}
			}
		});
		
		JMenuItem stepback=new JMenuItem("step back");
		runmenu.addSeparator();
		runmenu.add(stepback);
		stepback.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(current_task!=null){
					current_task.stepback();
				}
			}
		});
		
		JMenuItem autoback=new JMenuItem("auto back");
		runmenu.addSeparator();
		runmenu.add(autoback);
		autoback.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(current_task!=null){
					current_task.run_mode=PipelineTask.STEPBACK;
				}
			}
		});
	}
	
	//initialize main part of view
	public void initMainView(){
		//Right-hand
		JLabel clockcycle=new JLabel("cycle:");
		add(clockcycle);
		clockcycle.setBounds(1080,20,100,30);
		//clockcycle.setBackground(Color.WHITE);
		
		cycle=new JTextField("0",20);
		add(cycle);
		cycle.setEditable(false);
		cycle.setBounds(1150,20,120,30);
		
		
		JLabel registers=new JLabel("REGISTERS:");
		add(registers);
		registers.setBounds(1080,70,100,30);
		
		JLabel rEAX=new JLabel("REAX");
		add(rEAX);
		rEAX.setBounds(1080,120,50,30);
		
		REAX=new JTextField("0x0",20);
		add(REAX);
		REAX.setEditable(false);
		REAX.setBounds(1150,120,120,30);
		
		JLabel rECX=new JLabel("RECX");
		add(rECX);
		rECX.setBounds(1080,170,50,30);
		
		RECX=new JTextField("0x0",20);
		add(RECX);
		RECX.setEditable(false);
		RECX.setBounds(1150,170,120,30);
		
		JLabel rEDX=new JLabel("REDX");
		add(rEDX);
		rEDX.setBounds(1080,220,50,30);
		
		REDX=new JTextField("0x0",20);
		add(REDX);
		REDX.setEditable(false);
		REDX.setBounds(1150,220,120,30);
		
		JLabel rEBX=new JLabel("REBX");
		add(rEBX);
		rEBX.setBounds(1080,270,50,30);
		
		REBX=new JTextField("0x0",20);
		add(REBX);
		REBX.setEditable(false);
		REBX.setBounds(1150,270,120,30);
		
		JLabel rESP=new JLabel("RESP");
		add(rESP);
		rESP.setBounds(1080,320,50,30);
		
		RESP=new JTextField("0x0",20);
		add(RESP);
		RESP.setEditable(false);
		RESP.setBounds(1150,320,120,30);
		
		JLabel rEBP=new JLabel("REBP");
		add(rEBP);
		rEBP.setBounds(1080,370,50,30);
		
		REBP=new JTextField("0x0",20);
		add(REBP);
		REBP.setEditable(false);
		REBP.setBounds(1150,370,120,30);
		
		JLabel rESI=new JLabel("RESI");
		add(rESI);
		rESI.setBounds(1080,420,50,30);
		
		RESI=new JTextField("0x0",20);
		add(RESI);
		RESI.setEditable(false);
		RESI.setBounds(1150,420,120,30);
		
		JLabel rEDI=new JLabel("REDI");
		add(rEDI);
		rEDI.setBounds(1080,470,50,30);
		
		REDI=new JTextField("0x0",20);
		add(REDI);
		REDI.setEditable(false);
		REDI.setBounds(1150,470,120,30);

		//Line1
		JLabel Wstat=new JLabel("W_stat");
		add(Wstat);
		Wstat.setBounds(20,20,50,30);
		
		W_stat=new JTextField("SAOK",20);
		add(W_stat);
		W_stat.setEditable(false);
		W_stat.setBounds(75,20,65,30);
		
		JLabel WBubble=new JLabel("W_Bubble");
		add(WBubble);
		WBubble.setBounds(150,20,60,30);
		
		W_Bubble=new JTextField("False",20);
		add(W_Bubble);
		W_Bubble.setEditable(false);
		W_Bubble.setBounds(225,20,65,30);
		
		JLabel Wstall=new JLabel("W_Stall");
		add(Wstall);
		Wstall.setBounds(305,20,60,30);
		
		W_stall=new JTextField("False",20);
		add(W_stall);
		W_stall.setEditable(false);
		W_stall.setBounds(375,20,65,30);
		
		JLabel clock=new JLabel("clock");
		add(clock);
		clock.setBounds(465,20,60,30);
		
		JLabel mini=new JLabel("100");
		add(mini);
		mini.setBounds(530,20,50,30);
		
		clockFrequency=new JSlider(100, 1000, 500);
		add(clockFrequency);
		clockFrequency.setMajorTickSpacing(100);
		clockFrequency.setMinorTickSpacing(20);
		clockFrequency.setPaintTicks(true);
		clockFrequency.setSnapToTicks(true);
		clockFrequency.setBounds(560,25,400,30);
		clockFrequency.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				if(current_task!=null){
					current_task.interval=clockFrequency.getValue();
				}
			}
		});
		
		JLabel maxi=new JLabel("1000");
		add(maxi);
		maxi.setBounds(965,20,50,30);
		
		//Line2
		JLabel Wicode=new JLabel("W_icode");
		add(Wicode);
		Wicode.setBounds(20,70,50,30);
		
		W_icode=new JTextField("0x0",20);
		add(W_icode);
		W_icode.setEditable(false);
		W_icode.setBounds(75,70,65,30);
		
		JLabel WdstE=new JLabel("W_dstE");
		add(WdstE);
		WdstE.setBounds(150,70,50,30);
		
		W_dstE=new JTextField("0x0",20);
		add(W_dstE);
		W_dstE.setEditable(false);
		W_dstE.setBounds(225,70,65,30);
		
		JLabel WdstM=new JLabel("W_dstM");
		add(WdstM);
		WdstM.setBounds(305,70,50,30);
		
		W_dstM=new JTextField("0x0",20);
		add(W_dstM);
		W_dstM.setEditable(false);
		W_dstM.setBounds(375,70,65,30);
		
		JLabel WvalE=new JLabel("W_valE");
		add(WvalE);
		WvalE.setBounds(460,70,50,30);
		
		W_valE=new JTextField("0x0",20);
		add(W_valE);
		W_valE.setEditable(false);
		W_valE.setBounds(525,70,65,30);
		
		JLabel WvalM=new JLabel("W_valM");
		add(WvalM);
		WvalM.setBounds(610,70,50,30);
		
		W_valM=new JTextField("0x0",20);
		add(W_valM);
		W_valM.setEditable(false);
		W_valM.setBounds(675,70,65,30);
		
		//Line3
		JLabel mvalM=new JLabel("m_valM");
		add(mvalM);
		mvalM.setBounds(20,120,50,30);
		
		m_valM=new JTextField("0x0",20);
		add(m_valM);
		m_valM.setEditable(false);
		m_valM.setBounds(75,120,65,30);
		
		JLabel memaddr=new JLabel("mem_addr");
		add(memaddr);
		memaddr.setBounds(150,120,80,30);
		
		mem_addr=new JTextField("0x0",20);
		add(mem_addr);
		mem_addr.setEditable(false);
		mem_addr.setBounds(230,120,100,30);
		
		JLabel memread=new JLabel("mem_read");
		add(memread);
		memread.setBounds(350,120,80,30);
		
		mem_read=new JTextField("False",20);
		add(mem_read);
		mem_read.setEditable(false);
		mem_read.setBounds(440,120,100,30);
		
		JLabel memwrite=new JLabel("mem_write");
		add(memwrite);
		memwrite.setBounds(550,120,80,30);
		
		mem_write=new JTextField("False",20);
		add(mem_write);
		mem_write.setEditable(false);
		mem_write.setBounds(640,120,100,30);
		
		JLabel dmemerror=new JLabel("dmem_error");
		add(dmemerror);
		dmemerror.setBounds(750,120,80,30);
		
		dmem_error=new JTextField("False",20);
		add(dmem_error);
		dmem_error.setEditable(false);
		dmem_error.setBounds(840,120,100,30);
		
		//Line4
		JLabel Mstat=new JLabel("M_stat");
		add(Mstat);
		Mstat.setBounds(20,170,50,30);
		
		M_stat=new JTextField("SAOK",20);
		add(M_stat);
		M_stat.setEditable(false);
		M_stat.setBounds(75,170,65,30);
		
		JLabel MBubble=new JLabel("M_Bubble");
		add(MBubble);
		MBubble.setBounds(150,170,70,30);
		
		M_Bubble=new JTextField("False",20);
		add(M_Bubble);
		M_Bubble.setEditable(false);
		M_Bubble.setBounds(225,170,65,30);
		
		JLabel Mstall=new JLabel("M_stall");
		add(Mstall);
		Mstall.setBounds(305,170,70,30);
		
		M_stall=new JTextField("False",20);
		add(M_stall);
		M_stall.setEditable(false);
		M_stall.setBounds(375,170,65,30);
		
		JLabel Micode=new JLabel("M_icode");
		add(Micode);
		Micode.setBounds(450,170,70,30);
		
		M_icode=new JTextField("0x0",20);
		add(M_icode);
		M_icode.setEditable(false);
		M_icode.setBounds(510,170,65,30);
		
		JLabel Mifun=new JLabel("M_ifun");
		add(Mifun);
		Mifun.setBounds(585,170,60,30);
		
		M_ifun=new JTextField("0x0",20);
		add(M_ifun);
		M_ifun.setEditable(false);
		M_ifun.setBounds(645,170,65,30);
		
		JLabel MvalA=new JLabel("M_valA");
		add(MvalA);
		MvalA.setBounds(720,170,60,30);
		
		M_valA=new JTextField("0x0",20);
		add(M_valA);
		M_valA.setEditable(false);
		M_valA.setBounds(780,170,65,30);
		
		//Line5
		JLabel MdstE=new JLabel("M_dstE");
		add(MdstE);
		MdstE.setBounds(20,220,50,30);
		
		M_dstE=new JTextField("0x0",20);
		add(M_dstE);
		M_dstE.setEditable(false);
		M_dstE.setBounds(75,220,65,30);
		
		JLabel MvalE=new JLabel("M_valE");
		add(MvalE);
		MvalE.setBounds(150,220,70,30);
		
		M_valE=new JTextField("0x0",20);
		add(M_valE);
		M_valE.setEditable(false);
		M_valE.setBounds(225,220,65,30);
		
		JLabel MdstM=new JLabel("M_dstM");
		add(MdstM);
		MdstM.setBounds(305,220,70,30);
		
		M_dstM=new JTextField("0x0",20);
		add(M_dstM);
		M_dstM.setEditable(false);
		M_dstM.setBounds(375,220,65,30);
		
		JLabel MCnd=new JLabel("M_Cnd");
		add(MCnd);
		MCnd.setBounds(450,220,70,30);
		
		M_Cnd=new JTextField("False",20);
		add(M_Cnd);
		M_Cnd.setEditable(false);
		M_Cnd.setBounds(510,220,65,30);
		
		//Line6
		JLabel evalE=new JLabel("e_valE");
		add(evalE);
		evalE.setBounds(20,270,50,30);
		
		e_valE=new JTextField("0x0",20);
		add(e_valE);
		e_valE.setEditable(false);
		e_valE.setBounds(75,270,65,30);
		
		JLabel eCnd=new JLabel("e_Cnd");
		add(eCnd);
		eCnd.setBounds(150,270,70,30);
		
		e_Cnd=new JTextField("False",20);
		add(e_Cnd);
		e_Cnd.setEditable(false);
		e_Cnd.setBounds(225,270,65,30);
		
		JLabel edstE=new JLabel("e_dstE");
		add(edstE);
		edstE.setBounds(305,270,70,30);
		
		e_dstE=new JTextField("0x0",20);
		add(e_dstE);
		e_dstE.setEditable(false);
		e_dstE.setBounds(375,270,65,30);
		
		JLabel zF=new JLabel("ZF");
		add(zF);
		zF.setBounds(450,270,70,30);
		
		ZF=new JTextField("False",20);
		add(ZF);
		ZF.setEditable(false);
		ZF.setBounds(510,270,65,30);
		
		JLabel oF=new JLabel("OF");
		add(oF);
		oF.setBounds(585,270,70,30);
		
		OF=new JTextField("False",20);
		add(OF);
		OF.setEditable(false);
		OF.setBounds(645,270,65,30);
		
		JLabel sF=new JLabel("SF");
		add(sF);
		sF.setBounds(720,270,70,30);
		
		SF=new JTextField("False",20);
		add(SF);
		SF.setEditable(false);
		SF.setBounds(780,270,65,30);
		
		JLabel alu_fun=new JLabel("alufun");
		add(alu_fun);
		alu_fun.setBounds(855,270,70,30);
		
		alufun=new JTextField("False",20);
		add(alufun);
		alufun.setEditable(false);
		alufun.setBounds(900,270,65,30);
		
		//Line7
		JLabel Estat=new JLabel("E_stat");
		add(Estat);
		Estat.setBounds(20,320,50,30);
		
		E_stat=new JTextField("SAOK",20);
		add(E_stat);
		E_stat.setEditable(false);
		E_stat.setBounds(75,320,65,30);
		
		JLabel EBubble=new JLabel("E_Bubble");
		add(EBubble);
		EBubble.setBounds(150,320,70,30);
		
		E_Bubble=new JTextField("False",20);
		add(E_Bubble);
		E_Bubble.setEditable(false);
		E_Bubble.setBounds(225,320,65,30);
		
		JLabel Estall=new JLabel("E_stall");
		add(Estall);
		Estall.setBounds(305,320,70,30);
		
		E_stall=new JTextField("False",20);
		add(E_stall);
		E_stall.setEditable(false);
		E_stall.setBounds(375,320,65,30);
		
		JLabel alua=new JLabel("aluA");
		add(alua);
		alua.setBounds(450,320,70,30);
		
		aluA=new JTextField("0x0",20);
		add(aluA);
		aluA.setEditable(false);
		aluA.setBounds(510,320,65,30);
		
		JLabel alub=new JLabel("aluB");
		add(alub);
		alub.setBounds(585,320,70,30);
		
		aluB=new JTextField("0x0",20);
		add(aluB);
		aluB.setEditable(false);
		aluB.setBounds(645,320,65,30);
		
		JLabel EsrcA=new JLabel("E_srcA");
		add(EsrcA);
		EsrcA.setBounds(720,320,70,30);
		
		E_srcA=new JTextField("0x0",20);
		add(E_srcA);
		E_srcA.setEditable(false);
		E_srcA.setBounds(780,320,65,30);
		
		JLabel EsrcB=new JLabel("E_srcB");
		add(EsrcB);
		EsrcB.setBounds(855,320,70,30);
		
		E_srcB=new JTextField("0x0",20);
		add(E_srcB);
		E_srcB.setEditable(false);
		E_srcB.setBounds(900,320,65,30);
		
		//Line8
		JLabel Eicode=new JLabel("E_icode");
		add(Eicode);
		Eicode.setBounds(20,370,50,30);
		
		E_icode=new JTextField("0x0",20);
		add(E_icode);
		E_icode.setEditable(false);
		E_icode.setBounds(75,370,65,30);
		
		JLabel Eifun=new JLabel("E_ifun");
		add(Eifun);
		Eifun.setBounds(150,370,70,30);
		
		E_ifun=new JTextField("0x0",20);
		add(E_ifun);
		E_ifun.setEditable(false);
		E_ifun.setBounds(225,370,65,30);
		
		JLabel EvalC=new JLabel("E_valC");
		add(EvalC);
		EvalC.setBounds(305,370,70,30);
		
		E_valC=new JTextField("0x0",20);
		add(E_valC);
		E_valC.setEditable(false);
		E_valC.setBounds(375,370,65,30);
		
		JLabel EvalA=new JLabel("E_valA");
		add(EvalA);
		EvalA.setBounds(450,370,70,30);
		
		E_valA=new JTextField("0x0",20);
		add(E_valA);
		E_valA.setEditable(false);
		E_valA.setBounds(510,370,65,30);
		
		JLabel EvalB=new JLabel("E_valB");
		add(EvalB);
		EvalB.setBounds(585,370,70,30);
		
		E_valB=new JTextField("0x0",20);
		add(E_valB);
		E_valB.setEditable(false);
		E_valB.setBounds(645,370,65,30);
		
		JLabel EdstE=new JLabel("E_dstE");
		add(EdstE);
		EdstE.setBounds(720,370,70,30);
		
		E_dstE=new JTextField("0x0",20);
		add(E_dstE);
		E_dstE.setEditable(false);
		E_dstE.setBounds(780,370,65,30);
		
		JLabel EdstM=new JLabel("E_dstM");
		add(EdstM);
		EdstM.setBounds(855,370,70,30);
		
		E_dstM=new JTextField("0x0",20);
		add(E_dstM);
		E_dstM.setEditable(false);
		E_dstM.setBounds(900,370,65,30);
		
		//Line9
		JLabel dsrcA=new JLabel("d_srcA");
		add(dsrcA);
		dsrcA.setBounds(20,420,50,30);
		
		d_srcA=new JTextField("0x0",20);
		add(d_srcA);
		d_srcA.setEditable(false);
		d_srcA.setBounds(75,420,65,30);
		
		JLabel dsrcB=new JLabel("d_srcB");
		add(dsrcB);
		dsrcB.setBounds(150,420,70,30);
		
		d_srcB=new JTextField("0x0",20);
		add(d_srcB);
		d_srcB.setEditable(false);
		d_srcB.setBounds(225,420,65,30);
		
		JLabel ddstE=new JLabel("d_dstE");
		add(ddstE);
		ddstE.setBounds(305,420,70,30);
		
		d_dstE=new JTextField("0x0",20);
		add(d_dstE);
		d_dstE.setEditable(false);
		d_dstE.setBounds(375,420,65,30);
		
		JLabel ddstM=new JLabel("d_dstM");
		add(ddstM);
		ddstM.setBounds(450,420,70,30);
		
		d_dstM=new JTextField("0x0",20);
		add(d_dstM);
		d_dstM.setEditable(false);
		d_dstM.setBounds(510,420,65,30);
		
		JLabel drvalA=new JLabel("d_rvalA");
		add(drvalA);
		drvalA.setBounds(585,420,70,30);
		
		d_rvalA=new JTextField("0x0",20);
		add(d_rvalA);
		d_rvalA.setEditable(false);
		d_rvalA.setBounds(645,420,65,30);
		
		JLabel drvalB=new JLabel("d_rvalB");
		add(drvalB);
		drvalB.setBounds(720,420,70,30);
		
		d_rvalB=new JTextField("0x0",20);
		add(d_rvalB);
		d_rvalB.setEditable(false);
		d_rvalB.setBounds(780,420,65,30);
		
		//Line10
		JLabel Dstat=new JLabel("D_stat");
		add(Dstat);
		Dstat.setBounds(20,470,50,30);
		
		D_stat=new JTextField("SAOK",20);
		add(D_stat);
		D_stat.setEditable(false);
		D_stat.setBounds(75,470,65,30);
		
		JLabel DBubble=new JLabel("D_Bubble");
		add(DBubble);
		DBubble.setBounds(150,470,70,30);
		
		D_Bubble=new JTextField("False",20);
		add(D_Bubble);
		D_Bubble.setEditable(false);
		D_Bubble.setBounds(225,470,65,30);
		
		JLabel Dstall=new JLabel("D_stall");
		add(Dstall);
		Dstall.setBounds(305,470,70,30);
		
		D_stall=new JTextField("False",20);
		add(D_stall);
		D_stall.setEditable(false);
		D_stall.setBounds(375,470,65,30);
		
		JLabel Dicode=new JLabel("D_icode");
		add(Dicode);
		Dicode.setBounds(450,470,70,30);
		
		D_icode=new JTextField("0x0",20);
		add(D_icode);
		D_icode.setEditable(false);
		D_icode.setBounds(510,470,65,30);
		
		JLabel Difun=new JLabel("D_ifun");
		add(Difun);
		Difun.setBounds(585,470,70,30);
		
		D_ifun=new JTextField("0x0",20);
		add(D_ifun);
		D_ifun.setEditable(false);
		D_ifun.setBounds(645,470,65,30);
		
		JLabel DrA=new JLabel("D_rA");
		add(DrA);
		DrA.setBounds(720,470,70,30);
		
		D_rA=new JTextField("0x0",20);
		add(D_rA);
		D_rA.setEditable(false);
		D_rA.setBounds(780,470,65,30);
		
		JLabel DrB=new JLabel("D_rB");
		add(DrB);
		DrB.setBounds(855,470,70,30);
		
		D_rB=new JTextField("0x0",20);
		add(D_rB);
		D_rB.setEditable(false);
		D_rB.setBounds(900,470,65,30);
		
		//Line11
		JLabel DvalC=new JLabel("D_valC");
		add(DvalC);
		DvalC.setBounds(20,520,50,30);
		
		D_valC=new JTextField("0x0",20);
		add(D_valC);
		D_valC.setEditable(false);
		D_valC.setBounds(75,520,65,30);
		
		JLabel DvalP=new JLabel("D_valP");
		add(DvalP);
		DvalP.setBounds(150,520,70,30);
		
		D_valP=new JTextField("0x0",20);
		add(D_valP);
		D_valP.setEditable(false);
		D_valP.setBounds(225,520,65,30);
		
		JLabel needregids=new JLabel("need_regids");
		add(needregids);
		needregids.setBounds(300,520,100,30);
		
		need_regids=new JTextField("False",20);
		add(need_regids);
		need_regids.setEditable(false);
		need_regids.setBounds(375,520,65,30);
		
		JLabel needvalC=new JLabel("need_valC");
		add(needvalC);
		needvalC.setBounds(450,520,100,30);
		
		need_valC=new JTextField("False",20);
		add(need_valC);
		need_valC.setEditable(false);
		need_valC.setBounds(510,520,65,30);
		
		//Line12
		JLabel fstat=new JLabel("f_stat");
		add(fstat);
		fstat.setBounds(20,570,50,30);
		
		f_stat=new JTextField("SAOK",20);
		add(f_stat);
		f_stat.setEditable(false);
		f_stat.setBounds(75,570,65,30);
		
		JLabel ficode=new JLabel("f_icode");
		add(ficode);
		ficode.setBounds(150,570,70,30);
		
		f_icode=new JTextField("0x0",20);
		add(f_icode);
		f_icode.setEditable(false);
		f_icode.setBounds(225,570,65,30);
		
		JLabel fifun=new JLabel("f_ifun");
		add(fifun);
		fifun.setBounds(305,570,70,30);
		
		f_ifun=new JTextField("0x0",20);
		add(f_ifun);
		f_ifun.setEditable(false);
		f_ifun.setBounds(375,570,65,30);
		
		JLabel fpc=new JLabel("f_pc");
		add(fpc);
		fpc.setBounds(450,570,70,30);
		
		f_pc=new JTextField("0x0",20);
		add(f_pc);
		f_pc.setEditable(false);
		f_pc.setBounds(510,570,65,30);
		
		JLabel fpredPC=new JLabel("f_predPC");
		add(fpredPC);
		fpredPC.setBounds(585,570,70,30);
		
		f_predPC=new JTextField("0x0",20);
		add(f_predPC);
		f_predPC.setEditable(false);
		f_predPC.setBounds(645,570,65,30);
		
		JLabel fvalC=new JLabel("f_valC");
		add(fvalC);
		fvalC.setBounds(720,570,70,30);
		
		f_valC=new JTextField("0x0",20);
		add(f_valC);
		f_valC.setEditable(false);
		f_valC.setBounds(780,570,65,30);
		
		JLabel fvalP=new JLabel("f_valP");
		add(fvalP);
		fvalP.setBounds(855,570,70,30);
		
		f_valP=new JTextField("0x0",20);
		add(f_valP);
		f_valP.setEditable(false);
		f_valP.setBounds(900,570,65,30);
		
		//Line13
		JLabel FBubble=new JLabel("F_Bubble");
		add(FBubble);
		FBubble.setBounds(20,620,70,30);
		
		F_Bubble=new JTextField("False",20);
		add(F_Bubble);
		F_Bubble.setEditable(false);
		F_Bubble.setBounds(75,620,65,30);
		
		JLabel Fstall=new JLabel("F_stall");
		add(Fstall);
		Fstall.setBounds(150,620,70,30);
		
		F_stall=new JTextField("0x0",20);
		add(F_stall);
		F_stall.setEditable(false);
		F_stall.setBounds(225,620,65,30);
		
		JLabel FpredPC=new JLabel("F_predPC");
		add(FpredPC);
		FpredPC.setBounds(305,620,70,30);
		
		F_predPC=new JTextField("0x0",20);
		add(F_predPC);
		F_predPC.setEditable(false);
		F_predPC.setBounds(375,620,150,30);
	}
	
	//read values from pipelineResult to update UI
	public void SetValues(PipelineResult pipeline,int count){
		//RightHand
		cycle.setText(Integer.toString(count));
		REAX.setText("0x"+Integer.toHexString(pipeline.REAX));
		RECX.setText("0x"+Integer.toHexString(pipeline.RECX));
		REDX.setText("0x"+Integer.toHexString(pipeline.REDX));
		REBX.setText("0x"+Integer.toHexString(pipeline.REBX));
		RESP.setText("0x"+Integer.toHexString(pipeline.RESP));
		REBP.setText("0x"+Integer.toHexString(pipeline.REBP));
		RESI.setText("0x"+Integer.toHexString(pipeline.RESI));
		REDI.setText("0x"+Integer.toHexString(pipeline.REDI));
		
		//Line1
		W_stat.setText(stat[pipeline.W_stat]);
		W_Bubble.setText(Boolean.toString(pipeline.W_bubble));
		W_stall.setText(Boolean.toString(pipeline.W_stall));
		
		//Line2
		W_icode.setText("0x"+Integer.toHexString(pipeline.W_icode));
		W_dstE.setText(registers[pipeline.W_dstE]);
		W_dstM.setText(registers[pipeline.W_dstM]);
		W_valE.setText("0x"+Integer.toHexString(pipeline.W_valE));
		W_valM.setText("0x"+Integer.toHexString(pipeline.W_valM));
		
		//Line3
		m_valM.setText("0x"+Integer.toHexString(pipeline.m_valM));
		mem_addr.setText("0x"+Integer.toHexString(pipeline.mem_addr));
		mem_read.setText(Boolean.toString(pipeline.mem_read));
		mem_write.setText(Boolean.toString(pipeline.mem_write));
		dmem_error.setText(Boolean.toString(pipeline.dmem_error));
		
		//Line4
		M_stat.setText(stat[pipeline.M_stat]);
		M_Bubble.setText(Boolean.toString(pipeline.M_bubble));
		M_stall.setText(Boolean.toString(pipeline.M_stall));
		M_icode.setText("0x"+Integer.toHexString(pipeline.M_icode));
		M_ifun.setText("0x"+Integer.toHexString(pipeline.M_ifun));
		M_valA.setText("0x"+Integer.toHexString(pipeline.M_valA));
		
		//Line5
		M_dstE.setText(registers[pipeline.M_dstE]);
		M_valE.setText("0x"+Integer.toHexString(pipeline.M_valE));
		M_dstM.setText(registers[pipeline.M_dstM]);
		M_Cnd.setText(Boolean.toString(pipeline.M_Cnd));
		
		//Line6
		e_valE.setText("0x"+Integer.toHexString(pipeline.e_valE));
		e_Cnd.setText(Boolean.toString(pipeline.e_Cnd));
		e_dstE.setText(registers[pipeline.e_dstE]);
		ZF.setText(Boolean.toString(pipeline.ZF));
		OF.setText(Boolean.toString(pipeline.OF));
		SF.setText(Boolean.toString(pipeline.SF));
		alufun.setText(alu[pipeline.alufun]);
		
		//Line7
		E_stat.setText(stat[pipeline.E_stat]);
		E_Bubble.setText(Boolean.toString(pipeline.E_bubble));
		E_stall.setText(Boolean.toString(pipeline.E_stall));
		aluA.setText("0x"+Integer.toHexString(pipeline.aluA));
		aluB.setText("0x"+Integer.toHexString(pipeline.aluB));
		E_srcA.setText(registers[pipeline.E_srcA]);
		E_srcB.setText(registers[pipeline.E_srcB]);
		
		//Line8
		E_icode.setText("0x"+Integer.toHexString(pipeline.E_icode));
		E_ifun.setText("0x"+Integer.toHexString(pipeline.E_ifun));
		E_valC.setText("0x"+Integer.toHexString(pipeline.E_valC));
		E_valA.setText("0x"+Integer.toHexString(pipeline.E_valA));
		E_valB.setText("0x"+Integer.toHexString(pipeline.E_valB));
		E_dstE.setText(registers[pipeline.E_dstE]);
		E_dstM.setText(registers[pipeline.E_dstM]);
		
		//Line9
		d_srcA.setText(registers[pipeline.d_srcA]);
		d_srcB.setText(registers[pipeline.d_srcB]);
		d_dstE.setText(registers[pipeline.d_dstE]);
		d_dstM.setText(registers[pipeline.d_dstM]);
		d_rvalA.setText("0x"+Integer.toHexString(pipeline.d_rvalA));
		d_rvalB.setText("0x"+Integer.toHexString(pipeline.d_rvalB));
		
		//Line10
		D_stat.setText(stat[pipeline.D_stat]);
		D_Bubble.setText(Boolean.toString(pipeline.D_bubble));
		D_stall.setText(Boolean.toString(pipeline.D_stall));
		D_icode.setText("0x"+Integer.toHexString(pipeline.D_icode));
		D_ifun.setText("0x"+Integer.toHexString(pipeline.D_ifun));
		D_rA.setText(registers[pipeline.D_rA]);
		D_rB.setText(registers[pipeline.D_rB]);
		
		//Line11
		D_valC.setText("0x"+Integer.toHexString(pipeline.D_valC));
		D_valP.setText("0x"+Integer.toHexString(pipeline.D_valP));
		need_regids.setText(Boolean.toString(pipeline.need_regids));
		need_valC.setText(Boolean.toString(pipeline.need_valC));
		
		//Line12
		f_stat.setText(stat[pipeline.f_stat]);
		f_icode.setText("0x"+Integer.toHexString(pipeline.f_icode));
		f_ifun.setText("0x"+Integer.toHexString(pipeline.f_ifun));
		f_pc.setText("0x"+Integer.toHexString(pipeline.f_pc));
		f_predPC.setText("0x"+Integer.toHexString(pipeline.f_predPC));
		f_valC.setText("0x"+Integer.toHexString(pipeline.f_valC));
		f_valP.setText("0x"+Integer.toHexString(pipeline.f_valP));
		
		//Line13
		F_Bubble.setText(Boolean.toString(pipeline.F_bubble));
		F_stall.setText(Boolean.toString(pipeline.F_stall));
		F_predPC.setText("0x"+Integer.toHexString(pipeline.F_predPC));
	}
	
	//set init values for the mainview
	public void SetDefault(){
		//RightHand
				cycle.setText(Integer.toString(0));
				REAX.setText("0x"+Integer.toHexString(0));
				RECX.setText("0x"+Integer.toHexString(0));
				REDX.setText("0x"+Integer.toHexString(0));
				REBX.setText("0x"+Integer.toHexString(0));
				RESP.setText("0x"+Integer.toHexString(0));
				REBP.setText("0x"+Integer.toHexString(0));
				RESI.setText("0x"+Integer.toHexString(0));
				REDI.setText("0x"+Integer.toHexString(0));
				
				//Line1
				W_stat.setText(stat[0]);
				W_Bubble.setText(Boolean.toString(false));
				W_stall.setText(Boolean.toString(false));
				
				//Line2
				W_icode.setText("0x"+Integer.toHexString(0));
				W_dstE.setText(registers[8]);
				W_dstM.setText(registers[8]);
				W_valE.setText("0x"+Integer.toHexString(0));
				W_valM.setText("0x"+Integer.toHexString(0));
				
				//Line3
				m_valM.setText("0x"+Integer.toHexString(0));
				mem_addr.setText("0x"+Integer.toHexString(0));
				mem_read.setText(Boolean.toString(false));
				mem_write.setText(Boolean.toString(false));
				dmem_error.setText(Boolean.toString(false));
				
				//Line4
				M_stat.setText(stat[0]);
				M_Bubble.setText(Boolean.toString(false));
				M_stall.setText(Boolean.toString(false));
				M_icode.setText("0x"+Integer.toHexString(0));
				M_ifun.setText("0x"+Integer.toHexString(0));
				M_valA.setText("0x"+Integer.toHexString(0));
				
				//Line5
				M_dstE.setText(registers[8]);
				M_valE.setText("0x"+Integer.toHexString(0));
				M_dstM.setText(registers[8]);
				M_Cnd.setText(Boolean.toString(false));
				
				//Line6
				e_valE.setText("0x"+Integer.toHexString(0));
				e_Cnd.setText(Boolean.toString(false));
				e_dstE.setText(registers[8]);
				ZF.setText(Boolean.toString(false));
				OF.setText(Boolean.toString(false));
				SF.setText(Boolean.toString(false));
				alufun.setText(alu[0]);
				
				//Line7
				E_stat.setText(stat[0]);
				E_Bubble.setText(Boolean.toString(false));
				E_stall.setText(Boolean.toString(false));
				aluA.setText("0x"+Integer.toHexString(0));
				aluB.setText("0x"+Integer.toHexString(0));
				E_srcA.setText(registers[8]);
				E_srcB.setText(registers[8]);
				
				//Line8
				E_icode.setText("0x"+Integer.toHexString(0));
				E_ifun.setText("0x"+Integer.toHexString(0));
				E_valC.setText("0x"+Integer.toHexString(0));
				E_valA.setText("0x"+Integer.toHexString(0));
				E_valB.setText("0x"+Integer.toHexString(0));
				E_dstE.setText(registers[8]);
				E_dstM.setText(registers[8]);
				
				//Line9
				d_srcA.setText(registers[8]);
				d_srcB.setText(registers[8]);
				d_dstE.setText(registers[8]);
				d_dstM.setText(registers[8]);
				d_rvalA.setText("0x"+Integer.toHexString(0));
				d_rvalB.setText("0x"+Integer.toHexString(0));
				
				//Line10
				D_stat.setText(stat[0]);
				D_Bubble.setText(Boolean.toString(false));
				D_stall.setText(Boolean.toString(false));
				D_icode.setText("0x"+Integer.toHexString(0));
				D_ifun.setText("0x"+Integer.toHexString(0));
				D_rA.setText(registers[8]);
				D_rB.setText(registers[8]);
				
				//Line11
				D_valC.setText("0x"+Integer.toHexString(0));
				D_valP.setText("0x"+Integer.toHexString(0));
				need_regids.setText(Boolean.toString(false));
				need_valC.setText(Boolean.toString(false));
				
				//Line12
				f_stat.setText(stat[0]);
				f_icode.setText("0x"+Integer.toHexString(0));
				f_ifun.setText("0x"+Integer.toHexString(0));
				f_pc.setText("0x"+Integer.toHexString(0));
				f_predPC.setText("0x"+Integer.toHexString(0));
				f_valC.setText("0x"+Integer.toHexString(0));
				f_valP.setText("0x"+Integer.toHexString(0));
				
				//Line13
				F_Bubble.setText(Boolean.toString(false));
				F_stall.setText(Boolean.toString(false));
				F_predPC.setText("0x"+Integer.toHexString(0));
	}
	
	//main function of project
	public static void main(String...strings){
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				PipelineFrame frame=new PipelineFrame();
				frame.setTitle("Pipeline");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
				
				
				Runnable refreshUI=new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try{
							while(true){
								if(frame.current_task!=null){
									int index=frame.current_task.current_index;
									if(index==-1){
										Thread.sleep(50);
										continue;
									}
									PipelineResult result=frame.current_task.pipelineHistory.elementAt(index);
									frame.SetValues(result, index);
									Thread.sleep(50);
								}else{
									Thread.sleep(1000);
								}
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				};
				
				frame.currentRefreshUI=new Thread(refreshUI);
				frame.currentRefreshUI.start();
			}
		});
	}
}

