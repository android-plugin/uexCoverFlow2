package org.zywx.wbpalmstar.plugin.uexCoverFlow;

import java.util.ArrayList;
import java.util.List;

import org.zywx.wbpalmstar.engine.universalex.EUExUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView.OnItemClickListener;

public class CoverFlowMainActivity extends Activity {

    public static final String COVERFLOW_IMG_WIDTH = "coverFlowImgWidth";
    public static final String COVERFLOW_IMG_HEIGHT = "coverFlowImgHeight";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(EUExUtil.getResLayoutID("plugin_uexcoverflow_gallery"));
    }

    public void setData(String imgBgPath,CoverFlowData coverFlowData, OnItemClickListener itemClickListener) {
        Intent intent = getIntent();
        int imgWidth = intent.getIntExtra(COVERFLOW_IMG_WIDTH, 0);
        int imgHeight = intent.getIntExtra(COVERFLOW_IMG_HEIGHT, 0);
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
            imgAdapter = new ImageAdapter(CoverFlowMainActivity.this,imgBgPath, coverList, coverFlowData.getList());
        } else {
            imgAdapter = new ImageAdapter(CoverFlowMainActivity.this,imgBgPath, coverFlowData.getList(), null);
        }
        imgAdapter.setImgWidth(imgWidth);
        imgAdapter.setImgHeight(imgHeight);
        imgAdapter.createReflectedImages();
        galleryFlow.setAdapter(imgAdapter);
        galleryFlow.setOnItemClickListener(itemClickListener);
        galleryFlow.setSelection(imgAdapter.getCount() / 2);
    }

}