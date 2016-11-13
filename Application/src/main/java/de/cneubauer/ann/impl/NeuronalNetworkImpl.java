package de.cneubauer.ann.impl;

import de.cneubauer.ann.Neuron;
import de.cneubauer.ann.NeuronalNetwork;
import de.cneubauer.ann.training.TrainData;

import java.util.ArrayList;

/**
 * Created by Christoph Neubauer on 11.11.2016.
 */
public class NeuronalNetworkImpl implements NeuronalNetwork {
    @Override
    public void train(TrainData trainingData) {
        /*
        * Dim i As Long
Dim someNeuron As INeuron

i = 0

'Give our inputs to the first layer.
't is an object of TrainingData class

For Each someNeuron In InputLayer
    someNeuron.OutputValue = t.Inputs(i)
    i = i + 1
Next

'Step1: Find the output of hidden layer
'neurons and output layer neurons

Dim nl As NeuronLayer
Dim count As Long = 1

For count = 1 To _layers.Count - 1
    nl = _layers(count)
    For Each someNeuron In nl
        someNeuron.UpdateOutput()
    Next
Next

'Step2: Finding Delta

'2.1) Find the delta (error rate) of output layer

i = 0
For Each someNeuron In OutputLayer
    'Find the target-output value and pass it
    someNeuron.UpdateDelta(t.Outputs(i) - _
               someNeuron.OutputValue)
    i = i + 1
Next

'2.2) Calculate delta of all the hidden layers, backwards

Dim layer As Long
Dim currentLayer As NeuronLayer

For i = _layers.Count - 2 To 1 Step -1


    currentLayer = _layers(i)

    For Each someNeuron In currentLayer
        Dim errorFactor As Single = 0
        Dim connectedNeuron As INeuron

        For Each connectedNeuron In _
                 someNeuron.ForwardConnections
            'Sum up all the delta * weight
            errorFactor = _
              errorFactor + (connectedNeuron.DeltaValue * _
              connectedNeuron.Inputs.Weight(someNeuron))
        Next

        someNeuron.UpdateDelta(errorFactor)
    Next

Next

'Step3: Update the free parameters of hidden and output layers

For i = 1 To _layers.Count - 1
    For Each someNeuron In _layers(i)
        someNeuron.UpdateFreeParams()
    Next
Next*/
    }

    @Override
    public void connectNeurons(Neuron src, Neuron dst, double weight) {

    }

    @Override
    public void connectNeurons(Neuron src, Neuron dst) {

    }

    @Override
    public void connectLayers(NeuronalLayer l1, NeuronalLayer l2) {

    }

    @Override
    public void connectLayers() {

    }

    @Override
    public ArrayList<Neuron> run(ArrayList<Neuron> input) {
        /*Dim someNeuron As INeuron

        Dim i As Long = 0
        For Each someNeuron In InputLayer
        someNeuron.OutputValue = CType(inputs(i), System.Single)
        i += 1
        Next

        'Step1: Find the output of each hidden neuron layer


        Dim nl As NeuronLayer

        For i = 1 To _layers.Count - 1

        nl = _layers(i)
        For Each someNeuron In nl
        someNeuron.UpdateOutput()
        Next
        Next */
        return null;
    }

    @Override
    public ArrayList<Neuron> getOutput() {
        return null;
    }
}
