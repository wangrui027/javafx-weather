package com.wangrui027.javafx.weather.util;

import kong.unirest.GetRequest;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.Builder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * UnirestUtils工具类，封装常用的get和post请求，并支持代理设置
 */
@Builder
public class UnirestUtils {

    /**
     * 默认编码格式
     */
    private static final String DEFAULT_ENCODING = "UTF-8";
    /**
     * 代理是否开启
     */
    private boolean openProxy;

    /**
     * 代理主机
     */
    private String proxyHost;

    /**
     * 代理端口
     */
    private int proxyPort;

    /**
     * URL前缀
     */
    private String urlPrefix;

    /**
     * 默认请求头
     */
    private Map<String, String> defaultHeader;

    /**
     * json请求头，Content-Type: application/json
     */
    public static Map<String, String> header_JSON;

    /**
     * form请求头，Content-Type: application/x-www-form-urlencoded
     */
    public static Map<String, String> header_FORM;

    static {
        header_JSON = new HashMap<String, String>() {{
            put("Content-Type", "application/json");
        }};
        header_FORM = new HashMap<String, String>() {{
            put("Content-Type", "application/x-www-form-urlencoded");
        }};
    }

    /**
     * 设置代理
     *
     * @param host 代理主机
     * @param port 代理端口
     */
    public UnirestUtils proxy(String host, Integer port) {
        proxyHost = host;
        proxyPort = port;
        openProxy = true;
        return this;
    }

    /**
     * 设置代理
     *
     * @param host 代理主机
     * @param port 代理端口
     * @param open 代理是否开启
     */
    public UnirestUtils proxy(String host, Integer port, boolean open) {
        proxyHost = host;
        proxyPort = port;
        openProxy = open;
        return this;
    }

    /**
     * get请求
     *
     * @param url 请求地址
     * @return
     */
    public String get(String url) {
        return get(url, null, defaultHeader);
    }

    /**
     * get请求
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return
     */
    public String get(String url, Map<String, Object> params) {
        return get(url, params, defaultHeader);
    }

    /**
     * get请求
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param headers 请求头
     * @return
     */
    public String get(String url, Map<String, Object> params, Map<String, String> headers) {
        url = processUrl(url);
        GetRequest getRequest = Unirest.get(url);
        if (openProxy) {
            getRequest.proxy(proxyHost, proxyPort);
        }
        if (params != null && params.size() > 0) {
            for (String s : params.keySet()) {
                getRequest.queryString(s, params.get(s));
            }
        }
        if (headers != null && headers.size() > 0) {
            for (String s : headers.keySet()) {
                getRequest.header(s, headers.get(s));
            }
        }
        HttpResponse<String> response = getRequest.asString();
        return response.getBody();
    }

    /**
     * post请求
     *
     * @param url 请求地址
     * @return
     */
    public String post(String url) {
        return post(url, null, defaultHeader);
    }

    /**
     * post请求
     *
     * @param url     请求地址
     * @param payload 请求body
     * @return
     */
    public String post(String url, String payload) {
        return post(url, payload, defaultHeader);
    }

    /**
     * post请求
     *
     * @param url     请求地址
     * @param headers 请求头
     * @return
     */
    public String post(String url, Map<String, String> headers) {
        return post(url, null, headers);
    }

    /**
     * post请求
     *
     * @param url     请求地址
     * @param payload 请求body
     * @param headers 请求头
     * @return
     */
    public String post(String url, String payload, Map<String, String> headers) {
        url = processUrl(url);
        HttpRequestWithBody httpRequestWithBody = Unirest.post(url);
        if (openProxy) {
            httpRequestWithBody.proxy(proxyHost, proxyPort);
        }
        if (headers != null && headers.size() > 0) {
            for (String s : headers.keySet()) {
                httpRequestWithBody.header(s, headers.get(s));
            }
        }
        if (payload == null) {
            return httpRequestWithBody.asString().getBody();
        } else {
            return httpRequestWithBody.body(payload).asString().getBody();
        }
    }

    /**
     * 处理url
     *
     * @param url 原始url，如urlPrefix不为空，则会在url前补上urlPrefix
     * @return
     */
    private String processUrl(String url) {
        if (urlPrefix != null) {
            url = urlPrefix + url;
        }
        return url;
    }

    /**
     * uri编码，默认采用UTF-8编码
     *
     * @param text 编码前的文本
     * @return
     */
    public static String encodeURIComponent(String text) {
        return encodeURIComponent(text, DEFAULT_ENCODING);
    }

    /**
     * uri编码
     *
     * @param text     编码前的文本
     * @param encoding 编码格式
     * @return
     */
    public static String encodeURIComponent(String text, String encoding) {
        try {
            return URLEncoder.encode(text, encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return text;
        }
    }

    /**
     * uri解码，默认采用UTF-8解码
     *
     * @param text 解码前的文本
     * @return
     */
    public static String decodeURIComponent(String text) {
        return decodeURIComponent(text, DEFAULT_ENCODING);
    }

    /**
     * uri解码
     *
     * @param text     解码前的文本
     * @param encoding 编码格式
     * @return
     */
    public static String decodeURIComponent(String text, String encoding) {
        try {
            return URLDecoder.decode(text, encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return text;
        }
    }

    /**
     * 测试main函数
     *
     * @param args
     */
    public static void main(String[] args) {
        UnirestUtils utils = UnirestUtils.builder().proxyHost("localhost").proxyPort(8888).openProxy(true).build();
        System.out.println(utils.get("http://www.baidu.com"));
    }

}