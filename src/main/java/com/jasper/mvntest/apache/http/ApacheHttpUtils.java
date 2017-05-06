package com.jasper.mvntest.apache.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * 对应引入的包org.apache.httpcomponents.httpclient
 * 
 * Apache HttpComponents Client
 * 
 * @author zxy
 */
public class ApacheHttpUtils {
    private static final Logger logger = LoggerFactory.getLogger(ApacheHttpUtils.class);

    public static final String HTTP_HEADER_XFF = "X-Forwarded-For";
    public static final String HTTP_HEADER_XRP = "X-Real-IP";
    public static final String CHARSET = "UTF-8";
    private static final String EMPTY = "";
    private static final int SOCKET_TIMEOUT = 3000;
    private static final int CONNECT_TIMEOUT = 3000;
    private static final int CONNECTION_REQUEST_TIMEOUT = 5000;

    private static Map<String, String> headers = new HashMap<>();
//    static {
//        headers.put("User-Agent",
//                "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.1.2)");
//        headers.put("Accept-Language", "zh-cn,zh;q=0.5");
//        headers.put("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
//        headers.put(
//                "Accept",
//                " image/gif, image/x-xbitmap, image/jpeg, "
//                        + "image/pjpeg, application/x-silverlight, application/vnd.ms-excel, "
//                        + "application/vnd.ms-powerpoint, application/msword, application/x-shockwave-flash, */*");
//        headers.put("Content-Type", "application/x-www-form-urlencoded");
//        headers.put("Accept-Encoding", "gzip, deflate");
//    }

    /**
     * 获取客户端真实IP
     *
     * @param request
     * @return
     */
    public static String getRemoteIP(HttpServletRequest request) {
        if (request == null)
            return null;

        logger.debug("[getRemortIP] source remote ip. XFF: {}, Real: {}, remote: {}",
            request.getHeader(HTTP_HEADER_XFF), request.getHeader(HTTP_HEADER_XRP), request.getRemoteAddr());

        // 优先级1：从X-Forwarded-For中取，有被伪造的风险
        String xff = request.getHeader(HTTP_HEADER_XFF);
        if (StringUtils.isNotBlank(xff) && !"unknown".equalsIgnoreCase(xff)) {
            if (xff.indexOf(",") < 0) {
                return xff;
            }
            return StringUtils.substringBefore(xff, ",");
        }

        // 优先级2：从X-Real-IP中取，有多重代理时，这个地址可能不准确，但一般不会被伪造
        String ip = request.getHeader(HTTP_HEADER_XRP);
        if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        // 优先级3：直接获取RemoteAddr，有反向代理时这个地址会不准确
        return request.getRemoteAddr();
    }

    private static RequestConfig createRequestConfig() {
        return RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT)
            .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT).build();
    }

    private static String httpGetOrPost(HttpRequestBase request) {
        return httpGetOrPost(request, null);
    }

    private static String httpGetOrPost(HttpRequestBase request, Map<String, String> headerMap) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;

        HttpEntity entity;
        String result = EMPTY;
        try {
            httpClient = HttpClients.createDefault();

            request.setConfig(createRequestConfig());

            // 设置各种头信息
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
            if (headerMap != null && !headerMap.isEmpty()) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    request.setHeader(entry.getKey(), entry.getValue());
                }
            }

            response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                logger.error("httpGetOrPost ERROR Method failed. request:{} code:{}", request.getURI().toString(), statusCode);
                return result;
            } else {
                entity = response.getEntity();
                if (null != entity) {
                    byte[] bytes = EntityUtils.toByteArray(entity);
                    result = new String(bytes, CHARSET);
                } else {
                    logger.error("httpGetOrPost URL [" + request.getURI().toString() + "],httpEntity is null.");
                }
                return result;
            }
        } catch (Exception e) {
            logger.error("httpGetOrPost URL [" + request.getURI().toString() + "] error, ", e);
            return result;
        } finally {
            IOUtils.closeQuietly(httpClient);
            IOUtils.closeQuietly(response);
        }
    }

    public static String httpGet(String url) {
        return httpGet(url, null);
    }

    /**
     * 异常或者没拿到返回结果的情况下,result为""
     */
    public static String httpGet(String url, Map<String, Object> param) {
        HttpGet httpGet = new HttpGet(url);
        // 传入各种参数
        if (null != param && !param.isEmpty()) {
            URIBuilder uriBuilder = new URIBuilder();
            List<NameValuePair> nvps = new ArrayList<>();
            for (Entry<String, Object> set : param.entrySet()) {
                String key = set.getKey();
                String value = set.getValue() == null ? EMPTY : set.getValue().toString();
                nvps.add(new BasicNameValuePair(key, value));
            }
            uriBuilder.addParameters(nvps);
            uriBuilder.setPath(url);
            try {
                httpGet.setURI(uriBuilder.build());
            } catch (URISyntaxException e) {
                logger.error("httpGet build url error.", e);
                return EMPTY;
            }
        } else {
            httpGet.setURI(URI.create(url));
        }

        return httpGetOrPost(httpGet);
    }

    public static String httpPost(String url) {
        return httpPost(url, EMPTY);
    }

    /**
     * 异常或者没拿到返回结果的情况下,result为""
     */
    public static String httpPost(String url, Map<String, Object> param) {
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<>();
        // 传入各种参数
        if (null != param && !param.isEmpty()) {
            for (Entry<String, Object> set : param.entrySet()) {
                String key = set.getKey();
                String value = set.getValue() == null ? EMPTY : set.getValue().toString();
                nvps.add(new BasicNameValuePair(key, value));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8));
        }

        return httpGetOrPost(httpPost);
    }

    /**
     * 发送post请求
     */
    public static String httpPost(String url, String params, Map<String, String> postHeader) {
        HttpPost post = new HttpPost(url);
        post.setConfig(createRequestConfig());
        if (StringUtils.isNotBlank(params)) {
            StringEntity entity = new StringEntity(params.toString(), CHARSET);
            entity.setContentEncoding(CHARSET);
            entity.setContentType("application/json");// 设置为 json数据
            post.setEntity(entity);
        }

        return httpGetOrPost(post, postHeader);
    }

    public static String httpPost(String url, String params) {
        return httpPost(url, params, null);
    }

}
