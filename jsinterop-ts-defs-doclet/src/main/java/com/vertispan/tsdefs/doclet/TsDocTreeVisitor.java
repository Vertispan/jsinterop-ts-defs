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
import com.vertispan.tsdefs.impl.Formatting;
import com.vertispan.tsdefs.impl.HasProcessorEnv;
import com.vertispan.tsdefs.impl.builders.TsElement;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import jdk.javadoc.doclet.DocletEnvironment;

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
    return "{@link "
        + visit(node.getReference(), element)
        + (node.getLabel().isEmpty() ? "" : formatLabel(String.valueOf(node.getLabel().get(0))))
        + "}";
  }

  @Override
  public String visitReference(ReferenceTree node, Element element) {
    return getElementFromReference(node, element)
        .map(
            referencedElement -> {
              if (ElementKind.METHOD == referencedElement.getKind()) {
                TsElement tsElement = TsElement.of(element, env);
                TsElement refTsElement = TsElement.of(referencedElement, env);
                TsElement parentTsElement =
                    TsElement.of(referencedElement.getEnclosingElement(), env);

                String namespace = tsElement.getNamespace();
                String refNamespace = refTsElement.getNamespace();
                String parentNamespace = parentTsElement.getNamespace();

                boolean isSetterOrGetter = refTsElement.isSetter() || refTsElement.isGetter();

                String name =
                    isSetterOrGetter
                        ? Formatting.nonGetSetName(refTsElement.getName())
                        : refTsElement.getName();
                String parentName = parentTsElement.getName();
                if (refNamespace.equals(parentNamespace)) {
                  String fullName = refNamespace + "." + parentName + "." + name;
                  return fullName
                      .replace(namespace + ".", "")
                      .replace(tsElement.getName() + ".", "");
                } else {
                  return refNamespace + "." + name;
                }
              }
              return formatName(TsElement.of(referencedElement, env));
            })
        .orElse(node.getSignature());
  }

  private Optional<Element> getElementFromReference(DocTree node, Element element) {
    DocTrees trees = docletEnv.getDocTrees();
    final Element referencedElement =
        trees.getElement(
            new DocTreePath(
                new DocTreePath(trees.getPath(element), trees.getDocCommentTree(element)), node));
    return Optional.ofNullable(referencedElement);
  }

  public String formatName(TsElement tsElement) {
    String namespace = tsElement.getNamespace();
    String jsName = tsElement.getName();
    return namespace.isEmpty() ? jsName : (namespace + "." + jsName);
  }

  private String formatLabel(String label) {
    if (nonNull(label) && !label.trim().isEmpty()) {
      return " | " + label;
    }
    return "";
  }

  @Override
  public String visitSee(SeeTree node, Element element) {
    return "@see "
        + node.getReference().stream()
            .map(
                docTree ->
                    getElementFromReference(docTree, element)
                        .map(refElement -> "{@link " + docTree.accept(this, element) + "}")
                        .orElse(docTree.accept(this, element)))
            .collect(Collectors.joining());
  }

  @Override
  public String visitDocComment(DocCommentTree node, Element element) {
    String body =
        node.getFullBody().stream()
            .map(docTree -> visit(docTree, element))
            .collect(Collectors.joining());
    String tags =
        node.getBlockTags().stream()
            .map(tag -> tag.accept(this, element))
            .collect(Collectors.joining("\n"));
    return " " + body + "\n" + tags;
  }

  @Override
  public String visitLiteral(LiteralTree node, Element element) {
    if (nonNull(node.getBody())) {
      String body = node.getBody().getBody();
      String wrap = "`";
      if (body.contains(System.lineSeparator())) {
        wrap = "```";
      }
      return wrap + node.getBody().getBody() + wrap;
    }
    return super.visitLiteral(node, element);
  }

  @Override
  public String visitText(TextTree node, Element element) {
    return "" + node.getBody();
  }
}
