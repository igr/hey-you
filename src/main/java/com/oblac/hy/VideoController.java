package com.oblac.hy;

import com.oblac.hy.ui.Histogram;
import com.oblac.hy.ui.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The controller associated with the only view of our application.
 */
public class VideoController {
	@FXML
	private Button button;
	@FXML
	private CheckBox grayscale;
	@FXML
	private ImageView histogramImageView;
	@FXML
	private ImageView currentFrame;

	private ScheduledExecutorService timer;     // a timer for acquiring the video stream
	private VideoCapture capture;               // the OpenCV object that realizes the video capture
	private boolean cameraActive;               // a flag to change the button behavior
	private Mat logo;                           // the logo to be loaded

	private Histogram histogram;
	private FaceDetector faceDetector;
	private UserRecognizer userRecognizer;

	/**
	 * Initialize method, automatically called by @{link FXMLLoader}.
	 */
	public void initialize() {
		this.capture = new VideoCapture();
		this.cameraActive = false;

		this.histogram = new Histogram();

		this.faceDetector = new FaceDetector();
		this.userRecognizer = new UserRecognizer();

		userRecognizer.train();

		this.logo = Imgcodecs.imread("src/main/resources/logo.png");
	}

	/**
	 * The action triggered by pushing the button on the GUI.
	 */
	@FXML
	protected void startCamera() {
		this.currentFrame.setFitWidth(600);
		this.currentFrame.setPreserveRatio(true);

		if (!this.cameraActive) {
			// start the video capture
			this.capture.open(0);

			if (this.capture.isOpened()) {      // is the video stream available?
				this.cameraActive = true;

				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = () -> {
					// effectively grab and process a single frame
					Mat frame = grabFrame();

					// convert and show the frame
					Image imageToShow = Utils.mat2Image(frame);
					updateImageView(currentFrame, imageToShow);
				};

				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

				// update the button content
				this.button.setText("Stop Camera");
			} else {
				// log the error
				System.err.println("Camera connection error...");
			}
		} else {
			this.cameraActive = false;
			this.button.setText("Start Camera");
			this.stopAcquisition();
		}
	}

	/**
	 * Get a frame from the opened video stream (if any).
	 */
	private Mat grabFrame() {
		Mat frame = new Mat();

		if (this.capture.isOpened()) {
			try {
				this.capture.read(frame);

				if (!frame.empty()) {

					this.detectAndDisplay(frame);

					// add a logo...
					Rect roi = new Rect(frame.cols() - logo.cols(), frame.rows() - logo.rows(), logo.cols(), logo.rows());
					Mat imageROI = frame.submat(roi);
					Core.addWeighted(imageROI, 1.0, logo, 0.8, 0.0, imageROI);

					// to grayscale
					if (grayscale.isSelected()) {
						Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
					}

					// show the histogram
					Image histImg = histogram.makeHistogram(frame, grayscale.isSelected());
					updateImageView(histogramImageView, histImg);
				}
			} catch (Exception e) {
				System.err.println("Frame elaboration error: " + e);
				e.printStackTrace();
			}
		}

		return frame;
	}

	/**
	 * Stop the acquisition from the camera and release all the resources.
	 */
	private void stopAcquisition() {
		if (this.timer != null && !this.timer.isShutdown()) {
			try {
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				System.err.println("Exception in stopping the frame capture " + e);
			}
		}

		if (this.capture.isOpened()) {
			this.capture.release();
		}
	}

	/**
	 * Update the {@link ImageView} in the JavaFX main thread.
	 */
	private void updateImageView(ImageView view, Image image) {
		Utils.onFXThread(view.imageProperty(), image);
	}

	/**
	 * On application close, stop the acquisition from the camera.
	 */
	protected void setClosed() {
		this.stopAcquisition();
	}


	private void detectAndDisplay(Mat frame) {
		List<Rect> faces = faceDetector.detectFace(frame);

		faces.forEach(face -> {
			Rect cropFace = new Rect(face.x, face.y, face.width, face.height);

			Mat cropedFace = new Mat(frame, cropFace);

			RecognizedUser recognizedUser = userRecognizer.matchFace(cropedFace);

			User.findUserById(recognizedUser.getLabel(), user -> {
				Imgproc.rectangle(frame, face.tl(), face.br(), COLOR_SUCCESS, 2);

				Imgproc.rectangle(frame,
					new Point(face.x, face.y + face.height),
					new Point(face.x + face.width, face.y + face.height + 34),
					COLOR_SUCCESS, -1);

				String text = user.name();
				switch (recognizedUser.getGenederType()) {
					case 0: text += " (F)"; break;
					case 1: text += " (M)"; break;
				}

				Imgproc.putText(frame, text,
					new Point(face.x + 10, face.y + face.height + 24),
					Core.FONT_HERSHEY_PLAIN, 2,
					COLOR_TEXT, 2);
			}, () ->
				Imgproc.rectangle(frame, face.tl(), face.br(), COLOR_UNRECOGNIZED, 4)
			);


		});
	}

	private Scalar COLOR_TEXT = new Scalar(255, 255, 255);
	private Scalar COLOR_SUCCESS = new Scalar(0, 200, 0);
	private Scalar COLOR_UNRECOGNIZED = new Scalar(0, 0, 255);
}