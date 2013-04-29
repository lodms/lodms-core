/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Alex Kreiser
 */
public class JobMetadata implements Serializable {

  static public final String T_SCHEDULED = "scheduled";
  static public final String T_CHAINED = "chained";
  private String name = "";
  private String description = "";
  private boolean scheduled;
  private String interval = "";
  private Date created = new Date();
  private String previousJobId = "";
  private String scheduleType = T_SCHEDULED;

  public String getPreviousJobId() {
    return previousJobId;
  }

  public void setPreviousJobId(String previousJobId) {
    this.previousJobId = previousJobId;
  }

  public boolean isChained() {
    return !previousJobId.isEmpty();
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getInterval() {
    return interval;
  }

  public void setInterval(String interval) {
    this.interval = interval;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isScheduled() {
    return scheduled;
  }

  public void setScheduled(boolean scheduled) {
    this.scheduled = scheduled;
  }

  public String getScheduleType() {
    return scheduleType;
  }

  public void setScheduleType(String scheduleType) {
    this.scheduleType = scheduleType;
  }
}
