package urm.Controllers;

import com.sun.javafx.scene.control.behavior.ScrollBarBehavior;
import com.sun.javafx.scene.control.skin.ListViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.sun.org.apache.xerces.internal.dom.events.EventImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.controlsfx.control.PopOver;
import org.fxmisc.richtext.*;
import sun.plugin.com.DispatchImpl;
import urm.Main;
import urm.Presenters.EditorPresenter;
import urm.Utilities.*;
import urm.Views.RegisterListCell;

//import java;
import java.net.URL;
//import org.fxmisc.richtext
import java.time.format.TextStyle;
import java.util.*;

enum EditorStates{

    MayBeAutoCompleted,
    Editing,
    NonDefined
}

public class Controller implements Initializable , InvalidationListener , CodeManagerDelegate{


    //MARK: VIPER
    public EditorControllerPresenterInterface presenter;

    //MARK: OUTLETS
    @FXML
    public CodeArea codeArea;

    @FXML
    public AnchorPane stage;

    @FXML
    public ListView listView;

    @FXML
    public void onKeyPressed(KeyEvent keyEvent){

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
    public void playButtonPressed(){

        CodeManager.sharedManager().setupManagerWithText(this.codeArea.getText());
    }

    @FXML
    public void stepButtonPressed(){

    }

    @FXML
    public void stopButtonPressed(){

    }

    @FXML
    public void resetButtonPressed(){


    }

    //MARK: PROPERTY'S
    private PopOver autocompletionAlertPopover;
    private EditorStates editorState = EditorStates.NonDefined;


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

        stage.getStylesheets().add(Controller.class.getResource("textAreaStyle.css").toExternalForm());

        this.presenter = new EditorPresenter();
        this.presenter.setView(this);

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
//        codeArea.setSty

        codeArea.textProperty().addListener(this);
        codeArea.caretPositionProperty().addListener(this);




        Main.stage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {

                for (Node node: listView.lookupAll(".scroll-bar")){

                    if (node instanceof ScrollBar) {
                        final ScrollBar bar = (ScrollBar) node;
                        bar.valueProperty().addListener(new ChangeListener<Number>() {
                            @Override public void changed(ObservableValue<? extends Number> value, Number oldValue, Number newValue) {
                                System.out.println(bar.getOrientation() + " " + newValue);
                                presenter.registersDidScroll();
                            }
                        });
                    }

                    System.out.println(node);

                }

            }
        });




        this.listView.setOnScroll(new javafx.event.EventHandler<ScrollEvent>() {

            @Override
            public void handle(ScrollEvent event) {

                presenter.registersDidScroll();

                System.out.println(event);
            }
        });

        System.out.println( this.listView.getOnScroll());

        this.presenter.viewInitializationCompeted();

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

    //MARK: text highlighting

    public void highlightTextWithStyleInIndexRow(ArrayList<IndexRange> rangesOfProblemRow , String style) {

        int lastKwEnd = 0;

        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        for(int counter = 0 ; counter < rangesOfProblemRow.size() ; counter++ ){

            IndexRange range = rangesOfProblemRow.get(counter);

            spansBuilder.add(Collections.<String>emptyList(), range.getStart() - lastKwEnd);
            spansBuilder.add(Collections.singleton("style"), range.getEnd() - range.getStart());
            lastKwEnd = range.getEnd();
        }

        spansBuilder.add(Collections.<String>emptyList(), codeArea.getLength() - lastKwEnd);
    }

    public void highlightTextAsErrorInIndexRows(ArrayList<IndexRange> rangesOfProblemRow ){

        this.highlightTextWithStyleInIndexRow( rangesOfProblemRow , "error" );

    }

    public void highlightTextAsWorkOperationInIndexRows(ArrayList<IndexRange> rangesOfProblemRow ){

        this.highlightTextWithStyleInIndexRow( rangesOfProblemRow , "operation" );

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

        this.createAndShowDetachablePopupWithText(errorDescription);

    }
}
