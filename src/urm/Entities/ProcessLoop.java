package urm.Entities;

import javafx.application.Platform;
import urm.Utilities.operations.UrmOperation;

import java.util.ArrayList;

/**
 * Created by Дом on 15.08.2016.
 */
public class ProcessLoop {

    //MARK: PROPERTIES
    public ProcessLoopDatasource datasource;
    public ProcessLoopDelegate delegate;

    public boolean isNeedToRunInDebug = false;
    public boolean isProcessRunInDebug = false;

    private volatile boolean processThreadIsRunning = false;
    private volatile boolean isNeedToHaltThread = false;

    //List of operations that will be used in loop
    private ArrayList<UrmOperation> operations = null;
    private int currentOperationIndex = 0;

    private Thread backgroundLoop = null;

    //MARK: CONSTRUCTORS
    public ProcessLoop(){

        super();
    }

    //MARK: METHODS
    public void startEventLoop(){

        if (isNeedToRunInDebug){

            if (isProcessRunInDebug){

                debugOperationAndNextStep();
            }else {

                this.configureStartData();
                this.isProcessRunInDebug = true;
            }

            this.delegate.processLoopDidChangeOperation();

        }else{

            this.configureStartData();

            //check , is there any loop in  background
            if (!this.processThreadIsRunning){

                this.backgroundLoop = new Thread("URM_BACKGROUND_PROCESS_LOOP"){

                    @Override
                    public void run(){

                        System.out.println(backgroundLoop.getName() + " STARTED ");

                        while (processThreadIsRunning){

                            //check for stop signal
                            if(!isNeedToHaltThread){

                                //events
                                performCurrentOperation();

                                if (isNextStep()){
                                    nextStep();
                                }else {
                                    processThreadIsRunning = false;
                                    //completed successfully

                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {

                                            delegate.processLoopFinishedRunning();
                                        }
                                    });

                                }

                            }else{

                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {

                                        delegate.processLoopFinishedRunning();
                                    }
                                });

                                break;
                            }
                        }

                        System.out.println(backgroundLoop.getName() + " HALT ");
                    }

                };

                this.processThreadIsRunning = true;
                this.backgroundLoop.start();

            }else{
                //assert error
            }



        }
    }

    public void stopEventLoop(){

        if (this.backgroundLoop != null) {
            this.isNeedToHaltThread = true;
        }else {

            this.isProcessRunInDebug = false;
        }

    }

    public void performCurrentOperation(){

        if (this.operations.size() > this.datasource.getCurrentOperation()){

            this.operations.get(this.datasource.getCurrentOperation()).performOperation();
        }else {
            //assert error
        }

    }

    public void performCurrentOperationInMain(){

        if (this.operations.size() > this.datasource.getCurrentOperation()){

            this.operations.get(this.datasource.getCurrentOperation()).performOperationInMain();
        }else {
            //assert error
        }

    }

    public void nextStep(){

        if (this.isNextStep()){

            this.datasource.setCurrentOperation(this.datasource.getCurrentOperation()+1);

        }else{
            //assert error
        }

    }

    public void debugOperationAndNextStep(){


        this.performCurrentOperationInMain();

        if (isNextStep()){
            nextStep();
        }else {

            //completed successfully
            this.isProcessRunInDebug = false;
            delegate.processLoopFinishedRunning();
        }

    }

    //private
    private void configureStartData(){

        if (this.datasource != null){
            this.operations = this.datasource.getOperationsForProcessLoop();
        }

        this.datasource.setCurrentOperation(0);
//        currentOperationIndex = 0;
    }

    private boolean isNextStep(){

        if (this.datasource.getCurrentOperation() + 1 >= this.operations.size()){
            return false;
        }else {
            return true;
        }

    }


}
