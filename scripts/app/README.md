### fk-ip-inventory-planner service setup in the nm cloud.

ssh -at iaas-cli-0001.ch.flipkart.com

# publish package to the repo-svc
reposervice --host 10.85.51.142 --port 8080 pub --repo inventory-planner --appkey fk-rp-telescope --debs #{debian-package}

# create repo-svc environment
reposervice --host 10.85.51.142 --port 8080 penv --name prod-fk-inventory-planner-env --envdef env.json --appkey fk-rp-telescope

# instance group APIs
kloud-cli --zone=in-mumbai-prod instanceTemplate --appId=fk-rp-telescope create --name=tpl-app-fk-inventory-planner-4 --type=c0.small --users=users.txt --script=setup.sh --user=fk-alpha-tech --password=alphatech
kloud-cli --zone in-mumbai-prod instanceGroup create --appId fk-rp-telescope --name app-fk-inventory-planner-4 --template tpl-app-fk-inventory-planner-4 --size 2 --reservationId 6592 --user=fk-alpha-tech --password=alphatech

kloud-cli --zone=in-mumbai-prod instanceGroup --appId=fk-rp-telescope describe --name=app-fk-inventory-planner-4


# instance APIs
# getting details of all instances of an appId
kloud-cli --zone in-mumbai-prod instance --appId fk-rp-telescope list

## ELB Create

**Don't run this again!**

elb-cli --zone in-mumbai-prod elb create --app-id fk-rp-telescope --scope regional --vip-name elb-prod-fk-rp-telescope-1 --mode http --instance-group app-fk-inventory-planner-4 --backend-port 32000 --frontend-port 80 --health-check-path /healthcheck --health-check-port 32001