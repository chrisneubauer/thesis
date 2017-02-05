package de.cneubauer.ml;

import java.io.IOException;

/**
 * Created by Christoph Neubauer on 02.02.2017.
 * This class is used for initiating the search
 */
public class LearningService {

    public boolean exists(String position) {
        Model fakeModel = new Model();
        fakeModel.setPosition(position);
        return this.isModelExisting(fakeModel);
    }

    public boolean isModelExisting(Model model) {
        ModelReader reader = new ModelReader();
        //TODO: url of the reader
        try {
            for (Model m : reader.getModels()) {
                if (m.positionEqualsWith(model.getPosition())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
