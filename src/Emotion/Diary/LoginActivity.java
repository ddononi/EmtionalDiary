package Emotion.Diary;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.FacebookError;


/**
 *	single sign on (통합인증) 로그인 처리 클래스
 */
public class LoginActivity extends BaseActivity{
	private AsyncFacebookRunner mAsyncRunner; // 비동기 요청 처리를 위한 객체

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		mAsyncRunner = new AsyncFacebookRunner(facebook);

		facebook.authorize(LoginActivity.this, PERMISSIONS, new LoginDialogListener());
		Log.d("myfacebook", "로그인 처리중!");
	}
	
	/** 
	 * 로그인 페이지 처리후 콜백 처리  
	 * sso 처리후 반환되는 requestCode 값으로 인증처리를 한다.
	 */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 
        Log.d("myfacebook", "onActivityResult(): " + requestCode);
        // 반환된 결과코드를 페이스북 인증콜백에 넣어준다.
        facebook.authorizeCallback(requestCode, resultCode, data);
      }	

	/**
	 * 로그인다이얼로그 리스너 클래스
	 */
	private final class LoginDialogListener implements
			com.facebook.android.Facebook.DialogListener {

		@Override
		public void onComplete(Bundle values) {
			// TODO Auto-generated method stub
			Log.d("myfacebook", "로그인 완료~~!");
			//doStartService();	// 알람서비스 시작
           
            // 공유 환경 설정에 AccessToken 및 AccessExpires을 저장한다.
            SharedPreferences mPrefs = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString("access_token", facebook.getAccessToken());
            editor.putLong("access_expires", facebook.getAccessExpires());
            Log.d("myfacebook", "access_token------->" + facebook.getAccessToken());
            editor.commit();  	// 커밋으로  저장 완료          
            
			Intent intent = new Intent(LoginActivity.this, EmotionalDiaryActivity.class);
			startActivity(intent);	
			finish();	// 현재 엑티비티는 종료

		}

		@Override
		public void onFacebookError(FacebookError e) {
			// TODO Auto-generated method stub
			Log.d("myfacebook", "로그인 에러!");
			new AlertDialog.Builder(LoginActivity.this)
			.setMessage("로그인 에러").create().show();				
		}

		@Override
		public void onError(DialogError e) {
			// TODO Auto-generated method stub
			Log.d("myfacebook", "로그인 에러!");
			new AlertDialog.Builder(LoginActivity.this)
			.setMessage("로그인 에러").create().show();				
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			Log.d("myfacebook", "로그인 취소!");
			new AlertDialog.Builder(LoginActivity.this)
			.setMessage("로그인 취소").create().show();				
		}

		/**
		 * 담벼락 업데이트를 알리기 위한 서비스
		 */
		/*
		private void doStartService() {
			// TODO Auto-generated method stub
			// 서비스로 알람설정
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
			boolean isSetAlarm = sp.getBoolean("alarm", true);	
			if(isSetAlarm){
				Intent serviceIntent = new Intent(LoginActivity.this, MyService.class);
				stopService(serviceIntent);
				startService(serviceIntent);
				Log.i("myfacebook", "service start!!");
			}	
		}
		*/		

	}
    
	/**
     * 시작화면에서는 옵션 메뉴 처리 안함
     */
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		//return super.onCreateOptionsMenu(menu);
    	return false;
	}

    
}
