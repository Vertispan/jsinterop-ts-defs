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
package com.vertispan.tsdefs.doclet;

import static java.util.Objects.nonNull;

import com.sun.source.doctree.*;
import com.sun.source.util.DocTreePath;
import com.sun.source.util.DocTrees;
import com.sun.source.util.SimpleDocTreeVisitor;
import com.vertispan.tsdefs.impl.HasProcessorEnv;
import com.vertispan.tsdefs.impl.builders.TsElement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import jdk.javadoc.doclet.DocletEnvironment;
import jsinterop.annotations.JsType;

public class TsDocTreeVisitor extends SimpleDocTreeVisitor<String, Element> {

  private final HasProcessorEnv env;
  private final DocletEnvironment docletEnv;

  public TsDocTreeVisitor(HasProcessorEnv env, DocletEnvironment docletEnv) {
    this.env = env;
    this.docletEnv = docletEnv;
  }

  @Override
  protected String defaultAction(DocTree node, Element element) {
    return node.toString();
  }

  @Override
  public String visitLink(LinkTree node, Element element) {
    ReferenceTree reference = node.getReference();
    List<? extends DocTree> label = node.getLabel();
    // TODO: newer JDKs split a label with inline tags into parts; only the first is kept.
    return formatLink(
        reference,
        getElementFromReference(reference, element),
        label.isEmpty() ? label : label.subList(0, 1),
        element);
  }

  /** A TSDoc link to the reference, with the given label or else a default one. */
  private String formatLink(
      ReferenceTree reference,
      Optional<Element> target,
      List<? extends DocTree> labelParts,
      Element element) {
    String label = visitAll(labelParts, element);
    // typedoc shows an unresolved link's whole body as text, so only label what it can resolve.
    if (label.trim().isEmpty()) {
      label = target.filter(this::isEmitted).map(this::defaultLabel).orElse("");
    }
    String path = target.map(this::formatPath).orElse(reference.getSignature());
    return "{@link " + path + (label.isEmpty() ? "" : " | " + label) + "}";
  }

  private String visitAll(List<? extends DocTree> docTrees, Element element) {
    return docTrees.stream().map(docTree -> visit(docTree, element)).collect(Collectors.joining());
  }

  /** Whether this doclet run emits the referenced type, or the member and its type. */
  private boolean isEmitted(Element referencedElement) {
    Element type =
        isMember(referencedElement) ? referencedElement.getEnclosingElement() : referencedElement;
    TsElement tsType = TsElement.of(type, env);
    JsType jsType = type.getAnnotation(JsType.class);
    boolean skippedNative = nonNull(jsType) && jsType.isNative() && !tsType.isTsInterface();
    return docletEnv.isIncluded(type)
        && tsType.isExportable()
        && !skippedNative
        && (!isMember(referencedElement) || TsElement.of(referencedElement, env).isExportable());
  }

  @Override
  public String visitReference(ReferenceTree node, Element element) {
    return getElementFromReference(node, element).map(this::formatPath).orElse(node.getSignature());
  }

  /** The referenced type's or member's JS path. */
  private String formatPath(Element referencedElement) {
    if (isMember(referencedElement)) {
      TsElement member = TsElement.of(referencedElement, env);
      String owner = memberOwner(member);
      return owner.isEmpty() ? memberName(member) : (owner + "." + memberName(member));
    }
    return formatName(TsElement.of(referencedElement, env));
  }

  /** The type's JS name, or for a member its type's JS name and its own, e.g. "Foo.bar()". */
  private String defaultLabel(Element referencedElement) {
    if (!isMember(referencedElement)) {
      return lastSegment(formatName(TsElement.of(referencedElement, env)));
    }
    TsElement member = TsElement.of(referencedElement, env);
    String name = memberName(member);
    if (ElementKind.METHOD == referencedElement.getKind()
        && !member.isGetter()
        && !member.isSetter()) {
      name += "()";
    }
    String owner = lastSegment(memberOwner(member));
    return owner.isEmpty() ? name : (owner + "." + name);
  }

  private static boolean isMember(Element element) {
    return ElementKind.METHOD == element.getKind() || ElementKind.FIELD == element.getKind();
  }

  private static String memberName(TsElement member) {
    return member.isSetter() || member.isGetter() ? member.nonGetSetName() : member.getName();
  }

  /** The JS path a member hangs from: its own namespace if it declares one, else its type's. */
  private String memberOwner(TsElement member) {
    return member.getDeclaredNamespace().orElseGet(() -> formatName(member.parent()));
  }

  private static String lastSegment(String path) {
    return path.substring(path.lastIndexOf('.') + 1);
  }

  /** The referenced element; a constructor resolves to its type, which TSDoc links to. */
  private Optional<Element> getElementFromReference(DocTree node, Element element) {
    DocTrees trees = docletEnv.getDocTrees();
    final Element referencedElement =
        trees.getElement(
            new DocTreePath(
                new DocTreePath(trees.getPath(element), trees.getDocCommentTree(element)), node));
    return Optional.ofNullable(referencedElement)
        .map(e -> ElementKind.CONSTRUCTOR == e.getKind() ? e.getEnclosingElement() : e);
  }

  public String formatName(TsElement tsElement) {
    String namespace = tsElement.getNamespace();
    String jsName = tsElement.getFqName();
    return namespace.isEmpty() ? jsName : (namespace + "." + jsName);
  }

  @Override
  public String visitSee(SeeTree node, Element element) {
    List<? extends DocTree> parts = node.getReference();
    if (parts.isEmpty() || parts.get(0).getKind() != DocTree.Kind.REFERENCE) {
      // A quoted string or HTML.
      return "@see " + visitAll(parts, element);
    }
    ReferenceTree reference = (ReferenceTree) parts.get(0);
    List<? extends DocTree> labelParts = parts.subList(1, parts.size());
    Optional<Element> target = getElementFromReference(reference, element);
    if (target.isPresent()) {
      return "@see " + formatLink(reference, target, labelParts, element);
    }
    String label = visitAll(labelParts, element);
    return "@see " + reference.getSignature() + (label.isEmpty() ? "" : " " + label);
  }

  @Override
  public String visitDocComment(DocCommentTree node, Element element) {
    String body =
        node.getFullBody().stream()
            .map(docTree -> visit(docTree, element))
            .collect(Collectors.joining());
    String tags =
        node.getBlockTags().stream()
            .map(tag -> " " + tag.accept(this, element))
            .collect(Collectors.joining("\n"));
    return " " + body + "\n" + tags;
  }

  @Override
  public String visitLiteral(LiteralTree node, Element element) {
    if (nonNull(node.getBody())) {
      return "<code>" + node.getBody().getBody() + "</code>";
    }
    return super.visitLiteral(node, element);
  }

  @Override
  public String visitText(TextTree node, Element element) {
    return node.getBody();
  }

  @Override
  public String visitReturn(ReturnTree node, Element element) {
    return "@return "
        + node.getDescription().stream()
            .map(docTree -> docTree.accept(this, element))
            .collect(Collectors.joining());
  }

  @Override
  public String visitParam(ParamTree node, Element element) {
    return (node.isTypeParameter() ? "@typeParam " : "@param ")
        + node.getName()
        + " - "
        + node.getDescription().stream()
            .map(docTree -> docTree.accept(this, element))
            .collect(Collectors.joining());
  }

  @Override
  public String visitInheritDoc(InheritDocTree node, Element element) {
    return "@inheritDoc";
  }
}
