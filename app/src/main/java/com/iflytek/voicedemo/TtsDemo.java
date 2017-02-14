package com.iflytek.voicedemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.sunflower.FlowerCollector;


public class TtsDemo {
	private static String TAG = TtsDemo.class.getSimpleName();
	// 语音合成对象
	private SpeechSynthesizer mTts;

	// 默认发音人
	private String voicer = "xiaoyan";
	
	// 缓冲进度
	private int mPercentForBuffering = 0;
	// 播放进度
	private int mPercentForPlaying = 0;
	
	// 云端/本地单选按钮
	private RadioGroup mRadioGroup;
	// 引擎类型
	private String mEngineType = SpeechConstant.TYPE_LOCAL;
	
	private Toast mToast;

	private Context mContext = null;
	
	@SuppressLint("ShowToast")
	public void onCreate(Context ctx) {
		mContext = ctx;
		// 初始化合成对象
		mTts = SpeechSynthesizer.createSynthesizer(mContext, mTtsInitListener);

		mToast = Toast.makeText(mContext,"",Toast.LENGTH_SHORT);
	}

	public void startSpeaking(String str) {
		// 设置参数
		setParam();
		int code = mTts.startSpeaking(str, mTtsListener);

		if (code != ErrorCode.SUCCESS) {
			if(code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED){
				//未安装则跳转到提示安装页面
			}else {
				showTip("语音合成失败,错误码: " + code);
			}
		}
	}

	public void onClick(int id) {
		switch(id) {
		// 开始合成
		// 收到onCompleted 回调时，合成结束、生成合成音频
        // 合成的音频格式：只支持pcm格式
		case 1:
			String text = "123";
			// 设置参数
			setParam();
			int code = mTts.startSpeaking(text, mTtsListener);
			
			if (code != ErrorCode.SUCCESS) {
				if(code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED){
					//未安装则跳转到提示安装页面
				}else {
					showTip("语音合成失败,错误码: " + code);	
				}
			}
			break;
		// 取消合成
		case 2:
			mTts.stopSpeaking();
			break;
		// 暂停播放
		case 3:
			mTts.pauseSpeaking();
			break;
		// 继续播放
		case 4:
			mTts.resumeSpeaking();
			break;
		// 选择发音人
		case 5:
			//showPresonSelectDialog();
			break;
		}
	}

	/**
	 * 初始化监听。
	 */
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(TAG, "InitListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
        		showTip("初始化失败,错误码："+code);
        	} else {
				// 初始化成功，之后可以调用startSpeaking方法
        		// 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
        		// 正确的做法是将onCreate中的startSpeaking调用移至这里
			}		
		}
	};

	/**
	 * 合成回调监听。
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {
		
		@Override
		public void onSpeakBegin() {
			showTip("开始播放");
		}

		@Override
		public void onSpeakPaused() {
			showTip("暂停播放");
		}

		@Override
		public void onSpeakResumed() {
			showTip("继续播放");
		}

		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos,
				String info) {
			// 合成进度
			mPercentForBuffering = percent;
			//showTip(String.format(getString(R.string.tts_toast_format),
			//		mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
			// 播放进度
			mPercentForPlaying = percent;
			//showTip(String.format(getString(R.string.tts_toast_format),
			//		mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onCompleted(SpeechError error) {
			if (error == null) {
				showTip("播放完成");
			} else if (error != null) {
				showTip(error.getPlainDescription(true));
			}
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			//	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			//		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			//		Log.d(TAG, "session id =" + sid);
			//	}
		}
	};

	private void showTip(final String str) {
		/*
		mToast.setText(str);
		mToast.show();
		*/
		Log.e(TAG, str);
	}

	private void setParam(){
		// 清空参数
		mTts.setParameter(SpeechConstant.PARAMS, null);
		// 根据合成引擎设置相应参数
		if(mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
			mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
			// 设置在线合成发音人
			mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);

			//设置合成语速
			mTts.setParameter(SpeechConstant.SPEED, "50");
			//设置合成音调
			mTts.setParameter(SpeechConstant.PITCH, "50");
			//设置合成音量
			mTts.setParameter(SpeechConstant.VOLUME, "50");

		}else {
			mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
			// 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
			mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
			/**
			 * TODO 本地合成不设置语速、音调、音量，默认使用语记设置
			 * 开发者如需自定义参数，请参考在线合成参数设置
			 */
		}
		//设置播放器音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
		// 设置播放合成音频打断音乐播放，默认为true
		mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
		
		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
		mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");
	}

	protected void onDestroy() {
		mTts.stopSpeaking();
		// 退出时释放连接
		mTts.destroy();
	}

	protected void onResume() {
		//移动数据统计分析
		FlowerCollector.onResume(mContext);
		FlowerCollector.onPageStart(TAG);
	}

	protected void onPause() {
		//移动数据统计分析
		FlowerCollector.onPageEnd(TAG);
		FlowerCollector.onPause(mContext);
	}

}
