package urm.Controllers;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.controlsfx.control.PopOver;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpansBuilder;
import urm.Main;
import urm.Presenters.EditorPresenter;
import urm.Utilities.CompletionDatasource;
import urm.Utilities.StringExtension;

import java.io.*;
import java.net.URL;
import java.util.*;

//import java;
//import org.fxmisc.richtext

enum EditorStates{
    MayBeAutoCompleted,
    Editing,
    NonDefined
}

public class Controller implements Initializable, InvalidationListener {

    private boolean isCheckCode = false;
    private boolean isStopedProgramm = false;
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
    public void compileButtonPressed(){

        this.createInformationPopup("Info");
        this.isCheckCode = true;
        this.isStopedProgramm = false;
        //call compile , it means get get code
        //test it for errors
        //prepare rows , rows indexes and operation

        this.presenter.compileButtonPressed();

    }

    @FXML
    public void playButtonPressed(){
        if(isCheckCode||this.isStopedProgramm) {
            this.highlightTextWithStyleInIndexRow(new ArrayList<IndexRange>(), "none");
            this.presenter.playButtonPressed();
            this.isStopedProgramm = false;
        }
    }

    @FXML
    public void stepButtonPressed(){
        if(this.isCheckCode&!this.isStopedProgramm) {
            this.presenter.stepButtonPressed();
        }
    }

    @FXML
    public void stopButtonPressed(){
        if(this.isCheckCode) {
            this.presenter.stopButtonPressed();
            this.isStopedProgramm = true;
        }

    }

    @FXML
    public void resetButtonPressed(){

        this.presenter.resetButtonPressed();
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
                            @Override
                            public void changed(ObservableValue<? extends Number> value, Number oldValue, Number newValue) {
                                System.out.println(bar.getOrientation() + " " + newValue);
                                presenter.registersDidScroll();
                            }
                        });
                    }

                    System.out.println(node);

                }

            }
        });




        this.listView.setOnScroll(new EventHandler<ScrollEvent>() {

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

    public void  createPopupForProgramFinished(){

        createInformationPopup("Your Program did finshed");
    }

    private void createInformationPopup(String message){

//        org.controlsfx.dialog.Dialog dialog = new Dialog(this.stage , "Information");
//
//        dialog.setContent("\nmessage\n");;
//
//        dialog.show();

        WizardPane page1 = new WizardPane();
        page1.setContentText(message);

        // create wizard
        Wizard wizard = new Wizard();

        // create and assign the flow
        wizard.setFlow(new Wizard.LinearFlow(page1));

        // show wizard and wait for response
        wizard.showAndWait();

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
            spansBuilder.add(Collections.singleton(style), range.getEnd() - range.getStart());
            lastKwEnd = range.getEnd();
        }

        spansBuilder.add(Collections.<String>emptyList(), codeArea.getLength() - lastKwEnd >= 0 ? codeArea.getLength() - lastKwEnd : 0 );

        this.codeArea.setStyleSpans(0 , spansBuilder.create());
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
        String character = String.valueOf(this.codeArea.getText().charAt(caretPosition - 1));

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

        this.isCheckCode = false;
        //get row
        String row = StringExtension.getTextRowWithCharAtIndex(this.codeArea.getText(), this.codeArea.getCaretPosition());
//        System.out.println(row);

        //get cursor position at raw
        int cursorPosition = StringExtension.getCursorPositionAtRowWithText(this.codeArea.getText(), row, this.codeArea.getCaretPosition());
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

    //            |
    //ADDed Denis V
    @FXML
    public void openButtonPressed(){



        String curCode = this.codeArea.getText();
        if(curCode.length() == 0) {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showOpenDialog(new Stage());
            if(file != null){
                String s = this.readFile(file.getPath());
                codeArea.insertText(0, s);
            }
            /*JFileChooser c = new JFileChooser();
            int rVal = c.showOpenDialog(new JFrame());
            if (rVal == JFileChooser.APPROVE_OPTION) {
                String path = c.getCurrentDirectory().toString() + c.getSelectedFile().getName();
                String s = this.readFile(path);
                codeArea.insertText(0, s);
            }*/
        }else {
            textInCodeArea();
        }
    }

    public void textInCodeArea(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("attention, you have not saved your code");
        alert.setHeaderText("for to save the current code, click \"save\"\n" +
                "if not needed, click \"open\"");
        alert.setContentText("Choose your option.");

        ButtonType buttonSave = new ButtonType("save");
        ButtonType buttonOpen = new ButtonType("open");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonSave, buttonOpen,  buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonSave){
            this.saveButtonPressed();
            this.openButtonPressed();
        } else if (result.get() == buttonOpen) {
            this.codeArea.deleteText(0,codeArea.getText().length());
            this.openButtonPressed();
        }

    }

    private String readFile(String path){
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader( new File(path).getAbsoluteFile()));
            try {
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } finally {
                in.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
    private void writeFile(String path, String text)throws IOException {
        PrintWriter out = new PrintWriter(new File(path).getAbsoluteFile());
        try {
            char code[] = text.toCharArray();
            for (int i = 0; i < code.length; i++) {
                if(code[i]=='\n')
                    out.print(System.lineSeparator());
                else out.print(code[i]);
            }
        } finally {
            out.close();
        }
    }

    @FXML
    public void saveButtonPressed(){

        String myCode = this.codeArea.getText();
        //System.out.println(myCode.length());
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(new Stage());
        if(file != null) {
            try {
                writeFile(file.getPath(), myCode);
            }catch (IOException e){}
        }

    }

    @FXML
    public void helpButtonPressed(){
        if (this.autocompletionAlertPopover == null || !this.autocompletionAlertPopover.isShowing()){

            PopOver popOver = new PopOver();

            popOver.setDetachable(false);
            Label text;
            text = new Label();

            text.setPrefSize(400,470);
            String helpText = readFile("src\\help.txt");
            text.setText(helpText);
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


}
