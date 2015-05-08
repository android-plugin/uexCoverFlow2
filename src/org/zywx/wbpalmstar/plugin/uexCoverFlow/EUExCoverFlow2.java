package org.zywx.wbpalmstar.plugin.uexCoverFlow;

import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class EUExCoverFlow2 extends EUExBase {

    public static final String CALLBACK_LOAD_DATA = "uexCoverFlow2.loadData";
    public static final String ON_ITEM_SELECTED = "uexCoverFlow2.onItemSelected";
    private static String TAG = "EUExCoverFlow";
    private HashSet<String> tmIdSet = new HashSet<String>();

    public EUExCoverFlow2(Context context, EBrowserView inParent) {
        super(context, inParent);
        // TODO Auto-generated constructor stub
    }

    public void open(String[] params) {
        if (params.length != 5) {
            return;
        }
        try {
            final String tmId = params[0];
            tmIdSet.add(tmId);
            final int x = Integer.parseInt(params[1]);
            final int y = Integer.parseInt(params[2]);
            final int w = Integer.parseInt(params[3]);
            final int h = Integer.parseInt(params[4]);
            ((ActivityGroup) mContext).runOnUiThread(new Runnable() {
                
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(mContext, CoverFlowMainActivity.class);
                    intent.putExtra(CoverFlowMainActivity.COVERFLOW_IMG_WIDTH, w);
                    intent.putExtra(CoverFlowMainActivity.COVERFLOW_IMG_HEIGHT, h);
                    LocalActivityManager mgr = ((ActivityGroup) mContext).getLocalActivityManager();
                    CoverFlowMainActivity coverFlowActivity = (CoverFlowMainActivity) mgr.getActivity(TAG + tmId);
                    if (coverFlowActivity != null) {
                        View view = coverFlowActivity.getWindow().getDecorView();
                        removeViewFromCurrentWindow(view);
                        view = null;
                    }
                    Window window = mgr.startActivity(TAG + tmId, intent);
                    View decorView = window.getDecorView();
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, h);
                    lp.leftMargin = x;
                    lp.topMargin = y;
                    addView2CurrentWindow(decorView, lp);
                    String js = SCRIPT_HEADER + "if(" + CALLBACK_LOAD_DATA + "){" + CALLBACK_LOAD_DATA + "('" + tmId + "');}";
                    onCallback(js);
                }
            });
           
        } catch (Exception e) {
            e.printStackTrace();
        }
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
		// adptLayoutParams(parms, lp);
		// Log.i(TAG, "addView2CurrentWindow");
		mBrwView.addViewToCurrentWindow(child, lp);
	}

    /**
     * 为CoverFlowData设置参数<br>
     * 实际形式:setJsonData(String json);
     * 
     * @param params
     */
    public void setJsonData(String[] params) {
        if (params.length != 2) {
            return;
        }
        final CoverFlowData coverFlowData = CoverFlowData.parseCoverFlowJson(params[0]);
        final String imgBgPath = params[1];
        if (coverFlowData == null) {
            return;
        }
        final ActivityGroup activityGroup = (ActivityGroup) mContext;
        activityGroup.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                LocalActivityManager mgr = activityGroup.getLocalActivityManager();
                Activity activity = mgr.getActivity(TAG + coverFlowData.getTmId());
                if (activity != null && activity instanceof CoverFlowMainActivity) {
                    CoverFlowMainActivity coverFlowMainActivity = ((CoverFlowMainActivity) activity);
                    coverFlowMainActivity.setData(imgBgPath,coverFlowData, new OnItemClickListener() {
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
            }
        });

    }

    public void close(String[] params) {
        if (params.length != 1) {
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
        tmIdSet.clear();
    }

    @Override
    protected boolean clean() {
        // TODO Auto-generated method stub
        for (String tmId : tmIdSet) {
            if (!TextUtils.isEmpty(tmId)) {
                closeCoverFlowView(tmId);
            }
        }
        return false;
    }

    private void closeCoverFlowView(final String tmId) {
        ((ActivityGroup) mContext).runOnUiThread(new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                LocalActivityManager mgr = ((ActivityGroup) mContext).getLocalActivityManager();
                Activity activity = mgr.getActivity(TAG + tmId);
                if (activity != null && activity instanceof CoverFlowMainActivity) {
                    CoverFlowMainActivity coverFlowActivity = ((CoverFlowMainActivity) activity);
                    View decorView = coverFlowActivity.getWindow().getDecorView();
                    removeViewFromCurrentWindow(decorView);
                    mgr.destroyActivity(TAG+tmId, true);
                }
            }
        });
       
    }
}
