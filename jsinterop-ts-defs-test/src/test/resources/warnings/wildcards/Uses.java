/*
 * Copyright © 2026 Vertispan
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
package com.vertispan.tsdefs.tests.warnings.wildcards;

import com.vertispan.tsdefs.annotations.TsTypeRef;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType
public interface Uses {
  // Warns: the type produces T.
  @JsMethod
  Producer<? super Item> producerOfSuper();

  // Warns: the type produces T through a field.
  @JsMethod
  Holder<? super Item> holderOfSuper();

  // Warns: the type produces T through an inherited member.
  @JsMethod
  SubProducer<? super Item> inheritedOfSuper();

  // Silent: the type only consumes T.
  @JsMethod
  Consumer<? super Item> consumerOfSuper();

  // Silent: ? extends never over-promises.
  @JsMethod
  Producer<? extends Item> producerOfExtends();

  // Silent: @TsTypeRef chose the emitted type.
  @JsMethod
  Producer<@TsTypeRef(Item.class) ? super Item> overridden();

  // Silent: in a parameter position the erased signature is stricter, not wrong.
  @JsMethod
  void takes(Producer<? super Item> producer);

  // Warns: the inlined function returns T.
  @JsMethod
  Supplier<? super Item> supplier();

  // Warns: the index signature reads T.
  @JsMethod
  JsPropertyMap<? super Item> map();

  // Warns, and again in each subtype that re-emits the getter.
  @JsProperty
  Producer<? super Item> getProp();

  // Silent: @TsTypeRef on the bound also chose the emitted type.
  @JsMethod
  Producer<? super @TsTypeRef(Item.class) Item> typeRefOnBound();

  // Silent: the only member that reaches TypeScript consumes T.
  @JsMethod
  Mixed<? super Item> mixed();
}
