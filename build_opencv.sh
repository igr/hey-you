#!/usr/bin/env bash

set -e

#tar xzf opencv-3.3.0.tar.gz
#tar xzf opencv_contrib-3.3.0.tar.gz

# MAKE THE FOLLOWING CHANGE:
# open opencv_contrib-3.3.0/modules/face/CMakeLists.txt
# add wrapper for java
# ocv_define_module(face opencv_core opencv_imgproc opencv_objdetect WRAP python java)


readonly DIR=`pwd`
echo $DIR
cd opencv-3.3.0
mkdir -p release
cd release

cmake \
      -DCMAKE_C_FLAGS_RELEASE=-DNDEBUG \
      -DCMAKE_CXX_FLAGS_RELEASE=-DNDEBUG \
      -DCMAKE_INSTALL_PREFIX=$DIR/opencv-3.3.0 \
      -DCMAKE_BUILD_TYPE=Release \
      -DCMAKE_FIND_FRAMEWORK=LAST \
      -DCMAKE_VERBOSE_MAKEFILE=ON \
      -Wno-dev \
      -DCMAKE_OSX_DEPLOYMENT_TARGET= \
      -DBUILD_JASPER=OFF \
      -DBUILD_JPEG=ON \
      -DBUILD_OPENEXR=OFF \
      -DBUILD_PERF_TESTS=OFF \
      -DBUILD_PNG=OFF \
      -DBUILD_TESTS=OFF \
      -DBUILD_TIFF=OFF \
      -DBUILD_ZLIB=OFF \
      -DBUILD_opencv_java=ON \
      -DOPENCV_ENABLE_NONFREE=ON \
      -DOPENCV_EXTRA_MODULES_PATH=$DIR/opencv_contrib-3.3.0/modules \
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
      ..

#-DPYTHON2_EXECUTABLE=/usr/local/opt/python/libexec/bin/python
#-DPYTHON2_LIBRARY=/usr/local/Cellar/python/2.7.13_1/Frameworks/Python.framework/Versions/2.7/lib/libpython2.7.dylib
#-DPYTHON2_INCLUDE_DIR=/usr/local/Cellar/python/2.7.13_1/Frameworks/Python.framework/Versions/2.7/include/python2.7
#-DPYTHON3_EXECUTABLE=/usr/local/opt/python3/bin/python3
#-DPYTHON3_LIBRARY=/usr/local/opt/python3/Frameworks/Python.framework/Versions/3.6/lib/python3.6/config-3.6m-darwin/libpython3.6.dylib
#-DPYTHON3_INCLUDE_DIR=/usr/local/Cellar/python3/3.6.3/Frameworks/Python.framework/Versions/3.6/include/python3.6m

make -j8
make install

# -DPYTHON_EXECUTABLE=$(which python)
# -DPYTHON_INCLUDE_DIR=$(python -c "from distutils.sysconfig import get_python_inc; print(get_python_inc())")
# -DPYTHON_PACKAGES_PATH=$(python -c "from distutils.sysconfig import get_python_lib; print(get_python_lib())") ..
