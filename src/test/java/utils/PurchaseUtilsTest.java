package utils;

import entity.Layer;
import entity.RecognitionItem;
import entity.RecognitionResult;
import entity.Stock;
import exception.ExceptionEnum;
import exception.RecognitionException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PurchaseUtilsTest {
    @Test
    void weightIdentification_1() {
        List<Layer> beforePurchase = new ArrayList<>() {{
            add(new Layer(1, 3000));
            add(new Layer(2, 4250));
            add(new Layer(3, 3500));
        }};
        List<Layer> afterPurchase = new ArrayList<>() {{
            add(new Layer(1, 1850));
            add(new Layer(2, 3000));
            add(new Layer(3, 1900));
        }};
        List<Stock> stocks = new ArrayList<>() {{
            add(new Stock("000001", 1, 10));
            add(new Stock("000005", 1, 5));
            add(new Stock("000003", 2, 5));
            add(new Stock("000008", 2, 3));
            add(new Stock("000002", 3, 5));
            add(new Stock("000010", 3, 2));
        }};
        HashMap<String, Integer> goodsMap = new HashMap<>() {{
            put("000001", 50);
            put("000002", 100);
            put("000003", 250);
            put("000004", 300);
            put("000005", 500);
            put("000006", 600);
            put("000007", 750);
            put("000008", 1000);
            put("000009", 1200);
            put("000010", 1500);
        }};

        RecognitionResult expectedResult = new RecognitionResult();
        expectedResult.setSuccessful(false);
        List<RecognitionException> exceptions = new ArrayList<>();
        exceptions.add(new RecognitionException(2, 4250, 3000, ExceptionEnum.UNRECOGNIZED_EXCEPTION));
        expectedResult.setExceptions(exceptions);
        expectedResult.setItems(new ArrayList<>() {{
            add(new RecognitionItem("000001", 3));
            add(new RecognitionItem("000002", 1));
            add(new RecognitionItem("000005", 2));
            add(new RecognitionItem("000010", 1));
        }});

        assertEquals(expectedResult, PurchaseUtils.WeightIdentification(beforePurchase, afterPurchase, stocks, goodsMap));
    }

    @Test
    void weightIdentification_2() {
        List<Layer> beforePurchase = new ArrayList<>() {{
            add(new Layer(1, 3000));
            add(new Layer(2, 3750));
            add(new Layer(3, 3500));
        }};
        List<Layer> afterPurchase = new ArrayList<>() {{
            add(new Layer(1, 1850));
            add(new Layer(2, 2750));
            add(new Layer(3, 1900));
        }};
        List<Stock> stocks = new ArrayList<>() {{
            add(new Stock("000001", 1, 10));
            add(new Stock("000005", 1, 5));
            add(new Stock("000003", 2, 3));
            add(new Stock("000008", 2, 3));
            add(new Stock("000002", 3, 5));
            add(new Stock("000010", 3, 2));
        }};
        HashMap<String, Integer> goodsMap = new HashMap<>() {{
            put("000001", 50);
            put("000002", 100);
            put("000003", 250);
            put("000004", 300);
            put("000005", 500);
            put("000006", 600);
            put("000007", 750);
            put("000008", 1000);
            put("000009", 1200);
            put("000010", 1500);
        }};

        RecognitionResult expectedResult = new RecognitionResult();
        expectedResult.setSuccessful(true);
        List<RecognitionException> exceptions = new ArrayList<>();
        expectedResult.setExceptions(exceptions);
        expectedResult.setItems(new ArrayList<>() {{
            add(new RecognitionItem("000001", 3));
            add(new RecognitionItem("000002", 1));
            add(new RecognitionItem("000005", 2));
            add(new RecognitionItem("000008", 1));
            add(new RecognitionItem("000010", 1));
        }});

        assertEquals(expectedResult, PurchaseUtils.WeightIdentification(beforePurchase, afterPurchase, stocks, goodsMap));
    }
}