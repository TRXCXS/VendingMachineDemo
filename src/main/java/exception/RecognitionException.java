package exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecognitionException extends RuntimeException {
    private int layer;
    private int beginWeight;
    private int endWeight;
    private ExceptionEnum exception;
}