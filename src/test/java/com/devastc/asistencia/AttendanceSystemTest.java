package com.devastc.asistencia;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class AttendanceSystemTest {

    @Test
    public void contextLoads() {
        assertTrue(true, "Application context should load");
    }
}
