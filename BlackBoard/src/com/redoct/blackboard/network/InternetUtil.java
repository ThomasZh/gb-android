package com.redoct.blackboard.network;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.SocketTimeoutException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import android.util.Log;

public class InternetUtil {

    public interface OnHttpListener {


        void onResult(int resultCode, String result, Header[] headers);

        void onResult(int resultCode, String result);

    }

    private OnHttpListener mOnHttpListener;

    public void setOnHttpListener(OnHttpListener mOnHttpListener) {

        this.mOnHttpListener = mOnHttpListener;
    }


    //post方式上传内容至服务器，content为Json格式的字符串
    public boolean postContentToServer(String url, String sessionId, String content, Boolean needSession) {

        Log.e("zyf", "post url: " + url);

        HttpClient mPostHttpClient;

        StringEntity entity = null;

        mPostHttpClient = null;

        try {
            entity = new StringEntity(content, HTTP.UTF_8);
            entity.setContentType("application/json");
            HttpPost request = new HttpPost(url);
            request.addHeader("Content-Type", "application/json");
            if (needSession) {

                request.addHeader("X-Session-Id", sessionId);
                Log.e("zyf", "X-Session-Id: " + sessionId);
            }
            request.addHeader("Cache-Control", "no-cache");

            request.setEntity(entity);


            mPostHttpClient = new DefaultHttpClient();
            mPostHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, Constant.TIME_OUT);
            mPostHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, Constant.TIME_OUT);

            HttpResponse httpResponse = null;

            httpResponse = mPostHttpClient.execute(request);

            Log.e("zyf", "post StatusCode: " + httpResponse.getStatusLine().getStatusCode());

            if (httpResponse.getStatusLine().getStatusCode() == 200) {

                Log.e("zyf", "post content success...");

                InputStream inputStream = httpResponse.getEntity().getContent();

                Header[] headers = httpResponse.getAllHeaders();

                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                int count = -1;
                while ((count = inputStream.read(data, 0, 1024)) != -1)
                    outStream.write(data, 0, count);
                String datas = new String(outStream.toByteArray());

                if (mOnHttpListener != null) {

                    mOnHttpListener.onResult(200, datas, headers);
                    //mOnHttpListener.onResult(200,datas);
                }

                return true;

            } else {

                if (mOnHttpListener != null) {
                    Header[] headers = httpResponse.getAllHeaders();

                    mOnHttpListener.onResult(httpResponse.getStatusLine().getStatusCode(), "", headers);
                    //	mOnHttpListener.onResult(httpResponse.getStatusLine().getStatusCode(),"");

                    return false;
                }
            }

        } catch (Exception e) {

            Log.e("zyf", "post exception: " + e.toString());
            

            if(e instanceof SocketTimeoutException|| e instanceof ConnectTimeoutException || e instanceof HttpHostConnectException){
            	
            	if (mOnHttpListener != null) {

                    mOnHttpListener.onResult(Constant.RESULT_CODE_TIME_OUT, "", null);
                }
                return false;
            }
        } finally {
            if (mPostHttpClient != null) {
                mPostHttpClient.getConnectionManager().shutdown();
                mPostHttpClient = null;

                Log.e("zyf", "post completed,shutdowm the network......");
            }
        }

        if (mOnHttpListener != null) {

            mOnHttpListener.onResult(-1, "", null);
            //mOnHttpListener.onResult(-1,"");
        }
        return false;
    }

    //get方式从服务器获取内容(JSON)
    public boolean getContentFromServer(String url, String sessionId, Boolean needSession) {

        Log.e("zyf", "get url: " + url);

        HttpGet request = new HttpGet(url);
        if (needSession) {

            request.addHeader("X-Session-Id", sessionId);
        }

        HttpClient mGetHttpClient = new DefaultHttpClient();
        mGetHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, Constant.TIME_OUT);
        mGetHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, Constant.TIME_OUT);

        try {
            HttpResponse response = mGetHttpClient.execute(request); //发起GET请求

            if (response.getStatusLine().getStatusCode() == 200) {

                Log.e("zyf", "get content success...status code: 200");
                Header[] headers = response.getAllHeaders();

                InputStream inputStream = response.getEntity().getContent();

                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                int count = -1;
                while ((count = inputStream.read(data, 0, 1024)) != -1)
                    outStream.write(data, 0, count);
                String datas = new String(outStream.toByteArray());

                Log.e("zyf", "get content success...result: " + datas);

                if (mOnHttpListener != null) {

                    mOnHttpListener.onResult(200, datas, headers);

                }

                return true;

            } else {

                Log.e("zyf", "get content failed...status code:  " + response.getStatusLine().getStatusCode());

                if (mOnHttpListener != null) {

                    Header[] headers = response.getAllHeaders();

                    mOnHttpListener.onResult(response.getStatusLine().getStatusCode(), "", headers);
                    //	mOnHttpListener.onResult(httpResponse.getStatusLine().getStatusCode(),"");

                    return false;
                }
            }
        } catch (Exception e) {

            Log.e("zyf", "get exception: " + e.toString());
            
            if(e instanceof SocketTimeoutException|| e instanceof ConnectTimeoutException || e instanceof HttpHostConnectException){
            	
            	if (mOnHttpListener != null) {

                    mOnHttpListener.onResult(Constant.RESULT_CODE_TIME_OUT, "", null);
                }
                return false;
            }

        } finally {

            if (mGetHttpClient != null) {
                mGetHttpClient.getConnectionManager().shutdown();
                mGetHttpClient = null;
            }
        }

        if (mOnHttpListener != null) {

            mOnHttpListener.onResult(-1, "", null);
        }

        return false;

    }

    //put方式像服务器提交内容(JSON)
    public boolean putContentToServer(String url, String sessionId, String content, boolean needSession) {

        Log.e("zyf", "put url: " + url);

        HttpClient mPostHttpClient;

        StringEntity entity = null;

        mPostHttpClient = null;

        try {
            entity = new StringEntity(content, HTTP.UTF_8);
            entity.setContentType("application/json");
            HttpPut request = new HttpPut(url);
            request.addHeader("Content-Type", "application/json");
            if (needSession) {

                request.addHeader("X-Session-Id", sessionId);
            }
            request.setEntity(entity);

            mPostHttpClient = new DefaultHttpClient();
            mPostHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, Constant.TIME_OUT);
            mPostHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, Constant.TIME_OUT);

            HttpResponse httpResponse = null;

            httpResponse = mPostHttpClient.execute(request);

            Log.e("zyf", "put StatusCode: " + httpResponse.getStatusLine().getStatusCode());

            if (httpResponse.getStatusLine().getStatusCode() == 200) {

                Log.e("zyf", "put content success...");

                InputStream inputStream = httpResponse.getEntity().getContent();

                Header[] headers = httpResponse.getAllHeaders();

                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                int count = -1;
                while ((count = inputStream.read(data, 0, 1024)) != -1)
                    outStream.write(data, 0, count);
                String datas = new String(outStream.toByteArray());

                if (mOnHttpListener != null) {

                    mOnHttpListener.onResult(200, datas, headers);
                    //mOnHttpListener.onResult(200,datas);
                }

                return true;

            } else {

                if (mOnHttpListener != null) {
                    Header[] headers = httpResponse.getAllHeaders();

                    mOnHttpListener.onResult(httpResponse.getStatusLine().getStatusCode(), "", headers);
                    //	mOnHttpListener.onResult(httpResponse.getStatusLine().getStatusCode(),"");

                    return false;
                }
            }

        } catch (Exception e) {
            Log.e("zyf", "put exception: " + e.toString());
            

            if(e instanceof SocketTimeoutException|| e instanceof ConnectTimeoutException || e instanceof HttpHostConnectException){
            	
            	if (mOnHttpListener != null) {

                    mOnHttpListener.onResult(Constant.RESULT_CODE_TIME_OUT, "", null);
                }
                return false;
            }
        } finally {
            if (mPostHttpClient != null) {
                mPostHttpClient.getConnectionManager().shutdown();
                mPostHttpClient = null;

                Log.e("zyf", "put completed,shutdowm the network......");
            }
        }

        if (mOnHttpListener != null) {

            mOnHttpListener.onResult(-1, "", null);
            //mOnHttpListener.onResult(-1,"");
        }
        return false;
    }

    //delete方式向服务器提交内容(JSON)
    public boolean deleteContentFromServer(String url, String sessionId, String content, boolean needSession) {

        Log.e("zyf", "delete url: " + url);

        HttpClient mPostHttpClient;

        //StringEntity entity = null;

        mPostHttpClient = null;

        try {
            /*entity = new StringEntity(content, HTTP.UTF_8);
            entity.setContentType("application/json");*/
            HttpDelete request = new HttpDelete(url);
            request.addHeader("Content-Type", "application/json");
            if (needSession) {
                request.addHeader("X-Session-Id", sessionId);
            }
            //request.setEntity(entity);

            mPostHttpClient = new DefaultHttpClient();
            mPostHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, Constant.TIME_OUT);
            mPostHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, Constant.TIME_OUT);

            HttpResponse httpResponse = null;

            httpResponse = mPostHttpClient.execute(request);

            Log.e("zyf", "delete StatusCode: " + httpResponse.getStatusLine().getStatusCode());

            if (httpResponse.getStatusLine().getStatusCode() == 200) {

                Log.e("zyf", "delete content success...");

                InputStream inputStream = httpResponse.getEntity().getContent();

                Header[] headers = httpResponse.getAllHeaders();

                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                int count = -1;
                while ((count = inputStream.read(data, 0, 1024)) != -1)
                    outStream.write(data, 0, count);
                String datas = new String(outStream.toByteArray());

                if (mOnHttpListener != null) {

                    mOnHttpListener.onResult(200, datas, headers);
                }

                return true;

            } else {

                if (mOnHttpListener != null) {
                    Header[] headers = httpResponse.getAllHeaders();

                    mOnHttpListener.onResult(httpResponse.getStatusLine().getStatusCode(), "", headers);

                    return false;
                }
            }

        } catch (Exception e) {
            Log.e("zyf", "delete exception: " + e.toString());
            

            if(e instanceof SocketTimeoutException|| e instanceof ConnectTimeoutException || e instanceof HttpHostConnectException){
            	
            	if (mOnHttpListener != null) {

                    mOnHttpListener.onResult(Constant.RESULT_CODE_TIME_OUT, "", null);
                }
                return false;
            }
        } finally {
            if (mPostHttpClient != null) {
                mPostHttpClient.getConnectionManager().shutdown();
                mPostHttpClient = null;

                Log.e("zyf", "delete completed,shutdowm the network......");
            }
        }

        if (mOnHttpListener != null) {

            mOnHttpListener.onResult(-1, "", null);
        }
        return false;
    }


}
