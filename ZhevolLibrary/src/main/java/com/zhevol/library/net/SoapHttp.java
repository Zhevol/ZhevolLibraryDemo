package com.zhevol.library.net;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.blankj.ALog;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * SoapHttp 封装类
 *
 * @author Zhevol
 * @date 2018/3/23 0023
 */
public class SoapHttp {

    /**
     * SoapHttpHolder
     */
    private static class SoapHttpHolder {
        /**
         * 单例
         */
        final static SoapHttp INSTANCE = new SoapHttp();
    }

    /**
     * 获取 SoapHttp 单例
     *
     * @return SoapHttp 单例
     */
    static SoapHttp getInstance() {
        return SoapHttpHolder.INSTANCE;
    }

    /**
     * Soap 命名空间
     */
    private String nameSpace;

    /**
     * 请求的 URL
     */
    private String requestUrl;

    /**
     * 请求的方法名
     */
    private String methodName;

    /**
     * 请求的参数集合
     */
    private Map<String, String> param;

    /**
     * 超时时间
     */
    private final int TIME_OUT = 100000;

    /**
     * 网络请求的回调
     */
    private OnSoapHttpListener mOnSoapHttpListener;

    /**
     * Handler 的子类对象，用于进行异步请求时的消息的传递和发送
     */
    private SoapHandler mSoapHandler;

    /**
     * 构造器
     */
    private SoapHttp() {
        mSoapHandler = new SoapHandler();
    }

    /**
     * 设置命名空间
     *
     * @param nameSpace 命名空间
     * @return 单例
     */
    SoapHttp setNameSpace(String nameSpace) {
        SoapHttpHolder.INSTANCE.nameSpace = nameSpace;
        return SoapHttpHolder.INSTANCE;
    }

    /**
     * 设置请求的 URL
     *
     * @param requestUrl 请求的 URL
     * @return 单例
     */
    SoapHttp setRequestUrl(String requestUrl) {
        SoapHttpHolder.INSTANCE.requestUrl = requestUrl;
        return SoapHttpHolder.INSTANCE;
    }

    /**
     * 设置请求的方法名称
     *
     * @param methodName 请求的方法名称
     * @return 单例
     */
    SoapHttp setMethodName(String methodName) {
        SoapHttpHolder.INSTANCE.methodName = methodName;
        return SoapHttpHolder.INSTANCE;
    }

    /**
     * 设置请求的参数
     *
     * @param param 请求的参数
     * @return 单例
     */
    SoapHttp setParam(Map<String, String> param) {
        SoapHttpHolder.INSTANCE.param = param;
        return SoapHttpHolder.INSTANCE;
    }

    /**
     * 设置网络请求的回调监听
     *
     * @param onSoapHttpListener 回调监听
     * @return 单例
     */
    SoapHttp setOnSoapHttpListener(OnSoapHttpListener onSoapHttpListener) {
        SoapHttpHolder.INSTANCE.mOnSoapHttpListener = onSoapHttpListener;
        return SoapHttpHolder.INSTANCE;
    }

    /**
     * 获取监听器对象
     *
     * @return 监听器
     */
    OnSoapHttpListener getOnSoapHttpListener() {
        return mOnSoapHttpListener;
    }

    /**
     * 发送网络请求
     */
    void doSoapHttpRequest() {
        ThreadFactory mThreadFactory = new ThreadFactoryBuilder().setNameFormat("networkThread").build();
        ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024), mThreadFactory,
                new ThreadPoolExecutor.AbortPolicy());
        singleThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                // 命名空间 + 方法名称
                final String soapAction = nameSpace + methodName;
                // 创建请求
                SoapObject request = new SoapObject(nameSpace, methodName);
                // 生成调用web service 方法的soap请求消息
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                // 设置.net web service
                envelope.dotNet = true;
                // 请求參数
                if (param != null && !param.isEmpty()) {
                    for (Map.Entry<String, String> entry : param.entrySet()) {
                        request.addProperty(entry.getKey(), entry.getValue());
                    }
                }
                // 发送请求
                envelope.setOutputSoapObject(request);
                HttpTransportSE transport = new HttpTransportSE(requestUrl);
                SoapObject result = null;
                Exception exception = null;
                Bundle bundle = new Bundle();
                try {
                    // web service请求
                    transport.call(soapAction, envelope);
                    result = (SoapObject) envelope.bodyIn;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    exception = ex;
                } finally {
                    if (result != null) {
                        ALog.e("PropertyCount\n" + result.getPropertyCount());
                        ALog.e("Property(0)\n" + result.getProperty(0));
                        String resultS = null;
                        if (result.getPropertyCount() > 0) {
                            for (int i = 0; i < result.getPropertyCount(); i++) {
                                resultS = result.getProperty(i).toString();
                            }
                            bundle.putString("result", resultS);
                        } else {
                            resultS = "";
                            bundle.putString("result", resultS);
                        }
                    } else {
                        bundle.putSerializable("exception", exception == null ? new NullPointerException("返回数据为空") : exception);
                    }
                    Message msg = mSoapHandler.obtainMessage();
                    msg.setData(bundle);
                    mSoapHandler.sendMessage(msg);
                }
            }
        });

        try {
            if (singleThreadPool.awaitTermination(TIME_OUT, TimeUnit.MILLISECONDS)) {
                singleThreadPool.shutdown();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            singleThreadPool.shutdown();
        }

    }


    /**
     * 去除初始化 Handler 时的警告，使用这样的方式
     */
    private static final class SoapHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String result = bundle.getString("result", "");
            Exception exception = (Exception) bundle.getSerializable("exception");
            OnSoapHttpListener onSoapHttpListener = getInstance().getOnSoapHttpListener();
            if (onSoapHttpListener != null) {
                if (TextUtils.isEmpty(result)) {
                    onSoapHttpListener.onFailed(exception == null ? new NullPointerException("返回数据为空") : exception);
                } else {
                    onSoapHttpListener.onSuccess(result);
                }
            }
        }
    }

    /**
     * 网络请求回调
     */
    public interface OnSoapHttpListener {
        /**
         * 请求成功
         *
         * @param result 成功后返回的内容
         */
        void onSuccess(String result);

        /**
         * 请求失败
         *
         * @param e 失败后返回异常
         */
        void onFailed(Exception e);
    }


//    // 使用dom4j解析xml
////    Document doc = DocumentHelper.parseText(xml);
//    public void readStringXml(String xml) {
//        Document doc = null;
//        try {
//            // 读取并解析XML文档，SAXReader就是一个管道，用一个流的方式，把xml文件读出来
//            SAXReader reader = new SAXReader();
//            new ForFile().createFile("axml.xml", xml);
//            doc = reader.read(new File("/storage/emulated/0/PT123/axml.xml"));
//            Element rootElt = doc.getRootElement(); // 获取根节点
//            Log.d(TAG, "根节点的名称: " + rootElt.getName());
//            String isSuccessed = rootElt.element("IsSuccessed").getText();
//            String errorMsg = rootElt.element("ErrorMsg").getText();
//            String ipdNo = rootElt.element("IpdNo").getText();
//            String patientName = rootElt.element("PatientName").getText();
//            String station = rootElt.element("Station").getText();
//            String ctzDesc = rootElt.element("CtzDesc").getText();
//            String bed = rootElt.element("Bed").getText();
//            String price = rootElt.element("Price").getText();
//            String deptCode = rootElt.element("DeptCode").getText();
//            String totalPrice = rootElt.element("TotalPrice").getText();
//            strArr = new String[]{isSuccessed, errorMsg, ipdNo, patientName, station, ctzDesc, bed, price, deptCode, totalPrice};
////            list.add(isSuccessed);
////            list.add(errorMsg);
////            list.add(ipdNo);
////            list.add(patientName);
////            list.add(station);
////            list.add(ctzDesc);
////            list.add(bed);
////            list.add(price);
////            list.add(deptCode);
////            list.add(totalPrice);
//            //取得某节点的单个子节点
//            Element resultsElm = rootElt.element("Results");// "Results"是节点名
//            Iterator<Element> iter = resultsElm.elementIterator("Result"); // 获取根节点下的子节点head
//            // 遍历head节点
////            List<Listings> list = new ArrayList<Listings>();
//            while (iter.hasNext()) {
//                Element element = (Element) iter.next();
//                String bilDate = element.elementTextTrim("BilDate"); // 拿到head节点下的子节点title值
//                String feeTypeDesc = element.elementTextTrim("FeeTypeDesc");
//                String orderDesc = element.elementTextTrim("OrderDesc");
//                String unitDesc = element.elementTextTrim("UnitDesc");
//                String ownPrice = element.elementTextTrim("OwnPrice");
//                String dosageQty = element.elementTextTrim("DosageQty");
//                String totAmt = element.elementTextTrim("TotAmt");
//                String execDept = element.elementTextTrim("ExecDept");
//                String execDate = element.elementTextTrim("ExecDate");
//                Log.d(TAG, "readStringXml: " + bilDate);
//                Listings listings = new Listings(bilDate, feeTypeDesc, orderDesc, unitDesc, ownPrice, dosageQty, totAmt, execDept, execDate);
//                dailyList.add(listings);
//            }
//
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
