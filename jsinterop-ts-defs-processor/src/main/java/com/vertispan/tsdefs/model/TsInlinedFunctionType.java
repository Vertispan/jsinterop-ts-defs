/*
 * Copyright © 2023 Vertispan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertispan.tsdefs.model;

import com.vertispan.tsdefs.builders.HasProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TsInlinedFunctionType extends TsType implements HasProperties<TsInlinedFunctionType> {
  private List<TsProperty> properties = new ArrayList<>();
  private TsType returnType;

  public TsInlinedFunctionType() {
    super("", "");
  }

  @Override
  public TsInlinedFunctionType addProperty(TsProperty property) {
    properties.add(property);
    return this;
  }

  public TsType getReturnType() {
    return returnType;
  }

  public void setReturnType(TsType returnType) {
    this.returnType = returnType;
  }

  @Override
  public String emit(String parentNamespace) {
    return "(" + emitProperties() + ")=>" + returnType.emit("");
  }

  private String emitProperties() {
    return properties.stream()
        .map(property -> property.emit("", "", ""))
        .collect(Collectors.joining(","));
  }
}
