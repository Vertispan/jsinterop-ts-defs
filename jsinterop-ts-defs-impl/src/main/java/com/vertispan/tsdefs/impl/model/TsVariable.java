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

import static com.vertispan.tsdefs.impl.Formatting.resolveName;
import static java.util.Objects.nonNull;

import com.vertispan.tsdefs.impl.builders.Deprecation;
import com.vertispan.tsdefs.impl.builders.HasDocs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class TsVariable {
  protected final String name;
  protected final TsType type;
  protected TsDoc tsDoc;
  protected boolean jsOptional;
  protected boolean deprecated;
  protected boolean varargs = false;
  protected List<TsModifier> modifiers = new ArrayList<>();

  protected TsVariable(String name, TsType type) {
    this.name = name;
    this.type = type;
  }

  public abstract String emit(String indent, String ending, String parentNamespace);

  protected String emit(
      String indent, String ending, String parentNamespace, boolean skipModifiers) {
    StringBuffer sb = new StringBuffer();

    if (nonNull(tsDoc)) {
      sb.append(tsDoc.emit(indent, deprecated));
    } else if (deprecated) {
      sb.append(Deprecation.emit(indent));
    }
    if (!skipModifiers) {
      sb.append(modifiers.stream().map(TsModifier::emit).collect(Collectors.joining("", "", "")));
    }

    if (varargs) {
      sb.append("...");
    }
    sb.append(resolveName(name));
    if (jsOptional) {
      sb.append("?");
    }
    sb.append(":");
    sb.append(emitType(parentNamespace));
    sb.append(ending);

    return sb.toString();
  }

  public abstract String emitType(String parentNamespace);

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TsVariable)) return false;
    TsVariable that = (TsVariable) o;
    return Objects.equals(name, that.name) && Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  public static class TsPropertyBuilder<T extends TsVariable>
      implements HasDocs<TsPropertyBuilder<T>> {

    private final T property;

    public TsPropertyBuilder(T property) {
      this.property = property;
    }

    public TsPropertyBuilder<T> addModifiers(TsModifier... modifiers) {
      this.property.modifiers.addAll(Arrays.asList(modifiers));
      return this;
    }

    @Override
    public TsPropertyBuilder<T> setDocs(TsDoc tsDoc) {
      this.property.tsDoc = tsDoc;
      return this;
    }

    public TsPropertyBuilder<T> setOptional(boolean optional) {
      this.property.jsOptional = optional;
      return this;
    }

    public TsPropertyBuilder<T> setDeprecated(boolean deprecated) {
      this.property.deprecated = deprecated;
      return this;
    }

    public TsPropertyBuilder<T> setVarargs(boolean varargs) {
      this.property.varargs = varargs;
      return this;
    }

    public T build() {
      return property;
    }
  }
}
