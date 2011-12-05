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

	 
	ListView m_listview; 			// 검색 목록을 출력해줄 listView 객체
	ArrayList<EmoInfo> m_emoarr;	// ListView에 출력해줄 정보를 저장하는 EmoInfo class ArrayList
	
	ArrayList<String> m_contentsfileStr=null;	// 파일명을 저장하고 있는 배열
	
	ImageButton m_NoteBtn;		// 노트로 돌아가는 버튼
	
	int deletepos;
	// 생성자
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Activity에 컨텐츠를 편집한 layout으로 설정한다.
		setContentView(R.layout.emotionlist);
		
		// layout에서 listview를 받아온다. 
		m_listview = (ListView) findViewById(R.id.listview01);
		m_NoteBtn = (ImageButton) findViewById(R.id.NoteBtn);
		// ArrayList 동적할당
		m_emoarr = new ArrayList<EmoInfo>();
		m_contentsfileStr= new ArrayList<String>();
		//파일로 부터 데이타를 읽는다. 
		ReadSavedData();
		
		// EmoAdapter생성
		EmoAdapter m_adapter = new EmoAdapter(this, R.layout.list_item, m_emoarr); // 어댑터를 생성합니다.
		
		// Adapter와 ListView연결	ㅁㄴㅇㅁㄴㅇㄹㄴ
		m_listview.setAdapter(m_adapter);
		
		// 리스트 뷰가 클릭 되었을때를 정의하는 이벤트
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
			intent.putExtra(NOTEFILENAME, m_contentsfileStr.get(position));  // edtText의 값을 가져오는 부분
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
		
		
		// 노트 버튼이 눌렸을때 그냥 노트로 돌어감
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
		// 리스트 뷰는 이렇게 하면 삭제가 된다 
		m_emoarr.remove(pos);
		EmoAdapter adapter = new EmoAdapter(this, R.layout.list_item, m_emoarr);
		m_listview.setAdapter(adapter);
		m_listview.refreshDrawableState();
		
		
		// 실제 삭제및 header파일 만들어주기 
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
	
	
	//저장된 데이터를 읽는 함수 
	private void ReadSavedData()
	{
		// 1. header file을 읽는다. 
		// 2. header file에서 추출된 파일을 토대로 리스트를 읽는다. 
		
		String FILENAME = "header.dat";
		FileInputStream fis; 
		//String[] ContentsStr=null;
		int numberofContents =0;
		
		int i=0;	
		// 파일이름 형식은 emosave000 ~emosave999 까지이다. 
		try {
			fis = openFileInput(FILENAME);							// 파일 열기
			byte[] buffer = new byte[50000];						// 버퍼 설정
			fis.read(buffer);										// 버퍼 에 파일내용을 읽는다. 
			String contents = new String(buffer);					// 버퍼를 String으로 변환
			String[] splitStr = contents.split("\n");				// 줄바꿈 단위로 split을 한다. 
														// for문을 위한 i
	    	numberofContents = Integer.parseInt( splitStr[0] ); // 맨위의 헤더를 숫자만큼 for문을 돈다. 
	    	
	    	// for문을 돌면서 모두 읽는다. 
	    	for(i=0; i < numberofContents; i++)
	    	{
	    		m_contentsfileStr.add(splitStr[i+1]); 
	    	}
	    	
	    	//파일 닫기 
	    	fis.close();
	       	
	    	
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			
		}
    	
		//파일 수만큼 for문을 돌면서 파일을 읽는다. 
		for(i = 0; i< numberofContents; i++)
		{
			try {
				fis = openFileInput(m_contentsfileStr.get(i));
				byte[] buffer = new byte[50000];
			
				int readsize = fis.read(buffer);
				byte[] buffer2 = new byte[readsize];
				
				int j; 
				
				//내용 복사 
				for(j=0; j< readsize;j++)
					buffer2[j] = buffer[j];
				
				String contents = new String(buffer2);
				String[] splitStr = contents.split("\n");	
				
				String time = splitStr[0];
				String day = splitStr[1];
				String year = splitStr[2];
				int emoid = Integer.parseInt( splitStr[3] );
				// 내용이 있으면
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
                .setTitle("삭제 확인")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //ok버튼을 눌렀을때 실행될 내용입니다.
                    
                    	DeleteListItem(deletepos);
                    	Toast toast = Toast.makeText(EmotionListActivity.this, "삭제가 완료 되었습니다.", Toast.LENGTH_LONG);
                    	toast.show();
                    	
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //cancel버튼을 눌렀을때 실행될 내용입니다.
                    	Toast toast = Toast.makeText(EmotionListActivity.this, "취소 되었습니다.", Toast.LENGTH_LONG);
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
	
	// 이모티콘 정보를 저장하는 클래스 
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
