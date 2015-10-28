package org.zywx.wbpalmstar.plugin.uexCoverFlow;

import java.util.ArrayList;
import java.util.List;

import org.zywx.wbpalmstar.engine.universalex.EUExUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;

public class CoverFlowMainView extends FrameLayout {

    public static final String COVERFLOW_IMG_WIDTH = "coverFlowImgWidth";
    public static final String COVERFLOW_IMG_HEIGHT = "coverFlowImgHeight";
    private Context mContext;
    private int imgWidth;
    private int imgHeight;

    public CoverFlowMainView(Context context, int w, int h) {
        super(context);
        mContext = context;
        imgWidth = w;
        imgHeight = h;
        LayoutInflater.from(mContext).inflate(EUExUtil.getResLayoutID("plugin_uexcoverflow_gallery"),
                this, true);
    }

    public void setData(String imgBgPath,CoverFlowData coverFlowData,
                        OnItemClickListener itemClickListener) {

        GalleryFlow galleryFlow = (GalleryFlow) findViewById(EUExUtil.getResIdID("plugin_uexcoverflow_gallery"));
        ImageAdapter imgAdapter = null;
        if (coverFlowData.getList().size() < 4) {
            List<CoverFlowData.ItemInfo> coverList = new ArrayList<CoverFlowData.ItemInfo>(coverFlowData.getList());
            int i = 0;
            while (coverList.size() < 4) {
                if (i >= coverFlowData.getList().size()) {
                    i = 0;
                }
                CoverFlowData.ItemInfo itemInfo = coverFlowData.getList().get(i);
                coverList.add(itemInfo);
                i++;
            }
            imgAdapter = new ImageAdapter(mContext, imgBgPath, coverList, coverFlowData.getList());
        } else {
            imgAdapter = new ImageAdapter(mContext, imgBgPath, coverFlowData.getList(), null);
        }
        imgAdapter.setImgWidth(imgWidth);
        imgAdapter.setImgHeight(imgHeight);
        imgAdapter.createReflectedImages();
        galleryFlow.setAdapter(imgAdapter);
        galleryFlow.setOnItemClickListener(itemClickListener);
        galleryFlow.setSelection(imgAdapter.getCount() / 2);
    }

}