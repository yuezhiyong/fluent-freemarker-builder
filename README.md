## FreeMarker Fluent Builder
A fluent API for building FreeMarker templates with compile-time validation and type safety.

### 📋 Overview

FreeMarker Fluent Builder provides a type-safe, fluent interface for constructing FreeMarker templates programmatically. It offers:

* Fluent API for intuitive template construction 
* Compile-time validation of variable references and types
* Scope-aware validation for list items, macros, and conditional blocks
* Type inference for collections and complex objects
* Error detection with detailed error messages and suggestions

### 🚀 Features
#### Fluent Template Building
```java

FtlBuilder builder = FtlBuilder.create(context);
builder
    .text("User: ")
    .var("user.name")
    .text(" (Age: ")
    .var("user.age")
    .text(")\n")
    .list("order", "orders", listBuilder -> 
        listBuilder
            .text("Order ")
            .var("order.id")
            .text(": $")
            .var("order.amount")
            .text("\n")
    );

```

#### Type-Safe Validation
```java
// Automatically validates variable existence and field access
builder.var("user.nonExistentField"); // Throws validation error
```

#### Scope Management

```java

builder.list("item", "items", listBuilder -> {
    listBuilder.var("item"); // Valid - item is in scope
});
builder.var("item"); // Invalid - item is out of scope
```

### 📦 Installation

#### maven

```xml
<dependency>
    <groupId>com.yourcompany</groupId>
    <artifactId>freemarker-fluent-builder</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### gradle
```groovy
implementation 'com.yourcompany:freemarker-fluent-builder:1.0.0'
```

### 🛠️ Usage

#### Basic Setup
```java

// Create context with variables
FluentFreemarkerContext context = FluentFreemarkerContext.create()
    .var("user", new User("John", 25))
    .var("orders", Arrays.asList(new Order("ORD001", 100.0)))
    .at("template.ftl");

// Create builder
FtlBuilder builder = FtlBuilder.create(context);
```

#### Text and Variables

```java
builder
    .text("Hello ")
    .var("user.name")
    .text("!")
    .newline();
```


#### Conditional Blocks

```java
builder.ifBlock("user.age > 18", ifBuilder -> 
    ifBuilder.text("Adult user")
);
```

#### Lists

```java
builder.list("order", "orders", listBuilder -> 
    listBuilder
        .text("Order: ")
        .var("order.id")
        .text(" - $")
        .var("order.amount")
        .newline()
);
```

#### Assignments

```java
builder
    .assign("total", "0")
    .list("order", "orders", listBuilder -> 
        listBuilder.assign("total", "total + order.amount")
    );
```

### 🔍 Validation Features

#### Variable Validation

* Checks if referenced variables exist in context
* Validates field access on objects
* Provides suggestions for typos


#### Scope Validation
* Ensures variables are only used within their valid scope
* Manages list item scopes automatically
* Handles nested scope scenarios

#### Type Validation
* Infers types from collection elements
* Validates field existence on complex objects
* Supports custom type registries


### 🏗️ Architecture
#### Core Components

##### FtlBuilder
Main fluent interface for building templates:

* text() - Add literal text
* var() - Add variable reference
* ifBlock() - Conditional blocks
* list() - List iterations
* assign() - Variable assignments

##### FluentFreemarkerContext

Manages template context and validation:
* Variable registration
* Type registry integration
* Validation configuration

##### Validation System
Multi-layered validation approach:

* Variable Validation - Checks variable existence
* Scope Validation - Ensures proper scope usage
* Type Validation - Validates field access and types

##### Expression Parsing

```java
// Supports complex expressions
"user.age > 18 && user.active == true"
"order.total >= 100.0"
"item.name != ''"
```


### 🧪 Testing
#### Unit Tests

```java
@Test
void testTemplateBuilding() {
    FtlBuilder builder = FtlBuilder.create(context);
    builder.text("Hello ").var("user.name");
    
    List<FtlNode> nodes = builder.build();
    assertEquals(2, nodes.size());
}
```

#### Validation Tests

```java
@Test
void testValidationError() {
    FtlBuilder builder = FtlBuilder.create(context);
    builder.var("undefined.variable");
    
    assertThrows(IllegalStateException.class, builder::build);
}
```

### ⚙️ Configuration

#### Custom Validation

```java
// Custom validation chain
VariableValidationChain customChain = VariableValidationChain.createDefaultChain()
.addValidator(new CustomSecurityValidator());

FluentFreemarkerContext context = FluentFreemarkerContext.create()
.withValidationChain(customChain);
```

#### Type Registry
```java
// Custom type registration
TypeRegistry typeRegistry = TypeRegistryFactory.create(TypeRegistryFactory.TypeRegistryType.CONCURRENT);
typeRegistry.register("CustomType", CustomClass.class);
```


### 🤝 Integration

#### With Existing FreeMarker
```java
// Convert to FreeMarker template string
List<FtlNode> nodes = builder.build();
String templateString = TemplateRenderer.render(nodes);
```

#### Spring Integration

```java
@Configuration
public class FreeMarkerConfig {
    
    @Bean
    public FtlBuilder ftlBuilder() {
        return FtlBuilder.create();
    }
}
```

### 📚 API Reference

#### FtlBuilder Methods
| METHOD | DESCRIPTION |
|--------|-------------|
| text(String)    | Add literal text        |
| var(String)    | Add variable reference         |
|ifBlock(String, Consumer)| Conditional block|
|list(String, String, Consumer) | List iteration|
| assign(String, String) | Variable assignment|
|macro(String, Map, Consumer) | Macro definition |


#### Context Methods
| METHOD | DESCRIPTION |
|--------|-------------|
|var(String, Object) | Register variable|
|at(String) | Set source location |
| withValidationChain(Chain) | Custom validation |