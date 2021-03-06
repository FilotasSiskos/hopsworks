/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.hops.hopsworks.api.admin.dto;

import io.hops.hopsworks.common.dao.util.Variables;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class VariablesRequest {
  
  @XmlElement(name = "variables", type = Variables.class)
  private List<Variables> variables;
  
  public VariablesRequest() {
  
  }
  
  public VariablesRequest(List<Variables> variables) {
    this.variables = variables;
  }
  
  public List<Variables> getVariables() {
    return variables;
  }
  
  public void setVariables(List<Variables> variables) {
    this.variables = variables;
  }
}
