package eu.europeana.metis.reprocessing.execution;

import eu.europeana.corelib.solr.bean.impl.FullBeanImpl;
import eu.europeana.metis.reprocessing.model.DatasetStatus;
import eu.europeana.metis.reprocessing.utilities.MongoDao;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@europeana.eu)
 * @since 2019-05-14
 */
public class ReprocessForDataset implements Callable<Void> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReprocessForDataset.class);
  private final String datasetId;
  private MongoDao mongoDao;

  public ReprocessForDataset(String datasetId, MongoDao mongoDao) {
    // TODO: 15-5-19 remember to correctly set datasetId
    this.datasetId = "2051942";
    this.mongoDao = mongoDao;
  }

  @Override
  public Void call() {
    return reprocessDataset();
  }

  private Void reprocessDataset() {
    LOGGER.info("Processing DatasetId: {} started", datasetId);
    final DatasetStatus datasetStatus = retrieveOrInitializeDatasetStatus();
    if (datasetStatus.getTotalRecords() == datasetStatus.getTotalProcessed()) {
      LOGGER.info("Processing DatasetId: {} not started because it was already completely processed",
          datasetId);
      return null;
    }
    final long startTime = System.nanoTime();
    loopOverAllRecordsAndProcess(datasetStatus);

    //Code here
//    String dsId = "2051942";
//    final long startTime = System.nanoTime();
//    List<FullBeanImpl> nextPageOfRecords = mongoDao.getNextPageOfRecords(dsId, 0);
//    List<FullBeanImpl> nextPageOfRecords = new ArrayList<>();
//    for (int i = 0; i < 1000; i++) {
//      nextPageOfRecords.add(mongoDao.getRecord(
//          "/03915/public_mistral_memoire_fr_ACTION_CHERCHER_FIELD_1_REF_VALUE_1_AP70L00682F"));
//    }
    final long endTime = System.nanoTime();
    System.out.println(String
        .format("Total time: %s DatasetId: %s ", (double) (endTime - startTime) / 1_000_000_000.0,
            datasetId));

    LOGGER.info("Processing DatasetId: {} end", datasetId);
    return null;
  }

  private DatasetStatus retrieveOrInitializeDatasetStatus() {
    DatasetStatus datasetStatus = mongoDao.getDatasetStatus(datasetId);
    if (datasetStatus == null) {
      datasetStatus = new DatasetStatus();
      final long totalRecordsForDataset = mongoDao.getTotalRecordsForDataset(datasetId);
      datasetStatus.setDatasetId(datasetId);
      datasetStatus.setTotalRecords(totalRecordsForDataset);
      mongoDao.storeDatasetStatusToDb(datasetStatus);
    }
    return datasetStatus;
  }

  private int getNextPage(DatasetStatus datasetStatus) {
    final long totalProcessed = datasetStatus.getTotalProcessed();
    return (int) (totalProcessed / MongoDao.PAGE_SIZE);
  }

  private void loopOverAllRecordsAndProcess(final DatasetStatus datasetStatus) {
    int nextPage = getNextPage(datasetStatus);
    List<FullBeanImpl> nextPageOfRecords = mongoDao.getNextPageOfRecords(datasetId, nextPage);
    while (CollectionUtils.isNotEmpty(nextPageOfRecords)) {
      LOGGER.info("Processing number of records: {}", nextPageOfRecords.size());
      //ProcessRecords
      //IndexRecords
      datasetStatus.setTotalProcessed(datasetStatus.getTotalProcessed() + nextPageOfRecords.size());
      mongoDao.storeDatasetStatusToDb(datasetStatus);
      nextPage++;
      nextPageOfRecords = mongoDao.getNextPageOfRecords(datasetId, nextPage);
    }
  }

}
