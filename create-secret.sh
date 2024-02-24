#!/bin/bash

### Kubernetes Secret
# Create a Kubernetes secret with the queue manager key and certificate
oc create secret generic example-qm-tls --type="kubernetes.io/tls" --from-file=example-app1.p12=example-app1.p12 --from-file=tls.key=example-qm.key --from-file=tls.crt=example-qm.crt --from-file=ca.crt -n cp4i
