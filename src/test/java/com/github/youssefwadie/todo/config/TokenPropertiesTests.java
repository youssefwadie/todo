package com.github.youssefwadie.todo.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.yml")
@EnableConfigurationProperties(TokenProperties.class)
public class TokenPropertiesTests {
    @Autowired
    TokenProperties tokenProperties;

    @Test
    void testPropertiesRead() throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        for (PropertyDescriptor descriptor : Introspector.getBeanInfo(TokenProperties.class).getPropertyDescriptors()) {
            System.out.println(descriptor.getReadMethod().invoke(tokenProperties));
        }
    }

    /***
     * The following tests were tests against values than the default values defined in the class itself
     */
    @Test
    void testAuthenticationSchemeIsSet() {
        assertThat(tokenProperties.getAuthenticationScheme()).isEqualTo("Bearer");
    }

    @Test
    void testAccessTokenLifeTimeIsSet() {
        assertThat(tokenProperties.getAccessTokenLifeTime()).isEqualTo(900000);
    }

    @Test
    void testRefreshTokenLifeTimeIsSet() {
        assertThat(tokenProperties.getRefreshTokenLifeTime()).isEqualTo(2592000000L);
    }

    @Test
    void testJwtRefreshTokenCookieNameIsSet() {
        assertThat(tokenProperties.getRefreshTokenCookieName()).isEqualTo("refresh-token");
    }

    @Test
    void testJwtKeyIsSet() {
        assertThat(tokenProperties.getKey()).isEqualTo("jAOa=kms)dLxgE#*($#Qe_XH!uPq8Vdby@YlnvkANox^u3dQ53YU%n4B");
    }

    @Test
    void testJwtHeaderNameIsSet() {
        assertThat(tokenProperties.getAccessTokenHeaderNameSentByClient()).isEqualTo("Authorization");
    }
}
