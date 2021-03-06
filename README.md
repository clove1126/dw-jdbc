# dw-jdbc

dw-jdbc is a JDBC driver for connecting to datasets hosted on data.world.
It can be used to provide read-only access to any dataset provided by data.world
from any JVM language.  dw-jdbc supports query access both in dwSQL
(data.world's SQL dialect) and in SPARQL 1.1, the native query language
for semantic web data sources.


## JDBC URLs

JDBC connects to data source based on a provided JDBC url.  data.world
JDBC urls have the form

    jdbc:data:world:[language]:[user id]:[dataset id]

where:

* `[language]` is either `sql` or `sparql`
* `[user id]` is the data.world id of the dataset owner
* `[dataset id]` is the data.world id of the dataset

You can extract these ids from the dataset home page url: `https://data.world/[user id]/[dataset id]`.

## Sample code (Java 8)

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;


final String QUERY = "select * from HallOfFame where playerID = ? order by yearid, playerID limit 10";
final String URL = "jdbc:data:world:sql:dave:lahman-sabremetrics-dataset";


try (final Connection connection =    // get a connection to the database, which will automatically be closed when done
         DriverManager.getConnection(URL, "<your user name>", "<your API token>");
     final PreparedStatement statement = // get a connection to the database, which will automatically be closed when done
         connection.prepareStatement(QUERY)) {
    statement.setString(1, "alexape01"); //bind a query parameter
    try (final ResultSet resultSet = statement.executeQuery()) { //execute the query
        ResultSetMetaData rsmd = resultSet.getMetaData();  //print out the column headers
        int columnsNumber = rsmd.getColumnCount();
        for (int i = 1; i <= columnsNumber; i++) {
            if (i > 1) System.out.print(",  ");
            System.out.print(rsmd.getColumnName(i));
        }
        System.out.println("");
        while (resultSet.next()) { //loop through the query results
            for (int i = 1; i <= columnsNumber; i++) { //print out the column headers
                if (i > 1) System.out.print(",  ");
                String columnValue = resultSet.getString(i);
                System.out.print(columnValue);
            }
            System.out.println("");

            // Note: when calling ResultSet.getObject() prefer the version that takes an explicit Class argument:
            // Integer n = resultSet.getObject(param, Integer.class);
        }
    }
}
```

## Using dw-jdbc in your project

If using Maven, you can use dw-jdbc by just including the following in your pom.xml file:

```xml
<dependency>
    <groupId>world.data</groupId>
    <artifactId>dw-jdbc</artifactId>
    <version>0.4.1</version>
</dependency>
```

See [this link at Maven Central](https://search.maven.org/#search%7Cga%7C1%7Cdw-jdbc) to find the latest version
number for the JDBC driver.

For some database tools it's easier to install the jdbc driver if it's a single jar.  For this reason we also
provide dw-jdbc bundled with all its dependencies under the following:

```xml
<dependency>
    <groupId>world.data</groupId>
    <artifactId>dw-jdbc</artifactId>
    <classifier>shaded</classifier>
    <version>0.4.1</version>
</dependency>
```


## Finding your Token

1. Visit https://data.world
2. Visit your user settings, and click the advanced tab.
3. Copy your token.

## Features

* JDBC 4.2

* The driver only supports read-only queries.  It does not support INSERT/UPDATE/DELETE, DDL, or transactions.

* Queries can be written in [SPARQL 1.1](https://www.w3.org/TR/sparql11-query/) or in the SQL dialect described at https://docs.data.world/tutorials/dwsql/.

* [SQL-only] Table and column metadata via `java.sql.DatabaseMetaData`.

* [SQL-only] Support for positional parameters via `java.sql.PreparedStatement`.

* [SPARQL-only] Support for named parameters via `java.sql.CallableStatement`.

   * For example, `CallableStatement.setString("name", "value")` will bind the string `value` to `?name` within the query.

* The `DataWorldStatement.setJdbcCompatibilityLevel(JdbcCompatibility)` method can be used to adjust how the JDBC driver maps query results to Java objects in `java.sql.ResultSetMetaData`.  This is particularly relevant to SPARQL queries where result types in a column can vary from row to row.

   * `JdbcCompatibility.LOW` - No assumptions are made about types.  `ResultSetMetaData.getColumnType()` returns `java.sql.Types.OTHER` and `ResultSet.getObject()` returns `world.data.jdbc.model.Node`.
   
   * `JdbcCompatibility.MEDIUM` - [SPARQL default] All columns are typed as string.  `ResultSetMetaData.getColumnType()` returns `java.sql.Types.NVARCHAR` and `ResultSet.getObject()` returns `java.lang.String`.
   
   * `JdbcCompatibility.HIGH` - [SQL default] Columns are typed based on the underlying data, either using table metadata (SQL) or by inspecting the first row of the response (SPARQL).
