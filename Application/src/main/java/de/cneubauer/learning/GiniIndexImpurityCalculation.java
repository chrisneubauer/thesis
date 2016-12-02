package de.cneubauer.learning;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Christoph Neubauer on 21.11.2016.
 */
public class GiniIndexImpurityCalculation implements ImpurityCalculationMethod {
    @Override
    public double calculateImpurity(List<DataSample> splitData) {
        List<Label> labels = splitData.parallelStream().map(data -> data.getLabel()).distinct().collect(Collectors.toList());
        if (labels.size() > 1) {
            double p = getEmpiricalProbability(splitData, labels.get(0), labels.get(1)); // TODO fix to multiple labels
            return 2.0 * p * (1 - p);
        } else if (labels.size() == 1) {
            return 0.0; // if only one label data is pure
        } else {
            throw new IllegalStateException("This should never happen. Probably a bug.");
        }
    }
}