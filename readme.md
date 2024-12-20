# **FlexiEnum Library**

### A lightweight, runtime extensible enumeration system for Java.

---

## **Features**
- **Dynamic Enums:** Add and manage enums at runtime.
- **Serialization/Deserialization:** Seamless integration with JSON using Jackson.
- **Type Safety:** Provides compile-time safety for enum usage.
- **Thread Safety:** Ensures thread-safe operations for enums.
- **Customizable Exceptions:** Handles errors effectively with `FlexiEnumException`.

---
## **Getting Started**
### **Prerequisites**
- Java 8 or above
- Maven or Gradle for dependency management


### **Installation**
1. Clone the repository and include it in your project:

```
    git clone <repository_url>
    cd flexienum
    mvn clean install
```
2. Add the project as a dependency in your build system:

   - For Maven:
    ```
    <dependency>
        <groupId>com.bhawesh_source</groupId>
        <artifactId>flexienum</artifactId>
        <version>1.0.0</version>
    </dependency>
    ```
   - For Gradle:
    ```
    implementation 'com.bhawesh_source:flexienum:1.0.0'
    ```
## **Usage**
### Define Your FlexiEnum Classes ###
Make any class flexi enum, by extending FlexiEnum, defining a String parameterized constructor.
Define default enums as stated in example below
```
public class Region extends FlexiEnum {
    protected Region(String name) {
        super(name);
    }

    public static final Region all = new Region("all");

}
```
### Managing Enums with FlexiEnumStore ###
```
FlexiEnumStore store = FlexiEnumStore.getInstance();

// Add a new runtime enum
Region asia = store.addEnum(Region.class, "Asia");

// Retrieve existing enums
Region all = FlexiEnumStore.valueOf("ALL", Region.class);

// List all enums
List<Region> regions = FlexiEnumStore.values(Region.class);
```
### Serialization & Deserialization ### 
Enable serialization and deserialization with Jackson:

```
ObjectMapper mapper = new ObjectMapper();

// Serialize
String json = mapper.writeValueAsString(Region.ALL);

// Deserialize
Region region = mapper.readValue(json, Region.class);

```

## Testing ## 
Comprehensive unit tests are included in the src/test directory. To run the tests:

```
mvn test
```

## Contributing ##
Contributions are welcome! Please follow these steps:

1. Fork the repository.
2. Create a feature branch: git checkout -b feature/your-feature-name.
3. Commit your changes: git commit -m "Add your message here".
4. Push to the branch: git push origin feature/your-feature-name.
5. Open a pull request.


## License ##
This project is licensed under the [MIT License](https://opensource.org/license/mit).

## Contact ## 
For any questions or suggestions, feel free to contact [bhawesh-source](https://github.com/bhawesh-source).
