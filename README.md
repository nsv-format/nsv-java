# NSV Java

Java implementation of the [NSV (Newline-Separated Values)](https://nsv-format.org) format.

## Installation

Maven Central publishing is planned. For now, the package is available via [GitHub Packages](https://github.com/nsv-format/nsv-java/packages), which requires a GitHub account and a personal access token with `read:packages` scope.

### Maven

Add the repository to your `pom.xml` or `~/.m2/settings.xml`:

```xml
<repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/nsv-format/nsv-java</url>
</repository>
```

Then add the dependency:

```xml
<dependency>
    <groupId>org.nsv-format</groupId>
    <artifactId>nsv-java</artifactId>
    <version>0.2.0</version>
</dependency>
```

### Gradle

```groovy
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/nsv-format/nsv-java")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation 'org.nsv-format:nsv-java:0.2.0'
}
```

## Usage

### Basic encoding/decoding

```java
import org.nsvformat.Nsv;
import java.util.List;

// Decode from string
var data = Nsv.decode("a\nb\nc\n\nd\ne\nf\n\n");
// [[a, b, c], [d, e, f]]

// Encode to string
var encoded = Nsv.encode(List.of(List.of("a", "b"), List.of("c", "d")));
// "a\nb\n\nc\nd\n\n"
```

### Cell-level escaping

```java
import org.nsvformat.Nsv;

// Escape individual cells
Nsv.escape("hello\nworld");  // "hello\\nworld"
Nsv.escape("");              // "\\"

// Unescape
Nsv.unescape("hello\\nworld");  // "hello\nworld"
Nsv.unescape("\\");             // ""
```

### Incremental reading (streaming, tailing)

```java
import org.nsvformat.Reader;
import java.io.File;
import java.util.List;

// From file
var reader = Reader.fromFile(new File("data.nsv"));
while (reader.hasNext()) {
    List<String> row = reader.next();
    System.out.println(row);  // Each row is List<String>
}

// From any java.io.Reader
var reader2 = new Reader(someJavaReader);
```

**Resumable semantics**: Reader preserves partial state across EOF, enabling tailing/streaming:

```java
import org.nsvformat.Reader;
import java.util.List;

var reader = new Reader(socketInputStream);

while (true) {
    if (reader.hasNext()) {
        List<String> row = reader.next();
        processRow(row);
    } else {
        // No complete row available, wait for more data
        Thread.sleep(100);
    }
}
```

### Incremental writing

```java
import org.nsvformat.Writer;
import java.io.File;
import java.io.IOException;
import java.util.List;

var writer = Writer.fromFile(new File("output.nsv"));

writer.writeRow(List.of("a", "b", "c"));
writer.writeRow(List.of("d", "e", "f"));

// Or write multiple rows at once
writer.writeRows(data);
```

### Structural operations (spill/unspill)

```java
import org.nsvformat.Util;
import java.util.List;

// Flatten with terminators
var flat = Util.spill(List.of(List.of("a", "b"), List.of("c")), "");
// ["a", "b", "", "c", ""]

// Recover structure
var structured = Util.unspill(flat, "");
// [["a", "b"], ["c"]]

// Generic over types
Util.spill(List.of(List.of(1, 2), List.of(3)), -1);
// [1, 2, -1, 3, -1]
```

## API

### Nsv class

- `decode(String s)` - Parse NSV string to list of rows
- `encode(List<List<String>> data)` - Serialize rows to NSV string
- `escape(String s)` - Escape cell content (handles `\`, `\n`, empty string)
- `unescape(String s)` - Unescape cell content

### Reader class

Iterator for row-by-row reading with resumable semantics:

- `new Reader(java.io.Reader reader)` - Construct from any Reader
- `Reader.fromFile(File file)` - Convenience factory (buffered)
- `Reader.fromPath(Path path)` - Convenience factory (buffered)
- `hasNext()` - Check if complete row available (non-terminal)
- `next()` - Get next row

### Writer class

- `new Writer(java.io.Writer writer)` - Construct from any Writer
- `Writer.fromFile(File file)` - Convenience factory (buffered)
- `Writer.fromPath(Path path)` - Convenience factory (buffered)
- `writeRow(Iterable<String> row)` - Write single row
- `writeRows(Iterable<? extends Iterable<String>> rows)` - Write multiple rows

### Util class

Generic structural operations:

- `<T> spill(Iterable<? extends Iterable<T>> seqseq, T marker)` - Flatten dimension with terminators
- `<T> unspill(Iterable<T> seq, T marker)` - Recover dimension by picking up terminators

## Requirements

- Java 11 or higher
