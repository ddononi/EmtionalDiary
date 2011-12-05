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
 *	single sign on (��������) �α��� ó�� Ŭ����
 */
public class LoginActivity extends BaseActivity{
	private AsyncFacebookRunner mAsyncRunner; // �񵿱� ��û ó���� ���� ��ü

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		mAsyncRunner = new AsyncFacebookRunner(facebook);

		facebook.authorize(LoginActivity.this, PERMISSIONS, new LoginDialogListener());
		Log.d("myfacebook", "�α��� ó����!");
	}
	
	/** 
	 * �α��� ������ ó���� �ݹ� ó��  
	 * sso ó���� ��ȯ�Ǵ� requestCode ������ ����ó���� �Ѵ�.
	 */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 
        Log.d("myfacebook", "onActivityResult(): " + requestCode);
        // ��ȯ�� ����ڵ带 ���̽��� �����ݹ鿡 �־��ش�.
        facebook.authorizeCallback(requestCode, resultCode, data);
      }	

	/**
	 * �α��δ��̾�α� ������ Ŭ����
	 */
	private final class LoginDialogListener implements
			com.facebook.android.Facebook.DialogListener {

		@Override
		public void onComplete(Bundle values) {
			// TODO Auto-generated method stub
			Log.d("myfacebook", "�α��� �Ϸ�~~!");
			//doStartService();	// �˶����� ����
           
            // ���� ȯ�� ������ AccessToken �� AccessExpires�� �����Ѵ�.
            SharedPreferences mPrefs = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString("access_token", facebook.getAccessToken());
            editor.putLong("access_expires", facebook.getAccessExpires());
            Log.d("myfacebook", "access_token------->" + facebook.getAccessToken());
            editor.commit();  	// Ŀ������  ���� �Ϸ�          
            
			Intent intent = new Intent(LoginActivity.this, EmotionalDiaryActivity.class);
			startActivity(intent);	
			finish();	// ���� ��Ƽ��Ƽ�� ����

		}

		@Override
		public void onFacebookError(FacebookError e) {
			// TODO Auto-generated method stub
			Log.d("myfacebook", "�α��� ����!");
			new AlertDialog.Builder(LoginActivity.this)
			.setMessage("�α��� ����").create().show();				
		}

		@Override
		public void onError(DialogError e) {
			// TODO Auto-generated method stub
			Log.d("myfacebook", "�α��� ����!");
			new AlertDialog.Builder(LoginActivity.this)
			.setMessage("�α��� ����").create().show();				
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			Log.d("myfacebook", "�α��� ���!");
			new AlertDialog.Builder(LoginActivity.this)
			.setMessage("�α��� ���").create().show();				
		}

		/**
		 * �㺭�� ������Ʈ�� �˸��� ���� ����
		 */
		/*
		private void doStartService() {
			// TODO Auto-generated method stub
			// ���񽺷� �˶�����
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
     * ����ȭ�鿡���� �ɼ� �޴� ó�� ����
     */
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		//return super.onCreateOptionsMenu(menu);
    	return false;
	}

    
}
