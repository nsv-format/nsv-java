# nsv-java

Java implementation of the NSV (Newline-Separated Values) format.

## Installation

### Maven

```xml
<dependency>
    <groupId>org.nsv-format</groupId>
    <artifactId>nsv-java</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'org.nsv-format:nsv-java:0.1.0'
```

## Usage

### Reading NSV

```java
import org.nsvformat.Nsv;

// From string
var data = Nsv.read("v1\n---\nfirst\nrow\n\nsecond\nrow\n\n");
var metadata = data.metadata();
var rows = data.rows();

// From InputStream
var data = Nsv.read(inputStream);

// From Reader
var data = Nsv.read(new FileReader("data.nsv"));
```

### Writing NSV

```java
import org.nsvformat.Nsv;
import java.util.List;

var rows = List.of(
    List.of("first", "row"),
    List.of("second", "row")
);

// To string
var nsv = Nsv.write(rows);

// With metadata
var metadata = List.of("v1", "table");
var nsv = Nsv.write(rows, metadata);

// To OutputStream
Nsv.write(rows, outputStream);

// To Writer
Nsv.write(rows, new FileWriter("output.nsv"));
```

### Streaming

For large files, use `NsvReader` directly:

```java
import org.nsvformat.NsvReader;

try (var reader = new NsvReader(inputStream)) {
    var metadata = reader.metadata();
    
    // Process rows one by one
    reader.rows().forEach(row -> {
        // Process each row
    });
}
```

## Requirements

- Java 17 or higher

## Features

- [x] Core parsing
- [ ] `table`
