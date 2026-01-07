package camp.nextstep.edu.cipherlab;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationTest {
    @Test
    void sanity_check() {
        assertThat("cipher").startsWith("c");
    }
}
