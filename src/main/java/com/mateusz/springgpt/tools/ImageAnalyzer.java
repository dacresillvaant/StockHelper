package com.mateusz.springgpt.tools;

import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Base64;

public class ImageAnalyzer {

    public static final String IMAGE_ANALYZER_VERSION = "1.0";

    private ImageAnalyzer() {
        throw new IllegalStateException("Utility class");
    }

    public static String byteToBase64(byte[] image) {
        return Base64.getEncoder().encodeToString(image);
    }

    public static Mat byteToMat(byte[] imageBytes) {
        return opencv_imgcodecs.imdecode(new Mat(imageBytes), opencv_imgcodecs.IMREAD_COLOR);
    }

    public static Mat base64ToMat(String base64) {
        byte[] decodeBytes = Base64.getDecoder().decode(base64);
        return opencv_imgcodecs.imdecode(new Mat(decodeBytes), opencv_imgcodecs.IMREAD_COLOR);
    }

    /**
     * @param image - image in Mat object
     * @return - coefficient between green and red pixels - the higher the number, the more green pixels are there
     *
     * OpenCV's images are stored in BGR format (Blue-Green-Red)
     * k argument of get() method refers to channel:
     * 0 -> blue channel
     * 1 -> green channel
     * 2 -> red channel
     */
    public static BigDecimal greenRedRatio(Mat image) {
        UByteIndexer indexer = image.createIndexer();

        int greenPixelsCount = 0;
        int redPixelsCount = 0;

        for (int x = 0; x < image.rows(); x++) {        // Iterate over rows (horizontal)
            for (int y = 0; y < image.cols(); y++) {    // Iterate over columns (vertical)
                int blue = indexer.get(x, y, 0);
                int green = indexer.get(x, y, 1);
                int red = indexer.get(x, y, 2);

                if (red > green && red > blue) {
                    redPixelsCount++;
                } else if (green > red && green > blue) {
                    greenPixelsCount++;
                }
            }
        }

        indexer.close();

        if (redPixelsCount + greenPixelsCount == 0) {
            return BigDecimal.ZERO;
        } else {
            BigDecimal totalPixels = BigDecimal.valueOf(greenPixelsCount).add(BigDecimal.valueOf(redPixelsCount));
            return BigDecimal.valueOf(greenPixelsCount).divide(totalPixels, 2, RoundingMode.HALF_UP);
        }
    }
}