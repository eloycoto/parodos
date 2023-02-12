package com.redhat.parodos.config;

import org.junit.Test;
// import org.mockito.Mockito;

// import com.redhat.parodos.workflows.work.Work;
// import com.redhat.parodos.workflows.work.WorkContext;
// import com.redhat.parodos.workflows.work.WorkReportPredicate;
import static org.assertj.core.api.Assertions.assertThat;

public class SwaggerConfigTest {
    @Test
    public void test() {
        SwaggerConfig c = new SwaggerConfig();
        boolean res = c.Eloy();
        assertThat(res).isEqualTo(true);
    }
}
