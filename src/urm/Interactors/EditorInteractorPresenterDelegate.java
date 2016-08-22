package urm.Interactors;

import javafx.scene.control.IndexRange;

/**
 * Created by Дмитрий on 14.08.2016.
 */
public interface EditorInteractorPresenterDelegate {

    void registersDataDidUpdated();

    void reseavedError(String errorDescription , IndexRange problemRange);

    void updateCurrentOperation();

    void programDidFinished();

}
