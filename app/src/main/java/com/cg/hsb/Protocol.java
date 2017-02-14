
package com.cg.hsb;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.os.Handler;
import android.os.Message;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.iflytek.cloud.SpeechUtility;
import com.iflytek.voicedemo.TtsDemo;

import java.io.OutputStream;
import java.net.Socket;

/**
 * 
 * Protocol
 * 
 */
public class Protocol  implements Runnable {
	public Boolean IsThreadDisable = false;
	InetAddress mBcAddress = null;
	InetAddress mHsbAddress = null;
	Handler handler;
	private Boolean IsHsbConnected = false;
	Socket mSocket = null;

	OutputStream mOutputStream;

	private ArrayList<HsbDevice> mDevList;
	private ArrayList<HsbScene> mSceneList;

	private HsbListener mListener = null;
	private HsbSceneListener mSceneListener = null;
	private TtsDemo mTts = null;

	private final int HSB_CMD_BOX_DISCOVER = 0x8801;
	private final int HSB_CMD_BOX_DISCOVER_RESP = 0x8802;
	private final int HSB_CMD_GET_DEVS = 0x8811;
	private final int HSB_CMD_GET_DEVS_RESP = 0x8812;
	private final int HSB_CMD_GET_INFO = 0x8813;
	private final int HSB_CMD_GET_INFO_RESP = 0x8814;
	private final int HSB_CMD_SET_CONFIG = 0x8815;
	private final int HSB_CMD_GET_CONFIG = 0x8816;
	private final int HSB_CMD_GET_CONFIG_RESP = 0x8817;
	private final int HSB_CMD_DEV_ONLINE = 0x881A;
	private final int HSB_CMD_GET_STATUS = 0x8821;
	private final int HSB_CMD_GET_STATUS_RESP = 0x8822;
	private final int HSB_CMD_SET_STATUS = 0x8823;
	private final int HSB_CMD_SET_CHANNEL = 0x8824;
	private final int HSB_CMD_DEL_CHANNEL = 0x8825;
	private final int HSB_CMD_SWITCH_CHANNEL = 0x8826;
	private final int HSB_CMD_GET_CHANNEL = 0x8827;
	private final int HSB_CMD_GET_CHANNEL_RESP = 0x8828;
	private final int HSB_CMD_STATUS_UPDATE = 0x8829;
	private final int HSB_CMD_GET_TIMER = 0x8831;
	private final int HSB_CMD_GET_TIMER_RESP = 0x8832;
	private final int HSB_CMD_SET_TIMER = 0x8833;
	private final int HSB_CMD_DEL_TIMER = 0x8834;
	private final int HSB_CMD_GET_DELAY = 0x8841;
	private final int HSB_CMD_GET_DELAY_RESP = 0x8842;
	private final int HSB_CMD_SET_DELAY = 0x8843;
	private final int HSB_CMD_DEL_DELAY = 0x8844;
	private final int HSB_CMD_GET_LINKAGE = 0x8851;
	private final int HSB_CMD_GET_LINKAGE_RESP = 0x8852;
	private final int HSB_CMD_SET_LINKAGE = 0x8853;
	private final int HSB_CMD_DEL_LINKAGE = 0x8854;
	private final int HSB_CMD_PROBE_DEV = 0x8861;
	private final int HSB_CMD_ADD_DEV = 0x8862;
	private final int HSB_CMD_DEL_DEV = 0x8863;
	private final int HSB_CMD_EVENT = 0x8871;
	private final int HSB_CMD_SET_SCENE = 0x8891;
	private final int HSB_CMD_DEL_SCENE = 0x8892;
	private final int HSB_CMD_ENTER_SCENE = 0x8893;
	private final int HSB_CMD_GET_SCENE = 0x8894;
	private final int HSB_CMD_SCENE_UPDATE = 0x8895;
	private final int HSB_CMD_DO_ACTION = 0x8881;
	private final int HSB_CMD_RESULT = 0x88A1;

	private final String mUnknownCmd = "请重新输入指令";

	public Protocol(Context ctx, Handler handler) {
		this.handler = handler;

		mDevList = new ArrayList<HsbDevice>();
		mSceneList = new ArrayList<HsbScene>();

		try {
			mBcAddress = getBroadcastAddress(ctx);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (null == mBcAddress)
			Log.e("hsbservice", "getBroadcastAddress fail");

		InitVoiceRecognizerListener();

		SpeechUtility.createUtility(ctx, "appid=56f89078");
		mTts = new TtsDemo();
		mTts.onCreate(ctx);
	}

	public void Speaking(String sentence)
	{
		if (mTts != null)
			mTts.startSpeaking(sentence);
	}

	private void UnknownCmd()
	{
		Speaking(mUnknownCmd);
	}

	public ArrayList<HsbDevice> GetDeviceList() {
		return mDevList;
	}

	public void SetListener(HsbListener listener) {
		mListener = listener;
	}

	public void SetSceneListener(HsbSceneListener listener) {
		mSceneListener = listener;
	}

	private void ProbeHsb() {
		mHsbAddress = null;

		int server_port = 18000;
		DatagramSocket s = null;
		try {
			s = new DatagramSocket();
			s.setSoTimeout(1000);
			s.setBroadcast(true);
		} catch (SocketException e) {
			e.printStackTrace();
		}

		byte[] message = new byte[16];
		int msg_length = 8;

		Decode2LE(message, 0, HSB_CMD_BOX_DISCOVER);
		Decode2LE(message, 2, msg_length);
		message[4] = 0;
		message[5] = 1;
		message[6] = 0;
		message[7] = 0;

		byte[] rbuf = new byte[16];

		DatagramPacket p = new DatagramPacket(message, msg_length, mBcAddress,
				server_port);

		DatagramPacket rp = new DatagramPacket(rbuf, msg_length);

		Boolean bFound = false;
		int i = 0;
		while (!bFound && i < 5) {
			i++;
			try {
				s.send(p);
				s.receive(rp);
			} catch (IOException e) {
				//e.printStackTrace();
			}

			if (rp.getLength() > 0) {
				bFound = true;
				break;
			}
		}

		s.close();

		if (bFound) {
			mHsbAddress = rp.getAddress();
		}
	}

	public boolean GetConnectivity() {
		return IsHsbConnected;
	}

	private void UpdateConnectivity(Boolean conn) {
		IsHsbConnected = conn;

		Log.d("hsbservice", "UpdateConnectivity " + conn);

		if (mListener != null)
			mListener.onHsbOnline(conn);

		if (null == handler)
			return;

		Message msg = new Message();
		msg.what = 0x125;
		handler.sendMessage(msg);
	}

	/* voice recognize */
	private HsbDevice mCurrentVrDevice = null;
	private void SetVrDevice(HsbDevice device) {
		mCurrentVrDevice = device;
	}

	private void WaitVoiceRecognizerMessage()
	{
		int server_port = 60021;
		boolean quit = false;
		DatagramSocket s = null;
		try {
			s = new DatagramSocket(server_port);
		} catch (SocketException e) {
			e.printStackTrace();
		}

		byte[] rbuf = new byte[1024];

		DatagramPacket rp = new DatagramPacket(rbuf, rbuf.length);

		Log.e("hsbservice", "Wait voice command");

		while (!quit)
		{
			try {
				s.receive(rp);
			} catch (IOException e) {
				e.printStackTrace();
			}

			String ret = new String(rp.getData(), rp.getOffset(), rp.getLength());
			if (!onVoiceRecognizerResult(ret)) {
				UnknownCmd();
			}
		}

		s.close();
	}

	private void InitVoiceRecognizerListener() {
		new Thread()
		{
			@Override
			public void run()
			{
				WaitVoiceRecognizerMessage();
			}
		}.start();
	}

	private void InitVoiceRecognizer() {
		SendVoiceRecognizerCommand("init");
	}

	public void StartVoiceRecognizer() {
		SendVoiceRecognizerCommand("start");
	}

	public void StopVoiceRecognizer() {
		SendVoiceRecognizerCommand("stop");
	}

	public void ConfigVR() {
		SetVrDevice(null);
		String name = null;
		String location = null;
		ArrayList<String> locArray = new ArrayList<String>();
		ArrayList<String> nameArray = new ArrayList<String>();
		String actions = new String("<action>:");

		for (HsbDevice device : mDevList)
		{
			name = device.GetName();
			location = device.GetLocation();

			if (locArray.indexOf(location) < 0)
				locArray.add(location);

			if (nameArray.indexOf(name) < 0)
				nameArray.add(name);
		}

		for (HsbScene scene : mSceneList)
		{
			name = scene.GetName();

			if (locArray.indexOf(name) < 0)
				locArray.add(name);
		}

		if (nameArray.size() == 0 || locArray.size() == 0)
			return;

		String locations = new String("<location>:");
		int size = locArray.size();
		for (int id = 0; id < size; id++)
		{
			locations += locArray.get(id);
			if (id < size - 1)
				locations += "|";
		}
		locations += ";\n";

		String names = new String("<object>:");
		size = nameArray.size();
		for (int id = 0; id < size; id++) {
			names += nameArray.get(id);
			if (id < size - 1)
				names += "|";
		}

		if (mSceneList.size() > 0) {
			name += "|场景";
		}

		actions += "打开|关闭|进入;\n";
		names += ";\n";

		SendVoiceRecognizerCommand("global=" + actions + locations + names);
	}

	public void ConfigVoiceRecognizer(HsbDevice device, ArrayList<String> actions, ArrayList<String> objects) {
		SetVrDevice(device);
		String action = new String("<action>:");
		int size = actions.size();
		for (int id = 0; id < size; id++) {
			action += actions.get(id);
			if (id < size - 1)
				action += "|";
		}
		action += ";\n";

		String object = new String("<object>:");
		size = objects.size();
		for (int id = 0; id < size; id++) {
			object += objects.get(id);
			if (id < size - 1)
				object += "|";
		}
		object += ";\n";

		SendVoiceRecognizerCommand("device=" + action + object);
	}

	public HsbDevice FindDevice(String name, String location) {
		HsbDevice device = null;
		for (int id = 0; id < mDevList.size(); id++) {
			device = (HsbDevice) mDevList.get(id);
			if (name.equals(device.GetName()) &&
				location.equals(device.GetLocation()))
			{
				return device;
			}
		}

		return null;
	}

	private boolean onVoiceRecognizerResult(String result) {
		Log.d("hsbservice", "onVoiceRecognizerResult: " + result);

		boolean ret;

		if (result.equals("unknown")) {
			return false;
		}

		if (mCurrentVrDevice != null) {
			return mCurrentVrDevice.onVoiceRecognizerResult(result);
		}

		String[] tmp = null;
		tmp = result.split(",");
		if (tmp.length < 3)
			return false;

		if (tmp[2].equals("场景")) {
			if (!tmp[0].equals("进入"))
				return false;

			HsbScene scene = FindScene(tmp[1]);
			if (scene == null)
				return false;

			EnterScene(scene.GetName());
			return true;
		}

		HsbDevice device = FindDevice(tmp[2], tmp[1]);
		if (null == device)
			return false;

		boolean power = false;

		if (tmp[0].matches(".*开"))
			power = true;
		else if (tmp[0].matches("关.*"))
			power = false;
		else
			return false;

		device.SetPowerStatus(power);
		return true;
	}

	private void SendVoiceRecognizerCommand(String command)
	{
		if (null == mHsbAddress)
			return;

		final String cmd = new String(command);
		new Thread()
		{
			@Override
			public void run()
			{
				SendVoiceRecognizerCommandAsync(cmd);
			}
		}.start();
	}

	private void SendVoiceRecognizerCommandAsync(String command) {
		DatagramSocket s = null;
		try {
			s = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}

		if (null == s)
			Log.e("hsbservice", "create DatagramSocket fail");

		byte[] data = StringToBytes(command);
		if (data == null) {
			Log.e("hsbservice", "StringToBytes fail: " + command);
			return;
		}

		DatagramPacket p = new DatagramPacket(data, data.length, mHsbAddress, 18020);

		try {
			s.send(p);
		} catch (IOException e) {
			e.printStackTrace();
		}

		s.close();
	}

	public void GetDevices()
	{
		if (mSocket == null || IsHsbConnected == false) {
			Log.e("hsbservice", "hsb not connected");
			return;
		}

		new Thread()
		{
			@Override
			public void run()
			{
				GetDevicesAsyc();
			}
		}.start();
	}

	private void SendHsbCmd(byte[] buf, int len) {
		try {
			mOutputStream.write(buf, 0, len);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private boolean CheckConnectivity() {
		if (mSocket == null) {
			Log.e("hsbservice", "mSocket == null");
			return false;
		}

		if (IsHsbConnected == false) {
			Log.e("hsbservice", "hsb not connected");
			return false;
		}

		return true;
	}

	/* 1. get devices */
	private void GetDevicesAsyc()
	{
		byte[] sbuf = new byte[16];
		int len = 4;
		Decode2LE(sbuf, 0, HSB_CMD_GET_DEVS);
		Decode2LE(sbuf, 2, len);

		SendHsbCmd(sbuf, len);
	}

	public void GetDeviceInfo(int devid)
	{
		if (!CheckConnectivity())
			return;

		final int _devid = devid;

		new Thread()
		{
			@Override
			public void run() {
				GetDeviceInfoAsyc(_devid);
			}
		}.start();
	}

	/* 2. get device info */
	private void GetDeviceInfoAsyc(int devid)
	{
		byte[] sbuf = new byte[16];
		int len = 8;
		Decode2LE(sbuf, 0, HSB_CMD_GET_INFO);
		Decode2LE(sbuf, 2, len);
		Decode4LE(sbuf, 4, devid);

		SendHsbCmd(sbuf, len);
	}

	public void SetDeviceConfig(int devid, HsbDeviceConfig config)
	{
		if (!CheckConnectivity())
			return;

		final int _devid = devid;
		final HsbDeviceConfig _config = config;

		new Thread()
		{
			@Override
			public void run()
			{
				SetDeviceConfigAsync(_devid, _config);
			}
		}.start();
	}

	/* set device config */
	private void SetDeviceConfigAsync(int devid, HsbDeviceConfig config)
	{
		byte[] sbuf = new byte[40];
		int len = 40;
		Decode2LE(sbuf, 0, HSB_CMD_SET_CONFIG);
		Decode2LE(sbuf, 2, len);
		Decode4LE(sbuf, 4, devid);
		byte[] nameBytes = StringToBytes(config.GetName());
		byte[] locBytes = StringToBytes(config.GetLocation());

		if (nameBytes == null || locBytes == null)
			return;

		System.arraycopy(nameBytes, 0, sbuf, 8, nameBytes.length);
		System.arraycopy(locBytes, 0, sbuf, 24, locBytes.length);

		SendHsbCmd(sbuf, len);
	}

	public void GetDeviceConfig(int devid)
	{
		if (!CheckConnectivity())
			return;

		final int _devid = devid;

		new Thread()
		{
			@Override
			public void run()
			{
				GetDeviceConfigAsync(_devid);
			}
		}.start();
	}

	/* get device config */
	private void GetDeviceConfigAsync(int devid)
	{
		byte[] sbuf = new byte[16];
		int len = 8;
		Decode2LE(sbuf, 0, HSB_CMD_GET_CONFIG);
		Decode2LE(sbuf, 2, len);
		Decode4LE(sbuf, 4, devid);

		SendHsbCmd(sbuf, len);
	}

	public void GetDeviceStatus(int devid)
	{
		if (!CheckConnectivity())
			return;

		final int _devid = devid;

		new Thread()
		{
			@Override
			public void run()
			{
				GetDeviceStatusAsync(_devid);
			}
		}.start();
	}

	/* 3. get device status */
	private void GetDeviceStatusAsync(int devid)
	{
		byte[] sbuf = new byte[16];
		int len = 8;
		Decode2LE(sbuf, 0, HSB_CMD_GET_STATUS);
		Decode2LE(sbuf, 2, len);
		Decode4LE(sbuf, 4, devid);

		SendHsbCmd(sbuf, len);
	}

	public void SetDeviceStatus(int devid, HsbDeviceStatus status)
	{
		if (!CheckConnectivity())
			return;

		final int _devid = devid;
		final HsbDeviceStatus _status = status;

		new Thread()
		{
			@Override
			public void run()
			{
				SetDeviceStatusAsync(_devid, _status);
			}
		}.start();
	}

	/* 3. set device status */
	private void SetDeviceStatusAsync(int devid, HsbDeviceStatus status)
	{
		int len = 8 + 4 * status.mNum;
		byte[] sbuf = new byte[len];
		Decode2LE(sbuf, 0, HSB_CMD_SET_STATUS);
		Decode2LE(sbuf, 2, len);
		Decode4LE(sbuf, 4, devid);
		for (int id = 0; id < status.mNum; id++) {
			Decode2LE(sbuf, 8 + id * 4, status.mId[id]);
			Decode2LE(sbuf, 10 + id * 4, status.mVal[id]);
		}

		SendHsbCmd(sbuf, len);
	}

	public void SetDeviceChannel(int devid, String name, int id)
	{
		if (!CheckConnectivity())
			return;

		final int _devid = devid;
		final String _name = name;
		final int _id = id;

		new Thread()
		{
			@Override
			public void run() {
				SetDeviceChannelAsync(_devid, _name, _id);
			}
		}.start();
	}

	private void SetDeviceChannelAsync(int devid, String name, int id)
	{
		int len = 28;
		byte[] sbuf = new byte[len];
		Decode2LE(sbuf, 0, HSB_CMD_SET_CHANNEL);
		Decode2LE(sbuf, 2, len);
		Decode4LE(sbuf, 4, devid);

		byte[] nameBytes = StringToBytes(name);
		if (nameBytes == null)
			return;

		System.arraycopy(nameBytes, 0, sbuf, 8, nameBytes.length);
		Decode4LE(sbuf, 24, id);

		SendHsbCmd(sbuf, len);
	}

	public void DelDeviceChannel(int devid, String name)
	{
		if (!CheckConnectivity())
			return;

		final int _devid = devid;
		final String _name = name;

		new Thread()
		{
			@Override
			public void run()
			{
				DelDeviceChannelAsync(_devid, _name);
			}
		}.start();
	}

	private void DelDeviceChannelAsync(int devid, String name)
	{
		int len = 24;
		byte[] sbuf = new byte[len];
		Decode2LE(sbuf, 0, HSB_CMD_DEL_CHANNEL);
		Decode2LE(sbuf, 2, len);
		Decode4LE(sbuf, 4, devid);

		byte[] nameBytes = StringToBytes(name);
		if (nameBytes == null)
			return;

		System.arraycopy(nameBytes, 0, sbuf, 8, nameBytes.length);

		SendHsbCmd(sbuf, len);
	}

	public void SwitchDeviceChannel(int devid, String name)
	{
		if (!CheckConnectivity())
			return;

		final int _devid = devid;
		final String _name = name;

		new Thread()
		{
			@Override
			public void run() {
				SwitchDeviceChannelAsync(_devid, _name);
			}
		}.start();
	}

	private void SwitchDeviceChannelAsync(int devid, String name)
	{
		int len = 24;
		byte[] sbuf = new byte[len];
		Decode2LE(sbuf, 0, HSB_CMD_SWITCH_CHANNEL);
		Decode2LE(sbuf, 2, len);
		Decode4LE(sbuf, 4, devid);

		byte[] nameBytes = StringToBytes(name);
		if (nameBytes == null)
			return;

		System.arraycopy(nameBytes, 0, sbuf, 8, nameBytes.length);

		SendHsbCmd(sbuf, len);
	}

	public void GetDeviceChannel(int devid)
	{
		if (!CheckConnectivity())
			return;

		final int _devid = devid;

		new Thread()
		{
			@Override
			public void run()
			{
				GetDeviceChannelAsync(_devid);
			}
		}.start();
	}

	private void GetDeviceChannelAsync(int devid)
	{
		byte[] sbuf = new byte[16];
		int len = 8;
		Decode2LE(sbuf, 0, HSB_CMD_GET_CHANNEL);
		Decode2LE(sbuf, 2, len);
		Decode4LE(sbuf, 4, devid);

		SendHsbCmd(sbuf, len);
	}

	public void ProbeDevice(int drvid)
	{
		if (!CheckConnectivity())
			return;

		final int _drvid = drvid;

		new Thread()
		{
			@Override
			public void run()
			{
				ProbeDeviceAsync(_drvid);
			}
		}.start();
	}

	/* 5. probe device */
	private void ProbeDeviceAsync(int drvid)
	{
		byte[] sbuf = new byte[16];
		int len = 8;
		Decode2LE(sbuf, 0, HSB_CMD_PROBE_DEV);
		Decode2LE(sbuf, 2, len);
		Decode4LE(sbuf, 4, drvid);

		SendHsbCmd(sbuf, len);
	}

	public void DoAction(int devid, HsbDeviceAction action)
	{
		if (!CheckConnectivity())
			return;

		final int _devid = devid;
		final HsbDeviceAction _action = action;

		new Thread()
		{
			@Override
			public void run()
			{
				DoActionAsync(_devid, _action);
			}
		}.start();
	}

	/* 6. device action */
	private void DoActionAsync(int devid, HsbDeviceAction action)
	{
		int len = 16;
		byte[] sbuf = new byte[len];

		Decode2LE(sbuf, 0, HSB_CMD_DO_ACTION);
		Decode2LE(sbuf, 2, len);
		Decode4LE(sbuf, 4, devid);
		Decode2LE(sbuf, 8, action.GetID());
		Decode2LE(sbuf, 10, action.GetParam1());
		Decode4LE(sbuf, 12, action.GetParam2());

		SendHsbCmd(sbuf, len);
	}

	public void AddDevice(int drvid, int dev_type, HsbDeviceConfig config)
	{
		if (!CheckConnectivity())
			return;

		final int _drvid = drvid;
		final int _dev_type = dev_type;
		final HsbDeviceConfig _config = config;

		new Thread()
		{
			@Override
			public void run() {
				AddDeviceAsync(_drvid, _dev_type, _config);
			}
		}.start();
	}

	/* 7. add device */
	private void AddDeviceAsync(int drvid, int dev_type, HsbDeviceConfig config)
	{
		int len = 40;
		byte[] sbuf = new byte[len];
		Decode2LE(sbuf, 0, HSB_CMD_ADD_DEV);
		Decode2LE(sbuf, 2, len);
		Decode2LE(sbuf, 4, drvid);
		Decode2LE(sbuf, 6, dev_type);

		byte[] nameBytes = StringToBytes(config.GetName());
		byte[] locBytes = StringToBytes(config.GetLocation());
		if (nameBytes == null || locBytes == null)
			return;

		System.arraycopy(nameBytes, 0, sbuf, 8, nameBytes.length);
		System.arraycopy(locBytes, 0, sbuf, 24, locBytes.length);

		SendHsbCmd(sbuf, len);
	}

	public void DelDevice(int devid)
	{
		if (!CheckConnectivity())
			return;

		final int _devid = devid;

		new Thread()
		{
			@Override
			public void run()
			{
				DelDeviceAsync(_devid);
			}
		}.start();
	}

	private void DelDeviceAsync(int devid)
	{
		int len = 8;
		byte[] sbuf = new byte[len];
		Decode2LE(sbuf, 0, HSB_CMD_DEL_DEV);
		Decode2LE(sbuf, 2, len);
		Decode4LE(sbuf, 4, devid);

		SendHsbCmd(sbuf, len);
	}

	public void GetDeviceTimer(int devid, int id)
	{
		if (!CheckConnectivity())
			return;

		final int _devid = devid;
		final int _id = id;

		new Thread()
		{
			@Override
			public void run()
			{
				GetDeviceTimerAsync(_devid, _id);
			}
		}.start();
	}

	/* get device timer */
	private void GetDeviceTimerAsync(int devid, int id)
	{
		int len = 12;
		byte[] sbuf = new byte[len];
		Decode2LE(sbuf, 0, HSB_CMD_GET_TIMER);
		Decode2LE(sbuf, 2, len);
		Decode4LE(sbuf, 4, devid);
		Decode2LE(sbuf, 8, id);
		Decode2LE(sbuf, 10, 0);

		SendHsbCmd(sbuf, len);
	}

	public void SetDeviceTimer(int devid, int id, HsbDeviceTimer timer)
	{
		if (!CheckConnectivity())
			return;

		final int _devid = devid;
		final int _id = id;
		final HsbDeviceTimer _timer = timer;

		new Thread()
		{
			@Override
			public void run() {
				SetDeviceTimerAsync(_devid, _id, _timer);
			}
		}.start();
	}

	/* set device timer */
	private void SetDeviceTimerAsync(int devid, int id, HsbDeviceTimer timer)
	{
		int len = 28;
		byte[] sbuf = new byte[len];
		Decode2LE(sbuf, 0, HSB_CMD_SET_TIMER);
		Decode2LE(sbuf, 2, len);
		Decode4LE(sbuf, 4, devid);
		Decode2LE(sbuf, 8, id);
		sbuf[10] = (byte)(timer.mWorkMode & 0xFF);
		byte flag = (byte)(timer.mAction.GetFlag() & 0xFF);
		if (timer.mActive)
			flag |= (1 << 1);
		sbuf[11] = flag;
		sbuf[12] = timer.mHou;
		sbuf[13] = timer.mMin;
		sbuf[14] = timer.mSec;
		sbuf[15] = (byte)(timer.mWeekday & 0xFF);
		Decode2LE(sbuf, 16, timer.mYear);
		sbuf[18] = timer.mMon;
		sbuf[19] = timer.mDay;
		Decode2LE(sbuf, 20, timer.mAction.GetID());
		Decode2LE(sbuf, 22, timer.mAction.GetParam1());
		Decode4LE(sbuf, 24, timer.mAction.GetParam2());

		SendHsbCmd(sbuf, len);
	}

	public void DelDeviceTimer(int devid, int id)
	{
		if (!CheckConnectivity())
			return;

		final int _devid = devid;
		final int _id = id;

		new Thread()
		{
			@Override
			public void run()
			{
				DelDeviceTimerAsync(_devid, _id);
			}
		}.start();
	}

	/* del device timer */
	private void DelDeviceTimerAsync(int devid, int id)
	{
		int len = 12;
		byte[] sbuf = new byte[len];
		Decode2LE(sbuf, 0, HSB_CMD_DEL_TIMER);
		Decode2LE(sbuf, 2, len);
		Decode4LE(sbuf, 4, devid);
		Decode2LE(sbuf, 8, id);
		Decode2LE(sbuf, 10, 0);

		SendHsbCmd(sbuf, len);
	}

	public void GetDeviceDelay(int devid, int id)
	{
		if (!CheckConnectivity())
			return;

		final int _devid = devid;
		final int _id = id;

		new Thread()
		{
			@Override
			public void run()
			{
				GetDeviceDelayAsync(_devid, _id);
			}
		}.start();
	}

	/* get device delay */
	private void GetDeviceDelayAsync(int devid, int id)
	{
		int len = 12;
		byte[] sbuf = new byte[len];
		Decode2LE(sbuf, 0, HSB_CMD_GET_DELAY);
		Decode2LE(sbuf, 2, len);
		Decode4LE(sbuf, 4, devid);
		Decode2LE(sbuf, 8, id);
		Decode2LE(sbuf, 10, 0);

		SendHsbCmd(sbuf, len);
	}

	public void SetDeviceDelay(int devid, int id, HsbDeviceDelay delay)
	{
		if (!CheckConnectivity())
			return;

		final int _devid = devid;
		final int _id = id;
		final HsbDeviceDelay _delay = delay;

		new Thread()
		{
			@Override
			public void run() {
				SetDeviceDelayAsync(_devid, _id, _delay);
			}
		}.start();
	}

	/* set device delay */
	private void SetDeviceDelayAsync(int devid, int id, HsbDeviceDelay delay)
	{
		int flag = 0;
		int len = 32;
		byte[] sbuf = new byte[len];
		Decode2LE(sbuf, 0, HSB_CMD_SET_DELAY);
		Decode2LE(sbuf, 2, len);
		Decode4LE(sbuf, 4, devid);
		Decode2LE(sbuf, 8, id);
		sbuf[10] = (byte)(delay.mWorkMode & 0xFF);

		flag |= (delay.mEvent.GetFlag() << 1);
		flag |= delay.mAction.GetFlag();
		sbuf[11] = (byte)(flag & 0xFF);
		Decode2LE(sbuf, 12, delay.mEvent.GetID());
		Decode2LE(sbuf, 14, delay.mEvent.GetParam1());
		Decode4LE(sbuf, 16, delay.mEvent.GetParam2());
		Decode2LE(sbuf, 20, delay.mAction.GetID());
		Decode2LE(sbuf, 22, delay.mAction.GetParam1());
		Decode4LE(sbuf, 24, delay.mAction.GetParam2());
		Decode4LE(sbuf, 28, delay.mDelaySec);

		SendHsbCmd(sbuf, len);
	}

	public void DelDeviceDelay(int devid, int id)
	{
		if (!CheckConnectivity())
			return;

		final int _devid = devid;
		final int _id = id;

		new Thread()
		{
			@Override
			public void run()
			{
				DelDeviceDelayAsync(_devid, _id);
			}
		}.start();
	}

	/* del device delay */
	private void DelDeviceDelayAsync(int devid, int id)
	{
		int len = 12;
		byte[] sbuf = new byte[len];
		Decode2LE(sbuf, 0, HSB_CMD_DEL_DELAY);
		Decode2LE(sbuf, 2, len);
		Decode4LE(sbuf, 4, devid);
		Decode2LE(sbuf, 8, id);
		Decode2LE(sbuf, 10, 0);

		SendHsbCmd(sbuf, len);
	}

	public void GetDeviceLinkage(int devid, int id)
	{
		if (!CheckConnectivity())
			return;

		final int _devid = devid;
		final int _id = id;

		new Thread()
		{
			@Override
			public void run()
			{
				GetDeviceLinkageAsync(_devid, _id);
			}
		}.start();
	}

	/* get device linkage */
	private void GetDeviceLinkageAsync(int devid, int id)
	{
		int len = 12;
		byte[] sbuf = new byte[len];
		Decode2LE(sbuf, 0, HSB_CMD_GET_LINKAGE);
		Decode2LE(sbuf, 2, len);
		Decode4LE(sbuf, 4, devid);
		Decode2LE(sbuf, 8, id);
		Decode2LE(sbuf, 10, 0);

		SendHsbCmd(sbuf, len);
	}

	public void SetDeviceLinkage(int devid, int id, HsbDeviceLinkage link)
	{
		if (!CheckConnectivity())
			return;

		final int _devid = devid;
		final int _id = id;
		final HsbDeviceLinkage _link = link;

		new Thread()
		{
			@Override
			public void run() {
				SetDeviceLinkageAsync(_devid, _id, _link);
			}
		}.start();
	}

	/* set device linkage */
	private void SetDeviceLinkageAsync(int devid, int id, HsbDeviceLinkage link)
	{
		int flag = 0;
		int len = 32;
		byte[] sbuf = new byte[len];
		Decode2LE(sbuf, 0, HSB_CMD_SET_LINKAGE);
		Decode2LE(sbuf, 2, len);
		Decode4LE(sbuf, 4, devid);
		Decode2LE(sbuf, 8, id);
		sbuf[10] = (byte)(link.mWorkMode & 0xFF);

		flag |= (link.mEvent.GetFlag() << 1);
		flag |= link.mAction.GetFlag();
		sbuf[11] = (byte)(flag & 0xFF);
		Decode2LE(sbuf, 12, link.mEvent.GetID());
		Decode2LE(sbuf, 14, link.mEvent.GetParam1());
		Decode4LE(sbuf, 16, link.mEvent.GetParam2());
		Decode4LE(sbuf, 20, link.mActDevId);
		Decode2LE(sbuf, 24, link.mAction.GetID());
		Decode2LE(sbuf, 26, link.mAction.GetParam1());
		Decode4LE(sbuf, 28, link.mAction.GetParam2());

		SendHsbCmd(sbuf, len);
	}

	public void DelDeviceLinkage(int devid, int id)
	{
		if (!CheckConnectivity())
			return;

		final int _devid = devid;
		final int _id = id;

		new Thread()
		{
			@Override
			public void run()
			{
				DelDeviceLinkageAsync(_devid, _id);
			}
		}.start();
	}

	/* del device linkage */
	private void DelDeviceLinkageAsync(int devid, int id)
	{
		int len = 12;
		byte[] sbuf = new byte[len];
		Decode2LE(sbuf, 0, HSB_CMD_DEL_LINKAGE);
		Decode2LE(sbuf, 2, len);
		Decode4LE(sbuf, 4, devid);
		Decode2LE(sbuf, 8, id);
		Decode2LE(sbuf, 10, 0);

		SendHsbCmd(sbuf, len);
	}

	public HsbScene FindScene(String name) {
		for (HsbScene scene : mSceneList) {
			if (scene != null && name.equals(scene.GetName()))
				return scene;
		}

		return null;
	}

	public ArrayList<HsbScene> GetSceneList() {
		return mSceneList;
	}

	public void SetScene(HsbScene scene)
	{
		if (!CheckConnectivity())
			return;

		final HsbScene _scene = scene;

		new Thread()
		{
			@Override
			public void run()
			{
				SetSceneAsync(_scene);
			}
		}.start();
	}

	private void SetSceneAsync(HsbScene scene)
	{
		int len = scene.GetByteLen();
		byte[] sbuf = new byte[len];
		FillBytes(sbuf, 0);
		Decode2LE(sbuf, 0, HSB_CMD_SET_SCENE);
		Decode2LE(sbuf, 2, len);

		byte[] nameBytes = StringToBytes(scene.GetName());
		if (nameBytes == null)
			return;

		System.arraycopy(nameBytes, 0, sbuf, 4, nameBytes.length);

		int id = 0;
		int offset = 20;
		HsbSceneAction action = null;
		for (id = 0; id < scene.GetActionNum(); id++) {
			action = scene.GetAction(id);
			offset += action.FillBuffer(sbuf, offset);
		}

		SendHsbCmd(sbuf, len);
	}

	public void GetScene() {
		if (!CheckConnectivity())
			return;

		new Thread()
		{
			@Override
			public void run()
			{
				GetSceneAsync();
			}
		}.start();
	}

	private void GetSceneAsync()
	{
		int len = 4;
		byte[] sbuf = new byte[len];
		Decode2LE(sbuf, 0, HSB_CMD_GET_SCENE);
		Decode2LE(sbuf, 2, len);

		SendHsbCmd(sbuf, len);
	}

	public void DelScene(String SceneName) {
		if (!CheckConnectivity())
			return;

		final String _SceneName = SceneName;

		new Thread()
		{
			@Override
			public void run()
			{
				DelSceneAsync(_SceneName);
			}
		}.start();

		int index = mSceneList.indexOf(new HsbScene(SceneName));
		if (index < 0)
			return;
		mSceneList.remove(index);
	}

	private void FillBytes(byte[] buf, int val) {
		int id;
		for (id = 0; id < buf.length; id++)
			buf[id] = (byte)val;
	}

	private void DelSceneAsync(String SceneName)
	{
		int len = 20;
		byte[] sbuf = new byte[len];
		FillBytes(sbuf, 0);

		Decode2LE(sbuf, 0, HSB_CMD_DEL_SCENE);
		Decode2LE(sbuf, 2, len);

		byte[] nameBytes = StringToBytes(SceneName);
		if (nameBytes == null)
			return;

		System.arraycopy(nameBytes, 0, sbuf, 4, nameBytes.length);

		SendHsbCmd(sbuf, len);
	}

	public void EnterScene(String SceneName) {
		if (!CheckConnectivity())
			return;

		final String _SceneName = SceneName;

		new Thread()
		{
			@Override
			public void run()
			{
				EnterSceneAsync(_SceneName);
			}
		}.start();
	}

	private void EnterSceneAsync(String SceneName)
	{
		int len = 20;
		byte[] sbuf = new byte[len];
		FillBytes(sbuf, 0);

		Decode2LE(sbuf, 0, HSB_CMD_ENTER_SCENE);
		Decode2LE(sbuf, 2, len);

		byte[] nameBytes = StringToBytes(SceneName);
		if (nameBytes == null)
			return;

		System.arraycopy(nameBytes, 0, sbuf, 4, nameBytes.length);

		SendHsbCmd(sbuf, len);
	}

	public boolean ValidateDevice(int devid) {
		for (HsbDevice device : mDevList) {
			if (device.GetDevId() == devid)
				return true;
		}

		return false;
	}

	private void ValidateScene()
	{
		for (HsbScene scene : mSceneList) {
			scene.Validate(this);
		}
	}

	public static int Make4LE(byte[] buf, int offset)
	{
		int ret = (buf[offset] & 0xFF) + ((buf[offset+1] & 0xFF) << 8) +
				   ((buf[offset+2] & 0xFF) << 16) + ((buf[offset+3] & 0xFF) << 24);

		return ret;
	}

	public static int Make2LE(byte[] buf, int offset)
	{
		int ret = (buf[offset] & 0xFF) + ((buf[offset+1] & 0xFF) << 8);
		return ret;
	}

	public static void Decode2LE(byte[] buf, int offset, int val)
	{
		buf[offset] = (byte)(val & 0xff);
		buf[offset+1] = (byte)((val >> 8) & 0xff);
	}

	public static void Decode4LE(byte[] buf, int offset, int val)
	{
		buf[offset] = (byte)(val & 0xff);
		buf[offset+1] = (byte)((val >> 8) & 0xff);
		buf[offset+2] = (byte)((val >> 16) & 0xff);
		buf[offset+3] = (byte)((val >> 24) & 0xff);
	}

    private void TcpClientLoop()
    {
    	InputStream inputStream = null;
    	try {
	    	mSocket = new Socket(mHsbAddress, 18002);
	    	
	    	inputStream = mSocket.getInputStream();
	    	mOutputStream = mSocket.getOutputStream();
    	}
    	catch (IOException e)
    	{
    		e.printStackTrace();
    	}
    	
    	if (null == mSocket) {
			Log.e("hsbservice", "Socket fail");
			return;
		}

    	UpdateConnectivity(true);

    	byte[] rbuf = new byte[1024];
	    	
	    int len, cmd, tmp = 0;
    	while (true) {
    		try {
    			tmp = inputStream.read(rbuf, 0, 4);
    		}
    		catch (IOException e)
        	{
        		e.printStackTrace();
        		break;
        	}
    		
    		if (tmp == -1) {
				Log.e("hsbservice", "socket read fail");
				break;
			}

    		if (rbuf[1] != (byte)0x88) {
				Log.e("hsbservice", "wrong cmd " + rbuf[1]);
				continue;
			}

			cmd = Make2LE(rbuf, 0);
			len = Make2LE(rbuf, 2);
    		if (len <= 4) {
				Log.e("hsbservice", "wrong cmd[" + cmd + "] len=" + len);
				continue;
			}

			try {
				tmp = inputStream.read(rbuf, 4, len - 4);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				break;
			}

			if (tmp == -1) {
				Log.e("hsbservice", "socket read fail");
				break;
			} else if (tmp != len - 4){
				Log.e("hsbservice", "wrong cmd[" + cmd + "] len=" + len + " tmp=" + tmp);
				continue;
			}

			DealHsbCommand(rbuf, cmd, len);
    	}

		try {
			mSocket.close();
			mSocket = null;
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		UpdateConnectivity(false);
    }

	private boolean CheckCmdLengthEqual(byte[] buf, int len)
	{
		int cmd = Make2LE(buf, 0);
		int length = Make2LE(buf, 2);

		if (length != len) {
			Log.e("hsbservice", "cmd len error, cmd=" + cmd +
								  " len=" + len +
								  " length=" + length);
			return false;
		}

		return true;
	}

	private boolean CheckCmdLengthLT(byte[] buf, int len)
	{
		int cmd = Make2LE(buf, 0);
		int length = Make2LE(buf, 2);

		if (length < len) {
			Log.e("hsbservice", "cmd len lt error, cmd=" + cmd +
					" len=" + len +
					" length=" + length);
			return false;
		}

		return true;
	}

	private void DealHsbCommand(byte[] rbuf, int cmd, int len)
	{
		switch (cmd) {
			case HSB_CMD_GET_INFO_RESP: // get device info resp
			{
				if (!CheckCmdLengthEqual(rbuf, 28))
					return;

				int devid = Make4LE(rbuf, 4);
				int drvid = Make4LE(rbuf, 8);
				int cls = Make2LE(rbuf, 12);
				int intf = Make2LE(rbuf, 14);
				int devtype = Make4LE(rbuf, 16);
				byte[] mac = new byte[8];
				System.arraycopy(rbuf, 20, mac, 0, 8);

				DeviceInfoUpdated(devid, new HsbDeviceInfo(drvid, devtype, cls, intf, mac));

				break;
			}
			case HSB_CMD_GET_CONFIG_RESP: // get device config resp
			{
				if (!CheckCmdLengthEqual(rbuf, 40))
					return;

				int dev_id = Make4LE(rbuf, 4);
				byte[] name = new byte[16];
				byte[] loc =  new byte[16];

				System.arraycopy(rbuf, 8, name, 0, name.length);
				System.arraycopy(rbuf, 24, loc, 0, loc.length);

				DeviceConfigUpdated(dev_id, name, loc);

				break;
			}
			case HSB_CMD_DEV_ONLINE:
			{
				if (!CheckCmdLengthLT(rbuf, 60))
					return;

				int devid = Make4LE(rbuf, 4);
				int drvid = Make4LE(rbuf, 8);
				int cls = Make2LE(rbuf, 12);
				int intf = Make2LE(rbuf, 14);
				int devtype = Make4LE(rbuf, 16);
				byte[] mac = new byte[8];
				System.arraycopy(rbuf, 20, mac, 0, 8);

				byte[] name = new byte[16];
				byte[] loc =  new byte[16];

				System.arraycopy(rbuf, 28, name, 0, name.length);
				System.arraycopy(rbuf, 44, loc, 0, loc.length);

				DeviceAdded(devid, devtype);

				DeviceInfoUpdated(devid, new HsbDeviceInfo(drvid, devtype, cls, intf, mac));
				DeviceConfigUpdated(devid, name, loc);

				int num = (len - 60) / 4;
				HsbDeviceStatus status = new HsbDeviceStatus(num);
				for (int id = 0; id < num; id++) {
					status.Set(Make2LE(rbuf, 60 + id * 4), Make2LE(rbuf, 62 + id * 4));
				}

				DeviceStatusUpdated(devid, status);

				DeviceOnline(devid, true, devtype);

				break;
			}
			case HSB_CMD_GET_STATUS_RESP: // get device status resp
			case HSB_CMD_STATUS_UPDATE:
			{
				if (!CheckCmdLengthLT(rbuf, 12))
					return;

				int dev_id = Make4LE(rbuf, 4);
				int num = (len - 8) / 4;
				HsbDeviceStatus status = new HsbDeviceStatus(num);
				for (int id = 0; id < num; id++) {
					status.Set(id, Make2LE(rbuf, 8 + id * 4), Make2LE(rbuf, 10 + id * 4));
				}

				DeviceStatusUpdated(dev_id, status);
				break;
			}
			case HSB_CMD_GET_CHANNEL_RESP:
			{
				if (!CheckCmdLengthEqual(rbuf, 28))
					return;

				int devid = Make4LE(rbuf, 4);
				int id = Make4LE(rbuf, 24);
				byte[] name = new byte[16];
				System.arraycopy(rbuf, 8, name, 0, name.length);

				DeviceChannelUpdated(devid, name, id);
				break;
			}
			case HSB_CMD_GET_TIMER_RESP: // get device timer resp
			{
				if (!CheckCmdLengthEqual(rbuf, 28))
					return;

				int devid = Make4LE(rbuf, 4);
				int id = Make2LE(rbuf, 8);
				int workmode = rbuf[10];
				byte flag = rbuf[11];
				byte hou = rbuf[12];
				byte min = rbuf[13];
				byte sec = rbuf[14];
				int wday = rbuf[15];
				int year = Make2LE(rbuf, 16);
				byte mon = rbuf[18];
				byte day = rbuf[19];
				int actid = Make2LE(rbuf, 20);
				int actparam1 = Make2LE(rbuf, 22);
				int actparam2 = Make4LE(rbuf, 24);

				HsbDeviceTimer timer = new HsbDeviceTimer(devid);
				timer.SetTime(hou, min, sec);
				timer.SetDate(year, mon, day);
				timer.SetWorkMode(workmode);
				timer.SetWeekDay(wday);
				if ((flag & (1 << 1)) > 0)
					timer.SetActive(true);
				else
					timer.SetActive(false);
				timer.SetAction(new HsbDeviceAction(devid, actid, actparam1, actparam2, (flag & HsbDeviceAction.FLAG_MASK)));

				DeviceTimerUpdated(devid, id, timer);
				break;
			}
			case HSB_CMD_GET_DELAY_RESP: // get device delay resp
			{
				if (!CheckCmdLengthEqual(rbuf, 32))
					return;

				int devid = Make4LE(rbuf, 4);
				int id = Make2LE(rbuf, 8);
				int workmode = rbuf[10];
				int actflag = rbuf[11];
				int evtid = Make2LE(rbuf, 12);
				int evtparam1 = Make2LE(rbuf, 14);
				int evtparam2 = Make4LE(rbuf, 16);
				int actid = Make2LE(rbuf, 20);
				int actparam1 = Make2LE(rbuf, 22);
				int actparam2 = Make4LE(rbuf, 24);
				int delaysec = Make4LE(rbuf, 28);
				HsbDeviceDelay delay = new HsbDeviceDelay();
				delay.SetWorkMode(workmode);
				delay.SetDelaySec(delaysec);
				delay.SetEvent(new HsbDeviceEvent(evtid, evtparam1, evtparam2, ((actflag & 0x0E) >> 1)));
				delay.SetAction(new HsbDeviceAction(devid, actid, actparam1, actparam2, (actflag & 0x01)));

				DeviceDelayUpdated(devid, id, delay);
				break;
			}
			case HSB_CMD_GET_LINKAGE_RESP: // get device linkage resp
			{
				if (!CheckCmdLengthEqual(rbuf, 32))
					return;

				int devid = Make4LE(rbuf, 4);
				int id = Make2LE(rbuf, 8);
				int workmode = rbuf[10];
				int actflag = rbuf[11];
				int evtid = Make2LE(rbuf, 12);
				int evtparam1 = Make2LE(rbuf, 14);
				int evtparam2 = Make4LE(rbuf, 16);
				int actdevid = Make4LE(rbuf, 20);
				int actid = Make2LE(rbuf, 24);
				int actparam1 = Make2LE(rbuf, 26);
				int actparam2 = Make4LE(rbuf, 28);
				HsbDeviceLinkage link = new HsbDeviceLinkage();
				link.SetWorkMode(workmode);
				link.SetActDevId(actdevid);
				link.SetEvent(new HsbDeviceEvent(evtid, evtparam1, evtparam2, ((actflag & 0x0E) >> 1)));
				link.SetAction(new HsbDeviceAction(devid, actid, actparam1, actparam2, (actflag & 0x01)));

				DeviceLinkageUpdated(devid, id, link);
				break;
			}
			case HSB_CMD_EVENT: // event
			{
				int dev_id = Make4LE(rbuf, 4);
				int evt_id = Make2LE(rbuf, 8);
				int evt_param1 = Make2LE(rbuf, 10);
				int evt_param2 = Make4LE(rbuf, 12);
				switch (evt_id) {
					case HsbConstant.HSB_EVT_STATUS_UPDATED:
					{
						HsbDeviceStatus status = new HsbDeviceStatus(1);
						status.Set(0, evt_param1, evt_param2);
						DeviceStatusUpdated(dev_id, status);
						break;
					}
					case HsbConstant.HSB_EVT_DEV_UPDATED:
					{
						boolean online = (evt_param1 > 0) ? true : false;
						int dev_type = evt_param2;
						if (online)
							DeviceAdded(dev_id, dev_type);

						DeviceOnline(dev_id, online, dev_type);
						break;
					}
					case HsbConstant.HSB_EVT_SENSOR_TRIGGERED:
						DeviceSensorTiggered(dev_id, evt_param1, evt_param2);
						break;
					case HsbConstant.HSB_EVT_SENSOR_RECOVERED:
						DeviceSensorRecovered(dev_id, evt_param1, evt_param2);
						break;
					case HsbConstant.HSB_EVT_MODE_CHANGED:
						break;
					case HsbConstant.HSB_EVT_IR_KEY:
						break;
					default:
						break;
				}
				break;
			}
			case HSB_CMD_SCENE_UPDATE:
			{
				if (!CheckCmdLengthLT(rbuf, 20))
					return;

				byte[] name = new byte[16];
				System.arraycopy(rbuf, 4, name, 0, name.length);

				String sceneName = BytesToString(name);
				if (null == sceneName)
					Log.e("hsbservice", "scene name null");

				int offset = 20;
				HsbScene scene = new HsbScene(sceneName);
				HsbSceneAction action = null;

				while (offset < len) {
					action = new HsbSceneAction();
					offset += action.ParseBuffer(rbuf, offset);
					scene.AddAction(action);
				}

				SceneUpdated(scene);
				break;
			}
			case HSB_CMD_RESULT:
			{
				if (!CheckCmdLengthEqual(rbuf, 12))
					return;

				int dev_id = Make4LE(rbuf, 4);
				int origin_cmd = Make2LE(rbuf, 8);
				int error_code = Make2LE(rbuf, 10);

				HsbResult result = new HsbResult(origin_cmd, error_code);
				DeviceResult(dev_id, result);

				break;
			}
			default:
				Log.e("hsbservice", "get unknown cmd " + cmd);
				break;
		}
	}

	public HsbDevice FindDevice(int devid) {

		for (HsbDevice device : mDevList) {
			if (null != device && device.GetDevId() == devid)
				return device;
		}

		return null;
	}

	private void DeviceAdded(int devid, int dev_type) {
		HsbDevice device = FindDevice(devid);
		if (device != null) {
			Log.e("hsbservice", "device already exist, devid=" + devid);
			return;
		}

		Log.i("hsbservice", "DeviceAdded, devid=" + devid + " type=" + dev_type);

		switch (dev_type) {
			case HsbConstant.HSB_DEV_TYPE_PLUG:
				device = new PlugDevice(this, devid);
				break;
			case HsbConstant.HSB_DEV_TYPE_SENSOR:
				device = new SensorDevice(this, devid);
				break;
			case HsbConstant.HSB_DEV_TYPE_REMOTE_CTL:
				device = new RemoteCtlDevice(this, devid);
				break;
			case HsbConstant.HSB_DEV_TYPE_STB_CC9201:
				device = new CC9201(this, devid);
				break;
			case HsbConstant.HSB_DEV_TYPE_GRAY_AC:
				device = new GrayAirConditioner(this, devid);
				break;
			default:
				device = new HsbDevice(this, devid);
				break;
		}

		mDevList.add(device);
	}

	private void FreeDevice(int devid) {
		HsbDevice device = FindDevice(devid);
		if (device == null) {
			Log.e("hsbservice", "FreeDevice device not found, devid=" + devid);
			return;
		}

		mDevList.remove(device);
	}

	private void DeviceOnline(int devid, boolean online, int dev_type)
	{
		Log.e("hsbservice", "DeviceOnline devid=" + devid + " " + online);
		HsbDevice device = FindDevice(devid);
		if (null == device) {
			Log.e("hsbservice", "DeviceOnline device not found, devid=" + devid);
			return;
		}

		if (online) {
			if (mListener != null)
				mListener.onDeviceOnline(device);
		} else {
			FreeDevice(devid);
			device.Offline();
			if (mListener != null)
				mListener.onDeviceOffline(device);
		}

		ValidateScene();
	}

	private void DeviceSensorTiggered(int devid, int param1, int param2) {
		HsbDevice device = FindDevice(devid);
		if (null == device) {
			Log.e("hsbservice", "DeviceSensorTiggered device not found, devid=" + devid);
			return;
		}

		Log.d("hsbservice", "DeviceSensorTiggered");
		device.onSensorTiggered(param1, param2);
	}

	private void DeviceSensorRecovered(int devid, int param1, int param2) {
		HsbDevice device = FindDevice(devid);
		if (null == device) {
			Log.e("hsbservice", "DeviceSensorRecovered device not found, devid=" + devid);
			return;
		}

		Log.d("hsbservice", "DeviceSensorRecovered");
		device.onSensorRecovered(param1, param2);
	}

	private void DeviceStatusUpdated(int devid, HsbDeviceStatus status)
	{
		HsbDevice device = FindDevice(devid);
		if (null == device) {
			Log.e("hsbservice", "DeviceStatusUpdated device not found, devid=" + devid);
			return;
		}

		Log.d("hsbservice", "DeviceStatusUpdated");
		device.onStatusUpdated(status);

		if (mListener != null)
			mListener.onDeviceStatusUpdated(device, status);
	}

	private void DeviceChannelUpdated(int devid, byte[] nameBytes, int id) {
		HsbDevice device = FindDevice(devid);
		if (null == device) {
			Log.e("hsbservice", "DeviceChannelUpdated device not found, devid=" + devid);
			return;
		}

		String name = BytesToString(nameBytes);

		if (null == name) {
			Log.e("hsbservice", "DeviceChannelUpdated name==null");
			return;
		}

		Log.d("hsbservice", "DeviceChannelUpdated: [" + name + "]");
		device.onChannelUpdated(name, id);
	}

	private void SceneUpdated(HsbScene scene) {
		int index = mSceneList.indexOf(scene);

		if (index < 0) {
			mSceneList.add(scene);
		} else {
			mSceneList.set(index, scene);
		}
	}

	private void DeviceInfoUpdated(int devid, HsbDeviceInfo info)
	{
		HsbDevice device = FindDevice(devid);
		if (null == device) {
			Log.e("hsbservice", "DeviceInfoUpdated device not found, devid=" + devid);
			return;
		}

		Log.d("hsbservice", "DeviceInfoUpdated");
		device.onInfoUpdated(info);
	}

	private void DeviceTimerUpdated(int devid, int id, HsbDeviceTimer timer)
	{
		HsbDevice device = FindDevice(devid);
		if (null == device) {
			Log.e("hsbservice", "DeviceTimerUpdated device not found, devid=" + devid);
			return;
		}

		Log.d("hsbservice", "DeviceTimerUpdated");
		device.onTimerUpdated(id, timer);
	}

	private void DeviceDelayUpdated(int devid, int id, HsbDeviceDelay delay)
	{
		HsbDevice device = FindDevice(devid);
		if (null == device) {
			Log.e("hsbservice", "DeviceDelayUpdated device not found, devid=" + devid);
			return;
		}

		Log.d("hsb", "DeviceDelayUpdated");
		device.onDelayUpdated(id, delay);
	}

	private void DeviceLinkageUpdated(int devid, int id, HsbDeviceLinkage link)
	{
		HsbDevice device = FindDevice(devid);
		if (null == device) {
			Log.e("hsbservice", "DeviceLinkageUpdated device not found, devid=" + devid);
			return;
		}

		Log.d("hsbservice", "DeviceLinkageUpdated");
		device.onLinkageUpdated(id, link);
	}

	private void DeviceConfigUpdated(int devid, byte[] nameBytes, byte[] locBytes)
	{
		HsbDevice device = FindDevice(devid);
		if (null == device) {
			Log.e("hsbservice", "DeviceConfigUpdated device not found, devid=" + devid);
			return;
		}

		String name = BytesToString(nameBytes);
		String location = BytesToString(locBytes);

		if (null == name || null == location) {
			Log.e("hsbservice", "DeviceConfigUpdated name or loc null");
			return;
		}

		Log.d("hsbservice", "DeviceConfigUpdated: [" + name + "][" + location + "]");
		device.onConfigUpdated(new HsbDeviceConfig(name, location));
	}

	private void DeviceResult(int devid, HsbResult result)
	{
		int cmd = result.mCommand;
		int errcode = result.mResult;

		if (0 == devid) {
			switch (cmd) {
				case HSB_CMD_ADD_DEV:
					if (mListener != null)
						mListener.onAddDevResult(errcode);
					break;
				case HSB_CMD_DEL_DEV:
					if (mListener != null)
						mListener.onDelDevResult(errcode);
					break;
				case HSB_CMD_PROBE_DEV:
					if (mListener != null)
						mListener.onProbeResult(errcode);
					break;
				case HSB_CMD_SET_SCENE:
					if (mSceneListener != null)
						mSceneListener.onSetSceneResult(errcode);
					break;
				case HSB_CMD_DEL_SCENE:
					if (mSceneListener != null)
						mSceneListener.onDelSceneResult(errcode);
					break;
				case HSB_CMD_ENTER_SCENE:
					if (mSceneListener != null)
						mSceneListener.onEnterSceneResult(errcode);
					break;
				case HSB_CMD_GET_SCENE:
					ValidateScene();

					if (mSceneListener != null)
						mSceneListener.onGetSceneResult(errcode);
					break;
				default:
					Log.e("hsbservice", "unknown cmd: " + cmd);
					break;
			}

			return;
		}

		HsbDevice device = FindDevice(devid);
		if (null == device) {
			Log.e("hsbservice", "DeviceResult device not found " + devid);
			return;
		}

		Log.d("hsbservice", "DeviceResult");

		switch (cmd) {
			case HSB_CMD_GET_INFO:
				device.onGetInfoError(errcode);
				break;
			case HSB_CMD_SET_CONFIG:
				device.onSetConfigResult(errcode);
				break;
			case HSB_CMD_GET_CONFIG:
				device.onGetConfigError(errcode);
				break;
			case HSB_CMD_GET_STATUS:
				device.onGetStatusError(errcode);
				break;
			case HSB_CMD_SET_STATUS:
				device.onSetStatusResult(errcode);
				break;
			case HSB_CMD_SET_CHANNEL:
				device.onSetChannelResult(errcode);
				break;
			case HSB_CMD_DEL_CHANNEL:
				device.onDelChannelResult(errcode);
				break;
			case HSB_CMD_SWITCH_CHANNEL:
				device.onSwitchChannelResult(errcode);
				break;
			case HSB_CMD_GET_CHANNEL:
				device.onGetChannelResult(errcode);
				break;
			case HSB_CMD_GET_TIMER:
				device.onGetTimerError(errcode);
				break;
			case HSB_CMD_SET_TIMER:
				device.onSetTimerResult(errcode);
				break;
			case HSB_CMD_DEL_TIMER:
				device.onDelTimerResult(errcode);
				break;
			case HSB_CMD_GET_DELAY:
				device.onGetDelayError(errcode);
				break;
			case HSB_CMD_SET_DELAY:
				device.onSetDelayResult(errcode);
				break;
			case HSB_CMD_DEL_DELAY:
				device.onDelDelayResult(errcode);
				break;
			case HSB_CMD_GET_LINKAGE:
				device.onGetLinkageError(errcode);
				break;
			case HSB_CMD_SET_LINKAGE:
				device.onSetLinkageResult(errcode);
				break;
			case HSB_CMD_DEL_LINKAGE:
				device.onDelLinkageResult(errcode);
				break;
			case HSB_CMD_DO_ACTION:
				device.onDoActionResult(errcode);
				break;
			default:
				Log.e("hsbservice", "unknown cmd: " + cmd);
				break;
		}
	}

    private void StartListen()  {
    	while (true) {
    		ProbeHsb();
    		
    		if (null != mHsbAddress)
    			TcpClientLoop();
    	}
    }

	private void StartListenTest()  {
		try {
			mHsbAddress = InetAddress.getByName("121.42.149.168");
		} catch(UnknownHostException e) {
			Log.e("hsbservice", "unknown host: 121.42.149.168");
		}

		while (true) {
			if (null != mHsbAddress)
				TcpClientLoop();
		}
	}

    @Override
    public void run() {
            StartListen();
    }

	private InetAddress getBroadcastAddress(Context ctx) throws IOException
	{
		WifiManager myWifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo myDhcpInfo = myWifiManager.getDhcpInfo();
		if (myDhcpInfo == null) {
			Log.e("hsbservice", "Could not get broadcast address");
			return null;
		}
		int broadcast = (myDhcpInfo.ipAddress & myDhcpInfo.netmask)
				| ~myDhcpInfo.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		return InetAddress.getByAddress(quads);
	}

	private int SearchZero(byte[] src) {
		int i;
		for (i = 0; i < src.length; i++) {
			if (src[i] == 0)
				return i;
		}

		return -1;
	}

	private String BytesToString(byte[] src) {
		String ret = null;

		int len = SearchZero(src);
		if (len < 0) {
			try {
				ret = new String(src, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			Log.e("hsbservice", "byte2string error: [" + ret + "]");
			return null;
		}

		try {
			ret = new String(src, 0, len, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return ret;
	}

	private byte[] StringToBytes(String src) {
		byte[] data = null;

		try {
			data = src.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return data;
	}
}