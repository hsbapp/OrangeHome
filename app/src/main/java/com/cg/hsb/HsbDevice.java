
package com.cg.hsb;

import android.os.SystemClock;
import android.util.Log;

import com.cg.hsb.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class  HsbDevice {
    protected int mDevId;
    protected String mDevType = "";
    protected String mIrType = "";
    protected String mMac;
    protected String mName;
    protected String mLocation;

    protected ArrayList<HsbDeviceEndpoint> mEndpoints;

    protected boolean mOnline;
    protected ArrayList<HsbChannel> mChannelList;

    protected Protocol mProto = null;
    protected HsbDeviceListener mListener = null;
    protected boolean mSupportCondition;
    protected boolean mSupportAction;

    public HsbDevice(Protocol proto, int devid) {
        mProto = proto;
        mOnline = true;
        mDevId = devid;
        mChannelList = null;
        mEndpoints = new ArrayList<HsbDeviceEndpoint>();
        mSupportCondition = false;
        mSupportAction = false;
        mName = "";
        mLocation = "";
    }

    static public HsbDevice CreateDevice(Protocol proto, int devid, String devtype, String irtype)
    {
        HsbDevice device = null;

        if (devtype == HsbConstant.HSB_DEV_TYPE_PLUG) {
            device = new PlugDevice(proto, devid);
        } else if (devtype == HsbConstant.HSB_DEV_TYPE_SENSOR) {
            device = new SensorDevice(proto, devid);
        } else if (devtype == HsbConstant.HSB_DEV_TYPE_REMOTE_CTL) {
            device = new RemoteCtlDevice(proto, devid);
        } else if (devtype == HsbConstant.HSB_DEV_TYPE_IR) {
            if (irtype == HsbConstant.HSB_IR_TYPE_TV) {
                device = new TVDevice(proto, devid);
            } else if (irtype == HsbConstant.HSB_IR_TYPE_AC) {
                device = new ACDevice(proto, devid);
            }
        } else if (devtype == HsbConstant.HSB_DEV_TYPE_RELAY) {
            device = new RelayDevice(proto, devid);
        } else if (devtype == HsbConstant.HSB_DEV_TYPE_CURTAIN) {
            device = new CurtainDevice(proto, devid);
        }

        if (null == device)
            device = new HsbDevice(proto, devid);

        return device;
    }

    public void Offline()
    {
        mOnline = false;
    }

    public boolean Available() {
        if (null != mProto && mOnline)
            return true;
        return false;
    }

    protected HsbDeviceEndpoint FindEndpoint(int epid)
    {
        for (int id = 0; id < mEndpoints.size(); id++)
        {
            HsbDeviceEndpoint ep = mEndpoints.get(id);
            if (ep.GetID() == epid)
                return ep;
        }

        return null;
    }

    protected HsbDeviceEndpoint FindEndpoint(String name)
    {
        for (int id = 0; id < mEndpoints.size(); id++)
        {
            HsbDeviceEndpoint ep = mEndpoints.get(id);
            if (ep.GetName() == name)
                return ep;
        }

        return null;
    }

    public boolean SetObject(JSONObject object)
    {
        try {
            if (!object.has("mac") || !object.has("devtype"))
                return false;

            mMac = object.getString("mac");
            mDevType = object.getString("devtype");

            if (object.has("irtype"))
            {
                mIrType = object.getString("irtype");
            }

            if (object.has("attrs"))
            {
                JSONObject attrs = object.getJSONObject("attrs");
                mName = attrs.getString("name");
                mLocation = attrs.getString("location");
            }

            if (object.has("endpoints"))
            {
                JSONArray eps = object.getJSONArray("endpoints");
                for (int i = 0; i < eps.length(); i++)
                {
                    JSONObject ep = (JSONObject)eps.opt(i);
                    if (!ep.has("epid"))
                        continue;

                    int epid = ep.getInt("epid");
                    HsbDeviceEndpoint endpoint = FindEndpoint(epid);
                    if (null == endpoint)
                    {
                        endpoint = new HsbDeviceEndpoint(epid);
                        mEndpoints.add(endpoint);
                    }

                    endpoint.SetObject(ep);
                }
            }
        } catch (JSONException e) {
            Log.e("hsbservice", "SetObject fail");
            return false;
        }

        UpdateCapabilities();

        onDeviceUpdated();

        return true;
    }

    private void onDeviceUpdated()
    {
        if (null != mListener)
            mListener.onDeviceUpdated(this);
    }

    protected boolean SetEndpointVal(String name, int val)
    {
        HsbDeviceEndpoint ep = FindEndpoint(name);
        if (null == ep)
            return false;

        return SetEndpointVal(ep, val);
    }

    protected boolean SetEndpointVal(String name, String valDesc)
    {
        HsbDeviceEndpoint ep = FindEndpoint(name);
        if (null == ep)
            return false;

        return SetEndpointVal(ep, ep.GetVal(valDesc));
    }

    protected boolean SetEndpointVal(int id, int val)
    {
        HsbDeviceEndpoint ep = FindEndpoint(id);
        if (null == ep)
            return false;

        return SetEndpointVal(ep, val);
    }

    protected boolean SetEndpointVal(HsbDeviceEndpoint endpoint, int val)
    {
        if (!endpoint.CheckVal(val))
            return false;

        int epid = endpoint.GetID();
        JSONObject object = null;

        try {
            object = new JSONObject();
            object.put("devid", mDevId);

            JSONArray eps = new JSONArray();
            JSONObject ep = new JSONObject();
            ep.put("epid", epid);
            ep.put("val", val);
            eps.put(ep);
            object.put("endpoints", eps);
        } catch (JSONException e) {
            Log.e("hsbservice", "SetName fail");
            return false;
        }

        return mProto.SetDevice(object);
    }

    protected boolean AddEndpointVal(int id)
    {
        HsbDeviceEndpoint ep = FindEndpoint(id);
        if (null == ep)
            return false;

        int val = ep.GetNextVal();

        return SetEndpointVal(ep, val);
    }

    protected boolean AddEndpointVal(String name)
    {
        HsbDeviceEndpoint ep = FindEndpoint(name);
        if (null == ep)
            return false;

        int val = ep.GetNextVal();

        return SetEndpointVal(ep, val);
    }

    protected boolean DecEndpointVal(int id)
    {
        HsbDeviceEndpoint ep = FindEndpoint(id);
        if (null == ep)
            return false;

        int val = ep.GetPrevVal();

        return SetEndpointVal(ep, val);
    }

    protected boolean DecEndpointVal(String name)
    {
        HsbDeviceEndpoint ep = FindEndpoint(name);
        if (null == ep)
            return false;

        int val = ep.GetPrevVal();

        return SetEndpointVal(ep, val);
    }

    protected int GetEndpointVal(int id)
    {
        HsbDeviceEndpoint ep = FindEndpoint(id);
        if (null == ep)
            return 0;

        return GetEndpointVal(ep);
    }

    protected int GetEndpointVal(String name)
    {
        HsbDeviceEndpoint ep = FindEndpoint(name);
        if (null == ep)
            return 0;

        return GetEndpointVal(ep);
    }

    protected int GetEndpointVal(HsbDeviceEndpoint endpoint)
    {
        return endpoint.GetVal();
    }

    protected String GetEndpointValDesc(int id)
    {
        HsbDeviceEndpoint ep = FindEndpoint(id);
        if (null == ep)
            return "";

        return GetEndpointValDesc(ep);
    }

    protected String GetEndpointValDesc(String name)
    {
        HsbDeviceEndpoint ep = FindEndpoint(name);
        if (null == ep)
            return "";

        return GetEndpointValDesc(ep);
    }

    protected String GetEndpointValDesc(HsbDeviceEndpoint endpoint)
    {
        return endpoint.GetValDesc();
    }

    private boolean SetAttrs(String name, String value)
    {
        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put("devid", mDevId);

            JSONObject attrs = new JSONObject();
            attrs.put(name, value);
            object.put("attrs", attrs);
        } catch (JSONException e) {
            Log.e("hsbservice", "SetName fail");
            return false;
        }

        return mProto.SetDevice(object);
    }

    public void UpdateCapabilities()
    {
        if (mSupportCondition && mSupportAction)
            return;

        for (int id = 0; id < mEndpoints.size(); id++)
        {
            HsbDeviceEndpoint ep = mEndpoints.get(id);
            if (!mSupportCondition && ep.Readable())
                mSupportCondition = true;
            if (!mSupportAction && ep.Writable())
                mSupportAction = true;
        }
    }

    public HsbDeviceCondition MakeCondition(HsbDeviceEndpoint ep, int val, String expr)
    {
        return new HsbDeviceCondition(mDevId, ep.GetID(), val, expr);
    }

    public HsbDeviceCondition MakeCondition(HsbDeviceEndpoint ep, String valDesc, String expr)
    {
        return new HsbDeviceCondition(mDevId, ep.GetID(), ep.GetVal(valDesc), expr);
    }

    public HsbDeviceAction MakeAction(HsbDeviceEndpoint ep, int val)
    {
        return new HsbDeviceAction(mDevId, ep.GetID(), val);
    }

    public HsbDeviceAction MakeAction(HsbDeviceEndpoint ep, String valDesc)
    {
        return new HsbDeviceAction(mDevId, ep.GetID(), ep.GetVal(valDesc));
    }

    public boolean SetName(String name)
    {
        return SetAttrs("name", name);
    }

    public boolean SetLocation(String location)
    {
        return SetAttrs("location", location);
    }

    public void SetListener(HsbDeviceListener listener) {
        mListener = listener;
    }

    public HsbDeviceListener GetListener() {
        return mListener;
    }

    public int GetDevId() {
        return mDevId;
    }

    public String GetDevType() {
        return mDevType;
    }

    public String GetIrType() {
        return mIrType;
    }

    public boolean SupportCondition() {
        return mSupportCondition;
    }

    public boolean SupportAction() {
        return mSupportAction;
    }

    public String GetName() {
        return mName;
    }

    public String GetLocation() {
        return mLocation;
    }

    public boolean SupportChannel() {
        return (mChannelList != null);
    }

    public ArrayList<HsbChannel> GetChannelList() {
        return mChannelList;
    }

    public void SetChannel(String name, int id)
    {
        if (!Available() || !SupportChannel())
            return;

        // TODO
    }

    public void DelChannel(String name)
    {
        if (!Available() || !SupportChannel())
            return;

        // TODO
    }

    public void SwitchChannel(String name)
    {
        if (!Available() || !SupportChannel())
            return;

        // TODO
    }

    public void onSetDeviceResult(int errcode) {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onSetDeviceResult(errcode);
    }

    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof HsbDevice)
        {
            sameSame = (this.mDevId == ((HsbDevice) object).mDevId);
        }

        return sameSame;
    }

    /* Channel */
    public class HsbChannel {
        private String mName;
        private int mId;

        public HsbChannel(String name, int id) {
            mName = name;
            mId = id;
        }

        public String GetName(){
            return mName;
        }

        public int GetId() {
            return mId;
        }

        @Override
        public boolean equals(Object object)
        {
            boolean sameSame = false;

            if (object != null && object instanceof HsbChannel)
            {
                sameSame = (this.mName.equals(((HsbChannel)object).mName));
            }

            return sameSame;
        }
    }
}