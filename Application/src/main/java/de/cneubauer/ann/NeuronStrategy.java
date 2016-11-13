package de.cneubauer.ann;

import java.util.Collection;

/**
 * Created by Christoph Neubauer on 11.11.2016.
 */
public interface NeuronStrategy {

    // returns delta value of the neuron (or errorFactor)
    double findDelta(double output, double errorFactor);

    // threshold function, returns an activation value
    double threshold(double thresholdValue);

    // finds the net value
    double findNetValue(Collection<Neuron> neurons, double biasValue);

    // recalculating the bias
    double findBias(double biasValue, double deltaValue);

    // updating the weights of the neuron
    void updateNeuronWeights(Collection<Neuron> connections);
}
