package Emotion.Diary;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.Random;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;




import Emotion.Diary.EmotionListActivity.EmoInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class EmotionalDiaryActivity extends BaseActivity {
	private AsyncFacebookRunner mAsyncRunner; 	// 페이스북 비동기 요청을 위한 객체
	private ProgressDialog progressDialog; 		// 진행상태 다이얼로그
	private String[] emoticons;					// 이모티콘 파일명을 담을 배열
	private int selectedEmoNumber = 0;			// 선택된 이미티콘 번호
	
    /** Called when the activity is first created. */
	private static final int DIALOG_YES_NO_MESSAGE = 1;
	
	static final int[] EMOTION_TABLE = new int[] {
		R.drawable.angry1,			// angry	0
		R.drawable.angry2,			// angry	1
		R.drawable.angry3,			// angry	2
		R.drawable.fear1,			// fear		3
		R.drawable.fear2,			// fear		4
		R.drawable.fear2,			// fear		5
		R.drawable.neutral1,			// neutral	6
		R.drawable.neutral2,			// neutral	7
		R.drawable.neutral3,			// neutral	8
		R.drawable.sad1,			// sad		9
		R.drawable.sad2,			// sad		10
		R.drawable.sad3,			// sad		11
		R.drawable.smile1,			// smile	12
		R.drawable.smile2,			// smile	13
		R.drawable.smile3,			// smile	14
		R.drawable.surprise1,			// surprise	15
		R.drawable.suprise2,			// surprise	16
		R.drawable.surprise3			// surprise	17
	     };
	
	ImageButton m_emotionBtn;		// 감성 연결 버튼
	ImageButton m_facebookBtn;		// 페이스북 연동 버튼
	ImageButton m_twitterBtn;		// 트위터 연동 버튼
	ImageButton m_galleryBtn;		// 겔러리 버튼
	ImageButton m_saveBtn;			// 저장 버튼
	ImageButton m_listBtn;			// 리스트 버튼
	ImageButton m_deleteBtn;		// 삭제 버튼
	
	TextView m_daytext;
	TextView m_monthyeartext;
	TextView m_timetext;
	
	EditText m_edittext;
	
	DbAdapter m_dbap;				// sqlite연동 클래스
	int m_emoIcon = 6;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //m_dbap = new DbAdapter(this);
        
        // 페이스북 인증 설정
        setFaceBookAuth();
        // arrays xml에서 이모티콘 파일명들을 배열로 가져오자
        emoticons = getResources().getStringArray(R.array.emoticons);
        
        m_emotionBtn = (ImageButton)findViewById(R.id.EmotionIconBtn);
        m_facebookBtn = (ImageButton)findViewById(R.id.facebookBtn);
        m_twitterBtn = (ImageButton)findViewById(R.id.TwitterBtn);
        m_galleryBtn = (ImageButton)findViewById(R.id.GalleryBtn);
        m_saveBtn = (ImageButton)findViewById(R.id.SaveBtn);
        m_listBtn = (ImageButton)findViewById(R.id.ListBtn);
        m_deleteBtn = (ImageButton)findViewById(R.id.DeleteBtn);
        
        m_daytext = (TextView)findViewById(R.id.daytext);
        m_monthyeartext = (TextView)findViewById(R.id.monthyeartext);
        m_timetext = (TextView)findViewById(R.id.timetext);
        
        m_edittext = (EditText)findViewById(R.id.textEdit);
        
        CurrentTimeSetting();
        m_emotionBtn.setBackgroundDrawable(getResources().getDrawable(EMOTION_TABLE[m_emoIcon]));
        
        
		// 데이터를 가져올동안 보여줄 프로그레스다이얼로그 설정
		progressDialog = new ProgressDialog(this);
		progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
  
        
        // System.out.println("현재 월: " + (oCalendar.get(Calendar.MONTH) + 1));
       // System.out.println("현재 일: " +  oCalendar.get(Calendar.DAY_OF_MONTH));
        // 감성 버튼이 눌렸을때 이벤트 정의
        m_emotionBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v){
				
				Random oRandom = new Random();
				
			    // 1~10까지의 정수를 랜덤하게 출력
			    int i = oRandom.nextInt(17);
			    // 랜덤번호를 이모티콘 선택 번호에 저장
			    selectedEmoNumber = i;
			    m_emoIcon = i;
				m_emotionBtn.setBackgroundDrawable(getResources().getDrawable(EMOTION_TABLE[i]));
				
			}
			
		});
        
        // 페이스북 버튼이 눌렸을때 이벤트 정의
        m_facebookBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v){
				// 메시지를 가져온다.
				CharSequence msg = m_edittext.getText();				
				if( checkMessage(msg) == false ){
					return;
				}			
				
				if(progressDialog.isShowing() != true){
					progressDialog.setMessage("전송중...");
					progressDialog.show();
				}
				
				// Bundle 이용해 사진 url을 실어 보내주면 됩니다. photo_upload permission도 추가되야 합니다.
				// graph api에서 json의 obejct형을  보고 params를 설정해줍니다.
            	Bundle params = new Bundle();
            	// 여기서 이미지 주소를 넣어주시면 됩니다.
            	// 이미지 url과 이미지파일명을 붙여 image url을 만든다.
            	params.putString("picture", IMAGE_URL + emoticons[selectedEmoNumber]);
                params.putString("caption", "감성이미티콘");		// 캡션을 달아줍니다.
                params.putString("message", msg.toString() );	// 메세지를  달아줍니다.

                // 나에게 post wall 을 합니다.
                // 친구에게 보낼경우는 me가 아닌 친구 id를 넣으면 됩니다.
                mAsyncRunner.request("me/feed", params, "POST",
                        new PhotoUploadListener(), null);
			}
			
		});
        
        // 트위터 버튼이 눌렸을 때 이벤트 정의
        m_twitterBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v){
				FileOutputStream fos2;
				try {
					fos2 = openFileOutput("header.dat", Context.MODE_PRIVATE);
					String fileContents =  "1"+"\n"+"emosave000"+"\n";
					fos2.write(fileContents.getBytes());
					fos2.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				FileOutputStream fos;
				try {
					fos = openFileOutput("emosave000", Context.MODE_PRIVATE);
					String fileContents =  "12:00"+"\n"+"28"+"\n"+"11"+"\n"+"0"+"\n"+"alkvkjhasdkjfalskjdflkasjdlfkasjdlkfjsadl;kfjs;lkdfjasl;kdfj";
					fos.write(fileContents.getBytes());
					fos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*Cursor c = m_dbap.fetchAllBooks();
				int i = c.getColumnCount();
				String day = c.getString(c.getColumnIndex(DbAdapter.KEY_DAY));
				String time = c.getString(c.getColumnIndex(DbAdapter.KEY_TIME));
				String month = c.getString(c.getColumnIndex(DbAdapter.KEY_MONTH));
				String emoid = c.getString(c.getColumnIndex(DbAdapter.KEY_EMOTIONID));
				String contents = c.getString(c.getColumnIndex(DbAdapter.KEY_TEXT));
				*/
			}
			
		});
        
        // 겔러리 버튼이 눌렸을 때 이벤트 정의
        m_galleryBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v){
				
			}
			
		});
        
        // 저장 버튼을 눌렀을 때 이벤트 정의
        m_saveBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v){
				CharSequence msg = EmotionalDiaryActivity.this.m_edittext.getText();
				// 메세지 유무 체크
				if( checkMessage(msg) == false ){
					return;
				}
				
				
				// 다이얼로그로 확인후 저장처리 하자
				new AlertDialog.Builder(EmotionalDiaryActivity.this)
				.setMessage("저장하시겠습니까?").setPositiveButton("저장", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						
						
						String FILENAME = "header.dat";
						FileInputStream fis; 
						String[] ContentsStr=null;
						int numberofContents =0;
						String newfilename = "emosave000";
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
					    	ContentsStr = new String[numberofContents];				// 배열 할당
					    	
					    	// for문을 돌면서 모두 읽는다. 
					    	for(i=0; i < numberofContents; i++)
					    	{
					    		ContentsStr[i] = splitStr[i+1]; 
					    	}
					    	
					    	//파일 닫기 
					    	fis.close();
					    	String filenum = ContentsStr[ContentsStr.length-1].substring(7,ContentsStr[ContentsStr.length-1].length());
					    	int nuwnum = Integer.parseInt( filenum )+1;
					    	newfilename = "emosave"+nuwnum;
					    	numberofContents= numberofContents+1;
					    	
					    	
						} catch (FileNotFoundException e) {
							
							// 파일이 없는경우 그냥 0짜리 파일을 생성해준다. 
							newfilename = "emosave000";
							numberofContents = 1;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    	
						
						
						String fileContents= numberofContents +"\n";
						
						for(i=0; i<(numberofContents-1); i++ )
						{
							fileContents = fileContents + ContentsStr[i]+"\n";
						}
						
						// 새로운 파일 명을 끝에 넣어준다. 
						fileContents = fileContents + ""+ newfilename+"\n";
						
						//FileOutputStream fos;
						try {
							FileOutputStream fos2 = openFileOutput(FILENAME, Context.MODE_PRIVATE);
							fos2.write(fileContents.getBytes());
							fos2.close();
							
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			  			
			  			//////////////////////////헤더부 완료////////////////
			  			
			  			
			  			
			  			// 실제 내용 저장 
			  			String ttime  =  m_timetext.getText().toString();
						String tday  =  m_daytext.getText().toString();
						
						String tmonth  =  m_monthyeartext.getText().toString().substring(0,2);
						String emoid  =  m_emoIcon+"";
						String tcontents  =  m_edittext.getText().toString();
						
			  			fileContents =ttime + "\n" + tday + "\n" + tmonth + "\n" +emoid +"\n" + tcontents;
			  			FileOutputStream fos;
						try {
							fos = openFileOutput(newfilename, Context.MODE_PRIVATE);
							fos.write(fileContents.getBytes());
							fos.close();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			  			catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			  			
			  			Intent intent = new Intent(EmotionalDiaryActivity.this, EmotionListActivity.class); 
						startActivity(intent);
					}
				}).setNegativeButton("취소", null).create().show();
			
 			}
			
		});
        
        // 리스트 버튼을 눌렀을 때 이벤트 정의
        m_listBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v){	
				Intent intent = new Intent(EmotionalDiaryActivity.this, EmotionListActivity.class); 
				startActivity(intent);
				
				
			}
			
		});
        
        // 삭제 버튼을 눌렀을 때 이벤트 정의
        m_deleteBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v){
				//삭제 다이얼로그 띄워주고 환인 되었으면 삭제를 수행 해준다. 
				showDialog(DIALOG_YES_NO_MESSAGE);
				
			}
			
		});
        
        Intent intent = getIntent(); 
        String intmsg =null;
        Bundle b=null;
        if(intent != null)
        	b=intent.getExtras();
        
        if(b !=null)
        	intmsg = b.getString(EmotionListActivity.NOTEFILENAME);
        
        
        //String intmsg = intent.getExtras().getString(EmotionListActivity.NOTEFILENAME).toString();
        if(intmsg != null)
        {
        	try {
        		FileInputStream fis = openFileInput(intmsg);
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
				String year = splitStr[2]+" 2011";
				int emoid = Integer.parseInt( splitStr[3] );
				String econtents = splitStr[4];
				
				for(j=5; j< splitStr.length; j++)
					econtents = econtents + "\n"+ splitStr[j];
				
				 m_daytext.setText(day.toCharArray(),0,day.length());
				 m_monthyeartext.setText(year.toCharArray(),0,year.length());
				 m_timetext.setText(time.toCharArray(),0,time.length());
				 m_emotionBtn.setBackgroundDrawable(getResources().getDrawable(EMOTION_TABLE[emoid]));
				 m_emoIcon = emoid;
				 m_edittext.setText(econtents.toCharArray(),0,econtents.length());
				 
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
        case DIALOG_YES_NO_MESSAGE:
            return new AlertDialog.Builder(EmotionalDiaryActivity.this)
                .setIcon(R.drawable.fear1)
                .setTitle("삭제 확인")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //ok버튼을 눌렀을때 실행될 내용입니다.
                    
                    	CurrentTimeSetting();
                    	m_edittext.setText("");
                    	m_emotionBtn.setBackgroundDrawable(getResources().getDrawable(EMOTION_TABLE[6]));
                    	Toast toast = Toast.makeText(EmotionalDiaryActivity.this, "모든값이 삭제 되었습니다.", Toast.LENGTH_LONG);
                    	toast.show();
                    	
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //cancel버튼을 눌렀을때 실행될 내용입니다.
                    	Toast toast = Toast.makeText(EmotionalDiaryActivity.this, "취소 되었습니다.", Toast.LENGTH_LONG);
                    	toast.show();
                    }
                })
                .create();
            
		default:
			return null;
        }

    }
	
	protected void CurrentTimeSetting()
	{
		Calendar oCalendar = Calendar.getInstance( );
	    
		int day = oCalendar.get(Calendar.DAY_OF_MONTH);
		String dayString;
		if( day >= 10)
			dayString = day+"";
		else
			dayString = "0"+day;
		
		
        m_daytext.setText(dayString.toCharArray(),0,dayString.length());
		 
        int month = oCalendar.get(Calendar.MONTH)+1;
        int year = oCalendar.get(Calendar.YEAR);
        
        String monthStr;
        String yearStr;
      
        if(month <10)
        	monthStr = "0"+ month; 
        else
        	monthStr = month +"";
        		
        yearStr = year+"";
        
        String monthyearString = monthStr+" "+yearStr;
        m_monthyeartext.setText(monthyearString.toCharArray(),0,monthyearString.length());
        
        int hour =  oCalendar.get(Calendar.HOUR_OF_DAY);
        int minute =  oCalendar.get(Calendar.MINUTE);
        
        String hourStr;
        String minuteStr;
      
        if(hour >= 10)
        	hourStr =  hour+""; 
        else
        	hourStr = "0"+hour;
        
        if(minute >= 10)
        	minuteStr =  minute+""; 
        else
        	minuteStr = "0"+minute;
        
        String timeString = hourStr+":"+minuteStr;
        m_timetext.setText(timeString.toCharArray(),0,timeString.length());
        
	}
	
	/**
	 * 페이스북 인증토큰 설정
	 */
	private void setFaceBookAuth(){
		 //	공유 환경 설정에서 액세스 토큰 가져오기
		SharedPreferences mPrefs = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);
		// 페이스북 인증 토큰 설정
		if (access_token != null) {
			facebook.setAccessToken(access_token); // 토큰 설정
		}
		if (expires != 0) {
			facebook.setAccessExpires(expires); // 토큰 만료 설정
		}
		mAsyncRunner = new AsyncFacebookRunner(facebook);
	}
	
	private boolean checkMessage(CharSequence msg){
		// 메세지가 비였으면 토스트를 굽는다.
		if(TextUtils.isEmpty(msg)){
			Toast.makeText(EmotionalDiaryActivity.this,
					"내용을 입력하세요", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	/**
	 *	사진 업로드 다이얼로그에 들어갈 요청 리스너
	 */
	private class PhotoUploadListener implements
			com.facebook.android.AsyncFacebookRunner.RequestListener {

		/**
		 * Called when the wall post request has completed
		 */

		@Override
		public void onComplete(String response, Object state) {
			// TODO Auto-generated method stub
			Log.d("myfacebook", "response-->" + response);
			progressDialog.dismiss();	// 로딩 다이얼로그를 닫자
			// ui 쓰레드로
			EmotionalDiaryActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                	Util.showAlert(EmotionalDiaryActivity.this, 
                			"성공",  "메세지를 전송했습니다.");	
                	m_edittext.setText("");	// 전송완료했으므로 메세지는 지워준다.
                }
            });			
		}

		@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
		}
	}		
	
}