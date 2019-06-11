package eu.europeana.metis.reprocessing.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.europeana.metis.core.workflow.HasMongoObjectId;
import eu.europeana.metis.json.ObjectIdSerializer;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexed;

/**
 * Model class for containing dataset status information.
 *
 * @author Simon Tzanakis (Simon.Tzanakis@europeana.eu)
 * @since 2019-05-14
 */
public class DatasetStatus implements HasMongoObjectId {

  @Id
  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId id;

  @Indexed(options = @IndexOptions(unique = true))
  private String datasetId;
  private int indexInOrderedList;
  private Date startDate;
  private Date endDate;
  private long totalRecords;
  private long totalProcessed;
  private long totalFailedRecords;
  private Set<Integer> pagesProcessed = new HashSet<>();
  private double totalTimeProcessingInSecs;
  private double totalTimeIndexingInSecs;
  private double averageTimeRecordProcessingInSecs;
  private double averageTimeRecordIndexingInSecs;

  public DatasetStatus() {
    //Default constructor
  }

  @Override
  public ObjectId getId() {
    return id;
  }

  @Override
  public void setId(ObjectId objectId) {
    this.id = objectId;
  }

  public String getDatasetId() {
    return datasetId;
  }

  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }

  public int getIndexInOrderedList() {
    return indexInOrderedList;
  }

  public void setIndexInOrderedList(int indexInOrderedList) {
    this.indexInOrderedList = indexInOrderedList;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public long getTotalRecords() {
    return totalRecords;
  }

  public void setTotalRecords(long totalRecords) {
    this.totalRecords = totalRecords;
  }

  public long getTotalProcessed() {
    return totalProcessed;
  }

  public void setTotalProcessed(long totalProcessed) {
    this.totalProcessed = totalProcessed;
  }

  public long getTotalFailedRecords() {
    return totalFailedRecords;
  }

  public void setTotalFailedRecords(long totalFailedRecords) {
    this.totalFailedRecords = totalFailedRecords;
  }

  public Set<Integer> getPagesProcessed() {
    return pagesProcessed;
  }

  public double getTotalTimeProcessingInSecs() {
    return totalTimeProcessingInSecs;
  }

  public void setTotalTimeProcessingInSecs(double totalTimeProcessingInSecs) {
    this.totalTimeProcessingInSecs = totalTimeProcessingInSecs;
  }

  public double getTotalTimeIndexingInSecs() {
    return totalTimeIndexingInSecs;
  }

  public void setTotalTimeIndexingInSecs(double totalTimeIndexingInSecs) {
    this.totalTimeIndexingInSecs = totalTimeIndexingInSecs;
  }

  public double getAverageTimeRecordProcessingInSecs() {
    return averageTimeRecordProcessingInSecs;
  }

  public void setAverageTimeRecordProcessingInSecs(double averageTimeRecordProcessingInSecs) {
    this.averageTimeRecordProcessingInSecs = averageTimeRecordProcessingInSecs;
  }

  public double getAverageTimeRecordIndexingInSecs() {
    return averageTimeRecordIndexingInSecs;
  }

  public void setAverageTimeRecordIndexingInSecs(double averageTimeRecordIndexingInSecs) {
    this.averageTimeRecordIndexingInSecs = averageTimeRecordIndexingInSecs;
  }

  public void updateAverages() {
    this.averageTimeRecordProcessingInSecs = this.totalTimeProcessingInSecs / this.totalProcessed;
    this.averageTimeRecordIndexingInSecs = this.totalTimeIndexingInSecs / this.totalProcessed;
  }

  private double secondsTimeToHours(double secondTime) {
    return secondTime / 3600;
  }

  @Override
  public String toString() {
    return String.format(
        "ObjectId: %s, datasetId: %s, totalRecords: %d, totalProcessed: %d, totalFailedRecords: %d, "
            + "totalTimeProcessingInSecs: %fs = %fh, totalTimeIndexingInSecs: %fs = %fh, "
            + "averageTimeRecordProcessingInSecs: %fs, averageTimeRecordIndexingInSecs: %fs",
        id, datasetId, totalRecords, totalProcessed, totalFailedRecords,
        totalTimeProcessingInSecs, secondsTimeToHours(totalTimeProcessingInSecs),
        totalTimeIndexingInSecs, secondsTimeToHours(totalTimeIndexingInSecs),
        averageTimeRecordProcessingInSecs, averageTimeRecordIndexingInSecs);
  }
}
