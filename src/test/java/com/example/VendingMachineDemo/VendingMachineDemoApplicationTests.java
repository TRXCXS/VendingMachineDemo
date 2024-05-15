package com.example.VendingMachineDemo;

import exception.ExceptionEnum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VendingMachineDemoApplicationTests {

    @Test
    void exceptionTest() {
        System.out.println(
                ExceptionEnum.SENSOR_EXCEPTION
        );
    }

}
