package com.oblac.hy;

import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Recognizes users.
 */
public class UserRecognizer {

	public static Size TRAIN_FACE_IMAGE_SIZE = new Size(160, 160);

	private final FaceRecognizer faceRecognizer;
	private final WeightedStandardPixelTrainer weightedStandardPixelTrainer;

	public UserRecognizer() {
		faceRecognizer = LBPHFaceRecognizer.create();
		//faceRecognizer = EigenFaceRecognizer.create();

		weightedStandardPixelTrainer = new WeightedStandardPixelTrainer();
	}

	public void train() {
		List<User> users = User.allUsers();

		System.out.println("Training face recognizer...");

		// experience file
		weightedStandardPixelTrainer.loadTrainedData("data/gender.txt");


		List<Mat> faces = new ArrayList<>();
		List<Integer> labels = new ArrayList<>();

		users.forEach(user -> {
			System.out.println("\t" + user.name() + " " + user.id());

			user.
				listFaceFiles()
				.stream()
				.peek(file -> System.out.println(file.getName()))
				.map(faceUserFile ->
					Imgcodecs.imread(faceUserFile.getAbsolutePath(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE))
				.peek(face ->
					Imgproc.resize(face, face, TRAIN_FACE_IMAGE_SIZE)
				)
				.forEach(userFaceMat -> {
					faces.add(userFaceMat);
					labels.add(user.id());
				});
		});

		MatOfInt allLabels = new MatOfInt(new int[labels.size()]);
		for (int i = 0; i < labels.size(); i++) {
			allLabels.put(i, 0, labels.get(i));
		}
		faceRecognizer.train(faces, allLabels);

		System.out.println("Training complete.");
	}

	/**
	 * Recognizes user based on his detected face. Also performs gender detection.
	 */
	public RecognizedUser matchFace(Mat face) {
		Imgproc.cvtColor(face, face, Imgproc.COLOR_BGR2GRAY);
		Imgproc.resize(face, face, TRAIN_FACE_IMAGE_SIZE);

		int[] label = {0};
		double[] confidence = {0};

		faceRecognizer.predict(face, label, confidence);

		int gender = weightedStandardPixelTrainer.predict(face);

		if (confidence[0] < 100) {
			return new RecognizedUser(label[0], confidence[0], gender);
		}

		return new RecognizedUser(-1, 0, -1);
	}
}
