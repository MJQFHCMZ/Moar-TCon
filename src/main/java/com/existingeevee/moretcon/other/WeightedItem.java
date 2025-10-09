package com.existingeevee.moretcon.other;

import java.util.Collection;
import java.util.Random;

public class WeightedItem<T> {
    private T item;
    private double weight;

    public WeightedItem(T item, double weight) {
        this.item = item;
        this.weight = weight;
    }

    public T getItem() {
        return item;
    }

    public double getWeight() {
        return weight;
    }

    public static <T> double getTotalWeight(Collection<WeightedItem<T>> weightedList) {
    	return weightedList.stream().mapToDouble(WeightedItem::getWeight).sum();
    }
    
    public static <T> T getWeightedRandomItem(Collection<WeightedItem<T>> weightedList, Random rand, boolean remove) {
        double totalWeight = getTotalWeight(weightedList);
        double randomWeight = rand.nextDouble() * totalWeight;

    	if (totalWeight <= 0) {
    		return null;
    	}
        
        WeightedItem<T> ret = null;
        
        for (WeightedItem<T> item : weightedList) {
            randomWeight -= item.getWeight();
            if (randomWeight <= 0) {
            	ret = item;
            	break;
            }
        }
        
        if (remove && ret != null) {
        	weightedList.remove(ret);
        }
        return ret.getItem();
    }

    public static <T> T getWeightedRandomItem(Collection<WeightedItem<T>> weightedList) {
        return getWeightedRandomItem(weightedList, new Random(), false);
    }

    public static <T> T getWeightedRandomItem(Collection<WeightedItem<T>> weightedList, boolean remove) {
        return getWeightedRandomItem(weightedList, new Random(), remove);
    }
}