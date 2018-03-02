# Harbor Samples  
This guide provides sample yamls and basic instructions to use with Harbor. It assumes you have a working Harbor installation and Kubernetes cluster.

Make sure to identify your `<HARBOR_DOMAIN>` as we will refere to it during the next steps.

For more details on how to use Harbor check the [Harbor GitHub repository](https://github.com/vmware/harbor) and in particular the [user guide](https://github.com/vmware/harbor/blob/master/docs/user_guide.md).

## My first Harbor app
Locate the Harbor Project (`<HARBOR_PROJECT>`) you will be using to push & pull images, or create a new one via Harbor GUI. More on managing projects [here](https://github.com/vmware/harbor/blob/master/docs/user_guide.md#managing-projects).

### Push image to Harbor
If your Harbor was configured with a self-signed certificate or signed by an unknown CA then you need to add `--insecure-registry myregistrydomain.com` your docker daemon.
If using docker for mac you can easily do this via preferences of your docker app: Settings > Daemon > Basic > Insecure Registries. Apply & Restart when done

Login to Harbor using docker CLI
- Run: `docker login <HARBOR_DOMAIN>`.
- Example: `docker login harbor.pks.pcfgcp.jagapps.co`
- When prompted, enter a username & password from a Harbor account with permissions to push images.

Tag an existing docker image with the Harbor Domain and Project using docker CLI
- Run: `docker tag IMAGE[:TAG] <HARBOR_DOMAIN>/<HARBOR_PROJECT>/IMAGE[:TAG]`
- Exmaple: `docker tag jaimegag/spring-boot-timesample:latest harbor.pks.pcfgcp.jagapps.co/jaimegag/spring-boot-timesample:v1.0.0`
- Here I’m using an image previously pulled from Docker Hub: `jaimegag/spring-boot-timesample`
  - Here's the [app code and Docker file](https://github.com/jaimegag/spring-boot-timesample)

Push the tagged image to Harbor using docker CLI
- Tag an image ...
- Run `docker push <HARBOR_DOMAIN>/<HARBOR_PROJECT>/IMAGE[:TAG]`.
- Select a `<HARBOR_PROJECT>` you have permisions to push/pull images to/from.
- Example: `docker push harbor.pks.pcfgcp.jagapps.co/jaimegag/spring-boot-timesample:v1.0.0`

### Pull image from Harbor into Kubernetes
The first thing is to create a Registry Secret with the necessary credentials to be able to pull images from Harbor. Your app yaml will need to use this to work.
- Run this command:
```
kubectl create secret docker-registry regsecret \
--docker-server="<HARBOR_DOMAIN>" \
--docker-username="<HARBOR_USERNAME>" --docker-password="<HARBOR_PASSWORD>" \
--docker-email="<USER_EMAIL>"
```
- Example:
```
kubectl create secret docker-registry regsecret \
--docker-server="harbor.pks.pcfgcp.jagapps.co" \
--docker-username="admin" --docker-password="pass" \
--docker-email="you@domain.com"
```

In your app Deployment yaml make sure to:
- Reference the Harbor image like this: `image: <HARBOR_DOMAIN>/<HARBOR_PROJECT>/<IMAGE>[:TAG]`
- Example: `image: harbor.pks.pcfgcp.jagapps.co/jaimegag/spring-boot-timesample:v1.0.0`
- Reference the secrets inserting this (at the same level as containers):
```
      imagePullSecrets:
      - name: regsecret
```
Check the [timesample.yml](timesample.yml) in this repo to see an example of the above.
