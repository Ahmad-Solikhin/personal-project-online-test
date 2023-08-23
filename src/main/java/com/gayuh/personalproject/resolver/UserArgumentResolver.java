package com.gayuh.personalproject.resolver;

import com.gayuh.personalproject.dto.UserDetails;
import com.gayuh.personalproject.dto.UserObject;
import com.gayuh.personalproject.enumerated.Role;
import com.gayuh.personalproject.util.ExtractHeaderUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Value("${SIGNATURE_KEY}")
    private String secretKey;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return UserObject.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {
        UserDetails details = ExtractHeaderUtil.extractHeader((HttpServletRequest) webRequest.getNativeRequest(), secretKey);

        return new UserObject(details.id(), details.email(), Role.getValueOf(details.role()));
    }
}
