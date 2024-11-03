package test.container;

import org.junit.jupiter.api.function.Executable;

public record JdbcTest(String name,
                       Executable executable) {
}
