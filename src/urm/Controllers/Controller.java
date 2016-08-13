package urm.Controllers;

import com.sun.javafx.scene.control.skin.ListViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.PopupWindow;
import javafx.util.Callback;
import org.controlsfx.control.PopOver;
import org.fxmisc.richtext.*;
import sun.plugin.com.DispatchImpl;
import urm.Main;
import urm.Utilities.*;
import urm.Views.RegisterListCell;

//import java;
import java.net.URL;
import java.util.*;

enum EditorStates{

    MayBeAutoCompleted,
    Editing,
    NonDefined
}

public class Controller implements Initializable , InvalidationListener , CodeManagerDelegate{

    //MARK: OUTLETS
    @FXML
    InlineCssTextArea codeArea;

    @FXML
    ListView listView;

    @FXML
    void onKeyPressed(KeyEvent keyEvent){

        if (keyEvent.getCode() == KeyCode.ENTER && keyEvent.isAltDown() && this.editorState == EditorStates.MayBeAutoCompleted){
            //auto-completion event
            this.performCompletion();
            this.setEditorState(EditorStates.NonDefined);

        }

        if (!keyEvent.isAltDown()){

            this.setEditorState( EditorStates.NonDefined);
        }


    }

    @FXML
    void playButtonPressed(){

        CodeManager.sharedManager().setupManagerWithText(this.codeArea.getText());
    }

    @FXML
    void stepButtonPressed(){

    }

    @FXML
    void stopButtonPressed(){

    }

    @FXML
    void resetButtonPressed(){


    }

    //MARK: PROPERTY'S
    private PopOver autocompletionAlertPopover;
    private EditorStates editorState = EditorStates.NonDefined;
    public ObservableList data = FXCollections.observableArrayList();

    private void setEditorState(EditorStates editorState) {
        this.editorState = editorState;

        if (editorState == EditorStates.MayBeAutoCompleted){
            this.showAutocompletionPopup();
        }else {
            this.hideAutocompletionPopup();
        }
    }

    //MARK: LIFECICLE
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

        codeArea.textProperty().addListener(this);
        codeArea.caretPositionProperty().addListener(this);

        CodeManager.sharedManager().delegate = this;

        this.listView.setOnScroll(new javafx.event.EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {

                ListViewSkin<?> skin = (ListViewSkin<?>)listView.getSkin();
                VirtualFlow<?> flow = (VirtualFlow<?>)skin.getChildren().get(0);

                int last = flow.getLastVisibleCell().getIndex();

                if (last >= data.size()-1){
                    //update
                    appendDataToIndex(data.size()+30);
                }

                System.out.println(event);
            }
        });


        this.updateData();


        this.listView.setCellFactory(new Callback<ListView<Register>, ListCell<Register>>() {

            public ListCell<Register> call(ListView<Register> p) {

                RegisterListCell cell = new RegisterListCell();

                return cell;


            }

        });

    }

    //MARK: VIEW

    private void showAutocompletionPopup(){

        if (this.autocompletionAlertPopover == null || !this.autocompletionAlertPopover.isShowing()){

            PopOver popOver = new PopOver();
            this.autocompletionAlertPopover = popOver;

            popOver.setDetachable(false);
            Label text;
            text = new Label();
            text.setPrefSize(300,50);
            text.setText(" for auto-completion press alt + enter ");
            popOver.setContentNode(text);
            popOver.setArrowSize(0);
            double x = codeArea.getLayoutX();
            double y = codeArea.getLayoutY();
            popOver.setX(x);
            popOver.setY(y);
            codeArea.setPopupWindow(popOver);

            popOver.show(codeArea.getScene().getWindow());
        }
    }

    private void hideAutocompletionPopup(){

        if (this.autocompletionAlertPopover != null){
            this.autocompletionAlertPopover.hide();
        }
    }

    private void createAndShowDetachablePopupWithText(String text){

        PopOver popOver = new PopOver();
        this.autocompletionAlertPopover = popOver;

        popOver.setDetachable(true);
        Label textLabel;
        textLabel = new Label();
        textLabel.setPrefSize(300,50);
        textLabel.setText(text);
        popOver.setContentNode(textLabel);
        popOver.setArrowSize(0);

        popOver.show(codeArea.getScene().getWindow());

    }

    public void updateData(){

        data = FXCollections.observableArrayList();

        data.addAll(Register.createEmptyRegisters(30));

        this.listView.setItems(data);

    }

    public void appendDataToIndex(int index){

        for (int i = data.size() ; i <= index ; i++ ){

            Register register = new Register();
            register.index = i;
            register.value = 0;

            this.data.add(register);

        }

        this.listView.refresh();
    }

    //MARK: CONTROLLER

    private void performCompletion(){

        int caretPosition = this.codeArea.getCaretPosition();
        String character = String.valueOf(this.codeArea.getText().charAt(caretPosition-1));

        String completion = CompletionDatasource.sharedCompletion().completionValue(character);

        this.codeArea.deleteText(caretPosition-1,caretPosition);
        this.codeArea.insertText(caretPosition-1 , completion);
        this.codeArea.moveTo(caretPosition);



    }

    //MARK: DELEGATES AND OBSERVERS

    //CODE AREA DELEGATE
    @Override
    public void invalidated(Observable observable) {
        //text changed
        if (observable == this.codeArea.textProperty()){

            this.observeTextPropertyOfCodeEditor(observable);
        }else if (observable == this.codeArea.caretPositionProperty()){

            this.observeCaretPositionPropertyOfCodeEditor(observable);
        }

    }

    private void observeTextPropertyOfCodeEditor(Observable observable){


        //get row
        String row = StringExtension.getTextRowWithCharAtIndex(this.codeArea.getText(),this.codeArea.getCaretPosition());
//        System.out.println(row);

        //get cursor position at raw
        int cursorPosition = StringExtension.getCursorPositionAtRowWithText(this.codeArea.getText(), row ,this.codeArea.getCaretPosition());
//        System.out.println(cursorPosition);

        //separate row by comment line and get uncommented line
        int commentSignIndex = row.indexOf("//");
        commentSignIndex = commentSignIndex == -1 ? row.length() : commentSignIndex;

        String uncommentedPart = row.substring(0 , commentSignIndex);
        System.out.println(uncommentedPart);

        //if line contains just one symbol provide auto-complete popup
        String code = uncommentedPart.replaceAll("\\s+","");

        if (code.length() == 1 ){

            if (CompletionDatasource.sharedCompletion().isHaveValueForCompletion(code)){

                //set state to can be auto-completed and show popup
                this.setEditorState(EditorStates.MayBeAutoCompleted); //;

            }

        }
    }

    private void observeCaretPositionPropertyOfCodeEditor(Observable observable){

        if (this.editorState != EditorStates.MayBeAutoCompleted){
            this.setEditorState(EditorStates.NonDefined);
        }
    }

    //CODE MANAGER DELEGATE

    @Override
    public void codeManagerCurrentOperationChanged(CodeManager sender, int operationNumber) {

    }

    @Override
    public void errorWhileParsing(CodeManager sender, String errorDescription, IndexRange rangeOfProblemRow) {

//        ArrayList<String> styles = new ArrayList<>();
//        styles.add();

//        StringListImpl style = new StringListImpl( new String[]{} , 1);

        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        spansBuilder.add(Collections.<String>emptyList() , rangeOfProblemRow.getStart());
        spansBuilder.add(Collections.singleton("error") , rangeOfProblemRow.getEnd());

        this.codeArea.setStyleSpans( rangeOfProblemRow.getStart() , rangeOfProblemRow.getEnd() , "-fx-text-fill: red;" );
//        this.codeArea.setStyleSpans(0 , spansBuilder.create());
//        this.codeArea.setStyle(rangeOfProblemRow.getStart(),rangeOfProblemRow.getEnd() , styles);
        this.createAndShowDetachablePopupWithText(errorDescription);

    }
}
