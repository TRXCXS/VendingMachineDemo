package utils;

import entity.Layer;

import java.util.Comparator;
import java.util.List;

public class SortUtils {
    public static void sortLayersByIndex(List<Layer> layers) {
        layers.sort(Comparator.comparingInt(Layer::getIndex));
    }
}
