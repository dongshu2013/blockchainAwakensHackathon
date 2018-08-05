package com.walletalarm.platform.handlers;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.common.hash.Hashing;
import com.walletalarm.platform.ApiConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.sql.Timestamp;

public class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);
    private static final String IMAGE_BUCKET_NAME = "icoalarm-images";

    public static final int FCM_BATCH_SIZE = 1000;

    //https://stackoverflow.com/a/4050276
    public static String getLastBitFromUrl(final String url) {
        // return url.replaceFirst("[^?]*/(.*?)(?:\\?.*)","$1);" <-- incorrect
        return url.replaceFirst(".*/([^/?]+).*", "$1");
    }

    public static Boolean saveFileToS3(ApiConfiguration apiConfiguration, String url, String filename) {
        File file = null;
        Boolean status = false;
        try {
            file = downloadFromUrl(new URL(url), filename);
            if (file.exists()) {
                BasicAWSCredentials awsCredentials = new BasicAWSCredentials(apiConfiguration.getAwsAccessKey(),
                        apiConfiguration.getAwsSecretKey());
                AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                        .withRegion(Regions.US_WEST_1)
                        .build();
                s3Client.putObject(IMAGE_BUCKET_NAME, filename, file);
                status = true;
            } else {
                LOGGER.error("File does not exist");
            }
        } catch (Exception ex) {
            LOGGER.error("Error downloading/uploading file - " + url, ex);
        } finally {
            if (file != null && file.exists()) {
                file.delete();
            }
        }
        return status;
    }

    //https://smarterco.de/java-download-file-from-url-to-temp-directory/
    private static File downloadFromUrl(URL url, String localFilename) throws IOException {
        InputStream is = null;
        FileOutputStream fos = null;
        File file = null;
        String tempDir = System.getProperty("java.io.tmpdir");
        String outputPath = tempDir + "/" + localFilename;
        System.setProperty("http.agent", "Chrome");

        try {
            //connect
            URLConnection urlConn = url.openConnection();
            urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; " +
                    "rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

            //get input stream from connection
            is = urlConn.getInputStream();
            fos = new FileOutputStream(outputPath);

            // 4KB buffer
            byte[] buffer = new byte[4096];
            int length;

            // read from source and write into local file
            while ((length = is.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            file = new File(outputPath);
        } catch (Exception ex) {
            LOGGER.error("Error getting file ", ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
        }
        return file;
    }

    public static String getMD5Hash(String stringToHash) {
        return Hashing.md5().hashString(stringToHash, Charset.forName("utf-8")).toString();
    }

    public static Timestamp getTimestampFromEpoch(long epoch) {
        return new Timestamp(epoch * 1000L);
    }
}
