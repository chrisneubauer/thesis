package de.cneubauer.ann.strategy;

import de.cneubauer.ann.Neuron;
import de.cneubauer.ann.NeuronStrategy;

import java.util.Collection;

/**
 * Created by Christoph Neubauer on 11.11.2016.
 */
public class BackPropStrategy implements NeuronStrategy {
    @Override
    public double findDelta(double output, double errorFactor) {
        return 0;
    }

    @Override
    public double threshold(double thresholdValue) {
        return 0;
    }

    @Override
    public double findNetValue(Collection<Neuron> neurons, double biasValue) {
        return 0;
    }

    @Override
    public double findBias(double biasValue, double deltaValue) {
        return 0;
    }

    @Override
    public void updateNeuronWeights(Collection<Neuron> connections) {

    }
}
