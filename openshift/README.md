# Installation on OpenShift

## Prerequisites

Install the `oc` command, login into the cluster and set yourself into your OS project.

In bash, navigate yourself into this directory.

## Create PostgreSQL Driver Image

This is an image containing the PostgreSQL driver for the JBoss EAP or Wildfly application server
(assuming you are going to use the PostgreSQL database).

Create an ImageStream and a BuildConfig objects:

```shell script
oc apply -f eap-postgresql-driver/is.yaml
oc apply -f eap-postgresql-driver/bc.yaml
```

If a build doesn't start automatically, start it with:

```shell script
oc start-build eap-postgresql-driver-build
```

The image will be created in the internal OS registry.

## Create Application Image

### Application Build

This BuildConfig pushes the application image into an external repository. Instead you could push
just into the internal OS repository - edit bc.yaml to achieve that. 

Import JBoss EAP 7.2 S2I image:

```shell script
oc import-image registry.redhat.io/jboss-eap-7/eap72-openjdk11-openshift-rhel8:latest --confirm --scheduled
```

Create pull secret for your external repository
(assumed name is images-paas-pull-secret, you can change it in the bc.yaml file).

Create a BuildConfig object:

```shell script
oc apply -f bc.yaml
```

If a build doesn't start automatically, start it with:

```shell script
oc start-build mjolnir-archive-service-build
```

## Application Deployment

List of OpenShift YAML files:

* dc.yaml
* ping-svc.yaml
* archive-pvc.yaml
* is.yml

### Steps to Deploy

Create image stream:

```shell script
oc create -f is.yaml
```

Create following secrets:

* mjolnir-archive-config - using template files in `mjolnir-archive-config/` directory,
* mjolnir-archive-secret - should contain `keystore.jks` for HTTPS configuration and `jgroups.jceks`
  for JGroups encryption
  (see <https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/7.2/html-single/getting_started_with_jboss_eap_for_openshift_container_platform/index#prepare_for_deployment>)

Create a persistent volume claim for the repository archive:

```shell script
oc apply -f archive-pvc.yaml
```

Create a ping service (for clustering between pods):

```shell script
oc apply -f ping-svc.yaml
```

Create a DeploymentConfig:

```shell script
oc apply -f dc.yaml
```

Start deployment (if it's not started automatically):

```shell script
oc rollout latest dc/mjolnir-archive-service
```
