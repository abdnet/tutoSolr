import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Before;
import org.junit.Test;
import org.sid.tuto.ProductBean;
import org.sid.tuto.SolrJavaIntegration;

public class SolrJavaIntegrationTest {

    private SolrJavaIntegration solrJavaIntegration;

    @Before
    public void setUp() throws Exception {

        solrJavaIntegration = new SolrJavaIntegration("http://localhost:8081/solr/bigboxstore");
        solrJavaIntegration.addSolrDocument("123456", "Kenmore Dishwasher", "599.99");
    }

    @Test
    public void whenAdd_thenVerifyAddedByQueryOnId() throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery();
        query.set("q", "id:123456");
        QueryResponse response = null;

        response = solrJavaIntegration.getSolrClient().query(query);

        SolrDocumentList docList = response.getResults();
        assertEquals(1, docList.getNumFound());

        for (SolrDocument doc : docList) {
            assertEquals("Kenmore Dishwasher", (String) doc.getFieldValue("name"));
            assertEquals((Double) 599.99, (Double) doc.getFieldValue("price"));
        }
    }

    @Test
    public void whenAdd_thenVerifyAddedByQueryOnPrice() throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery();
        query.set("q", "price:599.99");
        QueryResponse response = null;

        response = solrJavaIntegration.getSolrClient().query(query);

        SolrDocumentList docList = response.getResults();
        assertEquals(1, docList.getNumFound());

        for (SolrDocument doc : docList) {
            assertEquals("123456", (String) doc.getFieldValue("id"));
            assertEquals((Double) 599.99, (Double) doc.getFieldValue("price"));
        }
    }

    @Test
    public void whenAdd_thenVerifyAddedByQuery() throws SolrServerException, IOException {

        SolrDocument doc = solrJavaIntegration.getSolrClient().getById("123456");
        assertEquals("Kenmore Dishwasher", (String) doc.getFieldValue("name"));
        assertEquals((Double) 599.99, (Double) doc.getFieldValue("price"));
    }

    @Test
    public void whenAddBean_thenVerifyAddedByQuery() throws SolrServerException, IOException {

        ProductBean pBean = new ProductBean("888", "Apple iPhone 7s", "299.99");
        ProductBean pBean1 = new ProductBean("45", "Apple iPhone 7s", "299.99");
        ProductBean pBean2 = new ProductBean("12", "Apple iPhone 7s", "299.99");

        solrJavaIntegration.addProductBean(pBean);
        solrJavaIntegration.addProductBean(pBean1);
        solrJavaIntegration.addProductBean(pBean2);

        SolrDocument doc = solrJavaIntegration.getSolrClient().getById("888");
        assertEquals("Apple iPhone 6s", (String) doc.getFieldValue("name"));
        assertEquals((Double) 299.99, (Double) doc.getFieldValue("price"));
    }

    @Test
    public void whenDeleteById_thenVerifyDeleted() throws SolrServerException, IOException {

        solrJavaIntegration.deleteSolrDocumentById("123456");

        SolrQuery query = new SolrQuery();
        query.set("q", "id:123456");
        QueryResponse response = solrJavaIntegration.getSolrClient().query(query);

        SolrDocumentList docList = response.getResults();
        assertEquals(0, docList.getNumFound());
    }

    @Test
    public void whenDeleteByQuery_thenVerifyDeleted() throws SolrServerException, IOException {

        solrJavaIntegration.deleteSolrDocumentByQuery("name:Kenmore Dishwasher");

        SolrQuery query = new SolrQuery();
        query.set("q", "id:123456");
        QueryResponse response = null;

        response = solrJavaIntegration.getSolrClient().query(query);

        SolrDocumentList docList = response.getResults();
        assertEquals(0, docList.getNumFound());
    }
}