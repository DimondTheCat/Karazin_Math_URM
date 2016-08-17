package urm.Interactors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.IndexRange;
import urm.Entities.ProcessLoop;
import urm.Entities.ProcessLoopDatasource;
import urm.Entities.ProcessLoopDelegate;
import urm.Utilities.CodeManager;
import urm.Utilities.CodeManagerDelegate;
import urm.Utilities.Register;
import urm.Utilities.RegistersManager;
import urm.Utilities.operations.UrmOperation;

import java.util.ArrayList;

/**
 * Created by Дмитрий on 14.08.2016.
 */
public class EditorInteractor implements RegistersManager , ProcessLoopDatasource , CodeManagerDelegate, ProcessLoopDelegate {

    //MARK: Property's
    public EditorInteractorPresenterDelegate presenter;

    //MARK: Mechanism
    public ObservableList registersData = FXCollections.observableArrayList();
    public int mechanismCurrentOperation = 0;

    private ProcessLoop processLoopEntity = new ProcessLoop();

    //MARK: Constructors

    public EditorInteractor(){

        super();

        this.processLoopEntity.datasource = this;
        this.processLoopEntity.delegate = this;
        CodeManager.sharedManager().delegate = this;

    }

    //MARK: Methods

    public void startMachine(String code , boolean inRealtime){

        //create operations and check for errors
        if ( CodeManager.sharedManager().setupManagerWithText(code) ){

            //start processing
            this.processLoopEntity.isNeedToRunInDebug = !inRealtime;
            this.processLoopEntity.startEventLoop();
        }

    }

    //MARK: METHODS - CODE PROCESS LOOP

    @Override
    public void codeManagerCurrentOperationChanged(CodeManager sender, int operationNumber) {

    }

    @Override
    public void errorWhileParsing(CodeManager sender, String errorDescription, IndexRange rangeOfProblemRow) {

        this.presenter.reseavedError(errorDescription , rangeOfProblemRow);

    }

    @Override
    public void processLoopFinishedRunning() {

    }

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
    public int getCurrentOperation() {
        return this.mechanismCurrentOperation;
    }

    @Override
    public void setCurrentOperation(int currentOperation) {

        this.mechanismCurrentOperation = currentOperation;
    }


    @Override
    public ArrayList<UrmOperation> getOperationsForProcessLoop() {
        return CodeManager.sharedManager().operations;
    }



}
