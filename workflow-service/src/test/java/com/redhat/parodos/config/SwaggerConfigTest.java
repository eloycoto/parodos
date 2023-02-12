package com.redhat.parodos.config;

import org.junit.Test;
// import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

public class SwaggerConfigTest {
    @Test
    public void test() {
        SwaggerConfig c = new SwaggerConfig();
        boolean res = c.Eloy();
        assertThat(res).isEqualTo(true);
    }
}
