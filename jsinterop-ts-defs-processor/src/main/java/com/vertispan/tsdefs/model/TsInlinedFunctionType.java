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

import com.vertispan.tsdefs.builders.HasParameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TsInlinedFunctionType extends TsType implements HasParameters<TsInlinedFunctionType> {
  private List<TsParameter> parameters = new ArrayList<>();
  private TsType returnType;

  public TsInlinedFunctionType() {
    super("", "");
  }

  @Override
  public TsInlinedFunctionType addParameter(TsParameter parameter) {
    parameters.add(parameter);
    return this;
  }

  public TsType getReturnType() {
    return returnType;
  }

  public TsInlinedFunctionType setReturnType(TsType returnType) {
    this.returnType = returnType;
    return this;
  }

  @Override
  public String emit(String parentNamespace) {
    return "(" + emitParameters() + ")=>" + returnType.emit("");
  }

  private String emitParameters() {
    return parameters.stream()
        .map(parameter -> parameter.emit("", "", ""))
        .collect(Collectors.joining(","));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TsInlinedFunctionType)) return false;
    if (!super.equals(o)) return false;
    TsInlinedFunctionType that = (TsInlinedFunctionType) o;
    return Objects.equals(parameters, that.parameters)
        && Objects.equals(getReturnType(), that.getReturnType());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), parameters, getReturnType());
  }
}
