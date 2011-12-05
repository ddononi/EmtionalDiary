package Emotion.Diary;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * ��� �� �⺻ �޼ҵ� ���Ǹ� �����ϸ�
 * FACEBOOK API ���� �׻� ���Ǵ� facebook
 */
public class BaseActivity extends Activity {
	/*
	 * ���̽��� �� ��� ������ ���� �� ���̵� Ű ���� ����� �Ʒ� ���İ� ����. ���� �������Ҷ��� ����� Ű�� �ƴ϶� ������ ����Ű��
	 * ����ؾߵȴ�. example) $ keytool -exportcert -alias androiddebugkey -keystore
	 * "C:\Documents and Settin gs\ddononi\.android\debug.keystore" | openssl
	 * sha1 -binary | openssl base64 ������ Ű���� ���̽��� ������ ������
	 * https://developers.facebook.com/apps �� ����ϸ� �۾��̵� �������ִ�.
	 */								
	public static final String APP_ID = "102239696559238"; // ���̽��� ������ ���� �� ���̵�
	public static final String SHARED_PREFERENCE = "mypreference";
	
	// �̹����� ��ũ��ų �ӽ� �������Դϴ�. 
	// ���� ������ �������� �̹����� �÷������� �̿��Ͻø� �˴ϴ�.
	public static final String IMAGE_URL = "http://ddononi.cafe24.com/EmotionalDairy/images/";
	
	// ���̽����� ����ҷ��� �ݵ�� �����Ǿ���� �⺻ ��ü
	public static final Facebook facebook = new Facebook(APP_ID);
	private boolean isTwoClickBack = false; // �ι�Ŭ�� ���Ῡ��
	public static final int ZOOM_DEEP = 17; // ���� �� ����

	/*
	 * ���ٿ� ���� �۹̼�
	 * http://developers.facebook.com/docs/reference/api/permissions/
	 */
	public static final String[] PERMISSIONS = new String[] { "publish_stream",
			"read_stream", "offline_access", "user_checkins", "user_photos", "publish_checkins", "photo_upload" };


	/** ���ư �ι��̸� �� ���� */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/*
		 * back ��ư�̸� Ÿ�̸�(2��)�� �̿��Ͽ� �ٽ��ѹ� �ڷ� ���⸦ ������ ���ø����̼��� ���� �ǵ����Ѵ�.
		 */
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (!isTwoClickBack) {
					Toast.makeText(this, "'�ڷ�' ��ư�� �ѹ� �� ������ ����˴ϴ�.",
							Toast.LENGTH_SHORT).show();
					CntTimer timer = new CntTimer(2000, 1);
					timer.start();
				} else {
					moveTaskToBack(true);
					finish();
					return true;
				}

			}
		}
		return false;
	}

	// �ڷΰ��� ���Ḧ ���� Ÿ�̸�
	class CntTimer extends CountDownTimer {
		public CntTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			isTwoClickBack = true;
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			isTwoClickBack = false;
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			//Log.i("Test", " isTwoClickBack " + isTwoClickBack);
		}

	}

	/**
	 * �ε��߿� ȭ���� ȸ���ϸ� ������ �߻��ϱ� ������
	 * �Ϸᰡ �ɶ����� ȭ���� ��ٴ�.
	 */
	public void mLockScreenRotation() {
		// Stop the screen orientation changing during an event
		switch (this.getResources().getConfiguration().orientation) {
		case Configuration.ORIENTATION_PORTRAIT:
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		}
	}

	/**
	 * ȭ�� ��� ����
	 */
	public void unLockScreenRotation() {
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	}

}
