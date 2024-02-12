# Entity Resolution with Stardog and Databricks
### Introduction
In this tutorial, we will demonstrate Entity Resolution matching capabilities with Stardog and Databricks. This tutorial used sample datasets from the Centers for Medicare and Medicaid Services’ (CMS) National Plan and Provider Enumeration System (NPPES) and CMS’ OpenPayments. NPPES contains basic directory information for every individual physician, while OpenPayments discloses relationships between Drug and Durable Medical Equipment (DME) with physicians. Our goal was to identify the physicians on OpenPayments with their directory information.


### Step 1: Run Data Preparation Notebook in Databricks  

This Databricks notebook includes instructions for subscribing to the correct [Databricks Marketplace](https://marketplace.databricks.com/) listings for the Centers for Medicare and Medicaid Services’ (CMS) National Plan and Provider Enumeration System (NPPES) and CMS’ OpenPayments datasets. 

[Entity Resolution Blog - Data Preparation.dbc](https://raw.githubusercontent.com/stardog-union/stardog-examples/develop/examples/entity-resolution-databricks/Entity_Resolution_Blog_Data_Preparation.dbc)

Once subscribed to this marketplace listings, this notebook performs basic data transformations to prepare the data for the Entity Resolution process.

### Step 2: Confirm Stardog Environment Set Up

[Stardog Cloud](https://cloud.stardog.com/) is a complete Enterprise Knowledge Graph Platform provided as a service. As a part of your subscription, you get a dedicated Stardog Knowledge Graph endpoint (hosted by Stardog) and your account will have administrative access to your new environment. You also get easy access to the Stardog Applications: Explorer, Designer, and Studio. For more information on getting started Stardog Cloud please visit our [documentation](https://docs.stardog.com/stardog-cloud/getting-started-stardog-cloud).

### Step 3: Create Stardog Database

Create a Stardog Database using the Stardog [Studio](https://docs.stardog.com/stardog-applications/studio/) Database tab

### Step 4: Create a Stardog Data Source connection to Databricks

There are multiple options to create a Stardog Data Source connection to Databricks:
1. Databricks [Partner Connect](https://docs.stardog.com/stardog-cloud/databricks-partner-connect) allows Databricks users to connect to applications hosted by Databricks partners, such as Stardog.
2. Stardog Studio

It is important to confirm the following [Stardog Data Source configurations](https://docs.stardog.com/virtual-graphs/data-sources/data-source-configuration#data-source-configuration) are set. A Databricks Data Source added in Stardog can also be registered as an [external compute platform](https://docs.stardog.com/external-compute/). We will add [Databricks specific properties](https://docs.stardog.com/external-compute/configuring-external-compute-databricks#optional-properties) in the Data Source definition to configure the Databricks data source as an external compute platform. This will be leveraged when running the Stardog Entity Resolution Service.


**Data Source Details:**
```
Data Source Name=YourDataSourceName
Data Source Type=Databricks Spark SQL
```

***JBDC Properties***
```
jdbc.url=jdbc:databricks://<instance hostname>:443;HttpPath=<HttpPath>
jdbc.username=token
jdbc.password=YourDatabricksTokenHere
jdbc.driver=com.databricks.client.jdbc.Driver

```
***Advanced Options:***
```
sql.schemas=mycatalog.*
```
***Other Options:***
```
stardog.external.compute.platform
external.compute=true
external.compute.host.name=adb-XXXXXXXXXXXXXX.XX.azuredatabricks.net
databricks.cluster.id=0704-XXXXXX-XXXXXdir
stardog.host.url=https://myhost.stardog.cloud:5820
sql.default.catalog=mycatalog
stardog.external.jar.path=s3://stardog-spark/stardog-spark-connector-3.2.0.jar
```

It is recommended that you also add ```unique.key.sets``` properties to the Data Source.

For data sources that do not express unique constraints in their metadata, either because unique constraints are not supported or because the data source did not include some or all of the valid constraints for reasons such as performance concerns, the [unique key sets](https://docs.stardog.com/virtual-graphs/data-sources/data-source-configuration#uniquekeysets) property is used to define additional constraints manually. 

***Example Unique Key Sets***:
```
unique.key.sets=(entity_resolution_demo.nationalprovidersclean.NPI),(entity_resolution_demo.nationalprovidersclean.Specialty),(entity_resolution_demo.nationalprovidersclean.source),(entity_resolution_demo.nationalprovidersclean.Medical_School_Name),(entity_resolution_demo.nationalprovidersclean.Full_Street_Address,entity_resolution_demo.nationalprovidersclean.City,entity_resolution_demo.nationalprovidersclean.State,entity_resolution_demo.nationalprovidersclean.Zip_Code),(entity_resolution_demo.openpaymentprovidersclean.Physician_Profile_ID),(entity_resolution_demo.openpaymentprovidersclean.Physician_Specialty),(entity_resolution_demo.openpaymentprovidersclean.source),(entity_resolution_demo.openpaymentprovidersclean.Full_Street_Address,entity_resolution_demo.openpaymentprovidersclean.Physician_Profile_City,entity_resolution_demo.openpaymentprovidersclean.Physician_Profile_State,entity_resolution_demo.openpaymentprovidersclean.Physician_Profile_Zipcode)
```

### Step 5: Load Data Model into Stardog Database

Use Stardog's Studio to import the data model into the Stardog database created in Step 3. Load the data model [found here](https://raw.githubusercontent.com/stardog-union/stardog-examples/develop/examples/entity-resolution-databricks/provider_model.ttl) using the "Load RDF" function found on the Database tab in Studio. When selecting the "Load RDF" option, please update the target graph under the "Load Data To" option to ```urn:er:demo:model```.

### Step 6: Create Virtual Graphs in Stardog

Use Stardog's Studio to create two [Virtual Graphs](https://docs.stardog.com/virtual-graphs/) with the Data Source created in the Step 4. We will leverage two mappings that were created using [Stardog Designer](https://docs.stardog.com/stardog-applications/designer/). Those mappings can be found here. Their contents can be used to create the following Virtual Graphs.
1. np_providers_vg - [np_providers_vg.sms](https://raw.githubusercontent.com/stardog-union/stardog-examples/develop/examples/entity-resolution-databricks/np_providers_vg.sms)
2. op_providers_vg - [op_providers_vg.sms](https://raw.githubusercontent.com/stardog-union/stardog-examples/develop/examples/entity-resolution-databricks/op_providers_vg.sms)

### Step 7: Run Stardog Entity Resolution Service

Stardog's [Entity Resolution Service](https://docs.stardog.com/external-compute/entity-resolution-external-compute) requires users to execute the service using the CLI. External compute is the recommended way to use the entity-resolution functionality, which was set up in Step 4. The Entity Resolution operation gets converted into a Databricks Spark job. This Spark job will be created and triggered on an external-compute platform. The Spark job is dependent on the [stardog-spark-connector.jar](s3://stardog-spark/stardog-spark-connector-3.2.0.jar). If this jar is not on the external compute platform the Stardog server will upload the latest compatible version of the stardog-spark-connector.jar based on the configured options. 


Running the CLI command below will kick off Stardog's Entity Resolution Service. It will federate queries down to the Databricks Data Source defined in Step 4 using the Virtual Graphs defined in Step 6 and then leverage the Databricks Spark Cluster to perform the Entity Resolution work. To simplify this exercise, the query below only focuses on Providers who have Practices in Washington DC. The Entity Match Results will be written to a Stardog graph in the database called ```urn:er:results:dc:specialty```.
```
stardog entity-resolution resolve -p YourPassWordHere -u YourUserNameHere https://YourStardogHostNameHere.stardog.cloud:5820/YourDatabaseNameHere "PREFIX provider: <urn:provider:model:>
 SELECT  distinct ?Physician_iri ?full_name ?full_address ?city ?zip ?specialty
 WHERE { {
    GRAPH <virtual://np_providers_vg>  {
    ?Physician_iri a provider:Physician ;
                   provider:full_name ?full_name ;
                   provider:specializes_in ?Specialty_iri;
                   provider:primary_practice_address ?Address_iri.
    ?Address_iri provider:full_address ?full_address;
                 provider:city ?city;
                 provider:state 'DC';
                 provider:zip_code ?zip.
    ?Specialty_iri provider:specialty_name ?specialty.}
    }
UNION
{   GRAPH <virtual://op_providers_vg>  {
    ?Physician_iri a provider:Physician ;
                   provider:full_name ?full_name ;
                   provider:specializes_in ?Specialty_iri;
                   provider:primary_practice_address ?Address_iri.
    ?Address_iri provider:full_address ?full_address;
                 provider:city ?city;
                 provider:state 'DC';
                 provider:zip_code ?zip.
    ?Specialty_iri provider:specialty_name ?specialty.}
    }}" Physician_iri urn:er:results:dc:specialty -c YourDatabricksSourceName -i true -f er.properties
```

### Step 9: View the Results

The results can be viewed in [Stardog Explorer](https://docs.stardog.com/stardog-applications/explorer/) or in Databricks. Stardog will pull Provider data from Open Payments and National Provider data sets in Databricks and the Entity Match Results from Stardog. 

This Databricks notebook,  [Entity Resolution Blog - Data Consumption](https://raw.githubusercontent.com/stardog-union/stardog-examples/develop/examples/entity-resolution-databricks/Entity_Resolution_Blog_Data_Consumption.dbc), uses Stardog's Python framework Pystardog to query for the Entity Match Results from Databricks. The notebook writes these results to a dataframe. Now users can query across the Open Payments and Providers data sets in Databricks leveraging the match results from Stardog's Entity Resolution Service.
