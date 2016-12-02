package de.cneubauer.learning;

import java.util.List;

/**
 * Created by Christoph Neubauer on 21.11.2016.
 */
@FunctionalInterface
public interface ImpurityCalculationMethod {

    /**
     * Calculates impurity value. High impurity implies low information gain and more random labels of data which in
     * turn means that split is not very good.
     *
     * @param splitData Data subset on which impurity is calculated.
     * @return Impurity.
     */
    double calculateImpurity(List<DataSample> splitData);


    /**
     * Calculate and return empirical probability of positive class. p+ = n+ / (n+ + n-).
     *
     * @param splitData Data on which positive label probability is calculated.
     * @return Empirical probability.
     */
    default double getEmpiricalProbability(List<DataSample> splitData, Label positive, Label negative) {
        // TODO cache calculated counts
        return (double) splitData.parallelStream().filter(d -> d.getLabel().equals(positive)).count() / splitData.size();
    }
}