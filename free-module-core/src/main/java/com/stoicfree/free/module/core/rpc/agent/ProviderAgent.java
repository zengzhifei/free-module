package com.stoicfree.free.module.core.rpc.agent;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.stoicfree.free.module.core.rpc.config.ProviderProperties;
import com.stoicfree.free.module.core.rpc.domain.ProxyRequest;
import com.stoicfree.free.module.core.rpc.domain.ProxyResponse;
import com.stoicfree.free.module.core.rpc.exception.ProviderException;
import com.stoicfree.free.module.core.rpc.safe.SecureKeeper;

import javafx.util.Pair;

/**
 * @author zengzhifei
 * @date 2023/2/26 00:15
 */
@RestController
public class ProviderAgent {
    private final ProviderProperties providerProperties;

    @Autowired
    private ProviderContainer providerContainer;

    public ProviderAgent(ProviderProperties providerProperties) {
        this.providerProperties = providerProperties;
    }

    @PostMapping("/proxy")
    public ProxyResponse proxy(@RequestBody ProxyRequest proxyRequest, HttpServletRequest request) throws Exception {
        try {
            verify(request);

            String providerId = proxyRequest.getProviderId();
            Object[] params = proxyRequest.getParams();

            Pair<Object, Method> provider = providerContainer.getProvider(providerId);
            if (provider == null) {
                throw new ProviderException(String.format("%s is not register", providerId));
            }

            Object bean = provider.getKey();
            Method method = provider.getValue();
            method.setAccessible(true);

            Object response = method.invoke(bean, params);
            return ProxyResponse.success(response);
        } catch (ProviderException e) {
            return ProxyResponse.fail(e.getMessage());
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
