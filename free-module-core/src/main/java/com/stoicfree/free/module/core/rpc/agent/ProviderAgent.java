package com.stoicfree.free.module.core.rpc.agent;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.AnnotationConfigurationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.stoicfree.free.module.core.common.support.AnnotatedBeanContainer;
import com.stoicfree.free.module.core.common.support.TwoTuple;
import com.stoicfree.free.module.core.rpc.annotation.ProviderClass;
import com.stoicfree.free.module.core.rpc.annotation.ProviderMethod;
import com.stoicfree.free.module.core.rpc.config.ProviderProperties;
import com.stoicfree.free.module.core.rpc.domain.ProxyRequest;
import com.stoicfree.free.module.core.rpc.domain.ProxyResponse;
import com.stoicfree.free.module.core.rpc.exception.ProviderException;
import com.stoicfree.free.module.core.rpc.safe.SecureKeeper;

/**
 * @author zengzhifei
 * @date 2023/2/26 00:15
 */
@RestController
public class ProviderAgent extends AnnotatedBeanContainer {
    private final Map<String, TwoTuple<Object, Method>> providers = new HashMap<>();
    private final ProviderProperties providerProperties;

    public ProviderAgent(ProviderProperties providerProperties) {
        super(ProviderClass.class, ProviderMethod.class);

        this.providerProperties = providerProperties;
    }

    @PostMapping("/proxy")
    public ProxyResponse proxy(@RequestBody ProxyRequest proxyRequest, HttpServletRequest request) throws Exception {
        try {
            verify(request);

            String providerId = proxyRequest.getProviderId();
            Object[] params = proxyRequest.getParams();

            TwoTuple<Object, Method> provider = providers.get(providerId);
            if (provider == null) {
                throw new ProviderException(String.format("%s is not register", providerId));
            }

            Object bean = provider.getFirst();
            Method method = provider.getSecond();
            method.setAccessible(true);

            Object response = method.invoke(bean, params);
            return ProxyResponse.success(response);
        } catch (ProviderException e) {
            return ProxyResponse.fail(e.getMessage());
        }
    }

    @Override
    protected void afterInitAnnotatedBeanContainer() {
        Map<Object, List<Method>> beanMethods = getAnnotatedBeanMethods();
        for (Map.Entry<Object, List<Method>> entry : beanMethods.entrySet()) {
            Object bean = entry.getKey();
            List<Method> methods = entry.getValue();
            for (Method method : methods) {
                ProviderMethod providerMethod = method.getDeclaredAnnotation(ProviderMethod.class);
                String id = providerMethod.id();
                if (StringUtils.isBlank(id)) {
                    throw new AnnotationConfigurationException(String.format("%s %s ProviderMethod id not be blank",
                            bean.getClass().getTypeName(), method.getName()));
                }
                if (providers.containsKey(id)) {
                    throw new AnnotationConfigurationException(String.format("%s %s ProviderMethod id not be repeat",
                            bean.getClass().getTypeName(), method.getName()));
                }
                providers.put(id, TwoTuple.of(bean, method));
            }
        }
    }

    private void verify(HttpServletRequest request) {
        String proxyToken = request.getHeader("rpc-proxy-token");
        if (StringUtils.isBlank(proxyToken)) {
            throw new ProviderException("rpc proxy token not be blank");
        }

        String allowProductIds = providerProperties.getAllowProductIds();
        Set<String> productIds = Arrays.stream(allowProductIds.split(",")).collect(Collectors.toSet());
        String productId = SecureKeeper.getProductId(proxyToken);

        if (!productIds.contains(productId)) {
            throw new ProviderException(String.format("product %s not be allow", productId));
        }
    }
}
