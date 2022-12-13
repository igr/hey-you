
# just displays the receipes
default:
    @just --list

opencv_install:
	./build_opencv.sh

train:
	./gradlew train

run:
	./gradlew run