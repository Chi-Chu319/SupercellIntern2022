package fi.intern.supercell.test;

import fi.intern.supercell.UserGraphProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Ex1Test {

    UserGraphProcessor userGraphProcessor;

    @BeforeEach
    void setUp() {
        this.userGraphProcessor = new UserGraphProcessor();
    }

    @Test
    @DisplayName("test input1")
    void testInput1() {
        // TODO impl
        Assertions.assertEquals(1, 1);
    }

}