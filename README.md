**Note, the functionality provided by this plugin is now built into pitest as of release 1.3.0**

# Export plugin

This plugin writes out the details of generated mutants to disk - a common feature request from academic researcher.

When active the plugin will create a folder structure as follows in the report directory

```
pit-reports\com\example\Foo\com.example.Foo.txt
pit-reports\com\example\Foo\mutants\0\com.example.Foo.class
pit-reports\com\example\Foo\mutants\0\com.example.Foo.txt
pit-reports\com\example\Foo\mutants\0\details.txt
pit-reports\com\example\Foo\mutants\1\com.example.Foo.class
pit-reports\com\example\Foo\mutants\1\com.example.Foo.txt
pit-reports\com\example\Foo\mutants\1\details.txt

pit-reports\com\example\Bar\com.example.Bar.txt
pit-reports\com\example\Bar\mutants\0\com.example.Bar.class
pit-reports\com\example\Bar\mutants\0\com.example.Bar.txt
pit-reports\com\example\Bar\mutants\0\details.txt
pit-reports\com\example\Bar\mutants\1\com.example.Bar.class
pit-reports\com\example\Bar\mutants\1\com.example.Bar.txt
pit-reports\com\example\Bar\mutants\1\details.txt
```
etc etc


## What's in the files?

pit-reports\com\example\Foo\com.example.Foo.txt

and

pit-reports\com\example\Bar\com.example.Bar.txt

Will contain dissasembled versions of the **unmutated** classes.

Similarly the com.exampled.Foo.txt and com.example.Bar.txt files in the mutants directories will contain dissasembled version of each mutant.

The .class files contain the mutant versions of the classes.

The details.txt files contain a description of the mutant class in that folder.

Note that the numbering of the mutants is arbritrary.

## Usage

The plugin requires pitest 1.2.2 or later and Java 7 or higher.

To activate the plugin it must be placed on the classpath of the pitest tool (**not** on the classpath of the project being mutated).

e.g for maven

```xml
    <plugins>
      <plugin>
        <groupId>org.pitest</groupId>
        <artifactId>pitest-maven</artifactId>
        <version>1.2.1-SNAPSHOT</version>
        <dependencies>
          <dependency>
            <groupId>org.pitest.plugins</groupId>
            <artifactId>pitest-export-plugin</artifactId>
            <version>0.1-SNAPSHOT</version>
          </dependency>
        </dependencies>

        <configuration>
		blah
        </configuration>
      </plugin>
   </plugin>
```

or for gradle

```
buildscript {
   repositories {
       mavenCentral()
       maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
   }
   configurations.maybeCreate("pitest")
   dependencies {
       classpath 'info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.1.11'
       pitest 'org.pitest.plugins:pitest-export-plugin:0.1-SNAPSHOT'
   }
}

pitest {
    pitestVersion = "1.2.1-SNAPSHOT"
}
```

