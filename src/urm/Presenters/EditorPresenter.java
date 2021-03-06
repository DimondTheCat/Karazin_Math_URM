package urm.Presenters;

import com.sun.javafx.scene.control.skin.ListViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.IndexRange;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import urm.Controllers.Controller;
import urm.Controllers.EditorControllerPresenterInterface;
import urm.Interactors.EditorInteractor;
import urm.Interactors.EditorInteractorPresenterDelegate;
import urm.Utilities.CodeManager;
import urm.Utilities.Register;
import urm.Views.RegisterListCell;

import java.util.ArrayList;

/**
 * Created by Дмитрий on 14.08.2016.
 */
public class EditorPresenter
        implements
        EditorControllerPresenterInterface,
        EditorInteractorPresenterDelegate {

    //MARK: CONSTRUCTORS

    public EditorPresenter(){
        super();

        interactor = new EditorInteractor();
        interactor.presenter = this;

    }

    //MARK: VIPER
    public Controller view;
    public EditorInteractor interactor;


    //MARK: VIEW DELEGAT
    @Override
    public void setView(Controller view){

        this.view = view;
    }

    @Override
    public void viewInitializationCompeted() {

        this.updateData();

        this.view.listView.setCellFactory(new Callback<ListView<Register>, ListCell<Register>>() {

            public ListCell<Register> call(ListView<Register> p) {

                RegisterListCell cell = new RegisterListCell();

                return cell;


            }

        });
    }

    @Override
    public void compileButtonPressed() {

        this.interactor.buildCode(this.view.codeArea.getText());;
    }

    @Override
    public void playButtonPressed() {

        this.interactor.runProgramm();
    }


    @Override
    public void stepButtonPressed() {

        this.interactor.performOneStepOfProgramm();
    }

    @Override
    public void stopButtonPressed() {

        this.interactor.stopProgramm();
    }

    @Override
    public void resetButtonPressed() {
        ObservableList registersData = this.interactor.registersData;
        for(int i = 0; i < registersData.size();i++){
            Register register = (Register)registersData.get(i);
            register.setValue(0);
            registersData.set(i,register);
        }
        interactor.presenter.registersDataDidUpdated();
    }

    @Override
    public void registersDidScroll() {

        ListViewSkin<?> skin = (ListViewSkin<?>)this.view.listView.getSkin();
        VirtualFlow<?> flow = (VirtualFlow<?>)skin.getChildren().get(0);

        int last = flow.getLastVisibleCell().getIndex();

        if (last >= this.interactor.registersData.size()-1){
            //update
            this.interactor.appendDataToIndex(this.interactor.registersData.size()+30);
        }

    }

    public void updateData(){

        this.interactor.registersData = FXCollections.observableArrayList();

        this.interactor.registersData.addAll(Register.createEmptyRegisters(30));

        this.view.listView.setItems(this.interactor.registersData);

    }

    //MARK: INTERACTOR

    @Override
    public void registersDataDidUpdated() {

    }

    @Override
    public void reseavedError(String errorDescription, IndexRange problemRange) {

        ArrayList<IndexRange> ranges = new ArrayList<>();
        ranges.add(problemRange);

        this.view.highlightTextAsErrorInIndexRows(ranges);
    }

    @Override
    public void updateCurrentOperation(){

        IndexRange range = CodeManager.sharedManager().rowsRanges.get(this.interactor.getCurrentOperation());
        ArrayList<IndexRange> ranges = new ArrayList<>();
        ranges.add(range);

        this.view.highlightTextAsWorkOperationInIndexRows(ranges);
    }

    @Override
    public void programDidFinished() {

        this.view.createPopupForProgramFinished();

        updateRegistersUI();
    }

    public void updateRegistersUI(){

        for (Object obj : this.interactor.registersData) {

            if (obj instanceof Register){
                Register reg = (Register)obj;
                reg.setValue(reg.value);
            }

        }
    }
}
