package urm.Interactors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import urm.Utilities.Register;

/**
 * Created by Дмитрий on 14.08.2016.
 */
public class EditorInteractor {

    public EditorInteractorPresenterDelegate presenter;

    public ObservableList registersData = FXCollections.observableArrayList();

    public void appendDataToIndex(int index){

        for (int i = this.registersData.size() ; i <= index ; i++ ) {

            Register register = new Register();
            register.index = i;
            register.value = 0;

            this.registersData.add(register);

        }

        this.presenter.registersDataDidUpdated();
    }
}
