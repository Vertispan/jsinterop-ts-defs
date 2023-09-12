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
package com.vertispan.tsdefs.impl.model;

public class TsProperty extends TsVariable {

  public TsProperty(String name, TsType type) {
    super(name, type);
  }

  @Override
  public String emit(String indent, String ending, String parentNamespace) {
    return super.emit(indent, ending, parentNamespace, false);
  }

  @Override
  public String emitType(String parentNamespace) {
    return type.emit(parentNamespace);
  }

  public static TsPropertyBuilder<TsProperty> builder(String name, TsType type) {
    return new TsPropertyBuilder<>(new TsProperty(name, type));
  }
}
