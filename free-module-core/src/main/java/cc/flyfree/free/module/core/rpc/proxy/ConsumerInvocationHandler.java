package cc.flyfree.free.module.core.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.AnnotationConfigurationException;

import cc.flyfree.free.module.core.common.gson.GsonUtil;
import cc.flyfree.free.module.core.rpc.annotation.ConsumerMethod;
import cc.flyfree.free.module.core.rpc.config.ConsumerProperties;
import cc.flyfree.free.module.core.rpc.domain.ProxyRequest;
import cc.flyfree.free.module.core.rpc.domain.ProxyResponse;
import cc.flyfree.free.module.core.rpc.exception.ConsumerException;
import cc.flyfree.free.module.core.rpc.safe.SecureKeeper;

import cn.hutool.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2023/2/26 16:21
 */
@Slf4j
public class ConsumerInvocationHandler implements InvocationHandler {
    private final ConsumerProxy consumer;
    private final ConsumerProperties consumerProperties;

    public ConsumerInvocationHandler(ConsumerProxy consumer) {
        this.consumer = consumer;
        this.consumerProperties = consumer.getConsumerProperties();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ConsumerMethod consumerMethod = method.getDeclaredAnnotation(ConsumerMethod.class);
        if (consumerMethod == null) {
            throw new ConsumerException(String.format("%s not annotated ConsumerMethod", method.getName()));
        }

        String providerId = consumerMethod.providerId();
        if (StringUtils.isBlank(providerId)) {
            throw new AnnotationConfigurationException(String.format("%s ConsumerMethod providerId not be blank",
                    method.getName()));
        }

        String url = consumer.getHost() + "/rpc/proxy";
        ProxyRequest proxyParam = new ProxyRequest();
        proxyParam.setProviderId(providerId);
        proxyParam.setParams(args);

        Map<String, String> headers = new HashMap<>(1);
        headers.put("rpc-proxy-token", SecureKeeper.getProxyToken(consumerProperties.getProductId()));

        try {
            String response = post(url, headers, GsonUtil.toJson(proxyParam));
            ProxyResponse proxyResponse = GsonUtil.fromJson(response, ProxyResponse.class);
            if (!proxyResponse.isSuccess()) {
                throw new ConsumerException(proxyResponse.getMessage());
            }
            return proxyResponse.getResponse();
        } catch (ConsumerException e) {
            throw e;
        } catch (Exception e) {
            throw new ConsumerException("provider is not connected");
        }
    }

    private String post(String url, Map<String, String> headers, String body) {
        return HttpRequest.post(url).headerMap(headers, true)
                .setConnectionTimeout(consumerProperties.getConnectionTimeout())
                .setReadTimeout(consumerProperties.getReadTimeout())
                .body(body).execute()
                .body();
    }
}
