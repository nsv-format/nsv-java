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
import java.util.List;

// Parse from string
List<List<String>> data = Nsv.parse("first\nrow\n\nsecond\nrow\n\n");

// Read from InputStream
List<List<String>> data = Nsv.read(inputStream);

// Read from Reader
List<List<String>> data = Nsv.read(new FileReader("data.nsv"));
```

### Writing NSV

```java
import org.nsvformat.Nsv;
import java.util.List;

var rows = List.of(
    List.of("first", "row"),
    List.of("second", "row")
);

// Format to string
String nsv = Nsv.format(rows);

// Write to OutputStream
Nsv.write(rows, outputStream);

// Write to Writer
Nsv.write(rows, new FileWriter("output.nsv"));
```

### Escape Sequences

NSV handles special characters using backslash escaping:
- `\\` represents a literal backslash
- `\n` represents a newline character
- `\` (alone) represents an empty string

```java
// Example with special characters
var data = List.of(
    List.of("field with\nnewline", "field with\\backslash"),
    List.of("", "empty field")
);
String nsv = Nsv.format(data);
// Output: "field with\\nnewline\nfield with\\\\backslash\n\n\\\nempty field\n\n"
```

## API Reference

### Static Methods

- `parse(String s)` - Parse NSV string into rows
- `format(List<List<String>> data)` - Format rows as NSV string
- `read(InputStream in)` - Read NSV from input stream
- `read(Reader reader)` - Read NSV from reader
- `write(List<List<String>> data, OutputStream out)` - Write NSV to output stream
- `write(List<List<String>> data, Writer writer)` - Write NSV to writer

## Requirements

- Java 17 or higher

