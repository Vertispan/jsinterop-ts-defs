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

import java.util.Objects;

public class TsTemplatedInlinedType extends TsType {
  private String template;
  private TsType type;

  public TsTemplatedInlinedType(String template, TsType type) {
    super("", "");
    this.template = template;
    this.type = type;
  }

  public static TsTemplatedInlinedType of(String template, TsType type) {
    return new TsTemplatedInlinedType(template, type);
  }

  @Override
  public String emit(String parentNamespace) {
    return template.replace("$T", type.emit(parentNamespace));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TsTemplatedInlinedType)) return false;
    if (!super.equals(o)) return false;
    TsTemplatedInlinedType that = (TsTemplatedInlinedType) o;
    return Objects.equals(template, that.template) && Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), template, type);
  }
}
