#!/usr/bin/env bash

set -e

#tar xzf opencv-4.6.0.tar.gz
#tar xzf opencv_contrib-4.6.0.tar.gz

readonly DIR=$(pwd)
echo "$DIR"
cd opencv-4.6.0
mkdir -p release
cd release

# use `arm64` for Apple Silicon

cmake \
      -DCMAKE_SYSTEM_PROCESSOR=arm64 \
      -DCMAKE_OSX_ARCHITECTURES=arm64 \
      -DCMAKE_OSX_DEPLOYMENT_TARGET= \
      -DCMAKE_C_FLAGS_RELEASE=-DNDEBUG \
      -DCMAKE_CXX_FLAGS_RELEASE=-DNDEBUG \
      -DCMAKE_INSTALL_PREFIX=$DIR/opencv-4.6.0 \
      -DCMAKE_BUILD_TYPE=Release \
      -DCMAKE_FIND_FRAMEWORK=LAST \
      -DCMAKE_VERBOSE_MAKEFILE=ON \
      -Wno-dev \
      -DBUILD_JASPER=OFF \
      -DBUILD_JPEG=ON \
      -DBUILD_OPENEXR=OFF \
      -DBUILD_PERF_TESTS=OFF \
      -DBUILD_PNG=OFF \
      -DBUILD_TESTS=OFF \
      -DBUILD_opencv_apps=OFF \
      -DBUILD_TIFF=OFF \
      -DBUILD_ZLIB=OFF \
      -DBUILD_opencv_java=ON \
      -DOPENCV_ENABLE_NONFREE=ON \
      -DOPENCV_EXTRA_MODULES_PATH=$DIR/opencv_contrib-4.6.0/modules \
      -DWITH_1394=OFF \
      -DWITH_CUDA=OFF \
      -DWITH_EIGEN=ON \
      -DWITH_FFMPEG=ON \
      -DWITH_GPHOTO2=OFF \
      -DWITH_GSTREAMER=OFF \
      -DWITH_JASPER=OFF \
      -DWITH_OPENEXR=ON \
      -DWITH_OPENGL=OFF \
      -DWITH_QT=OFF \
      -DWITH_TBB=OFF \
      -DWITH_VTK=OFF \
      -DBUILD_opencv_python2=OFF \
      -DBUILD_opencv_python3=OFF \
      -DBUILD_SHARED_LIBS=ON \
      ..

make -j8
make install

echo "Done"