package world.data.jdbc;

import fi.iki.elonen.NanoHTTPD;
import org.apache.commons.io.IOUtils;
import org.junit.ClassRule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

public class SparqlTest {

    private static String lastUri;
    private static String resultResourceName;
    private static String resultMimeType;

    @ClassRule
    public static final NanoHTTPDResource proxiedServer = new NanoHTTPDResource(3333) {
        @Override
        protected NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) throws Exception {
            final String queryParameterString = session.getQueryParameterString();
            if (queryParameterString != null) {
                lastUri = "http://localhost:3333" + session.getUri() + '?' + queryParameterString;
            } else {
                lastUri = "http://localhost:3333" + session.getUri();
            }
            return newResponse(NanoHTTPD.Response.Status.OK, resultMimeType, IOUtils.toString(SparqlTest.class.getResourceAsStream("/" + resultResourceName)));
        }
    };

    @org.junit.Test
    public void test() throws Exception {
        resultResourceName = "select.json";
        resultMimeType = "application/json";

        try (final Connection connection = DriverManager.getConnection("jdbc:data:world:sparql:dave:lahman-sabremetrics-dataset", TestConfigSource.testProperties());
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery("select ?s ?p ?o where{?s ?p ?o.} limit 10")) {
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                System.out.print(rsmd.getColumnName(i));
            }
            System.out.println("");
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(columnValue);
                }
                System.out.println("");
            }
        }
        assertThat(lastUri).isEqualTo("http://localhost:3333/sparql/dave/lahman-sabremetrics-dataset?query=SELECT++%3Fs+%3Fp+%3Fo%0AWHERE%0A++%7B+%3Fs++%3Fp++%3Fo+%7D%0ALIMIT+++10%0A");
    }

    @org.junit.Test
    public void testAsk() throws Exception {
        resultMimeType = "application/json";
        resultResourceName = "ask.json";
        try (final Connection connection = DriverManager.getConnection("jdbc:data:world:sparql:dave:lahman-sabremetrics-dataset", TestConfigSource.testProperties());
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery("ask{?s ?p ?o.}")) {
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                System.out.print(rsmd.getColumnName(i));
            }
            System.out.println("");
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(columnValue);
                }
                System.out.println("");
            }
        }
        assertThat(lastUri).isEqualTo("http://localhost:3333/sparql/dave/lahman-sabremetrics-dataset?query=ASK%0AWHERE%0A++%7B+%3Fs++%3Fp++%3Fo+%7D%0A");
    }

    @org.junit.Test
    public void testDescribe() throws Exception {
        resultResourceName = "describe.json";
        resultMimeType = "text/turtle";

        try (final Connection connection = DriverManager.getConnection("jdbc:data:world:sparql:dave:lahman-sabremetrics-dataset", TestConfigSource.testProperties());
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery("DESCRIBE ?s where{?s ?p ?o.} limit 10")) {
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                System.out.print(rsmd.getColumnName(i));
            }
            System.out.println("");
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(columnValue);
                }
                System.out.println("");
            }
        }
        assertThat(lastUri).isEqualTo("http://localhost:3333/sparql/dave/lahman-sabremetrics-dataset?query=DESCRIBE+%3Fs%0AWHERE%0A++%7B+%3Fs++%3Fp++%3Fo+%7D%0ALIMIT+++10%0A");
    }

    @org.junit.Test
    public void testConstruct() throws Exception {
        resultResourceName = "construct.json";
        resultMimeType = "text/turtle";

        try (final Connection connection = DriverManager.getConnection("jdbc:data:world:sparql:dave:lahman-sabremetrics-dataset", TestConfigSource.testProperties());
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery("Construct{?o ?p ?s} where{?s ?p ?o.} limit 10")) {
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                System.out.print(rsmd.getColumnName(i));
            }
            System.out.println("");
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(columnValue);
                }
                System.out.println("");
            }
        }
        assertThat(lastUri).isEqualTo("http://localhost:3333/sparql/dave/lahman-sabremetrics-dataset?query=CONSTRUCT+%0A++%7B+%0A++++%3Fo+%3Fp+%3Fs+.%0A++%7D%0AWHERE%0A++%7B+%3Fs++%3Fp++%3Fo+%7D%0ALIMIT+++10%0A");
    }

    @Test
    public void testPrepared() throws Exception {
        resultResourceName = "select.json";
        resultMimeType = "application/json";

        try (final Connection connection = DriverManager.getConnection("jdbc:data:world:sparql:dave:lahman-sabremetrics-dataset", TestConfigSource.testProperties());
             final PreparedStatement statement = connection.prepareStatement("select ?s ?p ?o where{?s ?p ?o.} limit 10");
             final ResultSet resultSet = statement.executeQuery()) {
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                System.out.print(rsmd.getColumnName(i));
            }
            System.out.println("");
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(columnValue);
                }
                System.out.println("");
            }
        }
        assertThat(lastUri).isEqualTo("http://localhost:3333/sparql/dave/lahman-sabremetrics-dataset?query=SELECT++%3Fs+%3Fp+%3Fo%0AWHERE%0A++%7B+%3Fs++%3Fp++%3Fo+%7D%0ALIMIT+++10%0A");
    }

    @org.junit.Test(expected = SQLException.class)
    public void testBadQuery() throws Exception {
        try (final Connection connection = DriverManager.getConnection("jdbc:data:world:sparql:dave:lahman-sabremetrics-dataset", TestConfigSource.testProperties());
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery("select ?s ?p ?o where{?s ?p ?o.} limit ?")) {

        }
    }
}