package test;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import template.Calculator;

import java.io.IOException;

public class CalcSumTest {
    @Test
    public void sumOfNumbers() throws IOException {
        Calculator calculator = new Calculator();
        String path = getClass().getResource("/numbers.txt").getPath();
        int sum = calculator.calcSum(path);

        Assertions.assertThat(sum).isEqualTo(10);
    }
}
