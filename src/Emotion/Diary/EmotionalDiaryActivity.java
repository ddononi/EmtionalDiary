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
	private AsyncFacebookRunner mAsyncRunner; 	// ���̽��� �񵿱� ��û�� ���� ��ü
	private ProgressDialog progressDialog; 		// ������� ���̾�α�
	private String[] emoticons;					// �̸�Ƽ�� ���ϸ��� ���� �迭
	private int selectedEmoNumber = 0;			// ���õ� �̹�Ƽ�� ��ȣ
	
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
	
	ImageButton m_emotionBtn;		// ���� ���� ��ư
	ImageButton m_facebookBtn;		// ���̽��� ���� ��ư
	ImageButton m_twitterBtn;		// Ʈ���� ���� ��ư
	ImageButton m_galleryBtn;		// �ַ��� ��ư
	ImageButton m_saveBtn;			// ���� ��ư
	ImageButton m_listBtn;			// ����Ʈ ��ư
	ImageButton m_deleteBtn;		// ���� ��ư
	
	TextView m_daytext;
	TextView m_monthyeartext;
	TextView m_timetext;
	
	EditText m_edittext;
	
	DbAdapter m_dbap;				// sqlite���� Ŭ����
	int m_emoIcon = 6;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //m_dbap = new DbAdapter(this);
        
        // ���̽��� ���� ����
        setFaceBookAuth();
        // arrays xml���� �̸�Ƽ�� ���ϸ���� �迭�� ��������
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
        
        
		// �����͸� �����õ��� ������ ���α׷������̾�α� ����
		progressDialog = new ProgressDialog(this);
		progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
  
        
        // System.out.println("���� ��: " + (oCalendar.get(Calendar.MONTH) + 1));
       // System.out.println("���� ��: " +  oCalendar.get(Calendar.DAY_OF_MONTH));
        // ���� ��ư�� �������� �̺�Ʈ ����
        m_emotionBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v){
				
				Random oRandom = new Random();
				
			    // 1~10������ ������ �����ϰ� ���
			    int i = oRandom.nextInt(17);
			    // ������ȣ�� �̸�Ƽ�� ���� ��ȣ�� ����
			    selectedEmoNumber = i;
			    m_emoIcon = i;
				m_emotionBtn.setBackgroundDrawable(getResources().getDrawable(EMOTION_TABLE[i]));
				
			}
			
		});
        
        // ���̽��� ��ư�� �������� �̺�Ʈ ����
        m_facebookBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v){
				// �޽����� �����´�.
				CharSequence msg = m_edittext.getText();				
				if( checkMessage(msg) == false ){
					return;
				}			
				
				if(progressDialog.isShowing() != true){
					progressDialog.setMessage("������...");
					progressDialog.show();
				}
				
				// Bundle �̿��� ���� url�� �Ǿ� �����ָ� �˴ϴ�. photo_upload permission�� �߰��Ǿ� �մϴ�.
				// graph api���� json�� obejct����  ���� params�� �������ݴϴ�.
            	Bundle params = new Bundle();
            	// ���⼭ �̹��� �ּҸ� �־��ֽø� �˴ϴ�.
            	// �̹��� url�� �̹������ϸ��� �ٿ� image url�� �����.
            	params.putString("picture", IMAGE_URL + emoticons[selectedEmoNumber]);
                params.putString("caption", "�����̹�Ƽ��");		// ĸ���� �޾��ݴϴ�.
                params.putString("message", msg.toString() );	// �޼�����  �޾��ݴϴ�.

                // ������ post wall �� �մϴ�.
                // ģ������ �������� me�� �ƴ� ģ�� id�� ������ �˴ϴ�.
                mAsyncRunner.request("me/feed", params, "POST",
                        new PhotoUploadListener(), null);
			}
			
		});
        
        // Ʈ���� ��ư�� ������ �� �̺�Ʈ ����
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
        
        // �ַ��� ��ư�� ������ �� �̺�Ʈ ����
        m_galleryBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v){
				
			}
			
		});
        
        // ���� ��ư�� ������ �� �̺�Ʈ ����
        m_saveBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v){
				CharSequence msg = EmotionalDiaryActivity.this.m_edittext.getText();
				// �޼��� ���� üũ
				if( checkMessage(msg) == false ){
					return;
				}
				
				
				// ���̾�α׷� Ȯ���� ����ó�� ����
				new AlertDialog.Builder(EmotionalDiaryActivity.this)
				.setMessage("�����Ͻðڽ��ϱ�?").setPositiveButton("����", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						
						
						String FILENAME = "header.dat";
						FileInputStream fis; 
						String[] ContentsStr=null;
						int numberofContents =0;
						String newfilename = "emosave000";
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
					    	ContentsStr = new String[numberofContents];				// �迭 �Ҵ�
					    	
					    	// for���� ���鼭 ��� �д´�. 
					    	for(i=0; i < numberofContents; i++)
					    	{
					    		ContentsStr[i] = splitStr[i+1]; 
					    	}
					    	
					    	//���� �ݱ� 
					    	fis.close();
					    	String filenum = ContentsStr[ContentsStr.length-1].substring(7,ContentsStr[ContentsStr.length-1].length());
					    	int nuwnum = Integer.parseInt( filenum )+1;
					    	newfilename = "emosave"+nuwnum;
					    	numberofContents= numberofContents+1;
					    	
					    	
						} catch (FileNotFoundException e) {
							
							// ������ ���°�� �׳� 0¥�� ������ �������ش�. 
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
						
						// ���ο� ���� ���� ���� �־��ش�. 
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
			  			
			  			//////////////////////////����� �Ϸ�////////////////
			  			
			  			
			  			
			  			// ���� ���� ���� 
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
				}).setNegativeButton("���", null).create().show();
			
 			}
			
		});
        
        // ����Ʈ ��ư�� ������ �� �̺�Ʈ ����
        m_listBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v){	
				Intent intent = new Intent(EmotionalDiaryActivity.this, EmotionListActivity.class); 
				startActivity(intent);
				
				
			}
			
		});
        
        // ���� ��ư�� ������ �� �̺�Ʈ ����
        m_deleteBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v){
				//���� ���̾�α� ����ְ� ȯ�� �Ǿ����� ������ ���� ���ش�. 
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
				
				//���� ���� 
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
                .setTitle("���� Ȯ��")
                .setPositiveButton("��", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //ok��ư�� �������� ����� �����Դϴ�.
                    
                    	CurrentTimeSetting();
                    	m_edittext.setText("");
                    	m_emotionBtn.setBackgroundDrawable(getResources().getDrawable(EMOTION_TABLE[6]));
                    	Toast toast = Toast.makeText(EmotionalDiaryActivity.this, "��簪�� ���� �Ǿ����ϴ�.", Toast.LENGTH_LONG);
                    	toast.show();
                    	
                    }
                })
                .setNegativeButton("�ƴϿ�", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //cancel��ư�� �������� ����� �����Դϴ�.
                    	Toast toast = Toast.makeText(EmotionalDiaryActivity.this, "��� �Ǿ����ϴ�.", Toast.LENGTH_LONG);
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
	 * ���̽��� ������ū ����
	 */
	private void setFaceBookAuth(){
		 //	���� ȯ�� �������� �׼��� ��ū ��������
		SharedPreferences mPrefs = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);
		// ���̽��� ���� ��ū ����
		if (access_token != null) {
			facebook.setAccessToken(access_token); // ��ū ����
		}
		if (expires != 0) {
			facebook.setAccessExpires(expires); // ��ū ���� ����
		}
		mAsyncRunner = new AsyncFacebookRunner(facebook);
	}
	
	private boolean checkMessage(CharSequence msg){
		// �޼����� ������ �佺Ʈ�� ���´�.
		if(TextUtils.isEmpty(msg)){
			Toast.makeText(EmotionalDiaryActivity.this,
					"������ �Է��ϼ���", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	/**
	 *	���� ���ε� ���̾�α׿� �� ��û ������
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
			progressDialog.dismiss();	// �ε� ���̾�α׸� ����
			// ui �������
			EmotionalDiaryActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                	Util.showAlert(EmotionalDiaryActivity.this, 
                			"����",  "�޼����� �����߽��ϴ�.");	
                	m_edittext.setText("");	// ���ۿϷ������Ƿ� �޼����� �����ش�.
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