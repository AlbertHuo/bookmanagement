package com.practice.albert.bookmanagement;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.practice.albert.book.BookInfo;
import com.practice.albert.tool.LogTool;
import com.practice.albert.zbar.CameraManager;
import com.practice.albert.zbar.CameraPreview;
import com.practice.albert.zbar.DouBanBookInfoXmlParser;
import com.practice.albert.zbar.InactivityTimer;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.zbar.camera.ScanCallback;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator.
 */

public class ZBarScanActivity extends AppCompatActivity {

    private CameraPreview mPreviewView;

    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    private ImageView ivLightControl;
    private TextView tvLightStatus;
    private boolean flag = false;
    private CameraManager cameraManager;
    private InactivityTimer inactivityTimer;

    private Context mContext;
    private ProgressBar progressBar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan);
        initView();

        //隐藏标题栏 继承AppCompatActivity的解决方法
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mContext = this;
        mPreviewView = (CameraPreview) findViewById(R.id.capture_preview);
        ImageView mScanLine = (ImageView) findViewById(R.id.capture_scan_line);

        inactivityTimer = new InactivityTimer(this);

        progressBar = (ProgressBar) findViewById(R.id.download_progress_bar);
        LogTool.v("TAG","测试进入");

        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        animation.setDuration(4500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        mScanLine.startAnimation(animation);

        mPreviewView.setScanCallback(resultCallback);
        cameraManager = mPreviewView.mCameraManager;
//        startScanUnKnowPermission();
    }

    /**
     * Accept scan result.
     */
    private ScanCallback resultCallback = new ScanCallback() {
        @Override
        public void onScanResult(String result) {
            stopScan();
            inactivityTimer.onActivity();
            playBeepSoundAndVibrate();
            dispatch(result);
        }
    };

    private void initView() {
        findViewById(R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZBarScanActivity.this.finish();
            }
        });
        ivLightControl = (ImageView) findViewById(R.id.mo_scanner_light);
        tvLightStatus = (TextView) findViewById(R.id.light_status_tv);
        ivLightControl.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 闪光灯
                        if (!flag) { // 开启
                            flag = true;
                            ivLightControl.setImageResource(R.drawable.scan_open);
                            tvLightStatus.setText("关灯");
                            cameraManager.openLight();
                        } else {  // 关闭
                            flag = false;
                            ivLightControl.setImageResource(R.drawable.scan_close);
                            tvLightStatus.setText("开灯");
                            cameraManager.offLight();
                        }
                    }
                });

    }

    private void dispatch(String result) {
        flag = false;
        cameraManager.offLight();
        ivLightControl.setImageResource(R.drawable.scan_close);
        tvLightStatus.setText("开灯");

        //检查网络连接
        if(networkIsAvailable()) {
            //网络连接，查询并显示
            new DownloadBookInfoTask().execute(result);
            LogTool.v("TAG","result:"+result);
        } else {
            //网络未连接，显示扫描结果
            AlertDialog ad=new AlertDialog.Builder(this).create();
            ad.setTitle("扫描结果");
            ad.setIcon(R.mipmap.ic_launcher);
            ad.setMessage(result);
            ad.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startScanUnKnowPermission();
                }
            });
            ad.show();
            LogTool.v("TAG","result:"+result);
        }

    }

    /**
     * 初始化声音池
     */
    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.mo_scanner_beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    /**
     * 播放扫描结束声音
     */
    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        inactivityTimer.onResume();
        startScanUnKnowPermission();

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    public void onPause() {
        // Must be called here, otherwise the camera should not be released properly.
        stopScan();
        inactivityTimer.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * Do not have permission to request for permission and start scanning.
     */
    private void startScanUnKnowPermission() {
        AndPermission.with(this)
                .permission(Manifest.permission.CAMERA)
                .permission(Manifest.permission.VIBRATE)
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                        startScanWithPermission();
                    }

                    @Override
                    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                        AndPermission.defaultSettingDialog(ZBarScanActivity.this).show();
                    }
                })
                .start();
    }

    /**
     * There is a camera when the direct scan.
     */
    private void startScanWithPermission() {
        if (!mPreviewView.start()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.camera_failure)
                    .setMessage(R.string.camera_hint)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
    }

    /**
     * Stop scan.
     */
    private void stopScan() {
        mPreviewView.stop();
    }

    /**
     * 由于书本信息需要联网获取，因此要先检查网络状态
     * @return
     */
    private boolean networkIsAvailable() {
        boolean isConnected = false;

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if((info != null) && info.isConnected()) {
            isConnected = true;
        } else {
            Toast toast = Toast.makeText(this, R.string.msg_no_network, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        return isConnected;
    }
    // Implementation of AsyncTask used to download bookInfo from douban.com.
    private class DownloadBookInfoTask extends
            AsyncTask<String, Integer, BookInfo> {
        private int progressCount = 0;

        @Override
        protected BookInfo doInBackground(String... isbn) {
            try {
                DouBanBookInfoXmlParser bookInfo = new DouBanBookInfoXmlParser();
                return bookInfo.fetchBookInfoByXML(isbn[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(BookInfo result) {
            progressBar.setVisibility(View.INVISIBLE);
            if (result == null) {
                new android.app.AlertDialog.Builder(mContext)
                        .setMessage(R.string.msg_rescan_bookbar_info)
                        .setPositiveButton(R.string.confirm,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialoginterface,
                                            int i) {
                                        startScanUnKnowPermission();
                                    }
                                }).show();
                return;
            }
            LogTool.d("BookInfo:",BookInfo.class.getName() );
            LogTool.d("result:",result.toString() );

            Intent intent = new Intent(ZBarScanActivity.this,
                    BookDetailActivity.class);
            intent.putExtra(BookInfo.class.getName(), result);
            intent.setAction(ViewBook.ViewBookInfo.SCAN_BOOKINFO_INTENT);
            startActivity(intent);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressCount += values[0];
            progressBar.setProgress(progressCount);
            super.onProgressUpdate(values);
        }
    }

}
