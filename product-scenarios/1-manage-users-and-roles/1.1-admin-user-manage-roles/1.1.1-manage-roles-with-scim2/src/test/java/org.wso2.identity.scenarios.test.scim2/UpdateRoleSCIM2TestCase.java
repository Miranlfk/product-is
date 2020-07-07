/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.identity.scenarios.test.scim2;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.identity.scenarios.commons.SCIM2CommonClient;
import org.wso2.identity.scenarios.commons.ScenarioTestBase;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.wso2.identity.scenarios.commons.util.Constants.IS_HTTPS_URL;
import static org.wso2.identity.scenarios.commons.util.IdentityScenarioUtil.getJSONFromResponse;

public class UpdateRoleSCIM2TestCase extends ScenarioTestBase {

    public static final String ID_ATTRIBUTE = "id";

    private String username;
    private String password;
    private String tenantDomain;
    private CloseableHttpClient client;
    private SCIM2CommonClient scim2Client;
    private String groupId;

    @Factory(dataProvider = "manageRolesConfigProvider")
    public UpdateRoleSCIM2TestCase(String username, String password, String tenantDomain) {

        this.username = username;
        this.password = password;
        this.tenantDomain = tenantDomain;
    }

    @DataProvider(name = "manageRolesConfigProvider")
    private static Object[][] manageRolesConfigProvider() {

        return new Object[][] {
                {
                        ADMIN_USERNAME, ADMIN_PASSWORD, SUPER_TENANT_DOMAIN
                }
        };
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {

        super.init();
        scim2Client = new SCIM2CommonClient(getDeploymentProperty(IS_HTTPS_URL));
        client = HttpClients.createDefault();
    }

    @Test
    public void testCreatePrimaryGroup() throws Exception {

        JSONObject groupJSON = scim2Client.getRoleJSON("scim2Group1.json");
        HttpResponse response = scim2Client.provisionGroup(client, groupJSON, username, password);
        assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_CREATED,
                "Group has not been created successfully");
        JSONObject returnedUserJSON = getJSONFromResponse(response);
        groupId = returnedUserJSON.get(ID_ATTRIBUTE).toString();
        assertNotNull(groupId, "SCIM2 group id not available in the response.");
        EntityUtils.consume(response.getEntity());
    }

    @Test(dependsOnMethods = "testCreatePrimaryGroup")
    public void testUpdatePrimaryGroup() throws Exception {

        JSONObject groupJSON = scim2Client.getRoleJSON("scim2UpdateGroup.json");
        HttpResponse response = scim2Client.updateGroup(client, groupJSON, groupId, username, password);
        assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK, "Group has been updated successfully");
        JSONObject returnedUserJSON = getJSONFromResponse(response);
        groupId = returnedUserJSON.get(ID_ATTRIBUTE).toString();
        assertNotNull(groupId, "updated SCIM2 group id not available in the response.");
        EntityUtils.consume(response.getEntity());
    }

    @Test(dependsOnMethods = "testUpdatePrimaryGroup")
    public void testDeletePrimaryGroup() throws Exception {

        HttpResponse response = scim2Client.deleteGroup(client, groupId, ADMIN_USERNAME, ADMIN_PASSWORD);
        assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_NO_CONTENT, "Failed to delete the group");
        EntityUtils.consume(response.getEntity());
    }
}
