package de.cneubauer.ann.strategy;

import de.cneubauer.ann.NeuronalNetworkFactory;

import java.util.ArrayList;

/**
 * Created by Christoph Neubauer on 11.11.2016.
 */
public class BackPropNetworkFactory implements NeuronalNetworkFactory {
    @Override
    public void createNetwork(ArrayList<Long> neurons) {
   /*     Dim bnn As New NeuralNetwork()
        Dim neurons As Long

        Dim strategy As New BackPropNeuronStrategy()

        'NeuronsInLayers is an arraylist which holds
        'the number of neurons in each layer

        For Each neurons In neuronsInLayers
        Dim layer As NeuronLayer
        Dim i As Long

        layer = New NeuronLayer()

        'Let us add
        For i = 0 To neurons - 1
        layer.Add(New Neuron(strategy))
        Next

        bnn.Layers.Add(layer)
        Next

        'Connect all layers together
        bnn.ConnectLayers()
*/
    }
}
