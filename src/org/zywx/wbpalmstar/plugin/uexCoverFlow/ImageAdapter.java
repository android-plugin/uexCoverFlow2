
package org.zywx.wbpalmstar.plugin.uexCoverFlow;

import java.util.List;

import org.zywx.wbpalmstar.base.cache.BytesArrayFactory$BytesArray;
import org.zywx.wbpalmstar.base.cache.ImageLoadTask;
import org.zywx.wbpalmstar.base.cache.ImageLoadTask$ImageLoadTaskCallback;
import org.zywx.wbpalmstar.base.cache.ImageLoaderManager;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

@SuppressWarnings("unused")
public class ImageAdapter extends BaseAdapter {

    int mGalleryItemBackground;
    private Context mContext;
    private List<CoverFlowData.ItemInfo> mImgList;
    private List<CoverFlowData.ItemInfo> mOrgImgList;
    private LinearLayout[] mImages;
    private ImageLoaderManager imgLoadMgr;
    private int imgWidth;
    private int imgHeight;
    private String mImgBgPath;
    private static Bitmap bgBitmap = null;
    public ImageAdapter(Context c,String imgBgPath, List<CoverFlowData.ItemInfo> imgList, List<CoverFlowData.ItemInfo> orgImgList) {
        mContext = c;
        mImgList = imgList;
        mOrgImgList = orgImgList;
        mImgBgPath = imgBgPath;
        imgLoadMgr = ImageLoaderManager.initImageLoaderManager(c);
        mImages = new LinearLayout[mImgList.size()];
        if(bgBitmap == null)
            bgBitmap = combinateFrame(drawBgBitmap(CoverFlowDataUtility.getImage(mContext, mImgBgPath)));
    }

    public boolean createReflectedImages() {
        // TODO Auto-generated method stub
        for (int i = 0, length = mImages.length; i < length; i++) {
            CoverFlowData.ItemInfo itemInfo = mImgList.get(i);
            if (itemInfo != null) {
                final LinearLayout imgLinearLayout = new LinearLayout(mContext);
                int height = imgWidth * 706/609  ;
                imgLinearLayout.setLayoutParams(new GalleryFlow.LayoutParams((imgWidth*2/3-20),height));
                imgLinearLayout.setBackgroundColor(Color.TRANSPARENT);
                imgLinearLayout.setGravity(Gravity.CENTER);
                final ImageView imageView = new CoverFlowImageView(mContext);
                LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT); 
                imgParams.setMargins(5, 5, 5, 5);
                imageView.setLayoutParams(imgParams);
                imageView.setScaleType(ScaleType.FIT_CENTER);
                imageView.setBackgroundColor(Color.TRANSPARENT);
                imgLinearLayout.addView(imageView);
                mImages[i] = imgLinearLayout;
                imageView.setImageBitmap(bgBitmap);
                Bitmap bitmap = imgLoadMgr.getCacheBitmap(itemInfo.getImgUrl());
                if (bitmap == null) {
                    CoveryFlowImageTask coverFlowTask = new CoveryFlowImageTask(mContext, imageView,
                            itemInfo.getImgUrl());
                    coverFlowTask.addCallback(new ImageLoadTask$ImageLoadTaskCallback() {

                        @Override
                        public void onImageLoaded(ImageLoadTask arg0, Bitmap bitmap) {
                            // TODO Auto-generated method stub
                            if(bitmap != null){
                                imageView.setImageBitmap(bitmap);
                            }
                            // 需要缓存更新
//                          createReflectedImages();
//                            notifyDataSetChanged();
                        }

                    });
                    imgLoadMgr.asyncLoad(coverFlowTask);
                    if((mOrgImgList != null) && (mOrgImgList.size() < 4))
                    {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    imageView.setImageBitmap(bitmap);
                }
            }

        }
        return true;
    }

    public int getCount() {
        return ((mImages!= null) ? Integer.MAX_VALUE : 0);
    }

    public Integer getItem(int position) {
        if ((mOrgImgList != null) && (mOrgImgList.size() < 4)) {
            int nowPosition = position % mImages.length;
            switch (mOrgImgList.size()) {
            case 1: {
                nowPosition = 0;
            }
                break;
            case 2: {
                if (nowPosition == 2) {
                    nowPosition = 0;
                } else if (nowPosition == 3) {
                    nowPosition = 1;
                }
            }
                break;
            case 3: {
                if (nowPosition == 3) {
                    nowPosition = 0;
                }
            }
                break;
            }
            return nowPosition;
        } else {
            return getId(position);
        }
    }

    public long getItemId(int position) {
        return getId(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return mImages[getId(position)];
    }
    
    private int getId(int position)
    {
        return ((position >= mImages.length) 
                ? (position %= mImages.length) : position);
    }

    public float getScale(boolean focused, int offset) {
        return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
    }

    private class CoveryFlowImageTask extends ImageLoadTask {

        private static final long serialVersionUID = 1L;
        private Context mContext;
        private String mImgUrl;

        public CoveryFlowImageTask(Context context, ImageView imageView, String imgUrl) {
            // TODO Auto-generated constructor stub
            super(imgUrl);
            this.mContext = context;
            this.mImgUrl = imgUrl;
        }

        @Override
        protected Bitmap doInBackground() {
            // TODO Auto-generated method stub
        	Log.e("Tag==url==", mImgUrl);
            Bitmap myoriginalImage = CoverFlowDataUtility.getImage(mContext, mImgUrl);
//            if (myoriginalImage == null){
//                if(bgBitmap == null)
//                    myoriginalImage = CoverFlowDataUtility.getImage(mContext, mImgBgPath);
//                else
//                    myoriginalImage = bgBitmap;
//            }
            if(myoriginalImage == null)
                return null;
            //对图片进行按比例缩放，否则图片太大，内存溢出
            if(myoriginalImage != null){
                int width = myoriginalImage.getWidth();
                int height = myoriginalImage.getHeight();
                if(width > 250 && height > 300){
                	myoriginalImage = getZoomBitmap(myoriginalImage,250,300);
                }
            }
            Bitmap originalImage = combinateFrame(myoriginalImage);
            Bitmap bitmapWithReflection = drawBgBitmap(originalImage);
            return bitmapWithReflection;

        }

        @Override
        protected BytesArrayFactory$BytesArray transBitmapToBytesArray(Bitmap originalImage) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    public int getImgWidth() {
        return imgWidth;
    }

    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }

    public int getImgHeight() {
        return imgHeight;
    }

    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }
    /**
     * 绘制倒影
     * @param originalImage
     * @return
     */
    private Bitmap drawBgBitmap(Bitmap originalImage){
    	
    	if (originalImage == null) {
			return null;
		}
        final int reflectionGap = 4;
        // TODO Auto-generated method stub
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap
                .createBitmap(originalImage, 0, height / 2, width, height / 2, matrix, false);
        
        final Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 4), Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);

        canvas.drawBitmap(originalImage, 0, 0, null);
        
        Paint deafaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);

        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);

        paint.setShader(shader);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
        return bitmapWithReflection;
    }

    /**
     * 绘制图片白色边框
     * @param bm
     * @return
     */
    private Bitmap combinateFrame(Bitmap bm)
    {
        if(bm == null)
            return null;
        BitmapFactory.Options options = new BitmapFactory.Options();  
        options.inJustDecodeBounds = true;  
        
        // 边框的宽高
        final int smallW = 5;
        final int smallH = 5;
        
        // 原图片的宽高
        final int bigW = bm.getWidth();
        final int bigH = bm.getHeight();
        
        int wCount = (int) Math.ceil(bigW * 1.0 / smallW);
        int hCount = (int) Math.ceil(bigH  * 1.0 / smallH);
        
        // 组合后图片的宽高
        int newW = (wCount + 2) * smallW;
        int newH = (hCount + 2) * smallH;
        
        // 重新定义大小
        Bitmap newBitmap = Bitmap.createBitmap(newW, newH, Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        canvas.drawRect(new Rect(0, 0, newW, newH), p);
        // 绘原图
        canvas.drawBitmap(bm, (newW - bigW - 2 * smallW) / 2 + smallW, (newH - bigH - 2 * smallH) / 2 + smallH, null);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return newBitmap;
    }
    
    /**
     * 等比例缩放图片
     * 
     * @param bitmap
     * @param w
     * @param h
     * @return
     */
    private Bitmap getZoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidht = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidht, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newbmp;
    }
    
    /**
     * 自定义ImageView
     * 解决4.1以上出现的定位不准问题
     * @author zywx
     *
     */
    private class CoverFlowImageView extends ImageView{

		public CoverFlowImageView(Context context) {
			super(context);
		}
		
		@Override
		public void offsetLeftAndRight(int offset) {
			super.offsetLeftAndRight(offset);
			//解决在4.1以上出现的定位不准的问题
			if (android.os.Build.VERSION.SDK_INT >= 16) {
				invalidate();
			}
		}
    }
    
}
