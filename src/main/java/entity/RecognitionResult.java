package entity;

import exception.RecognitionException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecognitionResult {
    // 没有任何异常时，为true
    private boolean successful;
    // 允许为empty，不允许为null。成功且为empty时即为无购物。
    private List<RecognitionItem> items;
    // 允许为empty，不允许为null。
    private List<RecognitionException> exceptions;
}
