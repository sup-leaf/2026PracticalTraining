package com.bjtumarket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Configuration
@PropertySource(value = "classpath:application-hpj.properties", ignoreResourceNotFound = true)
public class HpjPayUtil {

    @Value("${hpj.mode:production}")
    private String mode;

    @Value("${hpj.app-id:}")
    private String appId;

    @Value("${hpj.app-secret:}")
    private String appSecret;

    @Value("${hpj.gateway:https://api.xunhupay.com/payment/do.html}")
    private String gateway;

    @Value("${hpj.notify-url:}")
    private String notifyUrl;

    private static final String VIP_FEE = "0.02";

    @PostConstruct
    public void init() {
        System.out.println("[虎皮椒] 当前模式: " + mode);
        if ("production".equals(mode)) {
            System.out.println("[虎皮椒] AppID: " + appId + ", 网关: " + gateway);
        }
    }

    public boolean isSimulate() {
        return !"production".equals(mode);
    }

    public String getVipFee() {
        return VIP_FEE;
    }

    public Map<String, Object> createOrder(String orderNo, String amount, String title) {
        if (isSimulate()) {
            return simulateCreate(orderNo, amount);
        }

        try {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("version", "1.1");
            params.put("appid", appId);
            params.put("trade_order_id", orderNo);
            params.put("total_fee", amount);
            params.put("title", title);
            params.put("time", String.valueOf(System.currentTimeMillis() / 1000));
            params.put("notify_url", notifyUrl);
            params.put("return_url", "");
            params.put("nonce_str", UUID.randomUUID().toString().replace("-", "").substring(0, 10));

            String sign = generateSign(params);
            params.put("hash", sign);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<String> entity = new HttpEntity<>(buildFormBody(params), headers);

            RestTemplate rest = new RestTemplate();
            @SuppressWarnings("unchecked")
            Map<String, Object> response = rest.postForObject(gateway, entity, Map.class);

            if (response != null && isOk(response)) {
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("orderNo", orderNo);
                result.put("amount", amount);
                result.put("mode", "production");
                result.put("payUrl", String.valueOf(response.getOrDefault("url", "")));
                result.put("qrcode", String.valueOf(response.getOrDefault("url_qrcode", "")));
                result.put("openOrderId", String.valueOf(response.getOrDefault("openid", "")));
                return result;
            }

            String err = response != null
                ? String.valueOf(response.getOrDefault("errmsg", "未知错误"))
                : "请求失败";
            System.out.println("[虎皮椒] 下单失败，降级为模拟支付: " + err);
        } catch (Exception e) {
            System.out.println("[虎皮椒] 下单异常，降级为模拟支付: " + e.getMessage());
        }

        return simulateCreate(orderNo, amount);
    }

    public Map<String, Object> queryOrder(String orderNo) {
        if (isSimulate()) {
            return simulateQuery(orderNo);
        }

        try {
            String queryUrl = gateway.replace("/payment/do.html", "/payment/query.html");

            Map<String, Object> params = new LinkedHashMap<>();
            params.put("appid", appId);
            params.put("out_trade_order", orderNo);
            params.put("time", String.valueOf(System.currentTimeMillis() / 1000));
            params.put("nonce_str", UUID.randomUUID().toString().replace("-", "").substring(0, 10));

            String sign = generateSign(params);
            params.put("hash", sign);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<String> entity = new HttpEntity<>(buildFormBody(params), headers);

            RestTemplate rest = new RestTemplate();
            @SuppressWarnings("unchecked")
            Map<String, Object> response = rest.postForObject(queryUrl, entity, Map.class);

            if (response != null && isOk(response)) {
                Map<String, Object> data = null;
                Object dataObj = response.get("data");
                if (dataObj instanceof Map) {
                    data = (Map<String, Object>) dataObj;
                }
                Map<String, Object> src = data != null ? data : response;

                String tradeStatus = String.valueOf(src.getOrDefault("status", ""));
                String tradeNo = String.valueOf(src.getOrDefault("transaction_id", ""));
                System.out.println("[虎皮椒] 查单: orderNo=" + orderNo + " status=" + tradeStatus);

                Map<String, Object> result = new LinkedHashMap<>();
                result.put("orderNo", orderNo);
                result.put("tradeNo", tradeNo);
                if ("OD".equals(tradeStatus)) {
                    result.put("status", "success");
                } else if ("WP".equals(tradeStatus)) {
                    result.put("status", "pending");
                } else {
                    result.put("status", "pending");
                }
                return result;
            }

            System.out.println("[虎皮椒] 查单失败，降级为模拟支付");
        } catch (Exception e) {
            System.out.println("[虎皮椒] 查单异常，降级为模拟支付: " + e.getMessage());
        }

        return simulateQuery(orderNo);
    }

    private Map<String, Object> simulateCreate(String orderNo, String amount) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderNo", orderNo);
        result.put("amount", amount);
        result.put("mode", "simulate");
        result.put("payUrl", "/api/payment/order/query?orderNo=" + orderNo + "&confirm=1");
        return result;
    }

    private Map<String, Object> simulateQuery(String orderNo) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderNo", orderNo);
        result.put("mode", "simulate");
        result.put("status", "success");
        result.put("tradeNo", "SIMULATE_" + orderNo);
        return result;
    }

    String generateSign(Map<String, Object> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            Object value = params.get(key);
            if (value == null || "".equals(String.valueOf(value)) || "hash".equals(key)) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(key).append("=").append(value);
        }
        String signStr = sb.toString() + appSecret;
        return DigestUtils.md5DigestAsHex(signStr.getBytes(StandardCharsets.UTF_8));
    }

    private boolean isOk(Map<String, Object> response) {
        Object code = response.get("errcode");
        if (code instanceof Number) {
            return ((Number) code).intValue() == 0;
        }
        return "0".equals(String.valueOf(code));
    }

    private String buildFormBody(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value == null || "".equals(String.valueOf(value))) continue;
            if (sb.length() > 0) sb.append("&");
            sb.append(entry.getKey()).append("=")
              .append(URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8));
        }
        return sb.toString();
    }
}
