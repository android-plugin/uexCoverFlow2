package org.zywx.wbpalmstar.plugin.uexCoverFlow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CoverFlowData {

    public static final String JK_ID = "id";
    public static final String JK_DATA = "data";
    public static final String JK_TITLE = "title";
    public static final String JK_URL_IMAGE_URL = "imageUrl";
    public static final String JK_PLACEHOLDER_IMAGE = "placeholderImage";

    /**
     * 解析TimeMachine相关信息
     *
     * @param msg
     * @return
     */

    public static CoverFlowData parseCoverFlowJson(String msg) {
        if (msg == null || msg.length() == 0) {
            return null;
        }
        CoverFlowData coverFlow = null;
        try {
            JSONObject json = new JSONObject(msg);
            coverFlow = new CoverFlowData();
            coverFlow.setTmId(json.optString(JK_ID, String.valueOf(getRandomId())));
            coverFlow.setPlaceholderImage(json.getString(JK_PLACEHOLDER_IMAGE));
            JSONArray array = json.getJSONArray(JK_DATA);
            for (int i = 0, size = array.length(); i < size; i++) {
                ItemInfo itemInfo = new ItemInfo();
                if (array.get(i) instanceof String) {//如果数组中只有图片的url
                    itemInfo.setImgUrl(array.get(i).toString());
                } else {//如果数组中是对象，包含有title和url
                    JSONObject jsonItem = array.getJSONObject(i);
                    itemInfo.setTitle(jsonItem.optString(JK_TITLE));
                    itemInfo.setImgUrl(jsonItem.getString(JK_URL_IMAGE_URL));
                }
                coverFlow.add(itemInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return coverFlow;
    }
    private static int getRandomId() {
        return (int)(Math.random() * 100000);
    }
    private String tmId;
    private String placeholderImage;
    private List<ItemInfo> list;

    public CoverFlowData() {
        list = new ArrayList<CoverFlowData.ItemInfo>();
    }

    public String getTmId() {
        return tmId;
    }

    public void setTmId(String tmId) {
        this.tmId = tmId;
    }

    public void setPlaceholderImage(String placeholderImage) {
        this.placeholderImage = placeholderImage;
    }

    public String getPlaceholderImage() {
        return placeholderImage;
    }

    public void add(ItemInfo item) {
        list.add(item);
    }

    public List<ItemInfo> getList() {
        return list;
    }

    public static class ItemInfo {
        private String title;
        private String imgUrl;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

    }
}