# JSInterop TS defs

This annotation processor generates TypeScript definitions from Java classes annotated with JsInterop annotations.

### Maven dependencies

- Annotations :

```xml

<parent>
    <artifactId>tsinterop-annotations</artifactId>
    <groupId>com.vertispan.tsdefs</groupId>
    <version>HEAD-SNAPSHOT</version>
</parent>
```
- Processor

```xml

<depndency>
    <artifactId>tsinterop-processor</artifactId>
    <groupId>com.vertispan.tsdefs</groupId>
    <version>HEAD-SNAPSHOT</version>
</depndency>
```

### Controlling the output

The output from this processor can be controlled by adding custom annotations to the java classes.

- `@TsModule` : use this annotation on a package-info to define the name of the generated `.d.ts` file, by default the name is `types`.
- `@TsName` : Use this annotation on a java type to change the its name or name space in the typescript definitions.

    **Example** :
    
    The following Java type

    ```java
    import com.vertispan.tsdefs.annotations.TsName;
    import jsinterop.annotations.JsPackage;
    import jsinterop.annotations.JsType;
    
    @TsName(name = "TypeSimple", namespace = "examples")
    public interface SimpleJsType {
    
        @JsMethod
        String name();
    }
    ```
    
    Will be exported as TypeScript definition as :
    
    ```typescript
    export class TypeSimple {
        name:string;
    
        constructor();
    }
    ```
- `@TsTypeRef` : Use this annotation on a type, parameter, method, or field to override the element type by the type of another element.

    **Example** :
        
    The following Java type

    ```java
    import com.vertispan.tsdefs.annotations.TsName;
    import jsinterop.annotations.JsPackage;
    import jsinterop.annotations.JsType;
    
    @TsName(name = "TypeSimple", namespace = "examples")
    public interface SimpleJsType {
    
        @TsTypeRef(Boolean.class)
        public String name();
    }
    ```
    
    Will be exported as TypeScript definition as :
    
    ```typescript
    export class TypeSimple {
        name:boolean;
    
        constructor();
    }
    ```
- `@TsTypeDef` : Use this annotation on a type to define a custom type, when this type is referenced by `@TsTypeRef` the custom TypeScript type specified by the `TsTypeDef` will be used instead of the actual element type.

  **Example** :

  The following Java type

    ```java
  @JsType(name = "SimpleType", namespace = "com.vertispan")
  @TsName(name = "TypeSimple", namespace = JsPackage.GLOBAL)
  public class SimpleJsType {
  
      @TsTypeRef(AnotherType.class)
      public Integer name;
  
      @JsType
      @TsTypeDef(name="IdType", tsType = "number")
      public static class AnotherType {
          public String id;
      }
  }
    ```

  Will be exported as TypeScript definition as :

  ```typescript
  type IdType = number;
  
  export class TypeSimple {
  name:IdType;
  
      constructor();
  }
  ```
  
- `@TsInterface` : Use this annotation on a class type to force exporting the class as an interface, useful for cases when the class will only export methods and should not be instantiated.
- `@TsIgnore` : Use this annotation on a type to prevent it from being exported, but the children of this type will still inherit its exportable members.