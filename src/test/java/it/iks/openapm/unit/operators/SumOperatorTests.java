package it.iks.openapm.unit.operators;

import it.iks.openapm.operators.SumOperator;
import it.iks.openapm.operators.Operator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SumOperatorTests extends OperatorTestCase {

    private Map<String, Object> multiItem(Object a, Object b) {
        Map<String, Object> item = new HashMap<>();
        item.put("a", a);
        item.put("b", b);
        return item;
    }

    @Test
    public void it_must_have_one_operand() {
        Operator noOperands = new SumOperator(new String[]{}, templator);
        assertThat(noOperands.valid()).isFalse();

        Operator oneOperand = new SumOperator(new String[]{"a"}, templator);
        assertThat(oneOperand.valid()).isTrue();

        Operator twoOperands = new SumOperator(new String[]{"a", "b"}, templator);
        assertThat(twoOperands.valid()).isTrue();
    }

    @Test
    public void it_calculates_correctly_one_attribute() {
        Operator operator = new SumOperator(new String[]{"attribute"}, templator);

        assertThat(operator.calculate(Arrays.asList(
                item("attribute", "ignored"),
                item("attribute", 1),
                item("attribute", 1.2),
                item("attribute", 1.2F),
                item("attribute", 1L),
                item("something", "else")
        ))).isEqualTo(4.400000047683716); // Floating-point arithmetic ❤️
    }

    @Test
    public void it_calculates_correctly_multiple_attributes() {
        Operator operator = new SumOperator(new String[]{"a", "b"}, templator);

        assertThat(operator.calculate(Arrays.asList(
                multiItem(1, "ignored"),
                multiItem(2, 2),
                multiItem(new Object(), 3)
        ))).isEqualTo(8);
    }

    @Test
    public void it_cannot_match() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> {
                    Operator operator = new SumOperator(new String[]{"attribute"}, templator);

                    operator.match(item("attribute", "abc"));
                });
    }

    @Test
    public void it_cannot_group() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> {
                    Operator operator = new SumOperator(new String[]{"attribute"}, templator);

                    operator.group(item("attribute", "abc"));
                });
    }
}
