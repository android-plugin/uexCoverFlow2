package org.zywx.wbpalmstar.plugin.uexCoverFlow;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;

import java.util.HashMap;

public class EUExCoverFlow2 extends EUExBase {

    public static final String CALLBACK_LOAD_DATA = "uexCoverFlow2.loadData";
    public static final String ON_ITEM_SELECTED = "uexCoverFlow2.onItemSelected";
    private static String TAG = "EUExCoverFlow";
    private static final String BUNDLE_DATA = "data";
    private static final int MSG_OPEN = 1;
    private static final int MSG_SET_JSON_DATA = 2;
    private static final int MSG_CLOSE = 3;
    private HashMap<String, CoverFlowMainView> coverViews = new HashMap<String, CoverFlowMainView>();

    final String INVALID_CODE = null;

    public EUExCoverFlow2(Context context, EBrowserView inParent) {
        super(context, inParent);
    }

    /**
	 * @param child
	 * @param parms
	 */
	private void addView2CurrentWindow(View child,
			RelativeLayout.LayoutParams parms) {
		int l = (int) (parms.leftMargin);
		int t = (int) (parms.topMargin);
		int w = parms.width;
		int h = parms.height;
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
		lp.gravity = Gravity.NO_GRAVITY;
		lp.leftMargin = l;
		lp.topMargin = t;
		mBrwView.addViewToCurrentWindow(child, lp);
	}

    @Override
    protected boolean clean() {
        return false;
    }

    private void closeCoverFlowView(final String tmId) {
        if (!coverViews.containsKey(tmId) || (coverViews.get(tmId) == null)) return;
        removeViewFromCurrentWindow(coverViews.get(tmId));
        removeViewFromWebView(tmId);
        coverViews.remove(tmId);
    }
    public void open(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_OPEN;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void openMsg(String[] params) {
        if (params.length < 5) {
            return;
        }
        try {
            final String tmId = params[0];
            if (coverViews.containsKey(tmId)) return;
            final int x = (int)Float.parseFloat(params[1]);
            final int y = (int)Float.parseFloat(params[2]);
            final int w = (int)Float.parseFloat(params[3]);
            final int h = (int)Float.parseFloat(params[4]);
			String isAddToWebView = "0";
			if (params.length == 6) {
				isAddToWebView = params[5];
			}
            CoverFlowMainView coverFlowView = new CoverFlowMainView(mContext, w, h);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, h);
            lp.leftMargin = x;
            lp.topMargin = y;
            if("1".equals(isAddToWebView)){
				@SuppressWarnings("deprecation")
				AbsoluteLayout.LayoutParams lps = new AbsoluteLayout.LayoutParams(
						w, h, x, y);
            	addViewToWebView(coverFlowView, lps, tmId);
            }else{
            	addView2CurrentWindow(coverFlowView, lp);
            }
            String js = SCRIPT_HEADER + "if(" + CALLBACK_LOAD_DATA + "){"
                    + CALLBACK_LOAD_DATA + "('" + tmId + "');}";
            onCallback(js);
            coverViews.put(tmId, coverFlowView);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String create(String [] params) {
        if (params == null || params.length < 1) {
            return INVALID_CODE;
        }
        try {
            JSONObject jsonObject = new JSONObject(params[0]);
            int x = jsonObject.optInt("x");
            int y = jsonObject.optInt("y");
            int w = jsonObject.getInt("width");
            int h = jsonObject.getInt("height");
            boolean isAddToWebView = jsonObject.optBoolean("isScrollWithWeb", false);

            final CoverFlowData coverFlowData = CoverFlowData.parseCoverFlowJson(params[0]);

            if (coverFlowData == null) {
                return INVALID_CODE;
            }
            if (coverViews.containsKey(coverFlowData.getTmId())){
                return INVALID_CODE;
            }

            String tmId = coverFlowData.getTmId();
            final String imgBgPath = coverFlowData.getPlaceholderImage();

            CoverFlowMainView coverFlowView = new CoverFlowMainView(mContext, w, h);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, h);
            lp.leftMargin = x;
            lp.topMargin = y;
            if(isAddToWebView){
                @SuppressWarnings("deprecation")
                AbsoluteLayout.LayoutParams lps = new AbsoluteLayout.LayoutParams(
                        w, h, x, y);
                addViewToWebView(coverFlowView, lps, tmId);
            }else{
                addView2CurrentWindow(coverFlowView, lp);
            }
            coverViews.put(tmId, coverFlowView);


            coverFlowView.setData(imgBgPath, coverFlowData, new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    String js = SCRIPT_HEADER + "if(" + ON_ITEM_SELECTED + "){" + ON_ITEM_SELECTED + "('"
                            + coverFlowData.getTmId() + "'," + adapterView.getAdapter().getItem(position)
                            + ");}";
                    onCallback(js);
                }
            });
            return tmId;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return INVALID_CODE;
    }

    public void setJsonData(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_SET_JSON_DATA;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void setJsonDataMsg(String[] params) {
        if (params.length < 2) {
            return;
        }
        final CoverFlowData coverFlowData = CoverFlowData.parseCoverFlowJson(params[0]);
        final String imgBgPath = params[1];
        if (coverFlowData == null) {
            return;
        }
        if (!coverViews.containsKey(coverFlowData.getTmId())) return;
        CoverFlowMainView coverFlowView = coverViews.get(coverFlowData.getTmId());
        coverFlowView.setData(imgBgPath, coverFlowData, new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // TODO Auto-generated method stub
                String js = SCRIPT_HEADER + "if(" + ON_ITEM_SELECTED + "){" + ON_ITEM_SELECTED + "('"
                        + coverFlowData.getTmId() + "'," + adapterView.getAdapter().getItem(position)
                        + ");}";
                onCallback(js);
            }
        });
    }

    public void close(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_CLOSE;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void closeMsg(String[] params) {
        if (params == null || params.length < 1) {
            return;
        }
        if (!params[0].contains(",")) {
            closeCoverFlowView(params[0]);
            return;
        }
        String[] paramsArray = params[0].split(",");
        if (paramsArray != null) {
            for (int i = 0, length = paramsArray.length; i < length; i++) {
                final String tmId = paramsArray[i];
                if (TextUtils.isEmpty(tmId)) {
                    return;
                }
                closeCoverFlowView(tmId);
            }
        }
    }

    @Override
    public void onHandleMessage(Message message) {
        if(message == null){
            return;
        }
        Bundle bundle=message.getData();
        switch (message.what) {

            case MSG_OPEN:
                openMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_SET_JSON_DATA:
                setJsonDataMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_CLOSE:
                closeMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            default:
                super.onHandleMessage(message);
        }
    }
}
