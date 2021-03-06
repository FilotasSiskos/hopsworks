/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.hops.hopsworks.admin.llap;

import io.hops.hopsworks.admin.lims.MessagesController;
import io.hops.hopsworks.common.admin.llap.LlapClusterFacade;
import io.hops.hopsworks.common.admin.llap.LlapClusterLifecycle;
import io.hops.hopsworks.common.admin.llap.LlapClusterStatus;
import org.primefaces.context.RequestContext;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedBean(name = "LlapBean")
@ViewScoped
public class LlapBean implements Serializable {

  @EJB
  LlapClusterFacade llapClusterFacade;
  @EJB
  LlapClusterLifecycle llapClusterLifecycle;

  private static final Logger logger = Logger.getLogger(LlapBean.class.getName());

  private static int MAXWAITINGITERATIONS = 240;

  private int nInstances = 2;
  private long execMemory = 4096;
  private long cacheMemory = 4096;
  private int nExecutors = 4;
  private int nIOThreads = 4;

  private LlapClusterStatus.Status clusterStatus;

  private List<String> llapHosts = null;
  private String selectedHost = "";

  @PostConstruct
  public void init() {
    LlapClusterStatus status = llapClusterFacade.getClusterStatus();
    this.clusterStatus = status.getClusterStatus();
    this.nInstances = status.getInstanceNumber();
    this.execMemory = status.getExecutorsMemory();
    this.cacheMemory = status.getCacheMemory();
    this.nExecutors = status.getExecutorsPerInstance();
    this.nIOThreads = status.getIOThreadsPerInstance();
    this.llapHosts = status.getHosts();

    if (llapHosts != null && llapHosts.size() > 0) {
      this.selectedHost = llapHosts.get(0);
    }
  }

  public int getnInstances() { return nInstances; }

  public void setnInstances(int nInstances) {
    this.nInstances = nInstances;
  }

  public long getExecMemory() { return execMemory; }

  public void setExecMemory(long execMemory) {
    this.execMemory = execMemory;
  }

  public long getCacheMemory() { return cacheMemory; }

  public void setCacheMemory(long cacheMemory) {
    this.cacheMemory = cacheMemory;
  }

  public int getnExecutors() { return nExecutors; }

  public void setnExecutors(int nExecutors) {
    this.nExecutors = nExecutors;
  }

  public int getnIOThreads() { return nIOThreads; }

  public void setnIOThreads(int nIOThreads) {
    this.nIOThreads = nIOThreads;
  }

  public List<String> getLlapHosts() { return llapHosts; }

  public String getSelectedHost() { return selectedHost; }

  public void setSelectedHost(String selectedHost) {
    this.selectedHost = selectedHost;
  }

  public boolean isClusterUp() {
    return clusterStatus == LlapClusterStatus.Status.UP;
  }

  public boolean isClusterStarting() {
    return clusterStatus == LlapClusterStatus.Status.LAUNCHING;
  }

  public boolean areContainersRunning() {
    return !llapClusterFacade.getLlapHosts().isEmpty();
  }

  public void waitForCluster(boolean shouldBeStarting) {
    if (!shouldBeStarting && (isClusterUp() || !isClusterUp() && !isClusterStarting())) {
      // The cluster is up or down and nobody asked for a new cluster. Unlock the ui
      RequestContext.getCurrentInstance().addCallbackParam("alreadyUp", "true");
      return;
    }

    // Wait for the cluster to come up.
    int counter = MAXWAITINGITERATIONS;
    try {
      // We can't immediately start polling for isClusterStarting, as this execution path
      // is faster than the startCluster function.
      Thread.sleep(5000);
      while (llapClusterFacade.isClusterStarting() && counter > 0) {
        Thread.sleep(1500);
        counter--;
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error on waiting for LLAP cluster to come up", e);
    }

    if (counter > 0 && !llapClusterFacade.isClusterUp()) {
      RequestContext.getCurrentInstance().addCallbackParam("alreadyUp", "error");
      MessagesController.addErrorMessage("Error starting the cluster. Check the glassfish logs for more info.");
    } else if (counter > 0) {
      RequestContext.getCurrentInstance().addCallbackParam("alreadyUp", "false");
    } else {
      RequestContext.getCurrentInstance().addCallbackParam("alreadyUp", "timeout");
      MessagesController.addErrorMessage("Timeout while starting the cluster. Try again.");
    }
  }

  public void startLLAP() {
    llapClusterLifecycle.startCluster(nInstances, execMemory,
        cacheMemory, nExecutors, nIOThreads);
    waitForCluster(true);
  }

  public void stopLLAP() {
    llapClusterLifecycle.stopCluster();
  }
}
