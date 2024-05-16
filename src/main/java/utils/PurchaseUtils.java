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
import java.util.Map;

public class PurchaseUtils {
    /**
     * 获取消费者的购物清单
     *
     * @param beforePurchase 购买之前的Layer组成的List
     * @param afterPurchase  购买之后的Layer组成的List
     * @param stocks         库存列表
     * @param goodsMap       商品ID和商品重量组成的map
     * @return 消费者购物的识别结果
     */
    public static RecognitionResult WeightIdentification(List<Layer> beforePurchase, List<Layer> afterPurchase, List<Stock> stocks, HashMap<String, Integer> goodsMap) {
        RecognitionResult recognitionResult = new RecognitionResult(); // 识别结果
        HashMap<String, Integer> purchasedGoodsMap = new HashMap<>(); // 购买商品的map
        List<RecognitionException> exceptions = new ArrayList<>(); // 识别到的异常列表
        List<Map.Entry<String, Integer>> goodsList = SortUtils.sortGoodsMapByWeight(goodsMap); // 商品ID和商品重量组成的map，按照商品重量从高到低排序得到的列表
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
                if (hasSensorException(before.getWeight(), after.getWeight(), stocks, i + 1, goodsMap)) {
                    RecognitionException exception = new RecognitionException(i + 1, before.getWeight(), after.getWeight(), ExceptionEnum.SENSOR_EXCEPTION);
                    exceptions.add(exception);
                    throw exception;
                }
                if (before.getWeight() < after.getWeight()) {
                    RecognitionException exception = new RecognitionException(i + 1, before.getWeight(), after.getWeight(), ExceptionEnum.FOREIGN_OBJECT_EXCEPTION);
                    exceptions.add(exception);
                    throw exception;
                }
                HashMap<String, Integer> goods = getGoodsOfLayer(stocks, i + 1);
                HashMap<String, Integer> purchasedGoods = getRecognitionItem(goods, goodsList, before.getWeight() - after.getWeight());
                if (purchasedGoods == null) {
                    RecognitionException exception = new RecognitionException(i + 1, before.getWeight(), after.getWeight(), ExceptionEnum.UNRECOGNIZED_EXCEPTION);
                    exceptions.add(exception);
                    throw exception;
                } else {
                    for (String goodsId : purchasedGoods.keySet()) {
                        purchasedGoodsMap.put(goodsId, purchasedGoodsMap.getOrDefault(goodsId, 0) + purchasedGoods.get(goodsId));
                    }
                }
            } catch (RecognitionException ignored) {

            }
        }

        recognitionResult.setSuccessful(exceptions.isEmpty());
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
     * @param stocks      库存列表
     * @param layerNumber 层号
     * @return 某层商品清单，key-value为商品ID和商品数量组成的map
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
     * @param stocks      库存列表
     * @param layerNumber 层号
     * @param goodsMap    商品ID和商品重量组成的map
     * @return 初始最大和最小重量，以及最终的最小重量
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
     * @param beforePurchaseWeight 购买之前的重量
     * @param afterPurchaseWeight  购买之后的重量
     * @param stocks               库存列表
     * @param layerNumber          层号
     * @param goodsMap             商品ID和商品重量组成的map
     * @return 布尔值，是否存在传感器异常
     */
    public static boolean hasSensorException(int beforePurchaseWeight, int afterPurchaseWeight, List<Stock> stocks, int layerNumber, HashMap<String, Integer> goodsMap) {
        int[] validWeight = getValidWeightOfLayer(stocks, layerNumber, goodsMap);
        int maxStartWeight = validWeight[0], minStartWeight = validWeight[1], minEndWeight = validWeight[2];
        return beforePurchaseWeight < minStartWeight || beforePurchaseWeight > maxStartWeight || afterPurchaseWeight < minEndWeight;
    }

    /**
     * 尝试获取一个可能的购买组合
     *
     * @param goods            该层商品ID和商品数量组成的map
     * @param goodsList        商品ID和商品重量组成的map经过排序获得的List
     * @param weightDifference 购买重量与购买之前的重量的差值
     * @return 一个可能的购买组合，若不存在或存在多个则返回null
     */
    public static HashMap<String, Integer> getRecognitionItem(HashMap<String, Integer> goods, List<Map.Entry<String, Integer>> goodsList, int weightDifference) {
        HashMap<String, Integer> onePurchasedGoods_forward = new HashMap<>();
        HashMap<String, Integer> onePurchasedGoods_reverse = new HashMap<>();

        if (findOneRecognitionItem_forward(goods, goodsList, weightDifference, onePurchasedGoods_forward, 0) && findOneRecognitionItem_reverse(goods, goodsList, weightDifference, onePurchasedGoods_reverse, goodsList.size() - 1)) {
            if (onePurchasedGoods_forward.equals(onePurchasedGoods_reverse)) {
                return onePurchasedGoods_forward;
            }
        }
        return null;
    }

    /**
     * 使用回溯算法的思路找到一个可能的购买组合，正向寻找，即优先考虑购买重量最大的商品
     *
     * @param goods              该层商品ID和商品数量组成的map
     * @param goodsList          商品ID和商品重量组成的map经过排序获得的List
     * @param weightDifference   购买重量与购买之前的重量的差值
     * @param currentCombination 一个可能的购买组合
     * @param startIndex         开始考虑的商品下标
     * @return 一个可能的购买组合
     */
    private static boolean findOneRecognitionItem_forward(HashMap<String, Integer> goods, List<Map.Entry<String, Integer>> goodsList, int weightDifference, HashMap<String, Integer> currentCombination, int startIndex) {
        if (weightDifference == 0) {
            // 找到一个满足条件的组合
            return true;
        }

        for (int i = startIndex; i < goodsList.size(); i++) {
            Map.Entry<String, Integer> goodsItem = goodsList.get(i);
            String goodsId = goodsItem.getKey();
            int goodsWeight = goodsItem.getValue();
            int goodsNumber = goods.get(goodsId);

            if (goodsWeight > weightDifference || goodsNumber == 0) {
                // 跳过重量超过剩余重量差或数量为零的商品
                continue;
            }

            // 从最大数量开始考虑
            for (int quantity = goodsNumber; quantity >= 1; quantity--) {
                if (goodsWeight * quantity > weightDifference) {
                    // 当前商品重量超过剩余重量差时，终止循环
                    continue;
                }

                currentCombination.put(goodsId, quantity);
                if (findOneRecognitionItem_forward(goods, goodsList, weightDifference - goodsWeight * quantity, currentCombination, i + 1)) {
                    // 从下一件商品继续考虑，如果可以找到，返回true
                    return true;
                } else {
                    // 如果找不到，排除当前的商品数量组合，继续考虑
                    currentCombination.remove(goodsId);
                }
            }
        }
        return false;
    }

    /**
     * 使用回溯算法的思路找到一个可能的购买组合，反向寻找，即优先考虑购买重量最小的商品
     *
     * @param goods              该层商品ID和商品数量组成的map
     * @param goodsList          商品ID和商品重量组成的map经过排序获得的List
     * @param weightDifference   购买重量与购买之前的重量的差值
     * @param currentCombination 一个可能的购买组合
     * @param startIndex         开始考虑的商品下标
     * @return 一个可能的购买组合
     */
    private static boolean findOneRecognitionItem_reverse(HashMap<String, Integer> goods, List<Map.Entry<String, Integer>> goodsList, int weightDifference, HashMap<String, Integer> currentCombination, int startIndex) {
        if (weightDifference == 0) {
            // 找到一个满足条件的组合
            return true;
        }

        for (int i = startIndex; i >= 0; i--) {
            Map.Entry<String, Integer> goodsItem = goodsList.get(i);
            String goodsId = goodsItem.getKey();
            int goodsWeight = goodsItem.getValue();
            int goodsNumber = goods.get(goodsId);

            if (goodsWeight > weightDifference || goodsNumber == 0) {
                // 跳过重量超过剩余重量差或数量为零的商品
                continue;
            }

            // 从最大数量开始考虑
            for (int quantity = goodsNumber; quantity >= 1; quantity--) {
                if (goodsWeight * quantity > weightDifference) {
                    // 当前商品重量超过剩余重量差时，终止循环
                    continue;
                }

                currentCombination.put(goodsId, quantity);
                if (findOneRecognitionItem_reverse(goods, goodsList, weightDifference - goodsWeight * quantity, currentCombination, i - 1)) {
                    // 从下一件商品继续考虑，如果可以找到，返回true
                    return true;
                } else {
                    // 如果找不到，排除当前的商品数量组合，继续考虑
                    currentCombination.remove(goodsId);
                }
            }
        }
        return false;
    }
}