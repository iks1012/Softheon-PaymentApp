package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.opencv.core.*;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import javax.naming.Name;
import java.io.IOException;
import java.util.*;

public class Main extends Application {

    public String project_ID = "30da8cac-12bf-44f3-bcbd-85a9e30c8921";
    public String project_secret = "27a7e9ae-0f37-4485-84e1-f5475a62357a";

    Pane appPane;
    ListView paymentHistoryList;
    Button payButton;
    static String accessToken;

    TextField creditCardEntry;
    ChoiceBox cardType;
    TextField cvvEntry;
    TextField expMonthEntry;
    TextField expDateEntry;

    String cardHolderName = "";
    String address1 = "";
    String address2 = "";
    String city = "";
    String state = "";
    String zipCode = "";
    String email = "";
    String referenceID = "";

    TextField nameField ;
    TextField addressLine1;
    TextField addressLine2;
    TextField cityField;
    TextField stateField;
    TextField zipCodeField;
    TextField emailField;
    TextField referenceIDField;



    boolean toPay = false;

    // the FXML area for showing the current frame
    private ImageView originalFrame;
    // checkbox for selecting the Haar Classifier
    private CheckBox haarClassifier;
    // checkbox for selecting the LBP Classifier
    private CheckBox lbpClassifier;
    // a timer for acquiring the video stream
    private Timer timer;
    // the OpenCV object that performs the video capture
    private VideoCapture capture;
    // a flag to change the button behavior
    private boolean cameraActive;
    // the face cascade classifier object
    private CascadeClassifier faceCascade;
    // minimum face size
    private int absoluteFaceSize;
    private Image CamStream;


    ObservableList<String> paymentHistory = FXCollections.observableArrayList(
            "");
    @Override
    public void start(Stage primaryStage) throws Exception{

        accessToken = getAccessToken();
        setupGUI(primaryStage);
        setupControllers();
    }

    public void setupControllers(){
        payButton.setOnAction(e->{
            //Verify face!

            //capture = new VideoCapture();
            //faceCascade = new CascadeClassifier();
            //absoluteFaceSize = 0;

            cameraActive = false;
            //startCamera();

            payButton.setDisable(true);

            //Check for info




            //make sure all the args are in check

            payButton.setDisable(false);


        });

        haarClassifier.setOnAction(e -> {
            //haarSelected();
        });

        lbpClassifier.setOnAction(e -> {
            //lbpSelected();
        });
    }

    public String getAccessToken(){
        String token = "";

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("https://hack.softheon.io/oauth2/connect/token/");
        String encodedString = Base64.getEncoder().encodeToString((project_ID+":"+project_secret).getBytes());
        //System.out.println("Encoded String: "+encodedString);

        List<NameValuePair> arguments = new ArrayList<>(2);
        arguments.add(new BasicNameValuePair("grant_type", "client_credentials"));
        arguments.add(new BasicNameValuePair("scope", "paymentapi"));

        try {
            post.addHeader("Authorization","Basic "+encodedString);
            post.addHeader("Accept","application/json");
            post.addHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setEntity(new UrlEncodedFormEntity(arguments));
            HttpResponse response = client.execute(post);


            // Print out the response message
            String rawData = (EntityUtils.toString(response.getEntity()));
            //System.out.println("Rawdata: "+rawData);

            token = rawData.substring(17, rawData.indexOf('"',17));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return token;
    }



    public void setupGUI(Stage primaryStage){
        appPane = new Pane();
        HBox mainDivider = new HBox();

        originalFrame = new ImageView();
        haarClassifier = new CheckBox();
        lbpClassifier = new CheckBox();


        timer = new Timer();
        //capture = new VideoCapture();


        payButton = new Button("Pay");

        paymentHistoryList = new ListView(paymentHistory);
        paymentHistoryList.setPrefWidth(400);
        paymentHistoryList.setPrefHeight(1080);

        mainDivider.getChildren().add(paymentHistoryList);

        VBox rightContainer = new VBox();
        //add the video to the rightContainer
        rightContainer.setAlignment(Pos.CENTER);

        rightContainer.getChildren().add(originalFrame);

        VBox buttonControls = new VBox();
        buttonControls.setAlignment(Pos.CENTER);

        VBox personDetails = new VBox();

        VBox nameBox = new VBox();
        Label nameLabel = new Label("Name: ");
        nameLabel.setMinWidth(200);
        nameLabel.setPrefWidth(200);
        nameLabel.setMaxWidth(200);
        nameField = new TextField();
        nameField.setMinWidth(200);
        nameField.setPrefWidth(200);
        nameField.setMaxWidth(200);
        nameBox.getChildren().add(nameLabel);
        nameBox.getChildren().add(nameField);

        VBox addressBox = new VBox();
        Label addressLabel = new Label("Address: ");
        addressLabel.setMinWidth(200);
        addressLabel.setPrefWidth(200);
        addressLabel.setMaxWidth(200);
        addressLine1 = new TextField();
        addressLine2 = new TextField();
        addressLine1.setMinWidth(200);
        addressLine1.setPrefWidth(200);
        addressLine1.setMaxWidth(200);
        addressLine2.setMinWidth(200);
        addressLine2.setPrefWidth(200);
        addressLine2.setMaxWidth(200);
        addressBox.getChildren().add(addressLabel);
        addressBox.getChildren().add(addressLine1);
        addressBox.getChildren().add(addressLine2);

        VBox cityBox = new VBox();
        Label cityLabel = new Label("City: ");
        cityLabel.setMinWidth(200);
        cityLabel.setPrefWidth(200);
        cityLabel.setMaxWidth(200);
        cityField = new TextField();
        cityField.setMinWidth(200);
        cityField.setPrefWidth(200);
        cityField.setMaxWidth(200);
        nameBox.getChildren().add(cityLabel);
        nameBox.getChildren().add(cityField);

        VBox stateBox = new VBox();
        Label stateLabel = new Label("state: ");
        stateLabel.setMinWidth(200);
        stateLabel.setPrefWidth(200);
        stateLabel.setMaxWidth(200);
        stateField = new TextField();
        stateField.setMinWidth(200);
        stateField.setPrefWidth(200);
        stateField.setMaxWidth(200);
        nameBox.getChildren().add(nameLabel);
        nameBox.getChildren().add(nameField);








        VBox cardTypeBox = new VBox();
        Label cardTypeLabel = new Label("Credit Card Number: ");
        cardTypeLabel.setMinWidth(200);
        cardTypeLabel.setPrefWidth(200);
        cardTypeLabel.setMaxWidth(200);
        cardType = new ChoiceBox(FXCollections.observableArrayList(
                "Visa", "American Express", "Discover", "Mastercard"
        ));
        cardType.setMinWidth(200);
        cardType.setPrefWidth(200);
        cardType.setMaxWidth(200);
        cardTypeBox.getChildren().add(cardTypeLabel);
        cardTypeBox.getChildren().add(cardType);





        VBox creditCardNumber = new VBox();
        Label creditCardLabel = new Label("Credit Card Number: ");
        creditCardLabel.setMinWidth(200);
        creditCardLabel.setPrefWidth(200);
        creditCardLabel.setMaxWidth(200);
        creditCardEntry = new TextField();
        creditCardEntry.setEditable(true);
        creditCardEntry.setMinWidth(200);
        creditCardEntry.setPrefWidth(200);
        creditCardEntry.setMaxWidth(200);
        creditCardNumber.getChildren().add(creditCardLabel);
        creditCardNumber.getChildren().add(creditCardEntry);

        VBox cvv = new VBox();
        Label cvvLabel = new Label("CVV: ");
        cvvEntry = new TextField();
        cvvLabel.setMinWidth(200);
        cvvLabel.setPrefWidth(200);
        cvvLabel.setMaxWidth(200);
        cvvEntry.setEditable(true);
        cvvEntry.setMinWidth(200);
        cvvEntry.setPrefWidth(200);
        cvvEntry.setMaxWidth(200);
        cvv.getChildren().add(cvvLabel);
        cvv.getChildren().add(cvvEntry);



        VBox expMonth = new VBox();
        Label expMonthLabel = new Label("MM: ");
        expMonthLabel.setMinWidth(200);
        expMonthLabel.setPrefWidth(200);
        expMonthLabel.setMaxWidth(200);
        expMonthEntry = new TextField();
        expMonthEntry.setMinWidth(200);
        expMonthEntry.setPrefWidth(200);
        expMonthEntry.setMaxWidth(200);
        expMonth.getChildren().add(expMonthLabel);
        expMonth.getChildren().add(expMonthEntry);




        VBox expYear = new VBox();
        Label expYearLabel = new Label("YYYY");
        expYearLabel.setMinWidth(200);
        expYearLabel.setPrefWidth(200);
        expYearLabel.setMaxWidth(200);
        expDateEntry = new TextField();
        expDateEntry.setMinWidth(200);
        expDateEntry.setPrefWidth(200);
        expDateEntry.setMaxWidth(200);
        expYear.getChildren().add(expYearLabel);
        expYear.getChildren().add(expDateEntry);


        VBox cardInfo = new VBox();
        cardInfo.setSpacing(5);
        cardInfo.getChildren().add(cardTypeBox);
        cardInfo.getChildren().add(creditCardNumber);
        cardInfo.getChildren().add(cvv);
        cardInfo.getChildren().add(expMonth);
        cardInfo.getChildren().add(expYear);

        HBox allInfo = new HBox();
        allInfo.getChildren().add(personDetails);
        allInfo.getChildren().add(cardInfo);
        buttonControls.getChildren().add(allInfo);
        buttonControls.getChildren().add(payButton);
        rightContainer.getChildren().add(buttonControls);
        mainDivider.getChildren().add(rightContainer);

        appPane.getChildren().add(mainDivider);
        primaryStage.setTitle("Softheon Peer-to-Peer Payment");
        primaryStage.setScene(new Scene(new Group(appPane), 1920, 1080));
        primaryStage.show();
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    /*

    protected void startCamera() {
        if (!cameraActive) {
            // disable setting checkboxes
            haarClassifier.setDisable(true);
            lbpClassifier.setDisable(true);
            //capture = new VideoCapture();
            // start the video capture
            capture.open(0);

            // is the video stream available?
            if (capture.isOpened()) {
                cameraActive = true;

                // grab a frame every 33 ms (30 frames/sec)
                TimerTask frameGrabber = new TimerTask() {
                    @Override
                    public void run() {
                        CamStream = grabFrame();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                                // show the original frames
                                originalFrame.setImage(CamStream);
                                // set fixed width
                                originalFrame.setFitWidth(600);
                                // preserve image ratio
                                originalFrame.setPreserveRatio(true);

                            }
                        });
                    }
                };
                timer = new Timer();
                timer.schedule(frameGrabber, 0, 33);

                // update the button content
                exitPayButton.setText("Stop Camera");
            }
            else {
                // log the error
                System.err.println("Failed to open the camera connection...");
            }
        }
        else {
            // the camera is not active at this point
            cameraActive = false;
            // update again the button content
            exitPayButton.setText("Start Camera");
            // enable setting checkboxes
            haarClassifier.setDisable(false);
            lbpClassifier.setDisable(false);

            // stop the timer
            if (timer != null)
            {
                timer.cancel();
                timer = null;
            }
            // release the camera
            capture.release();
            // clean the image area
            originalFrame.setImage(null);
        }
    }
    */



    /**
     * Get a frame from the opened video stream (if any)
     *
     * @return the {@link Image} to show
     */
    /*private Image grabFrame() {
        // init everything
        Image imageToShow = null;
        Mat frame = new Mat();

        // check if the capture is open
        if (capture.isOpened())
        {
            try
            {
                // read the current frame
                capture.read(frame);

                // if the frame is not empty, process it
                if (!frame.empty())
                {
                    // face detection
                    detectAndDisplay(frame);

                    // convert the Mat object (OpenCV) to Image (JavaFX)
                    imageToShow = mat2Image(frame);
                }

            }
            catch (Exception e)
            {
                // log the (full) error
                System.err.print("ERROR");
                e.printStackTrace();
            }
        }

        return imageToShow;
    }
    */


    /**
     * Perform face detection and show a rectangle around the detected face.
     *
     * @param frame
     *            the current frame
     *//*
    private void detectAndDisplay(Mat frame) {
        // init
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        // convert the frame in gray scale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayFrame, grayFrame);

        // compute minimum face size (20% of the frame height)
        if (absoluteFaceSize == 0)
        {
            int height = grayFrame.rows();
            if (Math.round(height * 0.2f) > 0)
            {
                absoluteFaceSize = Math.round(height * 0.2f);
            }
        }

        // detect faces
        faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE, new Size(
                absoluteFaceSize, absoluteFaceSize), new Size());

        // each rectangle in faces is a face
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);

    }

    /**
     * When the Haar checkbox is selected, deselect the other one and load the
     * proper XML classifier
     *

    protected void haarSelected() {
        // check whether the lpb checkbox is selected and deselect it
        if (lbpClassifier.isSelected())
            lbpClassifier.setSelected(false);

        //checkboxSelection("resources/haarcascades/haarcascade_frontalface_alt.xml");
    }*/

    /**
     *
     When the LBP checkbox is selected, deselect the other one and load the
     * proper XML classifier
     *//*
    protected void lbpSelected() {
        // check whether the haar checkbox is selected and deselect it
        if (haarClassifier.isSelected())
            haarClassifier.setSelected(false);

        //checkboxSelection("resources/lbpcascades/lbpcascade_frontalface.xml");
    }*/

    /**
     * Common operation for both checkbox selections
     *
     * @param classifierPath
     *            the absolute path where the XML file representing a training
     *            set for a classifier is present
     */
    /*
    private void checkboxSelection(String... classifierPath) {
        // load the classifier(s)
        for (String xmlClassifier : classifierPath) {
            faceCascade.load(xmlClassifier);
        }

        // now the capture can start
        exitPayButton.setDisable(false);
    }*/

    /**
     * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
     *
     * @param frame
     *            the {@link Mat} representing the current frame
     * @return the {@link Image} to show
     *//*
    private Image mat2Image(Mat frame) {
        // create a temporary buffer
        MatOfByte buffer = new MatOfByte();
        // encode the frame in the buffer, according to the PNG format
        Imgcodecs.imencode(".png", frame, buffer);
        // build and return an Image created from the image encoded in the
        // buffer
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }*/

    public static void main(String[] args) {
        launch(args);
    }
}
