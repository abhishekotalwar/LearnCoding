import java.math.BigDecimal;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Component
@Converter(autoApply = true)
public class AmountConverter implements AttributeConverter<Amount, BigDecimal> {

   @Override
   public BigDecimal convertToDatabaseColumn(Amount amount) {
      return amount != null ? amount.getValue() : null;
   }

   @Override
   public Amount convertToEntityAttribute(BigDecimal amount) {
      return amount != null ? Amount.of(amount) : null;
   }
}
