package urm.Presenters;

import com.sun.javafx.scene.control.skin.ListViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.collections.FXCollections;
import javafx.scene.control.IndexRange;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import urm.Controllers.Controller;
import urm.Controllers.EditorControllerPresenterInterface;
import urm.Interactors.EditorInteractor;
import urm.Interactors.EditorInteractorPresenterDelegate;
import urm.Utilities.Register;
import urm.Views.RegisterListCell;

import java.util.ArrayList;

/**
 * Created by Дмитрий on 14.08.2016.
 */
public class EditorPresenter
        implements
        EditorControllerPresenterInterface ,
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
    public void playButtonPressed() {

        this.interactor.startMachine(this.view.codeArea.getText() , true);
    }

    @Override
    public void stepButtonPressed() {

    }

    @Override
    public void stopButtonPressed() {

    }

    @Override
    public void resetButtonPressed() {

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
}
