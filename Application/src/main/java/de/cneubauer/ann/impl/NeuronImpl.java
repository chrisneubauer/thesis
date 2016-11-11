package de.cneubauer.ann.impl;

import de.cneubauer.ann.Neuron;
import de.cneubauer.ann.NeuronStrategy;

/**
 * Created by Christoph Neubauer on 11.11.2016.
 */
public class NeuronImpl implements Neuron {
    @Override
    public void setStrategy(NeuronStrategy ns) {

    }

    @Override
    public NeuronStrategy getStrategy() {
        return null;
    }

    @Override
    public void updateOutputValue() {

    }

    @Override
    public void updateDelta(double errorFactor) {

    }

    @Override
    public void updateFreeParams() {

    }
  /*  'Calculate the error value
    Public Sub UpdateDelta(ByVal errorFactor As Single) Implements _
    NeuralFramework.INeuron.UpdateDelta

    If _strategy Is Nothing Then
    Throw New StrategyNotInitializedException("", Nothing)

    'Error factor is found and passed to this
    DeltaValue = Strategy.FindDelta(OutputValue, errorFactor)
    End Sub

'Calculate the output
    Public Sub UpdateOutput() _
    Implements NeuralFramework.INeuron.UpdateOutput

    If _strategy Is Nothing Then
    Throw New StrategyNotInitializedException("..", Nothing)

    Dim netValue As Single = Strategy.FindNetValue(Inputs, BiasValue)
    OutputValue = Strategy.Activation(netValue)
    End Sub

'Calculate the free parameters
    Public Sub UpdateFreeParams() _
    Implements NeuralFramework.INeuron.UpdateFreeParams

    If _strategy Is Nothing Then
    Throw New StrategyNotInitializedException("..", Nothing)

    BiasValue = Strategy.FindNewBias(BiasValue, DeltaValue)
            Strategy.UpdateWeights(Inputs, DeltaValue)
*/
}
