package alien4cloud.it.application;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;

import alien4cloud.cloud.CloudResourceTopologyMatchResult;
import alien4cloud.it.Context;
import alien4cloud.it.cloud.CloudComputeTemplateStepDefinitions;
import alien4cloud.it.cloud.CloudNetworkStepDefinitions;
import alien4cloud.it.cloudImage.CloudImageStepDefinitions;
import alien4cloud.model.application.Application;
import alien4cloud.model.application.DeploymentSetup;
import alien4cloud.model.cloud.CloudImage;
import alien4cloud.model.cloud.CloudImageFlavor;
import alien4cloud.model.cloud.ComputeTemplate;
import alien4cloud.model.cloud.NetworkTemplate;
import alien4cloud.rest.application.UpdateDeploymentSetupRequest;
import alien4cloud.rest.cloud.CloudDTO;
import alien4cloud.rest.model.RestResponse;
import alien4cloud.rest.utils.JsonUtil;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ApplicationResourceMatcherStepDefinitions {

    @When("^I match for resources for my application on the cloud$")
    public void I_match_for_resources_for_my_application_on_the_cloud() throws Throwable {
        Application application = Context.getInstance().getApplication();
        String applicationId = application.getId();
        Context.getInstance().registerRestResponse(
                Context.getRestClientInstance().get(
                        "/rest/applications/" + applicationId + "/environments/"
                                + Context.getInstance().getDefaultApplicationEnvironmentId(application.getName()) + "/cloud-resources"));
    }

    @Then("^I should receive a match result with (\\d+) compute templates for the node \"([^\"]*)\":$")
    public void I_should_receive_a_match_result_with_compute_templates_for_the_node(int numberOfComputeTemplates, String nodeName,
            DataTable expectedTemplatesTable) throws Throwable {
        RestResponse<CloudResourceTopologyMatchResult> matchResultResponse = JsonUtil.read(Context.getInstance().getRestResponse(),
                CloudResourceTopologyMatchResult.class);
        Assert.assertNull(matchResultResponse.getError());
        Assert.assertNotNull(matchResultResponse.getData());
        CloudComputeTemplateStepDefinitions.assertComputeTemplates(numberOfComputeTemplates,
                Sets.newHashSet(matchResultResponse.getData().getComputeMatchResult().get(nodeName)), expectedTemplatesTable);
    }

    @And("^The match result should contain (\\d+) cloud images:$")
    public void The_match_result_should_contain_cloud_images(int numberOfImages, DataTable expectedImagesTable) throws Throwable {
        Set<CloudImage> images = Sets.newHashSet(JsonUtil.read(Context.getInstance().getRestResponse(), CloudResourceTopologyMatchResult.class).getData()
                .getImages().values());
        CloudImageStepDefinitions.assertCloudImages(numberOfImages, images, expectedImagesTable);
    }

    @And("^The match result should contain (\\d+) flavors:$")
    public void The_match_result_should_contain_flavors(int numberOfFlavors, DataTable expectedFlavorsTable) throws Throwable {
        Set<CloudImageFlavor> flavors = Sets.newHashSet(JsonUtil.read(Context.getInstance().getRestResponse(), CloudResourceTopologyMatchResult.class)
                .getData().getFlavors().values());
        CloudComputeTemplateStepDefinitions.assertFlavors(numberOfFlavors, flavors, expectedFlavorsTable);
    }

    @Then("^I should receive an empty match result$")
    public void I_should_receive_an_empty_match_result() throws Throwable {
        RestResponse<CloudResourceTopologyMatchResult> matchResultResponse = JsonUtil.read(Context.getInstance().getRestResponse(),
                CloudResourceTopologyMatchResult.class);
        Assert.assertTrue(matchResultResponse.getData().getImages().isEmpty());
        Assert.assertTrue(matchResultResponse.getData().getFlavors().isEmpty());
        for (List<ComputeTemplate> templates : matchResultResponse.getData().getComputeMatchResult().values()) {
            Assert.assertTrue(templates.isEmpty());
        }
    }

    @When("^I select the template composed of image \"([^\"]*)\" and flavor \"([^\"]*)\" for my node \"([^\"]*)\"$")
    public void I_select_the_template_composed_of_image_and_flavor_for_my_node(String cloudImageName, String flavorId, String nodeName) throws Throwable {
        Map<String, ComputeTemplate> cloudResourcesMatching = Maps.newHashMap();
        cloudResourcesMatching.put(nodeName, new ComputeTemplate(Context.getInstance().getCloudImageId(cloudImageName), flavorId));
        UpdateDeploymentSetupRequest request = new UpdateDeploymentSetupRequest(null, null, cloudResourcesMatching, null, null);
        Application application = Context.getInstance().getApplication();
        String response = Context.getRestClientInstance().putJSon(
                "/rest/applications/" + application.getId() + "/environments/"
                        + Context.getInstance().getDefaultApplicationEnvironmentId(application.getName()) + "/deployment-setup", JsonUtil.toString(request));
        Context.getInstance().registerRestResponse(response);
    }

    @And("^The deployment setup of the application should contain following resources mapping:$")
    public void The_deployment_setup_of_the_application_should_contain_following_resources_mapping(DataTable resourcesMatching) throws Throwable {
        Map<String, ComputeTemplate> expectedCloudResourcesMatching = Maps.newHashMap();
        for (List<String> resourceMatching : resourcesMatching.raw()) {
            String cloudImageId = Context.getInstance().getCloudImageId(resourceMatching.get(1));
            String cloudImageFlavorId = resourceMatching.get(2);
            String nodeTemplate = resourceMatching.get(0);
            expectedCloudResourcesMatching.put(nodeTemplate, new ComputeTemplate(cloudImageId, cloudImageFlavorId));
        }
        Application application = ApplicationStepDefinitions.CURRENT_APPLICATION;
        DeploymentSetup deploymentSetup = JsonUtil
                .read(Context.getRestClientInstance().get(
                        "/rest/applications/" + application.getId() + "/environments/"
                                + Context.getInstance().getDefaultApplicationEnvironmentId(application.getName()) + "/deployment-setup"), DeploymentSetup.class)
                .getData();
        Assert.assertNotNull(deploymentSetup.getCloudResourcesMapping());
        Assert.assertEquals(expectedCloudResourcesMatching, deploymentSetup.getCloudResourcesMapping());
    }

    @Then("^The deployment setup of the application should contain no resources mapping$")
    public void The_deployment_setup_of_the_application_should_contain_no_resources_mapping() throws Throwable {
        Application application = ApplicationStepDefinitions.CURRENT_APPLICATION;
        DeploymentSetup deploymentSetup = JsonUtil
                .read(Context.getRestClientInstance().get(
                        "/rest/applications/" + application.getId() + "/environments/"
                                + Context.getInstance().getDefaultApplicationEnvironmentId(application.getName()) + "/deployment-setup"), DeploymentSetup.class)
                .getData();
        Assert.assertTrue(deploymentSetup.getCloudResourcesMapping() == null || deploymentSetup.getCloudResourcesMapping().isEmpty());
    }

    @Then("^I should receive a match result with (\\d+) networks for the node \"([^\"]*)\":$")
    public void I_should_receive_a_match_result_with_networks_for_the_node_(int numberOfNetworks, String networkNodeName, DataTable expectedTemplatesTable)
            throws Throwable {
        RestResponse<CloudResourceTopologyMatchResult> matchResultResponse = JsonUtil.read(Context.getInstance().getRestResponse(),
                CloudResourceTopologyMatchResult.class);
        Assert.assertNull(matchResultResponse.getError());
        Assert.assertNotNull(matchResultResponse.getData());
        Assert.assertNotNull(matchResultResponse.getData().getNetworkMatchResult());
        Assert.assertTrue(matchResultResponse.getData().getNetworkMatchResult().containsKey(networkNodeName));
        CloudNetworkStepDefinitions.assertNetworks(numberOfNetworks,
                Sets.newHashSet(matchResultResponse.getData().getNetworkMatchResult().get(networkNodeName)), expectedTemplatesTable);
    }

    @Then("^I should receive a match result with no networks for the node \"([^\"]*)\"$")
    public void I_should_receive_a_match_result_with_no_networks_for_the_node(String networkNodeName) throws Throwable {
        RestResponse<CloudResourceTopologyMatchResult> matchResultResponse = JsonUtil.read(Context.getInstance().getRestResponse(),
                CloudResourceTopologyMatchResult.class);
        Assert.assertTrue(matchResultResponse.getData().getNetworkMatchResult() == null
                || matchResultResponse.getData().getNetworkMatchResult().get(networkNodeName).isEmpty());
    }

    @When("^I select the network with name \"([^\"]*)\" for my node \"([^\"]*)\"$")
    public void I_select_the_the_network_with_name_for_my_node(String networkName, String nodeName) throws Throwable {
        Context.getInstance().getCloudForTopology();
        String cloudId = Context.getInstance().getCloudForTopology();
        CloudDTO cloudDTO = JsonUtil.read(Context.getRestClientInstance().get("/rest/clouds/" + cloudId), CloudDTO.class).getData();
        Map<String, NetworkTemplate> networkMatching = Maps.newHashMap();
        networkMatching.put(nodeName, cloudDTO.getNetworks().get(networkName).getResource());
        UpdateDeploymentSetupRequest request = new UpdateDeploymentSetupRequest(null, null, null, networkMatching, null);
        Application application = Context.getInstance().getApplication();
        String response = Context.getRestClientInstance().putJSon(
                "/rest/applications/" + application.getId() + "/environments/"
                        + Context.getInstance().getDefaultApplicationEnvironmentId(application.getName()) + "/deployment-setup", JsonUtil.toString(request));
        Context.getInstance().registerRestResponse(response);
    }

    @And("^The deployment setup of the application should contain following network mapping:$")
    public void The_deployment_setup_of_the_application_should_contain_following_network_mapping(DataTable networksMatching) throws Throwable {
        Map<String, NetworkTemplate> expectedNetworksMatching = Maps.newHashMap();
        for (List<String> rows : networksMatching.raw()) {
            String name = rows.get(1);
            String cidr = rows.get(2);
            int ipVersion = Integer.parseInt(rows.get(3));
            String gateWay = rows.get(4);
            NetworkTemplate network = new NetworkTemplate();
            network.setId(name);
            network.setIpVersion(ipVersion);
            network.setCidr(cidr);
            network.setGatewayIp(gateWay);
            expectedNetworksMatching.put(rows.get(0), network);
        }
        Application application = ApplicationStepDefinitions.CURRENT_APPLICATION;
        DeploymentSetup deploymentSetup = JsonUtil
                .read(Context.getRestClientInstance().get(
                        "/rest/applications/" + application.getId() + "/environments/"
                                + Context.getInstance().getDefaultApplicationEnvironmentId(application.getName()) + "/deployment-setup"), DeploymentSetup.class)
                .getData();
        Assert.assertNotNull(deploymentSetup.getCloudResourcesMapping());
        Assert.assertEquals(expectedNetworksMatching, deploymentSetup.getNetworkMapping());
    }

    @Then("^The deployment setup of the application should contain an empty network mapping$")
    public void The_deployment_setup_of_the_application_should_contain_an_empty_network_mapping() throws Throwable {
        Application application = ApplicationStepDefinitions.CURRENT_APPLICATION;
        DeploymentSetup deploymentSetup = JsonUtil
                .read(Context.getRestClientInstance().get(
                        "/rest/applications/" + application.getId() + "/environments/"
                                + Context.getInstance().getDefaultApplicationEnvironmentId(application.getName()) + "/deployment-setup"), DeploymentSetup.class)
                .getData();
        Assert.assertTrue(deploymentSetup.getNetworkMapping() == null || deploymentSetup.getNetworkMapping().isEmpty());
    }
}
