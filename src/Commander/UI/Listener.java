package Commander.UI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.RenderingHints.Key;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.IconUIResource;

import Commander.Commander;

public class Listener implements MouseListener,KeyListener,ListSelectionListener,ActionListener{
	Date d=null;
	SimpleDateFormat sdf=new SimpleDateFormat("HH:mm>>");
	boolean in=false;
	int s=0;
	int end=0;
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==GUI.get) {						//����get��
			System.out.println("������");
			
			GUI.get.setIcon(new ImageIcon("properties/lib/key/press.png"));
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==GUI.get) {						//����get��
			System.out.println("������");
			
			GUI.get.setIcon(new ImageIcon("properties/lib/key/s4.png"));
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==GUI.get) {						//����get��
			System.out.println("������");
			
			GUI.get.setIcon(new ImageIcon("properties/lib/key/s4.png"));
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if(e.getSource()==GUI.get) {
			GUI.get.setIcon(new ImageIcon("properties/lib/key/2.png"));
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==GUI.dos) {
			if(e.getKeyCode()==KeyEvent.VK_ENTER) {
				d=new Date();
				if(!GUI.dos.getText().trim().equals("")) {
								//��������
					Commander.exec("run "+GUI.dos.getText(),"gui");
					GUI.dosin.append(sdf.format(d)+GUI.dos.getText()+"\n");
				}
				GUI.dos.setText("");
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {//ѡ������
		GUI.link.setEnabled(true);
		GUI.link.setBackground(Color.green);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==GUI.link) {	//�������
			GUI.unlink.setEnabled(true);
			GUI.unlink.setBackground(Color.red);
			String host=GUI.host.getSelectedValue();
			Commander.exec("link "+host,"gui");
			if(GUI.host.getSelectedValue().equals(Commander.loc)) {
				GUI.hostname.setText("��ǰ���ӣ�"+GUI.host.getSelectedValue());
				GUI.status.setStat(1);
				GUI.msgtext.setText("�˿ڣ�"+Integer.parseInt(Commander.setup.get(0).substring(5))+"\n");
				GUI.msgtext.append("loc��"+Commander.locDesk+"\n");
				GUI.msgtext.append("rem��"+Commander.path+"\n");
				//ִ��
				Commander.exec("view -ip","gui");
				Timer.alive=true;
				GUI.msgtext.append("IP��ַ��"+GUI.IP==null?"δ֪":GUI.IP+"\n");
				new Timer().start();
				GUI.link.setEnabled(false);
				GUI.link.setBackground(new Color(150,150,150));
			}else {
				JOptionPane.showMessageDialog(GUI.link, "�����쳣��", "����", JOptionPane.ERROR_MESSAGE);
			}
		}else if(e.getSource()==GUI.unlink){	//����Ͽ�
			GUI.unlink.setEnabled(false);
			Commander.loc="Mrright";
			Commander.path="C:/";
			GUI.hostname.setText("������");
			GUI.unlink.setBackground(new Color(150,150,150));
			GUI.status.setStat(0);
			Timer.alive=false;
			GUI.timer.setText("TIMER");
			if(Commander.list.size()!=0) {
				GUI.link.setBackground(Color.GREEN);
				GUI.link.setEnabled(true);
			}
		}
	}

}
