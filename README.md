[![Build Status](https://github.com/giuliopulina/swagger-validator-maven-plugin/actions/workflows/maven.yml/badge.svg)]

# swagger-validator-maven-plugin

This maven plugin allows to validate Swagger 2.0 yaml and json files.

## Plugin configuration

```xml
      <plugin>
        <groupId>com.github.giuliopulina</groupId>
        <artifactId>swagger-validator-maven-plugin</artifactId>
        <version>1.2.8-giuliopulina</version>
        <executions>
          <execution>
            <id>validate</id>
            <phase>validate</phase>
            <goals>
              <goal>validate</goal>
            </goals>
            <configuration>
                <failOnErrors>true</failOnErrors>
              <includes>
                <include>src/main/resources/swagger-*.yml</include>
                <include>src/main/resources/swagger-*.json</include>
                <!-- other <include> may be added -->
              </includes>
              <excludes>
                <exclude>src/main/resources/swagger-do-not-validate*.yml</exclude>
                <!-- <exclude> is optional, others may be added -->
              </excludes>
              <!-- package names where custom validators are located -->
              <customModelValidatorsPackage>com.example.validators</customModelValidatorsPackage>
              <customPathValidatorsPackage>com.example.validators</customPathValidatorsPackage>
            </configuration>
          </execution>
        </executions>
        <dependencies>
            <!-- dependency with custom validators -->
            <dependency>
                <groupId>com.example</groupId>
                <artifactId>custom-validators</artifactId>
                <version>${project.version}</version>
            </dependency>
        </dependencies>
      </plugin>
```

Validation failures make the build fail by default. You can change this by setting `<failOnErrors>false</failOnErrors>`.

You can add your custom validators and provide plugin with them. Extend from ModelValidatorTemplate or PathValidatorTemplate 
classes for writing your validators and override necessary validation methods. See ReferenceValidator and PathValidator as examples.

Requires at least JDK 11, CI test matrix is configured with JDK11, JDK17, JDK21.

## Source code
The source code is available on GitHub : https://github.com/giuliopulina/swagger-validator-maven-plugin

### How to build source
Use Maven >= 3.9.6 and a JDK >=11, and run `mvn clean verify` in the root directory of the git repository.

### How to create a release
`mvn release:prepare release:perform` and answer the questions about version number.

Then push the commits and tags to github.

## License
This software is licensed under the Apache Software License version 2.0, see [LICENSE.txt](LICENSE.txt).
