/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.persistence;

import at.punkt.lodms.spi.extract.Extractor;
import at.punkt.lodms.spi.load.Loader;
import at.punkt.lodms.spi.transform.Transformer;
import at.punkt.lodms.web.JobMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex Kreiser
 */
public class ETLJobBlueprint {

  private String id;
  private JobMetadata metadata;
  private String pipelineId;
  private List<ComponentBlueprint<Extractor>> extractors = new ArrayList<ComponentBlueprint<Extractor>>();
  private List<ComponentBlueprint<Transformer>> transformers = new ArrayList<ComponentBlueprint<Transformer>>();
  private List<ComponentBlueprint<Loader>> loaders = new ArrayList<ComponentBlueprint<Loader>>();

  public ETLJobBlueprint() {

  }

  public ETLJobBlueprint(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }


  public JobMetadata getMetadata() {
    return metadata;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setMetadata(JobMetadata metadata) {
    this.metadata = metadata;
  }

  public String getPipelineId() {
    return pipelineId;
  }

  public void setPipelineId(String pipelineId) {
    this.pipelineId = pipelineId;
  }

  public List<ComponentBlueprint<Extractor>> getExtractors() {
    return extractors;
  }

  public void setExtractors(List<ComponentBlueprint<Extractor>> extractors) {
    this.extractors = extractors;
  }

  public List<ComponentBlueprint<Loader>> getLoaders() {
    return loaders;
  }

  public void setLoaders(List<ComponentBlueprint<Loader>> loaders) {
    this.loaders = loaders;
  }

  public List<ComponentBlueprint<Transformer>> getTransformers() {
    return transformers;
  }

  public void setTransformers(List<ComponentBlueprint<Transformer>> transformers) {
    this.transformers = transformers;
  }
}
