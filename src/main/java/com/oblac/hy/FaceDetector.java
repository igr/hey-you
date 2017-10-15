package com.oblac.hy;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.util.List;

/**
 * Detect faces.
 */
public class FaceDetector {

	private boolean lbpClassifier = false;
	private boolean haarClassifier = true;
	private CascadeClassifier faceCascade;

	public FaceDetector() {
		this.faceCascade = new CascadeClassifier();

		if (this.lbpClassifier) {
			// 2,5,7
			//this.faceCascade.load("data/lbpcascades/lbpcascade_frontalface.xml");

			// not much
			//this.faceCascade.load("data/lbpcascades/lbpcascade_profileface.xml");
		}
		if (this.haarClassifier) {
			// 2
			//this.faceCascade.load("data/haarcascades/haarcascade_profileface.xml");

			// 1,2,5,7
			//this.faceCascade.load("data/haarcascades/haarcascade_frontalface_extended.xml");
			//this.faceCascade.load("data/haarcascades/haarcascade_frontalface_default.xml");

			// 1,2,5,7
			//this.faceCascade.load("data/haarcascades/haarcascade_frontalface_alt.xml");

			// 2,5
			//this.faceCascade.load("data/haarcascades/haarcascade_frontalface_alt_tree.xml");

			// 1,2,5,7
			this.faceCascade.load("data/haarcascades/haarcascade_frontalface_alt2.xml");
		}
	}

	/**
	 * Detects faces on the input and returns a list of rectangulars around each detected face.
	 */
	public List<Rect> detectFace(final Mat frame) {
		Mat grayFrame = new Mat();

		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

		// equalize the frame histogram to improve the result
		Imgproc.equalizeHist(grayFrame, grayFrame);

		// compute minimum face size (20% of the frame height, in our case)

		int minSize = 0;
		int height = grayFrame.rows();

		if (Math.round(height * 0.2f) > 0) {
			minSize = Math.round(height * 0.2f);
		}

		final MatOfRect detectedFacesRectangulars = new MatOfRect();

		this.faceCascade.detectMultiScale(grayFrame,
			detectedFacesRectangulars,
			1.1,
			1,
			Objdetect.CASCADE_DO_CANNY_PRUNING,
			new Size(minSize, minSize),
			grayFrame.size());

		return detectedFacesRectangulars.toList();
	}

}
