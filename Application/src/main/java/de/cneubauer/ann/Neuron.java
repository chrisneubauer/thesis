package de.cneubauer.ann;

import java.util.Collection;

/**
 * Created by Christoph Neubauer on 11.11.2016.
 */
public interface Neuron {
    double outputValue = 0.0;
    double biasValue = 0.0;
    double delta = 0.0;

    // connections from this neuron
    Collection<Neuron> forwardConnections = null;

    // connections to this neuron
    Collection<Neuron> backwardConnections = null;

    NeuronStrategy strategy = null;

    void setStrategy(NeuronStrategy ns);
    NeuronStrategy getStrategy();

    // recalculate output value
    void updateOutputValue();

    // update delta value by error factor
    void updateDelta(double errorFactor);

    // update all free params
    void updateFreeParams();
}
