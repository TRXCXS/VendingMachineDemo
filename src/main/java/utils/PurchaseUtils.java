package utils;

import entity.Layer;
import entity.RecognitionItem;
import entity.RecognitionResult;
import entity.Stock;
import exception.ExceptionEnum;
import exception.RecognitionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PurchaseUtils {
    /**
     * 获取消费者的购物清单
     *
     * @param beforePurchase
     * @param afterPurchase
     * @param stocks
     * @param goodsMap
     * @return
     */
    public static RecognitionResult WeightIdentification(List<Layer> beforePurchase, List<Layer> afterPurchase, List<Stock> stocks, HashMap<String, Integer> goodsMap) {
        RecognitionResult recognitionResult = new RecognitionResult();
        HashMap<String, Integer> purchasedGoodsMap = new HashMap<>();
        List<RecognitionException> exceptions = new ArrayList<>();
        SortUtils.sortLayersByIndex(beforePurchase);
        SortUtils.sortLayersByIndex(afterPurchase);

        for (int i = 0; i < beforePurchase.size(); i++) {
            Layer before = beforePurchase.get(i);
            Layer after = afterPurchase.get(i);

            // 如果重量相等，说明没有购买
            if (before.getWeight() == after.getWeight()) {
                continue;
            }

            try {
                if (hasSensorException(beforePurchase, afterPurchase, stocks, i + 1, goodsMap)) {
                    RecognitionException exception = new RecognitionException(i + 1, before.getWeight(), after.getWeight(), ExceptionEnum.SENSOR_EXCEPTION);
                    exceptions.add(exception);
                    throw exception;
                }
                if (before.getWeight() < after.getWeight()) {
                    RecognitionException exception = new RecognitionException(i + 1, before.getWeight(), after.getWeight(), ExceptionEnum.FOREIGN_OBJECT_EXCEPTION);
                    exceptions.add(exception);
                    throw exception;
                }
                // todo 识别商品组合
            } catch (RecognitionException ignored) {

            }
        }

        if (!exceptions.isEmpty()) {
            recognitionResult.setSuccessful(false);
        } else {
            recognitionResult.setSuccessful(true);
        }
        List<RecognitionItem> items = new ArrayList<>();
        for (String goodsId : purchasedGoodsMap.keySet()) {
            RecognitionItem item = new RecognitionItem(goodsId, purchasedGoodsMap.get(goodsId));
            items.add(item);
        }
        recognitionResult.setItems(items);
        recognitionResult.setExceptions(exceptions);
        return recognitionResult;
    }

    /**
     * 获取某一层的商品清单，由商品ID和商品数量组成的map
     *
     * @param stocks
     * @param layerNumber
     * @return
     */
    public static HashMap<String, Integer> getGoodsOfLayer(List<Stock> stocks, int layerNumber) {
        HashMap<String, Integer> goods = new HashMap<>();
        for (Stock stock : stocks) {
            if (stock.getLayer() == layerNumber) {
                goods.put(stock.getGoodsId(), stock.getNum());
            }
        }
        return goods;
    }

    /**
     * 获取某一层的商品重量范围
     *
     * @param stocks
     * @param layerNumber
     * @param goodsMap
     * @return
     */
    public static int[] getValidWeightOfLayer(List<Stock> stocks, int layerNumber, HashMap<String, Integer> goodsMap) {
        int maxStartWeight = 0, minStartWeight = 0, minEndWeight = 0;
        int[] validWeight = new int[3];

        // goods是商品ID和商品数量组成的map，goodsMap是商品ID和商品重量组成的map
        HashMap<String, Integer> goods = getGoodsOfLayer(stocks, layerNumber);
        for (String goodsId : goods.keySet()) {
            maxStartWeight += goods.get(goodsId) * goodsMap.get(goodsId);
        }

        validWeight[0] = maxStartWeight;
        validWeight[1] = minStartWeight;
        validWeight[2] = minEndWeight;
        return validWeight;
    }

    /**
     * 判断某一层是否存在传感器异常
     *
     * @param beforePurchase
     * @param afterPurchase
     * @param stocks
     * @param layerNumber
     * @param goodsMap
     * @return
     */
    public static boolean hasSensorException(List<Layer> beforePurchase, List<Layer> afterPurchase, List<Stock> stocks, int layerNumber, HashMap<String, Integer> goodsMap) {
        int[] validWeight = getValidWeightOfLayer(stocks, layerNumber, goodsMap);
        int maxStartWeight = validWeight[0], minStartWeight = validWeight[1], minEndWeight = validWeight[2], beforePurchaseWeight = beforePurchase.get(layerNumber - 1).getWeight(), afterPurchaseWeight = afterPurchase.get(layerNumber - 1).getWeight();
        return beforePurchaseWeight < minStartWeight || beforePurchaseWeight > maxStartWeight || afterPurchaseWeight < minEndWeight;
    }
}
