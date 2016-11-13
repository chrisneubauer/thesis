package de.cneubauer.ann;

import de.cneubauer.ann.impl.NeuronalLayer;
import de.cneubauer.ann.training.TrainData;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Christoph Neubauer on 11.11.2016.
 */
public interface NeuronalNetwork {
    Collection<NeuronalLayer> layers = null;
    NeuronalLayer inputLayer = null;
    NeuronalLayer outputLayer = null;

    // trains the network with the given trainingdata
    void train(TrainData trainingData);

    // connects two neurons with a weight
    void connectNeurons(Neuron src, Neuron dst, double weight);

    // connects two neurons with a random weight
    void connectNeurons(Neuron src, Neuron dst);

    // connect two layers with random weight
    void connectLayers(NeuronalLayer l1, NeuronalLayer l2);

    // connects all neurons in all layers
    void connectLayers();

    // runs the actual network
    ArrayList<Neuron> run(ArrayList<Neuron> input);

    // get output
    ArrayList<Neuron> getOutput();

    static NeuronalLayer getInputLayer() {
        return inputLayer;
    }

    static NeuronalLayer getOutputLayer() {
        return outputLayer;
    }
}
