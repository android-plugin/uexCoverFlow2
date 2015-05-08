package org.zywx.wbpalmstar.plugin.uexCoverFlow;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.zywx.wbpalmstar.base.BUtility;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CoverFlowDataUtility {

    public static Bitmap getImage(Context ctx, String imgUrl) {
        if (imgUrl == null || imgUrl.length() == 0) {
            return null;
        }

        Bitmap bitmap = null;
        InputStream is = null;
        try {
            if (imgUrl.startsWith(BUtility.F_Widget_RES_SCHEMA)) {
                is = BUtility.getInputStreamByResPath(ctx, imgUrl);
                bitmap = BitmapFactory.decodeStream(is);
            } else if (imgUrl.startsWith(BUtility.F_FILE_SCHEMA)) {
                imgUrl = imgUrl.replace(BUtility.F_FILE_SCHEMA, "");
                bitmap = BitmapFactory.decodeFile(imgUrl);
            } else if (imgUrl.startsWith(BUtility.F_Widget_RES_path)) {
                try {
                    is = ctx.getAssets().open(imgUrl);
                    if (is != null) {
                        bitmap = BitmapFactory.decodeStream(is);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (imgUrl.startsWith("/")) {
                bitmap = BitmapFactory.decodeFile(imgUrl);
            } else if (imgUrl.startsWith("http://")) {
                bitmap = downloadNetworkBitmap(imgUrl);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    private static Bitmap downloadNetworkBitmap(String url) {
        byte[] data = downloadImageFromNetwork(url);
        if (data == null || data.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    private static byte[] downloadImageFromNetwork(String url) {
        InputStream is = null;
        byte[] data = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            BasicHttpParams httpParams = new BasicHttpParams();
//            HttpConnectionParams.setConnectionTimeout(httpParams, 60);
//            HttpConnectionParams.setSoTimeout(httpParams, 60);
            HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(httpGet);
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                is = httpResponse.getEntity().getContent();
                data = transStreamToBytes(is, 4096);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    private static byte[] transStreamToBytes(InputStream is, int buffSize) {
        if (is == null) {
            return null;
        }
        if (buffSize <= 0) {
            throw new IllegalArgumentException("buffSize can not less than zero.....");
        }
        byte[] data = null;
        byte[] buffer = new byte[buffSize];
        int actualSize = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            while ((actualSize = is.read(buffer)) != -1) {
                baos.write(buffer, 0, actualSize);
            }
            data = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }
}
