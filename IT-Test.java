import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-unit.properties")
@AutoConfigureMockMvc
public class DataSourceControllerTest {

   @Autowired
   private MockMvc mvc;
   
   @MockBean
   private UserManagementClient userManagementClient;

   @Test
   public void testAddDataSource() throws Exception {
      String areCode = "ADK21";
      String capId = "2130";
      String sysId = "ADK";
      String clientId = "999";
      Boolean importStop = false;
      Boolean initAllowed = true;
      Boolean rdrStop = false;
      String tenantCode = "SAG";
      String exportDirectory = "IT_TEST_2023";
      Byte cstId = 1;
      
      Set<String> userRoles = new HashSet<>();
      userRoles.add(RoleEnum.BACK_OFFICE.getName());
      when(userManagementClient.userRoles(any())).thenReturn(userRoles);

      DataSourceApiModel dataSourceApiModel = new DataSourceApiModel(null, areCode, capId, sysId, clientId, importStop,
               null, initAllowed, rdrStop, tenantCode, exportDirectory, null, null, exportDirectory, cstId, null);

      String request = LogUtility.jsonFormattedToString(dataSourceApiModel);

      // @formatter:off
      this.mvc.perform(post("/dataSource")
               .contentType(MediaType.APPLICATION_JSON).content(request)
               .header("X-Gid", "IT-Test"))
         .andDo(print())
         .andExpect(status().isOk())
         .andExpect(jsonPath("$").isNotEmpty())
         .andExpect(jsonPath("$.areCode").isNotEmpty())
         .andExpect(jsonPath("$.capId").isNotEmpty())
         .andExpect(jsonPath("$.id").isNotEmpty())
         .andExpect(jsonPath("$.created").isNotEmpty())
         .andExpect(jsonPath("$.areCode", is("ADK21")))
         .andExpect(jsonPath("$.capId", is("2130")));
      // @formatter:on
   }

   @Test
   public void testGetDataSource() throws Exception {
      Integer id = 331;

      // @formatter:off
      this.mvc.perform(get("/dataSource/"+ id)
               .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isNotEmpty())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.areCode").isNotEmpty())
            .andExpect(jsonPath("$.areCode", is("5672")))
            .andExpect(jsonPath("$.capId", is("1204")))
            .andExpect(jsonPath("$.sysId", is("AAA")))
            .andExpect(jsonPath("$.clientId", is("100")))
            .andExpect(jsonPath("$.id", is(331)));
      // @formatter:on
   }

   @Test
   public void testDataSourceNotFound() throws Exception {
      Integer id = -21;

      // @formatter:off
      this.mvc.perform(get("/dataSource/"+ id)
               .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$").isNotEmpty())
            .andExpect(jsonPath("$.message", is("DataSource not found with id : -21")));
      // @formatter:on
   }

   @Test
   public void testSearchByAreCode() throws Exception {
      String areCode = "5160";
      Boolean importStop = null;
      String sysId = null;
      String lastDeliveryDateUntil = null;
      String lastDeliveryDateFrom = null;
      DataSourceSearchParam dataSourceSearchParam = new DataSourceSearchParam(0, 0, null, areCode, importStop, sysId,
               lastDeliveryDateUntil, lastDeliveryDateFrom);

      String request = LogUtility.jsonFormattedToString(dataSourceSearchParam);

      // @formatter:off
      this.mvc.perform(post("/dataSource/search")
               .contentType(MediaType.APPLICATION_JSON).content(request))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(equalTo(1))))
            .andExpect(jsonPath("$.content[0].id", is(37)))
            .andExpect(jsonPath("$.content[0].areCode", is("5160")));
      // @formatter:on
   }
   
   @Test
   public void testSearchByAreCodes() throws Exception {
      List<String> areCodes = Arrays.asList("5160", "469G");
      DataSourceAreCodesSearchRequest dataSourceAreCodesSearchRequest = new DataSourceAreCodesSearchRequest();
      dataSourceAreCodesSearchRequest.setAreCodes(areCodes);

      String request = LogUtility.jsonFormattedToString(dataSourceAreCodesSearchRequest);

      // @formatter:off
      this.mvc.perform(post("/dataSource/search/areCodes")
               .contentType(MediaType.APPLICATION_JSON).content(request))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(equalTo(2))));
      // @formatter:on
   }

   @Test
   public void testSearchBySysId() throws Exception {
      String areCode = null;
      Boolean importStop = null;
      String sysId = "E1P";
      String lastDeliveryDateUntil = null;
      String lastDeliveryDateFrom = null;
      DataSourceSearchParam dataSourceSearchParam = new DataSourceSearchParam(0, 0, null, areCode, importStop, sysId,
               lastDeliveryDateUntil, lastDeliveryDateFrom);

      String request = LogUtility.jsonFormattedToString(dataSourceSearchParam);

      // @formatter:off
      this.mvc.perform(post("/dataSource/search")
               .contentType(MediaType.APPLICATION_JSON).content(request))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(equalTo(2))))
            .andExpect(jsonPath("$.content[0].id", is(56)))
            .andExpect(jsonPath("$.content[0].areCode", is("5620")))
            .andExpect(jsonPath("$.content[1].id", is(57)))
            .andExpect(jsonPath("$.content[1].areCode", is("4433")));
      // @formatter:on
   }

   @Test
   public void testSearchByImportStop() throws Exception {
      String areCode = null;
      Boolean importStop = false;
      String sysId = null;
      String lastDeliveryDateUntil = null;
      String lastDeliveryDateFrom = null;
      DataSourceSearchParam dataSourceSearchParam = new DataSourceSearchParam(0, 0, null, areCode, importStop, sysId,
               lastDeliveryDateUntil, lastDeliveryDateFrom);

      String request = LogUtility.jsonFormattedToString(dataSourceSearchParam);

      // @formatter:off
      this.mvc.perform(post("/dataSource/search")
               .contentType(MediaType.APPLICATION_JSON).content(request))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(equalTo(11))));
      // @formatter:on
   }
   
    @Test
    public void testSearchByLastDeliveryDateUntil() throws Exception {
    String areCode = null;
    Boolean importStop = null;
    String sysId = null;
    String lastDeliveryDateUntil = "2022-11-21T07:58:40.000Z";
    String lastDeliveryDateFrom = null;
    DataSourceSearchParam dataSourceSearchParam = new DataSourceSearchParam(0, 0, null, areCode, importStop, sysId,
    lastDeliveryDateUntil, lastDeliveryDateFrom);
   
    String request = LogUtility.jsonFormattedToString(dataSourceSearchParam);
   
      // @formatter:off
      this.mvc.perform(post("/dataSource/search")
               .contentType(MediaType.APPLICATION_JSON).content(request))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(equalTo(8))));
      // @formatter:on
    }
    
    @Test
    public void testSearchByLastDeliveryDateFrom() throws Exception {
    String areCode = null;
    Boolean importStop = null;
    String sysId = null;
    String lastDeliveryDateUntil = null;
    String lastDeliveryDateFrom = "2022-11-20T07:58:40.000Z";
    DataSourceSearchParam dataSourceSearchParam = new DataSourceSearchParam(0, 0, null, areCode, importStop, sysId,
    lastDeliveryDateUntil, lastDeliveryDateFrom);
   
    String request = LogUtility.jsonFormattedToString(dataSourceSearchParam);
   
      // @formatter:off
      this.mvc.perform(post("/dataSource/search")
               .contentType(MediaType.APPLICATION_JSON).content(request))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(equalTo(8))));
      // @formatter:on
    }
    
    @Test
    public void testUpdateDataSource() throws Exception {
       Integer id = 9;
       String areCode = "ADK21";
       String capId = "2130";
       String sysId = "ADK";
       String clientId = "999";
       Boolean importStop = false;
       Boolean initAllowed = true;
       Boolean rdrStop = false;
       String tenantCode = "SAG";
       String exportDirectory = "IT_TEST_2023";
       Byte cstId = 1;
       
       Set<String> userRoles = new HashSet<>();
       userRoles.add(RoleEnum.BACK_OFFICE.getName());
       when(userManagementClient.userRoles(any())).thenReturn(userRoles);
       
       DataSourceApiModel dataSourceApiModel = new DataSourceApiModel(id, areCode, capId, sysId, clientId, importStop,
                null, initAllowed, rdrStop, tenantCode, exportDirectory, null, null, exportDirectory, cstId, null);

       String request = LogUtility.jsonFormattedToString(dataSourceApiModel);

       // @formatter:off
       this.mvc.perform(patch("/dataSource/"+id)
                .contentType(MediaType.APPLICATION_JSON).content(request)
                .header("X-Gid", "IT-Test"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$").isNotEmpty())
          .andExpect(jsonPath("$.areCode").isNotEmpty())
          .andExpect(jsonPath("$.id").isNotEmpty())
          .andExpect(jsonPath("$.areCode", is("ADK21")))
          .andExpect(jsonPath("$.sysId", is("ADK")));
       // @formatter:on
    }
}
