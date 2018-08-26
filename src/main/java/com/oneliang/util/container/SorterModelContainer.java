package com.oneliang.util.container;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SorterModelContainer<MODEL> implements ModelContainer<MODEL> {

    private List<MODEL> modelList = new ArrayList<MODEL>();
    private Comparator<MODEL> comparator = null;

    public SorterModelContainer(Comparator<MODEL> comparator) {
        this.comparator = comparator;
    }

    private int count = 0;

    private boolean breaker() {
        if (modelList.size() == 5) {
            if (count == 1) {
                return false;// true;
            }
            count++;
        }
        return false;
    }

    // 0,1,2,3,4,5,6,7,8,9
    private void findIndex(MODEL model, int startIndex, int endIndex) {
        int middleIndex = (startIndex + endIndex + 1) / 2;
        System.out.println(String.format("start:%s, end:%s, middle:%s, model:%s", startIndex, endIndex, middleIndex, model));
        if (breaker()) {
            return;
        }
        MODEL middleModel = modelList.get(middleIndex);
        int compareResult = this.comparator.compare(model, middleModel);
        int diffIndex = endIndex - startIndex;
        if (compareResult == 0) {
            // the same object
            System.out.println(String.format("find the same object, index:%s, model:%s", middleIndex, middleModel));
            return;
        } else if (compareResult > 0) {
            // int diffIndex = middleIndex - startIndex;
            if (diffIndex == 0) {
                modelList.add(middleIndex + 1, model);
            } else {
                if (middleIndex == endIndex) {
                    modelList.add(middleIndex + 1, model);
                } else {
                    findIndex(model, middleIndex + 1, endIndex);
                }
            }
        } else {
            // int diffIndex = endIndex - middleIndex;
            if (diffIndex == 0) {
                modelList.add(middleIndex, model);
            } else {
                if (middleIndex == startIndex) {
                    modelList.add(middleIndex, model);
                } else {
                    findIndex(model, startIndex, middleIndex - 1);
                }
            }
        }
    }

    public boolean add(MODEL model) {
        if (modelList.isEmpty()) {
            modelList.add(model);
        } else {
            int total = modelList.size();
            findIndex(model, 0, total - 1);
        }

        return true;
    }

    public boolean remove(MODEL model) {
        modelList.remove(model);
        return true;
    }

    public MODEL get(int index) {
        return modelList.get(index);
    }

    public int size() {
        return modelList.size();
    }

    public void print() {
        for (MODEL model : modelList) {
            System.out.println(model);
        }
    }
}
