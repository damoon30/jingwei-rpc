package com.jingwei.rpc.core.util;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultServiceUnavailableRetryStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HttpClientUtil class
 *
 * @author zhanghaoran25
 * @date 2022/2/11 3:18 下午
 */
public class HttpUtils {

    private static final String CHARSET_UTF8 = "UTF-8";

    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    // 自定义响应处理
    // 使用ResponseHandler接口处理响应，HttpClient使用ResponseHandler会自动管理连接的释放，解决了对连接的释放管理
    private static ResponseHandler<String> responseHandler = response -> {

        int code = response.getStatusLine().getStatusCode();

        // 如果不是200，则返回状态码
        if (code < 200 || code >= 500) {
            return String.valueOf(code);
        }
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            Charset charset = ContentType.getOrDefault(entity).getCharset();
            if (null != charset) {
                return new String(EntityUtils.toByteArray(entity), charset);
            } else {
                return new String(EntityUtils.toByteArray(entity));
            }
        }
        return null;
    };

    private HttpUtils() {
    }

    public static HttpClientConnectionManager getConnectionManager() {

        LayeredConnectionSocketFactory sslSocketFactory;

        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();

        PlainConnectionSocketFactory plainSocketFactory = PlainConnectionSocketFactory.getSocketFactory();
        registryBuilder.register("http", plainSocketFactory);
        try {
            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial((chain, authType) -> true).build();
            HostnameVerifier allowAllHostnameVerifier = NoopHostnameVerifier.INSTANCE;
            sslSocketFactory = new SSLConnectionSocketFactory(sslcontext, allowAllHostnameVerifier);
            registryBuilder.register("https", sslSocketFactory);

        } catch (Throwable e) {

            log.error("https ssl init failed", e);
        }

        Registry<ConnectionSocketFactory> r = registryBuilder.build();

        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(r);
        // 连接池最大并发连接数
        connManager.setMaxTotal(100);
        // 单路由最大并发数
        connManager.setDefaultMaxPerRoute(100);
        return connManager;
    }

    /**
     * 获取HttpClient实例
     *
     * @param charset
     *            参数编码集, 可空
     * @return HttpClient 对象
     */
    private static CloseableHttpClient getHttpClient(final String charset) {
        return getHttpClientBuilder(charset, null, 0).build();

    }

    private static HttpClientBuilder getHttpClientBuilder(final String charset, String proxyIp, int proxyPort) {

        HttpClientBuilder builder = HttpClients.custom();

        Charset cs = charset == null ? Charset.forName(CHARSET_UTF8) : Charset.forName(charset);
        ConnectionConfig.Builder connBuilder = ConnectionConfig.custom().setCharset(cs);

        RequestConfig.Builder reqBuilder = RequestConfig.custom();
        reqBuilder.setExpectContinueEnabled(false);
        reqBuilder.setSocketTimeout(5000);
        reqBuilder.setConnectTimeout(5000);
        reqBuilder.setConnectionRequestTimeout(5000);
        reqBuilder.setMaxRedirects(10);

        if (ToolUtils.isNotEmpty(proxyIp) && proxyPort > 0) {
            log.info("using proxy {}:{} to request ", proxyIp, String.valueOf(proxyPort));
            HttpHost proxy = new HttpHost(proxyIp, proxyPort);
            reqBuilder.setProxy(proxy);
        }

        ServiceUnavailableRetryStrategy serviceUnavailableRetryStrategy = new DefaultServiceUnavailableRetryStrategy(3,
                3000);
        builder.setServiceUnavailableRetryStrategy(serviceUnavailableRetryStrategy);
        // 模拟浏览器，解决一些服务器程序只允许浏览器访问的问题
        builder.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
        builder.setDefaultRequestConfig(reqBuilder.build());
        builder.setDefaultConnectionConfig(connBuilder.build());
        builder.setConnectionManager(getConnectionManager());

        return builder;

    }
    public static String post(URI url, String params) {
        return post(url, params, HttpUtils.CHARSET_UTF8, getHttpClient(HttpUtils.CHARSET_UTF8),false, new HashMap<>());
    }
    /**
     * 重载post方法，可配置是否带Content-Encoding的Header
     * 适用于请求Node服务（Node的后端框架不支持Content-Encoding请求头）
     * @param url 目标url
     * @param params 请求参数
     * @param charset 字符集
     * @param client 请求client
     * @param withoutCharsetHeader 是否去掉Content-Encoding
     * @return 返回
     */
    public static String post(
            URI url, String params, String charset, CloseableHttpClient client, Boolean withoutCharsetHeader, Map<String, String> headerMap) {

        StringEntity entity = null;
        try {
            if (charset == null || ToolUtils.isEmpty(charset)) {
                entity = new StringEntity(params);
            } else {
                entity = new StringEntity(params, charset);
            }
        } catch (Exception e) {
            throw new RuntimeException("不支持的编码集", e);
        }
        if (!withoutCharsetHeader) {
            entity.setContentEncoding("UTF-8");
        }
        entity.setContentType("application/json;charset=UTF-8");
        HttpPost hp = new HttpPost(url);

        if (ToolUtils.isNotEmpty(headerMap)) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                hp.setHeader(entry.getKey(), entry.getValue());
            }
        }

        hp.setEntity(entity);

        // 发送请求，得到响应
        String respString;
        respString = execute(client, hp);

        return respString;
    }
    /**
     * JSON参数请求，UTF8编码
     *
     * @param url
     * @param params
     * @return
     */
    public static String post(String url, String params) {
        return post(url, params, HttpUtils.CHARSET_UTF8, getHttpClient(HttpUtils.CHARSET_UTF8));
    }

    public static String post(String url, String params, String charset, CloseableHttpClient client) {
        return post(url, params, charset, client, false, null);
    }

    public static String post(String url, String params, Map<String, String> headerMap) {
        return post(url, params, HttpUtils.CHARSET_UTF8, getHttpClient(HttpUtils.CHARSET_UTF8), false, headerMap);
    }


    /**
     * 重载post方法，可配置是否带Content-Encoding的Header
     * 适用于请求Node服务（Node的后端框架不支持Content-Encoding请求头）
     * @param url 目标url
     * @param params 请求参数
     * @param charset 字符集
     * @param client 请求client
     * @param withoutCharsetHeader 是否去掉Content-Encoding
     * @return 返回
     */
    private static String post(
            String url, String params, String charset, CloseableHttpClient client, Boolean withoutCharsetHeader, Map<String, String> headerMap) {


        if (ToolUtils.isEmpty(url) || params == null) {
            return null;
        }
        StringEntity entity = null;
        try {
            if (charset == null || ToolUtils.isEmpty(charset)) {
                entity = new StringEntity(params);
            } else {
                entity = new StringEntity(params, charset);
            }
        } catch (Exception e) {
            throw new RuntimeException("不支持的编码集", e);
        }

        if (!withoutCharsetHeader) {
            entity.setContentEncoding("UTF-8");
        }
        entity.setContentType("application/json;charset=UTF-8");
        if (ToolUtils.isEmpty(url)) {
            return null;
        }
        HttpPost hp = new HttpPost(url);
        if (ToolUtils.isNotEmpty(headerMap)) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                hp.setHeader(entry.getKey(), entry.getValue());
            }
        }
        hp.setEntity(entity);

        // 发送请求，得到响应
        String respString;
        respString = execute(client, hp);

        return respString;
    }


    /**
     * Get,URL中包含查询参数
     *
     * @param url  URL
     * @return     响应消息
     */
    public static String get(String url) {
        return get(url, null, CHARSET_UTF8);
    }

    /**
     * Get,URL中不包含查询参数
     *
     * @param url      URL
     * @param params   查询参数集, 键/值对
     * @return         响应消息
     */
    public static String get(String url, Map<String, String> params) {
        return get(url, params, CHARSET_UTF8);
    }

    /**
     * Get,URL中不包含查询参数
     *
     * @param url      URL
     * @param params   查询参数集, 键/值对
     * @param charset  参数提交编码集
     * @return 响应消息
     */
    public static String get(String url, Map<String, String> params, String charset) {
        return get(url, params, charset, getHttpClient(charset));
    }

    /**
     * Get,指定url参数和字符集和自定义的httpclient
     */
    public static String get(String url, Map<String, String> params, String charset, CloseableHttpClient client) {
        log.info("[GET] {} with:{}", url, JSON.toJSONString(params));
        if (url == null || ToolUtils.isEmpty(url)) {
            return null;
        }
        List<NameValuePair> queryParams = getParamsList(params);
        if (queryParams != null && queryParams.size() > 0) {
            charset = (charset == null ? CHARSET_UTF8 : charset);
            String formatParams = URLEncodedUtils.format(queryParams, charset);
            url = (url.indexOf("?")) < 0 ? (url + "?" + formatParams) : (url.substring(0, url.indexOf("?") + 1) + formatParams);
        }
        HttpGet hg = new HttpGet(url);
        String respString;
        respString = execute(client, hg);
        log.info("[GET] {} response :{}", url, JSON.toJSONString(respString));
        return respString;
    }

    /**
     * 将传入的键/值对参数转换为NameValuePair参数集
     *
     * @param paramsMap   参数集, 键/值对
     * @return NameValuePair参数集
     */
    public static List<NameValuePair> getParamsList(Map<String, String> paramsMap) {
        if (paramsMap == null || paramsMap.size() == 0) {
            return null;
        }
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> map : paramsMap.entrySet()) {
            params.add(new BasicNameValuePair(map.getKey(), map.getValue()));
        }
        return params;
    }

    /**
     * 释放HttpClient连接
     *
     * @param hrb
     *            请求对象
     * @param httpclient
     *            对象
     */
    public static void abortConnection(final HttpUriRequest hrb, final CloseableHttpClient httpclient) {
        if (hrb != null) {
            hrb.abort();
        }
        if (httpclient != null) {
            httpclient.getConnectionManager().shutdown();
            try {
                log.debug("closing httpclient ...");
                httpclient.close();
            } catch (IOException e) {
                log.error("failed to close httpclient", e);
            }

        }
    }

    private static String execute(CloseableHttpClient client, HttpUriRequest req) {
        String respString;
        try {
            respString = client.execute(req, responseHandler);
        } catch (ClientProtocolException e) {
            throw new RuntimeException("客户端连接协议错误", e);
        } catch (IOException e) {
            throw new RuntimeException("IO操作异常", e);
        } finally {
            abortConnection(req, client);
        }
        return respString;
    }
}