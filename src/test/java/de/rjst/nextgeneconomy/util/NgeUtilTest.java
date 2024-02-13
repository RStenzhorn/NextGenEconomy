package de.rjst.nextgeneconomy.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class NgeUtilTest {

    @Test
    void getArgsLength() {
        String command = "/pay Player test";
        int argsLength = NgeUtil.getArgsLength(command);
        log.info("Command length: {}", argsLength);

    }
}
