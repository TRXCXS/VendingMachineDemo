package utils;

import entity.Layer;
import entity.RecognitionResult;
import entity.Stock;
import exception.ExceptionEnum;
import exception.RecognitionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PurchaseUtils {
    public static RecognitionResult WeightIdentification(List<Layer> beforePurchase, List<Layer> afterPurchase, List<Stock> stocks) {
        RecognitionResult recognitionResult = new RecognitionResult();
        HashMap<String, Integer> goodsMap = new HashMap<>();
        List<RecognitionException> exceptions = new ArrayList<>();

        for (int i = 0; i < beforePurchase.size(); i++) {
            Layer before = beforePurchase.get(i);
            Layer after = afterPurchase.get(i);

            if (before.getWeight() == after.getWeight()) {
                continue;
            }

            if (before.getWeight() < after.getWeight()) {
                exceptions.add(new RecognitionException(i, before.getWeight(), after.getWeight(), ExceptionEnum.FOREIGN_OBJECT_EXCEPTION));
            }
        }

        return null;
    }
}
