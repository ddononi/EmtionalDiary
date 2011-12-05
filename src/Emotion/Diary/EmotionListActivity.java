package Emotion.Diary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EmotionListActivity  extends Activity  {

	private static final int DIALOG_DELETE_YES_NO_MESSAGE = 1;
	public static final String NOTEFILENAME = "NoteFile_Name";

	 
	ListView m_listview; 			// �˻� ����� ������� listView ��ü
	ArrayList<EmoInfo> m_emoarr;	// ListView�� ������� ������ �����ϴ� EmoInfo class ArrayList
	
	ArrayList<String> m_contentsfileStr=null;	// ���ϸ��� �����ϰ� �ִ� �迭
	
	ImageButton m_NoteBtn;		// ��Ʈ�� ���ư��� ��ư
	
	int deletepos;
	// ������
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Activity�� �������� ������ layout���� �����Ѵ�.
		setContentView(R.layout.emotionlist);
		
		// layout���� listview�� �޾ƿ´�. 
		m_listview = (ListView) findViewById(R.id.listview01);
		m_NoteBtn = (ImageButton) findViewById(R.id.NoteBtn);
		// ArrayList �����Ҵ�
		m_emoarr = new ArrayList<EmoInfo>();
		m_contentsfileStr= new ArrayList<String>();
		//���Ϸ� ���� ����Ÿ�� �д´�. 
		ReadSavedData();
		
		// EmoAdapter����
		EmoAdapter m_adapter = new EmoAdapter(this, R.layout.list_item, m_emoarr); // ����͸� �����մϴ�.
		
		// Adapter�� ListView����	����������������
		m_listview.setAdapter(m_adapter);
		
		// ����Ʈ �䰡 Ŭ�� �Ǿ������� �����ϴ� �̺�Ʈ
		m_listview.setClickable(true);
		m_listview.setItemsCanFocus(true);

		
		m_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		  @Override
		  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

		    //Object o = m_listview.getItemAtPosition(position);
		    /* write you handling code like...
		    String st = "sdcard/";
		    File f = new File(st+o.toString());
		    // do whatever u want to do with 'f' File object
		    */  
			Intent intent = new Intent(EmotionListActivity.this, EmotionalDiaryActivity.class); 
			intent.putExtra(NOTEFILENAME, m_contentsfileStr.get(position));  // edtText�� ���� �������� �κ�
			startActivity(intent);
				
			//  Toast.makeText(EmotionListActivity.this, "hi", Toast.LENGTH_SHORT).show();
		  }
		});
		
		m_listview.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) 
	        {
				deletepos = position;
				showDialog(DIALOG_DELETE_YES_NO_MESSAGE);
				
				return false;
	        }
		});
		
		
		// ��Ʈ ��ư�� �������� �׳� ��Ʈ�� ���
		m_NoteBtn.setOnClickListener(new View.OnClickListener() {			
				@Override
			public void onClick(View v){
					
				Intent intent = new Intent(EmotionListActivity.this, EmotionalDiaryActivity.class); 
				startActivity(intent);
			}
				
		});
	}
	
	private void DeleteListItem(int pos)
	{
		// ����Ʈ ��� �̷��� �ϸ� ������ �ȴ� 
		m_emoarr.remove(pos);
		EmoAdapter adapter = new EmoAdapter(this, R.layout.list_item, m_emoarr);
		m_listview.setAdapter(adapter);
		m_listview.refreshDrawableState();
		
		
		// ���� ������ header���� ������ֱ� 
		int cursize =m_contentsfileStr.size();
		if((cursize -1)== 0)
		{
			deleteFile(m_contentsfileStr.get(pos));
	        
	        m_contentsfileStr.remove(pos);
	        
	        deleteFile("header.dat");
	       
		}
		else
		{
			deleteFile(m_contentsfileStr.get(pos));
	      
	        
	        m_contentsfileStr.remove(pos);
	        
	        try {
	        	FileOutputStream fos  = openFileOutput("header.dat", Context.MODE_PRIVATE);
				String headerfilecontents;
				headerfilecontents = m_contentsfileStr.size() + "\n";
				
				for(int i=0; i< m_contentsfileStr.size(); i++)
				{
					headerfilecontents = headerfilecontents+ m_contentsfileStr.get(i)+"\n";
					
				}
				fos.write(headerfilecontents.getBytes());
				
				fos.close();
				
			} 
	        catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
		}
	}
	
	
	//����� �����͸� �д� �Լ� 
	private void ReadSavedData()
	{
		// 1. header file�� �д´�. 
		// 2. header file���� ����� ������ ���� ����Ʈ�� �д´�. 
		
		String FILENAME = "header.dat";
		FileInputStream fis; 
		//String[] ContentsStr=null;
		int numberofContents =0;
		
		int i=0;	
		// �����̸� ������ emosave000 ~emosave999 �����̴�. 
		try {
			fis = openFileInput(FILENAME);							// ���� ����
			byte[] buffer = new byte[50000];						// ���� ����
			fis.read(buffer);										// ���� �� ���ϳ����� �д´�. 
			String contents = new String(buffer);					// ���۸� String���� ��ȯ
			String[] splitStr = contents.split("\n");				// �ٹٲ� ������ split�� �Ѵ�. 
														// for���� ���� i
	    	numberofContents = Integer.parseInt( splitStr[0] ); // ������ ����� ���ڸ�ŭ for���� ����. 
	    	
	    	// for���� ���鼭 ��� �д´�. 
	    	for(i=0; i < numberofContents; i++)
	    	{
	    		m_contentsfileStr.add(splitStr[i+1]); 
	    	}
	    	
	    	//���� �ݱ� 
	    	fis.close();
	       	
	    	
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			
		}
    	
		//���� ����ŭ for���� ���鼭 ������ �д´�. 
		for(i = 0; i< numberofContents; i++)
		{
			try {
				fis = openFileInput(m_contentsfileStr.get(i));
				byte[] buffer = new byte[50000];
			
				int readsize = fis.read(buffer);
				byte[] buffer2 = new byte[readsize];
				
				int j; 
				
				//���� ���� 
				for(j=0; j< readsize;j++)
					buffer2[j] = buffer[j];
				
				String contents = new String(buffer2);
				String[] splitStr = contents.split("\n");	
				
				String time = splitStr[0];
				String day = splitStr[1];
				String year = splitStr[2];
				int emoid = Integer.parseInt( splitStr[3] );
				// ������ ������
				String econtents ="";
				if(splitStr.length >= 5){
					econtents = splitStr[4];
				}
				
				for(j=5; j< splitStr.length; j++)
					econtents = econtents + "\n"+ splitStr[j];
				
				EmoInfo e1 = new EmoInfo(time, day,year,emoid,econtents);
				m_emoarr.add(e1);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
		}
		
	}
	
	protected Dialog onCreateDialog(int id) {
		switch (id) {
        case DIALOG_DELETE_YES_NO_MESSAGE:
            return new AlertDialog.Builder(EmotionListActivity.this)
                .setIcon(R.drawable.fear1)
                .setTitle("���� Ȯ��")
                .setPositiveButton("��", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //ok��ư�� �������� ����� �����Դϴ�.
                    
                    	DeleteListItem(deletepos);
                    	Toast toast = Toast.makeText(EmotionListActivity.this, "������ �Ϸ� �Ǿ����ϴ�.", Toast.LENGTH_LONG);
                    	toast.show();
                    	
                    }
                })
                .setNegativeButton("�ƴϿ�", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //cancel��ư�� �������� ����� �����Դϴ�.
                    	Toast toast = Toast.makeText(EmotionListActivity.this, "��� �Ǿ����ϴ�.", Toast.LENGTH_LONG);
                    	toast.show();
                    }
                })
                .create();
            
		default:
			return null;
        }

    }
	
	private class EmoAdapter extends ArrayAdapter<EmoInfo> {
	
		private ArrayList<EmoInfo> items;
		 
		public EmoAdapter(Context context, int textViewResourceId, ArrayList<EmoInfo> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.list_item, null);
			}
			
			
			EmoInfo p = items.get(position);
			if (p != null) {
				TextView time = (TextView) v.findViewById(R.id.list_time);
				TextView day = (TextView) v.findViewById(R.id.list_day);
				TextView year = (TextView) v.findViewById(R.id.list_year);
				ImageView emoicon = (ImageView) v.findViewById(R.id.list_emotionIcon);
				TextView contents = (TextView) v.findViewById(R.id.list_contents);
				
				if (time != null){
					time.setText(p.getTime());                           
				}
				if(day != null){
					day.setText(p.getDay());           
				}
				if(year != null){
					year.setText(p.getYear());           
				}
				if(emoicon != null){
					emoicon.setBackgroundDrawable(getResources().getDrawable(EmotionalDiaryActivity.EMOTION_TABLE[p.getEmoid()]));
				}
				if(contents != null){
					contents.setText(p.getContents());           
				}
				
				
				
			}
			
			
			return v;
		}
	}
	
	// �̸�Ƽ�� ������ �����ϴ� Ŭ���� 
	class EmoInfo {
		
		private String m_Time;
		private String m_Day;
		private String m_year;
		private int	m_emotionid = -1;
		private String m_contents;
		 
		public EmoInfo(String _time, String _day,String _year,int _emoid,String _contents){
			this.m_Time = _time;
			this.m_Day = _day;
			this.m_year = _year;
			this.m_emotionid = _emoid;
			this.m_contents = _contents;
			
		}
		 
		public String getTime() {
			return m_Time;
		}
		 
		public String getDay() {
			return m_Day;
			}
		
		public String getYear() {
			return m_year;
			}
		
		public int getEmoid() {
			return m_emotionid;
			}
		
		public String getContents() {
			return m_contents;
		}
		 
	}
	
}
