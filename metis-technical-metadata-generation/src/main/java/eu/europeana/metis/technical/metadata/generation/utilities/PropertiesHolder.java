package eu.europeana.metis.technical.metadata.generation.utilities;

import eu.europeana.metis.technical.metadata.generation.model.Mode;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Contains all properties that are required for execution.
 * <p>During construction will read properties from the specified file from the classpath.</p>
 *
 * @author Simon Tzanakis (Simon.Tzanakis@europeana.eu)
 * @since 2019-04-16
 */
public class PropertiesHolder {

  private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesHolder.class);
  public static final Marker EXECUTION_LOGS_MARKER = MarkerFactory.getMarker("EXECUTION_LOGS");
  public static final Marker STATISTICS_LOGS_MARKER = MarkerFactory.getMarker("STATISTICS_LOGS");

  public final File directoryWithResourcesPerDatasetPath;
  public final Mode mode;
  public final int maxParallelThreads;
  public final int parallelThreadsPerFile;
  public final int startFromFileIndexInDirectory;
  public final int endAtFileIndexInDirectory;
  public final int resourceConnectTimeout;
  public final int resourceSocketTimeout;

  public final String truststorePath;
  public final String truststorePassword;
  public final String[] mongoHosts;
  public final int[] mongoPorts;
  public final String mongoAuthenticationDb;
  public final String mongoUsername;
  public final String mongoPassword;
  public final boolean mongoEnablessl;
  public final String mongoDb;

  //S3
  public final String s3AccessKey;
  public final String s3SecretKey;
  public final String s3Endpoint;
  public final String s3Bucket;

  public PropertiesHolder(String configurationFileName) {
    Properties properties = new Properties();
    final URL resource = getClass().getClassLoader().getResource(configurationFileName);
    final String filePathInResources = resource == null ? null : resource.getFile();
    String filePath;
    if (filePathInResources != null && new File(filePathInResources).exists()) {
      LOGGER.info("Will try to load {} properties file", filePathInResources);
      filePath = filePathInResources;
    } else {
      LOGGER.info(
          "{} properties file does NOT exist, probably running in standalone .jar mode where the properties file should be on the same directory "
              + "as the .jar file is. Will try to load {} properties file",
          filePathInResources, configurationFileName);
      filePath = configurationFileName;
    }
    try {
      properties.load(new FileInputStream(filePath));
    } catch (IOException e) {
      throw new ExceptionInInitializerError(e);
    }

    directoryWithResourcesPerDatasetPath = new File(properties.getProperty("directory.with.resources.per.dataset.path"));
    mode = Mode.getModeFromEnumName(properties.getProperty("mode"));
    maxParallelThreads = Integer.parseInt(properties.getProperty("max.parallel.threads"));
    parallelThreadsPerFile = Integer.parseInt(properties.getProperty("parallel.threads.per.file"));
    startFromFileIndexInDirectory = Integer.parseInt(properties.getProperty("start.from.file.index.in.directory"));
    endAtFileIndexInDirectory = Integer.parseInt(properties.getProperty("end.at.file.index.in.directory"));
    resourceConnectTimeout = Integer.parseInt(properties.getProperty("resource.connect.timeout"));
    resourceSocketTimeout = Integer.parseInt(properties.getProperty("resource.socket.timeout"));
    truststorePath = properties.getProperty("truststore.path");
    truststorePassword = properties.getProperty("truststore.password");
    mongoHosts = properties.getProperty("mongo.hosts").split(",");
    mongoPorts = Arrays.stream(properties.getProperty("mongo.port").split(","))
        .mapToInt(Integer::parseInt).toArray();
    mongoAuthenticationDb = properties.getProperty("mongo.authentication.db");
    mongoUsername = properties.getProperty("mongo.username");
    mongoPassword = properties.getProperty("mongo.password");
    mongoEnablessl = Boolean.parseBoolean(properties.getProperty("mongo.enableSSL"));
    mongoDb = properties.getProperty("mongo.db");

    //S3
    s3AccessKey = properties.getProperty("s3.access.key");
    s3SecretKey = properties.getProperty("s3.secret.key");
    s3Endpoint = properties.getProperty("s3.endpoint");
    s3Bucket = properties.getProperty("s3.bucket");
  }
}
