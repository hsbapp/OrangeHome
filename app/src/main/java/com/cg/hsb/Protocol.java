
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	private Boolean IsHsbConnected = false;
	Socket mSocket = null;

	OutputStream mOutputStream;

	private ArrayList<HsbDevice> mDevList;
	private ArrayList<HsbScene> mSceneList;
	private ArrayList<HsbDeviceTimer> mTimerList;

	private HsbListener mListener = null;
	private HsbSceneListener mSceneListener = null;
	private HsbTimerListener mTimerListener = null;

	private final String HSB_CMD_BOX_DISCOVER = "are you hsb?";
	private final String HSB_CMD_BOX_DISCOVER_RESP = "i'm a hsb";
	private final String HSB_CMD_GET_DEVS = "get_devices";
	private final String HSB_CMD_SET_DEVS = "set_devices";
	private final String HSB_CMD_ADD_DEVICES = "add_devices";
	private final String HSB_CMD_DEL_DEVICES = "del_devices";
	private final String HSB_CMD_DEVS_ONLINE = "devices_online";
	private final String HSB_CMD_DEVS_UPDATED = "devices_updated";
	private final String HSB_CMD_DEVS_OFFLINE = "devices_offline";
	private final String HSB_CMD_GET_SCENES = "get_scenes";
	private final String HSB_CMD_SET_SCENES = "set_scenes";
	private final String HSB_CMD_DEL_SCENES = "del_scenes";
	private final String HSB_CMD_ENTER_SCENE = "enter_scene";
	private final String HSB_CMD_SCENES_UPDATED = "scenes_updated";
	private final String HSB_CMD_GET_TIMERS = "get_timers";
	private final String HSB_CMD_SET_TIMERS = "set_timers";
	private final String HSB_CMD_DEL_TIMERS = "del_timers";
	private final String HSB_CMD_TIMERS_UPDATED = "timers_updated";

	public Protocol(Context ctx) {
		mDevList = new ArrayList<HsbDevice>();
		mSceneList = new ArrayList<HsbScene>();
		mTimerList = new ArrayList<HsbDeviceTimer>();

		try {
			mBcAddress = getBroadcastAddress(ctx);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (null == mBcAddress)
			Log.e("hsbservice", "getBroadcastAddress fail");
	}

	private void log(String content)
	{
		Log.e("hsbservice", content);
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

	public void SetTimerListener(HsbTimerListener listener) {
		mTimerListener = listener;
	}

	private boolean ProbeHsb() {
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

		byte[] cmd = HSB_CMD_BOX_DISCOVER.getBytes();
		int len = cmd.length;

		byte[] rbuf = new byte[16];

		DatagramPacket p = new DatagramPacket(cmd, len, mBcAddress, server_port);
		DatagramPacket rp = new DatagramPacket(rbuf, rbuf.length);

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

		return bFound;
	}

	public boolean GetConnectivity() {
		return IsHsbConnected;
	}

	private void UpdateConnectivity(Boolean conn) {
		IsHsbConnected = conn;

		Log.d("hsbservice", "UpdateConnectivity " + conn);

		if (mListener != null)
			mListener.onHsbOnline(conn);
	}

	public HsbDevice FindDevice(String name, String location) {
		for (HsbDevice device : mDevList)
		{
			if (name.equals(device.GetName()) &&
				location.equals(device.GetLocation()))
			{
				return device;
			}
		}

		return null;
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

	private boolean SendHsbCmd(byte[] buf) {
		try {
			mOutputStream.write(buf, 0, buf.length);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private boolean SendHsbCmd(String cmd, JSONObject obj) {
		JSONObject command = obj;
		if (null == command)
			command = new JSONObject();

		try {
			command.put("cmd", cmd);
		} catch (JSONException ex) {
			Log.e("hsbservice", "SendHsbCmd pack fail");
			return false;
		}

		byte[] buf = command.toString().getBytes();
		byte[] rbuf = new byte[buf.length + 4];
		Decode2LE(rbuf, 0, 0x55AA);
		Decode2LE(rbuf, 2, rbuf.length);
		System.arraycopy(buf, 0, rbuf, 4, buf.length);

		return SendHsbCmd(rbuf);
	}

	private boolean CheckConnectivity()
	{
		if (mSocket == null || IsHsbConnected == false)
			return false;

		return true;
	}

	public void StartVoiceRecognizer() {

	}

	public void StopVoiceRecognizer() {

	}

	/* 1. get devices */
	private void GetDevicesAsyc()
	{
		SendHsbCmd(HSB_CMD_GET_DEVS, null);
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

	private boolean SetSceneAsync(HsbScene scene)
	{
		JSONObject total = new JSONObject();
		JSONArray scenes = new JSONArray();
		JSONObject obj = scene.GetObject();

		if (null == total || null == scenes || null == obj)
			return false;

		try {
			scenes.put(obj);
			total.put("scenes", scenes);
		} catch (JSONException ex) {
			log("SetSceneAsync fail");
			return false;
		}

		return SendHsbCmd(HSB_CMD_SET_SCENES, total);
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

	private boolean GetSceneAsync()
	{
		return SendHsbCmd(HSB_CMD_GET_SCENES, null);
	}

	public void DelScene(String SceneName)
	{
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

		for (HsbScene scene : mSceneList)
		{
			if (scene.GetName() == SceneName)
				mSceneList.remove(scene);
		}
	}

	private boolean DelSceneAsync(String SceneName)
	{
		JSONObject total = new JSONObject();
		JSONObject obj = new JSONObject();
		JSONArray scenes = new JSONArray();

		try {
			obj.put("name", SceneName);
			scenes.put(obj);
			total.put("scenes", scenes);
		} catch (JSONException ex) {
			log("DelSceneAsync fail");
			return false;
		}

		return SendHsbCmd(HSB_CMD_DEL_SCENES, total);
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

	private boolean EnterSceneAsync(String SceneName)
	{
		JSONObject obj = new JSONObject();

		try {
			obj.put("name", SceneName);
		} catch (JSONException ex) {
			log("EnterSceneAsync fail");
			return false;
		}

		return SendHsbCmd(HSB_CMD_ENTER_SCENE, obj);
	}


	public ArrayList<HsbDeviceTimer> GetTimerList() {
		return mTimerList;
	}

	public void SetTimer(HsbDeviceTimer timer)
	{
		if (!CheckConnectivity())
			return;

		final HsbDeviceTimer _timer = timer;

		new Thread()
		{
			@Override
			public void run()
			{
				SetTimerAsync(_timer);
			}
		}.start();
	}

	private boolean SetTimerAsync(HsbDeviceTimer timer)
	{
		JSONObject total = new JSONObject();
		JSONArray timers = new JSONArray();
		JSONObject obj = timer.GetObject();

		if (null == total || null == timers || null == obj)
			return false;

		try {
			timers.put(obj);
			total.put("timers", timers);
		} catch (JSONException ex) {
			log("SetTimerAsync fail");
			return false;
		}

		return SendHsbCmd(HSB_CMD_SET_TIMERS, total);
	}

	public void GetTimer() {
		if (!CheckConnectivity())
			return;

		new Thread()
		{
			@Override
			public void run()
			{
				GetTimerAsync();
			}
		}.start();
	}

	private boolean GetTimerAsync()
	{
		return SendHsbCmd(HSB_CMD_GET_TIMERS, null);
	}

	public void DelTimer(int TimerID)
	{
		if (!CheckConnectivity())
			return;

		final int _TimerID = TimerID;

		new Thread()
		{
			@Override
			public void run()
			{
				DelTimerAsync(_TimerID);
			}
		}.start();

		for (HsbDeviceTimer timer : mTimerList)
		{
			if (timer.GetID() == TimerID)
				mTimerList.remove(timer);
		}
	}

	private boolean DelTimerAsync(int TimerID)
	{
		JSONObject total = new JSONObject();
		JSONObject obj = new JSONObject();
		JSONArray timers = new JSONArray();

		try {
			obj.put("tmid", TimerID);
			timers.put(obj);
			total.put("timers", timers);
		} catch (JSONException ex) {
			log("DelTimerAsync fail");
			return false;
		}

		return SendHsbCmd(HSB_CMD_DEL_TIMERS, total);
	}

	public void AddDevice(String irtype, String name, String location)
	{
		if (!CheckConnectivity())
			return;

		final String _irtype = irtype;
		final String _name = name;
		final String _location = location;

		new Thread()
		{
			@Override
			public void run()
			{
				AddDeviceAsync(_irtype, _name, _location);
			}
		}.start();
	}

	private boolean AddDeviceAsync(String irtype, String name, String location)
	{
		JSONObject attrs = new JSONObject();
		JSONObject device = new JSONObject();
		JSONArray devices = new JSONArray();
		JSONObject object = new JSONObject();

		try {
			attrs.put("name", name);
			attrs.put("location", location);
			attrs.put("irtype", irtype);
			device.put("attrs", attrs);

			devices.put(device);

			object.put("devices", devices);
		} catch (JSONException ex) {
			log("AddDeviceAsync fail");
			return false;
		}

		return SendHsbCmd(HSB_CMD_ADD_DEVICES, object);
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

	private boolean DelDeviceAsync(int devid)
	{
		JSONObject device = new JSONObject();
		JSONArray devices = new JSONArray();
		JSONObject object = new JSONObject();

		try {
			device.put("devid", devid);
			devices.put(device);
			object.put("devices", devices);
		} catch (JSONException ex) {
			log("DelDeviceAsync fail");
			return false;
		}

		return SendHsbCmd(HSB_CMD_DEL_DEVICES, object);
	}

	public boolean SetDevice(JSONObject devobj)
	{
		JSONObject object = new JSONObject();
		JSONArray devices = new JSONArray();

		try {
			devices.put(devobj);
			object.put("devices", devices);
		} catch (JSONException e) {
			Log.e("hsbservice", "SetDevice fail");
			return false;
		}

		return SendHsbCmd(HSB_CMD_SET_DEVS, object);
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

	    int magic, ret, len = 0;
    	while (true) {
    		try {
    			ret = inputStream.read(rbuf, 0, 4);
    		}
    		catch (IOException e)
        	{
        		e.printStackTrace();
        		break;
        	}

    		if (ret == -1) {
				Log.e("hsbservice", "socket read fail");
				break;
			}

			magic = Make2LE(rbuf, 0);
			len = Make2LE(rbuf, 2);

			if (magic != 0x55AA || len <= 4 || len > rbuf.length) {
				log("wrong cmd: magic=" + magic + "len=" + len);
				continue;
			}

			try {
				ret = inputStream.read(rbuf, 4, len - 4);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				break;
			}

			if (ret == -1) {
				log("socket read fail");
				break;
			} else if (ret != len - 4){
				log("wrong cmd: len=" + len + "ret=" + ret);
				continue;
			}

			String cmd = new String(rbuf, 4, len - 4);

			DealHsbCommand(cmd);
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

	private void DealHsbCommand(String data)
	{
		boolean breply = false;
		String cmd = null;
		JSONObject object = null;

		try {
			object = new JSONObject(data);
			cmd = object.getString("cmd");
		} catch (JSONException e) {
			log(e.toString());
			return;
		}

		if (null == object || null == cmd)
			return;

		breply = cmd.endsWith("_reply");
		if (breply)
			cmd =  cmd.substring(0, cmd.length() - 7);

		if (!breply) { // event
			if (cmd == HSB_CMD_DEVS_ONLINE || cmd == HSB_CMD_DEVS_UPDATED) {
				UpdateDevices(cmd, object);
			} else if (cmd == HSB_CMD_DEVS_OFFLINE) {
				OfflineDevices(object);
			} else if (cmd == HSB_CMD_SCENES_UPDATED) {
				UpdateScenes(object);
			} else if (cmd == HSB_CMD_TIMERS_UPDATED) {
				UpdateTimers(object);
			}
		} else { // breply
			if (cmd == HSB_CMD_SET_DEVS) {

			} else if (cmd == HSB_CMD_GET_SCENES) {
				UpdateScenes(object);
			} else if (cmd == HSB_CMD_GET_TIMERS) {
				UpdateTimers(object);
			}
		}
	}

	public HsbDevice FindDevice(int devid) {

		for (HsbDevice device : mDevList) {
			if (null != device && device.GetDevId() == devid)
				return device;
		}

		return null;
	}

	private HsbDevice DeviceAdded(int devid, String devtype, String irtype) {
		HsbDevice device = FindDevice(devid);
		if (device != null) {
			return device;
		}

		Log.i("hsbservice", "DeviceAdded, devid=" + devid + " type=" + devtype);

		device = HsbDevice.CreateDevice(this, devid, devtype, irtype);

		mDevList.add(device);
		return device;
	}

	private void UpdateDevices(String cmd, JSONObject object)
	{
		JSONArray devices = null;
		int devid = 0;
		String devtype = "";
		String irtype = "";
		try {
			devices = object.getJSONArray("devices");

			for (int i = 0; i < devices.length(); i++) {
				JSONObject devobj = (JSONObject)devices.opt(i);
				if (!devobj.has("devid") || !devobj.has("devtype")) {
					log("device devid or devtype not found");
					continue;
				}

				devid = devobj.getInt("devid");
				devtype = devobj.getString("devtype");

				if (devobj.has("irtype"))
					irtype = devobj.getString("irtype");

				HsbDevice device = DeviceAdded(devid, devtype, irtype);
				device.SetObject(devobj);

				if (cmd == HSB_CMD_DEVS_ONLINE)
					DeviceOnline(devid, true);
				else
					DeviceUpdated(devid);
			}
		} catch (JSONException e) {
			log(e.toString());
			return;
		}
	}

	private void OfflineDevices(JSONObject object)
	{
		JSONArray devices = null;
		try {
			devices = object.getJSONArray("devices");

			for (int i = 0; i < devices.length(); i++) {
				JSONObject devobj = (JSONObject) devices.opt(i);
				if (!devobj.has("devid")) {
					log("device devid not found");
					continue;
				}

				int devid = devobj.getInt("devid");
				DeviceOnline(devid, false);
			}
		} catch (JSONException e) {
			log(e.toString());
			return;
		}
	}

	private void UpdateScenes(JSONObject object)
	{
		ArrayList<HsbScene> scenes = new ArrayList<HsbScene>();
		try {
			JSONArray objs = object.getJSONArray("scenes");

			for (int i = 0; i < objs.length(); i++)
			{
				JSONObject obj = (JSONObject)objs.opt(i);
				if (!obj.has("name")) {
					log("scene name not found");
					continue;
				}

				String name = obj.getString("name");

				HsbScene scene = new HsbScene(name);
				if (null == scene)
					continue;

				scene.SetObject(obj);
				scenes.add(scene);
			}
		} catch (JSONException e) {
			log(e.toString());
			return;
		}

		mSceneList = scenes;
		if (null != mSceneListener)
			mSceneListener.onSceneUpdated();
	}

	private void UpdateTimers(JSONObject object)
	{
		ArrayList<HsbDeviceTimer> timers = new ArrayList<HsbDeviceTimer>();
		try {
			JSONArray objs = object.getJSONArray("timers");

			for (int i = 0; i < objs.length(); i++)
			{
				JSONObject obj = (JSONObject)objs.opt(i);
				HsbDeviceTimer timer = new HsbDeviceTimer();
				if (null == timer)
					continue;

				timer.SetObject(obj);
				timers.add(timer);
			}
		} catch (JSONException e) {
			log(e.toString());
			return;
		}

		mTimerList = timers;
		if (null != mTimerListener)
			mTimerListener.onTimerUpdated();
	}

	private void FreeDevice(int devid) {
		HsbDevice device = FindDevice(devid);
		if (device == null) {
			log("FreeDevice device not found, devid=" + devid);
			return;
		}

		mDevList.remove(device);
	}

	private void DeviceOnline(int devid, boolean online)
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
	}

	private void DeviceUpdated(int devid)
	{
		HsbDevice device = FindDevice(devid);
		if (null == device) {
			Log.e("hsbservice", "DeviceUpdated device not found, devid=" + devid);
			return;
		}

		if (mListener != null)
			mListener.onDeviceUpdated(device);
	}

	public boolean ValidateDevice(int devid) {
		for (HsbDevice device : mDevList) {
			if (device.GetDevId() == devid)
				return true;
		}

		return false;
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
            StartListenTest();
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
}