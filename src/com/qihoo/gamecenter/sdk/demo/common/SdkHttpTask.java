
package com.qihoo.gamecenter.sdk.demo.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.qihoo.gamecenter.sdk.buildin.utils.HttpUtils;

/***
 * 通过http访问应用服务器，获取http返回结果
 */
public class SdkHttpTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "SdkHttpTask";

    private SdkHttpListener mListener;

    private ArrayList<NameValuePair> mKeyValueArray;

    private boolean mIsHttpPost;

    private Context mContext;

    public SdkHttpTask(Context context) {
        mContext = context;
    }

    public void doPost(SdkHttpListener listener, ArrayList<NameValuePair> keyValueArray,
            String url) {
        this.mListener = listener;
        this.mIsHttpPost = true;
        this.mKeyValueArray = keyValueArray;

        execute(url);
    }

    public void doGet(SdkHttpListener listener, String url) {
        this.mListener = listener;
        this.mIsHttpPost = false;

        execute(url);
    }

    @Override
    protected String doInBackground(String... params) {

        if (isCancelled())
            return null;

        String response = null;
        try {
            HttpResponse httpResp = executeHttp(mContext, params[0]);
            if (httpResp != null && !isCancelled()) {

                // int st = httpResp.getStatusLine().getStatusCode();
                // if (st == HttpStatus.SC_OK) {
                HttpEntity entity = httpResp.getEntity();
                if (entity != null) {
                    InputStream content = entity.getContent();
                    if (content != null) {
                        response = convertStreamToString(content);
                    }
                }
                // }
                Log.d(TAG, "response=" + response);
            }
        } catch (SSLHandshakeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        if (mListener != null) {
            mListener.onCancelled();
            mListener = null;
            Log.d(TAG, "onCancelled||" + this.toString());
        }
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);

        if (mListener != null && !isCancelled()) {
            mListener.onResponse(response);
            mListener = null;
            Log.d(TAG, "onResponse||" + this.toString());
        }
    }

    private HttpResponse executeHttp(Context context, String uri) throws SSLHandshakeException,
            ClientProtocolException, IOException {
        return mIsHttpPost ? HttpUtils.post(context, uri, mKeyValueArray) : HttpUtils.get(context,
                uri);
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
