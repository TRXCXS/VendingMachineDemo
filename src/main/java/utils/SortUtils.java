package utils;

import entity.Layer;
import entity.RecognitionItem;

import java.util.*;

public class SortUtils {
    public static void sortLayersByIndex(List<Layer> layers) {
        layers.sort(Comparator.comparingInt(Layer::getIndex));
    }

    public static List<Map.Entry<String, Integer>> sortGoodsMapByWeight(HashMap<String, Integer> goodsMap) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(goodsMap.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        return list;
    }

    public static void sortListByGoodsId(List<RecognitionItem> items) {
        items.sort(Comparator.comparing(RecognitionItem::getGoodsId));
    }
}
