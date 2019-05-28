package eu.europeana.metis.technical.metadata.generation;

import eu.europeana.metis.technical.metadata.generation.model.FileStatus;
import eu.europeana.metis.technical.metadata.generation.utilities.ExecutorManager;
import eu.europeana.metis.technical.metadata.generation.utilities.MongoDao;
import eu.europeana.metis.technical.metadata.generation.utilities.MongoInitializer;
import eu.europeana.metis.technical.metadata.generation.utilities.PropertiesHolder;
import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.core.net.ssl.TrustStoreConfigurationException;
import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResetFileStatusMain {


  private static final Logger LOGGER = LoggerFactory.getLogger(ProgressCheckerMain.class);

  private static final String CONFIGURATION_FILE = "application.properties";
  private static final PropertiesHolder propertiesHolder = new PropertiesHolder(CONFIGURATION_FILE);

  public static void main(String[] args) throws TrustStoreConfigurationException, IOException {

    // Initialize.
    LOGGER.info("Starting script - initializing connections.");
    final MongoInitializer mongoInitializer = TechnicalMetadataGenerationMain
        .prepareConfiguration();
    final Datastore datastore = TechnicalMetadataGenerationMain
        .createDatastore(mongoInitializer.getMongoClient(), propertiesHolder.mongoDb);

    // Check the progress.
    resetFiles(datastore, 1, 131);
    resetFiles(datastore, 397, 656);
    resetFiles(datastore, 1089, 1921);

    // Cleanup.
    LOGGER.info("Done.");
    mongoInitializer.close();
  }

  private static void resetFiles(Datastore datastore, int from, int to) throws IOException {
    final MongoDao mongoDao = new MongoDao(datastore);
    final File[] filesPerDataset = ExecutorManager
        .getAllFiles(propertiesHolder.directoryWithResourcesPerDatasetPath);


    long totalLinkCount = 0;
    long processedLinkCount = 0;
    int fileIndex = 1; // one-based!
    for (File datasetFile : filesPerDataset) {

      if (fileIndex >= from && fileIndex <= to) {

        // Get the current status.
        final String fileName = datasetFile.getName();
        final FileStatus fileStatus = mongoDao.getFileStatus(fileName);

        // Reset
        if (fileStatus != null) {
          fileStatus.setEndOfFileReached(false);
          fileStatus.setLineReached(0);
        }

        // Re-store the status in db
        mongoDao.storeFileStatusToDb(fileStatus);
        System.out.println(String.format("Reset file %s: %s.", fileIndex, fileName));

      }

      // Next file.
      fileIndex++;
    }

    LOGGER.info("{} files examined. Processed {} of {} links. {} links left.", fileIndex,
        processedLinkCount, totalLinkCount, totalLinkCount - processedLinkCount);
  }
}
