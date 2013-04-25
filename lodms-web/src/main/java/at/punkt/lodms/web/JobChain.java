package at.punkt.lodms.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobChain {
  private Map<String, List<String>> chain = new HashMap<String, List<String>>();

  public void add(String parent, String child) {
    if (chain.containsKey(parent)) {
      List<String> children = chain.get(parent);
      children.add(child);
    }
    else {
      ArrayList<String> children = new ArrayList<String>();
      children.add(child);
      chain.put(parent, children);
    }
  }

  public List<String> getChildren(String parent) {
    return chain.get(parent);
  }

}
