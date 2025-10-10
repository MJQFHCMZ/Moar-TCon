package com.existingeevee.moretcon.other;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.function.Predicate;

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
    
    public static <T> T getWeightedRandomItem(Collection<WeightedItem<T>> weightedList, boolean remove, Random rand) {
        return getWeightedRandomItem(weightedList, remove, rand, t -> true);
    }

    public static <T> T getWeightedRandomItem(Collection<WeightedItem<T>> weightedList) {
        return getWeightedRandomItem(weightedList, false, new Random());
    }

    public static <T> T getWeightedRandomItem(Collection<WeightedItem<T>> weightedList, boolean remove) {
        return getWeightedRandomItem(weightedList, remove, new Random());
    }

	public static <T> T getWeightedRandomItem(Collection<WeightedItem<T>> weightedList, boolean remove, Random rand, Predicate<T> filter) {
		
		Collection<WeightedItem<T>> filtered = new ArrayList<>(weightedList);
		filtered.removeIf(w -> !filter.test(w.getItem()));
		
		double totalWeight = getTotalWeight(filtered);
        double randomWeight = rand.nextDouble() * totalWeight;

    	if (totalWeight <= 0) {
    		return null;
    	}
        
        WeightedItem<T> ret = null;
        
        for (WeightedItem<T> item : filtered) {
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
}