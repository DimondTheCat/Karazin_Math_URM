package urm.Interactors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import urm.Entities.ProcessLoop;
import urm.Entities.ProcessLoopDatasource;
import urm.Utilities.Register;
import urm.Utilities.RegistersManager;
import urm.Utilities.operations.UrmOperation;

import java.util.ArrayList;

/**
 * Created by Дмитрий on 14.08.2016.
 */
public class EditorInteractor implements RegistersManager , ProcessLoopDatasource {

    //MARK: Property's
    public EditorInteractorPresenterDelegate presenter;
    public ObservableList registersData = FXCollections.observableArrayList();

    private ProcessLoop processLoopEntity = new ProcessLoop();

    //MARK: Constructors

    public EditorInteractor(){

        super();

        this.processLoopEntity.datasource = this;

    }

    //MARK: Methods


    //MARK: METHODS - CODE PROCESS LOOP



    /*
    increase size of registers list
    */
    public void appendDataToIndex(int index){

        for (int i = this.registersData.size() ; i <= index ; i++ ) {

            Register register = new Register();
            register.index = i;
            register.value = 0;

            this.registersData.add(register);

        }

        this.presenter.registersDataDidUpdated();
    }


    //MARK: REGISTERS MANAGER DELEGATE
    @Override
    public Register getRegisterAtIndex(int index) {

        if (index > registersData.size()-1){
            this.appendDataToIndex(index);
        }

        return (Register) this.registersData.get(index);
    }

    @Override
    public ArrayList<UrmOperation> getOperationsForProcessLoop() {
        return null;
    }
}
